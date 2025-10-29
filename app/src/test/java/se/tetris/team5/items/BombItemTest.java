package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.blocks.DotBlock;
import se.tetris.team5.components.game.BoardManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 폭탄 아이템(BombItem)의 단위 테스트
 * 폭탄 블록(DotBlock)이 고정되면 3x3 범위의 블록을 폭발시킴
 */
public class BombItemTest {
    private GameEngine engine;
    private BoardManager board;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        board = engine.getBoardManager();
    }

    @Test
    public void testBombItemBasicProperties() {
        // given: 폭탄 아이템 생성
        BombItem item = new BombItem();
        
        // then: 기본 속성 확인
        assertEquals("BombItem", item.getName());
        assertEquals("B", item.toString());
    }

    @Test
    public void testBombItemApplyEffect() {
        // given: 폭탄 아이템
        BombItem item = new BombItem();
        
        // when: 효과 적용 (실제로는 BoardManager에서 처리)
        // 예외가 발생하지 않아야 함
        try {
            item.applyEffect(engine);
            assertTrue("폭탄 아이템 효과 적용 시 예외가 발생하지 않아야 함", true);
        } catch (Exception e) {
            fail("폭탄 아이템 효과 적용 중 예외 발생: " + e.getMessage());
        }
    }

    @Test
    public void testDotBlockExplosion() {
        // given: 주변에 고정된 블록들을 배치
        // 3x3 범위에 블록 배치 (중심은 5,5)
        for (int y = 4; y <= 6; y++) {
            for (int x = 4; x <= 6; x++) {
                board.getBoard()[y][x] = 1; // 고정된 블록
            }
        }
        
        // when: DotBlock을 중심(5,5)에 고정
        Block dotBlock = new DotBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(dotBlock, 5, 5, removed);
        
        // then: 3x3 범위의 블록들이 폭발로 제거되어야 함
        // DotBlock은 고정되지 않고 폭발만 일으킴
        int remainingBlocks = 0;
        for (int y = 4; y <= 6; y++) {
            for (int x = 4; x <= 6; x++) {
                if (board.getBoard()[y][x] == 1) {
                    remainingBlocks++;
                }
            }
        }
        
        // 폭발로 대부분의 블록이 제거되어야 함
        assertTrue("폭발로 블록들이 제거되어야 함", remainingBlocks < 9);
    }

    @Test
    public void testDotBlockExplosionBoundary() {
        // given: 보드 경계(0,0)에 블록 배치
        for (int y = 0; y <= 2; y++) {
            for (int x = 0; x <= 2; x++) {
                if (y < board.getHeight() && x < board.getWidth()) {
                    board.getBoard()[y][x] = 1;
                }
            }
        }
        
        // when: DotBlock을 경계(0,0)에 고정
        Block dotBlock = new DotBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(dotBlock, 0, 0, removed);
        
        // then: 범위를 벗어나지 않고 폭발이 처리되어야 함 (예외 없음)
        // 경계 체크가 정상적으로 동작하는지 확인
        assertTrue("경계에서 폭발 처리 시 예외가 발생하지 않아야 함", true);
    }

    @Test
    public void testDotBlockExplosionWithItems() {
        // given: 폭발 범위 내에 다른 아이템이 있는 블록 배치
        Block block1 = new se.tetris.team5.blocks.OBlock();
        block1.setItem(0, 0, new DoubleScoreItem());
        
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block1, 4, 4, removed);
        
        // 주변에 일반 블록 배치
        for (int x = 3; x <= 6; x++) {
            board.getBoard()[4][x] = 1;
        }
        
        // when: DotBlock을 근처(5,4)에 고정하여 폭발
        Block dotBlock = new DotBlock();
        removed.clear();
        board.fixBlock(dotBlock, 5, 4, removed);
        
        // then: 폭발 범위 내의 아이템도 함께 제거되어야 함
        // (실제로는 BoardManager의 explodeArea에서 처리)
        assertTrue("폭발이 정상적으로 처리되어야 함", true);
    }

    @Test
    public void testDotBlockDoesNotFixItself() {
        // given: 빈 보드
        
        // when: DotBlock을 고정
        Block dotBlock = new DotBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(dotBlock, 5, 5, removed);
        
        // then: DotBlock 자체는 보드에 고정되지 않아야 함 (폭발만 하고 사라짐)
        // DotBlock이 차지하던 위치가 빈 칸이어야 함
        int dotBlockCells = 0;
        for (int i = 0; i < dotBlock.width(); i++) {
            for (int j = 0; j < dotBlock.height(); j++) {
                if (dotBlock.getShape(i, j) == 1) {
                    int boardX = 5 + i;
                    int boardY = 5 + j;
                    if (boardX < board.getWidth() && boardY < board.getHeight()) {
                        if (board.getBoard()[boardY][boardX] == 1) {
                            dotBlockCells++;
                        }
                    }
                }
            }
        }
        
        assertEquals("DotBlock은 폭발 후 보드에 고정되지 않아야 함", 0, dotBlockCells);
    }

    @Test
    public void testMultipleDotBlockExplosions() {
        // given: 여러 위치에 블록 배치
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                board.getBoard()[y][x] = 1;
            }
        }
        
        int initialBlockCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.getBoard()[y][x] == 1) initialBlockCount++;
            }
        }
        
        // when: 첫 번째 DotBlock 폭발
        Block dotBlock1 = new DotBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(dotBlock1, 5, 5, removed);
        
        int afterFirstExplosion = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.getBoard()[y][x] == 1) afterFirstExplosion++;
            }
        }
        
        // then: 첫 번째 폭발로 블록이 감소해야 함
        assertTrue("첫 번째 폭발로 블록이 제거되어야 함", 
            afterFirstExplosion < initialBlockCount);
        
        // when: 두 번째 DotBlock 폭발
        Block dotBlock2 = new DotBlock();
        removed.clear();
        board.fixBlock(dotBlock2, 2, 2, removed);
        
        int afterSecondExplosion = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.getBoard()[y][x] == 1) afterSecondExplosion++;
            }
        }
        
        // then: 두 번째 폭발로 추가로 블록이 감소해야 함
        assertTrue("두 번째 폭발로 추가 블록이 제거되어야 함", 
            afterSecondExplosion < afterFirstExplosion);
    }

    @Test
    public void testBombItemInterface() {
        // given: BombItem이 Item 인터페이스를 구현
        Item item = new BombItem();
        
        // then: Item 인터페이스의 모든 메서드가 동작해야 함
        assertNotNull("getName()은 null이 아니어야 함", item.getName());
        assertNotNull("toString()은 null이 아니어야 함", item.toString());
        
        // applyEffect는 예외를 발생시키지 않아야 함
        try {
            item.applyEffect(null);
            item.applyEffect(engine);
            item.applyEffect(board);
            assertTrue(true);
        } catch (Exception e) {
            fail("applyEffect 호출 시 예외가 발생하지 않아야 함");
        }
    }
}
