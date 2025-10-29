package se.tetris.team5.gamelogic.scoring;

import org.junit.Test;
import static org.junit.Assert.*;
import se.tetris.team5.gamelogic.block.BlockFactory;

public class GameScoringSpeedTest {
  @Test
  public void testNormalSpeedIncrease() {
    GameScoring scoring = new GameScoring();
    scoring.setDifficulty(BlockFactory.Difficulty.NORMAL);
    // 1레벨: 1000, 2레벨: 900, 3레벨: 800, ...
    assertEquals(1000, scoring.getTimerInterval());
    for (int i = 1; i <= 9; i++)
      scoring.addLinesCleared(1); // 9줄
    assertEquals(1000, scoring.getTimerInterval()); // 아직 1레벨
    scoring.addLinesCleared(1); // 10줄째
    assertEquals(900, scoring.getTimerInterval()); // 2레벨
    for (int i = 1; i <= 10; i++)
      scoring.addLinesCleared(1); // 20줄째
    assertEquals(800, scoring.getTimerInterval()); // 3레벨
  }

  @Test
  public void testEasySpeedIncrease() {
    GameScoring scoring = new GameScoring();
    scoring.setDifficulty(BlockFactory.Difficulty.EASY);
    assertEquals(1000, scoring.getTimerInterval());
    for (int i = 1; i <= 10; i++)
      scoring.addLinesCleared(1); // 10줄
    assertEquals(920, scoring.getTimerInterval()); // 2레벨
    for (int i = 1; i <= 10; i++)
      scoring.addLinesCleared(1); // 20줄
    assertEquals(840, scoring.getTimerInterval()); // 3레벨
  }

  @Test
  public void testHardSpeedIncrease() {
    GameScoring scoring = new GameScoring();
    scoring.setDifficulty(BlockFactory.Difficulty.HARD);
    assertEquals(1000, scoring.getTimerInterval());
    for (int i = 1; i <= 10; i++)
      scoring.addLinesCleared(1); // 10줄
    assertEquals(880, scoring.getTimerInterval()); // 2레벨
    for (int i = 1; i <= 10; i++)
      scoring.addLinesCleared(1); // 20줄
    assertEquals(760, scoring.getTimerInterval()); // 3레벨
  }

  @Test
  public void testSpeedMinimum100ms() {
    GameScoring scoring = new GameScoring();
    scoring.setDifficulty(BlockFactory.Difficulty.HARD);
    // 1000 - 120*8 = 40, 최소 100ms로 제한
    for (int i = 1; i <= 90; i++)
      scoring.addLinesCleared(1);
    assertEquals(100, scoring.getTimerInterval());
  }
}
