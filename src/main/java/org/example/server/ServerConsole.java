package org.example.server;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ServerConsole — the scrollable log panel shown in the main area.
 * Supports colour-coded log levels: INFO, OK, WARN, ERR, STEP.
 *
 * Usage:
 *   ServerConsole console = new ServerConsole();
 *   console.log("INFO", "Server starting...");
 *   console.log("OK",   "Database ready.");
 *   console.clear();
 */
public class ServerConsole extends JPanel {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private JTextPane      logPane;
    private StyledDocument logDoc;

    public ServerConsole() {
        setLayout(new BorderLayout());
        setBackground(ServerTheme.BG);
        setBorder(new EmptyBorder(14, 18, 0, 18));
        buildContents();
    }

    private void buildContents() {
        // Column header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x161926));
        header.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 0, 1, ServerTheme.BORDER),
                new EmptyBorder(5, 14, 5, 14)));

        JLabel col = new JLabel("TIME         TYPE        MESSAGE");
        col.setFont(new Font("Monospaced", Font.BOLD, 10));
        col.setForeground(ServerTheme.MUTED);
        header.add(col, BorderLayout.WEST);

        // Log text pane
        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setBackground(new Color(0x111421));
        logPane.setBorder(new EmptyBorder(10, 14, 10, 14));
        logDoc = logPane.getStyledDocument();

        JScrollPane scroll = new JScrollPane(logPane);
        scroll.setBorder(new MatteBorder(0, 1, 1, 1, ServerTheme.BORDER));
        scroll.getVerticalScrollBar().setUI(new ServerTheme.DarkScrollBarUI());
        scroll.getVerticalScrollBar().setBackground(new Color(0x111421));
        scroll.setBackground(new Color(0x111421));

        add(header, BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Append a log line. Must be called from any thread — marshals to EDT internally.
     *
     * @param type    "INFO" | "OK" | "WARN" | "ERR" | "STEP"
     * @param message the message text
     */
    public void log(String type, String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                String timeStr = LocalTime.now().format(TIME_FMT);

                String tag = switch (type) {
                    case "OK"   -> "  [ OK   ] ";
                    case "ERR"  -> "  [ ERR  ] ";
                    case "WARN" -> "  [ WARN ] ";
                    case "STEP" -> "  [ .... ] ";
                    default     -> "  [ INFO ] ";
                };

                Color typeColor = switch (type) {
                    case "OK"   -> ServerTheme.SUCCESS;
                    case "ERR"  -> ServerTheme.DANGER;
                    case "WARN" -> ServerTheme.WARNING;
                    case "STEP" -> ServerTheme.PURPLE;
                    default     -> ServerTheme.MUTED;
                };

                Color msgColor = switch (type) {
                    case "OK"   -> ServerTheme.LOG_OK_BG;
                    case "ERR"  -> ServerTheme.LOG_ERR_BG;
                    case "WARN" -> ServerTheme.LOG_WARN_BG;
                    case "STEP" -> ServerTheme.LOG_STEP_BG;
                    default     -> ServerTheme.TEXT;
                };

                Style tStyle = logPane.addStyle(null, null);
                StyleConstants.setForeground(tStyle, ServerTheme.ACCENT);
                StyleConstants.setFontFamily(tStyle, "Monospaced");
                StyleConstants.setFontSize(tStyle, 12);

                Style kStyle = logPane.addStyle(null, null);
                StyleConstants.setForeground(kStyle, typeColor);
                StyleConstants.setFontFamily(kStyle, "Monospaced");
                StyleConstants.setFontSize(kStyle, 12);
                StyleConstants.setBold(kStyle, true);

                Style mStyle = logPane.addStyle(null, null);
                StyleConstants.setForeground(mStyle, msgColor);
                StyleConstants.setFontFamily(mStyle, "Monospaced");
                StyleConstants.setFontSize(mStyle, 12);

                logDoc.insertString(logDoc.getLength(), timeStr, tStyle);
                logDoc.insertString(logDoc.getLength(), tag,     kStyle);
                logDoc.insertString(logDoc.getLength(), message + "\n", mStyle);

                logPane.setCaretPosition(logDoc.getLength());
            } catch (Exception ignored) {}
        });
    }

    /** Clear all log entries. */
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            try { logDoc.remove(0, logDoc.getLength()); } catch (Exception ignored) {}
        });
    }
}
