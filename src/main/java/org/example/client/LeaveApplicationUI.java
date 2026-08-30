package org.example.client;

import org.example.model.LeaveRequest;
import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LeaveApplicationUI extends JFrame {

    private final int employeeId;
    private JTextField startDate, endDate;
    private JTextArea  reason;

    public LeaveApplicationUI(int employeeId) {
        this.employeeId = employeeId;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "Apply Leave", 640, 440);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(UITheme.createHeader("Apply for Leave", "Enter your requested leave period and reason."), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(16, 20, 0, 20));
        root.add(content, BorderLayout.CENTER);

        JPanel card = UITheme.createCardPanel(new BorderLayout(0, 18));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        // Start date
        gc.gridx=0; gc.gridy=0; gc.weightx=0;
        form.add(UITheme.createSectionLabel("Start Date (YYYY-MM-DD)"), gc);
        gc.gridx=1; gc.weightx=1;
        startDate = UITheme.createTextField();
        form.add(startDate, gc);

        // End date
        gc.gridx=0; gc.gridy=1; gc.weightx=0;
        form.add(UITheme.createSectionLabel("End Date (YYYY-MM-DD)"), gc);
        gc.gridx=1; gc.weightx=1;
        endDate = UITheme.createTextField();
        form.add(endDate, gc);

        // Reason
        gc.gridx=0; gc.gridy=2; gc.anchor=GridBagConstraints.NORTHWEST; gc.weightx=0;
        form.add(UITheme.createSectionLabel("Reason"), gc);
        gc.gridx=1; gc.weightx=1;
        reason = UITheme.createTextArea(5,20);
        JScrollPane rs = new JScrollPane(reason);
        rs.setBorder(new LineBorder(UITheme.BORDER,1,true));
        form.add(rs, gc);

        card.add(form, BorderLayout.CENTER);
        content.add(card, BorderLayout.CENTER);

        JPanel actions = UITheme.createActionsPanel();
        JButton backBtn   = UITheme.createSecondaryButton("← Back");
        JButton submitBtn = UITheme.createPrimaryButton("📝  Submit Leave Request");
        actions.add(backBtn); actions.add(submitBtn);
        root.add(actions, BorderLayout.SOUTH);

        add(root);
        submitBtn.addActionListener(e -> applyLeave());
        backBtn.addActionListener(e -> { dispose(); new EmployeeDashboard(employeeId); });
        setVisible(true);
    }

    private void applyLeave() {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { UITheme.error(this,"Cannot connect to RMI server. Please start the server first."); return; }

            String s = startDate.getText().trim(), e = endDate.getText().trim(), r = reason.getText().trim();
            if (s.isEmpty()||e.isEmpty()||r.isEmpty()) { UITheme.error(this,"Please fill all fields."); return; }

            LocalDate start = LocalDate.parse(s), end = LocalDate.parse(e);

            // Must not be in the past
            if (start.isBefore(LocalDate.now())) { UITheme.error(this,"Start date cannot be in the past."); return; }

            // End must be after or equal to start
            if (end.isBefore(start)) { UITheme.error(this,"End date cannot be before start date."); return; }

            // Check balance before submitting
            long requestedDays = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
            int  remaining     = svc.checkLeaveBalance(employeeId);
            if (remaining < requestedDays) {
                UITheme.error(this, "Not enough leave balance.\n"
                        + "Requested: " + requestedDays + " day(s)\n"
                        + "Remaining: " + remaining + " day(s)");
                return;
            }

            LeaveRequest leave = new LeaveRequest();
            leave.setEmployeeId(employeeId);
            leave.setStartDate(start); leave.setEndDate(end);
            leave.setReason(r); leave.setStatus("Pending");

            boolean ok = svc.applyLeave(leave);
            if (ok) { UITheme.info(this,"Leave request submitted successfully.\nRemaining balance after approval: " + (remaining - requestedDays) + " day(s)."); clearFields(); }
            else      UITheme.error(this,"Failed to submit leave request.");

        } catch (DateTimeParseException ex) {
            UITheme.error(this,"Invalid date format. Please use YYYY-MM-DD. Example: 2026-03-13");
        } catch (Exception ex) { ex.printStackTrace(); UITheme.error(this,"An error occurred while submitting leave."); }
    }

    private void clearFields() { startDate.setText(""); endDate.setText(""); reason.setText(""); }
}
