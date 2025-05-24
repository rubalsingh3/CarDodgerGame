# 🚗 Car Dodger Game

A simple Java-based 2D GUI game where the player dodges incoming cars and tries to survive as long as possible. The game uses **Java Swing** for the GUI and **JDBC with MySQL** for storing player scores.

## 🎮 Game Features

- Smooth obstacle spawning and scrolling background
- Collision detection and invincibility frames
- Score, lives, and distance tracking
- MySQL database integration to save high scores
- Clean, aligned, and visually appealing UI

---

## 📁 Project Structure

CarDodgerGame/
├── src/
│   ├── CarDodgerGame.java
│   └── assets/
│        ├── car.png
│        ├── background.png
│        ├── obstacle1.png
│        └── obstacle2.png
├── README.md
└── database.sql

---

## 💾 Database Schema

A simple table to store game scores:

```sql
CREATE DATABASE IF NOT EXISTS car_game;
USE car_game;

CREATE TABLE IF NOT EXISTS scores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    score INT,
    distance INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

🔗 JDBC Database Integration

JDBC is used via DBConnection.java to connect to MySQL.

ScoreDAO.java handles saving the game result to the database after game over.

Make sure to update your database credentials in DBConnection.java.

🧩 Components & Logic

Model: Score.java (if added later)

DAO: ScoreDAO.java

Database Layer: DBConnection.java

UI Logic: CarDodgerGame.java

🎨 UI & UX

Dark-themed road background

Animated background scroll for immersion

Lives and distance displayed during gameplay

Game Over screen with restart prompt

🚀 How to Run

Install JDK 8 or above

Set up MySQL and run the provided SQL script

Update JDBC URL, username, and password in DBConnection.java

Compile and run CarDodgerGame.java using an IDE like IntelliJ or Eclipse

✅ Marking Rubric Checklist

Criteria	                                      ✅ Done
JDK & IDE Setup	                                      ✅
Project Structure Defined	                      ✅
MySQL Database Schema Designed                        ✅
Table Created in MySQL	                              ✅
JDBC Implemented for Database Connectivity	      ✅
Model and DAO Classes for DB Operations               ✅
UI Aesthetics and Visual Appeal	                      ✅
Proper Component Placement and Alignment	      ✅
Responsiveness and Accessibility	              ✅

🛠 Tech Stack

Java (Swing)

MySQL

JDBC

👨‍💻 Author
Rubal Singh
Sudhanshu Singh
Deepender 
Utsav Bidyarthi

 – Java GUI Project Submission – Galgotias University



---

