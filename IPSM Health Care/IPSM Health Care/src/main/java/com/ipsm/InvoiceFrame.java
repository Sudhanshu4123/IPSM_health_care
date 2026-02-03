package com.ipsm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.ipsm.db.DatabaseManager;

public class InvoiceFrame extends JFrame {
    private JTextField txtFromDate;
    private JTextField txtToDate;
    private JTextField txtPatientName;
    private JTextField txtPatientId;
    private DefaultTableModel resultTableModel;

    public InvoiceFrame() {
        setTitle("Invoice Reprint - IPSM Health Care");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- TOP TITLE BAR ---
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(220, 240, 255));
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel lblTitle = new JLabel("Invoice Reprint");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(lblTitle);
        add(titlePanel, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Selection Criteria Panel
        JPanel criteriaPanel = new JPanel(new GridBagLayout());
        criteriaPanel.setBackground(new Color(235, 245, 255));
        criteriaPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMM-yyyy");
        String today = sdf.format(new java.util.Date());

        // Row 1: From Date and To Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        criteriaPanel.add(new JLabel("From Date :"), gbc);
        gbc.gridx = 1;
        JPanel fromDateBox = createDateBox(today);
        txtFromDate = (JTextField) fromDateBox.getComponent(0);
        criteriaPanel.add(fromDateBox, gbc);

        gbc.gridx = 2;
        criteriaPanel.add(new JLabel("To Date :"), gbc);
        gbc.gridx = 3;
        JPanel toDateBox = createDateBox(today);
        txtToDate = (JTextField) toDateBox.getComponent(0);
        criteriaPanel.add(toDateBox, gbc);

        // Row 2: Invoice No and Panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        criteriaPanel.add(new JLabel("Patient Name :"), gbc);
        gbc.gridx = 1;
        txtPatientName = new JTextField(15);
        criteriaPanel.add(txtPatientName, gbc);

        gbc.gridx = 2;
        criteriaPanel.add(new JLabel("Patient ID :"), gbc);
        gbc.gridx = 3;
        txtPatientId = new JTextField(15);
        criteriaPanel.add(txtPatientId, gbc);

        // Search Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSearch = new JButton("Search");
        btnSearch.setPreferredSize(new Dimension(80, 25));
        btnSearch.addActionListener(e -> searchInvoices());
        criteriaPanel.add(btnSearch, gbc);

        mainPanel.add(criteriaPanel);

        // Search Result Header
        JPanel resultHeader = new JPanel(new BorderLayout());
        resultHeader.setBackground(Color.WHITE);
        // JLabel lblResult = new JLabel("Search Result");
        // lblResult.setForeground(new Color(150, 0, 0));
        // lblResult.setFont(new Font("Arial", Font.BOLD, 12));
        // resultHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
        // Color.GRAY));
        // resultHeader.add(lblResult, BorderLayout.WEST);

        JPanel tableSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableSearchPanel.setOpaque(false);
        tableSearchPanel.add(new JLabel("Search Patient: "));
        JTextField tableSearchField = new JTextField(15);
        tableSearchField.setPreferredSize(new Dimension(150, 22));
        tableSearchPanel.add(tableSearchField);
        resultHeader.add(tableSearchPanel, BorderLayout.EAST);

        mainPanel.add(resultHeader);

        // Table for Results
        String[] columns = { "S.No", "Date", "Patient ID", "Patient Name", "Amount", "Status", "Download" };
        resultTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(resultTableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        final javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(
                resultTableModel);
        table.setRowSorter(sorter);

        tableSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = tableSearchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 3)); // Filter Patient Name column (index
                                                                                  // 3)
                }
            }
        });

        mainPanel.add(new JScrollPane(table));

        add(mainPanel, BorderLayout.CENTER);
    }

    private void searchInvoices() {
        resultTableModel.setRowCount(0);
        String fromDate = txtFromDate.getText().trim();
        String toDate = txtToDate.getText().trim();
        String patientName = txtPatientName.getText().trim();
        String patientId = txtPatientId.getText().trim();

        java.text.SimpleDateFormat displayFmt = new java.text.SimpleDateFormat("dd-MMM-yyyy");
        java.text.SimpleDateFormat sqlFmt = new java.text.SimpleDateFormat("yyyy-MM-dd");

        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT r.reg_id, r.reg_date, p.patient_id, p.patient_name, r.total_amount, r.balance_amount " +
                            "FROM registrations r " +
                            "JOIN patients p ON r.patient_id = p.patient_id " +
                            "WHERE 1=1 ");

            if (!fromDate.isEmpty()) {
                sql.append(" AND DATE(r.reg_date) >= ?");
            }
            if (!toDate.isEmpty()) {
                sql.append(" AND DATE(r.reg_date) <= ?");
            }
            if (!patientName.isEmpty()) {
                sql.append(" AND p.patient_name LIKE ?");
            }
            if (!patientId.isEmpty()) {
                sql.append(" AND p.patient_id = ?");
            }

            sql.append(" ORDER BY r.reg_date DESC");

            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int paramIdx = 1;
                if (!fromDate.isEmpty()) {
                    java.util.Date d = displayFmt.parse(fromDate);
                    pstmt.setString(paramIdx++, sqlFmt.format(d));
                }
                if (!toDate.isEmpty()) {
                    java.util.Date d = displayFmt.parse(toDate);
                    pstmt.setString(paramIdx++, sqlFmt.format(d));
                }
                if (!patientName.isEmpty()) {
                    pstmt.setString(paramIdx++, "%" + patientName + "%");
                }
                if (!patientId.isEmpty()) {
                    pstmt.setString(paramIdx++, patientId);
                }

                try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                    int sno = 1;
                    while (rs.next()) {
                        String date = displayFmt.format(rs.getTimestamp("reg_date"));
                        String pId = rs.getString("patient_id");
                        String pName = rs.getString("patient_name");
                        double amount = rs.getDouble("total_amount");
                        double balance = rs.getDouble("balance_amount");
                        String status = (balance <= 0) ? "Paid" : "Pending (" + balance + ")";

                        resultTableModel.addRow(new Object[] {
                                sno++, date, pId, pName, amount, status, "Reprint"
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during search: " + e.getMessage());
        }
    }

    private JPanel createDateBox(String date) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        JTextField txt = new JTextField(date, 10);
        JButton btnIcon = new JButton("\uD83D\uDCC5"); // Calendar icon
        btnIcon.setPreferredSize(new Dimension(25, 20));
        p.add(txt);
        p.add(btnIcon);
        return p;
    }
}
