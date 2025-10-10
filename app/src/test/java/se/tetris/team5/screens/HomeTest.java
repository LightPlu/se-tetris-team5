package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JButton;
import javax.swing.JLabel;

import se.tetris.team5.ScreenController;
import se.tetris.team5.screens.home;
import se.tetris.team5.components.home.Title;

/**
 * home 화면 컴포넌트 테스트
 */
public class HomeTest {

    private home homeScreen;
    private ScreenController mockScreenController;

    @Before
    public void setUp() {
        // Mock ScreenController 생성
        mockScreenController = new ScreenController() {
            @Override
            public void showScreen(String screenName) {
                // Mock implementation - do nothing
            }
        };
        
        homeScreen = new home(mockScreenController);
    }

    @After
    public void tearDown() {
        if (homeScreen != null) {
            homeScreen.setVisible(false);
        }
        homeScreen = null;
        mockScreenController = null;
    }

    // 생성자 테스트
    @Test
    public void testConstructorInitialization() {
        // Given & When
        home screen = new home(mockScreenController);

        // Then
        assertNotNull("Home screen should not be null", screen);
    }

    @Test
    public void testConstructorWithNullScreenController() {
        // Given & When & Then
        try {
            home screen = new home(null);
            assertNotNull("Home screen should handle null ScreenController", screen);
        } catch (Exception e) {
            fail("Constructor should handle null ScreenController gracefully: " + e.getMessage());
        }
    }

    // 메뉴 선택 테스트
    @Test
    public void testInitialMenuSelection() throws Exception {
        // Given
        Field selectedMenuField = home.class.getDeclaredField("selectedMenu");
        selectedMenuField.setAccessible(true);

        // When
        int selectedMenu = (Integer) selectedMenuField.get(homeScreen);

        // Then
        assertEquals("Initial selected menu should be 0", 0, selectedMenu);
    }

    @Test
    public void testMenuOptions() throws Exception {
        // Given
        Field menuOptionsField = home.class.getDeclaredField("menuOptions");
        menuOptionsField.setAccessible(true);

        // When
        String[] menuOptions = (String[]) menuOptionsField.get(homeScreen);

        // Then
        assertNotNull("Menu options should not be null", menuOptions);
        assertEquals("Should have 4 menu options", 4, menuOptions.length);
        assertEquals("First option should be 게임 시작", "게임 시작", menuOptions[0]);
        assertEquals("Second option should be 스코어 보기", "스코어 보기", menuOptions[1]);
        assertEquals("Third option should be 설정", "설정", menuOptions[2]);
        assertEquals("Fourth option should be 종료", "종료", menuOptions[3]);
    }

    @Test
    public void testMenuButtonsInitialization() throws Exception {
        // Given
        Field menuButtonsField = home.class.getDeclaredField("menuButtons");
        menuButtonsField.setAccessible(true);

        // When
        JButton[] menuButtons = (JButton[]) menuButtonsField.get(homeScreen);

        // Then
        assertNotNull("Menu buttons should not be null", menuButtons);
        assertEquals("Should have 4 menu buttons", 4, menuButtons.length);
        
        for (int i = 0; i < menuButtons.length; i++) {
            assertNotNull("Menu button " + i + " should not be null", menuButtons[i]);
            assertTrue("Menu button " + i + " should be enabled", menuButtons[i].isEnabled());
        }
    }

    // 키보드 이벤트 테스트
    @Test
    public void testKeyPressedUpArrow() throws Exception {
        // Given
        Field selectedMenuField = home.class.getDeclaredField("selectedMenu");
        selectedMenuField.setAccessible(true);
        selectedMenuField.set(homeScreen, 1); // Set to second option

        KeyEvent upKeyEvent = new KeyEvent(homeScreen, KeyEvent.KEY_PRESSED, 
                                          System.currentTimeMillis(), 0, 
                                          KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);

        // When
        homeScreen.keyPressed(upKeyEvent);

        // Then
        int selectedMenu = (Integer) selectedMenuField.get(homeScreen);
        assertEquals("Up arrow should move to previous menu", 0, selectedMenu);
    }

