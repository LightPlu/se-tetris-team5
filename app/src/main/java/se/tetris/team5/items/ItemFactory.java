package se.tetris.team5.items;

import java.util.Random;

public class ItemFactory {
  private Random random = new Random();

  public Item createRandomItem() {
    // 현재는 ScoreItem만, 추후 확장 가능
    int score = 100 + random.nextInt(200); // 100~299점
    return new ScoreItem(score);
  }
}