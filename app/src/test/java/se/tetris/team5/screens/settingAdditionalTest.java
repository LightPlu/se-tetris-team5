package se.tetris.team5.screens;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.tetris.team5.ScreenController;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * setting 클래스 추가 테스트
 * 설정 화면 테스트 (SettingScreenTest.java에 추가)
 */
public class settingAdditionalTest {

    private ScreenController screenController;
    private setting settingScreen;

    @Before
    public void setUp() {
        screenController = new ScreenController();
        screenController.setVisible(false);
        settingScreen = new setting(screenController);
    }

    @After
    public void tearDown() {
        if (screenController != null) {
            screenController.dispose();
        }
    }

    // === 1. 기본 생성 테스트 ===
    @Test
    public void testSettingCreation() {
        assertNotNull("setting 인스턴스가 생성되어야 함", settingScreen);
    }

    @Test
    public void testSettingConstructorWithScreenController() {
        setting s = new setting(screenController);
        assertNotNull("ScreenController로 생성되어야 함", s);
    }

    // === 2. display 메서드 테스트 ===
    @Test
    public void testDisplay() {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        // display 메서드가 정상적으로 실행되어야 함
    }

    @Test
    public void testDisplayWithNullTextPane() {
        try {
            settingScreen.display(null);
            fail("null textPane으로 예외가 발생해야 함");
        } catch (NullPointerException e) {
            // 예상된 동작
        }
    }

    @Test
    public void testDisplayMultipleTimes() {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        settingScreen.display(textPane);
        settingScreen.display(textPane);
        // 여러 번 호출해도 안전해야 함
    }

    // === 3. 리플렉션을 통한 private 필드 테스트 ===
    @Test
    public void testScreenControllerField() throws Exception {
        Field screenControllerField = setting.class.getDeclaredField("screenController");
        screenControllerField.setAccessible(true);
        Object sc = screenControllerField.get(settingScreen);
        assertNotNull("screenController가 초기화되어야 함", sc);
    }

    @Test
    public void testCurrentTextPaneField() throws Exception {
        Field currentTextPaneField = setting.class.getDeclaredField("currentTextPane");
        currentTextPaneField.setAccessible(true);
        Object currentTextPane = currentTextPaneField.get(settingScreen);
        // currentTextPane은 display 호출 전까지 null일 수 있음
    }

    @Test
    public void testGameSettingsField() throws Exception {
        Field gameSettingsField = setting.class.getDeclaredField("gameSettings");
        gameSettingsField.setAccessible(true);
        Object gameSettings = gameSettingsField.get(settingScreen);
        assertNotNull("gameSettings가 초기화되어야 함", gameSettings);
    }

    @Test
    public void testSelectedOptionField() throws Exception {
        Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
        selectedOptionField.setAccessible(true);
        Integer selectedOption = (Integer) selectedOptionField.get(settingScreen);
        assertNotNull("selectedOption 필드가 존재해야 함", selectedOption);
        assertEquals("selectedOption 초기값은 0이어야 함", 0, selectedOption.intValue());
    }

    @Test
    public void testMenuOptionsField() throws Exception {
        Field menuOptionsField = setting.class.getDeclaredField("menuOptions");
        menuOptionsField.setAccessible(true);
        String[] menuOptions = (String[]) menuOptionsField.get(settingScreen);
        assertNotNull("menuOptions가 초기화되어야 함", menuOptions);
        assertEquals("menuOptions는 9개 항목을 가져야 함", 9, menuOptions.length);
    }

    @Test
    public void testWindowSizesField() throws Exception {
        Field windowSizesField = setting.class.getDeclaredField("windowSizes");
        windowSizesField.setAccessible(true);
        String[] windowSizes = (String[]) windowSizesField.get(settingScreen);
        assertNotNull("windowSizes가 초기화되어야 함", windowSizes);
        assertEquals("windowSizes는 3개 항목을 가져야 함", 3, windowSizes.length);
    }

    @Test
    public void testWindowSizeValuesField() throws Exception {
        Field windowSizeValuesField = setting.class.getDeclaredField("windowSizeValues");
        windowSizeValuesField.setAccessible(true);
        String[] windowSizeValues = (String[]) windowSizeValuesField.get(settingScreen);
        assertNotNull("windowSizeValues가 초기화되어야 함", windowSizeValues);
        assertEquals("windowSizeValues는 3개 항목을 가져야 함", 3, windowSizeValues.length);
    }

