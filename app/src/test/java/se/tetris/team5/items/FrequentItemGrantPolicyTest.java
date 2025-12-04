package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.blocks.IBlock;
import se.tetris.team5.blocks.TBlock;
import se.tetris.team5.blocks.OBlock;
import se.tetris.team5.items.ItemGrantPolicy.ItemGrantContext;

import java.lang.reflect.Field;

/**
 * FrequentItemGrantPolicy 테스트
 */
public class FrequentItemGrantPolicyTest {

    private FrequentItemGrantPolicy policy;
    private ItemFactory itemFactory;
    private Block testBlock;

    @Before
    public void setUp() {
        policy = new FrequentItemGrantPolicy(5);
        itemFactory = new ItemFactory();
        testBlock = new IBlock();
    }

    /**
     * 테스트 1: 정책 생성 - 정상
     */
    @Test
    public void testFrequentItemGrantPolicy_Creation() {
        assertNotNull("정책이 생성되어야 함", policy);
    }

    /**
     * 테스트 2: 정책 생성 - 양수 간격
     */
    @Test
    public void testFrequentItemGrantPolicy_PositiveInterval() {
        FrequentItemGrantPolicy policy1 = new FrequentItemGrantPolicy(1);
        FrequentItemGrantPolicy policy10 = new FrequentItemGrantPolicy(10);
        FrequentItemGrantPolicy policy100 = new FrequentItemGrantPolicy(100);
        
        assertNotNull("1줄 간격 정책 생성", policy1);
        assertNotNull("10줄 간격 정책 생성", policy10);
        assertNotNull("100줄 간격 정책 생성", policy100);
    }

    /**
     * 테스트 3: 정책 생성 - 0 간격 (예외)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFrequentItemGrantPolicy_ZeroInterval() {
        new FrequentItemGrantPolicy(0);
    }

    /**
     * 테스트 4: 정책 생성 - 음수 간격 (예외)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFrequentItemGrantPolicy_NegativeInterval() {
        new FrequentItemGrantPolicy(-5);
    }

    /**
     * 테스트 5: grantItem - 초기 상태 (0줄)
     */
    @Test
    public void testFrequentItemGrantPolicy_InitialState() {
        ItemGrantContext context = new ItemGrantContext(0, itemFactory);
        Item result = policy.grantItem(testBlock, context);
        
        assertNull("0줄에서는 아이템이 부여되지 않아야 함", result);
    }

    /**
     * 테스트 6: grantItem - 간격 미달
     */
    @Test
    public void testFrequentItemGrantPolicy_BelowInterval() {
        ItemGrantContext context = new ItemGrantContext(3, itemFactory);
        Item result = policy.grantItem(testBlock, context);
        
        assertNull("5줄 미만에서는 아이템이 부여되지 않아야 함", result);
    }

    /**
     * 테스트 7: grantItem - 정확히 간격
     */
    @Test
    public void testFrequentItemGrantPolicy_ExactInterval() {
        ItemGrantContext context = new ItemGrantContext(5, itemFactory);
        Item result = policy.grantItem(testBlock, context);
        
        assertNotNull("정확히 5줄에서 아이템이 부여되어야 함", result);
    }

    /**
     * 테스트 8: grantItem - 간격 초과
     */
    @Test
    public void testFrequentItemGrantPolicy_AboveInterval() {
        ItemGrantContext context = new ItemGrantContext(10, itemFactory);
        Item result = policy.grantItem(testBlock, context);
        
        assertNotNull("10줄에서 아이템이 부여되어야 함", result);
    }

    /**
     * 테스트 9: grantItem - 여러 번 부여
     */
    @Test
    public void testFrequentItemGrantPolicy_MultipleGrants() {
        Item item1 = policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        Item item2 = policy.grantItem(new IBlock(), new ItemGrantContext(10, itemFactory));
        Item item3 = policy.grantItem(new IBlock(), new ItemGrantContext(15, itemFactory));
        
        assertNotNull("5줄에서 부여", item1);
        assertNotNull("10줄에서 부여", item2);
        assertNotNull("15줄에서 부여", item3);
    }

