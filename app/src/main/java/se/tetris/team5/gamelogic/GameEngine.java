
package se.tetris.team5.gamelogic;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.block.BlockFactory;
import se.tetris.team5.gamelogic.block.BlockRotationManager;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.gamelogic.movement.MovementManager;
import se.tetris.team5.gamelogic.scoring.GameScoring;
import se.tetris.team5.items.ItemGrantPolicy;

public class GameEngine {
  private static final int START_X = 3;
  private static final int START_Y = 0;
  // 아이템 관련: 총 삭제 줄 수 추적
  private int totalClearedLines = 0;
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

  // 플레이어가 획득한 아이템 (1개만 보유, 큐로 확장 가능)
  private se.tetris.team5.items.Item acquiredItem = null;
  private se.tetris.team5.items.ItemFactory itemFactory;
  private se.tetris.team5.items.ItemGrantPolicy itemGrantPolicy;

  public GameEngine(int height, int width) {
    boardManager = new BoardManager();
    movementManager = new MovementManager(boardManager);
    rotationManager = new BlockRotationManager();
    blockFactory = new BlockFactory();
    gameScoring = new GameScoring();
    itemFactory = new se.tetris.team5.items.ItemFactory();
    itemGrantPolicy = new se.tetris.team5.items.Every10LinesItemGrantPolicy();

    startNewGame();
  }

  public void startNewGame() {
    boardManager.reset();
    gameScoring.reset();
    gameOver = false;

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
      handleItemSpawnAndCollect(clearedLines);
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
    handleItemSpawnAndCollect(clearedLines);
    spawnNextBlock();
    return true;
  }

  /**
   * 라인 삭제 후 아이템 생성 및 획득 처리
   */
  private void handleItemSpawnAndCollect(int clearedLines) {
    if (clearedLines <= 0)
      return;
    totalClearedLines += clearedLines;

    // 1줄만 삭제해도 아이템 생성: 다음 블록에 랜덤 한 칸에 아이템 부여
    grantItemToNextBlock();

    // 아이템 획득 처리: 삭제된 줄의 아이템을 확인하여 획득
    // (BoardManager에 collectClearedLineItems()가 있다고 가정, 없으면 아래처럼 직접 구현)
    // BoardManager에 collectClearedLineItems()가 없다면 임시로 빈 리스트 반환
    // 실제 구현 시: boardManager.collectClearedLineItems()로 대체
    // for (se.tetris.team5.items.Item item : collected) {
    // if (item != null) {
    // acquiredItem = item;
    // System.out.println("[아이템 획득] " + item);
    // }
    // }
    // 임시: 아이템이 실제로 부여된 블록이 삭제된 경우 획득 처리 (현재는 1개만 생성되므로 nextBlock의 아이템을 획득했다고 가정)
    // 실제로는 boardManager에서 삭제된 줄의 아이템을 반환해야 함
    if (nextBlock != null) {
      for (int j = 0; j < nextBlock.height(); j++) {
        for (int i = 0; i < nextBlock.width(); i++) {
          se.tetris.team5.items.Item item = nextBlock.getItem(i, j);
          if (item != null) {
            acquiredItem = item;
            System.out.println("[DEBUG][아이템] 획득: " + item);
            // 아이템은 한 번만 획득(즉시 break)
            break;
          }
        }
      }
    }
  }

  // 아이템 보유 여부 반환
  public boolean hasAcquiredItem() {
    return acquiredItem != null;
  }

  // 아이템 사용(사용 후 null 처리)
  public void useAcquiredItem() {
    if (acquiredItem != null) {
      System.out.println("[DEBUG][아이템] 사용: " + acquiredItem.getName());
      acquiredItem = null;
    } else {
      System.out.println("[DEBUG][아이템] 사용: (보유한 아이템 없음)");
    }
  }

  /**
   * 다음 블록에 랜덤 한 칸에 아이템 부여
   */
  private void grantItemToNextBlock() {
    if (nextBlock == null)
      return;
    // 정책 객체를 통해 아이템 부여 위임
    itemGrantPolicy.grantItem(nextBlock, new ItemGrantPolicy.ItemGrantContext(totalClearedLines, itemFactory));
    // 아이템 부여 상태 전체 출력
    int w = nextBlock.width();
    int h = nextBlock.height();
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        if (nextBlock.getItem(i, j) != null) {
          System.out.println("[DEBUG] nextBlock.items[" + j + "][" + i + "] = " + nextBlock.getItem(i, j));
        }
      }
    }
  }

  private void spawnNextBlock() {
    currentBlock = nextBlock;
    // currentBlock의 아이템 상태 출력
    System.out.println("[DEBUG] spawnNextBlock: currentBlock(" + currentBlock.getBlockType() + ")의 아이템 상태");
    for (int j = 0; j < currentBlock.height(); j++) {
      for (int i = 0; i < currentBlock.width(); i++) {
        if (currentBlock.getItem(i, j) != null) {
          System.out.println("[DEBUG] currentBlock.items[" + j + "][" + i + "] = " + currentBlock.getItem(i, j));
        }
      }
    }
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