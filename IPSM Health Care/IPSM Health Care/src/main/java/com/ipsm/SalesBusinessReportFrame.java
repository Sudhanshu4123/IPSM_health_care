package com.ipsm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;

public class SalesBusinessReportFrame extends JFrame {

    public SalesBusinessReportFrame() {
        setTitle("Sales Report FOR LIS");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 250, 255));

        // Header Title
        JLabel headerTitle = new JLabel("Sales Report FOR LIS", SwingConstants.CENTER);
        headerTitle.setOpaque(true);
        headerTitle.setBackground(new Color(210, 230, 250));
        headerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        headerTitle.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        add(headerTitle, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 250, 255));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 1. Top Options Section
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(new Color(230, 240, 255));
        optionsPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(180, 200, 220)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: From Date & To Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        optionsPanel.add(new JLabel("From Date :"), gbc);

        gbc.gridx = 1;
        optionsPanel.add(createDatePicker(), gbc);

        gbc.gridx = 2;
        optionsPanel.add(new JLabel("To Date :"), gbc);

        gbc.gridx = 3;
        optionsPanel.add(createDatePicker(), gbc);

        // Row 1: Report Type & Sample Checkbox
        gbc.gridx = 0;
        gbc.gridy = 1;
        optionsPanel.add(new JLabel("Report Type :"), gbc);

        gbc.gridx = 1;
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typePanel.setOpaque(false);
        JRadioButton rbSummary = new JRadioButton("Summary", true);
        JRadioButton rbDateWise = new JRadioButton("Date Wise");
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(rbSummary);
        typeGroup.add(rbDateWise);
        rbSummary.setOpaque(false);
        rbDateWise.setOpaque(false);
        typePanel.add(rbSummary);
        typePanel.add(rbDateWise);
        optionsPanel.add(typePanel, gbc);

        gbc.gridx = 3;
        JCheckBox chkSample = new JCheckBox("Sample Lab Receive Only", true);
        chkSample.setOpaque(false);
        chkSample.setFont(new Font("Arial", Font.PLAIN, 11));
        optionsPanel.add(chkSample, gbc);

        // Row 2: Report Formate
        gbc.gridx = 0;
        gbc.gridy = 2;
        optionsPanel.add(new JLabel("Report Formate :"), gbc);

        gbc.gridx = 1;
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        formatPanel.setOpaque(false);
        JRadioButton rbPdf = new JRadioButton("PDF", true);
        JRadioButton rbExcel = new JRadioButton("Excel");
        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(rbPdf);
        formatGroup.add(rbExcel);
        rbPdf.setOpaque(false);
        rbExcel.setOpaque(false);
        formatPanel.add(rbPdf);
        formatPanel.add(rbExcel);
        optionsPanel.add(formatPanel, gbc);

        mainPanel.add(optionsPanel);

        // 2. Select Centre Section
        JPanel centerSection = new JPanel(new BorderLayout());
        centerSection.setBackground(new Color(230, 240, 255));
        centerSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));

        JPanel centerHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerHeader.setOpaque(false);
        JCheckBox chkSelectCenter = new JCheckBox("Select Centre :");
        chkSelectCenter.setForeground(new Color(150, 0, 0));
        chkSelectCenter.setFont(new Font("Arial", Font.BOLD, 12));
        chkSelectCenter.setOpaque(false);
        centerHeader.add(chkSelectCenter);
        centerSection.add(centerHeader, BorderLayout.NORTH);

        JPanel centerListPanel = new JPanel();
        centerListPanel.setLayout(new BoxLayout(centerListPanel, BoxLayout.Y_AXIS));
        centerListPanel.setBackground(new Color(245, 250, 255));
        JCheckBox centerItem = new JCheckBox("LDPL1439 = SOLICITOUS WELLNESS PVT LTD");
        centerItem.setOpaque(false);
        centerListPanel.add(centerItem);

        JScrollPane scrollCenter = new JScrollPane(centerListPanel);
        scrollCenter.setPreferredSize(new Dimension(0, 300));
        scrollCenter.setBorder(BorderFactory.createEmptyBorder());
        centerSection.add(scrollCenter, BorderLayout.CENTER);
        mainPanel.add(centerSection);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Footer Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footer.setBackground(new Color(245, 250, 255));

        JButton btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(240, 240, 240));
        btnSearch.setForeground(Color.BLACK);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 25));
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        btnSearch.setBorder(new javax.swing.border.LineBorder(Color.GRAY));

        footer.add(btnSearch);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createDatePicker() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        p.setOpaque(false);

        String[] days = new String[31];
        for (int i = 0; i < 31; i++)
            days[i] = String.valueOf(i + 1);
        JComboBox<String> cbDay = new JComboBox<>(days);

        String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        JComboBox<String> cbMonth = new JComboBox<>(months);

        String[] years = new String[10];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++)
            years[i] = String.valueOf(currentYear - i);
        JComboBox<String> cbYear = new JComboBox<>(years);

        Calendar now = Calendar.getInstance();
        cbDay.setSelectedItem(String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
        cbMonth.setSelectedIndex(now.get(Calendar.MONTH));
        cbYear.setSelectedItem(String.valueOf(now.get(Calendar.YEAR)));

        p.add(cbDay);
        p.add(cbMonth);
        p.add(cbYear);
        return p;
    }
}
