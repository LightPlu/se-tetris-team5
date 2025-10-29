package se.tetris.team5.items;

import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.components.game.BoardManager;

import java.util.ArrayList;
import java.util.List;

public class DoubleScoreItemTest {
  @Test
  public void testDoubleScoreItemActivatesAndExpires() throws Exception {
    // given: 2배 점수 아이템이 포함된 블록을 한 줄에 배치
    GameEngine engine = new GameEngine(20, 10);
    BoardManager board = engine.getBoardManager();
    // OBlock(2x2) 생성 및 (0,0)에 DoubleScoreItem 부여
    Block block = new se.tetris.team5.blocks.OBlock();
    block.setItem(0, 0, new DoubleScoreItem());
    List<Item> removed = new ArrayList<>();
    // OBlock을 (0,0)에 고정
    board.fixBlock(block, 0, 0, removed);
    // 0, 1번째 줄을 모두 1로 채워 두 줄이 동시에 삭제되게 만듦
    for (int y = 0; y <= 1; y++) {
      for (int x = 0; x < board.getWidth(); x++) {
        board.getBoard()[y][x] = 1;
      }
    }
    // DoubleScoreItem이 삭제될 줄에 있으므로, clearLines로 효과 발동
    removed.clear();
    board.clearLines(removed);
    for (Item item : removed) {
      item.applyEffect(engine);
    }
    // then: 효과가 바로 활성화됨
    assertTrue(engine.isDoubleScoreActive());
    // 2배 효과 중 점수 증가가 2배로 적용되는지 확인 (GameEngine의 내부 로직을 직접 검증)
    assertTrue(engine.isDoubleScoreActive());
    java.lang.reflect.Method method = engine.getClass().getDeclaredMethod("applyDoubleScore", int.class);
    method.setAccessible(true); // private 메서드 접근 허용
    int doubled = engine.isDoubleScoreActive() ? (int) method.invoke(engine, 100) : 100;
    assertEquals(200, doubled); // 2배 적용
    // 20초 후 효과가 꺼지는지 확인 (테스트 속도 위해 100ms로 강제 단축)
    engine.activateDoubleScore(100); // 0.1초만 적용
    Thread.sleep(150);
    assertFalse(engine.isDoubleScoreActive());
  }
}
