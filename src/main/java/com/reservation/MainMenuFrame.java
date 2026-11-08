package com.reservation;

import javax.swing.*;
import java.awt.*;

/**
 * Main menu after successful login.
 */
public class MainMenuFrame extends JFrame {

    private final String loggedInUser;

    public MainMenuFrame(String username) {
        this.loggedInUser = username;
        setTitle("Train Reservation System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(245, 248, 250));

        // Header
        JLabel header = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(new Color(25, 55, 109));
        mainPanel.add(header, BorderLayout.NORTH);

        // Buttons panel
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 0, 18));
        btnPanel.setOpaque(false);

        JButton bookBtn = createMenuButton("Book Ticket / Reservation", new Color(34, 139, 34));
        bookBtn.addActionListener(e -> {
            ReservationFrame frame = new ReservationFrame(this);
            frame.setVisible(true);
            setVisible(false);
        });

        JButton cancelBtn = createMenuButton("Cancel Booking (by PNR)", new Color(178, 34, 34));
        cancelBtn.addActionListener(e -> {
            CancellationFrame frame = new CancellationFrame(this);
            frame.setVisible(true);
            setVisible(false);
        });

        JButton logoutBtn = createMenuButton("Logout", new Color(70, 70, 70));
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        });

        btnPanel.add(bookBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(logoutBtn);

        mainPanel.add(btnPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("Train Reservation System  |  SQLite + Swing", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(Color.GRAY);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
