package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class YearlyEmployeeReport implements Serializable {

    private Employee employee;
    private List<FamilyDetails> familyDetails;
    private List<LeaveRequest> leaveHistory;
    private int reportYear;

    public YearlyEmployeeReport() {
        this.familyDetails = new ArrayList<>();
        this.leaveHistory = new ArrayList<>();
    }

    public YearlyEmployeeReport(Employee employee, List<FamilyDetails> familyDetails,
                                List<LeaveRequest> leaveHistory, int reportYear) {
        this.employee = employee;
        this.familyDetails = familyDetails;
        this.leaveHistory = leaveHistory;
        this.reportYear = reportYear;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<FamilyDetails> getFamilyDetails() {
        return familyDetails;
    }

    public void setFamilyDetails(List<FamilyDetails> familyDetails) {
        this.familyDetails = familyDetails;
    }

    public List<LeaveRequest> getLeaveHistory() {
        return leaveHistory;
    }

    public void setLeaveHistory(List<LeaveRequest> leaveHistory) {
        this.leaveHistory = leaveHistory;
    }

    public int getReportYear() {
        return reportYear;
    }

    public void setReportYear(int reportYear) {
        this.reportYear = reportYear;
    }
}
