
$parts = 0..11
foreach ($part in $parts) {
    Write-Host "Processing part $part..."
    powershell -ExecutionPolicy Bypass -File ".\upload_healthcare_part.ps1" -partNumber $part -password "iPSMNURSING@2024#" -serverIp "72.61.253.79"
}
