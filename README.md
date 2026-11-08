# Train Reservation System

A complete **GUI-based Online Train Reservation System** built with:

- **Java Swing** – desktop GUI
- **JDBC** – database access
- **SQLite** – embedded database (no server required)

## Features

- **Login Form** – username + password; access denied for invalid credentials
- **Reservation Form**
  - Passenger name, train number, train name (auto-populated), class type, date of journey, source & destination
  - Book button → saves to DB and generates a unique **PNR**
  - Confirmation dialog with full booking details
- **Cancellation Form**
  - Enter PNR → Fetch button loads full booking details
  - Confirm Cancellation with “Are you sure?” dialog → removes booking
- **Input validation** – required fields, numeric train number, valid date format (`yyyy-MM-dd`), past dates rejected

## Default Login Credentials

| Username | Password  |
|----------|-----------|
| admin    | admin123  |
| user     | user123   |

## Sample Train Numbers

| Train No. | Train Name            | Source     | Destination |
|-----------|-----------------------|------------|-------------|
| 12345     | Rajdhani Express      | New Delhi  | Mumbai      |
| 12346     | Shatabdi Express      | New Delhi  | Chandigarh  |
| 12627     | Karnataka Express     | New Delhi  | Bengaluru   |
| 12951     | Mumbai Rajdhani       | Mumbai     | New Delhi   |
| 12001     | Bhopal Shatabdi       | New Delhi  | Bhopal      |
| 12841     | Coromandel Express    | Chennai    | Howrah      |
| 12245     | Duronto Express       | Howrah     | Pune        |
| 11077     | Jhelum Express        | Pune       | Jammu Tawi  |
| 12615     | Grand Trunk Express   | Chennai    | New Delhi   |
| 12903     | Golden Temple Mail    | Mumbai     | Amritsar    |

## Project Structure

```
TrainReservationSystem/
├── pom.xml                          # Maven build file
├── lib/
│   └── sqlite-jdbc-3.46.1.3.jar     # SQLite JDBC driver (already included)
├── db/                              # SQLite database file is created here at runtime
├── src/main/java/com/reservation/
│   ├── Main.java                    # Entry point
│   ├── DatabaseHelper.java          # All JDBC / SQLite logic
│   ├── LoginFrame.java
│   ├── MainMenuFrame.java
│   ├── ReservationFrame.java
│   └── CancellationFrame.java
└── README.md
```

## How to Run in VS Code

### Option 1 – Using the included JAR + simple compile (no Maven required)

1. Open the `TrainReservationSystem` folder in VS Code.
2. Make sure **Java Extension Pack** is installed.
3. Open a terminal in the project root and run:

```bash
# Create output directory
mkdir -p out

# Compile all Java files (include the SQLite driver on classpath)
javac -cp "lib/sqlite-jdbc-3.46.1.3.jar" -d out src/main/java/com/reservation/*.java

# Run the application
java -cp "out:lib/sqlite-jdbc-3.46.1.3.jar" com.reservation.Main
```

> On Windows use `;` instead of `:` in the classpath:
> ```
> java -cp "out;lib/sqlite-jdbc-3.46.1.3.jar" com.reservation.Main
> ```

### Option 2 – Using Maven (recommended)

1. Install Maven if you don’t have it.
2. From the project root:

```bash
mvn clean package
java -jar target/TrainReservationSystem-1.0-SNAPSHOT.jar
```

### Option 3 – VS Code “Run” button

1. Open `Main.java`.
2. Make sure the SQLite JAR is added to the project classpath  
   (VS Code Java: right-click `lib/sqlite-jdbc-...jar` → “Add to Project Dependencies” or configure `.vscode/settings.json` / `classpath`).
3. Click the **Run** link above `main` or press `F5`.

## Database

- The SQLite database file is automatically created at `db/reservation.db` on first run.
- Tables (`users`, `trains`, `reservations`) and sample data are seeded automatically.
- No MySQL / XAMPP / server setup is required.

## Requirements

- JDK 11 or higher (tested with 17+)
- SQLite JDBC driver (already present in `lib/`)

## Notes

- All SQL uses `PreparedStatement` to prevent SQL injection.
- PNR is a unique auto-generated 10-digit style identifier.
- Journey date must be in `yyyy-MM-dd` format and cannot be in the past.
