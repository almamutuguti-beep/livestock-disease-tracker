package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL  =
        "jdbc:mysql://localhost:3306/livestock_disease_tracker" +
        "?useSSL=false&serverTimezone=Africa/Nairobi";
    private static final String USER = "root";
    private static final String PASS = "Aa-@46774613";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connection successful: " + conn.getMetaData().getURL());
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}