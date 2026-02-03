package com.ipsm;

import java.awt.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReceiptPrinter implements Printable {
    private RegistrationData data;

    public ReceiptPrinter(RegistrationData data) {
        this.data = data;
    }

    public static void printReceipt(RegistrationData data) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new ReceiptPrinter(data));

        // Show print dialog to user
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        int width = (int) pageFormat.getImageableWidth();
        int height = (int) pageFormat.getImageableHeight();
        int y = 5;

        // --- Watermark ---
        g2d.setColor(new Color(245, 245, 245));
        g2d.setFont(new Font("Serif", Font.BOLD, 100));
        g2d.rotate(Math.toRadians(-30), width / 2, height / 2);
        g2d.drawString("IPSM", width / 2 - 120, height / 2 + 30);
        g2d.rotate(Math.toRadians(30), width / 2, height / 2);

        // --- Header Section ---
        Color themeColor = new Color(110, 19, 103); // Deep Purple from image
        g2d.setColor(themeColor);
        g2d.fillRect(0, y, width, 5); // Thin top border
        y += 10;

        // Hospital Logo
        try {
            java.io.File logoFile = new java.io.File("Untitled design.png");
            if (logoFile.exists()) {
                Image logo = javax.imageio.ImageIO.read(logoFile);
                g2d.drawImage(logo, 5, y + 5, 45, 45, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Hospital Name
        g2d.setColor(themeColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.drawString("IPSM Health Care", 55, y + 25);

        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("In Collaboration With Solicitous Wellness Pvt. Ltd.", 55, y + 40);

        y += 50;
        g2d.drawLine(0, y, width, y);
        y += 15;

        // --- Receipt Info ---
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("Receipt No: " + data.receiptNo, 10, y);
        g2d.drawString("Date: " + data.date, width / 2 - 40, y);
        g2d.drawString("Time: " + data.time, width - 110, y);
        y += 15;
        g2d.drawLine(0, y, width, y);
        y += 15;

        // --- Registration Info ---
        g2d.drawString("Date: " + data.date, 10, y);
        g2d.drawString("Srl No. " + String.format("%04d", Integer.parseInt(data.receiptNo) % 10000), width / 2 - 40, y);
        g2d.drawString("Patient ID: " + data.patientId, width - 110, y);
        y += 15;

        g2d.drawString("Name: " + data.patientName, 10, y);
        g2d.drawString("Age: " + data.age, width / 2 - 40, y);
        g2d.drawString("Sex: " + data.sex, width - 110, y);
        y += 15;

        g2d.drawString("Ref.By: " + data.refBy, 10, y);
        y += 10;

        g2d.setColor(themeColor);
        g2d.drawLine(0, y, width, y);
        y += 15;

        // --- Table Header ---
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("Sr.", 10, y);
        g2d.drawString("Particular", 50, y);
        g2d.drawString("Charges (Rs)", width - 110, y);
        y += 5;
        g2d.drawLine(0, y, width, y);
        y += 15;

        // --- Table Body ---
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        int sr = 1;
        for (String[] item : data.items) {
            g2d.drawString(String.format("%02d", sr++), 10, y);

            String particular = item[0];
            if (particular.length() > 50) {
                g2d.drawString(particular.substring(0, 50), 50, y);
                y += 12;
                g2d.drawString(particular.substring(50), 50, y);
            } else {
                g2d.drawString(particular, 50, y);
            }

            g2d.drawString(item[1] + "/-", width - 110, y);
            y += 15;
        }

        // Summary Position
        y = height / 2 - 50;

        // --- Summary Section ---
        g2d.drawLine(0, y, width, y);
        y += 15;
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("Total", 10, y);
        g2d.drawString(data.total + "/-", width - 110, y);
        y += 15;

        double discPercent = (data.total > 0) ? (data.discount / data.total) * 100 : 0;
        g2d.drawString("Discount of " + String.format("%.0f", discPercent) + "%", 10, y);
        g2d.drawString(data.discount + "/-", width - 110, y);
        y += 15;

        g2d.drawLine(0, y, width, y);
        y += 15;
        g2d.drawString("Amount to be Paid", 10, y);
        g2d.drawString(data.netAmount + "/-", width - 110, y);
        y += 25;

        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        g2d.drawString("Received with thanks a sum of Rs. " + data.netAmount + "/- From Mr. " + data.patientName
                + " By " + data.paymentMode + ".", 10, y);

        // --- Slogan ---
        y += 60;
        g2d.setColor(new Color(180, 180, 180));
        // Use Nirmala UI (standard Windows Hindi font) or Dialog as fallback
        g2d.setFont(new Font("Nirmala UI", Font.BOLD, 20));
        String slogan1 = "सर्वे सन्तु निरामया:";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(slogan1, (width - fm.stringWidth(slogan1)) / 2, y);
        y += 20;
        g2d.setFont(new Font("Dialog", Font.ITALIC, 16));
        String slogan2 = "May all be free from illness";
        fm = g2d.getFontMetrics();
        g2d.drawString(slogan2, (width - fm.stringWidth(slogan2)) / 2, y);

        // --- Footer ---
        y = height - 70;
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 8));
        String medicolegal = "NOT FOR MEDICO-LEGAL PURPOSE, KINDLY CO-RELATE CLINICALLY";
        fm = g2d.getFontMetrics();
        g2d.drawString(medicolegal, (width - fm.stringWidth(medicolegal)) / 2, y);

        y += 10;
        g2d.setColor(themeColor);
        g2d.fillRect(0, y, width, 55);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        String address = "Address: A-86, Mohan Garden, Gurudwara Road, Uttam Nagar, New Delhi-110059";
        String contact = "www.solicitousindia.in | solicitousindia@gmail.com | Mob: 8178375671, 7632970231";
        String branches = "Our Branches: Dwarka | Uttam Nagar | Gurugram | Rajasthan | Odisha";

        fm = g2d.getFontMetrics();
        g2d.drawString(address, (width - fm.stringWidth(address)) / 2, y + 15);
        g2d.drawString(contact, (width - fm.stringWidth(contact)) / 2, y + 30);
        g2d.drawString(branches, (width - fm.stringWidth(branches)) / 2, y + 45);

        return PAGE_EXISTS;
    }

    public static class RegistrationData {
        public String receiptNo;
        public String date;
        public String time;
        public String patientId;
        public String patientName;
        public String age;
        public String sex;
        public String refBy;
        public List<String[]> items;
        public double total;
        public double discount;
        public double netAmount;
        public String paymentMode;
    }
}
