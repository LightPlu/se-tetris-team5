package se.tetris.team5.items;

import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.gamelogic.GameEngine;

/**
 * 점수 2배 아이템. 해당 칸이 줄 삭제로 사라지면 20초간 점수 2배 효과를 부여한다.
 */
public class DoubleScoreItem implements Item {
  @Override
  public String getName() {
    return "DoubleScoreItem";
  }

  /**
   * 점수 2배 효과 적용 (GameEngine에 적용)
   */
  @Override
  public void applyEffect(Object target) {
    if (target instanceof GameEngine) {
      ((GameEngine) target).activateDoubleScore(20_000); // 20초 = 20,000ms
    }
  }

  @Override
  public String toString() {
    return "D";
  }
}
