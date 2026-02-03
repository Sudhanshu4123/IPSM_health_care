package com.ipsm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.ipsm.db.DatabaseManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PatientManagementFrame extends JFrame {
    private UserSession session;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JTextField txtFromDate;
    private JTextField txtToDate;
    private JLabel lblCount;
    private JButton btnAction; // Dynamic action button (Reprint or Done)
    private JComboBox<String> cmbViewMode; // To toggle views for Admins

    public PatientManagementFrame(UserSession session) {
        this.session = session;
        setTitle("Manage Patients - IPSM Health Care");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(new Color(245, 245, 255));
        searchPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(15);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadPatients();
            }
        });
        searchPanel.add(txtSearch);

        searchPanel.add(new JLabel("From:"));
        txtFromDate = new JTextField(8);
        txtFromDate.setEditable(false);
        txtFromDate.setBackground(Color.WHITE);
        txtFromDate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtFromDate.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                DatePicker dp = new DatePicker(PatientManagementFrame.this);
                dp.setVisible(true);
                String picked = dp.getPickedDate();
                if (!picked.isEmpty()) {
                    txtFromDate.setText(picked);
                    loadPatients();
                }
            }
        });
        searchPanel.add(txtFromDate);

        searchPanel.add(new JLabel("To:"));
        txtToDate = new JTextField(8);
        txtToDate.setEditable(false);
        txtToDate.setBackground(Color.WHITE);
        txtToDate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtToDate.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                DatePicker dp = new DatePicker(PatientManagementFrame.this);
                dp.setVisible(true);
                String picked = dp.getPickedDate();
                if (!picked.isEmpty()) {
                    txtToDate.setText(picked);
                    loadPatients();
                }
            }
        });
        searchPanel.add(txtToDate);

        lblCount = new JLabel("Total: 0");
        lblCount.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(lblCount);

        add(searchPanel, BorderLayout.NORTH);

        // View Mode Selector (For Non-Doctors)
        if (!"DOCTOR".equalsIgnoreCase(session.getRole())) {
            searchPanel.add(new JLabel("View:"));
            cmbViewMode = new JComboBox<>(new String[] { "Registrations", "Test Status" });
            cmbViewMode.addActionListener(e -> {
                updateTableStructure();
                loadPatients();
            });
            searchPanel.add(cmbViewMode);
        }

        // Set default dates
        setCurrentDates();

        // --- Table ---
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Initialize btnAction early (needed for updateTableStructure)
        btnAction = new JButton("Action");
        btnAction.setForeground(Color.BLACK);

        // Initial Table Setup
        updateTableStructure();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // btnAction configured in updateTableStructure

        // btnAction configured in updateTableStructure

        btnAction.addActionListener(e -> handleAction());
        bottomPanel.add(btnAction);
        add(bottomPanel, BorderLayout.SOUTH);

        // Events
        txtSearch.addActionListener(e -> loadPatients());

        loadPatients();
    }

    private void updateTableStructure() {
        String[] columns;
        if (isTestView()) {
            columns = new String[] { "Reg ID", "Test Code", "Reg. Date", "Patient Name", "Gender", "Age", "Test Name",
                    "Status" };
        } else {
            columns = new String[] { "Reg ID", "Patient ID", "Patient Name", "Gender", "Age", "Mobile", "Email",
                    "Reg. Date", "Total Amount", "Status", "Action" };
        }

        tableModel.setColumnIdentifiers(columns);

        // Hide appropriate ID columns
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        if (isTestView()) {
            // Hide Test Code
            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setPreferredWidth(0);

            btnAction.setText("Mark as Done");
            btnAction.setBackground(new Color(46, 204, 113));
            if (!session.canTestStatus()) {
                btnAction.setEnabled(false);
                btnAction.setToolTipText("You do not have permission to update test status.");
            } else {
                btnAction.setEnabled(true);
                btnAction.setToolTipText(null);
            }
        } else {
            btnAction.setText("Bill Receipt");
            btnAction.setBackground(new Color(52, 152, 219));
            btnAction.setEnabled(true); // Always enable reprint
        }
    }

    // Fallback constructor if referenced elsewhere without session (though
    // currently updated)
    public PatientManagementFrame() {
        this(new UserSession("guest", "GUEST", null, false, false, false, false, false, false, false, false, false,
                false, false));
    }

    private boolean isTestView() {
        if ("DOCTOR".equalsIgnoreCase(session.getRole())) {
            return true;
        }
        return cmbViewMode != null && "Test Status".equals(cmbViewMode.getSelectedItem());
    }

    private void handleAction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry.");
            return;
        }

        if (isTestView()) {
            if (session.canTestStatus()) {
                markAsDone(row);
            } else {
                JOptionPane.showMessageDialog(this, "Permission Denied: You cannot update test status.");
            }
        } else {
            reprintSelected(row);
        }
    }

    private void markAsDone(int row) {
        String status = (String) tableModel.getValueAt(row, 7);
        if ("Done".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Already marked as Done.");
            return;
        }

        long regId = (Long) tableModel.getValueAt(row, 0);
        String testCode = (String) tableModel.getValueAt(row, 1);

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE registration_tests SET status = 'Done' WHERE reg_id = ? AND test_code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, regId);
                pstmt.setString(2, testCode);
                int updated = pstmt.executeUpdate();
                if (updated > 0) {
                    tableModel.setValueAt("Done", row, 7);
                    JOptionPane.showMessageDialog(this, "Test updated to Done.");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void reprintSelected(int row) {
        long regId = (Long) tableModel.getValueAt(row, 0);
        // Note: With the new granular view, reprint might print the whole receipt for
        // the Reg ID.
        // We'll proceed with reprinting the broad receipt for simplicity as per
        // previous logic.

        // Need to refetch basic patient info for reprint logic or adapt existing logic
        // Re-using the previous logic essentially:
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM registrations WHERE reg_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, regId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int patientId = rs.getInt("patient_id");
                    String pSql = "SELECT * FROM patients WHERE patient_id = ?";
                    try (PreparedStatement pPstmt = conn.prepareStatement(pSql)) {
                        pPstmt.setInt(1, patientId);
                        ResultSet pRs = pPstmt.executeQuery();
                        if (pRs.next()) {
                            ReceiptPrinter.RegistrationData receiptData = new ReceiptPrinter.RegistrationData();
                            receiptData.receiptNo = String.valueOf(regId);
                            java.sql.Timestamp ts = rs.getTimestamp("reg_date");
                            receiptData.date = new SimpleDateFormat("dd.MM.yyyy").format(ts);
                            receiptData.time = new SimpleDateFormat("hh:mm a").format(ts);
                            receiptData.patientId = "IPSM/" + patientId;
                            receiptData.patientName = pRs.getString("patient_name");
                            receiptData.age = pRs.getInt("age") + " " + pRs.getString("age_unit");
                            receiptData.sex = pRs.getString("gender");
                            receiptData.refBy = "Self";
                            receiptData.items = new java.util.ArrayList<>();

                            String tSql = "SELECT t.test_name, t.mrp FROM registration_tests rt " +
                                    "JOIN tests t ON rt.test_code = t.test_code " +
                                    "WHERE rt.reg_id = ?";
                            try (PreparedStatement tPstmt = conn.prepareStatement(tSql)) {
                                tPstmt.setLong(1, regId);
                                ResultSet tRs = tPstmt.executeQuery();
                                while (tRs.next()) {
                                    receiptData.items
                                            .add(new String[] { tRs.getString("test_name"), tRs.getString("mrp") });
                                }
                            }
                            receiptData.total = rs.getDouble("total_amount");
                            receiptData.netAmount = rs.getDouble("paid_amount") + rs.getDouble("balance_amount");
                            receiptData.discount = receiptData.total - receiptData.netAmount;
                            receiptData.paymentMode = rs.getString("payment_mode");
                            new ReceiptPreviewDialog((Frame) SwingUtilities.getWindowAncestor(this), receiptData)
                                    .setVisible(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCurrentDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        txtToDate.setText(sdf.format(cal.getTime()));
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        txtFromDate.setText(sdf.format(cal.getTime()));
    }

    private void loadPatients() {
        tableModel.setRowCount(0);
        String search = txtSearch.getText().trim();
        String fromDate = txtFromDate.getText().trim();
        String toDate = txtToDate.getText().trim();

        // Base Query
        String baseSql;
        boolean isDoctor = "DOCTOR".equalsIgnoreCase(session.getRole());

        if (isTestView()) {
            // Doctor View: Granular by Test
            baseSql = "SELECT r.reg_id, r.reg_date, p.patient_name, p.gender, p.age, p.age_unit, t.test_name, t.test_code, rt.status "
                    +
                    "FROM registration_tests rt " +
                    "JOIN registrations r ON rt.reg_id = r.reg_id " +
                    "JOIN patients p ON r.patient_id = p.patient_id " +
                    "JOIN tests t ON rt.test_code = t.test_code " +
                    "WHERE (p.patient_name LIKE ? OR p.mobile LIKE ?) ";
        } else {
            // Admin/Receptionist View: By Registration (Previous Style)
            // Modified to include aggregated status
            baseSql = "SELECT r.reg_id, p.patient_id, p.title, p.patient_name, p.gender, p.age, p.age_unit, p.mobile, p.email, r.reg_date, r.total_amount, "
                    + "(SELECT COUNT(*) FROM registration_tests rt WHERE rt.reg_id = r.reg_id) as total_tests, "
                    + "(SELECT COUNT(*) FROM registration_tests rt WHERE rt.reg_id = r.reg_id AND rt.status = 'Done') as done_tests "
                    + "FROM patients p " +
                    "LEFT JOIN registrations r ON p.patient_id = r.patient_id " +
                    "WHERE (p.patient_name LIKE ? OR p.patient_id LIKE ? OR p.mobile LIKE ?) ";
        }

        StringBuilder sql = new StringBuilder(baseSql);

        boolean hasDateFilter = !fromDate.isEmpty() && !toDate.isEmpty();
        if (hasDateFilter) {
            sql.append("AND r.reg_date >= ? AND r.reg_date <= ? ");
        }

        if (isDoctor) {
            String dept = session.getDepartment();
            if (dept != null && !dept.isEmpty() && !"None".equalsIgnoreCase(dept)) {
                sql.append("AND t.category = ? ");
            }
        }

        sql.append("ORDER BY r.reg_date DESC");

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            String queryPattern = "%" + search + "%";
            pstmt.setString(paramIndex++, queryPattern);
            pstmt.setString(paramIndex++, queryPattern);
            if (!isTestView()) {
                // Admin query has 3 placeholders
                pstmt.setString(paramIndex++, queryPattern);
            }

            if (hasDateFilter) {
                SimpleDateFormat sdfDisp = new SimpleDateFormat("dd-MM-yyyy");
                java.util.Date dFrom = sdfDisp.parse(fromDate);
                java.util.Date dTo = sdfDisp.parse(toDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dTo);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                dTo = cal.getTime();

                pstmt.setTimestamp(paramIndex++, new java.sql.Timestamp(dFrom.getTime()));
                pstmt.setTimestamp(paramIndex++, new java.sql.Timestamp(dTo.getTime()));
            }

            if (isDoctor) {
                String dept = session.getDepartment();
                if (dept != null && !dept.isEmpty() && !"None".equalsIgnoreCase(dept)) {
                    pstmt.setString(paramIndex++, dept);
                }
            }

            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

            int count = 0;
            while (rs.next()) {
                count++;
                if (isTestView()) {
                    // Doctor Row
                    String status = rs.getString("status");
                    if (status == null || status.isEmpty())
                        status = "Pending";
                    tableModel.addRow(new Object[] {
                            rs.getLong("reg_id"),
                            rs.getString("test_code"),
                            sdf.format(rs.getTimestamp("reg_date")),
                            rs.getString("patient_name"),
                            rs.getString("gender"),
                            rs.getInt("age") + " " + rs.getString("age_unit"),
                            rs.getString("test_name"),
                            status
                    });
                } else {
                    // Admin Row
                    // Determine aggregate status
                    long totalTests = rs.getLong("total_tests");
                    long doneTests = rs.getLong("done_tests");
                    String aggStatus = (totalTests > 0 && totalTests == doneTests) ? "Done" : "Pending";

                    Object[] row = {
                            rs.getObject("reg_id") != null ? rs.getLong("reg_id") : 0L,
                            rs.getInt("patient_id"),
                            rs.getString("title") + " " + rs.getString("patient_name"),
                            rs.getString("gender"),
                            rs.getInt("age") + " " + rs.getString("age_unit"),
                            rs.getString("mobile"),
                            rs.getString("email"),
                            rs.getTimestamp("reg_date") != null ? sdf.format(rs.getTimestamp("reg_date")) : "-",
                            rs.getDouble("total_amount"),
                            aggStatus,
                            "Bill Receipt"
                    };
                    tableModel.addRow(row);
                }
            }
            lblCount.setText("Total: " + count);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }
}
