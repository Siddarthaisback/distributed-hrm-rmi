package org.example.utils;

import org.example.database.DBConnection;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            String employees = """
                CREATE TABLE IF NOT EXISTS employees (
                    employee_id SERIAL PRIMARY KEY,
                    first_name VARCHAR(50),
                    last_name VARCHAR(50),
                    passport_no VARCHAR(50) UNIQUE,
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    department VARCHAR(100),
                    position VARCHAR(100),
                    join_date DATE DEFAULT CURRENT_DATE
                );
            """;

            String hrStaff = """
                CREATE TABLE IF NOT EXISTS hr_staff (
                    hr_id SERIAL PRIMARY KEY,
                    username VARCHAR(50) UNIQUE,
                    password VARCHAR(100),
                    full_name VARCHAR(100),
                    email VARCHAR(100)
                );
            """;

            String employeeLogin = """
                CREATE TABLE IF NOT EXISTS employee_login (
                    login_id SERIAL PRIMARY KEY,
                    employee_id INT UNIQUE,
                    username VARCHAR(50) UNIQUE,
                    password VARCHAR(100)
                );
            """;

            String leaveBalance = """
                CREATE TABLE IF NOT EXISTS leave_balance (
                    balance_id SERIAL PRIMARY KEY,
                    employee_id INT UNIQUE,
                    total_leave INT DEFAULT 20,
                    used_leave INT DEFAULT 0,
                    remaining_leave INT DEFAULT 20
                );
            """;

            String leaveRequests = """
                CREATE TABLE IF NOT EXISTS leave_requests (
                    leave_id SERIAL PRIMARY KEY,
                    employee_id INT,
                    start_date DATE,
                    end_date DATE,
                    reason TEXT,
                    status VARCHAR(20) DEFAULT 'Pending'
                );
            """;

            String familyDetails = """
                CREATE TABLE IF NOT EXISTS family_details (
                    family_id SERIAL PRIMARY KEY,
                    employee_id INT NOT NULL,
                    member_name VARCHAR(100) NOT NULL,
                    relationship VARCHAR(50),
                    age INT
                );
            """;

            stmt.execute(employees);
            stmt.execute(hrStaff);
            stmt.execute(employeeLogin);
            stmt.execute(leaveBalance);
            stmt.execute(leaveRequests);
            stmt.execute(familyDetails);

            System.out.println("Database initialized successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
