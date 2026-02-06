package com.ipsm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;
// import com.ipsm.db.DatabaseManager; // Removed as we use API now

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JButton loginButton;
    private boolean isPasswordVisible = false;

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        Main.setAppIcon(this);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        setContentPane(mainPanel);

        // 1. Welcome Title
        JLabel titleLabel = new JLabel("Welcome");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(51, 51, 51)); // Dark Gray
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(60));

        // 2. Username Section
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.BLACK);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));
        usernameField.setBackground(Color.WHITE);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(Color.WHITE);
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(usernameField);

        mainPanel.add(userPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // 3. Password Section
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(Color.BLACK);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password container to hold field and eye icon
        JPanel passContainer = new JPanel(new BorderLayout());
        passContainer.setBackground(Color.WHITE);
        passContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));
        passContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBorder(null); // No border for the field itself
        passwordField.setBackground(Color.WHITE);

        // Eye Icon Label
        JLabel eyeIcon = new JLabel(formatEyeIcon(false));
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    passwordField.setEchoChar((char) 0);
                    eyeIcon.setText(formatEyeIcon(true));
                } else {
                    passwordField.setEchoChar('•');
                    eyeIcon.setText(formatEyeIcon(false));
                }
            }
        });

        passContainer.add(passwordField, BorderLayout.CENTER);
        passContainer.add(eyeIcon, BorderLayout.EAST);

        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(Color.WHITE);
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passPanel.add(passLabel);
        passPanel.add(Box.createVerticalStrut(5));
        passPanel.add(passContainer);

        mainPanel.add(passPanel);
        // Forgot Password Link
        JLabel forgotPassAndSignup = new JLabel(
                "<html><a href='#' style='text-decoration:none; color: gray;'>Forgot Password?</a></html>");
        forgotPassAndSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassAndSignup.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPassAndSignup.setBorder(new EmptyBorder(5, 0, 0, 0)); // Padding top
        // Align to right side
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        linkPanel.setBackground(Color.WHITE);
        linkPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        linkPanel.add(forgotPassAndSignup);
        forgotPassAndSignup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Change Password Logic (as requested: Old Password + New Password in one box)
                JDialog dialog = new JDialog(LoginFrame.this, "Change Password", true);
                dialog.setLayout(new GridBagLayout());
                dialog.setSize(350, 250);
                dialog.setLocationRelativeTo(LoginFrame.this);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Fields
                JTextField txtUser = new JTextField(15);
                JPasswordField txtOldPass = new JPasswordField(15);
                JPasswordField txtNewPass = new JPasswordField(15);

                gbc.gridx = 0;
                gbc.gridy = 0;
                dialog.add(new JLabel("Username:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtUser, gbc);

                // Auto-fill username if provided in login field
                if (!usernameField.getText().trim().isEmpty()) {
                    txtUser.setText(usernameField.getText().trim());
                }

                gbc.gridx = 0;
                gbc.gridy = 1;
                dialog.add(new JLabel("Old Password:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtOldPass, gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                dialog.add(new JLabel("New Password:"), gbc);
                gbc.gridx = 1;
                dialog.add(txtNewPass, gbc);

                JButton btnUpdate = new JButton("Update Password");
                btnUpdate.setBackground(new Color(0, 102, 204));
                btnUpdate.setForeground(Color.BLACK); // Make text visible

                btnUpdate.addActionListener(al -> {
                    String user = txtUser.getText().trim();
                    String oldP = new String(txtOldPass.getPassword());
                    String newP = new String(txtNewPass.getPassword());

                    if (user.isEmpty() || oldP.isEmpty() || newP.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "All fields are required.");
                        return;
                    }

                    // Use API to change password securely
                    try {
                        boolean success = com.ipsm.api.ApiClient.changePassword(user, oldP, newP);
                        if (success) {
                            JOptionPane.showMessageDialog(dialog, "Password updated successfully!");
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Invalid Username or Old Password.");
                        }
                    } catch (Exception ex) {
                        ErrorHandler.showError(dialog, "Error connecting to server", ex);
                    }
                });

                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.gridwidth = 2;
                dialog.add(btnUpdate, gbc);

                dialog.setVisible(true);
            }
        });

        mainPanel.add(linkPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // 4. Gradient Login Button
        loginButton = new GradientButton("LOGIN");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());

        mainPanel.add(loginButton);

        // Message Label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(messageLabel);
    }

    private String formatEyeIcon(boolean visible) {
        // Simple unicode representation for the eye icon
        return visible ? "<html><font size='5'>👁</font></html>"
                : "<html><font size='5' color='gray'>\uD83D\uDC41</font></html>";
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        loginButton.setEnabled(false); // Assume loginButton is accessible or make it field level
        messageLabel.setText("Logging in...");
        messageLabel.setForeground(Color.BLUE);

        // Perform network request on background thread
        new SwingWorker<UserSession, Void>() {
            @Override
            protected UserSession doInBackground() throws Exception {
                return com.ipsm.api.ApiClient.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    UserSession session = get();
                    if (session != null) {
                        new DashboardFrame(session).setVisible(true);
                        LoginFrame.this.dispose();
                    }
                } catch (Exception e) {
                    // Extract execution exception cause if present
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    ErrorHandler.showError(LoginFrame.this, "Login Connection Error", (Exception) cause);

                    String msg = cause.getMessage();

                    // Handle specific connection errors for "No Internet"
                    if (cause instanceof java.net.UnknownHostException ||
                            msg != null && msg.contains("UnknownHostException")) {
                        messageLabel.setText("No Internet or Invalid Server IP");
                    } else if (cause instanceof java.net.ConnectException ||
                            msg != null && msg.contains("Connection refused")) {
                        messageLabel.setText("Server unreachable. Check internet/server status.");
                    } else if (msg != null && (msg.contains("401") || msg.contains("405"))) { // Unauthorized or
                                                                                              // redirected error
                        messageLabel.setText("Invalid Username or Password");
                    } else if (msg != null && (msg.contains("timeout") || msg.contains("Timed out"))) {
                        messageLabel.setText("Connection timed out. Check your internet.");
                    } else {
                        messageLabel.setText("Error: " + (msg != null ? msg : "Contact Admin"));
                    }
                    messageLabel.setForeground(Color.RED);
                } finally {
                    loginButton.setEnabled(true);
                }
            }
        }.execute();
    }

    // Custom Gradient Button Class
    private class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Gradient from Cyan to Purple
            Color color1 = new Color(0, 200, 255); // Cyan
            Color color2 = new Color(160, 50, 255); // Purple
            GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), 0, color2);
            g2d.setPaint(gp);

            // Fully rounded pill shape
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);

            super.paintComponent(g);
        }
    }
}
