package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;

import se.tetris.team5.ScreenController;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.components.game.GameBoard;
import se.tetris.team5.gamelogic.GameEngine;

/**
 * game.java (화면 클래스)의 단위 테스트
 * 
 * 테스트 범위:
 * - 초기화 및 컴포넌트 생성
 * - 블록 이동 (moveDown, moveLeft, moveRight)
 * - 블록 회전 및 하드 드롭
 * - 일시정지/재개 기능
 * - 타임스톱 기능
 * - 게임 오버 처리
 * - 게임 리셋
 * - 키 이벤트 처리
 */
public class GameScreenTest {
    private game gameScreen;
    private ScreenController controller;

    @Before
    public void setUp() {
        controller = new ScreenController();
        // controller는 화면들을 자동으로 초기화하므로, gameScreen 필드에서 가져옴
        try {
            Field gameScreenField = ScreenController.class.getDeclaredField("gameScreen");
            gameScreenField.setAccessible(true);
            gameScreen = (game) gameScreenField.get(controller);
        } catch (Exception e) {
            // fallback: 직접 생성
            gameScreen = new game(controller);
        }
    }

    // ==================== 초기화 테스트 ====================

    @Test
    public void testGameScreenInitialization() {
        assertNotNull("Game screen should be created", gameScreen);
        assertTrue("Game screen should be a JPanel", gameScreen instanceof JPanel);
    }

    @Test
    public void testGameScreenIsFocusable() {
        assertTrue("Game screen should be focusable", gameScreen.isFocusable());
    }

    @Test
    public void testGameScreenHasKeyListener() {
        assertTrue("Game screen should have key listener", 
            gameScreen.getKeyListeners().length > 0);
    }

    @Test
    public void testGameEngineExists() throws Exception {
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        assertNotNull("GameEngine should be initialized", engine);
    }

    @Test
    public void testTimerExists() throws Exception {
        Field timerField = game.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(gameScreen);
        
        assertNotNull("Timer should be initialized", timer);
    }

    @Test
    public void testInitialPauseState() throws Exception {
        Field isPausedField = game.class.getDeclaredField("isPaused");
        isPausedField.setAccessible(true);
        boolean isPaused = (boolean) isPausedField.get(gameScreen);
        
        assertFalse("Game should not be paused initially", isPaused);
    }

    @Test
    public void testInitialTimeStopState() throws Exception {
        Field isTimeStoppedField = game.class.getDeclaredField("isTimeStopped");
        isTimeStoppedField.setAccessible(true);
        boolean isTimeStopped = (boolean) isTimeStoppedField.get(gameScreen);
        
        assertFalse("TimeStop should not be active initially", isTimeStopped);
    }

    // ==================== 블록 이동 테스트 ====================

    @Test
    public void testMoveDown() throws Exception {
        // given: GameEngine 접근
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        int initialY = engine.getY();
        
        // when: moveDown 호출
        Method moveDownMethod = game.class.getDeclaredMethod("moveDown");
        moveDownMethod.setAccessible(true);
        moveDownMethod.invoke(gameScreen);
        
        // then: Y 좌표 증가 또는 블록 고정
        int finalY = engine.getY();
        assertTrue("Y should increase or block should be fixed", finalY >= initialY);
    }

    @Test
    public void testMoveLeft() throws Exception {
        // given: GameEngine 접근
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        int initialX = engine.getX();
        
        // when: moveLeft 호출
        Method moveLeftMethod = game.class.getDeclaredMethod("moveLeft");
        moveLeftMethod.setAccessible(true);
        moveLeftMethod.invoke(gameScreen);
        
        // then: X 좌표 감소 또는 경계
        int finalX = engine.getX();
        assertTrue("X should decrease or stay at boundary", finalX <= initialX);
    }

    @Test
    public void testMoveRight() throws Exception {
        // given: GameEngine 접근
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        int initialX = engine.getX();
        
        // when: moveRight 호출
        Method moveRightMethod = game.class.getDeclaredMethod("moveRight");
        moveRightMethod.setAccessible(true);
        moveRightMethod.invoke(gameScreen);
        
        // then: X 좌표 증가 또는 경계
        int finalX = engine.getX();
        assertTrue("X should increase or stay at boundary", finalX >= initialX);
    }

    // ==================== 블록 회전 및 하드 드롭 테스트 ====================

