#!/bin/bash
set -e

# 1. Update & Install
echo "[Server] Updating System..."
apt-get update -qq >/dev/null
apt-get install -y openjdk-17-jre-headless mysql-server >/dev/null

# 2. Configure MySQL Config (Blindly replace if needed)
echo "[Server] Configuring MySQL for Remote Access..."
sed -i 's/bind-address.*/bind-address = 0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf 2>/dev/null || true
sed -i 's/bind-address.*/bind-address = 0.0.0.0/' /etc/mysql/my.cnf 2>/dev/null || true
systemctl restart mysql

# 3. Database Setup
echo "[Server] Configuring Database Users..."
mysql -e "CREATE DATABASE IF NOT EXISTS ipsm_healthcare;" 2>/dev/null || true
mysql -e "CREATE USER IF NOT EXISTS 'ipsm_admin'@'%' IDENTIFIED BY 'ipsm98765@#';" 2>/dev/null || true
mysql -e "GRANT ALL PRIVILEGES ON ipsm_healthcare.* TO 'ipsm_admin'@'%';" 2>/dev/null || true
mysql -e "FLUSH PRIVILEGES;" 2>/dev/null || true

# 4. Stop Old App
echo "[Server] Stopping old process..."
pkill -f ipsm-backend || true
sleep 2

# 5. Start App
echo "[Server] Starting Backend..."
# We use a separate startup script or just nohup here.
# Explicitly redirecting stdio
nohup java -jar /root/ipsm-backend.jar \
  --spring.datasource.username='ipsm_admin' \
  --spring.datasource.password='ipsm98765@#' \
  --server.port=8080 > /root/ipsm.log 2>&1 &

echo "✅ App Startup Command Issued."
echo "Waiting for app to initialize (5 seconds)..."
sleep 5
tail -n 10 /root/ipsm.log
