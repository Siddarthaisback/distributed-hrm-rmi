package org.example.client;

import org.example.model.Employee;
import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class EmployeeProfileUI extends JFrame {

    private final int employeeId;
    private JTextField employeeIdField, firstNameField, lastNameField, passportField,
                       emailField, phoneField, departmentField, positionField, joinDateField;

    public EmployeeProfileUI(int employeeId) {
        this.employeeId = employeeId;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "Update Profile", 720, 580);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(UITheme.createHeader("My Profile", "View and update your personal information."), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(16, 20, 0, 20));
        root.add(content, BorderLayout.CENTER);

        JPanel card = UITheme.createCardPanel(new BorderLayout(0, 18));

        // ── Two-column form using GridBagLayout ───────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(6, 6, 6, 6);
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.anchor  = GridBagConstraints.WEST;

        employeeIdField  = UITheme.createTextField(); lockField(employeeIdField);
        firstNameField   = UITheme.createTextField();
        lastNameField    = UITheme.createTextField();
        passportField    = UITheme.createTextField();
        emailField       = UITheme.createTextField();
        phoneField       = UITheme.createTextField();
        departmentField  = UITheme.createTextField(); lockField(departmentField);
        positionField    = UITheme.createTextField(); lockField(positionField);
        joinDateField    = UITheme.createTextField(); lockField(joinDateField);

        // Row 0 — Employee ID (full width, read-only, so make it span)
        addRow(form, gc, 0, "Employee ID", employeeIdField, "First Name", firstNameField);
        addRow(form, gc, 1, "Last Name",   lastNameField,   "Passport No", passportField);
        addRow(form, gc, 2, "Email",       emailField,      "Phone",       phoneField);

        // Divider label
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 4; gc.insets = new Insets(14, 6, 4, 6);
        JLabel divider = new JLabel("HR-MANAGED FIELDS");
        divider.setFont(UITheme.F_MICRO);
        divider.setForeground(UITheme.MUTED);
        divider.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, UITheme.BORDER),
            new EmptyBorder(10, 0, 0, 0)));
        form.add(divider, gc);
        gc.gridwidth = 1; gc.insets = new Insets(6, 6, 6, 6);

        addRow(form, gc, 4, "Department 🔒", departmentField, "Position 🔒", positionField);

        // Join date — single field, left column only
        gc.gridx = 0; gc.gridy = 5; gc.weightx = 0;
        form.add(lockedLabel("Join Date 🔒"), gc);
        gc.gridx = 1; gc.weightx = 1;
        form.add(joinDateField, gc);
        // spacer in right column
        gc.gridx = 2; gc.weightx = 0;
        form.add(new JLabel(""), gc);
        gc.gridx = 3; gc.weightx = 1;
        form.add(new JLabel(""), gc);

        // Notice
        JLabel notice = new JLabel("🔒  Department, Position, and Join Date can only be changed by HR.");
        notice.setFont(UITheme.F_SMALL); notice.setForeground(UITheme.MUTED);

        card.add(form,   BorderLayout.CENTER);
        card.add(notice, BorderLayout.SOUTH);
        content.add(card, BorderLayout.CENTER);

        JPanel actions = UITheme.createActionsPanel();
        JButton backBtn   = UITheme.createSecondaryButton("← Back");
        JButton updateBtn = UITheme.createPrimaryButton("💾  Update Profile");
        actions.add(backBtn); actions.add(updateBtn);
        root.add(actions, BorderLayout.SOUTH);

        add(root);
        loadEmployeeDetails();
        updateBtn.addActionListener(e -> updateProfile());
        backBtn.addActionListener(e -> { dispose(); new EmployeeDashboard(employeeId); });
        setVisible(true);
    }

    /** Add a label+field pair in columns 0-1, and another pair in columns 2-3. */
    private void addRow(JPanel form, GridBagConstraints gc, int row,
                        String label1, JTextField field1,
                        String label2, JTextField field2) {
        gc.gridy = row;
        gc.gridx = 0; gc.weightx = 0;
        form.add(UITheme.createSectionLabel(label1), gc);
        gc.gridx = 1; gc.weightx = 1;
        form.add(field1, gc);
        gc.gridx = 2; gc.weightx = 0;
        form.add(UITheme.createSectionLabel(label2), gc);
        gc.gridx = 3; gc.weightx = 1;
        form.add(field2, gc);
    }

    private void lockField(JTextField f) {
        f.setEditable(false);
        f.setBackground(UITheme.SURFACE2);
        f.setForeground(UITheme.MUTED);
    }

    private JLabel lockedLabel(String text) {
        JLabel l = UITheme.createSectionLabel(text);
        l.setForeground(UITheme.MUTED);
        return l;
    }

    private void loadEmployeeDetails() {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { JOptionPane.showMessageDialog(this, "Cannot connect to RMI server."); return; }
            Employee emp = svc.getEmployee(employeeId);
            if (emp == null) { JOptionPane.showMessageDialog(this, "Employee record not found."); return; }
            employeeIdField.setText(String.valueOf(emp.getEmployeeId()));
            firstNameField.setText(emp.getFirstName());
            lastNameField.setText(emp.getLastName());
            passportField.setText(emp.getPassportNo());
            emailField.setText(emp.getEmail());
            phoneField.setText(emp.getPhone());
            departmentField.setText(emp.getDepartment());
            positionField.setText(emp.getPosition());
            joinDateField.setText(emp.getJoinDate() != null ? emp.getJoinDate().toString() : "");
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Failed to load employee profile."); }
    }

    private void updateProfile() {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { JOptionPane.showMessageDialog(this, "Cannot connect to RMI server."); return; }
            String fn = firstNameField.getText().trim(), ln = lastNameField.getText().trim(), pp = passportField.getText().trim();
            if (fn.isEmpty() || ln.isEmpty() || pp.isEmpty()) { JOptionPane.showMessageDialog(this, "First Name, Last Name, and Passport No are required."); return; }
            Employee emp = new Employee();
            emp.setEmployeeId(employeeId); emp.setFirstName(fn); emp.setLastName(ln); emp.setPassportNo(pp);
            emp.setEmail(emailField.getText().trim()); emp.setPhone(phoneField.getText().trim());
            emp.setDepartment(departmentField.getText()); emp.setPosition(positionField.getText());
            boolean ok = svc.updateEmployeeProfile(emp);
            if (ok) { JOptionPane.showMessageDialog(this, "Profile updated successfully."); loadEmployeeDetails(); }
            else       JOptionPane.showMessageDialog(this, "Failed to update profile.");
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error occurred while updating profile."); }
    }
}
