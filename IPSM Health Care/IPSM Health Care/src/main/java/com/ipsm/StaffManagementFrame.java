package com.ipsm;

import com.ipsm.db.DatabaseManager;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StaffManagementFrame extends JFrame {

    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // Form fields
    private JTextField txtStaffId, txtStaffName, txtFatherName, txtMobile, txtAltMobile, txtAadhar,
            txtEmail, txtState, txtPincode, txtGrossSalary, txtWifeName, txtChildrenCount, txtLanguages,
            txtHigherQual;
    private JComboBox<String> comboBranch, comboMarital, comboGender, comboDepartment;
    private JTextArea areaAddress, areaCorrAddress;
    private JTextField dojField, dobField;
    private String[] docPaths = new String[10]; // 0:10th, 1:12th, 2:Photo, 3:Resume, 4:Aadhar, 5:PAN, 6:Signature,
                                                // 7:Bachelor, 8:Master, 9:PHD
    private JLabel[] docStatusLabels = new JLabel[10];

    public StaffManagementFrame() {
        setTitle("Staff Management - IPSM Health Care");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Staff List", createListPanel());
        tabbedPane.addTab("Add New Staff", createFormPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createTitledBorder("Search Staff (Name/ID/Department)"));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterStaff();
            }
        });
        searchPanel.add(searchField);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadStaffData());
        searchPanel.add(btnRefresh);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Name", "Department", "Branch", "Joining Date", "Mobile", "Salary", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffTable = new JTable(tableModel);
        staffTable.setRowHeight(30);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        staffTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);

        loadStaffData();
        return panel;
    }

    private void loadStaffData() {
        tableModel.setRowCount(0);
        List<Object[]> staff = DatabaseManager.getAllStaff();
        for (Object[] s : staff) {
            // Mapping: 0:staff_id, 1:name, 3:designation, 4:branch, 5:doj, 7:mobile,
            // 12:salary, 25:status
            tableModel.addRow(new Object[] { s[0], s[1], s[3], s[4], s[5], s[7], s[12], s[25] });
        }
    }

    private void filterStaff() {
        String query = searchField.getText().toLowerCase();
        // Simple client-side filtering or reload from DB with LIKE
        loadStaffData(); // For now just reload
    }

    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Header
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 4;
        JLabel header = new JLabel("Staff Registration Information", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(new Color(18, 66, 119));
        formPanel.add(header, gbc);

        gbc.gridwidth = 1;

        // Basic Details
        addSectionHeader(formPanel, gbc, "Basic Details", row++);

        txtStaffId = createStyledField();
        txtStaffId.setEditable(false);
        txtStaffId.setBackground(new Color(245, 245, 245));
        addFormRow(formPanel, gbc, row++, "Staff ID:", txtStaffId, "Branch:",
                comboBranch = new JComboBox<>(DatabaseManager.getBranches().toArray(new String[0])));

        comboBranch.addActionListener(e -> updateStaffId());

        txtStaffName = createStyledField();
        txtFatherName = createStyledField();
        addFormRow(formPanel, gbc, row++, "Staff Name *:", txtStaffName, "Father's Name:", txtFatherName);

        comboDepartment = new JComboBox<>(
                new String[] { "Dental", "Pathology", "Physiotherapy", "Radiology", "Admin", "Other" });
        dojField = createDateField();
        addFormRow(formPanel, gbc, row++, "Department *:", comboDepartment, "Date of Joining:", dojField);

        // Contact & ID
        addSectionHeader(formPanel, gbc, "Contact & Identification", row++);
        txtMobile = createStyledField();
        setNumericFilter(txtMobile, 10);
        txtAltMobile = createStyledField();
        setNumericFilter(txtAltMobile, 10);
        addFormRow(formPanel, gbc, row++, "Mobile Number *:", txtMobile, "Alternative No:", txtAltMobile);

        txtAadhar = createStyledField();
        setNumericFilter(txtAadhar, 12);
        txtEmail = createStyledField();
        addFormRow(formPanel, gbc, row++, "Aadhar Number *:", txtAadhar, "Email:", txtEmail);

        // Address
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Permanent Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        areaAddress = new JTextArea(3, 20);
        areaAddress.setBorder(new LineBorder(Color.LIGHT_GRAY));
        formPanel.add(new JScrollPane(areaAddress), gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Corresponding Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        areaCorrAddress = new JTextArea(3, 20);
        areaCorrAddress.setBorder(new LineBorder(Color.LIGHT_GRAY));
        formPanel.add(new JScrollPane(areaCorrAddress), gbc);
        row++;

        txtState = createStyledField();
        txtPincode = createStyledField();
        setNumericFilter(txtPincode, 6);
        addFormRow(formPanel, gbc, row++, "State:", txtState, "Pincode:", txtPincode);

        // Personal
        addSectionHeader(formPanel, gbc, "Personal Details", row++);
        dobField = createDateField();
        comboGender = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        addFormRow(formPanel, gbc, row++, "Date of Birth:", dobField, "Gender:", comboGender);

        comboMarital = new JComboBox<>(new String[] { "Single", "Married" });
        txtWifeName = createStyledField();
        txtChildrenCount = createStyledField();
        setNumericFilter(txtChildrenCount, 2);
        addFormRow(formPanel, gbc, row++, "Marital Status:", comboMarital, "Spouse Name:", txtWifeName);
        addFormRow(formPanel, gbc, row++, "Children Count:", txtChildrenCount, "Qualification:",
                txtHigherQual = createStyledField());

        txtLanguages = createStyledField();
        addFormRow(formPanel, gbc, row++, "Languages Known:", txtLanguages, "", new JLabel());

        // Professional & Salary
        addSectionHeader(formPanel, gbc, "Professional & Salary", row++);
        txtGrossSalary = createStyledField();
        addFormRow(formPanel, gbc, row++, "Gross Salary *:", txtGrossSalary, "", new JLabel());

        // Document Section
        addSectionHeader(formPanel, gbc, "Documents", row++);
        JPanel docPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        docPanel.setOpaque(false);
        String[] docLabels = { "10th Marksheet", "12th Marksheet", "Bachelor's", "Master's", "PHD", "Staff Photo",
                "Resume", "Aadhar Card", "PAN Card", "Signature" };
        for (int i = 0; i < 10; i++) {
            docPanel.add(createDocUploadRow(docLabels[i], i));
        }
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 4;
        formPanel.add(docPanel, gbc);

        // Actions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(248, 249, 250));
        JButton btnSave = new JButton("Save Staff Details");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(67, 160, 71));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(180, 40));
        btnSave.addActionListener(e -> saveStaff());

        JButton btnClear = new JButton("Clear Form");
        btnClear.setPreferredSize(new Dimension(120, 40));
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnClear);
        btnPanel.add(btnSave);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        updateStaffId();
        return mainPanel;
    }

    private JPanel createDocUploadRow(String label, int index) {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.setOpaque(false);
        p.add(new JLabel(label), BorderLayout.WEST);

        docStatusLabels[index] = new JLabel("No file", JLabel.RIGHT);
        docStatusLabels[index].setFont(new Font("Segoe UI", Font.ITALIC, 11));
        docStatusLabels[index].setForeground(Color.GRAY);

        JButton btn = new JButton("...");
        btn.setPreferredSize(new Dimension(30, 20));
        btn.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                docPaths[index] = jfc.getSelectedFile().getAbsolutePath();
                docStatusLabels[index].setText(jfc.getSelectedFile().getName());
                docStatusLabels[index].setForeground(new Color(67, 160, 71));
            }
        });

        JPanel EastP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        EastP.setOpaque(false);
        EastP.add(docStatusLabels[index]);
        EastP.add(btn);
        p.add(EastP, BorderLayout.EAST);
        return p;
    }

    private void addSectionHeader(JPanel p, GridBagConstraints gbc, String title, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(66, 135, 245));
        l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        p.add(l, gbc);
        gbc.gridwidth = 1;
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String l1, JComponent f1, String l2,
            JComponent f2) {
        gbc.gridy = row;
        gbc.gridx = 0;
        p.add(new JLabel(l1), gbc);
        gbc.gridx = 1;
        p.add(f1, gbc);
        gbc.gridx = 2;
        p.add(new JLabel(l2), gbc);
        gbc.gridx = 3;
        p.add(f2, gbc);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5)));
        return f;
    }

    private JTextField createDateField() {
        JTextField f = createStyledField();
        f.setEditable(false);
        f.setBackground(Color.WHITE);
        f.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        f.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DatePicker dp = new DatePicker(null); // Simple adaptation
                String date = dp.getPickedDate();
                if (!date.isEmpty())
                    f.setText(date);
            }
        });
        return f;
    }

    private void updateStaffId() {
        String branch = (String) comboBranch.getSelectedItem();
        if (branch != null) {
            String shortCode = DatabaseManager.getBranchShortCode(branch);
            int seq = DatabaseManager.getNextStaffSequence(branch);
            txtStaffId.setText(String.format("IPSM/%s/%03d", shortCode, seq));
        }
    }

    private void saveStaff() {
        String name = txtStaffName.getText().trim();
        String desig = (String) comboDepartment.getSelectedItem();
        String mob = txtMobile.getText().trim();
        String aadhar = txtAadhar.getText().trim();
        String salText = txtGrossSalary.getText().trim();

        if (name.isEmpty() || desig.isEmpty() || mob.isEmpty() || aadhar.isEmpty() || salText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all mandatory fields marked with *", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double salary = Double.parseDouble(salText);

            // Handle file copies
            String uploadDir = "uploads/staff/" + txtStaffId.getText().replace("/", "_");
            new File(uploadDir).mkdirs();
            String[] savedPaths = new String[10];
            for (int i = 0; i < 10; i++) {
                if (docPaths[i] != null) {
                    File src = new File(docPaths[i]);
                    File dest = new File(uploadDir, src.getName());
                    Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    savedPaths[i] = dest.getAbsolutePath();
                }
            }

            boolean ok = DatabaseManager.saveStaff(
                    txtStaffId.getText(), name, desig, (String) comboBranch.getSelectedItem(), dojField.getText(),
                    salary,
                    mob, aadhar, areaAddress.getText(), dobField.getText(), "Active", txtFatherName.getText(),
                    savedPaths[0], savedPaths[1], savedPaths[7], savedPaths[8], savedPaths[9], savedPaths[5],
                    savedPaths[6], savedPaths[2], savedPaths[3], savedPaths[4], // Note indices changed to match DB
                                                                                // columns in saveMethod
                    txtEmail.getText(), (String) comboMarital.getSelectedItem(), txtState.getText(),
                    txtPincode.getText(),
                    txtAltMobile.getText(), areaCorrAddress.getText(), txtWifeName.getText(),
                    txtChildrenCount.getText().isEmpty() ? "0" : txtChildrenCount.getText(),
                    txtLanguages.getText(), txtHigherQual.getText(), "", "");

            if (ok) {
                JOptionPane.showMessageDialog(this, "Staff Registered Successfully!");
                loadStaffData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Database update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving staff: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtStaffName.setText("");
        txtFatherName.setText("");
        txtMobile.setText("");
        txtAltMobile.setText("");
        txtAadhar.setText("");
        txtEmail.setText("");
        areaAddress.setText("");
        areaCorrAddress.setText("");
        txtPincode.setText("");
        txtGrossSalary.setText("");
        txtWifeName.setText("");
        txtChildrenCount.setText("");
        txtLanguages.setText("");
        txtHigherQual.setText("");
        for (int i = 0; i < 10; i++) {
            docPaths[i] = null;
            docStatusLabels[i].setText("No file");
            docStatusLabels[i].setForeground(Color.GRAY);
        }
        updateStaffId();
    }

    private void setNumericFilter(JTextField f, int max) {
        ((AbstractDocument) f.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("\\d*") && (fb.getDocument().getLength() - length + text.length()) <= max) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffManagementFrame().setVisible(true));
    }
}
