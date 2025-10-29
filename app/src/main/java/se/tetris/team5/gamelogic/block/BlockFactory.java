package se.tetris.team5.gamelogic.block;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.tetris.team5.blocks.*;

public class BlockFactory {
  /**
   * 블럭 생성 난이도 (UI에서 동적으로 변경 가능)
   */
  public enum Difficulty {
    NORMAL, EASY, HARD
  }

  private Random random;
  private Difficulty difficulty = Difficulty.NORMAL;
  // bag-of-7 support (shuffle bag) - kept for compatibility with older logic
  private List<Integer> bag;

  /**
   * 기본 생성자 (난이도: NORMAL, seed: 랜덤)
   */
  public BlockFactory() {
    this.random = new Random();
    this.bag = new ArrayList<>();
    refillBag();
  }

  /**
   * 난이도 지정 생성자 (seed: 랜덤)
   */
  public BlockFactory(Difficulty difficulty) {
    this.random = new Random();
    this.difficulty = difficulty;
  }

  /**
   * 난이도 설정 (UI에서 호출)
   */
  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  /**
   * 현재 난이도 반환 (UI에서 조회용)
   */
  public Difficulty getDifficulty() {
    return this.difficulty;
  }

  /**
   * 랜덤 시드(Seed) 재설정 (UI에서 seed 고정 플레이 등 활용 가능)
   */
  public void setRandomSeed(long seed) {
    this.random = new Random(seed);
  }

  public Block createRandomBlock() {
    int[] weights;
    // 블럭 순서: I, J, L, Z, S, T, O
    switch (difficulty) {
      case EASY:
        weights = new int[] { 12, 10, 10, 10, 10, 10, 10 }; // I만 20% 더 높음
        break;
      case HARD:
        weights = new int[] { 8, 10, 10, 10, 10, 10, 10 }; // I만 20% 낮음
        break;
      case NORMAL:
      default:
        weights = new int[] { 10, 10, 10, 10, 10, 10, 10 }; // 모두 동일
    }
    int total = 0;
    for (int w : weights)
      total += w;
    int r = random.nextInt(total);
    int idx = 0;
    while (r >= weights[idx]) {
      r -= weights[idx];
      idx++;
    }
    return createBlock(idx);
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

  /**
   * 랜덤 시드를 현재 시간으로 재설정 (UI에서 '새로고침' 등 활용 가능)
   */
  public void refreshRandomSeed() {
    this.random = new Random(System.currentTimeMillis());
  }

  // Fill and shuffle the 7-bag. Kept for compatibility; current creation
  // methods may not use the bag but tests or other modules may expect it.
  private void refillBag() {
    bag.clear();
    for (int i = 0; i < 7; i++) {
      bag.add(i);
    }
    Collections.shuffle(bag, random);
  }
}
