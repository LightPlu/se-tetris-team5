package se.tetris.team5.gamelogic.scoring;

/**
 * 게임 점수와 레벨을 관리하는 클래스
 */
public class GameScoring {
  private int currentScore;
  private int level;
  private int linesCleared;
  private int dropSpeed;

  private static final int POINTS_PER_LINE = 100;
  private static final int POINTS_PER_TETRIS = 800;
  private static final int SOFT_DROP_POINTS = 1;
  private static final int HARD_DROP_MULTIPLIER = 2;
  private static final int INITIAL_DROP_SPEED = 1000; // 1초
  private static final int BASE_LEVEL_SPEED_DECREASE = 100; // Normal 기준 레벨당 속도 증가
  private int levelSpeedDecrease = BASE_LEVEL_SPEED_DECREASE;

  /**
   * 난이도에 따라 레벨당 속도 증가폭을 조정합니다.
   * 
   * @param difficulty BlockFactory.Difficulty
   */
  public void setDifficulty(se.tetris.team5.gamelogic.block.BlockFactory.Difficulty difficulty) {
    switch (difficulty) {
      case EASY:
        levelSpeedDecrease = (int) Math.round(BASE_LEVEL_SPEED_DECREASE * 0.8); // 20% 덜 증가
        break;
      case HARD:
        levelSpeedDecrease = (int) Math.round(BASE_LEVEL_SPEED_DECREASE * 1.2); // 20% 더 증가
        break;
      case NORMAL:
      default:
        levelSpeedDecrease = BASE_LEVEL_SPEED_DECREASE;
    }
    // 난이도 변경 시 현재 레벨에 맞춰 속도 재계산
    updateDropSpeed();
  }

  public GameScoring() {
    reset();
  }

  /**
   * 점수 시스템을 초기화합니다
   */
  public void reset() {
    currentScore = 0;
    level = 1;
    linesCleared = 0;
    dropSpeed = INITIAL_DROP_SPEED;
  }

  /**
   * 점수를 추가합니다 (소프트 드롭용)
   * 레벨에 따라 점수가 증가합니다
   */
  public void addPoints(int points) {
    currentScore += points * level;
  }

  /**
   * 줄 클리어에 따른 점수 계산
   */
  public void addLinesCleared(int lines) {
    linesCleared += lines;

    // 줄 수에 따른 점수 계산
    int points = 0;
    switch (lines) {
      case 1:
        points = POINTS_PER_LINE * level;
        break;
      case 2:
        points = POINTS_PER_LINE * 3 * level;
        break;
      case 3:
        points = POINTS_PER_LINE * 5 * level;
        break;
      case 4:
        points = POINTS_PER_TETRIS * level;
        break;
      default:
        points = lines * POINTS_PER_LINE * level;
    }

    currentScore += points;

    // 레벨 업 체크 (10줄마다 레벨 증가)
    int newLevel = (linesCleared / 10) + 1;
    if (newLevel > level) {
      level = newLevel;
      updateDropSpeed();
    }
  }

  /**
   * 하드 드롭 점수 추가
   * 레벨에 따라 점수가 증가
   */
  public void addHardDropPoints(int distance) {
    currentScore += distance * HARD_DROP_MULTIPLIER * level;
  }

  /**
   * 드롭 속도 업데이트
   */
  private void updateDropSpeed() {
    dropSpeed = Math.max(100, INITIAL_DROP_SPEED - ((level - 1) * levelSpeedDecrease));
  }

  /**
   * 현재 타이머 간격 반환 (밀리초)
   */
  public int getTimerInterval() {
    return dropSpeed;
  }

  // Getters
  public int getCurrentScore() {
    return currentScore;
  }

  public int getLevel() {
    return level;
  }

  public int getLinesCleared() {
    return linesCleared;
  }
}
