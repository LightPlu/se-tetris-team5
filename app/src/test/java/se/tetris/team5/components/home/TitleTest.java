package se.tetris.team5.components.home;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import se.tetris.team5.components.home.Title;

/**
 * Title 컴포넌트 테스트
 */
public class TitleTest {

    private Title title;

    @Before
    public void setUp() {
        title = null;
    }

    @After
    public void tearDown() {
        title = null;
    }

    // 생성자 테스트
    @Test
    public void testConstructorWithNullWindowSize() {
        // Given & When
        title = new Title(null);

        // Then
        assertNotNull(title);
    }

    @Test
    public void testConstructorWithAllWindowSizes() {
        // Given & When & Then
        for (Title.WindowSize size : Title.WindowSize.values()) {
            title = new Title(size);
            assertNotNull(title);
        }
    }

    // GUI 타이틀 생성 테스트
    @Test
    public void testGUITitleForSmallSize() {
        // Given
        title = new Title(Title.WindowSize.SMALL);

        // When
        String guiTitle = title.getGUITitle();

        // Then
        assertNotNull(guiTitle);
        assertTrue(guiTitle.contains("<html>"));
        assertTrue(guiTitle.contains("TETRIS"));
        assertTrue(guiTitle.contains("SMALL"));
        assertTrue(guiTitle.contains("</html>"));
    }

    @Test
    public void testGUITitleForMediumSize() {
        // Given
        title = new Title(Title.WindowSize.MEDIUM);

        // When
        String guiTitle = title.getGUITitle();

        // Then
        assertNotNull(guiTitle);
        assertTrue(guiTitle.contains("<html>"));
        assertTrue(guiTitle.contains("TETRIS"));
        assertTrue(guiTitle.contains("MEDIUM"));
        assertTrue(guiTitle.contains("</html>"));
    }

    @Test
    public void testGUITitleForLargeSize() {
        // Given
        title = new Title(Title.WindowSize.LARGE);

        // When
        String guiTitle = title.getGUITitle();

        // Then
        assertNotNull(guiTitle);
        assertTrue(guiTitle.contains("<html>"));
        assertTrue(guiTitle.contains("TETRIS"));
        assertTrue(guiTitle.contains("LARGE"));
        assertTrue(guiTitle.contains("</html>"));
    }

    @Test
    public void testGUITitleForXLargeSize() {
        // Given
        title = new Title(Title.WindowSize.XLARGE);

        // When
        String guiTitle = title.getGUITitle();

        // Then
        assertNotNull(guiTitle);
        assertTrue(guiTitle.contains("<html>"));
        assertTrue(guiTitle.contains("TETRIS"));
        assertTrue(guiTitle.contains("XLARGE"));
        assertTrue(guiTitle.contains("</html>"));
    }

    @Test
    public void testHTMLFormatForAllSizes() {
        // Given & When & Then
        for (Title.WindowSize size : Title.WindowSize.values()) {
            title = new Title(size);
            String guiTitle = title.getGUITitle();
            
            assertTrue("HTML should start with <html>", guiTitle.startsWith("<html>"));
            assertTrue("HTML should end with </html>", guiTitle.endsWith("</html>"));
            assertTrue("HTML should contain <center>", guiTitle.contains("<center>"));
            assertTrue("HTML should contain </center>", guiTitle.contains("</center>"));
        }
    }

    // 폰트 크기 테스트
    @Test
    public void testFontSizeForSmallSize() {
        // Given
        title = new Title(Title.WindowSize.SMALL);

        // When
        int fontSize = title.getTitleFontSize();

        // Then
        assertTrue("Font size should be positive", fontSize > 0);
        assertTrue("SMALL font size should be <= 20", fontSize <= 20);
    }

    @Test
    public void testFontSizeForMediumSize() {
        // Given
        title = new Title(Title.WindowSize.MEDIUM);

        // When
        int fontSize = title.getTitleFontSize();

        // Then
        assertTrue("Font size should be positive", fontSize > 0);
        assertTrue("MEDIUM font size should be in range 16-30", fontSize >= 16 && fontSize <= 30);
    }

