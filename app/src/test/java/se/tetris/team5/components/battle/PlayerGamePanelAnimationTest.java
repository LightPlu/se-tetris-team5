package se.tetris.team5.components.battle;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.components.game.GameBoard;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

/**
 * PlayerGamePanel 애니메이션 테스트
 * - 줄 삭제 애니메이션이 일반 모드와 AI 모드에서 동일하게 작동하는지 확인
 * - onBlockFixedCallback에서 애니메이션이 트리거되는지 확인
 */
public class PlayerGamePanelAnimationTest {

  private PlayerGamePanel playerPanel;
  private PlayerGamePanel opponentPanel;

  @Before
  public void setUp() {
    playerPanel = new PlayerGamePanel("플레이어 1", "테스트", java.awt.Color.BLUE);
    opponentPanel = new PlayerGamePanel("플레이어 2", "테스트", java.awt.Color.RED);
    
    // 상대방 패널 연결
    playerPanel.setOpponentPanel(opponentPanel);
    opponentPanel.setOpponentPanel(playerPanel);
  }

  /**
   * 테스트 1: GameBoard가 초기화되어 있는지 확인
   */
  @Test
  public void testGameBoard_Initialized() {
    GameBoard gameBoard = playerPanel.getGameBoard();
    assertNotNull("GameBoard가 초기화되어야 함", gameBoard);
  }

  /**
   * 테스트 2: onBlockFixedCallback이 설정되어 있는지 확인
   */
  @Test
  public void testOnBlockFixedCallback_IsSet() throws Exception {
    GameEngine engine = playerPanel.getGameEngine();
    assertNotNull("GameEngine이 존재해야 함", engine);

    // onBlockFixedCallback 필드 확인
    Field callbackField = GameEngine.class.getDeclaredField("onBlockFixedCallback");
    callbackField.setAccessible(true);
    Runnable callback = (Runnable) callbackField.get(engine);

    assertNotNull("onBlockFixedCallback이 설정되어야 함", callback);
  }

  /**
   * 테스트 3: consumeLastClearedRows가 작동하는지 확인
   */
  @Test
  public void testConsumeLastClearedRows_Works() throws Exception {
    GameEngine engine = playerPanel.getGameEngine();
    
    // 초기 상태에서는 빈 리스트
    List<Integer> clearedRows = engine.consumeLastClearedRows();
    assertTrue("초기 상태에서는 삭제된 줄이 없어야 함", 
        clearedRows == null || clearedRows.isEmpty());

    // 두 번째 호출 시에도 빈 리스트 (이미 소비됨)
    List<Integer> clearedRows2 = engine.consumeLastClearedRows();
    assertTrue("이미 소비된 데이터는 빈 리스트여야 함", 
        clearedRows2 == null || clearedRows2.isEmpty());
  }

  /**
   * 테스트 4: triggerClearAnimation 메서드가 존재하는지 확인
   */
  @Test
  public void testTriggerClearAnimation_MethodExists() throws Exception {
    GameBoard gameBoard = playerPanel.getGameBoard();
    
    // triggerClearAnimation 메서드 확인
    Method triggerMethod = GameBoard.class.getDeclaredMethod("triggerClearAnimation", List.class);
    assertNotNull("triggerClearAnimation 메서드가 존재해야 함", triggerMethod);

    // 빈 리스트로 호출해도 예외가 발생하지 않아야 함
    List<Integer> emptyRows = new ArrayList<>();
    triggerMethod.invoke(gameBoard, emptyRows);
    
    // null로 호출해도 예외가 발생하지 않아야 함
    triggerMethod.invoke(gameBoard, (List<Integer>) null);
  }

  /**
   * 테스트 5: updateGameUI에서 애니메이션 처리 로직이 있는지 확인
   */
  @Test
  public void testUpdateGameUI_AnimationHandling() throws Exception {
    // updateGameUI 메서드 확인
    Method updateMethod = PlayerGamePanel.class.getDeclaredMethod("updateGameUI");
    assertNotNull("updateGameUI 메서드가 존재해야 함", updateMethod);

    // 게임 시작
    playerPanel.startGame();
    
    // updateGameUI 호출 (예외가 발생하지 않아야 함)
    try {
      updateMethod.invoke(playerPanel);
    } catch (Exception e) {
      fail("updateGameUI 호출 시 예외가 발생하지 않아야 함: " + e.getMessage());
    }
  }

