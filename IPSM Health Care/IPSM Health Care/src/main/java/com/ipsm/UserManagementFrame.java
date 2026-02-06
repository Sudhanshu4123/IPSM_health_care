package com.ipsm;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementFrame extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole, comboStaffId;
    private java.util.Map<String, String> staffDeptMap = new java.util.HashMap<>();
    private JComboBox<String> cmbDepartment; // New Department Field
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;

    // Permission Checkboxes
    private JCheckBox chkRegNew, chkRegEdit, chkRegManage;
    private JCheckBox chkInvStatus, chkInvReprint, chkTestStatus;
    private JCheckBox chkRepOutstanding, chkRepSummary, chkRepLedger, chkRepBusiness, chkRepSales;

    // Track selected user for updates
    private String selectedUsername = null;

    public UserManagementFrame() {
        setTitle("User Management - Add Staff & Control Access");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        Main.setAppIcon(this);

        // --- Top: Input Form ---
        JPanel topPanel = new JPanel(new BorderLayout());

        // 1. User Details Panel
        JPanel userDetailsPanel = new JPanel(new GridBagLayout());
        userDetailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "User Details",
                TitledBorder.LEADING, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);

        // Password Visibility Toggle
        JToggleButton btnShowPass = new JToggleButton("👁");
        btnShowPass.setMargin(new Insets(0, 0, 0, 0));
        btnShowPass.setPreferredSize(new Dimension(30, 20)); // Small square-ish button
        btnShowPass.setFocusPainted(false);
        btnShowPass.addActionListener(e -> {
            if (btnShowPass.isSelected()) {
                txtPassword.setEchoChar((char) 0); // Show
            } else {
                txtPassword.setEchoChar('•'); // Hide
            }
        });

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.add(txtPassword, BorderLayout.CENTER);
        passPanel.add(btnShowPass, BorderLayout.EAST);
        passPanel.add(btnShowPass, BorderLayout.EAST);
        comboRole = new JComboBox<>(new String[] { "RECEPTIONIST", "DOCTOR", "ADMIN", "STAFF" });

        // Department ComboBox
        cmbDepartment = new JComboBox<>(new String[] { "None", "Dental", "Physiotherapy", "Pathology", "Radiology" });
        cmbDepartment.setEnabled(false); // Default disabled

        // Row 0: Staff ID Selector
        comboStaffId = new JComboBox<>();
        comboStaffId.addItem("--- Select Staff ---");
        loadStaffList();
        comboStaffId.addActionListener(e -> {
            String selected = (String) comboStaffId.getSelectedItem();
            if (selected != null && !"--- Select Staff ---".equals(selected)) {
                String id = selected.split(" - ")[0];
                String dept = staffDeptMap.get(id);
                if (dept != null) {
                    cmbDepartment.setSelectedItem(dept);
                }
                // Auto-fill username if empty
                if (txtUsername.getText().trim().isEmpty()) {
                    txtUsername.setText(selected.split(" - ")[1].toLowerCase().replace(" ", "."));
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        userDetailsPanel.add(new JLabel("Link Staff:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        userDetailsPanel.add(comboStaffId, gbc);
        gbc.gridwidth = 1;

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        userDetailsPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        userDetailsPanel.add(txtUsername, gbc);

        gbc.gridx = 2;
        userDetailsPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3;
        userDetailsPanel.add(passPanel, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        userDetailsPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        userDetailsPanel.add(comboRole, gbc);

        gbc.gridx = 2;
        userDetailsPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 3;
        userDetailsPanel.add(cmbDepartment, gbc);

        // 2. Permissions Panel
        JPanel permPanel = new JPanel(new GridBagLayout());
        permPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Permissions",
                TitledBorder.LEADING, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.BLACK));

        // Initialize Checkboxes
        chkRegNew = new JCheckBox("New Registration");
        chkRegEdit = new JCheckBox("Edit Patient");
        chkRegManage = new JCheckBox("Manage/Delete Patient");

        chkInvStatus = new JCheckBox("Invoice Status");
        chkInvReprint = new JCheckBox("Reprint Invoice");
        chkTestStatus = new JCheckBox("Update Test Status");

        chkRepOutstanding = new JCheckBox("Outstanding Report");
        chkRepSummary = new JCheckBox("Summary Report");
        chkRepLedger = new JCheckBox("Ledger Report");
        chkRepBusiness = new JCheckBox("Business Report");
        chkRepSales = new JCheckBox("Sales Report");

        // Layout Checkboxes
        GridBagConstraints pGbc = new GridBagConstraints();
        pGbc.insets = new Insets(2, 10, 2, 10);
        pGbc.anchor = GridBagConstraints.WEST;

        // Col 1: Registration
        pGbc.gridx = 0;
        pGbc.gridy = 0;
        permPanel.add(new JLabel("Registration:"), pGbc);
        pGbc.gridy++;
        permPanel.add(chkRegNew, pGbc);
        pGbc.gridy++;
        permPanel.add(chkRegEdit, pGbc);
        pGbc.gridy++;
        permPanel.add(chkRegManage, pGbc);

        // Col 2: Invoice
        pGbc.gridx = 1;
        pGbc.gridy = 0;
        permPanel.add(new JLabel("Invoice:"), pGbc);
        pGbc.gridy++;
        permPanel.add(chkInvStatus, pGbc);
        pGbc.gridy++;
        permPanel.add(chkInvReprint, pGbc);
        pGbc.gridy++;
        permPanel.add(chkTestStatus, pGbc);

        // Col 3: Reports
        pGbc.gridx = 2;
        pGbc.gridy = 0;
        permPanel.add(new JLabel("Reports:"), pGbc);
        pGbc.gridy++;
        permPanel.add(chkRepOutstanding, pGbc);
        pGbc.gridy++;
        permPanel.add(chkRepSummary, pGbc);
        pGbc.gridy++;
        permPanel.add(chkRepLedger, pGbc);
        pGbc.gridy++;
        permPanel.add(chkRepBusiness, pGbc);
        pGbc.gridy++;
        permPanel.add(chkRepSales, pGbc);

        topPanel.add(userDetailsPanel, BorderLayout.NORTH);
        topPanel.add(permPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAdd = new JButton("Add User");
        btnAdd.setBackground(new Color(46, 204, 113)); // Green
        btnAdd.setForeground(Color.BLACK);

        btnUpdate = new JButton("Update Selected User");
        btnUpdate.setBackground(new Color(52, 152, 219)); // Blue
        btnUpdate.setForeground(Color.BLACK);
        btnUpdate.setEnabled(false);

        JButton btnClear = new JButton("Clear / Cancel");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnClear);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- Center: User List Table ---
        // Added "Password" column and hidden "ID" column logic if needed, but here we
        // show it.
        String[] columns = { "ID", "Username", "Password", "Role", "Department", "Edit", "Manage", "Status",
                "Reprint", "Outstanding", "Summary", "Ledger", "Business", "Sales", "Test Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Hide complex permission columns if too wide, but user requested visibility.
        // We'll keep them but might need scrolling.
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Set some column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Password

        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // --- Bottom: Delete Button ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDelete = new JButton("Delete");
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.BLACK);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Events ---

        // Table Selection
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                loadUserToForm(userTable.getSelectedRow());
            }
        });

        // Add User
        btnAdd.addActionListener(e -> {
            if (validateForm()) {
                if (addUser()) {
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                    loadUsers();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding user (Username might exist).");
                }
            }
        });

        // Update User
        btnUpdate.addActionListener(e -> {
            if (selectedUsername == null) {
                JOptionPane.showMessageDialog(this, "No user selected.");
                return;
            }
            if (validateForm()) {
                if (updateUser()) {
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                    loadUsers();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating user.");
                }
            }
        });

        // Delete User
        btnDelete.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                String uName = (String) tableModel.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete user '" + uName + "'?", "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (deleteUser(uName)) {
                        loadUsers();
                        clearForm();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a user to delete.");
            }
        });

        // Clear
        btnClear.addActionListener(e -> clearForm());

        // Role Preset Logic
        comboRole.addActionListener(e -> applyRolePresets());

        loadUsers();
    }

    private void loadStaffList() {
        java.util.List<Object[]> staff = com.ipsm.db.DatabaseManager.getStaffDataForUserManagement();
        for (Object[] s : staff) {
            String entry = s[0] + " - " + s[1];
            comboStaffId.addItem(entry);
            staffDeptMap.put((String) s[0], (String) s[2]);
        }
    }

    private void applyRolePresets() {
        String role = (String) comboRole.getSelectedItem();

        // Enable Department for Doctor
        if ("DOCTOR".equals(role)) {
            cmbDepartment.setEnabled(true);
        } else {
            cmbDepartment.setEnabled(false);
            cmbDepartment.setSelectedIndex(0); // Reset to None
        }

        // Reset all
        // Only reset if NO user is selected (fresh entry), otherwise we might overwrite
        // loaded data
        if (selectedUsername == null) {
            boolean isAll = "ADMIN".equals(role);
            setAllPermissions(isAll);

            if ("RECEPTIONIST".equals(role)) {
                chkRegNew.setSelected(true);
                chkRegEdit.setSelected(true);
                chkInvStatus.setSelected(true);
                chkInvReprint.setSelected(true);
            } else if ("DOCTOR".equals(role)) {
                chkRegNew.setSelected(false);
                chkRegManage.setSelected(false);
                // Doctors might need report access
                chkRepSummary.setSelected(true);
                chkRepOutstanding.setSelected(true);
                chkTestStatus.setSelected(true); // Default enabled for Doctors
            }
        }
    }

    private void setAllPermissions(boolean state) {
        chkRegNew.setSelected(state);
        chkRegEdit.setSelected(state);
        chkRegManage.setSelected(state);
        chkInvStatus.setSelected(state);
        chkInvReprint.setSelected(state);
        chkTestStatus.setSelected(state);
        chkRepOutstanding.setSelected(state);
        chkRepSummary.setSelected(state);
        chkRepLedger.setSelected(state);
        chkRepBusiness.setSelected(state);
        chkRepSales.setSelected(state);
    }

    private boolean validateForm() {
        if (txtUsername.getText().trim().isEmpty() || new String(txtPassword.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password required.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        comboRole.setSelectedIndex(0);
        cmbDepartment.setSelectedIndex(0);
        cmbDepartment.setEnabled(false);
        setAllPermissions(false);
        userTable.clearSelection();
        selectedUsername = null;
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        txtUsername.setEditable(true);
        comboStaffId.setSelectedIndex(0);
    }

    private void loadUserToForm(int row) {
        if (tableModel.getColumnCount() <= 1)
            return;
        String username = (String) tableModel.getValueAt(row, 1);
        selectedUsername = username;
        txtUsername.setText(username);

        // Fetch full details from API or use the table data if sufficient
        // Actually, table data seems to have everything except maybe password (which we
        // don't show anyway)
        // Check loadUsers() - it populates all booleans.
        // So we can just read from table model for everything except password.
        // BUT, we need to know the Role/Dept.

        // Let's rely on the row data to fill the form, avoiding an extra API call.
        // The table has: ID, Username, Password (hidden/hash?), Role, Department, ...
        // permissions ...

        // Note: Password in table is likely the hash or hidden. We shouldn't show it in
        // the password field.
        // We leave password field empty to indicate "No Change".
        txtPassword.setText("");

        comboRole.setSelectedItem(tableModel.getValueAt(row, 3));
        String dept = (String) tableModel.getValueAt(row, 4);
        if (dept != null && !"null".equals(dept)) {
            cmbDepartment.setSelectedItem(dept);
        } else {
            cmbDepartment.setSelectedIndex(0);
        }

        // Permissions - Columns start at 5
        chkRegEdit.setSelected((Boolean) tableModel.getValueAt(row, 5));
        chkRegManage.setSelected((Boolean) tableModel.getValueAt(row, 6));
        chkInvStatus.setSelected((Boolean) tableModel.getValueAt(row, 7));
        chkInvReprint.setSelected((Boolean) tableModel.getValueAt(row, 8));
        chkRepOutstanding.setSelected((Boolean) tableModel.getValueAt(row, 9));
        chkRepSummary.setSelected((Boolean) tableModel.getValueAt(row, 10));
        chkRepLedger.setSelected((Boolean) tableModel.getValueAt(row, 11));
        chkRepBusiness.setSelected((Boolean) tableModel.getValueAt(row, 12));
        chkRepSales.setSelected((Boolean) tableModel.getValueAt(row, 13));
        chkTestStatus.setSelected((Boolean) tableModel.getValueAt(row, 14));

        // Missing chkRegNew in table?
        // Let's see loadUsers: tableModel has 15 columns.
        // Wait, the loadUsers Logic below populates data.

        // Actually, to be safe and accurate, let's fetch from API if we want perfect
        // sync.
        // But for speed, let's just implement loadUsers properly and use row data.
        // One missing column in table: New Registration.
        // The table model definition:
        // "ID", "Username", "Password", "Role", "Department", "Edit", "Manage",
        // "Status", ...
        // It seems "New Registration" (reg_new) is MISSING in the table columns!
        // So we DO need to fetch strictly or add it to table.
        // Adding to table is better.

        // However, I will implement API fetch just to be "deployment ready" robust.
        new SwingWorker<java.util.List<com.ipsm.model.User>, Void>() {
            @Override
            protected java.util.List<com.ipsm.model.User> doInBackground() throws Exception {
                return com.ipsm.api.ApiClient.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    java.util.List<com.ipsm.model.User> users = get();
                    for (com.ipsm.model.User u : users) {
                        if (u.getUsername().equals(selectedUsername)) {
                            // Populate Form
                            comboRole.setSelectedItem(u.getRole());
                            if (u.getDepartment() != null)
                                cmbDepartment.setSelectedItem(u.getDepartment());
                            else
                                cmbDepartment.setSelectedIndex(0);

                            chkRegNew.setSelected(u.isRegNew());
                            chkRegEdit.setSelected(u.isRegEdit());
                            chkRegManage.setSelected(u.isRegManage());
                            chkInvStatus.setSelected(u.isInvStatus());
                            chkInvReprint.setSelected(u.isInvReprint());
                            chkTestStatus.setSelected(u.isTestStatus());
                            chkRepOutstanding.setSelected(u.isRepOutstanding());
                            chkRepSummary.setSelected(u.isRepSummary());
                            chkRepLedger.setSelected(u.isRepLedger());
                            chkRepBusiness.setSelected(u.isRepBusiness());
                            chkRepSales.setSelected(u.isRepSales());
                            if (u.getStaffId() != null) {
                                for (int i = 0; i < comboStaffId.getItemCount(); i++) {
                                    if (comboStaffId.getItemAt(i).startsWith(u.getStaffId() + " - ")) {
                                        comboStaffId.setSelectedIndex(i);
                                        break;
                                    }
                                }
                            } else {
                                comboStaffId.setSelectedIndex(0);
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    ErrorHandler.showError(UserManagementFrame.this, "Failed to fetch user details", e);
                }
            }
        }.execute();

        txtUsername.setEditable(false);
        btnAdd.setEnabled(false);
        btnUpdate.setEnabled(true);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        // Use SwingWorker to fetch users asynchronously
        new SwingWorker<java.util.List<com.ipsm.model.User>, Void>() {
            @Override
            protected java.util.List<com.ipsm.model.User> doInBackground() throws Exception {
                return com.ipsm.api.ApiClient.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    java.util.List<com.ipsm.model.User> users = get();
                    for (com.ipsm.model.User u : users) {
                        tableModel.addRow(new Object[] {
                                0, // ID placeholder
                                u.getUsername(),
                                "********", // Hide Hash
                                u.getRole(),
                                u.getDepartment(),
                                u.isRegEdit(),
                                u.isRegManage(),
                                u.isInvStatus(),
                                u.isInvReprint(),
                                u.isRepOutstanding(),
                                u.isRepSummary(),
                                u.isRepLedger(),
                                u.isRepBusiness(),
                                u.isRepSales(),
                                u.isTestStatus()
                        });
                    }
                } catch (Exception ex) {
                    ErrorHandler.showError(UserManagementFrame.this, "Failed to load users", ex);
                }
            }
        }.execute();
    }

    private boolean addUser() {
        com.ipsm.model.User user = new com.ipsm.model.User();
        user.setUsername(txtUsername.getText().trim());
        user.setPassword(new String(txtPassword.getPassword())); // API will hash it
        user.setRole((String) comboRole.getSelectedItem());
        String dept = (String) cmbDepartment.getSelectedItem();
        user.setDepartment("None".equals(dept) ? null : dept);

        String selStaff = (String) comboStaffId.getSelectedItem();
        if (selStaff != null && !"--- Select Staff ---".equals(selStaff)) {
            user.setStaffId(selStaff.split(" - ")[0]);
        }

        user.setRegNew(chkRegNew.isSelected());
        user.setRegEdit(chkRegEdit.isSelected());
        user.setRegManage(chkRegManage.isSelected());
        user.setInvStatus(chkInvStatus.isSelected());
        user.setInvReprint(chkInvReprint.isSelected());
        user.setTestStatus(chkTestStatus.isSelected());
        user.setRepOutstanding(chkRepOutstanding.isSelected());
        user.setRepSummary(chkRepSummary.isSelected());
        user.setRepLedger(chkRepLedger.isSelected());
        user.setRepBusiness(chkRepBusiness.isSelected());
        user.setRepSales(chkRepSales.isSelected());

        try {
            boolean success = com.ipsm.api.ApiClient.addUser(user);
            return success;
        } catch (Exception e) {
            ErrorHandler.showError(this, "Network error while adding user", e);
            return false;
        }
    }

    private boolean updateUser() {
        com.ipsm.model.User user = new com.ipsm.model.User();
        // Username is from the fixed text field (which matches selectedUsername)
        user.setUsername(txtUsername.getText().trim());

        String pass = new String(txtPassword.getPassword());
        if (!pass.isEmpty()) {
            user.setPassword(pass); // Only send if changed
        } else {
            // If empty, backend might overwrite with null or empty string if we are not
            // careful.
            // We configured UserController to only update if not empty.
            user.setPassword("");
        }

        user.setRole((String) comboRole.getSelectedItem());
        String dept = (String) cmbDepartment.getSelectedItem();
        user.setDepartment("None".equals(dept) ? null : dept);

        String selStaff = (String) comboStaffId.getSelectedItem();
        if (selStaff != null && !"--- Select Staff ---".equals(selStaff)) {
            user.setStaffId(selStaff.split(" - ")[0]);
        }

        user.setRegNew(chkRegNew.isSelected());
        user.setRegEdit(chkRegEdit.isSelected());
        user.setRegManage(chkRegManage.isSelected());
        user.setInvStatus(chkInvStatus.isSelected());
        user.setInvReprint(chkInvReprint.isSelected());
        user.setTestStatus(chkTestStatus.isSelected());
        user.setRepOutstanding(chkRepOutstanding.isSelected());
        user.setRepSummary(chkRepSummary.isSelected());
        user.setRepLedger(chkRepLedger.isSelected());
        user.setRepBusiness(chkRepBusiness.isSelected());
        user.setRepSales(chkRepSales.isSelected());

        try {
            boolean success = com.ipsm.api.ApiClient.updateUser(user);
            return success;
        } catch (Exception e) {
            ErrorHandler.showError(this, "Network error while updating user", e);
            return false;
        }
    }

    private boolean deleteUser(String username) {
        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Cannot delete default admin.");
            return false;
        }
        try {
            boolean success = com.ipsm.api.ApiClient.deleteUser(username);
            return success;
        } catch (Exception e) {
            ErrorHandler.showError(this, "Network error while deleting user", e);
            return false;
        }
    }
}