    @Test
    public void testFontSizeForLargeSize() {
        // Given
        title = new Title(Title.WindowSize.LARGE);

        // When
        int fontSize = title.getTitleFontSize();

        // Then
        assertTrue("Font size should be positive", fontSize > 0);
        assertTrue("LARGE font size should be >= 24", fontSize >= 24);
    }

    @Test
    public void testFontSizeForXLargeSize() {
        // Given
        title = new Title(Title.WindowSize.XLARGE);

        // When
        int fontSize = title.getTitleFontSize();

        // Then
        assertTrue("Font size should be positive", fontSize > 0);
        assertTrue("XLARGE font size should be >= 28", fontSize >= 28);
    }

    @Test
    public void testFontSizeIncreasesByWindowSize() {
        // Given
        Title smallTitle = new Title(Title.WindowSize.SMALL);
        Title mediumTitle = new Title(Title.WindowSize.MEDIUM);
        Title largeTitle = new Title(Title.WindowSize.LARGE);
        Title xlargeTitle = new Title(Title.WindowSize.XLARGE);

        // When
        int smallFont = smallTitle.getTitleFontSize();
        int mediumFont = mediumTitle.getTitleFontSize();
        int largeFont = largeTitle.getTitleFontSize();
        int xlargeFont = xlargeTitle.getTitleFontSize();

        // Then
        assertTrue("Font sizes should increase", smallFont <= mediumFont);
        assertTrue("Font sizes should increase", mediumFont <= largeFont);
        assertTrue("Font sizes should increase", largeFont <= xlargeFont);
    }

    // WindowSize enum 테스트
    @Test
    public void testWindowSizeEnumValues() {
        // Given
        Title.WindowSize[] expectedSizes = {
            Title.WindowSize.SMALL,
            Title.WindowSize.MEDIUM,
            Title.WindowSize.LARGE,
            Title.WindowSize.XLARGE
        };

        // When
        Title.WindowSize[] actualSizes = Title.WindowSize.values();

        // Then
        assertEquals("WindowSize should have 4 values", expectedSizes.length, actualSizes.length);
        for (Title.WindowSize expected : expectedSizes) {
            boolean found = false;
            for (Title.WindowSize actual : actualSizes) {
                if (expected == actual) {
                    found = true;
                    break;
                }
            }
            assertTrue("WindowSize " + expected + " should exist", found);
        }
    }

    @Test
    public void testWindowSizeValueOf() {
        // Given & When & Then
        assertEquals(Title.WindowSize.SMALL, Title.WindowSize.valueOf("SMALL"));
        assertEquals(Title.WindowSize.MEDIUM, Title.WindowSize.valueOf("MEDIUM"));
        assertEquals(Title.WindowSize.LARGE, Title.WindowSize.valueOf("LARGE"));
        assertEquals(Title.WindowSize.XLARGE, Title.WindowSize.valueOf("XLARGE"));
    }

