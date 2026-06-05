package com.payroll;

import java.sql.*;

public class DatabaseManager {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "payroll_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "PIPO1234";  

    private static final String DB_URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
        "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void initializeDatabase() {
        String createTable =
            "CREATE TABLE IF NOT EXISTS employees (" +
            "  id     INT PRIMARY KEY AUTO_INCREMENT," +
            "  name   VARCHAR(100) NOT NULL," +
            "  salary DOUBLE NOT NULL DEFAULT 0.0," +
            "  bonus  DOUBLE NOT NULL DEFAULT 0.0" +
            ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
            seedData(conn);
            System.out.println("Database ready.");
        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    private static void seedData(Connection conn) throws SQLException {
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM employees")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        String sql = "INSERT INTO employees (name, salary, bonus) VALUES (?, ?, ?)";
        Object[][] data = {
            {"Aarav Sharma",  75000.0,  8000.0},
            {"Priya Menon",   92000.0, 11000.0},
            {"Rohan Desai",   68000.0,  6500.0},
            {"Sneha Iyer",   110000.0, 15000.0},
            {"Karan Mehta",   55000.0,  4000.0},
            {"Divya Nair",    83000.0,  9500.0},
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] row : data) {
                ps.setString(1, (String) row[0]);
                ps.setDouble(2, (Double) row[1]);
                ps.setDouble(3, (Double) row[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            System.err.println("Close error: " + e.getMessage());
        }
    }
}