package org.example.client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class HRDashboard extends JFrame {

    public HRDashboard() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "HR Dashboard", 860, 520);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(buildTopBar(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(18, 22, 18, 22));
        root.add(content, BorderLayout.CENTER);

        JLabel sectionLbl = new JLabel("QUICK ACTIONS");
        sectionLbl.setFont(UITheme.F_MICRO);
        sectionLbl.setForeground(UITheme.MUTED);
        sectionLbl.setBorder(new EmptyBorder(0, 2, 10, 0));

        // 2 rows x 3 cols — last slot is a filler
        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);

        grid.add(card("➕", "Register Employee", "Create a new employee account and login record.",  UITheme.PRIMARY,            UITheme.PRIMARY_LIGHT,  () -> { dispose(); new RegisterEmployeeUI(); }));
        grid.add(card("👥", "View Employees",    "Browse, edit, or remove employee records.",         UITheme.SUCCESS,            UITheme.SUCCESS_LIGHT,  () -> { dispose(); new ViewEmployeesUI(); }));
        grid.add(card("📋", "Leave Requests",    "Review, approve, or reject leave applications.",    UITheme.WARNING,            UITheme.WARNING_LIGHT,  () -> { dispose(); new HRLeaveRequestsUI(); }));
        grid.add(card("📊", "Yearly Report",     "Generate annual leave summary reports.",            UITheme.ACCENT,             UITheme.ACCENT_LIGHT,   () -> { dispose(); new HRYearlyLeaveReportUI(); }));
        grid.add(card("🚪", "Logout",            "Return to the login screen.",                       UITheme.DANGER,             UITheme.DANGER_LIGHT,   () -> { dispose(); new LoginUI(); }));

        // Empty filler for the 6th slot
        JPanel filler = new JPanel(); filler.setOpaque(false);
        grid.add(filler);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(sectionLbl, BorderLayout.NORTH);
        wrap.add(grid,       BorderLayout.CENTER);
        content.add(wrap, BorderLayout.CENTER);

        add(root);
        setVisible(true);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.CARD);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(12, 22, 12, 22)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JLabel app = new JLabel("HRM System");
        app.setFont(UITheme.F_H2); app.setForeground(UITheme.SECONDARY);
        JLabel pill = new JLabel("HR Manager");
        pill.setFont(UITheme.F_SMBD); pill.setForeground(UITheme.PRIMARY);
        pill.setBackground(UITheme.PRIMARY_LIGHT); pill.setOpaque(true);
        pill.setBorder(new CompoundBorder(new LineBorder(new Color(0xBFDBFE), 1, true), new EmptyBorder(3, 10, 3, 10)));
        left.add(app); left.add(pill);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JLabel welcome = new JLabel("Welcome, HR Admin");
        welcome.setFont(UITheme.F_BODY); welcome.setForeground(UITheme.MUTED);

        // Avatar circle
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(1, 1, getWidth()-2, getHeight()-2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(34, 34));
        JLabel initials = new JLabel("HR");
        initials.setFont(UITheme.F_SMBD); initials.setForeground(UITheme.PRIMARY);
        avatar.add(initials);

        right.add(welcome); right.add(avatar);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Compact horizontal card — identical pattern to EmployeeDashboard ───────
    private JPanel card(String emoji, String title, String desc, Color accent, Color accentBg, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            boolean h = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { h = true;  repaint(); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                public void mouseExited(MouseEvent e)  { h = false; repaint(); setCursor(Cursor.getDefaultCursor()); }
                public void mouseClicked(MouseEvent e) { action.run(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, h ? 18 : 8));
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth()-4, getHeight()-3, 12, 12));
                g2.setColor(h ? accentBg : UITheme.CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-2, getHeight()-2, 12, 12));
                g2.setColor(h ? accent : UITheme.BORDER);
                g2.setStroke(new BasicStroke(h ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-3, getHeight()-3, 12, 12));
                // left accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 16, 4, getHeight()-32, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Fixed-size icon box — no more oval blobs
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
        iconBox.setPreferredSize(new Dimension(38, 38));
        iconBox.setMinimumSize(new Dimension(38, 38));
        iconBox.setMaximumSize(new Dimension(38, 38));
        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        iconBox.add(emojiLbl);

        // Text
        JPanel text = new JPanel(new GridBagLayout());
        text.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.F_BOLD); titleLbl.setForeground(UITheme.SECONDARY);
        text.add(titleLbl, gc);
        gc.gridy = 1; gc.insets = new Insets(3, 0, 0, 0);
        JLabel descLbl = new JLabel("<html><body style='width:140px;color:#64748B'>" + desc + "</body></html>");
        descLbl.setFont(UITheme.F_SMALL);
        text.add(descLbl, gc);

        // Arrow
        JLabel arrow = new JLabel("→");
        arrow.setFont(UITheme.F_BOLD); arrow.setForeground(accent);
        arrow.setVerticalAlignment(SwingConstants.CENTER);

        card.add(iconBox, BorderLayout.WEST);
        card.add(text,    BorderLayout.CENTER);
        card.add(arrow,   BorderLayout.EAST);
        return card;
    }
}