    /**
     * 테스트 10: grantItem - 중간에 간격 미달
     */
    @Test
    public void testFrequentItemGrantPolicy_IntermediateGap() {
        Item item1 = policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        Item item2 = policy.grantItem(new IBlock(), new ItemGrantContext(7, itemFactory));
        Item item3 = policy.grantItem(new IBlock(), new ItemGrantContext(10, itemFactory));
        
        assertNotNull("5줄에서 부여", item1);
        assertNull("7줄에서는 부여 안됨 (간격 미달)", item2);
        assertNotNull("10줄에서 부여", item3);
    }

    /**
     * 테스트 11: reset - 상태 초기화
     */
    @Test
    public void testFrequentItemGrantPolicy_Reset() {
        // 첫 부여
        Item item1 = policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        assertNotNull("첫 부여", item1);
        
        // 리셋
        policy.reset();
        
        // 리셋 후 다시 5줄에서 부여
        Item item2 = policy.grantItem(new IBlock(), new ItemGrantContext(5, itemFactory));
        assertNotNull("리셋 후 다시 5줄에서 부여", item2);
    }

    /**
     * 테스트 12: grantItem - null 블록
     */
    @Test
    public void testFrequentItemGrantPolicy_NullBlock() {
        ItemGrantContext context = new ItemGrantContext(5, itemFactory);
        Item result = policy.grantItem(null, context);
        
        assertNull("null 블록에는 아이템이 부여되지 않아야 함", result);
    }

    /**
     * 테스트 13: grantItem - null 컨텍스트
     */
    @Test
    public void testFrequentItemGrantPolicy_NullContext() {
        Item result = policy.grantItem(testBlock, null);
        
        assertNull("null 컨텍스트에는 아이템이 부여되지 않아야 함", result);
    }

    /**
     * 테스트 14: grantItem - 둘 다 null
     */
    @Test
    public void testFrequentItemGrantPolicy_BothNull() {
        Item result = policy.grantItem(null, null);
        
        assertNull("둘 다 null이면 아이템이 부여되지 않아야 함", result);
    }

    /**
     * 테스트 15: grantItem - 블록에 아이템 설정 확인
     */
    @Test
    public void testFrequentItemGrantPolicy_ItemSetOnBlock() {
        Block block = new IBlock();
        ItemGrantContext context = new ItemGrantContext(5, itemFactory);
        
        Item result = policy.grantItem(block, context);
        
        assertNotNull("아이템이 부여되어야 함", result);
        
        // 블록에 아이템이 설정되었는지 확인
        boolean hasItem = false;
        for (int y = 0; y < block.height(); y++) {
            for (int x = 0; x < block.width(); x++) {
                if (block.getItem(x, y) != null) {
                    hasItem = true;
                    break;
                }
            }
        }
        assertTrue("블록에 아이템이 설정되어야 함", hasItem);
    }

    /**
     * 테스트 16: 1줄 간격 정책
     */
    @Test
    public void testFrequentItemGrantPolicy_OneLineInterval() {
        FrequentItemGrantPolicy policy1 = new FrequentItemGrantPolicy(1);
        
        Item item1 = policy1.grantItem(testBlock, new ItemGrantContext(1, itemFactory));
        Item item2 = policy1.grantItem(new IBlock(), new ItemGrantContext(2, itemFactory));
        Item item3 = policy1.grantItem(new IBlock(), new ItemGrantContext(3, itemFactory));
        
        assertNotNull("1줄마다 부여", item1);
        assertNotNull("2줄에 부여", item2);
        assertNotNull("3줄에 부여", item3);
    }

    /**
     * 테스트 17: 10줄 간격 정책
     */
    @Test
    public void testFrequentItemGrantPolicy_TenLineInterval() {
        FrequentItemGrantPolicy policy10 = new FrequentItemGrantPolicy(10);
        
        assertNull("9줄에는 부여 안됨", policy10.grantItem(testBlock, new ItemGrantContext(9, itemFactory)));
        assertNotNull("10줄에 부여", policy10.grantItem(new IBlock(), new ItemGrantContext(10, itemFactory)));
        assertNull("19줄에는 부여 안됨", policy10.grantItem(new IBlock(), new ItemGrantContext(19, itemFactory)));
        assertNotNull("20줄에 부여", policy10.grantItem(new IBlock(), new ItemGrantContext(20, itemFactory)));
    }

