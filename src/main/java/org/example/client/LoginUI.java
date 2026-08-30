package org.example.client;

import org.example.security.AuthenticationService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginUI extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;

    public LoginUI() {
        setTitle("BHEL HRM System — Login");
        setSize(820, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // ── LEFT panel — brand side ────────────────────────────────────────────
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Deep indigo background
                g2.setColor(new Color(0x1A1F5E));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(0x4361EE));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                g2.fillOval(-60, -60, 280, 280);
                g2.setColor(new Color(0x7209B7));
                g2.fillOval(getWidth()-160, getHeight()-160, 300, 300);
                g2.setColor(new Color(0x4361EE));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2.fillOval(40, getHeight()-200, 200, 200);
                // Grid dots
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setColor(Color.WHITE);
                for (int x = 20; x < getWidth(); x += 26)
                    for (int y = 20; y < getHeight(); y += 26)
                        g2.fillOval(x-1, y-1, 2, 2);
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(320, 0));
        left.setLayout(new GridBagLayout());

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(0, 36, 0, 36));

        // Logo circle
        JPanel logoCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x4361EE));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,30));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(2, 2, getWidth()-4, getHeight()-4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoCircle.setOpaque(false);
        logoCircle.setPreferredSize(new Dimension(64, 64));
        logoCircle.setMaximumSize(new Dimension(64, 64));
        logoCircle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel logoEmoji = new JLabel("👥");
        logoEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        logoCircle.add(logoEmoji);

        JLabel appName = new JLabel("HRM System");
        appName.setFont(new Font(UITheme.F_DISPLAY.getFamily(), Font.BOLD, 26));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html>Human Resource<br>Management Portal</html>");
        tagline.setFont(UITheme.F_BODY);
        tagline.setForeground(new Color(255,255,255,140));
        tagline.setAlignmentX(LEFT_ALIGNMENT);

        // Feature pills
        JPanel pills = new JPanel();
        pills.setOpaque(false);
        pills.setLayout(new BoxLayout(pills, BoxLayout.Y_AXIS));
        pills.setAlignmentX(LEFT_ALIGNMENT);
        for (String f : new String[]{"✓  Employee management", "✓  Leave tracking", "✓  Yearly reports"}) {
            JLabel pill = new JLabel(f);
            pill.setFont(UITheme.F_SMALL);
            pill.setForeground(new Color(0xA5B4FC));
            pill.setAlignmentX(LEFT_ALIGNMENT);
            pill.setBorder(new EmptyBorder(3, 0, 3, 0));
            pills.add(pill);
        }

        brand.add(logoCircle);
        brand.add(Box.createVerticalStrut(18));
        brand.add(appName);
        brand.add(Box.createVerticalStrut(8));
        brand.add(tagline);
        brand.add(Box.createVerticalStrut(28));
        brand.add(pills);

        left.add(brand);
        root.add(left, BorderLayout.WEST);

        // ── RIGHT panel — login form ───────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 48, 0, 48));
        form.setPreferredSize(new Dimension(500, 400));

        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(UITheme.F_DISPLAY);
        welcome.setForeground(UITheme.SECONDARY);
        welcome.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(UITheme.F_BODY);
        sub.setForeground(UITheme.MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        usernameField = UITheme.createTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        usernameField.setAlignmentX(LEFT_ALIGNMENT);

        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setAlignmentX(LEFT_ALIGNMENT);

        JLabel errorLbl = new JLabel(" ");
        errorLbl.setFont(UITheme.F_SMALL);
        errorLbl.setForeground(UITheme.DANGER);
        errorLbl.setAlignmentX(LEFT_ALIGNMENT);

        JButton loginBtn = new JButton("Sign In") {
            boolean h = false;
            boolean p = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { h=true; repaint(); }
                    public void mouseExited(MouseEvent e)  { h=false; p=false; repaint(); }
                    public void mousePressed(MouseEvent e) { p=true; repaint(); }
                    public void mouseReleased(MouseEvent e){ p=false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = p ? UITheme.PRIMARY_DARK.darker() : h ? UITheme.PRIMARY_DARK : UITheme.PRIMARY;
                // shadow
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
                g2.fill(new RoundRectangle2D.Float(2, 4, getWidth()-4, getHeight()-2, 10, 10));
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-2, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginBtn.setFont(UITheme.F_BOLD);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setOpaque(false); loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false); loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(new EmptyBorder(12, 0, 12, 0));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hint = new JLabel("HR: hr / 1234   ·   Employee: emp / 1234");
        hint.setFont(UITheme.F_MICRO);
        hint.setForeground(new Color(0xBCC5D3));
        hint.setAlignmentX(LEFT_ALIGNMENT);

        form.add(welcome);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(32));
        form.add(fieldLabel("USERNAME"));
        form.add(Box.createVerticalStrut(6));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(16));
        form.add(fieldLabel("PASSWORD"));
        form.add(Box.createVerticalStrut(6));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(8));
        form.add(errorLbl);
        form.add(Box.createVerticalStrut(8));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(16));
        form.add(hint);

        right.add(form);
        root.add(right, BorderLayout.CENTER);

        ActionListener doLogin = e -> handleLogin(errorLbl);
        loginBtn.addActionListener(doLogin);
        usernameField.addActionListener(doLogin);
        passwordField.addActionListener(doLogin);

        setVisible(true);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_MICRO);
        l.setForeground(UITheme.MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void handleLogin(JLabel errorLbl) {
        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            errorLbl.setText(" ");

            if (username.isEmpty() || password.isEmpty()) {
                errorLbl.setText("⚠  Please enter username and password.");
                return;
            }

            AuthenticationService auth = new AuthenticationService();

            if (auth.authenticateHR(username, password)) {
                dispose();
                new HRDashboard();
                return;
            }

            int employeeId = auth.authenticateEmployee(username, password);
            if (employeeId != -1) {
                dispose();
                new EmployeeDashboard(employeeId);
            } else {
                errorLbl.setText("⚠  Invalid credentials. Please try again.");
                passwordField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed. Please check the server connection.");
        }
    }
}
