package se.tetris.team5.gamelogic.ai;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.block.BlockRotationManager;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * 테트리스 AI 플레이어
 * Beam Search 기반 2-Piece Lookahead 알고리즘
 */
public class TetrisAI {

  private final GameEngine gameEngine;
  private final BlockRotationManager rotationManager;
  private int thinkDelayMs = 700; // 기본값: 보통 난이도
  private long lastThinkTime = 0;

  // AI 상태
  private int rotate = 0; // 회전 횟수
  private int shift = 0; // 좌우 이동
  private boolean isInitial = true;

  // 보드 크기
  private static final int WIDTH = 10;
  private static final int HEIGHT = 20;
  // N_BUFFER_LINES 제거: BoardManager와 동일하게 전체 보드(HEIGHT)를 사용

  // Beam Search 설정
  private static final int BEAM_WIDTH = 3; // 상위 K개만 유지

  // AI 난이도
  private AIPlayerController.AIDifficulty difficulty = AIPlayerController.AIDifficulty.NORMAL;

  // 평가 함수 가중치 (기본값)
  private WeightSet weights;

  public TetrisAI(GameEngine gameEngine) {
    this.gameEngine = gameEngine;
    this.rotationManager = new BlockRotationManager();
    // 기본 가중치 설정
    this.weights = new WeightSet();
  }

  /**
   * 가중치 설정 (유전 알고리즘용)
   */
  public void setWeights(WeightSet weights) {
    this.weights = weights;
  }

  /**
   * AI 난이도 설정
   * 난이도에 따라 사고 시간만 조절합니다. 가중치는 항상 기본값을 사용합니다.
   * 
   * @param difficulty AI 난이도 (NORMAL: 보통, HARD: 어려움)
   */
  public void setDifficulty(AIPlayerController.AIDifficulty difficulty) {
    this.difficulty = difficulty;

    // 난이도에 따라 사고 시간만 조절
    switch (difficulty) {
      case NORMAL:
        this.thinkDelayMs = 500; // 보통: 500ms
        break;
      case HARD:
        this.thinkDelayMs = 200; // 어려움: 200ms (더 빠른 사고)
        break;
    }
  }

  /**
   * 현재 가중치 반환
   */
  public WeightSet getWeights() {
    return weights;
  }

  /**
   * AI가 다음 행동을 결정하고 실행
   * 
   * @return true if action was taken, false otherwise
   */
  public boolean makeMove() {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastThinkTime < thinkDelayMs) {
      return false;
    }

    if (gameEngine.isGameOver()) {
      return false;
    }

    if (gameEngine.getCurrentBlock() == null) {
      return false;
    }

    lastThinkTime = currentTime;

    // 초기 상태인지 확인
    if (isInitial) {
      initializeAI();
    }

    // 이동 실행
    if (rotate > 0) {
      rotate--;
      gameEngine.rotateBlock();
      return true;
    }

    if (shift != 0) {
      if (shift > 0) {
        shift--;
        gameEngine.moveBlockLeft();
      } else {
        shift++;
        gameEngine.moveBlockRight();
      }
      return true;
    }

