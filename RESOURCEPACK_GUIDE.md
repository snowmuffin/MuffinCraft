# MuffinCraft 리소스팩 생성 가이드

이 가이드는 MuffinCraft 플러그인용 커스텀 아이템 리소스팩을 만드는 방법을 설명합니다.

## 1. 리소스팩 구조

```
muffincraft-resourcepack/
├── pack.mcmeta
├── assets/
│   └── minecraft/
│       ├── models/
│       │   └── item/
│       │       ├── bread.json
│       │       └── custom/
│       │           └── muffin.json
│       └── textures/
│           └── item/
│               └── custom/
│                   └── muffin.png
```

## 2. pack.mcmeta 파일 생성

```json
{
  "pack": {
    "pack_format": 15,
    "description": "MuffinCraft Custom Items Resource Pack"
  }
}
```

**pack_format 버전:**
- Minecraft 1.20.x: 15
- Minecraft 1.19.x: 9
- Minecraft 1.18.x: 8

## 3. 커스텀 아이템 모델 설정

### bread.json (수정)
`assets/minecraft/models/item/bread.json`

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "item/bread"
  },
  "overrides": [
    {
      "predicate": {
        "custom_model_data": 1001
      },
      "model": "item/custom/muffin"
    }
  ]
}
```

### muffin.json (새로 생성)
`assets/minecraft/models/item/custom/muffin.json`

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "item/custom/muffin"
  }
}
```

## 4. 텍스처 파일

### muffin.png
`assets/minecraft/textures/item/custom/muffin.png`

- 크기: 16x16 픽셀 (기본) 또는 32x32, 64x64 등 (고해상도)
- 포맷: PNG
- 투명 배경 지원
- 머핀 모양의 아이콘 디자인

**디자인 팁:**
- 황금색/갈색 베이스로 맛있어 보이게
- 작은 크기에서도 인식 가능하도록 단순하게
- 기존 Minecraft 스타일과 어울리도록

## 5. 리소스팩 빌드 및 배포

### 5.1 ZIP 파일 생성
1. 리소스팩 폴더의 **내용물**을 선택 (폴더 자체가 아님)
2. ZIP으로 압축
3. 파일명: `muffincraft-resourcepack.zip`

### 5.2 웹 서버에 업로드
1. HTTP/HTTPS로 접근 가능한 웹 서버에 업로드
2. 직접 다운로드 링크 확인 (리다이렉트 없이)
3. CORS 설정 (필요한 경우)

### 5.3 SHA-1 해시 계산
```bash
# Windows PowerShell
Get-FileHash -Algorithm SHA1 muffincraft-resourcepack.zip

# Linux/Mac
sha1sum muffincraft-resourcepack.zip
```

### 5.4 서버 설정 업데이트
`config.yml` 파일 수정:
```yaml
resourcepack:
  url: "https://your-server.com/path/to/muffincraft-resourcepack.zip"
  sha1: "여기에_계산된_SHA1_해시값_입력"
  required: true
```

## 6. 테스트 방법

### 6.1 로컬 테스트
1. 리소스팩을 Minecraft 리소스팩 폴더에 복사
2. 게임에서 리소스팩 활성화
3. `/muffin give 1` 명령어로 아이템 생성
4. 텍스처가 올바르게 표시되는지 확인

### 6.2 서버 테스트
1. 서버에 플러그인과 설정 적용
2. 플레이어 접속 시 자동 다운로드 확인
3. 커스텀 아이템 생성 및 사용 테스트

## 7. 문제 해결

### 리소스팩이 다운로드되지 않는 경우
- URL이 올바른지 확인
- 서버가 HTTP/HTTPS로 접근 가능한지 확인
- 파일 크기 제한 확인 (일반적으로 50MB 이하)

### 커스텀 모델이 표시되지 않는 경우
- CustomModelData 값이 일치하는지 확인
- JSON 문법 오류 확인
- 텍스처 파일 경로 확인

### SHA-1 해시 오류
- 파일이 변경된 경우 해시 재계산
- 정확한 해시값 입력 확인

## 8. 향후 확장

### 추가 커스텀 아이템
새로운 아이템 추가 시:
1. `CustomItemManager.java`에서 아이템 등록
2. 베이스 아이템의 모델 파일에 override 추가
3. 새로운 모델과 텍스처 파일 생성
4. CustomModelData 값을 고유하게 설정

### 권장 CustomModelData 값
- 머핀: 1001
- 다른 음식: 1002-1099
- 도구: 1100-1199
- 장비: 1200-1299
- 기타: 1300+

## 9. 유용한 도구

### 리소스팩 생성 도구
- [Blockbench](https://blockbench.net/) - 3D 모델 생성
- [MCreator](https://mcreator.net/) - 모드/리소스팩 생성
- [ItemsAdder](https://github.com/LoneDev6/ItemsAdder) - 플러그인 기반 커스텀 아이템

### 텍스처 편집 도구
- GIMP (무료)
- Photoshop
- Aseprite (픽셀 아트 특화)

### JSON 검증 도구
- [JSONLint](https://jsonlint.com/)
- VS Code JSON 확장

이 가이드를 따라 리소스팩을 생성하면 MuffinCraft 플러그인의 커스텀 아이템들이 올바르게 표시됩니다.
