package map.socialnetwork.repository.database;


import map.socialnetwork.domain.model.User;
import map.socialnetwork.domain.validator.Validator;
import map.socialnetwork.repository.Repository;
import map.socialnetwork.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDatabaseRepository implements Repository<User, Long> {

    private static final String SAVE_QUERY = "INSERT INTO users(id, username, password, first_name, last_name) " +
            "VALUES(?, ?, ?, ?, ?)";

    private static final String DELETE_QUERY = "DELETE FROM users " +
            "WHERE id=?";

    private static final String FIND_BY_ID_QUERY = "SELECT * " +
            "FROM users " +
            "WHERE id=?";

    private static final String FIND_BY_USERNAME = "SELECT * " +
            "FROM users " +
            "WHERE username=?";

    private static final String FIND_USERNAME = "SELECT * " +
            "FROM users " +
            "WHERE username=?";

    private static final String SELECT_ALL_QUERY = "SELECT * " +
            "FROM users";

    private static final String UPDATE_QUERY = "UPDATE users " +
            "SET username=? " +
            "WHERE id=?";

    private static final String GENERATE_ID_QUERY = "SELECT MAX(id) + 1 AS generated_id " +
            "FROM users";

    private final Validator<User> validator;

    public UserDatabaseRepository(Validator<User> validator) {
        this.validator = validator;
    }

    @Override
    public User findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must be not null!");
        }
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findOneByUsername(String username) {
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USERNAME);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        return getAll();
    }

    @Override
    public Collection<User> getAll() {
        List<User> userList = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userList.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public String getUsername(String username) {
        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_USERNAME);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractUsernameFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public String getFriend()

    @Override
    public User save(User entity) {
        User user = findOne(entity.getId());
        if (user != null) {
            return user;
        }
        validator.validate(entity);
        Connection connection = ConnectionFactory.getDatabaseConnection();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(SAVE_QUERY);
            preparedStatement.setLong(1, entity.getId());
            preparedStatement.setString(2, entity.getUsername());
            preparedStatement.setString(3, PasswordUtil.hash(entity.getPassword().toCharArray()));
            preparedStatement.setString(4, entity.getFirstName());
            preparedStatement.setString(5, entity.getLastName());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        User user = findOne(id);

        try {
            Connection connection = ConnectionFactory.getDatabaseConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public User update(User entity) {
        User user = findOne(entity.getId());
        if (user != null) {
            try {
                Connection connection = ConnectionFactory.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY);
                preparedStatement.setString(1, entity.getUsername());
                preparedStatement.setLong(2, entity.getId());
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

    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User(resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"));
        user.setId(resultSet.getLong("id"));
        validator.validate(user);
        return user;
    }

    private String extractUsernameFromResultSet(ResultSet resultSet) throws SQLException {
        return resultSet.getString("username");
    }
}