    @Test
    public void testCurrentSizeIndexField() throws Exception {
        Field currentSizeIndexField = setting.class.getDeclaredField("currentSizeIndex");
        currentSizeIndexField.setAccessible(true);
        Integer currentSizeIndex = (Integer) currentSizeIndexField.get(settingScreen);
        assertNotNull("currentSizeIndex 필드가 존재해야 함", currentSizeIndex);
        assertEquals("currentSizeIndex 초기값은 1이어야 함", 1, currentSizeIndex.intValue());
    }

    // === 4. 키 설정 관련 필드 테스트 ===
    @Test
    public void testIsKeySettingModeField() throws Exception {
        Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
        isKeySettingModeField.setAccessible(true);
        Boolean isKeySettingMode = (Boolean) isKeySettingModeField.get(settingScreen);
        assertNotNull("isKeySettingMode 필드가 존재해야 함", isKeySettingMode);
        assertFalse("isKeySettingMode 초기값은 false여야 함", isKeySettingMode);
    }

    @Test
    public void testCurrentKeyActionField() throws Exception {
        Field currentKeyActionField = setting.class.getDeclaredField("currentKeyAction");
        currentKeyActionField.setAccessible(true);
        String currentKeyAction = (String) currentKeyActionField.get(settingScreen);
        assertNotNull("currentKeyAction 필드가 존재해야 함", currentKeyAction);
    }

    @Test
    public void testKeyActionsField() throws Exception {
        Field keyActionsField = setting.class.getDeclaredField("keyActions");
        keyActionsField.setAccessible(true);
        String[] keyActions = (String[]) keyActionsField.get(settingScreen);
        assertNotNull("keyActions가 초기화되어야 함", keyActions);
        assertEquals("keyActions는 7개 항목을 가져야 함", 7, keyActions.length);
    }

    @Test
    public void testKeyActionKeysField() throws Exception {
        Field keyActionKeysField = setting.class.getDeclaredField("keyActionKeys");
        keyActionKeysField.setAccessible(true);
        String[] keyActionKeys = (String[]) keyActionKeysField.get(settingScreen);
        assertNotNull("keyActionKeys가 초기화되어야 함", keyActionKeys);
        assertEquals("keyActionKeys는 7개 항목을 가져야 함", 7, keyActionKeys.length);
    }

    @Test
    public void testCurrentKeyIndexField() throws Exception {
        Field currentKeyIndexField = setting.class.getDeclaredField("currentKeyIndex");
        currentKeyIndexField.setAccessible(true);
        Integer currentKeyIndex = (Integer) currentKeyIndexField.get(settingScreen);
        assertNotNull("currentKeyIndex 필드가 존재해야 함", currentKeyIndex);
        assertEquals("currentKeyIndex 초기값은 0이어야 함", 0, currentKeyIndex.intValue());
    }

    // === 5. 대전모드 키 설정 관련 필드 테스트 ===
    @Test
    public void testIsBattleKeySettingModeField() throws Exception {
        Field isBattleKeySettingModeField = setting.class.getDeclaredField("isBattleKeySettingMode");
        isBattleKeySettingModeField.setAccessible(true);
        Boolean isBattleKeySettingMode = (Boolean) isBattleKeySettingModeField.get(settingScreen);
        assertNotNull("isBattleKeySettingMode 필드가 존재해야 함", isBattleKeySettingMode);
        assertFalse("isBattleKeySettingMode 초기값은 false여야 함", isBattleKeySettingMode);
    }

    @Test
    public void testBattleKeyPlayerNumField() throws Exception {
        Field battleKeyPlayerNumField = setting.class.getDeclaredField("battleKeyPlayerNum");
        battleKeyPlayerNumField.setAccessible(true);
        Integer battleKeyPlayerNum = (Integer) battleKeyPlayerNumField.get(settingScreen);
        assertNotNull("battleKeyPlayerNum 필드가 존재해야 함", battleKeyPlayerNum);
        assertEquals("battleKeyPlayerNum 초기값은 1이어야 함", 1, battleKeyPlayerNum.intValue());
    }

    @Test
    public void testBattleKeyActionsField() throws Exception {
        Field battleKeyActionsField = setting.class.getDeclaredField("battleKeyActions");
        battleKeyActionsField.setAccessible(true);
        String[] battleKeyActions = (String[]) battleKeyActionsField.get(settingScreen);
        assertNotNull("battleKeyActions가 초기화되어야 함", battleKeyActions);
        assertEquals("battleKeyActions는 6개 항목을 가져야 함", 6, battleKeyActions.length);
    }

