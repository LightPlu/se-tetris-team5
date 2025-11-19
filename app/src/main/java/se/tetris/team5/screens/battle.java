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
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.ScreenController;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.components.game.GameBoard;
import se.tetris.team5.components.game.NextBlockBoard;
import se.tetris.team5.components.game.ScoreBoard;

/**
 * 2인 대전 모드
 * 왼쪽: WASD + Z (하드드롭)
 * 오른쪽: 화살표 + 오른쪽Ctrl (하드드롭)
 */
public class battle extends JPanel implements KeyListener {

  private static final long serialVersionUID = 1L;

  public static final int HEIGHT = GameBoard.HEIGHT;
  public static final int WIDTH = GameBoard.WIDTH;
  public static final char BORDER_CHAR = GameBoard.BORDER_CHAR;

  private ScreenController screenController;

  // 플레이어1 (왼쪽) 컴포넌트
  private GameBoard player1GameBoard;
  private NextBlockBoard player1NextBlockBoard;
  private ScoreBoard player1ScoreBoard;
  private JPanel player1NextVisualPanel;
  private JLabel player1ScoreValueLabel;
  private JLabel player1LevelLabel;
  private JLabel player1LinesLabel;
  private JLabel player1TimerLabel;
  private GameEngine player1GameEngine;
  private Timer player1Timer;

  // 플레이어2 (오른쪽) 컴포넌트
  private GameBoard player2GameBoard;
  private NextBlockBoard player2NextBlockBoard;
  private ScoreBoard player2ScoreBoard;
  private JPanel player2NextVisualPanel;
  private JLabel player2ScoreValueLabel;
  private JLabel player2LevelLabel;
  private JLabel player2LinesLabel;
  private JLabel player2TimerLabel;
  private GameEngine player2GameEngine;
  private Timer player2Timer;

  private SimpleAttributeSet styleSet;
  private boolean isPaused = false;
  private boolean isGameOver = false;
  private String originalWindowSize; // 대전 모드 시작 시 원래 화면 크기 저장

  private Font createKoreanFont(int style, int size) {
    String[] koreanFonts = {"맑은 고딕", "Malgun Gothic", "굴림", "Gulim", "Arial Unicode MS", "Dialog"};
    for (String fontName : koreanFonts) {
      Font font = new Font(fontName, style, size);
      if (font.getFamily().equals(fontName) || font.canDisplay('한')) {
        return font;
      }
    }
    return new Font(Font.DIALOG, style, size);
  }

  public battle(ScreenController screenController) {
    this.screenController = screenController;
    // 시스템 속성에 저장된 원래 화면 크기 가져오기
    this.originalWindowSize = System.getProperty("tetris.battle.originalSize");
    // 만약 시스템 속성이 없으면 현재 설정값 사용
    if (this.originalWindowSize == null) {
      se.tetris.team5.utils.setting.GameSettings settings = 
        se.tetris.team5.utils.setting.GameSettings.getInstance();
      this.originalWindowSize = settings.getWindowSize();
    }
    setLayout(new BorderLayout());
    setBackground(Color.BLACK);

    initComponents();
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);
    addKeyListener(this);
    
    addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
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
    
