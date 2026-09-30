@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
title ScapeEmulator 592 - Client

set "CLIENT_URL=http://127.0.0.1:8080/index_unsigned.html"

if not exist "www\loader.jar" (
    echo [ERROR] The client has not been built yet.
    echo Expected: %CD%\www\loader.jar
    echo Run build.bat first.
    pause
    exit /b 1
)

set "APPLETVIEWER_EXE="
for /f "delims=" %%J in ('where appletviewer 2^>nul') do if not defined APPLETVIEWER_EXE set "APPLETVIEWER_EXE=%%J"
if not defined APPLETVIEWER_EXE if defined JAVA_HOME if exist "%JAVA_HOME%\bin\appletviewer.exe" set "APPLETVIEWER_EXE=%JAVA_HOME%\bin\appletviewer.exe"

if not defined APPLETVIEWER_EXE (
    echo [ERROR] Java appletviewer was not found.
    echo Install or select a Java 8 JDK and ensure its bin directory is on PATH.
    pause
    exit /b 1
)

echo Checking server at %CLIENT_URL%...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ProgressPreference='SilentlyContinue'; try { $response=Invoke-WebRequest -UseBasicParsing -Uri '%CLIENT_URL%' -TimeoutSec 3; if($response.StatusCode -ne 200){exit 1} } catch { exit 1 }"
if errorlevel 1 (
    echo [ERROR] The ScapeEmulator server is not available at 127.0.0.1:8080.
    echo Start C:\Users\Callum\Documents\GitHub\10-Server\run.bat first.
    pause
    exit /b 1
)

echo Starting ScapeEmulator 592 client...
"%APPLETVIEWER_EXE%" "-J-Djava.security.policy=%CD%\client.policy" "%CLIENT_URL%"
exit /b %ERRORLEVEL%
