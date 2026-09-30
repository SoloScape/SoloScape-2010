@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
title ScapeEmulator 592 - Client Build

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

"%JAVA_EXE%" -version 2>&1 | findstr /c:"1.8." >nul
if errorlevel 1 (
    echo [ERROR] The client bundler requires Java 8 because it uses Pack200.
    "%JAVA_EXE%" -version
    goto :fail
)

set "LIB_DIR=%CD%\.tools\lib"
set "CLASSES_DIR=asm\target\classes"
set "CP_FILE=asm\target\runtime-classpath.txt"
set "SOURCES_FILE=asm\target\sources.txt"

echo ============================================================
echo   ScapeEmulator 592 - Client Build
echo ============================================================
echo.

if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"
if not exist "asm\target" mkdir "asm\target"

echo [1/3] Downloading required libraries when missing...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ErrorActionPreference='Stop'; $ProgressPreference='SilentlyContinue'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $lib='%LIB_DIR%'; $base='https://repo.maven.apache.org/maven2/'; $artifacts=@('org/ow2/asm/asm/4.1/asm-4.1.jar','org/ow2/asm/asm-tree/4.1/asm-tree-4.1.jar','org/ow2/asm/asm-util/4.1/asm-util-4.1.jar','org/ow2/asm/asm-analysis/4.1/asm-analysis-4.1.jar','org/slf4j/slf4j-api/1.7.5/slf4j-api-1.7.5.jar','org/slf4j/slf4j-jdk14/1.7.5/slf4j-jdk14-1.7.5.jar'); foreach($a in $artifacts){$dest=Join-Path $lib (Split-Path $a -Leaf); if(!(Test-Path -LiteralPath $dest)){Write-Host ('Downloading '+(Split-Path $a -Leaf)); Invoke-WebRequest -UseBasicParsing -Uri ($base+$a) -OutFile $dest}}; $jars=Get-ChildItem -LiteralPath $lib -Filter '*.jar' | Sort-Object Name; $cp=[string]::Join(';',@($jars | ForEach-Object {$_.FullName})); [IO.File]::WriteAllText('%CP_FILE%',$cp,[Text.Encoding]::ASCII); $files=Get-ChildItem -LiteralPath 'asm\src\main\java' -Recurse -File -Filter '*.java'; $lines=@($files | ForEach-Object {'"'+$_.FullName+'"'}); [IO.File]::WriteAllLines('%SOURCES_FILE%',$lines,[Text.Encoding]::ASCII); Write-Host ('Prepared '+$files.Count+' Java source files and '+$jars.Count+' libraries.')"
if errorlevel 1 goto :fail

set "CLIENT_CP="
set /p CLIENT_CP=<"%CP_FILE%"
if not defined CLIENT_CP goto :fail

echo.
echo [2/3] Compiling client bundler...
if exist "%CLASSES_DIR%" rmdir /s /q "%CLASSES_DIR%"
mkdir "%CLASSES_DIR%"
"%JAVAC_EXE%" -encoding UTF-8 -source 8 -target 8 -cp "%CLIENT_CP%" -d "%CLASSES_DIR%" @"%SOURCES_FILE%"
if errorlevel 1 goto :fail

echo.
echo [3/3] Patching and packaging client files...
pushd asm
"%JAVA_EXE%" -cp "target\classes;%CLIENT_CP%" net.scapeemulator.asm.bundler.ClientBundler
set "BUILD_RESULT=%ERRORLEVEL%"
popd
if not "%BUILD_RESULT%"=="0" goto :fail

echo.
echo ============================================================
echo   CLIENT BUILD SUCCESSFUL
echo ============================================================
echo Output: %CD%\www
exit /b 0

:fail
echo.
echo ============================================================
echo   CLIENT BUILD FAILED
echo ============================================================
exit /b 1