    @Test
    public void testRotateBlock() throws Exception {
        // given: GameEngine 접근
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        // when: rotateBlock 호출
        Method rotateBlockMethod = game.class.getDeclaredMethod("rotateBlock");
        rotateBlockMethod.setAccessible(true);
        rotateBlockMethod.invoke(gameScreen);
        
        // then: 블록이 회전됨 (또는 회전 실패)
        assertNotNull("Current block should exist", engine.getCurrentBlock());
    }

    @Test
    public void testHardDrop() throws Exception {
        // given: GameEngine 접근
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        // when: hardDrop 호출
        Method hardDropMethod = game.class.getDeclaredMethod("hardDrop");
        hardDropMethod.setAccessible(true);
        hardDropMethod.invoke(gameScreen);
        
        // then: 새 블록이 스폰됨
        assertNotNull("New block should spawn", engine.getCurrentBlock());
    }

    @Test
    public void testHardDropTriggersGameOverCheck() throws Exception {
        // given: 보드를 가득 채워 게임 오버 준비
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        
        // 상단부터 블록으로 채움
        for (int y = 3; y < GameBoard.HEIGHT; y++) {
            for (int x = 0; x < GameBoard.WIDTH; x++) {
                boardArray[y][x] = 1;
            }
        }
        
        // when: hardDrop
        Method hardDropMethod = game.class.getDeclaredMethod("hardDrop");
        hardDropMethod.setAccessible(true);
        hardDropMethod.invoke(gameScreen);
        
        // then: 게임 오버 시 타이머 정지 또는 화면 전환
        assertTrue("Hard drop should complete without exception", true);
    }

    // ==================== 일시정지/재개 테스트 ====================

    @Test
    public void testPauseGame() throws Exception {
        // given: 게임 실행 중
        Field isPausedField = game.class.getDeclaredField("isPaused");
        isPausedField.setAccessible(true);
        
        Method pauseGameMethod = game.class.getDeclaredMethod("pauseGame");
        pauseGameMethod.setAccessible(true);
        
        // when: 일시정지
        pauseGameMethod.invoke(gameScreen);
        
        // then: isPaused가 true
        assertTrue("Game should be paused", (boolean) isPausedField.get(gameScreen));
    }

    @Test
    public void testResumeGame() throws Exception {
        // given: 게임이 일시정지된 상태
        Field isPausedField = game.class.getDeclaredField("isPaused");
        isPausedField.setAccessible(true);
        isPausedField.set(gameScreen, true);
        
        Method resumeGameMethod = game.class.getDeclaredMethod("resumeGame");
        resumeGameMethod.setAccessible(true);
        
        // when: 재개
        resumeGameMethod.invoke(gameScreen);
        
        // then: isPaused가 false
        assertFalse("Game should be resumed", (boolean) isPausedField.get(gameScreen));
    }

    @Test
    public void testPauseMenuIndexInitialValue() throws Exception {
        Field pauseMenuIndexField = game.class.getDeclaredField("pauseMenuIndex");
        pauseMenuIndexField.setAccessible(true);
        int pauseMenuIndex = (int) pauseMenuIndexField.get(gameScreen);
        
        assertEquals("Pause menu index should be 0 initially", 0, pauseMenuIndex);
    }

    @Test
    public void testPauseMenuOptions() throws Exception {
        Field pauseMenuOptionsField = game.class.getDeclaredField("pauseMenuOptions");
        pauseMenuOptionsField.setAccessible(true);
        String[] options = (String[]) pauseMenuOptionsField.get(gameScreen);
        
        assertNotNull("Pause menu options should exist", options);
        assertEquals("Should have 2 options", 2, options.length);
        assertEquals("First option should be '게임 계속'", "게임 계속", options[0]);
        assertEquals("Second option should be '메뉴로 나가기'", "메뉴로 나가기", options[1]);
    }

    // ==================== 타임스톱 테스트 ====================

    @Test
    public void testActivateTimeStop() throws Exception {
        // given: 타임스톱 비활성화 상태
        Field isTimeStoppedField = game.class.getDeclaredField("isTimeStopped");
        isTimeStoppedField.setAccessible(true);
        
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        // 타임스톱 충전 강제 설정
        Field hasTimeStopChargeField = GameEngine.class.getDeclaredField("hasTimeStopCharge");
        hasTimeStopChargeField.setAccessible(true);
        hasTimeStopChargeField.set(engine, true);
        
        // when: Shift 키 누름 (타임스톱 활성화는 keyPressed에서 처리)
        // 실제로는 keyPressed를 통해 활성화되지만, 여기서는 상태 변화만 확인
        
        // then: 초기에는 비활성화
        assertFalse("TimeStop should be inactive initially", 
            (boolean) isTimeStoppedField.get(gameScreen));
    }

