package com.ipsm;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHandler {

    /**
     * Shows a user-friendly error dialog with an option to see details.
     */
    public static void showError(java.awt.Component parent, String message, Exception e) {
        e.printStackTrace(); // Still log to console for developers

        String fullMessage = message;
        if (e != null && e.getMessage() != null) {
            fullMessage += "\n\nDetails: " + e.getMessage();
        }

        Object[] options = { "OK", "Show Details" };
        int selected = JOptionPane.showOptionDialog(
                parent,
                fullMessage,
                "Error Occurred",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);

        if (selected == 1) {
            showDetails(parent, e);
        }
    }

    private static void showDetails(java.awt.Component parent, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (e != null) {
            e.printStackTrace(pw);
        } else {
            pw.println("No exception details available.");
        }

        JTextArea textArea = new JTextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 400));

        JOptionPane.showMessageDialog(parent, scrollPane, "Error Stack Trace", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Standardized warning message
     */
    public static void showWarning(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Standardized info message
     */
    public static void showInfo(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
