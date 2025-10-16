package se.tetris.team5.gamelogic.scoring;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameScoringTest {
  private GameScoring scoring;

  @Before
  public void setUp() {
    scoring = new GameScoring();
  }

  @Test
  public void testInitialState() {
    assertEquals(0, scoring.getCurrentScore());
    assertEquals(1, scoring.getLevel());
    assertEquals(0, scoring.getLinesCleared());
  }

  @Test
  public void testAddPoints() {
    scoring.addPoints(50);
    assertEquals(50, scoring.getCurrentScore());
  }

  @Test
  public void testAddLinesCleared_1Line() {
    scoring.addLinesCleared(1);
    assertEquals(100, scoring.getCurrentScore());
    assertEquals(1, scoring.getLinesCleared());
  }

  @Test
  public void testAddLinesCleared_2Lines() {
    scoring.addLinesCleared(2);
    assertEquals(300, scoring.getCurrentScore());
    assertEquals(2, scoring.getLinesCleared());
  }

  @Test
  public void testAddLinesCleared_3Lines() {
    scoring.addLinesCleared(3);
    assertEquals(500, scoring.getCurrentScore());
    assertEquals(3, scoring.getLinesCleared());
  }

  @Test
  public void testAddLinesCleared_4Lines_Tetris() {
    scoring.addLinesCleared(4);
    assertEquals(800, scoring.getCurrentScore());
    assertEquals(4, scoring.getLinesCleared());
  }

  @Test
  public void testLevelUpAndBonus() {
    for (int i = 0; i < 10; i++) {
      scoring.addLinesCleared(1);
    }
    assertEquals(2, scoring.getLevel());
    int expectedScore = 100 * 10 + 1000;
    assertEquals(expectedScore, scoring.getCurrentScore());
  }

  @Test
  public void testTickScore() throws InterruptedException {
    int before = scoring.getCurrentScore();
    Thread.sleep(1100);
    scoring.tickScore();
    assertTrue(scoring.getCurrentScore() > before);
  }

  @Test
  public void testAddHardDropPoints() {
    scoring.addHardDropPoints(5);
    assertEquals(10, scoring.getCurrentScore());
  }
}