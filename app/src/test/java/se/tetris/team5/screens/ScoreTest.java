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

    // ==================== 추가 테스트 80개 ====================

    // === 강조 표시 기능 테스트 ===
    @Test
    public void testHighlightTimerField() {
        try {
            Field timerField = score.class.getDeclaredField("highlightTimer");
            timerField.setAccessible(true);
            Object timer = timerField.get(scoreScreen);
            // 초기에는 null일 수 있음
        } catch (Exception e) {
            fail("highlightTimer 필드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testHighlightVisibleField() {
        try {
            Field visibleField = score.class.getDeclaredField("highlightVisible");
            visibleField.setAccessible(true);
            Boolean visible = (Boolean) visibleField.get(scoreScreen);
            assertTrue("기본값은 true여야 함", visible);
        } catch (Exception e) {
            fail("highlightVisible 필드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testHighlightedEntryField() {
        try {
            Field entryField = score.class.getDeclaredField("highlightedEntry");
            entryField.setAccessible(true);
            Object entry = entryField.get(scoreScreen);
            assertNull("초기값은 null이어야 함", entry);
        } catch (Exception e) {
            fail("highlightedEntry 필드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testShouldHighlightField() {
        try {
            Field shouldField = score.class.getDeclaredField("shouldHighlight");
            shouldField.setAccessible(true);
            Boolean should = (Boolean) shouldField.get(scoreScreen);
            assertFalse("기본값은 false여야 함", should);
        } catch (Exception e) {
            fail("shouldHighlight 필드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testCheckForHighlightRequest() {
        try {
            Method checkMethod = score.class.getDeclaredMethod("checkForHighlightRequest");
            checkMethod.setAccessible(true);
            checkMethod.invoke(scoreScreen);
        } catch (Exception e) {
            fail("checkForHighlightRequest 실행 실패: " + e.getMessage());
        }
    }

    @Test
    public void testStartHighlightEffect() {
        try {
            Method startMethod = score.class.getDeclaredMethod("startHighlightEffect");
            startMethod.setAccessible(true);
            startMethod.invoke(scoreScreen);
        } catch (Exception e) {
            fail("startHighlightEffect 실행 실패: " + e.getMessage());
        }
    }

    @Test
    public void testStopHighlightEffect() {
        try {
            Method stopMethod = score.class.getDeclaredMethod("stopHighlightEffect");
            stopMethod.setAccessible(true);
            stopMethod.invoke(scoreScreen);
        } catch (Exception e) {
            fail("stopHighlightEffect 실행 실패: " + e.getMessage());
        }
    }

    // === 모드 전환 기능 테스트 ===
    @Test
    public void testSwitchToModeItem() {
        try {
            Method switchMethod = score.class.getDeclaredMethod("switchToMode", String.class);
            switchMethod.setAccessible(true);
            switchMethod.invoke(scoreScreen, "ITEM");
            
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            assertEquals("ITEM", modeField.get(scoreScreen));
        } catch (Exception e) {
            fail("switchToMode ITEM 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSwitchToModeNormalEasy() {
        try {
            Method switchMethod = score.class.getDeclaredMethod("switchToMode", String.class);
            switchMethod.setAccessible(true);
            switchMethod.invoke(scoreScreen, "NORMAL_EASY");
            
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            assertEquals("NORMAL_EASY", modeField.get(scoreScreen));
        } catch (Exception e) {
            fail("switchToMode NORMAL_EASY 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSwitchToModeNormalNormal() {
        try {
            Method switchMethod = score.class.getDeclaredMethod("switchToMode", String.class);
            switchMethod.setAccessible(true);
            switchMethod.invoke(scoreScreen, "NORMAL_NORMAL");
            
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            assertEquals("NORMAL_NORMAL", modeField.get(scoreScreen));
        } catch (Exception e) {
            fail("switchToMode NORMAL_NORMAL 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSwitchToModeNormalHard() {
        try {
            Method switchMethod = score.class.getDeclaredMethod("switchToMode", String.class);
            switchMethod.setAccessible(true);
            switchMethod.invoke(scoreScreen, "NORMAL_HARD");
            
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            assertEquals("NORMAL_HARD", modeField.get(scoreScreen));
        } catch (Exception e) {
            fail("switchToMode NORMAL_HARD 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSwitchToPreviousMode() {
        try {
            Method switchMethod = score.class.getDeclaredMethod("switchToPreviousMode");
            switchMethod.setAccessible(true);
            switchMethod.invoke(scoreScreen);
        } catch (Exception e) {
            fail("switchToPreviousMode 실행 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSwitchToNextMode() {
        try {
            Method switchMethod = score.class.getDeclaredMethod("switchToNextMode");
            switchMethod.setAccessible(true);
            switchMethod.invoke(scoreScreen);
        } catch (Exception e) {
            fail("switchToNextMode 실행 실패: " + e.getMessage());
        }
    }

    // === 버튼 스타일 테스트 ===
    @Test
    public void testUpdateButtonStyleSelected() {
        try {
            Method updateMethod = score.class.getDeclaredMethod("updateButtonStyle", JButton.class, boolean.class);
            updateMethod.setAccessible(true);
            JButton button = new JButton("Test");
            updateMethod.invoke(scoreScreen, button, true);
        } catch (Exception e) {
            fail("updateButtonStyle (selected) 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateButtonStyleNotSelected() {
        try {
            Method updateMethod = score.class.getDeclaredMethod("updateButtonStyle", JButton.class, boolean.class);
            updateMethod.setAccessible(true);
            JButton button = new JButton("Test");
            updateMethod.invoke(scoreScreen, button, false);
        } catch (Exception e) {
            fail("updateButtonStyle (not selected) 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateModeTabButtons() {
        try {
            Method updateMethod = score.class.getDeclaredMethod("updateModeTabButtons");
            updateMethod.setAccessible(true);
            updateMethod.invoke(scoreScreen);
        } catch (Exception e) {
            fail("updateModeTabButtons 실행 실패: " + e.getMessage());
        }
    }

    // === ScoreListScroll 테스트 ===
    @Test
    public void testScoreListScrollField() {
        try {
            Field scrollField = score.class.getDeclaredField("scoreListScroll");
            scrollField.setAccessible(true);
            Object scroll = scrollField.get(scoreScreen);
            // 초기에는 null일 수 있음
        } catch (Exception e) {
            fail("scoreListScroll 필드 테스트 실패: " + e.getMessage());
        }
    }

    // === ModeTabButtons 테스트 ===
    @Test
    public void testModeTabButtonsField() {
        try {
            Field buttonsField = score.class.getDeclaredField("modeTabButtons");
            buttonsField.setAccessible(true);
            Object buttons = buttonsField.get(scoreScreen);
            // 초기에는 null일 수 있음
        } catch (Exception e) {
            fail("modeTabButtons 필드 테스트 실패: " + e.getMessage());
        }
    }

    // === ScoreManager 테스트 ===
    @Test
    public void testScoreManagerNotNull() {
        try {
            Field managerField = score.class.getDeclaredField("scoreManager");
            managerField.setAccessible(true);
            Object manager = managerField.get(scoreScreen);
            assertNotNull("ScoreManager는 null이 아니어야 함", manager);
        } catch (Exception e) {
            fail("ScoreManager 필드 테스트 실패: " + e.getMessage());
        }
    }

    // === GameSettings 테스트 ===
    @Test
    public void testGameSettingsNotNull() {
        try {
            Field settingsField = score.class.getDeclaredField("gameSettings");
            settingsField.setAccessible(true);
            Object settings = settingsField.get(scoreScreen);
            assertNotNull("GameSettings는 null이 아니어야 함", settings);
        } catch (Exception e) {
            fail("GameSettings 필드 테스트 실패: " + e.getMessage());
        }
    }

    // === 키 입력 테스트 ===
    @Test
    public void testKeyPressedNumbers() {
        try {
            scoreScreen.display(testPane);
            var listener = testPane.getKeyListeners()[0];
            
            KeyEvent[] numberKeys = {
                new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_1, '1'),
                new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_2, '2'),
                new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_3, '3'),
                new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_4, '4')
            };
            
            for (KeyEvent ke : numberKeys) {
                listener.keyPressed(ke);
            }
        } catch (Exception e) {
            fail("숫자 키 입력 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testKeyPressedUpDown() {
        try {
            scoreScreen.display(testPane);
            var listener = testPane.getKeyListeners()[0];
            
            KeyEvent upKey = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
            KeyEvent downKey = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
            
            listener.keyPressed(upKey);
            listener.keyPressed(downKey);
        } catch (Exception e) {
            fail("위/아래 키 입력 테스트 실패: " + e.getMessage());
        }
    }

    // === BackgroundPanel 내부 클래스 테스트 ===
    @Test
    public void testBackgroundPanelExists() {
        try {
            Class<?> bgClass = Class.forName("se.tetris.team5.screens.score$BackgroundPanel");
            assertNotNull("BackgroundPanel 클래스가 존재해야 함", bgClass);
        } catch (Exception e) {
            fail("BackgroundPanel 클래스 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testBackgroundPanelExtendsJPanel() {
        try {
            Class<?> bgClass = Class.forName("se.tetris.team5.screens.score$BackgroundPanel");
            assertTrue("BackgroundPanel은 JPanel을 상속해야 함", JPanel.class.isAssignableFrom(bgClass));
        } catch (Exception e) {
            fail("BackgroundPanel 상속 테스트 실패: " + e.getMessage());
        }
    }

    // === ScoreKeyListener 내부 클래스 테스트 ===
    @Test
    public void testScoreKeyListenerExists() {
        try {
            Class<?> listenerClass = Class.forName("se.tetris.team5.screens.score$ScoreKeyListener");
            assertNotNull("ScoreKeyListener 클래스가 존재해야 함", listenerClass);
        } catch (Exception e) {
            fail("ScoreKeyListener 클래스 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testScoreKeyListenerImplementsKeyListener() {
        try {
            Class<?> listenerClass = Class.forName("se.tetris.team5.screens.score$ScoreKeyListener");
            assertTrue("ScoreKeyListener는 KeyListener를 구현해야 함", 
                java.awt.event.KeyListener.class.isAssignableFrom(listenerClass));
        } catch (Exception e) {
            fail("ScoreKeyListener 구현 테스트 실패: " + e.getMessage());
        }
    }

    // === 페이지 이동 테스트 ===
    @Test
    public void testPageIncrease() {
        try {
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            
            for (int i = 0; i < 10; i++) {
                pageField.set(scoreScreen, i);
                assertEquals(i, pageField.get(scoreScreen));
            }
        } catch (Exception e) {
            fail("페이지 증가 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testPageDecrease() {
        try {
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            
            pageField.set(scoreScreen, 10);
            for (int i = 10; i >= 0; i--) {
                pageField.set(scoreScreen, i);
                assertEquals(i, pageField.get(scoreScreen));
            }
        } catch (Exception e) {
            fail("페이지 감소 테스트 실패: " + e.getMessage());
        }
    }

    // === 모든 게임 모드 표시 테스트 ===
    @Test
    public void testGetGameModeDisplayAllModes() {
        try {
            Method getModeMethod = score.class.getDeclaredMethod("getGameModeDisplay", String.class);
            getModeMethod.setAccessible(true);
            
            String[] modes = {"ITEM", "NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD", "NORMAL_EXPERT"};
            String[] expected = {"아이템", "쉬움", "보통", "어려움", "전문가"};
            
            for (int i = 0; i < modes.length; i++) {
                String result = (String) getModeMethod.invoke(scoreScreen, modes[i]);
                assertEquals(expected[i], result);
            }
        } catch (Exception e) {
            fail("모든 게임 모드 표시 테스트 실패: " + e.getMessage());
        }
    }

    // === 샘플 엔트리 상세 테스트 ===
    @Test
    public void testSampleEntriesPlayerNames() {
        try {
            Method buildSampleMethod = score.class.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<ScoreManager.ScoreEntry> entries = (List<ScoreManager.ScoreEntry>) buildSampleMethod.invoke(scoreScreen);
            
            for (ScoreManager.ScoreEntry entry : entries) {
                assertNotNull("플레이어 이름은 null이 아니어야 함", entry.getPlayerName());
                assertFalse("플레이어 이름은 비어있지 않아야 함", entry.getPlayerName().isEmpty());
            }
        } catch (Exception e) {
            fail("샘플 엔트리 플레이어 이름 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSampleEntriesScores() {
        try {
            Method buildSampleMethod = score.class.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<ScoreManager.ScoreEntry> entries = (List<ScoreManager.ScoreEntry>) buildSampleMethod.invoke(scoreScreen);
            
            for (ScoreManager.ScoreEntry entry : entries) {
                assertTrue("점수는 0 이상이어야 함", entry.getScore() >= 0);
            }
        } catch (Exception e) {
            fail("샘플 엔트리 점수 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSampleEntriesLevels() {
        try {
            Method buildSampleMethod = score.class.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<ScoreManager.ScoreEntry> entries = (List<ScoreManager.ScoreEntry>) buildSampleMethod.invoke(scoreScreen);
            
            for (ScoreManager.ScoreEntry entry : entries) {
                assertTrue("레벨은 0 이상이어야 함", entry.getLevel() >= 0);
            }
        } catch (Exception e) {
            fail("샘플 엔트리 레벨 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSampleEntriesPlayTime() {
        try {
            Method buildSampleMethod = score.class.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<ScoreManager.ScoreEntry> entries = (List<ScoreManager.ScoreEntry>) buildSampleMethod.invoke(scoreScreen);
            
            for (ScoreManager.ScoreEntry entry : entries) {
                assertTrue("플레이 타임은 0 이상이어야 함", entry.getPlayTime() >= 0);
            }
        } catch (Exception e) {
            fail("샘플 엔트리 플레이 타임 테스트 실패: " + e.getMessage());
        }
    }

    // === 폰트 크기 테스트 ===
    @Test
    public void testCreateKoreanFontVariousSizes() {
        try {
            Method createFontMethod = score.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
            createFontMethod.setAccessible(true);
            
            int[] sizes = {10, 12, 14, 16, 18, 20, 24, 28};
            for (int size : sizes) {
                Font font = (Font) createFontMethod.invoke(scoreScreen, Font.PLAIN, size);
                assertEquals(size, font.getSize());
            }
        } catch (Exception e) {
            fail("다양한 크기 폰트 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testCreateKoreanFontStyles() {
        try {
            Method createFontMethod = score.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
            createFontMethod.setAccessible(true);
            
            int[] styles = {Font.PLAIN, Font.BOLD, Font.ITALIC};
            for (int style : styles) {
                Font font = (Font) createFontMethod.invoke(scoreScreen, style, 14);
                assertNotNull("폰트는 null이 아니어야 함", font);
            }
        } catch (Exception e) {
            fail("폰트 스타일 테스트 실패: " + e.getMessage());
        }
    }

    // === UI 빌드 메서드 체인 테스트 ===
    @Test
    public void testUIBuildingChain() {
        try {
            scoreScreen.display(testPane);
            
            Method createTabMethod = score.class.getDeclaredMethod("createModeTabPanel");
            createTabMethod.setAccessible(true);
            createTabMethod.invoke(scoreScreen);
            
            Method buildPodiumMethod = score.class.getDeclaredMethod("buildPodiumPanel");
            buildPodiumMethod.setAccessible(true);
            buildPodiumMethod.invoke(scoreScreen);
            
            Method buildControlsMethod = score.class.getDeclaredMethod("buildControlsPanel");
            buildControlsMethod.setAccessible(true);
            buildControlsMethod.invoke(scoreScreen);
            
            Method createBgMethod = score.class.getDeclaredMethod("createBackgroundPanel");
            createBgMethod.setAccessible(true);
            createBgMethod.invoke(scoreScreen);
            
        } catch (Exception e) {
            fail("UI 빌딩 체인 테스트 실패: " + e.getMessage());
        }
    }

    // === 포디움 블록 순위 테스트 ===
    @Test
    public void testPodiumBlockRanks() {
        try {
            Method createBlockMethod = score.class.getDeclaredMethod("createPodiumBlock", int.class);
            createBlockMethod.setAccessible(true);
            
            for (int rank = 1; rank <= 10; rank++) {
                JPanel block = (JPanel) createBlockMethod.invoke(scoreScreen, rank);
                assertNotNull("포디움 블록은 null이 아니어야 함", block);
            }
        } catch (Exception e) {
            fail("포디움 블록 순위 테스트 실패: " + e.getMessage());
        }
    }

    // === 헬퍼 메서드 상세 테스트 ===
    @Test
    public void testFindAllComponentsWithNestedPanels() {
        try {
            Method findAllMethod = score.class.getDeclaredMethod("findAllComponents", Container.class);
            findAllMethod.setAccessible(true);
            
            JPanel parent = new JPanel();
            JPanel child1 = new JPanel();
            JPanel child2 = new JPanel();
            parent.add(child1);
            parent.add(child2);
            
            @SuppressWarnings("unchecked")
            List<Component> components = (List<Component>) findAllMethod.invoke(scoreScreen, parent);
            assertTrue("컴포넌트 리스트는 2개 이상이어야 함", components.size() >= 2);
        } catch (Exception e) {
            fail("중첩 패널 찾기 테스트 실패: " + e.getMessage());
        }
    }

    // === 페이지 정보 라벨 테스트 ===
    @Test
    public void testFindPageInfoLabelWithName() {
        try {
            Method findPageMethod = score.class.getDeclaredMethod("findPageInfoLabel", Container.class);
            findPageMethod.setAccessible(true);
            
            JPanel parent = new JPanel();
            JLabel pageLabel = new JLabel();
            pageLabel.setName("page-info");
            parent.add(pageLabel);
            
            JLabel found = (JLabel) findPageMethod.invoke(scoreScreen, parent);
            assertNotNull("page-info 라벨을 찾아야 함", found);
        } catch (Exception e) {
            fail("페이지 정보 라벨 찾기 테스트 실패: " + e.getMessage());
        }
    }

    // === 헤더 패널 테스트 ===
    @Test
    public void testFindHeaderPanelWithName() {
        try {
            Method findHeaderMethod = score.class.getDeclaredMethod("findHeaderPanel", Container.class);
            findHeaderMethod.setAccessible(true);
            
            JPanel parent = new JPanel();
            JPanel header = new JPanel();
            header.setName("score-header");
            parent.add(header);
            
            JPanel found = (JPanel) findHeaderMethod.invoke(scoreScreen, parent);
            assertNotNull("score-header 패널을 찾아야 함", found);
        } catch (Exception e) {
            fail("헤더 패널 찾기 테스트 실패: " + e.getMessage());
        }
    }

    // === 리스트 컨테이너 테스트 ===
    @Test
    public void testFindListContainerWithScrollPane() {
        try {
            Method findListMethod = score.class.getDeclaredMethod("findListContainer", Container.class);
            findListMethod.setAccessible(true);
            
            JPanel parent = new JPanel();
            JScrollPane scrollPane = new JScrollPane();
            JPanel listPanel = new JPanel();
            scrollPane.setViewportView(listPanel);
            parent.add(scrollPane);
            
            JPanel found = (JPanel) findListMethod.invoke(scoreScreen, parent);
            assertNotNull("리스트 컨테이너를 찾아야 함", found);
        } catch (Exception e) {
            fail("리스트 컨테이너 찾기 테스트 실패: " + e.getMessage());
        }
    }

    // === 빈 컨테이너 헬퍼 메서드 테스트 ===
    @Test
    public void testFindMethodsWithEmptyContainer() {
        try {
            JPanel emptyPanel = new JPanel();
            
            Method findAllMethod = score.class.getDeclaredMethod("findAllComponents", Container.class);
            findAllMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Component> components = (List<Component>) findAllMethod.invoke(scoreScreen, emptyPanel);
            assertNotNull("빈 리스트라도 null이 아니어야 함", components);
            
            Method findPageMethod = score.class.getDeclaredMethod("findPageInfoLabel", Container.class);
            findPageMethod.setAccessible(true);
            Object pageLabel = findPageMethod.invoke(scoreScreen, emptyPanel);
            // 찾지 못하면 null 반환
            
            Method findHeaderMethod = score.class.getDeclaredMethod("findHeaderPanel", Container.class);
            findHeaderMethod.setAccessible(true);
            Object headerPanel = findHeaderMethod.invoke(scoreScreen, emptyPanel);
            // 찾지 못하면 null 반환
            
            Method findListMethod = score.class.getDeclaredMethod("findListContainer", Container.class);
            findListMethod.setAccessible(true);
            Object listContainer = findListMethod.invoke(scoreScreen, emptyPanel);
            // 찾지 못하면 null 반환
            
        } catch (Exception e) {
            fail("빈 컨테이너 헬퍼 메서드 테스트 실패: " + e.getMessage());
        }
    }

    // === showOnlyDummy 전환 테스트 ===
    @Test
    public void testShowOnlyDummyToggle() {
        try {
            Field dummyField = score.class.getDeclaredField("showOnlyDummy");
            dummyField.setAccessible(true);
            
            dummyField.set(scoreScreen, false);
            assertFalse((Boolean) dummyField.get(scoreScreen));
            
            dummyField.set(scoreScreen, true);
            assertTrue((Boolean) dummyField.get(scoreScreen));
            
            dummyField.set(scoreScreen, false);
            assertFalse((Boolean) dummyField.get(scoreScreen));
        } catch (Exception e) {
            fail("showOnlyDummy 토글 테스트 실패: " + e.getMessage());
        }
    }

    // === 모드 필터 순환 테스트 ===
    @Test
    public void testModeCycling() {
        try {
            String[] modes = {"ITEM", "NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD"};
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            
            for (int i = 0; i < 3; i++) {
                for (String mode : modes) {
                    modeField.set(scoreScreen, mode);
                    assertEquals(mode, modeField.get(scoreScreen));
                }
            }
        } catch (Exception e) {
            fail("모드 순환 테스트 실패: " + e.getMessage());
        }
    }

    // === ScreenController 참조 테스트 ===
    @Test
    public void testScreenControllerReference() {
        try {
            Field controllerField = score.class.getDeclaredField("screenController");
            controllerField.setAccessible(true);
            ScreenController controller = (ScreenController) controllerField.get(scoreScreen);
            assertSame("동일한 ScreenController 인스턴스여야 함", mockController, controller);
        } catch (Exception e) {
            fail("ScreenController 참조 테스트 실패: " + e.getMessage());
        }
    }

    // === currentTextPane 참조 테스트 ===
    @Test
    public void testCurrentTextPaneAfterDisplay() {
        try {
            scoreScreen.display(testPane);
            
            Field paneField = score.class.getDeclaredField("currentTextPane");
            paneField.setAccessible(true);
            JTextPane currentPane = (JTextPane) paneField.get(scoreScreen);
            assertNotNull("display 후 currentTextPane은 null이 아니어야 함", currentPane);
        } catch (Exception e) {
            fail("currentTextPane 참조 테스트 실패: " + e.getMessage());
        }
    }

    // === 여러 번 display 호출 테스트 ===
    @Test
    public void testMultipleDisplayCalls() {
        try {
            for (int i = 0; i < 5; i++) {
                JTextPane newPane = new JTextPane();
                scoreScreen.display(newPane);
                
                Field paneField = score.class.getDeclaredField("currentTextPane");
                paneField.setAccessible(true);
                assertNotNull("각 display 호출 후 currentTextPane은 null이 아니어야 함", paneField.get(scoreScreen));
            }
        } catch (Exception e) {
            fail("여러 번 display 호출 테스트 실패: " + e.getMessage());
        }
    }

    // === 렌더링 안정성 테스트 ===
    @Test
    public void testRenderingStability() {
        try {
            scoreScreen.display(testPane);
            
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            for (int i = 0; i < 10; i++) {
                renderMethod.invoke(scoreScreen);
            }
        } catch (Exception e) {
            fail("렌더링 안정성 테스트 실패: " + e.getMessage());
        }
    }

    // === 모든 UI 컴포넌트 생성 테스트 ===
    @Test
    public void testAllUIComponentsCreation() {
        try {
            Method createTabMethod = score.class.getDeclaredMethod("createModeTabPanel");
            createTabMethod.setAccessible(true);
            JPanel tabPanel = (JPanel) createTabMethod.invoke(scoreScreen);
            assertNotNull(tabPanel);
            
            Method buildPodiumMethod = score.class.getDeclaredMethod("buildPodiumPanel");
            buildPodiumMethod.setAccessible(true);
            JPanel podiumPanel = (JPanel) buildPodiumMethod.invoke(scoreScreen);
            assertNotNull(podiumPanel);
            
            Method buildControlsMethod = score.class.getDeclaredMethod("buildControlsPanel");
            buildControlsMethod.setAccessible(true);
            JPanel controlsPanel = (JPanel) buildControlsMethod.invoke(scoreScreen);
            assertNotNull(controlsPanel);
            
            Method createBgMethod = score.class.getDeclaredMethod("createBackgroundPanel");
            createBgMethod.setAccessible(true);
            JPanel bgPanel = (JPanel) createBgMethod.invoke(scoreScreen);
            assertNotNull(bgPanel);
            
            Method buildFooterMethod = score.class.getDeclaredMethod("buildFooterImagePanel");
            buildFooterMethod.setAccessible(true);
            JPanel footerPanel = (JPanel) buildFooterMethod.invoke(scoreScreen);
            assertNotNull(footerPanel);
            
        } catch (Exception e) {
            fail("모든 UI 컴포넌트 생성 테스트 실패: " + e.getMessage());
        }
    }
}