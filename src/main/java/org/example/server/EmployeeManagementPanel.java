package org.example.server;

import org.example.model.Employee;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * EmployeeManagementPanel — full CRUD panel embedded in the server window.
 *
 * Features:
 *   - Table listing all employees (auto-refreshes)
 *   - Add Employee (opens form dialog)
 *   - Edit Employee (pre-fills form with selected row)
 *   - Delete Employee (confirm dialog)
 *   - Search / filter by name, department, or ID
 *
 * Requires: ServerTheme, a live HRMServiceImpl accessible via ServerManager.getService()
 */
public class EmployeeManagementPanel extends JPanel {

    // ── Table columns ──────────────────────────────────────────────────────────
    private static final String[] COLS = {
        "ID", "First Name", "Last Name", "Email", "Phone", "Department", "Position", "Join Date"
    };

    private JTable           table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JLabel            countLbl;

    private List<Employee> allEmployees; // full unfiltered list

    public EmployeeManagementPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ServerTheme.BG);
        setBorder(new EmptyBorder(16, 18, 16, 18));
        buildUI();
        refreshTable();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI BUILD
    // ══════════════════════════════════════════════════════════════════════════
    private void buildUI() {
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(ServerTheme.SURFACE);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, ServerTheme.BORDER),
                new EmptyBorder(12, 16, 12, 16)));

        // Title
        JLabel title = new JLabel("Employee Management");
        title.setFont(ServerTheme.F_TITLE);
        title.setForeground(ServerTheme.TEXT);

        // Right side: search + buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        searchField = darkTextField("Search name, dept, ID...", 200);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { applyFilter(searchField.getText()); }
        });

        JButton refreshBtn  = ServerTheme.actionButton("\u21BB  Refresh",          new Color(0x334155));
        JButton addBtn      = ServerTheme.actionButton("\u002B  Add",               ServerTheme.SUCCESS);
        JButton editBtn     = ServerTheme.actionButton("\u270E  Edit",              ServerTheme.ACCENT);
        JButton deleteBtn   = ServerTheme.actionButton("\u2715  Delete",            ServerTheme.DANGER);
        JButton leaveBtn    = ServerTheme.actionButton("\uD83D\uDCC5  Leave Days",  new Color(0x7C3AED));

        refreshBtn.addActionListener(e -> refreshTable());
        addBtn.addActionListener(e    -> openForm(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        leaveBtn.addActionListener(e  -> editLeaveAllowance());

        right.add(searchField);
        right.add(refreshBtn);
        right.add(addBtn);
        right.add(editBtn);
        right.add(leaveBtn);
        right.add(deleteBtn);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(new Color(0x111421));
        table.setForeground(ServerTheme.TEXT);
        table.setFont(ServerTheme.F_BODY);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0x1E3A5F));
        table.setSelectionForeground(ServerTheme.TEXT);
        table.setFillsViewportHeight(true);

        // Column header style
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0x161926));
        header.setForeground(ServerTheme.MUTED);
        header.setFont(new Font("Monospaced", Font.BOLD, 11));
        header.setBorder(new MatteBorder(0, 0, 1, 0, ServerTheme.BORDER));
        header.setReorderingAllowed(false);

        // Column widths
        int[] widths = {45, 100, 100, 160, 100, 120, 120, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Alternating row renderer
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        // Double-click to edit
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new MatteBorder(0, 0, 0, 0, ServerTheme.BORDER));
        scroll.getViewport().setBackground(new Color(0x111421));
        scroll.getVerticalScrollBar().setUI(new ServerTheme.DarkScrollBarUI());
        scroll.getVerticalScrollBar().setBackground(new Color(0x111421));
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ServerTheme.SURFACE);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, ServerTheme.BORDER),
                new EmptyBorder(8, 16, 8, 16)));

        countLbl = new JLabel("0 employees");
        countLbl.setFont(ServerTheme.F_SMALL);
        countLbl.setForeground(ServerTheme.MUTED);

        JLabel hint = new JLabel("Double-click a row to edit  \u2022  Select a row then click Delete");
        hint.setFont(ServerTheme.F_SMALL);
        hint.setForeground(ServerTheme.MUTED);

        bar.add(countLbl, BorderLayout.WEST);
        bar.add(hint,     BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DATA
    // ══════════════════════════════════════════════════════════════════════════
    public void refreshTable() {
        try {
            allEmployees = ServerManager.getService().getAllEmployees();
            applyFilter(searchField != null ? searchField.getText() : "");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load employees:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter(String query) {
        tableModel.setRowCount(0);
        if (allEmployees == null) return;

        String q = query == null ? "" : query.trim().toLowerCase();
        int shown = 0;

        for (Employee e : allEmployees) {
            String fullName = (e.getFirstName() + " " + e.getLastName()).toLowerCase();
            String dept     = e.getDepartment() == null ? "" : e.getDepartment().toLowerCase();
            String idStr    = String.valueOf(e.getEmployeeId());

            if (q.isEmpty() || fullName.contains(q) || dept.contains(q) || idStr.contains(q)) {
                tableModel.addRow(new Object[]{
                    e.getEmployeeId(),
                    e.getFirstName(),
                    e.getLastName(),
                    e.getEmail(),
                    e.getPhone(),
                    e.getDepartment(),
                    e.getPosition(),
                    e.getJoinDate()
                });
                shown++;
            }
        }

        if (countLbl != null)
            countLbl.setText(shown + " employee" + (shown == 1 ? "" : "s")
                    + (q.isEmpty() ? "" : " matching \"" + query.trim() + "\""));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CRUD ACTIONS
    // ══════════════════════════════════════════════════════════════════════════
    private void editLeaveAllowance() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee row first.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int empId = (int) tableModel.getValueAt(row, 0);
        String empName = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);

        String input = JOptionPane.showInputDialog(this,
                "Set total leave days for " + empName + ":",
                "Edit Leave Allowance",
                JOptionPane.PLAIN_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        try {
            int newDays = Integer.parseInt(input.trim());
            if (newDays < 0) {
                JOptionPane.showMessageDialog(this, "Leave days cannot be negative.", "Invalid", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = ServerManager.getService().updateLeaveAllowance(empId, newDays);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        empName + "'s leave allowance updated to " + newDays + " days.",
                        "Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee row to edit.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int empId = (int) tableModel.getValueAt(row, 0);
        try {
            Employee emp = ServerManager.getService().getEmployee(empId);
            openForm(emp);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load employee: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee row to delete.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int    empId = (int) tableModel.getValueAt(row, 0);
        String name  = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete employee #" + empId + " — " + name + "?\n"
                + "This will also remove their login, leave records, and family details.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = ServerManager.getService().deleteEmployee(empId);
            if (ok) {
                refreshTable();
                JOptionPane.showMessageDialog(this,
                        "Employee #" + empId + " deleted successfully.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Delete failed — employee may not exist.",
                        "Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Delete error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Open the Add/Edit form dialog.
     * @param emp null = Add mode, non-null = Edit mode (pre-filled)
     */
    private void openForm(Employee emp) {
        boolean isEdit = (emp != null);
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit Employee" : "Add Employee",
                true);
        dialog.setSize(520, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(ServerTheme.BG);
        dialog.setLayout(new BorderLayout());

        // ── Form fields ────────────────────────────────────────────────────────
        JTextField fFirst      = formField(isEdit ? emp.getFirstName()   : "");
        JTextField fLast       = formField(isEdit ? emp.getLastName()    : "");
        JTextField fEmail      = formField(isEdit ? emp.getEmail()       : "");
        JTextField fPhone      = formField(isEdit ? emp.getPhone()       : "");
        JTextField fPassport   = formField(isEdit ? emp.getPassportNo()  : "");
        JTextField fDept       = formField(isEdit ? emp.getDepartment()  : "");
        JTextField fPosition   = formField(isEdit ? emp.getPosition()    : "");
        JTextField fJoinDate   = formField(isEdit && emp.getJoinDate() != null
                                           ? emp.getJoinDate().toString() : "");
        JTextField fUsername   = formField(isEdit ? emp.getUsername()    : "");
        JTextField fPassword   = formField(isEdit ? emp.getPassword()    : "");

        // ── Form layout ────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ServerTheme.BG);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(6, 4, 6, 4);
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        String[][] rows = {
            {"First Name",  null},    {"Last Name",   null},
            {"Email",       null},    {"Phone",       null},
            {"Passport No", null},    {"Department",  null},
            {"Position",    null},    {"Join Date (YYYY-MM-DD)", null},
            {"Username",    null},    {"Password",    null},
        };
        JTextField[] fields = {
            fFirst, fLast, fEmail, fPhone, fPassport,
            fDept, fPosition, fJoinDate, fUsername, fPassword
        };

        for (int i = 0; i < fields.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.3;
            JLabel lbl = new JLabel(rows[i][0]);
            lbl.setFont(ServerTheme.F_SMALL);
            lbl.setForeground(ServerTheme.MUTED);
            form.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 0.7;
            form.add(fields[i], gc);
        }

        // ── Buttons ────────────────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(ServerTheme.SURFACE);
        btnRow.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, ServerTheme.BORDER),
                new EmptyBorder(12, 16, 12, 16)));

        JButton cancelBtn = ServerTheme.actionButton("Cancel", new Color(0x334155));
        JButton saveBtn   = ServerTheme.actionButton(isEdit ? "Save Changes" : "Add Employee",
                                                      ServerTheme.SUCCESS);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            // ── Validate ───────────────────────────────────────────────────────
            if (fFirst.getText().trim().isEmpty() || fLast.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "First name and last name are required.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate joinDate = null;
            if (!fJoinDate.getText().trim().isEmpty()) {
                try {
                    joinDate = LocalDate.parse(fJoinDate.getText().trim());
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Join date must be in YYYY-MM-DD format.",
                            "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // ── Build Employee object ──────────────────────────────────────────
            Employee e2 = new Employee();
            if (isEdit) e2.setEmployeeId(emp.getEmployeeId());
            e2.setFirstName(fFirst.getText().trim());
            e2.setLastName(fLast.getText().trim());
            e2.setEmail(fEmail.getText().trim());
            e2.setPhone(fPhone.getText().trim());
            e2.setPassportNo(fPassport.getText().trim());
            e2.setDepartment(fDept.getText().trim());
            e2.setPosition(fPosition.getText().trim());
            e2.setJoinDate(joinDate);
            e2.setUsername(fUsername.getText().trim());
            e2.setPassword(fPassword.getText().trim());

            // ── Call service ───────────────────────────────────────────────────
            try {
                boolean ok;
                if (isEdit) {
                    ok = ServerManager.getService().updateEmployeeProfile(e2);
                } else {
                    ok = ServerManager.getService().registerEmployee(e2);
                }

                if (ok) {
                    dialog.dispose();
                    refreshTable();
                    JOptionPane.showMessageDialog(this,
                            (isEdit ? "Employee updated." : "Employee added."),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            (isEdit ? "Update failed." : "Add failed — username/passport may already exist."),
                            "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        dialog.add(form,   BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private JTextField darkTextField(String placeholder, int width) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ServerTheme.CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(ServerTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setForeground(ServerTheme.TEXT);
        f.setCaretColor(ServerTheme.TEXT);
        f.setFont(ServerTheme.F_BODY);
        f.setBorder(new EmptyBorder(6, 10, 6, 10));
        f.setPreferredSize(new Dimension(width, 32));
        // Placeholder
        f.setText(placeholder);
        f.setForeground(ServerTheme.MUTED);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(ServerTheme.TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(ServerTheme.MUTED); }
            }
        });
        return f;
    }

    private JTextField formField(String value) {
        JTextField f = new JTextField(value) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ServerTheme.CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.setColor(ServerTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setForeground(ServerTheme.TEXT);
        f.setCaretColor(ServerTheme.TEXT);
        f.setFont(ServerTheme.F_BODY);
        f.setBorder(new EmptyBorder(6, 10, 6, 10));
        f.setPreferredSize(new Dimension(200, 32));
        return f;
    }

    // ── Alternating row colours ────────────────────────────────────────────────
    private static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        private static final Color ROW_A = new Color(0x111421);
        private static final Color ROW_B = new Color(0x151929);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setFont(ServerTheme.F_BODY);
            setBorder(new EmptyBorder(0, 8, 0, 8));
            if (isSelected) {
                setBackground(new Color(0x1E3A5F));
                setForeground(ServerTheme.TEXT);
            } else {
                setBackground(row % 2 == 0 ? ROW_A : ROW_B);
                setForeground(ServerTheme.TEXT);
            }
            return this;
        }
    }
}
