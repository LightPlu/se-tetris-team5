package se.tetris.team5.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.ScreenController;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.components.game.GameBoard;
import se.tetris.team5.components.game.DoubleScoreBadge;
import se.tetris.team5.components.game.NextBlockBoard;
import se.tetris.team5.components.game.ScoreBoard;
import se.tetris.team5.utils.score.ScoreManager;

public class game extends JPanel implements KeyListener {

  private static final long serialVersionUID = 2434035659171694595L;

  // GameBoard 클래스의 상수들을 사용
  public static final int HEIGHT = GameBoard.HEIGHT;
  public static final int WIDTH = GameBoard.WIDTH;
  public static final char BORDER_CHAR = GameBoard.BORDER_CHAR;

  private ScreenController screenController;

  // UI 컴포넌트들
  private GameBoard gameBoard;
  private NextBlockBoard nextBlockBoard;
  private ScoreBoard scoreBoard;
  // Modern UI fields
  private JPanel nextVisualPanel;
  private JLabel scoreValueLabel;
  private JLabel levelLabel;
  private JLabel linesLabel;
  private JLabel gameModeLabel;
  private DoubleScoreBadge doubleScoreBadge;
  private javax.swing.JTextPane itemDescPane;
  private JPanel itemDescWrapper; // 아이템 설명 패널 래퍼

  // 게임 엔진 (순수 게임 로직)
  private GameEngine gameEngine;

  // Overlay components for TimeStop (graphical, semi-transparent)
  private javax.swing.JLayeredPane boardLayeredPane;
  private javax.swing.JPanel timeStopOverlay;
  // center panel inside overlay to avoid HTML baseline clipping issues
  private javax.swing.JPanel timeStopCenterPanel;
  private javax.swing.JLabel timeStopIconLabel;
  private javax.swing.JLabel timeStopNumberLabel;
  private javax.swing.JLabel timeStopSubLabel;

  private SimpleAttributeSet styleSet; // 텍스트 스타일 설정
  private Timer timer; // 블록 자동 낙하 타이머
  private long gameStartTime; // 게임 시작 시간

  // 일시정지 관련 변수
  private boolean isPaused = false;
  private int pauseMenuIndex = 0; // 0: 게임 계속, 1: 메뉴로 나가기, 2: 게임 종료
  private String[] pauseMenuOptions = { "게임 계속", "메뉴로 나가기", "게임 종료" };

  // 타임스톱 관련 변수
  private boolean isTimeStopped = false; // 타임스톱 활성화 상태
  // Timer used to tick the visible countdown every second while time-stop is
  // active
  private Timer timeStopCountdownTimer;
  // remaining seconds to show in the UI countdown
  private int timeStopRemaining = 0;

  // 게임 속도 설정에 따른 초기 간격 계산 메소드
  private int getInitialInterval() {
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    int gameSpeed = settings.getGameSpeed(); // 1-5 범위

    // 각 속도별 간격 (더 체감되도록 큰 차이)
    switch (gameSpeed) {
      case 1:
        return 2000; // 매우느림: 2초
      case 2:
        return 1200; // 느림: 1.2초
      case 3:
        return 800; // 보통: 0.8초
      case 4:
        return 400; // 빠름: 0.4초
      case 5:
        return 150; // 매우빠름: 0.15초
      default:
        return 800; // 기본값 (보통)
    }
  }

  // 윈도우에서 한글을 제대로 표시하기 위한 폰트 생성 메서드
  private Font createKoreanFont(int style, int size) {
    // 윈도우에서 한글을 잘 지원하는 폰트들을 우선순위대로 시도
    String[] koreanFonts = { "맑은 고딕", "Malgun Gothic", "굴림", "Gulim", "Arial Unicode MS", "Dialog" };

    for (String fontName : koreanFonts) {
      Font font = new Font(fontName, style, size);
      // 폰트가 시스템에 있는지 확인
      if (font.getFamily().equals(fontName) || font.canDisplay('한')) {
        return font;
      }
    }

    // 모든 한글 폰트가 실패하면 기본 Dialog 폰트 사용
    return new Font(Font.DIALOG, style, size);
  }

