package se.tetris.team5.gamelogic.block;

import org.junit.Test;
import static org.junit.Assert.*;
import se.tetris.team5.blocks.Block;

public class BlockFactoryProbabilityTest {
  private static final int TRIALS = 10000; // 충분히 큰 반복 횟수
  private static final double TOLERANCE = 0.05; // ±5%

  @Test
  public void testNormalProbability() {
    BlockFactory factory = new BlockFactory(BlockFactory.Difficulty.NORMAL);
    int[] counts = new int[7];
    for (int i = 0; i < TRIALS; i++) {
      Block b = factory.createRandomBlock();
      counts[getBlockIndex(b)]++;
    }
    double expected = 1.0 / 7.0;
    for (int i = 0; i < 7; i++) {
      double actual = counts[i] / (double) TRIALS;
      assertEquals("NORMAL: block " + i + " freq", expected, actual, TOLERANCE);
    }
  }

  @Test
  public void testEasyProbability() {
    BlockFactory factory = new BlockFactory(BlockFactory.Difficulty.EASY);
    int[] counts = new int[7];
    for (int i = 0; i < TRIALS; i++) {
      Block b = factory.createRandomBlock();
      counts[getBlockIndex(b)]++;
    }
    // I: 12/72, others: 10/72
    double expectedI = 12.0 / 72.0;
    double expectedOther = 10.0 / 72.0;
    for (int i = 0; i < 7; i++) {
      double actual = counts[i] / (double) TRIALS;
      if (i == 0) {
        assertEquals("EASY: I freq", expectedI, actual, TOLERANCE);
      } else {
        assertEquals("EASY: block " + i + " freq", expectedOther, actual, TOLERANCE);
      }
    }
  }

  @Test
  public void testHardProbability() {
    BlockFactory factory = new BlockFactory(BlockFactory.Difficulty.HARD);
    int[] counts = new int[7];
    for (int i = 0; i < TRIALS; i++) {
      Block b = factory.createRandomBlock();
      counts[getBlockIndex(b)]++;
    }
    // I: 8/68, others: 10/68
    double expectedI = 8.0 / 68.0;
    double expectedOther = 10.0 / 68.0;
    for (int i = 0; i < 7; i++) {
      double actual = counts[i] / (double) TRIALS;
      if (i == 0) {
        assertEquals("HARD: I freq", expectedI, actual, TOLERANCE);
      } else {
        assertEquals("HARD: block " + i + " freq", expectedOther, actual, TOLERANCE);
      }
    }
  }

  // I=0, J=1, L=2, Z=3, S=4, T=5, O=6
  private int getBlockIndex(Block b) {
    String name = b.getClass().getSimpleName();
    switch (name) {
      case "IBlock":
        return 0;
      case "JBlock":
        return 1;
      case "LBlock":
        return 2;
      case "ZBlock":
        return 3;
      case "SBlock":
        return 4;
      case "TBlock":
        return 5;
      case "OBlock":
        return 6;
      default:
        throw new IllegalArgumentException("Unknown block: " + name);
    }
  }
}
