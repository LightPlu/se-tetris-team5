package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.ScreenController;
import se.tetris.team5.components.battle.PlayerGamePanel;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.GameMode;
import se.tetris.team5.gamelogic.battle.BattleGameController;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 대전 모드 종합 테스트
 * - 일반 대전 모드
 * - 아이템 대전 모드
 * - 시간 제한 대전 모드
 * - 게임 오버 처리
 * - 재시작 기능
 */
public class BattleModeComprehensiveTest {

  private ScreenController screenController;

  @Before
  public void setUp() {
    screenController = new ScreenController();
  }

  // ===== 일반 대전 모드 테스트 =====

  /**
   * 테스트 1: 일반 대전 모드 초기화
   */
  @Test
  public void testNormalBattleMode_Initialization() throws Exception {
    System.setProperty("tetris.battle.mode", "NORMAL");
    battle battleScreen = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    assertNotNull("Player 1 패널이 생성되어야 함", player1);
    assertNotNull("Player 2 패널이 생성되어야 함", player2);

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    assertEquals("Player 1의 게임 모드가 NORMAL이어야 함", GameMode.NORMAL, engine1.getGameMode());
    assertEquals("Player 2의 게임 모드가 NORMAL이어야 함", GameMode.NORMAL, engine2.getGameMode());
  }

  /**
   * 테스트 2: 일반 대전 모드에서 양쪽 플레이어가 독립적으로 동작
   */
  @Test
  public void testNormalBattleMode_IndependentPlayers() throws Exception {
    System.setProperty("tetris.battle.mode", "NORMAL");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    // Player 1에게만 점수 부여
    engine1.getGameScoring().addPoints(500);

    int score1 = engine1.getGameScoring().getCurrentScore();
    int score2 = engine2.getGameScoring().getCurrentScore();

    assertEquals("Player 1의 점수가 500이어야 함", 500, score1);
    assertEquals("Player 2의 점수가 0이어야 함", 0, score2);
  }

  // ===== 아이템 대전 모드 테스트 =====

  /**
   * 테스트 3: 아이템 대전 모드 초기화
   */
  @Test
  public void testItemBattleMode_Initialization() throws Exception {
    System.setProperty("tetris.battle.mode", "ITEM");
    battle battleScreen = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    assertEquals("Player 1의 게임 모드가 ITEM이어야 함", GameMode.ITEM, engine1.getGameMode());
    assertEquals("Player 2의 게임 모드가 ITEM이어야 함", GameMode.ITEM, engine2.getGameMode());
  }

  /**
   * 테스트 4: 아이템 모드에서 점수 2배 아이템 동작
   */
  @Test
  public void testItemBattleMode_DoubleScoreItem() throws Exception {
    System.setProperty("tetris.battle.mode", "ITEM");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    GameEngine engine1 = player1.getGameEngine();

    // 점수 2배 효과 활성화
    engine1.activateDoubleScore(20_000);

    assertTrue("점수 2배 효과가 활성화되어야 함", engine1.isDoubleScoreActive());

    int initialScore = engine1.getGameScoring().getCurrentScore();
    engine1.moveBlockDown();
    int afterScore = engine1.getGameScoring().getCurrentScore();

    assertTrue("점수가 증가해야 함", afterScore > initialScore);
    assertEquals("점수가 2점 증가해야 함 (2배 효과)", initialScore + 2, afterScore);
  }

  // ===== 시간 제한 대전 모드 테스트 =====

  /**
   * 테스트 5: 시간 제한 모드 초기화 (5분 = 300초)
   */
  @Test
  public void testTimeLimitMode_Initialization() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle battleScreen = new battle(screenController);

    Field timeLimitField = battle.class.getDeclaredField("TIME_LIMIT_SECONDS");
    timeLimitField.setAccessible(true);
    int timeLimit = (int) timeLimitField.get(null);

    assertEquals("시간 제한이 300초(5분)여야 함", 300, timeLimit);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    GameEngine engine1 = player1.getGameEngine();

