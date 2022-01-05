package map.socialnetwork.repository.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static Connection connection;

    public static Connection getDatabaseConnection() {
        if (connection == null) {
            String url = "jdbc:postgresql://localhost:5432/map";
            String user = "postgres";
            String password = "postgres";

            try {
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                System.out.println("Connection could not be established");
            }
        }
        return connection;
    }
}
