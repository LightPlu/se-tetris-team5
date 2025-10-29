package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.components.game.BoardManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 점수 2배 아이템의 단위 테스트 (엣지 케이스 포함)
 */
public class DoubleScoreItemTest {
  private GameEngine engine;
  private BoardManager board;

  @Before
  public void setUp() {
    engine = new GameEngine(20, 10);
    board = engine.getBoardManager();
  }

  @Test
  public void testDoubleScoreItemActivatesAndExpires() throws Exception {
    // given: 2배 점수 아이템이 포함된 블록을 한 줄에 배치
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
    assertTrue("점수 2배 효과가 활성화되어야 함", engine.isDoubleScoreActive());
    // 2배 효과 중 점수 증가가 2배로 적용되는지 확인 (GameEngine의 내부 로직을 직접 검증)
    assertTrue(engine.isDoubleScoreActive());
    java.lang.reflect.Method method = engine.getClass().getDeclaredMethod("applyDoubleScore", int.class);
    method.setAccessible(true); // private 메서드 접근 허용
    int doubled = engine.isDoubleScoreActive() ? (int) method.invoke(engine, 100) : 100;
    assertEquals("점수가 2배로 적용되어야 함", 200, doubled);
    // 20초 후 효과가 꺼지는지 확인 (테스트 속도 위해 100ms로 강제 단축)
    engine.activateDoubleScore(100); // 0.1초만 적용
    Thread.sleep(150);
    assertFalse("시간이 지나면 점수 2배 효과가 비활성화되어야 함", engine.isDoubleScoreActive());
  }

  @Test
  public void testDoubleScoreItemStacking() throws Exception {
    // given: 점수 2배 효과가 활성화된 상태
    engine.activateDoubleScore(5000); // 5초
    assertTrue("점수 2배 효과가 활성화되어야 함", engine.isDoubleScoreActive());
    
    // when: 다시 점수 2배 아이템을 획득
    engine.activateDoubleScore(5000); // 추가 5초
    
    // then: 효과가 계속 활성화되어 있어야 함
    assertTrue("점수 2배 효과가 여전히 활성화되어 있어야 함", engine.isDoubleScoreActive());
  }

  @Test
  public void testDoubleScoreItemApplyEffect() {
    // given: 점수 2배 아이템
    DoubleScoreItem item = new DoubleScoreItem();
    assertFalse("초기 상태에서는 점수 2배가 비활성화되어야 함", engine.isDoubleScoreActive());
    
    // when: 아이템 효과 적용
    item.applyEffect(engine);
    
    // then: 점수 2배 효과가 활성화됨
    assertTrue("아이템 효과 적용 후 점수 2배가 활성화되어야 함", engine.isDoubleScoreActive());
  }

  @Test
  public void testDoubleScoreItemToString() {
    // given: 점수 2배 아이템
    DoubleScoreItem item = new DoubleScoreItem();
    
    // then: toString이 "D"를 반환해야 함
    assertEquals("D", item.toString());
    assertEquals("DoubleScoreItem", item.getName());
  }

  @Test
  public void testDoubleScoreWithMultipleItems() {
    // given: 점수 2배 아이템 여러 개
    Block block1 = new se.tetris.team5.blocks.OBlock();
    block1.setItem(0, 0, new DoubleScoreItem());
    
    Block block2 = new se.tetris.team5.blocks.OBlock();
    block2.setItem(0, 0, new DoubleScoreItem());
    
    // when: 두 블록을 배치하고 동시에 줄 삭제
    List<Item> removed = new ArrayList<>();
    board.fixBlock(block1, 0, 0, removed);
    board.fixBlock(block2, 3, 0, removed);
    
    for (int x = 0; x < board.getWidth(); x++) {
      board.getBoard()[0][x] = 1;
    }
    
    removed.clear();
    board.clearLines(removed);
    
    // 모든 아이템 효과 적용
    for (Item item : removed) {
      item.applyEffect(engine);
    }
    
    // then: 점수 2배 효과가 활성화되어야 함
    assertTrue("여러 점수 2배 아이템 중 하나라도 효과가 적용되어야 함", engine.isDoubleScoreActive());
  }

  @Test
  public void testDoubleScoreExpiration() throws Exception {
    // given: 짧은 시간 동안만 점수 2배 효과 활성화
    engine.activateDoubleScore(50); // 50ms
    assertTrue("점수 2배 효과가 활성화되어야 함", engine.isDoubleScoreActive());
    
    // when: 충분한 시간이 지남
    Thread.sleep(100);
    
    // then: 효과가 만료됨 (isDoubleScoreActive 호출 시 자동으로 체크)
    assertFalse("시간이 지나면 점수 2배 효과가 비활성화되어야 함", engine.isDoubleScoreActive());
  }
}
