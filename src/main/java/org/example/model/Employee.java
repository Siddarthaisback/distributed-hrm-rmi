package org.example.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Employee implements Serializable {

    private int employeeId;
    private String firstName;
    private String lastName;
    private String passportNo;
    private String email;
    private String phone;
    private String department;
    private String position;
    private LocalDate joinDate;

    private String username;
    private String password;
    private int totalLeave = 20; // default, HR can override at registration

    public Employee() {
    }

    public Employee(int employeeId, String firstName, String lastName,
                    String passportNo, String email, String phone,
                    String department, String position, LocalDate joinDate,
                    String username, String password) {

        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNo = passportNo;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.position = position;
        this.joinDate = joinDate;
        this.username = username;
        this.password = password;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTotalLeave() {
        return totalLeave;
    }

    public void setTotalLeave(int totalLeave) {
        this.totalLeave = totalLeave;
    }
}