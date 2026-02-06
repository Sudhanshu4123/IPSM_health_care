$ErrorActionPreference = "Stop"
$serverIp = "72.61.253.79"
$user = "root"
Write-Host "Checking Server Logs..." -ForegroundColor Cyan
ssh -t "${user}@${serverIp}" "cat /root/ipsm.log"
Write-Host ""
Read-Host "Press Enter to exit..."
