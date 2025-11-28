package se.tetris.team5.items;

import java.util.Random;
import se.tetris.team5.blocks.Block;

/**
 * 지정한 줄 수마다 빠르게 아이템을 부여하는 정책.
 * 테스트 편의 기능으로 사용한다.
 */
public class FrequentItemGrantPolicy implements ItemGrantPolicy {
  private final int linesPerGrant;
  private final Random random = new Random();
  private int lastGrantLine = 0;

  public FrequentItemGrantPolicy(int linesPerGrant) {
    if (linesPerGrant <= 0) {
      throw new IllegalArgumentException("linesPerGrant must be positive");
    }
    this.linesPerGrant = linesPerGrant;
  }

  @Override
  public Item grantItem(Block block, ItemGrantContext context) {
    if (block == null || context == null) {
      return null;
    }
    if (context.totalClearedLines > 0 &&
        context.totalClearedLines >= lastGrantLine + linesPerGrant) {

      int w = block.width();
      int h = block.height();
      int x, y;

      do {
        x = random.nextInt(w);
        y = random.nextInt(h);
      } while (block.getShape(x, y) != 1);

      Item item = context.itemFactory.createRandomItem();
      block.setItem(x, y, item);

      lastGrantLine = context.totalClearedLines;
      System.out.println("[DEBUG] 빠른 아이템 부여 (" + linesPerGrant + "줄 간격) → (" + x + "," + y + ")");
      return item;
    }
    return null;
  }

  @Override
  public void reset() {
    lastGrantLine = 0;
  }
}