  public game(ScreenController screenController) {
    // 마우스 클릭 시 포커스 강제 요청 (클릭 후 키 입력 안 먹는 현상 방지)
    addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        requestFocusInWindow();
      }
    });

    this.screenController = screenController;
    setLayout(new BorderLayout());
    setBackground(Color.BLACK);

    initComponents();
    setFocusable(true);
    setFocusTraversalKeysEnabled(false); // Tab 등도 이벤트로 받기
    addKeyListener(this);
    // Robust focus handling: request focus when showing and on mouse enter
    addHierarchyListener(new java.awt.event.HierarchyListener() {
      @Override
      public void hierarchyChanged(java.awt.event.HierarchyEvent e) {
        if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
          requestFocusInWindow();
        }
      }
    });
    addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
        requestFocusInWindow();
      }
    });
    // macOS 대응: 생성 시점에 포커스 강제 요청
    requestFocusInWindow();
    // 화면에 추가될 때마다 포커스 강제 요청 (윈도우/패널 전환 시)
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
  }

  // ScreenController의 display 패턴을 위한 메서드 (사용하지 않지만 호환성 유지)
  public void display(JTextPane textPane) {
    // 이 메서드는 game이 JPanel이므로 직접 화면에 추가되기 때문에 사용하지 않음
    // 하지만 ScreenController 패턴 호환성을 위해 유지
  }

  private void initComponents() {
    // 전체 레이아웃 설정
    setLayout(new BorderLayout());

    // 게임 보드 (왼쪽)
    gameBoard = new GameBoard();

    // Create a layered pane so we can draw a semi-transparent overlay above the
    // game board
    boardLayeredPane = new javax.swing.JLayeredPane();
    boardLayeredPane.setLayout(null); // we'll manage child bounds on resize
    // add the gameBoard at the default layer
    boardLayeredPane.add(gameBoard, Integer.valueOf(0));

    // overlay panel (initially hidden) - semi-transparent dark background with
    // large centered label
    timeStopOverlay = new javax.swing.JPanel(null) {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        // semi-transparent fill
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setColor(new java.awt.Color(0, 0, 0, 140));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
      }
    };
    timeStopOverlay.setOpaque(false);
    timeStopOverlay.setVisible(false);

    // center panel holds three labels (icon, big number, subtext) to avoid HTML
    // renderer clipping
    timeStopCenterPanel = new javax.swing.JPanel();
    timeStopCenterPanel.setOpaque(false);
    timeStopCenterPanel.setLayout(new java.awt.GridBagLayout());

    // 이모지 대신 텍스트로 변경 (크로스 플랫폼 호환성)
    timeStopIconLabel = new javax.swing.JLabel("TIME STOP", javax.swing.SwingConstants.CENTER);
    timeStopIconLabel.setForeground(new java.awt.Color(191, 255, 230));
    timeStopIconLabel.setOpaque(false);

    timeStopNumberLabel = new javax.swing.JLabel("", javax.swing.SwingConstants.CENTER);
    timeStopNumberLabel.setForeground(new java.awt.Color(191, 255, 230));
    timeStopNumberLabel.setOpaque(false);

    timeStopSubLabel = new javax.swing.JLabel("초 남음", javax.swing.SwingConstants.CENTER);
    timeStopSubLabel.setForeground(new java.awt.Color(200, 230, 220));
    timeStopSubLabel.setOpaque(false);

    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = java.awt.GridBagConstraints.CENTER;
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    timeStopCenterPanel.add(timeStopIconLabel, gbc);
    gbc.gridy = 1;
    gbc.insets = new java.awt.Insets(6, 0, 6, 0);
    gbc.weighty = 1.0; // let the number take the vertical space so it's centered
    timeStopCenterPanel.add(timeStopNumberLabel, gbc);
    gbc.gridy = 2;
    gbc.insets = new java.awt.Insets(0, 0, 0, 0);
    gbc.weighty = 0.0;
    timeStopCenterPanel.add(timeStopSubLabel, gbc);

    timeStopOverlay.add(timeStopCenterPanel);

    boardLayeredPane.add(timeStopOverlay, Integer.valueOf(100));

    // keep child bounds synced when parent resizes
    boardLayeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
        java.awt.Dimension s = boardLayeredPane.getSize();
        gameBoard.setBounds(0, 0, s.width, s.height);
        timeStopOverlay.setBounds(0, 0, s.width, s.height);
  // position center panel roughly centered and large, add vertical padding so glyph tops aren't clipped
  int lblW = Math.max(200, s.width / 2);
  int lblH = Math.max(120, s.height / 4);
  int padTop = Math.max(12, lblH / 8);
  // give extra vertical room to avoid the top of the number being clipped; center the whole block
  int totalH = lblH + padTop;
  timeStopCenterPanel.setBounds((s.width - lblW) / 2, (s.height - totalH) / 2, lblW, totalH);
  // choose font sizes proportional to available height
  int numberFontSize = Math.max(40, (lblH - padTop) * 3 / 4);
  int iconFontSize = Math.max(24, (lblH - padTop) / 6);
  int subFontSize = Math.max(12, (lblH - padTop) / 8);
  // macOS/Windows 크로스 플랫폼 한글 지원 폰트 사용
  timeStopNumberLabel.setFont(createKoreanFont(java.awt.Font.BOLD, numberFontSize));
  timeStopIconLabel.setFont(createKoreanFont(java.awt.Font.PLAIN, iconFontSize));
  timeStopSubLabel.setFont(createKoreanFont(java.awt.Font.PLAIN, subFontSize));
      }
    });

    add(boardLayeredPane, BorderLayout.CENTER);

    // Ensure Shift works even when strict focus is lost: register a
    // WHEN_IN_FOCUSED_WINDOW binding
    this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, false),
        "useTimeStop");
    this.getActionMap().put("useTimeStop", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (gameEngine != null && gameEngine.hasTimeStopCharge() && !isTimeStopped) {
          activateTimeStop();
        }
      }
    });

    // DEBUG: Force multi-row clear animation test (press 'A') — triggers explosion
    // on bottom rows so
    // you can visually confirm multi-row animations regardless of actual game
    // clears.
    this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A'), "forceClearAnim");
    this.getActionMap().put("forceClearAnim", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          // choose a few bottom rows to demonstrate multiple simultaneous animations
          java.util.List<Integer> demo = new java.util.ArrayList<>();
          int h = gameBoard.getBoardHeight();
          // last 4 rows (if available)
          for (int r = Math.max(0, h - 4); r < h; r++)
            demo.add(r);
          System.out.println("[DEBUG] force clear anim rows=" + demo);
          gameBoard.triggerClearAnimation(demo);
        } catch (Exception ex) {
          // ignore
        }
      }
    });

    // 오른쪽 패널 (다음 블록 + 점수)
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    rightPanel.setBackground(new Color(18, 18, 24));
    rightPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    // Limit the overall right column width (the dark panel) so it stays visibly
    // narrower than the game area
    // restore right panel width to original comfortable size
    rightPanel.setPreferredSize(new java.awt.Dimension(260, 0));
    rightPanel.setMinimumSize(new java.awt.Dimension(220, 0));

    // Next block panel (titled box) - use a graphic preview for modern look
  nextBlockBoard = new NextBlockBoard();
  nextVisualPanel = new JPanel() {
    @Override
    protected void paintComponent(java.awt.Graphics g) {
      super.paintComponent(g);
      java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
      g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
      int w = getWidth();
      int h = getHeight();
      // Math.floor를 명시적으로 사용하여 항상 내림으로 통일 (깜빡임 방지)
      int cellSize = (int) Math.floor(Math.min((w - 8) / 4.0, (h - 8) / 4.0));
      int gridSize = cellSize * 4;
      int startX = (w - gridSize) / 2;
      int startY = (h - gridSize) / 2;
      Block next = null;
      if (gameEngine != null) next = gameEngine.getNextBlock();
      if (next != null) System.out.println("[UI DEBUG] nextVisualPanel.paintComponent next=" + next.getClass().getSimpleName());
      
      // 배경 그리기
      g2.setColor(new Color(18, 18, 24));
      g2.fillRoundRect(0, 0, w, h, 10, 10);
      
      // 빈 셀 배경 그리기 (GameBoard 스타일)
      for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
          int x = startX + c * cellSize;
          int y = startY + r * cellSize;
          g2.setColor(new Color(28, 28, 36)); // GameBoard와 동일한 색상
          g2.fillRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 6, 6);
        }
      }
      
      // 다음 블록 그리기 (GameBoard의 drawBlockWithPattern 스타일)
      if (next != null) {
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

              // Draw item indicator if this cell contains an item
              se.tetris.team5.items.Item cellItem = next.getItem(c, r);
              if (cellItem != null) {
                drawItemIndicator(g2, x + 4, y + 4, cellSize - 8, cellItem);
              }
            }
          }
        }
        g2.dispose();
      }
      g2.dispose();
    }
    
    // GameBoard의 drawBlockWithPattern과 동일한 로직
    private void drawBlockCellWithPattern(java.awt.Graphics2D g2, int x, int y, int cellSize, Color color, String blockType) {
      // 기본 블록 배경 그리기
      g2.setColor(color);
      g2.fillRoundRect(x+3, y+3, cellSize-6, cellSize-6, 6, 6);
      
      // 색맹 모드일 때만 패턴 추가
      se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
      if (settings.isColorblindMode()) {
        drawBlockPattern(g2, x, y, cellSize, blockType);
      }
      
      // 기본 하이라이트
      g2.setColor(new Color(255,255,255,40));
      g2.fillRoundRect(x+4, y+4, (cellSize-6)/2, (cellSize-6)/2, 4, 4);
    }
    
    // 블록 타입별 패턴 그리기
    private void drawBlockPattern(java.awt.Graphics2D g2, int x, int y, int cellSize, String blockType) {
      g2.setColor(new Color(0, 0, 0, 120));
      int innerSize = cellSize - 10;
      int patX = x + 5;
      int patY = y + 5;
      
      java.awt.Stroke oldStroke = g2.getStroke();
      g2.setStroke(new java.awt.BasicStroke(2.0f));
      
      switch (blockType) {
        case "I": // 수직선
          g2.drawLine(patX + innerSize/2, patY + 2, patX + innerSize/2, patY + innerSize - 2);
          break;
        case "O": // 사각형
          int rectSize = innerSize/3;
          g2.drawRect(patX + (innerSize-rectSize)/2, patY + (innerSize-rectSize)/2, rectSize, rectSize);
          break;
        case "T": // T자
          g2.drawLine(patX + 2, patY + innerSize/3, patX + innerSize - 2, patY + innerSize/3);
          g2.drawLine(patX + innerSize/2, patY + innerSize/3, patX + innerSize/2, patY + innerSize - 2);
          break;
        case "L": // L자
          g2.drawLine(patX + innerSize/3, patY + 2, patX + innerSize/3, patY + innerSize - 2);
          g2.drawLine(patX + innerSize/3, patY + innerSize - 2, patX + innerSize - 2, patY + innerSize - 2);
          break;
        case "J": // J자
          g2.drawLine(patX + 2*innerSize/3, patY + 2, patX + 2*innerSize/3, patY + innerSize - 2);
          g2.drawLine(patX + 2, patY + innerSize - 2, patX + 2*innerSize/3, patY + innerSize - 2);
          break;
        case "S": // S자
          g2.drawLine(patX + 2, patY + 2*innerSize/3, patX + innerSize/2, patY + 2*innerSize/3);
          g2.drawLine(patX + innerSize/2, patY + 2*innerSize/3, patX + innerSize/2, patY + innerSize/3);
          g2.drawLine(patX + innerSize/2, patY + innerSize/3, patX + innerSize - 2, patY + innerSize/3);
          break;
        case "Z": // Z자
          g2.drawLine(patX + 2, patY + innerSize/3, patX + innerSize/2, patY + innerSize/3);
          g2.drawLine(patX + innerSize/2, patY + innerSize/3, patX + innerSize/2, patY + 2*innerSize/3);
          g2.drawLine(patX + innerSize/2, patY + 2*innerSize/3, patX + innerSize - 2, patY + 2*innerSize/3);
          break;
        case "W": // 무게추 - X 패턴
          g2.drawLine(patX + 2, patY + 2, patX + innerSize - 2, patY + innerSize - 2);
          g2.drawLine(patX + innerSize - 2, patY + 2, patX + 2, patY + innerSize - 2);
          break;
      }
      
      g2.setStroke(oldStroke);
    }
    
    // GameBoard의 drawItemGlyph와 유사한 스타일로 아이템 표시
    private void drawItemIndicator(java.awt.Graphics2D g2, int x, int y, int cellSize, se.tetris.team5.items.Item item) {
      int cx = x + cellSize/2;
      int cy = y + cellSize/2;
      // 아이템 아이콘 크기를 블록보다 작게 조정 (cellSize/3 -> cellSize/4)
      int r = Math.max(5, cellSize/4);

      // 배경 링
      g2.setColor(new Color(0,0,0,120));
      g2.fillOval(cx - r - 1, cy - r - 1, r*2 + 2, r*2 + 2);

      if (item instanceof se.tetris.team5.items.TimeStopItem) {
        // 시계 아이콘
        g2.setColor(new Color(60, 180, 170));
        g2.fillOval(cx - r, cy - r, r*2, r*2);
        g2.setColor(Color.WHITE);
        int hw = Math.max(2, r/4);
        g2.fillOval(cx - hw, cy - hw, hw*2, hw*2);
        g2.setColor(new Color(255,255,255,200));
        g2.setStroke(new java.awt.BasicStroke(Math.max(1f, r/6)));
        g2.drawLine(cx, cy, cx + r/2, cy - r/3);
      } else if (item instanceof se.tetris.team5.items.BombItem) {
        // 폭탄 아이콘
        g2.setColor(new Color(30, 10, 10));
        g2.fillOval(cx - r, cy - r, r*2, r*2);
        g2.setColor(new Color(255, 90, 90));
        g2.setStroke(new java.awt.BasicStroke(Math.max(1f, r/6)));
        g2.drawOval(cx - r + 1, cy - r + 1, r*2 - 2, r*2 - 2);
        g2.setColor(new Color(255, 200, 80));
        g2.fillOval(cx + r - Math.max(4, r/4), cy - r - Math.max(2, r/6), Math.max(4, r/3), Math.max(4, r/3));
      } else if (item instanceof se.tetris.team5.items.LineClearItem) {
        // 라인 클리어 아이콘 (다른 아이템과 동일한 크기로 조정)
        g2.setColor(new Color(255, 200, 70));
        int arc = Math.max(4, r / 2);
        g2.fillRoundRect(cx - r, cy - r, r * 2, r * 2, arc, arc);
        g2.setColor(new Color(255,255,255,220));
        Font prev = g2.getFont();
        // 폰트 크기를 더 줄여서 다른 아이템과 균형 맞춤 (1.2 -> 0.9)
        Font glyphFont = prev.deriveFont(Font.BOLD, (float) Math.max(6, r * 0.9));
        g2.setFont(glyphFont);
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String text = "L";
        int sx = cx - fm.stringWidth(text) / 2;
        int sy = cy + fm.getAscent() / 2 - 2;
        g2.drawString(text, sx, sy);
        g2.setFont(prev);
      } else if (item instanceof se.tetris.team5.items.DoubleScoreItem) {
        // 더블 스코어 아이콘
        g2.setColor(new Color(255, 215, 0));
        g2.fillOval(cx - r, cy - r, r*2, r*2);
        g2.setColor(Color.WHITE);
        Font prev = g2.getFont();
        Font glyphFont = prev.deriveFont(Font.BOLD, (float) Math.max(8, r));
        g2.setFont(glyphFont);
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String text = "×2";
        int sx = cx - fm.stringWidth(text) / 2;
        int sy = cy + fm.getAscent() / 2 - 2;
        g2.drawString(text, sx, sy);
        g2.setFont(prev);
      } else if (item instanceof se.tetris.team5.items.WeightBlockItem) {
        // 무게추 아이콘
        g2.setColor(new Color(80, 80, 80));
        g2.fillOval(cx - r, cy - r, r*2, r*2);
        g2.setColor(Color.WHITE);
        Font prev = g2.getFont();
        Font glyphFont = prev.deriveFont(Font.BOLD, (float) Math.max(8, r));
        g2.setFont(glyphFont);
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String text = "W";
        int sx = cx - fm.stringWidth(text) / 2;
        int sy = cy + fm.getAscent() / 2 - 2;
        g2.drawString(text, sx, sy);
        g2.setFont(prev);
      } else if (item instanceof se.tetris.team5.items.ScoreItem) {
        // 스코어 아이콘
        g2.setColor(new Color(100, 200, 255));
        g2.fillOval(cx - r, cy - r, r*2, r*2);
        g2.setColor(Color.WHITE);
        Font prev = g2.getFont();
        Font glyphFont = prev.deriveFont(Font.BOLD, (float) Math.max(8, r));
        g2.setFont(glyphFont);
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String text = "S";
        int sx = cx - fm.stringWidth(text) / 2;
        int sy = cy + fm.getAscent() / 2 - 2;
        g2.drawString(text, sx, sy);
        g2.setFont(prev);
      }
    }
  };
  nextVisualPanel.setPreferredSize(new java.awt.Dimension(190, 130)); // 높이 증가 100->130
  JPanel nextWrapper = createTitledPanel("다음 블록", nextVisualPanel, new Color(255, 204, 0), new Color(255, 204, 0));
  nextWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  nextWrapper.setMaximumSize(nextWrapper.getPreferredSize());
  rightPanel.add(nextWrapper);
  rightPanel.add(javax.swing.Box.createVerticalStrut(8));

  // Item description panel (shows description when next block contains an item)
  // 아이템 모드일 때만 표시 (가시성은 reset()에서 제어)
  itemDescPane = new javax.swing.JTextPane();
  itemDescPane.setEditable(false);
  itemDescPane.setOpaque(false);
  itemDescPane.setFont(createKoreanFont(Font.PLAIN, 13));
  itemDescPane.setForeground(new Color(220, 220, 220));
  itemDescPane.setText("다음 블록에 포함된 아이템이 있으면 설명을 표시합니다.");
  itemDescWrapper = createTitledPanel("아이템 설명", itemDescPane, new Color(255, 180, 0), new Color(255,180,0));
  itemDescWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  itemDescWrapper.setMaximumSize(new java.awt.Dimension(240, 120));
  rightPanel.add(itemDescWrapper);
  rightPanel.add(javax.swing.Box.createVerticalStrut(12));

  // Score / Info panel (titled box) - modern cards for score, level, lines
  scoreBoard = new ScoreBoard();
  JPanel scoreInfo = new JPanel();
  scoreInfo.setOpaque(false);
  scoreInfo.setLayout(new BoxLayout(scoreInfo, BoxLayout.Y_AXIS));
  scoreValueLabel = new JLabel("0", javax.swing.SwingConstants.CENTER);
  scoreValueLabel.setFont(createKoreanFont(Font.BOLD, 28));
  scoreValueLabel.setForeground(new Color(255, 220, 100));
  scoreValueLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  scoreInfo.add(scoreValueLabel);
  doubleScoreBadge = new DoubleScoreBadge();
  doubleScoreBadge.setVisible(false);
  doubleScoreBadge.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  scoreInfo.add(doubleScoreBadge);
  scoreInfo.add(javax.swing.Box.createVerticalStrut(8));
  JPanel smallRow = new JPanel(); smallRow.setOpaque(false);
  smallRow.setLayout(new BoxLayout(smallRow, BoxLayout.X_AXIS));
  levelLabel = new JLabel("레벨: 1");
  levelLabel.setFont(createKoreanFont(Font.BOLD, 14));
  levelLabel.setForeground(new Color(200, 200, 200));
  levelLabel.setBorder(new EmptyBorder(0,4,0,4)); // 여백 축소 8→4
  linesLabel = new JLabel("줄: 0");
  linesLabel.setFont(createKoreanFont(Font.BOLD, 14));
  linesLabel.setForeground(new Color(200, 200, 200));
  linesLabel.setBorder(new EmptyBorder(0,4,0,4)); // 여백 축소 8→4
  smallRow.add(levelLabel);
  smallRow.add(javax.swing.Box.createHorizontalStrut(12)); // Glue 대신 고정 간격 12px
  smallRow.add(linesLabel);
  scoreInfo.add(smallRow);
  scoreInfo.add(javax.swing.Box.createVerticalStrut(6));
  
  // 게임 모드 라벨 추가
  gameModeLabel = new JLabel("모드: 아이템 모드", javax.swing.SwingConstants.CENTER);
  gameModeLabel.setFont(createKoreanFont(Font.BOLD, 13));
  gameModeLabel.setForeground(new Color(255, 215, 0)); // 골드 색상
  gameModeLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  scoreInfo.add(gameModeLabel);
  
  scoreInfo.setPreferredSize(new java.awt.Dimension(170, 160)); // 아이템 모드 UI 최적화: 220→160

  JPanel infoWrapper = createTitledPanel("게임 정보", scoreInfo, new Color(0, 230, 160), new Color(0, 230, 160));
  infoWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  infoWrapper.setMaximumSize(new java.awt.Dimension(240, 150)); // 아이템 모드 UI 최적화: 200→150
  rightPanel.add(infoWrapper);
  rightPanel.add(javax.swing.Box.createVerticalStrut(12));

  // Controls panel (titled box) — 소형 화면(450x600)에서는 숨김, 중형/대형에서만 표시
  // 프레임의 실제 크기로 판단 (450 이하는 소형으로 간주)
  final JPanel controlsBox = new JPanel(new BorderLayout());
  controlsBox.setOpaque(false);
  final JTextPane controlsPane = new JTextPane();
  controlsPane.setEditable(false);
  controlsPane.setOpaque(false);
  controlsPane.setFont(createKoreanFont(Font.PLAIN, 14));
  controlsPane.setForeground(Color.WHITE);
  StringBuilder ctrl = new StringBuilder();
  ctrl.append("↑ : 회전\n");
  ctrl.append("↓ : 소프트 드롭\n");
  ctrl.append("← → : 이동\n");
  ctrl.append("Space : 하드 드롭\n");
  ctrl.append("ESC : 나가기");
  controlsPane.setText(ctrl.toString());
  controlsBox.add(controlsPane, BorderLayout.CENTER);
  final JPanel controlsWrapper = createTitledPanel("조작키 안내", controlsBox, new Color(50, 150, 255), new Color(50, 150, 255));
  controlsWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
  controlsWrapper.setMaximumSize(new java.awt.Dimension(240, 220));
  
  // 컴포넌트가 화면에 표시될 때 프레임 크기를 확인하여 조작키 안내 표시 여부 결정
  addComponentListener(new java.awt.event.ComponentAdapter() {
    @Override
    public void componentShown(java.awt.event.ComponentEvent e) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(game.this);
          if (window != null) {
            int width = window.getWidth();
            boolean shouldShowControls = width > 450; // 450보다 크면 중형/대형
            System.out.println("[DEBUG] 창 너비: " + width + ", 조작키 안내 표시: " + shouldShowControls);
            controlsWrapper.setVisible(shouldShowControls);
          }
        }
      });
    }
  });
  
  rightPanel.add(controlsWrapper);

  add(rightPanel, BorderLayout.EAST);

    // Document default style.
    styleSet = new SimpleAttributeSet();
    StyleConstants.setFontSize(styleSet, 18);
    StyleConstants.setFontFamily(styleSet, "Courier New");
    StyleConstants.setBold(styleSet, true);
    StyleConstants.setForeground(styleSet, Color.WHITE);
    StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);

    // Initialize GameEngine
    gameEngine = new GameEngine(HEIGHT, WIDTH);
    // 모드에 따라 GameMode 설정
    String mode = System.getProperty("tetris.game.mode", "NORMAL");
    if ("ITEM".equals(mode)) {
      gameEngine.setGameMode(se.tetris.team5.gamelogic.GameMode.ITEM);
      if (gameModeLabel != null)
        gameModeLabel.setText("모드: 아이템 모드");
    } else {
      gameEngine.setGameMode(se.tetris.team5.gamelogic.GameMode.NORMAL);
      if (gameModeLabel != null)
        gameModeLabel.setText("모드: 일반 모드");
    }
    // ensure UI updates immediately when engine spawns next block
    gameEngine.addStateChangeListener(new Runnable() {
      @Override
      public void run() {
        try {
          updateAllBoards();
        } catch (Exception ex) {
          // ignore failures from listener
        }
      }
    });

    // BoardManager, BlockFactory 등은 GameEngine 내부에서만 관리

    // Set timer for block drops.
    timer = new Timer(getInitialInterval(), new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        moveDown();
        updateAllBoards();
      }
    });

    gameStartTime = gameEngine.getGameStartTime();
    syncWithGameEngine();
    updateAllBoards();

    // 게임 시작 시 타이머 완전 초기화 (0초부터 시작)
    int userInterval = getInitialInterval();
    timer.setDelay(userInterval);
    timer.setInitialDelay(userInterval); // 초기 지연을 설정하여 바로 실행 방지
    timer.start();
  }

  /**
   * Helper to create a titled boxed panel with a colored border and title label.
   * Keeps UI styling separate from game logic.
   */
  private JPanel createTitledPanel(String title, JComponent content, Color titleColor, Color borderColor) {
    // Modern boxed panel: subtle background, colored title and thin border
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);

    // Title
    JLabel titleLabel = new JLabel(title, javax.swing.SwingConstants.LEFT);
    titleLabel.setFont(createKoreanFont(Font.BOLD, 16));
    titleLabel.setForeground(titleColor);
    titleLabel.setBorder(new EmptyBorder(0, 4, 6, 4)); // 여백 축소 (좌우 6→4, 하단 8→6)

    // Inner panel with rounded border and dark background
    JPanel inner = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(24, 24, 32));
        int arc = 12;
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        // colored border
        g2.setColor(borderColor);
        g2.setStroke(new java.awt.BasicStroke(2f));
        g2.drawRoundRect(1, 1, Math.max(0, getWidth() - 2), Math.max(0, getHeight() - 2), arc, arc);
        g2.dispose();
        super.paintComponent(g);
      }
    };
    inner.setOpaque(false);
    inner.setBorder(new EmptyBorder(6, 6, 6, 6)); // 여백 축소 10→6
    // ensure content uses inner background when appropriate
    if (content != null) {
      content.setOpaque(false);
      inner.add(content, BorderLayout.CENTER);
    }

    wrapper.add(titleLabel, BorderLayout.NORTH);
    wrapper.add(inner, BorderLayout.CENTER);

    // Preferred sizing
    wrapper.setPreferredSize(
        new java.awt.Dimension(320, Math.max(120, content != null ? content.getPreferredSize().height + 48 : 140)));
    return wrapper;
  }

  protected void moveDown() {
    // GameEngine의 moveBlockDown()만 호출하여 상태 변경
    gameEngine.moveBlockDown();
    syncWithGameEngine();

    // 레벨 변경 시 타이머 속도 업데이트
    updateTimerSpeed();

    if (gameEngine.isGameOver()) {
      gameOver();
      return;
    }
  }

  protected void moveRight() {
    gameEngine.moveBlockRight();
    syncWithGameEngine();
  }

  protected void moveLeft() {
    gameEngine.moveBlockLeft();
    syncWithGameEngine();
  }

  // GameEngine과 UI 상태를 동기화하는 메서드 (next 블록만)
  private void syncWithGameEngine() {
    // UI는 GameEngine의 상태만 참조
    // (필드에 curr, next, x, y, boardManager, 점수 등 별도 저장하지 않음)
    // 필요시 바로 gameEngine.getCurrentBlock() 등으로 참조
  }

  /**
   * 타이머 속도를 현재 레벨에 맞게 업데이트합니다
   */
  private void updateTimerSpeed() {
    if (timer != null && !isPaused && !isTimeStopped) {
      int newInterval = gameEngine.getGameScoring().getTimerInterval();
      timer.setDelay(newInterval);
    }
  }

  protected void hardDrop() {
    gameEngine.hardDrop();
    syncWithGameEngine();

    // 레벨 변경 시 타이머 속도 업데이트
    updateTimerSpeed();

    if (gameEngine.isGameOver()) {
      gameOver();
      return;
    }
  }

  /**
   * 블록을 회전시키는 메서드 (Wall Kick 포함)
   */
  protected void rotateBlock() {
    gameEngine.rotateBlock();
    syncWithGameEngine();
  }

  // 블록 복사 메서드
  // Method removed as it is unused

  // 두 블록의 모양이 같은지 확인

  /**
   * 모든 보드를 업데이트합니다
   */
  private void updateAllBoards() {
    updateGameBoard();
    updateScoreBoard();
    updateNextBlockBoard();
  }

  /**
   * 게임 보드를 업데이트합니다
   */
  private void updateGameBoard() {
    // Prefer graphical rendering by default
    gameBoard.setShowTextOverlay(false);
    StringBuffer sb = new StringBuffer();

    // 게임 보드 테두리
    for (int t = 0; t < WIDTH + 2; t++) {
      sb.append(BORDER_CHAR);
    }
    sb.append("\n");

    // BoardManager에서 보드 정보 가져오기
    int[][] board = gameEngine.getBoardManager().getBoard();
    Color[][] boardColors = gameEngine.getBoardManager().getBoardColors();

    // 현재 이동 중인 블록 정보
    Block currBlock = gameEngine.getCurrentBlock();
    int currX = gameEngine.getX();
    int currY = gameEngine.getY();

    for (int i = 0; i < board.length; i++) {
      sb.append(BORDER_CHAR);
      for (int j = 0; j < board[i].length; j++) {
        boolean isCurrentBlock = false;
        // 현재 이동 중인 블록의 좌표에 해당하면 블록 표시
        if (currBlock != null) {
          int relX = j - currX;
          int relY = i - currY;
          if (relX >= 0 && relX < currBlock.width() && relY >= 0 && relY < currBlock.height()) {
            if (currBlock.getShape(relX, relY) == 1) {
              isCurrentBlock = true;
            }
          }
        }
        if (isCurrentBlock) {
          se.tetris.team5.items.Item item = currBlock.getItem(j - currX, i - currY);
          if (item != null) {
            if (item instanceof se.tetris.team5.items.TimeStopItem) {
              sb.append("⏱");
            } else if (item instanceof se.tetris.team5.items.DoubleScoreItem) {
              sb.append("x2");
            } else if (item instanceof se.tetris.team5.items.LineClearItem) {
              sb.append("L");
            } else {
              sb.append("★");
            }
          } else {
            sb.append("O");
          }
        } else if (board[i][j] == 1 || board[i][j] == 2) {
          se.tetris.team5.items.Item item = gameEngine.getBoardManager().getBoardItem(j, i);
          if (item != null) {
            if (item instanceof se.tetris.team5.items.TimeStopItem) {
              sb.append("⏱");
            } else if (item instanceof se.tetris.team5.items.DoubleScoreItem) {
              sb.append("x2");
            } else if (item instanceof se.tetris.team5.items.LineClearItem) {
              sb.append("L");
            } else {
              sb.append("★");
            }
          } else {
            sb.append("O");
          }
        } else {
          sb.append(" ");
        }
      }
      sb.append(BORDER_CHAR);
      sb.append("\n");
    }

    for (int t = 0; t < WIDTH + 2; t++) {
      sb.append(BORDER_CHAR);
    }

    gameBoard.setText(sb.toString());
    StyledDocument doc = gameBoard.getStyledDocument();

    // 기본 스타일 적용 (테두리 색상을 하얀색으로 고정)
    SimpleAttributeSet borderStyle = new SimpleAttributeSet();
    StyleConstants.setForeground(borderStyle, Color.WHITE);
    StyleConstants.setFontSize(borderStyle, 18);
    StyleConstants.setFontFamily(borderStyle, "Courier New");
    StyleConstants.setBold(borderStyle, true);
    StyleConstants.setLineSpacing(borderStyle, -0.4f);
    doc.setCharacterAttributes(0, doc.getLength(), borderStyle, false);
    doc.setParagraphAttributes(0, doc.getLength(), borderStyle, false);

    // 각 블록에 색상 적용
    int textOffset = WIDTH + 3; // 첫 번째 줄(위쪽 테두리) 건너뛰기
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        boolean isCurrentBlock = false;
        if (currBlock != null) {
          int relX = j - currX;
          int relY = i - currY;
          if (relX >= 0 && relX < currBlock.width() && relY >= 0 && relY < currBlock.height()) {
            if (currBlock.getShape(relX, relY) == 1) {
              isCurrentBlock = true;
            }
          }
        }
        if (isCurrentBlock && currBlock.getColor() != null) {
          SimpleAttributeSet colorStyle = new SimpleAttributeSet(borderStyle);
          StyleConstants.setForeground(colorStyle, currBlock.getColor());
          int charPos = textOffset + j + 1;
          if (charPos < doc.getLength()) {
            doc.setCharacterAttributes(charPos, 1, colorStyle, false);
          }
        } else if ((board[i][j] == 1 || board[i][j] == 2) && boardColors[i][j] != null) {
          SimpleAttributeSet colorStyle = new SimpleAttributeSet(borderStyle);
          StyleConstants.setForeground(colorStyle, boardColors[i][j]);
          int charPos = textOffset + j + 1;
          if (charPos < doc.getLength()) {
            doc.setCharacterAttributes(charPos, 1, colorStyle, false);
          }
        }
      }
      textOffset += WIDTH + 3;
    }

    // In addition to updating the text (for compatibility), push the raw board data
    // and
    // any items on cells to the graphical renderer so the visual blocks and items
    // are painted.
    se.tetris.team5.items.Item[][] items = new se.tetris.team5.items.Item[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        items[i][j] = gameEngine.getBoardManager().getBoardItem(j, i);
      }
    }
    int ghostY = gameEngine.getGhostY();
    gameBoard.renderBoard(board, boardColors, items, currBlock, currX, currY, ghostY);
    // If the engine recorded cleared rows during the last move, consume them and
    // trigger animations.
    try {
      java.util.List<Integer> clearedRows = gameEngine.consumeLastClearedRows();
      if (clearedRows != null && !clearedRows.isEmpty()) {
        System.out.println("[game screen] consuming cleared rows for animation: " + clearedRows);
        gameBoard.triggerClearAnimation(clearedRows);
      }
    } catch (Exception ex) {
      // safe-guard: don't let UI update fail due to animation triggering
    }
  }

  /**
   * 점수 보드를 업데이트합니다
   */
  private void updateScoreBoard() {
    // Update numeric labels for immediate visibility
    int currentScore = gameEngine.getGameScoring().getCurrentScore();
    int level = gameEngine.getGameScoring().getLevel();
    int linesCleared = gameEngine.getGameScoring().getLinesCleared();

    scoreValueLabel.setText(String.format("%,d", currentScore));
    levelLabel.setText("레벨: " + level);
    linesLabel.setText("줄: " + linesCleared);

    // 게임 모드 라벨 업데이트
    String gameMode = System.getProperty("tetris.game.mode", "ITEM");
    String gameDiff = System.getProperty("tetris.game.difficulty", "NORMAL");
    if ("ITEM".equals(gameMode)) {
      gameModeLabel.setText("모드: 아이템 모드");
      gameModeLabel.setForeground(new Color(255, 215, 0)); // 골드 색상
    } else {
      String modeText = "모드: 일반 모드";
      switch (gameDiff) {
        case "EASY":
          modeText += " (이지)";
          gameModeLabel.setForeground(new Color(144, 238, 144)); // 라이트 그린
          break;
        case "NORMAL":
          modeText += " (노말)";
          gameModeLabel.setForeground(new Color(173, 216, 230)); // 라이트 블루
          break;
        case "HARD":
          modeText += " (하드)";
          gameModeLabel.setForeground(new Color(255, 99, 99)); // 라이트 레드
          break;
        default:
          modeText += " (노말)";
          gameModeLabel.setForeground(new Color(173, 216, 230)); // 라이트 블루
          break;
      }
      gameModeLabel.setText(modeText);
    }

    // Keep the text pane (compat/backwards) updated but don't show it over graphics
    gameBoard.setShowTextOverlay(false);
    StringBuilder sb = new StringBuilder();
    sb.append("점수: ").append(String.format("%,d", currentScore)).append("\n");
    sb.append("레벨: ").append(level).append("\n");
    sb.append("줄: ").append(linesCleared).append("\n");
    sb.append("\n");

    // 게임 모드 표시
    String mode = System.getProperty("tetris.game.mode", "ITEM");
    String diff = System.getProperty("tetris.game.difficulty", "NORMAL");
    if ("ITEM".equals(mode)) {
      sb.append("모드: 아이템 모드\n");
    } else {
      sb.append("모드: 일반 모드");
      switch (diff) {
        case "EASY":
          sb.append(" (이지)");
          break;
        case "NORMAL":
          sb.append(" (노말)");
          break;
        case "HARD":
          sb.append(" (하드)");
          break;
        default:
          sb.append(" (노말)");
          break;
      }
      sb.append("\n");
    }
    sb.append("\n");

    if (gameEngine.hasTimeStopCharge()) {
      sb.append("⏱️ 타임스톱: 사용 가능\n");
      sb.append("(Shift로 5초 정지)\n");
      sb.append("\n");
    }

    scoreBoard.getTextPane().setText(sb.toString());
    scoreBoard.getTextPane().getStyledDocument().setCharacterAttributes(
        0, scoreBoard.getTextPane().getDocument().getLength(),
        scoreBoard.getStyleSet(), false);

    // Double-score visual indicator
    try {
      long rem = gameEngine.getDoubleScoreRemainingMillis();
      if (rem > 0) {
        doubleScoreBadge.setTotalMillis(20_000);
        doubleScoreBadge.setRemainingMillis(rem);
        doubleScoreBadge.setVisible(true);
        // highlight score label
        scoreValueLabel.setForeground(new Color(255, 220, 100));
      } else {
        doubleScoreBadge.setRemainingMillis(0);
        doubleScoreBadge.setVisible(false);
        scoreValueLabel.setForeground(new Color(255, 220, 100));
      }
    } catch (Exception ex) {
      // ignore UI update errors
    }
  }

  /**
   * 다음 블록 보드를 업데이트합니다
   */
  private void updateNextBlockBoard() {
    StringBuilder sb = new StringBuilder();

    Block nextBlock = gameEngine.getNextBlock();
    if (nextBlock != null) {
      // 4x4 크기의 블록 표시 영역
      for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
          if (row < nextBlock.height() && col < nextBlock.width() && nextBlock.getShape(col, row) == 1) {
            // 아이템이 있으면 종류에 따라 다른 모양 표시
            se.tetris.team5.items.Item item = nextBlock.getItem(col, row);
            if (item != null) {
              if (item instanceof se.tetris.team5.items.TimeStopItem) {
                sb.append("⏱");
              } else if (item instanceof se.tetris.team5.items.DoubleScoreItem) {
                sb.append("x2");
              } else if (item instanceof se.tetris.team5.items.LineClearItem) {
                sb.append("L");
              } else {
                sb.append("★");
              }
            } else {
              sb.append("O");
            }
          } else {
            sb.append(" ");
          }
        }
        sb.append("\n");
      }
    } else {
      // next가 null인 경우 빈 영역 표시
      for (int i = 0; i < 4; i++) {
        sb.append("    \n");
      }
    }

    nextBlockBoard.getTextPane().setText(sb.toString());
    StyledDocument doc = nextBlockBoard.getTextPane().getStyledDocument();

    // 기본 스타일 적용
    SimpleAttributeSet baseStyle = new SimpleAttributeSet(nextBlockBoard.getStyleSet());
    doc.setCharacterAttributes(0, doc.getLength(), baseStyle, false);

    // 다음 블록에 색상 적용
    if (nextBlock != null) {
      int textOffset = 0;
      for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
          if (row < nextBlock.height() && col < nextBlock.width() && nextBlock.getShape(col, row) == 1) {
            SimpleAttributeSet colorStyle = new SimpleAttributeSet(baseStyle);
            StyleConstants.setForeground(colorStyle, nextBlock.getColor());

            int charPos = textOffset + col;
            if (charPos < doc.getLength()) {
              doc.setCharacterAttributes(charPos, 1, colorStyle, false);
            }
          }
        }
        textOffset += 5; // 4개 문자 + 줄바꿈 1개
      }
    }

    // 아이템 설명 업데이트 - 다음 블록에 포함된 아이템만 설명
    String itemDesc = "다음 블록에 포함된 아이템이 없습니다.";

    if (nextBlock != null) {
      se.tetris.team5.items.Item found = null;
      outer: for (int r = 0; r < nextBlock.height(); r++) {
        for (int c = 0; c < nextBlock.width(); c++) {
          se.tetris.team5.items.Item it = nextBlock.getItem(c, r);
          if (it != null) {
            found = it;
            break outer;
          }
        }
      }
      if (found != null) {
        itemDesc = describeItem(found, false);
      }
    }

    if (itemDescPane != null) {
      itemDescPane.setText(itemDesc);
      try {
        itemDescPane.getStyledDocument().setCharacterAttributes(0, itemDescPane.getDocument().getLength(),
            new SimpleAttributeSet(), false);
      } catch (Exception ex) {
        // ignore styling errors
      }
    }
    // Ensure the graphical preview repaints immediately so the UI stays in sync
    // with engine state
    if (nextVisualPanel != null) {
      nextVisualPanel.repaint();
    }
    if (nextBlockBoard != null) {
      nextBlockBoard.repaint();
    }
  }

  /**
   * Return a user-facing description for an item.
   */
  private String describeItem(se.tetris.team5.items.Item it, boolean held) {
    if (it == null)
      return "다음 블록에 포함된 아이템이 없습니다.";

    String name = it.getName();

    // 다음 블록에 포함된 아이템 설명
    if (it instanceof se.tetris.team5.items.TimeStopItem || "TimeStopItem".equals(name))
      return "Shift 키로 5초간 게임을 멈출 수 있습니다!";
    
    if (it instanceof se.tetris.team5.items.BombItem || "BombItem".equals(name))
      return "블록 고정 시 폭발로 주변 블록을 제거합니다.";
    
    if (it instanceof se.tetris.team5.items.LineClearItem || "LineClearItem".equals(name))
      return "블록 고정 시 해당 줄을 즉시 삭제합니다.";
    
    if (it instanceof se.tetris.team5.items.DoubleScoreItem || "DoubleScoreItem".equals(name))
      return "블록 고정 시 20초간 모든 점수가 2배가 됩니다!";
    
    if (it instanceof se.tetris.team5.items.ScoreItem || "ScoreItem".equals(name)) {
      se.tetris.team5.items.ScoreItem si = (se.tetris.team5.items.ScoreItem) it;
      return "블록 고정 시 즉시 +" + si.getScoreAmount() + "점을 획득합니다.";
    }

    if (it instanceof se.tetris.team5.items.WeightBlockItem || "WeightBlockItem".equals(name))
      return "다음 블록이 무게추 블록(WBlock)으로 생성됩니다.";
    
    return "특수 아이템: " + name;
  }

  /**
   * 호환성을 위한 drawBoard 메서드
   */
  public void drawBoard() {
    updateAllBoards();
  }

  /**
   * 게임을 일시정지합니다
   */
  private void pauseGame() {
    isPaused = true;
    timer.stop();
    drawPauseMenu();
  }

  /**
   * ESC로 호출되는 일시정지 + 선택 모달. 타이머를 정지시키고 재개/메뉴로 나가기/게임 종료 선택을 받음.
   */
  private void showPauseConfirmDialog() {
    // Stop timer and mark paused
    isPaused = true;
    if (timer != null)
      timer.stop();

    String[] options = { "계속", "메뉴로 나가기", "게임 종료" };
    int choice = javax.swing.JOptionPane.showOptionDialog(this,
        "게임을 일시중단했습니다.\n\n" +
            "• 계속: 현재 게임을 이어서 진행합니다.\n" +
            "• 메뉴로 나가기: 현재 게임을 취소하고 메인 메뉴로 이동합니다.\n" +
            "• 게임 종료: 테트리스 프로그램을 완전히 종료합니다.",
        "일시정지",
        javax.swing.JOptionPane.DEFAULT_OPTION,
        javax.swing.JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);

    if (choice == 1) {
      // 메뉴로 나가기 선택: 현재 블록을 보드에서 제거 및 홈으로 이동
      Block currBlock = gameEngine.getCurrentBlock();
      int x = gameEngine.getX();
      int y = gameEngine.getY();
      if (currBlock != null) {
        gameEngine.getBoardManager().eraseBlock(currBlock, x, y);
      }
      isPaused = false;
      if (timer != null)
        timer.stop();
      screenController.showScreen("home");
      return;
    } else if (choice == 2) {
      // 게임 종료 선택: 테트리스 프로그램 완전 종료
      isPaused = false;
      if (timer != null)
        timer.stop();
      System.exit(0); // 프로그램 완전 종료
      return;
    }

    // 기본: 계속하기 (choice == 0 또는 창 닫기)
    resumeGame();
  }

  /**
   * 게임을 재개합니다
   */
  private void resumeGame() {
    isPaused = false;
    pauseMenuIndex = 0;
    timer.start();
    updateAllBoards(); // 게임 화면 복원
  }

  /**
   * 일시정지 메뉴를 그립니다 (3개 옵션 포함)
   */
  private void drawPauseMenu() {
    StringBuilder sb = new StringBuilder();

    // 일시정지 화면
    sb.append("\n\n\n\n");
    sb.append("          === 게임 일시정지 ===\n\n");

    // 메뉴 옵션들
    for (int i = 0; i < pauseMenuOptions.length; i++) {
      sb.append("          ");
      if (i == pauseMenuIndex) {
        sb.append("► ");
      } else {
        sb.append("  ");
      }
      sb.append(pauseMenuOptions[i]);

      // 각 옵션에 대한 간단한 설명 추가
      switch (i) {
        case 0:
          sb.append(" (현재 게임 이어하기)");
          break;
        case 1:
          sb.append(" (게임 취소 후 메인 메뉴)");
          break;
        case 2:
          sb.append(" (프로그램 완전 종료)");
          break;
      }
      sb.append("\n\n");
    }

    sb.append("\n");
    sb.append("     ↑↓: 선택    Enter: 확인    ESC: 계속\n");

    // 게임 보드에 일시정지 메뉴 표시
    // Enable text overlay so the pause menu (text) is visible over the graphical
    // board
    gameBoard.setShowTextOverlay(true);
    gameBoard.setText(sb.toString());
    StyledDocument doc = gameBoard.getStyledDocument();

    // 기본 스타일 적용
    SimpleAttributeSet baseStyle = new SimpleAttributeSet();
    StyleConstants.setForeground(baseStyle, Color.WHITE);
    StyleConstants.setFontSize(baseStyle, 16);
    StyleConstants.setFontFamily(baseStyle, "Courier New");
    StyleConstants.setBold(baseStyle, true);
    StyleConstants.setAlignment(baseStyle, StyleConstants.ALIGN_CENTER);

    doc.setCharacterAttributes(0, doc.getLength(), baseStyle, false);
    doc.setParagraphAttributes(0, doc.getLength(), baseStyle, false);

    // 선택된 메뉴 항목을 노란색으로 강조
    String text = sb.toString();
    String selectedOption = "> " + pauseMenuOptions[pauseMenuIndex];
    int selectedIndex = text.indexOf(selectedOption);
    if (selectedIndex >= 0) {
      SimpleAttributeSet highlightStyle = new SimpleAttributeSet(baseStyle);
      StyleConstants.setForeground(highlightStyle, Color.YELLOW);
      doc.setCharacterAttributes(selectedIndex, selectedOption.length(), highlightStyle, false);
    }
  }

  public void reset() {
    // 타이머 정지
    if (timer != null) {
      timer.stop();
    }

    // 타임스톱 타이머 정지 및 초기화
    if (timeStopCountdownTimer != null) {
      timeStopCountdownTimer.stop();
      timeStopCountdownTimer = null;
    }
    // hide graphical overlay if present
    if (timeStopOverlay != null) {
      timeStopOverlay.setVisible(false);
    }
    if (gameBoard != null) {
      gameBoard.setShowTextOverlay(false);
    }
    isTimeStopped = false;

    // 게임 모드에 따라 아이템 설명 패널 가시성 및 엔진 모드 설정
    String mode = System.getProperty("tetris.game.mode", "NORMAL");
    if (itemDescWrapper != null) {
      itemDescWrapper.setVisible("ITEM".equals(mode));
    }
    if ("ITEM".equals(mode)) {
      gameEngine.setGameMode(se.tetris.team5.gamelogic.GameMode.ITEM);
      if (gameModeLabel != null)
        gameModeLabel.setText("모드: 아이템 모드");
    } else {
      gameEngine.setGameMode(se.tetris.team5.gamelogic.GameMode.NORMAL);
      if (gameModeLabel != null)
        gameModeLabel.setText("모드: 일반 모드");
    }

    // GameEngine을 통해 게임 리셋
    gameEngine.resetGame();
    gameStartTime = gameEngine.getGameStartTime();
    isPaused = false;
    pauseMenuIndex = 0;
    updateAllBoards();
    // 새 게임 시작 시 사용자 설정 속도로 타이머 완전 초기화
    int userInterval = getInitialInterval(); // 최신 사용자 설정 속도 가져오기
    timer.setDelay(userInterval);
    timer.setInitialDelay(userInterval); // 바로 실행 방지
    timer.start(); // 0초부터 새로 시작
    // macOS 대응: 리셋 시에도 포커스 강제 요청
    requestFocusInWindow();
  }

  private void gameOver() {
    timer.stop(); // 타이머 정지

    // 현재 블록을 보드에서 제거 (다음 게임에 영향 안주도록)
    Block currBlock = gameEngine.getCurrentBlock();
    int x = gameEngine.getX();
    int y = gameEngine.getY();
    if (currBlock != null) {
      gameEngine.getBoardManager().eraseBlock(currBlock, x, y);
    }

    // 플레이 시간 계산
    long playTime = System.currentTimeMillis() - gameStartTime;

    // 현재 게임 모드 정보 가져오기
    String gameMode = System.getProperty("tetris.game.mode", "ITEM");
    String gameDiff = System.getProperty("tetris.game.difficulty", "NORMAL");
    String modeDisplayName;
    String modeString;

    if ("ITEM".equals(gameMode)) {
      modeDisplayName = "아이템 모드";
      modeString = "ITEM";
    } else {
      modeString = "NORMAL_" + gameDiff;
      switch (gameDiff) {
        case "EASY":
          modeDisplayName = "일반 모드 - 쉬움";
          break;
        case "NORMAL":
          modeDisplayName = "일반 모드 - 보통";
          break;
        case "HARD":
          modeDisplayName = "일반 모드 - 어려움";
          break;
        case "EXPERT":
          modeDisplayName = "일반 모드 - 전문가";
          break;
        default:
          modeDisplayName = "일반 모드 - 보통";
          break;
      }
    }

    // Prompt the user for their name using a modal dialog with score and mode info.
    ScoreManager scoreManager = ScoreManager.getInstance();
    int currentScore = gameEngine.getGameScoring().getCurrentScore();
    int level = gameEngine.getGameScoring().getLevel();
    int linesCleared = gameEngine.getGameScoring().getLinesCleared();

    String message = String.format(
        "게임이 끝났습니다!\n\n" +
            "게임 모드: %s\n" +
            "최종 점수: %,d점\n" +
            "달성 레벨: %d\n" +
            "제거한 줄: %d\n\n" +
            "플레이어 이름을 입력하세요:",
        modeDisplayName, currentScore, level, linesCleared);

    // 테스트 모드일 때는 다이얼로그 표시하지 않음
    String inputName;
    if ("true".equals(System.getProperty("tetris.test.mode"))) {
      // 테스트 모드에서는 기본 플레이어 이름 사용
      inputName = "TestPlayer";
    } else {
      // Note: this call is already on the EDT because Timer is a Swing Timer,
      // so it's safe to show a modal dialog here.
      inputName = JOptionPane.showInputDialog(this,
          message,
          "게임 종료",
          JOptionPane.PLAIN_MESSAGE);

      if (inputName == null) {
        // User cancelled -> go back to home without saving
        screenController.showScreen("home");
        return;
      }
      inputName = inputName.trim();
      if (inputName.isEmpty())
        inputName = "Player";
    }

    // Save the score with mode information and navigate to the scoreboard
    scoreManager.addScore(inputName, currentScore, level, linesCleared, playTime, modeString);

    // 방금 추가된 점수 정보를 시스템 프로퍼티로 저장 (스코어보드에서 강조용)
    System.setProperty("tetris.highlight.playerName", inputName);
    System.setProperty("tetris.highlight.score", String.valueOf(currentScore));
    System.setProperty("tetris.highlight.mode", modeString);
    System.setProperty("tetris.highlight.playTime", String.valueOf(playTime));

    screenController.showScreen("score");
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // GameSettings에서 키 코드 가져오기
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    int downKey = settings.getKeyCode("down");
    int leftKey = settings.getKeyCode("left");
    int rightKey = settings.getKeyCode("right");
    int rotateKey = settings.getKeyCode("rotate");
    int dropKey = settings.getKeyCode("drop");
    int pauseKey = settings.getKeyCode("pause");

    // 일시정지 상태일 때의 키 처리
    if (isPaused) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
          pauseMenuIndex = (pauseMenuIndex - 1 + pauseMenuOptions.length) % pauseMenuOptions.length;
          drawPauseMenu();
          break;
        case KeyEvent.VK_DOWN:
          pauseMenuIndex = (pauseMenuIndex + 1) % pauseMenuOptions.length;
          drawPauseMenu();
          break;
        case KeyEvent.VK_ENTER:
          if (pauseMenuIndex == 0) { // 게임 계속
            resumeGame();
          } else if (pauseMenuIndex == 1) { // 메뉴로 나가기
            // 게임 완전 정지 및 상태 정리
            timer.stop();
            isPaused = false;
            pauseMenuIndex = 0;

            // 현재 블록을 보드에서 제거 (다음 게임에 영향 안주도록)
            Block currBlock = gameEngine.getCurrentBlock();
            int x = gameEngine.getX();
            int y = gameEngine.getY();
            if (currBlock != null) {
              gameEngine.getBoardManager().eraseBlock(currBlock, x, y);
            }

            // ScreenController를 통해 홈으로 돌아가기
            screenController.showScreen("home");
          } else { // pauseMenuIndex == 2: 게임 종료
            // 게임 완전 정지 및 상태 정리
            timer.stop();
            isPaused = false;
            pauseMenuIndex = 0;

            // 프로그램 완전 종료
            System.exit(0);
          }
          break;
        case KeyEvent.VK_ESCAPE:
          resumeGame(); // ESC로도 게임 계속할 수 있게
          break;
      }

      // 일시정지 상태에서도 설정된 일시정지 키로 게임 재개 가능
      if (pauseKey != -1 && e.getKeyCode() == pauseKey) {
        resumeGame();
      }

      return; // 일시정지 상태에서는 다른 키 무시
    }

    // 게임 진행 중일 때의 키 처리 (설정된 키 사용)
    int keyCode = e.getKeyCode();

    if (keyCode == KeyEvent.VK_ESCAPE) {
      // Show a modal pause dialog that asks whether to resume or exit.
      showPauseConfirmDialog();
    } else if (keyCode == downKey) {
      moveDown();
      drawBoard();
    } else if (keyCode == rightKey) {
      moveRight();
      drawBoard();
    } else if (keyCode == leftKey) {
      moveLeft();
      drawBoard();
    } else if (keyCode == rotateKey) {
      rotateBlock();
      drawBoard();
    } else if (keyCode == dropKey) {
      hardDrop();
      drawBoard();
    } else if (keyCode == KeyEvent.VK_SHIFT) {
      // shift 키로 타임스톱 사용
      if (gameEngine.hasTimeStopCharge() && !isTimeStopped) {
        activateTimeStop();
      }
    } else if (keyCode == pauseKey) {
      pauseGame();
    }
  }

  /**
   * 타임스톱을 활성화합니다 (5초간 게임 멈춤)
   */
  private void activateTimeStop() {
    isTimeStopped = true;
    gameEngine.useTimeStop(); // 충전 소모
    timer.stop(); // 게임 타이머 정지

    // 화면 업데이트 (타임스톱 상태 표시 제거)
    updateAllBoards();

    // Start a 5-second visible countdown (tick every 1s) and show overlay
    timeStopRemaining = 5;
    showTimeStopMessage(timeStopRemaining);

    // Stop any existing countdown timer first
    if (timeStopCountdownTimer != null) {
      timeStopCountdownTimer.stop();
      timeStopCountdownTimer = null;
    }

    timeStopCountdownTimer = new Timer(1000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        timeStopRemaining -= 1;
        if (timeStopRemaining > 0) {
          // update overlay with remaining seconds
          showTimeStopMessage(timeStopRemaining);
        } else {
          // countdown finished
          if (timeStopCountdownTimer != null) {
            timeStopCountdownTimer.stop();
            timeStopCountdownTimer = null;
          }
          deactivateTimeStop();
        }
      }
    });
    timeStopCountdownTimer.setRepeats(true);
    timeStopCountdownTimer.start();
  }

  /**
   * 타임스톱을 해제합니다
   */
  private void deactivateTimeStop() {
    isTimeStopped = false;

    // 타임스톱 타이머 정리
    if (timeStopCountdownTimer != null) {
      timeStopCountdownTimer.stop();
      timeStopCountdownTimer = null;
    }

    // hide graphical overlay immediately
    if (timeStopOverlay != null) {
      timeStopOverlay.setVisible(false);
    }
    if (timeStopNumberLabel != null) {
      timeStopNumberLabel.setText("");
    }
    if (timeStopIconLabel != null) {
      timeStopIconLabel.setText("");
    }
    if (timeStopSubLabel != null) {
      timeStopSubLabel.setText("");
    }

    // 게임 타이머 재시작
    if (!isPaused && !gameEngine.isGameOver()) {
      // 타임스톱 중 레벨이 변경되었을 수 있으므로 속도 업데이트 후 시작
      int currentInterval = gameEngine.getGameScoring().getTimerInterval();
      timer.setDelay(currentInterval);
      timer.start();
    }

    // 화면 복원
    updateAllBoards();
  }

  /**
   * 타임스톱 메시지를 표시합니다
   */
  /**
   * Show a time-stop overlay message with remaining seconds visible.
   * 
   * @param seconds remaining seconds to display (e.g. 5..1)
   */
  private void showTimeStopMessage(int seconds) {
    StringBuilder sb = new StringBuilder();

    // center the message with a prominent countdown
    // We now use a graphical semi-transparent overlay with a large countdown label.
    if (timeStopOverlay != null && timeStopNumberLabel != null) {
      // Update the three labels instead of HTML to avoid clipping and give precise control
      timeStopIconLabel.setText("TIME STOP");
      timeStopNumberLabel.setText(String.valueOf(seconds));
      timeStopSubLabel.setText("초 남음");
      timeStopOverlay.setVisible(true);
      // Also keep the text overlay off so we don't have duplicate messages
      gameBoard.setShowTextOverlay(false);
    } else {
      // fallback to the old text overlay if graphical overlay isn't available
      sb.append("\n\n\n\n\n");
      sb.append(String.format("          ⏱️  타임스톱: %d초 남음  ⏱️\n\n", seconds));
      sb.append("          게임이 일시정지되었습니다.\n\n");
      sb.append("          잠시 숨을 고르세요...\n");
      gameBoard.setShowTextOverlay(true);
      gameBoard.setText(sb.toString());
      StyledDocument doc = gameBoard.getStyledDocument();

      // 스타일: cyan for message and a slightly larger countdown
      SimpleAttributeSet messageStyle = new SimpleAttributeSet();
      StyleConstants.setForeground(messageStyle, Color.CYAN);
      StyleConstants.setFontSize(messageStyle, 18);
      StyleConstants.setFontFamily(messageStyle, "Courier New");
      StyleConstants.setBold(messageStyle, true);
      StyleConstants.setAlignment(messageStyle, StyleConstants.ALIGN_CENTER);

      doc.setCharacterAttributes(0, doc.getLength(), messageStyle, false);
      doc.setParagraphAttributes(0, doc.getLength(), messageStyle, false);
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  // 색맹 모드 변경 시 모든 색상 업데이트
  public void updateColorsForColorblindMode() {
    // 현재 블록과 다음 블록의 색상 업데이트
    Block currBlock = gameEngine.getCurrentBlock();
    Block nextBlock = gameEngine.getNextBlock();
    if (currBlock != null) {
      currBlock.updateColor();
    }
    if (nextBlock != null) {
      nextBlock.updateColor();
    }
    // 보드에 고정된 블록들의 색상 업데이트
    updateBoardColors();
    // 화면 다시 그리기
    updateAllBoards();
  }

  // 게임 속도 변경 시 타이머 간격 업데이트
  public void updateGameSpeed() {
    if (timer != null && !isPaused) {
      // 사용자 설정 속도로만 타이머 설정 (레벨 기반 속도 증가 제거)
      int userInterval = getInitialInterval();
      timer.stop(); // 현재 타이머 정지
      timer.setDelay(userInterval); // 새 간격 설정
      timer.setInitialDelay(userInterval); // 초기 지연 설정
      timer.start(); // 0초부터 새로 시작
    }
  }

  // 보드에 고정된 블록들의 색상을 색맹 모드에 맞게 업데이트
  private void updateBoardColors() {
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();

    // BoardManager를 통해 보드와 색상 정보 접근
    int[][] board = gameEngine.getBoardManager().getBoard();
    Color[][] boardColors = gameEngine.getBoardManager().getBoardColors();

    for (int i = 0; i < HEIGHT; i++) {
      for (int j = 0; j < WIDTH; j++) {
        if (board[i][j] == 2 && boardColors[i][j] != null) {
          // 기존 색상을 바탕으로 블록 타입을 추정하고 새로운 색상 적용
          String blockType = guessBlockTypeFromColor(boardColors[i][j]);
          boardColors[i][j] = settings.getColorForBlock(blockType);
        }
      }
    }
  }

  // 색상을 바탕으로 블록 타입을 추정하는 헬퍼 메소드
  private String guessBlockTypeFromColor(Color color) {
    // 기본 색상을 바탕으로 블록 타입 추정
    if (color.equals(Color.CYAN))
      return "I";
    if (color.equals(Color.YELLOW))
      return "O";
    if (color.equals(Color.MAGENTA))
      return "T";
    if (color.equals(Color.ORANGE))
      return "L";
    if (color.equals(Color.BLUE))
      return "J";
    if (color.equals(Color.GREEN))
      return "S";
    if (color.equals(Color.RED))
      return "Z";
    if (color.equals(new Color(64, 64, 64)))
      return "W";

    // 색맹 모드 색상들도 체크 (8색 명확한 팔레트)
    if (color.equals(new Color(135, 206, 250))) // sky blue (하늘색)
      return "I";
    if (color.equals(new Color(255, 255, 0))) // yellow (노란색)
      return "O";
    if (color.equals(new Color(199, 21, 133))) // reddish purple (적자색)
      return "T";
    if (color.equals(new Color(255, 165, 0))) // orange (주황색)
      return "L";
    if (color.equals(new Color(0, 0, 255))) // blue (파란색)
      return "J";
    if (color.equals(new Color(0, 158, 115))) // bluish green (청록색)
      return "S";
    if (color.equals(new Color(213, 94, 0))) // vermilion (주홍색)
      return "Z";
    if (color.equals(new Color(85, 85, 85))) // 밝은 검정색
      return "W";

    // 현재 설정의 색상과도 비교
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    String[] blockTypes = { "I", "O", "T", "L", "J", "S", "Z", "W" };
    for (String type : blockTypes) {
      if (color.equals(settings.getColorForBlock(type))) {
        return type;
      }
    }

    return "O"; // 기본값
  }
}
