package org.example.client;

import org.example.model.EmployeeLeaveReport;
import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class HRYearlyLeaveReportUI extends JFrame {

    private JTextField yearField;
    private JTable table;
    private DefaultTableModel model;

    public HRYearlyLeaveReportUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "Yearly Leave Report", 1000, 560);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(UITheme.createHeader("Yearly Leave Report",
            "Generate and review annual leave summaries for all employees."), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(16,20,0,20));
        root.add(content, BorderLayout.CENTER);

        // Top filter card
        JPanel topCard = UITheme.createCardPanel(new BorderLayout(16, 0));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(UITheme.createSectionLabel("Enter Year:"));
        yearField = UITheme.createTextField();
        yearField.setPreferredSize(new Dimension(120,38));
        yearField.setText(String.valueOf(LocalDate.now().getYear()));
        left.add(yearField);
        topCard.add(left, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        topRight.setOpaque(false);
        JButton backBtn = UITheme.createSecondaryButton("← Back");
        JButton genBtn  = UITheme.createPrimaryButton("📊  Generate Report");
        topRight.add(backBtn); topRight.add(genBtn);
        topCard.add(topRight, BorderLayout.EAST);
        content.add(topCard, BorderLayout.NORTH);

        // Table card
        String[] columns = {"Employee ID","Employee Name","Total Leaves","Approved","Rejected","Pending"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        // Colour numeric columns
        TableCellRenderer numRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,focus,row,col);
                if (!sel) {
                    switch (col) {
                        case 3 -> { l.setForeground(UITheme.SUCCESS); l.setBackground(UITheme.SUCCESS_LIGHT); }
                        case 4 -> { l.setForeground(UITheme.DANGER);  l.setBackground(UITheme.DANGER_LIGHT);  }
                        case 5 -> { l.setForeground(UITheme.WARNING); l.setBackground(UITheme.WARNING_LIGHT); }
                        default -> { l.setForeground(UITheme.TEXT); l.setBackground(row%2==0?UITheme.CARD:UITheme.SURFACE2); }
                    }
                    l.setFont(col>=2 ? UITheme.F_BOLD : UITheme.F_BODY);
                }
                l.setHorizontalAlignment(col>=2 ? SwingConstants.CENTER : SwingConstants.LEFT);
                l.setBorder(new EmptyBorder(0,14,0,14));
                return l;
            }
        };
        for (int i=2;i<=5;i++) table.getColumnModel().getColumn(i).setCellRenderer(numRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);

        JPanel tableCard = UITheme.createCardPanel(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(0,0,0,0));

        JPanel cardHdr = new JPanel(new BorderLayout());
        cardHdr.setBackground(UITheme.CARD);
        cardHdr.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0,UITheme.BORDER),
            new EmptyBorder(12,18,12,18)));
        JLabel ttl = new JLabel("Annual Leave Statistics");
        ttl.setFont(UITheme.F_H2); ttl.setForeground(UITheme.SECONDARY);
        cardHdr.add(ttl, BorderLayout.WEST);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        legend.setBackground(UITheme.CARD);
        legend.add(legendPill("Approved", UITheme.SUCCESS, UITheme.SUCCESS_LIGHT));
        legend.add(legendPill("Rejected", UITheme.DANGER,  UITheme.DANGER_LIGHT));
        legend.add(legendPill("Pending",  UITheme.WARNING, UITheme.WARNING_LIGHT));
        cardHdr.add(legend, BorderLayout.EAST);

        tableCard.add(cardHdr,            BorderLayout.NORTH);
        tableCard.add(UITheme.wrapTable(table), BorderLayout.CENTER);
        content.add(tableCard, BorderLayout.CENTER);

        add(root);
        genBtn.addActionListener(e -> generateReport());
        backBtn.addActionListener(e -> { dispose(); new HRDashboard(); });
        yearField.addActionListener(e -> generateReport());
        setVisible(true);
    }

    private JLabel legendPill(String text, Color fg, Color bg) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SMBD); l.setForeground(fg); l.setBackground(bg); l.setOpaque(true);
        l.setBorder(new CompoundBorder(new LineBorder(fg.darker(),1,true),new EmptyBorder(3,10,3,10)));
        return l;
    }

    private void generateReport() {
        try {
            String yt = yearField.getText().trim();
            if (yt.isEmpty()) { UITheme.error(this,"Please enter a year."); return; }
            int year = Integer.parseInt(yt);
            HRMService svc = HRMClient.getService();
            if (svc == null) { UITheme.error(this,"Cannot connect to RMI server."); return; }
            List<EmployeeLeaveReport> list = svc.getYearlyLeaveReport(year);
            model.setRowCount(0);
            for (EmployeeLeaveReport r : list)
                model.addRow(new Object[]{r.getEmployeeId(),r.getEmployeeName(),
                    r.getTotalLeaves(),r.getApprovedLeaves(),r.getRejectedLeaves(),r.getPendingLeaves()});
            if (list.isEmpty()) UITheme.info(this,"No report data found for year "+year+".");
        } catch (NumberFormatException ex) { UITheme.error(this,"Please enter a valid year.");
        } catch (Exception ex) { ex.printStackTrace(); UITheme.error(this,"Failed to generate report."); }
    }
}
