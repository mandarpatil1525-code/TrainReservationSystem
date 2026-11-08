package com.reservation;

import javax.swing.*;

/**
 * Entry point of the Train Reservation System.
 */
public class Main {

    public static void main(String[] args) {
        // Use system look and feel for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default
        }

        // Ensure database is ready (static init in DatabaseHelper)
        // Then launch login on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
