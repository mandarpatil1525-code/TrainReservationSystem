@echo off
REM Simple compile & run script for Windows

cd /d "%~dp0"

if not exist out mkdir out
if not exist db mkdir db

echo Compiling...
javac -cp "lib\sqlite-jdbc-3.46.1.3.jar" -d out src\main\java\com\reservation\*.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)

echo Starting application...
java -cp "out;lib\sqlite-jdbc-3.46.1.3.jar" com.reservation.Main
pause
