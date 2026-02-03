package com.ipsm;

import javax.swing.*;
import java.awt.*;
import com.ipsm.db.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        // Apply Premium Look and Feel
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
            e.printStackTrace();
        }

        // Strict Connectivity Check
        // App will NOT open if the server is unreachable (Internet/API check)
        if (!com.ipsm.api.ApiClient.checkServerHealth()) {
            JOptionPane.showMessageDialog(null,
                    "Internet connection required.\nUnable to connect to the Server.\nPlease check your connection and restart the application.",
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Always initialize database to ensure tables and default admin user exist
        try {
            DatabaseManager.initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            // Continue to login even if DB init has minor issues, or handle fatal error?
            // Current initializeDatabase swallows errors.
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
