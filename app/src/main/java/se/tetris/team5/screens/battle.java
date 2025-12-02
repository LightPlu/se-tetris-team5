package se.tetris.team5.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import se.tetris.team5.ScreenController;
import se.tetris.team5.components.battle.PlayerGamePanel;
import se.tetris.team5.gamelogic.battle.BattleGameController;
import se.tetris.team5.gamelogic.input.Player1InputHandler;
import se.tetris.team5.gamelogic.input.Player2InputHandler;
import se.tetris.team5.gamelogic.GameMode;

/**
 * 2인 대전 모드 (리팩토링 버전)
 * 왼쪽: WASD + Z (하드드롭)
 * 오른쪽: 화살표 + RShift (하드드롭)
 */
public class battle extends JPanel implements KeyListener {

  /**
   * 배틀 모드 리소스 정리 (타이머, 컨트롤러 등)
   * 반드시 화면 전환 시 호출할 것
   */
  public void dispose() {
    // 타이머 정리
    if (timeLimitTimer != null) {
      timeLimitTimer.stop();
      timeLimitTimer = null;
    }
    if (gameOverCheckTimer != null) {
      gameOverCheckTimer.stop();
      gameOverCheckTimer = null;
    }
    // 컨트롤러 정지
    if (gameController != null) {
      gameController.stop();
    }
    // 키 리스너 등 기타 리소스 정리 필요시 여기에 추가
  }

  private static final long serialVersionUID = 1L;
  private static final int TIME_LIMIT_SECONDS = 300; // 5분 = 300초

  private ScreenController screenController;
  private String originalWindowSize;

  // 플레이어 패널
  private PlayerGamePanel player1Panel;
  private PlayerGamePanel player2Panel;

  // 게임 컨트롤러
  private BattleGameController gameController;

  // 입력 핸들러
  private Player1InputHandler player1Input;
  private Player2InputHandler player2Input;

  private boolean isPaused = false;

  // 시간제한 모드 관련
  private String battleMode; // "NORMAL", "ITEM", "TIMELIMIT"
  private javax.swing.Timer timeLimitTimer;
  private int remainingSeconds;

