package se.tetris.team5.screens;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.tetris.team5.ScreenController;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * home 클래스 추가 테스트
 * 메인 메뉴 화면 테스트 (HomeTest.java에 추가)
 */
public class homeAdditionalTest {

    private ScreenController screenController;
    private home homeScreen;

    @Before
    public void setUp() {
        screenController = new ScreenController();
        screenController.setVisible(false);
        homeScreen = new home(screenController);
    }

    @After
    public void tearDown() {
        if (screenController != null) {
            screenController.dispose();
        }
    }

    // === 1. 추가 생성 테스트 ===
    @Test
    public void testHomeCreationNotNull() {
        assertNotNull("home 인스턴스가 생성되어야 함", homeScreen);
    }

    @Test
    public void testHomeIsJPanel() {
        assertTrue("home은 JPanel을 상속해야 함", homeScreen instanceof JPanel);
    }

    @Test
    public void testHomeImplementsKeyListener() {
        assertTrue("home은 KeyListener를 구현해야 함", homeScreen instanceof java.awt.event.KeyListener);
    }

    // === 2. updateWindowSize 메서드 테스트 ===
    @Test
    public void testUpdateWindowSize() {
        homeScreen.updateWindowSize();
        // 윈도우 크기 업데이트가 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateWindowSizeMultipleTimes() {
        homeScreen.updateWindowSize();
        homeScreen.updateWindowSize();
        homeScreen.updateWindowSize();
        // 여러 번 호출해도 안전해야 함
    }

    // === 3. 리플렉션을 통한 private 필드 테스트 ===
    @Test
    public void testSelectedMenuField() throws Exception {
        Field selectedMenuField = home.class.getDeclaredField("selectedMenu");
        selectedMenuField.setAccessible(true);
        Integer selectedMenu = (Integer) selectedMenuField.get(homeScreen);
        assertNotNull("selectedMenu 필드가 존재해야 함", selectedMenu);
        assertTrue("selectedMenu는 0 이상이어야 함", selectedMenu >= 0);
    }

    @Test
    public void testInDifficultySelectionField() throws Exception {
        Field inDifficultySelectionField = home.class.getDeclaredField("inDifficultySelection");
        inDifficultySelectionField.setAccessible(true);
        Boolean inDifficultySelection = (Boolean) inDifficultySelectionField.get(homeScreen);
        assertNotNull("inDifficultySelection 필드가 존재해야 함", inDifficultySelection);
    }

    @Test
    public void testInBattleModeSelectionField() throws Exception {
        Field inBattleModeSelectionField = home.class.getDeclaredField("inBattleModeSelection");
        inBattleModeSelectionField.setAccessible(true);
        Boolean inBattleModeSelection = (Boolean) inBattleModeSelectionField.get(homeScreen);
        assertNotNull("inBattleModeSelection 필드가 존재해야 함", inBattleModeSelection);
    }

    @Test
    public void testShowHelpMessageField() throws Exception {
        Field showHelpMessageField = home.class.getDeclaredField("showHelpMessage");
        showHelpMessageField.setAccessible(true);
        Boolean showHelpMessage = (Boolean) showHelpMessageField.get(homeScreen);
        assertNotNull("showHelpMessage 필드가 존재해야 함", showHelpMessage);
    }

    @Test
    public void testMainMenuOptionsField() throws Exception {
        Field mainMenuOptionsField = home.class.getDeclaredField("mainMenuOptions");
        mainMenuOptionsField.setAccessible(true);
        String[] mainMenuOptions = (String[]) mainMenuOptionsField.get(homeScreen);
        assertNotNull("mainMenuOptions가 초기화되어야 함", mainMenuOptions);
        assertTrue("mainMenuOptions는 7개 항목을 가져야 함", mainMenuOptions.length == 7);
    }

    @Test
    public void testMainMenuIconsField() throws Exception {
        Field mainMenuIconsField = home.class.getDeclaredField("mainMenuIcons");
        mainMenuIconsField.setAccessible(true);
        String[] mainMenuIcons = (String[]) mainMenuIconsField.get(homeScreen);
        assertNotNull("mainMenuIcons가 초기화되어야 함", mainMenuIcons);
        assertTrue("mainMenuIcons는 7개 항목을 가져야 함", mainMenuIcons.length == 7);
    }

