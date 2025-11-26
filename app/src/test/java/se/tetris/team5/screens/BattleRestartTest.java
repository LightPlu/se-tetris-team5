package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.ScreenController;
import se.tetris.team5.components.battle.PlayerGamePanel;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.GameMode;
import se.tetris.team5.gamelogic.battle.BattleGameController;

import javax.swing.Timer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 배틀 모드 재시작 기능 종합 테스트
 * 게임 재시작 시 모든 상태가 올바르게 초기화되는지 검증
 */
public class BattleRestartTest {

  private battle battleScreen;
  private ScreenController screenController;

  @Before
  public void setUp() {
    screenController = new ScreenController();
    System.setProperty("tetris.battle.mode", "NORMAL");
  }

  @After
  public void tearDown() {
    if (battleScreen != null) {
      stopAllTimers();
    }
    System.clearProperty("tetris.battle.mode");
    System.clearProperty("tetris.battle.originalSize");
  }

  /**
   * 테스트 1: 기본 재시작 - 새로운 게임 시작
   */
  @Test
  public void testRestart_StartsNewGame() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    // 게임 진행 확인
    PlayerGamePanel player1Before = getPlayer1Panel();
    GameEngine engine1Before = player1Before.getGameEngine();
    assertNotNull("재시작 전 게임 엔진이 존재해야 함", engine1Before);

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    // 새로운 패널과 엔진 생성 확인
    PlayerGamePanel player1After = getPlayer1Panel();
    GameEngine engine1After = player1After.getGameEngine();

