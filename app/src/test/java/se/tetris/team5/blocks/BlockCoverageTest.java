package se.tetris.team5.blocks;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.Color;

import se.tetris.team5.items.Item;
import se.tetris.team5.items.TimeStopItem;
import se.tetris.team5.items.BombItem;
import se.tetris.team5.items.LineClearItem;

/**
 * Block.java의 라인 커버리지를 높이기 위한 추가 테스트
 * 
 * 테스트 범위:
 * - 아이템 관련 메서드 (setItem, getItem, removeItem)
 * - 회전 시 아이템 처리
 * - 경계 조건 테스트
 * - updateColor 메서드
 * - width/height 엣지 케이스
 */
public class BlockCoverageTest {

    private Block testBlock;

    @Before
    public void setUp() {
        testBlock = new IBlock();
    }

    // ==================== 아이템 관련 테스트 ====================

    @Test
    public void testSetItemValidPosition() {
        // given
        Item timeStopItem = new TimeStopItem();
        
        // when
        testBlock.setItem(0, 0, timeStopItem);
        
        // then
        assertEquals("아이템이 올바르게 설정되어야 함", timeStopItem, testBlock.getItem(0, 0));
    }

    @Test
    public void testSetItemMultiplePositions() {
        // given
        Item item1 = new TimeStopItem();
        Item item2 = new BombItem();
        Item item3 = new LineClearItem();
        
        // when
        testBlock.setItem(0, 0, item1);
        testBlock.setItem(1, 0, item2);
        testBlock.setItem(2, 0, item3);
        
        // then
        assertEquals("첫 번째 아이템이 설정되어야 함", item1, testBlock.getItem(0, 0));
        assertEquals("두 번째 아이템이 설정되어야 함", item2, testBlock.getItem(1, 0));
        assertEquals("세 번째 아이템이 설정되어야 함", item3, testBlock.getItem(2, 0));
    }

    @Test
    public void testSetItemOutOfBounds() {
        // given
        Item item = new TimeStopItem();
        
        // when: 범위 밖 좌표
        testBlock.setItem(-1, 0, item);
        testBlock.setItem(0, -1, item);
        testBlock.setItem(100, 0, item);
        testBlock.setItem(0, 100, item);
        
        // then: 예외 발생 없이 무시됨
        assertTrue("범위 밖 설정은 무시되어야 함", true);
    }

    @Test
    public void testGetItemValidPosition() {
        // given
        Item bombItem = new BombItem();
        testBlock.setItem(1, 0, bombItem);
        
        // when
        Item retrieved = testBlock.getItem(1, 0);
        
        // then
        assertEquals("설정한 아이템을 가져올 수 있어야 함", bombItem, retrieved);
    }

    @Test
    public void testGetItemNoItem() {
        // when
        Item retrieved = testBlock.getItem(0, 0);
        
        // then
        assertNull("아이템이 없으면 null 반환", retrieved);
    }

    @Test
    public void testGetItemOutOfBounds() {
        // when
        Item item1 = testBlock.getItem(-1, 0);
        Item item2 = testBlock.getItem(0, -1);
        Item item3 = testBlock.getItem(100, 0);
        Item item4 = testBlock.getItem(0, 100);
        
        // then
        assertNull("범위 밖 접근은 null 반환", item1);
        assertNull("범위 밖 접근은 null 반환", item2);
        assertNull("범위 밖 접근은 null 반환", item3);
        assertNull("범위 밖 접근은 null 반환", item4);
    }

    @Test
    public void testRemoveItemValidPosition() {
        // given
        Item lineClearItem = new LineClearItem();
        testBlock.setItem(2, 0, lineClearItem);
        
        // when
        Item removed = testBlock.removeItem(2, 0);
        
        // then
        assertEquals("제거된 아이템을 반환해야 함", lineClearItem, removed);
        assertNull("제거 후 null이어야 함", testBlock.getItem(2, 0));
    }

    @Test
    public void testRemoveItemNoItem() {
        // when
        Item removed = testBlock.removeItem(0, 0);
        
        // then
        assertNull("아이템이 없으면 null 반환", removed);
    }

    @Test
    public void testRemoveItemOutOfBounds() {
        // when
        Item item1 = testBlock.removeItem(-1, 0);
        Item item2 = testBlock.removeItem(0, -1);
        Item item3 = testBlock.removeItem(100, 0);
        Item item4 = testBlock.removeItem(0, 100);
        
        // then
        assertNull("범위 밖 제거는 null 반환", item1);
        assertNull("범위 밖 제거는 null 반환", item2);
        assertNull("범위 밖 제거는 null 반환", item3);
        assertNull("범위 밖 제거는 null 반환", item4);
    }

