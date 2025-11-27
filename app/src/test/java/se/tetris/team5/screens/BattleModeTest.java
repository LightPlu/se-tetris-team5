package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.ScreenController;
import se.tetris.team5.components.battle.PlayerGamePanel;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.GameMode;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 대전 모드 테스트
 * 일반 모드와 동일한 게임 로직이 작동하는지 검증
 */
public class BattleModeTest {

  private battle battleScreen;
  private ScreenController screenController;

  @Before
  public void setUp() {
    screenController = new ScreenController();
    // 대전 모드 설정
    System.setProperty("tetris.battle.mode", "NORMAL");
    battleScreen = new battle(screenController);
  }

  /**
   * 테스트 1: 대전 모드 초기화 시 빈 게임판으로 시작
   */
  @Test
  public void testBattleMode_StartsWithCleanBoard() throws Exception {
    PlayerGamePanel player1 = getPlayer1Panel();
    PlayerGamePanel player2 = getPlayer2Panel();

    assertNotNull("Player 1 패널이 null이 아니어야 함", player1);
    assertNotNull("Player 2 패널이 null이 아니어야 함", player2);

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    assertNotNull("Player 1 엔진이 null이 아니어야 함", engine1);
    assertNotNull("Player 2 엔진이 null이 아니어야 함", engine2);

    // 게임판이 비어있는지 확인
    int[][] board1 = engine1.getBoardManager().getBoard();
    int[][] board2 = engine2.getBoardManager().getBoard();

    int occupiedCells1 = countOccupiedCells(board1);
    int occupiedCells2 = countOccupiedCells(board2);

    // 현재 블록(4칸)만 존재해야 함
    assertTrue("Player 1 게임판이 거의 비어있어야 함 (현재 블록만 존재)", occupiedCells1 <= 4);
    assertTrue("Player 2 게임판이 거의 비어있어야 함 (현재 블록만 존재)", occupiedCells2 <= 4);
  }

  /**
   * 테스트 2: 양쪽 플레이어가 독립적으로 블록 조작 가능
   */
  @Test
  public void testBattleMode_IndependentBlockMovement() throws Exception {
    // 게임 시작
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel();
    PlayerGamePanel player2 = getPlayer2Panel();

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    int initialX1 = engine1.getX();
    int initialX2 = engine2.getX();

    // Player 1 왼쪽 이동 (A키)
    simulateKeyPress(KeyEvent.VK_A);
    int afterMoveX1 = engine1.getX();

    // Player 2는 영향 받지 않아야 함
    int afterMoveX2 = engine2.getX();

    assertEquals("Player 1이 왼쪽으로 이동해야 함", initialX1 - 1, afterMoveX1);
    assertEquals("Player 2는 영향 받지 않아야 함", initialX2, afterMoveX2);
  }

  /**
   * 테스트 3: 줄 삭제 애니메이션이 트리거될 수 있는 구조 확인
   */
  @Test
  public void testBattleMode_LineClearAnimationStructure() throws Exception {
    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();

    // consumeLastClearedRows 메서드가 존재하는지 확인
    java.util.List<Integer> clearedRows = engine1.consumeLastClearedRows();

    // 초기 상태에서는 null이거나 빈 리스트
    assertTrue("초기 상태에서 삭제된 줄이 없어야 함",
        clearedRows == null || clearedRows.isEmpty());
  }

  /**
   * 테스트 4: 아이템 모드 설정 확인
   */
  @Test
  public void testBattleMode_ItemModeConfiguration() throws Exception {
    // 아이템 모드로 재설정
    System.setProperty("tetris.battle.mode", "ITEM");
    battle itemBattle = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1PanelFrom(itemBattle);
    PlayerGamePanel player2 = getPlayer2PanelFrom(itemBattle);

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    assertEquals("Player 1이 아이템 모드여야 함", GameMode.ITEM, engine1.getGameMode());
    assertEquals("Player 2가 아이템 모드여야 함", GameMode.ITEM, engine2.getGameMode());
  }

  /**
   * 테스트 5: 시간제한 모드 타이머 작동 확인
   */
  @Test
  public void testBattleMode_TimeLimitModeTimer() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle timeLimitBattle = new battle(screenController);

    // 타이머가 생성되었는지 확인
    Field timerField = battle.class.getDeclaredField("timeLimitTimer");
    timerField.setAccessible(true);
    javax.swing.Timer timer = (javax.swing.Timer) timerField.get(timeLimitBattle);

