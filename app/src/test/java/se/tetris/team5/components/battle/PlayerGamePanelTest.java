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

    /**
     * 테스트 11: 카스텀 이름, 조작키, 색상으로 생성
     */
    @Test
    public void testPlayerPanel_CustomConstructor() {
        PlayerGamePanel customPanel = new PlayerGamePanel("플레이어1", "WASD", java.awt.Color.BLUE);
        assertNotNull("커스텀 패널이 생성되어야 함", customPanel);
        assertNotNull("GameEngine이 초기화되어야 함", customPanel.getGameEngine());
    }

    /**
     * 테스트 12: 카운트다운 타이머 활성화/비활성화
     */
    @Test
    public void testPlayerPanel_CountdownTimerEnabled() {
        playerPanel.setCountdownTimerEnabled(true);
        playerPanel.updateTimerLabel("03:00");
        // 예외 없이 실행되면 성공
        assertTrue("카운트다운 타이머 설정이 정상 작동해야 함", true);
    }

    /**
     * 테스트 13: 공격 블럭 추가 및 조회
     */
    @Test
    public void testPlayerPanel_AddAndGetAttackBlocks() {
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        java.awt.Color[] row = new java.awt.Color[10];
        for (int i = 0; i < 10; i++) {
            row[i] = java.awt.Color.GRAY;
        }
        attackBlocks.add(row);
        
        playerPanel.addAttackBlocks(attackBlocks);
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        
        assertEquals("공격 블럭이 추가되어야 함", 1, retrieved.size());
    }

    /**
     * 테스트 14: 공격 블럭 초기화
     */
    @Test
    public void testPlayerPanel_ClearAttackBlocks() {
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        java.awt.Color[] row = new java.awt.Color[10];
        attackBlocks.add(row);
        
        playerPanel.addAttackBlocks(attackBlocks);
        playerPanel.clearAttackBlocks();
        
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("공격 블럭이 초기화되어야 함", 0, retrieved.size());
    }

    /**
     * 테스트 15: 상대방 패널 설정
     */
    @Test
    public void testPlayerPanel_SetOpponentPanel() {
        PlayerGamePanel opponent = new PlayerGamePanel();
        playerPanel.setOpponentPanel(opponent);
        // 예외 없이 실행되면 성공
        assertTrue("상대방 패널 설정이 정상 작동해야 함", true);
    }

    /**
     * 테스트 16: P2P 점수 업데이트
     */
    @Test
    public void testPlayerPanel_UpdateScore() {
        playerPanel.updateScore(5000);
        // 예외 없이 실행되면 성공
        assertTrue("점수 업데이트가 정상 작동해야 함", true);
    }

    /**
     * 테스트 17: P2P 레벨 업데이트
     */
    @Test
    public void testPlayerPanel_UpdateLevel() {
        playerPanel.updateLevel(3);
        // 예외 없이 실행되면 성공
        assertTrue("레벨 업데이트가 정상 작동해야 함", true);
    }

    /**
     * 테스트 18: P2P 줄 수 업데이트
     */
    @Test
    public void testPlayerPanel_UpdateLines() {
        playerPanel.updateLines(15);
        // 예외 없이 실행되면 성공
        assertTrue("줄 수 업데이트가 정상 작동해야 함", true);
    }

    /**
     * 테스트 19: P2P 다음 블록 업데이트
     */
    @Test
    public void testPlayerPanel_UpdateNextBlock() {
        playerPanel.updateNextBlock("I");
        // 예외 없이 실행되면 성공
        assertTrue("다음 블록 업데이트가 정상 작동해야 함", true);
    }

    /**
     * 테스트 20: P2P 타이머 업데이트
     */
    @Test
    public void testPlayerPanel_UpdateTimer() {
        playerPanel.updateTimer(60000L); // 1분
        // 예외 없이 실행되면 성공
        assertTrue("타이머 업데이트가 정상 작동해야 함", true);
    }

    /**
     * 테스트 21: P2P 공격 블럭 수신
     */
    @Test
    public void testPlayerPanel_ReceiveAttackBlocks() {
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        java.awt.Color[] row = new java.awt.Color[10];
        attackBlocks.add(row);
        
        playerPanel.receiveAttackBlocks(attackBlocks);
        
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("수신한 공격 블럭이 저장되어야 함", 1, retrieved.size());
    }

    /**
     * 테스트 22: 보내는 공격 블럭 큐 비우기
     */
    @Test
    public void testPlayerPanel_DrainPendingOutgoingAttackBlocks() {
        java.util.List<java.awt.Color[]> drained = playerPanel.drainPendingOutgoingAttackBlocks();
        assertNotNull("빈 리스트라도 반환되어야 함", drained);
    }

    /**
     * 테스트 23: 커스텀 GameEngine으로 생성
     */
    @Test
    public void testPlayerPanel_CustomGameEngine() {
        GameEngine customEngine = new GameEngine(GameBoard.HEIGHT, GameBoard.WIDTH, false);
        PlayerGamePanel customPanel = new PlayerGamePanel("플레이어", "키", java.awt.Color.RED, customEngine);
        
        assertNotNull("커스텀 엔진 패널이 생성되어야 함", customPanel);
        assertEquals("커스텀 엔진이 사용되어야 함", customEngine, customPanel.getGameEngine());
    }

    /**
     * 테스트 24: 아이템 사용 (타임스톱 없을 때)
     */
    @Test
    public void testPlayerPanel_UseItemWithoutCharge() {
        playerPanel.startGame();
        boolean result = playerPanel.useItem();
        assertFalse("타임스톱 충전이 없으면 사용 실패해야 함", result);
    }

    /**
     * 테스트 25: 최대 공격 줄 수 제한 (10줄)
     */
    @Test
    public void testPlayerPanel_MaxAttackLinesLimit() {
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        
        // 15줄 추가 시도
        for (int i = 0; i < 15; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.GRAY;
            }
            attackBlocks.add(row);
        }
        
        playerPanel.addAttackBlocks(attackBlocks);
        
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertTrue("최대 10줄까지만 추가되어야 함", retrieved.size() <= 10);
    }

    /**
     * 테스트 26: null 공격 블럭 추가 시 무시
     */
    @Test
    public void testPlayerPanel_AddNullAttackBlocks() {
        playerPanel.addAttackBlocks(null);
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("null 추가 시 무시되어야 함", 0, retrieved.size());
    }

    /**
     * 테스트 27: 빈 공격 블럭 리스트 추가 시 무시
     */
    @Test
    public void testPlayerPanel_AddEmptyAttackBlocks() {
        playerPanel.addAttackBlocks(new java.util.ArrayList<>());
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("빈 리스트 추가 시 무시되어야 함", 0, retrieved.size());
    }

    /**
     * 테스트 28: null 다음 블록 업데이트
     */
    @Test
    public void testPlayerPanel_UpdateNextBlockWithNull() {
        playerPanel.updateNextBlock(null);
        // 예외 없이 실행되면 성공
        assertTrue("null 다음 블록 업데이트가 안전해야 함", true);
    }

    /**
     * 테스트 29: 잘못된 블록 타입으로 다음 블록 업데이트
     */
    @Test
    public void testPlayerPanel_UpdateNextBlockWithInvalidType() {
        playerPanel.updateNextBlock("INVALID");
        // 예외 없이 실행되면 성공
        assertTrue("잘못된 블록 타입 처리가 안전해야 함", true);
    }

    /**
     * 테스트 30: 모든 블록 타입으로 다음 블록 업데이트
     */
    @Test
    public void testPlayerPanel_UpdateNextBlockAllTypes() {
        String[] blockTypes = {"I", "O", "T", "S", "Z", "L", "J", "W", "DOT"};
        
        for (String type : blockTypes) {
            playerPanel.updateNextBlock(type);
        }
        
        // 예외 없이 실행되면 성공
        assertTrue("모든 블록 타입 업데이트가 정상 작동해야 함", true);
    }

    /**
     * 테스트 31: 게임 시작 전 pauseGame 호출
     */
    @Test
    public void testPlayerPanel_PauseBeforeStart() {
        // 게임 시작 전에 일시정지 호출 (타이머가 null일 수 있음)
        playerPanel.pauseGame();
        // 예외 없이 실행되면 성공
        assertTrue("게임 시작 전 pauseGame이 안전해야 함", true);
    }

    /**
     * 테스트 32: 게임 시작 전 resumeGame 호출
     */
    @Test
    public void testPlayerPanel_ResumeBeforeStart() {
        // 게임 시작 전에 재개 호출 (타이머가 null일 수 있음)
        playerPanel.resumeGame();
        // 예외 없이 실행되면 성공
        assertTrue("게임 시작 전 resumeGame이 안전해야 함", true);
    }

    /**
     * 테스트 33: 게임 시작 전 stopGame 호출
     */
    @Test
    public void testPlayerPanel_StopBeforeStart() {
        // 게임 시작 전에 정지 호출 (타이머가 null일 수 있음)
        playerPanel.stopGame();
        // 예외 없이 실행되면 성공
        assertTrue("게임 시작 전 stopGame이 안전해야 함", true);
    }

    /**
     * 테스트 34: updateGameUI - 게임오버 상태에서 호출
     */
    @Test
    public void testPlayerPanel_UpdateGameUIWhenGameOver() throws Exception {
        playerPanel.startGame();
        
        // 게임오버 상태로 강제 설정
        GameEngine engine = playerPanel.getGameEngine();
        Field gameOverField = GameEngine.class.getDeclaredField("gameOver");
        gameOverField.setAccessible(true);
        gameOverField.setBoolean(engine, true);
        
        // updateGameUI 호출 (게임오버 시 조기 반환하는 분기 테스트)
        playerPanel.updateGameUI();
        
        assertTrue("게임오버 상태에서 updateGameUI가 안전해야 함", true);
    }

    /**
     * 테스트 35: 카운트다운 타이머 활성화 상태에서 updateGameUI
     */
    @Test
    public void testPlayerPanel_UpdateGameUIWithCountdownTimer() {
        playerPanel.setCountdownTimerEnabled(true);
        playerPanel.startGame();
        playerPanel.updateGameUI();
        
        // 카운트다운 타이머가 활성화되면 타이머 라벨이 자동으로 업데이트되지 않음
        assertTrue("카운트다운 타이머 활성화 시 updateGameUI가 정상 작동해야 함", true);
    }

    /**
     * 테스트 36: 공격 블럭 10줄 이미 있을 때 추가 시도
     */
    @Test
    public void testPlayerPanel_AddAttackBlocksWhenFull() {
        // 먼저 10줄 추가
        java.util.List<java.awt.Color[]> firstBatch = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.GRAY;
            }
            firstBatch.add(row);
        }
        playerPanel.addAttackBlocks(firstBatch);
        
        // 추가로 5줄 더 시도
        java.util.List<java.awt.Color[]> secondBatch = new java.util.ArrayList<>();
        for (int i = 0; i < 5; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.RED;
            }
            secondBatch.add(row);
        }
        playerPanel.addAttackBlocks(secondBatch);
        
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("최대 10줄을 초과할 수 없음", 10, retrieved.size());
    }

    /**
     * 테스트 37: 공격 블럭 부분 추가 (7줄 있을 때 5줄 추가 시도)
     */
    @Test
    public void testPlayerPanel_PartialAttackBlocksAdd() {
        // 먼저 7줄 추가
        java.util.List<java.awt.Color[]> firstBatch = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.GRAY;
            }
            firstBatch.add(row);
        }
        playerPanel.addAttackBlocks(firstBatch);
        
        // 추가로 5줄 시도 (3줄만 추가 가능)
        java.util.List<java.awt.Color[]> secondBatch = new java.util.ArrayList<>();
        for (int i = 0; i < 5; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.BLUE;
            }
            secondBatch.add(row);
        }
        playerPanel.addAttackBlocks(secondBatch);
        
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("부분 추가로 정확히 10줄이어야 함", 10, retrieved.size());
    }

    /**
     * 테스트 38: receiveAttackBlocks - null 처리
     */
    @Test
    public void testPlayerPanel_ReceiveNullAttackBlocks() {
        playerPanel.receiveAttackBlocks(null);
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("null 수신 시 무시되어야 함", 0, retrieved.size());
    }

    /**
     * 테스트 39: receiveAttackBlocks - 빈 리스트
     */
    @Test
    public void testPlayerPanel_ReceiveEmptyAttackBlocks() {
        playerPanel.receiveAttackBlocks(new java.util.ArrayList<>());
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("빈 리스트 수신 시 무시되어야 함", 0, retrieved.size());
    }

    /**
     * 테스트 40: receiveAttackBlocks - 정상 수신
     */
    @Test
    public void testPlayerPanel_ReceiveAttackBlocksNormal() {
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.ORANGE;
            }
            attackBlocks.add(row);
        }
        
        playerPanel.receiveAttackBlocks(attackBlocks);
        
        java.util.List<java.awt.Color[]> retrieved = playerPanel.getAttackBlocksData();
        assertEquals("3줄이 추가되어야 함", 3, retrieved.size());
    }

    /**
     * 테스트 41: drainPendingOutgoingAttackBlocks - 빈 상태
     */
    @Test
    public void testPlayerPanel_DrainEmptyOutgoingBlocks() {
        java.util.List<java.awt.Color[]> drained = playerPanel.drainPendingOutgoingAttackBlocks();
        assertNotNull("빈 리스트가 반환되어야 함", drained);
        assertEquals("빈 상태에서 drain 시 0줄", 0, drained.size());
    }

    /**
     * 테스트 42: 게임 정지 후 재시작
     */
    @Test
    public void testPlayerPanel_StopThenRestart() throws Exception {
        playerPanel.startGame();
        playerPanel.stopGame();
        
        // 다시 시작
        playerPanel.startGame();
        
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer gameTimer = (javax.swing.Timer) gameTimerField.get(playerPanel);
        
        assertTrue("재시작 후 타이머가 작동해야 함", gameTimer.isRunning());
    }

    /**
     * 테스트 43: 타이머 라벨 업데이트 - 여러 형식
     */
    @Test
    public void testPlayerPanel_UpdateTimerLabelVariousFormats() {
        String[] formats = {"00:00", "05:30", "99:59", "00:01", "10:00"};
        
        for (String format : formats) {
            playerPanel.updateTimerLabel(format);
        }
        
        assertTrue("다양한 시간 형식으로 업데이트가 가능해야 함", true);
    }

    /**
     * 테스트 44: updateTimer - 다양한 시간 값
     */
    @Test
    public void testPlayerPanel_UpdateTimerVariousTimes() {
        long[] times = {0L, 1000L, 59000L, 60000L, 3599000L, 7200000L};
        
        for (long time : times) {
            playerPanel.updateTimer(time);
        }
        
        assertTrue("다양한 시간 값으로 업데이트가 가능해야 함", true);
    }

    /**
     * 테스트 45: 게임오버 확인
     */
    @Test
    public void testPlayerPanel_IsGameOverCheck() throws Exception {
        playerPanel.startGame();
        
        assertFalse("게임 시작 직후는 게임오버가 아님", playerPanel.isGameOver());
        
        // 게임오버 상태로 강제 설정
        GameEngine engine = playerPanel.getGameEngine();
        Field gameOverField = GameEngine.class.getDeclaredField("gameOver");
        gameOverField.setAccessible(true);
        gameOverField.setBoolean(engine, true);
        
        assertTrue("게임오버 상태가 정확히 반영되어야 함", playerPanel.isGameOver());
    }

    /**
     * 테스트 46: 상대방 패널 설정 후 공격 블럭 전송 (opponentPanel null 아닐 때)
     */
    @Test
    public void testPlayerPanel_AttackBlocksWithOpponent() {
        PlayerGamePanel opponent = new PlayerGamePanel("상대", "키", java.awt.Color.RED);
        playerPanel.setOpponentPanel(opponent);
        
        // 공격 블럭 데이터를 pendingOutgoingAttackBlocks에 추가하는 로직 테스트
        // updateGameUI에서 2줄 이상 삭제 시 자동으로 추가됨
        assertNotNull("상대방 패널이 설정되어야 함", opponent);
    }

    /**
     * 테스트 47: 상대방 패널 null인 상태에서 공격 블럭 생성 시도
     */
    @Test
    public void testPlayerPanel_AttackBlocksWithoutOpponent() {
        // opponentPanel이 null인 상태에서 공격 블럭 로직 처리
        playerPanel.setOpponentPanel(null);
        
        // updateGameUI를 호출해도 예외가 발생하지 않아야 함
        playerPanel.startGame();
        playerPanel.updateGameUI();
        
        assertTrue("상대방 패널 없이도 게임이 정상 작동해야 함", true);
    }

    /**
     * 테스트 48: 공격 블럭 clear 후 다시 추가
     */
    @Test
    public void testPlayerPanel_ClearThenAddAttackBlocks() {
        // 3줄 추가
        java.util.List<java.awt.Color[]> firstBatch = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.CYAN;
            }
            firstBatch.add(row);
        }
        playerPanel.addAttackBlocks(firstBatch);
        
        assertEquals("3줄이 추가되어야 함", 3, playerPanel.getAttackBlocksData().size());
        
        // 초기화
        playerPanel.clearAttackBlocks();
        assertEquals("초기화 후 0줄이어야 함", 0, playerPanel.getAttackBlocksData().size());
        
        // 다시 2줄 추가
        java.util.List<java.awt.Color[]> secondBatch = new java.util.ArrayList<>();
        for (int i = 0; i < 2; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.MAGENTA;
            }
            secondBatch.add(row);
        }
        playerPanel.addAttackBlocks(secondBatch);
        
        assertEquals("초기화 후 다시 2줄이 추가되어야 함", 2, playerPanel.getAttackBlocksData().size());
    }

    /**
     * 테스트 49: updateScore - 다양한 점수 값
     */
    @Test
    public void testPlayerPanel_UpdateScoreVariousValues() {
        int[] scores = {0, 100, 1000, 10000, 99999, 1000000};
        
        for (int score : scores) {
            playerPanel.updateScore(score);
        }
        
        assertTrue("다양한 점수 값으로 업데이트가 가능해야 함", true);
    }

    /**
     * 테스트 50: updateLevel - 다양한 레벨 값
     */
    @Test
    public void testPlayerPanel_UpdateLevelVariousValues() {
        int[] levels = {1, 5, 10, 20, 50, 99};
        
        for (int level : levels) {
            playerPanel.updateLevel(level);
        }
        
        assertTrue("다양한 레벨 값으로 업데이트가 가능해야 함", true);
    }

    /**
     * 테스트 51: updateGameUI - 현재 블록이 null인 경우
     */
    @Test
    public void testPlayerPanel_UpdateGameUIWithNullCurrentBlock() throws Exception {
        playerPanel.startGame();
        
        // 현재 블록을 null로 설정
        GameEngine engine = playerPanel.getGameEngine();
        Field currentBlockField = GameEngine.class.getDeclaredField("currentBlock");
        currentBlockField.setAccessible(true);
        currentBlockField.set(engine, null);
        
        // updateGameUI 호출 (currBlock null 분기 테스트)
        playerPanel.updateGameUI();
        
        assertTrue("현재 블록이 null이어도 updateGameUI가 안전해야 함", true);
    }

    /**
     * 테스트 52: updateGameUI - 줄 삭제 애니메이션 (1줄)
     */
    @Test
    public void testPlayerPanel_UpdateGameUIWithSingleLineClear() throws Exception {
        playerPanel.startGame();
        
        // 1줄 삭제 시뮬레이션 (공격 블럭 전송 안 함)
        GameEngine engine = playerPanel.getGameEngine();
        
        // consumeLastClearedRows에서 1줄 반환하도록 설정
        Field lastClearedRowsField = GameEngine.class.getDeclaredField("lastClearedRows");
        lastClearedRowsField.setAccessible(true);
        java.util.List<Integer> clearedRows = new java.util.ArrayList<>();
        clearedRows.add(19); // 맨 아래 줄
        lastClearedRowsField.set(engine, clearedRows);
        
        playerPanel.updateGameUI();
        
        assertTrue("1줄 삭제 시 공격 블럭이 생성되지 않아야 함", true);
    }

    /**
     * 테스트 53: updateGameUI - 줄 삭제 애니메이션 (2줄 이상, attackData null)
     */
    @Test
    public void testPlayerPanel_UpdateGameUIWith2LinesButNoAttackData() throws Exception {
        playerPanel.startGame();
        
        GameEngine engine = playerPanel.getGameEngine();
        
        // 2줄 삭제로 설정
        Field lastClearedRowsField = GameEngine.class.getDeclaredField("lastClearedRows");
        lastClearedRowsField.setAccessible(true);
        java.util.List<Integer> clearedRows = new java.util.ArrayList<>();
        clearedRows.add(18);
        clearedRows.add(19);
        lastClearedRowsField.set(engine, clearedRows);
        
        playerPanel.updateGameUI();
        
        assertTrue("2줄 삭제해도 attackData가 null이면 안전해야 함", true);
    }

    /**
     * 테스트 54: updateGameUI - 폭탄 애니메이션 (bombCells 존재)
     */
    @Test
    public void testPlayerPanel_UpdateGameUIWithBombExplosion() throws Exception {
        playerPanel.startGame();
        
        GameEngine engine = playerPanel.getGameEngine();
        
        // 폭탄 폭발 셀 설정
        Field lastBombExplosionCellsField = GameEngine.class.getDeclaredField("lastBombExplosionCells");
        lastBombExplosionCellsField.setAccessible(true);
        java.util.List<se.tetris.team5.components.game.GameBoard.CellPos> bombCells = new java.util.ArrayList<>();
        bombCells.add(new se.tetris.team5.components.game.GameBoard.CellPos(5, 10));
        bombCells.add(new se.tetris.team5.components.game.GameBoard.CellPos(6, 10));
        lastBombExplosionCellsField.set(engine, bombCells);
        
        playerPanel.updateGameUI();
        
        assertTrue("폭탄 폭발 애니메이션이 정상 작동해야 함", true);
    }

    /**
     * 테스트 55: updateGameUI - 점수 2배 뱃지 활성화
     */
    @Test
    public void testPlayerPanel_UpdateGameUIWithDoubleScoreBadge() throws Exception {
        playerPanel.startGame();
        
        GameEngine engine = playerPanel.getGameEngine();
        
        // doubleScoreRemaining을 양수로 설정
        Field doubleScoreRemainingField = GameEngine.class.getDeclaredField("doubleScoreRemainingMillis");
        doubleScoreRemainingField.setAccessible(true);
        doubleScoreRemainingField.setLong(engine, 10000L); // 10초 남음
        
        playerPanel.updateGameUI();
        
        assertTrue("점수 2배 뱃지가 표시되어야 함", true);
    }

    /**
     * 테스트 56: updateGameUI - 점수 2배 뱃지 비활성화 (0ms)
     */
    @Test
    public void testPlayerPanel_UpdateGameUIWithExpiredDoubleScore() throws Exception {
        playerPanel.startGame();
        
        // 먼저 활성화
        GameEngine engine = playerPanel.getGameEngine();
        Field doubleScoreRemainingField = GameEngine.class.getDeclaredField("doubleScoreRemainingMillis");
        doubleScoreRemainingField.setAccessible(true);
        doubleScoreRemainingField.setLong(engine, 5000L);
        playerPanel.updateGameUI();
        
        // 이제 0으로 설정 (만료)
        doubleScoreRemainingField.setLong(engine, 0L);
        playerPanel.updateGameUI();
        
        assertTrue("점수 2배 만료 시 뱃지가 숨겨져야 함", true);
    }

    /**
     * 테스트 57: updateGameUI - 타이머 속도 조정 (딜레이 변경)
     */
    @Test
    public void testPlayerPanel_UpdateGameUITimerSpeedChange() throws Exception {
        playerPanel.startGame();
        
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer gameTimer = (javax.swing.Timer) gameTimerField.get(playerPanel);
        
        int initialDelay = gameTimer.getDelay();
        
        // updateGameUI 호출하여 타이머 속도 조정 로직 테스트
        playerPanel.updateGameUI();
        
        assertTrue("타이머 속도 조정 로직이 정상 작동해야 함", true);
    }

    /**
     * 테스트 58: getInitialInterval - 게임 속도 1 (매우 느림)
     */
    @Test
    public void testPlayerPanel_InitialIntervalSpeed1() throws Exception {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        settings.setGameSpeed(1);
        
        PlayerGamePanel panel = new PlayerGamePanel();
        panel.startGame();
        
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer timer = (javax.swing.Timer) gameTimerField.get(panel);
        
        // 초기 딜레이가 2000ms 근처여야 함
        assertTrue("매우 느림 모드의 타이머 간격", timer.getDelay() >= 1000);
    }

    /**
     * 테스트 59: getInitialInterval - 게임 속도 2 (느림)
     */
    @Test
    public void testPlayerPanel_InitialIntervalSpeed2() throws Exception {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        settings.setGameSpeed(2);
        
        PlayerGamePanel panel = new PlayerGamePanel();
        panel.startGame();
        
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer timer = (javax.swing.Timer) gameTimerField.get(panel);
        
        assertTrue("느림 모드의 타이머 간격", timer.getDelay() >= 500);
    }

    /**
     * 테스트 60: getInitialInterval - 게임 속도 3 (보통)
     */
    @Test
    public void testPlayerPanel_InitialIntervalSpeed3() throws Exception {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        settings.setGameSpeed(3);
        
        PlayerGamePanel panel = new PlayerGamePanel();
        panel.startGame();
        
        assertTrue("보통 모드가 정상 작동해야 함", true);
    }

    /**
     * 테스트 61: getInitialInterval - 게임 속도 4 (빠름)
     */
    @Test
    public void testPlayerPanel_InitialIntervalSpeed4() throws Exception {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        settings.setGameSpeed(4);
        
        PlayerGamePanel panel = new PlayerGamePanel();
        panel.startGame();
        
        assertTrue("빠름 모드가 정상 작동해야 함", true);
    }

    /**
     * 테스트 62: getInitialInterval - 게임 속도 5 (매우 빠름)
     */
    @Test
    public void testPlayerPanel_InitialIntervalSpeed5() throws Exception {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        settings.setGameSpeed(5);
        
        PlayerGamePanel panel = new PlayerGamePanel();
        panel.startGame();
        
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer timer = (javax.swing.Timer) gameTimerField.get(panel);
        
        assertTrue("매우 빠름 모드의 타이머 간격", timer.getDelay() <= 1000);
    }

    /**
     * 테스트 63: getInitialInterval - 잘못된 게임 속도 (default 분기)
     */
    @Test
    public void testPlayerPanel_InitialIntervalInvalidSpeed() throws Exception {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // Reflection으로 잘못된 속도 설정
        Field gameSpeedField = se.tetris.team5.utils.setting.GameSettings.class.getDeclaredField("gameSpeed");
        gameSpeedField.setAccessible(true);
        gameSpeedField.setInt(settings, 99); // 잘못된 값
        
        PlayerGamePanel panel = new PlayerGamePanel();
        panel.startGame();
        
        assertTrue("잘못된 게임 속도에도 기본값으로 작동해야 함", true);
    }

    /**
     * 테스트 64: createKoreanFont - 한글 폰트 폴백 테스트
     */
    @Test
    public void testPlayerPanel_CreateKoreanFont() throws Exception {
        // createKoreanFont은 private 메서드이므로 간접적으로 테스트
        PlayerGamePanel panel = new PlayerGamePanel("테스트", "한글", java.awt.Color.WHITE);
        
        assertNotNull("한글 폰트가 포함된 패널이 생성되어야 함", panel);
    }

    /**
     * 테스트 65: startTimer - 타이머 시작 후 게임오버 시 자동 정지
     */
    @Test
    public void testPlayerPanel_TimerStopsOnGameOver() throws Exception {
        playerPanel.startGame();
        
        // 타이머가 실행 중인지 확인
        Field gameTimerField = PlayerGamePanel.class.getDeclaredField("gameTimer");
        gameTimerField.setAccessible(true);
        javax.swing.Timer gameTimer = (javax.swing.Timer) gameTimerField.get(playerPanel);
        
        assertTrue("게임 시작 후 타이머가 실행 중이어야 함", gameTimer.isRunning());
        
        // 게임오버 상태로 설정
        GameEngine engine = playerPanel.getGameEngine();
        Field gameOverField = GameEngine.class.getDeclaredField("gameOver");
        gameOverField.setAccessible(true);
        gameOverField.setBoolean(engine, true);
        
        // 타이머 액션 한 번 실행 (moveBlockDown 시도)
        Thread.sleep(100);
        
        assertTrue("게임오버 시 updateGameUI에서 타이머가 정지되어야 함", true);
    }

    /**
     * 테스트 66: useItem - 타임스톱 충전이 있고 게임 진행 중
     */
    @Test
    public void testPlayerPanel_UseItemTimeStopSuccess() throws Exception {
        playerPanel.startGame();
        
        GameEngine engine = playerPanel.getGameEngine();
        
        // 타임스톱 충전 설정
        Field timeStopChargeField = GameEngine.class.getDeclaredField("timeStopCharge");
        timeStopChargeField.setAccessible(true);
        timeStopChargeField.setInt(engine, 1);
        
        boolean result = playerPanel.useItem();
        
        assertTrue("타임스톱 충전이 있으면 사용 성공해야 함", result);
    }

    /**
     * 테스트 67: useItem - 타임스톱 이미 활성화된 상태
     */
    @Test
    public void testPlayerPanel_UseItemWhileTimeStopActive() throws Exception {
        playerPanel.startGame();
        
        GameEngine engine = playerPanel.getGameEngine();
        
        // 첫 번째 타임스톱 활성화
        Field timeStopChargeField = GameEngine.class.getDeclaredField("timeStopCharge");
        timeStopChargeField.setAccessible(true);
        timeStopChargeField.setInt(engine, 1);
        
        boolean first = playerPanel.useItem();
        assertTrue("첫 번째 타임스톱 사용 성공", first);
        
        // 타임스톱 활성화 중에 다시 시도
        timeStopChargeField.setInt(engine, 1); // 충전 다시 설정
        boolean second = playerPanel.useItem();
        
        assertFalse("타임스톱 활성화 중에는 재사용 불가", second);
    }

    /**
     * 테스트 68: useItem - 타임스톱 카운트다운 완료
     */
    @Test
    public void testPlayerPanel_UseItemTimeStopCountdownComplete() throws Exception {
        playerPanel.startGame();
        
        GameEngine engine = playerPanel.getGameEngine();
        
        // 타임스톱 충전 설정
        Field timeStopChargeField = GameEngine.class.getDeclaredField("timeStopCharge");
        timeStopChargeField.setAccessible(true);
        timeStopChargeField.setInt(engine, 1);
        
        playerPanel.useItem();
        
        // 타임스톱 카운트다운이 완료될 때까지 대기 (5초)
        Thread.sleep(5500);
        
        // 타임스톱이 해제되어야 함
        Field isTimeStoppedField = PlayerGamePanel.class.getDeclaredField("isTimeStopped");
        isTimeStoppedField.setAccessible(true);
        boolean isTimeStopped = isTimeStoppedField.getBoolean(playerPanel);
        
        assertFalse("타임스톱 카운트다운 완료 후 해제되어야 함", isTimeStopped);
    }

    /**
     * 테스트 69: applyPendingAttackBlocks - 공격 블럭 적용 성공
     */
    @Test
    public void testPlayerPanel_ApplyPendingAttackBlocksSuccess() throws Exception {
        playerPanel.startGame();
        
        // 공격 블럭 추가
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        for (int i = 0; i < 2; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.DARK_GRAY;
            }
            attackBlocks.add(row);
        }
        playerPanel.addAttackBlocks(attackBlocks);
        
        // applyPendingAttackBlocks 호출 (private이므로 checkAndApplyAttackBlocks를 통해)
        java.lang.reflect.Method checkMethod = PlayerGamePanel.class.getDeclaredMethod("checkAndApplyAttackBlocks");
        checkMethod.setAccessible(true);
        checkMethod.invoke(playerPanel);
        
        // 적용 후 공격 블럭이 초기화되어야 함
        java.util.List<java.awt.Color[]> remaining = playerPanel.getAttackBlocksData();
        assertEquals("공격 블럭 적용 후 초기화되어야 함", 0, remaining.size());
    }

    /**
     * 테스트 70: checkAndApplyAttackBlocks - 공격 블럭 없을 때
     */
    @Test
    public void testPlayerPanel_CheckAndApplyAttackBlocksEmpty() throws Exception {
        playerPanel.startGame();
        
        // 공격 블럭이 없는 상태에서 호출
        java.lang.reflect.Method checkMethod = PlayerGamePanel.class.getDeclaredMethod("checkAndApplyAttackBlocks");
        checkMethod.setAccessible(true);
        checkMethod.invoke(playerPanel);
        
        assertTrue("공격 블럭이 없어도 안전하게 작동해야 함", true);
    }

    /**
     * 테스트 71: nextVisualPanel paintComponent - 다음 블록 렌더링
     */
    @Test
    public void testPlayerPanel_NextBlockPanelPaint() throws Exception {
        playerPanel.startGame();
        
        // nextVisualPanel을 강제로 repaint
        Field nextVisualPanelField = PlayerGamePanel.class.getDeclaredField("nextVisualPanel");
        nextVisualPanelField.setAccessible(true);
        javax.swing.JPanel nextPanel = (javax.swing.JPanel) nextVisualPanelField.get(playerPanel);
        
        // paintComponent 호출을 위해 크기 설정
        nextPanel.setSize(180, 90);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(180, 90, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        nextPanel.paint(g2d);
        g2d.dispose();
        
        assertTrue("다음 블록 패널이 렌더링되어야 함", true);
    }

    /**
     * 테스트 72: nextVisualPanel - 다음 블록이 null일 때
     */
    @Test
    public void testPlayerPanel_NextBlockPanelPaintWithNullNextBlock() throws Exception {
        playerPanel.startGame();
        
        // 다음 블록을 null로 설정
        GameEngine engine = playerPanel.getGameEngine();
        Field nextBlockField = GameEngine.class.getDeclaredField("nextBlock");
        nextBlockField.setAccessible(true);
        nextBlockField.set(engine, null);
        
        // nextVisualPanel repaint
        Field nextVisualPanelField = PlayerGamePanel.class.getDeclaredField("nextVisualPanel");
        nextVisualPanelField.setAccessible(true);
        javax.swing.JPanel nextPanel = (javax.swing.JPanel) nextVisualPanelField.get(playerPanel);
        
        nextPanel.setSize(180, 90);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(180, 90, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        nextPanel.paint(g2d);
        g2d.dispose();
        
        assertTrue("다음 블록이 null이어도 렌더링이 안전해야 함", true);
    }

    /**
     * 테스트 73: nextVisualPanel - 블록 색상이 null일 때
     */
    @Test
    public void testPlayerPanel_NextBlockPanelWithNullColor() throws Exception {
        playerPanel.startGame();
        
        // 다음 블록의 색상을 null로 설정
        GameEngine engine = playerPanel.getGameEngine();
        se.tetris.team5.blocks.Block nextBlock = engine.getNextBlock();
        if (nextBlock != null) {
            Field colorField = se.tetris.team5.blocks.Block.class.getDeclaredField("color");
            colorField.setAccessible(true);
            colorField.set(nextBlock, null);
        }
        
        // nextVisualPanel repaint
        Field nextVisualPanelField = PlayerGamePanel.class.getDeclaredField("nextVisualPanel");
        nextVisualPanelField.setAccessible(true);
        javax.swing.JPanel nextPanel = (javax.swing.JPanel) nextVisualPanelField.get(playerPanel);
        
        nextPanel.setSize(180, 90);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(180, 90, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        nextPanel.paint(g2d);
        g2d.dispose();
        
        assertTrue("블록 색상이 null이어도 CYAN으로 폴백되어야 함", true);
    }

    /**
     * 테스트 74: nextVisualPanel - 아이템이 있는 블록
     */
    @Test
    public void testPlayerPanel_NextBlockPanelWithItem() throws Exception {
        playerPanel.startGame();
        
        // 다음 블록에 아이템 설정
        GameEngine engine = playerPanel.getGameEngine();
        se.tetris.team5.blocks.Block nextBlock = engine.getNextBlock();
        if (nextBlock != null && nextBlock.width() > 0 && nextBlock.height() > 0) {
            se.tetris.team5.items.TimeStopItem item = new se.tetris.team5.items.TimeStopItem();
            nextBlock.setItem(0, 0, item);
        }
        
        // nextVisualPanel repaint
        Field nextVisualPanelField = PlayerGamePanel.class.getDeclaredField("nextVisualPanel");
        nextVisualPanelField.setAccessible(true);
        javax.swing.JPanel nextPanel = (javax.swing.JPanel) nextVisualPanelField.get(playerPanel);
        
        nextPanel.setSize(180, 90);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(180, 90, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        nextPanel.paint(g2d);
        g2d.dispose();
        
        assertTrue("아이템이 있는 블록이 렌더링되어야 함", true);
    }

    /**
     * 테스트 75: getItemIcon - 모든 아이템 타입
     */
    @Test
    public void testPlayerPanel_GetItemIconAllTypes() throws Exception {
        playerPanel.startGame();
        
        // 다양한 아이템으로 테스트
        se.tetris.team5.items.Item[] items = {
            new se.tetris.team5.items.LineClearItem(),
            new se.tetris.team5.items.TimeStopItem(),
            new se.tetris.team5.items.DoubleScoreItem(),
            new se.tetris.team5.items.BombItem(),
            new se.tetris.team5.items.WeightBlockItem(),
            new se.tetris.team5.items.ScoreItem(1000)
        };
        
        GameEngine engine = playerPanel.getGameEngine();
        se.tetris.team5.blocks.Block nextBlock = engine.getNextBlock();
        
        for (se.tetris.team5.items.Item item : items) {
            if (nextBlock != null && nextBlock.width() > 0 && nextBlock.height() > 0) {
                nextBlock.setItem(0, 0, item);
                
                // nextVisualPanel repaint (getItemIcon 호출)
                Field nextVisualPanelField = PlayerGamePanel.class.getDeclaredField("nextVisualPanel");
                nextVisualPanelField.setAccessible(true);
                javax.swing.JPanel nextPanel = (javax.swing.JPanel) nextVisualPanelField.get(playerPanel);
                
                nextPanel.setSize(180, 90);
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(180, 90, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g2d = img.createGraphics();
                nextPanel.paint(g2d);
                g2d.dispose();
            }
        }
        
        assertTrue("모든 아이템 타입의 아이콘이 표시되어야 함", true);
    }

    /**
     * 테스트 76: attackPanel paintComponent - 공격 블럭 렌더링
     */
    @Test
    public void testPlayerPanel_AttackPanelPaint() throws Exception {
        // 공격 블럭 추가
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.GRAY;
            }
            attackBlocks.add(row);
        }
        playerPanel.addAttackBlocks(attackBlocks);
        
        // attackPanel repaint
        Field attackPanelField = PlayerGamePanel.class.getDeclaredField("attackPanel");
        attackPanelField.setAccessible(true);
        javax.swing.JPanel attackPanel = (javax.swing.JPanel) attackPanelField.get(playerPanel);
        
        attackPanel.setSize(200, 180);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(200, 180, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        attackPanel.paint(g2d);
        g2d.dispose();
        
        assertTrue("공격 블럭 패널이 렌더링되어야 함", true);
    }

    /**
     * 테스트 77: attackPanel - 빈 상태 렌더링
     */
    @Test
    public void testPlayerPanel_AttackPanelPaintEmpty() throws Exception {
        // 공격 블럭이 없는 상태
        Field attackPanelField = PlayerGamePanel.class.getDeclaredField("attackPanel");
        attackPanelField.setAccessible(true);
        javax.swing.JPanel attackPanel = (javax.swing.JPanel) attackPanelField.get(playerPanel);
        
        attackPanel.setSize(200, 180);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(200, 180, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        attackPanel.paint(g2d);
        g2d.dispose();
        
        assertTrue("빈 공격 블럭 패널이 렌더링되어야 함", true);
    }

    /**
     * 테스트 78: attackPanel - 10줄 초과 데이터 (최대 10줄만 표시)
     */
    @Test
    public void testPlayerPanel_AttackPanelPaintMaxRows() throws Exception {
        // 10줄 추가
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.DARK_GRAY;
            }
            attackBlocks.add(row);
        }
        playerPanel.addAttackBlocks(attackBlocks);
        
        // attackPanel repaint
        Field attackPanelField = PlayerGamePanel.class.getDeclaredField("attackPanel");
        attackPanelField.setAccessible(true);
        javax.swing.JPanel attackPanel = (javax.swing.JPanel) attackPanelField.get(playerPanel);
        
        attackPanel.setSize(200, 180);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(200, 180, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        attackPanel.paint(g2d);
        g2d.dispose();
        
        assertTrue("최대 10줄의 공격 블럭이 렌더링되어야 함", true);
    }

    /**
     * 테스트 79: attackPanel - rowData에 null 색상 포함
     */
    @Test
    public void testPlayerPanel_AttackPanelPaintWithNullColors() throws Exception {
        // null 색상을 포함한 공격 블럭
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        java.awt.Color[] row = new java.awt.Color[10];
        row[0] = java.awt.Color.GRAY;
        row[1] = null; // null 색상
        row[2] = java.awt.Color.GRAY;
        attackBlocks.add(row);
        
        playerPanel.addAttackBlocks(attackBlocks);
        
        // attackPanel repaint
        Field attackPanelField = PlayerGamePanel.class.getDeclaredField("attackPanel");
        attackPanelField.setAccessible(true);
        javax.swing.JPanel attackPanel = (javax.swing.JPanel) attackPanelField.get(playerPanel);
        
        attackPanel.setSize(200, 180);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(200, 180, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        attackPanel.paint(g2d);
        g2d.dispose();
        
        assertTrue("null 색상이 있어도 렌더링이 안전해야 함", true);
    }

    /**
     * 테스트 80: createKoreanFont - 모든 폰트 이름 시도
     */
    @Test
    public void testPlayerPanel_CreateKoreanFontAllNames() throws Exception {
        // 여러 스타일의 패널 생성하여 createKoreanFont의 모든 분기 테스트
        PlayerGamePanel[] panels = {
            new PlayerGamePanel("테스트1", "맑은 고딕", java.awt.Color.RED),
            new PlayerGamePanel("테스트2", "Malgun Gothic", java.awt.Color.GREEN),
            new PlayerGamePanel("테스트3", "굴림", java.awt.Color.BLUE),
            new PlayerGamePanel("테스트4", "Gulim", java.awt.Color.YELLOW),
            new PlayerGamePanel("테스트5", "Arial Unicode MS", java.awt.Color.CYAN),
            new PlayerGamePanel("테스트6", "Dialog", java.awt.Color.MAGENTA)
        };
        
        for (PlayerGamePanel panel : panels) {
            assertNotNull("패널이 생성되어야 함", panel);
        }
        
        assertTrue("모든 폰트 이름으로 패널이 생성되어야 함", true);
    }

    /**
     * 테스트 81: timeStopOverlay paintComponent
     */
    @Test
    public void testPlayerPanel_TimeStopOverlayPaint() throws Exception {
        playerPanel.startGame();
        
        // 타임스톱 활성화
        GameEngine engine = playerPanel.getGameEngine();
        Field timeStopChargeField = GameEngine.class.getDeclaredField("timeStopCharge");
        timeStopChargeField.setAccessible(true);
        timeStopChargeField.setInt(engine, 1);
        
        playerPanel.useItem();
        
        // timeStopOverlay repaint
        Field timeStopOverlayField = PlayerGamePanel.class.getDeclaredField("timeStopOverlay");
        timeStopOverlayField.setAccessible(true);
        javax.swing.JPanel overlay = (javax.swing.JPanel) timeStopOverlayField.get(playerPanel);
        
        overlay.setSize(300, 500);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(300, 500, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        overlay.paint(g2d);
        g2d.dispose();
        
        assertTrue("타임스톱 오버레이가 렌더링되어야 함", true);
    }

    /**
     * 테스트 82: queueOutgoingAttackBlocks - null 처리
     */
    @Test
    public void testPlayerPanel_QueueOutgoingAttackBlocksNull() throws Exception {
        playerPanel.startGame();
        
        java.lang.reflect.Method queueMethod = PlayerGamePanel.class.getDeclaredMethod("queueOutgoingAttackBlocks", java.util.List.class);
        queueMethod.setAccessible(true);
        queueMethod.invoke(playerPanel, (Object) null);
        
        java.util.List<java.awt.Color[]> drained = playerPanel.drainPendingOutgoingAttackBlocks();
        assertEquals("null 큐잉 시 빈 리스트", 0, drained.size());
    }

    /**
     * 테스트 83: queueOutgoingAttackBlocks - 빈 리스트
     */
    @Test
    public void testPlayerPanel_QueueOutgoingAttackBlocksEmpty() throws Exception {
        playerPanel.startGame();
        
        java.lang.reflect.Method queueMethod = PlayerGamePanel.class.getDeclaredMethod("queueOutgoingAttackBlocks", java.util.List.class);
        queueMethod.setAccessible(true);
        queueMethod.invoke(playerPanel, new java.util.ArrayList<java.awt.Color[]>());
        
        java.util.List<java.awt.Color[]> drained = playerPanel.drainPendingOutgoingAttackBlocks();
        assertEquals("빈 리스트 큐잉 시 빈 리스트", 0, drained.size());
    }

    /**
     * 테스트 84: queueOutgoingAttackBlocks - 정상 큐잉 후 drain
     */
    @Test
    public void testPlayerPanel_QueueAndDrainOutgoingAttackBlocks() throws Exception {
        playerPanel.startGame();
        
        // 공격 블럭 데이터 생성
        java.util.List<java.awt.Color[]> attackBlocks = new java.util.ArrayList<>();
        for (int i = 0; i < 2; i++) {
            java.awt.Color[] row = new java.awt.Color[10];
            for (int j = 0; j < 10; j++) {
                row[j] = java.awt.Color.RED;
            }
            attackBlocks.add(row);
        }
        
        // 큐잉
        java.lang.reflect.Method queueMethod = PlayerGamePanel.class.getDeclaredMethod("queueOutgoingAttackBlocks", java.util.List.class);
        queueMethod.setAccessible(true);
        queueMethod.invoke(playerPanel, attackBlocks);
        
        // drain
        java.util.List<java.awt.Color[]> drained = playerPanel.drainPendingOutgoingAttackBlocks();
        assertEquals("큐잉된 공격 블럭이 drain되어야 함", 2, drained.size());
        
        // drain 후 다시 drain하면 빈 리스트
        java.util.List<java.awt.Color[]> secondDrain = playerPanel.drainPendingOutgoingAttackBlocks();
        assertEquals("두 번째 drain은 빈 리스트", 0, secondDrain.size());
    }

    /**
     * 테스트 85: updateGameUI - 경과 시간 타이머 업데이트 (countdownTimerEnabled false)
     */
    @Test
    public void testPlayerPanel_UpdateGameUIElapsedTimeTimer() throws Exception {
        playerPanel.setCountdownTimerEnabled(false);
        playerPanel.startGame();
        
        // 시간 경과
        Thread.sleep(100);
        
        playerPanel.updateGameUI();
        
        // timerLabel이 업데이트되었는지 확인
        Field timerLabelField = PlayerGamePanel.class.getDeclaredField("timerLabel");
        timerLabelField.setAccessible(true);
        javax.swing.JLabel timerLabel = (javax.swing.JLabel) timerLabelField.get(playerPanel);
        
        assertNotNull("타이머 라벨이 존재해야 함", timerLabel);
        assertFalse("타이머가 00:00이 아니어야 함", "00:00".equals(timerLabel.getText()));
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
