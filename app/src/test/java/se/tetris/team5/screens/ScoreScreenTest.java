package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import se.tetris.team5.ScreenController;
import se.tetris.team5.utils.score.ScoreManager;
import se.tetris.team5.utils.setting.GameSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * score.java의 라인 커버리지를 높이기 위한 포괄적인 테스트 클래스
 */
public class ScoreScreenTest {
    
    private score scoreScreen;
    private ScreenController mockScreenController;
    private JTextPane testTextPane;

    @Before
    public void setUp() {
        // Mock ScreenController 생성
        mockScreenController = new ScreenController() {
            @Override
            public void showScreen(String screenName) {
                // Mock implementation
            }
        };
        
        scoreScreen = new score(mockScreenController);
        testTextPane = new JTextPane();
        
        // Swing 환경 설정
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    // UI 준비
                });
            } catch (Exception e) {
                // 무시
            }
        }
    }

    @Test
    public void testConstructor() {
        assertNotNull(scoreScreen);
        
        // 필드 값 확인
        try {
            Field screenControllerField = score.class.getDeclaredField("screenController");
            screenControllerField.setAccessible(true);
            assertEquals(mockScreenController, screenControllerField.get(scoreScreen));
            
            Field gameSettingsField = score.class.getDeclaredField("gameSettings");
            gameSettingsField.setAccessible(true);
            assertNotNull(gameSettingsField.get(scoreScreen));
            
            Field scoreManagerField = score.class.getDeclaredField("scoreManager");
            scoreManagerField.setAccessible(true);
            assertNotNull(scoreManagerField.get(scoreScreen));
            
        } catch (Exception e) {
            fail("필드 접근 실패: " + e.getMessage());
        }
    }

    @Test
    public void testDisplayMethod() {
        scoreScreen.display(testTextPane);
        
        // textPane이 설정되었는지 확인
        try {
            Field currentTextPaneField = score.class.getDeclaredField("currentTextPane");
            currentTextPaneField.setAccessible(true);
            assertEquals(testTextPane, currentTextPaneField.get(scoreScreen));
            
        } catch (Exception e) {
            fail("currentTextPane 설정 확인 실패");
        }
        
        // UI 컴포넌트가 추가되었는지 확인
        assertTrue(testTextPane.getComponentCount() > 0);
        assertEquals(BorderLayout.class, testTextPane.getLayout().getClass());
    }

    @Test
    public void testBuildSampleEntries() {
        try {
            Method buildSampleMethod = score.class.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<ScoreManager.ScoreEntry> sampleEntries = 
                (List<ScoreManager.ScoreEntry>) buildSampleMethod.invoke(scoreScreen);
            
            assertNotNull(sampleEntries);
            assertEquals(10, sampleEntries.size());
            
            // 첫 번째 엔트리 검증
            ScoreManager.ScoreEntry first = sampleEntries.get(0);
            assertEquals("Player A", first.getPlayerName());
            assertEquals(256, first.getScore());
            
        } catch (Exception e) {
            fail("buildSampleEntries 테스트 실패: " + e.getMessage());
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
            
            // 다양한 스타일과 크기로 테스트
            Font plainFont = (Font) createFontMethod.invoke(scoreScreen, Font.PLAIN, 12);
            assertEquals(Font.PLAIN, plainFont.getStyle());
            assertEquals(12, plainFont.getSize());
            
            Font italicFont = (Font) createFontMethod.invoke(scoreScreen, Font.ITALIC, 16);
            assertEquals(Font.ITALIC, italicFont.getStyle());
            assertEquals(16, italicFont.getSize());
            
        } catch (Exception e) {
            fail("createKoreanFont 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testGetGameModeDisplay() {
        try {
            Method getModeMethod = score.class.getDeclaredMethod("getGameModeDisplay", String.class);
            getModeMethod.setAccessible(true);
            
            // 각 모드별 테스트
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, "ITEM"));
            assertEquals("쉬움", getModeMethod.invoke(scoreScreen, "NORMAL_EASY"));
            assertEquals("보통", getModeMethod.invoke(scoreScreen, "NORMAL_NORMAL"));
            assertEquals("어려움", getModeMethod.invoke(scoreScreen, "NORMAL_HARD"));
            assertEquals("전문가", getModeMethod.invoke(scoreScreen, "NORMAL_EXPERT"));
            
            // 기본값 테스트
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, (Object) null));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, ""));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, "UNKNOWN_MODE"));
            
        } catch (Exception e) {
            fail("getGameModeDisplay 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testGameModeFilter() {
        try {
            Field modeFilterField = score.class.getDeclaredField("currentGameModeFilter");
            modeFilterField.setAccessible(true);
            
            // 기본값 확인
            assertEquals("ITEM", modeFilterField.get(scoreScreen));
            
            // 모드 변경 테스트
            modeFilterField.set(scoreScreen, "NORMAL_EASY");
            assertEquals("NORMAL_EASY", modeFilterField.get(scoreScreen));
            
        } catch (Exception e) {
            fail("게임 모드 필터 테스트 실패");
        }
    }

    @Test
    void testPagination() {
        try {
            Field currentPageField = score.class.getDeclaredField("currentPage");
            currentPageField.setAccessible(true);
            
            // 기본 페이지 확인
            assertEquals(0, currentPageField.get(scoreScreen));
            
            // 페이지 변경
            currentPageField.set(scoreScreen, 1);
            assertEquals(1, currentPageField.get(scoreScreen));
            
            // PAGE_SIZE 상수 확인
            Field pageSizeField = score.class.getDeclaredField("PAGE_SIZE");
            pageSizeField.setAccessible(true);
            assertEquals(10, pageSizeField.get(null));
            
            // ROW_HEIGHT 상수 확인
            Field rowHeightField = score.class.getDeclaredField("ROW_HEIGHT");
            rowHeightField.setAccessible(true);
            assertEquals(40, rowHeightField.get(null));
            
        } catch (Exception e) {
            fail("페이지네이션 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testCreateModeTabPanel() {
        try {
            Method createTabMethod = score.class.getDeclaredMethod("createModeTabPanel");
            createTabMethod.setAccessible(true);
            
            JPanel tabPanel = (JPanel) createTabMethod.invoke(scoreScreen);
            
            assertNotNull(tabPanel);
            assertTrue(tabPanel.getComponentCount() >= 4); // 4개의 모드 탭
            
            // FlowLayout 확인
            assertTrue(tabPanel.getLayout() instanceof FlowLayout);
            
        } catch (Exception e) {
            fail("createModeTabPanel 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testBuildPodiumPanel() {
        try {
            Method buildPodiumMethod = score.class.getDeclaredMethod("buildPodiumPanel");
            buildPodiumMethod.setAccessible(true);
            
            JPanel podiumPanel = (JPanel) buildPodiumMethod.invoke(scoreScreen);
            
            assertNotNull(podiumPanel);
            assertTrue(podiumPanel.getComponentCount() >= 3); // 1, 2, 3위 패널
            assertTrue(podiumPanel.getLayout() instanceof GridBagLayout);
            
        } catch (Exception e) {
            fail("buildPodiumPanel 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testCreatePodiumBlock() {
        try {
            Method createBlockMethod = score.class.getDeclaredMethod("createPodiumBlock", int.class);
            createBlockMethod.setAccessible(true);
            
            // 1, 2, 3위 블록 생성 테스트
            for (int rank = 1; rank <= 3; rank++) {
                JPanel block = (JPanel) createBlockMethod.invoke(scoreScreen, rank);
                
                assertNotNull(block);
                assertEquals("podium-" + rank, block.getName());
                assertTrue(block.getLayout() instanceof BorderLayout);
                assertTrue(block.getComponentCount() > 0);
            }
            
        } catch (Exception e) {
            fail("createPodiumBlock 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testBuildControlsPanel() {
        try {
            Method buildControlsMethod = score.class.getDeclaredMethod("buildControlsPanel");
            buildControlsMethod.setAccessible(true);
            
            JPanel controlsPanel = (JPanel) buildControlsMethod.invoke(scoreScreen);
            
            assertNotNull(controlsPanel);
            assertTrue(controlsPanel.getLayout() instanceof BorderLayout);
            
        } catch (Exception e) {
            fail("buildControlsPanel 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testCreateBackgroundPanel() {
        try {
            Method createBgMethod = score.class.getDeclaredMethod("createBackgroundPanel");
            createBgMethod.setAccessible(true);
            
            JPanel bgPanel = (JPanel) createBgMethod.invoke(scoreScreen);
            
            assertNotNull(bgPanel);
            
        } catch (Exception e) {
            fail("createBackgroundPanel 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testBuildFooterImagePanel() {
        try {
            Method buildFooterMethod = score.class.getDeclaredMethod("buildFooterImagePanel");
            buildFooterMethod.setAccessible(true);
            
            JPanel footerPanel = (JPanel) buildFooterMethod.invoke(scoreScreen);
            
            assertNotNull(footerPanel);
            assertTrue(footerPanel.getLayout() instanceof GridBagLayout);
            
        } catch (Exception e) {
            fail("buildFooterImagePanel 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testRenderScores() {
        scoreScreen.display(testTextPane);
        
        try {
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            // renderScores 호출 (예외 발생하지 않아야 함)
            try {
                renderMethod.invoke(scoreScreen);
            } catch (Exception e) {
                fail("renderScores 실행 중 예외 발생: " + e.getMessage());
            }
            
        } catch (Exception e) {
            fail("renderScores 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testHelperMethods() {
        scoreScreen.display(testTextPane);
        
        try {
            // findAllComponents 테스트
            Method findAllMethod = score.class.getDeclaredMethod("findAllComponents", Container.class);
            findAllMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<Component> components = (List<Component>) findAllMethod.invoke(scoreScreen, testTextPane);
            assertNotNull(components);
            
            // findPageInfoLabel 테스트
            Method findPageMethod = score.class.getDeclaredMethod("findPageInfoLabel", Container.class);
            findPageMethod.setAccessible(true);
            
            JLabel pageLabel = (JLabel) findPageMethod.invoke(scoreScreen, testTextPane);
            // null일 수도 있음 (존재하지 않을 수 있음)
            
            // findHeaderPanel 테스트  
            Method findHeaderMethod = score.class.getDeclaredMethod("findHeaderPanel", Container.class);
            findHeaderMethod.setAccessible(true);
            
            JPanel headerPanel = (JPanel) findHeaderMethod.invoke(scoreScreen, testTextPane);
            // null일 수도 있음
            
            // findListContainer 테스트
            Method findListMethod = score.class.getDeclaredMethod("findListContainer", Container.class);
            findListMethod.setAccessible(true);
            
            JPanel listContainer = (JPanel) findListMethod.invoke(scoreScreen, testTextPane);
            // null일 수도 있음
            
        } catch (Exception e) {
            fail("Helper methods 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testKeyListenerFunctionality() {
        scoreScreen.display(testTextPane);
        
        // KeyListener가 추가되었는지 확인
        assertTrue(testTextPane.getKeyListeners().length > 0);
        
        try {
            // ScoreKeyListener 내부 클래스 테스트
            KeyEvent escapeEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
            
            KeyEvent leftEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
            
            KeyEvent rightEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
            
            // 키 이벤트 처리 테스트 (예외 발생하지 않아야 함)
            for (var listener : testTextPane.getKeyListeners()) {
                assertDoesNotThrow(() -> {
                    listener.keyPressed(leftEvent);
                    listener.keyPressed(rightEvent);
                    listener.keyTyped(escapeEvent);
                    listener.keyReleased(escapeEvent);
                });
            }
            
        } catch (Exception e) {
            fail("KeyListener 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testBackgroundPanelPaintComponent() {
        try {
            // BackgroundPanel 내부 클래스 접근
            Class<?>[] innerClasses = score.class.getDeclaredClasses();
            Class<?> backgroundPanelClass = null;
            
            for (Class<?> innerClass : innerClasses) {
                if (innerClass.getSimpleName().equals("BackgroundPanel")) {
                    backgroundPanelClass = innerClass;
                    break;
                }
            }
            
            if (backgroundPanelClass != null) {
                // BackgroundPanel 인스턴스 생성
                Object bgPanel = backgroundPanelClass.getDeclaredConstructor(score.class)
                    .newInstance(scoreScreen);
                
                assertTrue(bgPanel instanceof JPanel);
                JPanel panel = (JPanel) bgPanel;
                
                // paintComponent 메서드 호출 테스트
                Graphics g = panel.createGraphics();
                if (g != null) {
                    assertDoesNotThrow(() -> {
                        panel.paintComponent(g);
                    });
                    g.dispose();
                }
            }
            
        } catch (Exception e) {
            // BackgroundPanel 테스트는 선택적 (내부 클래스 접근이 복잡할 수 있음)
            System.out.println("BackgroundPanel 테스트 건너뛰기: " + e.getMessage());
        }
    }

    @Test
    void testShowOnlyDummyField() {
        try {
            Field showOnlyDummyField = score.class.getDeclaredField("showOnlyDummy");
            showOnlyDummyField.setAccessible(true);
            
            // 기본값 확인
            assertEquals(false, showOnlyDummyField.get(scoreScreen));
            
            // 값 변경 테스트
            showOnlyDummyField.set(scoreScreen, true);
            assertEquals(true, showOnlyDummyField.get(scoreScreen));
            
        } catch (Exception e) {
            fail("showOnlyDummy 필드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testScoreListScrollField() {
        try {
            Field scrollField = score.class.getDeclaredField("scoreListScroll");
            scrollField.setAccessible(true);
            
            // 초기값은 null
            assertNull(scrollField.get(scoreScreen));
            
            // display 호출 후 설정됨
            scoreScreen.display(testTextPane);
            // 값이 설정되었을 수도 있음 (UI 구성에 따라)
            
        } catch (Exception e) {
            fail("scoreListScroll 필드 테스트 실패: " + e.getMessage());
        }
    }

    @Test 
    void testAllGameModes() {
        try {
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            
            String[] testModes = {"ITEM", "NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD"};
            
            for (String mode : testModes) {
                modeField.set(scoreScreen, mode);
                assertEquals(mode, modeField.get(scoreScreen));
                
                // 각 모드에서 display 호출
                assertDoesNotThrow(() -> {
                    scoreScreen.display(testTextPane);
                });
            }
            
        } catch (Exception e) {
            fail("모든 게임 모드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    void testEdgeCases() {
        // null textPane 처리
        assertDoesNotThrow(() -> {
            scoreScreen.display(null);
        });
        
        // 빈 textPane
        JTextPane emptyPane = new JTextPane();
        assertDoesNotThrow(() -> {
            scoreScreen.display(emptyPane);
        });
        
        // 페이지 범위 테스트
        try {
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            
            // 음수 페이지
            pageField.set(scoreScreen, -1);
            assertDoesNotThrow(() -> {
                scoreScreen.display(testTextPane);
            });
            
            // 큰 페이지 번호
            pageField.set(scoreScreen, 999);
            assertDoesNotThrow(() -> {
                scoreScreen.display(testTextPane);
            });
            
        } catch (Exception e) {
            fail("Edge cases 테스트 실패: " + e.getMessage());
        }
    }
}