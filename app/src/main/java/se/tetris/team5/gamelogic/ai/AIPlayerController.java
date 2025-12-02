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

  /**
   * AI 난이도 설정
   * 난이도는 AI의 사고 시간(thinkDelay)으로만 조절됩니다.
   */
  public enum AIDifficulty {
    /** 보통 난이도: 500ms 사고 시간 */
    NORMAL(500),
    /** 어려움 난이도: 200ms 사고 시간 */
    HARD(200);

    private final int thinkDelayMs;

    AIDifficulty(int thinkDelayMs) {
      this.thinkDelayMs = thinkDelayMs;
    }

    /**
     * 난이도에 해당하는 사고 시간 반환
     *
     * @return 사고 시간 (밀리초)
     */
    public int getThinkDelayMs() {
      return thinkDelayMs;
    }
  }

  private AIDifficulty difficulty = AIDifficulty.NORMAL;

  /** AI 행동 주기 (밀리초) - 고정값 (난이도는 사고 시간으로만 조절) */
  private static final int AI_ACTION_INTERVAL_MS = 200;

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
   * AI 난이도 설정
   * 난이도는 사고 시간으로만 조절됩니다. 행동 주기는 고정입니다.
   *
   * @param difficulty AI 난이도 (NORMAL: 보통, HARD: 어려움)
   */
  public void setDifficulty(AIDifficulty difficulty) {
    if (difficulty == null) {
      throw new IllegalArgumentException("난이도는 null일 수 없습니다.");
    }

    this.difficulty = difficulty;

    // AI에 난이도 적용 (사고 시간만 조절)
    if (ai != null) {
      ai.setDifficulty(difficulty);
    }
  }

  /**
   * 현재 AI 난이도 반환
   *
   * @return AI 난이도
   */
  public AIDifficulty getDifficulty() {
    return difficulty;
  }

  /**
   * AI 사고 시간 직접 설정 (AI vs AI 모드 등 특수한 경우용)
   *
   * @param thinkDelayMs 사고 시간 (밀리초)
   */
  public void setThinkDelay(int thinkDelayMs) {
    if (ai != null) {
      ai.setThinkDelay(thinkDelayMs);
    }
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

    aiTimer = new Timer(AI_ACTION_INTERVAL_MS, createAIActionListener());

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
   * AI 행동 타이머용 ActionListener 생성
   *
   * @return AI 행동을 처리하는 ActionListener
   */
  private ActionListener createAIActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleAIAction();
      }
    };
  }

  /**
   * AI 행동 처리 로직
   */
  private void handleAIAction() {
    // 게임 오버 체크를 먼저 수행 (게임 오버 시 즉시 정지)
    if (gameEngine.isGameOver()) {
      stop();
      if (onGameOverCallback != null) {
        onGameOverCallback.run();
      }
      return;
    }

    // 일시정지 상태이거나 비활성화 상태면 행동하지 않음
    if (isPaused || !isActive || ai == null) {
      return;
    }

    // AI 행동 실행
    ai.makeMove();

    // UI 업데이트 콜백 호출
    if (onMoveCallback != null) {
      onMoveCallback.run();
    }
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
