package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.ScreenController;
import se.tetris.team5.components.battle.PlayerGamePanel;
import se.tetris.team5.components.game.DoubleScoreBadge;
import se.tetris.team5.gamelogic.GameEngine;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 대전 - 아이템 모드 점수 2배 타이머 표시 테스트
 * 점수 2배 아이템 사용 시 오른쪽 점수 패널에 20초 타이머가 표시되는지 검증
 */
public class BattleDoubleScoreTimerTest {

  private battle battleScreen;
  private ScreenController screenController;

  @Before
  public void setUp() {
    screenController = new ScreenController();
    System.setProperty("tetris.battle.mode", "ITEM");
    battleScreen = new battle(screenController);
  }

  /**
   * 테스트 1: PlayerGamePanel에 DoubleScoreBadge가 생성되는지 확인
   */
  @Test
  public void testDoubleScoreBadge_IsCreated() throws Exception {
    PlayerGamePanel player1 = getPlayer1Panel();
    
    Field badgeField = PlayerGamePanel.class.getDeclaredField("doubleScoreBadge");
    badgeField.setAccessible(true);
    DoubleScoreBadge badge = (DoubleScoreBadge) badgeField.get(player1);
    
    assertNotNull("DoubleScoreBadge가 생성되어야 함", badge);
  }

  /**
   * 테스트 2: 점수 2배 효과 활성화 시 DoubleScoreBadge가 표시되는지 확인
   */
  @Test
  public void testDoubleScoreBadge_ShowsWhenActive() throws Exception {
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();
    
    // 점수 2배 효과 활성화
    engine1.activateDoubleScore(20_000);
    
    assertTrue("점수 2배 효과가 활성화되어야 함", engine1.isDoubleScoreActive());
    
    long remainingMillis = engine1.getDoubleScoreRemainingMillis();
    assertTrue("남은 시간이 0보다 커야 함", remainingMillis > 0);
    assertTrue("남은 시간이 20초 이하여야 함", remainingMillis <= 20_000);
  }

  /**
   * 테스트 3: 점수 2배 타이머가 시간에 따라 감소하는지 확인
   */
  @Test
  public void testDoubleScoreBadge_TimerDecreases() throws Exception {
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();
    
    // 점수 2배 효과 활성화 (5초)
    engine1.activateDoubleScore(5000);
    
    long initialRemaining = engine1.getDoubleScoreRemainingMillis();
    
    // 1초 대기
    Thread.sleep(1000);
    
    long afterRemaining = engine1.getDoubleScoreRemainingMillis();
    
    assertTrue("타이머가 감소해야 함", afterRemaining < initialRemaining);
  }

  /**
   * 테스트 4: 점수 2배 효과 종료 후 타이머가 0이 되는지 확인
   */
  @Test
  public void testDoubleScoreBadge_ExpiresCorrectly() throws Exception {
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();
    
    // 점수 2배 효과 활성화 (1초)
    engine1.activateDoubleScore(1000);
    
    assertTrue("점수 2배 효과가 활성화되어야 함", engine1.isDoubleScoreActive());
    
    // 1.5초 대기
    Thread.sleep(1500);
    
    assertFalse("점수 2배 효과가 종료되어야 함", engine1.isDoubleScoreActive());
    assertEquals("남은 시간이 0이어야 함", 0, engine1.getDoubleScoreRemainingMillis());
  }

  /**
   * 테스트 5: 양쪽 플레이어가 독립적으로 점수 2배 효과를 가질 수 있는지 확인
   */
  @Test
  public void testDoubleScoreBadge_IndependentForEachPlayer() throws Exception {
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel();
    PlayerGamePanel player2 = getPlayer2Panel();
    
    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();
    
    // Player 1만 점수 2배 효과 활성화
    engine1.activateDoubleScore(10_000);
    
    assertTrue("Player 1의 점수 2배 효과가 활성화되어야 함", engine1.isDoubleScoreActive());
    assertFalse("Player 2의 점수 2배 효과는 비활성화되어야 함", engine2.isDoubleScoreActive());
    
    assertTrue("Player 1의 남은 시간이 0보다 커야 함", engine1.getDoubleScoreRemainingMillis() > 0);
    assertEquals("Player 2의 남은 시간이 0이어야 함", 0, engine2.getDoubleScoreRemainingMillis());
  }