  public battle(ScreenController screenController) {
    this.screenController = screenController;
    // 시스템 속성에 저장된 원래 화면 크기 가져오기
    this.originalWindowSize = System.getProperty("tetris.battle.originalSize");
    // 만약 시스템 속성이 없으면 현재 설정값 사용
    if (this.originalWindowSize == null) {
      se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
      this.originalWindowSize = settings.getWindowSize();
    }

    // 대전 모드 타입 가져오기
    this.battleMode = System.getProperty("tetris.battle.mode", "NORMAL");
    System.out.println("[대전 모드] " + battleMode + " 모드로 시작");

    initializeGame();

    setFocusable(true);
    setFocusTraversalKeysEnabled(false);
    addKeyListener(this);

    // game.java와 동일한 포커스 관리 (마우스 클릭, 화면 표시, 마우스 진입 시 포커스 요청)
    addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        requestFocusInWindow();
      }

      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
        requestFocusInWindow();
      }
    });

    addHierarchyListener(new java.awt.event.HierarchyListener() {
      @Override
      public void hierarchyChanged(java.awt.event.HierarchyEvent e) {
        if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
          requestFocusInWindow();
        }
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentShown(java.awt.event.ComponentEvent e) {
        requestFocusInWindow();
      }

      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
        requestFocusInWindow();
      }
    });

    // 초기 포커스 요청
    requestFocusInWindow();
  }

  // === 테스트 지원 메서드 ===
  /**
   * 테스트 환경에서 강제로 일시정지 상태로 만듦
   */
  public void forcePause() {
    this.isPaused = true;
  }

  /**
   * 테스트 환경에서 타임리밋 타이머를 강제로 생성
   */
  public void forceStartTimeLimitTimer() {
    if (this.timeLimitTimer == null) {
      startTimeLimitMode();
    }
  }

  /**
   * 게임 초기화 (생성자와 재시작 시 공통 사용)
   */
  private void initializeGame() {
    setLayout(new BorderLayout());
    setBackground(Color.BLACK);

    // 기존 컴포넌트 제거
    removeAll();

    // 기존 타이머 정지
    if (timeLimitTimer != null) {
      timeLimitTimer.stop();
      timeLimitTimer = null;
    }
    if (gameOverCheckTimer != null) {
      gameOverCheckTimer.stop();
      gameOverCheckTimer = null;
    }

    // 플레이어 패널 생성
    player1Panel = new PlayerGamePanel();
    player2Panel = new PlayerGamePanel();
    boolean isTimeLimitMode = "TIMELIMIT".equals(battleMode);
    player1Panel.setCountdownTimerEnabled(isTimeLimitMode);
    player2Panel.setCountdownTimerEnabled(isTimeLimitMode);

    // 대전모드: 상대방 패널 서로 연결 (공격 블럭 전송용)
    player1Panel.setOpponentPanel(player2Panel);
    player2Panel.setOpponentPanel(player1Panel);

    // 게임 모드 설정 (NORMAL, ITEM, TIMELIMIT)
    if ("ITEM".equals(battleMode)) {
      player1Panel.getGameEngine().setGameMode(GameMode.ITEM);
      player2Panel.getGameEngine().setGameMode(GameMode.ITEM);
    } else if (isTimeLimitMode) {
      // 시간제한 모드는 일반 모드 기반 (아이템 없음)
      player1Panel.getGameEngine().setGameMode(GameMode.NORMAL);
      player2Panel.getGameEngine().setGameMode(GameMode.NORMAL);
    } else {
      // NORMAL 모드 - 명시적으로 설정
      player1Panel.getGameEngine().setGameMode(GameMode.NORMAL);
      player2Panel.getGameEngine().setGameMode(GameMode.NORMAL);
    }

    // 게임 컨트롤러 생성
    gameController = new BattleGameController(
        player1Panel,
        player2Panel,
        this::handleGameOver);

    // 입력 핸들러 생성
    player1Input = new Player1InputHandler(player1Panel.getGameEngine());
    player2Input = new Player2InputHandler(player2Panel.getGameEngine());

    buildUI();

    // 일시정지 상태 초기화
    isPaused = false;

    addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
      }
    });

    addHierarchyListener(new java.awt.event.HierarchyListener() {
      @Override
      public void hierarchyChanged(java.awt.event.HierarchyEvent e) {
        if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
          javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
        }
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentShown(java.awt.event.ComponentEvent e) {
        javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
      }
    });

    revalidate();
    repaint();
    javax.swing.SwingUtilities.invokeLater(() -> requestFocusInWindow());
  }

  /**
   * 게임을 실제로 시작 (ScreenController에서 화면 표시 후 호출)
   */
  public void startBattle() {
    // 게임 시작
    gameController.start();

    // 시간제한 모드인 경우 타이머가 항상 생성되도록 보장
    if ("TIMELIMIT".equals(battleMode)) {
      if (timeLimitTimer == null || !timeLimitTimer.isRunning()) {
        startTimeLimitMode();
      }
    }

    // 게임 오버 체크 타이머 (500ms마다)
    startGameOverCheckTimer();

    // 모든 UI 업데이트가 완료된 후 focus 요청
    javax.swing.SwingUtilities.invokeLater(() -> {
      requestFocusInWindow();
      setFocusable(true);
    });
  }

  private javax.swing.Timer gameOverCheckTimer;

  /**
   * 게임 오버 체크 타이머 시작
   */
  private void startGameOverCheckTimer() {
    if (gameOverCheckTimer != null) {
      gameOverCheckTimer.stop();
    }

    gameOverCheckTimer = new javax.swing.Timer(500, e -> {
      if (!isPaused && gameController != null) {
        gameController.checkGameOver();
      }
    });
    gameOverCheckTimer.start();
  }

  public void display(JTextPane textPane) {
    // ScreenController 호환성
  }

  private void buildUI() {
    // 중앙 패널 - 2개의 게임 영역을 가로로 배치
    JPanel centerPanel = new JPanel(new java.awt.GridLayout(1, 2, 20, 0));
    centerPanel.setBackground(Color.BLACK);
    centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    centerPanel.add(player1Panel);
    centerPanel.add(player2Panel);

    add(centerPanel, BorderLayout.CENTER);
  }

  private void handleGameOver(int winner) {
    isPaused = true;

    String message = winner == 1 ? "🎉 플레이어 1 승리! 🎉" : "🎉 플레이어 2 승리! 🎉";

    int option = JOptionPane.showOptionDialog(
        this,
        message,
        "게임 종료",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        new Object[] { "메인 메뉴", "다시 하기" },
        "메인 메뉴");

    if (option == 0 || option == JOptionPane.CLOSED_OPTION) {
      // 게임 정리
      if (gameController != null) {
        gameController.stop();
      }
      if (timeLimitTimer != null) {
        timeLimitTimer.stop();
        timeLimitTimer = null;
      }
      if (gameOverCheckTimer != null) {
        gameOverCheckTimer.stop();
        gameOverCheckTimer = null;
      }
      restoreWindowSize();
      screenController.showScreen("home");
    } else {
      // 게임 재시작
      if (gameOverCheckTimer != null) {
        gameOverCheckTimer.stop();
        gameOverCheckTimer = null;
      }
      if (gameController != null) {
        gameController.stop();
      }
      if (timeLimitTimer != null) {
        timeLimitTimer.stop();
        timeLimitTimer = null;
      }

      // 완전히 새로운 게임으로 초기화
      initializeGame();

      // 게임 시작
      javax.swing.SwingUtilities.invokeLater(() -> {
        startBattle();
        requestFocusInWindow();
      });
    }
  }

  private void restoreWindowSize() {
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    // 저장된 원래 화면 크기로 복원
    if (originalWindowSize != null) {
      settings.setWindowSize(originalWindowSize);
      // 설정 파일을 다시 로드하여 메모리 상태도 동기화
      settings.loadSettings();
    }
    screenController.updateWindowSize();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (gameController.isGameOver())
      return;

    int keyCode = e.getKeyCode();
    
    // GameSettings에서 키 코드 가져오기
    se.tetris.team5.utils.setting.GameSettings settings = 
        se.tetris.team5.utils.setting.GameSettings.getInstance();
    
    int player1ItemKey = settings.getPlayerKeyCode(1, "item");
    int player2ItemKey = settings.getPlayerKeyCode(2, "item");
    
    // 아이템 키 체크 (타이머 멈춤 효과)
    if (keyCode == player1ItemKey) {
      player1Panel.useItem();
      return;
    } else if (keyCode == player2ItemKey) {
      player2Panel.useItem();
      return;
    }
    
    // Player1 키 처리
    player1Input.handleKeyPress(keyCode);
    
    // Player2 키 처리
    player2Input.handleKeyPress(keyCode);
    
    // 공통 키 처리
    if (keyCode == KeyEvent.VK_P) {
      togglePause();
    } else if (keyCode == KeyEvent.VK_ESCAPE) {
      isPaused = true; // ESC 입력 시 명확히 일시정지
      showPauseMenu();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  private void togglePause() {
    isPaused = !isPaused;
    gameController.setPaused(isPaused);
    if (isPaused) {
      JOptionPane.showMessageDialog(this, "일시정지됨\nP 키를 눌러 계속하기", "일시정지", JOptionPane.INFORMATION_MESSAGE);
    }
    requestFocusInWindow();
  }

  private void showPauseMenu() {
    isPaused = true;
    gameController.setPaused(true);

    // 타이머 일시정지
    if (timeLimitTimer != null && timeLimitTimer.isRunning()) {
      timeLimitTimer.stop();
    }
    if (gameOverCheckTimer != null && gameOverCheckTimer.isRunning()) {
      gameOverCheckTimer.stop();
    }

    // 일반 모드와 동일한 형식의 일시정지 메뉴
    String[] options = { "계속", "메뉴로 나가기", "게임 종료" };
    int choice = JOptionPane.showOptionDialog(
        this,
        "게임을 일시중단했습니다.\n\n" +
            "• 계속: 현재 게임을 이어서 진행합니다.\n" +
            "• 메뉴로 나가기: 현재 게임을 취소하고 메인 메뉴로 이동합니다.\n" +
            "• 게임 종료: 테트리스 프로그램을 완전히 종료합니다.",
        "일시정지",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);

    if (choice == 0 || choice == JOptionPane.CLOSED_OPTION) {
      // 계속하기 (기본값)
      isPaused = false;
      gameController.setPaused(false);
      // 타이머 재개
      if (timeLimitTimer != null && !timeLimitTimer.isRunning()) {
        timeLimitTimer.start();
      }
      if (gameOverCheckTimer != null && !gameOverCheckTimer.isRunning()) {
        gameOverCheckTimer.start();
      }
      requestFocusInWindow();
    } else if (choice == 1) {
      // 메뉴로 나가기: 모든 리소스 정리
      isPaused = false;
      if (timeLimitTimer != null) {
        timeLimitTimer.stop();
      }
      if (gameOverCheckTimer != null) {
        gameOverCheckTimer.stop();
      }
      gameController.stop();
      restoreWindowSize();
      screenController.showScreen("home");
    } else if (choice == 2) {
      // 게임 종료: 테트리스 프로그램 완전 종료
      isPaused = false;
      if (timeLimitTimer != null) {
        timeLimitTimer.stop();
      }
      if (gameOverCheckTimer != null) {
        gameOverCheckTimer.stop();
      }
      gameController.stop();
      System.exit(0);
    }
  }

  /**
   * 시간제한 모드 시작
   */
  private void startTimeLimitMode() {
    remainingSeconds = TIME_LIMIT_SECONDS;

    // 기존 타이머가 있으면 정지 후 새로 생성
    if (timeLimitTimer != null) {
      timeLimitTimer.stop();
      timeLimitTimer = null;
    }

    timeLimitTimer = new javax.swing.Timer(1000, e -> {
      if (!isPaused && !gameController.isGameOver()) {
        remainingSeconds--;
        updateTimerLabels();

        if (remainingSeconds <= 0) {
          timeLimitTimer.stop();
          handleTimeUp();
        }
      }
    });
    timeLimitTimer.start();
    updateTimerLabels();
  }

  /**
   * 타이머 라벨 업데이트
   */
  private void updateTimerLabels() {
    int minutes = remainingSeconds / 60;
    int seconds = remainingSeconds % 60;
    String timeStr = String.format("%02d:%02d", minutes, seconds);

    player1Panel.updateTimerLabel(timeStr);
    player2Panel.updateTimerLabel(timeStr);
  }

  /**
   * 시간 종료 처리
   */
  private void handleTimeUp() {
    int player1Score = player1Panel.getGameEngine().getGameScoring().getCurrentScore();
    int player2Score = player2Panel.getGameEngine().getGameScoring().getCurrentScore();

    int winner;
    if (player1Score > player2Score) {
      winner = 1;
    } else if (player2Score > player1Score) {
      winner = 2;
    } else {
      // 동점인 경우 플레이어 1 승리
      winner = 1;
    }

    handleGameOver(winner);
  }
}