    @Test
    public void testKeyPressedDownArrow() throws Exception {
        // Given
        Field selectedMenuField = home.class.getDeclaredField("selectedMenu");
        selectedMenuField.setAccessible(true);
        selectedMenuField.set(homeScreen, 0); // Set to first option

        KeyEvent downKeyEvent = new KeyEvent(homeScreen, KeyEvent.KEY_PRESSED, 
                                           System.currentTimeMillis(), 0, 
                                           KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);

        // When
        homeScreen.keyPressed(downKeyEvent);

        // Then
        int selectedMenu = (Integer) selectedMenuField.get(homeScreen);
        assertEquals("Down arrow should move to next menu", 1, selectedMenu);
    }

    @Test
    public void testKeyPressedUpArrowWrapAround() throws Exception {
        // Given
        Field selectedMenuField = home.class.getDeclaredField("selectedMenu");
        selectedMenuField.setAccessible(true);
        selectedMenuField.set(homeScreen, 0); // Set to first option

        KeyEvent upKeyEvent = new KeyEvent(homeScreen, KeyEvent.KEY_PRESSED, 
                                          System.currentTimeMillis(), 0, 
                                          KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);

        // When
        homeScreen.keyPressed(upKeyEvent);

        // Then
        int selectedMenu = (Integer) selectedMenuField.get(homeScreen);
        assertEquals("Up arrow should wrap around to last menu", 3, selectedMenu);
    }

    @Test
    public void testKeyPressedDownArrowWrapAround() throws Exception {
        // Given
        Field selectedMenuField = home.class.getDeclaredField("selectedMenu");
        selectedMenuField.setAccessible(true);
        selectedMenuField.set(homeScreen, 3); // Set to last option

        KeyEvent downKeyEvent = new KeyEvent(homeScreen, KeyEvent.KEY_PRESSED, 
                                           System.currentTimeMillis(), 0, 
                                           KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);

        // When
        homeScreen.keyPressed(downKeyEvent);

        // Then
        int selectedMenu = (Integer) selectedMenuField.get(homeScreen);
        assertEquals("Down arrow should wrap around to first menu", 0, selectedMenu);
    }

    @Test
    public void testKeyPressedEscapeKey() {
        // Given
        KeyEvent escapeKeyEvent = new KeyEvent(homeScreen, KeyEvent.KEY_PRESSED, 
                                              System.currentTimeMillis(), 0, 
                                              KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);

        // When & Then
        try {
            homeScreen.keyPressed(escapeKeyEvent);
            // Should not throw exception
        } catch (Exception e) {
            fail("Escape key should be handled gracefully: " + e.getMessage());
        }
    }

    // 윈도우 크기 테스트
    @Test
    public void testWindowSizeInitialization() throws Exception {
        // Given
        Field windowWidthField = home.class.getDeclaredField("windowWidth");
        Field windowHeightField = home.class.getDeclaredField("windowHeight");
        windowWidthField.setAccessible(true);
        windowHeightField.setAccessible(true);

        // When
        int windowWidth = (Integer) windowWidthField.get(homeScreen);
        int windowHeight = (Integer) windowHeightField.get(homeScreen);

        // Then
        assertTrue("Window width should be positive", windowWidth > 0);
        assertTrue("Window height should be positive", windowHeight > 0);
    }

    @Test
    public void testCurrentWindowSize() throws Exception {
        // Given
        Field currentWindowSizeField = home.class.getDeclaredField("currentWindowSize");
        currentWindowSizeField.setAccessible(true);

        // When
        Title.WindowSize currentWindowSize = (Title.WindowSize) currentWindowSizeField.get(homeScreen);

        // Then
        assertNotNull("Current window size should not be null", currentWindowSize);
        assertTrue("Current window size should be valid", 
                   currentWindowSize == Title.WindowSize.SMALL ||
                   currentWindowSize == Title.WindowSize.MEDIUM ||
                   currentWindowSize == Title.WindowSize.LARGE ||
                   currentWindowSize == Title.WindowSize.XLARGE);
    }

