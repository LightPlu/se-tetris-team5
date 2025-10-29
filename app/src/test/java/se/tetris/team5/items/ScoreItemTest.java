package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ScoreItem.java에 대한 단위 테스트
 * 점수 증가 아이템의 기본 기능을 테스트합니다.
 */
public class ScoreItemTest {

    private ScoreItem scoreItem;

    @Before
    public void setUp() {
        scoreItem = new ScoreItem(100);
    }

    // ============ Constructor Tests ============

    @Test
    public void testConstructorWithPositiveScore() {
        ScoreItem item = new ScoreItem(100);
        assertNotNull("ScoreItem should be created", item);
        assertEquals("Score amount should be 100", 100, item.getScoreAmount());
    }

    @Test
    public void testConstructorWithZeroScore() {
        ScoreItem item = new ScoreItem(0);
        assertNotNull("ScoreItem should be created with 0 score", item);
        assertEquals("Score amount should be 0", 0, item.getScoreAmount());
    }

    @Test
    public void testConstructorWithNegativeScore() {
        ScoreItem item = new ScoreItem(-50);
        assertNotNull("ScoreItem should be created with negative score", item);
        assertEquals("Score amount should be -50", -50, item.getScoreAmount());
    }

    @Test
    public void testConstructorWithLargeScore() {
        ScoreItem item = new ScoreItem(1000000);
        assertNotNull("ScoreItem should be created with large score", item);
        assertEquals("Score amount should be 1000000", 1000000, item.getScoreAmount());
    }

    // ============ getName() Tests ============

    @Test
    public void testGetName() {
        assertEquals("Name should be 'ScoreItem'", "ScoreItem", scoreItem.getName());
    }

    @Test
    public void testGetNameIsConsistent() {
        String name1 = scoreItem.getName();
        String name2 = scoreItem.getName();
        assertEquals("getName should return consistent value", name1, name2);
    }

    @Test
    public void testGetNameNotNull() {
        assertNotNull("getName should not return null", scoreItem.getName());
    }

    @Test
    public void testGetNameNotEmpty() {
        assertFalse("getName should not return empty string", scoreItem.getName().isEmpty());
    }

    @Test
    public void testGetNameForDifferentScoreAmounts() {
        ScoreItem item1 = new ScoreItem(50);
        ScoreItem item2 = new ScoreItem(200);
        ScoreItem item3 = new ScoreItem(1000);
        
        assertEquals("All ScoreItems should have same name", "ScoreItem", item1.getName());
        assertEquals("All ScoreItems should have same name", "ScoreItem", item2.getName());
        assertEquals("All ScoreItems should have same name", "ScoreItem", item3.getName());
    }

    // ============ getScoreAmount() Tests ============

    @Test
    public void testGetScoreAmount() {
        assertEquals("Score amount should be 100", 100, scoreItem.getScoreAmount());
    }

    @Test
    public void testGetScoreAmountIsImmutable() {
        int amount1 = scoreItem.getScoreAmount();
        int amount2 = scoreItem.getScoreAmount();
        assertEquals("Score amount should be immutable", amount1, amount2);
    }

    @Test
    public void testGetScoreAmountWithVariousValues() {
        ScoreItem item1 = new ScoreItem(1);
        ScoreItem item2 = new ScoreItem(50);
        ScoreItem item3 = new ScoreItem(500);
        ScoreItem item4 = new ScoreItem(9999);
        
        assertEquals("Score amount should be 1", 1, item1.getScoreAmount());
        assertEquals("Score amount should be 50", 50, item2.getScoreAmount());
        assertEquals("Score amount should be 500", 500, item3.getScoreAmount());
        assertEquals("Score amount should be 9999", 9999, item4.getScoreAmount());
    }

    // ============ applyEffect() Tests ============

    @Test
    public void testApplyEffectWithNullTarget() {
        // applyEffect는 현재 빈 구현이므로 예외가 발생하지 않아야 함
        try {
            scoreItem.applyEffect(null);
            // 성공적으로 실행되면 테스트 통과
            assertTrue("applyEffect should not throw exception with null target", true);
        } catch (Exception e) {
            fail("applyEffect should not throw exception with null target: " + e.getMessage());
        }
    }