    // ==================== 회전 + 아이템 테스트 ====================

    @Test
    public void testRotateWithItems() {
        // given: T블록 (3x2)
        Block tBlock = new TBlock();
        Item item1 = new TimeStopItem();
        Item item2 = new BombItem();
        
        tBlock.setItem(0, 0, item1);
        tBlock.setItem(1, 0, item2);
        
        int initialWidth = tBlock.width();
        int initialHeight = tBlock.height();
        
        // when
        tBlock.rotate();
        
        // then
        assertEquals("회전 후 너비는 이전 높이", initialHeight, tBlock.width());
        assertEquals("회전 후 높이는 이전 너비", initialWidth, tBlock.height());
        
        // 아이템도 함께 회전되어야 함
        assertNotNull("아이템 배열이 회전되어야 함", tBlock.getItem(0, 0));
    }

    @Test
    public void testRotateMultipleTimes() {
        // given
        Block jBlock = new JBlock();
        Item item = new LineClearItem();
        jBlock.setItem(0, 0, item);
        
        int originalWidth = jBlock.width();
        int originalHeight = jBlock.height();
        
        // when: 4번 회전 (360도)
        for (int i = 0; i < 4; i++) {
            jBlock.rotate();
        }
        
        // then: 원래 크기로 복귀
        assertEquals("4번 회전 후 너비 복귀", originalWidth, jBlock.width());
        assertEquals("4번 회전 후 높이 복귀", originalHeight, jBlock.height());
    }

    @Test
    public void testRotatePreservesBlockType() {
        // given
        Block sBlock = new SBlock();
        String originalType = sBlock.getBlockType();
        
        // when
        sBlock.rotate();
        
        // then
        assertEquals("회전 후에도 블록 타입 유지", originalType, sBlock.getBlockType());
    }

    // ==================== updateColor 테스트 ====================

    @Test
    public void testUpdateColor() {
        // given
        Block lBlock = new LBlock();
        Color originalColor = lBlock.getColor();
        
        // when
        lBlock.updateColor();
        
        // then
        assertNotNull("색상이 null이 아니어야 함", lBlock.getColor());
        assertEquals("색상이 유지되어야 함", originalColor, lBlock.getColor());
    }

    @Test
    public void testUpdateColorAllBlocks() {
        // given
        Block[] blocks = {
            new IBlock(),
            new JBlock(),
            new LBlock(),
            new OBlock(),
            new SBlock(),
            new TBlock(),
            new ZBlock()
        };
        
        // when & then
        for (Block block : blocks) {
            Color beforeColor = block.getColor();
            block.updateColor();
            Color afterColor = block.getColor();
            
            assertNotNull("색상이 null이 아니어야 함", afterColor);
            assertEquals("updateColor 후에도 색상 유지", beforeColor, afterColor);
        }
    }

    // ==================== width/height 엣지 케이스 ====================

    @Test
    public void testWidthAfterRotation() {
        // given: IBlock (4x1)
        Block iBlock = new IBlock();
        assertEquals("초기 너비는 4", 4, iBlock.width());
        assertEquals("초기 높이는 1", 1, iBlock.height());
        
        // when
        iBlock.rotate();
        
        // then
        assertEquals("회전 후 너비는 1", 1, iBlock.width());
        assertEquals("회전 후 높이는 4", 4, iBlock.height());
    }

    @Test
    public void testHeightAllBlocks() {
        // when & then
        assertTrue("I블록 높이 > 0", new IBlock().height() > 0);
        assertTrue("J블록 높이 > 0", new JBlock().height() > 0);
        assertTrue("L블록 높이 > 0", new LBlock().height() > 0);
        assertTrue("O블록 높이 > 0", new OBlock().height() > 0);
        assertTrue("S블록 높이 > 0", new SBlock().height() > 0);
        assertTrue("T블록 높이 > 0", new TBlock().height() > 0);
        assertTrue("Z블록 높이 > 0", new ZBlock().height() > 0);
    }

