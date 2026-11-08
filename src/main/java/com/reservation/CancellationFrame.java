package com.reservation;

import javax.swing.*;
import java.awt.*;

/**
 * Cancellation form - fetch booking by PNR and cancel it.
 */
public class CancellationFrame extends JFrame {

    private final MainMenuFrame parentMenu;
    private final JTextField pnrField;
    private final JTextArea detailsArea;
    private final JButton confirmCancelBtn;

    private DatabaseHelper.Reservation currentReservation;

    public CancellationFrame(MainMenuFrame parent) {
        this.parentMenu = parent;
        setTitle("Cancel Booking - by PNR");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 248, 250));

        JLabel header = new JLabel("Cancel Reservation", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(new Color(25, 55, 109));
        mainPanel.add(header, BorderLayout.NORTH);

        // Top: PNR input + Fetch
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setOpaque(false);

        JLabel pnrLabel = new JLabel("PNR Number:");
        pnrLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(pnrLabel);

        pnrField = new JTextField(16);
        pnrField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(pnrField);

        JButton fetchBtn = new JButton("Fetch Details");
        fetchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fetchBtn.setBackground(new Color(25, 55, 109));
        fetchBtn.setForeground(Color.WHITE);
        fetchBtn.setFocusPainted(false);
        fetchBtn.addActionListener(e -> fetchDetails());
        topPanel.add(fetchBtn);

        // Details area
        detailsArea = new JTextArea(12, 40);
        detailsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(false);
        detailsArea.setBackground(new Color(255, 255, 240));
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 180)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        detailsArea.setText("Enter a PNR number and click 'Fetch Details' to view booking.");

        JScrollPane scroll = new JScrollPane(detailsArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Booking Details"));

        // Center content
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);
        center.add(topPanel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        mainPanel.add(center, BorderLayout.CENTER);

        // Bottom buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.setOpaque(false);

        confirmCancelBtn = new JButton("Confirm Cancellation");
        confirmCancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmCancelBtn.setBackground(new Color(178, 34, 34));
        confirmCancelBtn.setForeground(Color.WHITE);
        confirmCancelBtn.setFocusPainted(false);
        confirmCancelBtn.setPreferredSize(new Dimension(190, 38));
        confirmCancelBtn.setEnabled(false);
        confirmCancelBtn.addActionListener(e -> confirmCancellation());

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setPreferredSize(new Dimension(130, 38));
        backBtn.addActionListener(e -> goBack());

        btnPanel.add(confirmCancelBtn);
        btnPanel.add(backBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Enter key on PNR field
        pnrField.addActionListener(e -> fetchDetails());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                goBack();
            }
        });
    }

    private void fetchDetails() {
        String pnr = pnrField.getText().trim();
        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a PNR number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentReservation = DatabaseHelper.getReservationByPNR(pnr);
        if (currentReservation == null) {
            detailsArea.setText("No booking found for PNR: " + pnr + "\n\nPlease check the PNR and try again.");
            confirmCancelBtn.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "No reservation found with the given PNR.",
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            detailsArea.setText(currentReservation.toString());
            confirmCancelBtn.setEnabled(true);
        }
    }

    private void confirmCancellation() {
        if (currentReservation == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this booking?\n\n" +
                        "PNR: " + currentReservation.pnr + "\n" +
                        "Passenger: " + currentReservation.passengerName + "\n" +
                        "Train: " + currentReservation.trainName + " (" + currentReservation.trainNumber + ")\n\n" +
                        "This action cannot be undone.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean success = DatabaseHelper.cancelReservation(currentReservation.pnr);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Booking with PNR " + currentReservation.pnr + " has been cancelled successfully.",
                        "Cancellation Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                detailsArea.setText("Booking cancelled.\nEnter another PNR to continue.");
                pnrField.setText("");
                confirmCancelBtn.setEnabled(false);
                currentReservation = null;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to cancel the booking. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void goBack() {
        dispose();
        parentMenu.setVisible(true);
    }
}