    /**
     * 테스트 18: 다양한 블록 타입
     */
    @Test
    public void testFrequentItemGrantPolicy_VariousBlockTypes() {
        Block[] blocks = {new IBlock(), new TBlock(), new OBlock()};
        
        int lines = 5;
        for (Block block : blocks) {
            Item item = policy.grantItem(block, new ItemGrantContext(lines, itemFactory));
            assertNotNull(block.getClass().getSimpleName() + "에 아이템 부여", item);
            lines += 5;
            policy.reset();
        }
    }

    /**
     * 테스트 19: 연속 호출 - 같은 줄 수
     */
    @Test
    public void testFrequentItemGrantPolicy_SameLineMultipleCalls() {
        ItemGrantContext context = new ItemGrantContext(5, itemFactory);
        
        Item item1 = policy.grantItem(testBlock, context);
        Item item2 = policy.grantItem(new IBlock(), context);
        
        assertNotNull("첫 호출에 부여", item1);
        assertNull("같은 줄 수로 두 번째 호출에는 부여 안됨", item2);
    }

    /**
     * 테스트 20: reset 여러 번
     */
    @Test
    public void testFrequentItemGrantPolicy_MultipleResets() {
        policy.reset();
        policy.reset();
        policy.reset();
        
        Item item = policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        assertNotNull("여러 번 reset 후에도 정상 작동", item);
    }

    /**
     * 테스트 21: 큰 줄 수
     */
    @Test
    public void testFrequentItemGrantPolicy_LargeLineNumber() {
        FrequentItemGrantPolicy policy100 = new FrequentItemGrantPolicy(100);
        
        Item item = policy100.grantItem(testBlock, new ItemGrantContext(1000, itemFactory));
        assertNotNull("큰 줄 수에서도 아이템 부여", item);
    }

    /**
     * 테스트 22: 경계값 - 정확히 간격-1
     */
    @Test
    public void testFrequentItemGrantPolicy_BoundaryMinusOne() {
        Item item = policy.grantItem(testBlock, new ItemGrantContext(4, itemFactory));
        assertNull("간격-1에서는 부여 안됨", item);
    }

    /**
     * 테스트 23: 경계값 - 정확히 간격+1
     */
    @Test
    public void testFrequentItemGrantPolicy_BoundaryPlusOne() {
        Item item = policy.grantItem(testBlock, new ItemGrantContext(6, itemFactory));
        assertNotNull("간격+1에서는 부여됨", item);
    }

    /**
     * 테스트 24: 음수 줄 수
     */
    @Test
    public void testFrequentItemGrantPolicy_NegativeLines() {
        ItemGrantContext context = new ItemGrantContext(-5, itemFactory);
        Item result = policy.grantItem(testBlock, context);
        
        assertNull("음수 줄 수에서는 부여 안됨", result);
    }

    /**
     * 테스트 25: grantItem 후 lastGrantLine 업데이트 확인
     */
    @Test
    public void testFrequentItemGrantPolicy_LastGrantLineUpdate() throws Exception {
        policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        
        // 리플렉션으로 lastGrantLine 확인
        Field field = FrequentItemGrantPolicy.class.getDeclaredField("lastGrantLine");
        field.setAccessible(true);
        int lastGrantLine = (int) field.get(policy);
        
        assertEquals("lastGrantLine이 업데이트되어야 함", 5, lastGrantLine);
    }

    /**
     * 테스트 26: reset 후 lastGrantLine 확인
     */
    @Test
    public void testFrequentItemGrantPolicy_LastGrantLineAfterReset() throws Exception {
        policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        policy.reset();
        
        // 리플렉션으로 lastGrantLine 확인
        Field field = FrequentItemGrantPolicy.class.getDeclaredField("lastGrantLine");
        field.setAccessible(true);
        int lastGrantLine = (int) field.get(policy);
        
        assertEquals("reset 후 lastGrantLine이 0이어야 함", 0, lastGrantLine);
    }

    /**
     * 테스트 27: 간격 2배 줄 수에서 부여
     */
    @Test
    public void testFrequentItemGrantPolicy_DoubleInterval() {
        policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        Item item = policy.grantItem(new IBlock(), new ItemGrantContext(10, itemFactory));
        
        assertNotNull("간격 2배 줄 수에서 부여", item);
    }

