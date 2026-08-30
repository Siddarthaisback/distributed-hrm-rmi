package org.example.client;

import org.example.model.LeaveRequest;
import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class HRLeaveRequestsUI extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public HRLeaveRequestsUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "HR Leave Requests", 1000, 540);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(UITheme.createHeader("Leave Requests", "Approve or reject pending leave applications."), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0,14));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(16,20,0,20));
        root.add(content, BorderLayout.CENTER);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);
        filterBar.setBorder(new EmptyBorder(0,0,10,0));
        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setFont(UITheme.F_BOLD); filterLbl.setForeground(UITheme.TEXT);
        JComboBox<String> filter = new JComboBox<>(new String[]{"All","Pending","Approved","Rejected"});
        filter.setFont(UITheme.F_BODY); filter.setPreferredSize(new Dimension(140,34));
        filterBar.add(filterLbl); filterBar.add(filter);
        content.add(filterBar, BorderLayout.NORTH);

        String[] columns = {"Leave ID","Employee ID","Start Date","End Date","Reason","Status"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        // Status column colour
        table.getColumnModel().getColumn(5).setCellRenderer(UITheme.statusRenderer());
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);

        JScrollPane scroll = UITheme.wrapTable(table);
        JPanel card = UITheme.createCardPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(0,0,0,0));

        // Card header
        JPanel cardHdr = new JPanel(new BorderLayout());
        cardHdr.setBackground(UITheme.CARD);
        cardHdr.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0,UITheme.BORDER),
            new EmptyBorder(12,18,12,18)));
        JLabel ttl = new JLabel("All Leave Requests");
        ttl.setFont(UITheme.F_H2); ttl.setForeground(UITheme.SECONDARY);
        cardHdr.add(ttl, BorderLayout.WEST);

        // Summary pills
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pills.setBackground(UITheme.CARD);
        pills.add(summaryPill("Pending", UITheme.WARNING, UITheme.WARNING_LIGHT));
        pills.add(summaryPill("Approved", UITheme.SUCCESS, UITheme.SUCCESS_LIGHT));
        pills.add(summaryPill("Rejected", UITheme.DANGER, UITheme.DANGER_LIGHT));
        cardHdr.add(pills, BorderLayout.EAST);

        card.add(cardHdr, BorderLayout.NORTH);
        card.add(scroll,  BorderLayout.CENTER);
        content.add(card, BorderLayout.CENTER);

        JPanel actions = UITheme.createActionsPanel();
        JButton backBtn    = UITheme.createSecondaryButton("← Back");
        JButton refreshBtn = UITheme.createPrimaryButton("⟳  Refresh");
        JButton rejectBtn  = UITheme.createDangerButton("✗  Reject");
        JButton approveBtn = UITheme.createSuccessButton("✓  Approve");
        actions.add(backBtn); actions.add(refreshBtn);
        actions.add(rejectBtn); actions.add(approveBtn);
        root.add(actions, BorderLayout.SOUTH);

        add(root);
        loadLeaveRequests();

        approveBtn.addActionListener(e -> updateSelectedLeaveStatus("Approved"));
        rejectBtn.addActionListener(e  -> updateSelectedLeaveStatus("Rejected"));
        refreshBtn.addActionListener(e -> loadLeaveRequests());
        backBtn.addActionListener(e    -> { dispose(); new HRDashboard(); });

        // Filter
        filter.addActionListener(e -> {
            String sel = (String) filter.getSelectedItem();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
            sorter.setRowFilter("All".equals(sel) ? null : RowFilter.regexFilter("(?i)^"+sel+"$", 5));
        });

        setVisible(true);
    }

    private JLabel summaryPill(String text, Color fg, Color bg) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SMBD); l.setForeground(fg); l.setBackground(bg); l.setOpaque(true);
        l.setBorder(new CompoundBorder(new LineBorder(fg.darker(),1,true), new EmptyBorder(3,10,3,10)));
        return l;
    }

    private void loadLeaveRequests() {
        try {
            HRMService service = HRMClient.getService();
            if (service == null) { UITheme.error(this, "Cannot connect to RMI server."); return; }
            List<LeaveRequest> list = service.getAllLeaveRequests();
            model.setRowCount(0);
            for (LeaveRequest l : list)
                model.addRow(new Object[]{l.getLeaveId(),l.getEmployeeId(),l.getStartDate(),l.getEndDate(),l.getReason(),l.getStatus()});
        } catch (Exception e) { e.printStackTrace(); UITheme.error(this, "Failed to load leave requests."); }
    }

    private void updateSelectedLeaveStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) { UITheme.error(this, "Please select a leave request first."); return; }
        int modelRow = table.convertRowIndexToModel(row);
        int leaveId = (int) model.getValueAt(modelRow, 0);
        String cur  = model.getValueAt(modelRow, 5).toString();
        if (!cur.equalsIgnoreCase("Pending")) { UITheme.error(this, "Only pending requests can be updated."); return; }
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { UITheme.error(this, "Cannot connect to RMI server."); return; }
            boolean ok = svc.updateLeaveStatus(leaveId, status);
            if (ok) { UITheme.info(this, "Leave request " + status.toLowerCase() + " successfully."); loadLeaveRequests(); }
            else      UITheme.error(this, "Failed to update leave request.");
        } catch (Exception e) { e.printStackTrace(); UITheme.error(this, "Error updating leave request."); }
    }
}
