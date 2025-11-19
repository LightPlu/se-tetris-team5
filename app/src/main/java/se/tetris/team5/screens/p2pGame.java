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

import se.tetris.team5.ScreenController;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.components.game.GameBoard;
import se.tetris.team5.components.game.NextBlockBoard;
import se.tetris.team5.components.game.ScoreBoard;
import se.tetris.team5.network.NetworkManager;
import se.tetris.team5.network.GameStateMessage;

/**
 * P2P 네트워크 대전 모드
 * 왼쪽: 내 게임
 * 오른쪽: 상대방 게임 (실시간 동기화)
 */
public class p2pGame extends JPanel implements KeyListener {

  private static final long serialVersionUID = 1L;

  public static final int HEIGHT = GameBoard.HEIGHT;
  public static final int WIDTH = GameBoard.WIDTH;
  public static final char BORDER_CHAR = GameBoard.BORDER_CHAR;

  private ScreenController screenController;
  private NetworkManager networkManager;
  private boolean isServer; // 서버 여부

  // 내 게임 컴포넌트
  private GameBoard myGameBoard;
  private NextBlockBoard myNextBlockBoard;
  private ScoreBoard myScoreBoard;
  private JPanel myNextVisualPanel;
  private JLabel myScoreValueLabel;
  private JLabel myLevelLabel;
  private JLabel myLinesLabel;
  private JLabel myTimerLabel;
  private JLabel myLatencyLabel; // 지연 표시
  private GameEngine myGameEngine;
  private Timer myTimer;
  private long myGameStartTime;

  // 상대방 게임 컴포넌트 (읽기 전용, 네트워크로 수신)
  private GameBoard opponentGameBoard;
  private JPanel opponentNextVisualPanel;
  private JLabel opponentScoreValueLabel;
  private JLabel opponentLevelLabel;
  private JLabel opponentLinesLabel;
  private JLabel opponentTimerLabel;
  private JLabel opponentStatusLabel; // 연결 상태
  
  // 상대방 게임 상태 (네트워크로 수신한 데이터)
  private int[][] opponentBoard;
  private Color[][] opponentBoardColors;
  private Block opponentCurrentBlock;
  private int opponentX;
  private int opponentY;
  private Block opponentNextBlock;
  private int opponentScore;
  private int opponentLevel;
  private int opponentLines;

  private SimpleAttributeSet styleSet;
  private boolean isPaused = false;
  private boolean isGameOver = false;
  private String originalWindowSize;
  
  // 네트워크 동기화 타이머
  private Timer syncTimer; // 주기적으로 내 상태 전송
  private static final int SYNC_INTERVAL_MS = 50; // 50ms마다 동기화 (20 FPS)

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

