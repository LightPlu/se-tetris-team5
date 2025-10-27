package se.tetris.team5.gamelogic.block;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import se.tetris.team5.blocks.*;

public class BlockFactory {
  private Random random;
  private ArrayList<Integer> bag;
  private int bagIndex;

  public BlockFactory() {
    this.random = new Random();
    this.bag = new ArrayList<>();
    this.bagIndex = 0;
    refillBag();
  }

  public Block createRandomBlock() {
    if (bagIndex >= bag.size()) {
      refillBag();
    }
    int blockType = bag.get(bagIndex);
    bagIndex++;
    return createBlock(blockType);
  }

  private void refillBag() {
    bag.clear();
    for (int i = 0; i < 7; i++) {
      bag.add(i);
    }
    Collections.shuffle(bag, random);
    bagIndex = 0;
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