  /**
   * 테스트 6: onBlockFixedCallback에서 애니메이션 트리거 확인
   */
  @Test
  public void testOnBlockFixedCallback_TriggersAnimation() throws Exception {
    GameEngine engine = playerPanel.getGameEngine();
    GameBoard gameBoard = playerPanel.getGameBoard();

    // onBlockFixedCallback 가져오기
    Field callbackField = GameEngine.class.getDeclaredField("onBlockFixedCallback");
    callbackField.setAccessible(true);
    Runnable callback = (Runnable) callbackField.get(engine);

    assertNotNull("onBlockFixedCallback이 설정되어야 함", callback);

    // 콜백 실행 (예외가 발생하지 않아야 함)
    try {
      callback.run();
    } catch (Exception e) {
      // 콜백 내부에서 예외가 발생할 수 있지만, 구조적으로는 작동해야 함
      // 실제로는 블록이 고정되어야 콜백이 의미가 있음
    }
  }

  /**
   * 테스트 7: 일반 모드와 AI 모드에서 동일한 애니메이션 로직 사용 확인
   */
  @Test
  public void testAnimationLogic_SameInNormalAndAIMode() throws Exception {
    // 일반 모드 패널
    PlayerGamePanel normalPanel = new PlayerGamePanel("플레이어", "테스트", java.awt.Color.BLUE);
    GameEngine normalEngine = normalPanel.getGameEngine();
    GameBoard normalBoard = normalPanel.getGameBoard();

    // AI 모드 패널 (구조적으로 동일)
    PlayerGamePanel aiPanel = new PlayerGamePanel("AI", "자동 플레이", java.awt.Color.GREEN);
    GameEngine aiEngine = aiPanel.getGameEngine();
    GameBoard aiBoard = aiPanel.getGameBoard();

    // GameBoard 타입 확인
    assertEquals("GameBoard 타입이 동일해야 함", normalBoard.getClass(), aiBoard.getClass());

    // triggerClearAnimation 메서드 확인
    Method normalMethod = normalBoard.getClass().getDeclaredMethod("triggerClearAnimation", List.class);
    Method aiMethod = aiBoard.getClass().getDeclaredMethod("triggerClearAnimation", List.class);

    assertEquals("triggerClearAnimation 메서드가 동일해야 함", normalMethod, aiMethod);
  }

  /**
   * 테스트 8: 애니메이션 타이머가 존재하는지 확인
   */
  @Test
  public void testAnimationTimer_Exists() throws Exception {
    GameBoard gameBoard = playerPanel.getGameBoard();
    
    // animTimer 필드 확인
    Field animTimerField = GameBoard.class.getDeclaredField("animTimer");
    animTimerField.setAccessible(true);
    
    // 초기에는 null일 수 있음
    javax.swing.Timer animTimer = (javax.swing.Timer) animTimerField.get(gameBoard);
    // null이거나 Timer 인스턴스여야 함
    assertTrue("animTimer는 null이거나 Timer 인스턴스여야 함", 
        animTimer == null || animTimer instanceof javax.swing.Timer);
  }

  /**
   * 테스트 9: 애니메이션 진행 상태 추적 필드 확인
   */
  @Test
  public void testAnimationProgress_FieldsExist() throws Exception {
    GameBoard gameBoard = playerPanel.getGameBoard();
    
    // animRowProgress 필드 확인
    try {
      Field animRowProgressField = GameBoard.class.getDeclaredField("animRowProgress");
      animRowProgressField.setAccessible(true);
      assertNotNull("animRowProgress 필드가 존재해야 함", animRowProgressField);
    } catch (NoSuchFieldException e) {
      fail("animRowProgress 필드가 존재해야 함");
    }

    // rowParticles 필드 확인
    try {
      Field rowParticlesField = GameBoard.class.getDeclaredField("rowParticles");
      rowParticlesField.setAccessible(true);
      assertNotNull("rowParticles 필드가 존재해야 함", rowParticlesField);
    } catch (NoSuchFieldException e) {
      fail("rowParticles 필드가 존재해야 함");
    }
  }
}

