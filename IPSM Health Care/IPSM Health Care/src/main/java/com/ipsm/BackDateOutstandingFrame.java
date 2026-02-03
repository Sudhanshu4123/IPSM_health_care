package com.ipsm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;

public class BackDateOutstandingFrame extends JFrame {

    public BackDateOutstandingFrame() {
        setTitle("Back Date Outstanding");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 250, 255));

        // Header Title
        JLabel headerTitle = new JLabel("Back Date Outstanding", SwingConstants.CENTER);
        headerTitle.setOpaque(true);
        headerTitle.setBackground(new Color(210, 230, 250));
        headerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        headerTitle.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        add(headerTitle, BorderLayout.NORTH);

        // Main Scrollable Content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 250, 255));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 1. Select Date Section
        JPanel dateSection = new JPanel(new BorderLayout());
        dateSection.setBackground(new Color(230, 240, 255));
        dateSection.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(180, 200, 220)));

        JLabel lblSelectDate = new JLabel(" Select Date");
        lblSelectDate.setForeground(new Color(150, 0, 0)); // Red
        lblSelectDate.setFont(new Font("Arial", Font.BOLD, 12));
        lblSelectDate.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 200, 220)));
        dateSection.add(lblSelectDate, BorderLayout.NORTH);

        JPanel dateInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        dateInputPanel.setBackground(new Color(230, 240, 255));
        dateInputPanel.add(new JLabel("Date as on  : "));

        String[] days = new String[31];
        for (int i = 0; i < 31; i++)
            days[i] = String.valueOf(i + 1);
        JComboBox<String> cbDay = new JComboBox<>(days);

        String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        JComboBox<String> cbMonth = new JComboBox<>(months);

        String[] years = new String[10];
        int currentYearInt = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++)
            years[i] = String.valueOf(currentYearInt - i);
        JComboBox<String> cbYear = new JComboBox<>(years);

        // Auto select current date
        Calendar now = Calendar.getInstance();
        cbDay.setSelectedItem(String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
        cbMonth.setSelectedIndex(now.get(Calendar.MONTH));
        cbYear.setSelectedItem(String.valueOf(now.get(Calendar.YEAR)));

        dateInputPanel.add(cbDay);
        dateInputPanel.add(cbMonth);
        dateInputPanel.add(cbYear);
        dateSection.add(dateInputPanel, BorderLayout.CENTER);
        mainPanel.add(dateSection);

        // 2. Client Section
        JPanel clientSection = new JPanel(new BorderLayout());
        clientSection.setBackground(new Color(230, 240, 255));
        clientSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));

        JPanel clientHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientHeader.setOpaque(false);
        JCheckBox chkClient = new JCheckBox("Client :");
        chkClient.setForeground(new Color(150, 0, 0)); // Red
        chkClient.setFont(new Font("Arial", Font.BOLD, 12));
        chkClient.setOpaque(false);
        clientHeader.add(chkClient);
        clientSection.add(clientHeader, BorderLayout.NORTH);

        JPanel clientListPanel = new JPanel();
        clientListPanel.setLayout(new BoxLayout(clientListPanel, BoxLayout.Y_AXIS));
        clientListPanel.setBackground(new Color(245, 250, 255));
        JCheckBox clientItem = new JCheckBox("LDPL1439 = SOLICITOUS WELLNESS PVT LTD");
        clientItem.setOpaque(false);
        clientListPanel.add(clientItem);

        JScrollPane scrollClient = new JScrollPane(clientListPanel);
        scrollClient.setPreferredSize(new Dimension(0, 200));
        scrollClient.setBorder(BorderFactory.createEmptyBorder());
        clientSection.add(scrollClient, BorderLayout.CENTER);
        mainPanel.add(clientSection);

        // 3. Show Sample Collected Only
        JPanel samplePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        samplePanel.setBackground(new Color(230, 240, 255));
        samplePanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(180, 200, 220)));
        JCheckBox chkSample = new JCheckBox("Show Sample Collected Only", true);
        chkSample.setForeground(new Color(0, 0, 150));
        chkSample.setFont(new Font("Arial", Font.BOLD, 11));
        chkSample.setOpaque(false);
        samplePanel.add(chkSample);
        mainPanel.add(samplePanel);

        // 4. Designation Section
        JPanel desigSection = new JPanel(new BorderLayout());
        desigSection.setBackground(new Color(230, 240, 255));
        desigSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));

        JPanel desigHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        desigHeader.setOpaque(false);
        JCheckBox chkDesig = new JCheckBox("Designation");
        chkDesig.setForeground(new Color(0, 0, 150));
        chkDesig.setFont(new Font("Arial", Font.BOLD, 11));
        chkDesig.setOpaque(false);
        desigHeader.add(chkDesig);
        desigSection.add(desigHeader, BorderLayout.NORTH);

        JPanel desigGrid = new JPanel(new GridLayout(0, 6, 10, 5));
        desigGrid.setOpaque(false);
        desigGrid.setBorder(new EmptyBorder(5, 20, 10, 10));
        String[] designations = {
                "Sales Head", "Zonal Sales Manager(ZSM)", "SR. Regional Sales Manager(RSM)",
                "Regional Sales Manager(RSM)", "SR. Area Sales Manager(ASM)", "Area Sales Manager(ASM)",
                "Sr. Territory Manager(TM)", "Territory Manager(TM)", "Sr. Marketing Executive(ME/BDE)",
                "Marketing Executive(ME/BDE)", "Field Executive (FE)", "Dummy22122023",
                "GERNAL MANAGER - SALES", "Corporate Head", "FOLLOWUP ID"
        };
        for (String d : designations) {
            JCheckBox cb = new JCheckBox(d);
            cb.setFont(new Font("Arial", Font.PLAIN, 11));
            cb.setOpaque(false);
            desigGrid.add(cb);
        }
        desigSection.add(desigGrid, BorderLayout.CENTER);
        mainPanel.add(desigSection);

        // 5. SalesHead Section
        JPanel salesHeadSection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        salesHeadSection.setBackground(new Color(230, 240, 255));
        salesHeadSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));
        JCheckBox chkSalesHead = new JCheckBox("SalesHead");
        chkSalesHead.setForeground(new Color(0, 0, 150));
        chkSalesHead.setFont(new Font("Arial", Font.BOLD, 11));
        chkSalesHead.setOpaque(false);
        salesHeadSection.add(chkSalesHead);
        mainPanel.add(salesHeadSection);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footer.setBackground(new Color(245, 250, 255));

        JButton btnExcel = new JButton("Excel Report");
        btnExcel.setBackground(new Color(0, 0, 200));
        btnExcel.setForeground(Color.WHITE);
        btnExcel.setFocusPainted(false);
        btnExcel.setPreferredSize(new Dimension(120, 30));
        btnExcel.setFont(new Font("Arial", Font.BOLD, 12));

        JButton btnPdf = new JButton("Pdf Report");
        btnPdf.setBackground(new Color(0, 0, 200));
        btnPdf.setForeground(Color.WHITE);
        btnPdf.setFocusPainted(false);
        btnPdf.setPreferredSize(new Dimension(120, 30));
        btnPdf.setFont(new Font("Arial", Font.BOLD, 12));

        footer.add(btnExcel);
        footer.add(btnPdf);
        add(footer, BorderLayout.SOUTH);
    }
}
