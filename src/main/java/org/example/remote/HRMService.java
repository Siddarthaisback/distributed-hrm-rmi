package org.example.remote;

import org.example.model.Employee;
import org.example.model.EmployeeLeaveReport;
import org.example.model.FamilyDetails;
import org.example.model.LeaveRequest;
import org.example.model.YearlyEmployeeReport;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface HRMService extends Remote {

    boolean registerEmployee(Employee employee) throws RemoteException;

    Employee getEmployee(int employeeId) throws RemoteException;

    boolean updateEmployeeProfile(Employee employee) throws RemoteException;

    boolean deleteEmployee(int employeeId) throws RemoteException;

    List<Employee> getAllEmployees() throws RemoteException;

    boolean applyLeave(LeaveRequest leave) throws RemoteException;

    int checkLeaveBalance(int employeeId) throws RemoteException;

    int getActiveLeaveDaysRemaining(int employeeId) throws RemoteException;

    boolean updateLeaveStatus(int leaveId, String status) throws RemoteException;

    List<LeaveRequest> getAllLeaveRequests() throws RemoteException;

    List<LeaveRequest> getLeaveRequestsByEmployeeId(int employeeId) throws RemoteException;

    List<EmployeeLeaveReport> getYearlyLeaveReport(int year) throws RemoteException;

    boolean addFamilyMember(FamilyDetails familyDetails) throws RemoteException;

    List<FamilyDetails> getFamilyDetailsByEmployeeId(int employeeId) throws RemoteException;

    boolean updateFamilyMember(FamilyDetails familyDetails) throws RemoteException;

    boolean deleteFamilyMember(int familyId) throws RemoteException;

    YearlyEmployeeReport getYearlyEmployeeFullReport(int employeeId, int year) throws RemoteException;

    boolean updateLeaveAllowance(int employeeId, int newTotalDays) throws RemoteException;
}
