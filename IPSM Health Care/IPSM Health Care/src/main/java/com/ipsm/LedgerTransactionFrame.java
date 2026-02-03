package com.ipsm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LedgerTransactionFrame extends JFrame {

    public LedgerTransactionFrame() {
        setTitle("Ledger Transaction");
        setSize(800, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("LEDGER TRANSACTION", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(210, 235, 255));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(header, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 240, 255));
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Search Option Title
        JLabel lblSearchOption = new JLabel(" Search Option");
        lblSearchOption.setForeground(new Color(150, 0, 0)); // Red
        lblSearchOption.setFont(new Font("Arial", Font.BOLD, 12));
        lblSearchOption.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        lblSearchOption.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblSearchOption);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Panel & Checkbox
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Panel :"), gbc);

        gbc.gridx = 1;
        JComboBox<String> cbPanel = new JComboBox<>(new String[] { "--Select--", "SOLICITOUS WELLNESS PVT LTD" });
        cbPanel.setPreferredSize(new Dimension(250, 25));
        formPanel.add(cbPanel, gbc);

        gbc.gridx = 2;
        JCheckBox chkSample = new JCheckBox("Sample Lab Receive Only", true);
        chkSample.setOpaque(false);
        formPanel.add(chkSample, gbc);

        // Row 2: From Date & To Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstOfMonth = cal.getTime();

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("From Date :"), gbc);

        gbc.gridx = 1;
        JPanel fromDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fromDatePanel.setOpaque(false);
        JTextField txtFromDate = new JTextField(sdf.format(firstOfMonth), 10);
        JButton btnCalFrom = new JButton("\uD83D\uDCC5"); // Calendar icon
        btnCalFrom.setPreferredSize(new Dimension(25, 25));
        fromDatePanel.add(txtFromDate);
        fromDatePanel.add(btnCalFrom);
        formPanel.add(fromDatePanel, gbc);

        gbc.gridx = 2;
        JPanel toDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toDatePanel.setOpaque(false);
        toDatePanel.add(new JLabel("To Date : "));
        JTextField txtToDate = new JTextField(sdf.format(today), 10);
        JButton btnCalTo = new JButton("\uD83D\uDCC5"); // Calendar icon
        btnCalTo.setPreferredSize(new Dimension(25, 25));
        toDatePanel.add(txtToDate);
        toDatePanel.add(btnCalTo);
        formPanel.add(toDatePanel, gbc);

        // Row 3: Search Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSearch = new JButton("Search");
        btnSearch.setPreferredSize(new Dimension(80, 25));
        formPanel.add(btnSearch, gbc);

        mainPanel.add(formPanel);
        add(mainPanel, BorderLayout.CENTER);
    }
}
