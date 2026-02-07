package com.ipsm;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;
import com.ipsm.db.DatabaseManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import javax.swing.text.*;

public class RegistrationFrame extends JFrame {

        private DefaultTableModel mainTableModel;
        private JTable mainTable;
        private DefaultTableModel searchTableModel;
        private JTable searchTable;
        private JTextField searchField;
        private java.util.List<TestData> allTests = new ArrayList<>();
        private JTextField txtTotal;
        private JLabel lblBalAmtValue;

        // Patient Info Fields
        private JComboBox<String> comboTitle;
        private JTextField txtPatientName;
        private JTextField txtRegNo;
        private JTextField txtAge;
        private JComboBox<String> comboAgeUnit;
        private JComboBox<String> comboGender;
        private JTextField txtMobile;
        private JComboBox<String> comboDay;
        private JComboBox<String> comboMonth;
        private JComboBox<String> comboYear;
        private JTextField txtEmail;
        private JTextField txtAddress;
        private JComboBox<String> comboSource;
        private JTextField txtReferDoctor;
        private JComboBox<String> comboMaritalStatus;
        private JTextField txtSearchDoctor;
        private String lastDoctorSpecialization = "";
        private JRadioButton rbPathology, rbDental, rbPhysiotherapy, rbXRay;

        // Payment Fields
        private JTextField txtRemarks;
        private JTextField txtDiscPercent;
        private JTextField txtDiscAmount;
        private DefaultTableModel paymentTableModel;
        private boolean isAdjustingDiscount = false;

        private static class TestData {
                String code;
                String name;
                String cutoff;
                String reportTime;
                double specialPrice;
                double mrp;
                String category;
                boolean selected; // Note: kept for logic but initialized in row data
                boolean isCustom;

                TestData(String code, String name, String cutoff, String reportTime, double specialPrice, double mrp) {
                        this.code = code;
                        this.name = name;
                        this.cutoff = cutoff;
                        this.reportTime = reportTime;
                        this.specialPrice = specialPrice;
                        this.mrp = mrp;
                        this.category = "Pathology";
                        this.selected = false;
                        this.isCustom = false;
                }

                @Override
                public String toString() {
                        return code + " ~ " + name;
                }
        }

        private void populateTestData() {
                // Load tests directly from Database in a background thread
                new Thread(() -> {
                        try {
                                java.util.List<Object[]> dbTests = DatabaseManager.getAllTests();
                                if (dbTests != null && !dbTests.isEmpty()) {
                                        SwingUtilities.invokeLater(() -> {
                                                allTests.clear();
                                                for (Object[] row : dbTests) {
                                                        try {
                                                                TestData td = new TestData(
                                                                                String.valueOf(row[0]),
                                                                                String.valueOf(row[1]),
                                                                                String.valueOf(row[2]),
                                                                                String.valueOf(row[3]),
                                                                                Double.parseDouble(
                                                                                                String.valueOf(row[4])),
                                                                                Double.parseDouble(String
                                                                                                .valueOf(row[5])));
                                                                td.category = String.valueOf(row[6]);
                                                                td.isCustom = Boolean
                                                                                .parseBoolean(String.valueOf(row[7]));
                                                                allTests.add(td);
                                                        } catch (Exception e) {
                                                        }
                                                }
                                                refreshSearchTable("Pathology");
                                                System.out.println("SUCCESS: Loaded " + allTests.size()
                                                                + " tests from Server.");
                                        });
                                }
                        } catch (Exception e) {
                                SwingUtilities.invokeLater(() -> {
                                        ErrorHandler.showError(RegistrationFrame.this, "Server Connection Issue", e);
                                });
                        }
                }).start();
        }

        private void saveTestToFile(TestData test) {
                // Now handled primarily by database, but keeping signature for compatibility
        }