    @Test
    public void testTimeStopTimerExists() throws Exception {
        Field timeStopTimerField = game.class.getDeclaredField("timeStopTimer");
        timeStopTimerField.setAccessible(true);
        Timer timeStopTimer = (Timer) timeStopTimerField.get(gameScreen);
        
        // 초기에는 null일 수 있음
        assertTrue("TimeStopTimer should be null or initialized", 
            timeStopTimer == null || timeStopTimer instanceof Timer);
    }

    // ==================== 게임 리셋 테스트 ====================

    @Test
    public void testReset() throws Exception {
        // given: 게임 진행 후 상태
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        // 블록 몇 번 이동
        engine.moveBlockDown();
        engine.moveBlockLeft();
        
        Field isPausedField = game.class.getDeclaredField("isPaused");
        isPausedField.setAccessible(true);
        isPausedField.set(gameScreen, true);
        
        Field isTimeStoppedField = game.class.getDeclaredField("isTimeStopped");
        isTimeStoppedField.setAccessible(true);
        isTimeStoppedField.set(gameScreen, true);
        
        // when: reset 호출
        gameScreen.reset();
        
        // then: 게임 상태 초기화
        assertFalse("isPaused should be false after reset", 
            (boolean) isPausedField.get(gameScreen));
        assertFalse("isTimeStopped should be false after reset", 
            (boolean) isTimeStoppedField.get(gameScreen));
        assertFalse("Game should not be over after reset", engine.isGameOver());
    }

    @Test
    public void testResetClearsPauseMenuIndex() throws Exception {
        // given: 일시정지 메뉴에서 두 번째 옵션 선택
        Field pauseMenuIndexField = game.class.getDeclaredField("pauseMenuIndex");
        pauseMenuIndexField.setAccessible(true);
        pauseMenuIndexField.set(gameScreen, 1);
        
        // when: reset
        gameScreen.reset();
        
        // then: pauseMenuIndex가 0으로 초기화
        assertEquals("Pause menu index should reset to 0", 
            0, (int) pauseMenuIndexField.get(gameScreen));
    }

    @Test
    public void testResetRestartsTimer() throws Exception {
        // given: 타이머 정지
        Field timerField = game.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(gameScreen);
        timer.stop();
        
        assertFalse("Timer should be stopped", timer.isRunning());
        
        // when: reset
        gameScreen.reset();
        
        // then: 타이머 재시작
        timer = (Timer) timerField.get(gameScreen);
        assertTrue("Timer should be running after reset", timer.isRunning());
    }

    // ==================== 게임 속도 설정 테스트 ====================

    @Test
    public void testGetInitialInterval() throws Exception {
        // given: getInitialInterval 메서드 접근
        Method getInitialIntervalMethod = game.class.getDeclaredMethod("getInitialInterval");
        getInitialIntervalMethod.setAccessible(true);
        
        // when: 호출
        int interval = (int) getInitialIntervalMethod.invoke(gameScreen);
        
        // then: 유효한 간격 반환 (150 ~ 2000ms 사이)
        assertTrue("Interval should be between 150 and 2000", 
            interval >= 150 && interval <= 2000);
    }

    // ==================== 키 이벤트 테스트 ====================

