package se.tetris.team5.items;

import se.tetris.team5.blocks.Block;
import java.util.Random;

/**
 * 10줄마다 한 번, 블록 내 랜덤 한 칸에 아이템을 부여하는 정책
 */
public class Every10LinesItemGrantPolicy implements ItemGrantPolicy {
  private Random random = new Random();

  @Override
  public void grantItem(Block block, ItemGrantContext context) {
    if (block == null || context == null)
      return;
    if (context.totalClearedLines > 0 && context.totalClearedLines % 1 == 0) {
      int w = block.width();
      int h = block.height();
      int x, y;
      do {
        x = random.nextInt(w);
        y = random.nextInt(h);
      } while (block.getShape(x, y) != 1);
      Item item = context.itemFactory.createRandomItem();
      block.setItem(x, y, item);
      System.out.println("[DEBUG] 1줄마다 아이템 부여: (" + x + "," + y + ") " + item.getName());
    }
  }
}
