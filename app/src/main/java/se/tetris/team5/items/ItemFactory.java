package se.tetris.team5.items;

import java.util.Random;

public class ItemFactory {
  private Random random = new Random();

  /**
   * 랜덤으로 아이템을 생성합니다.
   * LineClearItem, WeightBlockItem, BombItem을 동일한 확률(33.33%)로 생성합니다.
   */
  public Item createRandomItem() {
    int itemType = random.nextInt(3); // 0, 1, 2 중 하나
    
    switch (itemType) {
      case 0:
        return new LineClearItem();
      case 1:
        return new WeightBlockItem();
      case 2:
        return new BombItem();
      default:
        return new LineClearItem(); // 기본값
    }
  }
}