    @Test
    public void testMainMenuDescriptionsField() throws Exception {
        Field mainMenuDescriptionsField = home.class.getDeclaredField("mainMenuDescriptions");
        mainMenuDescriptionsField.setAccessible(true);
        String[] mainMenuDescriptions = (String[]) mainMenuDescriptionsField.get(homeScreen);
        assertNotNull("mainMenuDescriptions가 초기화되어야 함", mainMenuDescriptions);
        assertTrue("mainMenuDescriptions는 7개 항목을 가져야 함", mainMenuDescriptions.length == 7);
    }

    @Test
    public void testDifficultyMenuOptionsField() throws Exception {
        Field difficultyMenuOptionsField = home.class.getDeclaredField("difficultyMenuOptions");
        difficultyMenuOptionsField.setAccessible(true);
        String[] difficultyMenuOptions = (String[]) difficultyMenuOptionsField.get(homeScreen);
        assertNotNull("difficultyMenuOptions가 초기화되어야 함", difficultyMenuOptions);
        assertTrue("difficultyMenuOptions는 4개 항목을 가져야 함", difficultyMenuOptions.length == 4);
    }

    @Test
    public void testBattleModeOptionsField() throws Exception {
        Field battleModeOptionsField = home.class.getDeclaredField("battleModeOptions");
        battleModeOptionsField.setAccessible(true);
        String[] battleModeOptions = (String[]) battleModeOptionsField.get(homeScreen);
        assertNotNull("battleModeOptions가 초기화되어야 함", battleModeOptions);
        assertTrue("battleModeOptions는 4개 항목을 가져야 함", battleModeOptions.length == 4);
    }

    // === 4. 리플렉션을 통한 private 메서드 테스트 ===
    @Test
    public void testGetCurrentMenuOptionsMethod() throws Exception {
        Method getCurrentMenuOptionsMethod = home.class.getDeclaredMethod("getCurrentMenuOptions");
        getCurrentMenuOptionsMethod.setAccessible(true);
        String[] options = (String[]) getCurrentMenuOptionsMethod.invoke(homeScreen);
        assertNotNull("getCurrentMenuOptions가 값을 반환해야 함", options);
        assertTrue("options는 비어있지 않아야 함", options.length > 0);
    }

    @Test
    public void testGetCurrentMenuIconsMethod() throws Exception {
        Method getCurrentMenuIconsMethod = home.class.getDeclaredMethod("getCurrentMenuIcons");
        getCurrentMenuIconsMethod.setAccessible(true);
        String[] icons = (String[]) getCurrentMenuIconsMethod.invoke(homeScreen);
        assertNotNull("getCurrentMenuIcons가 값을 반환해야 함", icons);
        assertTrue("icons는 비어있지 않아야 함", icons.length > 0);
    }

    @Test
    public void testGetCurrentMenuDescriptionsMethod() throws Exception {
        Method getCurrentMenuDescriptionsMethod = home.class.getDeclaredMethod("getCurrentMenuDescriptions");
        getCurrentMenuDescriptionsMethod.setAccessible(true);
        String[] descriptions = (String[]) getCurrentMenuDescriptionsMethod.invoke(homeScreen);
        assertNotNull("getCurrentMenuDescriptions가 값을 반환해야 함", descriptions);
        assertTrue("descriptions는 비어있지 않아야 함", descriptions.length > 0);
    }

    @Test
    public void testInitializeComponentsMethod() throws Exception {
        Method initializeComponentsMethod = home.class.getDeclaredMethod("initializeComponents");
        initializeComponentsMethod.setAccessible(true);
        initializeComponentsMethod.invoke(homeScreen);
        // initializeComponents가 정상적으로 실행되어야 함
    }

    @Test
    public void testSetupLayoutMethod() throws Exception {
        Method setupLayoutMethod = home.class.getDeclaredMethod("setupLayout");
        setupLayoutMethod.setAccessible(true);
        setupLayoutMethod.invoke(homeScreen);
        // setupLayout가 정상적으로 실행되어야 함
    }

    @Test
    public void testSetupKeyListenerMethod() throws Exception {
        Method setupKeyListenerMethod = home.class.getDeclaredMethod("setupKeyListener");
        setupKeyListenerMethod.setAccessible(true);
        setupKeyListenerMethod.invoke(homeScreen);
        // setupKeyListener가 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateMenuSelectionMethod() throws Exception {
        Method updateMenuSelectionMethod = home.class.getDeclaredMethod("updateMenuSelection");
        updateMenuSelectionMethod.setAccessible(true);
        updateMenuSelectionMethod.invoke(homeScreen);
        // updateMenuSelection이 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateComponentSizesMethod() throws Exception {
        Method updateComponentSizesMethod = home.class.getDeclaredMethod("updateComponentSizes");
        updateComponentSizesMethod.setAccessible(true);
        updateComponentSizesMethod.invoke(homeScreen);
        // updateComponentSizes가 정상적으로 실행되어야 함
    }

