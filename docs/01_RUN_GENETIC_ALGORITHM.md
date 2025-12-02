# 유전 알고리즘 실행 방법

## 방법 1: IDE에서 실행 (가장 간단)

### VS Code / Cursor
1. `app/src/main/java/se/tetris/team5/gamelogic/ai/GeneticAlgorithmRunner.java` 파일 열기
2. `main` 메서드 위에 있는 "Run" 버튼 클릭
3. 또는 `F5` 키를 누르고 "Java" 선택

### IntelliJ IDEA
1. `GeneticAlgorithmRunner.java` 파일 열기
2. `main` 메서드 옆의 초록색 실행 버튼 클릭
3. 또는 `Shift + F10`

## 방법 2: 터미널에서 실행

### Gradle 사용
```bash
cd /Users/jeongdaun/source/se-tetris-team5
./gradlew :app:run --args="se.tetris.team5.gamelogic.ai.GeneticAlgorithmRunner"
```

또는 직접 Java 실행:
```bash
cd /Users/jeongdaun/source/se-tetris-team5
./gradlew :app:build
java -cp "app/build/classes/java/main:app/build/libs/*" se.tetris.team5.gamelogic.ai.GeneticAlgorithmRunner
```

## 방법 3: VS Code Launch Configuration 추가

`.vscode/launch.json`에 다음 설정 추가:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Run App (UTF-8)",
      "request": "launch",
      "mainClass": "se.tetris.team5.App",
      "vmArgs": "-Dfile.encoding=UTF-8"
    },
    {
      "type": "java",
      "name": "Run Genetic Algorithm",
      "request": "launch",
      "mainClass": "se.tetris.team5.gamelogic.ai.GeneticAlgorithmRunner",
      "vmArgs": "-Dfile.encoding=UTF-8"
    }
  ]
}
```

그 다음 `F5` 키를 누르고 "Run Genetic Algorithm" 선택

## 실행 시 출력 예시

```
=== 테트리스 AI 가중치 최적화 (유전 알고리즘) ===

=== 유전 알고리즘 시작 ===
집단 크기: 20
엘리트 개체 수: 4
최대 세대 수: 5
기본 돌연변이 확률: 0.15 (적응적)
교차 확률: 0.7

※ 게임은 메모리 상에서만 실행되며 UI는 없습니다.
※ 빠른 평가 모드: 게임당 최대 10초 (실제 5분 = 평가 8초), 최대 1000회 이동
※ 정밀 평가 모드: 게임당 최대 20초 (실제 5분 = 평가 19초), 최대 2000회 이동

초기 집단 생성 완료: 20개 개체

=== 세대 0 평가 시작 ===
개체 1/20 평가 완료 (1.2초) - 적합도: 45.30
개체 2/20 평가 완료 (1.5초) - 적합도: 38.20
...
```

## 예상 소요 시간

**현재 설정 (집단 크기 20, 세대 수 5)**:
- **예상 시간**: 약 10-15분
- 각 세대당 약 2-3분 소요

**이전 설정 (집단 크기 30, 세대 수 10)**:
- **최선의 경우**: 약 15-20분
- **평균적인 경우**: 약 25-30분
- **최악의 경우**: 약 35-40분

## 결과 확인

실행이 완료되면 다음과 같은 결과가 출력됩니다:

```
=== 최적화 완료 ===
최적 가중치:
Landing Height: -7.80, EPCM: 5.58, Row Transitions: -1.58, Column Transitions: -1.13
Holes: -18.80, Well Sums: -8.69, Attack 2 Lines: 15.14, Attack 3 Lines: 19.18, Attack 4 Lines: 59.16

최종 성능:
평균 줄 수: 4.0
평균 점수: 2034
적합도: -2424.21

=== 코드에 사용할 가중치 ===
new WeightSet(-7.80, 5.58, -1.58, -1.13, -18.80, -8.69, 15.14, 19.18, 59.16)
```

이 가중치를 `TetrisAI`에 적용하면 최적화된 AI를 사용할 수 있습니다!

