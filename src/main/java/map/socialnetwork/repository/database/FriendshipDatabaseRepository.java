package map.socialnetwork.repository.database;


import map.socialnetwork.domain.model.Friendship;
import map.socialnetwork.domain.model.User;
import map.socialnetwork.domain.validator.ValidationException;
import map.socialnetwork.domain.validator.Validator;
import map.socialnetwork.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FriendshipDatabaseRepository implements Repository<Friendship, Long> {

    private static final String SAVE_QUERY = "INSERT INTO friendships(id, user_id_1, user_id_2, friends_since_date) " +
            "VALUES(?, ?, ?, ?)";

    private static final String DELETE_QUERY = "DELETE FROM friendships " +
            "WHERE id=?";

    private static final String FIND_BY_ID_QUERY = "SELECT * " +
            "FROM friendships " +
            "WHERE id=?";

    private static final String FIND_BY_USERS_ID_QUERY = "SELECT * " +
            "FROM friendships " +
            "WHERE user_id_1=? AND user_id_2=?";

    private static final String FIND_ID = "SELECT id " +
            "FROM friendships " +
            "WHERE user_id_1=? AND user_id_2=?";

    private static final String SELECT_ALL_QUERY = "SELECT * " +
            "FROM friendships " +
            "WHERE status= 'accepted'";

    private static final String SELECT_REQUESTS_QUERY = "SELECT * " +
            "FROM friendships " +
            "WHERE status= 'pending'";

    private static final String UPDATE_QUERY = "UPDATE friendships " +
            "SET user_id_1=?, user_id_2=?, friends_since_date=?, status=? " +
            "WHERE id=?";

    private static final String GENERATE_ID_QUERY = "SELECT MAX(id) + 1 AS generated_id " +
            "FROM friendships";

    private final Validator<Friendship> friendshipValidator;
    private final Repository<User, Long> userRepository;

    public FriendshipDatabaseRepository(Validator<Friendship> friendshipValidator, Repository<User, Long> userRepository) {
        this.friendshipValidator = friendshipValidator;
        this.userRepository = userRepository;
    }

    @Override
    public Friendship findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractFriendshipFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Friendship findOneByUser(Long user_id_1, Long user_id_2) {
        if (user_id_1 == null || user_id_2 == null) {
            throw new IllegalArgumentException("ID's cannot be null");
        }
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_USERS_ID_QUERY);
            preparedStatement.setLong(1, user_id_1);
            preparedStatement.setLong(2, user_id_2);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractFriendshipFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Long findID(Long user_id_1, Long user_id_2) {
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ID);
            preparedStatement.setLong(1, user_id_1);
            preparedStatement.setLong(2, user_id_2);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractFriendshipFromResultSet(resultSet).getId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        return getAll();
    }

    @Override
    public Collection<Friendship> getAll() {
        List<Friendship> friendshipList = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                friendshipList.add(extractFriendshipFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendshipList;
    }

    public Collection<Friendship> getRequests() {
        List<Friendship> friendshipList = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_REQUESTS_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                friendshipList.add(extractFriendshipFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendshipList;
    }

    @Override
    public Long generateNextID() {
        try {
            ResultSet resultSet = ConnectionFactory.getDatabaseConnection().prepareStatement(GENERATE_ID_QUERY).executeQuery();
            if(resultSet.next()) {
                return resultSet.getLong("generated_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship save(Friendship entity) {
        Friendship friendship = findOne(entity.getId());
        if (friendship != null) {
            return friendship;
        }
        User user1 = userRepository.findOne(entity.getUserID1());
        User user2 = userRepository.findOne(entity.getUserID2());
        if(user1 == null || user2 == null) {
            throw new ValidationException("Users must exist");
        }

        friendshipValidator.validate(entity);

        Connection connection = ConnectionFactory.getDatabaseConnection();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(SAVE_QUERY);
            preparedStatement.setLong(1, entity.getId());
            preparedStatement.setLong(2, entity.getUserID1());
            preparedStatement.setLong(3, entity.getUserID2());
            preparedStatement.setTimestamp(4, entity.getFriendshipStartDate());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    @Override
    public Friendship delete(Long id) {
        Friendship friendship = findOne(id);

        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendship;
    }

    @Override
    public Friendship update(Friendship entity) {
        Friendship friendship1 = findOneByUser(entity.getUserID2(), entity.getUserID1());
        Friendship friendship2 = findOneByUser(entity.getUserID1(), entity.getUserID2());
        if (friendship1 != null) {
            try {
                Connection connection = ConnectionFactory.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY);
                preparedStatement.setLong(1, entity.getUserID1());
                preparedStatement.setLong(2, entity.getUserID2());
                preparedStatement.setTimestamp(3, entity.getFriendshipStartDate());
                preparedStatement.setString(4, "accepted");
                preparedStatement.setLong(5, friendship1.getId());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (friendship2 != null){
            try {
                Connection connection = ConnectionFactory.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY);
                preparedStatement.setLong(1, entity.getUserID1());
                preparedStatement.setLong(2, entity.getUserID2());
                preparedStatement.setTimestamp(3, entity.getFriendshipStartDate());
                preparedStatement.setString(4, "accepted");
                preparedStatement.setLong(5, friendship2.getId());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    @Override
    public int getSize() {
        return getAll().size();
    }


    private Friendship extractFriendshipFromResultSet(ResultSet resultSet) throws SQLException {
        Friendship friendship = new Friendship(resultSet.getLong("user_id_1"),
                resultSet.getLong("user_id_2"), resultSet.getTimestamp("friends_since_date"));
        friendship.setId(resultSet.getLong("id"));
        friendshipValidator.validate(friendship);
        return friendship;
    }
}