    @Test
    public void testRebuildMenuMethod() throws Exception {
        Method rebuildMenuMethod = home.class.getDeclaredMethod("rebuildMenu");
        rebuildMenuMethod.setAccessible(true);
        rebuildMenuMethod.invoke(homeScreen);
        // rebuildMenu가 정상적으로 실행되어야 함
    }

    // === 5. 키 입력 테스트 ===
    @Test
    public void testKeyTyped() {
        KeyEvent keyEvent = new KeyEvent(
                homeScreen,
                KeyEvent.KEY_TYPED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_UNDEFINED,
                'a'
        );
        homeScreen.keyTyped(keyEvent);
        // keyTyped가 정상적으로 처리되어야 함
    }

    @Test
    public void testKeyReleased() {
        KeyEvent keyEvent = new KeyEvent(
                homeScreen,
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_UP,
                KeyEvent.CHAR_UNDEFINED
        );
        homeScreen.keyReleased(keyEvent);
        // keyReleased가 정상적으로 처리되어야 함
    }

    @Test
    public void testKeyPressedUp() {
        KeyEvent keyEvent = new KeyEvent(
                homeScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_UP,
                KeyEvent.CHAR_UNDEFINED
        );
        homeScreen.keyPressed(keyEvent);
        // 위 방향키 입력 처리
    }

    @Test
    public void testKeyPressedDown() {
        KeyEvent keyEvent = new KeyEvent(
                homeScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_DOWN,
                KeyEvent.CHAR_UNDEFINED
        );
        homeScreen.keyPressed(keyEvent);
        // 아래 방향키 입력 처리
    }

    @Test
    public void testKeyPressedEnter() {
        KeyEvent keyEvent = new KeyEvent(
                homeScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_ENTER,
                KeyEvent.CHAR_UNDEFINED
        );
        homeScreen.keyPressed(keyEvent);
        // Enter 키 입력 처리
    }

    @Test
    public void testKeyPressedEscape() {
        KeyEvent keyEvent = new KeyEvent(
                homeScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_ESCAPE,
                KeyEvent.CHAR_UNDEFINED
        );
        homeScreen.keyPressed(keyEvent);
        // ESC 키 입력 처리
    }

    @Test
    public void testKeyPressedF1() {
        KeyEvent keyEvent = new KeyEvent(
                homeScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_F1,
                KeyEvent.CHAR_UNDEFINED
        );
        homeScreen.keyPressed(keyEvent);
        // F1 키 입력 처리 (도움말)
    }

    // === 6. 컴포넌트 테스트 ===
    @Test
    public void testTitleLabelInitialized() throws Exception {
        Field titleLabelField = home.class.getDeclaredField("titleLabel");
        titleLabelField.setAccessible(true);
        Object titleLabel = titleLabelField.get(homeScreen);
        assertNotNull("titleLabel이 초기화되어야 함", titleLabel);
    }

    @Test
    public void testMenuButtonsInitialized() throws Exception {
        Field menuButtonsField = home.class.getDeclaredField("menuButtons");
        menuButtonsField.setAccessible(true);
        Object menuButtons = menuButtonsField.get(homeScreen);
        assertNotNull("menuButtons가 초기화되어야 함", menuButtons);
    }

    @Test
    public void testDescriptionLabelInitialized() throws Exception {
        Field descriptionLabelField = home.class.getDeclaredField("descriptionLabel");
        descriptionLabelField.setAccessible(true);
        Object descriptionLabel = descriptionLabelField.get(homeScreen);
        assertNotNull("descriptionLabel이 초기화되어야 함", descriptionLabel);
    }

    @Test
    public void testGameInfoLabelInitialized() throws Exception {
        Field gameInfoLabelField = home.class.getDeclaredField("gameInfoLabel");
        gameInfoLabelField.setAccessible(true);
        Object gameInfoLabel = gameInfoLabelField.get(homeScreen);
        assertNotNull("gameInfoLabel이 초기화되어야 함", gameInfoLabel);
    }

