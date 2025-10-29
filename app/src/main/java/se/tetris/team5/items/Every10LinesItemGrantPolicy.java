package se.tetris.team5.items;

import se.tetris.team5.blocks.Block;
import java.util.Random;

/**
 * 10줄마다 한 번, 블록 내 랜덤 한 칸에 아이템을 부여하는 정책
 */
public class Every10LinesItemGrantPolicy implements ItemGrantPolicy {
  private Random random = new Random();
  private int lastGrantLine = 0; // 마지막 아이템 부여 시점

  @Override
  public Item grantItem(Block block, ItemGrantContext context) {
    if (block == null || context == null)
      return null;

    // 10줄마다 아이템 부여
    if (context.totalClearedLines > 0 &&
        context.totalClearedLines >= lastGrantLine + 2) {

      int w = block.width();
      int h = block.height();
      int x, y;

      // 블록 내 랜덤 위치 선택 (블록이 있는 칸만)
      do {
        x = random.nextInt(w);
        y = random.nextInt(h);
      } while (block.getShape(x, y) != 1);

      // 아이템 생성 및 부여
      Item item = context.itemFactory.createRandomItem();
      block.setItem(x, y, item);

      lastGrantLine = context.totalClearedLines;
      System.out.println("[DEBUG] 10줄 삭제 아이템 부여: (" + x + "," + y + ") " + item.getName());
      return item;
    }

    return null;
  }

  /**
   * 정책 초기화 (게임 재시작 시 호출)
   */
  public void reset() {
    lastGrantLine = 0;
  }
}
