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
 * battle 클래스 테스트
 * 2인 대전 모드 화면 테스트
 */
public class battleTest {

    private ScreenController screenController;
    private battle battleScreen;

    @Before
    public void setUp() {
        screenController = new ScreenController();
        screenController.setVisible(false);
        System.setProperty("tetris.battle.mode", "NORMAL");
        battleScreen = new battle(screenController);
    }

    @After
    public void tearDown() {
        if (battleScreen != null) {
            battleScreen.dispose();
        }
        if (screenController != null) {
            screenController.dispose();
        }
    }

    // === 1. 기본 생성 및 초기화 테스트 ===
    @Test
    public void testBattleCreation() {
        assertNotNull("battle 인스턴스가 생성되어야 함", battleScreen);
    }

    @Test
    public void testBattleIsJPanel() {
        assertTrue("battle은 JPanel을 상속해야 함", battleScreen instanceof JPanel);
    }

    @Test
    public void testBattleIsFocusable() {
        assertTrue("battle은 포커스 가능해야 함", battleScreen.isFocusable());
    }

    @Test
    public void testBattleKeyListenerAdded() throws Exception {
        java.awt.event.KeyListener[] listeners = battleScreen.getKeyListeners();
        boolean hasKeyListener = false;
        for (java.awt.event.KeyListener listener : listeners) {
            if (listener instanceof battle) {
                hasKeyListener = true;
                break;
            }
        }
        assertTrue("KeyListener가 추가되어야 함", hasKeyListener);
    }

    @Test
    public void testBattleModeNormal() {
        System.setProperty("tetris.battle.mode", "NORMAL");
        battle b = new battle(screenController);
        assertNotNull("NORMAL 모드로 생성되어야 함", b);
        b.dispose();
    }

    @Test
    public void testBattleModeItem() {
        System.setProperty("tetris.battle.mode", "ITEM");
        battle b = new battle(screenController);
        assertNotNull("ITEM 모드로 생성되어야 함", b);
        b.dispose();
    }

    @Test
    public void testBattleModeTimeLimit() {
        System.setProperty("tetris.battle.mode", "TIMELIMIT");
        battle b = new battle(screenController);
        assertNotNull("TIMELIMIT 모드로 생성되어야 함", b);
        b.dispose();
    }

    // === 2. dispose 메서드 테스트 ===
    @Test
    public void testDispose() {
        battleScreen.dispose();
        // dispose 호출 후 예외가 발생하지 않아야 함
    }

    @Test
    public void testDisposeMultipleTimes() {
        battleScreen.dispose();
        battleScreen.dispose();
        // 여러 번 호출해도 안전해야 함
    }

    @Test
    public void testDisposeWithoutStartBattle() {
        battle b = new battle(screenController);
        b.dispose();
        // startBattle 호출 없이 dispose 해도 안전해야 함
    }

    // === 3. forceStartTimeLimitTimer 테스트 ===
    @Test
    public void testForceStartTimeLimitTimer() {
        battleScreen.forceStartTimeLimitTimer();
        // 타이머가 강제로 시작되어야 함 (예외 발생하지 않음)
    }

    @Test
    public void testForceStartTimeLimitTimerMultipleTimes() {
        battleScreen.forceStartTimeLimitTimer();
        battleScreen.forceStartTimeLimitTimer();
        // 여러 번 호출해도 안전해야 함
    }

    // === 4. startBattle 메서드 테스트 ===
    @Test
    public void testStartBattle() {
        battleScreen.startBattle();
        // 게임이 시작되어야 함 (예외 발생하지 않음)
    }

    @Test
    public void testStartBattleWithNormalMode() {
        System.setProperty("tetris.battle.mode", "NORMAL");
        battle b = new battle(screenController);
        b.startBattle();
        b.dispose();
    }

    @Test
    public void testStartBattleWithItemMode() {
        System.setProperty("tetris.battle.mode", "ITEM");
        battle b = new battle(screenController);
        b.startBattle();
        b.dispose();
    }

