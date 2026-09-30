@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
title ScapeEmulator 592 - Client

set "CLIENT_URL=http://127.0.0.1:8080/index.html"

if not exist "www\loader.jar" (
    echo [ERROR] The client has not been built yet.
    echo Expected: %CD%\www\loader.jar
    echo Run build.bat first.
    pause
    exit /b 1
)

set "APPLETVIEWER_EXE="
for /f "delims=" %%J in ('powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0select-appletviewer.ps1"') do if not defined APPLETVIEWER_EXE set "APPLETVIEWER_EXE=%%J"

if not defined APPLETVIEWER_EXE (
    echo [ERROR] A 32-bit Java 8 appletviewer was not found.
    echo Install a 32-bit Java 8 JDK, then run this file again.
    pause
    exit /b 1
)

echo Checking server at %CLIENT_URL%...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ProgressPreference='SilentlyContinue'; try { $response=Invoke-WebRequest -UseBasicParsing -Uri '%CLIENT_URL%' -TimeoutSec 3; if($response.StatusCode -ne 200){exit 1} } catch { exit 1 }"
if errorlevel 1 (
    echo [ERROR] The ScapeEmulator server is not available at 127.0.0.1:8080.
    echo Start %~dp0..\Server\run.bat first.
    pause
    exit /b 1
)

echo Starting ScapeEmulator 592 client...
"%APPLETVIEWER_EXE%" "-J-Djava.security.policy=%CD%\client.policy" "%CLIENT_URL%"
exit /b %ERRORLEVEL%
