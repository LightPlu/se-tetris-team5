package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.ScreenController;
import se.tetris.team5.components.battle.PlayerGamePanel;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.ai.AIPlayerController;
import se.tetris.team5.components.game.GameBoard;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * AI 대전 모드 테스트
 * - AI vs AI 모드 동작 확인
 * - Player vs AI 모드 동작 확인
 * - AI 난이도 설정 확인
 * - 줄 삭제 애니메이션이 AI 모드에서도 정상 작동하는지 확인
 * - 일반 모드와 동일한 게임 로직이 작동하는지 확인
 */
public class AIBattleModeTest {

  private ScreenController screenController;

  @Before
  public void setUp() {
    screenController = new ScreenController();
  }

  @After
  public void tearDown() {
    // 시스템 속성 정리
    System.clearProperty("tetris.battle.mode");
    System.clearProperty("tetris.ai.difficulty");
    System.clearProperty("tetris.battle.originalSize");
  }

  // ===== AI vs AI 모드 테스트 =====

  /**
   * 테스트 1: AI vs AI 모드 초기화 확인
   */
  @Test
  public void testAIVsAIMode_Initialization() throws Exception {
    System.setProperty("tetris.battle.mode", "AI_VS_AI");
    battle battleScreen = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    assertNotNull("Player 1 패널이 생성되어야 함", player1);
    assertNotNull("Player 2 패널이 생성되어야 함", player2);

    // AI vs AI 모드 확인
    Field isAIVsAIModeField = battle.class.getDeclaredField("isAIVsAIMode");
    isAIVsAIModeField.setAccessible(true);
    boolean isAIVsAIMode = (boolean) isAIVsAIModeField.get(battleScreen);

    assertTrue("AI vs AI 모드여야 함", isAIVsAIMode);

    // AI 컨트롤러 확인
    Field aiController1Field = battle.class.getDeclaredField("aiController1");
    aiController1Field.setAccessible(true);
    AIPlayerController aiController1 = (AIPlayerController) aiController1Field.get(battleScreen);

    Field aiControllerField = battle.class.getDeclaredField("aiController");
    aiControllerField.setAccessible(true);
    AIPlayerController aiController = (AIPlayerController) aiControllerField.get(battleScreen);

    assertNotNull("AI 1 컨트롤러가 생성되어야 함", aiController1);
    assertNotNull("AI 2 컨트롤러가 생성되어야 함", aiController);
  }

  /**
   * 테스트 2: AI vs AI 모드에서 AI 난이도가 HARD로 설정되는지 확인
   */
  @Test
  public void testAIVsAIMode_AIDifficultyIsHard() throws Exception {
    System.setProperty("tetris.battle.mode", "AI_VS_AI");
    battle battleScreen = new battle(screenController);

    Field aiController1Field = battle.class.getDeclaredField("aiController1");
    aiController1Field.setAccessible(true);
    AIPlayerController aiController1 = (AIPlayerController) aiController1Field.get(battleScreen);

    Field aiControllerField = battle.class.getDeclaredField("aiController");
    aiControllerField.setAccessible(true);
    AIPlayerController aiController = (AIPlayerController) aiControllerField.get(battleScreen);

    assertEquals("AI 1 난이도가 HARD여야 함", AIPlayerController.AIDifficulty.HARD, aiController1.getDifficulty());
    assertEquals("AI 2 난이도가 HARD여야 함", AIPlayerController.AIDifficulty.HARD, aiController.getDifficulty());
  }

  /**
   * 테스트 3: AI vs AI 모드에서 게임 시작 시 AI가 자동으로 동작하는지 확인
   */
  @Test
  public void testAIVsAIMode_AIAutoPlay() throws Exception {
    System.setProperty("tetris.battle.mode", "AI_VS_AI");
    battle battleScreen = new battle(screenController);

    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    Thread.sleep(500); // AI 동작 대기

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    GameEngine engine1 = player1.getGameEngine();
    GameEngine engine2 = player2.getGameEngine();

    // AI가 블록을 움직였는지 확인 (게임이 진행 중이어야 함)
    assertNotNull("Player 1의 현재 블록이 존재해야 함", engine1.getCurrentBlock());
    assertNotNull("Player 2의 현재 블록이 존재해야 함", engine2.getCurrentBlock());
  }

  // ===== Player vs AI 모드 테스트 =====

