package se.tetris.team5.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.ScreenController;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.blocks.IBlock;
import se.tetris.team5.blocks.JBlock;
import se.tetris.team5.blocks.LBlock;
import se.tetris.team5.blocks.OBlock;
import se.tetris.team5.blocks.SBlock;
import se.tetris.team5.blocks.TBlock;
import se.tetris.team5.blocks.ZBlock;
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
  
  // 게임 상태 관리
  private int[][] board;
  private Color[][] boardColors;
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
  private String[] pauseMenuOptions = {"게임 계속", "메뉴로 나가기"};

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

    // Set timer for block drops.
    timer = new Timer(initInterval, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        moveDown();
        updateAllBoards();
      }
    });

    // Initialize game board data
    board = new int[HEIGHT][WIDTH];
    boardColors = new Color[HEIGHT][WIDTH];

    // 게임 시작 시간 설정
    gameStartTime = System.currentTimeMillis();

    // Create the first block and next block
    curr = getRandomBlock();
    next = getRandomBlock();
    placeBlock();
    updateAllBoards();
    timer.start();
  }

  private Block getRandomBlock() {
    Random rnd = new Random(System.currentTimeMillis());
    int block = rnd.nextInt(7); // 0~6까지 7개 블록
    switch (block) {
      case 0:
        return new IBlock();
      case 1:
        return new JBlock();
      case 2:
        return new LBlock();
      case 3:
        return new ZBlock();
      case 4:
        return new SBlock();
      case 5:
        return new TBlock();
      case 6:
        return new OBlock();
    }
    return new LBlock();
  }

  private void placeBlock() {
    // 보드 배열 업데이트만 수행 (색상은 drawBoard에서 처리)
    for (int j = 0; j < curr.height(); j++) {
      for (int i = 0; i < curr.width(); i++) {
        if (y + j >= 0 && y + j < HEIGHT && x + i >= 0 && x + i < WIDTH) {
          if (curr.getShape(i, j) == 1) {
            board[y + j][x + i] = 2; // 움직이는 블록은 값 2로 설정
            boardColors[y + j][x + i] = curr.getColor(); // 색상 정보도 저장
          }
        }
      }
    }
  }

  private void eraseCurr() {
    for (int i = x; i < x + curr.width(); i++) {
      for (int j = y; j < y + curr.height(); j++) {
        if (curr.getShape(i - x, j - y) == 1) {
          // 배열 경계 검사를 추가
          if (j >= 0 && j < HEIGHT && i >= 0 && i < WIDTH) {
            // 움직이는 블록(값 2)만 지우고, 고정된 블록(값 1)은 지우지 않음
            if (board[j][i] == 2) {
              board[j][i] = 0;
              boardColors[j][i] = null; // 색상 정보도 제거
            }
          }
        }
      }
    }
  }

  // 블록이 주어진 위치로 이동할 수 있는지 확인하는 메서드
  private boolean canMove(int newX, int newY, Block block) {
    // 경계 검사
    if (newX < 0 || newX + block.width() > WIDTH ||
        newY + block.height() > HEIGHT) {
      return false;
    }

    // 상단 경계는 허용 (블록이 위에서 시작할 수 있도록)
    if (newY < 0) {
      // 블록의 일부가 보드 위에 있어도 되지만, 보드 안쪽 부분만 검사
      for (int i = 0; i < block.width(); i++) {
        for (int j = 0; j < block.height(); j++) {
          if (block.getShape(i, j) == 1 && newY + j >= 0) {
            if (board[newY + j][newX + i] == 1) {
              return false;
            }
          }
        }
      }
      return true;
    }

    // 고정된 블록과의 충돌 검사
    for (int i = 0; i < block.width(); i++) {
      for (int j = 0; j < block.height(); j++) {
        if (block.getShape(i, j) == 1) { // 블록의 실제 부분만 검사
          if (board[newY + j][newX + i] == 1) {
            return false; // 이미 고정된 블록이 있음
          }
        }
      }
    }
    return true;
  }

  // 현재 블록을 보드에 영구적으로 고정하는 메서드
  private void fixBlock() {
    for (int i = 0; i < curr.width(); i++) {
      for (int j = 0; j < curr.height(); j++) {
        if (curr.getShape(i, j) == 1 && y + j >= 0 && y + j < HEIGHT && x + i >= 0 && x + i < WIDTH) {
          board[y + j][x + i] = 1; // 움직이는 블록을 고정된 블록으로 변환
          boardColors[y + j][x + i] = curr.getColor(); // 색상도 고정
        }
      }
    }
  }

  protected void moveDown() {
    eraseCurr();

    // 한 칸 아래로 이동할 수 있는지 확인
    if (canMove(x, y + 1, curr)) {
      y++;
      currentScore += 1; // 블록이 한 칸 내려갈 때마다 1점 증가
      placeBlock();
    } else {
      // 더 이상 내려갈 수 없으면 현재 위치에 블록을 고정
      // 먼저 현재 위치에 블록을 다시 그리기
      placeBlock();
      fixBlock();

      // 가득 찬 줄이 있는지 확인하고 제거
      clearLines();

      // 새로운 블록 생성 (다음 블록을 현재 블록으로)
      curr = next;
      next = getRandomBlock();
      x = 3;
      y = 0;

      // 게임 오버 체크 (새 블록이 시작 위치에 놓일 수 없는 경우)
      if (!canMove(x, y, curr)) {
        // 게임 오버 처리
        gameOver();
        return;
      }

      placeBlock();
    }
  }

  protected void moveRight() {
    eraseCurr();
    if (canMove(x + 1, y, curr)) {
      x++;
    }
    placeBlock();
  }

  protected void moveLeft() {
    eraseCurr();
    if (canMove(x - 1, y, curr)) {
      x--;
    }
    placeBlock();
  }

  protected void hardDrop() {
    eraseCurr();
    
    // 블록이 바닥에 닿을 때까지 y 좌표를 증가시킴
    int dropDistance = 0;
    while (canMove(x, y + 1, curr)) {
      y++;
      dropDistance++;
    }
    
    // 하드드롭 시 떨어진 거리만큼 보너스 점수 (거리 * 2점)
    currentScore += dropDistance * 2;
    
    placeBlock();
    fixBlock();
    
    // 가득 찬 줄이 있는지 확인하고 제거
    clearLines();
    
    // 새로운 블록 생성 (다음 블록을 현재 블록으로)
    curr = next;
    next = getRandomBlock();
    x = 3;
    y = 0;
    
    // 게임 오버 체크 (새 블록이 시작 위치에 놓일 수 없는 경우)
    if (!canMove(x, y, curr)) {
      // 게임 오버 처리
      gameOver();
      return;
    }
    
    placeBlock();
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

  // 가득 찬 줄을 제거하는 메서드
  private void clearLines() {
    int clearedLinesCount = 0; // 한 번에 제거된 줄 수 카운트

    for (int row = HEIGHT - 1; row >= 0; row--) {
      // 현재 줄이 가득 찼는지 확인 (고정된 블록만 고려)
      boolean fullLine = true;
      for (int col = 0; col < WIDTH; col++) {
        if (board[row][col] != 1) { // 고정된 블록(값 1)만 고려
          fullLine = false;
          break;
        }
      }

      // 가득 찬 줄이 있으면 제거하고 위의 줄들을 아래로 내림
      if (fullLine) {
        clearedLinesCount++; // 제거된 줄 수 증가

        // 현재 줄부터 위의 모든 줄을 한 줄씩 아래로 이동
        for (int moveRow = row; moveRow > 0; moveRow--) {
          for (int col = 0; col < WIDTH; col++) {
            board[moveRow][col] = board[moveRow - 1][col];
            boardColors[moveRow][col] = boardColors[moveRow - 1][col];
          }
        }
        // 맨 위 줄은 빈 줄로 만듦
        for (int col = 0; col < WIDTH; col++) {
          board[0][col] = 0;
          boardColors[0][col] = null;
        }
        // 같은 줄을 다시 검사해야 하므로 row를 증가시킴
        row++;
      }
    }

    // 제거된 줄 수에 따른 점수 증가
    if (clearedLinesCount > 0) {
      linesCleared += clearedLinesCount;

      switch (clearedLinesCount) {
        case 1:
          currentScore += 100; // 1줄 제거: 100점
          break;
        case 2:
          currentScore += 300; // 2줄 제거: 300점
          break;
        case 3:
          currentScore += 500; // 3줄 제거: 500점
          break;
        case 4:
          currentScore += 800; // 4줄 제거: 800점 (테트리스)
          break;
        default:
          currentScore += clearedLinesCount * 100; // 그 외의 경우
          break;
      }

      // 레벨 업 체크 (10줄마다 레벨 증가)
      int newLevel = (linesCleared / 10) + 1;
      if (newLevel > level) {
        level = newLevel;
        // 레벨이 올라갈 때마다 게임 속도 증가
        int newInterval = Math.max(100, initInterval - ((level - 1) * 100));
        timer.setDelay(newInterval);
      }
    }
  }

  public void reset() {
    // 보드 리셋
    board = new int[HEIGHT][WIDTH];
    boardColors = new Color[HEIGHT][WIDTH];
    
    this.currentScore = 0;
    this.linesCleared = 0;
    this.level = 1;
    this.gameStartTime = System.currentTimeMillis();

    // 새 블록 생성 및 게임 재시작
    curr = getRandomBlock();
    next = getRandomBlock();
    x = 3;
    y = 0;
    placeBlock();
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
        eraseCurr();
        curr.rotate();
        // 회전 후 위치가 유효한지 확인
        if (!canMove(x, y, curr)) {
          // 회전이 불가능하면 다시 되돌림
          curr.rotate();
          curr.rotate();
          curr.rotate();
        }
        placeBlock();
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
