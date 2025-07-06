# MuffinCraft Plugin Build and Deploy Script
# Created: 2025-07-06

Write-Host "=== MuffinCraft Plugin Build and Deploy Started ===" -ForegroundColor Green

# Set current directory to MuffinCraft project root
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $scriptDir

Write-Host "Current directory: $(Get-Location)" -ForegroundColor Yellow

# 1. Clean existing build files
Write-Host "1. Cleaning existing build files..." -ForegroundColor Cyan
try {
    & ./gradlew clean
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ Build file cleanup completed" -ForegroundColor Green
    } else {
        throw "Clean command execution failed"
    }
} catch {
    Write-Host "   ✗ Build file cleanup failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Execute ShadowJar build
Write-Host "2. Executing ShadowJar build..." -ForegroundColor Cyan
try {
    & ./gradlew shadowJar
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ ShadowJar build completed" -ForegroundColor Green
    } else {
        throw "ShadowJar build failed"
    }
} catch {
    Write-Host "   ✗ ShadowJar build failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. Check built JAR file
$jarFile = "build/libs/MuffinCraft.jar"
Write-Host "3. Checking built JAR file..." -ForegroundColor Cyan
if (Test-Path $jarFile) {
    $fileInfo = Get-Item $jarFile
    Write-Host "   ✓ JAR file exists: $jarFile" -ForegroundColor Green
    Write-Host "   File size: $([math]::Round($fileInfo.Length / 1MB, 2)) MB" -ForegroundColor Yellow
    Write-Host "   Creation time: $($fileInfo.LastWriteTime)" -ForegroundColor Yellow
} else {
    Write-Host "   ✗ JAR file does not exist: $jarFile" -ForegroundColor Red
    exit 1
}

# 4. Check target directory
$targetDir = "e:\Game_Hub_Project\MinecraftServer\plugins"
Write-Host "4. Checking target directory..." -ForegroundColor Cyan
if (Test-Path $targetDir) {
    Write-Host "   ✓ Target directory exists: $targetDir" -ForegroundColor Green
} else {
    Write-Host "   ! Target directory does not exist. Creating..." -ForegroundColor Yellow
    try {
        New-Item -Path $targetDir -ItemType Directory -Force | Out-Null
        Write-Host "   ✓ Target directory creation completed" -ForegroundColor Green
    } catch {
        Write-Host "   ✗ Target directory creation failed: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
}

# 5. Backup existing plugin (optional)
$targetFile = Join-Path $targetDir "MuffinCraft.jar"
if (Test-Path $targetFile) {
    $backupFile = Join-Path $targetDir "MuffinCraft.jar.backup"
    Write-Host "5. Backing up existing plugin..." -ForegroundColor Cyan
    try {
        Copy-Item $targetFile $backupFile -Force
        Write-Host "   ✓ Existing plugin backup completed: MuffinCraft.jar.backup" -ForegroundColor Green
    } catch {
        Write-Host "   ! Backup failed (continuing anyway): $($_.Exception.Message)" -ForegroundColor Yellow
    }
} else {
    Write-Host "5. No existing plugin found, skipping backup." -ForegroundColor Yellow
}

# 6. Copy JAR file
Write-Host "6. Copying JAR file..." -ForegroundColor Cyan
try {
    Copy-Item $jarFile $targetFile -Force
    Write-Host "   ✓ JAR file copy completed" -ForegroundColor Green
    Write-Host "   Copy location: $targetFile" -ForegroundColor Yellow
} catch {
    Write-Host "   ✗ JAR file copy failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 7. Completion message
Write-Host "" -ForegroundColor White
Write-Host "=== Build and Deploy Completed ===" -ForegroundColor Green
Write-Host "MuffinCraft plugin has been successfully built and deployed!" -ForegroundColor Green
Write-Host "Restart the server or use '/reload' command to reload the plugin." -ForegroundColor Yellow
Write-Host "" -ForegroundColor White

# Optional: Automatically open plugins folder in explorer (remove # to enable)
# Write-Host "Would you like to open the plugins folder in explorer? (Y/N): " -ForegroundColor Cyan -NoNewline
# $response = Read-Host
# if ($response -eq "Y" -or $response -eq "y") {
#     Start-Process "explorer.exe" -ArgumentList $targetDir
# }
