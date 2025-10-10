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
import se.tetris.team5.gamelogic.block.BlockRotationManager;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.components.game.GameBoard;
import se.tetris.team5.components.game.NextBlockBoard;
import se.tetris.team5.components.game.ScoreBoard;
import se.tetris.team5.components.game.BoardManager;
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

  // 게임 보드 관리
  private BoardManager boardManager;

  // 블록 회전 관리 (UI에서 회전 시 사용)
  private BlockRotationManager rotationManager;

  private SimpleAttributeSet styleSet; // 텍스트 스타일 설정
  private Timer timer; // 블록 자동 낙하 타이머
  private Block curr; // 현재 움직이는 블록
  private Block next; // 다음 블록
  int x = 3; // Default Position.
  int y = 0;

  // 점수 시스템 관련 변수
  private int currentScore = 0; // 현재 점수
  private int linesCleared = 0; // 제거된 줄 수
  private int level = 1; // 현재 레벨
  private long gameStartTime; // 게임 시작 시간

  // 일시정지 관련 변수
  private boolean isPaused = false;
  private int pauseMenuIndex = 0; // 0: 게임 계속, 1: 메뉴로 나가기
  private String[] pauseMenuOptions = { "게임 계속", "메뉴로 나가기" };

  private static final int initInterval = 1000;

  public game(ScreenController screenController) {
    this.screenController = screenController;
    setLayout(new BorderLayout());
    setBackground(Color.BLACK);

    initComponents();
    setFocusable(true);
    addKeyListener(this);
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
    
    // BoardManager는 GameEngine에서 가져오기
    boardManager = gameEngine.getBoardManager();

    // Initialize BlockRotationManager
    rotationManager = new BlockRotationManager();

    // Set timer for block drops.
    timer = new Timer(initInterval, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        moveDown();
        updateAllBoards();
      }
    });

    // 게임 시작 시간 설정
    gameStartTime = gameEngine.getGameStartTime();

    // GameEngine에서 블록 정보 가져오기
    curr = gameEngine.getCurrentBlock();
    next = gameEngine.getNextBlock();
    x = gameEngine.getX();
    y = gameEngine.getY();
    updateAllBoards();
    timer.start();
  }

  protected void moveDown() {
    if (gameEngine.moveBlockDown()) {
      // 블록이 정상적으로 이동했을 때
      currentScore += 1; // 소프트 드롭 점수
    } else {
      // 블록이 고정되고 새 블록이 생성된 경우
      // 줄 제거 점수는 GameEngine에서 처리됨
      currentScore = gameEngine.getGameScoring().getCurrentScore();
      linesCleared = gameEngine.getGameScoring().getLinesCleared();
      
      // 레벨 업 체크
      int newLevel = (linesCleared / 10) + 1;
      if (newLevel != level) {
        level = newLevel;
        // 속도 증가 (레벨이 올라갈 때마다 타이머 간격 단축)
        int newInterval = Math.max(100, initInterval - ((level - 1) * 100));
        timer.setDelay(newInterval);
      }
      
      // 다음 블록 정보 업데이트
      curr = gameEngine.getCurrentBlock();
      next = gameEngine.getNextBlock();
      x = gameEngine.getX();
      y = gameEngine.getY();
      
      // 게임 오버 체크
      if (gameEngine.isGameOver()) {
        gameOver();
        return;
      }
    }
  }

  protected void moveRight() {
    gameEngine.moveBlockRight();
    // 블록 위치 동기화
    curr = gameEngine.getCurrentBlock();
    x = gameEngine.getX();
    y = gameEngine.getY();
  }

  protected void moveLeft() {
    gameEngine.moveBlockLeft();
    // 블록 위치 동기화
    curr = gameEngine.getCurrentBlock();
    x = gameEngine.getX();
    y = gameEngine.getY();
  }

  protected void hardDrop() {
    if (gameEngine.hardDrop()) {
      // 점수 동기화
      currentScore = gameEngine.getGameScoring().getCurrentScore();
      linesCleared = gameEngine.getGameScoring().getLinesCleared();
      
      // 레벨 업 체크
      int newLevel = gameEngine.getGameScoring().getLevel();
      if (newLevel != level) {
        level = newLevel;
        // 속도 증가
        timer.setDelay(gameEngine.getGameScoring().getTimerInterval());
      }
      
      // 블록 위치 동기화
      curr = gameEngine.getCurrentBlock();
      next = gameEngine.getNextBlock();
      x = gameEngine.getX();
      y = gameEngine.getY();
      
      // 게임 오버 체크
      if (gameEngine.isGameOver()) {
        gameOver();
        return;
      }
    }
  }

  /**
   * 블록을 회전시키는 메서드 (Wall Kick 포함)
   */
  protected void rotateBlock() {
    // 원본 블록 상태 저장
    Block originalBlock = copyBlock(curr);
    int originalX = x;
    int originalY = y;

    // 블록 회전 시도
    curr.rotate();

    // Wall Kick 오프셋 배열 (시도할 위치 조정값들)
    // 오른쪽, 왼쪽, 위, 아래 순서로 시도
    int[][] wallKickOffsets = {
        { 0, 0 }, // 현재 위치에서 회전 가능한지 먼저 확인
        { -1, 0 }, // 왼쪽으로 1칸 이동
        { 1, 0 }, // 오른쪽으로 1칸 이동
        { 0, -1 }, // 위로 1칸 이동
        { -3, 0 }, // 왼쪽으로 3칸 이동 (I블록 등을 위해)
        { 2, 0 }, // 오른쪽으로 2칸 이동
        { 0, 1 }, // 아래로 1칸 이동
        { -1, -1 }, // 왼쪽 위 대각선
        { 1, -1 }, // 오른쪽 위 대각선
    };

    // Wall Kick 시도
    boolean rotationSuccessful = false;
    for (int[] offset : wallKickOffsets) {
      int testX = originalX + offset[0];
      int testY = originalY + offset[1];

      if (boardManager.canMove(testX, testY, curr)) {
        // 회전 성공
        x = testX;
        y = testY;
        rotationSuccessful = true;
        break;
      }
    }

    // 모든 Wall Kick 시도가 실패하면 원래 상태로 복원
    if (!rotationSuccessful) {
      curr = originalBlock;
      x = originalX;
      y = originalY;
    }
  }

  /**
   * 블록 복사 메서드 (회전 상태 복사)
   */
  private Block copyBlock(Block original) {
    return rotationManager.copyBlock(original);
  }

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
    int[][] board = boardManager.getBoard();
    Color[][] boardColors = boardManager.getBoardColors();

    for (int i = 0; i < board.length; i++) {
      sb.append(BORDER_CHAR);
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] == 1 || board[i][j] == 2) {
          sb.append("O"); // 단순한 영문 문자
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
        if ((board[i][j] == 1 || board[i][j] == 2) && boardColors[i][j] != null) {
          SimpleAttributeSet colorStyle = new SimpleAttributeSet(borderStyle); // 기본 스타일 복사
          StyleConstants.setForeground(colorStyle, boardColors[i][j]); // 색상만 변경

          int charPos = textOffset + j + 1; // +1은 왼쪽 테두리
          if (charPos < doc.getLength()) {
            doc.setCharacterAttributes(charPos, 1, colorStyle, false);
          }
        }
      }
      textOffset += WIDTH + 3; // 다음 줄로 이동 (테두리 2개 + 줄바꿈 1개)
    }
  }

  /**
   * 점수 보드를 업데이트합니다
   */
  private void updateScoreBoard() {
    StringBuilder sb = new StringBuilder();
    sb.append("점수: ").append(String.format("%,d", currentScore)).append("\n");
    sb.append("레벨: ").append(level).append("\n");
    sb.append("줄: ").append(linesCleared).append("\n");
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

    if (next != null) {
      // 4x4 크기의 블록 표시 영역
      for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
          if (row < next.height() && col < next.width() && next.getShape(col, row) == 1) {
            sb.append("O");
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
    if (next != null) {
      int textOffset = 0;
      for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
          if (row < next.height() && col < next.width() && next.getShape(col, row) == 1) {
            SimpleAttributeSet colorStyle = new SimpleAttributeSet(baseStyle);
            StyleConstants.setForeground(colorStyle, next.getColor());

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
    // GameEngine을 통해 게임 리셋
    gameEngine.resetGame();
    
    // UI 상태 동기화
    currentScore = gameEngine.getGameScoring().getCurrentScore();
    linesCleared = gameEngine.getGameScoring().getLinesCleared();
    level = gameEngine.getGameScoring().getLevel();
    gameStartTime = gameEngine.getGameStartTime();

    // 블록 위치 동기화
    curr = gameEngine.getCurrentBlock();
    next = gameEngine.getNextBlock();
    x = gameEngine.getX();
    y = gameEngine.getY();
    
    updateAllBoards();
  }

  private void gameOver() {
    timer.stop(); // 타이머 정지

    // 플레이 시간 계산
    long playTime = System.currentTimeMillis() - gameStartTime;

    // 점수를 ScoreManager에 저장
    ScoreManager scoreManager = ScoreManager.getInstance();
    String playerName = "Player"; // 기본 플레이어 이름 (추후 입력 받도록 개선 가능)
    scoreManager.addScore(playerName, currentScore, level, linesCleared, playTime);

    // ScreenController를 통해 홈 화면으로 돌아가기
    screenController.showScreen("home");
  }

  @Override
  public void keyPressed(KeyEvent e) {
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
            // 게임 완전 정지 및 상태 리셋
            timer.stop();
            isPaused = false;
            pauseMenuIndex = 0;
            // ScreenController를 통해 홈으로 돌아가기
            screenController.showScreen("home");
          }
          break;
        case KeyEvent.VK_ESCAPE:
          resumeGame(); // ESC로도 게임 계속할 수 있게
          break;
      }
      return; // 일시정지 상태에서는 다른 키 무시
    }

    // 게임 진행 중일 때의 키 처리
    switch (e.getKeyCode()) {
      case KeyEvent.VK_ESCAPE:
        pauseGame();
        break;
      case KeyEvent.VK_DOWN:
        moveDown();
        drawBoard();
        break;
      case KeyEvent.VK_RIGHT:
        moveRight();
        drawBoard();
        break;
      case KeyEvent.VK_LEFT:
        moveLeft();
        drawBoard();
        break;
      case KeyEvent.VK_UP:
        boardManager.eraseBlock(curr, x, y);
        rotateBlock();
        boardManager.placeBlock(curr, x, y);
        drawBoard();
        break;
      case KeyEvent.VK_SPACE:
        hardDrop();
        drawBoard();
        break;
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }
}
