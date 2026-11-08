package com.reservation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Reservation form - book a train ticket.
 */
public class ReservationFrame extends JFrame {

    private final MainMenuFrame parentMenu;

    private final JTextField nameField;
    private final JTextField trainNumberField;
    private final JTextField trainNameField;
    private final JComboBox<String> classCombo;
    private final JTextField dateField;
    private final JTextField sourceField;
    private final JTextField destField;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReservationFrame(MainMenuFrame parent) {
        this.parentMenu = parent;
        setTitle("Book Ticket - Reservation Form");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 248, 250));

        JLabel header = new JLabel("New Reservation", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(new Color(25, 55, 109));
        mainPanel.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Passenger Name
        addLabel(form, gbc, row, "Passenger Name *");
        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addField(form, gbc, row++, nameField);

        // Train Number
        addLabel(form, gbc, row, "Train Number *");
        trainNumberField = new JTextField(20);
        trainNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addField(form, gbc, row++, trainNumberField);

        // Train Name (auto)
        addLabel(form, gbc, row, "Train Name");
        trainNameField = new JTextField(20);
        trainNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        trainNameField.setEditable(false);
        trainNameField.setBackground(new Color(230, 230, 230));
        addField(form, gbc, row++, trainNameField);

        // Class
        addLabel(form, gbc, row, "Class Type *");
        classCombo = new JComboBox<>(new String[]{
                "Sleeper (SL)", "AC 3-Tier (3A)", "AC 2-Tier (2A)",
                "AC First Class (1A)", "Chair Car (CC)", "Second Sitting (2S)"
        });
        classCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addField(form, gbc, row++, classCombo);

        // Journey Date
        addLabel(form, gbc, row, "Date of Journey *");
        dateField = new JTextField(20);
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateField.setToolTipText("Format: yyyy-MM-dd  (e.g. 2026-12-25)");
        dateField.setText(LocalDate.now().plusDays(1).format(DATE_FMT)); // default tomorrow
        addField(form, gbc, row++, dateField);

        // Source
        addLabel(form, gbc, row, "Source Station *");
        sourceField = new JTextField(20);
        sourceField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addField(form, gbc, row++, sourceField);

        // Destination
        addLabel(form, gbc, row, "Destination Station *");
        destField = new JTextField(20);
        destField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addField(form, gbc, row++, destField);

        // Auto-populate train name & stations when train number changes
        trainNumberField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupTrain(); }
            public void removeUpdate(DocumentEvent e) { lookupTrain(); }
            public void changedUpdate(DocumentEvent e) { lookupTrain(); }
        });

        mainPanel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.setOpaque(false);

        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookBtn.setBackground(new Color(34, 139, 34));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setPreferredSize(new Dimension(140, 38));
        bookBtn.addActionListener(e -> bookTicket());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clearBtn.setPreferredSize(new Dimension(100, 38));
        clearBtn.addActionListener(e -> clearForm());

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setPreferredSize(new Dimension(130, 38));
        backBtn.addActionListener(e -> goBack());

        btnPanel.add(bookBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(backBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        // Help note
        JLabel note = new JLabel("<html><center>Sample train numbers: 12345, 12346, 12627, 12951, 12001 ...</center></html>");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        note.setForeground(Color.GRAY);
        note.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(btnPanel, BorderLayout.CENTER);
        south.add(note, BorderLayout.SOUTH);
        mainPanel.add(south, BorderLayout.SOUTH);

        add(mainPanel);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                goBack();
            }
        });
    }

    private void addLabel(JPanel form, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(lbl, gbc);
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, JComponent field) {
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        form.add(field, gbc);
    }

    private void lookupTrain() {
        String text = trainNumberField.getText().trim();
        if (text.isEmpty()) {
            trainNameField.setText("");
            sourceField.setText("");
            destField.setText("");
            return;
        }
        try {
            int num = Integer.parseInt(text);
            String name = DatabaseHelper.getTrainName(num);
            if (name != null) {
                trainNameField.setText(name);
                String[] stations = DatabaseHelper.getTrainStations(num);
                if (stations != null) {
                    sourceField.setText(stations[0]);
                    destField.setText(stations[1]);
                }
            } else {
                trainNameField.setText("(Train not found)");
                sourceField.setText("");
                destField.setText("");
            }
        } catch (NumberFormatException ex) {
            trainNameField.setText("");
        }
    }

    private void bookTicket() {
        // Validation
        String name = nameField.getText().trim();
        String trainNumStr = trainNumberField.getText().trim();
        String trainName = trainNameField.getText().trim();
        String classType = (String) classCombo.getSelectedItem();
        String dateStr = dateField.getText().trim();
        String source = sourceField.getText().trim();
        String dest = destField.getText().trim();

        if (name.isEmpty() || trainNumStr.isEmpty() || dateStr.isEmpty()
                || source.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all required fields (marked with *).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int trainNumber;
        try {
            trainNumber = Integer.parseInt(trainNumStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Train number must be a valid number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (trainName.isEmpty() || trainName.startsWith("(")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid train number from the database.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Date validation
        try {
            LocalDate journeyDate = LocalDate.parse(dateStr, DATE_FMT);
            if (journeyDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                        "Journey date cannot be in the past.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use yyyy-MM-dd (e.g. 2026-12-25).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (source.equalsIgnoreCase(dest)) {
            JOptionPane.showMessageDialog(this,
                    "Source and Destination stations cannot be the same.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Generate PNR and save
        String pnr = DatabaseHelper.generatePNR();
        boolean success = DatabaseHelper.bookTicket(
                pnr, name, trainNumber, trainName, classType, dateStr, source, dest);

        if (success) {
            String details = String.format("""
                    Booking Successful!

                    PNR Number     : %s
                    Passenger Name : %s
                    Train Number   : %d
                    Train Name     : %s
                    Class          : %s
                    Journey Date   : %s
                    From           : %s
                    To             : %s

                    Please note down your PNR for future reference / cancellation.
                    """, pnr, name, trainNumber, trainName, classType, dateStr, source, dest);

            JOptionPane.showMessageDialog(this, details,
                    "Reservation Confirmed", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to book ticket. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        trainNumberField.setText("");
        trainNameField.setText("");
        classCombo.setSelectedIndex(0);
        dateField.setText(LocalDate.now().plusDays(1).format(DATE_FMT));
        sourceField.setText("");
        destField.setText("");
        nameField.requestFocus();
    }

    private void goBack() {
        dispose();
        parentMenu.setVisible(true);
    }
}
