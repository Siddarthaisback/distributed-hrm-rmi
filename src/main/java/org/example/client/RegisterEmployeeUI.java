package org.example.client;

import org.example.model.Employee;
import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class RegisterEmployeeUI extends JFrame {

    private JTextField    firstName, lastName, passport, email, phone, department, position, username, leaveDays;
    private JPasswordField password;

    public RegisterEmployeeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "Register Employee", 720, 580);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(UITheme.createHeader("Register Employee", "Create a new employee record and login credential."), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(16, 20, 0, 20));
        root.add(content, BorderLayout.CENTER);

        JPanel card = UITheme.createCardPanel(new BorderLayout(0, 16));

        // Two-column form
        JPanel form = new JPanel(new GridLayout(10, 2, 14, 12));
        form.setOpaque(false);

        form.add(UITheme.createSectionLabel("First Name"));   firstName  = UITheme.createTextField(); form.add(firstName);
        form.add(UITheme.createSectionLabel("Last Name"));    lastName   = UITheme.createTextField(); form.add(lastName);
        form.add(UITheme.createSectionLabel("Passport No"));  passport   = UITheme.createTextField(); form.add(passport);
        form.add(UITheme.createSectionLabel("Email"));        email      = UITheme.createTextField(); form.add(email);
        form.add(UITheme.createSectionLabel("Phone"));        phone      = UITheme.createTextField(); form.add(phone);
        form.add(UITheme.createSectionLabel("Department"));   department = UITheme.createTextField(); form.add(department);
        form.add(UITheme.createSectionLabel("Position"));     position   = UITheme.createTextField(); form.add(position);
        form.add(UITheme.createSectionLabel("Username"));     username   = UITheme.createTextField(); form.add(username);
        form.add(UITheme.createSectionLabel("Password"));     password   = UITheme.createPasswordField(); form.add(password);
        form.add(UITheme.createSectionLabel("Leave Days Allowed")); leaveDays = UITheme.createTextField(); leaveDays.setText("20"); form.add(leaveDays);

        JLabel note = new JLabel("ℹ  Leave days can be changed anytime by HR from the employee management panel.");
        note.setFont(UITheme.F_SMALL); note.setForeground(UITheme.MUTED);

        card.add(form, BorderLayout.CENTER);
        card.add(note, BorderLayout.SOUTH);
        content.add(card, BorderLayout.CENTER);

        JPanel actions = UITheme.createActionsPanel();
        JButton backBtn = UITheme.createSecondaryButton("← Back");
        JButton saveBtn = UITheme.createPrimaryButton("Register Employee");
        actions.add(backBtn); actions.add(saveBtn);
        root.add(actions, BorderLayout.SOUTH);

        add(root);
        saveBtn.addActionListener(e -> registerEmployee());
        backBtn.addActionListener(e -> { dispose(); new HRDashboard(); });
        setVisible(true);
    }

    private void registerEmployee() {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { UITheme.error(this, "Cannot connect to RMI server."); return; }

            Employee emp = new Employee();
            emp.setFirstName(firstName.getText().trim());
            emp.setLastName(lastName.getText().trim());
            emp.setPassportNo(passport.getText().trim());
            emp.setEmail(email.getText().trim());
            emp.setPhone(phone.getText().trim());
            emp.setDepartment(department.getText().trim());
            emp.setPosition(position.getText().trim());
            emp.setUsername(username.getText().trim());
            emp.setPassword(new String(password.getPassword()).trim());

            int total = 20;
            try { total = Integer.parseInt(leaveDays.getText().trim()); } catch (NumberFormatException ignored) {}
            emp.setTotalLeave(total);

            if (emp.getFirstName().isEmpty() || emp.getLastName().isEmpty()
                    || emp.getPassportNo().isEmpty() || emp.getUsername().isEmpty()
                    || emp.getPassword().isEmpty()) {
                UITheme.error(this, "Please fill all required fields."); return;
            }

            boolean ok = svc.registerEmployee(emp);
            if (ok) { UITheme.info(this, "Employee registered successfully."); clearFields(); }
            else       UITheme.error(this, "Registration failed. Username may already exist.");
        } catch (Exception e) { e.printStackTrace(); UITheme.error(this, "Error occurred during registration."); }
    }

    private void clearFields() {
        for (JTextField f : new JTextField[]{firstName,lastName,passport,email,phone,department,position,username,leaveDays})
            f.setText("");
        leaveDays.setText("20");
        password.setText("");
    }
}
