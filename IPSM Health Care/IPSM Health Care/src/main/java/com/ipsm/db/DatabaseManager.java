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
                e.printStackTrace();
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

            stmt.execute("CREATE TABLE IF NOT EXISTS registration_tests (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "reg_id INT, " +
                    "test_code VARCHAR(50), " +
                    "status VARCHAR(20) DEFAULT 'Pending', " +
                    "FOREIGN KEY (reg_id) REFERENCES registrations(reg_id), " +
                    "FOREIGN KEY (test_code) REFERENCES tests(test_code))");

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
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
                                rs.getString("mobile"), rs.getString("email"), rs.getString("address")
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
                                rs.getString("mobile"), rs.getString("email"), rs.getString("address")
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
}
