package se.tetris.team5.components.battle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.components.game.GameBoard;
import se.tetris.team5.components.game.NextBlockBoard;
import se.tetris.team5.components.game.ScoreBoard;

/**
 * 단일 플레이어의 게임 패널 (UI + GameEngine 캡슐화)
 * 대전 모드에서 각 플레이어별로 인스턴스 생성
 */
public class PlayerGamePanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private String playerName;
  private String controlInfo;
  private Color themeColor;

  // UI 컴포넌트
  private GameBoard gameBoard;
  private NextBlockBoard nextBlockBoard;
  private ScoreBoard scoreBoard;
  private JPanel nextVisualPanel;
  private JLabel scoreValueLabel;
  private JLabel levelLabel;
  private JLabel linesLabel;
  private JLabel timerLabel;
  private JPanel attackPanel;

  // 게임 로직
  private GameEngine gameEngine;
  private Timer gameTimer;
  private Timer uiTimer; // UI 업데이트용 별도 타이머
  private long gameStartTime;

  /**
   * 플레이어 게임 패널 생성 (기본값)
   */
  public PlayerGamePanel() {
    this("플레이어", "키 입력", new Color(100, 200, 255));
  }

  /**
   * 플레이어 게임 패널 생성
   * 
   * @param playerName  플레이어 이름
   * @param controlInfo 조작키 정보
   * @param themeColor  테마 색상
   */
  public PlayerGamePanel(String playerName, String controlInfo, Color themeColor) {
    this.playerName = playerName;
    this.controlInfo = controlInfo;
    this.themeColor = themeColor;

    setLayout(new BorderLayout());
    setBackground(Color.BLACK);

    initGameEngine();
    initComponents();
  }

  private void initGameEngine() {
    // autoStart=false로 생성하여 자동 시작 방지 (빈 보드 상태)
    gameEngine = new GameEngine(GameBoard.HEIGHT, GameBoard.WIDTH, false);
  }

  private void initComponents() {
    // 게임 보드 + 타이머 오버레이
    javax.swing.JLayeredPane boardContainer = new javax.swing.JLayeredPane();
    boardContainer.setLayout(null);

    gameBoard = new GameBoard();
    boardContainer.add(gameBoard, Integer.valueOf(0));

    // 타이머 라벨
    timerLabel = new JLabel("00:00");
    timerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
    timerLabel.setForeground(new Color(255, 50, 50));
    timerLabel.setOpaque(true);
    timerLabel.setBackground(new Color(0, 0, 0, 180));
    timerLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    boardContainer.add(timerLabel, Integer.valueOf(100));

    // 보드와 타이머 위치 설정
    boardContainer.addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
        java.awt.Dimension size = boardContainer.getSize();
        gameBoard.setBounds(0, 0, size.width, size.height);
        timerLabel.setBounds(10, 10, 80, 30);
      }
    });

    // 오른쪽 정보 패널
    JPanel rightPanel = createRightPanel();

    add(boardContainer, BorderLayout.CENTER);
    add(rightPanel, BorderLayout.EAST);
  }

  private JPanel createRightPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(18, 18, 24));
    panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    panel.setPreferredSize(new java.awt.Dimension(220, 0));

    // 플레이어 이름
    JLabel nameLabel = new JLabel(playerName, javax.swing.SwingConstants.CENTER);
    nameLabel.setFont(createKoreanFont(Font.BOLD, 18));
    nameLabel.setForeground(themeColor);
    nameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    panel.add(nameLabel);

    // 조작키 정보
    JLabel controlLabel = new JLabel(controlInfo, javax.swing.SwingConstants.CENTER);
    controlLabel.setFont(createKoreanFont(Font.PLAIN, 12));
    controlLabel.setForeground(new Color(150, 150, 150));
    controlLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    panel.add(controlLabel);
    panel.add(javax.swing.Box.createVerticalStrut(12));

    // 다음 블록
    nextVisualPanel = createNextBlockPanel();
    JPanel nextWrapper = BattleLayoutBuilder.createTitledPanel("다음 블록", nextVisualPanel,
        new Color(255, 204, 0), new Color(255, 204, 0));
    nextWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    panel.add(nextWrapper);
    panel.add(javax.swing.Box.createVerticalStrut(12));

    // 점수 정보
    JPanel scorePanel = createScorePanel();
    JPanel scoreWrapper = BattleLayoutBuilder.createTitledPanel("점수", scorePanel,
        new Color(100, 255, 200), new Color(100, 255, 200));
    scoreWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    panel.add(scoreWrapper);
    panel.add(javax.swing.Box.createVerticalStrut(12));

    // 공격 블록 패널
    attackPanel = createAttackPanel();
    JPanel attackWrapper = BattleLayoutBuilder.createTitledPanel("공격 블록", attackPanel,
        new Color(255, 100, 100), new Color(255, 100, 100));
    attackWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    panel.add(attackWrapper);
    panel.add(javax.swing.Box.createVerticalGlue());

    return panel;
  }

  private JPanel createNextBlockPanel() {
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int cellSize = Math.min(w / 6, h / 6);
        int gridSize = cellSize * 4;
        int startX = (w - gridSize) / 2;
        int startY = (h - gridSize) / 2;
        Block next = gameEngine != null ? gameEngine.getNextBlock() : null;

        g2.setColor(new Color(18, 18, 24));
        g2.fillRoundRect(0, 0, w, h, 10, 10);

        for (int r = 0; r < 4; r++) {
          for (int c = 0; c < 4; c++) {
            int x = startX + c * cellSize;
            int y = startY + r * cellSize;
            g2.setColor(new Color(40, 40, 48));
            g2.fillRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 6, 6);
            if (next != null && r < next.height() && c < next.width() && next.getShape(c, r) == 1) {
              Color col = next.getColor();
              if (col == null)
                col = Color.CYAN;
              g2.setColor(col);
              g2.fillRoundRect(x + 4, y + 4, cellSize - 8, cellSize - 8, 6, 6);
              g2.setColor(new Color(255, 255, 255, 40));
              g2.fillRoundRect(x + 4, y + 4, (cellSize - 8) / 2, (cellSize - 8) / 2, 4, 4);

              // 아이템이 있으면 시각적으로 표시
              se.tetris.team5.items.Item cellItem = next.getItem(c, r);
              if (cellItem != null) {
                // 반투명 금색 원
                g2.setColor(new Color(255, 215, 0, 200));
                int ovalSize = Math.max(cellSize / 2, 10);
                int ovalX = x + 4 + (cellSize - 8 - ovalSize) / 2;
                int ovalY = y + 4 + (cellSize - 8 - ovalSize) / 2;
                g2.fillOval(ovalX, ovalY, ovalSize, ovalSize);
                // 아이템 아이콘/문자
                g2.setColor(Color.BLACK);
                Font iconFont = new Font("Arial", Font.BOLD, Math.max(ovalSize / 2, 8));
                g2.setFont(iconFont);
                String icon = getItemIcon(cellItem);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int textX = ovalX + (ovalSize - fm.stringWidth(icon)) / 2;
                int textY = ovalY + (ovalSize + fm.getAscent()) / 2 - fm.getDescent();
                g2.drawString(icon, textX, textY);
              }
            }
          }
        }
        g2.dispose();
      }

      // 싱글 모드와 동일한 아이템 아이콘 반환 방식
      private String getItemIcon(se.tetris.team5.items.Item item) {
        if (item instanceof se.tetris.team5.items.LineClearItem)
          return "L";
        if (item instanceof se.tetris.team5.items.TimeStopItem)
          return "⏱";
        if (item instanceof se.tetris.team5.items.DoubleScoreItem)
          return "×2";
        if (item instanceof se.tetris.team5.items.BombItem)
          return "💣";
        if (item instanceof se.tetris.team5.items.WeightBlockItem)
          return "W";
        if (item instanceof se.tetris.team5.items.ScoreItem)
          return "S";
        return "?";
      }
    };
    panel.setPreferredSize(new java.awt.Dimension(180, 90));
    return panel;
  }

  private JPanel createScorePanel() {
    JPanel panel = new JPanel();
    panel.setOpaque(false);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    scoreValueLabel = new JLabel("0", javax.swing.SwingConstants.CENTER);
    scoreValueLabel.setFont(createKoreanFont(Font.BOLD, 24));
    scoreValueLabel.setForeground(new Color(255, 220, 100));
    scoreValueLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    panel.add(scoreValueLabel);
    panel.add(javax.swing.Box.createVerticalStrut(8));

    JPanel smallRow = new JPanel();
    smallRow.setOpaque(false);
    smallRow.setLayout(new BoxLayout(smallRow, BoxLayout.Y_AXIS));

    levelLabel = new JLabel("레벨: 1", javax.swing.SwingConstants.CENTER);
    levelLabel.setFont(createKoreanFont(Font.BOLD, 13));
    levelLabel.setForeground(new Color(200, 200, 200));
    levelLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

    linesLabel = new JLabel("줄: 0", javax.swing.SwingConstants.CENTER);
    linesLabel.setFont(createKoreanFont(Font.BOLD, 13));
    linesLabel.setForeground(new Color(200, 200, 200));
    linesLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

    smallRow.add(levelLabel);
    smallRow.add(javax.swing.Box.createVerticalStrut(4));
    smallRow.add(linesLabel);
    panel.add(smallRow);

    return panel;
  }

  private JPanel createAttackPanel() {
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int cellSize = Math.min(w / 5, h / 10);
        int gridWidth = cellSize * 5;
        int gridHeight = cellSize * 10;
        int startX = (w - gridWidth) / 2;
        int startY = (h - gridHeight) / 2;

        g2.setColor(new Color(18, 18, 24));
        g2.fillRoundRect(0, 0, w, h, 10, 10);

        for (int r = 0; r < 10; r++) {
          for (int c = 0; c < 5; c++) {
            int x = startX + c * cellSize;
            int y = startY + r * cellSize;
            g2.setColor(new Color(40, 40, 48));
            g2.fillRoundRect(x + 1, y + 1, cellSize - 2, cellSize - 2, 4, 4);
          }
        }
        g2.dispose();
      }
    };
    panel.setPreferredSize(new java.awt.Dimension(100, 180));
    return panel;
  }

  private Font createKoreanFont(int style, int size) {
    String[] koreanFonts = { "맑은 고딕", "Malgun Gothic", "굴림", "Gulim", "Arial Unicode MS", "Dialog" };
    for (String fontName : koreanFonts) {
      Font font = new Font(fontName, style, size);
      if (font.getFamily().equals(fontName) || font.canDisplay('한')) {
        return font;
      }
    }
    return new Font(Font.DIALOG, style, size);
  }

  // Public API

  public void startGame() {
    gameStartTime = System.currentTimeMillis();
    // 새 게임 시작 (깨끗한 보드에서 시작)
    gameEngine.startNewGame();
    startTimer();
  }

  public void pauseGame() {
    if (gameTimer != null) {
      gameTimer.stop();
    }
    if (uiTimer != null) {
      uiTimer.stop();
    }
  }

  public void resumeGame() {
    if (gameTimer != null) {
      gameTimer.start();
    }
    if (uiTimer != null) {
      uiTimer.start();
    }
  }

  public void stopGame() {
    if (gameTimer != null) {
      gameTimer.stop();
    }
    if (uiTimer != null) {
      uiTimer.stop();
    }
  }

  private void startTimer() {
    if (gameTimer != null) {
      gameTimer.stop();
    }
    if (uiTimer != null) {
      uiTimer.stop();
    }

    // game.java와 동일한 속도 설정 적용
    int timerInterval = getInitialInterval();

    // 블록 자동 낙하 타이머 (게임 속도에 따라)
    gameTimer = new Timer(timerInterval, e -> {
      if (!gameEngine.isGameOver()) {
        gameEngine.moveBlockDown();
      } else {
        gameTimer.stop();
        uiTimer.stop();
      }
    });

    // UI 업데이트 타이머 (60fps로 빠르게)
    uiTimer = new Timer(16, e -> {
      updateGameUI();
    });

    // 첫 블록이 제자리에서 시작하도록 초기 지연 설정
    gameTimer.setInitialDelay(timerInterval);

    gameTimer.start();
    uiTimer.start();
  }

  /**
   * 게임 속도 설정에 따른 초기 간격 계산 (game.java 패턴 적용)
   */
  private int getInitialInterval() {
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    int gameSpeed = settings.getGameSpeed(); // 1-5 범위

    switch (gameSpeed) {
      case 1:
        return 2000; // 매우느림: 2초
      case 2:
        return 1200; // 느림: 1.2초
      case 3:
        return 800; // 보통: 0.8초
      case 4:
        return 500; // 빠름: 0.5초
      case 5:
        return 300; // 매우빠름: 0.3초
      default:
        return 800; // 기본값: 보통
    }
  }

  public void updateGameUI() {
    // 게임 보드 업데이트
    if (gameBoard == null)
      return;

    // 게임 오버 시 타이머 정지
    if (gameEngine.isGameOver()) {
      if (gameTimer != null)
        gameTimer.stop();
      if (uiTimer != null)
        uiTimer.stop();
      return;
    }
    gameBoard.setShowTextOverlay(false);
    int[][] board = gameEngine.getBoardManager().getBoard();
    Color[][] boardColors = gameEngine.getBoardManager().getBoardColors();
    Block currBlock = gameEngine.getCurrentBlock();
    int currX = gameEngine.getX();
    int currY = gameEngine.getY();

    se.tetris.team5.items.Item[][] items = new se.tetris.team5.items.Item[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        items[i][j] = gameEngine.getBoardManager().getBoardItem(j, i);
      }
    }
    gameBoard.renderBoard(board, boardColors, items, currBlock, currX, currY);

    // 줄 삭제 애니메이션 처리
    try {
      java.util.List<Integer> clearedRows = gameEngine.consumeLastClearedRows();
      if (clearedRows != null && !clearedRows.isEmpty()) {
        gameBoard.triggerClearAnimation(clearedRows);
      }
    } catch (Exception ex) {
      // 애니메이션 처리 실패해도 게임 진행
    }

    // 다음 블록 업데이트
    if (nextVisualPanel != null) {
      nextVisualPanel.repaint();
    }

    // 점수 정보 업데이트
    if (scoreValueLabel != null) {
      scoreValueLabel.setText(String.format("%,d", gameEngine.getGameScoring().getCurrentScore()));
    }
    if (levelLabel != null) {
      levelLabel.setText("레벨: " + gameEngine.getGameScoring().getLevel());
    }
    if (linesLabel != null) {
      linesLabel.setText("줄: " + gameEngine.getGameScoring().getLinesCleared());
    }

    // 타이머 업데이트
    if (timerLabel != null) {
      long elapsed = System.currentTimeMillis() - gameStartTime;
      int minutes = (int) (elapsed / 60000);
      int seconds = (int) ((elapsed % 60000) / 1000);
      timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // 타이머 속도 조정
    if (gameTimer != null) {
      int newInterval = gameEngine.getGameScoring().getTimerInterval();
      if (gameTimer.getDelay() != newInterval) {
        gameTimer.setDelay(newInterval);
      }
    }
  }

  /**
   * 타이머 라벨 업데이트 (시간제한 모드용)
   */
  public void updateTimerLabel(String timeString) {
    if (timerLabel != null) {
      timerLabel.setText(timeString);
    }
  }

  // Getters
  public GameEngine getGameEngine() {
    return gameEngine;
  }

  public GameBoard getGameBoard() {
    return gameBoard;
  }

  public boolean isGameOver() {
    return gameEngine.isGameOver();
  }
}
