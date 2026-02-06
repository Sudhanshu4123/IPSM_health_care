package com.ipsm;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import com.ipsm.db.DatabaseManager;
import java.awt.*;
import java.util.Calendar;

public class DoctorRegistrationFrame extends JFrame {
    private JComboBox<String> comboTitle;
    private JTextField txtDoctorName;
    private JTextField txtPhone;
    private JTextField txtClinicName;
    private JTextArea txtAddress;
    private JComboBox<String> comboSpecialization;

    public DoctorRegistrationFrame() {
        setTitle("Referring Doctor Registration");
        setSize(700, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        Main.setAppIcon(this);
        getContentPane().setBackground(new Color(210, 235, 255));

        // --- TITLE BAR ---
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(210, 235, 255));
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel lblTitle = new JLabel("REFERRING DOCTOR REGISTRATION");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(lblTitle);
        add(titlePanel, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(new Color(230, 245, 255));
        TitledBorder border = BorderFactory.createTitledBorder("Doctor Details");
        border.setTitleColor(new Color(150, 0, 0));
        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- ROW 0: Name (L) | DateOfBirth (R) ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(createLabel("Name:", true), gbc);
        gbc.gridx = 1;
        JPanel nameBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        nameBox.setOpaque(false);
        comboTitle = new JComboBox<>(new String[] { "Dr.", "Prof." });
        nameBox.add(comboTitle);
        txtDoctorName = new JTextField(12);
        nameBox.add(txtDoctorName);
        detailsPanel.add(nameBox, gbc);

        gbc.gridx = 2;
        detailsPanel.add(createLabel("Date Of Birth:", false), gbc);
        gbc.gridx = 3;
        detailsPanel.add(createDatePicker(), gbc);

        // --- ROW 1: Area/Loc (L) | Marriage Anniversary (R) ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(createLabel("Phone No:", false), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        detailsPanel.add(txtPhone, gbc);

        gbc.gridx = 2;
        detailsPanel.add(createLabel("Specialization :", false), gbc);
        gbc.gridx = 3;
        comboSpecialization = new JComboBox<>(
                new String[] { "Pathology", "Dental", "Physiotherapy", "Radiologist", "Radiology Phar" });
        detailsPanel.add(comboSpecialization, gbc);

        // --- ROW 2: Clinic Name (L) | Clinic Address (R) ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(createLabel("Clinic Name:", false), gbc);
        gbc.gridx = 1;
        txtClinicName = new JTextField(20);
        detailsPanel.add(txtClinicName, gbc);

        gbc.gridx = 2;
        detailsPanel.add(createLabel("DNC No :", false), gbc);
        gbc.gridx = 3;
        detailsPanel.add(new JTextField(20), gbc);

        // --- ROW 3: Hospital Name (L) ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(createLabel("Hospital Name:", false), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JTextField(20), gbc);

        // --- ROW 4: Phone Clinic (L) | Hospital Address (R) ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsPanel.add(createLabel("Address :", false), gbc);
        gbc.gridx = 1;
        gbc.gridheight = 2;
        txtAddress = new JTextArea(3, 20);
        detailsPanel.add(new JScrollPane(txtAddress), gbc);
        gbc.gridheight = 1;
        // detailsPanel.add(createLabel("Phone Clinic:", false), gbc);
        // gbc.gridx = 1;
        // detailsPanel.add(new JTextField(20), gbc);

        // gbc.gridx = 2;
        // detailsPanel.add(createLabel("Hospital Address :", false), gbc);
        // gbc.gridx = 3;
        // gbc.gridheight = 2;
        // detailsPanel.add(new JScrollPane(new JTextArea(3, 20)), gbc);
        // gbc.gridheight = 1;

        // --- ROW 5: EMail (L) ---
        // gbc.gridx = 0;
        // gbc.gridy = 5;
        // detailsPanel.add(createLabel("EMail :", false), gbc);
        // gbc.gridx = 1;
        // detailsPanel.add(new JTextField(20), gbc);

        // // --- ROW 6: Phone Resi (L) | Phone Hospital (R) ---
        // gbc.gridx = 0;
        // gbc.gridy = 6;
        // detailsPanel.add(createLabel("Phone Resi:", false), gbc);
        // gbc.gridx = 1;
        // detailsPanel.add(new JTextField(20), gbc);

        // gbc.gridx = 2;
        // detailsPanel.add(createLabel("Phone Hospital:", false), gbc);
        // gbc.gridx = 3;
        // detailsPanel.add(new JTextField(15), gbc);

        // --- ROW 7: Address (L) | Mobile (R) ---
        // gbc.gridx = 0;
        // gbc.gridy = 5;

        // gbc.gridx = 2;
        // detailsPanel.add(createLabel("Mobile :", true), gbc);
        // gbc.gridx = 3;
        // detailsPanel.add(new JTextField(12), gbc);

        // --- ROW 9: ID (L) | PRO Name (R) ---
        // gbc.gridx = 0;
        // gbc.gridy = 9;
        // detailsPanel.add(createLabel("ID:", false), gbc);
        // gbc.gridx = 1;
        // detailsPanel.add(new JTextField(15), gbc);

        // gbc.gridx = 2;
        // detailsPanel.add(createLabel("PRO Name :", false), gbc);
        // gbc.gridx = 3;
        // detailsPanel.add(new JComboBox<>(new String[] { "-", "PRO 1", "PRO 2" }),
        // gbc);

        // --- ROW 10: Degree (L) | Password (R) ---
        // gbc.gridx = 0;
        // gbc.gridy = 10;
        // detailsPanel.add(createLabel("Degree:", false), gbc);
        // gbc.gridx = 1;
        // detailsPanel.add(new JComboBox<>(new String[] { "-", "MBBS", "MD", "MS" }),
        // gbc);

        // gbc.gridx = 2;
        // detailsPanel.add(createLabel("Password :", false), gbc);
        // gbc.gridx = 3;
        // detailsPanel.add(new JTextField(15), gbc);

        // --- FOOTER: Checkboxes and Save ---
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setOpaque(false);
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 5, 5, 5);
        fgbc.anchor = GridBagConstraints.WEST;

        // fgbc.gridx = 0;
        // fgbc.gridy = 0;
        // JCheckBox cbReceipt = new JCheckBox("Show On Receipt/LabReport :", true);
        // cbReceipt.setOpaque(false);
        // footer.add(cbReceipt, fgbc);

        // fgbc.gridx = 1;
        // JCheckBox cbShare = new JCheckBox("Refer Share from Master :", true);
        // cbShare.setOpaque(false);
        // footer.add(cbShare, fgbc);

        // fgbc.gridx = 0;
        // fgbc.gridy = 1;
        // JCheckBox cbLock = new JCheckBox("Lock OnLine Report :", false);
        // cbLock.setOpaque(false);
        // footer.add(cbLock, fgbc);

        fgbc.gridx = 1;
        JButton btnSave = new JButton("Save");
        btnSave.setPreferredSize(new Dimension(80, 25));
        btnSave.addActionListener(e -> saveDoctorToDB());
        footer.add(btnSave, fgbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 4;
        detailsPanel.add(footer, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainContent.add(detailsPanel, gbc);

        add(new JScrollPane(mainContent), BorderLayout.CENTER);
    }

    private void saveDoctorToDB() {
        String title = (String) comboTitle.getSelectedItem();
        String name = txtDoctorName.getText().trim();
        String phone = txtPhone.getText().trim();
        String clinic = txtClinicName.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty()) {
            ErrorHandler.showWarning(this, "Please enter doctor name");
            return;
        }

        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO doctors (doctor_name, mobile, address, specialization) VALUES (?, ?, ?, ?)";
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, title + " " + name);
                pstmt.setString(2, phone);
                pstmt.setString(3, (clinic.isEmpty() ? "" : clinic + ", ") + address);
                pstmt.setString(4, (String) comboSpecialization.getSelectedItem());
                pstmt.executeUpdate();

                ErrorHandler.showInfo(this, "Doctor Registered Successfully!");
                dispose();
            }
        } catch (java.sql.SQLException e) {
            ErrorHandler.showError(this, "Error saving doctor", e);
        }
    }

    private JLabel createLabel(String text, boolean red) {
        JLabel lbl = new JLabel(text);
        if (red) {
            lbl.setForeground(new Color(200, 0, 0));
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
        } else {
            lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        }
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        return lbl;
    }

    private JPanel createDatePicker() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        p.setOpaque(false);

        JComboBox<String> day = new JComboBox<>();
        day.addItem("dd");
        for (int i = 1; i <= 31; i++)
            day.addItem(String.format("%02d", i));

        JComboBox<String> month = new JComboBox<>();
        month.addItem("mm");
        for (int i = 1; i <= 12; i++)
            month.addItem(String.format("%02d", i));

        JComboBox<String> year = new JComboBox<>();
        year.addItem("yyyy");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= 1940; i--)
            year.addItem(String.valueOf(i));

        p.add(day);
        p.add(month);
        p.add(year);

        return p;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> {
            new DoctorRegistrationFrame().setVisible(true);
        });
    }
}
