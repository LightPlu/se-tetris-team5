package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import se.tetris.team5.ScreenController;
import se.tetris.team5.utils.score.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * score.java 클래스의 모든 기능을 테스트하여 높은 커버리지를 달성하는 테스트 클래스
 */
public class ScoreScreenFullCoverageTest {
    
    private score scoreScreen;
    private ScreenController mockScreenController;
    private JTextPane testTextPane;
    private ScoreManager scoreManager;

    @Before
    public void setUp() {
        // Mock ScreenController 생성
        mockScreenController = new ScreenController() {
            @Override
            public void showScreen(String screenName) {
                System.out.println("Navigating to: " + screenName);
            }
        };
        
        scoreScreen = new score(mockScreenController);
        testTextPane = new JTextPane();
        scoreManager = ScoreManager.getInstance();
        
        // 테스트 데이터 준비
        prepareTestData();
    }

    private void prepareTestData() {
        // ScoreManager에 테스트 데이터 추가
        for (int i = 0; i < 25; i++) {
            String gameMode = (i % 4 == 0) ? "ITEM" : 
                             (i % 4 == 1) ? "NORMAL_EASY" : 
                             (i % 4 == 2) ? "NORMAL_NORMAL" : "NORMAL_HARD";
            
            scoreManager.addScore("TestPlayer" + i, 1000 - i * 10, i + 1, i, 
                                i * 1000L, gameMode);
        }
    }

    @Test
    public void testCompleteUIBuildingFlow() {
        try {
            scoreScreen.display(testTextPane);
            
            // 모든 필드가 올바르게 설정되었는지 확인
            Field currentTextPaneField = score.class.getDeclaredField("currentTextPane");
            currentTextPaneField.setAccessible(true);
            assertNotNull(currentTextPaneField.get(scoreScreen));
            
            Field scoreListScrollField = score.class.getDeclaredField("scoreListScroll");
            scoreListScrollField.setAccessible(true);
            // scoreListScroll은 buildUI에서 설정됨
            
            // renderScores를 여러 번 호출하여 다양한 경로 테스트
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            renderMethod.invoke(scoreScreen);
            
        } catch (Exception e) {
            fail("Complete UI building flow test failed: " + e.getMessage());
        }
    }

    @Test 
    public void testAllModeTabFunctionality() {
        try {
            scoreScreen.display(testTextPane);
            
            Method createModeTabMethod = score.class.getDeclaredMethod("createModeTabPanel");
            createModeTabMethod.setAccessible(true);
            
            JPanel modeTabPanel = (JPanel) createModeTabMethod.invoke(scoreScreen);
            assertNotNull(modeTabPanel);
            
            // 모든 모드 탭 버튼 클릭 시뮬레이션
            String[] testModes = {"ITEM", "NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD"};
            
            for (String mode : testModes) {
                // 모드 필드 직접 설정
                Field modeField = score.class.getDeclaredField("currentGameModeFilter");
                modeField.setAccessible(true);
                modeField.set(scoreScreen, mode);
                
                Field pageField = score.class.getDeclaredField("currentPage");
                pageField.setAccessible(true);
                pageField.set(scoreScreen, 0);
                
                // buildUI와 renderScores 호출
                Method buildUIMethod = score.class.getDeclaredMethod("buildUI");
                buildUIMethod.setAccessible(true);
                buildUIMethod.invoke(scoreScreen);
                
                Method renderMethod = score.class.getDeclaredMethod("renderScores");
                renderMethod.setAccessible(true);
                renderMethod.invoke(scoreScreen);
            }
            
        } catch (Exception e) {
            fail("Mode tab functionality test failed: " + e.getMessage());
        }
    }

    @Test
    public void testPaginationLogicThoroughly() {
        try {
            scoreScreen.display(testTextPane);
            
            Field currentPageField = score.class.getDeclaredField("currentPage");
            currentPageField.setAccessible(true);
            
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            // 페이지 0, 1, 2, 3으로 이동하면서 렌더링
            for (int page = 0; page < 4; page++) {
                currentPageField.set(scoreScreen, page);
                renderMethod.invoke(scoreScreen);
            }
            
            // showOnlyDummy 모드도 테스트
            Field showOnlyDummyField = score.class.getDeclaredField("showOnlyDummy");
            showOnlyDummyField.setAccessible(true);
            showOnlyDummyField.set(scoreScreen, true);
            
            for (int page = 0; page < 2; page++) {
                currentPageField.set(scoreScreen, page);
                renderMethod.invoke(scoreScreen);
            }
            
        } catch (Exception e) {
            fail("Pagination logic test failed: " + e.getMessage());
        }
    }

