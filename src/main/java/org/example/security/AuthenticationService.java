package org.example.security;

import org.example.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthenticationService {

    public boolean authenticateHR(String username, String password) {

        String query = "SELECT password FROM hr_staff WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return PasswordUtil.verifyPassword(password, storedPassword);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int authenticateEmployee(String username, String password) {

        String query = "SELECT employee_id, password FROM employee_login WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    return rs.getInt("employee_id");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}