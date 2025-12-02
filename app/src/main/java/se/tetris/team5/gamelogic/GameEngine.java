
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
  // Default to ITEM mode to preserve legacy behavior where items are available.
  // If you want no items, call setGameMode(GameMode.NORMAL) or add a user
  // setting.
  private GameMode gameMode = GameMode.ITEM; // 기본값: 아이템 모드

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
  private boolean paused = false;
  private long gameStartTime;

  // listeners to notify UI or other observers about state changes (e.g., next
  // block spawned)
  private List<Runnable> listeners = new ArrayList<>();

  // 대전모드: 블럭 고정 후 콜백 (공격 블럭 적용용)
  private Runnable onBlockFixedCallback = null;

  // Last cleared rows (for UI to consume and animate). Cleared row indices are
  // 0..HEIGHT-1
  private java.util.List<Integer> lastClearedRows = new java.util.ArrayList<>();

  // 플레이어가 획득한 아이템 (1개만 보유, 큐로 확장 가능)
  private se.tetris.team5.items.Item acquiredItem = null;
  private se.tetris.team5.items.ItemFactory itemFactory;
  private se.tetris.team5.items.ItemGrantPolicy itemGrantPolicy;

  // 타임스톱 관련
  private boolean hasTimeStopCharge = false; // 타임스톱 사용 가능 여부

  // 펜딩 아이템: 다음 블록이 아닌 그 다음 블록에 적용될 아이템
  private se.tetris.team5.items.Item pendingItem = null;

  // 패널티 관련: 10줄 초과 시 -200점
  private static final int PENALTY_HEIGHT = 10; // 10줄 초과 시 패널티
  private static final int PENALTY_SCORE = 200; // 패널티 점수
  private boolean penaltyApplied = false; // 패널티가 적용되었는지 여부

  public GameEngine(int height, int width) {
    this(height, width, true); // 기본적으로 자동 시작
  }

  /**
   * GameEngine 생성자 (자동 시작 여부 선택 가능)
   * 
   * @param height    보드 높이
   * @param width     보드 너비
   * @param autoStart true면 자동으로 startNewGame() 호출, false면 수동 호출 필요
   */
  public GameEngine(int height, int width, boolean autoStart) {
    boardManager = new BoardManager();
    movementManager = new MovementManager(boardManager);
    rotationManager = new BlockRotationManager();
    blockFactory = new BlockFactory(difficulty);
    gameScoring = new GameScoring();
    itemFactory = new se.tetris.team5.items.ItemFactory();
    itemGrantPolicy = new se.tetris.team5.items.Every10LinesItemGrantPolicy();

    if (autoStart) {
      startNewGame();
    }
  }

  public void startNewGame() {
    boardManager.reset();
    gameScoring.reset();
    gameScoring.setDifficulty(difficulty);
    gameOver = false;
    totalClearedLines = 0;
    hasTimeStopCharge = false; // 타임스톱 충전 초기화
    pendingItem = null; // 펜딩 아이템 초기화
    penaltyApplied = false; // 패널티 플래그 초기화

    // BlockFactory 난이도 반영
    blockFactory.setDifficulty(difficulty);

    // 정책 리셋 (10줄 카운터 초기화)
    if (itemGrantPolicy instanceof se.tetris.team5.items.Every10LinesItemGrantPolicy) {
      ((se.tetris.team5.items.Every10LinesItemGrantPolicy) itemGrantPolicy).reset();
    }

    boolean itemModeOnly = (gameMode == GameMode.ITEM);
    currentBlock = blockFactory.createRandomBlock(itemModeOnly);
    nextBlock = blockFactory.createRandomBlock(itemModeOnly);
    x = START_X;
    y = START_Y;

    // 블록을 보드에 배치하지 않음 - renderBoard에서 동적으로 그려짐
    // boardManager.placeBlock(currentBlock, x, y);
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
    if (gameOver || paused)
      return false;

    boardManager.eraseBlock(currentBlock, x, y);

    if (movementManager.moveDown(currentBlock, x, y)) {
      y++;
      // 무게추 블록(WBlock)일 때는 점수를 부여하지 않음
      if (!(currentBlock instanceof se.tetris.team5.blocks.WBlock)) {
        gameScoring.addPoints(applyDoubleScore(1)); // 소프트 드롭 점수
      }
      boardManager.placeBlock(currentBlock, x, y);
      return true;
    } else {
      boardManager.placeBlock(currentBlock, x, y);
      java.util.List<se.tetris.team5.items.Item> removedItems = new java.util.ArrayList<>();
      int lineClearRemovedBlocks = boardManager.fixBlock(currentBlock, x, y, removedItems);
      // 줄삭제 아이템으로 지워진 블럭 수만큼 점수 추가 (30점/블럭)
      if (lineClearRemovedBlocks > 0) {
        gameScoring.addPoints(applyDoubleScore(lineClearRemovedBlocks * 30));
      }
      int clearedLines = boardManager.clearLines(removedItems);
      // capture cleared rows for UI animation and notify listeners before we continue
      try {
        lastClearedRows = boardManager.getLastClearedRows();
        // notify UI immediately so it can start animations before we spawn the next
        // block
        notifyListenersImmediate();
      } catch (Exception ex) {
        // swallow - non-fatal
      }

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

      // 블록 높이 패널티 체크
      checkHeightPenalty();

      // 대전모드: 블럭 고정 후 콜백 호출 (공격 블럭 적용)
      if (onBlockFixedCallback != null) {
        try {
          onBlockFixedCallback.run();
        } catch (Exception e) {
          System.err.println("[GameEngine] 콜백 실행 중 오류: " + e.getMessage());
          e.printStackTrace();
        }
      }

      spawnNextBlock();
      return false;
    }
  }

  public boolean moveBlockLeft() {
    if (gameOver || paused || currentBlock == null)
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
    if (gameOver || paused || currentBlock == null)
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
    if (gameOver || paused)
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
    if (gameOver || paused)
      return false;

    boardManager.eraseBlock(currentBlock, x, y);
    int dropDistance = movementManager.hardDrop(currentBlock, x, y);
    y = movementManager.getDropPosition(currentBlock, x, y);

    // 무게추 블록(WBlock)일 때는 하드드롭 점수를 부여하지 않음
    if (!(currentBlock instanceof se.tetris.team5.blocks.WBlock)) {
      gameScoring.addHardDropPoints(applyDoubleScore(dropDistance));
    }

    boardManager.placeBlock(currentBlock, x, y);
    java.util.List<se.tetris.team5.items.Item> removedItems = new java.util.ArrayList<>();
    int lineClearRemovedBlocks = boardManager.fixBlock(currentBlock, x, y, removedItems);
    // 줄삭제 아이템으로 지워진 블럭 수만큼 점수 추가 (30점/블럭)
    if (lineClearRemovedBlocks > 0) {
      gameScoring.addPoints(applyDoubleScore(lineClearRemovedBlocks * 30));
    }
    int clearedLines = boardManager.clearLines(removedItems);
    // capture cleared rows for UI animation and notify listeners before we continue
    try {
      lastClearedRows = boardManager.getLastClearedRows();
      notifyListenersImmediate();
    } catch (Exception ex) {
      // ignore
    }

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

    // 블록 높이 패널티 체크
    checkHeightPenalty();

    // 대전모드: 블럭 고정 후 콜백 호출 (공격 블럭 적용)
    if (onBlockFixedCallback != null) {
      try {
        onBlockFixedCallback.run();
      } catch (Exception e) {
        System.err.println("[GameEngine] 콜백 실행 중 오류: " + e.getMessage());
        e.printStackTrace();
      }
    }

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
   * Returns remaining milliseconds for double-score effect, or 0 if not active.
   */
  public long getDoubleScoreRemainingMillis() {
    if (!doubleScoreActive)
      return 0L;
    long rem = doubleScoreEndTime - System.currentTimeMillis();
    return rem > 0 ? rem : 0L;
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

    // 아이템 모드: 정책을 통해 아이템 부여 조건 확인 (10줄 체크는 정책 내부에서 처리)
    // 임시 블록을 만들어서 아이템 부여 조건을 확인
    boolean itemModeOnly = (gameMode == GameMode.ITEM);
    Block tempBlock = blockFactory.createRandomBlock(itemModeOnly);
    se.tetris.team5.items.Item grantedItem = itemGrantPolicy.grantItem(
        tempBlock,
        new ItemGrantPolicy.ItemGrantContext(totalClearedLines, itemFactory));

    if (grantedItem != null) {
      // 아이템이 부여되었다면, 다음다음 블록에 적용하기 위해 pendingItem에 저장
      pendingItem = grantedItem;
      System.out.println("[아이템 예약] " + grantedItem.getName() + " - 다음 블록 이후에 나타납니다!");
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

  // 획득 대기중인 아이템을 반환 (없으면 null)
  public se.tetris.team5.items.Item getAcquiredItem() {
    return acquiredItem;
  }

  /**
   * 블록 높이 패널티 체크
   * 10줄을 초과하면 -200점, 10줄 이하로 내려가면 패널티 플래그 리셋
   */
  private void checkHeightPenalty() {
    int highestRow = boardManager.getHighestBlockRow();

    if (highestRow > PENALTY_HEIGHT) {
      // 10줄 초과 && 아직 패널티가 적용되지 않았다면
      if (!penaltyApplied) {
        int currentScore = gameScoring.getCurrentScore();
        if (currentScore >= PENALTY_SCORE) {
          gameScoring.addPoints(-PENALTY_SCORE);
        } else {
          // 점수가 200점 미만이면 0점으로 만듦
          gameScoring.addPoints(-currentScore);
        }
        penaltyApplied = true;
      }
    } else {
      // 10줄 이하로 내려가면 패널티 플래그 리셋
      if (penaltyApplied) {
        penaltyApplied = false;
        System.out.println("[패널티 해제] 블록 높이가 10줄 이하로 내려갔습니다 (현재 높이: " + highestRow + "줄)");
      }
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

  private void spawnNextBlock() {
    currentBlock = nextBlock;

    // 펜딩 아이템이 있으면 다음 블록에 적용
    if (pendingItem != null) {
      if (pendingItem instanceof se.tetris.team5.items.WeightBlockItem) {
        // WeightBlockItem인 경우: 무게추 블록으로 교체
        nextBlock = blockFactory.createWeightBlock();
        System.out.println("[특수 블록] 무게추 블록(WBlock) 생성!");
      } else if (pendingItem instanceof se.tetris.team5.items.BombItem) {
        // BombItem인 경우: DotBlock으로 교체
        nextBlock = new se.tetris.team5.blocks.DotBlock();
        System.out.println("[특수 블록] 도트 블록(DotBlock) 생성!");
      } else {
        // 일반 블록 + 아이템 (LineClearItem, TimeStopItem 등)
        boolean itemModeOnly = (gameMode == GameMode.ITEM);
        nextBlock = blockFactory.createRandomBlock(itemModeOnly);

        // 블록의 모든 유효한 칸을 수집한 후 랜덤하게 선택
        java.util.List<int[]> validPositions = new java.util.ArrayList<>();
        for (int j = 0; j < nextBlock.height(); j++) {
          for (int i = 0; i < nextBlock.width(); i++) {
            if (nextBlock.getShape(i, j) == 1) {
              validPositions.add(new int[] { i, j });
            }
          }
        }

        // 유효한 위치가 있으면 랜덤하게 선택하여 아이템 설정
        if (!validPositions.isEmpty()) {
          java.util.Random rand = new java.util.Random();
          int[] chosen = validPositions.get(rand.nextInt(validPositions.size()));
          nextBlock.setItem(chosen[0], chosen[1], pendingItem);
          System.out
              .println("[특수 블록] " + pendingItem.getName() + " 아이템 블록 생성! (위치: " + chosen[0] + ", " + chosen[1] + ")");
        }
      }

      // 아이템 획득 처리 (타임스톱 제외 - 타임스톱은 줄 삭제 시 충전)
      for (int j = 0; j < nextBlock.height(); j++) {
        for (int i = 0; i < nextBlock.width(); i++) {
          se.tetris.team5.items.Item item = nextBlock.getItem(i, j);
          if (item != null) {
            // TimeStopItem은 줄 삭제 시에만 충전되므로 acquiredItem에 저장하지 않음
            if (!(item instanceof se.tetris.team5.items.TimeStopItem)) {
              acquiredItem = item;
              System.out.println("[아이템 획득 대기] " + item);
            } else {
              // TimeStopItem이 블록에 포함되어 있음을 알림
              System.out.println("[타임스톱 블록] 이 블록을 줄 삭제하면 타임스톱이 충전됩니다!");
            }
            break;
          }
        }
      }

      pendingItem = null; // 펜딩 아이템 소비
    } else {
      // 펜딩 아이템이 없으면 일반 블록 생성
      boolean itemModeOnly = (gameMode == GameMode.ITEM);
      nextBlock = blockFactory.createRandomBlock(itemModeOnly);
    }

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

  /**
   * 현재 블럭이 착지할 Y 위치를 계산하여 반환합니다 (고스트 블럭용)
   */
  public int getGhostY() {
    if (currentBlock == null || gameOver) {
      return -1;
    }
    return movementManager.getDropPosition(currentBlock, x, y);
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public boolean isGameRunning() {
    return gameRunning;
  }

  /**
   * 게임 일시정지/재개 설정
   * 
   * @param paused true면 일시정지, false면 재개
   */
  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  /**
   * 게임이 일시정지 상태인지 확인
   * 
   * @return true면 일시정지, false면 실행 중
   */
  public boolean isPaused() {
    return paused;
  }

  /**
   * Consume and return the last cleared rows recorded by the engine.
   * Returns an empty list if none. This method clears the stored list so
   * subsequent
   * calls won't return the same event again.
   */
  public java.util.List<Integer> consumeLastClearedRows() {
    if (lastClearedRows == null || lastClearedRows.isEmpty())
      return new java.util.ArrayList<>();
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

    boolean itemModeOnly = (gameMode == GameMode.ITEM);
    currentBlock = blockFactory.createRandomBlock(itemModeOnly);
    nextBlock = blockFactory.createRandomBlock(itemModeOnly);
    x = 3;
    y = 0;
    gameRunning = true;
    gameOver = false;
    paused = false;
    gameStartTime = System.currentTimeMillis();
    totalClearedLines = 0;
    hasTimeStopCharge = false; // 타임스톱 충전 초기화
    pendingItem = null; // 펜딩 아이템 초기화
    penaltyApplied = false; // 패널티 플래그 초기화
  }

  /**
   * Register a listener to be notified when engine state changes (e.g., next
   * block spawned).
   * Listener will be invoked on the EDT.
   */
  public void addStateChangeListener(Runnable r) {
    if (r == null)
      return;
    listeners.add(r);
  }

  /**
   * Invoke registered listeners immediately on the current thread.
   * Used to notify UI to update right after important state changes (like line
   * clears)
   * so the UI can render the cleared rows before the engine continues mutating
   * the board.
   */
  private void notifyListenersImmediate() {
    if (listeners == null || listeners.isEmpty())
      return;
    // Ensure listeners run on the Swing EDT so UI updates are safe and consistent.
    for (Runnable r : listeners) {
      try {
        javax.swing.SwingUtilities.invokeLater(r);
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
   * 대전모드: 블럭 고정 후 콜백 설정
   * 
   * @param callback 블럭이 고정된 직후 호출될 콜백
   */
  public void setOnBlockFixedCallback(Runnable callback) {
    this.onBlockFixedCallback = callback;
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
  }

  /**
   * 현재 게임 모드를 반환합니다
   */
  public GameMode getGameMode() {
    return gameMode;
  }
}