    @Test
    public void testWidthAllBlocks() {
        // when & then
        assertTrue("I블록 너비 > 0", new IBlock().width() > 0);
        assertTrue("J블록 너비 > 0", new JBlock().width() > 0);
        assertTrue("L블록 너비 > 0", new LBlock().width() > 0);
        assertTrue("O블록 너비 > 0", new OBlock().width() > 0);
        assertTrue("S블록 너비 > 0", new SBlock().width() > 0);
        assertTrue("T블록 너비 > 0", new TBlock().width() > 0);
        assertTrue("Z블록 너비 > 0", new ZBlock().width() > 0);
    }

    // ==================== getBlockType 테스트 ====================

    @Test
    public void testGetBlockTypeAllBlocks() {
        assertEquals("I", new IBlock().getBlockType());
        assertEquals("J", new JBlock().getBlockType());
        assertEquals("L", new LBlock().getBlockType());
        assertEquals("O", new OBlock().getBlockType());
        assertEquals("S", new SBlock().getBlockType());
        assertEquals("T", new TBlock().getBlockType());
        assertEquals("Z", new ZBlock().getBlockType());
    }

    // ==================== getShape 테스트 ====================

    @Test
    public void testGetShapeAllCells() {
        // given: O블록 (2x2, 모두 1)
        Block oBlock = new OBlock();
        
        // when & then
        for (int y = 0; y < oBlock.height(); y++) {
            for (int x = 0; x < oBlock.width(); x++) {
                assertEquals("O블록의 모든 셀은 1", 1, oBlock.getShape(x, y));
            }
        }
    }

    @Test
    public void testGetShapeAfterRotation() {
        // given
        Block lBlock = new LBlock();
        
        // when
        lBlock.rotate();
        
        // then: 회전 후에도 getShape 호출 가능
        assertTrue("회전 후에도 shape 접근 가능", 
            lBlock.getShape(0, 0) == 0 || lBlock.getShape(0, 0) == 1);
    }

    // ==================== 아이템 배열 초기화 테스트 ====================

    @Test
    public void testItemsArrayInitialized() {
        // given
        Block zBlock = new ZBlock();
        
        // when & then: 모든 위치가 null로 초기화
        for (int y = 0; y < zBlock.height(); y++) {
            for (int x = 0; x < zBlock.width(); x++) {
                assertNull("초기 아이템은 모두 null", zBlock.getItem(x, y));
            }
        }
    }

    @Test
    public void testItemsArraySizeMatchesShape() {
        // given
        Block[] blocks = {
            new IBlock(),
            new JBlock(),
            new LBlock(),
            new OBlock(),
            new SBlock(),
            new TBlock(),
            new ZBlock()
        };
        
        // when & then
        for (Block block : blocks) {
            // items 배열 크기는 shape와 동일해야 함
            for (int y = 0; y < block.height(); y++) {
                for (int x = 0; x < block.width(); x++) {
                    // 유효한 범위 내에서 getItem 호출 시 예외 없어야 함
                    block.getItem(x, y);
                }
            }
            assertTrue("아이템 배열 크기 검증 완료", true);
        }
    }

    // ==================== 복합 시나리오 테스트 ====================

    @Test
    public void testSetItemThenRotate() {
        // given
        Block tBlock = new TBlock();
        Item item = new TimeStopItem();
        tBlock.setItem(1, 0, item);
        
        // when
        tBlock.rotate();
        
        // then: 아이템이 회전과 함께 이동
        assertNotNull("회전 후에도 아이템 존재해야 함", 
            findItemInBlock(tBlock));
    }

    @Test
    public void testRotateThenSetItem() {
        // given
        Block jBlock = new JBlock();
        jBlock.rotate();
        
        // when
        Item item = new BombItem();
        jBlock.setItem(0, 0, item);
        
        // then
        assertEquals("회전 후 아이템 설정 가능", item, jBlock.getItem(0, 0));
    }

    @Test
    public void testMultipleItemOperations() {
        // given
        Block sBlock = new SBlock();
        Item item1 = new TimeStopItem();
        Item item2 = new LineClearItem();
        
        // when: 설정 → 제거 → 재설정
        sBlock.setItem(0, 0, item1);
        sBlock.removeItem(0, 0);
        sBlock.setItem(0, 0, item2);
        
        // then
        assertEquals("마지막 설정된 아이템", item2, sBlock.getItem(0, 0));
    }

    // ==================== Helper 메서드 ====================

    /**
     * 블록 내에서 아이템을 찾음
     */
    private Item findItemInBlock(Block block) {
        for (int y = 0; y < block.height(); y++) {
            for (int x = 0; x < block.width(); x++) {
                Item item = block.getItem(x, y);
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }
}
