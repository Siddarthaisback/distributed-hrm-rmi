package org.example.client;

import org.example.model.LeaveRequest;
import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class EmployeeLeaveStatusUI extends JFrame {

    private final int employeeId;
    private JTable table;
    private DefaultTableModel model;

    // Active leave progress widgets
    private JLabel activeBadge;
    private JLabel activeDaysLbl;
    private JLabel activeSubLbl;
    private JProgressBar progressBar;

    public EmployeeLeaveStatusUI(int employeeId) {
        this.employeeId = employeeId;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "My Leave Status", 880, 560);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(UITheme.createHeader("My Leave Status", "Review all leave requests and track your active leave progress."), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(16, 20, 0, 20));
        root.add(content, BorderLayout.CENTER);

        // ── Active Leave Progress banner ───────────────────────────────────────
        content.add(buildProgressBanner(), BorderLayout.NORTH);

        // ── Leave history table ────────────────────────────────────────────────
        String[] columns = {"Leave ID", "Start Date", "End Date", "Reason", "Status"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(4).setCellRenderer(UITheme.statusRenderer());
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);

        JScrollPane scroll = UITheme.wrapTable(table);

        JPanel card = UITheme.createCardPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel cardHdr = new JPanel(new BorderLayout());
        cardHdr.setBackground(UITheme.CARD);
        cardHdr.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(12, 18, 12, 18)));
        JLabel ttl = new JLabel("Leave History — Employee #" + employeeId);
        ttl.setFont(UITheme.F_H2);
        ttl.setForeground(UITheme.SECONDARY);
        cardHdr.add(ttl, BorderLayout.WEST);

        card.add(cardHdr, BorderLayout.NORTH);
        card.add(scroll,  BorderLayout.CENTER);
        content.add(card, BorderLayout.CENTER);

        JPanel actions = UITheme.createActionsPanel();
        JButton backBtn    = UITheme.createSecondaryButton("← Back");
        JButton refreshBtn = UITheme.createPrimaryButton("⟳  Refresh");
        actions.add(backBtn);
        actions.add(refreshBtn);
        root.add(actions, BorderLayout.SOUTH);

        add(root);
        loadAll();
        refreshBtn.addActionListener(e -> loadAll());
        backBtn.addActionListener(e -> { dispose(); new EmployeeDashboard(employeeId); });
        setVisible(true);
    }

    /** Builds the active-leave progress banner card at the top. */
    private JPanel buildProgressBanner() {
        JPanel banner = UITheme.createCardPanel(new BorderLayout(16, 0));
        banner.setBorder(new EmptyBorder(14, 18, 14, 18));

        // Left — icon box
        Color accentColor = new Color(0x8B5CF6);
        Color accentBg    = new Color(0xF3E8FF);
        JPanel iconBox = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(44, 44));
        iconBox.setMinimumSize(new Dimension(44, 44));
        iconBox.setMaximumSize(new Dimension(44, 44));
        JLabel iconLbl = new JLabel("⏳");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconBox.add(iconLbl);

        // Centre — text + progress bar
        JPanel centre = new JPanel(new GridBagLayout());
        centre.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        // Title row
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Active Leave Progress");
        title.setFont(UITheme.F_BOLD);
        title.setForeground(UITheme.SECONDARY);
        activeBadge = new JLabel("CHECKING...");
        activeBadge.setFont(UITheme.F_SMBD);
        activeBadge.setForeground(UITheme.MUTED);
        activeBadge.setOpaque(true);
        activeBadge.setBackground(UITheme.SURFACE2);
        activeBadge.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(2, 8, 2, 8)));
        titleRow.add(title);
        titleRow.add(activeBadge);
        centre.add(titleRow, gc);

        // Progress bar row
        gc.gridy = 1; gc.insets = new Insets(6, 0, 0, 0);
        progressBar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Track
                g2.setColor(accentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                // Fill
                if (getValue() > 0) {
                    int fillW = (int) (getWidth() * getValue() / (double) getMaximum());
                    g2.setColor(accentColor);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        progressBar.setOpaque(false);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 10));
        progressBar.setValue(0);
        centre.add(progressBar, gc);

        // Sub-label row
        gc.gridy = 2; gc.insets = new Insets(5, 0, 0, 0);
        activeSubLbl = new JLabel("Loading active leave information...");
        activeSubLbl.setFont(UITheme.F_SMALL);
        activeSubLbl.setForeground(UITheme.MUTED);
        centre.add(activeSubLbl, gc);

        // Right — big days remaining number
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        activeDaysLbl = new JLabel("—");
        activeDaysLbl.setFont(new Font(UITheme.F_H2.getFamily(), Font.BOLD, 28));
        activeDaysLbl.setForeground(accentColor);
        JLabel daysWord = new JLabel("days left");
        daysWord.setFont(UITheme.F_SMALL);
        daysWord.setForeground(UITheme.MUTED);
        JPanel daysStack = new JPanel();
        daysStack.setOpaque(false);
        daysStack.setLayout(new BoxLayout(daysStack, BoxLayout.Y_AXIS));
        activeDaysLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        daysWord.setAlignmentX(Component.CENTER_ALIGNMENT);
        daysStack.add(activeDaysLbl);
        daysStack.add(daysWord);
        rightPanel.add(daysStack);

        banner.add(iconBox,    BorderLayout.WEST);
        banner.add(centre,     BorderLayout.CENTER);
        banner.add(rightPanel, BorderLayout.EAST);
        return banner;
    }

    /** Load both the leave history table and the active leave progress. */
    private void loadAll() {
        loadLeaveStatus();
        loadActiveLeaveProgress();
    }

    private void loadLeaveStatus() {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { UITheme.error(this, "Cannot connect to RMI server."); return; }
            List<LeaveRequest> list = svc.getLeaveRequestsByEmployeeId(employeeId);
            model.setRowCount(0);
            for (LeaveRequest l : list)
                model.addRow(new Object[]{l.getLeaveId(), l.getStartDate(), l.getEndDate(), l.getReason(), l.getStatus()});
        } catch (Exception e) {
            e.printStackTrace();
            UITheme.error(this, "Failed to load leave status.");
        }
    }

    private void loadActiveLeaveProgress() {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) return;

            int daysRemaining = svc.getActiveLeaveDaysRemaining(employeeId);
            Color accentColor = new Color(0x8B5CF6);
            Color accentBg    = new Color(0xF3E8FF);

            if (daysRemaining > 0) {
                // Find total duration of the active leave to compute progress
                List<LeaveRequest> requests = svc.getLeaveRequestsByEmployeeId(employeeId);
                int totalDays = daysRemaining; // fallback
                for (LeaveRequest lr : requests) {
                    if ("Approved".equalsIgnoreCase(lr.getStatus()) && lr.getEndDate() != null && lr.getStartDate() != null) {
                        long dur = java.time.temporal.ChronoUnit.DAYS.between(lr.getStartDate(), lr.getEndDate()) + 1;
                        if (dur >= daysRemaining) { totalDays = (int) dur; break; }
                    }
                }
                int elapsed  = totalDays - daysRemaining;
                int progress = totalDays > 0 ? (int) (elapsed * 100.0 / totalDays) : 0;

                activeDaysLbl.setForeground(accentColor);
                activeDaysLbl.setText(String.valueOf(daysRemaining));
                progressBar.setValue(progress);
                activeSubLbl.setText(elapsed + " of " + totalDays + " day(s) elapsed  ·  " + daysRemaining + " day(s) remaining");
                activeBadge.setText("ON LEAVE");
                activeBadge.setForeground(accentColor);
                activeBadge.setBackground(accentBg);
                activeBadge.setBorder(new CompoundBorder(
                        new LineBorder(new Color(0xDDD6FE), 1, true),
                        new EmptyBorder(2, 8, 2, 8)));
            } else {
                activeDaysLbl.setForeground(UITheme.MUTED);
                activeDaysLbl.setText("0");
                progressBar.setValue(0);
                activeSubLbl.setText("No active approved leave today.");
                activeBadge.setText("NOT ON LEAVE");
                activeBadge.setForeground(UITheme.MUTED);
                activeBadge.setBackground(UITheme.SURFACE2);
                activeBadge.setBorder(new CompoundBorder(
                        new LineBorder(UITheme.BORDER, 1, true),
                        new EmptyBorder(2, 8, 2, 8)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            activeSubLbl.setText("Could not load active leave information.");
        }
    }
}