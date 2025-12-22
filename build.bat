@echo off

REM Build the project with Maven
echo Building project with Maven...
call mvn clean package

REM Check if the build was successful
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven build failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build successful!
echo Plugin copied to plugins folder.
echo Use "/plugman reload LucaPlugin" to hot reload.
echo ========================================
