
$file = "c:\Users\Asus\OneDrive\Desktop\healthcare\healthcare\target\healthcare-0.0.1-SNAPSHOT.jar"
$chunkSize = 5MB
$stream = [System.IO.File]::OpenRead($file)
$buffer = New-Object byte[] $chunkSize
$part = 0
if (!(Test-Path "target\parts")) { New-Item -ItemType Directory "target\parts" }
while ($read = $stream.Read($buffer, 0, $buffer.Length)) {
    $out = "c:\Users\Asus\OneDrive\Desktop\healthcare\healthcare\target\parts\part$part"
    if ($read -eq $chunkSize) {
        [System.IO.File]::WriteAllBytes($out, $buffer)
    } else {
        $lastBuffer = New-Object byte[] $read
        [System.Array]::Copy($buffer, $lastBuffer, $read)
        [System.IO.File]::WriteAllBytes($out, $lastBuffer)
    }
    $part++
}
$stream.Close()
Write-Host "Split into $part parts."
