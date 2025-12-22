@echo off
echo Starting Minecraft server...
java -Xms1G -Xmx2G -XX:+UseG1GC -jar finalJar.jar nogui
pause