    assertNotNull("재시작 후 게임 엔진이 존재해야 함", engine1After);
    assertNotSame("재시작 후 새로운 엔진 인스턴스가 생성되어야 함", engine1Before, engine1After);
  }

  /**
   * 테스트 2: 재시작 시 게임판 초기화
   */
  @Test
  public void testRestart_ClearsBoardState() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(200);

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    // 게임판 상태 확인
    PlayerGamePanel player1 = getPlayer1Panel();
    PlayerGamePanel player2 = getPlayer2Panel();

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    int[][] board1 = engine1.getBoardManager().getBoard();
    int[][] board2 = engine2.getBoardManager().getBoard();

    int occupied1 = countOccupiedCells(board1);
    int occupied2 = countOccupiedCells(board2);

    // 현재 블록만 존재 (최대 4칸)
    assertTrue("Player 1 게임판이 초기 상태여야 함 (occupied: " + occupied1 + ")", occupied1 <= 4);
    assertTrue("Player 2 게임판이 초기 상태여야 함 (occupied: " + occupied2 + ")", occupied2 <= 4);
  }

  /**
   * 테스트 3: 재시작 시 타이머 재생성
   */
  @Test
  public void testRestart_RecreatesTimers() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    Timer oldGameOverTimer = getGameOverCheckTimer();
    assertNotNull("게임 시작 후 게임 오버 체크 타이머가 존재해야 함", oldGameOverTimer);
    assertTrue("게임 오버 체크 타이머가 실행 중이어야 함", oldGameOverTimer.isRunning());

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    Timer newGameOverTimer = getGameOverCheckTimer();
    assertNotNull("재시작 후 게임 오버 체크 타이머가 다시 생성되어야 함", newGameOverTimer);
    assertTrue("재시작 후 타이머가 실행 중이어야 함", newGameOverTimer.isRunning());
    assertNotSame("재시작 후 새로운 타이머 인스턴스가 생성되어야 함", oldGameOverTimer, newGameOverTimer);
  }

  /**
   * 테스트 4: 재시작 시 게임 컨트롤러 재생성
   */
  @Test
  public void testRestart_RecreatesGameController() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    BattleGameController oldController = getGameController();
    assertNotNull("게임 시작 후 컨트롤러가 존재해야 함", oldController);

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    BattleGameController newController = getGameController();
    assertNotNull("재시작 후 컨트롤러가 존재해야 함", newController);
    assertNotSame("재시작 후 새로운 컨트롤러 인스턴스가 생성되어야 함", oldController, newController);
  }

  /**
   * 테스트 5: ITEM 모드 재시작 시 게임 모드 유지
   */
  @Test
  public void testRestart_MaintainsItemMode() throws Exception {
    System.setProperty("tetris.battle.mode", "ITEM");
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    PlayerGamePanel player1 = getPlayer1Panel();
    PlayerGamePanel player2 = getPlayer2Panel();

    GameMode mode1 = player1.getGameEngine().getGameMode();
    GameMode mode2 = player2.getGameEngine().getGameMode();

    assertEquals("Player 1이 ITEM 모드여야 함", GameMode.ITEM, mode1);
    assertEquals("Player 2가 ITEM 모드여야 함", GameMode.ITEM, mode2);
  }

  /**
   * 테스트 6: TIMELIMIT 모드 재시작 시 타이머 리셋
   */
  @Test
  public void testRestart_ResetsTimeLimitTimer() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    Timer oldTimeLimitTimer = getTimeLimitTimer();
    assertNotNull("시간제한 모드에서 타이머가 존재해야 함", oldTimeLimitTimer);
    assertTrue("시간제한 타이머가 실행 중이어야 함", oldTimeLimitTimer.isRunning());

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    Timer newTimeLimitTimer = getTimeLimitTimer();
    assertNotNull("재시작 후 시간제한 타이머가 다시 생성되어야 함", newTimeLimitTimer);
    assertTrue("재시작 후 타이머가 실행 중이어야 함", newTimeLimitTimer.isRunning());
    assertNotSame("재시작 후 새로운 타이머 인스턴스가 생성되어야 함", oldTimeLimitTimer, newTimeLimitTimer);
  }

  /**
   * 테스트 7: TIMELIMIT 모드 재시작 시 게임 모드 유지 (NORMAL)
   */
  @Test
  public void testRestart_TimeLimitMaintainsNormalMode() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    PlayerGamePanel player1 = getPlayer1Panel();
    PlayerGamePanel player2 = getPlayer2Panel();

    GameMode mode1 = player1.getGameEngine().getGameMode();
    GameMode mode2 = player2.getGameEngine().getGameMode();

    assertEquals("시간제한 모드에서 Player 1이 NORMAL 모드여야 함", GameMode.NORMAL, mode1);
    assertEquals("시간제한 모드에서 Player 2가 NORMAL 모드여야 함", GameMode.NORMAL, mode2);
  }

  /**
   * 테스트 8: 재시작 시 일시정지 상태 해제
   */
  @Test
  public void testRestart_ClearsPauseState() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    // 일시정지 상태로 설정
    setPaused(true);
    assertTrue("일시정지 상태여야 함", isPaused());

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    assertFalse("재시작 후 일시정지 상태가 해제되어야 함", isPaused());
  }

  /**
   * 테스트 9: 재시작 시 입력 핸들러 재생성
   */
  @Test
  public void testRestart_RecreatesInputHandlers() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    Object oldPlayer1Input = getPlayer1Input();
    Object oldPlayer2Input = getPlayer2Input();
    assertNotNull("Player 1 입력 핸들러가 존재해야 함", oldPlayer1Input);
    assertNotNull("Player 2 입력 핸들러가 존재해야 함", oldPlayer2Input);

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    Object newPlayer1Input = getPlayer1Input();
    Object newPlayer2Input = getPlayer2Input();
    assertNotNull("재시작 후 Player 1 입력 핸들러가 존재해야 함", newPlayer1Input);
    assertNotNull("재시작 후 Player 2 입력 핸들러가 존재해야 함", newPlayer2Input);
    assertNotSame("재시작 후 새로운 Player 1 입력 핸들러가 생성되어야 함", oldPlayer1Input, newPlayer1Input);
    assertNotSame("재시작 후 새로운 Player 2 입력 핸들러가 생성되어야 함", oldPlayer2Input, newPlayer2Input);
  }

  /**
   * 테스트 10: 연속 재시작 시 정상 작동
   */
  @Test
  public void testRestart_MultipleRestarts() throws Exception {
    battleScreen = new battle(screenController);

    // 첫 번째 재시작
    startBattle();
    Thread.sleep(100);
    restartGame();
    Thread.sleep(100);

    PlayerGamePanel player1First = getPlayer1Panel();
    assertNotNull("첫 번째 재시작 후 패널이 존재해야 함", player1First);

    // 두 번째 재시작
    restartGame();
    Thread.sleep(100);

    PlayerGamePanel player1Second = getPlayer1Panel();
    assertNotNull("두 번째 재시작 후 패널이 존재해야 함", player1Second);
    assertNotSame("연속 재시작 시 새로운 패널이 생성되어야 함", player1First, player1Second);

    // 세 번째 재시작
    restartGame();
    Thread.sleep(100);

    PlayerGamePanel player1Third = getPlayer1Panel();
    assertNotNull("세 번째 재시작 후 패널이 존재해야 함", player1Third);
    assertNotSame("연속 재시작 시 새로운 패널이 생성되어야 함", player1Second, player1Third);
  }

  /**
   * 테스트 11: 재시작 시 UI 컴포넌트 재구성
   */
  @Test
  public void testRestart_RebuildsUI() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    int componentCountBefore = battleScreen.getComponentCount();
    assertTrue("UI 컴포넌트가 존재해야 함", componentCountBefore > 0);

    // 재시작 실행
    restartGame();
    Thread.sleep(100);

    int componentCountAfter = battleScreen.getComponentCount();
    assertTrue("재시작 후에도 UI 컴포넌트가 존재해야 함", componentCountAfter > 0);
  }

  /**
   * 테스트 12: 재시작 전 기존 타이머 정지
   */
  @Test
  public void testRestart_StopsOldTimers() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    Timer oldGameOverTimer = getGameOverCheckTimer();
    assertTrue("기존 타이머가 실행 중이어야 함", oldGameOverTimer.isRunning());

    // 재시작 실행 (내부적으로 기존 타이머 정지)
    restartGame();
    Thread.sleep(100);

    // 기존 타이머는 정지되었어야 함
    assertFalse("재시작 후 기존 타이머가 정지되어야 함", oldGameOverTimer.isRunning());
  }

  /**
   * 테스트 13: 재시작 시 null 체크 (방어적 프로그래밍)
   */
  @Test
  public void testRestart_HandlesNullTimers() throws Exception {
    battleScreen = new battle(screenController);

    // 타이머가 null인 상태에서 재시작
    setTimeLimitTimer(null);
    setGameOverCheckTimer(null);

    // 예외 없이 재시작 가능해야 함
    try {
      restartGame();
      Thread.sleep(100);
      // 성공
      assertTrue("null 타이머 상태에서도 재시작이 가능해야 함", true);
    } catch (Exception e) {
      fail("null 타이머 상태에서 재시작 시 예외가 발생하지 않아야 함: " + e.getMessage());
    }
  }

  /**
   * 테스트 14: 재시작 시 게임 오버 상태 초기화
   */
  @Test
  public void testRestart_ClearsGameOverState() throws Exception {
    battleScreen = new battle(screenController);
    startBattle();
    Thread.sleep(100);

    // Player 1을 게임 오버 상태로 만들기
    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();

    Field gameOverField = GameEngine.class.getDeclaredField("gameOver");
    gameOverField.setAccessible(true);
    gameOverField.set(engine1, true);

    assertTrue("게임 오버 상태여야 함", engine1.isGameOver());

    // 재시작 실행 (새로운 엔진 생성)
    restartGame();
    Thread.sleep(100);

    // 새로운 엔진은 게임 오버 상태가 아니어야 함
    PlayerGamePanel player1After = getPlayer1Panel();
    GameEngine engine1After = player1After.getGameEngine();

    assertFalse("재시작 후 게임 오버 상태가 아니어야 함", engine1After.isGameOver());
  }

  /**
   * 테스트 15: 모든 모드에서 재시작 가능
   */
  @Test
  public void testRestart_AllModesSupported() throws Exception {
    String[] modes = { "NORMAL", "ITEM", "TIMELIMIT" };

    for (String mode : modes) {
      System.setProperty("tetris.battle.mode", mode);
      battleScreen = new battle(screenController);

      try {
        startBattle();
        Thread.sleep(100);
        restartGame();
        Thread.sleep(100);

        PlayerGamePanel player1 = getPlayer1Panel();
        assertNotNull(mode + " 모드에서 재시작 후 패널이 존재해야 함", player1);

        // 다음 테스트를 위해 타이머 정리
        stopAllTimers();
      } catch (Exception e) {
        fail(mode + " 모드에서 재시작 실패: " + e.getMessage());
      }
    }
  }

  // ===== Helper Methods =====

  private void startBattle() throws Exception {
    Method method = battle.class.getDeclaredMethod("startBattle");
    method.setAccessible(true);
    method.invoke(battleScreen);
  }

  private void restartGame() throws Exception {
    Method initMethod = battle.class.getDeclaredMethod("initializeGame");
    initMethod.setAccessible(true);
    initMethod.invoke(battleScreen);

    // 실제 재시작 로직과 동일하게 SwingUtilities.invokeLater 사용
    javax.swing.SwingUtilities.invokeAndWait(() -> {
      try {
        startBattle();
        battleScreen.requestFocusInWindow();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

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

  private BattleGameController getGameController() throws Exception {
    Field field = battle.class.getDeclaredField("gameController");
    field.setAccessible(true);
    return (BattleGameController) field.get(battleScreen);
  }

  private Timer getGameOverCheckTimer() throws Exception {
    Field field = battle.class.getDeclaredField("gameOverCheckTimer");
    field.setAccessible(true);
    return (Timer) field.get(battleScreen);
  }

  private Timer getTimeLimitTimer() throws Exception {
    Field field = battle.class.getDeclaredField("timeLimitTimer");
    field.setAccessible(true);
    return (Timer) field.get(battleScreen);
  }

  private void setTimeLimitTimer(Timer timer) throws Exception {
    Field field = battle.class.getDeclaredField("timeLimitTimer");
    field.setAccessible(true);
    field.set(battleScreen, timer);
  }

  private void setGameOverCheckTimer(Timer timer) throws Exception {
    Field field = battle.class.getDeclaredField("gameOverCheckTimer");
    field.setAccessible(true);
    field.set(battleScreen, timer);
  }

  private Object getPlayer1Input() throws Exception {
    Field field = battle.class.getDeclaredField("player1Input");
    field.setAccessible(true);
    return field.get(battleScreen);
  }

  private Object getPlayer2Input() throws Exception {
    Field field = battle.class.getDeclaredField("player2Input");
    field.setAccessible(true);
    return field.get(battleScreen);
  }

  private boolean isPaused() throws Exception {
    Field field = battle.class.getDeclaredField("isPaused");
    field.setAccessible(true);
    return (boolean) field.get(battleScreen);
  }

  private void setPaused(boolean paused) throws Exception {
    Field field = battle.class.getDeclaredField("isPaused");
    field.setAccessible(true);
    field.set(battleScreen, paused);
  }

  private int countOccupiedCells(int[][] board) {
    int count = 0;
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] != 0) {
          count++;
        }
      }
    }
    return count;
  }

  private void stopAllTimers() {
    try {
      Timer gameOverTimer = getGameOverCheckTimer();
      if (gameOverTimer != null && gameOverTimer.isRunning()) {
        gameOverTimer.stop();
      }
    } catch (Exception e) {
      // 무시
    }

    try {
      Timer timeLimitTimer = getTimeLimitTimer();
      if (timeLimitTimer != null && timeLimitTimer.isRunning()) {
        timeLimitTimer.stop();
      }
    } catch (Exception e) {
      // 무시
    }

    try {
      BattleGameController controller = getGameController();
      if (controller != null) {
        controller.stop();
      }
    } catch (Exception e) {
      // 무시
    }
  }
}
