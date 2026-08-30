package org.example.server;

import org.example.database.EmployeeDAO;
import org.example.database.FamilyDAO;
import org.example.database.LeaveDAO;
import org.example.model.Employee;
import org.example.model.EmployeeLeaveReport;
import org.example.model.FamilyDetails;
import org.example.model.LeaveRequest;
import org.example.model.YearlyEmployeeReport;
import org.example.remote.HRMService;
import org.example.security.HRMClientSocketFactory;
import org.example.security.HRMServerSocketFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class HRMServiceImpl extends UnicastRemoteObject implements HRMService {

    private final EmployeeDAO employeeDAO;
    private final LeaveDAO    leaveDAO;
    private final FamilyDAO   familyDAO;

    public HRMServiceImpl() throws RemoteException {
        // Export over SSL instead of plain TCP
        super(0, new HRMClientSocketFactory(), new HRMServerSocketFactory());
        employeeDAO = new EmployeeDAO();
        leaveDAO    = new LeaveDAO();
        familyDAO   = new FamilyDAO();
    }

    @Override
    public boolean registerEmployee(Employee employee) throws RemoteException {
        return employeeDAO.addEmployee(employee);
    }

    @Override
    public Employee getEmployee(int employeeId) throws RemoteException {
        return employeeDAO.getEmployeeById(employeeId);
    }

    @Override
    public boolean updateEmployeeProfile(Employee employee) throws RemoteException {
        return employeeDAO.updateEmployeeProfile(employee);
    }

    @Override
    public boolean deleteEmployee(int employeeId) throws RemoteException {
        return employeeDAO.deleteEmployee(employeeId);
    }

    @Override
    public List<Employee> getAllEmployees() throws RemoteException {
        return employeeDAO.getAllEmployees();
    }

    @Override
    public boolean applyLeave(LeaveRequest leave) throws RemoteException {
        return leaveDAO.applyLeave(
                leave.getEmployeeId(),
                leave.getStartDate().toString(),
                leave.getEndDate().toString(),
                leave.getReason()
        );
    }

    @Override
    public int checkLeaveBalance(int employeeId) throws RemoteException {
        return leaveDAO.checkLeaveBalance(employeeId);
    }

    @Override
    public int getActiveLeaveDaysRemaining(int employeeId) throws RemoteException {
        return leaveDAO.getActiveLeaveDaysRemaining(employeeId);
    }

    @Override
    public boolean updateLeaveStatus(int leaveId, String status) throws RemoteException {
        return leaveDAO.updateLeaveStatus(leaveId, status);
    }

    @Override
    public List<LeaveRequest> getAllLeaveRequests() throws RemoteException {
        return leaveDAO.getAllLeaveRequests();
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByEmployeeId(int employeeId) throws RemoteException {
        return leaveDAO.getLeaveRequestsByEmployeeId(employeeId);
    }

    @Override
    public List<EmployeeLeaveReport> getYearlyLeaveReport(int year) throws RemoteException {
        return leaveDAO.getYearlyLeaveReport(year);
    }

    @Override
    public boolean addFamilyMember(FamilyDetails familyDetails) throws RemoteException {
        return familyDAO.addFamilyMember(familyDetails);
    }

    @Override
    public List<FamilyDetails> getFamilyDetailsByEmployeeId(int employeeId) throws RemoteException {
        return familyDAO.getFamilyDetailsByEmployeeId(employeeId);
    }

    @Override
    public boolean updateFamilyMember(FamilyDetails familyDetails) throws RemoteException {
        return familyDAO.updateFamilyMember(familyDetails);
    }

    @Override
    public boolean deleteFamilyMember(int familyId) throws RemoteException {
        return familyDAO.deleteFamilyMember(familyId);
    }

    @Override
    public YearlyEmployeeReport getYearlyEmployeeFullReport(int employeeId, int year) throws RemoteException {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        List<FamilyDetails> familyDetails = familyDAO.getFamilyDetailsByEmployeeId(employeeId);
        List<LeaveRequest> allLeaves = leaveDAO.getLeaveRequestsByEmployeeId(employeeId);
        List<LeaveRequest> yearlyLeaves = new ArrayList<>();

        for (LeaveRequest leave : allLeaves) {
            if (leave.getStartDate() != null && leave.getStartDate().getYear() == year) {
                yearlyLeaves.add(leave);
            }
        }

        return new YearlyEmployeeReport(employee, familyDetails, yearlyLeaves, year);
    }
    @Override
    public boolean updateLeaveAllowance(int employeeId, int newTotalDays) throws RemoteException {
        return leaveDAO.updateLeaveAllowance(employeeId, newTotalDays);
    }
}