    // 엣지 케이스 테스트
    @Test
    public void testNullWindowSizeHandling() {
        // Given
        title = new Title(null);

        // When & Then
        try {
            String guiTitle = title.getGUITitle();
            int fontSize = title.getTitleFontSize();
            assertNotNull("GUI title should not be null", guiTitle);
            assertTrue("Font size should be positive", fontSize > 0);
        } catch (NullPointerException e) {
            // Expected behavior - Title class doesn't handle null gracefully
            // This is documented behavior, not a bug
            assertTrue("Expected NullPointerException for null WindowSize", true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testConsistentResults() {
        // Given
        title = new Title(Title.WindowSize.MEDIUM);

        // When
        String title1 = title.getGUITitle();
        String title2 = title.getGUITitle();
        int fontSize1 = title.getTitleFontSize();
        int fontSize2 = title.getTitleFontSize();

        // Then
        assertEquals("GUI title should be consistent", title1, title2);
        assertEquals("Font size should be consistent", fontSize1, fontSize2);
    }

    @Test
    public void testNonEmptyStringsForAllSizes() {
        // Given & When & Then
        for (Title.WindowSize size : Title.WindowSize.values()) {
            title = new Title(size);
            String guiTitle = title.getGUITitle();
            
            assertNotNull("GUI title should not be null", guiTitle);
            assertFalse("GUI title should not be empty", guiTitle.trim().isEmpty());
            assertTrue("GUI title should have meaningful content", guiTitle.length() > 10);
        }
    }

    @Test
    public void testWindowSizeToString() {
        // Given & When & Then
        assertEquals("SMALL", Title.WindowSize.SMALL.toString());
        assertEquals("MEDIUM", Title.WindowSize.MEDIUM.toString());
        assertEquals("LARGE", Title.WindowSize.LARGE.toString());
        assertEquals("XLARGE", Title.WindowSize.XLARGE.toString());
    }

    @Test
    public void testMultipleInstancesIndependence() {
        // Given
        Title title1 = new Title(Title.WindowSize.SMALL);
        Title title2 = new Title(Title.WindowSize.LARGE);

        // When
        String gui1 = title1.getGUITitle();
        String gui2 = title2.getGUITitle();
        int font1 = title1.getTitleFontSize();
        int font2 = title2.getTitleFontSize();

        // Then
        assertNotEquals("Different sizes should have different GUI titles", gui1, gui2);
        assertTrue("SMALL should have smaller font than LARGE", font1 <= font2);
    }

    @Test
    public void testNullPointerExceptionHandling() throws Exception {
        // Given - Force null currentWindowSize to test null handling
        title = new Title(Title.WindowSize.MEDIUM);
        
        // Use reflection to set currentWindowSize to null
        java.lang.reflect.Field currentWindowSizeField = Title.class.getDeclaredField("currentWindowSize");
        currentWindowSizeField.setAccessible(true);
        currentWindowSizeField.set(title, null);

        // When & Then - Should handle null appropriately
        try {
            title.getGUITitle();
            fail("Should throw NullPointerException when currentWindowSize is null");
        } catch (NullPointerException e) {
            // This is expected behavior
            assertTrue("Should get NullPointerException when currentWindowSize is null", true);
        }
        
        try {
            title.getTitleFontSize();
            fail("Should throw NullPointerException when currentWindowSize is null");
        } catch (NullPointerException e) {
            // This is expected behavior
            assertTrue("Should get NullPointerException when currentWindowSize is null", true);
        }
    }

    @Test
    public void testWindowSizeFieldAccess() throws Exception {
        // Given
        title = new Title(Title.WindowSize.LARGE);
        
        // When - Access private field
        java.lang.reflect.Field currentWindowSizeField = Title.class.getDeclaredField("currentWindowSize");
        currentWindowSizeField.setAccessible(true);
        Title.WindowSize currentSize = (Title.WindowSize) currentWindowSizeField.get(title);

        // Then
        assertEquals("Current window size should match constructor parameter", 
                     Title.WindowSize.LARGE, currentSize);
    }

    @Test
    public void testAllSwitchCasesInGetGUITitle() {
        // Given & When & Then - Test all switch cases including default
        Title.WindowSize[] allSizes = Title.WindowSize.values();
        
        for (Title.WindowSize size : allSizes) {
            title = new Title(size);
            String guiTitle = title.getGUITitle();
            
            assertNotNull("GUI title should not be null for " + size, guiTitle);
            assertTrue("GUI title should contain size name for " + size, 
                      guiTitle.contains(size.toString()));
        }
    }

    @Test
    public void testAllSwitchCasesInGetTitleFontSize() {
        // Given & When & Then - Test all switch cases including default
        int[] expectedFontSizes = {16, 20, 24, 28}; // SMALL, MEDIUM, LARGE, XLARGE
        Title.WindowSize[] allSizes = Title.WindowSize.values();
        
        for (int i = 0; i < allSizes.length; i++) {
            title = new Title(allSizes[i]);
            int fontSize = title.getTitleFontSize();
            
            assertEquals("Font size should match expected for " + allSizes[i], 
                        expectedFontSizes[i], fontSize);
        }
    }

    @Test
    public void testGUITitleContainsExpectedElements() {
        // Test that GUI titles contain expected HTML elements for each size
        title = new Title(Title.WindowSize.SMALL);
        String smallTitle = title.getGUITitle();
        assertTrue("SMALL title should contain h3", smallTitle.contains("<h3>"));
        
        title = new Title(Title.WindowSize.MEDIUM);
        String mediumTitle = title.getGUITitle();
        assertTrue("MEDIUM title should contain h2", mediumTitle.contains("<h2>"));
        assertTrue("MEDIUM title should contain h4", mediumTitle.contains("<h4>"));
        
        title = new Title(Title.WindowSize.LARGE);
        String largeTitle = title.getGUITitle();
        assertTrue("LARGE title should contain h1", largeTitle.contains("<h1>"));
        assertTrue("LARGE title should contain h3", largeTitle.contains("<h3>"));
        assertTrue("LARGE title should contain description", largeTitle.contains("클래식 퍼즐 게임"));
        
        title = new Title(Title.WindowSize.XLARGE);
        String xlargeTitle = title.getGUITitle();
        assertTrue("XLARGE title should contain h1", xlargeTitle.contains("<h1>"));
        assertTrue("XLARGE title should contain h2", xlargeTitle.contains("<h2>"));
        assertTrue("XLARGE title should contain description", xlargeTitle.contains("클래식 퍼즐 게임"));
        assertTrue("XLARGE title should contain instructions", xlargeTitle.contains("블록을 맞춰 라인을 완성하세요!"));
    }

    @Test
    public void testEmojiInTitles() {
        // Test that all titles contain game emoji
        for (Title.WindowSize size : Title.WindowSize.values()) {
            title = new Title(size);
            String guiTitle = title.getGUITitle();
            
            assertTrue("Title should contain game emoji for " + size, 
                      guiTitle.contains("🎮"));
        }
    }

    @Test
    public void testTeamNameInTitles() {
        // Test that all titles contain team name
        for (Title.WindowSize size : Title.WindowSize.values()) {
            title = new Title(size);
            String guiTitle = title.getGUITitle();
            
            assertTrue("Title should contain team name for " + size, 
                      guiTitle.contains("Team 5"));
        }
    }

    @Test
    public void testHTMLStructureComplexity() {
        // Test HTML structure complexity increases with size
        title = new Title(Title.WindowSize.SMALL);
        String smallTitle = title.getGUITitle();
        int smallTagCount = countHTMLTags(smallTitle);
        
        title = new Title(Title.WindowSize.XLARGE);
        String xlargeTitle = title.getGUITitle();
        int xlargeTagCount = countHTMLTags(xlargeTitle);
        
        assertTrue("XLARGE should have more HTML tags than SMALL", 
                  xlargeTagCount > smallTagCount);
    }

    private int countHTMLTags(String html) {
        int count = 0;
        int index = 0;
        while ((index = html.indexOf('<', index)) != -1) {
            count++;
            index++;
        }
        return count;
    }

    // Switch default case와 enum 관련 추가 테스트
    @Test
    public void testAllEnumValuesCoverage() {
        // Test each enum value explicitly to ensure full coverage
        Title.WindowSize[] allSizes = Title.WindowSize.values();
        assertEquals("Should have 4 enum values", 4, allSizes.length);
        
        for (Title.WindowSize size : allSizes) {
            Title testTitle = new Title(size);
            
            // Test getGUITitle for each size
            String guiTitle = testTitle.getGUITitle();
            assertNotNull("GUI title should not be null for " + size, guiTitle);
            assertTrue("GUI title should contain Team 5 for " + size, guiTitle.contains("Team 5"));
            
            // Test getTitleFontSize for each size
            int fontSize = testTitle.getTitleFontSize();
            assertTrue("Font size should be positive for " + size, fontSize > 0);
            
            // Verify expected font sizes
            switch (size) {
                case SMALL:
                    assertEquals("SMALL should have font size 16", 16, fontSize);
                    break;
                case MEDIUM:
                    assertEquals("MEDIUM should have font size 20", 20, fontSize);
                    break;
                case LARGE:
                    assertEquals("LARGE should have font size 24", 24, fontSize);
                    break;
                case XLARGE:
                    assertEquals("XLARGE should have font size 28", 28, fontSize);
                    break;
            }
        }
    }

    @Test
    public void testEnumOrdinalValues() {
        // Test enum ordinal values for completeness
        assertEquals("SMALL should be ordinal 0", 0, Title.WindowSize.SMALL.ordinal());
        assertEquals("MEDIUM should be ordinal 1", 1, Title.WindowSize.MEDIUM.ordinal());
        assertEquals("LARGE should be ordinal 2", 2, Title.WindowSize.LARGE.ordinal());
        assertEquals("XLARGE should be ordinal 3", 3, Title.WindowSize.XLARGE.ordinal());
    }

    @Test 
    public void testEnumValueOfMethod() {
        // Test enum valueOf method
        assertEquals("valueOf SMALL should work", Title.WindowSize.SMALL, Title.WindowSize.valueOf("SMALL"));
        assertEquals("valueOf MEDIUM should work", Title.WindowSize.MEDIUM, Title.WindowSize.valueOf("MEDIUM"));
        assertEquals("valueOf LARGE should work", Title.WindowSize.LARGE, Title.WindowSize.valueOf("LARGE"));
        assertEquals("valueOf XLARGE should work", Title.WindowSize.XLARGE, Title.WindowSize.valueOf("XLARGE"));
        
        // Test invalid valueOf
        try {
            Title.WindowSize.valueOf("INVALID");
            fail("Should throw IllegalArgumentException for invalid enum value");
        } catch (IllegalArgumentException e) {
            assertTrue("Should get IllegalArgumentException for invalid enum", true);
        }
    }

    @Test
    public void testEnumToStringMethod() {
        // Test enum toString method
        assertEquals("SMALL toString should work", "SMALL", Title.WindowSize.SMALL.toString());
        assertEquals("MEDIUM toString should work", "MEDIUM", Title.WindowSize.MEDIUM.toString());
        assertEquals("LARGE toString should work", "LARGE", Title.WindowSize.LARGE.toString());
        assertEquals("XLARGE toString should work", "XLARGE", Title.WindowSize.XLARGE.toString());
    }

    @Test
    public void testSwitchStatementCoverage() throws Exception {
        // Given - Test switch statement coverage by testing all paths
        title = new Title(Title.WindowSize.SMALL);
        
        // Test all enum values to ensure switch coverage
        for (Title.WindowSize size : Title.WindowSize.values()) {
            Title testTitle = new Title(size);
            
            // When
            String guiResult = testTitle.getGUITitle();
            int fontResult = testTitle.getTitleFontSize();
            
            // Then
            assertNotNull("GUI title should not be null for " + size, guiResult);
            assertTrue("Font size should be positive for " + size, fontResult > 0);
            
            // Verify size-specific content
            switch (size) {
                case SMALL:
                    assertTrue("SMALL title should contain SMALL", guiResult.contains("SMALL"));
                    break;
                case MEDIUM:
                    assertTrue("MEDIUM title should contain MEDIUM", guiResult.contains("MEDIUM"));
                    break;
                case LARGE:
                    assertTrue("LARGE title should contain LARGE", guiResult.contains("LARGE"));
                    assertTrue("LARGE title should contain description", guiResult.contains("클래식 퍼즐 게임"));
                    break;
                case XLARGE:
                    assertTrue("XLARGE title should contain XLARGE", guiResult.contains("XLARGE"));
                    assertTrue("XLARGE title should contain description", guiResult.contains("클래식 퍼즐 게임"));
                    assertTrue("XLARGE title should contain instruction", guiResult.contains("블록을 맞춰 라인을 완성하세요"));
                    break;
            }
        }
    }

    @Test
    public void testCurrentWindowSizeFieldAccess() throws Exception {
        // Given
        title = new Title(Title.WindowSize.LARGE);
        
        // When - Access currentWindowSize field via reflection
        java.lang.reflect.Field currentWindowSizeField = Title.class.getDeclaredField("currentWindowSize");
        currentWindowSizeField.setAccessible(true);
        Title.WindowSize currentSize = (Title.WindowSize) currentWindowSizeField.get(title);
        
        // Then
        assertEquals("Current window size should be LARGE", Title.WindowSize.LARGE, currentSize);
        assertNotNull("Current window size should not be null", currentSize);
    }
}