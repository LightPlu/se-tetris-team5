package se.tetris.team5.items;

/**
 * 예시: 점수 증가 아이템
 */
public class ScoreItem implements Item {
  private final int scoreAmount;

  public ScoreItem(int scoreAmount) {
    this.scoreAmount = scoreAmount;
  }

  @Override
  public String getName() {
    return "ScoreItem";
  }

  @Override
  public void applyEffect(Object target) {
    // target이 ScoreManager 등일 때 점수 증가
    // 실제 적용은 게임 컨트롤러에서 구현
  }

  public int getScoreAmount() {
    return scoreAmount;
  }
}
