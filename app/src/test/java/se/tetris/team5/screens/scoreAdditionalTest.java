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
 * score 클래스 추가 테스트
 * 점수 화면 테스트 (ScoreTest.java에 추가)
 */
public class scoreAdditionalTest {

    private ScreenController screenController;
    private score scoreScreen;

    @Before
    public void setUp() {
        screenController = new ScreenController();
        screenController.setVisible(false);
        scoreScreen = new score(screenController);
    }

    @After
    public void tearDown() {
        if (screenController != null) {
            screenController.dispose();
        }
    }

    // === 1. 기본 생성 테스트 ===
    @Test
    public void testScoreCreation() {
        assertNotNull("score 인스턴스가 생성되어야 함", scoreScreen);
    }

    @Test
    public void testScoreConstructorWithScreenController() {
        score s = new score(screenController);
        assertNotNull("ScreenController로 생성되어야 함", s);
    }

    // === 2. display 메서드 테스트 ===
    @Test
    public void testDisplay() {
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        // display 메서드가 정상적으로 실행되어야 함
    }

    @Test
    public void testDisplayWithNullTextPane() {
        try {
            scoreScreen.display(null);
            fail("null textPane으로 예외가 발생해야 함");
        } catch (NullPointerException e) {
            // 예상된 동작
        }
    }

    @Test
    public void testDisplayMultipleTimes() {
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        scoreScreen.display(textPane);
        scoreScreen.display(textPane);
        // 여러 번 호출해도 안전해야 함
    }

    // === 3. 리플렉션을 통한 private 필드 테스트 ===
    @Test
    public void testScreenControllerField() throws Exception {
        Field screenControllerField = score.class.getDeclaredField("screenController");
        screenControllerField.setAccessible(true);
        Object sc = screenControllerField.get(scoreScreen);
        assertNotNull("screenController가 초기화되어야 함", sc);
    }

    @Test
    public void testCurrentTextPaneField() throws Exception {
        Field currentTextPaneField = score.class.getDeclaredField("currentTextPane");
        currentTextPaneField.setAccessible(true);
        Object currentTextPane = currentTextPaneField.get(scoreScreen);
        // currentTextPane은 display 호출 전까지 null일 수 있음
    }

    @Test
    public void testGameSettingsField() throws Exception {
        Field gameSettingsField = score.class.getDeclaredField("gameSettings");
        gameSettingsField.setAccessible(true);
        Object gameSettings = gameSettingsField.get(scoreScreen);
        assertNotNull("gameSettings가 초기화되어야 함", gameSettings);
    }

    @Test
    public void testScoreManagerField() throws Exception {
        Field scoreManagerField = score.class.getDeclaredField("scoreManager");
        scoreManagerField.setAccessible(true);
        Object scoreManager = scoreManagerField.get(scoreScreen);
        assertNotNull("scoreManager가 초기화되어야 함", scoreManager);
    }

    @Test
    public void testCurrentGameModeFilterField() throws Exception {
        Field currentGameModeFilterField = score.class.getDeclaredField("currentGameModeFilter");
        currentGameModeFilterField.setAccessible(true);
        String currentGameModeFilter = (String) currentGameModeFilterField.get(scoreScreen);
        assertNotNull("currentGameModeFilter가 초기화되어야 함", currentGameModeFilter);
        assertEquals("기본값은 ITEM이어야 함", "ITEM", currentGameModeFilter);
    }

    @Test
    public void testModeTabButtonsField() throws Exception {
        Field modeTabButtonsField = score.class.getDeclaredField("modeTabButtons");
        modeTabButtonsField.setAccessible(true);
        Object modeTabButtons = modeTabButtonsField.get(scoreScreen);
        // modeTabButtons는 buildUI 호출 전까지 null일 수 있음
    }

    @Test
    public void testPageSizeConstant() throws Exception {
        Field pageSizeField = score.class.getDeclaredField("PAGE_SIZE");
        pageSizeField.setAccessible(true);
        Integer pageSize = (Integer) pageSizeField.get(null);
        assertNotNull("PAGE_SIZE가 정의되어야 함", pageSize);
        assertEquals("PAGE_SIZE는 10이어야 함", 10, pageSize.intValue());
    }

