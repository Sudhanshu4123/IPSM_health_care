package com.ipsm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;

public class InvoiceMonthWiseReportFrame extends JFrame {

    public InvoiceMonthWiseReportFrame() {
        setTitle("Month Wise Invoice Summary Report");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 250, 255));

        // Header Title
        JLabel headerTitle = new JLabel("Month Wise Invoice Summary Report", SwingConstants.CENTER);
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

        // 1. Date Selection Section (From/To Month & Year)
        JPanel dateSection = new JPanel(new GridLayout(1, 2));
        dateSection.setBackground(new Color(230, 240, 255));
        dateSection.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(180, 200, 220)));

        String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        String[] years = new String[10];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++)
            years[i] = String.valueOf(currentYear - i);

        // From Date
        JPanel fromDatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fromDatePanel.setOpaque(false);
        fromDatePanel.add(new JLabel("From Date : "));
        JComboBox<String> cbFromMonth = new JComboBox<>(months);
        JComboBox<String> cbFromYear = new JComboBox<>(years);
        cbFromMonth.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        fromDatePanel.add(cbFromMonth);
        fromDatePanel.add(cbFromYear);
        dateSection.add(fromDatePanel);

        // To Date
        JPanel toDatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        toDatePanel.setOpaque(false);
        toDatePanel.add(new JLabel("To Date : "));
        JComboBox<String> cbToMonth = new JComboBox<>(months);
        JComboBox<String> cbToYear = new JComboBox<>(years);
        cbToMonth.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        toDatePanel.add(cbToMonth);
        toDatePanel.add(cbToYear);
        dateSection.add(toDatePanel);

        mainPanel.add(dateSection);

        // 2. Report Type Section
        JPanel typeSection = new JPanel(new BorderLayout());
        typeSection.setBackground(new Color(230, 240, 255));
        typeSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));

        JLabel lblType = new JLabel(" Report Type : ");
        lblType.setForeground(new Color(150, 0, 0));
        lblType.setFont(new Font("Arial", Font.BOLD, 12));
        typeSection.add(lblType, BorderLayout.NORTH);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        radioPanel.setOpaque(false);
        JRadioButton rbPdf = new JRadioButton("PDF", true);
        JRadioButton rbExport = new JRadioButton("Export");
        JRadioButton rbExcel = new JRadioButton("Excel");
        ButtonGroup group = new ButtonGroup();
        group.add(rbPdf);
        group.add(rbExport);
        group.add(rbExcel);
        rbPdf.setOpaque(false);
        rbExport.setOpaque(false);
        rbExcel.setOpaque(false);
        radioPanel.add(rbPdf);
        radioPanel.add(rbExport);
        radioPanel.add(rbExcel);
        typeSection.add(radioPanel, BorderLayout.CENTER);
        mainPanel.add(typeSection);

        // 3. Panel Section
        JPanel panelSection = new JPanel(new BorderLayout());
        panelSection.setBackground(new Color(230, 240, 255));
        panelSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));

        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHeader.setOpaque(false);
        JCheckBox chkPanel = new JCheckBox("Panel :");
        chkPanel.setForeground(new Color(150, 0, 0));
        chkPanel.setFont(new Font("Arial", Font.BOLD, 12));
        chkPanel.setOpaque(false);
        panelHeader.add(chkPanel);
        panelSection.add(panelHeader, BorderLayout.NORTH);

        JPanel panelListPanel = new JPanel();
        panelListPanel.setLayout(new BoxLayout(panelListPanel, BoxLayout.Y_AXIS));
        panelListPanel.setBackground(new Color(245, 250, 255));
        JCheckBox panelItem = new JCheckBox("LDPL1439 = SOLICITOUS WELLNESS PVT LTD");
        panelItem.setOpaque(false);
        panelListPanel.add(panelItem);

        JScrollPane scrollPanel = new JScrollPane(panelListPanel);
        scrollPanel.setPreferredSize(new Dimension(0, 200));
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        panelSection.add(scrollPanel, BorderLayout.CENTER);
        mainPanel.add(panelSection);

        // 4. Designation Section
        JPanel desigSection = new JPanel(new BorderLayout());
        desigSection.setBackground(new Color(230, 240, 255));
        desigSection.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(180, 200, 220)));

        JPanel desigHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        desigHeader.setOpaque(false);
        JCheckBox chkDesig = new JCheckBox("Designation:");
        chkDesig.setForeground(new Color(150, 0, 0));
        chkDesig.setFont(new Font("Arial", Font.BOLD, 12));
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
        JCheckBox chkSalesHead = new JCheckBox("SalesHead:");
        chkSalesHead.setForeground(new Color(150, 0, 0));
        chkSalesHead.setFont(new Font("Arial", Font.BOLD, 11));
        chkSalesHead.setOpaque(false);
        salesHeadSection.add(chkSalesHead);
        mainPanel.add(salesHeadSection);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Footer Buttons
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
}
