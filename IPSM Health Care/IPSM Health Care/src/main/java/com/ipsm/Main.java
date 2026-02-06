package com.ipsm;

import javax.swing.*;
import java.awt.*;
import com.ipsm.db.DatabaseManager;

public class Main {
    private static Image APP_ICON;

    static {
        try {
            java.net.URL iconUrl = Main.class.getResource("/com/ipsm/logo.png");
            if (iconUrl != null) {
                APP_ICON = new ImageIcon(iconUrl).getImage();
            }
        } catch (Exception e) {
            System.err.println("Could not load app icon: " + e.getMessage());
        }
    }

    public static void setAppIcon(JFrame frame) {
        if (APP_ICON != null) {
            frame.setIconImage(APP_ICON);
        }
    }

    public static void main(String[] args) {
        // Set Global Exception Handler for Swing
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught Exception in thread " + t.getName());
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                ErrorHandler.showError(null, "An unexpected error occurred in the application thread.", (Exception) e);
            });
        });

        // Apply Premium Look and Feel
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // Strict Connectivity Check
        // App will NOT open if the server is unreachable (Internet/API check)
        try {
            if (!com.ipsm.api.ApiClient.checkServerHealth()) {
                JOptionPane.showMessageDialog(null,
                        "Internet connection required.\nUnable to connect to the Server.\nPlease check your connection and restart the application.",
                        "Network Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } catch (Exception e) {
            ErrorHandler.showError(null, "Failed to perform connectivity check.", e);
            System.exit(1);
        }

        // Always initialize database to ensure tables and default admin user exist
        try {
            DatabaseManager.initializeDatabase();
        } catch (Exception e) {
            ErrorHandler.showError(null, "Failed to initialize database. Some features may not work.", e);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                ErrorHandler.showError(null, "Failed to launch Login screen.", e);
            }
        });
    }
}