    @Test
    public void testRowHeightConstant() throws Exception {
        Field rowHeightField = score.class.getDeclaredField("ROW_HEIGHT");
        rowHeightField.setAccessible(true);
        Integer rowHeight = (Integer) rowHeightField.get(null);
        assertNotNull("ROW_HEIGHT가 정의되어야 함", rowHeight);
        assertEquals("ROW_HEIGHT는 40이어야 함", 40, rowHeight.intValue());
    }

    @Test
    public void testCurrentPageField() throws Exception {
        Field currentPageField = score.class.getDeclaredField("currentPage");
        currentPageField.setAccessible(true);
        Integer currentPage = (Integer) currentPageField.get(scoreScreen);
        assertNotNull("currentPage 필드가 존재해야 함", currentPage);
        assertEquals("currentPage 초기값은 0이어야 함", 0, currentPage.intValue());
    }

    // === 4. 점수 강조 관련 필드 테스트 ===
    @Test
    public void testHighlightTimerField() throws Exception {
        Field highlightTimerField = score.class.getDeclaredField("highlightTimer");
        highlightTimerField.setAccessible(true);
        Object highlightTimer = highlightTimerField.get(scoreScreen);
        // highlightTimer는 초기에 null일 수 있음
    }

    @Test
    public void testHighlightVisibleField() throws Exception {
        Field highlightVisibleField = score.class.getDeclaredField("highlightVisible");
        highlightVisibleField.setAccessible(true);
        Boolean highlightVisible = (Boolean) highlightVisibleField.get(scoreScreen);
        assertNotNull("highlightVisible 필드가 존재해야 함", highlightVisible);
    }

    @Test
    public void testHighlightedEntryField() throws Exception {
        Field highlightedEntryField = score.class.getDeclaredField("highlightedEntry");
        highlightedEntryField.setAccessible(true);
        Object highlightedEntry = highlightedEntryField.get(scoreScreen);
        // highlightedEntry는 초기에 null일 수 있음
    }

    @Test
    public void testShouldHighlightField() throws Exception {
        Field shouldHighlightField = score.class.getDeclaredField("shouldHighlight");
        shouldHighlightField.setAccessible(true);
        Boolean shouldHighlight = (Boolean) shouldHighlightField.get(scoreScreen);
        assertNotNull("shouldHighlight 필드가 존재해야 함", shouldHighlight);
    }

    @Test
    public void testShowOnlyDummyField() throws Exception {
        Field showOnlyDummyField = score.class.getDeclaredField("showOnlyDummy");
        showOnlyDummyField.setAccessible(true);
        Boolean showOnlyDummy = (Boolean) showOnlyDummyField.get(scoreScreen);
        assertNotNull("showOnlyDummy 필드가 존재해야 함", showOnlyDummy);
    }

    // === 5. 리플렉션을 통한 private 메서드 테스트 ===
    @Test
    public void testBuildSampleEntriesMethod() throws Exception {
        Method buildSampleEntriesMethod = score.class.getDeclaredMethod("buildSampleEntries");
        buildSampleEntriesMethod.setAccessible(true);
        Object entries = buildSampleEntriesMethod.invoke(scoreScreen);
        assertNotNull("buildSampleEntries가 값을 반환해야 함", entries);
        assertTrue("buildSampleEntries는 List를 반환해야 함", entries instanceof java.util.List);
    }

    @Test
    public void testBuildUIMethod() throws Exception {
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        Method buildUIMethod = score.class.getDeclaredMethod("buildUI");
        buildUIMethod.setAccessible(true);
        buildUIMethod.invoke(scoreScreen);
        // buildUI가 정상적으로 실행되어야 함
    }

