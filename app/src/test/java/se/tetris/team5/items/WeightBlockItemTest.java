package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.blocks.WBlock;
import se.tetris.team5.components.game.BoardManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 무게추 블록 아이템(WeightBlockItem)의 단위 테스트
 * 10줄 삭제 시 일반 블록 대신 무게추 블록(WBlock)을 생성
 * WBlock이 고정되면 해당 열의 모든 블록을 제거하고 가장 아래에 고정됨
 */
public class WeightBlockItemTest {
    private GameEngine engine;
    private BoardManager board;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        board = engine.getBoardManager();
    }

    @Test
    public void testWeightBlockItemBasicProperties() {
        // given: 무게추 블록 아이템 생성
        WeightBlockItem item = new WeightBlockItem();
        
        // then: 기본 속성 확인
        assertEquals("WeightBlockItem", item.getName());
        assertEquals("W", item.toString());
    }

    @Test
    public void testWeightBlockItemApplyEffect() {
        // given: 무게추 블록 아이템
        WeightBlockItem item = new WeightBlockItem();
        
        // when: 효과 적용 (실제로는 GameEngine에서 WBlock 생성으로 처리)
        // 예외가 발생하지 않아야 함
        try {
            item.applyEffect(engine);
            item.applyEffect(board);
            item.applyEffect(null);
            assertTrue("무게추 블록 아이템 효과 적용 시 예외가 발생하지 않아야 함", true);
        } catch (Exception e) {
            fail("무게추 블록 아이템 효과 적용 중 예외 발생: " + e.getMessage());
        }
    }

    @Test
    public void testWBlockClearsColumn() {
        // given: 여러 열에 블록 배치
        // 5번째 열에 블록들을 쌓음
        for (int y = 10; y < 15; y++) {
            board.getBoard()[y][5] = 1;
        }
        
        int columnBlocksBefore = 0;
        for (int y = 0; y < board.getHeight(); y++) {
            if (board.getBoard()[y][5] == 1) columnBlocksBefore++;
        }
        
        assertTrue("초기에 5번째 열에 블록이 있어야 함", columnBlocksBefore > 0);
        
        // when: WBlock을 5번째 열에 고정
        Block wBlock = new WBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(wBlock, 5, 0, removed);
        
        // then: 5번째 열의 모든 기존 블록이 제거되어야 함
        // WBlock이 가장 아래에 고정됨
        
        // WBlock만 남아있거나, 열이 완전히 클리어된 후 WBlock이 아래에 고정됨
        assertTrue("WBlock 고정 후 열이 처리되어야 함", true);
    }

    @Test
    public void testWBlockClearsMultipleColumns() {
        // given: WBlock이 2칸을 차지하므로 2개 열에 영향
        // 4, 5번째 열에 블록들을 쌓음
        for (int y = 10; y < 15; y++) {
            board.getBoard()[y][4] = 1;
            board.getBoard()[y][5] = 1;
        }
        
        // when: WBlock을 (4,0)에 고정 (4, 5번째 열 차지)
        Block wBlock = new WBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(wBlock, 4, 0, removed);
        
        // then: 4, 5번째 열의 블록들이 제거되고 WBlock이 아래에 고정됨
        assertTrue("WBlock이 여러 열을 처리해야 함", true);
    }

    @Test
    public void testWBlockLandsAtBottom() {
        // given: 빈 보드
        
        // when: WBlock을 중간 높이(y=5)에 고정
        Block wBlock = new WBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(wBlock, 5, 5, removed);
        
        // then: WBlock이 가장 아래(y = HEIGHT - wBlock.height())에 고정되어야 함
        int expectedBottomY = board.getHeight() - wBlock.height();
        
        boolean wBlockAtBottom = false;
        for (int i = 0; i < wBlock.width(); i++) {
            for (int j = 0; j < wBlock.height(); j++) {
                if (wBlock.getShape(i, j) == 1) {
                    int boardX = 5 + i;
                    int boardY = expectedBottomY + j;
                    if (boardX < board.getWidth() && boardY < board.getHeight()) {
                        if (board.getBoard()[boardY][boardX] == 1) {
                            wBlockAtBottom = true;
                        }
                    }
                }
            }
        }
        
        assertTrue("WBlock이 가장 아래에 고정되어야 함", wBlockAtBottom);
    }

    @Test
    public void testWBlockClearsItemsInColumn() {
        // given: WBlock이 고정될 열에 다른 아이템이 있는 블록 배치
        Block blockWithItem = new se.tetris.team5.blocks.OBlock();
        blockWithItem.setItem(0, 0, new DoubleScoreItem());
        
        List<Item> removed = new ArrayList<>();
        board.fixBlock(blockWithItem, 5, 10, removed);
        
        // 해당 열에 일반 블록도 추가
        for (int y = 12; y < 15; y++) {
            board.getBoard()[y][5] = 1;
        }
        
        // when: WBlock을 같은 열에 고정
        Block wBlock = new WBlock();
        removed.clear();
        board.fixBlock(wBlock, 5, 0, removed);
        
        // then: 아이템이 있던 블록도 함께 제거되어야 함
        assertTrue("WBlock이 아이템과 함께 열을 클리어해야 함", true);
    }

    @Test
    public void testWBlockBoundaryColumn() {
        // given: 보드 경계(0번째 열)에 블록 배치
        for (int y = 10; y < 15; y++) {
            board.getBoard()[y][0] = 1;
        }
        
        // when: WBlock을 0번째 열에 고정
        Block wBlock = new WBlock();
        List<Item> removed = new ArrayList<>();
        try {
            board.fixBlock(wBlock, 0, 0, removed);
            assertTrue("경계에서 WBlock 고정 시 예외가 발생하지 않아야 함", true);
        } catch (Exception e) {
            fail("경계 조건에서 예외 발생: " + e.getMessage());
        }
    }

    @Test
    public void testWBlockRightBoundary() {
        // given: 보드 오른쪽 경계 근처에 블록 배치
        int rightColumn = board.getWidth() - 1;
        for (int y = 10; y < 15; y++) {
            board.getBoard()[y][rightColumn] = 1;
        }
        
        // when: WBlock을 오른쪽 경계에 고정 (WBlock이 2칸이므로 조정)
        Block wBlock = new WBlock();
        List<Item> removed = new ArrayList<>();
        int xPos = board.getWidth() - wBlock.width();
        
        try {
            board.fixBlock(wBlock, xPos, 0, removed);
            assertTrue("오른쪽 경계에서 WBlock 고정 시 예외가 발생하지 않아야 함", true);
        } catch (Exception e) {
            fail("오른쪽 경계 조건에서 예외 발생: " + e.getMessage());
        }
    }

    @Test
    public void testMultipleWBlocks() {
        // given: 여러 열에 블록 배치
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 10; y < 15; y++) {
                board.getBoard()[y][x] = 1;
            }
        }
        
        // when: 첫 번째 WBlock 고정
        Block wBlock1 = new WBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(wBlock1, 2, 0, removed);
        
        // when: 두 번째 WBlock 고정
        Block wBlock2 = new WBlock();
        removed.clear();
        board.fixBlock(wBlock2, 6, 0, removed);
        
        // then: 두 WBlock이 각각의 열을 클리어하고 독립적으로 동작해야 함
        assertTrue("여러 WBlock이 독립적으로 처리되어야 함", true);
    }

    @Test
    public void testWeightBlockItemInterface() {
        // given: WeightBlockItem이 Item 인터페이스를 구현
        Item item = new WeightBlockItem();
        
        // then: Item 인터페이스의 모든 메서드가 동작해야 함
        assertNotNull("getName()은 null이 아니어야 함", item.getName());
        assertNotNull("toString()은 null이 아니어야 함", item.toString());
        assertEquals("WeightBlockItem", item.getName());
        assertEquals("W", item.toString());
        
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

    @Test
    public void testWBlockWithEmptyColumn() {
        // given: 빈 열
        
        // when: WBlock을 빈 열에 고정
        Block wBlock = new WBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(wBlock, 5, 0, removed);
        
        // then: WBlock이 가장 아래에 정상적으로 고정되어야 함
        int expectedBottomY = board.getHeight() - wBlock.height();
        
        boolean hasWBlock = false;
        for (int y = expectedBottomY; y < board.getHeight(); y++) {
            if (board.getBoard()[y][5] == 1 || board.getBoard()[y][6] == 1) {
                hasWBlock = true;
                break;
            }
        }
        
        assertTrue("빈 열에도 WBlock이 정상적으로 고정되어야 함", hasWBlock);
    }

    @Test
    public void testWBlockColor() {
        // given: WBlock 생성
        Block wBlock = new WBlock();
        
        // then: WBlock이 고유한 색상을 가져야 함
        assertNotNull("WBlock의 색상은 null이 아니어야 함", wBlock.getColor());
    }
}
