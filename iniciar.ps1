param([switch]$Demo)
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [Text.UTF8Encoding]::new($false)
$OutputEncoding = [Console]::OutputEncoding
$candidates = @()
if ($env:JAVA_HOME) { $candidates += Join-Path $env:JAVA_HOME 'bin\java.exe' }
$candidates += Get-ChildItem "$env:ProgramFiles\JetBrains\IntelliJ IDEA*\jbr\bin\java.exe" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName
$candidates += Get-ChildItem "$env:ProgramFiles\Java\*\bin\java.exe","$env:ProgramFiles\Eclipse Adoptium\*\bin\java.exe" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName
$onPath = Get-Command java.exe -ErrorAction SilentlyContinue
if ($onPath) { $candidates += $onPath.Source }
foreach ($candidate in ($candidates | Select-Object -Unique)) {
    if (-not (Test-Path -LiteralPath $candidate)) { continue }
    $info = New-Object System.Diagnostics.ProcessStartInfo
    $info.FileName = $candidate
    $info.Arguments = '-version'
    $info.UseShellExecute = $false
    $info.RedirectStandardError = $true
    $info.RedirectStandardOutput = $true
    $info.CreateNoWindow = $true
    $process = [Diagnostics.Process]::Start($info)
    $version = $process.StandardError.ReadToEnd() + $process.StandardOutput.ReadToEnd()
    $process.WaitForExit()
    $ok = $process.ExitCode -eq 0
    $process.Dispose()
    if ($ok -and $version -match 'version\s+"(?<major>\d+)(?:\.(?<minor>\d+))?') {
        $major = [int]$Matches.major
        if ($major -eq 1) { $major = [int]$Matches.minor }
        if ($major -ge 17) {
            Write-Host "Usando Java $major : $candidate"
            $jar = Join-Path $PSScriptRoot 'fitlife-1.0.0.jar'
            if ($Demo) { & $candidate -jar $jar --demo }
            else { & $candidate -jar $jar }
            exit $LASTEXITCODE
        }
    }
}
Write-Host 'Java 17 ou superior nao encontrado. Configure JAVA_HOME para um JDK compativel.'
exit 1