    @Test
    public void testCheckForHighlightRequestMethod() throws Exception {
        Method checkForHighlightRequestMethod = score.class.getDeclaredMethod("checkForHighlightRequest");
        checkForHighlightRequestMethod.setAccessible(true);
        checkForHighlightRequestMethod.invoke(scoreScreen);
        // checkForHighlightRequest가 정상적으로 실행되어야 함
    }

    @Test
    public void testStartHighlightEffectMethod() throws Exception {
        Method startHighlightEffectMethod = score.class.getDeclaredMethod("startHighlightEffect");
        startHighlightEffectMethod.setAccessible(true);
        startHighlightEffectMethod.invoke(scoreScreen);
        // startHighlightEffect가 정상적으로 실행되어야 함
    }

    @Test
    public void testStopHighlightEffectMethod() throws Exception {
        Method stopHighlightEffectMethod = score.class.getDeclaredMethod("stopHighlightEffect");
        stopHighlightEffectMethod.setAccessible(true);
        stopHighlightEffectMethod.invoke(scoreScreen);
        // stopHighlightEffect가 정상적으로 실행되어야 함
    }

    @Test
    public void testSwitchToModeMethod() throws Exception {
        Method switchToModeMethod = score.class.getDeclaredMethod("switchToMode", String.class);
        switchToModeMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        switchToModeMethod.invoke(scoreScreen, "ITEM");
        // switchToMode가 정상적으로 실행되어야 함
    }

    @Test
    public void testSwitchToPreviousModeMethod() throws Exception {
        Method switchToPreviousModeMethod = score.class.getDeclaredMethod("switchToPreviousMode");
        switchToPreviousModeMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        switchToPreviousModeMethod.invoke(scoreScreen);
        // switchToPreviousMode가 정상적으로 실행되어야 함
    }

    @Test
    public void testSwitchToNextModeMethod() throws Exception {
        Method switchToNextModeMethod = score.class.getDeclaredMethod("switchToNextMode");
        switchToNextModeMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        switchToNextModeMethod.invoke(scoreScreen);
        // switchToNextMode가 정상적으로 실행되어야 함
    }

    @Test
    public void testRenderScoresMethod() throws Exception {
        Method renderScoresMethod = score.class.getDeclaredMethod("renderScores");
        renderScoresMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        renderScoresMethod.invoke(scoreScreen);
        // renderScores가 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateButtonStyleMethod() throws Exception {
        Method updateButtonStyleMethod = score.class.getDeclaredMethod("updateButtonStyle", JButton.class, boolean.class);
        updateButtonStyleMethod.setAccessible(true);
        JButton button = new JButton("Test");
        updateButtonStyleMethod.invoke(scoreScreen, button, true);
        updateButtonStyleMethod.invoke(scoreScreen, button, false);
        // updateButtonStyle이 정상적으로 실행되어야 함
    }

    @Test
    public void testUpdateModeTabButtonsMethod() throws Exception {
        Method updateModeTabButtonsMethod = score.class.getDeclaredMethod("updateModeTabButtons");
        updateModeTabButtonsMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        updateModeTabButtonsMethod.invoke(scoreScreen);
        // updateModeTabButtons가 정상적으로 실행되어야 함
    }

    // === 6. 모드 전환 테스트 ===
    @Test
    public void testSwitchToItemMode() throws Exception {
        Method switchToModeMethod = score.class.getDeclaredMethod("switchToMode", String.class);
        switchToModeMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        switchToModeMethod.invoke(scoreScreen, "ITEM");
        
        Field currentGameModeFilterField = score.class.getDeclaredField("currentGameModeFilter");
        currentGameModeFilterField.setAccessible(true);
        String mode = (String) currentGameModeFilterField.get(scoreScreen);
        assertEquals("모드가 ITEM으로 전환되어야 함", "ITEM", mode);
    }

    @Test
    public void testSwitchToNormalEasyMode() throws Exception {
        Method switchToModeMethod = score.class.getDeclaredMethod("switchToMode", String.class);
        switchToModeMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        switchToModeMethod.invoke(scoreScreen, "NORMAL_EASY");
        
        Field currentGameModeFilterField = score.class.getDeclaredField("currentGameModeFilter");
        currentGameModeFilterField.setAccessible(true);
        String mode = (String) currentGameModeFilterField.get(scoreScreen);
        assertEquals("모드가 NORMAL_EASY로 전환되어야 함", "NORMAL_EASY", mode);
    }

