@echo off
REM Quick Version - MuffinCraft Build and Deploy

echo MuffinCraft build and deploy started...

REM Navigate to MuffinCraft directory
cd /d "%~dp0"

REM Execute build and deploy
call gradlew.bat clean shadowJar
copy "build\libs\MuffinCraft.jar" "e:\Game_Hub_Project\MinecraftServer\plugins\" /Y

echo Deploy completed!
pause
