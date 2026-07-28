package com.fraud.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/frauddb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = System.getProperty("db.user", "root");
    private static final String PASSWORD = System.getProperty("db.password", "password");

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found! Please add mysql-connector-j.jar to your classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new Connection to the MySQL database.
     * @return Connection object
     * @throws SQLException if database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Tests the database connection.
     * @return true if connection is successful, false otherwise.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("⚠️ Database Connection Failure: " + e.getMessage());
            return false;
        }
    }
}