    // 하드 드롭
    gameEngine.hardDrop();
    isInitial = true; // 다음 블록을 위해 초기화
    return true;
  }

  /**
   * AI 초기화 - Beam Search로 최적 위치 계산
   */
  private void initializeAI() {
    int[][] board = gameEngine.getBoardManager().getBoard();
    Block currentBlock = gameEngine.getCurrentBlock();
    Block nextBlock = gameEngine.getNextBlock();
    int currentX = gameEngine.getX();

    // Beam Search: 2-Piece Lookahead
    Move bestMove = beamSearch(board, currentBlock, nextBlock);

    if (bestMove != null) {
      rotate = bestMove.rotation;
      shift = currentX - bestMove.x;
      isInitial = false;
    } else {
      // 최적 수를 찾지 못한 경우 기본 동작
      rotate = 0;
      shift = 0;
      isInitial = false;
    }
  }

  /**
   * Beam Search 알고리즘: 2-Piece Lookahead
   */
  private Move beamSearch(int[][] board, Block currentPiece, Block nextPiece) {
    // Step 1: 현재 블록으로 가능한 모든 수 생성
    List<Move> currentMoves = generateMoves(board, currentPiece);

    if (currentMoves.isEmpty()) {
      return null;
    }

    // Step 2: 각 수를 평가하고 상위 K개만 선택
    PriorityQueue<MoveScore> beam = new PriorityQueue<>(
        Comparator.comparingDouble((MoveScore ms) -> ms.score).reversed());

    for (Move move : currentMoves) {
      SimulationResult result = simulateMove(board, currentPiece, move.x, move.rotation);
      if (result == null) {
        continue;
      }

      double score = evaluateBoard(result.board, result.linesCleared, result.landingHeight, result.usefulBlocks,
          currentPiece);
      beam.offer(new MoveScore(move, result.board, score));

      // Beam Width 제한
      if (beam.size() > BEAM_WIDTH * 10) { // 여유있게 생성 후 나중에 필터링
        PriorityQueue<MoveScore> temp = new PriorityQueue<>(
            Comparator.comparingDouble((MoveScore ms) -> ms.score).reversed());
        for (int i = 0; i < BEAM_WIDTH && !beam.isEmpty(); i++) {
          temp.offer(beam.poll());
        }
        beam = temp;
      }
    }

    // Step 3: 상위 K개에 대해 다음 블록 시뮬레이션
    PriorityQueue<MoveScore> finalScores = new PriorityQueue<>(
        Comparator.comparingDouble((MoveScore ms) -> ms.score).reversed());

    List<MoveScore> topMoves = new ArrayList<>();
    int count = 0;
    while (!beam.isEmpty() && count < BEAM_WIDTH) {
      MoveScore ms = beam.poll();
      topMoves.add(ms);
      count++;
    }

    for (MoveScore currentMoveScore : topMoves) {
      // 다음 블록으로 가능한 모든 수 생성
      List<Move> nextMoves = generateMoves(currentMoveScore.board, nextPiece);

      double bestNextScore = Double.NEGATIVE_INFINITY;
      for (Move nextMove : nextMoves) {
        SimulationResult nextResult = simulateMove(currentMoveScore.board, nextPiece, nextMove.x, nextMove.rotation);
        if (nextResult == null) {
          continue;
        }

        double nextScore = evaluateBoard(nextResult.board, nextResult.linesCleared, nextResult.landingHeight,
            nextResult.usefulBlocks, nextPiece);
        if (nextScore > bestNextScore) {
          bestNextScore = nextScore;
        }
      }

      // 현재 점수 + 미래 점수
      double totalScore = currentMoveScore.score + (bestNextScore * 0.5); // 미래 점수는 가중치 감소
      finalScores.offer(new MoveScore(currentMoveScore.move, currentMoveScore.board, totalScore));
    }

    // 최고 점수 선택
    if (!finalScores.isEmpty()) {
      return finalScores.poll().move;
    }

    // Fallback: Greedy (1-Piece Lookahead)
    return greedySearch(board, currentPiece);
  }

  /**
   * Greedy Search: 1-Piece Lookahead (Fallback)
   */
  private Move greedySearch(int[][] board, Block piece) {
    List<Move> moves = generateMoves(board, piece);
    Move bestMove = null;
    double bestScore = Double.NEGATIVE_INFINITY;

    for (Move move : moves) {
      SimulationResult result = simulateMove(board, piece, move.x, move.rotation);
      if (result == null) {
        continue;
      }

      double score = evaluateBoard(result.board, result.linesCleared, result.landingHeight, result.usefulBlocks, piece);
      if (score > bestScore) {
        bestScore = score;
        bestMove = move;
      }
    }

    return bestMove;
  }

  /**
   * 무브 제너레이터: 현재 블록으로 가능한 모든 위치 찾기
   */
  private List<Move> generateMoves(int[][] board, Block piece) {
    List<Move> moves = new ArrayList<>();

    // 원본 블록 복사
    Block originalBlock = rotationManager.copyBlock(piece);
    if (originalBlock == null) {
      return moves;
    }

    // O블록은 회전이 의미 없음
    boolean isOBlock = originalBlock instanceof se.tetris.team5.blocks.OBlock;

    // 최대 회전 횟수 (O블록은 1회, 나머지는 4회)
    int maxRotations = isOBlock ? 1 : 4;

    for (int rotation = 0; rotation < maxRotations; rotation++) {
      Block rotatedBlock = rotationManager.copyBlock(originalBlock);
      if (rotatedBlock == null) {
        continue;
      }

      // 회전 적용
      for (int r = 0; r < rotation; r++) {
        rotatedBlock.rotate();
      }

      // 모든 가능한 X 위치 시도
      for (int x = -2; x <= WIDTH - 2; x++) {
        // 블록을 위에서부터 떨어뜨리기 (y는 작을수록 위쪽)
        int y = 0;
        int lastValidY = -1;

        // 아래로 내려가면서 유효한 위치 찾기
        while (y < HEIGHT) { // N_BUFFER_LINES 제거: BoardManager와 동일하게 전체 보드 체크
          if (canPlaceBlock(rotatedBlock, x, y, board)) {
            lastValidY = y;
            y++;
          } else {
            break;
          }
        }

        // 유효한 위치가 있으면 추가
        if (lastValidY >= 0) {
          moves.add(new Move(x, rotation));
        }
      }
    }

    return moves;
  }

  /**
   * 블록을 특정 위치에 놓았을 때의 결과 보드 상태를 반환
   * 
   * @return SimulationResult (보드 상태와 줄 삭제 개수)
   */
  private SimulationResult simulateMove(int[][] board, Block piece, int x, int rotation) {
    // 보드 복사
    int[][] newBoard = copyBoard(board);

    // 블록 복사 및 회전
    Block rotatedBlock = rotationManager.copyBlock(piece);
    if (rotatedBlock == null) {
      return null;
    }

    for (int r = 0; r < rotation; r++) {
      rotatedBlock.rotate();
    }

    // 블록을 위에서부터 떨어뜨리기 (y는 작을수록 위쪽)
    int y = 0;
    int lastValidY = -1;

    // 아래로 내려가면서 유효한 위치 찾기
    while (y < HEIGHT) { // N_BUFFER_LINES 제거: BoardManager와 동일하게 전체 보드 체크
      if (canPlaceBlock(rotatedBlock, x, y, newBoard)) {
        lastValidY = y;
        y++;
      } else {
        break;
      }
    }

    // 유효한 위치가 없으면 null 반환
    if (lastValidY < 0) {
      return null;
    }

    y = lastValidY;

    // 블록 배치
    placeBlockOnBoard(rotatedBlock, x, y, newBoard);

    // 게임 오버 체크 (맨 위 줄에 블록이 있으면 게임 오버)
    if (isGameOver(newBoard)) {
      return null; // 게임 오버면 무효한 수
    }

    // El-Tetris: Landing Height 계산 (블록이 착지한 높이)
    // y는 작을수록 위쪽이므로, 블록의 중심 높이를 계산
    int landingHeight = calculateLandingHeight(rotatedBlock, x, y);

    // 줄 삭제 시뮬레이션
    int linesCleared = countFullRows(newBoard);

    // El-Tetris: Eroded Piece Cells Metric 계산 (유용한 블록 셀 수)
    int usefulBlocks = calculateUsefulBlocks(rotatedBlock, x, y, newBoard, linesCleared);

    newBoard = simulateLineClear(newBoard);

    // 줄 삭제 후에도 게임 오버 체크
    if (isGameOver(newBoard)) {
      return null; // 게임 오버면 무효한 수
    }

    return new SimulationResult(newBoard, linesCleared, landingHeight, usefulBlocks);
  }

  /**
   * 평가 함수: El-Tetris 알고리즘 기반 + 대전 모드 공격 규칙
   * El-Tetris 특징:
   * 1. Landing Height: 블록이 착지한 높이
   * 2. Eroded Piece Cells Metric: 삭제된 줄 수 × 유용한 블록 수
   * 3. Row Transitions: 가로 전환 횟수
   * 4. Column Transitions: 세로 전환 횟수
   * 5. Number of Holes: 구멍 개수
   * 6. Well Sums: Well의 깊이 합
   * 
   * 대전 모드 추가:
   * - 2줄 이상 삭제 시 공격 보너스
   */
  private double evaluateBoard(int[][] board, int linesCleared, int landingHeight, int usefulBlocks,
      @SuppressWarnings("unused") Block piece) {
    double score = 0.0;

    // El-Tetris 특징 계산
    // 1. Landing Height (블록이 착지한 높이)
    score += landingHeight * weights.weightLandingHeight;

    // 2. Eroded Piece Cells Metric (EPCM) = 삭제된 줄 수 × 유용한 블록 수
    int epcm = linesCleared * usefulBlocks;
    score += epcm * weights.weightEPCM;

    // 3. Row Transitions (가로 전환 횟수)
    int rowTransitions = countRowTransitions(board);
    score += rowTransitions * weights.weightRowTransitions;

    // 4. Column Transitions (세로 전환 횟수)
    int columnTransitions = countColumnTransitions(board);
    score += columnTransitions * weights.weightColumnTransitions;

    // 5. Number of Holes (구멍 개수)
    int holes = countHoles(board);
    score += holes * weights.weightHoles;

    // 6. Well Sums (Well의 깊이 합)
    int wellSums = calculateWellSums(board);
    score += wellSums * weights.weightWellSums;

    // 대전 모드 공격 규칙: 줄별 가중치 적용
    if (linesCleared == 2) {
      score += weights.weightAttack2Lines;
    } else if (linesCleared == 3) {
      score += weights.weightAttack3Lines;
    } else if (linesCleared == 4) {
      score += weights.weightAttack4Lines; // Tetris 보너스
    }

    return score;
  }

  /**
   * 구멍 개수 계산
   */
  private int countHoles(int[][] board) {
    int holes = 0;
    for (int x = 0; x < WIDTH; x++) {
      boolean foundBlock = false;
      for (int y = 0; y < HEIGHT; y++) { // N_BUFFER_LINES 제거: BoardManager와 동일하게 전체 보드 체크
        if (board[y][x] == 1) { // == 1로 통일: 고정된 블록만 체크
          foundBlock = true;
        } else if (foundBlock) {
          holes++; // 블록 위에 빈칸이 있으면 구멍
        }
      }
    }
    return holes;
  }

  /**
   * 가로 끊김 (Row Transitions) 계산
   */
  private int countRowTransitions(int[][] board) {
    int transitions = 0;
    int highestRow = findHighestRow(board);

    if (highestRow < 0) {
      return 0;
    }

    for (int y = 0; y <= highestRow; y++) {
      boolean last = true; // 왼쪽 경계는 채워져 있다고 가정
      for (int x = 0; x < WIDTH; x++) {
        boolean now = (board[y][x] == 1); // == 1로 통일: 고정된 블록만 체크
        if (last != now) {
          transitions++;
        }
        last = now;
      }
      if (!last) { // 오른쪽 경계 전환
        transitions++;
      }
    }

    return transitions;
  }

  /**
   * El-Tetris: Landing Height 계산 (블록이 착지한 높이)
   * 블록의 중심 높이를 계산 (y는 작을수록 위쪽)
   */
  private int calculateLandingHeight(Block block, int x, int y) {
    // 블록의 가장 아래쪽 셀의 y 좌표를 찾음
    int maxY = y;
    for (int bx = 0; bx < block.width(); bx++) {
      for (int by = 0; by < block.height(); by++) {
        if (block.getShape(bx, by) == 1) {
          int blockY = y + by;
          if (blockY > maxY) {
            maxY = blockY;
          }
        }
      }
    }
    // Landing Height = HEIGHT - maxY (높이로 변환, 높을수록 큰 값)
    return HEIGHT - maxY;
  }

  /**
   * El-Tetris: Eroded Piece Cells Metric (EPCM) 계산
   * 삭제된 줄에 기여한 블록 셀 수를 계산
   */
  private int calculateUsefulBlocks(Block block, int x, int y, int[][] boardBeforeClear, int linesCleared) {
    if (linesCleared == 0) {
      return 0;
    }

    // 삭제될 줄 찾기
    List<Integer> fullRows = new ArrayList<>();
    for (int row = 0; row < HEIGHT; row++) {
      boolean full = true;
      for (int col = 0; col < WIDTH; col++) {
        if (boardBeforeClear[row][col] != 1) {
          full = false;
          break;
        }
      }
      if (full) {
        fullRows.add(row);
      }
    }

    // 블록의 셀 중 삭제된 줄에 있는 셀 수 계산
    int usefulBlocks = 0;
    for (int bx = 0; bx < block.width(); bx++) {
      for (int by = 0; by < block.height(); by++) {
        if (block.getShape(bx, by) == 1) {
          int blockY = y + by;
          if (fullRows.contains(blockY)) {
            usefulBlocks++;
          }
        }
      }
    }

    return usefulBlocks;
  }

  /**
   * El-Tetris: Column Transitions 계산
   * 세로 방향으로 빈칸↔채움 전환 횟수
   */
  private int countColumnTransitions(int[][] board) {
    int transitions = 0;
    int highestRow = findHighestRow(board);

    if (highestRow < 0) {
      return 0;
    }

    for (int x = 0; x < WIDTH; x++) {
      boolean last = true; // 위쪽 경계는 채워져 있다고 가정
      for (int y = 0; y <= highestRow; y++) {
        boolean now = (board[y][x] == 1);
        if (last != now) {
          transitions++;
        }
        last = now;
      }
      if (!last) { // 아래쪽 경계 전환
        transitions++;
      }
    }

    return transitions;
  }

  /**
   * El-Tetris: Well Sums 계산
   * Well은 양쪽이 채워져 있고 가운데가 비어있는 연속된 빈칸
   * Well의 깊이 합을 계산
   */
  private int calculateWellSums(int[][] board) {
    int wellSums = 0;
    int highestRow = findHighestRow(board);

    if (highestRow < 0) {
      return 0;
    }

    for (int x = 0; x < WIDTH; x++) {
      int wellDepth = 0;
      for (int y = 0; y <= highestRow; y++) {
        // Well 조건: 현재 칸이 비어있고, 양쪽이 채워져 있음
        boolean isEmpty = (board[y][x] != 1);
        boolean leftFilled = (x == 0) || (board[y][x - 1] == 1);
        boolean rightFilled = (x == WIDTH - 1) || (board[y][x + 1] == 1);

        if (isEmpty && leftFilled && rightFilled) {
          wellDepth++;
        } else {
          // Well이 끊기면 깊이를 합산하고 리셋
          if (wellDepth > 0) {
            wellSums += wellDepth * (wellDepth + 1) / 2; // Well 깊이의 합 (1+2+3+...)
            wellDepth = 0;
          }
        }
      }
      // 마지막 Well 처리
      if (wellDepth > 0) {
        wellSums += wellDepth * (wellDepth + 1) / 2;
      }
    }

    return wellSums;
  }

  /**
   * 완성된 줄 개수 계산
   * BoardManager.clearLines()와 동일한 로직 사용
   */
  private int countFullRows(int[][] board) {
    int count = 0;
    for (int y = 0; y < HEIGHT; y++) { // N_BUFFER_LINES 제거: BoardManager와 동일하게 전체 보드 체크
      boolean full = true;
      for (int x = 0; x < WIDTH; x++) {
        if (board[y][x] != 1) { // BoardManager와 동일: != 1 체크 (고정된 블록만)
          full = false;
          break;
        }
      }
      if (full) {
        count++;
      }
    }
    return count;
  }

  /**
   * 가장 높은 행 찾기
   */
  private int findHighestRow(int[][] board) {
    for (int y = 0; y < HEIGHT; y++) { // N_BUFFER_LINES 제거: BoardManager와 동일하게 전체 보드 체크
      for (int x = 0; x < WIDTH; x++) {
        if (board[y][x] == 1) { // == 1로 통일: 고정된 블록만 체크
          return y;
        }
      }
    }
    return -1;
  }

  /**
   * 게임 오버 체크
   * 맨 위 줄(y=0)에 고정된 블록이 있으면 게임 오버
   * BoardManager의 게임 오버 로직과 동일
   */
  private boolean isGameOver(int[][] board) {
    for (int x = 0; x < WIDTH; x++) {
      if (board[0][x] == 1) { // 맨 위 줄에 고정된 블록이 있으면 게임 오버
        return true;
      }
    }
    return false;
  }

  /**
   * 블록을 보드에 배치할 수 있는지 확인
   * BoardManager.canMove()와 동일한 로직 사용
   */
  private boolean canPlaceBlock(Block block, int x, int y, int[][] board) {
    // 경계 검사
    if (x < 0 || x + block.width() > WIDTH || y + block.height() > HEIGHT) {
      return false;
    }

    // 상단 경계는 허용 (블록이 위에서 시작할 수 있도록)
    if (y < 0) {
      for (int bx = 0; bx < block.width(); bx++) {
        for (int by = 0; by < block.height(); by++) {
          if (block.getShape(bx, by) == 1 && y + by >= 0) {
            if (board[y + by][x + bx] == 1) { // == 1로 통일: 고정된 블록만 체크
              return false;
            }
          }
        }
      }
      return true;
    }

    // 고정된 블록과의 충돌 검사 (BoardManager와 동일)
    for (int bx = 0; bx < block.width(); bx++) {
      for (int by = 0; by < block.height(); by++) {
        if (block.getShape(bx, by) == 1) {
          int boardX = x + bx;
          int boardY = y + by;
          if (board[boardY][boardX] == 1) { // == 1로 통일: 고정된 블록만 체크
            return false; // 이미 고정된 블록이 있음
          }
        }
      }
    }
    return true;
  }

  /**
   * 블록을 보드에 배치
   */
  private void placeBlockOnBoard(Block block, int x, int y, int[][] board) {
    for (int bx = 0; bx < block.width(); bx++) {
      for (int by = 0; by < block.height(); by++) {
        if (block.getShape(bx, by) == 1) {
          int boardX = x + bx;
          int boardY = y + by;
          if (boardY >= 0 && boardY < HEIGHT && boardX >= 0 && boardX < WIDTH) {
            board[boardY][boardX] = 1;
          }
        }
      }
    }
  }

  /**
   * 줄 삭제 시뮬레이션
   * BoardManager.clearLines()와 동일한 로직 사용
   * y=0이 위쪽, y=HEIGHT-1이 아래쪽 (BoardManager와 동일한 좌표계)
   */
  private int[][] simulateLineClear(int[][] board) {
    // 완성된 줄 찾기 (BoardManager와 동일: 전체 보드 체크)
    List<Integer> fullRows = new ArrayList<>();
    for (int y = 0; y < HEIGHT; y++) { // N_BUFFER_LINES 제거: BoardManager와 동일하게 전체 보드 체크
      boolean full = true;
      for (int x = 0; x < WIDTH; x++) {
        if (board[y][x] != 1) { // BoardManager와 동일: != 1 체크 (고정된 블록만)
          full = false;
          break;
        }
      }
      if (full) {
        fullRows.add(y);
      }
    }

    // 완성된 줄이 없으면 원본 보드 반환
    if (fullRows.isEmpty()) {
      return board;
    }

    // 줄 삭제 시뮬레이션 (BoardManager와 동일한 로직)
    // 아래에서 위로 읽으면서, 삭제되지 않은 줄을 아래에서 위로 채움
    int[][] newBoard = new int[board.length][board[0].length];
    int writeRow = HEIGHT - 1; // 아래쪽부터 채움

    // 아래에서 위로 읽기 (HEIGHT-1부터 0까지)
    for (int readRow = HEIGHT - 1; readRow >= 0; readRow--) {
      if (!fullRows.contains(readRow)) {
        // 삭제되지 않은 줄을 아래에서 위로 복사
        System.arraycopy(board[readRow], 0, newBoard[writeRow], 0, WIDTH);
        writeRow--;
      }
    }

    // 남은 위쪽 공간을 빈 공간(0)으로 채움 (BoardManager와 동일)
    for (int r = writeRow; r >= 0; r--) {
      for (int c = 0; c < WIDTH; c++) {
        newBoard[r][c] = 0;
      }
    }

    return newBoard;
  }

  /**
   * 보드 복사
   */
  private int[][] copyBoard(int[][] board) {
    int[][] newBoard = new int[board.length][];
    for (int i = 0; i < board.length; i++) {
      newBoard[i] = board[i].clone();
    }
    return newBoard;
  }

  /**
   * 무브 정보를 담는 클래스
   */
  private static class Move {
    int x;
    int rotation;

    Move(int x, int rotation) {
      this.x = x;
      this.rotation = rotation;
    }
  }

  /**
   * 무브와 점수를 담는 클래스
   */
  private static class MoveScore {
    Move move;
    int[][] board;
    double score;

    MoveScore(Move move, int[][] board, double score) {
      this.move = move;
      this.board = board;
      this.score = score;
    }
  }

  /**
   * 시뮬레이션 결과를 담는 클래스
   */
  private static class SimulationResult {
    int[][] board;
    int linesCleared;
    int landingHeight; // El-Tetris: 블록이 착지한 높이
    int usefulBlocks; // El-Tetris: 삭제된 줄에 기여한 블록 셀 수

    SimulationResult(int[][] board, int linesCleared, int landingHeight, int usefulBlocks) {
      this.board = board;
      this.linesCleared = linesCleared;
      this.landingHeight = landingHeight;
      this.usefulBlocks = usefulBlocks;
    }
  }
}
