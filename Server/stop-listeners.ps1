# Free the build-592 server's listening ports before launching new JVMs.
# A forced stop cannot invoke Java shutdown hooks; log characters out first.
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateRange(1, 25535)]
    [int] $WorldId
)

$ErrorActionPreference = 'Stop'
$requiredPorts = @(40000, 43594, 8081, (40000 + $WorldId)) | Sort-Object -Unique
# Port 80 is optional and often hosts unrelated software. Only stop our own
# ScapeEmulator JVM on it; never terminate an unrelated web server.
$allPorts = @(80) + @($requiredPorts)

function Get-TargetListeners {
    @(Get-NetTCPConnection -State Listen -ErrorAction Stop |
        Where-Object { $allPorts -contains [int] $_.LocalPort })
}

try {
    if (-not (Get-Command Get-NetTCPConnection -ErrorAction SilentlyContinue)) {
        throw 'Get-NetTCPConnection is unavailable. Windows 8 / Server 2012 or newer is required.'
    }

    $listeners = @(Get-TargetListeners)
    $owners = @{}

    foreach ($listener in $listeners) {
        $port = [int] $listener.LocalPort
        $ownerPid = [int] $listener.OwningProcess

        if ($ownerPid -le 4) {
            if ($requiredPorts -contains $port) {
                throw "Required port $port is owned by system PID $ownerPid and cannot be cleared safely."
            }
            Write-Host "[PORT CLEANUP] Leaving system listener on optional port $port alone."
            continue
        }

        if ($port -eq 80) {
            $commandLine = $null
            try {
                $proc = Get-CimInstance Win32_Process -Filter "ProcessId = $ownerPid" -ErrorAction Stop
                $commandLine = $proc.CommandLine
            } catch {
                # Without proof of ownership, do not terminate a port-80 process.
            }
            if (-not $commandLine -or
                    $commandLine -notmatch 'net\.scapeemulator\.(game\.Launcher|xtalk\.CrosstalkServer)') {
                Write-Host "[PORT CLEANUP] Leaving unrelated listener on optional port 80 (PID $ownerPid)."
                continue
            }
        }

        if (-not $owners.ContainsKey($ownerPid)) {
            $owners[$ownerPid] = @()
        }
        $owners[$ownerPid] += $port
    }

    foreach ($ownerPid in @($owners.Keys)) {
        $ports = ($owners[$ownerPid] | Sort-Object -Unique) -join ', '
        Write-Host "[PORT CLEANUP] Stopping PID $ownerPid on port(s): $ports"
        Stop-Process -Id $ownerPid -Force -ErrorAction Stop
    }

    # Wait until the listeners disappear so the next JVM cannot race the old one.
    $remaining = @()
    for ($attempt = 0; $attempt -lt 20; $attempt++) {
        $remaining = @(Get-TargetListeners |
            Where-Object { $requiredPorts -contains [int] $_.LocalPort })
        if ($remaining.Count -eq 0) {
            break
        }
        Start-Sleep -Milliseconds 250
    }

    if ($remaining.Count -gt 0) {
        $busyPorts = ($remaining | ForEach-Object { [int] $_.LocalPort } |
            Sort-Object -Unique) -join ', '
        throw "Required port(s) still listening after cleanup: $busyPorts"
    }

    Write-Host "[PORT CLEANUP] Required ports are clear: $($requiredPorts -join ', ')"
    exit 0
} catch {
    [Console]::Error.WriteLine("[ERROR] Port cleanup failed: $($_.Exception.Message)")
    exit 1
}
