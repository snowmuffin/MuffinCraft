# MuffinCraft 리소스팩 배포 가이드

## 생성된 파일들

✅ **완성된 리소스팩**: `muffincraft-resourcepack.zip`
✅ **SHA-1 해시**: `537B9F97E860811A8D102DCC449463063F8A2A90`
✅ **머핀 텍스처**: 16x16 픽셀 PNG 파일

## 리소스팩 구조

```
muffincraft-resourcepack.zip
├── pack.mcmeta                 # 리소스팩 메타데이터
└── assets/
    └── minecraft/
        ├── models/
        │   └── item/
        │       ├── bread.json          # 빵 아이템에 머핀 모델 추가
        │       └── custom/
        │           └── muffin.json     # 머핀 커스텀 모델
        └── textures/
            └── item/
                └── custom/
                    └── muffin.png      # 머핀 텍스처 (16x16)
```

## 배포 방법

### 1. NestJS 백엔드에서 호스팅 (권장) ⭐

이제 리소스팩이 GameHub NestJS 백엔드에서 호스팅됩니다:

#### 자동 설정 (기본값)
```yaml
# config.yml
api:
  url: 'http://localhost:4000/api'
  
resourcepack:
  url: "http://localhost:4000/api/muffincraft/resourcepack/download"
  sha1: "537B9F97E860811A8D102DCC449463063F8A2A90"
  required: true
```

#### 백엔드 엔드포인트
- **다운로드**: `GET /api/muffincraft/resourcepack/download`
- **정보 조회**: `GET /api/muffincraft/resourcepack/info`
- **상태 확인**: `GET /api/muffincraft/resourcepack/status`

#### 프로덕션 환경 설정
```yaml
# config.yml (프로덕션)
api:
  url: 'https://your-gamehub-server.com/api'
  
resourcepack:
  url: "https://your-gamehub-server.com/api/muffincraft/resourcepack/download"
  required: true
```

### 2. 별도 웹 서버에 업로드 (대안)

리소스팩을 별도 웹 서버에 호스팅하려는 경우:

#### Apache/Nginx 웹 서버
```bash
# 웹 서버 디렉토리에 업로드
sudo cp muffincraft-resourcepack.zip /var/www/html/
sudo chmod 644 /var/www/html/muffincraft-resourcepack.zip
```

#### GitHub Pages (무료 호스팅)
1. GitHub 리포지토리 생성
2. `muffincraft-resourcepack.zip` 파일 업로드
3. GitHub Pages 활성화
4. URL: `https://username.github.io/repository/muffincraft-resourcepack.zip`

#### 클라우드 스토리지 (Google Drive, Dropbox 등)
- 직접 다운로드 링크 생성 필요
- 리다이렉트 없는 직접 링크여야 함

### 3. 백엔드 서버 시작 및 테스트

#### NestJS 서버 시작
```bash
cd game_hub_nest
npm run start:dev
```

#### 리소스팩 엔드포인트 테스트
```bash
# 리소스팩 정보 확인
curl http://localhost:4000/api/muffincraft/resourcepack/info

# 리소스팩 다운로드 테스트
curl -O http://localhost:4000/api/muffincraft/resourcepack/download

# 상태 확인
curl http://localhost:4000/api/muffincraft/resourcepack/status
```

### 4. 서버 설정 (자동 업데이트)

플러그인이 자동으로 백엔드에서 최신 리소스팩 정보를 가져옵니다:
```yaml
resourcepack:
  url: "https://your-actual-server.com/muffincraft-resourcepack.zip"
  sha1: "537B9F97E860811A8D102DCC449463063F8A2A90"
  required: true
```

### 5. 로컬 테스트 (개발용)

로컬에서 테스트하려면 간단한 HTTP 서버를 실행:

#### Python HTTP 서버
```bash
cd resourcepack
python -m http.server 8000
# URL: http://localhost:8000/muffincraft-resourcepack.zip
```

#### Node.js HTTP 서버
```bash
npx http-server resourcepack -p 8000
# URL: http://localhost:8000/muffincraft-resourcepack.zip
```

## 사용 방법

### 1. 서버 관리자
1. 플러그인 설치 및 설정
2. 리소스팩을 웹 서버에 업로드
3. `config.yml`에 올바른 URL 설정
4. 서버 재시작

### 2. 플레이어
1. 서버 접속 시 자동으로 리소스팩 다운로드 메시지 표시
2. "예"를 클릭하여 리소스팩 적용
3. `/muffin give 1` 명령어로 머핀 아이템 테스트 (관리자 권한 필요)
4. 머핀 아이템이 커스텀 텍스처로 표시됨

## 커스텀 아이템 사용법

### 머핀 아이템
- **획득**: `/muffin give <수량>` (관리자 전용)
- **사용**: 우클릭으로 먹기
- **효과**: 체력 +2하트, 배고픔 +6, 포화도 +8
- **외관**: 황금색/갈색 머핀 모양

### CustomModelData 값
- **머핀**: 1001
- **향후 아이템들**: 1002, 1003, ...

## 문제 해결

### 리소스팩이 다운로드되지 않는 경우
1. URL이 올바른지 확인
2. 웹 서버가 접근 가능한지 확인
3. 파일 크기 제한 확인 (보통 50MB 이하)
4. CORS 설정 확인 (필요한 경우)

### 머핀 아이템이 빵으로 보이는 경우
1. 리소스팩이 올바르게 적용되었는지 확인
2. CustomModelData 값 확인 (1001)
3. 클라이언트 재접속 시도

### SHA-1 해시 오류
1. 파일이 변경된 경우 새로운 해시 계산:
   ```powershell
   Get-FileHash -Algorithm SHA1 muffincraft-resourcepack.zip
   ```
2. `config.yml`에 새로운 해시값 업데이트

## 향후 확장

### 새로운 커스텀 아이템 추가
1. 텍스처 파일 생성 (`*.png`)
2. 모델 파일 생성 (`*.json`)
3. 베이스 아이템에 override 추가
4. CustomModelData 값 할당
5. Java 코드에서 아이템 등록
6. 리소스팩 재빌드 및 배포

### 고급 기능
- 3D 모델 지원 (Blockbench 사용)
- 애니메이션 텍스처
- 사운드 추가
- 폰트 커스터마이징

## 파일 위치

- **리소스팩 ZIP**: `e:\Game_Hub_Project\MuffinCraft\resourcepack\muffincraft-resourcepack.zip`
- **소스 폴더**: `e:\Game_Hub_Project\MuffinCraft\resourcepack\`
- **플러그인 설정**: `e:\Game_Hub_Project\MuffinCraft\src\main\resources\config.yml`

이제 MuffinCraft 서버에 접속하는 모든 플레이어가 자동으로 머핀 커스텀 아이템을 볼 수 있습니다! 🧁
