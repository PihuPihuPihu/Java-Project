package com.payroll;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PayrollUI extends JFrame {

    // ── Colors ──────────────────────────────────────────────
    private static final Color BG_DARK     = new Color(13, 17, 30);
    private static final Color BG_CARD     = new Color(22, 28, 48);
    private static final Color BG_ROW_ALT  = new Color(28, 36, 58);
    private static final Color ACCENT_BLUE = new Color(56, 139, 253);
    private static final Color ACCENT_TEAL = new Color(45, 212, 191);
    private static final Color ACCENT_GOLD = new Color(250, 189, 47);
    private static final Color TEXT_PRI    = new Color(230, 237, 255);
    private static final Color TEXT_SEC    = new Color(130, 148, 190);
    private static final Color BORDER_COL  = new Color(40, 52, 80);
    private static final Color SUCCESS     = new Color(52, 199, 89);
    private static final Color DANGER      = new Color(255, 69, 58);

    private static final Font FONT_HEAD  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_SUB   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_MONO  = new Font("Courier New", Font.PLAIN, 13);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);

    private final EmployeeDAO dao = new EmployeeDAO();

    // Table
    private DefaultTableModel tableModel;
    private JTable empTable;

    // Stat labels
    private JLabel lblTotalEmp, lblAvgSalary, lblTotalPayroll;

    public PayrollUI() {
        super("Payroll Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        refreshTable();
    }

    // ── HEADER ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(new MatteBorder(0, 0, 2, 0, ACCENT_BLUE));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("  💼  Payroll Management System");
        title.setFont(FONT_HEAD);
        title.setForeground(TEXT_PRI);
        header.add(title, BorderLayout.WEST);

        // Stats row
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        stats.setBackground(BG_CARD);
        lblTotalEmp    = makeStatLabel("Employees: —");
        lblAvgSalary   = makeStatLabel("Avg Salary: —");
        lblTotalPayroll= makeStatLabel("Total Payroll: —");
        stats.add(lblTotalEmp);
        stats.add(lblAvgSalary);
        stats.add(lblTotalPayroll);
        header.add(stats, BorderLayout.EAST);
        return header;
    }

    private JLabel makeStatLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(ACCENT_TEAL);
        return l;
    }

    // ── CENTER ───────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(12, 12, 4, 12));

        center.add(buildTablePanel(), BorderLayout.CENTER);
        center.add(buildActionPanel(), BorderLayout.EAST);
        return center;
    }

    // ── TABLE ────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_DARK);

        // Title
        JLabel lbl = new JLabel("Employee Records");
        lbl.setFont(FONT_SUB);
        lbl.setForeground(TEXT_SEC);
        panel.add(lbl, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "Annual Salary (₹)", "Bonus (₹)", "Total CTC (₹)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        empTable = new JTable(tableModel);
        styleTable(empTable);

        JScrollPane scroll = new JScrollPane(empTable);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(new LineBorder(BORDER_COL, 1));
        panel.add(scroll, BorderLayout.CENTER);

        // Bottom toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.setBackground(BG_DARK);

        JButton btnAdd    = makeBtn("＋  Add Employee", ACCENT_BLUE);
        JButton btnDelete = makeBtn("✕  Delete",        DANGER);
        JButton btnRefresh= makeBtn("⟳  Refresh",       TEXT_SEC);

        btnAdd.addActionListener(e -> showAddDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> refreshTable());

        toolbar.add(btnAdd);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private void styleTable(JTable t) {
        t.setBackground(BG_CARD);
        t.setForeground(TEXT_PRI);
        t.setFont(FONT_BODY);
        t.setRowHeight(36);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(56, 139, 253, 60));
        t.setSelectionForeground(TEXT_PRI);
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(16, 22, 38));
        header.setForeground(TEXT_SEC);
        header.setFont(FONT_LABEL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COL));
        header.setReorderingAllowed(false);

        // Column widths
        int[] widths = {50, 180, 160, 130, 160};
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Alternating row renderer
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBackground(sel ? new Color(56, 139, 253, 80) :
                              row % 2 == 0 ? BG_CARD : BG_ROW_ALT);
                setForeground(col == 4 ? ACCENT_GOLD : TEXT_PRI);
                setFont(col == 4 ? FONT_LABEL : FONT_BODY);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(col >= 2 ? RIGHT : LEFT);
                return this;
            }
        });
    }

    // ── ACTION PANEL ─────────────────────────────────────────
    private JPanel buildActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_DARK);
        panel.setPreferredSize(new Dimension(260, 0));

        panel.add(buildTaskCard("📊  Calculate Salary",
            "Compute salary breakdown\nwith tax & deductions",
            ACCENT_BLUE, e -> showCalculateSalary()));

        panel.add(Box.createVerticalStrut(10));

        panel.add(buildTaskCard("🎁  Update Bonus",
            "Modify employee bonus\nfor selected record",
            ACCENT_TEAL, e -> showUpdateBonus()));

        panel.add(Box.createVerticalStrut(10));

        panel.add(buildTaskCard("🧾  Generate Payslip",
            "Create full monthly\npayslip with breakdown",
            ACCENT_GOLD, e -> showPayslip()));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildTaskCard(String title, String desc, Color accent, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1),
            new EmptyBorder(14, 16, 14, 16)
        ));
        card.setMaximumSize(new Dimension(260, 140));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_SUB);
        lblTitle.setForeground(accent);

        JLabel lblDesc = new JLabel("<html>" + desc.replace("\n", "<br>") + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(TEXT_SEC);

        JButton btn = makeBtn("Open →", accent);
        btn.addActionListener(action);
        btn.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }

    // ── STATUS BAR ───────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bar.setBackground(new Color(8, 12, 22));
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COL));
        JLabel lbl = new JLabel("  ● Connected to payroll.db  |  SQLite  |  Java Swing UI");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SEC);
        bar.add(lbl);
        bar.setPreferredSize(new Dimension(0, 26));
        return bar;
    }

    // ── REFRESH TABLE ────────────────────────────────────────
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Employee> emps = dao.getAllEmployees();
        double totalSalary = 0, totalCtc = 0;
        for (Employee e : emps) {
            tableModel.addRow(new Object[]{
                e.getId(),
                e.getName(),
                String.format("%,.2f", e.getSalary()),
                String.format("%,.2f", e.getBonus()),
                String.format("%,.2f", e.getTotalCompensation())
            });
            totalSalary += e.getSalary();
            totalCtc    += e.getTotalCompensation();
        }
        int n = emps.size();
        lblTotalEmp.setText("Employees: " + n);
        lblAvgSalary.setText(n > 0 ? String.format("Avg Salary: ₹%,.0f", totalSalary / n) : "Avg Salary: —");
        lblTotalPayroll.setText(String.format("Total Payroll: ₹%,.0f", totalCtc));
    }

    private Employee getSelectedEmployee() {
        int row = empTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an employee from the table first.");
            return null;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        return dao.getEmployeeById(id);
    }

    // ══════════════════════════════════════════════════════════
    //  TASK 1: CALCULATE SALARY
    // ══════════════════════════════════════════════════════════
    private void showCalculateSalary() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        double monthly     = emp.getSalary() / 12.0;
        double pf          = PayslipGenerator.calculatePF(monthly);
        double tax         = PayslipGenerator.calculateMonthlyTax(emp.getSalary());
        double gratuity    = PayslipGenerator.calculateGratuity(monthly);
        double net         = monthly - pf - tax - gratuity;

        JDialog dlg = makeDialog("Salary Calculator — " + emp.getName(), 460, 420);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG_CARD);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 4, 5, 4);

        addSalaryRow(content, gc, 0, "Employee",          emp.getName(),             TEXT_PRI,   false);
        addSalaryRow(content, gc, 1, "Annual CTC",        fmt(emp.getTotalCompensation()), ACCENT_GOLD, false);
        addDivider(content, gc, 2);
        addSalaryRow(content, gc, 3, "Annual Salary",     fmt(emp.getSalary()),      TEXT_PRI,   false);
        addSalaryRow(content, gc, 4, "Annual Bonus",      fmt(emp.getBonus()),       ACCENT_TEAL,false);
        addDivider(content, gc, 5);
        addSalaryRow(content, gc, 6, "Monthly Gross",     fmt(monthly + emp.getBonus()/12.0), ACCENT_BLUE, true);
        addSalaryRow(content, gc, 7, "  ↳ PF (12%)",      "- " + fmt(pf),           DANGER,     false);
        addSalaryRow(content, gc, 8, "  ↳ TDS",           "- " + fmt(tax),          DANGER,     false);
        addSalaryRow(content, gc, 9, "  ↳ Gratuity",      "- " + fmt(gratuity),     DANGER,     false);
        addDivider(content, gc, 10);
        addSalaryRow(content, gc, 11,"Monthly Net Pay",   fmt(net),                 SUCCESS,    true);

        dlg.add(content, BorderLayout.CENTER);
        dlg.add(closeBtn(dlg), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void addSalaryRow(JPanel p, GridBagConstraints gc, int row,
                               String label, String value, Color valueColor, boolean bold) {
        gc.gridy = row;

        gc.gridx = 0; gc.weightx = 0.5;
        JLabel lbl = new JLabel(label);
        lbl.setFont(bold ? FONT_SUB : FONT_BODY);
        lbl.setForeground(TEXT_SEC);
        p.add(lbl, gc);

        gc.gridx = 1; gc.weightx = 0.5;
        JLabel val = new JLabel(value, SwingConstants.RIGHT);
        val.setFont(bold ? FONT_SUB : FONT_BODY);
        val.setForeground(valueColor);
        p.add(val, gc);
    }

    private void addDivider(JPanel p, GridBagConstraints gc, int row) {
        gc.gridy = row; gc.gridx = 0; gc.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COL);
        p.add(sep, gc);
        gc.gridwidth = 1;
    }

    // ══════════════════════════════════════════════════════════
    //  TASK 2: UPDATE BONUS
    // ══════════════════════════════════════════════════════════
    private void showUpdateBonus() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        JDialog dlg = makeDialog("Update Bonus — " + emp.getName(), 400, 300);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG_CARD);
        content.setBorder(new EmptyBorder(24, 28, 24, 28));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 4, 8, 4);

        JLabel empLbl = new JLabel(emp.getName());
        empLbl.setFont(FONT_SUB);
        empLbl.setForeground(ACCENT_TEAL);

        JLabel currentLbl = new JLabel("Current Bonus: " + fmt(emp.getBonus()));
        currentLbl.setFont(FONT_BODY);
        currentLbl.setForeground(TEXT_SEC);

        JLabel inputLbl = new JLabel("New Annual Bonus (₹):");
        inputLbl.setFont(FONT_LABEL);
        inputLbl.setForeground(TEXT_PRI);

        JTextField bonusField = new JTextField(String.valueOf((int) emp.getBonus()));
        styleField(bonusField);

        // Preset buttons
        JPanel presets = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        presets.setBackground(BG_CARD);
        for (int pct : new int[]{5, 10, 15, 20}) {
            JButton pb = makeSmallBtn("+" + pct + "%");
            final int p = pct;
            pb.addActionListener(e -> {
                double inc = emp.getSalary() * p / 100.0;
                bonusField.setText(String.format("%.0f", inc));
            });
            presets.add(pb);
        }

        JButton saveBtn = makeBtn("✔  Save Bonus", ACCENT_TEAL);
        saveBtn.addActionListener(e -> {
            try {
                double newBonus = Double.parseDouble(bonusField.getText().trim());
                if (newBonus < 0) throw new NumberFormatException();
                if (dao.updateBonus(emp.getId(), newBonus)) {
                    showSuccess(dlg, String.format("Bonus updated to %s for %s", fmt(newBonus), emp.getName()));
                    refreshTable();
                } else {
                    showError("Failed to update bonus.");
                }
            } catch (NumberFormatException ex) {
                showError("Enter a valid positive number.");
            }
        });

        gc.gridy = 0; gc.gridx = 0; gc.gridwidth = 2; content.add(empLbl, gc);
        gc.gridy = 1; content.add(currentLbl, gc);
        gc.gridy = 2; content.add(makeSep(), gc);
        gc.gridy = 3; gc.gridwidth = 1; content.add(inputLbl, gc);
        gc.gridx = 1; content.add(bonusField, gc);
        gc.gridy = 4; gc.gridx = 0; gc.gridwidth = 2; content.add(presets, gc);
        gc.gridy = 5; content.add(saveBtn, gc);

        dlg.add(content, BorderLayout.CENTER);
        dlg.add(closeBtn(dlg), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════
    //  TASK 3: GENERATE PAYSLIP
    // ══════════════════════════════════════════════════════════
    private void showPayslip() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        JDialog dlg = makeDialog("Payslip — " + emp.getName(), 560, 500);

        JTextArea area = new JTextArea(PayslipGenerator.generatePayslip(emp));
        area.setFont(FONT_MONO);
        area.setForeground(ACCENT_TEAL);
        area.setBackground(new Color(8, 14, 26));
        area.setEditable(false);
        area.setBorder(new EmptyBorder(16, 16, 16, 16));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        dlg.add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        south.setBackground(BG_CARD);
        JButton copyBtn = makeBtn("📋  Copy", ACCENT_BLUE);
        copyBtn.addActionListener(e -> {
            area.selectAll();
            area.copy();
            copyBtn.setText("✔  Copied!");
            Timer t = new Timer(1500, ev -> copyBtn.setText("📋  Copy"));
            t.setRepeats(false); t.start();
        });
        south.add(copyBtn);
        south.add(closeBtn(dlg));
        dlg.add(south, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── ADD EMPLOYEE DIALOG ───────────────────────────────────
    private void showAddDialog() {
        JDialog dlg = makeDialog("Add New Employee", 380, 290);
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG_CARD);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(7, 4, 7, 4);

        JTextField nameField   = new JTextField(); styleField(nameField);
        JTextField salaryField = new JTextField("50000"); styleField(salaryField);
        JTextField bonusField  = new JTextField("5000");  styleField(bonusField);

        addFormRow(content, gc, 0, "Full Name:",           nameField);
        addFormRow(content, gc, 1, "Annual Salary (₹):",   salaryField);
        addFormRow(content, gc, 2, "Annual Bonus (₹):",    bonusField);

        JButton addBtn = makeBtn("＋  Add Employee", ACCENT_BLUE);
        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2;
        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
                double sal  = Double.parseDouble(salaryField.getText().trim());
                double bon  = Double.parseDouble(bonusField.getText().trim());
                if (sal < 0 || bon < 0) throw new NumberFormatException();
                if (dao.addEmployee(name, sal, bon)) {
                    showSuccess(dlg, name + " added successfully!");
                    refreshTable();
                }
            } catch (NumberFormatException ex) {
                showError("Enter valid numbers for salary and bonus.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        content.add(addBtn, gc);
        dlg.add(content, BorderLayout.CENTER);
        dlg.add(closeBtn(dlg), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void addFormRow(JPanel p, GridBagConstraints gc, int row, String label, JTextField field) {
        gc.gridy = row;
        gc.gridx = 0; gc.weightx = 0.4;
        JLabel l = new JLabel(label); l.setFont(FONT_LABEL); l.setForeground(TEXT_SEC);
        p.add(l, gc);
        gc.gridx = 1; gc.weightx = 0.6;
        p.add(field, gc);
    }

    // ── DELETE ────────────────────────────────────────────────
    private void deleteSelected() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;
        int res = JOptionPane.showConfirmDialog(this,
            "Delete " + emp.getName() + "?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            dao.deleteEmployee(emp.getId());
            refreshTable();
        }
    }

    // ── HELPERS ───────────────────────────────────────────────
    private JDialog makeDialog(String title, int w, int h) {
        JDialog dlg = new JDialog(this, title, true);
        dlg.setSize(w, h);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(BG_CARD);
        return dlg;
    }

    private JButton makeBtn(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_LABEL);
        btn.setForeground(fg);
        btn.setBackground(BG_DARK);
        btn.setBorder(new CompoundBorder(
            new LineBorder(fg, 1),
            new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 40)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BG_DARK); }
        });
        return btn;
    }

    private JButton makeSmallBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(TEXT_SEC);
        btn.setBackground(new Color(30, 40, 62));
        btn.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1),
            new EmptyBorder(3, 8, 3, 8)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton closeBtn(JDialog dlg) {
        JButton btn = makeBtn("Close", TEXT_SEC);
        btn.addActionListener(e -> dlg.dispose());
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        p.setBackground(BG_CARD);
        p.add(btn);
        return btn;
    }

    private void styleField(JTextField f) {
        f.setFont(FONT_BODY);
        f.setForeground(TEXT_PRI);
        f.setBackground(new Color(16, 22, 38));
        f.setCaretColor(ACCENT_BLUE);
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private JSeparator makeSep() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER_COL);
        return s;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(JDialog parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private String fmt(double v) {
        return String.format("₹ %,.2f", v);
    }
}