    assertNotNull("시간제한 타이머가 생성되어야 함", timer);
    assertTrue("시간제한 타이머가 실행 중이어야 함", timer.isRunning());
  }

  /**
   * 테스트 6: 게임 재시작 시 완전 초기화 확인
   */
  @Test
  public void testBattleMode_RestartResetsCompletely() throws Exception {
    // 재시작 메서드 호출
    Method initMethod = battle.class.getDeclaredMethod("initializeGame");
    initMethod.setAccessible(true);
    initMethod.invoke(battleScreen);

    // 새로운 패널 가져오기
    PlayerGamePanel newPlayer1 = getPlayer1Panel();
    GameEngine newEngine1 = newPlayer1.getGameEngine();
    int[][] newBoard = newEngine1.getBoardManager().getBoard();
    int occupiedAfter = countOccupiedCells(newBoard);

    assertTrue("재시작 후 게임판이 초기화되어야 함 (현재 블록만 존재)", occupiedAfter <= 4);
  }

  /**
   * 테스트 7: 게임 오버 시 상태 확인
   */
  @Test
  public void testBattleMode_GameOverState() throws Exception {
    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();

    // Player 1 게임 오버 상태로 만들기
    Field gameOverField = GameEngine.class.getDeclaredField("gameOver");
    gameOverField.setAccessible(true);
    gameOverField.set(engine1, true);

    // 게임 오버 체크
    boolean isGameOver1 = engine1.isGameOver();
    assertTrue("Player 1이 게임 오버 상태여야 함", isGameOver1);
  }

  /**
   * 테스트 8: 일시정지 기능 작동 확인
   */
  @Test
  public void testBattleMode_PauseResumeFunctionality() throws Exception {
    // 게임 시작
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    // 테스트 환경에서는 ESC키 대신 강제로 일시정지
    battleScreen.forcePause();

    // 일시정지 상태 확인
    Field pausedField = battle.class.getDeclaredField("isPaused");
    pausedField.setAccessible(true);
    boolean isPaused = (boolean) pausedField.get(battleScreen);

    assertTrue("ESC 키 입력 후 일시정지 상태여야 함", isPaused);
  }

  // ===== Helper Methods =====

  private PlayerGamePanel getPlayer1Panel() throws Exception {
    Field field = battle.class.getDeclaredField("player1Panel");
    field.setAccessible(true);
    return (PlayerGamePanel) field.get(battleScreen);
  }

  private PlayerGamePanel getPlayer2Panel() throws Exception {
    Field field = battle.class.getDeclaredField("player2Panel");
    field.setAccessible(true);
    return (PlayerGamePanel) field.get(battleScreen);
  }

  private PlayerGamePanel getPlayer1PanelFrom(battle screen) throws Exception {
    Field field = battle.class.getDeclaredField("player1Panel");
    field.setAccessible(true);
    return (PlayerGamePanel) field.get(screen);
  }

  private PlayerGamePanel getPlayer2PanelFrom(battle screen) throws Exception {
    Field field = battle.class.getDeclaredField("player2Panel");
    field.setAccessible(true);
    return (PlayerGamePanel) field.get(screen);
  }

  private void simulateKeyPress(int keyCode) {
    KeyEvent event = new KeyEvent(
        battleScreen,
        KeyEvent.KEY_PRESSED,
        System.currentTimeMillis(),
        0,
        keyCode,
        (char) keyCode);
    battleScreen.keyPressed(event);
  }

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

  /**
   * 테스트 9: 게임 재시작 시 타이머가 정리되고 새로 시작되는지 확인
   */
  @Test
  public void testGameRestart_TimersResetProperly() throws Exception {
    // 게임 시작
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    Thread.sleep(100); // 타이머 시작 대기

    // gameOverCheckTimer가 시작되었는지 확인
    Field timerField = battle.class.getDeclaredField("gameOverCheckTimer");
    timerField.setAccessible(true);
    javax.swing.Timer oldTimer = (javax.swing.Timer) timerField.get(battleScreen);
    assertNotNull("게임 오버 체크 타이머가 시작되어야 함", oldTimer);
    assertTrue("타이머가 실행 중이어야 함", oldTimer.isRunning());

    // 게임 재시작 시뮬레이션
    Method initMethod = battle.class.getDeclaredMethod("initializeGame");
    initMethod.setAccessible(true);
    initMethod.invoke(battleScreen);

    // 재시작 후 타이머 확인
    javax.swing.Timer newTimer = (javax.swing.Timer) timerField.get(battleScreen);
    // initializeGame만 호출하면 타이머는 null이어야 함 (startBattle에서 생성)
    assertNull("재초기화 후 타이머는 아직 null이어야 함", newTimer);
  }

  /**
   * 테스트 10: 게임 재시작 시 블럭이 정상적으로 생성되는지 확인
   */
  @Test
  public void testGameRestart_BlocksGenerated() throws Exception {
    // 게임 시작
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    Thread.sleep(200); // 블럭 생성 대기

    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();

    // 현재 블럭과 다음 블럭 확인
    assertNotNull("현재 블럭이 생성되어야 함", engine1.getCurrentBlock());
    assertNotNull("다음 블럭이 생성되어야 함", engine1.getNextBlock());

    // 재시작
    Method initMethod = battle.class.getDeclaredMethod("initializeGame");
    initMethod.setAccessible(true);
    initMethod.invoke(battleScreen);

    startBattleMethod.invoke(battleScreen);
    Thread.sleep(200);

    // 새로운 패널과 엔진 가져오기
    PlayerGamePanel newPlayer1 = getPlayer1Panel();
    GameEngine newEngine1 = newPlayer1.getGameEngine();

    // 재시작 후에도 블럭이 생성되어야 함
    assertNotNull("재시작 후 현재 블럭이 생성되어야 함", newEngine1.getCurrentBlock());
    assertNotNull("재시작 후 다음 블럭이 생성되어야 함", newEngine1.getNextBlock());
  }

  /**
   * 테스트 11: 게임 재시작 시 점수가 초기화되는지 확인
   */
  @Test
  public void testGameRestart_ScoreResets() throws Exception {
    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();

    // 게임 시작
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    Thread.sleep(100);

    // 점수 증가 시뮬레이션
    engine1.getGameScoring().addPoints(1000);

    int scoreBeforeRestart = engine1.getGameScoring().getCurrentScore();
    assertTrue("게임 진행 후 점수가 증가해야 함", scoreBeforeRestart > 0);

    // 재시작
    Method initMethod = battle.class.getDeclaredMethod("initializeGame");
    initMethod.setAccessible(true);
    initMethod.invoke(battleScreen);

    startBattleMethod.invoke(battleScreen);
    Thread.sleep(100);

    // 새로운 엔진 가져오기
    PlayerGamePanel newPlayer1 = getPlayer1Panel();
    GameEngine newEngine1 = newPlayer1.getGameEngine();

    assertEquals("재시작 후 점수가 0으로 초기화되어야 함", 0, newEngine1.getGameScoring().getCurrentScore());
  }

  /**
   * 테스트 12: 게임 재시작 시 게임 모드가 유지되는지 확인
   */
  @Test
  public void testGameRestart_GameModePreserved() throws Exception {
    // ITEM 모드로 설정
    System.setProperty("tetris.battle.mode", "ITEM");
    battle itemBattle = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(itemBattle);

    Thread.sleep(100);

    PlayerGamePanel player1 = getPlayer1PanelFrom(itemBattle);
    GameEngine engine1 = player1.getGameEngine();

    assertEquals("초기 게임 모드가 ITEM이어야 함", GameMode.ITEM, engine1.getGameMode());

    // 재시작
    Method initMethod = battle.class.getDeclaredMethod("initializeGame");
    initMethod.setAccessible(true);
    initMethod.invoke(itemBattle);

    startBattleMethod.invoke(itemBattle);
    Thread.sleep(100);

    // 새로운 엔진 확인
    PlayerGamePanel newPlayer1 = getPlayer1PanelFrom(itemBattle);
    GameEngine newEngine1 = newPlayer1.getGameEngine();

    assertEquals("재시작 후에도 게임 모드가 ITEM으로 유지되어야 함", GameMode.ITEM, newEngine1.getGameMode());
  }

  /**
   * 테스트 13: 시간제한 모드 재시작 시 타이머가 리셋되는지 확인
   */
  @Test
  public void testGameRestart_TimeLimitResets() throws Exception {
    // TIMELIMIT 모드로 설정
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle timeLimitBattle = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(timeLimitBattle);

    // 테스트 환경에서 강제로 타이머 생성
    timeLimitBattle.forceStartTimeLimitTimer();

    // timeLimitTimer 확인 (최대 500ms 대기)
    Field timerField = battle.class.getDeclaredField("timeLimitTimer");
    timerField.setAccessible(true);
    javax.swing.Timer oldTimeLimitTimer = null;
    int wait = 0;
    while (wait < 500) {
      oldTimeLimitTimer = (javax.swing.Timer) timerField.get(timeLimitBattle);
      if (oldTimeLimitTimer != null)
        break;
      Thread.sleep(10);
      wait += 10;
    }
    assertNotNull("시간제한 타이머가 시작되어야 함", oldTimeLimitTimer);

    // 재시작
    Method initMethod = battle.class.getDeclaredMethod("initializeGame");
    initMethod.setAccessible(true);
    initMethod.invoke(timeLimitBattle);

    startBattleMethod.invoke(timeLimitBattle);
    Thread.sleep(100);

    // 새로운 타이머 확인
    javax.swing.Timer newTimeLimitTimer = (javax.swing.Timer) timerField.get(timeLimitBattle);
    assertNotNull("재시작 후 시간제한 타이머가 다시 생성되어야 함", newTimeLimitTimer);
    assertTrue("재시작 후 타이머가 실행 중이어야 함", newTimeLimitTimer.isRunning());
  }
}
