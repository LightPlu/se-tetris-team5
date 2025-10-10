package se.tetris.team5.gamelogic.block;

import java.util.Random;
import se.tetris.team5.blocks.*;

public class BlockFactory {
  private Random random;

  public BlockFactory() {
    this.random = new Random();
  }

  public Block createRandomBlock() {
    int blockType = random.nextInt(7);
    return createBlock(blockType);
  }

  public Block createBlock(int blockType) {
    switch (blockType) {
      case 0:
        return new IBlock();
      case 1:
        return new JBlock();
      case 2:
        return new LBlock();
      case 3:
        return new ZBlock();
      case 4:
        return new SBlock();
      case 5:
        return new TBlock();
      case 6:
        return new OBlock();
      default:
        return new LBlock();
    }
  }

  public void refreshRandomSeed() {
    this.random = new Random(System.currentTimeMillis());
  }
}