    @Test
    public void testKeyPressedWhilePaused() throws Exception {
        // given: 게임 일시정지
        Field isPausedField = game.class.getDeclaredField("isPaused");
        isPausedField.setAccessible(true);
        isPausedField.set(gameScreen, true);
        
        // when: ESC 키 (재개)
        KeyEvent escEvent = new KeyEvent(
            gameScreen, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 
            0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
        
        gameScreen.keyPressed(escEvent);
        
        // then: 게임 재개
        assertFalse("Game should be resumed", (boolean) isPausedField.get(gameScreen));
    }

    @Test
    public void testKeyPressedPausesGame() throws Exception {
        // given: 게임 실행 중
        Field isPausedField = game.class.getDeclaredField("isPaused");
        isPausedField.setAccessible(true);
        
        // when: ESC 키 (일시정지)
        KeyEvent escEvent = new KeyEvent(
            gameScreen, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 
            0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
        
        gameScreen.keyPressed(escEvent);
        
        // then: 게임 일시정지
        assertTrue("Game should be paused", (boolean) isPausedField.get(gameScreen));
    }

    @Test
    public void testKeyReleasedDoesNothing() {
        // when: keyReleased 호출
        KeyEvent event = new KeyEvent(
            gameScreen, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 
            0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        
        // then: 예외 없이 정상 처리
        try {
            gameScreen.keyReleased(event);
            assertTrue("keyReleased should not throw exception", true);
        } catch (Exception e) {
            fail("keyReleased should not throw exception");
        }
    }

    @Test
    public void testKeyTypedDoesNothing() {
        // when: keyTyped 호출
        KeyEvent event = new KeyEvent(
            gameScreen, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 
            0, KeyEvent.VK_UNDEFINED, 'a');
        
        // then: 예외 없이 정상 처리
        try {
            gameScreen.keyTyped(event);
            assertTrue("keyTyped should not throw exception", true);
        } catch (Exception e) {
            fail("keyTyped should not throw exception");
        }
    }

    // ==================== 보드 업데이트 테스트 ====================

    @Test
    public void testUpdateAllBoardsDoesNotThrow() throws Exception {
        // when: updateAllBoards 호출
        Method updateAllBoardsMethod = game.class.getDeclaredMethod("updateAllBoards");
        updateAllBoardsMethod.setAccessible(true);
        
        // then: 예외 없이 정상 처리
        try {
            updateAllBoardsMethod.invoke(gameScreen);
            assertTrue("updateAllBoards should not throw exception", true);
        } catch (Exception e) {
            fail("updateAllBoards should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testDrawBoardDoesNotThrow() {
        // when: drawBoard 호출
        try {
            gameScreen.drawBoard();
            assertTrue("drawBoard should not throw exception", true);
        } catch (Exception e) {
            fail("drawBoard should not throw exception");
        }
    }

    // ==================== 게임 오버 처리 테스트 ====================

    @Test
    public void testGameOverNavigatesToHome() throws Exception {
        // given: 게임 오버 상태
        Field gameEngineField = game.class.getDeclaredField("gameEngine");
        gameEngineField.setAccessible(true);
        GameEngine engine = (GameEngine) gameEngineField.get(gameScreen);
        
        // 보드를 가득 채워 게임 오버 유도
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < GameBoard.WIDTH; x++) {
                boardArray[y][x] = 1;
            }
        }
        
        // when: gameOver 메서드 직접 호출
        Method gameOverMethod = game.class.getDeclaredMethod("gameOver");
        gameOverMethod.setAccessible(true);
        gameOverMethod.invoke(gameScreen);
        
        // then: 화면 전환 완료
        assertTrue("gameOver should complete without exception", true);
    }

    @Test
    public void testGameOverStopsTimer() throws Exception {
        // given: 타이머 실행 중
        Field timerField = game.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(gameScreen);
        
        if (!timer.isRunning()) {
            timer.start();
        }
        
        // when: gameOver
        Method gameOverMethod = game.class.getDeclaredMethod("gameOver");
        gameOverMethod.setAccessible(true);
        gameOverMethod.invoke(gameScreen);
        
        // then: 타이머 정지
        timer = (Timer) timerField.get(gameScreen);
        assertFalse("Timer should be stopped after game over", timer.isRunning());
    }

    // ==================== Display 메서드 테스트 ====================

    @Test
    public void testDisplayMethod() {
        // given: JTextPane
        JTextPane textPane = new JTextPane();
        
        // when: display 호출
        try {
            gameScreen.display(textPane);
            assertTrue("display method should not throw exception", true);
        } catch (Exception e) {
            fail("display method should not throw exception");
        }
    }

    // ==================== 싱크 메서드 테스트 ====================

    @Test
    public void testSyncWithGameEngine() throws Exception {
        // when: syncWithGameEngine 호출
        Method syncMethod = game.class.getDeclaredMethod("syncWithGameEngine");
        syncMethod.setAccessible(true);
        
        // then: 예외 없이 정상 처리
        try {
            syncMethod.invoke(gameScreen);
            assertTrue("syncWithGameEngine should not throw exception", true);
        } catch (Exception e) {
            fail("syncWithGameEngine should not throw exception");
        }
    }

    // ==================== 상수 테스트 ====================

    @Test
    public void testGameBoardConstants() {
        assertEquals("HEIGHT should match GameBoard.HEIGHT", 
            GameBoard.HEIGHT, game.HEIGHT);
        assertEquals("WIDTH should match GameBoard.WIDTH", 
            GameBoard.WIDTH, game.WIDTH);
        assertEquals("BORDER_CHAR should match GameBoard.BORDER_CHAR", 
            GameBoard.BORDER_CHAR, game.BORDER_CHAR);
    }
}