  /**
   * 테스트 6: DoubleScoreBadge의 크기가 적절한지 확인
   */
  @Test
  public void testDoubleScoreBadge_HasCorrectSize() throws Exception {
    PlayerGamePanel player1 = getPlayer1Panel();
    
    Field badgeField = PlayerGamePanel.class.getDeclaredField("doubleScoreBadge");
    badgeField.setAccessible(true);
    DoubleScoreBadge badge = (DoubleScoreBadge) badgeField.get(player1);
    
    assertNotNull("DoubleScoreBadge가 생성되어야 함", badge);
    
    // 크기 확인 (90x24로 설정되어 있어야 함)
    java.awt.Dimension preferredSize = badge.getPreferredSize();
    assertEquals("너비가 90이어야 함", 90, preferredSize.width);
    assertEquals("높이가 24여야 함", 24, preferredSize.height);
  }

  /**
   * 테스트 7: 점수 2배 효과 중 점수가 실제로 2배로 적용되는지 확인
   */
  @Test
  public void testDoubleScore_ActuallyDoubles() throws Exception {
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();
    
    int initialScore = engine1.getGameScoring().getCurrentScore();
    
    // 점수 2배 효과 활성화
    engine1.activateDoubleScore(20_000);
    
    // 블록 이동으로 점수 획득
    engine1.moveBlockDown();
    
    int scoreAfterMove = engine1.getGameScoring().getCurrentScore();
    
    // 점수가 증가했는지 확인 (2배 효과로 2점 증가)
    assertTrue("점수가 증가해야 함", scoreAfterMove > initialScore);
    assertEquals("점수가 2점 증가해야 함 (1점의 2배)", initialScore + 2, scoreAfterMove);
  }

  /**
   * 테스트 8: 시간제한 모드에서는 countdownTimerEnabled가 true인지 확인
   */
  @Test
  public void testTimeLimitMode_CountdownEnabled() throws Exception {
    System.setProperty("tetris.battle.mode", "TIMELIMIT");
    battle timeLimitBattle = new battle(screenController);
    
    PlayerGamePanel player1 = getPlayer1PanelFrom(timeLimitBattle);
    
    Field countdownField = PlayerGamePanel.class.getDeclaredField("countdownTimerEnabled");
    countdownField.setAccessible(true);
    boolean countdownEnabled = (boolean) countdownField.get(player1);
    
    assertTrue("시간제한 모드에서 countdownTimerEnabled가 true여야 함", countdownEnabled);
  }

  /**
   * 테스트 9: 아이템 모드에서는 countdownTimerEnabled가 false인지 확인
   */
  @Test
  public void testItemMode_CountdownDisabled() throws Exception {
    PlayerGamePanel player1 = getPlayer1Panel();
    
    Field countdownField = PlayerGamePanel.class.getDeclaredField("countdownTimerEnabled");
    countdownField.setAccessible(true);
    boolean countdownEnabled = (boolean) countdownField.get(player1);
    
    assertFalse("아이템 모드에서 countdownTimerEnabled가 false여야 함", countdownEnabled);
  }

  /**
   * 테스트 10: 점수 2배 효과가 여러 번 활성화될 수 있는지 확인
   */
  @Test
  public void testDoubleScore_CanBeActivatedMultipleTimes() throws Exception {
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    PlayerGamePanel player1 = getPlayer1Panel();
    GameEngine engine1 = player1.getGameEngine();
    
    // 첫 번째 활성화
    engine1.activateDoubleScore(1000);
    assertTrue("첫 번째 활성화가 성공해야 함", engine1.isDoubleScoreActive());
    
    // 1.5초 대기 (효과 종료)
    Thread.sleep(1500);
    assertFalse("효과가 종료되어야 함", engine1.isDoubleScoreActive());
    
    // 두 번째 활성화
    engine1.activateDoubleScore(2000);
    assertTrue("두 번째 활성화가 성공해야 함", engine1.isDoubleScoreActive());
    
    long remaining = engine1.getDoubleScoreRemainingMillis();
    assertTrue("남은 시간이 0보다 커야 함", remaining > 0);
    assertTrue("남은 시간이 2초 이하여야 함", remaining <= 2000);
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
}

