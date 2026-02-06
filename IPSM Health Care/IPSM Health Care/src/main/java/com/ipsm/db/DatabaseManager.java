package com.ipsm.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.ipsm.UserSession;

public class DatabaseManager {
    private static com.zaxxer.hikari.HikariDataSource dataSource;
    private static String USER;
    private static String PASSWORD;
    private static String HOST;
    private static String PORT;
    private static String DB_NAME;

    static {
        loadProperties();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println(
                    "CRITICAL: MySQL JDBC Driver not found. Please ensure mysql-connector-j is in the classpath.");
            e.printStackTrace();
        }
    }

    private static void loadProperties() {
        java.util.Properties props = new java.util.Properties();
        // Priority 1: External file (for easy config change)
        java.io.File propsFile = new java.io.File("db.properties");
        try (java.io.InputStream fis = propsFile.exists() ? new java.io.FileInputStream(propsFile)
                : DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (fis != null) {
                props.load(fis);
            } else {
                System.err.println("db.properties not found!");
            }
        } catch (java.io.IOException e) {
            System.err.println("Error loading db.properties: " + e.getMessage());
        }

        HOST = props.getProperty("db.host", "72.61.253.79");
        PORT = props.getProperty("db.port", "3306");
        DB_NAME = props.getProperty("db.name", "ipsm_healthcare");
        USER = props.getProperty("db.user", "ipsm_admin");
        PASSWORD = props.getProperty("db.password", "ipsm98765@#");
    }

    private static void initDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
            String jdbcUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME +
                    "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000); // 5 minutes
            config.setConnectionTimeout(20000); // 20 seconds
            config.setValidationTimeout(5000); // 5 seconds
            config.setMaxLifetime(1800000); // 30 minutes
            config.setPoolName("IPSM-HikariPool");
            config.addDataSourceProperty("tcpKeepAlive", "true");
            config.addDataSourceProperty("autoReconnect", "true");

            dataSource = new com.zaxxer.hikari.HikariDataSource(config);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    public static boolean checkConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean isServerReachable() {
        // Direct Check without Pool for initial validation
        String serverUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/?connectTimeout=3000&socketTimeout=3000";
        try (Connection conn = DriverManager.getConnection(serverUrl, USER, PASSWORD)) {
            return conn.isValid(3);
        } catch (SQLException e) {
            System.err.println("Server Unreachable: " + e.getMessage());
            return false;
        }
    }

    public static void initializeDatabase() {
        String dbUrl = "jdbc:mysql://" + HOST + ":" + PORT
                + "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";
        try (Connection conn = DriverManager.getConnection(dbUrl, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS ipsm_healthcare");
            stmt.execute("USE ipsm_healthcare");

            // Self-Healing: Check if 'users' table is outdated (missing user_id)
            try {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet rs = meta.getColumns(null, null, "users", "user_id")) {
                    if (!rs.next()) {
                        // user_id column missing. The table is old/incompatible. Drop it to recreate.
                        System.out.println("Migrating outdated 'users' table...");
                        stmt.execute("DROP TABLE IF EXISTS users");
                    }
                }
            } catch (Exception e) {
                System.err.println("Info: Could not check for 'users' table column version: " + e.getMessage());
            }

            // Migration logic and initial table creation...
            stmt.execute("CREATE TABLE IF NOT EXISTS tests (" +
                    "test_code VARCHAR(50) PRIMARY KEY, " +
                    "test_name VARCHAR(255) NOT NULL, " +
                    "cutoff VARCHAR(100), " +
                    "report_time VARCHAR(100), " +
                    "special_price DECIMAL(10, 2), " +
                    "mrp DECIMAL(10, 2), " +
                    "category VARCHAR(100), " +
                    "is_custom BOOLEAN DEFAULT FALSE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "patient_id INT PRIMARY KEY, " +
                    "title VARCHAR(10), " +
                    "patient_name VARCHAR(255) NOT NULL, " +
                    "gender VARCHAR(10), " +
                    "age INT, " +
                    "age_unit VARCHAR(10), " +
                    "mobile VARCHAR(15), " +
                    "email VARCHAR(100), " +
                    "address TEXT, " +
                    "marital_status VARCHAR(20), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS registrations (" +
                    "reg_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "patient_id INT, " +
                    "doctor_id INT, " +
                    "reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "total_amount DECIMAL(10, 2), " +
                    "paid_amount DECIMAL(10, 2), " +
                    "balance_amount DECIMAL(10, 2), " +
                    "payment_mode VARCHAR(50), " +
                    "remarks TEXT, " +
                    "FOREIGN KEY (patient_id) REFERENCES patients(patient_id))");

            // Migration: Drop old doctor_id foreign key if it exists to allow linking to
            // 'users'
            try {
                stmt.execute("ALTER TABLE registrations DROP FOREIGN KEY registrations_ibfk_2");
            } catch (SQLException e) {
                // Ignore if not present
            }
            try {
                // Secondary check for common named constraints
                stmt.execute("ALTER TABLE registrations DROP FOREIGN KEY fk_doctor_id");
            } catch (SQLException e) {
            }

            stmt.execute("CREATE TABLE IF NOT EXISTS registration_tests (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "reg_id INT, " +
                    "test_code VARCHAR(50), " +
                    "status VARCHAR(20) DEFAULT 'Pending', " +
                    "FOREIGN KEY (reg_id) REFERENCES registrations(reg_id), " +
                    "FOREIGN KEY (test_code) REFERENCES tests(test_code))");

            stmt.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                    "doctor_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "doctor_name VARCHAR(255) NOT NULL, " +
                    "mobile VARCHAR(15), " +
                    "address TEXT, " +
                    "specialization VARCHAR(100))");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(50) NOT NULL, " +
                    "department VARCHAR(100), " +
                    "reg_new BOOLEAN DEFAULT TRUE, " +
                    "reg_edit BOOLEAN DEFAULT TRUE, " +
                    "reg_manage BOOLEAN DEFAULT TRUE, " +
                    "inv_status BOOLEAN DEFAULT TRUE, " +
                    "inv_reprint BOOLEAN DEFAULT TRUE, " +
                    "rep_outstanding BOOLEAN DEFAULT TRUE, " +
                    "rep_summary BOOLEAN DEFAULT TRUE, " +
                    "rep_ledger BOOLEAN DEFAULT TRUE, " +
                    "rep_business BOOLEAN DEFAULT TRUE, " +
                    "rep_sales BOOLEAN DEFAULT TRUE, " +
                    "test_status BOOLEAN DEFAULT FALSE)");

            stmt.execute("INSERT IGNORE INTO users (username, password, role) VALUES ('admin', 'admin', 'ADMIN')");

            // Migrations for missing columns
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN reg_new BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN reg_edit BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN reg_manage BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN inv_status BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN inv_reprint BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN rep_outstanding BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN rep_summary BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN rep_ledger BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN rep_business BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN rep_sales BOOLEAN DEFAULT TRUE");
            } catch (SQLException e) {
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN test_status BOOLEAN DEFAULT FALSE");
            } catch (SQLException e) {
                // Already exists or other non-critical error
            }

            try {
                stmt.execute("ALTER TABLE users ADD COLUMN staff_id VARCHAR(50)");
            } catch (SQLException e) {
            }

            try {
                stmt.execute("ALTER TABLE doctors ADD COLUMN specialization VARCHAR(100)");
            } catch (SQLException e) {
            }

            try {
                stmt.execute("ALTER TABLE patients ADD COLUMN marital_status VARCHAR(20)");
            } catch (SQLException e) {
            }

            // NEW: Staff Tables
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS branches (branch_id INT AUTO_INCREMENT PRIMARY KEY, branch_name VARCHAR(100) UNIQUE, short_code VARCHAR(10))");

            // Update branches: Replace old with new
            stmt.execute("DELETE FROM branches"); // Clear old branches
            stmt.execute(
                    "INSERT IGNORE INTO branches (branch_name, short_code) VALUES ('Uttam Nagar', 'UN'), ('Dwarka', 'DW'), ('Gurugram', 'GG')");

            stmt.execute("CREATE TABLE IF NOT EXISTS staff (" +
                    "staff_id VARCHAR(50) PRIMARY KEY, " +
                    "staff_name VARCHAR(255) NOT NULL, " +
                    "father_name VARCHAR(255), " +
                    "designation VARCHAR(100), " +
                    "branch VARCHAR(100), " +
                    "doj VARCHAR(20), " +
                    "dob VARCHAR(20), " +
                    "mobile VARCHAR(15), " +
                    "alt_mobile VARCHAR(15), " +
                    "aadhar VARCHAR(20), " +
                    "address TEXT, " +
                    "corr_address TEXT, " +
                    "gross_salary DECIMAL(10, 2), " +
                    "cl_forwarded INT, " +
                    "email VARCHAR(100), " +
                    "marital_status VARCHAR(20), " +
                    "wife_name VARCHAR(255), " +
                    "children_count INT, " +
                    "state VARCHAR(100), " +
                    "pincode VARCHAR(10), " +
                    "gender VARCHAR(10), " +
                    "languages TEXT, " +
                    "higher_qual VARCHAR(255), " +
                    "last_increment_date VARCHAR(20), " +
                    "next_increment_date VARCHAR(20), " +
                    "status VARCHAR(20) DEFAULT 'Active', " +
                    "doc_10th TEXT, doc_12th TEXT, doc_bachelor TEXT, doc_master TEXT, doc_phd TEXT, " +
                    "doc_photo TEXT, doc_resume TEXT, doc_aadhar TEXT, doc_pan TEXT, doc_signature TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS staff_bank_details (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "staff_id VARCHAR(50), " +
                    "bank_name VARCHAR(255), " +
                    "account_number VARCHAR(50), " +
                    "account_holder VARCHAR(255), " +
                    "ifsc_code VARCHAR(50), " +
                    "FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE)");

        } catch (SQLException e) {
            System.err.println("CRITICAL: Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            // Re-throw or handle as critical
        }
    }

    public static UserSession loginUser(String username, String password) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new UserSession(
                                rs.getString("username"),
                                rs.getString("role"),
                                rs.getString("department"),
                                rs.getBoolean("reg_new"),
                                rs.getBoolean("reg_edit"),
                                rs.getBoolean("reg_manage"),
                                rs.getBoolean("inv_status"),
                                rs.getBoolean("inv_reprint"),
                                rs.getBoolean("rep_outstanding"),
                                rs.getBoolean("rep_summary"),
                                rs.getBoolean("rep_ledger"),
                                rs.getBoolean("rep_business"),
                                rs.getBoolean("rep_sales"),
                                rs.getBoolean("test_status"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Object[]> getAllTests() {
        List<Object[]> tests = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM tests";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tests.add(new Object[] {
                            rs.getString("test_code"), rs.getString("test_name"),
                            rs.getString("cutoff"), rs.getString("report_time"),
                            rs.getDouble("special_price"), rs.getDouble("mrp"),
                            rs.getString("category"), rs.getBoolean("is_custom")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tests: " + e.getMessage());
            e.printStackTrace();
        }
        return tests;
    }

    public static void saveTest(String code, String name, String cutoff, String reportTime, double price, double mrp,
            String category, boolean isCustom) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT IGNORE INTO tests (test_code, test_name, cutoff, report_time, special_price, mrp, category, is_custom) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, code);
                pstmt.setString(2, name);
                pstmt.setString(3, cutoff);
                pstmt.setString(4, reportTime);
                pstmt.setDouble(5, price);
                pstmt.setDouble(6, mrp);
                pstmt.setString(7, category);
                pstmt.setBoolean(8, isCustom);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void syncAllTests(List<String[]> tests) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            String sql = "INSERT IGNORE INTO tests (test_code, test_name, cutoff, report_time, special_price, mrp, category, is_custom) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (String[] t : tests) {
                    pstmt.setString(1, t[0]);
                    pstmt.setString(2, t[1]);
                    pstmt.setString(3, t[2]);
                    pstmt.setString(4, t[3]);
                    pstmt.setDouble(5, Double.parseDouble(t[4]));
                    pstmt.setDouble(6, Double.parseDouble(t[5]));
                    pstmt.setString(7, t[6]);
                    pstmt.setBoolean(8, Boolean.parseBoolean(t[7]));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getNextPatientId() {
        int nextId = 2000;
        try (Connection conn = getConnection()) {
            String sql = "SELECT MAX(patient_id) FROM patients";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    int maxId = rs.getInt(1);
                    if (maxId >= 2000)
                        nextId = maxId + 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting next patient ID: " + e.getMessage());
            e.printStackTrace();
        }
        return nextId;
    }

    public static List<Object[]> searchPatients(String query) {
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM patients WHERE patient_name LIKE ? OR mobile LIKE ? OR patient_id LIKE ? LIMIT 50";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String pattern = "%" + query + "%";
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);
                pstmt.setString(3, pattern);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(new Object[] {
                                rs.getInt("patient_id"), rs.getString("title"), rs.getString("patient_name"),
                                rs.getString("gender"), rs.getInt("age"), rs.getString("age_unit"),
                                rs.getString("mobile"), rs.getString("email"), rs.getString("address"),
                                rs.getString("marital_status")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List<Object[]> searchPatientsSpecific(String query, String type) {
        List<Object[]> results = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "";
            if (type.equalsIgnoreCase("Name"))
                sql = "SELECT * FROM patients WHERE patient_name LIKE ? LIMIT 50";
            else if (type.equalsIgnoreCase("Mobile"))
                sql = "SELECT * FROM patients WHERE mobile LIKE ? LIMIT 50";
            else if (type.equalsIgnoreCase("ID"))
                sql = "SELECT * FROM patients WHERE patient_id LIKE ? LIMIT 50";
            else
                return searchPatients(query);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + query + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(new Object[] {
                                rs.getInt("patient_id"), rs.getString("title"), rs.getString("patient_name"),
                                rs.getString("gender"), rs.getInt("age"), rs.getString("age_unit"),
                                rs.getString("mobile"), rs.getString("email"), rs.getString("address"),
                                rs.getString("marital_status")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List<String> getDoctorsList() {
        List<String> doctors = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT doctor_name FROM doctors ORDER BY doctor_name ASC";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next())
                    doctors.add(rs.getString("doctor_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    public static Integer getDoctorIdByName(String name) {
        if (name == null || name.isEmpty())
            return null;
        try (Connection conn = getConnection()) {
            String sql = "SELECT doctor_id FROM doctors WHERE doctor_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next())
                        return rs.getInt("doctor_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getInternalDoctorsList() {
        List<String> doctors = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT s.staff_name FROM staff s " +
                    "JOIN users u ON s.staff_id = u.staff_id " +
                    "WHERE u.role = 'DOCTOR' " +
                    "ORDER BY s.staff_name ASC";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next())
                    doctors.add(rs.getString("staff_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching internal doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    public static Integer getInternalDoctorIdByName(String name) {
        if (name == null || name.isEmpty())
            return null;
        try (Connection conn = getConnection()) {
            String sql = "SELECT u.user_id FROM users u " +
                    "JOIN staff s ON u.staff_id = s.staff_id " +
                    "WHERE s.staff_name = ? AND u.role = 'DOCTOR'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next())
                        return rs.getInt("user_id");
                }
            }
            // Fallback: If not found in staff join, try username check (for legacy/admin
            // users)
            String fallbackSql = "SELECT user_id FROM users WHERE username = ? AND role = 'DOCTOR'";
            try (PreparedStatement pstmt = conn.prepareStatement(fallbackSql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next())
                        return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getInternalDoctorsByDepartment(String dept) {
        List<String> doctors = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT s.staff_name FROM staff s " +
                    "JOIN users u ON s.staff_id = u.staff_id " +
                    "WHERE u.role = 'DOCTOR' AND u.department = ? " +
                    "ORDER BY s.staff_name ASC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, dept);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next())
                        doctors.add(rs.getString("staff_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching internal doctors by dept: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    public static List<String> getDoctorsBySpecialization(String spec) {
        List<String> doctors = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT doctor_name FROM doctors WHERE specialization = ? ORDER BY doctor_name ASC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, spec);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next())
                        doctors.add(rs.getString("doctor_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctors by spec: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    public static String validateUser(String username, String password) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next())
                        return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updatePassword(String username, String newPassword) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE users SET password = ? WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newPassword);
                pstmt.setString(2, username);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getBranches() {
        List<String> branches = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT branch_name FROM branches ORDER BY branch_name")) {
            while (rs.next())
                branches.add(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public static String getBranchShortCode(String branch) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT short_code FROM branches WHERE branch_name = ?")) {
            pstmt.setString(1, branch);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ST";
    }

    public static int getNextStaffSequence(String branch) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM staff WHERE branch = ?")) {
            pstmt.setString(1, branch);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static boolean saveStaff(String staffId, String name, String desig, String branch, String doj, double salary,
            String mobile, String aadhar, String address, String dob, String status, String father,
            String doc10, String doc12, String bachelor, String master, String phd, String photo,
            String resume, String aadharDoc, String panDoc, String sig, String email, String marital,
            String state, String pincode, String altMob, String corrAddr, String wife, String children,
            String lang, String qual, String lastInc, String nextInc) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO staff (staff_id, staff_name, designation, branch, doj, gross_salary, mobile, aadhar, address, dob, status, father_name, "
                    +
                    "doc_10th, doc_12th, doc_bachelor, doc_master, doc_phd, doc_photo, doc_resume, doc_aadhar, doc_pan, doc_signature, "
                    +
                    "email, marital_status, state, pincode, alt_mobile, corr_address, wife_name, children_count, languages, higher_qual, last_increment_date, next_increment_date) "
                    +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE staff_name=VALUES(staff_name), designation=VALUES(designation), branch=VALUES(branch), gross_salary=VALUES(gross_salary), mobile=VALUES(mobile)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, staffId);
                pstmt.setString(2, name);
                pstmt.setString(3, desig);
                pstmt.setString(4, branch);
                pstmt.setString(5, doj);
                pstmt.setDouble(6, salary);
                pstmt.setString(7, mobile);
                pstmt.setString(8, aadhar);
                pstmt.setString(9, address);
                pstmt.setString(10, dob);
                pstmt.setString(11, status);
                pstmt.setString(12, father);
                pstmt.setString(13, doc10);
                pstmt.setString(14, doc12);
                pstmt.setString(15, bachelor);
                pstmt.setString(16, master);
                pstmt.setString(17, phd);
                pstmt.setString(18, photo);
                pstmt.setString(19, resume);
                pstmt.setString(20, aadharDoc);
                pstmt.setString(21, panDoc);
                pstmt.setString(22, sig);
                pstmt.setString(23, email);
                pstmt.setString(24, marital);
                pstmt.setString(25, state);
                pstmt.setString(26, pincode);
                pstmt.setString(27, altMob);
                pstmt.setString(28, corrAddr);
                pstmt.setString(29, wife);
                pstmt.setString(30, children);
                pstmt.setString(31, lang);
                pstmt.setString(32, qual);
                pstmt.setString(33, lastInc);
                pstmt.setString(34, nextInc);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveBank(String staffId, String bank, String acc, String holder, String ifsc) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO staff_bank_details (staff_id, bank_name, account_number, account_holder, ifsc_code) VALUES (?,?,?,?,?) "
                    +
                    "ON DUPLICATE KEY UPDATE bank_name=VALUES(bank_name), account_number=VALUES(account_number), account_holder=VALUES(account_holder), ifsc_code=VALUES(ifsc_code)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, staffId);
                pstmt.setString(2, bank);
                pstmt.setString(3, acc);
                pstmt.setString(4, holder);
                pstmt.setString(5, ifsc);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Object[]> getAllStaff() {
        List<Object[]> staffList = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT s.*, b.bank_name, b.account_number FROM staff s LEFT JOIN staff_bank_details b ON s.staff_id = b.staff_id ORDER BY s.staff_name")) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 1; i <= cols; i++)
                    row[i - 1] = rs.getObject(i);
                staffList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

    public static List<Object[]> getStaffDataForUserManagement() {
        List<Object[]> data = new java.util.ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT staff_id, staff_name, branch FROM staff ORDER BY staff_name")) {
            while (rs.next()) {
                data.add(new Object[] { rs.getString(1), rs.getString(2), rs.getString(3) });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
