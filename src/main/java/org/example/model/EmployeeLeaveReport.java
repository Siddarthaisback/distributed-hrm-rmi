package org.example.model;

import java.io.Serializable;

public class EmployeeLeaveReport implements Serializable {

    private int employeeId;
    private String employeeName;
    private int totalLeaves;
    private int approvedLeaves;
    private int rejectedLeaves;
    private int pendingLeaves;

    public EmployeeLeaveReport() {
    }

    public EmployeeLeaveReport(int employeeId, String employeeName, int totalLeaves,
                               int approvedLeaves, int rejectedLeaves, int pendingLeaves) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.totalLeaves = totalLeaves;
        this.approvedLeaves = approvedLeaves;
        this.rejectedLeaves = rejectedLeaves;
        this.pendingLeaves = pendingLeaves;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getTotalLeaves() {
        return totalLeaves;
    }

    public void setTotalLeaves(int totalLeaves) {
        this.totalLeaves = totalLeaves;
    }

    public int getApprovedLeaves() {
        return approvedLeaves;
    }

    public void setApprovedLeaves(int approvedLeaves) {
        this.approvedLeaves = approvedLeaves;
    }

    public int getRejectedLeaves() {
        return rejectedLeaves;
    }

    public void setRejectedLeaves(int rejectedLeaves) {
        this.rejectedLeaves = rejectedLeaves;
    }

    public int getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(int pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }
}