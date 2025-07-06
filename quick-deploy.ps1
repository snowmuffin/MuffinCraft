# Quick Version - MuffinCraft Build and Deploy
# Run with PowerShell: .\quick-deploy.ps1

Write-Host "MuffinCraft build and deploy started..." -ForegroundColor Green

# Navigate to MuffinCraft directory
Set-Location $PSScriptRoot

# Execute build and deploy
& ./gradlew clean shadowJar
Copy-Item "build/libs/MuffinCraft.jar" "e:\Game_Hub_Project\MinecraftServer\plugins\" -Force

Write-Host "Deploy completed!" -ForegroundColor Green