    @Test
    public void testBattleKeyActionKeysField() throws Exception {
        Field battleKeyActionKeysField = setting.class.getDeclaredField("battleKeyActionKeys");
        battleKeyActionKeysField.setAccessible(true);
        String[] battleKeyActionKeys = (String[]) battleKeyActionKeysField.get(settingScreen);
        assertNotNull("battleKeyActionKeys가 초기화되어야 함", battleKeyActionKeys);
        assertEquals("battleKeyActionKeys는 6개 항목을 가져야 함", 6, battleKeyActionKeys.length);
    }

    @Test
    public void testCurrentBattleKeyIndexField() throws Exception {
        Field currentBattleKeyIndexField = setting.class.getDeclaredField("currentBattleKeyIndex");
        currentBattleKeyIndexField.setAccessible(true);
        Integer currentBattleKeyIndex = (Integer) currentBattleKeyIndexField.get(settingScreen);
        assertNotNull("currentBattleKeyIndex 필드가 존재해야 함", currentBattleKeyIndex);
        assertEquals("currentBattleKeyIndex 초기값은 0이어야 함", 0, currentBattleKeyIndex.intValue());
    }

    // === 6. 배경 이미지 관련 필드 테스트 ===
    @Test
    public void testBackgroundPanelField() throws Exception {
        Field backgroundPanelField = setting.class.getDeclaredField("backgroundPanel");
        backgroundPanelField.setAccessible(true);
        Object backgroundPanel = backgroundPanelField.get(settingScreen);
        // backgroundPanel은 display 호출 전까지 null일 수 있음
    }

    @Test
    public void testBackgroundGifField() throws Exception {
        Field backgroundGifField = setting.class.getDeclaredField("backgroundGif");
        backgroundGifField.setAccessible(true);
        Object backgroundGif = backgroundGifField.get(settingScreen);
        // backgroundGif는 null일 수 있음
    }

    @Test
    public void testSelectorIconField() throws Exception {
        Field selectorIconField = setting.class.getDeclaredField("selectorIcon");
        selectorIconField.setAccessible(true);
        Object selectorIcon = selectorIconField.get(settingScreen);
        // selectorIcon은 null일 수 있음
    }

    @Test
    public void testSelectorIconSizeConstant() throws Exception {
        Field selectorIconSizeField = setting.class.getDeclaredField("SELECTOR_ICON_SIZE");
        selectorIconSizeField.setAccessible(true);
        Integer selectorIconSize = (Integer) selectorIconSizeField.get(null);
        assertNotNull("SELECTOR_ICON_SIZE가 정의되어야 함", selectorIconSize);
        assertEquals("SELECTOR_ICON_SIZE는 40이어야 함", 40, selectorIconSize.intValue());
    }

    // === 7. 리플렉션을 통한 private 메서드 테스트 ===
    @Test
    public void testLoadBackgroundImageMethod() throws Exception {
        Method loadBackgroundImageMethod = setting.class.getDeclaredMethod("loadBackgroundImage");
        loadBackgroundImageMethod.setAccessible(true);
        loadBackgroundImageMethod.invoke(settingScreen);
        // loadBackgroundImage가 정상적으로 실행되어야 함
    }

    @Test
    public void testLoadSelectorImageMethod() throws Exception {
        Method loadSelectorImageMethod = setting.class.getDeclaredMethod("loadSelectorImage");
        loadSelectorImageMethod.setAccessible(true);
        loadSelectorImageMethod.invoke(settingScreen);
        // loadSelectorImage가 정상적으로 실행되어야 함
    }

    @Test
    public void testInitializeCurrentSettingsMethod() throws Exception {
        Method initializeCurrentSettingsMethod = setting.class.getDeclaredMethod("initializeCurrentSettings");
        initializeCurrentSettingsMethod.setAccessible(true);
        initializeCurrentSettingsMethod.invoke(settingScreen);
        // initializeCurrentSettings가 정상적으로 실행되어야 함
    }

