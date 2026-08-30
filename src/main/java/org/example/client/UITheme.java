// UITheme.java
package org.example.client;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public final class UITheme {

    // ── Palette — light dashboard style ───────────────────────────────────────
    public static final Color BG            = new Color(0xEEF3FA);
    public static final Color CARD          = new Color(0xFFFFFF);
    public static final Color SURFACE2      = new Color(0xF7F9FC);

    public static final Color PRIMARY       = new Color(0x8B6FF2);   // soft purple
    public static final Color PRIMARY_DARK  = new Color(0x755BE0);
    public static final Color PRIMARY_LIGHT = new Color(0xF1ECFF);

    public static final Color ACCENT        = new Color(0x28C7D9);   // cyan
    public static final Color ACCENT_LIGHT  = new Color(0xE9FBFD);

    public static final Color SUCCESS       = new Color(0x22C55E);
    public static final Color SUCCESS_LIGHT = new Color(0xEBF9F0);

    public static final Color WARNING       = new Color(0xF59E0B);
    public static final Color WARNING_LIGHT = new Color(0xFFF7E8);

    public static final Color DANGER        = new Color(0xF97373);
    public static final Color DANGER_LIGHT  = new Color(0xFFF1F1);

    public static final Color SECONDARY     = new Color(0x171A2A);
    public static final Color TEXT          = new Color(0x2F3349);
    public static final Color MUTED         = new Color(0x8C90A3);

    public static final Color BORDER        = new Color(0xE8ECF3);
    public static final Color BORDER2       = new Color(0xD9DFEA);

    public static final Color SHADOW        = new Color(0, 0, 0, 14);
    public static final Color HEADER_BAR    = new Color(0xDDE3EC);

    // ── Typography ────────────────────────────────────────────────────────────
    private static final String[] FONT_STACK = {
            "Inter", "DM Sans", "Segoe UI", "Helvetica Neue", "Arial"
    };
    private static final String FN = pickFont();

    private static String pickFont() {
        for (String f : FONT_STACK) {
            Font test = new Font(f, Font.PLAIN, 12);
            if (!"Dialog".equals(test.getFamily())) return f;
        }
        return "Segoe UI";
    }

    public static final Font F_DISPLAY = new Font(FN, Font.BOLD,  28);
    public static final Font F_TITLE   = new Font(FN, Font.BOLD,  22);
    public static final Font F_H1      = new Font(FN, Font.BOLD,  18);
    public static final Font F_H2      = new Font(FN, Font.BOLD,  15);
    public static final Font F_BODY    = new Font(FN, Font.PLAIN, 13);
    public static final Font F_BOLD    = new Font(FN, Font.BOLD,  13);
    public static final Font F_SMALL   = new Font(FN, Font.PLAIN, 11);
    public static final Font F_SMBD    = new Font(FN, Font.BOLD,  11);
    public static final Font F_MICRO   = new Font(FN, Font.BOLD,  10);

    private UITheme() {}

    // ── Frame setup ────────────────────────────────────────────────────────────
    public static void applyFrame(JFrame frame, String title, int width, int height) {
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        UIManager.put("OptionPane.background", CARD);
        UIManager.put("Panel.background", CARD);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.font", F_BOLD);
    }

    // ── Root panel ─────────────────────────────────────────────────────────────
    public static JPanel createRootPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(BG);
        return p;
    }

    // ── Header ─────────────────────────────────────────────────────────────────
    public static JPanel createHeader(String title, String subtitle) {
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(PRIMARY);
                g2.fillRoundRect(18, 18, 4, getHeight() - 36, 4, 4);
                g2.dispose();
            }
        };
        outer.setBackground(CARD);
        outer.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(18, 30, 18, 26)
        ));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(F_TITLE);
        t.setForeground(SECONDARY);

        JLabel s = new JLabel(subtitle);
        s.setFont(F_BODY);
        s.setForeground(MUTED);

        inner.add(t);
        inner.add(Box.createVerticalStrut(4));
        inner.add(s);
        outer.add(inner, BorderLayout.WEST);
        return outer;
    }

    // ── Card panel ─────────────────────────────────────────────────────────────
    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 6; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, i == 1 ? 6 : 3));
                    g2.fill(new RoundRectangle2D.Float(
                            i, i + 1,
                            getWidth() - (i * 2),
                            getHeight() - (i * 2) - 1,
                            22, 22
                    ));
                }

                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 22, 22));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 3, getHeight() - 3, 22, 22));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 22, 20, 22));
        return p;
    }

    // ── Labels ─────────────────────────────────────────────────────────────────
    public static JLabel createSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_BOLD);
        l.setForeground(TEXT);
        return l;
    }

    // ── Inputs ────────────────────────────────────────────────────────────────
    public static JTextField createTextField() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField f = new JPasswordField();
        styleInput(f);
        return f;
    }

    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea a = new JTextArea(rows, cols);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(F_BODY);
        a.setForeground(TEXT);
        a.setBackground(CARD);
        a.setBorder(new CompoundBorder(
                new LineBorder(BORDER2, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        return a;
    }

    private static void styleInput(JComponent c) {
        c.setFont(F_BODY);
        c.setForeground(TEXT);
        c.setBackground(CARD);
        c.setPreferredSize(new Dimension(220, 42));
        c.setBorder(new CompoundBorder(
                new LineBorder(BORDER2, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        c.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                c.setBorder(new CompoundBorder(
                        new LineBorder(PRIMARY, 2, true),
                        new EmptyBorder(9, 11, 9, 11)
                ));
                c.setBackground(new Color(0xFCFBFF));
            }

            @Override public void focusLost(FocusEvent e) {
                c.setBorder(new CompoundBorder(
                        new LineBorder(BORDER2, 1, true),
                        new EmptyBorder(10, 12, 10, 12)
                ));
                c.setBackground(CARD);
            }
        });
    }

    // ── Buttons ────────────────────────────────────────────────────────────────
    public static JButton createPrimaryButton(String text) {
        return styledBtn(text, PRIMARY, PRIMARY_DARK, Color.WHITE, true);
    }

    public static JButton createDarkButton(String text) {
        return styledBtn(text, SECONDARY, new Color(0x101322), Color.WHITE, true);
    }

    public static JButton createSuccessButton(String text) {
        return styledBtn(text, SUCCESS, new Color(0x16A34A), Color.WHITE, true);
    }

    public static JButton createDangerButton(String text) {
        return styledBtn(text, DANGER, new Color(0xEF4444), Color.WHITE, true);
    }

    public static JButton createSecondaryButton(String text) {
        return styledBtn(text, CARD, new Color(0xF5F7FB), TEXT, false);
    }

    public static JButton createAccentButton(String text) {
        return styledBtn(text, ACCENT, new Color(0x11B9CB), Color.WHITE, true);
    }

    private static JButton styledBtn(String text, Color bg, Color hoverBg, Color fg, boolean filled) {
        JButton b = new JButton(text) {
            boolean hover = false;
            boolean pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; pressed = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                    @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = !isEnabled() ? new Color(0xE6EAF1)
                        : pressed ? hoverBg.darker()
                        : hover ? hoverBg
                        : bg;

                if (filled && isEnabled()) {
                    g2.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 36));
                    g2.fill(new RoundRectangle2D.Float(2, 4, getWidth() - 4, getHeight() - 2, 14, 14));
                }

                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 2, 14, 14));

                if (!filled) {
                    g2.setColor(hover ? BORDER2 : BORDER);
                    g2.setStroke(new BasicStroke(1.1f));
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 2, getHeight() - 3, 14, 14));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        b.setFont(F_BOLD);
        b.setForeground(filled ? fg : TEXT);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(11, 22, 11, 22));
        return b;
    }

    // ── Tables ─────────────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(F_BODY);
        table.setForeground(TEXT);
        table.setBackground(CARD);
        table.setSelectionBackground(new Color(0xF1EDFF));
        table.setSelectionForeground(PRIMARY_DARK);
        table.setGridColor(new Color(0xF0F3F8));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader h = table.getTableHeader();
        h.setFont(F_MICRO);
        h.setForeground(MUTED);
        h.setBackground(new Color(0xFAFBFE));
        h.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        h.setPreferredSize(new Dimension(0, 42));
        h.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? CARD : new Color(0xFCFDFF));
                    c.setForeground(TEXT);
                }
                c.setBorder(new EmptyBorder(0, 16, 0, 16));
                return c;
            }
        });
    }

    public static JScrollPane wrapTable(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(CARD);
        sp.setBackground(CARD);
        sp.getVerticalScrollBar().setUI(new LightScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new LightScrollBarUI());
        sp.getVerticalScrollBar().setUnitIncrement(14);
        sp.getHorizontalScrollBar().setUnitIncrement(14);
        return sp;
    }

    public static JPanel createActionsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(2, 12, 2, 12)
        ));
        return p;
    }

    public static TableCellRenderer statusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String s = val != null ? val.toString() : "";

                if (!sel) {
                    switch (s) {
                        case "Approved", "Onboarding", "Active" -> {
                            l.setForeground(SUCCESS);
                            l.setBackground(SUCCESS_LIGHT);
                        }
                        case "Rejected", "Offboarding", "Inactive" -> {
                            l.setForeground(DANGER);
                            l.setBackground(DANGER_LIGHT);
                        }
                        case "Pending", "Probation" -> {
                            l.setForeground(WARNING);
                            l.setBackground(WARNING_LIGHT);
                        }
                        default -> {
                            l.setForeground(PRIMARY_DARK);
                            l.setBackground(PRIMARY_LIGHT);
                        }
                    }
                    l.setFont(F_SMBD);
                }

                l.setBorder(new EmptyBorder(0, 14, 0, 14));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        };
    }

    // ── Optional stat card helper ──────────────────────────────────────────────
    public static JPanel createStatCard(String value, String label, Color accent) {
        JPanel p = new JPanel(new BorderLayout(0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 10));
                g2.fill(new RoundRectangle2D.Float(2, 4, getWidth() - 4, getHeight() - 4, 20, 20));

                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 20, 20));

                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 3, getHeight() - 3, 20, 20));

                g2.setColor(accent);
                g2.fillRoundRect(18, 16, 8, getHeight() - 32, 8, 8);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 22, 16, 20));

        JLabel v = new JLabel(value);
        v.setFont(F_DISPLAY);
        v.setForeground(SECONDARY);

        JLabel l = new JLabel(label);
        l.setFont(F_BODY);
        l.setForeground(MUTED);

        p.add(v, BorderLayout.CENTER);
        p.add(l, BorderLayout.SOUTH);
        return p;
    }

    // ── Dialog helpers ─────────────────────────────────────────────────────────
    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Light scrollbar ────────────────────────────────────────────────────────
    public static class LightScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(0xCBD5E1);
            trackColor = new Color(0xF8FAFC);
        }

        @Override protected JButton createDecreaseButton(int orientation) { return zeroBtn(); }
        @Override protected JButton createIncreaseButton(int orientation) { return zeroBtn(); }

        private JButton zeroBtn() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.width <= 0 || r.height <= 0) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 3, r.y + 3, r.width - 6, r.height - 6, 10, 10);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.dispose();
        }
    }
}