    @Test
    public void testSwitchToNormalNormalMode() throws Exception {
        Method switchToModeMethod = score.class.getDeclaredMethod("switchToMode", String.class);
        switchToModeMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        switchToModeMethod.invoke(scoreScreen, "NORMAL_NORMAL");
        
        Field currentGameModeFilterField = score.class.getDeclaredField("currentGameModeFilter");
        currentGameModeFilterField.setAccessible(true);
        String mode = (String) currentGameModeFilterField.get(scoreScreen);
        assertEquals("모드가 NORMAL_NORMAL로 전환되어야 함", "NORMAL_NORMAL", mode);
    }

    @Test
    public void testSwitchToNormalHardMode() throws Exception {
        Method switchToModeMethod = score.class.getDeclaredMethod("switchToMode", String.class);
        switchToModeMethod.setAccessible(true);
        JTextPane textPane = new JTextPane();
        scoreScreen.display(textPane);
        switchToModeMethod.invoke(scoreScreen, "NORMAL_HARD");
        
        Field currentGameModeFilterField = score.class.getDeclaredField("currentGameModeFilter");
        currentGameModeFilterField.setAccessible(true);
        String mode = (String) currentGameModeFilterField.get(scoreScreen);
        assertEquals("모드가 NORMAL_HARD로 전환되어야 함", "NORMAL_HARD", mode);
    }

    // === 7. 페이지 네비게이션 테스트 ===
    @Test
    public void testCurrentPageInitialValue() throws Exception {
        Field currentPageField = score.class.getDeclaredField("currentPage");
        currentPageField.setAccessible(true);
        Integer currentPage = (Integer) currentPageField.get(scoreScreen);
        assertEquals("currentPage 초기값은 0이어야 함", 0, currentPage.intValue());
    }

    @Test
    public void testPageSizeValue() throws Exception {
        Field pageSizeField = score.class.getDeclaredField("PAGE_SIZE");
        pageSizeField.setAccessible(true);
        Integer pageSize = (Integer) pageSizeField.get(null);
        assertTrue("PAGE_SIZE는 양수여야 함", pageSize > 0);
    }

    // === 8. 강조 효과 테스트 ===
    @Test
    public void testHighlightEffectLifecycle() throws Exception {
        Method startHighlightEffectMethod = score.class.getDeclaredMethod("startHighlightEffect");
        startHighlightEffectMethod.setAccessible(true);
        Method stopHighlightEffectMethod = score.class.getDeclaredMethod("stopHighlightEffect");
        stopHighlightEffectMethod.setAccessible(true);
        
        startHighlightEffectMethod.invoke(scoreScreen);
        stopHighlightEffectMethod.invoke(scoreScreen);
        // 강조 효과 라이프사이클이 정상적으로 동작해야 함
    }

    // === 9. 통합 테스트 ===
    @Test
    public void testScoreFullLifecycle() {
        score s = new score(screenController);
        JTextPane textPane = new JTextPane();
        s.display(textPane);
        // 전체 라이프사이클이 정상적으로 동작해야 함
    }

    @Test
    public void testScoreWithMultipleDisplayCalls() {
        JTextPane textPane1 = new JTextPane();
        JTextPane textPane2 = new JTextPane();
        scoreScreen.display(textPane1);
        scoreScreen.display(textPane2);
        // 여러 textPane으로 display를 호출해도 안전해야 함
    }

    @Test
    public void testScoreScrollPaneField() throws Exception {
        Field scoreListScrollField = score.class.getDeclaredField("scoreListScroll");
        scoreListScrollField.setAccessible(true);
        Object scoreListScroll = scoreListScrollField.get(scoreScreen);
        // scoreListScroll는 buildUI 호출 전까지 null일 수 있음
    }
}
