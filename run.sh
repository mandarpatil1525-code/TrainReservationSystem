#!/bin/bash
# Simple compile & run script for Train Reservation System
# Works on Linux / macOS. For Windows use run.bat

set -e
cd "$(dirname "$0")"

mkdir -p out db

echo "Compiling..."
javac -cp "lib/sqlite-jdbc-3.46.1.3.jar" -d out src/main/java/com/reservation/*.java

echo "Starting application..."
java -cp "out:lib/sqlite-jdbc-3.46.1.3.jar" com.reservation.Main
