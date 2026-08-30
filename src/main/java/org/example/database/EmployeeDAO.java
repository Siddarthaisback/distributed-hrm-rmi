package org.example.database;

import org.example.model.Employee;
import org.example.security.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public boolean addEmployee(Employee employee) {

        String employeeQuery = """
                INSERT INTO employees(first_name,last_name,passport_no,email,phone,department,position)
                VALUES (?,?,?,?,?,?,?)
                """;

        String loginQuery = """
                INSERT INTO employee_login(employee_id, username, password)
                VALUES (?,?,?)
                """;

        String leaveBalanceQuery = """
                INSERT INTO leave_balance(employee_id, total_leave, used_leave, remaining_leave)
                VALUES (?, ?, 0, ?)
                """;

        Connection con = null;
        PreparedStatement empPs = null;
        PreparedStatement loginPs = null;
        PreparedStatement leavePs = null;
        ResultSet generatedKeys = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            empPs = con.prepareStatement(employeeQuery, Statement.RETURN_GENERATED_KEYS);
            empPs.setString(1, employee.getFirstName());
            empPs.setString(2, employee.getLastName());
            empPs.setString(3, employee.getPassportNo());
            empPs.setString(4, employee.getEmail());
            empPs.setString(5, employee.getPhone());
            empPs.setString(6, employee.getDepartment());
            empPs.setString(7, employee.getPosition());

            int rows = empPs.executeUpdate();

            if (rows == 0) {
                con.rollback();
                return false;
            }

            generatedKeys = empPs.getGeneratedKeys();
            if (!generatedKeys.next()) {
                con.rollback();
                return false;
            }

            int employeeId = generatedKeys.getInt(1);

            String hashedPassword = PasswordUtil.hashPassword(employee.getPassword());

            loginPs = con.prepareStatement(loginQuery);
            loginPs.setInt(1, employeeId);
            loginPs.setString(2, employee.getUsername());
            loginPs.setString(3, hashedPassword);
            loginPs.executeUpdate();

            leavePs = con.prepareStatement(leaveBalanceQuery);
            leavePs.setInt(1, employeeId);
            leavePs.setInt(2, employee.getTotalLeave());
            leavePs.setInt(3, employee.getTotalLeave());
            leavePs.executeUpdate();

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (Exception ignored) {}
            try { if (empPs != null) empPs.close(); } catch (Exception ignored) {}
            try { if (loginPs != null) loginPs.close(); } catch (Exception ignored) {}
            try { if (leavePs != null) leavePs.close(); } catch (Exception ignored) {}
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception ignored) {}
        }

        return false;
    }

    public Employee getEmployeeById(int id) {

        String query = "SELECT * FROM employees WHERE employee_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Employee emp = new Employee();

                emp.setEmployeeId(rs.getInt("employee_id"));
                emp.setFirstName(rs.getString("first_name"));
                emp.setLastName(rs.getString("last_name"));
                emp.setPassportNo(rs.getString("passport_no"));
                emp.setEmail(rs.getString("email"));
                emp.setPhone(rs.getString("phone"));
                emp.setDepartment(rs.getString("department"));
                emp.setPosition(rs.getString("position"));

                Date joinDate = rs.getDate("join_date");
                if (joinDate != null) {
                    emp.setJoinDate(joinDate.toLocalDate());
                }

                return emp;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateEmployeeProfile(Employee employee) {

        String query = """
                UPDATE employees
                SET first_name=?,
                    last_name=?,
                    passport_no=?,
                    email=?,
                    phone=?,
                    department=?,
                    position=?
                WHERE employee_id=?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getPassportNo());
            ps.setString(4, employee.getEmail());
            ps.setString(5, employee.getPhone());
            ps.setString(6, employee.getDepartment());
            ps.setString(7, employee.getPosition());
            ps.setInt(8, employee.getEmployeeId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean deleteEmployee(int employeeId) {
        // Delete in dependency order to avoid FK constraint violations
        String[] sqls = {
                "DELETE FROM family_details   WHERE employee_id = ?",
                "DELETE FROM leave_requests   WHERE employee_id = ?",
                "DELETE FROM leave_balance    WHERE employee_id = ?",
                "DELETE FROM employee_login   WHERE employee_id = ?",
                "DELETE FROM employees        WHERE employee_id = ?"
        };

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            for (String sql : sqls) {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, employeeId);
                    ps.executeUpdate();
                }
            }
            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Employee> getAllEmployees() {

        List<Employee> employees = new ArrayList<>();

        String query = "SELECT * FROM employees ORDER BY employee_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee emp = new Employee();

                emp.setEmployeeId(rs.getInt("employee_id"));
                emp.setFirstName(rs.getString("first_name"));
                emp.setLastName(rs.getString("last_name"));
                emp.setPassportNo(rs.getString("passport_no"));
                emp.setEmail(rs.getString("email"));
                emp.setPhone(rs.getString("phone"));
                emp.setDepartment(rs.getString("department"));
                emp.setPosition(rs.getString("position"));

                Date joinDate = rs.getDate("join_date");
                if (joinDate != null) {
                    emp.setJoinDate(joinDate.toLocalDate());
                }

                employees.add(emp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }
}
