# 테트리스 AI 유전 알고리즘 문서

이 디렉토리에는 테트리스 AI의 유전 알고리즘 훈련과 관련된 모든 문서가 포함되어 있습니다.

## 문서 구조

### 주요 문서
1. **[AI_TRAINING_HISTORY.md](./AI_TRAINING_HISTORY.md)**: 훈련 이력 및 방법론
   - 실패했던 훈련 방법들
   - 현재 진행 중인 훈련 방법
   - 훈련 결과 비교
   - 주요 개선 사항

2. **[01_RUN_GENETIC_ALGORITHM.md](./01_RUN_GENETIC_ALGORITHM.md)**: 유전 알고리즘 실행 방법
   - IDE에서 실행하는 방법
   - 터미널에서 실행하는 방법
   - 예상 소요 시간
   - 결과 확인 방법

3. **[02_WEIGHT_COMPARISON_ANALYSIS.md](./02_WEIGHT_COMPARISON_ANALYSIS.md)**: 가중치 비교 분석
   - El-Tetris vs 유전 알고리즘 가중치 비교
   - 차이점 분석
   - 평가 함수 비교

### 보고서 디렉토리
- **[reports/](./reports/)**: 유전 알고리즘 실행 결과 보고서
  - `report_20251130_125500_initial_attempt.txt`: 초기 시도 (50세대)
  - `report_20251130_134541_improved_attempt.txt`: 개선 시도 (50세대)
  - `report_20251130_141902_ai_vs_ai.txt`: AI vs AI 대전 모드 적용 (50세대)
  - `report_29generations_old_method.txt`: 29세대까지 진행 (El-Tetris 미적용)
  - `report_20251201_133348_el_tetris_based.txt`: El-Tetris 알고리즘 기반 훈련 (10세대) ⭐ 최신
  
  자세한 내용은 [reports/README.md](./reports/README.md)를 참고하세요.

## 빠른 시작

### 유전 알고리즘 실행
```bash
cd /Users/jeongdaun/source/se-tetris-team5
java -cp "app/build/classes/java/main:app/build/libs/*" \
  -Dfile.encoding=UTF-8 \
  se.tetris.team5.gamelogic.ai.GeneticAlgorithmRunner
```

자세한 실행 방법은 [01_RUN_GENETIC_ALGORITHM.md](./01_RUN_GENETIC_ALGORITHM.md)를 참고하세요.

### 훈련 이력 확인
[AI_TRAINING_HISTORY.md](./AI_TRAINING_HISTORY.md)에서 지금까지의 훈련 방법과 결과를 확인할 수 있습니다.

## 파일 명명 규칙

### 문서 파일
- `01_`, `02_`, ...: 문서 순서
- 대문자와 언더스코어 사용: `RUN_GENETIC_ALGORITHM.md`
- 설명적 이름 사용

### 보고서 파일
- `genetic_algorithm_report_YYYYMMDD_HHMMSS.txt`: 타임스탬프 포함
- `genetic_algorithm_report_Ngenerations.txt`: 세대 수 포함

## 업데이트 이력

- **2025-11-30**: 초기 문서 작성
  - 훈련 이력 문서화
  - 기존 문서 정리 및 체계화
  - 보고서 파일 정리

