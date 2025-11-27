package se.tetris.team5.components.battle;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.GameMode;
import se.tetris.team5.components.game.GameBoard;

import java.lang.reflect.Field;

/**
 * PlayerGamePanel 컴포넌트 테스트
 * 대전 모드의 개별 플레이어 패널 기능 검증
 */
public class PlayerGamePanelTest {

    private PlayerGamePanel playerPanel;

    @Before
    public void setUp() {
        playerPanel = new PlayerGamePanel();
    }

    /**
     * 테스트 1: 패널 생성 시 GameEngine 초기화
     */
    @Test
    public void testPlayerPanel_InitializesGameEngine() {
        GameEngine engine = playerPanel.getGameEngine();
        assertNotNull("GameEngine이 초기화되어야 함", engine);
    }

    /**
     * 테스트 2: 패널 생성 시 GameBoard 초기화
     */
    @Test
    public void testPlayerPanel_InitializesGameBoard() {
        GameBoard gameBoard = playerPanel.getGameBoard();
        assertNotNull("GameBoard가 초기화되어야 함", gameBoard);
    }

    /**
     * 테스트 3: startGame 호출 시 새 게임 시작
     */
    @Test
    public void testPlayerPanel_StartGameInitializesBoard() {
        playerPanel.startGame();
        
        GameEngine engine = playerPanel.getGameEngine();
        int[][] board = engine.getBoardManager().getBoard();
        
        // 현재 블록만 존재하는지 확인
        int occupiedCells = countOccupiedCells(board);
        assertTrue("게임 시작 시 게임판이 거의 비어있어야 함", occupiedCells <= 4);
        
        // 현재 블록이 존재하는지 확인
        assertNotNull("현재 블록이 null이 아니어야 함", engine.getCurrentBlock());
        assertNotNull("다음 블록이 null이 아니어야 함", engine.getNextBlock());
    }

    /**
     * 테스트 4: 게임 일시정지/재개 기능
     */
    @Test
    public void testPlayerPanel_PauseAndResume() throws Exception {
        playerPanel.startGame();
        
        // 일시정지
        playerPanel.pauseGame();
        
        // 타이머가 정지되었는지 확인
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer gameTimer = (javax.swing.Timer) gameTimerField.get(playerPanel);
        
        assertFalse("게임 타이머가 정지되어야 함", gameTimer.isRunning());
        
        // 재개
        playerPanel.resumeGame();
        assertTrue("게임 타이머가 다시 시작되어야 함", gameTimer.isRunning());
    }

    /**
     * 테스트 5: 게임 정지 기능
     */
    @Test
    public void testPlayerPanel_StopGame() throws Exception {
        playerPanel.startGame();
        playerPanel.stopGame();
        
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer gameTimer = (javax.swing.Timer) gameTimerField.get(playerPanel);
        
        assertFalse("게임 정지 후 타이머가 멈춰야 함", gameTimer.isRunning());
    }

    /**
     * 테스트 6: 타이머 라벨 업데이트 (시간제한 모드)
     */
    @Test
    public void testPlayerPanel_UpdateTimerLabel() {
        playerPanel.updateTimerLabel("05:00");
        // 예외 없이 실행되면 성공
        assertTrue("타이머 라벨 업데이트가 정상 작동해야 함", true);
    }

    /**
     * 테스트 7: 게임 오버 상태 확인
     */
    @Test
    public void testPlayerPanel_CheckGameOverState() throws Exception {
        playerPanel.startGame();
        
        GameEngine engine = playerPanel.getGameEngine();
        
        // 게임 오버 상태로 변경
        Field gameOverField = GameEngine.class.getDeclaredField("gameOver");
        gameOverField.setAccessible(true);
        gameOverField.set(engine, true);
        
        assertTrue("게임 오버 상태가 반영되어야 함", playerPanel.isGameOver());
    }

    /**
     * 테스트 8: UI 업데이트가 정상 작동하는지 확인
     */
    @Test
    public void testPlayerPanel_UIUpdateWithoutErrors() throws Exception {
        playerPanel.startGame();
        
        // UI 업데이트 호출 (예외 없이 실행되어야 함)
        playerPanel.updateGameUI();
        
        // 예외 없이 실행되면 성공
        assertTrue("UI 업데이트가 정상 작동해야 함", true);
    }

    /**
     * 테스트 9: 아이템 모드 설정 확인
     */
    @Test
    public void testPlayerPanel_ItemModeConfiguration() {
        PlayerGamePanel itemPanel = new PlayerGamePanel();
        itemPanel.getGameEngine().setGameMode(GameMode.ITEM);
        
        assertEquals("아이템 모드가 설정되어야 함", 
                     GameMode.ITEM, 
                     itemPanel.getGameEngine().getGameMode());
    }

    /**
     * 테스트 10: 두 개의 타이머 독립 작동 (게임 로직 + UI 업데이트)
     */
    @Test
    public void testPlayerPanel_DualTimerOperation() throws Exception {
        playerPanel.startGame();
        
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer gameTimer = (javax.swing.Timer) gameTimerField.get(playerPanel);
        
        Field uiTimerField = PlayerGamePanel.class.getDeclaredField("uiTimer");
        uiTimerField.setAccessible(true);
        javax.swing.Timer uiTimer = (javax.swing.Timer) uiTimerField.get(playerPanel);
        
        assertNotNull("게임 로직 타이머가 존재해야 함", gameTimer);
        assertNotNull("UI 업데이트 타이머가 존재해야 함", uiTimer);
        
        assertTrue("게임 로직 타이머가 실행 중이어야 함", gameTimer.isRunning());
        assertTrue("UI 업데이트 타이머가 실행 중이어야 함", uiTimer.isRunning());
        
        // UI 타이머가 더 빠른 간격이어야 함 (60fps = 16ms)
        assertTrue("UI 타이머가 더 빠른 업데이트 간격을 가져야 함", 
                   uiTimer.getDelay() < gameTimer.getDelay());
    }

    // ===== Helper Methods =====

    private int countOccupiedCells(int[][] board) {
        int count = 0;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == 1 || board[r][c] == 2) {
                    count++;
                }
            }
        }
        return count;
    }
}
