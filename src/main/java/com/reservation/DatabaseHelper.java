package com.reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the Train Reservation System.
 * Uses SQLite for simplicity (no separate server required).
 */
public class DatabaseHelper {

    private static final String DB_URL;

    static {
        java.io.File dbDir = new java.io.File("db");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        DB_URL = "jdbc:sqlite:db/reservation.db";
        try {
            Class.forName("org.sqlite.JDBC");
            initDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA journal_mode=WAL;");
            s.execute("PRAGMA synchronous=NORMAL;");
        } catch (SQLException ignored) {
            // continue with defaults if PRAGMA fails
        }
        return conn;
    }

    /** Creates tables and seeds sample data if they don't exist. */
    private static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                )
            """);

            // Trains table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS trains (
                    train_number INTEGER PRIMARY KEY,
                    train_name TEXT NOT NULL,
                    source TEXT NOT NULL,
                    destination TEXT NOT NULL
                )
            """);

            // Reservations table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reservations (
                    pnr TEXT PRIMARY KEY,
                    passenger_name TEXT NOT NULL,
                    train_number INTEGER NOT NULL,
                    train_name TEXT NOT NULL,
                    class_type TEXT NOT NULL,
                    journey_date TEXT NOT NULL,
                    source_station TEXT NOT NULL,
                    destination_station TEXT NOT NULL,
                    booked_at TEXT DEFAULT (datetime('now','localtime')),
                    FOREIGN KEY (train_number) REFERENCES trains(train_number)
                )
            """);

            // Seed default user if empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM users");
            if (rs.next() && rs.getInt("cnt") == 0) {
                stmt.execute("INSERT INTO users (username, password) VALUES ('admin', 'admin123')");
                stmt.execute("INSERT INTO users (username, password) VALUES ('user', 'user123')");
            }
            rs.close();

            // Seed sample trains if empty
            rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM trains");
            if (rs.next() && rs.getInt("cnt") == 0) {
                String[] trains = {
                    "INSERT INTO trains VALUES (12345, 'Rajdhani Express', 'New Delhi', 'Mumbai')",
                    "INSERT INTO trains VALUES (12346, 'Shatabdi Express', 'New Delhi', 'Chandigarh')",
                    "INSERT INTO trains VALUES (12627, 'Karnataka Express', 'New Delhi', 'Bengaluru')",
                    "INSERT INTO trains VALUES (12951, 'Mumbai Rajdhani', 'Mumbai', 'New Delhi')",
                    "INSERT INTO trains VALUES (12001, 'Bhopal Shatabdi', 'New Delhi', 'Bhopal')",
                    "INSERT INTO trains VALUES (12841, 'Coromandel Express', 'Chennai', 'Howrah')",
                    "INSERT INTO trains VALUES (12245, 'Duronto Express', 'Howrah', 'Pune')",
                    "INSERT INTO trains VALUES (11077, 'Jhelum Express', 'Pune', 'Jammu Tawi')",
                    "INSERT INTO trains VALUES (12615, 'Grand Trunk Express', 'Chennai', 'New Delhi')",
                    "INSERT INTO trains VALUES (12903, 'Golden Temple Mail', 'Mumbai', 'Amritsar')"
                };
                for (String sql : trains) {
                    stmt.execute(sql);
                }
            }
            rs.close();

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -------------------- Authentication --------------------

    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // -------------------- Train Lookup --------------------

    public static String getTrainName(int trainNumber) {
        String sql = "SELECT train_name FROM trains WHERE train_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("train_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getTrainStations(int trainNumber) {
        String sql = "SELECT source, destination FROM trains WHERE train_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("source"), rs.getString("destination")};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String[]> getAllTrains() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT train_number, train_name, source, destination FROM trains ORDER BY train_number";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("train_number")),
                    rs.getString("train_name"),
                    rs.getString("source"),
                    rs.getString("destination")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // -------------------- Reservation --------------------

    /** Generates a unique 10-digit PNR-like number. */
    public static String generatePNR() {
        // Format: 2-digit year + 6 random digits + 2 sequential-ish
        long millis = System.currentTimeMillis();
        int random = (int) (Math.random() * 900000) + 100000;
        return String.format("%02d%06d%02d",
                (millis / 1000) % 100,
                random,
                (int) (millis % 100));
    }

    public static boolean bookTicket(String pnr, String passengerName, int trainNumber,
                                     String trainName, String classType, String journeyDate,
                                     String source, String destination) {
        String sql = """
            INSERT INTO reservations
            (pnr, passenger_name, train_number, train_name, class_type, journey_date, source_station, destination_station)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            ps.setString(2, passengerName);
            ps.setInt(3, trainNumber);
            ps.setString(4, trainName);
            ps.setString(5, classType);
            ps.setString(6, journeyDate);
            ps.setString(7, source);
            ps.setString(8, destination);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Reservation getReservationByPNR(String pnr) {
        String sql = "SELECT * FROM reservations WHERE pnr = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                            rs.getString("pnr"),
                            rs.getString("passenger_name"),
                            rs.getInt("train_number"),
                            rs.getString("train_name"),
                            rs.getString("class_type"),
                            rs.getString("journey_date"),
                            rs.getString("source_station"),
                            rs.getString("destination_station"),
                            rs.getString("booked_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean cancelReservation(String pnr) {
        String sql = "DELETE FROM reservations WHERE pnr = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Simple data holder for a reservation
    public static class Reservation {
        public final String pnr;
        public final String passengerName;
        public final int trainNumber;
        public final String trainName;
        public final String classType;
        public final String journeyDate;
        public final String source;
        public final String destination;
        public final String bookedAt;

        public Reservation(String pnr, String passengerName, int trainNumber, String trainName,
                           String classType, String journeyDate, String source, String destination,
                           String bookedAt) {
            this.pnr = pnr;
            this.passengerName = passengerName;
            this.trainNumber = trainNumber;
            this.trainName = trainName;
            this.classType = classType;
            this.journeyDate = journeyDate;
            this.source = source;
            this.destination = destination;
            this.bookedAt = bookedAt;
        }

        @Override
        public String toString() {
            return String.format("""
                PNR            : %s
                Passenger Name : %s
                Train Number   : %d
                Train Name     : %s
                Class          : %s
                Journey Date   : %s
                From           : %s
                To             : %s
                Booked At      : %s
                """, pnr, passengerName, trainNumber, trainName, classType,
                    journeyDate, source, destination, bookedAt);
        }
    }
}
