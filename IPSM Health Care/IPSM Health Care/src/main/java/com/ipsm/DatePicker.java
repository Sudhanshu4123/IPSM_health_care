package com.ipsm;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePicker extends JDialog {
    private int month;
    private int year;
    private JLabel l = new JLabel("", JLabel.CENTER);
    private String day = "";
    private JButton[] button = new JButton[49];

    public DatePicker(Window parent) {
        super(parent, "Select Date", ModalityType.APPLICATION_MODAL);

        Calendar cal = Calendar.getInstance();
        this.month = cal.get(Calendar.MONTH);
        this.year = cal.get(Calendar.YEAR);

        setLayout(new BorderLayout());
        setResizable(false);

        JPanel p1 = new JPanel(new GridLayout(7, 7));
        p1.setPreferredSize(new Dimension(400, 200));

        String[] header = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        for (int x = 0; x < button.length; x++) {
            final int selection = x;
            button[x] = new JButton();
            button[x].setFocusPainted(false);
            button[x].setBackground(Color.white);
            if (x > 6) {
                button[x].addActionListener(e -> {
                    day = button[selection].getActionCommand();
                    dispose();
                });
            }
            if (x < 7) {
                button[x].setText(header[x]);
                button[x].setForeground(new Color(150, 20, 20));
                button[x].setFont(new Font("Arial", Font.BOLD, 12));
                button[x].setEnabled(false);
                button[x].setBorder(null);
            }
            p1.add(button[x]);
        }

        JPanel p2 = new JPanel(new BorderLayout());
        JButton previous = new JButton("<<");
        previous.addActionListener(e -> {
            month--;
            displayDate();
        });
        JButton next = new JButton(">>");
        next.addActionListener(e -> {
            month++;
            displayDate();
        });

        l.setFont(new Font("Arial", Font.BOLD, 14));
        l.setPreferredSize(new Dimension(150, 30));

        p2.add(previous, BorderLayout.WEST);
        p2.add(l, BorderLayout.CENTER);
        p2.add(next, BorderLayout.EAST);
        p2.setBackground(new Color(230, 240, 255));

        add(p1, BorderLayout.CENTER);
        add(p2, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(parent);
        displayDate();
    }

    public void displayDate() {
        for (int x = 7; x < button.length; x++) {
            button[x].setText("");
            button[x].setEnabled(false);
            button[x].setBackground(Color.WHITE);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // Update variables in case of overflow/underflow
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar today = Calendar.getInstance();

        for (int x = 6 + dayOfWeek, d = 1; d <= daysInMonth; x++, d++) {
            button[x].setText("" + d);
            button[x].setActionCommand("" + d);
            button[x].setEnabled(true);

            if (d == today.get(Calendar.DAY_OF_MONTH) && month == today.get(Calendar.MONTH)
                    && year == today.get(Calendar.YEAR)) {
                button[x].setBackground(new Color(200, 230, 255));
                button[x].setFont(new Font("Arial", Font.BOLD, 12));
            } else {
                button[x].setBackground(Color.WHITE);
                button[x].setFont(new Font("Arial", Font.PLAIN, 12));
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        l.setText(sdf.format(cal.getTime()));
    }

    public String getPickedDate() {
        if (day.equals(""))
            return "";
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, Integer.parseInt(day));
        return new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
    }
}
