
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

  // 타임스톱 관련
  private boolean hasTimeStopCharge = false; // 타임스톱 사용 가능 여부

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
    totalClearedLines = 0;
    hasTimeStopCharge = false; // 타임스톱 충전 초기화
    
    // 정책 리셋 (10줄 카운터 초기화)
    if (itemGrantPolicy instanceof se.tetris.team5.items.Every10LinesItemGrantPolicy) {
      ((se.tetris.team5.items.Every10LinesItemGrantPolicy) itemGrantPolicy).reset();
    }

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
    se.tetris.team5.gamelogic.block.BlockRotationManager.WallKickResult result = 
        rotationManager.rotateBlockWithWallKick(currentBlock, x, y, boardManager.getBoard());
    
    if (result.success) {
      // Wall Kick 성공: 오프셋 적용
      x += result.offsetX;
      y += result.offsetY;
      boardManager.placeBlock(currentBlock, x, y);
      return true;
    } else {
      // 회전 실패: 원래 위치에 다시 배치
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

    // 정책을 통해 아이템 부여 (10줄 체크는 정책 내부에서 처리)
    se.tetris.team5.items.Item grantedItem = grantItemToNextBlock();
    
    if (grantedItem != null) {
      // 아이템이 부여된 경우
      if (grantedItem instanceof se.tetris.team5.items.WeightBlockItem) {
        // WeightBlockItem인 경우: 다음 블록을 무게추 블록으로 교체
        nextBlock = blockFactory.createWeightBlock();
        System.out.println("[특수 블록] 무게추 블록(WBlock) 생성!");
      } else if (grantedItem instanceof se.tetris.team5.items.LineClearItem) {
        // LineClearItem인 경우: 일반 블록 + 아이템 유지
        System.out.println("[특수 블록] 줄삭제 아이템 블록 생성!");
      } else if (grantedItem instanceof se.tetris.team5.items.BombItem) {
        // DotBlockItem인 경우: 다음 블록을 DotBlock으로 교체
        nextBlock = new se.tetris.team5.blocks.DotBlock();
        System.out.println("[특수 블록] 도트 블록(DotBlock) 생성!");
      } else if (grantedItem instanceof se.tetris.team5.items.TimeStopItem) {
        // TimeStopItem인 경우: 일반 블록 + 아이템 유지
        System.out.println("[특수 블록] 타임스톱 아이템 블록 생성!");
      }
    }

    // 아이템 획득 처리
    if (nextBlock != null) {
      for (int j = 0; j < nextBlock.height(); j++) {
        for (int i = 0; i < nextBlock.width(); i++) {
          se.tetris.team5.items.Item item = nextBlock.getItem(i, j);
          if (item != null) {
            acquiredItem = item;
            // TimeStopItem인 경우 충전 상태로 변경
            if (item instanceof se.tetris.team5.items.TimeStopItem) {
              hasTimeStopCharge = true;
              System.out.println("[타임스톱 충전] Shift 키를 눌러 5초간 게임을 멈출 수 있습니다!");
            }
            System.out.println("[아이템 획득 대기] " + item);
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
      System.out.println("[아이템 사용] " + acquiredItem.getName());
      acquiredItem = null;
    }
  }

  // 타임스톱 충전 여부 반환
  public boolean hasTimeStopCharge() {
    return hasTimeStopCharge;
  }

  // 타임스톱 사용 (충전 소모)
  public void useTimeStop() {
    if (hasTimeStopCharge) {
      hasTimeStopCharge = false;
      System.out.println("[타임스톱 사용] 게임이 5초간 멈춥니다!");
    }
  }

  /**
   * 다음 블록에 아이템 부여 (정책을 통해)
   * @return 부여된 아이템 (부여되지 않았으면 null)
   */
  private se.tetris.team5.items.Item grantItemToNextBlock() {
    if (nextBlock == null)
      return null;
    
    // 정책 객체를 통해 아이템 부여 위임 (10줄 체크는 정책 내부에서)
    se.tetris.team5.items.Item grantedItem = itemGrantPolicy.grantItem(
        nextBlock, 
        new ItemGrantPolicy.ItemGrantContext(totalClearedLines, itemFactory)
    );
    
    return grantedItem;
  }

  private void spawnNextBlock() {
    currentBlock = nextBlock;
    
    // 일반 블록 생성 (특수 블록은 handleItemSpawnAndCollect에서 처리)
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
    
    // 정책 리셋 (10줄 카운터 초기화)
    if (itemGrantPolicy instanceof se.tetris.team5.items.Every10LinesItemGrantPolicy) {
      ((se.tetris.team5.items.Every10LinesItemGrantPolicy) itemGrantPolicy).reset();
    }

    currentBlock = blockFactory.createRandomBlock();
    nextBlock = blockFactory.createRandomBlock();
    x = 3;
    y = 0;
    gameRunning = true;
    gameOver = false;
    gameStartTime = System.currentTimeMillis();
    totalClearedLines = 0;
    hasTimeStopCharge = false; // 타임스톱 충전 초기화
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