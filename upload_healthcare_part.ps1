
param($partNumber, $password, $serverIp)

$partFile = "target\parts\part$partNumber"
if (!(Test-Path $partFile)) { Write-Error "File $partFile not found"; return }

Write-Host "Encoding part $partNumber..."
$base64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes($partFile))

Write-Host "Uploading part $partNumber to /root/healthcare/..."
$base64 | .\plink.exe -ssh -pw "$password" -hostkey "SHA256:zUY97uW+jC6tnlFGD7GpUFWCDgudbNlXIvPobNAwzfs" root@$serverIp "mkdir -p /root/healthcare && base64 -d > /root/healthcare/part$partNumber"

Write-Host "Verifying part $partNumber..."
$remoteSize = .\plink.exe -ssh -pw "$password" -hostkey "SHA256:zUY97uW+jC6tnlFGD7GpUFWCDgudbNlXIvPobNAwzfs" root@$serverIp "stat -c%s /root/healthcare/part$partNumber"
$localSize = (Get-Item $partFile).Length

if ($remoteSize.Trim() -eq $localSize.ToString()) {
    Write-Host "Part $partNumber uploaded successfully!" -ForegroundColor Green
} else {
    Write-Error "Size mismatch for part $partNumber. Local: $localSize, Remote: $remoteSize"
}
