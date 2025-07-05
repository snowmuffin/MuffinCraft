# MuffinCraft 플러그인 핫 리로드 스크립트
Write-Host "=== MuffinCraft 플러그인 핫 리로드 ===" -ForegroundColor Green

# MuffinCraft 디렉토리로 이동
Set-Location "e:\Game_Hub_Project\MuffinCraft"

Write-Host "1. 플러그인 빌드 중..." -ForegroundColor Yellow
$buildResult = & ./gradlew clean shadowJar
if ($LASTEXITCODE -ne 0) {
    Write-Host "빌드 실패!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "2. 기존 플러그인 백업 중..." -ForegroundColor Yellow
$pluginPath = "e:\Game_Hub_Project\MinecraftServer\plugins\MuffinCraft.jar"
if (Test-Path $pluginPath) {
    Copy-Item $pluginPath "$pluginPath.backup" -Force
}

Write-Host "3. 새 플러그인 복사 중..." -ForegroundColor Yellow
Copy-Item "build\libs\MuffinCraft.jar" $pluginPath -Force

$fileInfo = Get-Item $pluginPath
Write-Host "4. 복사 완료! 파일 크기: $($fileInfo.Length) bytes" -ForegroundColor Green
Write-Host "   생성 시간: $($fileInfo.LastWriteTime)" -ForegroundColor Green

Write-Host "`n5. 이제 마인크래프트 서버 콘솔에서 다음 명령어를 실행하세요:" -ForegroundColor Cyan
Write-Host "   /reload confirm" -ForegroundColor White

Write-Host "`n주의: /reload는 데이터 손실 위험이 있으므로 개발 중에만 사용하세요!" -ForegroundColor Red
Read-Host "Press Enter to continue"
