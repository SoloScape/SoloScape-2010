$ErrorActionPreference = 'Continue'
$candidates = New-Object 'System.Collections.Generic.List[string]'

$pathCandidates = & where.exe appletviewer.exe 2>$null
foreach ($candidate in $pathCandidates) {
    if ($candidate) { [void]$candidates.Add($candidate.Trim()) }
}

if ($env:JAVA_HOME) {
    [void]$candidates.Add((Join-Path $env:JAVA_HOME 'bin\appletviewer.exe'))
}

$programFilesX86 = [Environment]::GetFolderPath('ProgramFilesX86')
foreach ($vendor in @('Java', 'Zulu', 'Eclipse Adoptium', 'AdoptOpenJDK', 'Amazon Corretto', 'Microsoft', 'BellSoft')) {
    $vendorPath = Join-Path $programFilesX86 $vendor
    if (-not (Test-Path -LiteralPath $vendorPath)) { continue }
    foreach ($installation in Get-ChildItem -LiteralPath $vendorPath -Directory -ErrorAction SilentlyContinue) {
        [void]$candidates.Add((Join-Path $installation.FullName 'bin\appletviewer.exe'))
    }
}

$seen = New-Object 'System.Collections.Generic.HashSet[string]' ([StringComparer]::OrdinalIgnoreCase)
foreach ($candidate in $candidates) {
    if (-not $seen.Add($candidate)) { continue }
    if (-not (Test-Path -LiteralPath $candidate -PathType Leaf)) { continue }
    $java = Join-Path (Split-Path -Parent $candidate) 'java.exe'
    if (-not (Test-Path -LiteralPath $java -PathType Leaf)) { continue }
    $properties = & $java -XshowSettings:properties -version 2>&1 | Out-String
    if ($properties -match 'java\.specification\.version\s*=\s*1\.8' -and
        $properties -match 'sun\.arch\.data\.model\s*=\s*32') {
        Write-Output $candidate
        exit 0
    }
}
exit 1