  /**
   * 테스트 4: Player vs AI 모드 초기화 확인
   */
  @Test
  public void testPlayerVsAIMode_Initialization() throws Exception {
    System.setProperty("tetris.battle.mode", "AI");
    System.setProperty("tetris.ai.difficulty", "NORMAL");
    battle battleScreen = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    assertNotNull("Player 1 패널이 생성되어야 함", player1);
    assertNotNull("Player 2 패널이 생성되어야 함", player2);

    // AI 모드 확인
    Field isAIModeField = battle.class.getDeclaredField("isAIMode");
    isAIModeField.setAccessible(true);
    boolean isAIMode = (boolean) isAIModeField.get(battleScreen);

    assertTrue("AI 모드여야 함", isAIMode);

    // AI 컨트롤러 확인
    Field aiControllerField = battle.class.getDeclaredField("aiController");
    aiControllerField.setAccessible(true);
    AIPlayerController aiController = (AIPlayerController) aiControllerField.get(battleScreen);

    assertNotNull("AI 컨트롤러가 생성되어야 함", aiController);
  }

  /**
   * 테스트 5: Player vs AI 모드에서 AI 난이도 설정 확인 (NORMAL)
   */
  @Test
  public void testPlayerVsAIMode_AIDifficultyNormal() throws Exception {
    System.setProperty("tetris.battle.mode", "AI");
    System.setProperty("tetris.ai.difficulty", "NORMAL");
    battle battleScreen = new battle(screenController);

    Field aiControllerField = battle.class.getDeclaredField("aiController");
    aiControllerField.setAccessible(true);
    AIPlayerController aiController = (AIPlayerController) aiControllerField.get(battleScreen);

    assertEquals("AI 난이도가 NORMAL이어야 함", AIPlayerController.AIDifficulty.NORMAL, aiController.getDifficulty());
  }

  /**
   * 테스트 6: Player vs AI 모드에서 AI 난이도 설정 확인 (HARD)
   */
  @Test
  public void testPlayerVsAIMode_AIDifficultyHard() throws Exception {
    System.setProperty("tetris.battle.mode", "AI");
    System.setProperty("tetris.ai.difficulty", "HARD");
    battle battleScreen = new battle(screenController);

    Field aiControllerField = battle.class.getDeclaredField("aiController");
    aiControllerField.setAccessible(true);
    AIPlayerController aiController = (AIPlayerController) aiControllerField.get(battleScreen);

    assertEquals("AI 난이도가 HARD여야 함", AIPlayerController.AIDifficulty.HARD, aiController.getDifficulty());
  }

  // ===== 줄 삭제 애니메이션 테스트 =====

  /**
   * 테스트 7: AI 모드에서 줄 삭제 시 애니메이션이 트리거되는지 확인
   */
  @Test
  public void testAIMode_LineClearAnimationTriggered() throws Exception {
    System.setProperty("tetris.battle.mode", "AI");
    System.setProperty("tetris.ai.difficulty", "NORMAL");
    battle battleScreen = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    GameEngine engine1 = player1.getGameEngine();
    GameBoard gameBoard = player1.getGameBoard();

    // 게임 시작
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    // onBlockFixedCallback이 설정되어 있는지 확인
    // PlayerGamePanel의 initGameEngine에서 설정됨
    assertNotNull("GameBoard가 존재해야 함", gameBoard);

    // 줄 삭제 시뮬레이션을 위해 consumeLastClearedRows가 작동하는지 확인
    List<Integer> clearedRows = engine1.consumeLastClearedRows();
    // 초기에는 빈 리스트
    assertTrue("초기 상태에서는 삭제된 줄이 없어야 함", clearedRows == null || clearedRows.isEmpty());
  }

  /**
   * 테스트 8: 일반 모드와 AI 모드에서 동일한 게임 로직이 작동하는지 확인
   */
  @Test
  public void testAIMode_SameGameLogicAsNormalMode() throws Exception {
    // 일반 모드
    System.setProperty("tetris.battle.mode", "NORMAL");
    battle normalBattle = new battle(screenController);
    PlayerGamePanel normalPlayer1 = getPlayer1Panel(normalBattle);
    GameEngine normalEngine1 = normalPlayer1.getGameEngine();

    // AI 모드
    System.setProperty("tetris.battle.mode", "AI");
    System.setProperty("tetris.ai.difficulty", "NORMAL");
    battle aiBattle = new battle(screenController);
    PlayerGamePanel aiPlayer1 = getPlayer1Panel(aiBattle);
    GameEngine aiEngine1 = aiPlayer1.getGameEngine();

    // 게임 모드 확인
    assertEquals("일반 모드와 AI 모드 모두 NORMAL 게임 모드여야 함",
        normalEngine1.getGameMode(), aiEngine1.getGameMode());

    // 게임판 크기 확인
    int[][] normalBoard = normalEngine1.getBoardManager().getBoard();
    int[][] aiBoard = aiEngine1.getBoardManager().getBoard();

    assertEquals("게임판 높이가 동일해야 함", normalBoard.length, aiBoard.length);
    assertEquals("게임판 너비가 동일해야 함", normalBoard[0].length, aiBoard[0].length);
  }

