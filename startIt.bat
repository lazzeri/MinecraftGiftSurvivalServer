@echo off
setlocal

REM Paths
set SOURCE_JAR=target\lucaPlugin.jar
set DEST_JAR="C:\Users\lucal\OneDrive\Documents\Coding Projects\MinecraftGiftSurvivalServer\plugins\lucaPlugin.jar"
set YOYO_SCRIPT=yoyo.bat

REM Check if the JAR exists
if not exist "%SOURCE_JAR%" (
    echo ERROR: Plugin jar not found at %SOURCE_JAR%
    pause
    exit /b 1
)

REM Copy and replace the jar
echo Copying %SOURCE_JAR% to %DEST_JAR%
copy /Y "%SOURCE_JAR%" %DEST_JAR%

REM Run yoyo.bat
echo Running %YOYO_SCRIPT%
call %YOYO_SCRIPT%

endlocal
