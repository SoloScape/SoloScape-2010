@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
title ScapeEmulator 592 - Build

rem Find a JDK on the current PATH without changing PATH.
set "JAVA_EXE="
set "JAVAC_EXE="
for /f "delims=" %%J in ('where javac 2^>nul') do if not defined JAVAC_EXE if exist "%%~dpJjava.exe" (
    set "JAVAC_EXE=%%J"
    set "JAVA_EXE=%%~dpJjava.exe"
)

if not defined JAVAC_EXE (
    echo [ERROR] javac was not found on PATH. A Java 8 JDK is required.
    goto :fail
)

if not exist "game\src\main\java\net\scapeemulator\game\Launcher.java" (
    echo [ERROR] This file must be run from the ScapeEmulator project root.
    goto :fail
)

set "LIB_DIR=%CD%\.tools\lib"
set "CLASSES_DIR=game\target\classes"
set "GAME_CP_FILE=game\target\runtime-classpath.txt"
set "SOURCES_FILE=game\target\sources.txt"

echo ============================================================
echo   ScapeEmulator 592 - Server Build
echo ============================================================
echo.
echo Java runtime:
"%JAVA_EXE%" -version
echo.
echo Java compiler:
"%JAVAC_EXE%" -version
echo.

if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"
if not exist "game\target" mkdir "game\target"

echo [1/2] Downloading required libraries when missing...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ErrorActionPreference='Stop'; $ProgressPreference='SilentlyContinue'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $lib='%LIB_DIR%'; $base='https://repo.maven.apache.org/maven2/'; $artifacts=@('io/netty/netty-common/4.0.0.CR3/netty-common-4.0.0.CR3.jar','io/netty/netty-buffer/4.0.0.CR3/netty-buffer-4.0.0.CR3.jar','io/netty/netty-transport/4.0.0.CR3/netty-transport-4.0.0.CR3.jar','io/netty/netty-codec/4.0.0.CR3/netty-codec-4.0.0.CR3.jar','io/netty/netty-codec-http/4.0.0.CR3/netty-codec-http-4.0.0.CR3.jar','io/netty/netty-handler/4.0.0.CR3/netty-handler-4.0.0.CR3.jar','org/slf4j/slf4j-api/1.7.5/slf4j-api-1.7.5.jar','org/slf4j/slf4j-jdk14/1.7.5/slf4j-jdk14-1.7.5.jar','org/mindrot/jbcrypt/0.3m/jbcrypt-0.3m.jar','mysql/mysql-connector-java/5.1.25/mysql-connector-java-5.1.25.jar'); foreach($a in $artifacts){$dest=Join-Path $lib (Split-Path $a -Leaf); if(!(Test-Path -LiteralPath $dest)){Write-Host ('Downloading '+(Split-Path $a -Leaf)); Invoke-WebRequest -UseBasicParsing -Uri ($base+$a) -OutFile $dest}}; $jars=Get-ChildItem -LiteralPath $lib -Filter '*.jar' | Sort-Object Name; if($jars.Count -lt $artifacts.Count){throw 'One or more libraries are missing.'}; $cp=[string]::Join(';',@($jars | ForEach-Object {$_.FullName})); [IO.File]::WriteAllText('%GAME_CP_FILE%',$cp,[Text.Encoding]::ASCII); $roots=@('api\src\main\java','cache\src\main\java','util\src\main\java','js5\src\main\java','login\src\main\java','game\src\main\java','game\crosstalk\main\java','game\src\content\core','game\src\content\grandexchange','game\src\content\interface'); $files=@(); foreach($r in $roots){if(Test-Path -LiteralPath $r){$files+=Get-ChildItem -LiteralPath $r -Recurse -File -Filter '*.java'}}; if($files.Count -eq 0){throw 'No Java source files were found.'}; $lines=@($files | ForEach-Object {'"'+$_.FullName+'"'}); [IO.File]::WriteAllLines('%SOURCES_FILE%',$lines,[Text.Encoding]::ASCII); Write-Host ('Prepared '+$files.Count+' Java source files and '+$jars.Count+' libraries.')"
if errorlevel 1 (
    echo [ERROR] Could not prepare the libraries or Java source list.
    goto :fail
)

set "GAME_CP="
set /p GAME_CP=<"%GAME_CP_FILE%"
if not defined GAME_CP (
    echo [ERROR] Runtime classpath is empty.
    goto :fail
)

echo.
echo [2/2] Compiling server sources...
if exist "%CLASSES_DIR%" rmdir /s /q "%CLASSES_DIR%"
mkdir "%CLASSES_DIR%"
"%JAVAC_EXE%" -encoding UTF-8 -source 8 -target 8 -cp "%GAME_CP%" -d "%CLASSES_DIR%" @"%SOURCES_FILE%"
if errorlevel 1 goto :fail

echo.
echo ============================================================
echo   BUILD SUCCESSFUL
echo ============================================================
echo Game main:  net.scapeemulator.game.Launcher 1
echo Login main: net.scapeemulator.xtalk.CrosstalkServer
echo Run run.bat next.
pause
exit /b 0

:fail
echo.
echo ============================================================
echo   BUILD FAILED
echo ============================================================
pause
exit /b 1