    @Test
    public void testApplyEffectWithObjectTarget() {
        Object target = new Object();
        try {
            scoreItem.applyEffect(target);
            // 성공적으로 실행되면 테스트 통과
            assertTrue("applyEffect should not throw exception with Object target", true);
        } catch (Exception e) {
            fail("applyEffect should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testApplyEffectWithStringTarget() {
        String target = "test target";
        try {
            scoreItem.applyEffect(target);
            assertTrue("applyEffect should not throw exception with String target", true);
        } catch (Exception e) {
            fail("applyEffect should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testApplyEffectMultipleTimes() {
        Object target = new Object();
        try {
            scoreItem.applyEffect(target);
            scoreItem.applyEffect(target);
            scoreItem.applyEffect(target);
            assertTrue("applyEffect should be callable multiple times", true);
        } catch (Exception e) {
            fail("applyEffect should not throw exception on multiple calls: " + e.getMessage());
        }
    }

    // ============ Item Interface Implementation Tests ============

    @Test
    public void testImplementsItemInterface() {
        assertTrue("ScoreItem should implement Item interface", scoreItem instanceof Item);
    }

    @Test
    public void testItemInterfaceMethodsExist() {
        // getName과 applyEffect가 Item 인터페이스의 메소드임을 확인
        Item item = scoreItem;
        assertNotNull("getName should be available through Item interface", item.getName());
        
        // applyEffect도 호출 가능해야 함
        try {
            item.applyEffect(null);
            assertTrue("applyEffect should be available through Item interface", true);
        } catch (Exception e) {
            fail("applyEffect should be callable through Item interface");
        }
    }

    // ============ Edge Cases and Boundary Tests ============

    @Test
    public void testScoreAmountBoundaries() {
        ScoreItem minItem = new ScoreItem(Integer.MIN_VALUE);
        ScoreItem maxItem = new ScoreItem(Integer.MAX_VALUE);
        
        assertEquals("Should handle Integer.MIN_VALUE", Integer.MIN_VALUE, minItem.getScoreAmount());
        assertEquals("Should handle Integer.MAX_VALUE", Integer.MAX_VALUE, maxItem.getScoreAmount());
    }

    @Test
    public void testMultipleInstancesAreIndependent() {
        ScoreItem item1 = new ScoreItem(100);
        ScoreItem item2 = new ScoreItem(200);
        ScoreItem item3 = new ScoreItem(300);
        
        assertEquals("Item1 score should be 100", 100, item1.getScoreAmount());
        assertEquals("Item2 score should be 200", 200, item2.getScoreAmount());
        assertEquals("Item3 score should be 300", 300, item3.getScoreAmount());
        
        // 각 인스턴스는 독립적이어야 함
        assertNotSame("Items should be different instances", item1, item2);
        assertNotSame("Items should be different instances", item2, item3);
    }

    @Test
    public void testEqualityOfScoreItems() {
        ScoreItem item1 = new ScoreItem(100);
        ScoreItem item2 = new ScoreItem(100);
        
        // 같은 값을 가져도 다른 인스턴스
        assertNotSame("Different instances with same score", item1, item2);
        assertEquals("Should have same score amount", item1.getScoreAmount(), item2.getScoreAmount());
        assertEquals("Should have same name", item1.getName(), item2.getName());
    }

    @Test
    public void testCommonScoreValues() {
        // 일반적으로 사용될 만한 점수 값들 테스트
        int[] commonScores = {10, 50, 100, 200, 500, 1000, 5000};
        
        for (int score : commonScores) {
            ScoreItem item = new ScoreItem(score);
            assertEquals("Score should be " + score, score, item.getScoreAmount());
            assertEquals("Name should be ScoreItem", "ScoreItem", item.getName());
        }
    }

    // ============ Immutability Tests ============

    @Test
    public void testScoreAmountCannotBeModified() {
        ScoreItem item = new ScoreItem(100);
        int originalScore = item.getScoreAmount();
        
        // getScoreAmount를 여러 번 호출해도 값이 변하지 않아야 함
        for (int i = 0; i < 10; i++) {
            assertEquals("Score should remain constant", originalScore, item.getScoreAmount());
        }
    }

    @Test
    public void testNameCannotBeModified() {
        ScoreItem item = new ScoreItem(100);
        String originalName = item.getName();
        
        // getName을 여러 번 호출해도 값이 변하지 않아야 함
        for (int i = 0; i < 10; i++) {
            assertEquals("Name should remain constant", originalName, item.getName());
        }
    }

    // ============ Type Safety Tests ============

    @Test
    public void testScoreItemTypeIsCorrect() {
        assertTrue("Should be instance of ScoreItem", scoreItem instanceof ScoreItem);
        assertTrue("Should be instance of Item", scoreItem instanceof Item);
        assertTrue("Should be instance of Object", scoreItem instanceof Object);
    }

    @Test
    public void testCanBeStoredAsItemInterface() {
        Item item = new ScoreItem(100);
        assertEquals("Should return correct name through interface", "ScoreItem", item.getName());
        
        // 다운캐스팅하여 getScoreAmount 접근 가능
        if (item instanceof ScoreItem) {
            ScoreItem scoreItem = (ScoreItem) item;
            assertEquals("Should return correct score through downcast", 100, scoreItem.getScoreAmount());
        } else {
            fail("Item should be castable to ScoreItem");
        }
    }

    // ============ Additional Functional Tests ============

    @Test
    public void testToStringDoesNotThrowException() {
        try {
            String result = scoreItem.toString();
            assertNotNull("toString should not return null", result);
        } catch (Exception e) {
            fail("toString should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testHashCodeDoesNotThrowException() {
        try {
            scoreItem.hashCode();
            // hashCode는 어떤 값이든 반환할 수 있음
            assertTrue("hashCode should be callable", true);
        } catch (Exception e) {
            fail("hashCode should not throw exception: " + e.getMessage());
        }
    }
}