        public RegistrationFrame() {
                setTitle("New Registration - IPSM Health Care");
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                setMinimumSize(new Dimension(1200, 800));
                setLayout(new BorderLayout());
                Main.setAppIcon(this);

                // --- TOP BLUE HEADER ---
                JPanel topHeader = new JPanel(new BorderLayout());
                topHeader.setBackground(new Color(220, 240, 255));
                topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));

                JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                leftTop.setOpaque(false);
                leftTop.add(new JLabel("Center: "));
                JComboBox<String> centerCombo = new JComboBox<>(
                                new String[] { "LDPL1439-SOLICITOUS WELLNESS PVT LTD" });
                centerCombo.setPreferredSize(new Dimension(250, 25));
                leftTop.add(centerCombo);

                JLabel lblTitle = new JLabel("Patient Registration", SwingConstants.CENTER);
                lblTitle.setFont(new Font("Arial", Font.BOLD, 16));

                JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
                rightTop.setOpaque(false);
                rightTop.add(new JLabel("Last Lab No: 0255925121800006"));
                JButton btnUpload = new JButton("Upload Document");
                rightTop.add(btnUpload);

                topHeader.add(leftTop, BorderLayout.WEST);
                topHeader.add(lblTitle, BorderLayout.CENTER);
                topHeader.add(rightTop, BorderLayout.EAST);

                add(topHeader, BorderLayout.NORTH);

                // --- MAIN SCROLLABLE CONTENT ---
                JPanel mainContent = new JPanel();
                mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
                mainContent.setBackground(Color.WHITE);

                mainContent.add(createPatientInfoPanel());
                mainContent.add(createInvestigationPanel());
                mainContent.add(createPaymentDetailPanel());

                add(new JScrollPane(mainContent), BorderLayout.CENTER);

                populateTestData();

                // --- BOTTOM ACTION BAR ---
                JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
                actionBar.setBackground(new Color(245, 245, 245));

                JButton btnSave = new JButton("Save Registration");
                btnSave.setBackground(new Color(0, 102, 204)); // Strong Blue
                btnSave.setForeground(Color.BLACK);
                btnSave.setFont(new Font("Arial", Font.BOLD, 14));
                btnSave.setPreferredSize(new Dimension(180, 40));
                btnSave.setFocusPainted(false);
                btnSave.addActionListener(e -> saveRegistrationToDB());

                actionBar.add(btnSave);
                add(actionBar, BorderLayout.SOUTH);
        }

        private void saveRegistrationToDB() {
                String title = (String) comboTitle.getSelectedItem();
                String name = txtPatientName.getText().trim();
                String ageStr = txtAge.getText().trim();
                String gender = (String) comboGender.getSelectedItem();
                String mobile = txtMobile.getText().trim();

                if (name.isEmpty() || mobile.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please fill required fields (Name and Mobile)");
                        return;
                }

                try (java.sql.Connection conn = DatabaseManager.getConnection()) {
                        conn.setAutoCommit(false);

                        // 1. Insert/Update Patient
                        String pSql = "INSERT INTO patients (patient_id, title, patient_name, gender, age, age_unit, mobile, email, address, marital_status) "
                                        +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                        "ON DUPLICATE KEY UPDATE title=?, patient_name=?, gender=?, age=?, age_unit=?, mobile=?, email=?, address=?, marital_status=?";
                        String regText = txtRegNo.getText();
                        long patientId = Long.parseLong(
                                        regText.contains("/") ? regText.substring(regText.lastIndexOf("/") + 1)
                                                        : regText);
                        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(pSql)) {
                                pstmt.setLong(1, patientId);
                                pstmt.setString(2, title);
                                pstmt.setString(3, name);
                                pstmt.setString(4, gender);
                                pstmt.setInt(5, ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr));
                                pstmt.setString(6, (String) comboAgeUnit.getSelectedItem());
                                pstmt.setString(7, mobile);
                                pstmt.setString(8, txtEmail.getText());
                                pstmt.setString(9, txtAddress.getText());
                                pstmt.setString(10, (String) comboMaritalStatus.getSelectedItem());

                                // For Update
                                pstmt.setString(11, title);
                                pstmt.setString(12, name);
                                pstmt.setString(13, gender);
                                pstmt.setInt(14, ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr));
                                pstmt.setString(15, (String) comboAgeUnit.getSelectedItem());
                                pstmt.setString(16, mobile);
                                pstmt.setString(17, txtEmail.getText());
                                pstmt.setString(18, txtAddress.getText());
                                pstmt.setString(19, (String) comboMaritalStatus.getSelectedItem());

                                pstmt.executeUpdate();
                        }

                        // 2. Insert Registration
                        double totalPaidValue = 0;
                        StringBuilder modes = new StringBuilder();
                        StringBuilder payDetails = new StringBuilder();
                        for (int i = 0; i < paymentTableModel.getRowCount(); i++) {
                                try {
                                        totalPaidValue += Double
                                                        .parseDouble(paymentTableModel.getValueAt(i, 2).toString());
                                        if (i > 0) {
                                                modes.append(",");
                                                payDetails.append("; ");
                                        }
                                        String mode = paymentTableModel.getValueAt(i, 1).toString();
                                        String detail = paymentTableModel.getValueAt(i, 3).toString();
                                        modes.append(mode);
                                        payDetails.append(mode).append(":").append(detail);
                                } catch (Exception ex) {
                                        System.err.println("Warning: Could not parse payment row " + i + ": "
                                                        + ex.getMessage());
                                }
                        }

                        String rSql = "INSERT INTO registrations (patient_id, doctor_id, total_amount, paid_amount, balance_amount, payment_mode, remarks) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        long regId = -1;
                        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(rSql,
                                        java.sql.Statement.RETURN_GENERATED_KEYS)) {
                                pstmt.setLong(1, patientId);
                                String docName = txtReferDoctor.getText().trim();
                                if (docName.isEmpty() && txtSearchDoctor != null) {
                                        docName = txtSearchDoctor.getText().trim();
                                }
                                Integer docId = DatabaseManager.getInternalDoctorIdByName(docName);
                                if (docId == null) {
                                        docId = DatabaseManager.getDoctorIdByName(docName);
                                }

                                if (docId != null) {
                                        pstmt.setInt(2, docId);
                                } else {
                                        pstmt.setNull(2, java.sql.Types.INTEGER);
                                }
                                pstmt.setDouble(3, Double
                                                .parseDouble(txtTotal.getText().isEmpty() ? "0" : txtTotal.getText()));
                                pstmt.setDouble(4, totalPaidValue);
                                pstmt.setDouble(5, Double
                                                .parseDouble(lblBalAmtValue.getText().replace("Rs. ", "").trim()));
                                String finalMode = modes.toString();
                                if (finalMode.length() > 50)
                                        finalMode = finalMode.substring(0, 47) + "...";
                                pstmt.setString(6, finalMode.isEmpty() ? "Cash" : finalMode);
                                String userRemarks = (txtRemarks != null ? txtRemarks.getText().trim() : "");
                                String payDet = payDetails.toString();
                                String finalRemarks = userRemarks;
                                if (!payDet.isEmpty()) {
                                        finalRemarks = userRemarks.isEmpty() ? payDet : userRemarks + " | " + payDet;
                                }
                                pstmt.setString(7, finalRemarks);
                                pstmt.executeUpdate();

                                java.sql.ResultSet rs = pstmt.getGeneratedKeys();
                                if (rs.next()) {
                                        regId = rs.getLong(1);
                                }
                        }

                        // 3. Insert Tests
                        String rtSql = "INSERT INTO registration_tests (reg_id, test_code) VALUES (?, ?)";
                        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(rtSql)) {
                                for (int i = 0; i < mainTableModel.getRowCount(); i++) {
                                        String fullTestStr = (String) mainTableModel.getValueAt(i, 1);
                                        String testCode = fullTestStr.contains(" ~ ") ? fullTestStr.split(" ~ ")[0]
                                                        : fullTestStr;
                                        pstmt.setLong(1, regId);
                                        pstmt.setString(2, testCode);
                                        pstmt.addBatch();
                                }
                                pstmt.executeBatch();
                        }

                        conn.commit();

                        // Collect data for receipt
                        ReceiptPrinter.RegistrationData receiptData = new ReceiptPrinter.RegistrationData();
                        receiptData.receiptNo = String.valueOf(regId);
                        receiptData.date = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date());
                        receiptData.time = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date());
                        receiptData.patientId = txtRegNo.getText();
                        receiptData.patientName = name;
                        receiptData.age = ageStr + " " + (String) comboAgeUnit.getSelectedItem();
                        receiptData.sex = (String) (comboGender != null ? comboGender.getSelectedItem() : "Male");
                        receiptData.refBy = txtReferDoctor.getText().isEmpty() ? "Self" : txtReferDoctor.getText();
                        receiptData.items = new java.util.ArrayList<>();
                        for (int i = 0; i < mainTableModel.getRowCount(); i++) {
                                String testFull = (String) mainTableModel.getValueAt(i, 1);
                                String testName = testFull.contains(" ~ ") ? testFull.split(" ~ ")[1] : testFull;
                                String charge = mainTableModel.getValueAt(i, 2).toString();
                                receiptData.items.add(new String[] { testName, charge });
                        }
                        receiptData.total = Double.parseDouble(txtTotal.getText().isEmpty() ? "0" : txtTotal.getText());
                        receiptData.discount = Double
                                        .parseDouble(txtDiscAmount.getText().isEmpty() ? "0" : txtDiscAmount.getText());
                        receiptData.netAmount = totalPaidValue;
                        receiptData.paymentMode = modes.toString().isEmpty() ? "Cash" : modes.toString();

                        JOptionPane.showMessageDialog(this, "Registration Saved Successfully!");

                        // Show Receipt Preview
                        try {
                                new ReceiptPreviewDialog(this, receiptData).setVisible(true);
                        } catch (Exception ex) {
                                ErrorHandler.showError(this, "Error showing receipt preview", ex);
                        }

                        dispose();

                } catch (Exception e) {
                        ErrorHandler.showError(this, "Error saving registration to database", e);
                }
        }

        private void showDoctorSelectionDialog() {
                showDoctorSelectionDialog(null, false, txtReferDoctor);
        }

        private void showDoctorSelectionDialog(String specialization, boolean useInternal, JTextField targetField) {
                java.util.List<String> tempDoctors;
                if (useInternal) {
                        tempDoctors = (specialization == null)
                                        ? DatabaseManager.getInternalDoctorsList()
                                        : DatabaseManager.getInternalDoctorsByDepartment(specialization);
                        if (tempDoctors.isEmpty() && specialization != null) {
                                tempDoctors = DatabaseManager.getInternalDoctorsList();
                        }
                } else {
                        tempDoctors = (specialization == null)
                                        ? DatabaseManager.getDoctorsList()
                                        : DatabaseManager.getDoctorsBySpecialization(specialization);
                        if (tempDoctors.isEmpty() && specialization != null) {
                                tempDoctors = DatabaseManager.getDoctorsList();
                        }
                }

                final java.util.List<String> doctorsList = tempDoctors;

                if (doctorsList.isEmpty()) {
                        ErrorHandler.showWarning(this, "No doctors found in database. Please add one.");
                        return;
                }

                JDialog dialog = new JDialog(this,
                                "Select Referring Doctor" + (specialization != null ? " (" + specialization + ")" : ""),
                                true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 500);
                dialog.setLocationRelativeTo(this);

                // Search Panel
                JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
                searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
                searchPanel.add(new JLabel("Search Doctor:"), BorderLayout.WEST);
                JTextField searchField = new JTextField();
                searchPanel.add(searchField, BorderLayout.CENTER);
                dialog.add(searchPanel, BorderLayout.NORTH);

                // List
                DefaultListModel<String> listModel = new DefaultListModel<>();
                for (String doc : doctorsList) {
                        listModel.addElement(doc);
                }
                JList<String> doctorJList = new JList<>(listModel);
                doctorJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane scrollPane = new JScrollPane(doctorJList);
                scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                dialog.add(scrollPane, BorderLayout.CENTER);

                // Buttons
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnSelect = new JButton("Select");
                JButton btnCancel = new JButton("Cancel");
                btnPanel.add(btnSelect);
                btnPanel.add(btnCancel);
                dialog.add(btnPanel, BorderLayout.SOUTH);

                // Search Logic
                searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                        public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                filter();
                        }

                        public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                filter();
                        }

                        public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                filter();
                        }

                        void filter() {
                                String text = searchField.getText().toLowerCase();
                                listModel.clear();
                                for (String doc : doctorsList) {
                                        if (doc.toLowerCase().contains(text)) {
                                                listModel.addElement(doc);
                                        }
                                }
                        }
                });

                // Selection Logic
                btnSelect.addActionListener(e -> {
                        String selected = doctorJList.getSelectedValue();
                        if (selected != null) {
                                if (targetField != null) {
                                        targetField.setText(selected);
                                }
                                // Update specialization context
                                String spec = specialization != null ? specialization : "";
                                if (spec.isEmpty()) {
                                        // Try to guess from radio buttons if spec was null (manual click)
                                        if (rbPathology.isSelected())
                                                spec = "Pathology";
                                        else if (rbDental.isSelected())
                                                spec = "Dental";
                                        else if (rbPhysiotherapy.isSelected())
                                                spec = "Physiotherapy";
                                        else
                                                spec = "Radiologist";
                                }
                                lastDoctorSpecialization = spec;
                                dialog.dispose();
                        } else {
                                ErrorHandler.showWarning(dialog, "Please select a doctor.");
                        }
                });

                btnCancel.addActionListener(e -> dialog.dispose());

                // Double click selection
                doctorJList.addMouseListener(new MouseAdapter() {

                        public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                        btnSelect.doClick();
                                }
                        }

                });

                dialog.setVisible(true);
        }

        private void showPatientSearchDialog() {
                JDialog dialog = new JDialog(this, "Search Existing Patient", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(700, 500);
                dialog.setLocationRelativeTo(this);

                // Main Panel for Inputs
                JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));
                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

                // Row 1: Name
                JPanel namePanel = new JPanel(new BorderLayout(5, 5));
                JTextField nameField = new JTextField();
                JButton btnNameSearch = new JButton("Search by Name");
                namePanel.add(new JLabel("Name:       "), BorderLayout.WEST);
                namePanel.add(nameField, BorderLayout.CENTER);
                namePanel.add(btnNameSearch, BorderLayout.EAST);
                inputPanel.add(namePanel);

                // Row 2: Mobile
                JPanel mobilePanel = new JPanel(new BorderLayout(5, 5));
                JTextField mobileField = new JTextField();
                JButton btnMobileSearch = new JButton("Search by Mobile");
                mobilePanel.add(new JLabel("Mobile:      "), BorderLayout.WEST);
                mobilePanel.add(mobileField, BorderLayout.CENTER);
                mobilePanel.add(btnMobileSearch, BorderLayout.EAST);
                inputPanel.add(mobilePanel);

                // Row 3: ID
                JPanel idPanel = new JPanel(new BorderLayout(5, 5));
                JTextField idField = new JTextField();
                JButton btnIdSearch = new JButton("Search by Registration No");
                idPanel.add(new JLabel("Reg No :"), BorderLayout.WEST);
                idPanel.add(idField, BorderLayout.CENTER);
                idPanel.add(btnIdSearch, BorderLayout.EAST);
                inputPanel.add(idPanel);

                dialog.add(inputPanel, BorderLayout.NORTH);

                // Result Table
                DefaultTableModel model = new DefaultTableModel(
                                new String[] { "Reg No", "Name", "Mobile", "Age", "Gender" }, 0);
                JTable patientTable = new JTable(model);
                dialog.add(new JScrollPane(patientTable), BorderLayout.CENTER);

                // Listeners
                ActionListener nameListener = e -> {
                        model.setRowCount(0);
                        java.util.List<Object[]> results = DatabaseManager.searchPatientsSpecific(nameField.getText(),
                                        "Name");
                        for (Object[] row : results) {
                                model.addRow(new Object[] { row[0], row[2], row[6], row[4] + " " + row[5], row[3] });
                        }
                };
                btnNameSearch.addActionListener(nameListener);
                nameField.addActionListener(nameListener);

                ActionListener mobileListener = e -> {
                        model.setRowCount(0);
                        java.util.List<Object[]> results = DatabaseManager.searchPatientsSpecific(mobileField.getText(),
                                        "Mobile");
                        for (Object[] row : results) {
                                model.addRow(new Object[] { row[0], row[2], row[6], row[4] + " " + row[5], row[3] });
                        }
                };
                btnMobileSearch.addActionListener(mobileListener);
                mobileField.addActionListener(mobileListener);

                ActionListener idListener = e -> {
                        model.setRowCount(0);
                        java.util.List<Object[]> results = DatabaseManager.searchPatientsSpecific(idField.getText(),
                                        "ID");
                        for (Object[] row : results) {
                                model.addRow(new Object[] { row[0], row[2], row[6], row[4] + " " + row[5], row[3] });
                        }
                };
                btnIdSearch.addActionListener(idListener);
                idField.addActionListener(idListener);

                JButton btnSelect = new JButton("Select Patient");
                btnSelect.addActionListener(e -> {
                        int row = patientTable.getSelectedRow();
                        if (row != -1) {
                                int id = (int) model.getValueAt(row, 0);
                                String query = String.valueOf(id);
                                java.util.List<Object[]> results = DatabaseManager.searchPatients(query); // General
                                                                                                          // search by
                                                                                                          // ID works
                                                                                                          // fine here
                                for (Object[] pData : results) {
                                        if ((int) pData[0] == id) {
                                                txtRegNo.setText("IPSM/" + pData[0]);
                                                comboTitle.setSelectedItem(pData[1]);
                                                txtPatientName.setText((String) pData[2]);
                                                comboGender.setSelectedItem(pData[3]);
                                                txtAge.setText(String.valueOf(pData[4]));
                                                comboAgeUnit.setSelectedItem(pData[5]);
                                                txtMobile.setText((String) pData[6]);
                                                txtEmail.setText((String) pData[7]);
                                                txtAddress.setText((String) pData[8]);
                                                comboMaritalStatus.setSelectedItem(pData[9]);
                                                break;
                                        }
                                }
                                dialog.dispose();
                        }
                });
                dialog.add(btnSelect, BorderLayout.SOUTH);

                dialog.setVisible(true);
        }

        private JPanel createPatientInfoPanel() {
                JPanel p = new JPanel(new GridBagLayout());
                p.setBackground(Color.WHITE);
                TitledBorder border = BorderFactory.createTitledBorder("Patient Info");
                border.setTitleColor(new Color(150, 0, 0));
                border.setTitleFont(new Font("Arial", Font.BOLD, 14));
                p.setBorder(border);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(2, 5, 2, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Row 1
                gbc.gridx = 0;
                gbc.gridy = 0;
                p.add(createLabel("Patient Name :", true), gbc);

                JPanel pNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                pNamePanel.setOpaque(false);
                comboTitle = new JComboBox<>(new String[] { "Mr.", "Mrs.", "Ms.", "baby", "master" });
                pNamePanel.add(comboTitle);
                txtPatientName = new JTextField(15);
                pNamePanel.add(txtPatientName);
                gbc.gridx = 1;
                p.add(pNamePanel, gbc);

                gbc.gridx = 2;
                p.add(createLabel("Reg No. :", false), gbc);
                gbc.gridx = 3;
                JPanel regPanel = new JPanel(new BorderLayout(2, 0));
                regPanel.setOpaque(false);
                txtRegNo = new JTextField("IPSM/" + DatabaseManager.getNextPatientId(), 10);
                txtRegNo.setEditable(false);
                txtRegNo.setBackground(new Color(240, 240, 240));
                regPanel.add(txtRegNo, BorderLayout.CENTER);
                JButton btnSearchPatient = new JButton("Search");
                btnSearchPatient.setPreferredSize(new Dimension(80, 25));
                btnSearchPatient.addActionListener(e -> showPatientSearchDialog());
                regPanel.add(btnSearchPatient, BorderLayout.EAST);
                p.add(regPanel, gbc);
                // JPanel regPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                // regPanel.setOpaque(false);
                // regPanel.add(new JTextField(8));
                // regPanel.add(new JButton("Search"));
                // regPanel.add(new JButton("Old Patients"));
                // gbc.gridx = 3;
                // p.add(regPanel, gbc);

                // Row 2: Age
                gbc.gridx = 0;
                gbc.gridy = 1;
                JPanel ageLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                ageLabelPanel.setOpaque(false);
                JRadioButton rbAge = new JRadioButton("", true);
                ageLabelPanel.add(rbAge);
                ageLabelPanel.add(new JLabel("<html><font color='red'>Age :</font></html>"));
                p.add(ageLabelPanel, gbc);

                JPanel ageFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                ageFieldPanel.setOpaque(false);
                txtAge = new JTextField(5);
                comboAgeUnit = new JComboBox<>(new String[] { "YRS", "MONTHS", "DAYS" });
                comboGender = new JComboBox<>(new String[] { "Male", "Female", "Other" });
                comboMaritalStatus = new JComboBox<>(new String[] { "Single", "Married" });
                ageFieldPanel.add(txtAge);
                ageFieldPanel.add(comboAgeUnit);
                ageFieldPanel.add(comboGender);
                ageFieldPanel.add(new JLabel("  Marital:"));
                ageFieldPanel.add(comboMaritalStatus);
                gbc.gridx = 1;
                p.add(ageFieldPanel, gbc);

                // Auto Title Update Logic
                ActionListener autoTitleListener = e -> updateTitleAuto();
                comboGender.addActionListener(autoTitleListener);
                comboAgeUnit.addActionListener(autoTitleListener);
                comboMaritalStatus.addActionListener(autoTitleListener);
                txtAge.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                                updateTitleAuto();
                        }
                });

                gbc.gridx = 2;
                p.add(createLabel("Mobile No :", true), gbc);
                gbc.gridx = 3;
                txtMobile = new JTextField("+91 ", 15);
                ((AbstractDocument) txtMobile.getDocument()).setDocumentFilter(new DocumentFilter() {
                        @Override
                        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                                        throws BadLocationException {
                                if (offset < 4)
                                        return;
                                if (fb.getDocument().getLength() + string.length() > 14)
                                        return;
                                if (string.matches("\\d+")) {
                                        super.insertString(fb, offset, string, attr);
                                }
                        }

                        @Override
                        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                                        throws BadLocationException {
                                if (offset < 4) {
                                        // Prevents deletion of prefix even by selection
                                        if (offset == 0 && length == fb.getDocument().getLength()
                                                        && text.startsWith("+91 ")) {
                                                // Allow if it's a full replacement starting with prefix
                                                String suffix = text.substring(4);
                                                if (suffix.matches("\\d*") && suffix.length() <= 10) {
                                                        super.replace(fb, offset, length, text, attrs);
                                                }
                                        }
                                        return;
                                }
                                if (fb.getDocument().getLength() + text.length() - length > 14)
                                        return;
                                if (text == null || text.matches("\\d*")) {
                                        super.replace(fb, offset, length, text, attrs);
                                }
                        }

                        @Override
                        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                                if (offset < 4)
                                        return;
                                super.remove(fb, offset, length);
                        }
                });
                p.add(txtMobile, gbc);

                // Row 3: DOB
                gbc.gridx = 0;
                gbc.gridy = 2;
                JPanel dobLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                dobLabelPanel.setOpaque(false);
                JRadioButton rbDob = new JRadioButton("", false);
                dobLabelPanel.add(rbDob);
                dobLabelPanel.add(new JLabel("Date of Birth :"));
                p.add(dobLabelPanel, gbc);

                ButtonGroup ageDobGroup = new ButtonGroup();
                ageDobGroup.add(rbAge);
                ageDobGroup.add(rbDob);

                JPanel dobFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                dobFieldPanel.setOpaque(false);
                comboDay = new JComboBox<>();
                comboMonth = new JComboBox<>();
                comboYear = new JComboBox<>();

                // Populate Day
                comboDay.addItem("Day");
                for (int i = 1; i <= 31; i++)
                        comboDay.addItem(String.valueOf(i));

                // Populate Month
                comboMonth.addItem("Month");
                String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                                "Dec" };
                for (String m : months)
                        comboMonth.addItem(m);

                // Populate Year
                comboYear.addItem("Year");
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                for (int i = currentYear; i >= 1900; i--)
                        comboYear.addItem(String.valueOf(i));

                dobFieldPanel.add(comboDay);
                dobFieldPanel.add(new JLabel("-"));
                dobFieldPanel.add(comboMonth);
                dobFieldPanel.add(new JLabel("-"));
                dobFieldPanel.add(comboYear);
                gbc.gridx = 1;
                p.add(dobFieldPanel, gbc);

                // Enable/Disable and Logic
                comboDay.setEnabled(false);
                comboMonth.setEnabled(false);
                comboYear.setEnabled(false);

                rbAge.addActionListener(e -> {
                        txtAge.setEnabled(true);
                        comboAgeUnit.setEnabled(true);
                        comboDay.setEnabled(false);
                        comboMonth.setEnabled(false);
                        comboYear.setEnabled(false);
                });

                rbDob.addActionListener(e -> {
                        txtAge.setEnabled(false);
                        comboAgeUnit.setEnabled(false);
                        comboDay.setEnabled(true);
                        comboMonth.setEnabled(true);
                        comboYear.setEnabled(true);
                });

                ActionListener dobCalcListener = e -> {
                        if (rbDob.isSelected() && comboDay.getSelectedIndex() > 0 && comboMonth.getSelectedIndex() > 0
                                        && comboYear.getSelectedIndex() > 0) {
                                try {
                                        int d = Integer.parseInt((String) comboDay.getSelectedItem());
                                        int m = comboMonth.getSelectedIndex(); // 1-based Month (Jan=1)
                                        int y = Integer.parseInt((String) comboYear.getSelectedItem());

                                        LocalDate birthDate = LocalDate.of(y, m, d);
                                        LocalDate now = LocalDate.now();
                                        Period period = Period.between(birthDate, now);

                                        if (period.getYears() > 0) {
                                                txtAge.setText(String.valueOf(period.getYears()));
                                                comboAgeUnit.setSelectedItem("YRS");
                                        } else if (period.getMonths() > 0) {
                                                txtAge.setText(String.valueOf(period.getMonths()));
                                                comboAgeUnit.setSelectedItem("MONTHS");
                                        } else {
                                                txtAge.setText(String.valueOf(period.getDays()));
                                                comboAgeUnit.setSelectedItem("DAYS");
                                        }
                                } catch (Exception ex) {
                                        // Invalid date (e.g. Feb 30)
                                }
                        }
                };

                comboDay.addActionListener(dobCalcListener);
                comboMonth.addActionListener(dobCalcListener);
                comboYear.addActionListener(dobCalcListener);

                gbc.gridx = 2;
                p.add(createLabel("Email ID :", false), gbc);
                gbc.gridx = 3;
                txtEmail = new JTextField(15);
                p.add(txtEmail, gbc);

                // Row 4: Address
                gbc.gridx = 0;
                gbc.gridy = 3;
                p.add(createLabel("Address :", false), gbc);
                gbc.gridx = 1;
                txtAddress = new JTextField(20);
                p.add(txtAddress, gbc);

                gbc.gridx = 2;
                p.add(createLabel("Source :", false), gbc);
                gbc.gridx = 3;
                comboSource = new JComboBox<>(
                                new String[] { "Walking", "Refer by Doctor", "Through Camp",
                                                "Reference" });
                comboSource.addActionListener(e -> {
                        if ("Refer by Doctor".equals(comboSource.getSelectedItem())) {
                                showDoctorSelectionDialog();
                        }
                });
                p.add(comboSource, gbc);

                // Row 5: Contact
                // gbc.gridx = 0;
                // gbc.gridy = 4;
                // p.add(createLabel("Mobile No :", true), gbc);
                // gbc.gridx = 1;
                // p.add(new JTextField(15), gbc);
                // gbc.gridx = 2;
                // p.add(createLabel("State :", false), gbc);
                // gbc.gridx = 3;
                // p.add(new JComboBox<>(new String[] { "Delhi" }), gbc);

                // Row 6
                // gbc.gridx = 0;
                // gbc.gridy = 5;
                // p.add(createLabel("Email ID :", false), gbc);
                // gbc.gridx = 1;
                // p.add(new JTextField(15), gbc);
                // gbc.gridx = 2;
                // p.add(createLabel("City :", false), gbc);
                // gbc.gridx = 3;
                // p.add(new JComboBox<>(new String[] { "Delhi" }), gbc);

                // Row 7
                // gbc.gridx = 0;
                // gbc.gridy = 6;
                // p.add(createLabel("Collection Type :", false), gbc);
                // gbc.gridx = 1;
                // p.add(new JComboBox<>(
                // new String[] { "Walkin", "Home Collection", "Refer by Doctor", "camp
                // through", "reference" }), gbc);
                // gbc.gridx = 2;
                // p.add(createLabel("Test :", true), gbc);
                // gbc.gridx = 3;
                // p.add(new JComboBox<>(new String[] { "LDPL1439#SOLICITOUS WELLNESS" }), gbc);

                // Row 8
                // gbc.gridx = 0;
                // gbc.gridy = 7;
                // p.add(createLabel("Passport No. :", false), gbc);
                // gbc.gridx = 1;
                // p.add(new JTextField(15), gbc);
                // gbc.gridx = 2;
                // p.add(createLabel("Phlebotomist :", false), gbc);
                // gbc.gridx = 3;
                // p.add(new JComboBox<>(new String[] { "ADIL AHAMAD (DHAMPUIR)" }), gbc);

                // Row 8
                gbc.gridx = 0;
                gbc.gridy = 7;
                p.add(createLabel("Refer Doctor :", true), gbc);
                JPanel docPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                docPanel.setOpaque(false);
                txtReferDoctor = new JTextField(15);
                docPanel.add(txtReferDoctor);
                JButton btnNewDoctor = new JButton("Add");
                btnNewDoctor.addActionListener(e -> new DoctorRegistrationFrame().setVisible(true));
                docPanel.add(btnNewDoctor);
                gbc.gridx = 1;
                p.add(docPanel, gbc);

                // gbc.gridx = 4;
                // p.add(createLabel("Weight :", false), gbc);
                // JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                // weightPanel.setOpaque(false);
                // weightPanel.add(new JTextField(5));
                // weightPanel.add(new JComboBox<>(new String[] { "Kg" }));
                // gbc.gridx = 5;
                // p.add(weightPanel, gbc);

                // Row 10
                // gbc.gridx = 0;
                // gbc.gridy = 9;
                // p.add(createLabel("Refer Lab :", false), gbc);
                // JPanel labPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                // labPanel.setOpaque(false);
                // labPanel.add(new JTextField("Self @1", 15));
                // labPanel.add(new JButton("New"));
                // gbc.gridx = 1;
                // p.add(labPanel, gbc);

                // gbc.gridx = 2;
                // p.add(createLabel("Height :", false), gbc);
                // JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                // heightPanel.setOpaque(false);
                // heightPanel.add(new JTextField(5));
                // heightPanel.add(new JComboBox<>(new String[] { "CM" }));
                // gbc.gridx = 3;
                // p.add(heightPanel, gbc);

                // Row 11: Comment
                // gbc.gridx = 0;
                // gbc.gridy = 10;
                // p.add(createLabel("Comment :", false), gbc);
                // gbc.gridx = 1;
                // gbc.gridwidth = 3;
                // p.add(new JTextField(50), gbc);

                return p;
        }

        private void updateTitleAuto() {
                String ageStr = txtAge.getText().trim();
                String gender = (String) comboGender.getSelectedItem();
                String ageUnit = (String) comboAgeUnit.getSelectedItem();
                String marital = (String) comboMaritalStatus.getSelectedItem();

                if (ageStr.isEmpty())
                        return;

                try {
                        int age = Integer.parseInt(ageStr);
                        boolean isChild = false;
                        if ("YRS".equals(ageUnit) && age < 12) {
                                isChild = true;
                        } else if ("MONTHS".equals(ageUnit) || "DAYS".equals(ageUnit)) {
                                isChild = true;
                        }

                        if (isChild) {
                                if ("Female".equals(gender)) {
                                        comboTitle.setSelectedItem("baby");
                                } else if ("Male".equals(gender)) {
                                        comboTitle.setSelectedItem("master");
                                }
                        } else {
                                if ("Male".equals(gender)) {
                                        comboTitle.setSelectedItem("Mr.");
                                } else if ("Female".equals(gender)) {
                                        if ("Married".equals(marital)) {
                                                comboTitle.setSelectedItem("Mrs.");
                                        } else {
                                                comboTitle.setSelectedItem("Ms.");
                                        }
                                }
                        }
                } catch (NumberFormatException e) {
                }
        }

        private JPanel createInvestigationPanel() {
                JPanel p = new JPanel(new BorderLayout());
                p.setBackground(new Color(230, 245, 255));
                TitledBorder border = BorderFactory.createTitledBorder("Investigation");
                border.setTitleColor(new Color(150, 0, 0));
                border.setTitleFont(new Font("Arial", Font.BOLD, 14));
                p.setBorder(border);

                // --- Top Bar: Category & Search ---
                JPanel topBar = new JPanel(new GridBagLayout());
                topBar.setOpaque(false);
                GridBagConstraints tbc = new GridBagConstraints();
                tbc.gridx = 0;
                tbc.gridy = 0;
                tbc.anchor = GridBagConstraints.WEST;
                tbc.weightx = 1.0;

                rbPathology = new JRadioButton("Pathology", true);
                rbDental = new JRadioButton("Dental");
                rbPhysiotherapy = new JRadioButton("Physiotherapy");
                rbXRay = new JRadioButton("Radiology");

                ButtonGroup mainCategoryGroup = new ButtonGroup();
                mainCategoryGroup.add(rbPathology);
                mainCategoryGroup.add(rbDental);
                mainCategoryGroup.add(rbPhysiotherapy);
                mainCategoryGroup.add(rbXRay);

                JPanel categoryGrid = new JPanel(new GridBagLayout());
                categoryGrid.setOpaque(false);
                GridBagConstraints gc = new GridBagConstraints();
                gc.anchor = GridBagConstraints.WEST;
                gc.insets = new Insets(2, 10, 2, 10);

                gc.gridx = 0;
                gc.gridy = 0;
                categoryGrid.add(new JLabel("Category:"), gc);

                gc.gridx = 1;
                categoryGrid.add(rbPathology, gc);
                gc.gridx = 2;
                categoryGrid.add(rbDental, gc);
                gc.gridx = 3;
                categoryGrid.add(rbPhysiotherapy, gc);
                gc.gridx = 4;
                categoryGrid.add(rbXRay, gc);

                // --- Radiology Sub-Category Panel ---
                JPanel radiologySubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
                radiologySubPanel.setOpaque(false);
                radiologySubPanel.setVisible(false);

                JRadioButton subXRay = new JRadioButton("X-Ray", true);
                JRadioButton subCT = new JRadioButton("CT Scan");
                JRadioButton subMRI = new JRadioButton("MRI");
                JRadioButton subUSG = new JRadioButton("Ultrasound");
                JRadioButton subECG = new JRadioButton("ECG");

                ButtonGroup radiologyGroup = new ButtonGroup();
                radiologyGroup.add(subXRay);
                radiologyGroup.add(subCT);
                radiologyGroup.add(subMRI);
                radiologyGroup.add(subUSG);
                radiologyGroup.add(subECG);

                radiologySubPanel.add(subXRay);
                radiologySubPanel.add(subCT);
                radiologySubPanel.add(subMRI);
                radiologySubPanel.add(subUSG);
                radiologySubPanel.add(subECG);

                gc.gridy = 1;
                gc.gridx = 4;
                gc.gridwidth = GridBagConstraints.REMAINDER;
                categoryGrid.add(radiologySubPanel, gc);

                topBar.add(categoryGrid, tbc);

                tbc.gridy = 1;
                JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                searchPanel.setOpaque(false);
                searchPanel.add(new JLabel("Search Test:"));
                searchField = new JTextField(30);
                searchPanel.add(searchField);

                JButton btnAddTest = new JButton("+ Add Test");
                btnAddTest.setBackground(new Color(0, 153, 76));
                btnAddTest.setForeground(Color.BLACK);
                btnAddTest.setFont(new Font("Arial", Font.BOLD, 12));
                btnAddTest.addActionListener(e -> showAddTestDialog());
                searchPanel.add(btnAddTest);

                JButton btnDeleteTest = new JButton("- Delete Test");
                btnDeleteTest.setBackground(new Color(204, 0, 0));
                btnDeleteTest.setForeground(Color.BLACK);
                btnDeleteTest.setFont(new Font("Arial", Font.BOLD, 12));
                btnDeleteTest.addActionListener(e -> deleteSelectedTest());
                searchPanel.add(btnDeleteTest);

                topBar.add(searchPanel, tbc);

                tbc.gridy = 2;
                JPanel doctorSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                doctorSearchPanel.setOpaque(false);
                doctorSearchPanel.add(new JLabel("Search Doctor:"));
                txtSearchDoctor = new JTextField(30);
                txtSearchDoctor.setEditable(false);
                txtSearchDoctor.setBackground(Color.WHITE);
                doctorSearchPanel.add(txtSearchDoctor);
                JButton btnDoctorSearch = new JButton("Select Doctor");
                btnDoctorSearch.addActionListener(e -> {
                        String spec = rbPathology.isSelected() ? "Pathology"
                                        : rbDental.isSelected() ? "Dental"
                                                        : rbPhysiotherapy.isSelected() ? "Physiotherapy"
                                                                        : "Radiology";
                        showDoctorSelectionDialog(spec, true, txtSearchDoctor);
                });
                doctorSearchPanel.add(btnDoctorSearch);
                topBar.add(doctorSearchPanel, tbc);
                p.add(topBar, BorderLayout.NORTH);

                // --- Center Panel: Search Table | Main Table ---
                JPanel centerPanel = new JPanel(new GridBagLayout());
                centerPanel.setOpaque(false);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weighty = 1.0;

                // Search Table (Left)
                String[] searchCols = { "Select", "Test Code", "Investigation Name" };
                searchTableModel = new DefaultTableModel(searchCols, 0) {
                        @Override
                        public Class<?> getColumnClass(int columnIndex) {
                                return (columnIndex == 0) ? Boolean.class : String.class;
                        }

                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return column == 0;
                        }
                };

                searchTable = new JTable(searchTableModel);
                searchTable.getColumnModel().getColumn(0).setMaxWidth(50);
                searchTable.getColumnModel().getColumn(1).setMaxWidth(100);

                rbPathology.addActionListener(e -> {
                        radiologySubPanel.setVisible(false);
                        refreshSearchTable("Pathology");
                });
                rbDental.addActionListener(e -> {
                        radiologySubPanel.setVisible(false);
                        refreshSearchTable("Dental");
                });
                rbPhysiotherapy.addActionListener(e -> {
                        radiologySubPanel.setVisible(false);
                        refreshSearchTable("Physiotherapy");
                });
                rbXRay.addActionListener(e -> {
                        radiologySubPanel.setVisible(true);
                        subXRay.setSelected(true);
                        refreshSearchTable("X-Ray");
                });

                subXRay.addActionListener(e -> {
                        refreshSearchTable("X-Ray");
                });
                subCT.addActionListener(e -> {
                        refreshSearchTable("CT Scan");
                });
                subMRI.addActionListener(e -> {
                        refreshSearchTable("MRI");
                });
                subUSG.addActionListener(e -> {
                        refreshSearchTable("Ultrasound");
                });
                subECG.addActionListener(e -> {
                        refreshSearchTable("ECG");
                });

                final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(searchTableModel);
                searchTable.setRowSorter(sorter);

                searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                        public void insertUpdate(javax.swing.event.DocumentEvent e) {
                                filter();
                        }

                        public void removeUpdate(javax.swing.event.DocumentEvent e) {
                                filter();
                        }

                        public void changedUpdate(javax.swing.event.DocumentEvent e) {
                                filter();
                        }

                        private void filter() {
                                String text = searchField.getText();
                                if (text.trim().length() == 0) {
                                        sorter.setRowFilter(null);
                                } else {
                                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                                }
                        }
                });

                searchTableModel.addTableModelListener(e -> {
                        if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 0) {
                                int modelRow = e.getFirstRow();
                                boolean selected = (Boolean) searchTableModel.getValueAt(modelRow, 0);
                                String code = (String) searchTableModel.getValueAt(modelRow, 1);
                                java.util.Optional<TestData> testDataOpt = allTests.stream()
                                                .filter(t -> t.code.equals(code)).findFirst();
                                TestData test = testDataOpt.orElse(null);
                                if (test != null) {
                                        updateMainTableSelection(test, selected);
                                }
                        }
                });

                JScrollPane searchScroll = new JScrollPane(searchTable);
                searchScroll.setPreferredSize(new Dimension(400, 250));
                gbc.gridx = 0;
                gbc.weightx = 0.4;
                centerPanel.add(searchScroll, gbc);

                // Main Table (Right)
                String[] mainCols = { "Sr.No", "Test", "MRP", "Disc.", "Amt.", "View" };
                mainTableModel = new DefaultTableModel(mainCols, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false; // All custom interactions via mouse listener
                        }
                };
                mainTable = new JTable(mainTableModel);
                mainTable.getTableHeader().setBackground(new Color(51, 153, 255));
                mainTable.getTableHeader().setForeground(Color.BLACK);
                mainTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

                mainTable.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                                int row = mainTable.getSelectedRow();
                                int col = mainTable.getSelectedColumn();
                                if (row != -1) {
                                        if (col == 5)
                                                showDetailsPopup(row);
                                }
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                                showPopup(e);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                                showPopup(e);
                        }

                        private void showPopup(MouseEvent e) {
                                if (e.isPopupTrigger()) {
                                        int row = mainTable.rowAtPoint(e.getPoint());
                                        if (row != -1) {
                                                mainTable.setRowSelectionInterval(row, row);
                                                JPopupMenu menu = new JPopupMenu();
                                                JMenuItem duplicateItem = new JMenuItem("Duplicate Test (Add Again)");
                                                JMenuItem removeItem = new JMenuItem("Remove from Bill");

                                                duplicateItem.addActionListener(al -> {
                                                        String testDetails = (String) mainTableModel.getValueAt(row, 1);
                                                        String code = testDetails.split(" ~ ")[0];
                                                        TestData testToDup = allTests.stream()
                                                                        .filter(t -> t.code.equals(code)).findFirst()
                                                                        .orElse(null);
                                                        if (testToDup != null) {
                                                                mainTableModel.addRow(new Object[] {
                                                                                mainTableModel.getRowCount() + 1,
                                                                                testToDup.code + " ~ " + testToDup.name,
                                                                                testToDup.mrp,
                                                                                0.0,
                                                                                testToDup.mrp,
                                                                                "View"
                                                                });
                                                                calculateTotal();
                                                        }
                                                });

                                                removeItem.addActionListener(al -> {
                                                        mainTableModel.removeRow(row);
                                                        // Re-number
                                                        for (int j = 0; j < mainTableModel.getRowCount(); j++) {
                                                                mainTableModel.setValueAt(j + 1, j, 0);
                                                        }
                                                        calculateTotal();
                                                        refreshSearchTable((String) (rbPathology.isSelected()
                                                                        ? "Pathology"
                                                                        : rbDental.isSelected() ? "Dental"
                                                                                        : rbPhysiotherapy.isSelected()
                                                                                                        ? "Physiotherapy"
                                                                                                        : "X-Ray"));
                                                });

                                                menu.add(duplicateItem);
                                                menu.add(removeItem);
                                                menu.show(e.getComponent(), e.getX(), e.getY());
                                        }
                                }
                        }
                });

                JScrollPane mainScroll = new JScrollPane(mainTable);
                gbc.gridx = 1;
                gbc.weightx = 0.6;
                centerPanel.add(mainScroll, gbc);

                p.add(centerPanel, BorderLayout.CENTER);

                refreshSearchTable("Pathology");

                return p;
        }

        private void refreshSearchTable(String category) {
                searchTableModel.setRowCount(0);
                java.util.List<Object[]> selectedRows = new java.util.ArrayList<>();
                java.util.List<Object[]> unselectedRows = new java.util.ArrayList<>();

                for (TestData test : allTests) {
                        if (test.category.equalsIgnoreCase(category)) {
                                boolean alreadySelected = false;
                                if (mainTableModel != null) {
                                        for (int i = 0; i < mainTableModel.getRowCount(); i++) {
                                                if (mainTableModel.getValueAt(i, 1).toString().startsWith(test.code)) {
                                                        alreadySelected = true;
                                                        break;
                                                }
                                        }
                                }
                                if (alreadySelected) {
                                        selectedRows.add(new Object[] { true, test.code, test.name });
                                } else {
                                        unselectedRows.add(new Object[] { false, test.code, test.name });
                                }
                        }
                }

                for (Object[] row : selectedRows)
                        searchTableModel.addRow(row);
                for (Object[] row : unselectedRows)
                        searchTableModel.addRow(row);
        }

        private void updateMainTableSelection(TestData test, boolean selected) {
                if (selected) {
                        // Add if not already present
                        boolean exists = false;
                        for (int i = 0; i < mainTableModel.getRowCount(); i++) {
                                if (mainTableModel.getValueAt(i, 1).toString().startsWith(test.code)) {
                                        exists = true;
                                        break;
                                }
                        }
                        if (exists) {
                                int choice = JOptionPane.showConfirmDialog(this,
                                                "Test '" + test.name
                                                                + "' is already added.\nDo you want to add it again?",
                                                "Duplicate Test", JOptionPane.YES_NO_OPTION);
                                if (choice != JOptionPane.YES_OPTION)
                                        return;
                        }

                        // Check for doctor context
                        String currentCategory = test.category;
                        if (!currentCategory.equals(lastDoctorSpecialization) || txtReferDoctor.getText().isEmpty()) {
                                String spec = currentCategory;
                                if (currentCategory.contains("Scan") || currentCategory.contains("X-Ray")
                                                || currentCategory.contains("Ultrasound")
                                                || currentCategory.contains("MRI")) {
                                        spec = "Radiology";
                                }
                                int choice = JOptionPane.showConfirmDialog(this,
                                                "Would you like to select a referring doctor for " + currentCategory
                                                                + "?",
                                                "Doctor Selection", JOptionPane.YES_NO_OPTION);
                                if (choice == JOptionPane.YES_OPTION) {
                                        showDoctorSelectionDialog(spec, true, txtSearchDoctor);
                                }
                                lastDoctorSpecialization = spec;
                        }

                        mainTableModel.addRow(new Object[] {
                                        mainTableModel.getRowCount() + 1,
                                        test.code + " ~ " + test.name,
                                        test.mrp,
                                        0.0,
                                        test.mrp,
                                        "View"
                        });
                        calculateTotal();
                } else {
                        // Remove ALL instances if present (staying consistent with checkbox)
                        for (int i = mainTableModel.getRowCount() - 1; i >= 0; i--) {
                                if (mainTableModel.getValueAt(i, 1).toString().startsWith(test.code)) {
                                        mainTableModel.removeRow(i);
                                }
                        }
                        // Re-number
                        for (int j = 0; j < mainTableModel.getRowCount(); j++) {
                                mainTableModel.setValueAt(j + 1, j, 0);
                        }
                        calculateTotal();
                }
        }

        private void calculateTotal() {
                double total = 0;
                for (int i = 0; i < mainTableModel.getRowCount(); i++) {
                        try {
                                Object val = mainTableModel.getValueAt(i, 4); // Amt. column
                                if (val != null) {
                                        total += Double.parseDouble(val.toString());
                                }
                        } catch (Exception e) {
                                // Ignore parsing errors
                        }
                }
                if (txtTotal != null) {
                        txtTotal.setText(String.format("%.2f", total));
                }
                updateBalanceFromPayments();
        }

        private JPanel createPaymentDetailPanel() {
                JPanel p = new JPanel(new BorderLayout());
                p.setBackground(new Color(220, 240, 255));
                TitledBorder border = BorderFactory.createTitledBorder("Payment Detail");
                border.setTitleColor(new Color(150, 0, 0));
                border.setTitleFont(new Font("Arial", Font.BOLD, 14));
                p.setBorder(border);

                // --- Top Controls ---
                JPanel topBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                topBox.setOpaque(false);

                JButton btnCash = new JButton("CASH");
                JButton btnCard = new JButton("CARD");
                JButton btnUpi = new JButton("UPI");

                btnCash.setBackground(new Color(255, 255, 200));
                btnCard.setBackground(new Color(200, 255, 200));
                btnUpi.setBackground(new Color(200, 200, 255));

                topBox.add(btnCash);
                topBox.add(btnCard);
                topBox.add(btnUpi);

                topBox.add(new JLabel("Total:"));
                txtTotal = new JTextField("0", 6);
                txtTotal.setEditable(false);
                topBox.add(txtTotal);

                topBox.add(new JLabel("Disc %:"));
                txtDiscPercent = new JTextField("0", 6);
                topBox.add(txtDiscPercent);

                topBox.add(new JLabel("Disc Amt:"));
                txtDiscAmount = new JTextField("0", 8);
                topBox.add(txtDiscAmount);

                topBox.add(new JLabel("BalAmt:"));
                lblBalAmtValue = new JLabel("0.00");
                lblBalAmtValue.setFont(new Font("Arial", Font.BOLD, 14));
                lblBalAmtValue.setForeground(Color.BLUE);
                topBox.add(lblBalAmtValue);

                p.add(topBox, BorderLayout.NORTH);

                // --- Table ---
                String[] cols = { "#", "Payment Mode", "Amount", "Payment Details", "Remarks" };
                paymentTableModel = new DefaultTableModel(cols, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false;
                        }
                };
                JTable table = new JTable(paymentTableModel);
                table.getTableHeader().setBackground(new Color(51, 153, 255));
                table.getTableHeader().setForeground(Color.BLACK);
                table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

                JScrollPane scroll = new JScrollPane(table);
                scroll.setPreferredSize(new Dimension(800, 100));
                p.add(scroll, BorderLayout.CENTER);

                // Listeners
                btnCash.addActionListener(e -> showPaymentDialog("Cash", "Receiver Name"));
                btnCard.addActionListener(e -> showPaymentDialog("Card", "Transaction ID"));
                btnUpi.addActionListener(e -> showPaymentDialog("UPI", "UPI ID"));

                // Discount Listeners
                txtDiscPercent.addActionListener(e -> calculateDiscountFromPercent());
                txtDiscAmount.addActionListener(e -> calculateDiscountFromAmount());

                // Focus lost listeners for real-time update
                txtDiscPercent.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusLost(java.awt.event.FocusEvent e) {
                                calculateDiscountFromPercent();
                        }
                });
                txtDiscAmount.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusLost(java.awt.event.FocusEvent e) {
                                calculateDiscountFromAmount();
                        }
                });

                return p;
        }

        private void calculateDiscountFromPercent() {
                if (isAdjustingDiscount)
                        return;
                isAdjustingDiscount = true;
                try {
                        double total = Double.parseDouble(txtTotal.getText());
                        double percent = Double.parseDouble(
                                        txtDiscPercent.getText().isEmpty() ? "0" : txtDiscPercent.getText());
                        double discAmt = (total * percent) / 100.0;
                        txtDiscAmount.setText(String.format("%.2f", discAmt));
                        updateBalanceFromPayments();
                } catch (Exception ex) {
                }
                isAdjustingDiscount = false;
        }

        private void calculateDiscountFromAmount() {
                if (isAdjustingDiscount)
                        return;
                isAdjustingDiscount = true;
                try {
                        double total = Double.parseDouble(txtTotal.getText());
                        double discAmt = Double
                                        .parseDouble(txtDiscAmount.getText().isEmpty() ? "0" : txtDiscAmount.getText());
                        if (total > 0) {
                                double percent = (discAmt * 100.0) / total;
                                txtDiscPercent.setText(String.format("%.2f", percent));
                        }
                        updateBalanceFromPayments();
                } catch (Exception ex) {
                }
                isAdjustingDiscount = false;
        }

        private void showPaymentDialog(String mode, String detailLabel) {
                JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
                JTextField amountField = new JTextField(lblBalAmtValue.getText());
                JTextField detailField = new JTextField();

                panel.add(new JLabel("Amount Paid:"));
                panel.add(amountField);
                panel.add(new JLabel(detailLabel + ":"));
                panel.add(detailField);

                int result = JOptionPane.showConfirmDialog(this, panel, mode + " Payment Details",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                        String amount = amountField.getText().trim();
                        String detail = detailField.getText().trim();

                        if (amount.isEmpty() || amount.equals("0") || detail.isEmpty()) {
                                JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
                                return;
                        }

                        paymentTableModel.addRow(new Object[] {
                                        paymentTableModel.getRowCount() + 1,
                                        mode,
                                        amount,
                                        detail,
                                        txtRemarks != null ? txtRemarks.getText() : ""
                        });

                        updateBalanceFromPayments();
                }
        }

        private void updateBalanceFromPayments() {
                try {
                        double total = Double.parseDouble(txtTotal.getText());
                        double discAmt = Double
                                        .parseDouble(txtDiscAmount.getText().isEmpty() ? "0" : txtDiscAmount.getText());
                        double totalPaid = 0;
                        for (int i = 0; i < paymentTableModel.getRowCount(); i++) {
                                totalPaid += Double.parseDouble(paymentTableModel.getValueAt(i, 2).toString());
                        }
                        double balance = total - discAmt - totalPaid;
                        lblBalAmtValue.setText(String.format("%.2f", balance));
                } catch (Exception e) {
                        // Ignore
                }
        }

        private void showDetailsPopup(int row) {
                String testInfo = mainTableModel.getValueAt(row, 1).toString();
                TestData data = allTests.stream()
                                .filter(t -> (t.code + " ~ " + t.name).equals(testInfo))
                                .findFirst().orElse(null);

                if (data != null) {
                        String details = "Test Code: " + data.code + "\n" +
                                        "Test Name: " + data.name + "\n" +
                                        "Special Price: " + data.specialPrice + "\n" +
                                        "MRP: " + data.mrp;
                        JOptionPane.showMessageDialog(this, details, "Test Details", JOptionPane.INFORMATION_MESSAGE);
                }
        }

        // private void showTimePopup(int row) {
        // String input = JOptionPane.showInputDialog(this, "Set Delivery Time
        // (HH:mm):",
        // mainTableModel.getValueAt(row, 6));
        // if (input != null && !input.trim().isEmpty()) {
        // mainTableModel.setValueAt(input, row, 6);
        // }
        // }

        private JLabel createLabel(String text, boolean red) {
                JLabel lbl = new JLabel(text);
                if (red) {
                        lbl.setForeground(new Color(200, 0, 0)); // Brighter Red
                        lbl.setFont(new Font("Arial", Font.BOLD, 12));
                } else {
                        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
                }
                return lbl;
        }

        private void showAddTestDialog() {
                JDialog dialog = new JDialog(this, "Add New Test", true);
                dialog.setLayout(new GridBagLayout());
                dialog.setSize(400, 400);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setLocationRelativeTo(this);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                JTextField txtCode = new JTextField(15);
                JTextField txtName = new JTextField(15);
                JTextField txtCutoff = new JTextField("4.30 PM", 15);
                JTextField txtReportTime = new JTextField("Same Day", 15);
                JTextField txtPrice = new JTextField(15);
                JTextField txtMrp = new JTextField(15);
                JComboBox<String> comboCategory = new JComboBox<>(
                                new String[] { "Pathology", "Dental", "Physiotherapy", "X-Ray", "CT Scan", "MRI",
                                                "Ultrasound", "ECG" });

                int row = 0;
                gbc.gridx = 0;
                gbc.gridy = row;
                dialog.add(new JLabel("Test Code:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtCode, gbc);
                row++;

                gbc.gridx = 0;
                gbc.gridy = row;
                dialog.add(new JLabel("Test Name:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtName, gbc);
                row++;

                gbc.gridx = 0;
                gbc.gridy = row;
                dialog.add(new JLabel("Cutoff Time:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtCutoff, gbc);
                row++;

                gbc.gridx = 0;
                gbc.gridy = row;
                dialog.add(new JLabel("Report Time:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtReportTime, gbc);
                row++;

                gbc.gridx = 0;
                gbc.gridy = row;
                dialog.add(new JLabel("Special Price:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtPrice, gbc);
                row++;

                gbc.gridx = 0;
                gbc.gridy = row;
                dialog.add(new JLabel("MRP:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtMrp, gbc);
                row++;

                // gbc.gridx = 0;
                // gbc.gridy = row;
                // dialog.add(new JLabel("Special Price:"), gbc);
                // gbc.gridx = 1;
                // dialog.add(txtPrice, gbc);
                // row++;

                // gbc.gridx = 0;
                // gbc.gridy = row;
                // dialog.add(new JLabel("MRP:"), gbc);
                // gbc.gridx = 1;
                // dialog.add(txtMrp, gbc);
                // row++;

                gbc.gridx = 0;
                gbc.gridy = row;
                dialog.add(new JLabel("Category:"), gbc);
                gbc.gridx = 1;
                dialog.add(comboCategory, gbc);
                row++;

                JButton btnSave = new JButton("Save Test");
                btnSave.setBackground(new Color(0, 102, 204));
                btnSave.setForeground(Color.BLACK);
                btnSave.setFont(new Font("Arial", Font.BOLD, 13));
                btnSave.addActionListener(e -> {
                        try {
                                if (txtCode.getText().isEmpty() || txtName.getText().isEmpty()) {
                                        JOptionPane.showMessageDialog(dialog, "Code and Name are required.");
                                        return;
                                }
                                TestData newTest = new TestData(
                                                txtCode.getText(),
                                                txtName.getText(),
                                                txtCutoff.getText(),
                                                txtReportTime.getText(),
                                                Double.parseDouble(txtPrice.getText().isEmpty() ? "0"
                                                                : txtPrice.getText()),
                                                Double.parseDouble(
                                                                txtMrp.getText().isEmpty() ? "0" : txtMrp.getText()));
                                newTest.category = (String) comboCategory.getSelectedItem();
                                newTest.isCustom = true;
                                allTests.add(newTest);
                                saveTestToFile(newTest);
                                DatabaseManager.saveTest(newTest.code, newTest.name, newTest.cutoff, newTest.reportTime,
                                                newTest.specialPrice, newTest.mrp, newTest.category, newTest.isCustom);
                                refreshSearchTable(newTest.category);
                                JOptionPane.showMessageDialog(dialog, "Test added successfully!");
                                dialog.dispose();
                        } catch (NumberFormatException ex) {
                                ErrorHandler.showWarning(dialog, "Invalid price or MRP. Please enter numeric values.");
                        } catch (Exception ex) {
                                ErrorHandler.showError(dialog, "Error saving test", ex);
                        }
                });

                gbc.gridx = 0;
                gbc.gridy = row;
                gbc.gridwidth = 2;
                dialog.add(btnSave, gbc);

                dialog.setVisible(true);
        }

        private void deleteSelectedTest() {
                int row = searchTable.getSelectedRow();
                if (row == -1) {
                        JOptionPane.showMessageDialog(this, "Please select a test to delete from the search table.");
                        return;
                }

                int modelRow = searchTable.convertRowIndexToModel(row);
                String code = (String) searchTableModel.getValueAt(modelRow, 1);

                java.util.Optional<TestData> testToDeleteOpt = allTests.stream()
                                .filter(t -> t.code.equals(code))
                                .findFirst();
                TestData testToDelete = testToDeleteOpt.orElse(null);

                if (testToDelete == null)
                        return;

                if (!testToDelete.isCustom) {
                        JOptionPane.showMessageDialog(this,
                                        "Cannot delete built-in tests. You can only delete tests you added.");
                        return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to delete the test: " + testToDelete.name + "?",
                                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                        allTests.remove(testToDelete);
                        rewriteCustomTestsFile();
                        refreshSearchTable(testToDelete.category);
                        ErrorHandler.showInfo(this, "Test deleted successfully!");
                }
        }

        private void rewriteCustomTestsFile() {
                try (FileWriter fw = new FileWriter("custom_tests.txt", false);
                                BufferedWriter bw = new BufferedWriter(fw);
                                PrintWriter out = new PrintWriter(bw)) {
                        for (TestData t : allTests) {
                                if (t.isCustom) {
                                        out.println(String.format("%s|%s|%s|%s|%.2f|%.2f|%s",
                                                        t.code, t.name, t.cutoff, t.reportTime,
                                                        t.specialPrice, t.mrp, t.category));
                                }
                        }
                } catch (IOException e) {
                        ErrorHandler.showError(this, "Error saving local test file", e);
                }
        }
}
