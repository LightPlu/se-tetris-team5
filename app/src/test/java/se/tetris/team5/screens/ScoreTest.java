package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import se.tetris.team5.ScreenController;
import se.tetris.team5.utils.score.ScoreManager;

/**
 * score.java 클래스의 커버리지를 높이기 위한 테스트
 */
public class ScoreTest {
    
    private score scoreScreen;
    private ScreenController mockController;
    private JTextPane testPane;
    
    @Before
    public void setUp() {
        mockController = new ScreenController() {
            @Override
            public void showScreen(String screenName) {
                // Mock implementation
            }
        };
        
        scoreScreen = new score(mockController);
        testPane = new JTextPane();
    }
    
    @Test
    public void testScoreConstructor() {
        assertNotNull("score 객체가 생성되어야 합니다", scoreScreen);
    }
    
    @Test 
    public void testDisplayMethod() {
        try {
            scoreScreen.display(testPane);
            
            Field currentTextPaneField = score.class.getDeclaredField("currentTextPane");
            currentTextPaneField.setAccessible(true);
            assertEquals(testPane, currentTextPaneField.get(scoreScreen));
            
        } catch (Exception e) {
            fail("display 메서드 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testBuildSampleEntries() {
        try {
            Method buildSampleMethod = score.class.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<ScoreManager.ScoreEntry> entries = (List<ScoreManager.ScoreEntry>) buildSampleMethod.invoke(scoreScreen);
            
            assertNotNull(entries);
            assertEquals(10, entries.size());
            
        } catch (Exception e) {
            fail("buildSampleEntries 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testCreateKoreanFont() {
        try {
            Method createFontMethod = score.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
            createFontMethod.setAccessible(true);
            
            Font font = (Font) createFontMethod.invoke(scoreScreen, Font.BOLD, 14);
            assertNotNull(font);
            assertEquals(Font.BOLD, font.getStyle());
            assertEquals(14, font.getSize());
            
        } catch (Exception e) {
            fail("createKoreanFont 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testGetGameModeDisplay() {
        try {
            Method getModeMethod = score.class.getDeclaredMethod("getGameModeDisplay", String.class);
            getModeMethod.setAccessible(true);
            
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, "ITEM"));
            assertEquals("쉬움", getModeMethod.invoke(scoreScreen, "NORMAL_EASY"));
            assertEquals("보통", getModeMethod.invoke(scoreScreen, "NORMAL_NORMAL"));
            assertEquals("어려움", getModeMethod.invoke(scoreScreen, "NORMAL_HARD"));
            assertEquals("전문가", getModeMethod.invoke(scoreScreen, "NORMAL_EXPERT"));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, (Object) null));
            
        } catch (Exception e) {
            fail("getGameModeDisplay 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testCreateModeTabPanel() {
        try {
            Method createTabMethod = score.class.getDeclaredMethod("createModeTabPanel");
            createTabMethod.setAccessible(true);
            
            JPanel tabPanel = (JPanel) createTabMethod.invoke(scoreScreen);
            assertNotNull(tabPanel);
            assertTrue(tabPanel.getComponentCount() >= 4);
            
        } catch (Exception e) {
            fail("createModeTabPanel 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testBuildPodiumPanel() {
        try {
            Method buildPodiumMethod = score.class.getDeclaredMethod("buildPodiumPanel");
            buildPodiumMethod.setAccessible(true);
            
            JPanel podiumPanel = (JPanel) buildPodiumMethod.invoke(scoreScreen);
            assertNotNull(podiumPanel);
            assertTrue(podiumPanel.getComponentCount() >= 3);
            
        } catch (Exception e) {
            fail("buildPodiumPanel 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testCreatePodiumBlock() {
        try {
            Method createBlockMethod = score.class.getDeclaredMethod("createPodiumBlock", int.class);
            createBlockMethod.setAccessible(true);
            
            for (int rank = 1; rank <= 3; rank++) {
                JPanel block = (JPanel) createBlockMethod.invoke(scoreScreen, rank);
                assertNotNull(block);
                assertEquals("podium-" + rank, block.getName());
            }
            
        } catch (Exception e) {
            fail("createPodiumBlock 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testBuildControlsPanel() {
        try {
            Method buildControlsMethod = score.class.getDeclaredMethod("buildControlsPanel");
            buildControlsMethod.setAccessible(true);
            
            JPanel controlsPanel = (JPanel) buildControlsMethod.invoke(scoreScreen);
            assertNotNull(controlsPanel);
            
        } catch (Exception e) {
            fail("buildControlsPanel 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testCreateBackgroundPanel() {
        try {
            Method createBgMethod = score.class.getDeclaredMethod("createBackgroundPanel");
            createBgMethod.setAccessible(true);
            
            JPanel bgPanel = (JPanel) createBgMethod.invoke(scoreScreen);
            assertNotNull(bgPanel);
            
        } catch (Exception e) {
            fail("createBackgroundPanel 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testBuildFooterImagePanel() {
        try {
            Method buildFooterMethod = score.class.getDeclaredMethod("buildFooterImagePanel");
            buildFooterMethod.setAccessible(true);
            
            JPanel footerPanel = (JPanel) buildFooterMethod.invoke(scoreScreen);
            assertNotNull(footerPanel);
            
        } catch (Exception e) {
            fail("buildFooterImagePanel 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testRenderScores() {
        try {
            scoreScreen.display(testPane);
            
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            renderMethod.invoke(scoreScreen);
            
        } catch (Exception e) {
            fail("renderScores 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testBuildUIMethod() {
        try {
            scoreScreen.display(testPane);
            
            Method buildUIMethod = score.class.getDeclaredMethod("buildUI");
            buildUIMethod.setAccessible(true);
            
            buildUIMethod.invoke(scoreScreen);
            
        } catch (Exception e) {
            fail("buildUI 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testHelperMethods() {
        try {
            scoreScreen.display(testPane);
            
            Method findAllMethod = score.class.getDeclaredMethod("findAllComponents", Container.class);
            findAllMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Component> components = (List<Component>) findAllMethod.invoke(scoreScreen, testPane);
            assertNotNull(components);
            
            Method findPageMethod = score.class.getDeclaredMethod("findPageInfoLabel", Container.class);
            findPageMethod.setAccessible(true);
            findPageMethod.invoke(scoreScreen, testPane);
            
            Method findHeaderMethod = score.class.getDeclaredMethod("findHeaderPanel", Container.class);
            findHeaderMethod.setAccessible(true);
            findHeaderMethod.invoke(scoreScreen, testPane);
            
            Method findListMethod = score.class.getDeclaredMethod("findListContainer", Container.class);
            findListMethod.setAccessible(true);
            findListMethod.invoke(scoreScreen, testPane);
            
        } catch (Exception e) {
            fail("Helper methods 실행 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListener() {
        try {
            scoreScreen.display(testPane);
            
            assertTrue(testPane.getKeyListeners().length > 0);
            
            var listener = testPane.getKeyListeners()[0];
            
            KeyEvent leftEvent = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
            KeyEvent rightEvent = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
            KeyEvent escapeEvent = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
            
            listener.keyPressed(leftEvent);
            listener.keyPressed(rightEvent);
            listener.keyPressed(escapeEvent);
            listener.keyTyped(leftEvent);
            listener.keyReleased(leftEvent);
            
        } catch (Exception e) {
            fail("KeyListener 테스트 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testFields() {
        try {
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            assertEquals("ITEM", modeField.get(scoreScreen));
            
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            assertEquals(0, pageField.get(scoreScreen));
            
            Field showOnlyDummyField = score.class.getDeclaredField("showOnlyDummy");
            showOnlyDummyField.setAccessible(true);
            assertEquals(false, showOnlyDummyField.get(scoreScreen));
            
        } catch (Exception e) {
            fail("필드 테스트 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testModeChange() {
        try {
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            
            String[] modes = {"ITEM", "NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD"};
            
            for (String mode : modes) {
                modeField.set(scoreScreen, mode);
                assertEquals(mode, modeField.get(scoreScreen));
                
                scoreScreen.display(testPane);
                
                Method renderMethod = score.class.getDeclaredMethod("renderScores");
                renderMethod.setAccessible(true);
                renderMethod.invoke(scoreScreen);
            }
            
        } catch (Exception e) {
            fail("모드 변경 테스트 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testPagination() {
        try {
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            
            scoreScreen.display(testPane);
            
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            for (int page = 0; page < 5; page++) {
                pageField.set(scoreScreen, page);
                renderMethod.invoke(scoreScreen);
            }
            
        } catch (Exception e) {
            fail("페이지네이션 테스트 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowOnlyDummyMode() {
        try {
            Field dummyField = score.class.getDeclaredField("showOnlyDummy");
            dummyField.setAccessible(true);
            
            dummyField.set(scoreScreen, true);
            scoreScreen.display(testPane);
            
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            renderMethod.invoke(scoreScreen);
            
        } catch (Exception e) {
            fail("showOnlyDummy 모드 테스트 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testConstants() {
        try {
            Field pageSizeField = score.class.getDeclaredField("PAGE_SIZE");
            pageSizeField.setAccessible(true);
            assertEquals(10, pageSizeField.get(null));
            
            Field rowHeightField = score.class.getDeclaredField("ROW_HEIGHT");
            rowHeightField.setAccessible(true);
            assertEquals(40, rowHeightField.get(null));
            
        } catch (Exception e) {
            fail("상수 테스트 실패: " + e.getMessage());
        }
    }
}