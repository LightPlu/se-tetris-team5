# 🎮 ChainSaw Tetris - EXE 파일 배포 완료!

## ✅ 네이티브 EXE 파일 생성 성공!

**JPackage로 생성된 진짜 .exe 파일이 준비되었습니다!** 🚀

## 📦 배포 옵션 3가지

### 1️⃣ **네이티브 EXE 파일 (권장)**

```
📂 app\build\native-exe\ChainSaw-Tetris\
├── 🎮 ChainSaw-Tetris.exe  (479KB) - 진짜 네이티브 실행파일
├── 📁 app\                          - 게임 데이터
└── 📁 runtime\                      - 내장된 Java 런타임
```

**장점**:

- ✅ Java 설치 불필요 (런타임 내장)
- ✅ 더블클릭으로 바로 실행
- ✅ Windows 네이티브 앱처럼 동작
- ✅ 아이콘 자동 적용

### 2️⃣ **JAR + 배치파일 (호환성 높음)**

```
📂 app\build\windows-package\
├── 🎮 ChainSaw-Tetris.jar  (38.9MB) - Fat JAR
├── 🚀 ChainSaw-Tetris.bat  (889B)   - 실행 스크립트
└── 📄 README.txt           (883B)   - 사용자 가이드
```

**장점**:

- ✅ 모든 Java 버전 호환
- ✅ 파일 크기 작음
- ✅ 간단한 구조

### 3️⃣ **배포용 ZIP 파일**

```
📦 app\build\distributions\ChainSaw-Tetris-v1.0-Windows.zip (38.7MB)
```

## 🚀 **사용자 실행 방법**

### 네이티브 EXE 방식 (권장):

1. `ChainSaw-Tetris` 폴더를 통째로 사용자에게 제공
2. 폴더 안의 `ChainSaw-Tetris.exe` 더블클릭
3. **Java 설치 없이 바로 실행!** 🎯

### JAR 방식:

1. ZIP 파일 압축 해제
2. `ChainSaw-Tetris.bat` 더블클릭
3. Java가 필요함

## 🎯 **배포 권장사항**

### 일반 사용자용 (권장):

- **네이티브 EXE** 사용
- Java 설치 걱정 없음
- 폴더 크기: 약 200MB (런타임 포함)

### 개발자/고급 사용자용:

- **JAR + 배치파일** 사용
- 파일 크기 작음 (38MB)
- Java 8+ 필요

## 🔧 **네이티브 EXE 재생성 방법**

```bash
# JPackage로 네이티브 실행파일 생성
jpackage --input app\build\libs \
  --name "ChainSaw-Tetris" \
  --main-jar "app-fat.jar" \
  --main-class "se.tetris.team5.App" \
  --type app-image \
  --dest app\build\native-exe \
  --app-version "1.0" \
  --description "ChainSaw Tetris Game - Team 5" \
  --vendor "Team 5"
```

## 📋 **시스템 요구사항**

### 네이티브 EXE:

- ✅ Windows 10 이상
- ✅ **Java 설치 불필요** (런타임 내장)
- ✅ 메모리: 512MB
- ✅ 디스크: 200MB

### JAR 버전:

- ✅ Windows 10 이상
- ✅ Java 8+ 설치 필요
- ✅ 메모리: 512MB
- ✅ 디스크: 50MB

---

## 🎉 **배포 완료!**

**두 가지 배포 방식 모두 준비완료:**

1. **네이티브 EXE**: Java 없이 바로 실행 (일반 사용자용)
2. **JAR + 배치파일**: 호환성 높음 (개발자용)

**더블클릭으로 간편하게 즐기는 ChainSaw Tetris!** 🎮✨
