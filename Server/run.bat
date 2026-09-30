@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
title ScapeEmulator 592 - Run

set "WORLD_ID=%~1"
if not defined WORLD_ID set "WORLD_ID=1"

set "CLIENT_DIR=%SCAPE_CLIENT_DIR%"
if not defined CLIENT_DIR set "CLIENT_DIR=%~dp0..\10-Client\www"
if not exist "%CLIENT_DIR%\index.html" (
    echo [ERROR] Client files were not found.
    echo Expected: %CLIENT_DIR%\index.html
    echo Set SCAPE_CLIENT_DIR to the 10-Client\www directory if it is elsewhere.
    pause
    exit /b 1
)

set "JAVA_EXE="
for /f "delims=" %%J in ('where java 2^>nul') do if not defined JAVA_EXE set "JAVA_EXE=%%J"
if not defined JAVA_EXE (
    echo [ERROR] Java was not found on PATH.
    echo Install/use Java 8 and reopen this command prompt.
    pause
    exit /b 1
)

if not exist "game\target\classes\net\scapeemulator\game\Launcher.class" (
    echo [ERROR] The game server has not been built yet.
    echo Expected: game\target\classes\net\scapeemulator\game\Launcher.class
    echo Run build.bat manually, then run this file again.
    pause
    exit /b 1
)

if not exist "game\target\classes\net\scapeemulator\xtalk\CrosstalkServer.class" (
    echo [ERROR] The login/Crosstalk server has not been built yet.
    echo Expected: game\target\classes\net\scapeemulator\xtalk\CrosstalkServer.class
    echo Run build.bat manually, then run this file again.
    pause
    exit /b 1
)

if not exist "game\target\runtime-classpath.txt" (
    echo [ERROR] Missing game\target\runtime-classpath.txt
    echo Run build.bat manually once to generate it.
    pause
    exit /b 1
)

set "GAME_CP="
set /p GAME_CP=<"game\target\runtime-classpath.txt"
set "LOGIN_CP=%GAME_CP%"

if not defined GAME_CP (
    echo [ERROR] Game runtime classpath is empty.
    pause
    exit /b 1
)

if not defined LOGIN_CP (
    echo [ERROR] Login runtime classpath is empty.
    pause
    exit /b 1
)

echo ============================================================
echo   ScapeEmulator 592 - Run Only
echo   World: %WORLD_ID%
echo ============================================================
echo.

echo Starting Crosstalk/login server...
start "ScapeEmulator 592 - Login" /D "%~dp0login" cmd.exe /k call "%JAVA_EXE%" -Xms128m -Xmx512m -cp "%~dp0game\target\classes;%LOGIN_CP%" net.scapeemulator.xtalk.CrosstalkServer

echo Waiting for login server to start...
timeout /t 2 /nobreak >nul

echo Starting game server world %WORLD_ID%...
start "ScapeEmulator 592 - World %WORLD_ID%" /D "%~dp0game" cmd.exe /k call "%JAVA_EXE%" -Xms256m -Xmx1024m "-Dscape.client.dir=%CLIENT_DIR%" -cp "%~dp0game\target\classes;%GAME_CP%" net.scapeemulator.game.Launcher %WORLD_ID%

echo.
echo Server windows started.
echo This run.bat does NOT build or compile anything.
exit /b 0