    requestFocusInWindow();
  }

  public void display(JTextPane textPane) {
    // ScreenController 호환성
  }

  private void initComponents() {
    setLayout(new BorderLayout());

    // 전체 메인 패널
    JPanel mainContainer = new JPanel(new BorderLayout());
    mainContainer.setBackground(Color.BLACK);
    
    // 중앙 패널 - 2개의 게임 영역을 가로로 배치
    JPanel centerPanel = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
    centerPanel.setBackground(Color.BLACK);
    centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // ========== 플레이어1 (왼쪽) ==========
    JPanel player1Panel = new JPanel(new BorderLayout());
    player1Panel.setBackground(Color.BLACK);

    // 플레이어1 게임 보드 + 타이머 오버레이 (JLayeredPane 사용)
    javax.swing.JLayeredPane player1BoardContainer = new javax.swing.JLayeredPane();
    player1BoardContainer.setLayout(null);
    
    player1GameBoard = new GameBoard();
    player1BoardContainer.add(player1GameBoard, Integer.valueOf(0));
    
    // 플레이어1 타이머 라벨 (왼쪽 상단)
    player1TimerLabel = new JLabel("00:00");
    player1TimerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
    player1TimerLabel.setForeground(new Color(255, 50, 50));
    player1TimerLabel.setOpaque(true);
    player1TimerLabel.setBackground(new Color(0, 0, 0, 180));
    player1TimerLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    player1BoardContainer.add(player1TimerLabel, Integer.valueOf(100));
    
    // 보드와 타이머 위치 설정
    player1BoardContainer.addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
        java.awt.Dimension size = player1BoardContainer.getSize();
        player1GameBoard.setBounds(0, 0, size.width, size.height);
        player1TimerLabel.setBounds(10, 10, 80, 30);
      }
    });

    // 플레이어1 오른쪽 정보 패널
    JPanel player1RightPanel = new JPanel();
    player1RightPanel.setLayout(new BoxLayout(player1RightPanel, BoxLayout.Y_AXIS));
    player1RightPanel.setBackground(new Color(18, 18, 24));
    player1RightPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
    player1RightPanel.setPreferredSize(new java.awt.Dimension(220, 0));

    // 플레이어1 이름 라벨
    JLabel player1NameLabel = new JLabel("플레이어 1", javax.swing.SwingConstants.CENTER);
    player1NameLabel.setFont(createKoreanFont(Font.BOLD, 18));
    player1NameLabel.setForeground(new Color(100, 200, 255));
    player1NameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player1RightPanel.add(player1NameLabel);
    
    JLabel player1ControlLabel = new JLabel("WASD + Z", javax.swing.SwingConstants.CENTER);
    player1ControlLabel.setFont(createKoreanFont(Font.PLAIN, 12));
    player1ControlLabel.setForeground(new Color(150, 150, 150));
    player1ControlLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player1RightPanel.add(player1ControlLabel);
    player1RightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 플레이어1 다음 블록
    player1NextBlockBoard = new NextBlockBoard();
    player1NextVisualPanel = new JPanel() {
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
        Block next = null;
        if (player1GameEngine != null) next = player1GameEngine.getNextBlock();
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
              if (col == null) col = Color.CYAN;
              g2.setColor(col);
              g2.fillRoundRect(x + 4, y + 4, cellSize - 8, cellSize - 8, 6, 6);
              g2.setColor(new Color(255,255,255,40));
              g2.fillRoundRect(x + 4, y + 4, (cellSize - 8)/2, (cellSize - 8)/2, 4, 4);
            }
          }
        }
        g2.dispose();
      }
    };
    player1NextVisualPanel.setPreferredSize(new java.awt.Dimension(180, 90));
    JPanel player1NextWrapper = createTitledPanel("다음 블록", player1NextVisualPanel, new Color(255, 204, 0), new Color(255, 204, 0));
    player1NextWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player1RightPanel.add(player1NextWrapper);
    player1RightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 플레이어1 점수
    player1ScoreBoard = new ScoreBoard();
    JPanel player1ScoreInfo = new JPanel();
    player1ScoreInfo.setOpaque(false);
    player1ScoreInfo.setLayout(new BoxLayout(player1ScoreInfo, BoxLayout.Y_AXIS));
    player1ScoreValueLabel = new JLabel("0", javax.swing.SwingConstants.CENTER);
    player1ScoreValueLabel.setFont(createKoreanFont(Font.BOLD, 24));
    player1ScoreValueLabel.setForeground(new Color(255, 220, 100));
    player1ScoreValueLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player1ScoreInfo.add(player1ScoreValueLabel);
    player1ScoreInfo.add(javax.swing.Box.createVerticalStrut(8));
    
    JPanel player1SmallRow = new JPanel(); 
    player1SmallRow.setOpaque(false);
    player1SmallRow.setLayout(new BoxLayout(player1SmallRow, BoxLayout.Y_AXIS));
    player1LevelLabel = new JLabel("레벨: 1", javax.swing.SwingConstants.CENTER);
    player1LevelLabel.setFont(createKoreanFont(Font.BOLD, 13));
    player1LevelLabel.setForeground(new Color(200, 200, 200));
    player1LevelLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player1LinesLabel = new JLabel("줄: 0", javax.swing.SwingConstants.CENTER);
    player1LinesLabel.setFont(createKoreanFont(Font.BOLD, 13));
    player1LinesLabel.setForeground(new Color(200, 200, 200));
    player1LinesLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player1SmallRow.add(player1LevelLabel);
    player1SmallRow.add(javax.swing.Box.createVerticalStrut(4));
    player1SmallRow.add(player1LinesLabel);
    player1ScoreInfo.add(player1SmallRow);
    
    JPanel player1ScoreWrapper = createTitledPanel("점수", player1ScoreInfo, new Color(100, 255, 200), new Color(100, 255, 200));
    player1ScoreWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player1RightPanel.add(player1ScoreWrapper);
    player1RightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 플레이어1 공격 블록
    JPanel player1AttackPanel = new JPanel() {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int cellSize = Math.min(w / 5, h / 10);
        int gridWidth = cellSize * 5;
        int gridHeight = cellSize * 10;
        int startX = (w - gridWidth) / 2;
        int startY = (h - gridHeight) / 2;
        
        g2.setColor(new Color(18, 18, 24));
        g2.fillRoundRect(0, 0, w, h, 10, 10);
        
        // 5x10 그리드 그리기 (가로 5, 세로 10)
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
    player1AttackPanel.setPreferredSize(new java.awt.Dimension(100, 180));
    JPanel player1AttackWrapper = createTitledPanel("공격 블록", player1AttackPanel, new Color(255, 100, 100), new Color(255, 100, 100));
    player1AttackWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player1RightPanel.add(player1AttackWrapper);
    player1RightPanel.add(javax.swing.Box.createVerticalGlue());

    player1Panel.add(player1BoardContainer, BorderLayout.CENTER);
    player1Panel.add(player1RightPanel, BorderLayout.EAST);

    // ========== 플레이어2 (오른쪽) ==========
    JPanel player2Panel = new JPanel(new BorderLayout());
    player2Panel.setBackground(Color.BLACK);

    // 플레이어2 게임 보드 + 타이머 오버레이 (JLayeredPane 사용)
    javax.swing.JLayeredPane player2BoardContainer = new javax.swing.JLayeredPane();
    player2BoardContainer.setLayout(null);
    
    player2GameBoard = new GameBoard();
    player2BoardContainer.add(player2GameBoard, Integer.valueOf(0));
    
    // 플레이어2 타이머 라벨 (왼쪽 상단)
    player2TimerLabel = new JLabel("00:00");
    player2TimerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
    player2TimerLabel.setForeground(new Color(255, 50, 50));
    player2TimerLabel.setOpaque(true);
    player2TimerLabel.setBackground(new Color(0, 0, 0, 180));
    player2TimerLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    player2BoardContainer.add(player2TimerLabel, Integer.valueOf(100));
    
    // 보드와 타이머 위치 설정
    player2BoardContainer.addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
        java.awt.Dimension size = player2BoardContainer.getSize();
        player2GameBoard.setBounds(0, 0, size.width, size.height);
        player2TimerLabel.setBounds(10, 10, 80, 30);
      }
    });

    // 플레이어2 오른쪽 정보 패널
    JPanel player2RightPanel = new JPanel();
    player2RightPanel.setLayout(new BoxLayout(player2RightPanel, BoxLayout.Y_AXIS));
    player2RightPanel.setBackground(new Color(18, 18, 24));
    player2RightPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
    player2RightPanel.setPreferredSize(new java.awt.Dimension(220, 0));

    // 플레이어2 이름 라벨
    JLabel player2NameLabel = new JLabel("플레이어 2", javax.swing.SwingConstants.CENTER);
    player2NameLabel.setFont(createKoreanFont(Font.BOLD, 18));
    player2NameLabel.setForeground(new Color(255, 150, 100));
    player2NameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player2RightPanel.add(player2NameLabel);
    
    JLabel player2ControlLabel = new JLabel("방향키 + RShift", javax.swing.SwingConstants.CENTER);
    player2ControlLabel.setFont(createKoreanFont(Font.PLAIN, 12));
    player2ControlLabel.setForeground(new Color(150, 150, 150));
    player2ControlLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player2RightPanel.add(player2ControlLabel);
    player2RightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 플레이어2 다음 블록
    player2NextBlockBoard = new NextBlockBoard();
    player2NextVisualPanel = new JPanel() {
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
        Block next = null;
        if (player2GameEngine != null) next = player2GameEngine.getNextBlock();
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
              if (col == null) col = Color.CYAN;
              g2.setColor(col);
              g2.fillRoundRect(x + 4, y + 4, cellSize - 8, cellSize - 8, 6, 6);
              g2.setColor(new Color(255,255,255,40));
              g2.fillRoundRect(x + 4, y + 4, (cellSize - 8)/2, (cellSize - 8)/2, 4, 4);
            }
          }
        }
        g2.dispose();
      }
    };
    player2NextVisualPanel.setPreferredSize(new java.awt.Dimension(180, 90));
    JPanel player2NextWrapper = createTitledPanel("다음 블록", player2NextVisualPanel, new Color(255, 204, 0), new Color(255, 204, 0));
    player2NextWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player2RightPanel.add(player2NextWrapper);
    player2RightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 플레이어2 점수
    player2ScoreBoard = new ScoreBoard();
    JPanel player2ScoreInfo = new JPanel();
    player2ScoreInfo.setOpaque(false);
    player2ScoreInfo.setLayout(new BoxLayout(player2ScoreInfo, BoxLayout.Y_AXIS));
    player2ScoreValueLabel = new JLabel("0", javax.swing.SwingConstants.CENTER);
    player2ScoreValueLabel.setFont(createKoreanFont(Font.BOLD, 24));
    player2ScoreValueLabel.setForeground(new Color(255, 220, 100));
    player2ScoreValueLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player2ScoreInfo.add(player2ScoreValueLabel);
    player2ScoreInfo.add(javax.swing.Box.createVerticalStrut(8));
    
    JPanel player2SmallRow = new JPanel(); 
    player2SmallRow.setOpaque(false);
    player2SmallRow.setLayout(new BoxLayout(player2SmallRow, BoxLayout.Y_AXIS));
    player2LevelLabel = new JLabel("레벨: 1", javax.swing.SwingConstants.CENTER);
    player2LevelLabel.setFont(createKoreanFont(Font.BOLD, 13));
    player2LevelLabel.setForeground(new Color(200, 200, 200));
    player2LevelLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player2LinesLabel = new JLabel("줄: 0", javax.swing.SwingConstants.CENTER);
    player2LinesLabel.setFont(createKoreanFont(Font.BOLD, 13));
    player2LinesLabel.setForeground(new Color(200, 200, 200));
    player2LinesLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player2SmallRow.add(player2LevelLabel);
    player2SmallRow.add(javax.swing.Box.createVerticalStrut(4));
    player2SmallRow.add(player2LinesLabel);
    player2ScoreInfo.add(player2SmallRow);
    
    JPanel player2ScoreWrapper = createTitledPanel("점수", player2ScoreInfo, new Color(100, 255, 200), new Color(100, 255, 200));
    player2ScoreWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player2RightPanel.add(player2ScoreWrapper);
    player2RightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 플레이어2 공격 블록
    JPanel player2AttackPanel = new JPanel() {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int cellSize = Math.min(w / 5, h / 10);
        int gridWidth = cellSize * 5;
        int gridHeight = cellSize * 10;
        int startX = (w - gridWidth) / 2;
        int startY = (h - gridHeight) / 2;
        
        g2.setColor(new Color(18, 18, 24));
        g2.fillRoundRect(0, 0, w, h, 10, 10);
        
        // 5x10 그리드 그리기 (가로 5, 세로 10)
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
    player2AttackPanel.setPreferredSize(new java.awt.Dimension(100, 180));
    JPanel player2AttackWrapper = createTitledPanel("공격 블록", player2AttackPanel, new Color(255, 100, 100), new Color(255, 100, 100));
    player2AttackWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    player2RightPanel.add(player2AttackWrapper);
    player2RightPanel.add(javax.swing.Box.createVerticalGlue());

    player2Panel.add(player2BoardContainer, BorderLayout.CENTER);
    player2Panel.add(player2RightPanel, BorderLayout.EAST);

    centerPanel.add(player1Panel);
    centerPanel.add(player2Panel);

    mainContainer.add(centerPanel, BorderLayout.CENTER);
    add(mainContainer, BorderLayout.CENTER);

    // 텍스트 스타일 설정
    styleSet = new SimpleAttributeSet();
    StyleConstants.setFontFamily(styleSet, "Courier New");
    StyleConstants.setFontSize(styleSet, 18);
    StyleConstants.setBold(styleSet, true);
    StyleConstants.setForeground(styleSet, Color.WHITE);
    StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
  }

  private JPanel createTitledPanel(String title, JComponent content, Color titleColor, Color borderColor) {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);

    JLabel titleLabel = new JLabel(title, javax.swing.SwingConstants.LEFT);
    titleLabel.setFont(createKoreanFont(Font.BOLD, 14));
    titleLabel.setForeground(titleColor);
    titleLabel.setBorder(new EmptyBorder(0, 6, 8, 6));

    JPanel inner = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(24, 24, 32));
        int arc = 12;
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.setColor(borderColor);
        g2.setStroke(new java.awt.BasicStroke(2f));
        g2.drawRoundRect(1, 1, Math.max(0, getWidth()-2), Math.max(0, getHeight()-2), arc, arc);
        g2.dispose();
        super.paintComponent(g);
      }
    };
    inner.setOpaque(false);
    inner.setBorder(new EmptyBorder(10, 10, 10, 10));
    if (content != null) {
      content.setOpaque(false);
      inner.add(content, BorderLayout.CENTER);
    }

    wrapper.add(titleLabel, BorderLayout.NORTH);
    wrapper.add(inner, BorderLayout.CENTER);

    wrapper.setPreferredSize(new java.awt.Dimension(200, Math.max(100, content != null ? content.getPreferredSize().height + 48 : 120)));
    return wrapper;
  }

  public void startNewGame() {
    isGameOver = false;
    isPaused = false;

    // 플레이어1 초기화
    player1GameEngine = new GameEngine(HEIGHT, WIDTH);
    player1GameEngine.startNewGame();
    updatePlayer1AllBoards();

    // 플레이어2 초기화
    player2GameEngine = new GameEngine(HEIGHT, WIDTH);
    player2GameEngine.startNewGame();
    updatePlayer2AllBoards();

    // 타이머 설정
    int interval = getInitialInterval();
    
    player1Timer = new Timer(interval, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!isPaused && !isGameOver) {
          player1GameEngine.moveBlockDown();
          updatePlayer1AllBoards();
          if (player1GameEngine.isGameOver()) {
            gameOver(2); // 플레이어2 승리
          }
        }
      }
    });
    player1Timer.start();

    player2Timer = new Timer(interval, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!isPaused && !isGameOver) {
          player2GameEngine.moveBlockDown();
          updatePlayer2AllBoards();
          if (player2GameEngine.isGameOver()) {
            gameOver(1); // 플레이어1 승리
          }
        }
      }
    });
    player2Timer.start();

    requestFocusInWindow();
  }

  private int getInitialInterval() {
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    int gameSpeed = settings.getGameSpeed();
    switch (gameSpeed) {
      case 1: return 2000;
      case 2: return 1200;
      case 3: return 800;
      case 4: return 400;
      case 5: return 150;
      default: return 800;
    }
  }

  private void updatePlayer1AllBoards() {
    updatePlayer1GameBoard();
    updatePlayer1ScoreBoard();
    updatePlayer1NextBlockBoard();
  }

  private void updatePlayer2AllBoards() {
    updatePlayer2GameBoard();
    updatePlayer2ScoreBoard();
    updatePlayer2NextBlockBoard();
  }

  private void updatePlayer1GameBoard() {
    player1GameBoard.setShowTextOverlay(false);
    int[][] board = player1GameEngine.getBoardManager().getBoard();
    Color[][] boardColors = player1GameEngine.getBoardManager().getBoardColors();
    Block currBlock = player1GameEngine.getCurrentBlock();
    int currX = player1GameEngine.getX();
    int currY = player1GameEngine.getY();

    se.tetris.team5.items.Item[][] items = new se.tetris.team5.items.Item[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        items[i][j] = player1GameEngine.getBoardManager().getBoardItem(j, i);
      }
    }
    player1GameBoard.renderBoard(board, boardColors, items, currBlock, currX, currY);
  }

  private void updatePlayer2GameBoard() {
    player2GameBoard.setShowTextOverlay(false);
    int[][] board = player2GameEngine.getBoardManager().getBoard();
    Color[][] boardColors = player2GameEngine.getBoardManager().getBoardColors();
    Block currBlock = player2GameEngine.getCurrentBlock();
    int currX = player2GameEngine.getX();
    int currY = player2GameEngine.getY();

    se.tetris.team5.items.Item[][] items = new se.tetris.team5.items.Item[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        items[i][j] = player2GameEngine.getBoardManager().getBoardItem(j, i);
      }
    }
    player2GameBoard.renderBoard(board, boardColors, items, currBlock, currX, currY);
  }

  private void updatePlayer1ScoreBoard() {
    int currentScore = player1GameEngine.getGameScoring().getCurrentScore();
    int level = player1GameEngine.getGameScoring().getLevel();
    int linesCleared = player1GameEngine.getGameScoring().getLinesCleared();

    player1ScoreValueLabel.setText(String.format("%,d", currentScore));
    player1LevelLabel.setText("레벨: " + level);
    player1LinesLabel.setText("줄: " + linesCleared);
  }

  private void updatePlayer2ScoreBoard() {
    int currentScore = player2GameEngine.getGameScoring().getCurrentScore();
    int level = player2GameEngine.getGameScoring().getLevel();
    int linesCleared = player2GameEngine.getGameScoring().getLinesCleared();

    player2ScoreValueLabel.setText(String.format("%,d", currentScore));
    player2LevelLabel.setText("레벨: " + level);
    player2LinesLabel.setText("줄: " + linesCleared);
  }

  private void updatePlayer1NextBlockBoard() {
    player1NextVisualPanel.repaint();
  }

  private void updatePlayer2NextBlockBoard() {
    player2NextVisualPanel.repaint();
  }

  private void gameOver(int winner) {
    isGameOver = true;
    if (player1Timer != null) player1Timer.stop();
    if (player2Timer != null) player2Timer.stop();

    String message = winner == 1 ? 
      "🎉 플레이어 1 승리! 🎉" :
      "🎉 플레이어 2 승리! 🎉";

    int option = JOptionPane.showOptionDialog(
      this,
      message,
      "게임 종료",
      JOptionPane.DEFAULT_OPTION,
      JOptionPane.INFORMATION_MESSAGE,
      null,
      new Object[]{"메인 메뉴", "다시 하기"},
      "메인 메뉴"
    );

    if (option == 0 || option == JOptionPane.CLOSED_OPTION) {
      restoreWindowSize();
      screenController.showScreen("home");
    } else {
      startNewGame();
    }
  }

  private void restoreWindowSize() {
    se.tetris.team5.utils.setting.GameSettings settings = 
      se.tetris.team5.utils.setting.GameSettings.getInstance();
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
    if (isPaused || isGameOver) return;

    int keyCode = e.getKeyCode();

    // 플레이어1: WASD + Z
    if (keyCode == KeyEvent.VK_A) {
      player1GameEngine.moveBlockLeft();
      updatePlayer1GameBoard();
    } else if (keyCode == KeyEvent.VK_D) {
      player1GameEngine.moveBlockRight();
      updatePlayer1GameBoard();
    } else if (keyCode == KeyEvent.VK_S) {
      player1GameEngine.moveBlockDown();
      updatePlayer1GameBoard();
    } else if (keyCode == KeyEvent.VK_W) {
      player1GameEngine.rotateBlock();
      updatePlayer1GameBoard();
    } else if (keyCode == KeyEvent.VK_Z) {
      player1GameEngine.hardDrop();
      updatePlayer1AllBoards();
      if (player1GameEngine.isGameOver()) {
        gameOver(2);
      }
    }
    // 플레이어2: 화살표 + 오른쪽Ctrl
    else if (keyCode == KeyEvent.VK_LEFT) {
      player2GameEngine.moveBlockLeft();
      updatePlayer2GameBoard();
    } else if (keyCode == KeyEvent.VK_RIGHT) {
      player2GameEngine.moveBlockRight();
      updatePlayer2GameBoard();
    } else if (keyCode == KeyEvent.VK_DOWN) {
      player2GameEngine.moveBlockDown();
      updatePlayer2GameBoard();
    } else if (keyCode == KeyEvent.VK_UP) {
      player2GameEngine.rotateBlock();
      updatePlayer2GameBoard();
    } else if (keyCode == KeyEvent.VK_SHIFT && e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
      player2GameEngine.hardDrop();
      updatePlayer2AllBoards();
      if (player2GameEngine.isGameOver()) {
        gameOver(1);
      }
    }
    // 공통
    else if (keyCode == KeyEvent.VK_P) {
      togglePause();
    } else if (keyCode == KeyEvent.VK_ESCAPE) {
      showPauseMenu();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}

  private void togglePause() {
    isPaused = !isPaused;
    if (isPaused) {
      JOptionPane.showMessageDialog(this, "일시정지됨\nP 키를 눌러 계속하기", "일시정지", JOptionPane.INFORMATION_MESSAGE);
    }
    requestFocusInWindow();
  }

  private void showPauseMenu() {
    isPaused = true;
    int option = JOptionPane.showOptionDialog(
      this,
      "게임 일시정지",
      "일시정지",
      JOptionPane.DEFAULT_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      new Object[]{"게임 계속", "메뉴로 나가기"},
      "게임 계속"
    );

    if (option == 0) {
      isPaused = false;
      requestFocusInWindow();
    } else {
      if (player1Timer != null) player1Timer.stop();
      if (player2Timer != null) player2Timer.stop();
      restoreWindowSize();
      screenController.showScreen("home");
    }
  }
}
