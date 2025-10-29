package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ItemFactory의 단위 테스트
 */
public class ItemFactoryTest {
    private ItemFactory factory;

    @Before
    public void setUp() {
        factory = new ItemFactory();
    }

    @Test
    public void testCreateRandomItemReturnsValidItem() {
        // when: 랜덤 아이템 생성
        Item item = factory.createRandomItem();
        
        // then: null이 아닌 유효한 아이템이 반환되어야 함
        assertNotNull("생성된 아이템은 null이 아니어야 함", item);
    }

    @Test
    public void testCreateRandomItemReturnsOneOfFourTypes() {
        // when: 여러 번 아이템을 생성
        boolean hasLineClear = false;
        boolean hasWeightBlock = false;
        boolean hasBomb = false;
        boolean hasTimeStop = false;
        
        // 100번 생성하여 모든 타입이 나오는지 확인
        for (int i = 0; i < 100; i++) {
            Item item = factory.createRandomItem();
            
            if (item instanceof LineClearItem) hasLineClear = true;
            else if (item instanceof WeightBlockItem) hasWeightBlock = true;
            else if (item instanceof BombItem) hasBomb = true;
            else if (item instanceof TimeStopItem) hasTimeStop = true;
            else if (item instanceof DoubleScoreItem) {
                // DoubleScoreItem은 직접 생성되지 않지만, 혹시 모르니 체크
            }
        }
        
        // then: 모든 아이템 타입이 적어도 한 번은 나와야 함 (확률적으로)
        assertTrue("LineClearItem이 생성되어야 함", hasLineClear);
        assertTrue("WeightBlockItem이 생성되어야 함", hasWeightBlock);
        assertTrue("BombItem이 생성되어야 함", hasBomb);
        assertTrue("TimeStopItem이 생성되어야 함", hasTimeStop);
    }

    @Test
    public void testCreateRandomItemDistribution() {
        // when: 많은 수의 아이템을 생성하여 분포 확인
        int totalItems = 1000;
        int lineClearCount = 0;
        int weightBlockCount = 0;
        int bombCount = 0;
        int timeStopCount = 0;
        
        for (int i = 0; i < totalItems; i++) {
            Item item = factory.createRandomItem();
            
            if (item instanceof LineClearItem) lineClearCount++;
            else if (item instanceof WeightBlockItem) weightBlockCount++;
            else if (item instanceof BombItem) bombCount++;
            else if (item instanceof TimeStopItem) timeStopCount++;
        }
        
        // then: 각 아이템이 대략 25% (250개) 정도씩 나와야 함 (허용 오차 10%)
        int expectedCount = totalItems / 4; // 250
        int tolerance = (int) (expectedCount * 0.5); // 50% 허용 오차 (통계적 변동 고려)
        
        assertTrue("LineClearItem 비율이 적절해야 함", 
            Math.abs(lineClearCount - expectedCount) < tolerance);
        assertTrue("WeightBlockItem 비율이 적절해야 함", 
            Math.abs(weightBlockCount - expectedCount) < tolerance);
        assertTrue("BombItem 비율이 적절해야 함", 
            Math.abs(bombCount - expectedCount) < tolerance);
        assertTrue("TimeStopItem 비율이 적절해야 함", 
            Math.abs(timeStopCount - expectedCount) < tolerance);
    }

    @Test
    public void testEachItemTypeHasCorrectProperties() {
        // when: 각 타입의 아이템 직접 생성
        Item lineClear = new LineClearItem();
        Item weightBlock = new WeightBlockItem();
        Item bomb = new BombItem();
        Item timeStop = new TimeStopItem();
        Item doubleScore = new DoubleScoreItem();
        
        // then: 각 아이템이 올바른 이름과 문자열 표현을 가져야 함
        assertEquals("LineClearItem", lineClear.getName());
        assertEquals("L", lineClear.toString());
        
        assertEquals("WeightBlockItem", weightBlock.getName());
        assertEquals("W", weightBlock.toString());
        
        assertEquals("BombItem", bomb.getName());
        assertEquals("B", bomb.toString());
        
        assertEquals("TimeStopItem", timeStop.getName());
        assertEquals("T", timeStop.toString());
        
        assertEquals("DoubleScoreItem", doubleScore.getName());
        assertEquals("D", doubleScore.toString());
    }

    @Test
    public void testItemFactoryIndependence() {
        // given: 두 개의 ItemFactory 인스턴스
        ItemFactory factory1 = new ItemFactory();
        ItemFactory factory2 = new ItemFactory();
        
        // when: 각각 아이템 생성
        Item item1 = factory1.createRandomItem();
        Item item2 = factory2.createRandomItem();
        
        // then: 독립적으로 동작해야 함 (null이 아니어야 함)
        assertNotNull("첫 번째 팩토리가 아이템을 생성해야 함", item1);
        assertNotNull("두 번째 팩토리가 아이템을 생성해야 함", item2);
    }
}
