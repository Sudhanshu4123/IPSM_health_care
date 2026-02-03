package com.ipsm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesReportFrame extends JFrame {

    public SalesReportFrame() {
        setTitle("Patient Information");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 250, 255));

        // Header Title
        JLabel headerTitle = new JLabel("Patient Information", SwingConstants.CENTER);
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

        // 1. Top Section (From/To Date, OrderBy)
        JPanel topSection = new JPanel(new GridBagLayout());
        topSection.setBackground(new Color(230, 240, 255));
        topSection.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(180, 200, 220)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String today = sdf.format(new Date());

        // Row 0: From Date & To Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        topSection.add(new JLabel("From Date :"), gbc);
        gbc.gridx = 1;
        JPanel fromP = createDateField(today);
        topSection.add(fromP, gbc);

        gbc.gridx = 2;
        topSection.add(new JLabel("To Date :"), gbc);
        gbc.gridx = 3;
        JPanel toP = createDateField(today);
        topSection.add(toP, gbc);

        // Row 1: OrderBy
        gbc.gridx = 0;
        gbc.gridy = 1;
        topSection.add(new JLabel("OrderBy :"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cbOrder = new JComboBox<>(new String[] { "Lab No", "Patient Name", "Registration Date" });
        cbOrder.setPreferredSize(new Dimension(150, 22));
        topSection.add(cbOrder, gbc);

        mainPanel.add(topSection);

        // 2. Report Type Section
        JPanel typeSection = new JPanel(new BorderLayout());
        typeSection.setBackground(new Color(230, 240, 255));
        typeSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));

        JLabel lblTypeHeader = new JLabel(" Report Type");
        lblTypeHeader.setForeground(new Color(150, 0, 0));
        lblTypeHeader.setFont(new Font("Arial", Font.BOLD, 12));
        typeSection.add(lblTypeHeader, BorderLayout.NORTH);

        JPanel typeOptionPanel = new JPanel(new GridBagLayout());
        typeOptionPanel.setOpaque(false);
        GridBagConstraints gbcT = new GridBagConstraints();
        gbcT.insets = new Insets(2, 5, 2, 5);
        gbcT.anchor = GridBagConstraints.WEST;

        String[] radioOptions = {
                "Patient List With Dept", "Patient List With Test", "Patient List Without Test",
                "Center Summary Report", "Excel Report", "Test Code",
                "Single Line Centre Summary Report"
        };
        ButtonGroup typeGroup = new ButtonGroup();
        for (int i = 0; i < radioOptions.length; i++) {
            JRadioButton rb = new JRadioButton(radioOptions[i]);
            rb.setOpaque(false);
            rb.setFont(new Font("Arial", Font.PLAIN, 11));
            if (i == 6)
                rb.setSelected(true); // Single Line Centre Summary Report
            typeGroup.add(rb);
            gbcT.gridx = (i % 3);
            gbcT.gridy = (i / 3);
            typeOptionPanel.add(rb, gbcT);
        }

        // Checkboxes in a row
        JPanel checkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        checkRow.setOpaque(false);
        JCheckBox chkDue = new JCheckBox("Only Due Patient");
        JCheckBox chkDisc = new JCheckBox("Only Discount Patient");
        JCheckBox chkSample = new JCheckBox("Sample Lab Receive Only", true);
        chkDue.setOpaque(false);
        chkDisc.setOpaque(false);
        chkSample.setOpaque(false);
        chkDue.setFont(new Font("Arial", Font.PLAIN, 11));
        chkDisc.setFont(new Font("Arial", Font.PLAIN, 11));
        chkSample.setFont(new Font("Arial", Font.PLAIN, 11));
        checkRow.add(chkDue);
        checkRow.add(chkDisc);
        checkRow.add(chkSample);

        gbcT.gridx = 0;
        gbcT.gridy = 3;
        gbcT.gridwidth = 3;
        typeOptionPanel.add(checkRow, gbcT);

        typeSection.add(typeOptionPanel, BorderLayout.CENTER);
        mainPanel.add(typeSection);

        // 3. Panel Section
        JPanel panelSection = new JPanel(new BorderLayout());
        panelSection.setBackground(new Color(230, 240, 255));
        panelSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));

        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHeader.setOpaque(false);
        JCheckBox chkPanelHeader = new JCheckBox("Panel");
        chkPanelHeader.setForeground(new Color(0, 0, 150));
        chkPanelHeader.setFont(new Font("Arial", Font.BOLD, 12));
        chkPanelHeader.setOpaque(false);
        panelHeader.add(chkPanelHeader);
        panelSection.add(panelHeader, BorderLayout.NORTH);

        JPanel panelListPanel = new JPanel();
        panelListPanel.setLayout(new BoxLayout(panelListPanel, BoxLayout.Y_AXIS));
        panelListPanel.setBackground(new Color(245, 250, 255));
        JCheckBox panelItem = new JCheckBox("LDPL1439 = SOLICITOUS WELLNESS PVT LTD");
        panelItem.setOpaque(false);
        panelListPanel.add(panelItem);

        JScrollPane scrollPanel = new JScrollPane(panelListPanel);
        scrollPanel.setPreferredSize(new Dimension(0, 300));
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        panelSection.add(scrollPanel, BorderLayout.CENTER);
        mainPanel.add(panelSection);

        // 4. Report Format Section
        JPanel formatSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        formatSection.setBackground(new Color(230, 240, 255));
        formatSection.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(180, 200, 220)));

        JLabel lblFormat = new JLabel("Report Format : ");
        lblFormat.setForeground(new Color(150, 0, 0));
        lblFormat.setFont(new Font("Arial", Font.BOLD, 12));
        formatSection.add(lblFormat);

        JRadioButton rbPdf = new JRadioButton("PDF", true);
        JRadioButton rbExcel = new JRadioButton("Excel");
        ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(rbPdf);
        formatGroup.add(rbExcel);
        rbPdf.setOpaque(false);
        rbExcel.setOpaque(false);
        formatSection.add(rbPdf);
        formatSection.add(rbExcel);

        mainPanel.add(formatSection);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Footer Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        footer.setBackground(new Color(245, 250, 255));

        JButton btnReport = new JButton("Report");
        btnReport.setBackground(new Color(240, 240, 240));
        btnReport.setForeground(Color.BLACK);
        btnReport.setFocusPainted(false);
        btnReport.setPreferredSize(new Dimension(100, 25));
        btnReport.setFont(new Font("Arial", Font.PLAIN, 12));
        btnReport.setBorder(new javax.swing.border.LineBorder(Color.GRAY));

        footer.add(btnReport);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createDateField(String value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        JTextField txt = new JTextField(value, 12);
        JButton btn = new JButton("\uD83D\uDCC5"); // Calendar icon
        btn.setPreferredSize(new Dimension(22, 22));
        p.add(txt);
        p.add(btn);
        return p;
    }
}
