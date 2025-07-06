@echo off
REM MuffinCraft Plugin Build and Deploy Script (Batch File Version)
REM Created: 2025-07-06

echo === MuffinCraft Plugin Build and Deploy Started ===
echo.

REM Navigate to MuffinCraft project root directory
cd /d "%~dp0"
echo Current directory: %CD%
echo.

REM 1. Clean existing build files
echo 1. Cleaning existing build files...
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo    X Build file cleanup failed
    pause
    exit /b 1
)
echo    √ Build file cleanup completed
echo.

REM 2. Execute ShadowJar build
echo 2. Executing ShadowJar build...
call gradlew.bat shadowJar
if %ERRORLEVEL% NEQ 0 (
    echo    X ShadowJar build failed
    pause
    exit /b 1
)
echo    √ ShadowJar build completed
echo.

REM 3. Check built JAR file
echo 3. Checking built JAR file...
if not exist "build\libs\MuffinCraft.jar" (
    echo    X JAR file does not exist: build\libs\MuffinCraft.jar
    pause
    exit /b 1
)
echo    √ JAR file exists: build\libs\MuffinCraft.jar
echo.

REM 4. Check and create target directory
echo 4. Checking target directory...
set TARGET_DIR=e:\Game_Hub_Project\MinecraftServer\plugins
if not exist "%TARGET_DIR%" (
    echo    ! Target directory does not exist. Creating...
    mkdir "%TARGET_DIR%"
    if %ERRORLEVEL% NEQ 0 (
        echo    X Target directory creation failed
        pause
        exit /b 1
    )
    echo    √ Target directory creation completed
) else (
    echo    √ Target directory exists: %TARGET_DIR%
)
echo.

REM 5. Backup existing plugin
echo 5. Backing up existing plugin...
if exist "%TARGET_DIR%\MuffinCraft.jar" (
    copy "%TARGET_DIR%\MuffinCraft.jar" "%TARGET_DIR%\MuffinCraft.jar.backup" >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo    √ Existing plugin backup completed: MuffinCraft.jar.backup
    ) else (
        echo    ! Backup failed ^(continuing anyway^)
    )
) else (
    echo    No existing plugin found, skipping backup.
)
echo.

REM 6. Copy JAR file
echo 6. Copying JAR file...
copy "build\libs\MuffinCraft.jar" "%TARGET_DIR%\MuffinCraft.jar" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo    X JAR file copy failed
    pause
    exit /b 1
)
echo    √ JAR file copy completed
echo    Copy location: %TARGET_DIR%\MuffinCraft.jar
echo.

REM 7. Completion message
echo === Build and Deploy Completed ===
echo MuffinCraft plugin has been successfully built and deployed!
echo Restart the server or use '/reload' command to reload the plugin.
echo.

echo Press any key to continue...
pause >nul
