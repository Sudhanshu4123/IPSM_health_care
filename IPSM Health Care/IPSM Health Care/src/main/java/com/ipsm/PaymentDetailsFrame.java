package com.ipsm;

import com.ipsm.db.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class PaymentDetailsFrame extends JFrame {

    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalCollected, lblTotalPending;

    public PaymentDetailsFrame() {
        setTitle("Payment Details - IPSM Health Care");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Top Panel ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topPanel.setBackground(new Color(220, 240, 255));
        JLabel title = new JLabel("All Payment Transactions");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        // --- Table ---
        String[] columns = { "Patient Name", "Date", "Total", "Paid", "Balance", "Mode", "Payment Details" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(25);
        paymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        lblTotalCollected = new JLabel("Total Collected: Rs. 0.00");
        lblTotalCollected.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalCollected.setForeground(new Color(0, 102, 0));

        lblTotalPending = new JLabel("Total Pending: Rs. 0.00");
        lblTotalPending.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalPending.setForeground(Color.RED);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadPayments());

        bottomPanel.add(lblTotalCollected);
        bottomPanel.add(lblTotalPending);
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);

        loadPayments();
    }

    private void loadPayments() {
        tableModel.setRowCount(0);
        double grandTotalCollected = 0;
        double grandTotalPending = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT p.patient_name, r.reg_date, r.total_amount, r.paid_amount, r.balance_amount, r.payment_mode, r.remarks "
                    + "FROM registrations r "
                    + "JOIN patients p ON r.patient_id = p.patient_id "
                    + "ORDER BY r.reg_date DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    double paid = rs.getDouble("paid_amount");
                    double balance = rs.getDouble("balance_amount");
                    grandTotalCollected += paid;
                    grandTotalPending += balance;

                    String remarks = rs.getString("remarks");
                    if (remarks != null && remarks.startsWith(" | ")) {
                        remarks = remarks.substring(3);
                    } else if (remarks != null && remarks.trim().equals("|")) {
                        remarks = "";
                    }

                    tableModel.addRow(new Object[] {
                            rs.getString("patient_name"),
                            sdf.format(rs.getTimestamp("reg_date")),
                            String.format("%.2f", rs.getDouble("total_amount")),
                            String.format("%.2f", paid),
                            String.format("%.2f", balance),
                            rs.getString("payment_mode"),
                            remarks
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage());
        }

        lblTotalCollected.setText("Total Collected: Rs. " + String.format("%.2f", grandTotalCollected));
        lblTotalPending.setText("Total Pending: Rs. " + String.format("%.2f", grandTotalPending));
    }
}
