package se.tetris.team5.gamelogic.ai;

/**
 * 유전 알고리즘의 개체 (Individual)
 * 가중치 집합과 적합도를 담음
 */
public class Individual implements Comparable<Individual> {
  public WeightSet weights;
  public double fitness;
  public int gamesPlayed;

  // 게임 통계
  public int totalLinesCleared;
  public int totalScore;
  public int totalGames;
  public int wins; // 승리 횟수
  public int losses; // 패배 횟수
  public long totalGameTimeMs; // 총 게임 시간 (밀리초)
  public double averageLinesPerGame;
  public double averageScorePerGame;
  public double averageGameTimeMs; // 평균 게임 시간 (밀리초)

  public Individual(WeightSet weights) {
    this.weights = weights;
    this.fitness = 0.0;
    this.gamesPlayed = 0;
    this.totalLinesCleared = 0;
    this.totalScore = 0;
    this.totalGames = 0;
    this.wins = 0;
    this.losses = 0;
    this.totalGameTimeMs = 0;
    this.averageLinesPerGame = 0.0;
    this.averageScorePerGame = 0.0;
    this.averageGameTimeMs = 0.0;
  }

  /**
   * 게임 결과를 추가하고 적합도 업데이트
   * 개선된 적합도 함수: 생존 시간, 줄 삭제, 점수, 승리/패배를 종합적으로 고려
   * 생존 능력 향상을 위해 생존 시간 보너스 강화
   * 
   * @param linesCleared 삭제한 줄 수
   * @param score 점수
   * @param isWin 승리 여부 (true: 승리, false: 패배, null: 무승부)
   * @param gameTimeMs 게임 시간 (밀리초)
   */
  public void addGameResult(int linesCleared, int score, Boolean isWin, long gameTimeMs) {
    totalLinesCleared += linesCleared;
    totalScore += score;
    totalGames++;
    totalGameTimeMs += gameTimeMs;
    
    if (isWin != null) {
      if (isWin) {
        wins++;
      } else {
        losses++;
      }
    }
    
    averageLinesPerGame = (double) totalLinesCleared / totalGames;
    averageScorePerGame = (double) totalScore / totalGames;
    averageGameTimeMs = (double) totalGameTimeMs / totalGames;

    // 개선된 적합도 계산 (생존 능력 강화)
    // 1. 생존 시간 보너스 (평균 생존 시간에 비례) - 강화
    // 실제 5분 = 평가 환경 19초 (18750ms) 기준
    double survivalTimeBonus = averageGameTimeMs / 1000.0 * 10.0; // 1초당 10점
    
    // 2. 생존 보너스 (게임 수에 비례) - 증가
    double survivalBonus = totalGames * 20.0; // 5.0 -> 20.0으로 증가
    
    // 3. 줄 삭제 보너스 (유지)
    double linesBonus = averageLinesPerGame * 100.0;
    
    // 4. 점수 보너스 (유지)
    double scoreBonus = averageScorePerGame * 0.5;
    
    // 5. 승리 보너스 (감소) - 생존과의 균형
    double winBonus = wins * 500.0; // 1000.0 -> 500.0으로 감소
    
    // 6. 패배 패널티 (증가) - 생존 실패에 대한 강한 패널티
    double lossPenalty = losses * 1000.0; // 500.0 -> 1000.0으로 증가
    
    // 7. 조기 게임 오버 패널티 (증가)
    double earlyGameOverPenalty = (score == 0 && totalGames > 0) ? -500.0 : 0.0; // -100 -> -500
    
    // 8. 장기 생존 보너스 (평균 19초 이상 생존 시) - 실제 5분에 해당
    double longSurvivalBonus = (averageGameTimeMs > 18750) ? 1000.0 : 0.0; // 5분(19초) 이상 생존 시 큰 보너스
    
    fitness = survivalTimeBonus + survivalBonus + linesBonus + scoreBonus 
            + winBonus - lossPenalty + earlyGameOverPenalty + longSurvivalBonus;
  }

  /**
   * 게임 결과를 추가하고 적합도 업데이트 (기존 호환성 유지)
   */
  public void addGameResult(int linesCleared, int score, Boolean isWin) {
    addGameResult(linesCleared, score, isWin, 0);
  }

  /**
   * 게임 결과를 추가하고 적합도 업데이트 (기존 호환성 유지)
   */
  public void addGameResult(int linesCleared, int score) {
    // 승리/패배 정보 없이 호출된 경우, 점수로 추정
    // 점수가 10000 이상이면 승리로 간주 (승리 보너스 포함)
    Boolean isWin = null;
    if (score >= 10000) {
      isWin = true;
    } else if (score < 0) {
      isWin = false;
    }
    addGameResult(linesCleared, score, isWin);
  }

  /**
   * 적합도 초기화
   */
  public void resetFitness() {
    fitness = 0.0;
    gamesPlayed = 0;
    totalLinesCleared = 0;
    totalScore = 0;
    totalGames = 0;
    wins = 0;
    losses = 0;
    totalGameTimeMs = 0;
    averageLinesPerGame = 0.0;
    averageScorePerGame = 0.0;
    averageGameTimeMs = 0.0;
  }

  @Override
  public int compareTo(Individual other) {
    return Double.compare(other.fitness, this.fitness); // 내림차순 정렬
  }

  @Override
  public String toString() {
    return String.format("Fitness: %.2f (Lines: %.1f, Score: %.0f) - %s", 
        fitness, averageLinesPerGame, averageScorePerGame, weights.toString());
  }
}