    @Test
    public void testDrawSettingScreenMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method drawSettingScreenMethod = setting.class.getDeclaredMethod("drawSettingScreen");
        drawSettingScreenMethod.setAccessible(true);
        drawSettingScreenMethod.invoke(settingScreen);
        // drawSettingScreen이 정상적으로 실행되어야 함
    }

    @Test
    public void testDrawKeySettingScreenMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method drawKeySettingScreenMethod = setting.class.getDeclaredMethod("drawKeySettingScreen");
        drawKeySettingScreenMethod.setAccessible(true);
        drawKeySettingScreenMethod.invoke(settingScreen);
        // drawKeySettingScreen이 정상적으로 실행되어야 함
    }

    @Test
    public void testDrawBattleKeySettingScreenMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method drawBattleKeySettingScreenMethod = setting.class.getDeclaredMethod("drawBattleKeySettingScreen");
        drawBattleKeySettingScreenMethod.setAccessible(true);
        drawBattleKeySettingScreenMethod.invoke(settingScreen);
        // drawBattleKeySettingScreen이 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateDisplayMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method updateDisplayMethod = setting.class.getDeclaredMethod("updateDisplay", String.class);
        updateDisplayMethod.setAccessible(true);
        updateDisplayMethod.invoke(settingScreen, "Test Message");
        // updateDisplay가 정상적으로 실행되어야 함
    }

    @Test
    public void testApplySelectorIconsMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method applySelectorIconsMethod = setting.class.getDeclaredMethod("applySelectorIcons");
        applySelectorIconsMethod.setAccessible(true);
        applySelectorIconsMethod.invoke(settingScreen);
        // applySelectorIcons가 정상적으로 실행되어야 함
    }

    @Test
    public void testHighlightSelectedLineMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method highlightSelectedLineMethod = setting.class.getDeclaredMethod("highlightSelectedLine");
        highlightSelectedLineMethod.setAccessible(true);
        highlightSelectedLineMethod.invoke(settingScreen);
        // highlightSelectedLine이 정상적으로 실행되어야 함
    }

    @Test
    public void testHandleMenuActionMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method handleMenuActionMethod = setting.class.getDeclaredMethod("handleMenuAction");
        handleMenuActionMethod.setAccessible(true);
        handleMenuActionMethod.invoke(settingScreen);
        // handleMenuAction이 정상적으로 실행되어야 함
    }

    @Test
    public void testHandleLeftRightMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method handleLeftRightMethod = setting.class.getDeclaredMethod("handleLeftRight", boolean.class);
        handleLeftRightMethod.setAccessible(true);
        handleLeftRightMethod.invoke(settingScreen, true);
        handleLeftRightMethod.invoke(settingScreen, false);
        // handleLeftRight가 정상적으로 실행되어야 함
    }

    @Test
    public void testApplySizeChangeMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method applySizeChangeMethod = setting.class.getDeclaredMethod("applySizeChange");
        applySizeChangeMethod.setAccessible(true);
        applySizeChangeMethod.invoke(settingScreen);
        // applySizeChange가 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateGameColorsMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method updateGameColorsMethod = setting.class.getDeclaredMethod("updateGameColors");
        updateGameColorsMethod.setAccessible(true);
        updateGameColorsMethod.invoke(settingScreen);
        // updateGameColors가 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateGameSpeedMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method updateGameSpeedMethod = setting.class.getDeclaredMethod("updateGameSpeed");
        updateGameSpeedMethod.setAccessible(true);
        updateGameSpeedMethod.invoke(settingScreen);
        // updateGameSpeed가 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateSettingColorsMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method updateSettingColorsMethod = setting.class.getDeclaredMethod("updateSettingColors");
        updateSettingColorsMethod.setAccessible(true);
        updateSettingColorsMethod.invoke(settingScreen);
        // updateSettingColors가 정상적으로 실행되어야 함
    }

    @Test
    public void testControlBGMMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        settingScreen.display(textPane);
        Method controlBGMMethod = setting.class.getDeclaredMethod("controlBGM");
        controlBGMMethod.setAccessible(true);
        controlBGMMethod.invoke(settingScreen);
        // controlBGM이 정상적으로 실행되어야 함
    }

    // === 8. 통합 테스트 ===
    @Test
    public void testSettingFullLifecycle() {
        setting s = new setting(screenController);
        JTextPane textPane = new JTextPane();
        s.display(textPane);
        // 전체 라이프사이클이 정상적으로 동작해야 함
    }

    @Test
    public void testSettingWithMultipleDisplayCalls() {
        JTextPane textPane1 = new JTextPane();
        JTextPane textPane2 = new JTextPane();
        settingScreen.display(textPane1);
        settingScreen.display(textPane2);
        // 여러 textPane으로 display를 호출해도 안전해야 함
    }

    @Test
    public void testSettingMenuOptionsCount() throws Exception {
        Field menuOptionsField = setting.class.getDeclaredField("menuOptions");
        menuOptionsField.setAccessible(true);
        String[] menuOptions = (String[]) menuOptionsField.get(settingScreen);
        
        assertTrue("menuOptions는 최소 5개 이상이어야 함", menuOptions.length >= 5);
    }

    @Test
    public void testSettingStyleSetField() throws Exception {
        Field styleSetField = setting.class.getDeclaredField("styleSet");
        styleSetField.setAccessible(true);
        Object styleSet = styleSetField.get(settingScreen);
        assertNotNull("styleSet이 초기화되어야 함", styleSet);
    }
}
