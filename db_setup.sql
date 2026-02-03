CREATE USER IF NOT EXISTS 'ipsm_admin'@'localhost' IDENTIFIED BY 'ipsm98765@#';
GRANT ALL PRIVILEGES ON ipsm_healthcare.* TO 'ipsm_admin'@'localhost';
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'ipsm98765@#';
FLUSH PRIVILEGES;