    // 타이틀 라벨 테스트
    @Test
    public void testTitleLabelInitialization() throws Exception {
        // Given
        Field titleLabelField = home.class.getDeclaredField("titleLabel");
        titleLabelField.setAccessible(true);

        // When
        JLabel titleLabel = (JLabel) titleLabelField.get(homeScreen);

        // Then
        assertNotNull("Title label should not be null", titleLabel);
        assertNotNull("Title label text should not be null", titleLabel.getText());
        assertFalse("Title label should not be empty", titleLabel.getText().trim().isEmpty());
        assertTrue("Title label should contain HTML", titleLabel.getText().contains("<html>"));
    }

    // 배경 관련 테스트
    @Test
    public void testBackgroundInitialization() throws Exception {
        // Given
        Field particlesField = home.class.getDeclaredField("particles");
        Field animationTimerField = home.class.getDeclaredField("animationTimer");
        Field randomField = home.class.getDeclaredField("random");
        
        particlesField.setAccessible(true);
        animationTimerField.setAccessible(true);
        randomField.setAccessible(true);

        // When
        Object particles = particlesField.get(homeScreen);
        Object animationTimer = animationTimerField.get(homeScreen);
        Object random = randomField.get(homeScreen);

        // Then
        assertNotNull("Particles should be initialized", particles);
        assertNotNull("Animation timer should be initialized", animationTimer);
        assertNotNull("Random should be initialized", random);
    }