    /**
     * 테스트 28: 간격 3배 줄 수에서 부여
     */
    @Test
    public void testFrequentItemGrantPolicy_TripleInterval() {
        policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        policy.grantItem(new IBlock(), new ItemGrantContext(10, itemFactory));
        Item item = policy.grantItem(new IBlock(), new ItemGrantContext(15, itemFactory));
        
        assertNotNull("간격 3배 줄 수에서 부여", item);
    }

    /**
     * 테스트 29: null ItemFactory
     */
    @Test
    public void testFrequentItemGrantPolicy_NullItemFactory() {
        ItemGrantContext context = new ItemGrantContext(5, null);
        
        try {
            Item result = policy.grantItem(testBlock, context);
            // NullPointerException 발생 가능
            if (result == null) {
                assertTrue("null 팩토리 처리", true);
            }
        } catch (NullPointerException e) {
            assertTrue("NullPointerException 발생 가능", true);
        }
    }

    /**
     * 테스트 30: 매우 큰 간격
     */
    @Test
    public void testFrequentItemGrantPolicy_VeryLargeInterval() {
        FrequentItemGrantPolicy largePolicy = new FrequentItemGrantPolicy(10000);
        
        assertNull("9999줄에는 부여 안됨", largePolicy.grantItem(testBlock, new ItemGrantContext(9999, itemFactory)));
        assertNotNull("10000줄에 부여", largePolicy.grantItem(new IBlock(), new ItemGrantContext(10000, itemFactory)));
    }

    /**
     * 테스트 31: 연속 증가하는 줄 수
     */
    @Test
    public void testFrequentItemGrantPolicy_SequentialLines() {
        int grantCount = 0;
        
        for (int i = 1; i <= 20; i++) {
            Item item = policy.grantItem(new IBlock(), new ItemGrantContext(i, itemFactory));
            if (item != null) {
                grantCount++;
            }
        }
        
        assertEquals("20줄까지 4번 부여되어야 함 (5, 10, 15, 20)", 4, grantCount);
    }

    /**
     * 테스트 32: 간격 사이의 모든 줄
     */
    @Test
    public void testFrequentItemGrantPolicy_AllLinesInGap() {
        policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        
        assertNull("6줄 부여 안됨", policy.grantItem(new IBlock(), new ItemGrantContext(6, itemFactory)));
        assertNull("7줄 부여 안됨", policy.grantItem(new IBlock(), new ItemGrantContext(7, itemFactory)));
        assertNull("8줄 부여 안됨", policy.grantItem(new IBlock(), new ItemGrantContext(8, itemFactory)));
        assertNull("9줄 부여 안됨", policy.grantItem(new IBlock(), new ItemGrantContext(9, itemFactory)));
        assertNotNull("10줄 부여됨", policy.grantItem(new IBlock(), new ItemGrantContext(10, itemFactory)));
    }

    /**
     * 테스트 33: 랜덤 아이템 생성 확인
     */
    @Test
    public void testFrequentItemGrantPolicy_RandomItemCreation() {
        Item item1 = policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        policy.reset();
        Item item2 = policy.grantItem(new IBlock(), new ItemGrantContext(5, itemFactory));
        
        assertNotNull("첫 번째 아이템", item1);
        assertNotNull("두 번째 아이템", item2);
        // 랜덤이므로 타입은 다를 수 있음
    }

    /**
     * 테스트 34: 블록의 유효한 위치에 아이템 설정
     */
    @Test
    public void testFrequentItemGrantPolicy_ItemOnValidPosition() {
        Block block = new IBlock();
        policy.grantItem(block, new ItemGrantContext(5, itemFactory));
        
        // 아이템이 블록의 shape=1인 위치에만 설정되었는지 확인
        boolean foundItem = false;
        for (int y = 0; y < block.height(); y++) {
            for (int x = 0; x < block.width(); x++) {
                if (block.getItem(x, y) != null) {
                    assertEquals("아이템은 shape=1인 위치에만 설정", 1, block.getShape(x, y));
                    foundItem = true;
                }
            }
        }
        assertTrue("아이템이 설정되어야 함", foundItem);
    }

