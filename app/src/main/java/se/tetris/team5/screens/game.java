package se.tetris.team5.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import se.tetris.team5.ScreenController;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.block.BlockFactory;
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

  private ScreenController screenController;

  // UI 컴포넌트들
  private GameBoard gameBoard;
  private NextBlockBoard nextBlockBoard;
  private ScoreBoard scoreBoard;

  // 게임 엔진 (순수 게임 로직)
  private GameEngine gameEngine;

  // 하드드롭/스폰 연속 입력 방지용 debounce 플래그
  private boolean justSpawned = false;

  // 게임 보드 관리
  private BoardManager boardManager;
  
  // 블록 팩토리
  private BlockFactory blockFactory;

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

  // 테스트 및 외부 접근용 setter (package-private)
  void setCurrentScore(int score) { this.currentScore = score; }
  void setLevel(int level) { this.level = level; }
  void setLinesCleared(int lines) { this.linesCleared = lines; }
  void setGameStartTime(long time) { this.gameStartTime = time; }
  private long gameStartTime; // 게임 시작 시간

  // 일시정지 관련 변수
  private boolean isPaused = false;
  private int pauseMenuIndex = 0; // 0: 게임 계속, 1: 메뉴로 나가기
  private String[] pauseMenuOptions = { "게임 계속", "메뉴로 나가기" };

  // 게임 속도 설정에 따른 초기 간격 계산 메소드
  private int getInitialInterval() {
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    int gameSpeed = settings.getGameSpeed(); // 1-5 범위
    // 각 속도별 간격 (더 천천히, 초보자도 쉽게)
    switch (gameSpeed) {
      case 1: return 3500; // 매우느림: 3.5초
      case 2: return 2000; // 느림: 2초
      case 3: return 1200; // 보통: 1.2초
      case 4: return 700;  // 빠름: 0.7초
      case 5: return 300;  // 매우빠름: 0.3초
      default: return 1200; // 기본값 (보통)
    }
  }

  public game(ScreenController screenController) {
    this.screenController = screenController;
    setLayout(new BorderLayout());
    setBackground(Color.BLACK);

    initComponents();
    setFocusable(true);
    addKeyListener(this);
  }

  private void initComponents() {
    // 전체 레이아웃 설정
    setLayout(new BorderLayout());

    // 오른쪽 패널 (다음 블록 + 점수 + 조작키 안내)
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout(0, 8));
    rightPanel.setBackground(new Color(24, 26, 48));

    // 다음 블록 보드 (오른쪽 위)
    nextBlockBoard = new NextBlockBoard();
    rightPanel.add(nextBlockBoard, BorderLayout.NORTH);

    // 점수 보드 (중간)
    scoreBoard = new ScoreBoard();
    rightPanel.add(scoreBoard, BorderLayout.CENTER);

    // 조작키 안내 (아래)
    JPanel controlsPanel = ScoreBoard.createControlsPanel();
    rightPanel.add(controlsPanel, BorderLayout.SOUTH);

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
    // BlockFactory 초기화
    blockFactory = new BlockFactory();
    // 보드 완전 초기화
    boardManager.reset();

    // 게임 보드 (왼쪽) - 반드시 boardManager 초기화 후 생성
    System.out.println("[DEBUG] Before GameBoard creation. boardManager=" + (boardManager == null ? "null" : "ok"));
    gameBoard = new GameBoard(boardManager);
    add(gameBoard, BorderLayout.CENTER);
    gameBoard.setVisible(true);
    gameBoard.setOpaque(true);
    System.out.println("[DEBUG] GameBoard added to game panel. Parent=" + gameBoard.getParent());
    this.revalidate();
    this.repaint();
    System.out.println("[DEBUG] game panel revalidated and repainted");
    // 게임 패널에 포커스 요청 (키 입력 활성화)
    boolean focusResult = this.requestFocusInWindow();
    System.out.println("[DEBUG] requestFocusInWindow result: " + focusResult);

    // Set timer for block drops.
    if (timer != null) {
      timer.stop();
      timer = null;
    }
    timer = new Timer(getInitialInterval(), new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!isPaused) {
          moveDown();
          updateAllBoards();
        }
      }
    });

    // 게임 시작 시간 설정
    gameStartTime = gameEngine.getGameStartTime();

    // 초기 블록 설정
    curr = blockFactory.createRandomBlock();
    next = blockFactory.createRandomBlock();
    nextBlockBoard.setNextBlock(next);
    x = 3;
    y = 0;

    // 초기 블록을 보드에 배치
    if (curr != null) {
      boardManager.placeBlock(curr, x, y);
    }

    syncWithGameEngine();
    updateAllBoards();

    // 게임 시작 시 타이머 완전 초기화 (0초부터 시작)
    int userInterval = getInitialInterval();
    timer.setDelay(userInterval);
    timer.setInitialDelay(userInterval); // 초기 지연을 설정하여 바로 실행 방지
    timer.start();
  }

  protected void moveDown() {
  if (isPaused) return;
  // 현재 블록을 보드에서 지우기
  boardManager.eraseBlock(curr, x, y);
    
    // 블록 이동 시도
    if (boardManager.canMove(x, y + 1, curr)) {
      y++;
      currentScore += 1; // 소프트 드롭 점수
      boardManager.placeBlock(curr, x, y);
    } else {
      // 블록을 고정하고 새 블록 생성
      boardManager.fixBlock(curr, x, y);
      handleLineClearAndLevelUp();
      spawnNextBlock();
    }
  }

  protected void moveRight() {
    // 1프레임(타이머 tick) 후 justSpawned 해제
    javax.swing.Timer debounceTimer = new javax.swing.Timer(50, evt -> {
      justSpawned = false;
      ((javax.swing.Timer)evt.getSource()).stop();
    });
    debounceTimer.setRepeats(false);
    debounceTimer.start();
    System.out.println("[DEBUG] moveRight called, x=" + x + ", y=" + y);
    boardManager.eraseBlock(curr, x, y);
    // x+1만 적용
    if (boardManager.canMove(x + 1, y, curr)) {
      x = x + 1;
    }
    System.out.println("[DEBUG] moveRight after, x=" + x + ", y=" + y);
    boardManager.placeBlock(curr, x, y);
  }

  protected void moveLeft() {
    System.out.println("[DEBUG] moveLeft called, x=" + x + ", y=" + y);
    boardManager.eraseBlock(curr, x, y);
    // x-1만 적용
    if (boardManager.canMove(x - 1, y, curr)) {
      x = x - 1;
    }
    System.out.println("[DEBUG] moveLeft after, x=" + x + ", y=" + y);
    boardManager.placeBlock(curr, x, y);
  }
  
  // GameEngine과 UI 상태를 동기화하는 메서드 (next 블록만)
  private void syncWithGameEngine() {
    next = gameEngine.getNextBlock();
    
    // 블록 색상 업데이트
    if (curr != null) {
      curr.updateColor();
    }
    if (next != null) {
      next.updateColor();
    }
  }

  protected void hardDrop() {
    if (justSpawned) {
      System.out.println("[DEBUG] hardDrop blocked by justSpawned");
      return;
    }
    justSpawned = true;
    System.out.println("[DEBUG] hardDrop called, x=" + x + ", y=" + y);
    boardManager.eraseBlock(curr, x, y);
    int dropDistance = 0;
    while (boardManager.canMove(x, y + 1, curr)) {
      y++;
      dropDistance++;
    }
    System.out.println("[DEBUG] hardDrop after, x=" + x + ", y=" + y + ", dropDistance=" + dropDistance);
    currentScore += dropDistance * 2;
    boardManager.fixBlock(curr, x, y);
    handleLineClearAndLevelUp();
    spawnNextBlock();
    // 1프레임(타이머 tick) 후 justSpawned 해제
    javax.swing.Timer debounceTimer = new javax.swing.Timer(50, evt -> {
      justSpawned = false;
      ((javax.swing.Timer)evt.getSource()).stop();
    });
    debounceTimer.setRepeats(false);
    debounceTimer.start();
  }
  // 줄 제거 및 레벨업 처리 (moveDown, hardDrop 공통)
  private void handleLineClearAndLevelUp() {
    int clearedLines = boardManager.clearLines();
    if (clearedLines > 0) {
      linesCleared += clearedLines;
      int points = 0;
      switch (clearedLines) {
        case 1: points = 100 * level; break;
        case 2: points = 300 * level; break;
        case 3: points = 500 * level; break;
        case 4: points = 800 * level; break;
      }
      currentScore += points;
    }
    int newLevel = (linesCleared / 10) + 1;
    if (newLevel != level) {
      level = newLevel;
    }
  }

  // curr/next/preview 동기화 및 미리보기 보드 갱신을 일원화
  private void spawnNextBlock() {
    curr = next;
    curr.setRotationState(0); // 항상 0으로 초기화
    next = blockFactory.createRandomBlock();
    next.setRotationState(0);
    nextBlockBoard.setNextBlock(next);
    x = 3;
    y = 0;
    // 게임 오버 체크
    if (!boardManager.canMove(x, y, curr)) {
      gameOver();
      return;
    }
    // 새 블록 생성 시 타이머 완전 초기화 (0초부터 다시 시작)
    int userInterval = getInitialInterval();
    timer.setDelay(userInterval);
    timer.setInitialDelay(userInterval);
    timer.start();
    boardManager.placeBlock(curr, x, y);
    updateAllBoards();
  }

  /**
   * 블록을 회전시키는 메서드 (Wall Kick 포함)
   */
  protected void rotateBlock() {
    boardManager.eraseBlock(curr, x, y);
    se.tetris.team5.gamelogic.block.BlockRotationManager rotationManager = new se.tetris.team5.gamelogic.block.BlockRotationManager();
    int[][] board = boardManager.getBoard();
    boolean rotated = false;
    int originalX = x;
    int originalY = y;
    Block originalBlock = createBlockCopy(curr);
    int originalState = curr.getRotationState();
    // SRS 월킥 오프셋 한 번만 적용
    if (rotationManager.rotateBlockWithWallKick(curr, originalX, originalY, board)) {
      rotated = true;
    }
    if (!rotated) {
      curr = originalBlock;
      x = originalX;
      y = originalY;
      curr.setRotationState(originalState);
    }
    boardManager.placeBlock(curr, x, y);
  }
  
  // 블록 복사 메서드
  private Block createBlockCopy(Block original) {
    // BlockFactory를 통해 같은 타입의 새 블록 생성 후 회전 상태 맞추기
    Block copy = null;
    if (original instanceof se.tetris.team5.blocks.IBlock) copy = new se.tetris.team5.blocks.IBlock();
    else if (original instanceof se.tetris.team5.blocks.JBlock) copy = new se.tetris.team5.blocks.JBlock();
    else if (original instanceof se.tetris.team5.blocks.LBlock) copy = new se.tetris.team5.blocks.LBlock();
    else if (original instanceof se.tetris.team5.blocks.OBlock) copy = new se.tetris.team5.blocks.OBlock();
    else if (original instanceof se.tetris.team5.blocks.SBlock) copy = new se.tetris.team5.blocks.SBlock();
    else if (original instanceof se.tetris.team5.blocks.TBlock) copy = new se.tetris.team5.blocks.TBlock();
    else if (original instanceof se.tetris.team5.blocks.ZBlock) copy = new se.tetris.team5.blocks.ZBlock();
    if (copy != null) {
      // 원본과 같은 회전 상태로 맞추기
      copy.setRotationState(original.getRotationState());
      for (int i = 0; i < 4; i++) {
        if (isSameShape(copy, original)) break;
        copy.rotate();
      }
    }
    return copy;
  }
  
  // 두 블록의 모양이 같은지 확인
  private boolean isSameShape(Block block1, Block block2) {
    if (block1.width() != block2.width() || block1.height() != block2.height()) {
      return false;
    }
    
    for (int i = 0; i < block1.width(); i++) {
      for (int j = 0; j < block1.height(); j++) {
        if (block1.getShape(i, j) != block2.getShape(i, j)) {
          return false;
        }
      }
    }
    
    return true;
  }



  /**
   * 모든 보드를 업데이트합니다
   */
  private void updateAllBoards() {
  updateGameBoard();
  updateScoreBoard();
  nextBlockBoard.setNextBlock(next);
  }

  /**
   * 게임 보드를 업데이트합니다
   */
  private void updateGameBoard() {
    // 그래픽 기반 보드이므로 repaint만 호출
    gameBoard.repaint();
  }

  /**
   * 점수 보드를 업데이트합니다
   */
  private void updateScoreBoard() {
  StringBuilder sb = new StringBuilder();
  sb.append("점수: ").append(String.format("%,d", currentScore)).append("\n");
  sb.append("레벨: ").append(level).append("\n");
  sb.append("줄: ").append(linesCleared).append("\n");
  scoreBoard.getTextPane().setText(sb.toString());
  scoreBoard.getTextPane().getStyledDocument().setCharacterAttributes(
    0, scoreBoard.getTextPane().getDocument().getLength(),
    scoreBoard.getStyleSet(), false);
  }

  /**
   * 다음 블록 보드를 업데이트합니다
   */
  // nextBlockBoard는 setNextBlock으로 직접 갱신합니다.

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
  if (isPaused) return; // 이미 일시정지 상태면 중복 실행 방지
  isPaused = true;
  if (timer != null && timer.isRunning()) timer.stop();
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
    // Swing 기반 일시정지 모달
    javax.swing.SwingUtilities.invokeLater(() -> {
      String[] options = {"계속", "메뉴로 나가기"};
      int result = javax.swing.JOptionPane.showOptionDialog(
        this,
        "게임이 일시정지되었습니다.",
        "일시정지",
        javax.swing.JOptionPane.DEFAULT_OPTION,
        javax.swing.JOptionPane.INFORMATION_MESSAGE,
        null,
        options,
        options[0]
      );
      if (result == 0) { // 계속
        resumeGame();
      } else if (result == 1) { // 메뉴로 나가기
        isPaused = false;
        pauseMenuIndex = 0;
        if (timer != null && timer.isRunning()) timer.stop();
        if (curr != null) {
          boardManager.eraseBlock(curr, x, y);
        }
        screenController.showScreen("home");
      }
    });
  }

  public void reset() {
    // 타이머 정지
    if (timer != null) {
      timer.stop();
    }
    // 보드 완전 초기화
    boardManager.reset();
    // GameEngine을 통해 게임 리셋
    gameEngine.resetGame();
    // UI 상태 초기화
    curr = blockFactory.createRandomBlock();
    curr.setRotationState(0);
    next = blockFactory.createRandomBlock();
    next.setRotationState(0);
    nextBlockBoard.setNextBlock(next);
    x = 3;
    y = 0;
    currentScore = 0;
    linesCleared = 0;
    level = 1;
    gameStartTime = gameEngine.getGameStartTime();
    isPaused = false;
    pauseMenuIndex = 0;
    // 초기 블록을 보드에 배치
    if (curr != null) {
      boardManager.placeBlock(curr, x, y);
    }
    updateAllBoards();
    // 새 게임 시작 시 사용자 설정 속도로 타이머 완전 초기화
    int userInterval = getInitialInterval(); // 최신 사용자 설정 속도 가져오기
    timer.setDelay(userInterval);
    timer.setInitialDelay(userInterval); // 바로 실행 방지
    timer.start(); // 0초부터 새로 시작
  }

  void gameOver() {
    timer.stop(); // 타이머 정지

    // 현재 블록을 보드에서 제거 (다음 게임에 영향 안주도록)
    if (curr != null) {
      boardManager.eraseBlock(curr, x, y);
    }

    // 플레이 시간 계산
    long playTime = System.currentTimeMillis() - gameStartTime;

    // 사용자 이름 입력 모달
    String playerName = javax.swing.JOptionPane.showInputDialog(this, "이름을 입력하세요:", "게임 종료", javax.swing.JOptionPane.PLAIN_MESSAGE);
    if (playerName == null) {
      // 취소 또는 닫기: 홈화면으로 이동
      screenController.showScreen("home");
      return;
    }
    if (playerName.trim().isEmpty()) {
      playerName = "Player";
    }

    // 점수를 ScoreManager에 저장
    ScoreManager scoreManager = ScoreManager.getInstance();
    scoreManager.addScore(playerName, currentScore, level, linesCleared, playTime);

    // 스코어보드 화면으로 이동
    screenController.showScreen("score");
  }

  // 키 입력 중복 방지 플래그
  private boolean leftPressed = false;
  private boolean rightPressed = false;
  private boolean rotatePressed = false;

  @Override
  public void keyPressed(KeyEvent e) {
    System.out.println("[DEBUG] keyPressed: keyCode=" + e.getKeyCode() + ", x=" + x + ", y=" + y);
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    int downKey = settings.getKeyCode("down");
    int leftKey = settings.getKeyCode("left");
    int rightKey = settings.getKeyCode("right");
    int rotateKey = settings.getKeyCode("rotate");
    int dropKey = settings.getKeyCode("drop");
    int pauseKey = settings.getKeyCode("pause");

    if (isPaused) {
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE || (pauseKey != -1 && e.getKeyCode() == pauseKey)) {
        resumeGame();
      }
      return;
    }
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_ESCAPE) {
      pauseGame();
    } else if (keyCode == downKey) {
      moveDown();
      drawBoard();
    } else if (keyCode == rightKey) {
      if (!rightPressed) {
        rightPressed = true;
        moveRight();
        drawBoard();
      }
    } else if (keyCode == leftKey) {
      if (!leftPressed) {
        leftPressed = true;
        moveLeft();
        drawBoard();
      }
    } else if (keyCode == rotateKey) {
      if (!rotatePressed) {
        rotatePressed = true;
        rotateBlock();
        drawBoard();
      }
    } else if (keyCode == dropKey) {
      hardDrop();
      drawBoard();
    } else if (keyCode == pauseKey) {
      pauseGame();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    int leftKey = settings.getKeyCode("left");
    int rightKey = settings.getKeyCode("right");
    int rotateKey = settings.getKeyCode("rotate");
    int keyCode = e.getKeyCode();
    if (keyCode == leftKey) {
      leftPressed = false;
    } else if (keyCode == rightKey) {
      rightPressed = false;
    } else if (keyCode == rotateKey) {
      rotatePressed = false;
    }
  }
  
  // 색맹 모드 변경 시 모든 색상 업데이트
  public void updateColorsForColorblindMode() {
    // 현재 블록과 다음 블록의 색상 업데이트
    if (curr != null) {
      curr.updateColor();
    }
    if (next != null) {
      next.updateColor();
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
    int[][] board = boardManager.getBoard();
    Color[][] boardColors = boardManager.getBoardColors();
    
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
    if (color.equals(Color.CYAN)) return "I";
    if (color.equals(Color.YELLOW)) return "O";
    if (color.equals(Color.MAGENTA)) return "T";
    if (color.equals(Color.ORANGE)) return "L";
    if (color.equals(Color.BLUE)) return "J";
    if (color.equals(Color.GREEN)) return "S";
    if (color.equals(Color.RED)) return "Z";
    
    // 색맹 모드 색상들도 체크
    se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
    if (color.equals(settings.getColorForBlock("I"))) return "I";
    if (color.equals(settings.getColorForBlock("O"))) return "O";
    if (color.equals(settings.getColorForBlock("T"))) return "T";
    if (color.equals(settings.getColorForBlock("L"))) return "L";
    if (color.equals(settings.getColorForBlock("J"))) return "J";
    if (color.equals(settings.getColorForBlock("S"))) return "S";
    if (color.equals(settings.getColorForBlock("Z"))) return "Z";
    
    return "O"; // 기본값
  }
}
