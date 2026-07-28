-- Digital Payment Fraud Detection System
-- MySQL Database Setup Script

CREATE DATABASE IF NOT EXISTS frauddb;
USE frauddb;

-- Table 1: Users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: Transactions
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_name VARCHAR(100) NOT NULL,
    receiver_name VARCHAR(100) NOT NULL,
    amount DOUBLE NOT NULL,
    transaction_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sample Data Insertion
-- Default Admin User (username: admin, password: password123)
INSERT INTO users (username, password, full_name)
VALUES ('admin', 'password123', 'System Administrator')
ON DUPLICATE KEY UPDATE username=username;

-- Sample Seed Transactions
INSERT INTO transactions (sender_name, receiver_name, amount, transaction_date, status) VALUES
('John Doe', 'Alice Smith', 12500.00, '2026-07-20', 'Safe'),
('Robert Vance', 'TechCorp International', 85000.00, '2026-07-21', 'Fraud'),
('Sarah Connor', 'Cyberdyne Systems', 45000.50, '2026-07-22', 'Safe'),
('Michael Scott', 'Dunder Mifflin', 62000.00, '2026-07-23', 'Fraud'),
('Emily Watson', 'James Bond', 3500.00, '2026-07-24', 'Safe'),
('David Miller', 'Global Offshore Ltd', 150000.00, '2026-07-25', 'Fraud');

-- Verify inserted records
SELECT * FROM users;
SELECT * FROM transactions;