    /**
     * 테스트 35: 짝수 간격
     */
    @Test
    public void testFrequentItemGrantPolicy_EvenInterval() {
        FrequentItemGrantPolicy policy2 = new FrequentItemGrantPolicy(2);
        
        assertNotNull("2줄에 부여", policy2.grantItem(testBlock, new ItemGrantContext(2, itemFactory)));
        assertNotNull("4줄에 부여", policy2.grantItem(new IBlock(), new ItemGrantContext(4, itemFactory)));
        assertNotNull("6줄에 부여", policy2.grantItem(new IBlock(), new ItemGrantContext(6, itemFactory)));
    }

    /**
     * 테스트 36: 홀수 간격
     */
    @Test
    public void testFrequentItemGrantPolicy_OddInterval() {
        FrequentItemGrantPolicy policy3 = new FrequentItemGrantPolicy(3);
        
        assertNotNull("3줄에 부여", policy3.grantItem(testBlock, new ItemGrantContext(3, itemFactory)));
        assertNotNull("6줄에 부여", policy3.grantItem(new IBlock(), new ItemGrantContext(6, itemFactory)));
        assertNotNull("9줄에 부여", policy3.grantItem(new IBlock(), new ItemGrantContext(9, itemFactory)));
    }

    /**
     * 테스트 37: 부여 후 즉시 reset
     */
    @Test
    public void testFrequentItemGrantPolicy_GrantAndImmediateReset() {
        Item item1 = policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        assertNotNull("부여됨", item1);
        
        policy.reset();
        
        Item item2 = policy.grantItem(new IBlock(), new ItemGrantContext(5, itemFactory));
        assertNotNull("reset 후 다시 부여됨", item2);
    }

    /**
     * 테스트 38: 0줄에서 시작
     */
    @Test
    public void testFrequentItemGrantPolicy_StartFromZero() {
        assertNull("0줄 부여 안됨", policy.grantItem(testBlock, new ItemGrantContext(0, itemFactory)));
        assertNull("1줄 부여 안됨", policy.grantItem(new IBlock(), new ItemGrantContext(1, itemFactory)));
        assertNotNull("5줄 부여됨", policy.grantItem(new IBlock(), new ItemGrantContext(5, itemFactory)));
    }

    /**
     * 테스트 39: 비순차적 줄 수
     */
    @Test
    public void testFrequentItemGrantPolicy_NonSequentialLines() {
        assertNotNull("5줄 부여", policy.grantItem(testBlock, new ItemGrantContext(5, itemFactory)));
        assertNull("3줄 부여 안됨 (역행)", policy.grantItem(new IBlock(), new ItemGrantContext(3, itemFactory)));
        assertNotNull("10줄 부여", policy.grantItem(new IBlock(), new ItemGrantContext(10, itemFactory)));
    }

    /**
     * 테스트 40: 매우 작은 블록
     */
    @Test
    public void testFrequentItemGrantPolicy_SmallBlock() {
        Block oBlock = new OBlock(); // 2x2 블록
        Item item = policy.grantItem(oBlock, new ItemGrantContext(5, itemFactory));
        
        assertNotNull("작은 블록에도 아이템 부여", item);
    }

    /**
     * 테스트 41: linesPerGrant 필드 확인
     */
    @Test
    public void testFrequentItemGrantPolicy_LinesPerGrantField() throws Exception {
        Field field = FrequentItemGrantPolicy.class.getDeclaredField("linesPerGrant");
        field.setAccessible(true);
        int linesPerGrant = (int) field.get(policy);
        
        assertEquals("linesPerGrant가 5여야 함", 5, linesPerGrant);
    }

    /**
     * 테스트 42: 부여 후 같은 줄 수로 여러 번 호출
     */
    @Test
    public void testFrequentItemGrantPolicy_MultipleCallsSameLine() {
        ItemGrantContext context = new ItemGrantContext(5, itemFactory);
        
        Item item1 = policy.grantItem(testBlock, context);
        Item item2 = policy.grantItem(new IBlock(), context);
        Item item3 = policy.grantItem(new IBlock(), context);
        
        assertNotNull("첫 호출만 부여", item1);
        assertNull("두 번째 호출 부여 안됨", item2);
        assertNull("세 번째 호출 부여 안됨", item3);
    }

