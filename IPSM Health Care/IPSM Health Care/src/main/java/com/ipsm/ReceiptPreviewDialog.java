package com.ipsm;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;

public class ReceiptPreviewDialog extends JDialog {
    private ReceiptPrinter.RegistrationData data;

    public ReceiptPreviewDialog(Frame owner, ReceiptPrinter.RegistrationData data) {
        super(owner, "Receipt Preview", true);
        this.data = data;

        setSize(500, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Preview Panel
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Define paper size in points
                double paperW = 595;
                double paperH = 842;

                // Calculate scale to fit in panel with padding
                double padding = 20;
                double scale = Math.min(
                        (getWidth() - 2 * padding) / paperW,
                        (getHeight() - 2 * padding) / paperH);

                // Center the paper
                double offsetX = (getWidth() - paperW * scale) / 2;
                double offsetY = (getHeight() - paperH * scale) / 2;

                g2d.translate(offsetX, offsetY);
                g2d.scale(scale, scale);

                // Draw Shadow
                g2d.setColor(new Color(50, 50, 50, 100));
                g2d.fillRect(5, 5, (int) paperW, (int) paperH);

                // Draw Paper
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, (int) paperW, (int) paperH);

                // Draw Receipt Content
                ReceiptPrinter printer = new ReceiptPrinter(data);
                PageFormat pf = new PageFormat();
                Paper paper = new Paper();
                paper.setSize(paperW, paperH);
                paper.setImageableArea(36, 36, paperW - 72, paperH - 72);
                pf.setPaper(paper);

                try {
                    printer.print(g2d, pf, 0);
                } catch (PrinterException e) {
                    System.err.println("Preview drawing error: " + e.getMessage());
                }
            }
        };
        previewPanel.setPreferredSize(new Dimension(550, 800));
        previewPanel.setBackground(new Color(80, 80, 80));
        add(new JScrollPane(previewPanel), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("Print Now");
        JButton btnCancel = new JButton("Close");

        btnPrint.addActionListener(e -> {
            ReceiptPrinter.printReceipt(data);
            dispose();
        });
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }
}
