package com.ipsm;

import javax.swing.*;
import java.awt.*;
import com.ipsm.db.DatabaseManager;

public class SearchFrame extends JFrame {
    private JTextField searchField;
    private JTextArea resultArea;

    public SearchFrame() {
        setTitle("IPSM Health Care - Search Tests");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel NorthPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel titleLabel = new JLabel("Search Tests", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        NorthPanel.add(titleLabel);

        JPanel searchBar = new JPanel(new FlowLayout());
        searchField = new JTextField(30);
        JButton searchButton = new JButton("Search");
        searchBar.add(searchField);
        searchBar.add(searchButton);
        NorthPanel.add(searchBar);
        add(NorthPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        searchButton.addActionListener(e -> handleSearch());
    }

    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty())
            return;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s | %-50s | %-10s\n", "Code", "Test Name", "MRP"));
        sb.append("----------------------------------------------------------------------\n");

        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM tests WHERE test_name LIKE ? OR test_code LIKE ?";
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + query + "%");
                pstmt.setString(2, "%" + query + "%");
                try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        sb.append(String.format("%-10s | %-50s | %-10.2f\n",
                                rs.getString("test_code"),
                                rs.getString("test_name"),
                                rs.getDouble("mrp")));
                    }
                    if (!found) {
                        sb.append("No records found for '" + query + "'");
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            ErrorHandler.showError(this, "Error searching database", e);
            sb.append("Error searching database: " + e.getMessage());
        }
        resultArea.setText(sb.toString());
    }
}