    @Test
    public void testControlPanelButtons() {
        try {
            scoreScreen.display(testTextPane);
            
            Method buildControlsMethod = score.class.getDeclaredMethod("buildControlsPanel");
            buildControlsMethod.setAccessible(true);
            
            JPanel controlsPanel = (JPanel) buildControlsMethod.invoke(scoreScreen);
            assertNotNull(controlsPanel);
            
            // 컨트롤 패널의 버튼들을 찾아서 클릭 시뮬레이션
            Component[] components = controlsPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    for (Component innerComp : panel.getComponents()) {
                        if (innerComp instanceof JButton) {
                            JButton button = (JButton) innerComp;
                            // 버튼 클릭 이벤트 시뮬레이션
                            for (ActionListener listener : button.getActionListeners()) {
                                ActionEvent event = new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "");
                                listener.actionPerformed(event);
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            fail("Control panel buttons test failed: " + e.getMessage());
        }
    }

    @Test
    public void testPodiumRenderingWithDifferentScenarios() {
        try {
            scoreScreen.display(testTextPane);
            
            // 상위 3명이 있는 경우
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            renderMethod.invoke(scoreScreen);
            
            // 빈 점수 리스트인 경우 (모든 점수 삭제)
            scoreManager.clearAllScores();
            renderMethod.invoke(scoreScreen);
            
            // 1명만 있는 경우
            scoreManager.addScore("OnlyPlayer", 100, 1, 0, 1000L, "ITEM");
            renderMethod.invoke(scoreScreen);
            
            // 2명만 있는 경우
            scoreManager.addScore("SecondPlayer", 90, 1, 0, 2000L, "ITEM");
            renderMethod.invoke(scoreScreen);
            
        } catch (Exception e) {
            fail("Podium rendering test failed: " + e.getMessage());
        }
    }

    @Test
    public void testFooterImagePanelRendering() {
        try {
            Method buildFooterMethod = score.class.getDeclaredMethod("buildFooterImagePanel");
            buildFooterMethod.setAccessible(true);
            
            JPanel footerPanel = (JPanel) buildFooterMethod.invoke(scoreScreen);
            assertNotNull(footerPanel);
            
            // 다양한 크기로 footer 패널 테스트
            footerPanel.setSize(800, 160);
            footerPanel.setSize(400, 160);
            footerPanel.setSize(1200, 160);
            
            // Graphics 객체를 생성해서 paintComponent 테스트
            BufferedImage img = new BufferedImage(800, 160, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            footerPanel.paint(g);
            g.dispose();
            
        } catch (Exception e) {
            fail("Footer image panel test failed: " + e.getMessage());
        }
    }

    @Test
    public void testBackgroundPanelPainting() {
        try {
            Method createBgMethod = score.class.getDeclaredMethod("createBackgroundPanel");
            createBgMethod.setAccessible(true);
            
            JPanel bgPanel = (JPanel) createBgMethod.invoke(scoreScreen);
            assertNotNull(bgPanel);
            
            // 다양한 크기로 배경 패널 페인팅 테스트
            bgPanel.setSize(800, 600);
            BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            bgPanel.paint(g);
            g.dispose();
            
            bgPanel.setSize(1024, 768);
            img = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB);
            g = img.createGraphics();
            bgPanel.paint(g);
            g.dispose();
            
        } catch (Exception e) {
            fail("Background panel painting test failed: " + e.getMessage());
        }
    }

    @Test
    public void testAllKeyListenerEvents() {
        try {
            scoreScreen.display(testTextPane);
            
            if (testTextPane.getKeyListeners().length > 0) {
                var listener = testTextPane.getKeyListeners()[0];
                
                // 모든 주요 키 이벤트 테스트
                int[] keyCodes = {
                    KeyEvent.VK_ESCAPE, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                    KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_ENTER,
                    KeyEvent.VK_SPACE, KeyEvent.VK_A, KeyEvent.VK_Z
                };
                
                for (int keyCode : keyCodes) {
                    KeyEvent pressedEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                        System.currentTimeMillis(), 0, keyCode, (char) keyCode);
                    
                    KeyEvent typedEvent = new KeyEvent(testTextPane, KeyEvent.KEY_TYPED, 
                        System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, (char) keyCode);
                    
                    KeyEvent releasedEvent = new KeyEvent(testTextPane, KeyEvent.KEY_RELEASED, 
                        System.currentTimeMillis(), 0, keyCode, (char) keyCode);
                    
                    listener.keyPressed(pressedEvent);
                    listener.keyTyped(typedEvent);
                    listener.keyReleased(releasedEvent);
                }
                
                // 페이지 변경 키 이벤트 테스트
                Field pageField = score.class.getDeclaredField("currentPage");
                pageField.setAccessible(true);
                
                // 왼쪽 화살표로 페이지 이동
                pageField.set(scoreScreen, 2);
                KeyEvent leftEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                    System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
                listener.keyPressed(leftEvent);
                
                // 오른쪽 화살표로 페이지 이동
                KeyEvent rightEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                    System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
                listener.keyPressed(rightEvent);
            }
            
        } catch (Exception e) {
            fail("Key listener events test failed: " + e.getMessage());
        }
    }

    @Test
    public void testHelperMethodsWithVariousInputs() {
        try {
            scoreScreen.display(testTextPane);
            
            // findAllComponents with different containers
            Method findAllMethod = score.class.getDeclaredMethod("findAllComponents", Container.class);
            findAllMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            List<Component> components1 = (List<Component>) findAllMethod.invoke(scoreScreen, testTextPane);
            assertNotNull(components1);
            
            // Empty container
            JPanel emptyPanel = new JPanel();
            @SuppressWarnings("unchecked")
            List<Component> components2 = (List<Component>) findAllMethod.invoke(scoreScreen, emptyPanel);
            assertNotNull(components2);
            
            // Nested container
            JPanel parentPanel = new JPanel();
            JPanel childPanel = new JPanel();
            childPanel.add(new JLabel("test"));
            parentPanel.add(childPanel);
            @SuppressWarnings("unchecked")
            List<Component> components3 = (List<Component>) findAllMethod.invoke(scoreScreen, parentPanel);
            assertNotNull(components3);
            
            // findPageInfoLabel with different containers
            Method findPageMethod = score.class.getDeclaredMethod("findPageInfoLabel", Container.class);
            findPageMethod.setAccessible(true);
            
            findPageMethod.invoke(scoreScreen, testTextPane);
            findPageMethod.invoke(scoreScreen, emptyPanel);
            
            // Test with label that has correct name
            JPanel panelWithPageLabel = new JPanel();
            JLabel pageLabel = new JLabel("Page 1 / 1");
            pageLabel.setName("page-info");
            panelWithPageLabel.add(pageLabel);
            
            JLabel foundLabel = (JLabel) findPageMethod.invoke(scoreScreen, panelWithPageLabel);
            assertNotNull(foundLabel);
            assertEquals("page-info", foundLabel.getName());
            
            // findHeaderPanel
            Method findHeaderMethod = score.class.getDeclaredMethod("findHeaderPanel", Container.class);
            findHeaderMethod.setAccessible(true);
            
            findHeaderMethod.invoke(scoreScreen, testTextPane);
            
            // Test with header panel
            JPanel panelWithHeader = new JPanel();
            JPanel headerPanel = new JPanel();
            headerPanel.setName("score-header");
            panelWithHeader.add(headerPanel);
            
            JPanel foundHeader = (JPanel) findHeaderMethod.invoke(scoreScreen, panelWithHeader);
            assertNotNull(foundHeader);
            assertEquals("score-header", foundHeader.getName());
            
            // findListContainer
            Method findListMethod = score.class.getDeclaredMethod("findListContainer", Container.class);
            findListMethod.setAccessible(true);
            
            findListMethod.invoke(scoreScreen, testTextPane);
            
            // Test with scroll pane containing list
            JPanel listContainer = new JPanel();
            JScrollPane scrollPane = new JScrollPane(listContainer);
            JPanel containerWithScroll = new JPanel();
            containerWithScroll.add(scrollPane);
            
            JPanel foundList = (JPanel) findListMethod.invoke(scoreScreen, containerWithScroll);
            assertEquals(listContainer, foundList);
            
        } catch (Exception e) {
            fail("Helper methods test failed: " + e.getMessage());
        }
    }

    @Test
    public void testCreatePodiumBlockForAllRanks() {
        try {
            Method createBlockMethod = score.class.getDeclaredMethod("createPodiumBlock", int.class);
            createBlockMethod.setAccessible(true);
            
            // Test ranks 1-10 (even though only 1-3 are typically used)
            for (int rank = 1; rank <= 10; rank++) {
                JPanel block = (JPanel) createBlockMethod.invoke(scoreScreen, rank);
                
                assertNotNull(block);
                assertEquals("podium-" + rank, block.getName());
                assertTrue(block.getComponentCount() >= 3); // name, medal, pedestal
                
                // Verify components have correct names
                boolean hasNameComponent = false;
                boolean hasMedalComponent = false;
                
                for (Component comp : block.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        if (("podium-name-" + rank).equals(label.getName())) {
                            hasNameComponent = true;
                        }
                    }
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        for (Component innerComp : panel.getComponents()) {
                            if (innerComp instanceof JLabel) {
                                JLabel label = (JLabel) innerComp;
                                if (("podium-medal-" + rank).equals(label.getName())) {
                                    hasMedalComponent = true;
                                }
                            }
                        }
                    }
                }
                
                assertTrue("Rank " + rank + " should have name component", hasNameComponent);
                assertTrue("Rank " + rank + " should have medal component", hasMedalComponent);
            }
            
        } catch (Exception e) {
            fail("Create podium block test failed: " + e.getMessage());
        }
    }

    @Test
    public void testFontCreationWithAllParameters() {
        try {
            Method createFontMethod = score.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
            createFontMethod.setAccessible(true);
            
            // Test all font styles
            int[] styles = {Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD | Font.ITALIC};
            int[] sizes = {8, 10, 12, 14, 16, 18, 20, 24, 32, 48};
            
            for (int style : styles) {
                for (int size : sizes) {
                    Font font = (Font) createFontMethod.invoke(scoreScreen, style, size);
                    assertNotNull(font);
                    assertEquals(style, font.getStyle());
                    assertEquals(size, font.getSize());
                    
                    // Test Korean character support
                    assertTrue("Font should support Korean characters", 
                              font.canDisplay('한') || font.getFamily().contains("Dialog"));
                }
            }
            
        } catch (Exception e) {
            fail("Font creation test failed: " + e.getMessage());
        }
    }

    @Test
    public void testRenderScoresWithEmptyAndFullData() {
        try {
            scoreScreen.display(testTextPane);
            
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            // Test with current data
            renderMethod.invoke(scoreScreen);
            
            // Test with cleared data
            scoreManager.clearAllScores();
            renderMethod.invoke(scoreScreen);
            
            // Test with maximum data (more than page size)
            for (int i = 0; i < 50; i++) {
                scoreManager.addScore("Player" + i, 2000 - i, i % 10 + 1, i, 
                                    i * 1000L, "ITEM");
            }
            renderMethod.invoke(scoreScreen);
            
            // Test different pages with full data
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            
            for (int page = 0; page < 5; page++) {
                pageField.set(scoreScreen, page);
                renderMethod.invoke(scoreScreen);
            }
            
        } catch (Exception e) {
            fail("Render scores with different data test failed: " + e.getMessage());
        }
    }

    @Test
    public void testAllGameModeDisplayNames() {
        try {
            Method getModeMethod = score.class.getDeclaredMethod("getGameModeDisplay", String.class);
            getModeMethod.setAccessible(true);
            
            // Test all defined modes
            String[] modes = {"ITEM", "NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD", "NORMAL_EXPERT"};
            String[] expectedNames = {"아이템", "쉬움", "보통", "어려움", "전문가"};
            
            for (int i = 0; i < modes.length; i++) {
                String result = (String) getModeMethod.invoke(scoreScreen, modes[i]);
                assertEquals(expectedNames[i], result);
            }
            
            // Test edge cases
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, (Object) null));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, ""));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, "UNKNOWN"));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, "invalid_mode"));
            
        } catch (Exception e) {
            fail("Game mode display names test failed: " + e.getMessage());
        }
    }

    @Test
    public void testCompleteUIFlow() {
        try {
            // Test complete flow: display -> mode change -> pagination
            scoreScreen.display(testTextPane);
            
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            String[] modes = {"ITEM", "NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD"};
            
            for (String mode : modes) {
                modeField.set(scoreScreen, mode);
                
                for (int page = 0; page < 3; page++) {
                    pageField.set(scoreScreen, page);
                    renderMethod.invoke(scoreScreen);
                }
            }
            
            // Test both showOnlyDummy modes
            Field dummyField = score.class.getDeclaredField("showOnlyDummy");
            dummyField.setAccessible(true);
            
            dummyField.set(scoreScreen, false);
            renderMethod.invoke(scoreScreen);
            
            dummyField.set(scoreScreen, true);
            renderMethod.invoke(scoreScreen);
            
        } catch (Exception e) {
            fail("Complete UI flow test failed: " + e.getMessage());
        }
    }
}