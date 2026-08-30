package org.example.database;

import org.example.model.EmployeeLeaveReport;
import org.example.model.LeaveRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    public boolean applyLeave(int employeeId, String startDate, String endDate, String reason) {

        // 1. Check employee has enough remaining balance
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);
        long requestedDays = ChronoUnit.DAYS.between(start, end) + 1;

        int remaining = checkLeaveBalance(employeeId);
        if (remaining < requestedDays) {
            return false; // not enough balance
        }

        String query = "INSERT INTO leave_requests(employee_id,start_date,end_date,reason,status) VALUES (?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, employeeId);
            ps.setDate(2, java.sql.Date.valueOf(startDate));
            ps.setDate(3, java.sql.Date.valueOf(endDate));
            ps.setString(4, reason);
            ps.setString(5, "Pending");

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int checkLeaveBalance(int employeeId) {

        String query = "SELECT remaining_leave FROM leave_balance WHERE employee_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, employeeId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("remaining_leave");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getActiveLeaveDaysRemaining(int employeeId) {
        String query = """
                SELECT start_date, end_date
                FROM leave_requests
                WHERE employee_id = ?
                  AND status = 'Approved'
                  AND CURRENT_DATE BETWEEN start_date AND end_date
                ORDER BY end_date ASC
                LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate today = LocalDate.now();
                    LocalDate endDate = rs.getDate("end_date").toLocalDate();
                    long daysRemaining = ChronoUnit.DAYS.between(today, endDate) + 1;
                    return (int) Math.max(daysRemaining, 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean updateLeaveStatus(int leaveId, String status) {

        // First fetch the leave request details so we know who and how many days
        String fetchQuery = "SELECT employee_id, start_date, end_date, status FROM leave_requests WHERE leave_id=?";
        int    employeeId   = -1;
        long   days         = 0;
        String currentStatus = "";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(fetchQuery)) {
            ps.setInt(1, leaveId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                employeeId    = rs.getInt("employee_id");
                currentStatus = rs.getString("status");
                LocalDate start = rs.getDate("start_date").toLocalDate();
                LocalDate end   = rs.getDate("end_date").toLocalDate();
                days = ChronoUnit.DAYS.between(start, end) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (employeeId == -1) return false; // leave not found

        String updateQuery = "UPDATE leave_requests SET status=? WHERE leave_id=?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // Update the leave status
            try (PreparedStatement ps = con.prepareStatement(updateQuery)) {
                ps.setString(1, status);
                ps.setInt(2, leaveId);
                int rows = ps.executeUpdate();
                if (rows == 0) { con.rollback(); return false; }
            }

            // Deduct balance when Approved (only if it was Pending before)
            if (status.equals("Approved") && currentStatus.equals("Pending")) {
                String deduct = """
                        UPDATE leave_balance
                        SET used_leave      = used_leave + ?,
                            remaining_leave = GREATEST(remaining_leave - ?, 0)
                        WHERE employee_id   = ?
                        """;
                try (PreparedStatement ps = con.prepareStatement(deduct)) {
                    ps.setLong(1, days);
                    ps.setLong(2, days);
                    ps.setInt(3, employeeId);
                    ps.executeUpdate();
                }
            }

            // Restore balance when Rejected (only if it was Approved before)
            if (status.equals("Rejected") && currentStatus.equals("Approved")) {
                String restore = """
                        UPDATE leave_balance
                        SET used_leave      = GREATEST(used_leave - ?, 0),
                            remaining_leave = remaining_leave + ?
                        WHERE employee_id   = ?
                        """;
                try (PreparedStatement ps = con.prepareStatement(restore)) {
                    ps.setLong(1, days);
                    ps.setLong(2, days);
                    ps.setInt(3, employeeId);
                    ps.executeUpdate();
                }
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<LeaveRequest> getAllLeaveRequests() {

        List<LeaveRequest> leaveRequests = new ArrayList<>();

        String query = "SELECT * FROM leave_requests ORDER BY leave_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LeaveRequest leave = new LeaveRequest();
                leave.setLeaveId(rs.getInt("leave_id"));
                leave.setEmployeeId(rs.getInt("employee_id"));
                leave.setStartDate(rs.getDate("start_date").toLocalDate());
                leave.setEndDate(rs.getDate("end_date").toLocalDate());
                leave.setReason(rs.getString("reason"));
                leave.setStatus(rs.getString("status"));

                leaveRequests.add(leave);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return leaveRequests;
    }

    public List<LeaveRequest> getLeaveRequestsByEmployeeId(int employeeId) {

        List<LeaveRequest> leaveRequests = new ArrayList<>();

        String query = "SELECT * FROM leave_requests WHERE employee_id=? ORDER BY leave_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, employeeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LeaveRequest leave = new LeaveRequest();
                leave.setLeaveId(rs.getInt("leave_id"));
                leave.setEmployeeId(rs.getInt("employee_id"));
                leave.setStartDate(rs.getDate("start_date").toLocalDate());
                leave.setEndDate(rs.getDate("end_date").toLocalDate());
                leave.setReason(rs.getString("reason"));
                leave.setStatus(rs.getString("status"));

                leaveRequests.add(leave);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return leaveRequests;
    }

    public List<EmployeeLeaveReport> getYearlyLeaveReport(int year) {

        List<EmployeeLeaveReport> reports = new ArrayList<>();

        String query = """
                SELECT
                    e.employee_id,
                    e.first_name || ' ' || e.last_name AS employee_name,
                    COUNT(lr.leave_id) AS total_leaves,
                    COUNT(CASE WHEN lr.status = 'Approved' THEN 1 END) AS approved_leaves,
                    COUNT(CASE WHEN lr.status = 'Rejected' THEN 1 END) AS rejected_leaves,
                    COUNT(CASE WHEN lr.status = 'Pending' THEN 1 END) AS pending_leaves
                FROM employees e
                LEFT JOIN leave_requests lr
                    ON e.employee_id = lr.employee_id
                    AND EXTRACT(YEAR FROM lr.start_date) = ?
                GROUP BY e.employee_id, e.first_name, e.last_name
                ORDER BY e.employee_id
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, year);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EmployeeLeaveReport report = new EmployeeLeaveReport();
                report.setEmployeeId(rs.getInt("employee_id"));
                report.setEmployeeName(rs.getString("employee_name"));
                report.setTotalLeaves(rs.getInt("total_leaves"));
                report.setApprovedLeaves(rs.getInt("approved_leaves"));
                report.setRejectedLeaves(rs.getInt("rejected_leaves"));
                report.setPendingLeaves(rs.getInt("pending_leaves"));

                reports.add(report);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reports;
    }
    public boolean updateLeaveAllowance(int employeeId, int newTotalDays) {
        String query = """
                UPDATE leave_balance
                SET total_leave     = ?,
                    remaining_leave = GREATEST(? - used_leave, 0)
                WHERE employee_id   = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, newTotalDays);
            ps.setInt(2, newTotalDays);
            ps.setInt(3, employeeId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