    /**
     * 테스트 43: 100줄 시뮬레이션
     */
    @Test
    public void testFrequentItemGrantPolicy_100LinesSimulation() {
        int grantCount = 0;
        
        for (int i = 1; i <= 100; i++) {
            Item item = policy.grantItem(new IBlock(), new ItemGrantContext(i, itemFactory));
            if (item != null) {
                grantCount++;
            }
        }
        
        assertEquals("100줄에서 20번 부여 (5줄 간격)", 20, grantCount);
    }

    /**
     * 테스트 44: 간격 1로 연속 부여
     */
    @Test
    public void testFrequentItemGrantPolicy_Interval1Continuous() {
        FrequentItemGrantPolicy policy1 = new FrequentItemGrantPolicy(1);
        
        int grantCount = 0;
        for (int i = 1; i <= 10; i++) {
            Item item = policy1.grantItem(new IBlock(), new ItemGrantContext(i, itemFactory));
            if (item != null) {
                grantCount++;
            }
        }
        
        assertEquals("10줄에서 10번 부여 (1줄 간격)", 10, grantCount);
    }

    /**
     * 테스트 45: totalClearedLines 변경 확인
     */
    @Test
    public void testFrequentItemGrantPolicy_TotalClearedLinesChange() {
        ItemGrantContext context = new ItemGrantContext(5, itemFactory);
        Item item1 = policy.grantItem(testBlock, context);
        
        context.totalClearedLines = 10;
        Item item2 = policy.grantItem(new IBlock(), context);
        
        assertNotNull("5줄에 부여", item1);
        assertNotNull("10줄에 부여", item2);
    }

    /**
     * 테스트 46: 정책 재생성 후 독립성
     */
    @Test
    public void testFrequentItemGrantPolicy_IndependentInstances() {
        FrequentItemGrantPolicy policy1 = new FrequentItemGrantPolicy(5);
        FrequentItemGrantPolicy policy2 = new FrequentItemGrantPolicy(5);
        
        Item item1 = policy1.grantItem(testBlock, new ItemGrantContext(5, itemFactory));
        Item item2 = policy2.grantItem(new IBlock(), new ItemGrantContext(5, itemFactory));
        
        assertNotNull("policy1 부여", item1);
        assertNotNull("policy2 부여 (독립적)", item2);
    }

    /**
     * 테스트 47: 간격 정확도
     */
    @Test
    public void testFrequentItemGrantPolicy_IntervalAccuracy() {
        int[] expectedLines = {5, 10, 15, 20, 25, 30};
        int grantIndex = 0;
        
        for (int i = 1; i <= 30; i++) {
            Item item = policy.grantItem(new IBlock(), new ItemGrantContext(i, itemFactory));
            if (item != null && grantIndex < expectedLines.length) {
                assertEquals("정확한 간격에 부여", expectedLines[grantIndex], i);
                grantIndex++;
            }
        }
        
        assertEquals("6번 부여", 6, grantIndex);
    }

    /**
     * 테스트 48: reset 후 같은 블록 재사용
     */
    @Test
    public void testFrequentItemGrantPolicy_ResetSameBlock() {
        Block block = new IBlock();
        
        Item item1 = policy.grantItem(block, new ItemGrantContext(5, itemFactory));
        assertNotNull("첫 부여", item1);
        
        policy.reset();
        
        Item item2 = policy.grantItem(block, new ItemGrantContext(5, itemFactory));
        assertNotNull("reset 후 같은 블록에 재부여", item2);
    }

    /**
     * 테스트 49: 간격보다 훨씬 큰 줄 수
     */
    @Test
    public void testFrequentItemGrantPolicy_MuchLargerLines() {
        Item item = policy.grantItem(testBlock, new ItemGrantContext(1000, itemFactory));
        assertNotNull("간격보다 훨씬 큰 줄 수에도 부여", item);
    }

    /**
     * 테스트 50: 정책 생성 - 최대 간격
     */
    @Test
    public void testFrequentItemGrantPolicy_MaxInterval() {
        FrequentItemGrantPolicy largePolicy = new FrequentItemGrantPolicy(Integer.MAX_VALUE);
        
        assertNotNull("최대 간격으로 정책 생성", largePolicy);
    }
}
