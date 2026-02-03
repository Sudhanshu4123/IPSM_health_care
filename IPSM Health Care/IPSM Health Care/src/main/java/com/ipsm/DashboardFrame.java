package com.ipsm;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private UserSession session;

    public DashboardFrame(UserSession session) {
        this.session = session;
        setTitle("SOLICITOUS WELLNESS PVT LTD - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setMinimumSize(new Dimension(1100, 700));
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 242, 245)); // Soft gray background

        // --- TOP BAR ---
        add(createTopBar(), BorderLayout.NORTH);

        // --- MAIN CONTENT AREA ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(240, 242, 245));
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Padding

        // 1. Navigation/Breadcrumb Strip (Optional, or just header)
        mainContent.add(createNavBar());
        mainContent.add(Box.createVerticalStrut(20));

        // 2. Welcome Message
        JLabel lblWelcomeBig = new JLabel("Welcome To SOLICITOUS WELLNESS PVT LTD", SwingConstants.CENTER);
        lblWelcomeBig.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcomeBig.setForeground(new Color(192, 57, 43)); // Deep Red
        lblWelcomeBig.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContent.add(lblWelcomeBig);

        // 5. Bottom Info Grid (Preserved)
        mainContent.add(Box.createVerticalStrut(40));
        mainContent.add(createInfoGrid());

        add(new JScrollPane(mainContent), BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        // Use BorderLayout to easily push things to edges
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(44, 62, 80)); // Dark Slate Blue
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        // Left Side: Welcome, Centre, Role
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        leftPanel.setOpaque(false);

        JLabel lblWelcome = new JLabel("Welcome: SOLICITOUS WELLNESS PVT LTD");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblCentre = new JLabel("Centre:");
        lblCentre.setForeground(new Color(200, 200, 200));

        JComboBox<String> comboCentre = new JComboBox<>(new String[] { "SOLICITOUS WELLNESS PVT LTD" });
        comboCentre.setBackground(Color.WHITE);

        JCheckBox chkArchive = new JCheckBox("Archive Data");
        chkArchive.setBackground(new Color(44, 62, 80));
        chkArchive.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel("Role: " + (session.getRole() != null ? session.getRole() : "FRANCHISEE"));
        lblRole.setForeground(Color.WHITE);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));

        leftPanel.add(lblWelcome);
        leftPanel.add(lblCentre);
        leftPanel.add(comboCentre);
        leftPanel.add(chkArchive);
        leftPanel.add(lblRole);

        // Right Side: Logout Button and Admin Tools
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        if ("ADMIN".equalsIgnoreCase(session.getRole())) {
            JButton btnUsers = new JButton("Manage Users");
            btnUsers.setBackground(new Color(52, 152, 219));
            btnUsers.setForeground(Color.BLACK);
            btnUsers.setFocusPainted(false);
            btnUsers.addActionListener(e -> new UserManagementFrame().setVisible(true));
            rightPanel.add(btnUsers);
        }

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Do you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
        rightPanel.add(btnLogout);

        // Add to main bar
        p.add(leftPanel, BorderLayout.CENTER);
        p.add(rightPanel, BorderLayout.EAST);

        return p;
    }

    private JPanel createNavBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        String[] menus = { "Registration", "Patient Report", "Invoice", "Payment Details", "Reports" };
        for (String m : menus) {
            // High-level check: if user has ANY permission in this category, show the menu
            if (!hasCategoryAccess(m)) {
                continue;
            }

            JButton btn = new JButton(m);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setForeground(new Color(50, 50, 50));
            btn.setBackground(Color.WHITE);
            // Reduced padding inside buttons
            btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setForeground(new Color(0, 102, 204));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setForeground(new Color(50, 50, 50));
                }
            });
            btn.addActionListener(e -> handleMenuClick(m, btn));
            p.add(btn);
        }
        return p;
    }

    private boolean hasCategoryAccess(String category) {
        if ("ADMIN".equalsIgnoreCase(session.getRole()))
            return true;

        switch (category) {
            case "Registration":
                return session.canRegNew() || session.canRegEdit() || session.canRegManage();
            case "Invoice":
                return session.canInvStatus() || session.canInvReprint();
            case "Reports":
                return session.canRepOutstanding() || session.canRepSummary() ||
                        session.canRepLedger() || session.canRepBusiness() || session.canRepSales();
            case "Patient Report":
                // Anyone with report access or Doctor role
                return session.canRepSummary() || "DOCTOR".equalsIgnoreCase(session.getRole());
            case "Payment Details":
                return "ADMIN".equalsIgnoreCase(session.getRole()) || session.canRepBusiness();
            default:
                return false;
        }
    }

    private JPanel createInfoGrid() {
        JPanel p = new JPanel(new GridLayout(1, 2, 30, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));

        // Left Box: Login Info
        JPanel leftBox = new JPanel(new GridLayout(5, 1, 5, 5));
        leftBox.setBackground(new Color(240, 255, 230));
        leftBox.setBorder(new LineBorder(new Color(150, 200, 150), 1));

        leftBox.add(new JLabel("  User: " + session.getUsername()));
        leftBox.add(new JLabel("  Role: " + session.getRole()));
        // Placeholder info
        leftBox.add(new JLabel("  Login Time: " + java.time.LocalDateTime.now()));

        // Right Box: News
        JPanel rightBox = new JPanel(new BorderLayout());
        rightBox.setBackground(new Color(240, 255, 230));
        rightBox.setBorder(new LineBorder(new Color(150, 200, 150), 1));

        JLabel newsHeader = new JLabel(" News:", SwingConstants.LEFT);
        newsHeader.setForeground(new Color(150, 0, 0));
        newsHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel newsContent = new JLabel("Labcorp Diagnostics Pvt Ltd-LDPL", SwingConstants.CENTER);

        rightBox.add(newsHeader, BorderLayout.NORTH);
        rightBox.add(newsContent, BorderLayout.CENTER);

        p.add(leftBox);
        p.add(rightBox);
        return p;
    }

    private void handleMenuClick(String menuName, JComponent source) {
        switch (menuName) {
            case "Registration":
                showRegistrationPopup(source);
                break;
            case "Patient Report":
                openPatientReport();
                break;
            case "Invoice":
                showInvoicePopup(source);
                break;
            case "Payment Details":
                new PaymentDetailsFrame().setVisible(true);
                break;
            case "Reports":
                showReportsPopup(source);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Opening " + menuName);
        }
    }

    private void showReportsPopup(JComponent source) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(Color.WHITE);

        // Define items and their specific permission checks
        addMenuItemIfAuthorized(popup, "Back Date Outstanding Report I", session.canRepOutstanding(),
                () -> new BackDateOutstandingFrame().setVisible(true));
        addMenuItemIfAuthorized(popup, "Invoice Month Wise Summary Report", session.canRepSummary(),
                () -> new InvoiceMonthWiseReportFrame().setVisible(true));
        addMenuItemIfAuthorized(popup, "Ledger Transaction", session.canRepLedger(),
                () -> new LedgerTransactionFrame().setVisible(true));
        addMenuItemIfAuthorized(popup, "Sales Business Report", session.canRepBusiness(),
                () -> new SalesBusinessReportFrame().setVisible(true));
        addMenuItemIfAuthorized(popup, "Sales Report", session.canRepSales(),
                () -> new SalesReportFrame().setVisible(true));

        popup.show(source, 0, source.getHeight());
    }

    private void showRegistrationPopup(JComponent source) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(Color.WHITE);

        addMenuItemIfAuthorized(popup, "New Registration", session.canRegNew(), () -> openRegistration());
        addMenuItemIfAuthorized(popup, "Edit Patient", session.canRegEdit(),
                () -> JOptionPane.showMessageDialog(this, "Edit Patient functionality coming soon."));
        addMenuItemIfAuthorized(popup, "Manage Patient", session.canRegManage(),
                () -> new PatientManagementFrame(session).setVisible(true));

        popup.show(source, 0, source.getHeight() + 5);
    }

    private void showInvoicePopup(JComponent source) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(Color.WHITE);

        addMenuItemIfAuthorized(popup, "Invoice Reprint", session.canInvReprint(), () -> openInvoice());
        addMenuItemIfAuthorized(popup, "Invoice Status", session.canInvStatus(),
                () -> JOptionPane.showMessageDialog(this, "Invoice Status functionality coming soon."));

        popup.show(source, 0, source.getHeight());
    }

    // Helper to add menu items conditionally
    private void addMenuItemIfAuthorized(JPopupMenu popup, String label, boolean condition, Runnable action) {
        if ("ADMIN".equalsIgnoreCase(session.getRole()) || condition) {
            JMenuItem item = new JMenuItem(label);
            item.setForeground(new Color(150, 0, 0));
            item.setBackground(Color.WHITE);
            item.addActionListener(e -> action.run());
            popup.add(item);
        }
    }

    public void openInvoice() {
        new InvoiceFrame().setVisible(true);
    }

    public void openPatientReport() {
        JOptionPane.showMessageDialog(this, "Fetching Patient Reports...");
    }

    public void openRegistration() {
        new RegistrationFrame().setVisible(true);
    }

    public void openReports() {
        JOptionPane.showMessageDialog(this, "Generating Business Reports...");
    }

    public void showTickets() {
        JOptionPane.showMessageDialog(this, "Support Tickets Loaded.");
    }
}