    // 메서드 테스트
    @Test
    public void testRefreshDisplayMethod() {
        // Given & When & Then
        try {
            homeScreen.refreshDisplay();
            // Should not throw exception
        } catch (Exception e) {
            fail("refreshDisplay should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testGetButtonWidthMethod() throws Exception {
        // Given
        Method getButtonWidthMethod = home.class.getDeclaredMethod("getButtonWidth");
        getButtonWidthMethod.setAccessible(true);

        // When
        int buttonWidth = (Integer) getButtonWidthMethod.invoke(homeScreen);

        // Then
        assertTrue("Button width should be positive", buttonWidth > 0);
        assertTrue("Button width should be reasonable", buttonWidth >= 100 && buttonWidth <= 400);
    }

    @Test
    public void testGetButtonHeightMethod() throws Exception {
        // Given
        Method getButtonHeightMethod = home.class.getDeclaredMethod("getButtonHeight");
        getButtonHeightMethod.setAccessible(true);

        // When
        int buttonHeight = (Integer) getButtonHeightMethod.invoke(homeScreen);

        // Then
        assertTrue("Button height should be positive", buttonHeight > 0);
        assertTrue("Button height should be reasonable", buttonHeight >= 20 && buttonHeight <= 100);
    }

    // 패널 투명도 테스트
    @Test
    public void testPanelOpacity() {
        // Given & When & Then
        assertFalse("Main panel should be transparent", homeScreen.isOpaque());
    }

    // 엣지 케이스 테스트
    @Test
    public void testMultipleKeyPresses() throws Exception {
        // Given
        Field selectedMenuField = home.class.getDeclaredField("selectedMenu");
        selectedMenuField.setAccessible(true);

        KeyEvent downKeyEvent = new KeyEvent(homeScreen, KeyEvent.KEY_PRESSED, 
                                           System.currentTimeMillis(), 0, 
                                           KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);

        // When - Press down multiple times
        for (int i = 0; i < 10; i++) {
            homeScreen.keyPressed(downKeyEvent);
        }

        // Then
        int selectedMenu = (Integer) selectedMenuField.get(homeScreen);
        assertEquals("Multiple key presses should wrap around correctly", 2, selectedMenu); // 0 + 10 % 4 = 2
    }

    @Test
    public void testKeyTypedAndKeyReleased() {
        // Given
        KeyEvent keyTypedEvent = new KeyEvent(homeScreen, KeyEvent.KEY_TYPED, 
                                            System.currentTimeMillis(), 0, 
                                            KeyEvent.VK_UNDEFINED, 'a');
        KeyEvent keyReleasedEvent = new KeyEvent(homeScreen, KeyEvent.KEY_RELEASED, 
                                                System.currentTimeMillis(), 0, 
                                                KeyEvent.VK_A, KeyEvent.CHAR_UNDEFINED);

        // When & Then
        try {
            homeScreen.keyTyped(keyTypedEvent);
            homeScreen.keyReleased(keyReleasedEvent);
            // Should not throw exception
        } catch (Exception e) {
            fail("keyTyped and keyReleased should be handled gracefully: " + e.getMessage());
        }
    }

    @Test
    public void testDisplayMethod() {
        // Given & When & Then
        try {
            homeScreen.display(null);
            // Should not throw exception
        } catch (Exception e) {
            fail("display method should handle null parameter gracefully: " + e.getMessage());
        }
    }

    @Test
    public void testScreenVisible() {
        // Given & When & Then
        assertNotNull("Home screen should not be null", homeScreen);
    }

    @Test
    public void testFocusable() {
        // Given & When & Then
        assertTrue("Home screen should be focusable", homeScreen.isFocusable());
    }

    @Test
    public void testMenuDescriptionsInitialization() throws Exception {
        // Given
        Field menuDescriptionsField = home.class.getDeclaredField("menuDescriptions");
        menuDescriptionsField.setAccessible(true);

        // When
        String[] menuDescriptions = (String[]) menuDescriptionsField.get(homeScreen);

        // Then
        assertNotNull("Menu descriptions should not be null", menuDescriptions);
        assertEquals("Should have 4 menu descriptions", 4, menuDescriptions.length);
        
        for (int i = 0; i < menuDescriptions.length; i++) {
            assertNotNull("Menu description " + i + " should not be null", menuDescriptions[i]);
            assertFalse("Menu description " + i + " should not be empty", 
                       menuDescriptions[i].trim().isEmpty());
        }
    }

    // 버튼 관련 추가 테스트
    @Test
    public void testCreateMenuButtonMethod() throws Exception {
        // Given
        Method createMenuButtonMethod = home.class.getDeclaredMethod("createMenuButton", int.class);
        createMenuButtonMethod.setAccessible(true);

        // When
        JButton button = (JButton) createMenuButtonMethod.invoke(homeScreen, 0);

        // Then
        assertNotNull("Created button should not be null", button);
        assertTrue("Button should be enabled", button.isEnabled());
        assertNotNull("Button text should not be null", button.getText());
    }

    @Test
    public void testUpdateMenuSelectionMethod() throws Exception {
        // Given
        Method updateMenuSelectionMethod = home.class.getDeclaredMethod("updateMenuSelection");
        updateMenuSelectionMethod.setAccessible(true);
        
        Field selectedMenuField = home.class.getDeclaredField("selectedMenu");
        selectedMenuField.setAccessible(true);
        selectedMenuField.set(homeScreen, 1);

        // When & Then
        try {
            updateMenuSelectionMethod.invoke(homeScreen);
            // Should not throw exception
        } catch (Exception e) {
            fail("updateMenuSelection should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testSelectCurrentMenuMethod() throws Exception {
        // Given
        Method selectCurrentMenuMethod = home.class.getDeclaredMethod("selectCurrentMenu");
        selectCurrentMenuMethod.setAccessible(true);

        // When & Then
        try {
            selectCurrentMenuMethod.invoke(homeScreen);
            // Should not throw exception (mock ScreenController handles navigation)
        } catch (Exception e) {
            fail("selectCurrentMenu should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testShowExitConfirmationMethod() throws Exception {
        // Given
        Method showExitConfirmationMethod = home.class.getDeclaredMethod("showExitConfirmation");
        showExitConfirmationMethod.setAccessible(true);

        // When & Then
        try {
            showExitConfirmationMethod.invoke(homeScreen);
            // Should not throw exception
        } catch (Exception e) {
            fail("showExitConfirmation should not throw exception: " + e.getMessage());
        }
    }

    // 윈도우 크기 관련 추가 테스트
    @Test
    public void testUpdateWindowSizeMethod() throws Exception {
        // Given
        Method updateWindowSizeMethod = home.class.getDeclaredMethod("updateWindowSize");
        updateWindowSizeMethod.setAccessible(true);

        // When
        updateWindowSizeMethod.invoke(homeScreen);

        // Then
        Field windowWidthField = home.class.getDeclaredField("windowWidth");
        Field windowHeightField = home.class.getDeclaredField("windowHeight");
        windowWidthField.setAccessible(true);
        windowHeightField.setAccessible(true);

        int windowWidth = (Integer) windowWidthField.get(homeScreen);
        int windowHeight = (Integer) windowHeightField.get(homeScreen);

        assertTrue("Window width should be positive after update", windowWidth > 0);
        assertTrue("Window height should be positive after update", windowHeight > 0);
    }

    @Test
    public void testUpdateComponentSizesMethod() throws Exception {
        // Given
        Method updateComponentSizesMethod = home.class.getDeclaredMethod("updateComponentSizes");
        updateComponentSizesMethod.setAccessible(true);

        // When & Then
        try {
            updateComponentSizesMethod.invoke(homeScreen);
            // Should not throw exception
        } catch (Exception e) {
            fail("updateComponentSizes should not throw exception: " + e.getMessage());
        }
    }

    // 배경 관련 추가 테스트
    @Test
    public void testInitializeBackgroundMethod() throws Exception {
        // Given
        Method initializeBackgroundMethod = home.class.getDeclaredMethod("initializeBackground");
        initializeBackgroundMethod.setAccessible(true);

        // When
        initializeBackgroundMethod.invoke(homeScreen);

        // Then
        Field particlesField = home.class.getDeclaredField("particles");
        Field animationTimerField = home.class.getDeclaredField("animationTimer");
        particlesField.setAccessible(true);
        animationTimerField.setAccessible(true);

        assertNotNull("Particles should be initialized", particlesField.get(homeScreen));
        assertNotNull("Animation timer should be initialized", animationTimerField.get(homeScreen));
    }

    @Test
    public void testLoadBackgroundImageMethod() throws Exception {
        // Given
        Method loadBackgroundImageMethod = home.class.getDeclaredMethod("loadBackgroundImage");
        loadBackgroundImageMethod.setAccessible(true);

        // When & Then
        try {
            loadBackgroundImageMethod.invoke(homeScreen);
            // Should not throw exception even if no background image is found
        } catch (Exception e) {
            fail("loadBackgroundImage should handle missing files gracefully: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateParticlesMethod() throws Exception {
        // Given
        Method updateParticlesMethod = home.class.getDeclaredMethod("updateParticles");
        updateParticlesMethod.setAccessible(true);

        // Initialize particles first
        Method initializeBackgroundMethod = home.class.getDeclaredMethod("initializeBackground");
        initializeBackgroundMethod.setAccessible(true);
        initializeBackgroundMethod.invoke(homeScreen);

        // When & Then
        try {
            updateParticlesMethod.invoke(homeScreen);
            // Should not throw exception
        } catch (Exception e) {
            fail("updateParticles should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testDrawBackgroundMethod() throws Exception {
        // Given
        Method drawBackgroundMethod = home.class.getDeclaredMethod("drawBackground", java.awt.Graphics2D.class);
        drawBackgroundMethod.setAccessible(true);
        
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = image.createGraphics();

        // When & Then
        try {
            drawBackgroundMethod.invoke(homeScreen, g2d);
            // Should not throw exception
        } catch (Exception e) {
            fail("drawBackground should not throw exception: " + e.getMessage());
        } finally {
            g2d.dispose();
        }
    }

    @Test
    public void testDrawParticlesMethod() throws Exception {
        // Given
        Method drawParticlesMethod = home.class.getDeclaredMethod("drawParticles", java.awt.Graphics2D.class);
        drawParticlesMethod.setAccessible(true);
        
        // Initialize particles first
        Method initializeBackgroundMethod = home.class.getDeclaredMethod("initializeBackground");
        initializeBackgroundMethod.setAccessible(true);
        initializeBackgroundMethod.invoke(homeScreen);
        
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = image.createGraphics();

        // When & Then
        try {
            drawParticlesMethod.invoke(homeScreen, g2d);
            // Should not throw exception
        } catch (Exception e) {
            fail("drawParticles should not throw exception: " + e.getMessage());
        } finally {
            g2d.dispose();
        }
    }

    // 컴포넌트 초기화 관련 추가 테스트
    @Test
    public void testInitializeComponentsMethod() throws Exception {
        // Given
        Method initializeComponentsMethod = home.class.getDeclaredMethod("initializeComponents");
        initializeComponentsMethod.setAccessible(true);

        // When & Then
        try {
            initializeComponentsMethod.invoke(homeScreen);
            // Should not throw exception
        } catch (Exception e) {
            fail("initializeComponents should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testSetupLayoutMethod() throws Exception {
        // Given
        Method setupLayoutMethod = home.class.getDeclaredMethod("setupLayout");
        setupLayoutMethod.setAccessible(true);

        // When & Then
        try {
            setupLayoutMethod.invoke(homeScreen);
            // Should not throw exception
        } catch (Exception e) {
            fail("setupLayout should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testSetupKeyListenerMethod() throws Exception {
        // Given
        Method setupKeyListenerMethod = home.class.getDeclaredMethod("setupKeyListener");
        setupKeyListenerMethod.setAccessible(true);

        // When & Then
        try {
            setupKeyListenerMethod.invoke(homeScreen);
            // Should not throw exception
        } catch (Exception e) {
            fail("setupKeyListener should not throw exception: " + e.getMessage());
        }
    }

    // 라벨 관련 추가 테스트
    @Test
    public void testDescriptionLabelInitialization() throws Exception {
        // Given
        Field descriptionLabelField = home.class.getDeclaredField("descriptionLabel");
        descriptionLabelField.setAccessible(true);

        // When
        JLabel descriptionLabel = (JLabel) descriptionLabelField.get(homeScreen);

        // Then
        assertNotNull("Description label should not be null", descriptionLabel);
    }

    @Test
    public void testControlsLabelInitialization() throws Exception {
        // Given
        Field controlsLabelField = home.class.getDeclaredField("controlsLabel");
        controlsLabelField.setAccessible(true);

        // When
        JLabel controlsLabel = (JLabel) controlsLabelField.get(homeScreen);

        // Then
        assertNotNull("Controls label should not be null", controlsLabel);
        assertNotNull("Controls label text should not be null", controlsLabel.getText());
        assertTrue("Controls label should contain control instructions", 
                  controlsLabel.getText().contains("↑↓"));
    }

    @Test
    public void testGameInfoLabelInitialization() throws Exception {
        // Given
        Field gameInfoLabelField = home.class.getDeclaredField("gameInfoLabel");
        gameInfoLabelField.setAccessible(true);

        // When
        JLabel gameInfoLabel = (JLabel) gameInfoLabelField.get(homeScreen);

        // Then
        assertNotNull("Game info label should not be null", gameInfoLabel);
        assertNotNull("Game info label text should not be null", gameInfoLabel.getText());
    }

    // 에니메이션 타이머 테스트
    @Test
    public void testAnimationTimerRunning() throws Exception {
        // Given
        Field animationTimerField = home.class.getDeclaredField("animationTimer");
        animationTimerField.setAccessible(true);

        // When
        javax.swing.Timer animationTimer = (javax.swing.Timer) animationTimerField.get(homeScreen);

        // Then
        assertNotNull("Animation timer should not be null", animationTimer);
        assertTrue("Animation timer should be running", animationTimer.isRunning());
    }

    // paintComponent 오버라이드 테스트
    @Test
    public void testPaintComponent() {
        // Given
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(450, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics g = image.getGraphics();

        // When & Then
        try {
            homeScreen.paintComponent(g);
            // Should not throw exception
        } catch (Exception e) {
            fail("paintComponent should not throw exception: " + e.getMessage());
        } finally {
            g.dispose();
        }
    }
}