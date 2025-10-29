
package se.tetris.team5.gamelogic;

import se.tetris.team5.blocks.Block;
import java.util.ArrayList;
import java.util.List;
import se.tetris.team5.gamelogic.block.BlockFactory;
import se.tetris.team5.gamelogic.block.BlockRotationManager;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.gamelogic.movement.MovementManager;
import se.tetris.team5.gamelogic.scoring.GameScoring;
import se.tetris.team5.items.ItemGrantPolicy;

public class GameEngine {
  // 게임 모드 (NORMAL: 아이템 없음, ITEM: 10줄마다 아이템)
  // 기본 모드를 일반 모드로 변경했습니다 (아이템이 나오지 않음)
  private GameMode gameMode = GameMode.NORMAL; // 기본값은 일반 모드

  // 점수 2배 아이템 관련
  private boolean doubleScoreActive = false;
  private long doubleScoreEndTime = 0L;
  private static final int START_X = 3;
  private static final int START_Y = 0;
  // 아이템 관련: 총 삭제 줄 수 추적
  private int totalClearedLines = 0;
  private BoardManager boardManager;
  private MovementManager movementManager;
  private BlockRotationManager rotationManager;
  private BlockFactory blockFactory;
  private BlockFactory.Difficulty difficulty = BlockFactory.Difficulty.NORMAL;
  private GameScoring gameScoring;

  private Block currentBlock;
  private Block nextBlock;
  private int x, y;
  private boolean gameOver;
  private boolean gameRunning;
  private long gameStartTime;

  // listeners to notify UI or other observers about state changes (e.g., next block spawned)
  private List<Runnable> listeners = new ArrayList<>();

  // Last cleared rows (for UI to consume and animate). Cleared row indices are 0..HEIGHT-1
  private java.util.List<Integer> lastClearedRows = new java.util.ArrayList<>();

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
    blockFactory = new BlockFactory(difficulty);
    gameScoring = new GameScoring();
    itemFactory = new se.tetris.team5.items.ItemFactory();
    itemGrantPolicy = new se.tetris.team5.items.Every10LinesItemGrantPolicy();

