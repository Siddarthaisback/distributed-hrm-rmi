package org.example.client;

import org.example.model.FamilyDetails;
import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class FamilyDetailsUI extends JFrame {

    private final int employeeId;
    private JTextField memberNameField, relationshipField, ageField;
    private JTable table;
    private DefaultTableModel model;
    private int selectedFamilyId = -1;

    public FamilyDetailsUI(int employeeId) {
        this.employeeId = employeeId;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "Manage Family Details", 820, 580);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(UITheme.createHeader("Family Details", "Manage your family member records."), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(16, 20, 0, 20));
        root.add(content, BorderLayout.CENTER);

        // ── Form card (top) — fields in a proper row with full labels ──────────
        JPanel formCard = UITheme.createCardPanel(new BorderLayout(0, 12));

        // Fields row using GridBagLayout so labels never truncate
        JPanel fieldsRow = new JPanel(new GridBagLayout());
        fieldsRow.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(0, 0, 0, 12);
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.anchor  = GridBagConstraints.WEST;

        memberNameField   = UITheme.createTextField();
        relationshipField = UITheme.createTextField();
        ageField          = UITheme.createTextField();

        // Member Name — gets the most space
        gc.gridx = 0; gc.weightx = 0; gc.gridy = 0;
        fieldsRow.add(makeLabel("Member Name"), gc);
        gc.gridx = 1; gc.weightx = 2.0;
        fieldsRow.add(memberNameField, gc);

        // Relationship
        gc.gridx = 2; gc.weightx = 0;
        fieldsRow.add(makeLabel("Relationship"), gc);
        gc.gridx = 3; gc.weightx = 1.5;
        fieldsRow.add(relationshipField, gc);

        // Age — narrow
        gc.gridx = 4; gc.weightx = 0; gc.insets = new Insets(0, 0, 0, 12);
        fieldsRow.add(makeLabel("Age"), gc);
        gc.gridx = 5; gc.weightx = 0.5; gc.insets = new Insets(0, 0, 0, 0);
        ageField.setPreferredSize(new Dimension(70, 40));
        fieldsRow.add(ageField, gc);

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton clearBtn  = UITheme.createSecondaryButton("Clear");
        JButton addBtn    = UITheme.createSuccessButton("+ Add");
        JButton updateBtn = UITheme.createPrimaryButton("✎  Update");
        JButton deleteBtn = UITheme.createDangerButton("✗  Delete");
        btnRow.add(clearBtn); btnRow.add(addBtn); btnRow.add(updateBtn); btnRow.add(deleteBtn);

        formCard.add(fieldsRow, BorderLayout.CENTER);
        formCard.add(btnRow,    BorderLayout.SOUTH);
        content.add(formCard, BorderLayout.NORTH);

        // ── Table card ─────────────────────────────────────────────────────────
        String[] columns = {"ID", "Member Name", "Relationship", "Age"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);

        JPanel tableCard = UITheme.createCardPanel(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel cardHdr = new JPanel(new BorderLayout());
        cardHdr.setBackground(UITheme.CARD);
        cardHdr.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(12, 18, 12, 18)));
        JLabel ttl = new JLabel("Family Members");
        ttl.setFont(UITheme.F_H2); ttl.setForeground(UITheme.SECONDARY);
        cardHdr.add(ttl, BorderLayout.WEST);

        // Row count badge
        JLabel countLbl = new JLabel("0 members");
        countLbl.setFont(UITheme.F_SMALL); countLbl.setForeground(UITheme.MUTED);
        cardHdr.add(countLbl, BorderLayout.EAST);

        tableCard.add(cardHdr, BorderLayout.NORTH);
        tableCard.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        content.add(tableCard, BorderLayout.CENTER);

        // ── Bottom actions ─────────────────────────────────────────────────────
        JPanel actions = UITheme.createActionsPanel();
        JButton backBtn    = UITheme.createSecondaryButton("← Back");
        JButton refreshBtn = UITheme.createPrimaryButton("⟳  Refresh");
        actions.add(backBtn); actions.add(refreshBtn);
        root.add(actions, BorderLayout.SOUTH);

        add(root);
        loadFamilyDetails(countLbl);

        // Select row → fill fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedFamilyId = Integer.parseInt(model.getValueAt(row, 0).toString());
                memberNameField.setText(model.getValueAt(row, 1).toString());
                relationshipField.setText(model.getValueAt(row, 2).toString());
                ageField.setText(model.getValueAt(row, 3).toString());
            }
        });

        addBtn.addActionListener(e    -> addFamilyMember(countLbl));
        updateBtn.addActionListener(e -> updateFamilyMember(countLbl));
        deleteBtn.addActionListener(e -> deleteFamilyMember(countLbl));
        clearBtn.addActionListener(e  -> clearFields());
        refreshBtn.addActionListener(e-> loadFamilyDetails(countLbl));
        backBtn.addActionListener(e   -> { dispose(); new EmployeeDashboard(employeeId); });
        setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_BOLD); l.setForeground(UITheme.TEXT);
        return l;
    }

    private void loadFamilyDetails(JLabel countLbl) {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { JOptionPane.showMessageDialog(this, "Cannot connect to RMI server."); return; }
            List<FamilyDetails> list = svc.getFamilyDetailsByEmployeeId(employeeId);
            model.setRowCount(0);
            for (FamilyDetails f : list)
                model.addRow(new Object[]{f.getFamilyId(), f.getMemberName(), f.getRelationship(), f.getAge()});
            countLbl.setText(list.size() + " member" + (list.size() == 1 ? "" : "s"));
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Failed to load family details."); }
    }

    private void addFamilyMember(JLabel countLbl) {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { JOptionPane.showMessageDialog(this, "Cannot connect to RMI server."); return; }
            String n = memberNameField.getText().trim(), r = relationshipField.getText().trim(), a = ageField.getText().trim();
            if (n.isEmpty() || r.isEmpty() || a.isEmpty()) { JOptionPane.showMessageDialog(this, "Please fill all fields."); return; }
            FamilyDetails f = new FamilyDetails();
            f.setEmployeeId(employeeId); f.setMemberName(n); f.setRelationship(r); f.setAge(Integer.parseInt(a));
            boolean ok = svc.addFamilyMember(f);
            if (ok) { JOptionPane.showMessageDialog(this, "Family member added."); clearFields(); loadFamilyDetails(countLbl); }
            else      JOptionPane.showMessageDialog(this, "Failed to add family member.");
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Please enter a valid age.");
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error adding family member."); }
    }

    private void updateFamilyMember(JLabel countLbl) {
        try {
            if (selectedFamilyId == -1) { JOptionPane.showMessageDialog(this, "Please select a family member."); return; }
            HRMService svc = HRMClient.getService();
            if (svc == null) { JOptionPane.showMessageDialog(this, "Cannot connect to RMI server."); return; }
            String n = memberNameField.getText().trim(), r = relationshipField.getText().trim(), a = ageField.getText().trim();
            if (n.isEmpty() || r.isEmpty() || a.isEmpty()) { JOptionPane.showMessageDialog(this, "Please fill all fields."); return; }
            FamilyDetails f = new FamilyDetails();
            f.setFamilyId(selectedFamilyId); f.setEmployeeId(employeeId);
            f.setMemberName(n); f.setRelationship(r); f.setAge(Integer.parseInt(a));
            boolean ok = svc.updateFamilyMember(f);
            if (ok) { JOptionPane.showMessageDialog(this, "Family member updated."); clearFields(); loadFamilyDetails(countLbl); }
            else      JOptionPane.showMessageDialog(this, "Failed to update family member.");
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Please enter a valid age.");
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error updating family member."); }
    }

    private void deleteFamilyMember(JLabel countLbl) {
        try {
            if (selectedFamilyId == -1) { JOptionPane.showMessageDialog(this, "Please select a family member."); return; }
            int c = JOptionPane.showConfirmDialog(this, "Delete this family member?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            HRMService svc = HRMClient.getService();
            if (svc == null) { JOptionPane.showMessageDialog(this, "Cannot connect to RMI server."); return; }
            boolean ok = svc.deleteFamilyMember(selectedFamilyId);
            if (ok) { JOptionPane.showMessageDialog(this, "Family member deleted."); clearFields(); loadFamilyDetails(countLbl); }
            else      JOptionPane.showMessageDialog(this, "Failed to delete family member.");
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error deleting family member."); }
    }

    private void clearFields() {
        selectedFamilyId = -1;
        memberNameField.setText(""); relationshipField.setText(""); ageField.setText("");
        table.clearSelection();
    }
}
