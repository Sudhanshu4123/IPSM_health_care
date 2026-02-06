$ErrorActionPreference = "Stop"
$serverIp = "72.61.253.79"
$user = "root"
$password = "iPSMNURSTING@2024#"
$jarPath = "IPSM-Backend\target\ipsm-backend-0.0.1-SNAPSHOT.jar"

Write-Host "=== IPSM MULTI-USER DEPLOYER ===" -ForegroundColor Cyan
Write-Host "Target: $serverIp"

# 1. Copy Password to Clipboard
Set-Clipboard -Value $password
Write-Host "Detailed Instructions:" -ForegroundColor Yellow
Write-Host "1. The Server Password ($password) has been COPIED to your Clipboard."
Write-Host "2. When prompted for 'password:', press [Ctrl + V] then [Enter]."
Write-Host "3. You will need to do this TWICE."
Write-Host ""
Pause

# 2. Upload Files
Write-Host "Step 1: Uploading Backend JAR..." -ForegroundColor Cyan
scp $jarPath "${user}@${serverIp}:/root/ipsm-backend.jar"

Write-Host "Step 2: Uploading Setup Script..." -ForegroundColor Cyan
scp "setup.sh" "${user}@${serverIp}:/root/setup.sh"

# 3. Execute Setup
Write-Host "Step 3: Configuring Server (Running setup.sh)..." -ForegroundColor Cyan
# Fix Windows Line Endings in setup.sh on the fly using sed on server? 
# Better: Just run it with bash explicitly which handles LF nicely, but Windows CRLF might still be issue.
# We will run sed to fix it first.
$cmd = "sed -i 's/\r$//' /root/setup.sh && bash /root/setup.sh"

ssh -t "${user}@${serverIp}" $cmd

Write-Host ""
Write-Host "🎉 DEPLOYMENT COMMAND SENT!" -ForegroundColor Green
Write-Host "Check the output above for any startup errors."
Write-Host "Backend is live at: http://${serverIp}:8080"
Read-Host "Press Enter to exit..."
