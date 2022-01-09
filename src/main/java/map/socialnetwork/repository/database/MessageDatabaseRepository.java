package map.socialnetwork.repository.database;

import map.socialnetwork.domain.model.Message;
import map.socialnetwork.domain.validator.Validator;
import map.socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessageDatabaseRepository implements Repository<Message, Long> {

    private final Validator<Message> messageValidator;

    public MessageDatabaseRepository(Validator<Message> validator) {
        this.messageValidator = validator;
    }

    public static final String FIND_BY_ID_QUERY = "SELECT * " +
            "FROM messages " +
            "WHERE id=? ";

    private static final String FIND_BY_RECIPIENTS_ID = "SELECT * " +
            "FROM messages " +
            "WHERE sender_id=? AND receiver_id=?";

    private static final String SELECT_ALL_QUERY = "SELECT * " +
            "FROM messages";

    private static final String DELETE_QUERY = "DELETE FROM messages " +
            "WHERE id=?";

    private static final String SAVE_QUERY = "INSERT INTO messages(sender_id, receiver_id, message_time, message, " +
            "reply_message_id) " +
            "VALUES(?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE messages " +
            "SET sender_id=?, receiver_id=?, message_time=?, message= ?, reply_message_id=?" +
            "WHERE id=?";

    @Override
    public Message findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractMessageFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Message findOneByUser(Long sender_id, Long receiver_id) {
        if (sender_id == null || receiver_id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_RECIPIENTS_ID);
            preparedStatement.setLong(1, sender_id);
            preparedStatement.setLong(2, receiver_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractMessageFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        return null;
    }


    @Override
    public Collection<Message> getAll() {
        List<Message> messageList = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                messageList.add(extractMessageFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messageList;
    }

    public Collection<Message> getMessages() {
        List<Message> messageList = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                messageList.add(extractMessageFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messageList;
    }

    @Override
    public Long generateNextID() {
        return null;
    }

    @Override
    public Message save(Message entity) {
        messageValidator.validate(entity);
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(SAVE_QUERY);
            preparedStatement.setLong(1, entity.getSenderID());
            preparedStatement.setLong(2, entity.getReceiverID());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getMessageTime()));
            preparedStatement.setString(4, entity.getMessage());
            if(entity.getReplyMessageID() != null) {
                preparedStatement.setLong(5, entity.getReplyMessageID());
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Message delete(Long id) {
        Message message = findOne(id);
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return message;
    }

    @Override
    public Message update(Message entity) {
        Message message = findOne(entity.getId());
        if (message != null) {
            try {
                Connection connection = ConnectionFactory.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY);
                preparedStatement.setLong(1, entity.getSenderID());
                preparedStatement.setLong(2, entity.getReceiverID());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getMessageTime()));
                preparedStatement.setString(4, entity.getMessage());
                preparedStatement.setLong(5, entity.getReplyMessageID());
                preparedStatement.setLong(6, entity.getId());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    @Override
    public int getSize() {
        return 0;
    }

    private Message extractMessageFromResultSet(ResultSet resultSet) throws SQLException {
                Message message = new Message(resultSet.getLong("sender_id"),
                resultSet.getLong("receiver_id"),
                resultSet.getTimestamp("message_time").toLocalDateTime(),
                resultSet.getString("message"),
                resultSet.getLong("reply_message_id"));
                message.setId(resultSet.getLong("id"));
        return message;
    }
}