    startNewGame();
  }

  public void startNewGame() {
    boardManager.reset();
    gameScoring.reset();
    gameScoring.setDifficulty(difficulty);
    gameOver = false;
    totalClearedLines = 0;
    hasTimeStopCharge = false; // 타임스톱 충전 초기화

    // BlockFactory 난이도 반영
    blockFactory.setDifficulty(difficulty);

    // 정책 리셋 (10줄 카운터 초기화)
    if (itemGrantPolicy instanceof se.tetris.team5.items.Every10LinesItemGrantPolicy) {
      ((se.tetris.team5.items.Every10LinesItemGrantPolicy) itemGrantPolicy).reset();
    }

  currentBlock = blockFactory.createRandomBlock();
  nextBlock = blockFactory.createRandomBlock();
  // debug: log initial blocks
  System.out.println("[GameEngine DEBUG] startNewGame current=" + currentBlock.getClass().getSimpleName()
    + " next=" + nextBlock.getClass().getSimpleName());
    x = START_X;
    y = START_Y;

    boardManager.placeBlock(currentBlock, x, y);
  }

  /**
   * 블럭 생성 난이도 설정 (NORMAL, EASY, HARD)
   */
  public void setDifficulty(BlockFactory.Difficulty difficulty) {
    this.difficulty = difficulty;
    if (blockFactory != null) {
      blockFactory.setDifficulty(difficulty);
    }
    if (gameScoring != null) {
      gameScoring.setDifficulty(difficulty);
    }
  }

  public BlockFactory.Difficulty getDifficulty() {
    return difficulty;
  }

  public boolean moveBlockDown() {
    if (gameOver)
      return false;

    boardManager.eraseBlock(currentBlock, x, y);

    if (movementManager.moveDown(currentBlock, x, y)) {
      y++;
      gameScoring.addPoints(applyDoubleScore(1)); // 소프트 드롭 점수
      boardManager.placeBlock(currentBlock, x, y);
      return true;
    } else {
      boardManager.placeBlock(currentBlock, x, y);
      java.util.List<se.tetris.team5.items.Item> removedItems = new java.util.ArrayList<>();
      boardManager.fixBlock(currentBlock, x, y, removedItems);
      int clearedLines = boardManager.clearLines(removedItems);

      // 타임스톱 아이템이 줄 삭제로 제거되었는지 확인
      if (!removedItems.isEmpty()) {
        for (se.tetris.team5.items.Item it : removedItems) {
          if (it instanceof se.tetris.team5.items.TimeStopItem) {
            hasTimeStopCharge = true;
            System.out.println("[타임스톱 충전 완료] Shift 키를 눌러 5초간 게임을 멈출 수 있습니다!");
          }
          // 아이템 효과 적용
          try {
            it.applyEffect(this);
          } catch (Exception e) {
            System.err.println("[아이템 적용 오류] " + e.getMessage());
          }
        }
      }

      gameScoring.addLinesCleared(applyDoubleScoreToLines(clearedLines));
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
    se.tetris.team5.gamelogic.block.BlockRotationManager.WallKickResult result = rotationManager
        .rotateBlockWithWallKick(currentBlock, x, y, boardManager.getBoard());

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
    gameScoring.addHardDropPoints(applyDoubleScore(dropDistance));

    boardManager.placeBlock(currentBlock, x, y);
    java.util.List<se.tetris.team5.items.Item> removedItems = new java.util.ArrayList<>();
    boardManager.fixBlock(currentBlock, x, y, removedItems);
    int clearedLines = boardManager.clearLines(removedItems);

    // 타임스톱 아이템이 줄 삭제로 제거되었는지 확인 및 아이템 효과 적용
    if (!removedItems.isEmpty()) {
      for (se.tetris.team5.items.Item it : removedItems) {
        if (it instanceof se.tetris.team5.items.TimeStopItem) {
          hasTimeStopCharge = true;
          System.out.println("[타임스톱 충전 완료] Shift 키를 눌러 5초간 게임을 멈출 수 있습니다!");
        }
        try {
          it.applyEffect(this);
        } catch (Exception e) {
          System.err.println("[아이템 적용 오류] " + e.getMessage());
        }
      }
    }

    gameScoring.addLinesCleared(applyDoubleScoreToLines(clearedLines));
    handleItemSpawnAndCollect(clearedLines);
    spawnNextBlock();
    return true;
  }

  /**
   * 점수 2배 효과 활성화 (durationMillis 동안)
   */
  public void activateDoubleScore(int durationMillis) {
    doubleScoreActive = true;
    doubleScoreEndTime = System.currentTimeMillis() + durationMillis;
    System.out.println("[아이템 효과] 20초간 점수 2배 시작! (" + new java.util.Date(doubleScoreEndTime) + "까지)");
    doubleScoreEndLogged = false;
  }

  /**
   * 현재 점수 2배 효과가 활성화되어 있는지 확인
   */
  // 2배 효과 종료 로그가 이미 출력됐는지 추적
  private boolean doubleScoreEndLogged = false;

  public boolean isDoubleScoreActive() {
    if (doubleScoreActive && System.currentTimeMillis() > doubleScoreEndTime) {
      doubleScoreActive = false;
      if (!doubleScoreEndLogged) {
        System.out.println("[아이템 효과 종료] 점수 2배 효과가 종료되었습니다. (" + new java.util.Date() + ")");
        doubleScoreEndLogged = true;
      }
    }
    return doubleScoreActive;
  }

  /**
   * 점수 2배 효과 적용 (일반 점수)
   */
  private int applyDoubleScore(int baseScore) {
    if (isDoubleScoreActive()) {
      return baseScore * 2;
    } else {
      return baseScore;
    }
  }

  /**
   * 점수 2배 효과 적용 (라인 삭제 점수)
   */
  private int applyDoubleScoreToLines(int lines) {
    if (isDoubleScoreActive()) {
      return lines * 2;
    } else {
      return lines;
    }
  }

  /**
   * 라인 삭제 후 아이템 생성 및 획득 처리
   */
  private void handleItemSpawnAndCollect(int clearedLines) {
    if (clearedLines <= 0)
      return;
    totalClearedLines += clearedLines;

    // 일반 모드에서는 아이템을 생성하지 않음
    if (gameMode == GameMode.NORMAL) {
      return;
    }

    // 아이템 모드: 정책을 통해 아이템 부여 (10줄 체크는 정책 내부에서 처리)
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

    // 아이템 획득 처리 (타임스톱 제외 - 타임스톱은 줄 삭제 시 충전)
    if (nextBlock != null) {
      for (int j = 0; j < nextBlock.height(); j++) {
        for (int i = 0; i < nextBlock.width(); i++) {
          se.tetris.team5.items.Item item = nextBlock.getItem(i, j);
          if (item != null) {
            acquiredItem = item;
            // TimeStopItem은 줄 삭제 시에만 충전되므로 여기서 처리하지 않음
            if (!(item instanceof se.tetris.team5.items.TimeStopItem)) {
              System.out.println("[아이템 획득 대기] " + item);
            }
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

  // 획득 대기중인 아이템을 반환 (없으면 null) 새로추가된것
  public se.tetris.team5.items.Item getAcquiredItem() {
    return acquiredItem;
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
   * 
   * @return 부여된 아이템 (부여되지 않았으면 null)
   */
  private se.tetris.team5.items.Item grantItemToNextBlock() {
    if (nextBlock == null)
      return null;

    // 정책 객체를 통해 아이템 부여 위임 (10줄 체크는 정책 내부에서)
    se.tetris.team5.items.Item grantedItem = itemGrantPolicy.grantItem(
        nextBlock,
        new ItemGrantPolicy.ItemGrantContext(totalClearedLines, itemFactory));

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
   * Consume and return the last cleared rows recorded by the engine.
   * Returns an empty list if none. This method clears the stored list so subsequent
   * calls won't return the same event again.
   */
  public java.util.List<Integer> consumeLastClearedRows() {
    if (lastClearedRows == null || lastClearedRows.isEmpty()) return new java.util.ArrayList<>();
    java.util.List<Integer> out = new java.util.ArrayList<>(lastClearedRows);
    lastClearedRows.clear();
    return out;
  }

  /**
   * 게임을 초기 상태로 리셋합니다
   */
  public void resetGame() {
    boardManager = new BoardManager(); // 기본 크기
    movementManager = new MovementManager(boardManager);
    gameScoring = new GameScoring();
    gameScoring.setDifficulty(difficulty);
    blockFactory = new BlockFactory(difficulty);
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
   * Register a listener to be notified when engine state changes (e.g., next block spawned).
   * Listener will be invoked on the EDT.
   */
  public void addStateChangeListener(Runnable r) {
    if (r == null) return;
    listeners.add(r);
  }

  /**
   * Invoke registered listeners immediately on the current thread.
   * Used to notify UI to update right after important state changes (like line clears)
   * so the UI can render the cleared rows before the engine continues mutating the board.
   */
  private void notifyListenersImmediate() {
    if (listeners == null || listeners.isEmpty()) return;
    for (Runnable r : listeners) {
      try {
        r.run();
      } catch (Exception ex) {
        // ignore listener exceptions to avoid breaking engine flow
      }
    }
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

  /**
   * 게임 모드를 설정합니다
   * 
   * @param mode NORMAL (일반 모드, 아이템 없음) 또는 ITEM (아이템 모드, 10줄마다 아이템)
   */
  public void setGameMode(GameMode mode) {
    this.gameMode = mode;
    System.out.println("[게임 모드 변경] " + mode + " 모드로 설정되었습니다.");
  }

  /**
   * 현재 게임 모드를 반환합니다
   */
  public GameMode getGameMode() {
    return gameMode;
  }
}