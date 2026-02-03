CREATE DATABASE IF NOT EXISTS ipsm_healthcare;
USE ipsm_healthcare;

-- Table for Tests
CREATE TABLE IF NOT EXISTS tests (
    test_code VARCHAR(50) PRIMARY KEY,
    test_name VARCHAR(255) NOT NULL,
    cutoff VARCHAR(100),
    report_time VARCHAR(100),
    special_price DECIMAL(10, 2),
    mrp DECIMAL(10, 2),
    category VARCHAR(100) DEFAULT 'Pathology',
    is_custom BOOLEAN DEFAULT FALSE
);

-- Table for Doctors
-- Note: 'specialization' was removed in recent updates.
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_name VARCHAR(255) NOT NULL,
    mobile VARCHAR(15),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for Patients
-- Note: Structure updated to use single patient_name field
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT PRIMARY KEY,
    title VARCHAR(10),
    patient_name VARCHAR(255) NOT NULL,
    gender VARCHAR(10),
    age INT,
    age_unit VARCHAR(10),
    dob DATE,
    mobile VARCHAR(15),
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for Registrations
CREATE TABLE IF NOT EXISTS registrations (
    reg_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    doctor_id INT,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2),
    paid_amount DECIMAL(10, 2),
    balance_amount DECIMAL(10, 2),
    payment_mode VARCHAR(50),
    remarks TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

-- Table for Registration-Test Link
CREATE TABLE IF NOT EXISTS registration_tests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reg_id INT,
    test_code VARCHAR(50),
    FOREIGN KEY (reg_id) REFERENCES registrations(reg_id),
    FOREIGN KEY (test_code) REFERENCES tests(test_code)
);

-- Table for Users (Authentication)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Default Users
INSERT IGNORE INTO users (username, password, role) VALUES ('admin', 'admin', 'ADMIN');
INSERT IGNORE INTO users (username, password, role) VALUES ('recep', 'recep', 'RECEPTIONIST');
INSERT IGNORE INTO users (username, password, role) VALUES ('doc', 'doc', 'DOCTOR');