  public p2pGame(ScreenController screenController, NetworkManager networkManager, boolean isServer) {
    this.screenController = screenController;
    this.networkManager = networkManager;
    this.isServer = isServer;
    
    this.originalWindowSize = System.getProperty("tetris.battle.originalSize");
    if (this.originalWindowSize == null) {
      se.tetris.team5.utils.setting.GameSettings settings = 
        se.tetris.team5.utils.setting.GameSettings.getInstance();
      this.originalWindowSize = settings.getWindowSize();
    }
    
    // 상대방 보드 초기화
    opponentBoard = new int[HEIGHT][WIDTH];
    opponentBoardColors = new Color[HEIGHT][WIDTH];
    
    setLayout(new BorderLayout());
    setBackground(Color.BLACK);

    initComponents();
    setupNetworkHandlers();
    
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

  /**
   * 네트워크 핸들러 설정
   */
  private void setupNetworkHandlers() {
    // 메시지 수신 핸들러
    networkManager.setMessageHandler(message -> {
      javax.swing.SwingUtilities.invokeLater(() -> handleNetworkMessage(message));
    });
    
    // 연결 종료 핸들러
    networkManager.setOnDisconnected(() -> {
      javax.swing.SwingUtilities.invokeLater(() -> handleDisconnect());
    });
  }

  /**
   * 네트워크 메시지 처리
   */
  private void handleNetworkMessage(GameStateMessage message) {
    switch (message.getType()) {
      case GAME_STATE:
        // 상대방 게임 상태 업데이트
        updateOpponentState(message);
        break;
        
      case GAME_OVER:
        // 상대방이 게임오버
        handleOpponentGameOver();
        break;
        
      case DISCONNECT:
        // 상대방 연결 종료
        handleDisconnect();
        break;
        
      default:
        // 기타 메시지 무시
        break;
    }
  }

  /**
   * 상대방 게임 상태 업데이트
   */
  private void updateOpponentState(GameStateMessage message) {
    // 보드 상태
    if (message.getBoard() != null) {
      opponentBoard = message.getBoard();
    }
    if (message.getBoardColors() != null) {
      // int[][][] 를 Color[][]로 변환
      opponentBoardColors = GameStateMessage.intArrayToColorArray2D(message.getBoardColors());
    }
    
    // 현재 블록
    opponentCurrentBlock = message.getCurrentBlock();
    opponentX = message.getX();
    opponentY = message.getY();
    
    // 다음 블록
    opponentNextBlock = message.getNextBlock();
    
    // 점수 정보
    opponentScore = message.getScore();
    opponentLevel = message.getLevel();
    opponentLines = message.getLines();
    
    // UI 업데이트
    updateOpponentAllBoards();
  }

  /**
   * 상대방 게임오버 처리
   */
  private void handleOpponentGameOver() {
    isGameOver = true;
    if (myTimer != null) myTimer.stop();
    if (syncTimer != null) syncTimer.stop();
    
    JOptionPane.showMessageDialog(
      this,
      "🎉 승리! 🎉\n상대방이 게임오버되었습니다!",
      "게임 종료",
      JOptionPane.INFORMATION_MESSAGE
    );
    
    returnToLobby();
  }

  /**
   * 연결 종료 처리
   */
  private void handleDisconnect() {
    isGameOver = true;
    if (myTimer != null) myTimer.stop();
    if (syncTimer != null) syncTimer.stop();
    
    JOptionPane.showMessageDialog(
      this,
      "⚠️ 연결이 끊어졌습니다 ⚠️\n상대방과의 연결이 종료되었습니다.",
      "연결 종료",
      JOptionPane.WARNING_MESSAGE
    );
    
    returnToLobby();
  }

  /**
   * 로비로 돌아가기
   */
  private void returnToLobby() {
    cleanup();
    restoreWindowSize();
    screenController.showScreen("p2p");
  }

  public void display(JTextPane textPane) {
    // ScreenController 호환성
  }

  private void initComponents() {
    setLayout(new BorderLayout());

    JPanel mainContainer = new JPanel(new BorderLayout());
    mainContainer.setBackground(Color.BLACK);
    
    // 중앙 패널 - 2개의 게임 영역
    JPanel centerPanel = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
    centerPanel.setBackground(Color.BLACK);
    centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // ========== 내 게임 (왼쪽) ==========
    JPanel myPanel = new JPanel(new BorderLayout());
    myPanel.setBackground(Color.BLACK);

    javax.swing.JLayeredPane myBoardContainer = new javax.swing.JLayeredPane();
    myBoardContainer.setLayout(null);
    
    myGameBoard = new GameBoard();
    myBoardContainer.add(myGameBoard, Integer.valueOf(0));
    
    // 타이머 라벨
    myTimerLabel = new JLabel("00:00");
    myTimerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
    myTimerLabel.setForeground(new Color(100, 255, 100));
    myTimerLabel.setOpaque(true);
    myTimerLabel.setBackground(new Color(0, 0, 0, 180));
    myTimerLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    myBoardContainer.add(myTimerLabel, Integer.valueOf(100));
    
    // 지연 라벨 (우측 상단)
    myLatencyLabel = new JLabel("0ms");
    myLatencyLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
    myLatencyLabel.setForeground(new Color(100, 255, 100));
    myLatencyLabel.setOpaque(true);
    myLatencyLabel.setBackground(new Color(0, 0, 0, 180));
    myLatencyLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    myBoardContainer.add(myLatencyLabel, Integer.valueOf(100));
    
    myBoardContainer.addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
        java.awt.Dimension size = myBoardContainer.getSize();
        myGameBoard.setBounds(0, 0, size.width, size.height);
        myTimerLabel.setBounds(10, 10, 80, 30);
        myLatencyLabel.setBounds(size.width - 90, 10, 80, 30);
      }
    });

    // 내 정보 패널
    JPanel myRightPanel = new JPanel();
    myRightPanel.setLayout(new BoxLayout(myRightPanel, BoxLayout.Y_AXIS));
    myRightPanel.setBackground(new Color(18, 18, 24));
    myRightPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
    myRightPanel.setPreferredSize(new java.awt.Dimension(220, 0));

    JLabel myNameLabel = new JLabel("나", javax.swing.SwingConstants.CENTER);
    myNameLabel.setFont(createKoreanFont(Font.BOLD, 18));
    myNameLabel.setForeground(new Color(100, 200, 255));
    myNameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    myRightPanel.add(myNameLabel);
    
    JLabel myRoleLabel = new JLabel(isServer ? "(호스트)" : "(참가자)", javax.swing.SwingConstants.CENTER);
    myRoleLabel.setFont(createKoreanFont(Font.PLAIN, 12));
    myRoleLabel.setForeground(new Color(150, 150, 150));
    myRoleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    myRightPanel.add(myRoleLabel);
    myRightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 다음 블록
    myNextBlockBoard = new NextBlockBoard();
    myNextVisualPanel = new JPanel() {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        paintNextBlock(g, myGameEngine != null ? myGameEngine.getNextBlock() : null);
      }
    };
    myNextVisualPanel.setPreferredSize(new java.awt.Dimension(180, 90));
    JPanel myNextWrapper = createTitledPanel("다음 블록", myNextVisualPanel, new Color(255, 204, 0), new Color(255, 204, 0));
    myNextWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    myRightPanel.add(myNextWrapper);
    myRightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 점수
    myScoreBoard = new ScoreBoard();
    JPanel myScoreInfo = new JPanel();
    myScoreInfo.setOpaque(false);
    myScoreInfo.setLayout(new BoxLayout(myScoreInfo, BoxLayout.Y_AXIS));
    myScoreValueLabel = new JLabel("0", javax.swing.SwingConstants.CENTER);
    myScoreValueLabel.setFont(createKoreanFont(Font.BOLD, 24));
    myScoreValueLabel.setForeground(new Color(255, 220, 100));
    myScoreValueLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    myScoreInfo.add(myScoreValueLabel);
    myScoreInfo.add(javax.swing.Box.createVerticalStrut(8));
    
    JPanel mySmallRow = new JPanel(); 
    mySmallRow.setOpaque(false);
    mySmallRow.setLayout(new BoxLayout(mySmallRow, BoxLayout.Y_AXIS));
    myLevelLabel = new JLabel("레벨: 1", javax.swing.SwingConstants.CENTER);
    myLevelLabel.setFont(createKoreanFont(Font.BOLD, 13));
    myLevelLabel.setForeground(new Color(200, 200, 200));
    myLevelLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    myLinesLabel = new JLabel("줄: 0", javax.swing.SwingConstants.CENTER);
    myLinesLabel.setFont(createKoreanFont(Font.BOLD, 13));
    myLinesLabel.setForeground(new Color(200, 200, 200));
    myLinesLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    mySmallRow.add(myLevelLabel);
    mySmallRow.add(javax.swing.Box.createVerticalStrut(4));
    mySmallRow.add(myLinesLabel);
    myScoreInfo.add(mySmallRow);
    
    JPanel myScoreWrapper = createTitledPanel("점수", myScoreInfo, new Color(100, 255, 200), new Color(100, 255, 200));
    myScoreWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    myRightPanel.add(myScoreWrapper);
    myRightPanel.add(javax.swing.Box.createVerticalGlue());

    myPanel.add(myBoardContainer, BorderLayout.CENTER);
    myPanel.add(myRightPanel, BorderLayout.EAST);

    // ========== 상대방 게임 (오른쪽) ==========
    JPanel opponentPanel = new JPanel(new BorderLayout());
    opponentPanel.setBackground(Color.BLACK);

    javax.swing.JLayeredPane opponentBoardContainer = new javax.swing.JLayeredPane();
    opponentBoardContainer.setLayout(null);
    
    opponentGameBoard = new GameBoard();
    opponentBoardContainer.add(opponentGameBoard, Integer.valueOf(0));
    
    // 상대방 타이머 라벨
    opponentTimerLabel = new JLabel("00:00");
    opponentTimerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
    opponentTimerLabel.setForeground(new Color(255, 100, 100));
    opponentTimerLabel.setOpaque(true);
    opponentTimerLabel.setBackground(new Color(0, 0, 0, 180));
    opponentTimerLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    opponentBoardContainer.add(opponentTimerLabel, Integer.valueOf(100));
    
    // 상대방 연결 상태
    opponentStatusLabel = new JLabel("🟢 연결됨");
    opponentStatusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
    opponentStatusLabel.setForeground(new Color(100, 255, 100));
    opponentStatusLabel.setOpaque(true);
    opponentStatusLabel.setBackground(new Color(0, 0, 0, 180));
    opponentStatusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    opponentBoardContainer.add(opponentStatusLabel, Integer.valueOf(100));
    
    opponentBoardContainer.addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
        java.awt.Dimension size = opponentBoardContainer.getSize();
        opponentGameBoard.setBounds(0, 0, size.width, size.height);
        opponentTimerLabel.setBounds(10, 10, 80, 30);
        opponentStatusLabel.setBounds(size.width - 100, 10, 90, 30);
      }
    });

    // 상대방 정보 패널
    JPanel opponentRightPanel = new JPanel();
    opponentRightPanel.setLayout(new BoxLayout(opponentRightPanel, BoxLayout.Y_AXIS));
    opponentRightPanel.setBackground(new Color(18, 18, 24));
    opponentRightPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
    opponentRightPanel.setPreferredSize(new java.awt.Dimension(220, 0));

    JLabel opponentNameLabel = new JLabel("상대방", javax.swing.SwingConstants.CENTER);
    opponentNameLabel.setFont(createKoreanFont(Font.BOLD, 18));
    opponentNameLabel.setForeground(new Color(255, 150, 100));
    opponentNameLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    opponentRightPanel.add(opponentNameLabel);
    
    JLabel opponentRoleLabel = new JLabel(isServer ? "(참가자)" : "(호스트)", javax.swing.SwingConstants.CENTER);
    opponentRoleLabel.setFont(createKoreanFont(Font.PLAIN, 12));
    opponentRoleLabel.setForeground(new Color(150, 150, 150));
    opponentRoleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    opponentRightPanel.add(opponentRoleLabel);
    opponentRightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 상대방 다음 블록
    opponentNextVisualPanel = new JPanel() {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        paintNextBlock(g, opponentNextBlock);
      }
    };
    opponentNextVisualPanel.setPreferredSize(new java.awt.Dimension(180, 90));
    JPanel opponentNextWrapper = createTitledPanel("다음 블록", opponentNextVisualPanel, new Color(255, 204, 0), new Color(255, 204, 0));
    opponentNextWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    opponentRightPanel.add(opponentNextWrapper);
    opponentRightPanel.add(javax.swing.Box.createVerticalStrut(12));

    // 상대방 점수
    JPanel opponentScoreInfo = new JPanel();
    opponentScoreInfo.setOpaque(false);
    opponentScoreInfo.setLayout(new BoxLayout(opponentScoreInfo, BoxLayout.Y_AXIS));
    opponentScoreValueLabel = new JLabel("0", javax.swing.SwingConstants.CENTER);
    opponentScoreValueLabel.setFont(createKoreanFont(Font.BOLD, 24));
    opponentScoreValueLabel.setForeground(new Color(255, 220, 100));
    opponentScoreValueLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    opponentScoreInfo.add(opponentScoreValueLabel);
    opponentScoreInfo.add(javax.swing.Box.createVerticalStrut(8));
    
    JPanel opponentSmallRow = new JPanel(); 
    opponentSmallRow.setOpaque(false);
    opponentSmallRow.setLayout(new BoxLayout(opponentSmallRow, BoxLayout.Y_AXIS));
    opponentLevelLabel = new JLabel("레벨: 1", javax.swing.SwingConstants.CENTER);
    opponentLevelLabel.setFont(createKoreanFont(Font.BOLD, 13));
    opponentLevelLabel.setForeground(new Color(200, 200, 200));
    opponentLevelLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    opponentLinesLabel = new JLabel("줄: 0", javax.swing.SwingConstants.CENTER);
    opponentLinesLabel.setFont(createKoreanFont(Font.BOLD, 13));
    opponentLinesLabel.setForeground(new Color(200, 200, 200));
    opponentLinesLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    opponentSmallRow.add(opponentLevelLabel);
    opponentSmallRow.add(javax.swing.Box.createVerticalStrut(4));
    opponentSmallRow.add(opponentLinesLabel);
    opponentScoreInfo.add(opponentSmallRow);
    
    JPanel opponentScoreWrapper = createTitledPanel("점수", opponentScoreInfo, new Color(100, 255, 200), new Color(100, 255, 200));
    opponentScoreWrapper.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    opponentRightPanel.add(opponentScoreWrapper);
    opponentRightPanel.add(javax.swing.Box.createVerticalGlue());

    opponentPanel.add(opponentBoardContainer, BorderLayout.CENTER);
    opponentPanel.add(opponentRightPanel, BorderLayout.EAST);

    centerPanel.add(myPanel);
    centerPanel.add(opponentPanel);

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

  /**
   * 다음 블록 렌더링 헬퍼
   */
  private void paintNextBlock(java.awt.Graphics g, Block nextBlock) {
    java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
    
    java.awt.Rectangle bounds = g2.getClipBounds();
    int w = (bounds != null && bounds.width > 0) ? bounds.width : 180;
    int h = (bounds != null && bounds.height > 0) ? bounds.height : 90;
    
    int cellSize = Math.min(w / 6, h / 6);
    int gridSize = cellSize * 4;
    int startX = (w - gridSize) / 2;
    int startY = (h - gridSize) / 2;
    
    g2.setColor(new Color(18, 18, 24));
    g2.fillRoundRect(0, 0, w, h, 10, 10);
    for (int r = 0; r < 4; r++) {
      for (int c = 0; c < 4; c++) {
        int x = startX + c * cellSize;
        int y = startY + r * cellSize;
        g2.setColor(new Color(40, 40, 48));
        g2.fillRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 6, 6);
        if (nextBlock != null && r < nextBlock.height() && c < nextBlock.width() && nextBlock.getShape(c, r) == 1) {
          Color col = nextBlock.getColor();
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

  /**
   * 게임 시작
   */
  public void startNewGame() {
    System.out.println("[p2pGame] Starting new P2P game (isServer: " + isServer + ")");
    
    isGameOver = false;
    isPaused = false;
    myGameStartTime = System.currentTimeMillis();

    // 내 게임 엔진 초기화
    myGameEngine = new GameEngine(HEIGHT, WIDTH);
    myGameEngine.startNewGame();
    updateMyAllBoards();

    // 타이머 설정
    int interval = getInitialInterval();
    
    myTimer = new Timer(interval, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!isPaused && !isGameOver) {
          myGameEngine.moveBlockDown();
          updateMyAllBoards();
          if (myGameEngine.isGameOver()) {
            handleMyGameOver();
          }
        }
      }
    });
    myTimer.start();
    
    // 네트워크 동기화 타이머 시작
    syncTimer = new Timer(SYNC_INTERVAL_MS, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!isPaused && !isGameOver) {
          sendMyState();
          updateLatency();
        }
      }
    });
    syncTimer.start();

    System.out.println("[p2pGame] Game started successfully");
    requestFocusInWindow();
  }

  /**
   * 내 게임 상태를 네트워크로 전송
   */
  private void sendMyState() {
    if (networkManager == null || !networkManager.isConnected()) return;
    
    GameStateMessage message = new GameStateMessage(GameStateMessage.MessageType.GAME_STATE);
    
    // 보드 상태
    message.setBoard(myGameEngine.getBoardManager().getBoard());
    
    // Color[][] 를 int[][][]로 변환
    Color[][] colors = myGameEngine.getBoardManager().getBoardColors();
    message.setBoardColors(GameStateMessage.colorArray2DToIntArray(colors));
    
    // 현재 블록
    message.setCurrentBlock(myGameEngine.getCurrentBlock());
    message.setX(myGameEngine.getX());
    message.setY(myGameEngine.getY());
    
    // 다음 블록
    message.setNextBlock(myGameEngine.getNextBlock());
    
    // 점수 정보
    message.setScore(myGameEngine.getGameScoring().getCurrentScore());
    message.setLevel(myGameEngine.getGameScoring().getLevel());
    message.setLines(myGameEngine.getGameScoring().getLinesCleared());
    
    networkManager.sendMessage(message);
  }

  /**
   * 지연 정보 업데이트
   */
  private void updateLatency() {
    if (networkManager != null) {
      long latency = networkManager.getLatency();
      myLatencyLabel.setText(latency + "ms");
      
      // 지연에 따라 색상 변경
      if (latency > 200) {
        myLatencyLabel.setForeground(new Color(255, 50, 50)); // 빨강 (랙)
        opponentStatusLabel.setText("🔴 랙");
        opponentStatusLabel.setForeground(new Color(255, 50, 50));
      } else if (latency > 100) {
        myLatencyLabel.setForeground(new Color(255, 200, 50)); // 노랑 (보통)
        opponentStatusLabel.setText("🟡 보통");
        opponentStatusLabel.setForeground(new Color(255, 200, 50));
      } else {
        myLatencyLabel.setForeground(new Color(100, 255, 100)); // 초록 (좋음)
        opponentStatusLabel.setText("🟢 연결됨");
        opponentStatusLabel.setForeground(new Color(100, 255, 100));
      }
    }
  }

  /**
   * 내 게임오버 처리
   */
  private void handleMyGameOver() {
    isGameOver = true;
    if (myTimer != null) myTimer.stop();
    if (syncTimer != null) syncTimer.stop();
    
    // 상대방에게 게임오버 알림
    GameStateMessage gameOverMsg = new GameStateMessage(GameStateMessage.MessageType.GAME_OVER);
    networkManager.sendMessage(gameOverMsg);
    
    JOptionPane.showMessageDialog(
      this,
      "💀 패배 💀\n게임오버되었습니다.",
      "게임 종료",
      JOptionPane.INFORMATION_MESSAGE
    );
    
    returnToLobby();
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

  private void updateMyAllBoards() {
    updateMyGameBoard();
    updateMyScoreBoard();
    updateMyNextBlockBoard();
    updateMyTimer();
  }

  private void updateOpponentAllBoards() {
    updateOpponentGameBoard();
    updateOpponentScoreBoard();
    updateOpponentNextBlockBoard();
    updateOpponentTimer();
  }

  private void updateMyGameBoard() {
    myGameBoard.setShowTextOverlay(false);
    int[][] board = myGameEngine.getBoardManager().getBoard();
    Color[][] boardColors = myGameEngine.getBoardManager().getBoardColors();
    Block currBlock = myGameEngine.getCurrentBlock();
    int currX = myGameEngine.getX();
    int currY = myGameEngine.getY();

    se.tetris.team5.items.Item[][] items = new se.tetris.team5.items.Item[board.length][board[0].length];
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        items[i][j] = myGameEngine.getBoardManager().getBoardItem(j, i);
      }
    }
    myGameBoard.renderBoard(board, boardColors, items, currBlock, currX, currY);
  }

  private void updateOpponentGameBoard() {
    opponentGameBoard.setShowTextOverlay(false);
    
    // 아이템 배열은 null로 (상대방 아이템 정보는 필요시 추가)
    se.tetris.team5.items.Item[][] items = new se.tetris.team5.items.Item[opponentBoard.length][opponentBoard[0].length];
    
    opponentGameBoard.renderBoard(opponentBoard, opponentBoardColors, items, 
                                  opponentCurrentBlock, opponentX, opponentY);
  }

  private void updateMyScoreBoard() {
    int currentScore = myGameEngine.getGameScoring().getCurrentScore();
    int level = myGameEngine.getGameScoring().getLevel();
    int linesCleared = myGameEngine.getGameScoring().getLinesCleared();

    myScoreValueLabel.setText(String.format("%,d", currentScore));
    myLevelLabel.setText("레벨: " + level);
    myLinesLabel.setText("줄: " + linesCleared);
  }

  private void updateOpponentScoreBoard() {
    opponentScoreValueLabel.setText(String.format("%,d", opponentScore));
    opponentLevelLabel.setText("레벨: " + opponentLevel);
    opponentLinesLabel.setText("줄: " + opponentLines);
  }

  private void updateMyNextBlockBoard() {
    myNextVisualPanel.repaint();
  }

  private void updateOpponentNextBlockBoard() {
    opponentNextVisualPanel.repaint();
  }

  private void updateMyTimer() {
    long elapsed = System.currentTimeMillis() - myGameStartTime;
    int seconds = (int)(elapsed / 1000);
    int minutes = seconds / 60;
    seconds = seconds % 60;
    myTimerLabel.setText(String.format("%02d:%02d", minutes, seconds));
  }

  private void updateOpponentTimer() {
    // 상대방 타이머는 동기화된 정보로 업데이트 (향후 구현)
    // 현재는 내 타이머와 동일하게 표시
    updateMyTimer();
    opponentTimerLabel.setText(myTimerLabel.getText());
  }

  private void restoreWindowSize() {
    se.tetris.team5.utils.setting.GameSettings settings = 
      se.tetris.team5.utils.setting.GameSettings.getInstance();
    if (originalWindowSize != null) {
      settings.setWindowSize(originalWindowSize);
      settings.loadSettings();
    }
    screenController.updateWindowSize();
  }

  private void cleanup() {
    if (myTimer != null) {
      myTimer.stop();
      myTimer = null;
    }
    if (syncTimer != null) {
      syncTimer.stop();
      syncTimer = null;
    }
    // NetworkManager는 p2p.java에서 관리하므로 여기서 disconnect하지 않음
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (isPaused || isGameOver) return;

    int keyCode = e.getKeyCode();
    
    // 게임 설정에서 키 가져오기
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    int downKey = settings.getKeyCode("down");
    int leftKey = settings.getKeyCode("left");
    int rightKey = settings.getKeyCode("right");
    int rotateKey = settings.getKeyCode("rotate");
    int dropKey = settings.getKeyCode("drop");

    if (keyCode == leftKey) {
      myGameEngine.moveBlockLeft();
      updateMyGameBoard();
    } else if (keyCode == rightKey) {
      myGameEngine.moveBlockRight();
      updateMyGameBoard();
    } else if (keyCode == downKey) {
      myGameEngine.moveBlockDown();
      updateMyGameBoard();
    } else if (keyCode == rotateKey) {
      myGameEngine.rotateBlock();
      updateMyGameBoard();
    } else if (keyCode == dropKey) {
      myGameEngine.hardDrop();
      updateMyAllBoards();
      if (myGameEngine.isGameOver()) {
        handleMyGameOver();
      }
    } else if (keyCode == KeyEvent.VK_ESCAPE) {
      showPauseMenu();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}

  private void showPauseMenu() {
    isPaused = true;
    int option = JOptionPane.showOptionDialog(
      this,
      "게임 일시정지",
      "일시정지",
      JOptionPane.DEFAULT_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      new Object[]{"게임 계속", "나가기"},
      "게임 계속"
    );

    if (option == 0) {
      isPaused = false;
      requestFocusInWindow();
    } else {
      cleanup();
      
      // DISCONNECT 메시지 전송
      GameStateMessage disconnectMsg = new GameStateMessage(GameStateMessage.MessageType.DISCONNECT);
      networkManager.sendMessage(disconnectMsg);
      
      returnToLobby();
    }
  }
}
