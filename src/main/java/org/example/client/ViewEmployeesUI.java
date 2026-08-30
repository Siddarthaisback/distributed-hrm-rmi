package org.example.client;

import org.example.model.Employee;
import org.example.remote.HRMService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ViewEmployeesUI extends JFrame {

    private DefaultTableModel model;
    private JTable table;
    private List<Employee> allEmployees;

    public ViewEmployeesUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.applyFrame(this, "Employee List", 1060, 560);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UITheme.createRootPanel();
        root.add(UITheme.createHeader("Employees", "View, edit, or delete registered employee records."), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(UITheme.BG);
        content.setBorder(new EmptyBorder(16, 20, 0, 20));
        root.add(content, BorderLayout.CENTER);

        // ── Top bar: search + CRUD buttons ────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(UITheme.F_BOLD);
        searchLbl.setForeground(UITheme.TEXT);
        JTextField searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(240, 34));
        searchBar.add(searchLbl);
        searchBar.add(searchField);

        JPanel crudBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        crudBtns.setOpaque(false);
        JButton editBtn   = UITheme.createPrimaryButton("✎  Edit");
        JButton deleteBtn = UITheme.createDangerButton("✗  Delete");
        crudBtns.add(editBtn);
        crudBtns.add(deleteBtn);

        topBar.add(searchBar, BorderLayout.WEST);
        topBar.add(crudBtns,  BorderLayout.EAST);
        content.add(topBar, BorderLayout.NORTH);

        // ── Table ──────────────────────────────────────────────────────────────
        String[] columns = {"ID", "First Name", "Last Name", "Passport", "Email", "Phone", "Department", "Position"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scroll = UITheme.wrapTable(table);

        JPanel card = UITheme.createCardPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel cardHdr = new JPanel(new BorderLayout());
        cardHdr.setBackground(UITheme.CARD);
        cardHdr.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(12, 18, 12, 18)));

        JLabel ttl = new JLabel("Employee Directory");
        ttl.setFont(UITheme.F_H2);
        ttl.setForeground(UITheme.SECONDARY);
        cardHdr.add(ttl, BorderLayout.WEST);

        // Row count label on the right of the card header
        JLabel countLbl = new JLabel("");
        countLbl.setFont(UITheme.F_SMALL);
        countLbl.setForeground(UITheme.MUTED);
        cardHdr.add(countLbl, BorderLayout.EAST);

        card.add(cardHdr, BorderLayout.NORTH);
        card.add(scroll,  BorderLayout.CENTER);
        content.add(card, BorderLayout.CENTER);

        // ── Bottom actions ─────────────────────────────────────────────────────
        JPanel actions = UITheme.createActionsPanel();
        JButton backBtn    = UITheme.createSecondaryButton("← Back");
        JButton refreshBtn = UITheme.createPrimaryButton("⟳  Refresh");
        actions.add(backBtn);
        actions.add(refreshBtn);
        root.add(actions, BorderLayout.SOUTH);

        add(root);
        loadEmployees(countLbl);

        // ── Listeners ──────────────────────────────────────────────────────────
        refreshBtn.addActionListener(e -> loadEmployees(countLbl));
        backBtn.addActionListener(e    -> { dispose(); new HRDashboard(); });

        // Double-click to edit
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openEditDialog();
            }
        });

        editBtn.addActionListener(e   -> openEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected(countLbl));

        // Live search filter
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String q = searchField.getText().trim();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                sorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + q));
                int visible = table.getRowCount();
                countLbl.setText(visible + " of " + model.getRowCount() + " employees");
            }
        });

        setVisible(true);
    }

    // ── Load ───────────────────────────────────────────────────────────────────
    private void loadEmployees(JLabel countLbl) {
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { UITheme.error(this, "Cannot connect to RMI server."); return; }
            allEmployees = svc.getAllEmployees();
            model.setRowCount(0);
            for (Employee e : allEmployees)
                model.addRow(new Object[]{
                    e.getEmployeeId(), e.getFirstName(), e.getLastName(),
                    e.getPassportNo(), e.getEmail(), e.getPhone(),
                    e.getDepartment(), e.getPosition()
                });
            countLbl.setText(allEmployees.size() + " employees");
        } catch (Exception e) {
            e.printStackTrace();
            UITheme.error(this, "Failed to load employees.");
        }
    }

    // ── Edit ───────────────────────────────────────────────────────────────────
    private void openEditDialog() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            UITheme.error(this, "Please select an employee row to edit.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int empId    = (int) model.getValueAt(modelRow, 0);

        Employee emp;
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { UITheme.error(this, "Cannot connect to RMI server."); return; }
            emp = svc.getEmployee(empId);
            if (emp == null) { UITheme.error(this, "Employee record not found."); return; }
        } catch (Exception ex) {
            ex.printStackTrace();
            UITheme.error(this, "Failed to load employee details.");
            return;
        }

        // ── Build dialog ───────────────────────────────────────────────────────
        JDialog dialog = new JDialog(this, "Edit Employee — #" + empId, true);
        dialog.setSize(560, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(UITheme.BG);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel hdr = new JPanel();
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.setBackground(UITheme.CARD);
        hdr.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(16, 22, 16, 22)));
        JLabel hdrTitle = new JLabel("Edit Employee");
        hdrTitle.setFont(UITheme.F_TITLE);
        hdrTitle.setForeground(UITheme.SECONDARY);
        JLabel hdrSub = new JLabel("Update details for " + emp.getFirstName() + " " + emp.getLastName());
        hdrSub.setFont(UITheme.F_BODY);
        hdrSub.setForeground(UITheme.MUTED);
        hdr.add(hdrTitle);
        hdr.add(Box.createVerticalStrut(3));
        hdr.add(hdrSub);
        dialog.add(hdr, BorderLayout.NORTH);

        // Form
        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBackground(UITheme.BG);
        formWrap.setBorder(new EmptyBorder(16, 22, 16, 22));

        JPanel form = new JPanel(new GridLayout(9, 2, 14, 10));
        form.setOpaque(false);

        JTextField fFirst    = prefilled(emp.getFirstName());
        JTextField fLast     = prefilled(emp.getLastName());
        JTextField fPassport = prefilled(emp.getPassportNo());
        JTextField fEmail    = prefilled(emp.getEmail());
        JTextField fPhone    = prefilled(emp.getPhone());
        JTextField fDept     = prefilled(emp.getDepartment());
        JTextField fPosition = prefilled(emp.getPosition());
        JTextField fJoinDate = prefilled(emp.getJoinDate() != null ? emp.getJoinDate().toString() : "");
        fJoinDate.setEditable(false);
        fJoinDate.setBackground(UITheme.SURFACE2);
        JTextField fUsername = prefilled(emp.getUsername());

        form.add(UITheme.createSectionLabel("First Name"));    form.add(fFirst);
        form.add(UITheme.createSectionLabel("Last Name"));     form.add(fLast);
        form.add(UITheme.createSectionLabel("Passport No"));   form.add(fPassport);
        form.add(UITheme.createSectionLabel("Email"));         form.add(fEmail);
        form.add(UITheme.createSectionLabel("Phone"));         form.add(fPhone);
        form.add(UITheme.createSectionLabel("Department"));    form.add(fDept);
        form.add(UITheme.createSectionLabel("Position"));      form.add(fPosition);
        form.add(UITheme.createSectionLabel("Join Date"));     form.add(fJoinDate);
        form.add(UITheme.createSectionLabel("Username"));      form.add(fUsername);

        formWrap.add(form, BorderLayout.CENTER);
        dialog.add(formWrap, BorderLayout.CENTER);

        // Dialog buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnRow.setOpaque(false);
        btnRow.setBorder(new MatteBorder(1, 0, 0, 0, UITheme.BORDER));
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        JButton saveBtn   = UITheme.createPrimaryButton("💾  Save Changes");
        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        dialog.add(btnRow, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String fn = fFirst.getText().trim();
            String ln = fLast.getText().trim();
            String pp = fPassport.getText().trim();
            if (fn.isEmpty() || ln.isEmpty() || pp.isEmpty()) {
                UITheme.error(dialog, "First Name, Last Name, and Passport No are required.");
                return;
            }
            try {
                Employee updated = new Employee();
                updated.setEmployeeId(empId);
                updated.setFirstName(fn);
                updated.setLastName(ln);
                updated.setPassportNo(pp);
                updated.setEmail(fEmail.getText().trim());
                updated.setPhone(fPhone.getText().trim());
                updated.setDepartment(fDept.getText().trim());
                updated.setPosition(fPosition.getText().trim());
                updated.setUsername(fUsername.getText().trim());
                // Keep existing join date
                updated.setJoinDate(emp.getJoinDate());

                HRMService svc = HRMClient.getService();
                if (svc == null) { UITheme.error(dialog, "Cannot connect to RMI server."); return; }

                boolean ok = svc.updateEmployeeProfile(updated);
                if (ok) {
                    UITheme.info(dialog, "Employee updated successfully.");
                    dialog.dispose();
                    // Find the countLbl — refresh with a temp label and update the real one
                    JLabel tmp = new JLabel();
                    loadEmployees(tmp);
                    // Update the real count label via the card header
                    updateCountLabel();
                } else {
                    UITheme.error(dialog, "Update failed. Passport No may already be in use.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                UITheme.error(dialog, "Error updating employee: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    // ── Delete ─────────────────────────────────────────────────────────────────
    private void deleteSelected(JLabel countLbl) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            UITheme.error(this, "Please select an employee row to delete.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int    empId = (int) model.getValueAt(modelRow, 0);
        String name  = model.getValueAt(modelRow, 1) + " " + model.getValueAt(modelRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete employee #" + empId + " — " + name + "?\n\n"
                + "This will also remove their login, leave records, and family details.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) { UITheme.error(this, "Cannot connect to RMI server."); return; }

            boolean ok = svc.deleteEmployee(empId);
            if (ok) {
                UITheme.info(this, "Employee #" + empId + " deleted successfully.");
                loadEmployees(countLbl);
            } else {
                UITheme.error(this, "Delete failed.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            UITheme.error(this, "Error deleting employee: " + ex.getMessage());
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private JTextField prefilled(String value) {
        JTextField f = UITheme.createTextField();
        f.setText(value != null ? value : "");
        return f;
    }

    /** Refreshes table after edit when we don't have a direct reference to countLbl. */
    private void updateCountLabel() {
        // Traverse the component tree to find the countLbl and update it
        // Simpler: just reload. countLbl is inside cardHdr which is inside the card.
        // We rebuild the count label text by getting the model row count.
        // Since we don't store countLbl as a field, we use a local refresh here.
        try {
            HRMService svc = HRMClient.getService();
            if (svc == null) return;
            allEmployees = svc.getAllEmployees();
            model.setRowCount(0);
            for (Employee e : allEmployees)
                model.addRow(new Object[]{
                    e.getEmployeeId(), e.getFirstName(), e.getLastName(),
                    e.getPassportNo(), e.getEmail(), e.getPhone(),
                    e.getDepartment(), e.getPosition()
                });
        } catch (Exception ignored) {}
    }
}
