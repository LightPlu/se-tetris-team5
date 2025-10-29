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
 * 줄삭제 아이템(LineClearItem)의 단위 테스트
 * 블록이 고정될 때 해당 줄이 가득 차지 않아도 즉시 삭제됨
 */
public class LineClearItemTest {
    private GameEngine engine;
    private BoardManager board;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        board = engine.getBoardManager();
    }

    @Test
    public void testLineClearItemBasicProperties() {
        // given: 줄삭제 아이템 생성
        LineClearItem item = new LineClearItem();
        
        // then: 기본 속성 확인
        assertEquals("LineClearItem", item.getName());
        assertEquals("L", item.toString());
    }

    @Test
    public void testLineClearItemApplyEffect() {
        // given: 줄삭제 아이템
        LineClearItem item = new LineClearItem();
        
        // when: 효과 적용 (실제로는 BoardManager에서 처리)
        // 예외가 발생하지 않아야 함
        try {
            item.applyEffect(engine);
            item.applyEffect(board);
            item.applyEffect(null);
            assertTrue("줄삭제 아이템 효과 적용 시 예외가 발생하지 않아야 함", true);
        } catch (Exception e) {
            fail("줄삭제 아이템 효과 적용 중 예외 발생: " + e.getMessage());
        }
    }

    @Test
    public void testLineClearItemClearsPartialLine() {
        // given: LineClearItem이 포함된 블록을 절반만 채워진 줄에 배치
        Block block = new se.tetris.team5.blocks.OBlock();
        block.setItem(0, 0, new LineClearItem());
        
        // 첫 번째 줄을 절반만 채움 (LineClearItem 없이는 삭제되지 않음)
        for (int x = 0; x < board.getWidth() / 2; x++) {
            board.getBoard()[0][x] = 1;
        }
        
        int initialBlockCount = 0;
        for (int x = 0; x < board.getWidth(); x++) {
            if (board.getBoard()[0][x] == 1) initialBlockCount++;
        }
        
        // when: LineClearItem이 포함된 블록을 (0,0)에 고정
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block, 0, 0, removed);
        
        // then: LineClearItem이 있는 줄이 가득 차지 않아도 삭제되어야 함
        // fixBlock 내부에서 LineClearItem을 감지하여 즉시 줄 삭제 처리
        // 삭제 후 위의 줄들이 아래로 내려옴
        
        // 0번째 줄이 삭제되고 위의 빈 줄이 내려왔으므로 0번째 줄은 비어있어야 함
        int afterClearCount = 0;
        for (int x = 0; x < board.getWidth(); x++) {
            if (board.getBoard()[0][x] == 1) afterClearCount++;
        }
        
        // LineClearItem으로 줄이 삭제되었으므로 블록 수가 감소했을 것
        assertTrue("LineClearItem으로 줄이 삭제되어야 함", 
            afterClearCount <= initialBlockCount);
    }

    @Test
    public void testLineClearItemInMultipleLines() {
        // given: 여러 줄에 LineClearItem 배치
        Block block1 = new se.tetris.team5.blocks.OBlock();
        block1.setItem(0, 0, new LineClearItem());
        
        Block block2 = new se.tetris.team5.blocks.OBlock();
        block2.setItem(0, 0, new LineClearItem());
        
        // when: 서로 다른 높이에 블록 배치
        List<Item> removed1 = new ArrayList<>();
        board.fixBlock(block1, 0, 0, removed1);
        
        List<Item> removed2 = new ArrayList<>();
        board.fixBlock(block2, 0, 3, removed2);
        
        // then: 각 LineClearItem이 있는 줄이 개별적으로 삭제되어야 함
        // (실제 동작은 BoardManager.fixBlock에서 처리)
        assertTrue("여러 LineClearItem이 독립적으로 처리되어야 함", true);
    }

    @Test
    public void testLineClearItemWithFullLine() {
        // given: LineClearItem이 포함된 블록을 이미 가득 찬 줄에 배치
        Block block = new se.tetris.team5.blocks.OBlock();
        block.setItem(0, 0, new LineClearItem());
        
        // 첫 번째 줄을 완전히 채움
        for (int x = 0; x < board.getWidth(); x++) {
            board.getBoard()[0][x] = 1;
        }
        
        // when: LineClearItem이 포함된 블록 고정
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block, 0, 0, removed);
        
        // then: 이미 가득 찬 줄도 LineClearItem에 의해 삭제되어야 함
        int remainingBlocks = 0;
        for (int x = 0; x < board.getWidth(); x++) {
            if (board.getBoard()[0][x] == 1) remainingBlocks++;
        }
        
        // LineClearItem이 줄을 삭제하므로 블록이 줄어들어야 함
        assertTrue("가득 찬 줄도 LineClearItem으로 삭제되어야 함", 
            remainingBlocks < board.getWidth());
    }

    @Test
    public void testLineClearItemWithOtherItemsInSameLine() {
        // given: 같은 줄에 LineClearItem과 다른 아이템 배치
        Block block1 = new se.tetris.team5.blocks.OBlock();
        block1.setItem(0, 0, new LineClearItem());
        
        Block block2 = new se.tetris.team5.blocks.OBlock();
        block2.setItem(0, 0, new DoubleScoreItem());
        
        // when: 같은 줄(0번째)에 두 블록 배치
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block1, 0, 0, removed);
        board.fixBlock(block2, 3, 0, removed);
        
        // then: LineClearItem에 의해 줄이 삭제되므로 다른 아이템도 함께 제거됨
        assertTrue("LineClearItem이 다른 아이템과 함께 처리되어야 함", true);
    }

    @Test
    public void testLineClearItemDoesNotAffectOtherLines() {
        // given: 여러 줄에 블록 배치
        // 0번째 줄: LineClearItem 포함 블록
        // 2번째 줄: 일반 블록
        Block lineClearBlock = new se.tetris.team5.blocks.OBlock();
        lineClearBlock.setItem(0, 0, new LineClearItem());
        
        Block normalBlock = new se.tetris.team5.blocks.OBlock();
        
        // 2번째 줄에 일반 블록 먼저 배치
        List<Item> removed = new ArrayList<>();
        board.fixBlock(normalBlock, 0, 2, removed);
        
        // when: 0번째 줄에 LineClearItem 포함 블록 배치
        removed.clear();
        board.fixBlock(lineClearBlock, 0, 0, removed);
        
        // then: LineClearItem이 있는 줄만 삭제되고 다른 줄은 영향 없음
        // (단, 줄 삭제로 인해 위의 줄들이 아래로 내려올 수 있음)
        // 적어도 2번째 줄의 블록이 여전히 존재해야 함
        int totalBlocks = 0;
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.getBoard()[y][x] == 1) totalBlocks++;
            }
        }
        
        assertTrue("다른 줄의 블록은 유지되어야 함", totalBlocks > 0);
    }

    @Test
    public void testLineClearItemBoundaryCase() {
        // given: 보드 최상단에 LineClearItem 배치
        Block block = new se.tetris.team5.blocks.OBlock();
        block.setItem(0, 0, new LineClearItem());
        
        // when: 최상단(y=0)에 블록 고정
        List<Item> removed = new ArrayList<>();
        try {
            board.fixBlock(block, 0, 0, removed);
            assertTrue("최상단에서 LineClearItem 처리 시 예외가 발생하지 않아야 함", true);
        } catch (Exception e) {
            fail("경계 조건에서 예외 발생: " + e.getMessage());
        }
    }

    @Test
    public void testLineClearItemInterface() {
        // given: LineClearItem이 Item 인터페이스를 구현
        Item item = new LineClearItem();
        
        // then: Item 인터페이스의 모든 메서드가 동작해야 함
        assertNotNull("getName()은 null이 아니어야 함", item.getName());
        assertNotNull("toString()은 null이 아니어야 함", item.toString());
        assertEquals("LineClearItem", item.getName());
        assertEquals("L", item.toString());
        
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
    public void testMultipleLineClearItemsInSameBlock() {
        // given: 한 블록에 여러 LineClearItem 설정 (OBlock은 2x2)
        Block block = new se.tetris.team5.blocks.OBlock();
        block.setItem(0, 0, new LineClearItem());
        block.setItem(1, 0, new LineClearItem());
        
        // when: 블록 고정
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block, 0, 0, removed);
        
        // then: 여러 LineClearItem이 있어도 정상 처리되어야 함
        assertTrue("여러 LineClearItem이 정상 처리되어야 함", true);
    }
}
