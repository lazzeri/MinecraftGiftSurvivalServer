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

REM Check if the server JAR exists
if not exist "finalJar.jar" (
    echo ERROR: Server jar not found at finalJar.jar
    pause
    exit /b 1
)

REM Start Minecraft server
echo.
echo ========================================
echo Starting Minecraft server...
echo ========================================
java -Xms1G -Xmx2G -XX:+UseG1GC -jar finalJar.jar nogui

pause
