package com.payroll;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("salary"),
                    rs.getDouble("bonus")
                ));
            }
        } catch (SQLException e) {
            System.err.println("getAllEmployees error: " + e.getMessage());
        }
        return list;
    }

    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("salary"),
                    rs.getDouble("bonus")
                );
            }
        } catch (SQLException e) {
            System.err.println("getEmployeeById error: " + e.getMessage());
        }
        return null;
    }

    public boolean addEmployee(String name, double salary, double bonus) {
        String sql = "INSERT INTO employees (name, salary, bonus) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, salary);
            ps.setDouble(3, bonus);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addEmployee error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSalary(int id, double newSalary) {
        String sql = "UPDATE employees SET salary = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newSalary);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateSalary error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBonus(int id, double newBonus) {
        String sql = "UPDATE employees SET bonus = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBonus);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateBonus error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteEmployee error: " + e.getMessage());
            return false;
        }
    }
}
