package map.socialnetwork.repository.database;

import map.socialnetwork.domain.model.GroupChat;
import map.socialnetwork.domain.validator.Validator;
import map.socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupChatDatabaseRepository implements Repository<GroupChat, Long> {

    private final Validator<GroupChat> groupChatValidator;

    public GroupChatDatabaseRepository(Validator<GroupChat> groupChatValidator) {
        this.groupChatValidator = groupChatValidator;
    }

    public static final String FIND_BY_ID_QUERY = "SELECT * " +
            "FROM group_chats " +
            "WHERE id=? ";


    private static final String SELECT_ALL_QUERY = "SELECT * " +
            "FROM group_chats";

    private static final String DELETE_QUERY = "DELETE FROM group_chats " +
            "WHERE id=?";

    private static final String SAVE_QUERY = "INSERT INTO group_chats(name) " +
            "VALUES(?)";

    private static final String UPDATE_QUERY = "UPDATE group_chats " +
            "SET name=?" +
            "WHERE id=?";

    private static final String INSERT_INTO_XREF_TABLE = "INSERT INTO group_chats_users_xref(user_id, group_chat_id) " +
            "VALUES(?, ?) ";

    @Override
    public GroupChat findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractGroupChatFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<GroupChat> findAll() {
        List<GroupChat> groupChats = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                groupChats.add(extractGroupChatFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupChats;
    }

    @Override
    public Collection<GroupChat> getAll() {
        List<GroupChat> groupChats = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                groupChats.add(extractGroupChatFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupChats;
    }

    @Override
    public Long generateNextID() {
        return null;
    }

    @Override
    public GroupChat save(GroupChat entity) {
        groupChatValidator.validate(entity);
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getName());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next())
            {
                entity.setId(resultSet.getLong(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    @Override
    public GroupChat delete(Long id) {
        GroupChat groupChat = findOne(id);
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return groupChat;
    }

    @Override
    public GroupChat update(GroupChat entity) {
        GroupChat groupChat = findOne(entity.getId());
        if (groupChat != null) {
            try {
                Connection connection = ConnectionFactory.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY);
                preparedStatement.setString(1, entity.getName());
                preparedStatement.setLong(2, entity.getId());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    public void createGroupChatParticipant(Long groupChatId, Long userId) {
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_XREF_TABLE);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, groupChatId);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSize() {
        return 0;
    }

    private GroupChat extractGroupChatFromResultSet(ResultSet resultSet) throws SQLException {
        GroupChat groupChat = new GroupChat(resultSet.getString("name"));
        groupChat.setId(resultSet.getLong("id"));
        return groupChat;
    }
}
