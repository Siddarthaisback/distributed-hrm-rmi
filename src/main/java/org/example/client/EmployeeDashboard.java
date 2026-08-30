package org.example.client;

import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class EmployeeDashboard extends JFrame {

    private final int employeeId;

    public EmployeeDashboard(int employeeId) {
        this.employeeId = employeeId;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "Employee Dashboard", 860, 560);
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

        JPanel grid = new JPanel(new GridLayout(3, 2, 14, 14));
        grid.setOpaque(false);

        grid.add(card("👤", "Update Profile", "Edit your personal information and contact details.",
                UITheme.PRIMARY, UITheme.PRIMARY_LIGHT,
                () -> { dispose(); new EmployeeProfileUI(employeeId); }));

        grid.add(card("👨‍👩‍👧", "Family Details", "Add or update your family members' information.",
                UITheme.ACCENT, UITheme.ACCENT_LIGHT,
                () -> { dispose(); new FamilyDetailsUI(employeeId); }));

        grid.add(card("📝", "Apply Leave", "Create and submit a new leave request.",
                UITheme.WARNING, UITheme.WARNING_LIGHT,
                () -> { dispose(); new LeaveApplicationUI(employeeId); }));

        grid.add(card("📊", "Leave Balance", "View your current remaining leave days.",
                UITheme.SUCCESS, UITheme.SUCCESS_LIGHT,
                this::checkLeaveBalance));

        grid.add(card("📋", "Leave Status & Progress", "Track requests and view active leave progress.",
                new Color(0x0EA5E9), new Color(0xE0F2FE),
                () -> { dispose(); new EmployeeLeaveStatusUI(employeeId); }));

        grid.add(card("🚪", "Logout", "Return to the main login screen.",
                UITheme.DANGER, UITheme.DANGER_LIGHT,
                () -> { dispose(); new LoginUI(); }));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(sectionLbl, BorderLayout.NORTH);
        wrap.add(grid, BorderLayout.CENTER);
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
        app.setFont(UITheme.F_H2);
        app.setForeground(UITheme.SECONDARY);
        JLabel pill = new JLabel("Employee");
        pill.setFont(UITheme.F_SMBD);
        pill.setForeground(UITheme.SUCCESS);
        pill.setBackground(UITheme.SUCCESS_LIGHT);
        pill.setOpaque(true);
        pill.setBorder(new CompoundBorder(new LineBorder(new Color(0xA7F3D0), 1, true), new EmptyBorder(3, 10, 3, 10)));
        left.add(app);
        left.add(pill);
        bar.add(left, BorderLayout.WEST);

        JLabel idLbl = new JLabel("ID #" + employeeId);
        idLbl.setFont(UITheme.F_SMBD);
        idLbl.setForeground(UITheme.MUTED);
        idLbl.setOpaque(true);
        idLbl.setBackground(UITheme.SURFACE2);
        idLbl.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER, 1, true), new EmptyBorder(4, 10, 4, 10)));
        bar.add(idLbl, BorderLayout.EAST);
        return bar;
    }

    private JPanel card(String emoji, String title, String desc, Color accent, Color accentBg, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            boolean h = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        h = true;
                        repaint();
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                    public void mouseExited(MouseEvent e) {
                        h = false;
                        repaint();
                        setCursor(Cursor.getDefaultCursor());
                    }
                    public void mouseClicked(MouseEvent e) {
                        action.run();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, h ? 18 : 8));
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth() - 4, getHeight() - 3, 12, 12));
                g2.setColor(h ? accentBg : UITheme.CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.setColor(h ? accent : UITheme.BORDER);
                g2.setStroke(new BasicStroke(h ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 3, getHeight() - 3, 12, 12));
                g2.setColor(accent);
                g2.fillRoundRect(0, 16, 4, getHeight() - 32, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel iconBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
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

        JPanel text = new JPanel(new GridBagLayout());
        text.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.F_BOLD);
        titleLbl.setForeground(UITheme.SECONDARY);
        text.add(titleLbl, gc);

        gc.gridy = 1;
        gc.insets = new Insets(3, 0, 0, 0);
        JLabel descLbl = new JLabel("<html><body style='width:160px;color:#64748B'>" + desc + "</body></html>");
        descLbl.setFont(UITheme.F_SMALL);
        text.add(descLbl, gc);

        JLabel arrow = new JLabel("→");
        arrow.setFont(UITheme.F_BOLD);
        arrow.setForeground(accent);
        arrow.setVerticalAlignment(SwingConstants.CENTER);

        card.add(iconBox, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);
        return card;
    }

    private void checkLeaveBalance() {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) {
                UITheme.error(this, "Cannot connect to the RMI server.");
                return;
            }
            int balance = svc.checkLeaveBalance(employeeId);
            UITheme.info(this, "Your remaining leave balance is: " + balance + " day(s).");
        } catch (Exception e) {
            e.printStackTrace();
            UITheme.error(this, "Failed to fetch leave balance.");
        }
    }

}