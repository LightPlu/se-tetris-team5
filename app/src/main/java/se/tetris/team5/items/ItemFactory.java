package se.tetris.team5.items;

import java.util.Random;

public class ItemFactory {
  private Random random = new Random();

  public Item createRandomItem() {
    // 무조건 줄삭제 아이템만 생성
    return new LineClearItem();
  }
}