package se.tetris.team5.items;

import java.util.Random;

public class ItemFactory {
  private Random random = new Random();

  /**
   * 랜덤으로 아이템을 생성합니다.
   * LineClearItem, WeightBlockItem, BombItem, TimeStopItem을 동일한 확률(25%)로 생성합니다.
   */
  public Item createRandomItem() {
    int itemType = random.nextInt(4); // 0, 1, 2, 3 중 하나
    
    switch (itemType) {
      case 0:
        return new LineClearItem();
      case 1:
        return new WeightBlockItem();
      case 2:
        return new BombItem();
      case 3:
        return new TimeStopItem();
      default:
        return new LineClearItem(); // 기본값
    }
  }
}