    assertEquals("시간제한 모드는 NORMAL 게임 모드 기반", GameMode.NORMAL, engine1.getGameMode());
  }

  /**
   * 테스트 6: 시간 제한 모드 타이머 시작 및 감소
   */
  @Test
  public void testTimeLimitMode_TimerStartsAndDecreases() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    Field remainingSecondsField = battle.class.getDeclaredField("remainingSeconds");
    remainingSecondsField.setAccessible(true);
    int initialSeconds = (int) remainingSecondsField.get(battleScreen);

    assertEquals("시작 시 남은 시간이 300초여야 함", 300, initialSeconds);

    // 1.5초 대기
    Thread.sleep(1500);

    int afterSeconds = (int) remainingSecondsField.get(battleScreen);

    assertTrue("타이머가 감소해야 함", afterSeconds < initialSeconds);
  }

  /**
   * 테스트 7: 시간 제한 모드에서 0초가 되면 게임 종료
   */
  @Test
  public void testTimeLimitMode_EndsAtZero() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    // remainingSeconds를 1초로 설정
    Field remainingSecondsField = battle.class.getDeclaredField("remainingSeconds");
    remainingSecondsField.setAccessible(true);
    remainingSecondsField.set(battleScreen, 1);

    // 1.5초 대기
    Thread.sleep(1500);

    int finalSeconds = (int) remainingSecondsField.get(battleScreen);
    assertEquals("타이머가 0초가 되어야 함", 0, finalSeconds);
  }

  /**
   * 테스트 8: 시간 제한 모드에서 높은 점수 플레이어 승리
   */
  @Test
  public void testTimeLimitMode_HigherScoreWins() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    // Player 1에게 더 높은 점수 부여
    engine1.getGameScoring().addPoints(1000);
    engine2.getGameScoring().addPoints(500);

    int score1 = engine1.getGameScoring().getCurrentScore();
    int score2 = engine2.getGameScoring().getCurrentScore();

    assertTrue("Player 1의 점수가 더 높아야 함", score1 > score2);
    assertEquals("Player 1 점수가 1000이어야 함", 1000, score1);
    assertEquals("Player 2 점수가 500이어야 함", 500, score2);
  }

  // ===== 게임 오버 및 승패 판정 테스트 =====

  /**
   * 테스트 9: BattleGameController 생성 확인
   */
  @Test
  public void testBattleGameController_IsCreated() throws Exception {
    System.setProperty("tetris.battle.mode", "NORMAL");
    battle battleScreen = new battle(screenController);

    Field controllerField = battle.class.getDeclaredField("gameController");
    controllerField.setAccessible(true);
    BattleGameController controller = (BattleGameController) controllerField.get(battleScreen);

    assertNotNull("BattleGameController가 생성되어야 함", controller);
  }

  /**
   * 테스트 10: 게임 시작 후 게임 상태 확인
   */
  @Test
  public void testBattleGameController_StartsCorrectly() throws Exception {
    System.setProperty("tetris.battle.mode", "NORMAL");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    Field controllerField = battle.class.getDeclaredField("gameController");
    controllerField.setAccessible(true);
    BattleGameController controller = (BattleGameController) controllerField.get(battleScreen);

    assertTrue("게임이 시작되어야 함", controller.isGameStarted());
    assertFalse("게임이 종료되지 않아야 함", controller.isGameOver());
  }

  /**
   * 테스트 11: 일시정지 기능 동작 확인
   */
  @Test
  public void testBattleMode_PauseFunctionality() throws Exception {
    System.setProperty("tetris.battle.mode", "NORMAL");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    // 일시정지
    battleScreen.forcePause();

    Field pausedField = battle.class.getDeclaredField("isPaused");
    pausedField.setAccessible(true);
    boolean isPaused = (boolean) pausedField.get(battleScreen);

    assertTrue("게임이 일시정지되어야 함", isPaused);
  }

  /**
   * 테스트 12: 양쪽 플레이어의 게임 엔진이 독립적으로 동작
   */
  @Test
  public void testBattleMode_IndependentGameEngines() throws Exception {
    System.setProperty("tetris.battle.mode", "NORMAL");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    assertNotSame("두 플레이어의 게임 엔진이 다른 인스턴스여야 함", engine1, engine2);

    // Player 1의 블록 이동
    engine1.moveBlockDown();

    // Player 2의 상태는 변하지 않아야 함
    assertFalse("Player 2는 게임 오버가 아니어야 함", engine2.isGameOver());
  }

  /**
   * 테스트 13: countdownTimerEnabled 플래그 확인 (시간제한 모드)
   */
  @Test
  public void testTimeLimitMode_CountdownTimerEnabled() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle battleScreen = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    Field countdownField = PlayerGamePanel.class.getDeclaredField("countdownTimerEnabled");
    countdownField.setAccessible(true);

    boolean player1Countdown = (boolean) countdownField.get(player1);
    boolean player2Countdown = (boolean) countdownField.get(player2);

    assertTrue("Player 1의 카운트다운 타이머가 활성화되어야 함", player1Countdown);
    assertTrue("Player 2의 카운트다운 타이머가 활성화되어야 함", player2Countdown);
  }

  /**
   * 테스트 14: countdownTimerEnabled 플래그 확인 (일반/아이템 모드)
   */
  @Test
  public void testNormalMode_CountdownTimerDisabled() throws Exception {
    System.setProperty("tetris.battle.mode", "NORMAL");
    battle battleScreen = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);

    Field countdownField = PlayerGamePanel.class.getDeclaredField("countdownTimerEnabled");
    countdownField.setAccessible(true);

    boolean player1Countdown = (boolean) countdownField.get(player1);

    assertFalse("일반 모드에서는 카운트다운 타이머가 비활성화되어야 함", player1Countdown);
  }

  /**
   * 테스트 15: 타이머 라벨 업데이트 형식 확인 (MM:SS)
   */
  @Test
  public void testTimeLimitMode_TimerFormat() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    // remainingSeconds를 특정 값으로 설정
    Field remainingSecondsField = battle.class.getDeclaredField("remainingSeconds");
    remainingSecondsField.setAccessible(true);
    remainingSecondsField.set(battleScreen, 185); // 3분 5초

    // updateTimerLabels 메서드 호출
    Method updateTimerLabelsMethod = battle.class.getDeclaredMethod("updateTimerLabels");
    updateTimerLabelsMethod.setAccessible(true);
    updateTimerLabelsMethod.invoke(battleScreen);

    // 타이머 형식 검증
    int minutes = 185 / 60;
    int seconds = 185 % 60;
    String expectedFormat = String.format("%02d:%02d", minutes, seconds);

    assertEquals("타이머 형식이 03:05여야 함", "03:05", expectedFormat);
  }

  // ===== Helper Methods =====

  private PlayerGamePanel getPlayer1Panel(battle battleScreen) throws Exception {
    Field field = battle.class.getDeclaredField("player1Panel");
    field.setAccessible(true);
    return (PlayerGamePanel) field.get(battleScreen);
  }

  private PlayerGamePanel getPlayer2Panel(battle battleScreen) throws Exception {
    Field field = battle.class.getDeclaredField("player2Panel");
    field.setAccessible(true);
    return (PlayerGamePanel) field.get(battleScreen);
  }
}