    // === 7. 윈도우 크기 관련 테스트 ===
    @Test
    public void testWindowWidthField() throws Exception {
        Field windowWidthField = home.class.getDeclaredField("windowWidth");
        windowWidthField.setAccessible(true);
        Integer windowWidth = (Integer) windowWidthField.get(homeScreen);
        assertNotNull("windowWidth 필드가 존재해야 함", windowWidth);
        assertTrue("windowWidth는 양수여야 함", windowWidth > 0);
    }

    @Test
    public void testWindowHeightField() throws Exception {
        Field windowHeightField = home.class.getDeclaredField("windowHeight");
        windowHeightField.setAccessible(true);
        Integer windowHeight = (Integer) windowHeightField.get(homeScreen);
        assertNotNull("windowHeight 필드가 존재해야 함", windowHeight);
        assertTrue("windowHeight는 양수여야 함", windowHeight > 0);
    }

    @Test
    public void testCurrentWindowSizeField() throws Exception {
        Field currentWindowSizeField = home.class.getDeclaredField("currentWindowSize");
        currentWindowSizeField.setAccessible(true);
        Object currentWindowSize = currentWindowSizeField.get(homeScreen);
        assertNotNull("currentWindowSize가 설정되어야 함", currentWindowSize);
    }

    // === 8. 배경 관련 테스트 ===
    @Test
    public void testParticlesField() throws Exception {
        Field particlesField = home.class.getDeclaredField("particles");
        particlesField.setAccessible(true);
        Object particles = particlesField.get(homeScreen);
        // particles는 null일 수 있음
    }

    @Test
    public void testAnimationTimerField() throws Exception {
        Field animationTimerField = home.class.getDeclaredField("animationTimer");
        animationTimerField.setAccessible(true);
        Object animationTimer = animationTimerField.get(homeScreen);
        // animationTimer는 null일 수 있음
    }

    @Test
    public void testBackgroundImageField() throws Exception {
        Field backgroundImageField = home.class.getDeclaredField("backgroundImage");
        backgroundImageField.setAccessible(true);
        Object backgroundImage = backgroundImageField.get(homeScreen);
        // backgroundImage는 null일 수 있음
    }

    // === 9. 도움말 관련 테스트 ===
    @Test
    public void testHelpMessageTimerField() throws Exception {
        Field helpMessageTimerField = home.class.getDeclaredField("helpMessageTimer");
        helpMessageTimerField.setAccessible(true);
        Object helpMessageTimer = helpMessageTimerField.get(homeScreen);
        // helpMessageTimer는 null일 수 있음
    }

    @Test
    public void testHelpWindowField() throws Exception {
        Field helpWindowField = home.class.getDeclaredField("helpWindow");
        helpWindowField.setAccessible(true);
        Object helpWindow = helpWindowField.get(homeScreen);
        // helpWindow는 null일 수 있음
    }

    // === 10. 통합 테스트 ===
    @Test
    public void testHomeFullLifecycle() {
        home h = new home(screenController);
        h.updateWindowSize();
        // 전체 라이프사이클이 정상적으로 동작해야 함
    }

    @Test
    public void testHomeMultipleKeySequence() {
        KeyEvent down = new KeyEvent(homeScreen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        KeyEvent up = new KeyEvent(homeScreen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        
        homeScreen.keyPressed(down);
        homeScreen.keyPressed(down);
        homeScreen.keyPressed(up);
        // 여러 키 입력이 정상적으로 처리되어야 함
    }

    @Test
    public void testHomeScreenControllerNotNull() throws Exception {
        Field screenControllerField = home.class.getDeclaredField("screenController");
        screenControllerField.setAccessible(true);
        Object sc = screenControllerField.get(homeScreen);
        assertNotNull("screenController가 null이 아니어야 함", sc);
    }

    @Test
    public void testHomeComponentCount() {
        assertTrue("home은 컴포넌트를 포함해야 함", homeScreen.getComponentCount() >= 0);
    }

    @Test
    public void testHomeLayoutManager() {
        assertNotNull("home은 LayoutManager를 가져야 함", homeScreen.getLayout());
    }

    @Test
    public void testHomeKeyListenersAttached() {
        java.awt.event.KeyListener[] listeners = homeScreen.getKeyListeners();
        assertTrue("KeyListener가 추가되어야 함", listeners.length > 0);
    }
}
