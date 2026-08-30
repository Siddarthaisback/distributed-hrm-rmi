// ServerTheme.java
package org.example.server;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * ServerTheme — updated to a lighter dashboard style so the server UI feels
 * closer to the reference, while keeping all existing server-side pages intact.
 */
public class ServerTheme {

    // ── Palette ────────────────────────────────────────────────────────────────
    public static final Color BG         = new Color(0xEEF3FA);
    public static final Color SURFACE    = new Color(0xE4EAF2);
    public static final Color CARD       = new Color(0xFFFFFF);
    public static final Color BORDER     = new Color(0xE5EAF2);

    public static final Color ACCENT     = new Color(0x8B6FF2);
    public static final Color SUCCESS    = new Color(0x22C55E);
    public static final Color WARNING    = new Color(0xF59E0B);
    public static final Color DANGER     = new Color(0xF97373);
    public static final Color TEXT       = new Color(0x2F3349);
    public static final Color MUTED      = new Color(0x8C90A3);
    public static final Color PURPLE     = new Color(0x8B6FF2);
    public static final Color CYAN       = new Color(0x28C7D9);

    public static final Color LOG_OK_BG   = new Color(0x16A34A);
    public static final Color LOG_ERR_BG  = new Color(0xDC2626);
    public static final Color LOG_WARN_BG = new Color(0xD97706);
    public static final Color LOG_STEP_BG = new Color(0x7C3AED);

    // ── Fonts ──────────────────────────────────────────────────────────────────
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

    public static final Font F_LOGO  = new Font(FN, Font.BOLD,  30);
    public static final Font F_TITLE = new Font(FN, Font.BOLD,  20);
    public static final Font F_H2    = new Font(FN, Font.BOLD,  13);
    public static final Font F_BODY  = new Font(FN, Font.PLAIN, 12);
    public static final Font F_SMALL = new Font(FN, Font.PLAIN, 10);
    public static final Font F_BADGE = new Font(FN, Font.BOLD,  11);
    public static final Font F_NANO  = new Font(FN, Font.BOLD,   9);

    // ── Frame ──────────────────────────────────────────────────────────────────
    public static void applyFrame(JFrame frame, String title, int w, int h) {
        frame.setTitle(title);
        frame.setSize(w, h);
        frame.setMinimumSize(new Dimension(720, 480));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BG);
        frame.setLayout(new BorderLayout());
    }

    // ── Cards ──────────────────────────────────────────────────────────────────
    public static JPanel roundCard(int radius) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 6; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, i == 1 ? 6 : 3));
                    g2.fill(new RoundRectangle2D.Float(
                            i, i + 1,
                            getWidth() - (i * 2),
                            getHeight() - (i * 2) - 1,
                            radius, radius
                    ));
                }

                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, radius, radius));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 3, getHeight() - 3, radius, radius));
                g2.dispose();
            }
        };
    }

    public static JSeparator hRule() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setBackground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    public static JPanel infoCard(String label, String initialValue, Color valueColor) {
        JPanel card = roundCard(16);
        card.setLayout(new BorderLayout(0, 3));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(F_NANO);
        lbl.setForeground(MUTED);

        JLabel val = new JLabel(initialValue);
        val.setFont(F_H2);
        val.setForeground(valueColor);

        card.putClientProperty("val", val);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ── Buttons ────────────────────────────────────────────────────────────────
    public static JButton actionButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c = !isEnabled() ? new Color(0xDCE2EC)
                        : hover ? brighten(bg, 0.08f)
                        : bg;

                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 34));
                g2.fill(new RoundRectangle2D.Float(2, 4, getWidth() - 4, getHeight() - 2, 14, 14));

                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(F_BADGE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JPanel legendItem(Color dotColor, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);

        JLabel dot = new JLabel("\u25CF");
        dot.setFont(new Font(FN, Font.PLAIN, 10));
        dot.setForeground(dotColor);

        JLabel lbl = new JLabel(label);
        lbl.setFont(F_SMALL);
        lbl.setForeground(MUTED);

        p.add(dot);
        p.add(lbl);
        return p;
    }

    // ── Scrollbar ──────────────────────────────────────────────────────────────
    public static class DarkScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(0xCCD5E3);
            trackColor = new Color(0xF7F9FC);
        }

        @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }

        private JButton zeroBtn() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.width <= 0 || r.height <= 0) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 3, r.y + 3, r.width - 6, r.height - 6, 10, 10);
            g2.dispose();
        }

        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.dispose();
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────────
    private static Color brighten(Color c, float ratio) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        r += Math.round((255 - r) * ratio);
        g += Math.round((255 - g) * ratio);
        b += Math.round((255 - b) * ratio);

        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }
}