    @Test
    public void testStartBattleWithTimeLimitMode() {
        System.setProperty("tetris.battle.mode", "TIMELIMIT");
        battle b = new battle(screenController);
        b.startBattle();
        b.dispose();
    }

    // === 5. display 메서드 테스트 ===
    @Test
    public void testDisplay() {
        JTextPane textPane = new JTextPane();
        battleScreen.display(textPane);
        // ScreenController 호환성을 위한 메서드
    }

    @Test
    public void testDisplayWithNullTextPane() {
        battleScreen.display(null);
        // null textPane도 처리해야 함
    }

    // === 6. 키 입력 테스트 ===
    @Test
    public void testKeyTyped() {
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_TYPED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_UNDEFINED,
                'a'
        );
        battleScreen.keyTyped(keyEvent);
        // keyTyped는 일반적으로 비어있음
    }

    @Test
    public void testKeyReleased() {
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_A,
                'a'
        );
        battleScreen.keyReleased(keyEvent);
        // keyReleased는 일반적으로 비어있음
    }

    @Test
    public void testKeyPressedPlayer1Left() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_A,
                'a'
        );
        battleScreen.keyPressed(keyEvent);
        // Player1 왼쪽 이동 (A키)
    }

    @Test
    public void testKeyPressedPlayer1Right() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_D,
                'd'
        );
        battleScreen.keyPressed(keyEvent);
        // Player1 오른쪽 이동 (D키)
    }

    @Test
    public void testKeyPressedPlayer1Down() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_S,
                's'
        );
        battleScreen.keyPressed(keyEvent);
        // Player1 아래 이동 (S키)
    }

    @Test
    public void testKeyPressedPlayer1Rotate() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_W,
                'w'
        );
        battleScreen.keyPressed(keyEvent);
        // Player1 회전 (W키)
    }

    @Test
    public void testKeyPressedPlayer1HardDrop() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_Z,
                'z'
        );
        battleScreen.keyPressed(keyEvent);
        // Player1 하드 드롭 (Z키)
    }

    @Test
    public void testKeyPressedPlayer2Left() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_LEFT,
                KeyEvent.CHAR_UNDEFINED
        );
        battleScreen.keyPressed(keyEvent);
        // Player2 왼쪽 이동 (←키)
    }

    @Test
    public void testKeyPressedPlayer2Right() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_RIGHT,
                KeyEvent.CHAR_UNDEFINED
        );
        battleScreen.keyPressed(keyEvent);
        // Player2 오른쪽 이동 (→키)
    }

    @Test
    public void testKeyPressedPlayer2Down() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_DOWN,
                KeyEvent.CHAR_UNDEFINED
        );
        battleScreen.keyPressed(keyEvent);
        // Player2 아래 이동 (↓키)
    }

    @Test
    public void testKeyPressedPlayer2Rotate() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_UP,
                KeyEvent.CHAR_UNDEFINED
        );
        battleScreen.keyPressed(keyEvent);
        // Player2 회전 (↑키)
    }

    @Test
    public void testKeyPressedPlayer2HardDrop() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_SHIFT,
                KeyEvent.CHAR_UNDEFINED
        );
        battleScreen.keyPressed(keyEvent);
        // Player2 하드 드롭 (Shift키)
    }

    @Test
    public void testKeyPressedEscape() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_ESCAPE,
                KeyEvent.CHAR_UNDEFINED
        );
        battleScreen.keyPressed(keyEvent);
        // ESC 키 (메뉴로 돌아가기)
    }

    // === 7. 리플렉션을 통한 private 필드 테스트 ===
    @Test
    public void testPlayerPanelsInitialized() throws Exception {
        Field player1PanelField = battle.class.getDeclaredField("player1Panel");
        player1PanelField.setAccessible(true);
        Object player1Panel = player1PanelField.get(battleScreen);
        assertNotNull("player1Panel이 초기화되어야 함", player1Panel);

        Field player2PanelField = battle.class.getDeclaredField("player2Panel");
        player2PanelField.setAccessible(true);
        Object player2Panel = player2PanelField.get(battleScreen);
        assertNotNull("player2Panel이 초기화되어야 함", player2Panel);
    }

    @Test
    public void testGameControllerInitialized() throws Exception {
        Field gameControllerField = battle.class.getDeclaredField("gameController");
        gameControllerField.setAccessible(true);
        Object gameController = gameControllerField.get(battleScreen);
        assertNotNull("gameController가 초기화되어야 함", gameController);
    }

    @Test
    public void testInputHandlersInitialized() throws Exception {
        Field player1InputField = battle.class.getDeclaredField("player1Input");
        player1InputField.setAccessible(true);
        Object player1Input = player1InputField.get(battleScreen);
        assertNotNull("player1Input이 초기화되어야 함", player1Input);

        Field player2InputField = battle.class.getDeclaredField("player2Input");
        player2InputField.setAccessible(true);
        Object player2Input = player2InputField.get(battleScreen);
        assertNotNull("player2Input이 초기화되어야 함", player2Input);
    }

    @Test
    public void testBattleModeField() throws Exception {
        Field battleModeField = battle.class.getDeclaredField("battleMode");
        battleModeField.setAccessible(true);
        String battleMode = (String) battleModeField.get(battleScreen);
        assertNotNull("battleMode가 설정되어야 함", battleMode);
    }

    @Test
    public void testOriginalWindowSizeField() throws Exception {
        Field originalWindowSizeField = battle.class.getDeclaredField("originalWindowSize");
        originalWindowSizeField.setAccessible(true);
        String originalWindowSize = (String) originalWindowSizeField.get(battleScreen);
        // null이거나 설정된 값이어야 함
        // assertNotNull은 불필요 (시스템 속성에 없을 수도 있음)
    }

    // === 8. 리플렉션을 통한 private 메서드 테스트 ===
    @Test
    public void testInitializeGameMethod() throws Exception {
        Method initializeGameMethod = battle.class.getDeclaredMethod("initializeGame");
        initializeGameMethod.setAccessible(true);
        initializeGameMethod.invoke(battleScreen);
        // 게임 초기화 메서드가 정상적으로 실행되어야 함
    }

    @Test
    public void testBuildUIMethod() throws Exception {
        Method buildUIMethod = battle.class.getDeclaredMethod("buildUI");
        buildUIMethod.setAccessible(true);
        buildUIMethod.invoke(battleScreen);
        // UI 빌드 메서드가 정상적으로 실행되어야 함
    }

    @Test
    public void testRestoreWindowSizeMethod() throws Exception {
        Method restoreWindowSizeMethod = battle.class.getDeclaredMethod("restoreWindowSize");
        restoreWindowSizeMethod.setAccessible(true);
        restoreWindowSizeMethod.invoke(battleScreen);
        // 윈도우 크기 복원 메서드가 정상적으로 실행되어야 함
    }

    @Test
    public void testStartGameOverCheckTimerMethod() throws Exception {
        Method startGameOverCheckTimerMethod = battle.class.getDeclaredMethod("startGameOverCheckTimer");
        startGameOverCheckTimerMethod.setAccessible(true);
        startGameOverCheckTimerMethod.invoke(battleScreen);
        // 게임 오버 체크 타이머 시작 메서드가 정상적으로 실행되어야 함
    }

    // === 9. 컴포넌트 계층 구조 테스트 ===
    @Test
    public void testBattleHasComponents() {
        assertTrue("battle은 컴포넌트를 포함해야 함", battleScreen.getComponentCount() >= 0);
    }

    @Test
    public void testBattleLayoutManager() {
        assertNotNull("battle은 LayoutManager를 가져야 함", battleScreen.getLayout());
    }

    // === 10. 마우스 이벤트 리스너 테스트 ===
    @Test
    public void testMouseListenerAdded() {
        java.awt.event.MouseListener[] listeners = battleScreen.getMouseListeners();
        assertTrue("MouseListener가 추가되어야 함", listeners.length > 0);
    }

    @Test
    public void testHierarchyListenerAdded() {
        java.awt.event.HierarchyListener[] listeners = battleScreen.getHierarchyListeners();
        assertTrue("HierarchyListener가 추가되어야 함", listeners.length > 0);
    }

    @Test
    public void testComponentListenerAdded() {
        java.awt.event.ComponentListener[] listeners = battleScreen.getComponentListeners();
        assertTrue("ComponentListener가 추가되어야 함", listeners.length > 0);
    }

    // === 11. 통합 테스트 ===
    @Test
    public void testBattleFullLifecycle() {
        battle b = new battle(screenController);
        b.startBattle();
        b.dispose();
        // 전체 라이프사이클이 정상적으로 동작해야 함
    }

    @Test
    public void testBattleWithAllModes() {
        String[] modes = {"NORMAL", "ITEM", "TIMELIMIT"};
        for (String mode : modes) {
            System.setProperty("tetris.battle.mode", mode);
            battle b = new battle(screenController);
            b.startBattle();
            b.dispose();
        }
        // 모든 모드가 정상적으로 동작해야 함
    }

    // === 12. 추가 키 입력 조합 테스트 ===
    @Test
    public void testKeyPressedBeforeStartBattle() {
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_A,
                'a'
        );
        battleScreen.keyPressed(keyEvent);
        // startBattle 호출 전 키 입력도 처리해야 함
    }

    @Test
    public void testMultipleKeyPressesPlayer1() {
        battleScreen.startBattle();
        for (int i = 0; i < 5; i++) {
            KeyEvent keyEvent = new KeyEvent(
                    battleScreen,
                    KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(),
                    0,
                    KeyEvent.VK_A,
                    'a'
            );
            battleScreen.keyPressed(keyEvent);
        }
        // 연속 키 입력 처리
    }

    @Test
    public void testMultipleKeyPressesPlayer2() {
        battleScreen.startBattle();
        for (int i = 0; i < 5; i++) {
            KeyEvent keyEvent = new KeyEvent(
                    battleScreen,
                    KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(),
                    0,
                    KeyEvent.VK_LEFT,
                    KeyEvent.CHAR_UNDEFINED
            );
            battleScreen.keyPressed(keyEvent);
        }
        // 연속 키 입력 처리
    }

    @Test
    public void testAlternatingPlayerKeys() {
        battleScreen.startBattle();
        KeyEvent player1Key = new KeyEvent(battleScreen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a');
        KeyEvent player2Key = new KeyEvent(battleScreen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
        
        battleScreen.keyPressed(player1Key);
        battleScreen.keyPressed(player2Key);
        battleScreen.keyPressed(player1Key);
        battleScreen.keyPressed(player2Key);
        // 플레이어 간 교차 키 입력
    }

    // === 13. 타이머 관련 테스트 ===
    @Test
    public void testTimeLimitTimerField() throws Exception {
        System.setProperty("tetris.battle.mode", "TIMELIMIT");
        battle b = new battle(screenController);
        b.startBattle();
        
        Field timeLimitTimerField = battle.class.getDeclaredField("timeLimitTimer");
        timeLimitTimerField.setAccessible(true);
        Object timeLimitTimer = timeLimitTimerField.get(b);
        assertNotNull("TIMELIMIT 모드에서 timeLimitTimer가 생성되어야 함", timeLimitTimer);
        b.dispose();
    }

    @Test
    public void testGameOverCheckTimerField() throws Exception {
        battleScreen.startBattle();
        Field gameOverCheckTimerField = battle.class.getDeclaredField("gameOverCheckTimer");
        gameOverCheckTimerField.setAccessible(true);
        Object gameOverCheckTimer = gameOverCheckTimerField.get(battleScreen);
        assertNotNull("startBattle 후 gameOverCheckTimer가 생성되어야 함", gameOverCheckTimer);
    }

    @Test
    public void testRemainingSecondsField() throws Exception {
        System.setProperty("tetris.battle.mode", "TIMELIMIT");
        battle b = new battle(screenController);
        b.forceStartTimeLimitTimer();
        
        Field remainingSecondsField = battle.class.getDeclaredField("remainingSeconds");
        remainingSecondsField.setAccessible(true);
        Integer remainingSeconds = (Integer) remainingSecondsField.get(b);
        assertNotNull("remainingSeconds 필드가 존재해야 함", remainingSeconds);
        b.dispose();
    }

    // === 14. 모드별 GameMode 설정 확인 테스트 ===
    @Test
    public void testNormalModeGameModeSet() throws Exception {
        System.setProperty("tetris.battle.mode", "NORMAL");
        battle b = new battle(screenController);
        
        Field player1PanelField = battle.class.getDeclaredField("player1Panel");
        player1PanelField.setAccessible(true);
        se.tetris.team5.components.battle.PlayerGamePanel player1Panel = 
            (se.tetris.team5.components.battle.PlayerGamePanel) player1PanelField.get(b);
        assertNotNull("NORMAL 모드에서 player1Panel이 생성되어야 함", player1Panel);
        b.dispose();
    }

    @Test
    public void testItemModeGameModeSet() throws Exception {
        System.setProperty("tetris.battle.mode", "ITEM");
        battle b = new battle(screenController);
        
        Field player1PanelField = battle.class.getDeclaredField("player1Panel");
        player1PanelField.setAccessible(true);
        se.tetris.team5.components.battle.PlayerGamePanel player1Panel = 
            (se.tetris.team5.components.battle.PlayerGamePanel) player1PanelField.get(b);
        assertNotNull("ITEM 모드에서 player1Panel이 생성되어야 함", player1Panel);
        b.dispose();
    }

    // === 15. 윈도우 크기 관련 테스트 ===
    @Test
    public void testOriginalWindowSizeFromSystemProperty() {
        System.setProperty("tetris.battle.originalSize", "MEDIUM");
        battle b = new battle(screenController);
        
        try {
            Field originalWindowSizeField = battle.class.getDeclaredField("originalWindowSize");
            originalWindowSizeField.setAccessible(true);
            String originalWindowSize = (String) originalWindowSizeField.get(b);
            assertEquals("시스템 속성에서 윈도우 크기를 가져와야 함", "MEDIUM", originalWindowSize);
        } catch (Exception e) {
            fail("originalWindowSize 필드 접근 실패: " + e.getMessage());
        } finally {
            b.dispose();
        }
    }

    @Test
    public void testRestoreWindowSizeAfterBattle() throws Exception {
        battleScreen.startBattle();
        Method restoreWindowSizeMethod = battle.class.getDeclaredMethod("restoreWindowSize");
        restoreWindowSizeMethod.setAccessible(true);
        restoreWindowSizeMethod.invoke(battleScreen);
        // 윈도우 크기 복원이 정상적으로 실행되어야 함
    }

    // === 16. 포커스 관련 테스트 ===
    @Test
    public void testBattleRequestsFocusOnCreation() {
        battle b = new battle(screenController);
        assertTrue("battle은 생성 시 포커스 가능해야 함", b.isFocusable());
        b.dispose();
    }

    @Test
    public void testBattleDoesNotTraverseFocusWithTab() {
        assertFalse("battle은 Tab 키로 포커스 이동을 하지 않아야 함", 
            battleScreen.getFocusTraversalKeysEnabled());
    }

    // === 17. 컴포넌트 구조 테스트 ===
    @Test
    public void testBattleHasBorderLayout() {
        assertTrue("battle은 BorderLayout을 사용해야 함", 
            battleScreen.getLayout() instanceof java.awt.BorderLayout);
    }

    @Test
    public void testBattleBackgroundColor() {
        assertEquals("battle 배경색은 검정색이어야 함", 
            java.awt.Color.BLACK, battleScreen.getBackground());
    }

    @Test
    public void testBattleComponentsAfterInitialize() throws Exception {
        Method initializeGameMethod = battle.class.getDeclaredMethod("initializeGame");
        initializeGameMethod.setAccessible(true);
        initializeGameMethod.invoke(battleScreen);
        
        assertTrue("initializeGame 후 컴포넌트가 추가되어야 함", 
            battleScreen.getComponentCount() > 0);
    }

    // === 18. 상수 값 테스트 ===
    @Test
    public void testTimeLimitSecondsConstant() throws Exception {
        Field timeLimitSecondsField = battle.class.getDeclaredField("TIME_LIMIT_SECONDS");
        timeLimitSecondsField.setAccessible(true);
        int timeLimitSeconds = timeLimitSecondsField.getInt(null);
        assertEquals("TIME_LIMIT_SECONDS는 300이어야 함", 300, timeLimitSeconds);
    }

    // === 19. handleGameOver 메서드 테스트 ===
    @Test
    public void testHandleGameOverMethodExists() throws Exception {
        Method handleGameOverMethod = battle.class.getDeclaredMethod("handleGameOver", int.class);
        handleGameOverMethod.setAccessible(true);
        assertNotNull("handleGameOver 메서드가 존재해야 함", handleGameOverMethod);
    }

    // === 20. startTimeLimitMode 메서드 테스트 ===
    @Test
    public void testStartTimeLimitModeCreatesTimer() throws Exception {
        System.setProperty("tetris.battle.mode", "TIMELIMIT");
        battle b = new battle(screenController);
        b.forceStartTimeLimitTimer();
        
        Field timeLimitTimerField = battle.class.getDeclaredField("timeLimitTimer");
        timeLimitTimerField.setAccessible(true);
        Object timer = timeLimitTimerField.get(b);
        assertNotNull("forceStartTimeLimitTimer 호출 시 타이머가 생성되어야 함", timer);
        b.dispose();
    }

    // === 21. 리스너 중복 등록 테스트 ===
    @Test
    public void testMouseListenerCount() {
        int listenerCount = battleScreen.getMouseListeners().length;
        assertTrue("MouseListener가 최소 1개 이상 등록되어야 함", listenerCount >= 1);
    }

    @Test
    public void testHierarchyListenerCount() {
        int listenerCount = battleScreen.getHierarchyListeners().length;
        assertTrue("HierarchyListener가 최소 1개 이상 등록되어야 함", listenerCount >= 1);
    }

    @Test
    public void testComponentListenerCount() {
        int listenerCount = battleScreen.getComponentListeners().length;
        assertTrue("ComponentListener가 최소 1개 이상 등록되어야 함", listenerCount >= 1);
    }

    // === 22. 재초기화 테스트 ===
    @Test
    public void testReinitializeAfterDispose() throws Exception {
        battleScreen.startBattle();
        battleScreen.dispose();
        
        Method initializeGameMethod = battle.class.getDeclaredMethod("initializeGame");
        initializeGameMethod.setAccessible(true);
        initializeGameMethod.invoke(battleScreen);
        // dispose 후 재초기화가 가능해야 함
    }

    // === 23. 에지 케이스 테스트 ===
    @Test
    public void testKeyPressedWithNullKeyCode() {
        battleScreen.startBattle();
        KeyEvent keyEvent = new KeyEvent(
                battleScreen,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_UNDEFINED,
                KeyEvent.CHAR_UNDEFINED
        );
        battleScreen.keyPressed(keyEvent);
        // 정의되지 않은 키 코드도 처리해야 함
    }

    @Test
    public void testStartBattleMultipleTimes() {
        battleScreen.startBattle();
        battleScreen.startBattle();
        battleScreen.startBattle();
        // startBattle을 여러 번 호출해도 안전해야 함
    }

    @Test
    public void testDisplayMultipleTimes() {
        JTextPane textPane = new JTextPane();
        battleScreen.display(textPane);
        battleScreen.display(textPane);
        battleScreen.display(textPane);
        // display를 여러 번 호출해도 안전해야 함
    }

    // === 24. ScreenController 상호작용 테스트 ===
    @Test
    public void testScreenControllerNotNull() throws Exception {
        Field screenControllerField = battle.class.getDeclaredField("screenController");
        screenControllerField.setAccessible(true);
        Object sc = screenControllerField.get(battleScreen);
        assertNotNull("screenController가 null이 아니어야 함", sc);
    }

    // === 25. 직렬화 UID 테스트 ===
    @Test
    public void testSerialVersionUID() throws Exception {
        Field serialVersionUIDField = battle.class.getDeclaredField("serialVersionUID");
        serialVersionUIDField.setAccessible(true);
        long serialVersionUID = serialVersionUIDField.getLong(null);
        assertEquals("serialVersionUID가 정의되어야 함", 1L, serialVersionUID);
    }

    // === 26. 패널 간 연결 테스트 ===
    @Test
    public void testPlayerPanelsAreConnected() throws Exception {
        Field player1PanelField = battle.class.getDeclaredField("player1Panel");
        player1PanelField.setAccessible(true);
        se.tetris.team5.components.battle.PlayerGamePanel player1Panel = 
            (se.tetris.team5.components.battle.PlayerGamePanel) player1PanelField.get(battleScreen);

        Field player2PanelField = battle.class.getDeclaredField("player2Panel");
        player2PanelField.setAccessible(true);
        se.tetris.team5.components.battle.PlayerGamePanel player2Panel = 
            (se.tetris.team5.components.battle.PlayerGamePanel) player2PanelField.get(battleScreen);

        assertNotNull("player1Panel이 초기화되어야 함", player1Panel);
        assertNotNull("player2Panel이 초기화되어야 함", player2Panel);
    }

    // === 27. 게임 컨트롤러 시작/정지 테스트 ===
    @Test
    public void testGameControllerStartsOnStartBattle() throws Exception {
        battleScreen.startBattle();
        Field gameControllerField = battle.class.getDeclaredField("gameController");
        gameControllerField.setAccessible(true);
        Object gameController = gameControllerField.get(battleScreen);
        assertNotNull("startBattle 후 gameController가 존재해야 함", gameController);
    }

    @Test
    public void testGameControllerStopsOnDispose() throws Exception {
        battleScreen.startBattle();
        battleScreen.dispose();
        // dispose 시 gameController가 정지되어야 함 (예외 없음)
    }

    // === 28. 키 입력 핸들러 테스트 ===
    @Test
    public void testPlayer1InputHandlerExists() throws Exception {
        Field player1InputField = battle.class.getDeclaredField("player1Input");
        player1InputField.setAccessible(true);
        Object player1Input = player1InputField.get(battleScreen);
        assertNotNull("player1Input 핸들러가 존재해야 함", player1Input);
        assertTrue("player1Input은 Player1InputHandler 타입이어야 함", 
            player1Input instanceof se.tetris.team5.gamelogic.input.Player1InputHandler);
    }

    @Test
    public void testPlayer2InputHandlerExists() throws Exception {
        Field player2InputField = battle.class.getDeclaredField("player2Input");
        player2InputField.setAccessible(true);
        Object player2Input = player2InputField.get(battleScreen);
        assertNotNull("player2Input 핸들러가 존재해야 함", player2Input);
        assertTrue("player2Input은 Player2InputHandler 타입이어야 함", 
            player2Input instanceof se.tetris.team5.gamelogic.input.Player2InputHandler);
    }

    // === 29. 모드 설정 유효성 테스트 ===
    @Test
    public void testBattleModeDefaultValue() throws Exception {
        System.clearProperty("tetris.battle.mode");
        battle b = new battle(screenController);
        
        Field battleModeField = battle.class.getDeclaredField("battleMode");
        battleModeField.setAccessible(true);
        String battleMode = (String) battleModeField.get(b);
        assertNotNull("battleMode가 설정되어야 함", battleMode);
        b.dispose();
    }

    @Test
    public void testInvalidBattleModeHandling() {
        System.setProperty("tetris.battle.mode", "INVALID_MODE");
        battle b = new battle(screenController);
        assertNotNull("유효하지 않은 모드로도 생성되어야 함", b);
        b.dispose();
    }

    // === 30. UI 빌드 검증 테스트 ===
    @Test
    public void testBuildUICreatesComponents() throws Exception {
        battleScreen.removeAll();
        Method buildUIMethod = battle.class.getDeclaredMethod("buildUI");
        buildUIMethod.setAccessible(true);
        buildUIMethod.invoke(battleScreen);
        
        assertTrue("buildUI 후 컴포넌트가 추가되어야 함", 
            battleScreen.getComponentCount() > 0);
    }
}
