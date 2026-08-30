package org.example.server;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * ServerSidebar — left panel of the HRM server control window.
 * Shows server status, static info cards, and live uptime / connection counters.
 *
 * Usage:
 *   ServerSidebar sidebar = new ServerSidebar();
 *   frame.add(sidebar, BorderLayout.WEST);
 *
 *   // Update live values:
 *   sidebar.setStatus(true);
 *   sidebar.setUptime("00:01:23");
 *   sidebar.setConnections(3);
 */
public class ServerSidebar extends JPanel {

    private JLabel statusDot;
    private JLabel statusLbl;
    private JLabel uptimeVal;
    private JLabel connVal;

    public ServerSidebar() {
        setOpaque(false);
        setPreferredSize(new Dimension(220, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(26, 18, 22, 18));
        buildContents();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(ServerTheme.SURFACE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        // Right border line
        g2.setColor(ServerTheme.BORDER);
        g2.fillRect(getWidth() - 1, 0, 1, getHeight());
        g2.dispose();
    }

    private void buildContents() {
        // Logo
        JLabel logo = new JLabel("HRM");
        logo.setFont(ServerTheme.F_LOGO);
        logo.setForeground(ServerTheme.ACCENT);
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Server Control Panel");
        sub.setFont(ServerTheme.F_SMALL);
        sub.setForeground(ServerTheme.MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        add(logo);
        add(Box.createVerticalStrut(3));
        add(sub);
        add(Box.createVerticalStrut(20));
        add(ServerTheme.hRule());
        add(Box.createVerticalStrut(18));

        // Status card
        add(buildStatusCard());
        add(Box.createVerticalStrut(12));

        // Static info cards
        add(ServerTheme.infoCard("RMI PORT",  "1099",      ServerTheme.ACCENT));
        add(Box.createVerticalStrut(8));
        add(ServerTheme.infoCard("PROTOCOL",  "RMI / TCP", ServerTheme.MUTED));
        add(Box.createVerticalStrut(8));

        // Live uptime card
        JPanel upCard = ServerTheme.infoCard("UPTIME", "\u2014", ServerTheme.MUTED);
        uptimeVal = (JLabel) upCard.getClientProperty("val");
        add(upCard);
        add(Box.createVerticalStrut(8));

        // Live connections card
        JPanel connCard = ServerTheme.infoCard("CONNECTIONS", "0", ServerTheme.MUTED);
        connVal = (JLabel) connCard.getClientProperty("val");
        add(connCard);

        add(Box.createVerticalGlue());
        add(ServerTheme.hRule());
        add(Box.createVerticalStrut(10));

        JLabel ver = new JLabel("v1.0.0  \u2022  Java RMI");
        ver.setFont(ServerTheme.F_SMALL);
        ver.setForeground(ServerTheme.MUTED);
        ver.setAlignmentX(LEFT_ALIGNMENT);
        add(ver);
    }

    private JPanel buildStatusCard() {
        JPanel card = ServerTheme.roundCard(10);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(12, 13, 12, 13));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        card.setAlignmentX(LEFT_ALIGNMENT);

        statusDot = new JLabel("\u25CF");
        statusDot.setFont(new Font("SansSerif", Font.BOLD, 22));
        statusDot.setForeground(ServerTheme.DANGER);

        JPanel right = new JPanel(new BorderLayout(0, 2));
        right.setOpaque(false);

        JLabel title = new JLabel("SERVER STATUS");
        title.setFont(ServerTheme.F_NANO);
        title.setForeground(ServerTheme.MUTED);

        statusLbl = new JLabel("OFFLINE");
        statusLbl.setFont(ServerTheme.F_H2);
        statusLbl.setForeground(ServerTheme.DANGER);

        right.add(title,     BorderLayout.NORTH);
        right.add(statusLbl, BorderLayout.CENTER);

        card.add(statusDot, BorderLayout.WEST);
        card.add(right,     BorderLayout.CENTER);
        return card;
    }

    // ── Public update methods ──────────────────────────────────────────────────

    public void setStatus(boolean online) {
        Color c = online ? ServerTheme.SUCCESS : ServerTheme.DANGER;
        statusDot.setForeground(c);
        statusLbl.setForeground(c);
        statusLbl.setText(online ? "ONLINE" : "OFFLINE");
    }

    public void setUptime(String text) {
        uptimeVal.setText(text);
        uptimeVal.setForeground(ServerTheme.SUCCESS);
    }

    public void resetUptime() {
        uptimeVal.setText("\u2014");
        uptimeVal.setForeground(ServerTheme.MUTED);
    }

    public void setConnections(int count) {
        connVal.setText(String.valueOf(count));
        connVal.setForeground(count > 0 ? ServerTheme.SUCCESS : ServerTheme.MUTED);
    }
}
