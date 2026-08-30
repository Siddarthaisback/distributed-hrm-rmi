package org.example.model;

import java.io.Serializable;

public class FamilyDetails implements Serializable {

    private int familyId;
    private int employeeId;
    private String memberName;
    private String relationship;
    private int age;

    public FamilyDetails() {
    }

    public FamilyDetails(int familyId, int employeeId,
                         String memberName, String relationship, int age) {

        this.familyId = familyId;
        this.employeeId = employeeId;
        this.memberName = memberName;
        this.relationship = relationship;
        this.age = age;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}