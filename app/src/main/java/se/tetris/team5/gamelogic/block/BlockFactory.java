package se.tetris.team5.gamelogic.block;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.tetris.team5.blocks.*;

public class BlockFactory {
  private Random random;
  // 7-bag implementation to avoid unfair streaks/repeats
  private List<Integer> bag;

  public BlockFactory() {
    this.random = new Random();
    this.bag = new ArrayList<>();
    refillBag();
  }

  public Block createRandomBlock() {
    if (bag.isEmpty()) refillBag();
    int blockType = bag.remove(0);
    Block b = createBlock(blockType);
    return b;
  }

  private void refillBag() {
    bag.clear();
    for (int i = 0; i < 7; i++) bag.add(i);
    Collections.shuffle(bag, random);
    // small debug
    // System.out.println("[BlockFactory DEBUG] refillBag order=" + bag);
  }

  /**
   * 무게추 블록(WBlock)을 생성합니다.
   * 10줄 삭제 시 일반 블록 대신 생성될 수 있는 특수 블록입니다.
   */
  public Block createWeightBlock() {
    return new WBlock();
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
