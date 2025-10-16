package se.tetris.team5.gamelogic;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.block.BlockFactory;
import se.tetris.team5.gamelogic.block.BlockRotationManager;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.gamelogic.movement.MovementManager;
import se.tetris.team5.gamelogic.scoring.GameScoring;

public class GameEngine {
  private KeyMappingManager keyMappingManager = KeyMappingManager.getInstance();

  // 타이머 틱마다 호출 (UI 타이머에서 호출 필요)
  public void onTick() {
    gameScoring.tickScore();
  }

  private BoardManager boardManager;
  private MovementManager movementManager;
  private BlockRotationManager rotationManager;
  private BlockFactory blockFactory;
  private GameScoring gameScoring;

  private Block currentBlock;
  private Block nextBlock;
  private int x, y;
  private boolean gameOver;
  private boolean gameRunning;
  private long gameStartTime;

  private static final int START_X = 3;
  private static final int START_Y = 0;

  public GameEngine(int height, int width) {
    boardManager = new BoardManager();
    movementManager = new MovementManager(boardManager);
    rotationManager = new BlockRotationManager();
    blockFactory = new BlockFactory();
    gameScoring = new GameScoring();
    keyMappingManager.updateFromSettings();

    startNewGame();
  }

  public void startNewGame() {
    boardManager.reset();
    gameScoring.reset();
    gameOver = false;
    keyMappingManager.updateFromSettings();

    currentBlock = blockFactory.createRandomBlock();
    nextBlock = blockFactory.createRandomBlock();
    x = START_X;
    y = START_Y;

    boardManager.placeBlock(currentBlock, x, y);
  }

  public boolean moveBlockDown() {
    if (gameOver)
      return false;

    boardManager.eraseBlock(currentBlock, x, y);

    if (movementManager.moveDown(currentBlock, x, y)) {
      y++;
      gameScoring.addPoints(1); // 소프트 드롭 점수
      boardManager.placeBlock(currentBlock, x, y);
      return true;
    } else {
      boardManager.placeBlock(currentBlock, x, y);
      boardManager.fixBlock(currentBlock, x, y);

      int clearedLines = boardManager.clearLines();
      gameScoring.addLinesCleared(clearedLines);

      spawnNextBlock();
      return false;
    }
  }

  public boolean moveBlockLeft() {
    if (gameOver)
      return false;

    boardManager.eraseBlock(currentBlock, x, y);
    if (movementManager.moveLeft(currentBlock, x, y)) {
      x--;
      boardManager.placeBlock(currentBlock, x, y);
      return true;
    } else {
      boardManager.placeBlock(currentBlock, x, y);
      return false;
    }
  }

  public boolean moveBlockRight() {
    if (gameOver)
      return false;

    boardManager.eraseBlock(currentBlock, x, y);
    if (movementManager.moveRight(currentBlock, x, y)) {
      x++;
      boardManager.placeBlock(currentBlock, x, y);
      return true;
    } else {
      boardManager.placeBlock(currentBlock, x, y);
      return false;
    }
  }

  public boolean rotateBlock() {
    if (gameOver)
      return false;

    boardManager.eraseBlock(currentBlock, x, y);
    if (rotationManager.rotateBlockWithWallKick(currentBlock, x, y, boardManager.getBoard())) {
      boardManager.placeBlock(currentBlock, x, y);
      return true;
    } else {
      boardManager.placeBlock(currentBlock, x, y);
      return false;
    }
  }

  public boolean hardDrop() {
    if (gameOver)
      return false;

    boardManager.eraseBlock(currentBlock, x, y);
    int dropDistance = movementManager.hardDrop(currentBlock, x, y);
    y = movementManager.getDropPosition(currentBlock, x, y);
    gameScoring.addHardDropPoints(dropDistance);

    boardManager.placeBlock(currentBlock, x, y);
    boardManager.fixBlock(currentBlock, x, y);

    int clearedLines = boardManager.clearLines();
    gameScoring.addLinesCleared(clearedLines);

    spawnNextBlock();
    return true;
  }

  private void spawnNextBlock() {
    currentBlock = nextBlock;
    nextBlock = blockFactory.createRandomBlock();
    x = START_X;
    y = START_Y;

    if (!boardManager.canMove(x, y, currentBlock)) {
      gameOver = true;
      return;
    }

    boardManager.placeBlock(currentBlock, x, y);
  }

  // Getter methods
  public KeyMappingManager getKeyMappingManager() {
    return keyMappingManager;
  }

  public BoardManager getBoardManager() {
    return boardManager;
  }

  public GameScoring getGameScoring() {
    return gameScoring;
  }

  public Block getCurrentBlock() {
    return currentBlock;
  }

  public Block getNextBlock() {
    return nextBlock;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public boolean isGameRunning() {
    return gameRunning;
  }

  /**
   * 게임을 초기 상태로 리셋합니다
   */
  public void resetGame() {
    boardManager = new BoardManager(); // 기본 크기
    movementManager = new MovementManager(boardManager);
    gameScoring = new GameScoring();
    blockFactory = new BlockFactory();
    rotationManager = new BlockRotationManager();

    currentBlock = blockFactory.createRandomBlock();
    nextBlock = blockFactory.createRandomBlock();
    x = 3;
    y = 0;
    gameRunning = true;
    gameOver = false;
    gameStartTime = System.currentTimeMillis();
  }

  /**
   * 게임 시작 시간을 반환합니다
   */
  public long getGameStartTime() {
    return gameStartTime;
  }

  /**
   * 소프트 드롭 (한 칸 아래로 이동)
   */
  public void softDrop() {
    if (movementManager.moveDown(currentBlock, x, y)) {
      y++;
      gameScoring.addPoints(1); // 소프트 드롭 점수
    }
  }

  /**
   * 블록을 오른쪽으로 이동
   */
  public void moveRight() {
    if (movementManager.canMoveToPosition(currentBlock, x + 1, y)) {
      x++;
    }
  }

  /**
   * 블록을 왼쪽으로 이동
   */
  public void moveLeft() {
    if (movementManager.canMoveToPosition(currentBlock, x - 1, y)) {
      x--;
    }
  }
}