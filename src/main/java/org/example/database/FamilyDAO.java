package org.example.database;

import org.example.model.FamilyDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FamilyDAO {

    public boolean addFamilyMember(FamilyDetails familyDetails) {
        String query = """
                INSERT INTO family_details(employee_id, member_name, relationship, age)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, familyDetails.getEmployeeId());
            ps.setString(2, familyDetails.getMemberName());
            ps.setString(3, familyDetails.getRelationship());
            ps.setInt(4, familyDetails.getAge());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<FamilyDetails> getFamilyDetailsByEmployeeId(int employeeId) {
        List<FamilyDetails> familyList = new ArrayList<>();

        String query = "SELECT * FROM family_details WHERE employee_id=? ORDER BY family_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, employeeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                FamilyDetails family = new FamilyDetails();
                family.setFamilyId(rs.getInt("family_id"));
                family.setEmployeeId(rs.getInt("employee_id"));
                family.setMemberName(rs.getString("member_name"));
                family.setRelationship(rs.getString("relationship"));
                family.setAge(rs.getInt("age"));
                familyList.add(family);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return familyList;
    }

    public boolean updateFamilyMember(FamilyDetails familyDetails) {
        String query = """
                UPDATE family_details
                SET member_name=?, relationship=?, age=?
                WHERE family_id=?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, familyDetails.getMemberName());
            ps.setString(2, familyDetails.getRelationship());
            ps.setInt(3, familyDetails.getAge());
            ps.setInt(4, familyDetails.getFamilyId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteFamilyMember(int familyId) {
        String query = "DELETE FROM family_details WHERE family_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, familyId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
