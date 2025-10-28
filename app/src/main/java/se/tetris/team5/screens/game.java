package se.tetris.team5.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.ScreenController;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.components.game.GameBoard;
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

  // 게임 엔진 (순수 게임 로직)
  private GameEngine gameEngine;

  private SimpleAttributeSet styleSet; // 텍스트 스타일 설정
  private Timer timer; // 블록 자동 낙하 타이머
  private long gameStartTime; // 게임 시작 시간

  // 일시정지 관련 변수
  private boolean isPaused = false;
  private int pauseMenuIndex = 0; // 0: 게임 계속, 1: 메뉴로 나가기
  private String[] pauseMenuOptions = { "게임 계속", "메뉴로 나가기" };

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
    add(gameBoard, BorderLayout.CENTER);

    // 오른쪽 패널 (다음 블록 + 점수)
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setBackground(Color.BLACK);

    // 다음 블록 보드 (오른쪽 위)
    nextBlockBoard = new NextBlockBoard();
    rightPanel.add(nextBlockBoard, BorderLayout.NORTH);

    // 점수 보드 (오른쪽 아래)
    scoreBoard = new ScoreBoard();
    rightPanel.add(scoreBoard, BorderLayout.CENTER);

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

  protected void moveDown() {
    // GameEngine의 moveBlockDown()만 호출하여 상태 변경
        gameEngine.moveBlockDown();
    syncWithGameEngine();
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

  protected void hardDrop() {
    gameEngine.hardDrop();
    syncWithGameEngine();
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
            sb.append("★");
          } else {
            sb.append("O");
          }
        } else if (board[i][j] == 1 || board[i][j] == 2) {
          se.tetris.team5.items.Item item = gameEngine.getBoardManager().getBoardItem(j, i);
          if (item != null) {
            sb.append("★");
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
  }

  /**
   * 점수 보드를 업데이트합니다
   */
  private void updateScoreBoard() {
    StringBuilder sb = new StringBuilder();
  sb.append("점수: ").append(String.format("%,d", gameEngine.getGameScoring().getCurrentScore())).append("\n");
  sb.append("레벨: ").append(gameEngine.getGameScoring().getLevel()).append("\n");
  sb.append("줄: ").append(gameEngine.getGameScoring().getLinesCleared()).append("\n");
    sb.append("\n");
    sb.append("조작법:\n");
    sb.append("↑: 회전\n");
    sb.append("↓: 소프트 드롭\n");
    sb.append("←→: 이동\n");
    sb.append("Space: 하드 드롭\n");
    sb.append("ESC: 나가기\n");

    scoreBoard.getTextPane().setText(sb.toString());
    scoreBoard.getTextPane().getStyledDocument().setCharacterAttributes(
        0, scoreBoard.getTextPane().getDocument().getLength(),
        scoreBoard.getStyleSet(), false);
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
            // 아이템이 있으면 ★, 없으면 O
            se.tetris.team5.items.Item item = nextBlock.getItem(col, row);
            if (item != null) {
              sb.append("★");
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
   * 게임을 재개합니다
   */
  private void resumeGame() {
    isPaused = false;
    pauseMenuIndex = 0;
    timer.start();
    updateAllBoards(); // 게임 화면 복원
  }

  /**
   * 일시정지 메뉴를 그립니다 (단순한 디자인)
   */
  private void drawPauseMenu() {
    StringBuilder sb = new StringBuilder();

    // 단순한 일시정지 화면
    sb.append("\n\n\n\n\n");
    sb.append("          === 게임 일시정지 ===\n\n");

    // 메뉴 옵션들 (단순하게)
    for (int i = 0; i < pauseMenuOptions.length; i++) {
      sb.append("          ");
      if (i == pauseMenuIndex) {
        sb.append("> ");
      } else {
        sb.append("  ");
      }
      sb.append(pauseMenuOptions[i]);
      sb.append("\n\n");
    }

    sb.append("\n");
    sb.append("     ↑↓: 선택    Enter: 확인    ESC: 계속\n");

    // 게임 보드에 일시정지 메뉴 표시
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

    // 점수를 ScoreManager에 저장
    ScoreManager scoreManager = ScoreManager.getInstance();
    String playerName = "Player"; // 기본 플레이어 이름 (추후 입력 받도록 개선 가능)
    int currentScore = gameEngine.getGameScoring().getCurrentScore();
    int level = gameEngine.getGameScoring().getLevel();
    int linesCleared = gameEngine.getGameScoring().getLinesCleared();
    scoreManager.addScore(playerName, currentScore, level, linesCleared, playTime);

    // ScreenController를 통해 홈 화면으로 돌아가기
    screenController.showScreen("home");
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
          } else { // 메뉴로 나가기
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
      pauseGame();
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
      // shift 키로 아이템 사용: 실제로 아이템을 보유하고 있을 때만 사용
      if (gameEngine.hasAcquiredItem()) {
        gameEngine.useAcquiredItem();
      }
    } else if (keyCode == pauseKey) {
      pauseGame();
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

    // 색맹 모드 색상들도 체크
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    if (color.equals(settings.getColorForBlock("I")))
      return "I";
    if (color.equals(settings.getColorForBlock("O")))
      return "O";
    if (color.equals(settings.getColorForBlock("T")))
      return "T";
    if (color.equals(settings.getColorForBlock("L")))
      return "L";
    if (color.equals(settings.getColorForBlock("J")))
      return "J";
    if (color.equals(settings.getColorForBlock("S")))
      return "S";
    if (color.equals(settings.getColorForBlock("Z")))
      return "Z";
    if (color.equals(settings.getColorForBlock("W")))
      return "W";

    return "O"; // 기본값
  }
}
