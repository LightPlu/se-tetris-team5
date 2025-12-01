package se.tetris.team5.gamelogic.ai;

import se.tetris.team5.gamelogic.GameEngine;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * AI 플레이어 컨트롤러
 * AI의 생명주기와 행동을 관리하는 클래스
 */
public class AIPlayerController {

  private GameEngine gameEngine;
  private TetrisAI ai;
  private Timer aiTimer;
  private boolean isActive = false;
  private boolean isPaused = false;
  private Runnable onGameOverCallback;
  private Runnable onMoveCallback; // AI가 행동할 때마다 호출되는 콜백 (UI 업데이트용)

  // AI 행동 주기 (밀리초)
  private static final int AI_ACTION_INTERVAL = 200;

  /**
   * AI 플레이어 컨트롤러 생성
   * 
   * @param gameEngine AI가 제어할 게임 엔진
   */
  public AIPlayerController(GameEngine gameEngine) {
    this.gameEngine = gameEngine;
    this.ai = new TetrisAI(gameEngine);
  }

  /**
   * AI 시작
   */
  public void start() {
    if (isActive) {
      return; // 이미 시작됨
    }

    isActive = true;
    isPaused = false;

    // AI 타이머 생성 및 시작
    if (aiTimer != null) {
      aiTimer.stop();
    }

    aiTimer = new Timer(AI_ACTION_INTERVAL, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // 게임 오버 체크를 먼저 수행 (게임 오버 시 즉시 정지)
        if (gameEngine.isGameOver()) {
          stop(); // 게임 오버 시 AI 즉시 정지
          if (onGameOverCallback != null) {
            onGameOverCallback.run();
          }
          return;
        }

        if (!isPaused && isActive && ai != null) {
          ai.makeMove();

          // UI 업데이트 콜백 호출
          if (onMoveCallback != null) {
            onMoveCallback.run();
          }
        }
      }
    });

    aiTimer.start();
  }

  /**
   * AI 일시정지
   */
  public void pause() {
    isPaused = true;
    if (aiTimer != null) {
      aiTimer.stop();
    }
  }

  /**
   * AI 재개
   */
  public void resume() {
    isPaused = false;
    if (aiTimer != null && isActive) {
      aiTimer.start();
    }
  }

  /**
   * AI 정지
   */
  public void stop() {
    isActive = false;
    isPaused = false;
    if (aiTimer != null) {
      aiTimer.stop();
      aiTimer = null;
    }
  }

  /**
   * 게임 오버 콜백 설정
   * 
   * @param callback 게임 오버 시 호출될 콜백
   */
  public void setOnGameOverCallback(Runnable callback) {
    this.onGameOverCallback = callback;
  }

  /**
   * AI 행동 콜백 설정 (UI 업데이트용)
   * 
   * @param callback AI가 행동할 때마다 호출될 콜백
   */
  public void setOnMoveCallback(Runnable callback) {
    this.onMoveCallback = callback;
  }

  /**
   * AI가 활성화되어 있는지 확인
   * 
   * @return true if active, false otherwise
   */
  public boolean isActive() {
    return isActive;
  }

  /**
   * AI가 일시정지 상태인지 확인
   * 
   * @return true if paused, false otherwise
   */
  public boolean isPaused() {
    return isPaused;
  }

  /**
   * 리소스 정리
   */
  public void dispose() {
    stop();
    ai = null;
    gameEngine = null;
    onGameOverCallback = null;
    onMoveCallback = null;
  }
}