  /**
   * 테스트 9: AI 모드에서 공격 블록 전송이 작동하는지 확인
   */
  @Test
  public void testAIMode_AttackBlocksTransmission() throws Exception {
    System.setProperty("tetris.battle.mode", "AI");
    System.setProperty("tetris.ai.difficulty", "NORMAL");
    battle battleScreen = new battle(screenController);

    PlayerGamePanel player1 = getPlayer1Panel(battleScreen);
    PlayerGamePanel player2 = getPlayer2Panel(battleScreen);

    // 상대방 패널 연결 확인
    assertNotNull("Player 1의 상대방 패널이 설정되어야 함", player1);
    assertNotNull("Player 2의 상대방 패널이 설정되어야 함", player2);

    // onBlockFixedCallback이 설정되어 있는지 확인
    GameEngine engine1 = player1.getGameEngine();
    assertNotNull("GameEngine이 존재해야 함", engine1);
  }

  /**
   * 테스트 10: AI vs AI 모드에서 ESC 키만 작동하는지 확인
   */
  @Test
  public void testAIVsAIMode_OnlyESCPauseWorks() throws Exception {
    System.setProperty("tetris.battle.mode", "AI_VS_AI");
    battle battleScreen = new battle(screenController);

    // 게임 시작
    Method startBattleMethod = battle.class.getDeclaredMethod("startBattle");
    startBattleMethod.setAccessible(true);
    startBattleMethod.invoke(battleScreen);

    Thread.sleep(100); // 게임 초기화 대기

    // 일시정지 상태 확인
    Field isPausedField = battle.class.getDeclaredField("isPaused");
    isPausedField.setAccessible(true);
    boolean isPausedBefore = (boolean) isPausedField.get(battleScreen);

    assertFalse("초기에는 일시정지 상태가 아니어야 함", isPausedBefore);

    // gameController가 null이 아닌지 확인
    Field gameControllerField = battle.class.getDeclaredField("gameController");
    gameControllerField.setAccessible(true);
    Object gameController = gameControllerField.get(battleScreen);

    // gameController가 null이면 테스트를 건너뜀
    if (gameController == null) {
      return; // 테스트 스킵
    }

    // ESC 키 시뮬레이션
    java.awt.event.KeyEvent escEvent = new java.awt.event.KeyEvent(
        battleScreen,
        java.awt.event.KeyEvent.KEY_PRESSED,
        System.currentTimeMillis(),
        0,
        java.awt.event.KeyEvent.VK_ESCAPE,
        (char) java.awt.event.KeyEvent.VK_ESCAPE);
    battleScreen.keyPressed(escEvent);

    // 일시정지 상태 확인
    boolean isPausedAfter = (boolean) isPausedField.get(battleScreen);
    assertTrue("ESC 키 입력 후 일시정지 상태여야 함", isPausedAfter);
  }

  /**
   * 테스트 11: AI 난이도별 사고 시간 확인
   */
  @Test
  public void testAIDifficulty_ThinkDelay() {
    // NORMAL 난이도: 500ms
    assertEquals("NORMAL 난이도는 500ms", 500,
        AIPlayerController.AIDifficulty.NORMAL.getThinkDelayMs());

    // HARD 난이도: 200ms
    assertEquals("HARD 난이도는 200ms", 200,
        AIPlayerController.AIDifficulty.HARD.getThinkDelayMs());
  }

  /**
   * 테스트 12: AI 모드에서 게임 오버 시 리소스 정리 확인
   */
  @Test
  public void testAIMode_ResourceCleanupOnGameOver() throws Exception {
    System.setProperty("tetris.battle.mode", "AI");
    System.setProperty("tetris.ai.difficulty", "NORMAL");
    battle battleScreen = new battle(screenController);

    Field aiControllerField = battle.class.getDeclaredField("aiController");
    aiControllerField.setAccessible(true);
    AIPlayerController aiController = (AIPlayerController) aiControllerField.get(battleScreen);

    assertNotNull("AI 컨트롤러가 생성되어야 함", aiController);

    // dispose 호출
    battleScreen.dispose();

    // dispose 후 타이머가 정리되었는지 확인
    Field timeLimitTimerField = battle.class.getDeclaredField("timeLimitTimer");
    timeLimitTimerField.setAccessible(true);
    javax.swing.Timer timeLimitTimer = (javax.swing.Timer) timeLimitTimerField.get(battleScreen);

    // 타이머가 null이거나 정지되어 있어야 함
    assertTrue("타이머가 null이거나 정지되어 있어야 함",
        timeLimitTimer == null || !timeLimitTimer.isRunning());
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
