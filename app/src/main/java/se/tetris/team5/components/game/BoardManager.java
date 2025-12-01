package se.tetris.team5.components.game;

import java.awt.Color;
import se.tetris.team5.blocks.Block;

/**
 * 게임 보드의 상태 관리 및 블록 배치/제거 등을 담당하는 클래스
 */
public class BoardManager {
  /**
   * (x, y) 좌표의 아이템을 반환합니다 (없으면 null)
   */
  public se.tetris.team5.items.Item getBoardItem(int x, int y) {
    if (y >= 0 && y < HEIGHT && x >= 0 && x < WIDTH) {
      return boardItems[y][x];
    }
    return null;
  }

  // 보드 크기 상수
  public static final int HEIGHT = 20;
  public static final int WIDTH = 10;
  public static final char BORDER_CHAR = 'X';

  // 보드 상태와 색상 정보
  private int[][] board;
  private Color[][] boardColors;
  // 각 칸별 아이템 정보 (null이면 아이템 없음)
  private se.tetris.team5.items.Item[][] boardItems;

  /**
   * BoardManager 생성자
   */
  public BoardManager() {
    initializeBoard();
  }

  /**
   * 보드를 초기화합니다
   */
  public void initializeBoard() {
    board = new int[HEIGHT][WIDTH];
    boardColors = new Color[HEIGHT][WIDTH];
    boardItems = new se.tetris.team5.items.Item[HEIGHT][WIDTH];
  }

  /**
   * 보드 배열을 반환합니다
   * 
   * @return 보드 배열
   */
  public int[][] getBoard() {
    return board;
  }

  /**
   * 보드 색상 배열을 반환합니다
   * 
   * @return 보드 색상 배열
   */
  public Color[][] getBoardColors() {
    return boardColors;
  }

  /**
   * 보드의 테두리를 포함한 전체 너비를 반환합니다
   * 
   * @return 테두리 포함 너비
   */
  public int getTotalWidth() {
    return WIDTH + 2; // 좌우 테두리 포함
  }

  /**
   * 보드를 리셋합니다
   */
  public void reset() {
    initializeBoard();
  }

  /**
   * 보드의 텍스트 표현을 위한 테두리 문자열을 생성합니다
   * 
   * @return 테두리 문자열
   */
  public String createBorderLine() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < getTotalWidth(); i++) {
      sb.append(BORDER_CHAR);
    }
    return sb.toString();
  }

  /**
   * 블록을 보드에 배치합니다 (움직이는 블록)
   */
  public void placeBlock(Block block, int x, int y) {
    for (int j = 0; j < block.height(); j++) {
      for (int i = 0; i < block.width(); i++) {
        if (y + j >= 0 && y + j < HEIGHT && x + i >= 0 && x + i < WIDTH) {
          if (block.getShape(i, j) == 1) {
            board[y + j][x + i] = 2; // 움직이는 블록은 값 2
            boardColors[y + j][x + i] = block.getColor();
          }
        }
      }
    }
  }

  /**
   * 블록을 보드에서 제거합니다
   */
  public void eraseBlock(Block block, int x, int y) {
    for (int i = x; i < x + block.width(); i++) {
      for (int j = y; j < y + block.height(); j++) {
        if (block.getShape(i - x, j - y) == 1) {
          if (j >= 0 && j < HEIGHT && i >= 0 && i < WIDTH) {
            if (board[j][i] == 2) { // 움직이는 블록만 제거
              board[j][i] = 0;
              boardColors[j][i] = null;
            }
          }
        }
      }
    }
  }

  /**
   * 블록을 보드에 고정합니다 (고정된 블록)
   */
  /**
   * 블록을 보드에 고정합니다 (고정된 블록)
   * 
   * @param removedItems 이 리스트에 삭제로 인해 제거된 아이템들을 추가합니다 (null 허용)
   * @return 줄삭제 아이템으로 지워진 블럭 수 (점수 계산에 사용)
   */
  public int fixBlock(Block block, int x, int y, java.util.List<se.tetris.team5.items.Item> removedItems) {
    // 고정되는 블럭의 위치 저장 (대전모드 공격 블럭 계산용)
    lastFixedBlockPositions.clear();
    for (int i = 0; i < block.width(); i++) {
      for (int j = 0; j < block.height(); j++) {
        if (block.getShape(i, j) == 1) {
          int boardX = x + i;
          int boardY = y + j;
          if (boardX >= 0 && boardX < WIDTH && boardY >= 0 && boardY < HEIGHT) {
            lastFixedBlockPositions.add(boardX + "," + boardY);
          }
        }
      }
    }
    
    // 무게추 블록 특수 처리: WBlock인 경우, 해당 블록이 차지하는 열들의 고정 블록들을 모두 제거
    // 그리고 블록을 가장 아래에 고정시킨다.
    if (block instanceof se.tetris.team5.blocks.WBlock) {
      // 대상 열 집합 수집
      java.util.Set<Integer> colsToClear = new java.util.HashSet<>();
      for (int i = 0; i < block.width(); i++) {
        for (int j = 0; j < block.height(); j++) {
          if (block.getShape(i, j) == 1) {
            int col = x + i;
            if (col >= 0 && col < WIDTH) {
              colsToClear.add(col);
            }
          }
        }
      }

      // 각 열에 대해 바닥(아래)부터 위로 모든 고정 블록 및 아이템 제거
      for (int col : colsToClear) {
        for (int row = HEIGHT - 1; row >= 0; row--) {
          board[row][col] = 0;
          boardColors[row][col] = null;
          boardItems[row][col] = null;
        }
      }

      // 블록을 가장 아래에 고정
      int baseY = HEIGHT - block.height();
      for (int i = 0; i < block.width(); i++) {
        for (int j = 0; j < block.height(); j++) {
          if (block.getShape(i, j) == 1) {
            int boardX = x + i;
            int boardY = baseY + j;
            if (boardY >= 0 && boardY < HEIGHT && boardX >= 0 && boardX < WIDTH) {
              board[boardY][boardX] = 1;
              boardColors[boardY][boardX] = block.getColor();
              boardItems[boardY][boardX] = block.getItem(i, j);
            }
          }
        }
      }

      return 0; // 특수 처리 후 종료 (줄삭제 아이템 없음)
    }

    if (block instanceof se.tetris.team5.blocks.DotBlock) {
      // 폭탄 블록(DotBlock) 특수 처리: 블록의 각 칸 위치를 중심으로 3x3 범위 폭발
      System.out.println("[폭탄 블록] DotBlock 고정 - 폭발 시작!");

      // 폭발로 사라진 셀들의 좌표 초기화
      lastBombExplosionCells.clear();

      // 먼저 폭탄 블록 자체를 보드에서 제거 (움직이는 블록 상태 제거)
      for (int i = 0; i < block.width(); i++) {
        for (int j = 0; j < block.height(); j++) {
          if (block.getShape(i, j) == 1) {
            int boardX = x + i;
            int boardY = y + j;
            if (boardX >= 0 && boardX < WIDTH && boardY >= 0 && boardY < HEIGHT) {
              if (board[boardY][boardX] == 2) { // 움직이는 블록(값 2) 제거
                board[boardY][boardX] = 0;
                boardColors[boardY][boardX] = null;
                boardItems[boardY][boardX] = null;
              }
            }
          }
        }
      }

      // 블록의 각 칸 위치에서 폭발 실행하고 제거된 블럭 수 카운트
      int totalExplodedBlocks = 0;
      for (int i = 0; i < block.width(); i++) {
        for (int j = 0; j < block.height(); j++) {
          if (block.getShape(i, j) == 1) {
            int centerX = x + i;
            int centerY = y + j;

            // 각 칸을 중심으로 3x3 범위 폭발하고 제거된 블럭 수 합산
            totalExplodedBlocks += explodeArea(centerX, centerY);
          }
        }
      }

      System.out.println("[폭탄 블록] 폭발 완료! 총 " + totalExplodedBlocks + "개 블록 제거됨");
      return totalExplodedBlocks; // 폭발로 제거된 블럭 수 반환
    }

    // 기본 고정 처리 (기존 동작)
    java.util.Set<Integer> lineClearRows = new java.util.HashSet<>();
    for (int i = 0; i < block.width(); i++) {
      for (int j = 0; j < block.height(); j++) {
        if (block.getShape(i, j) == 1 && y + j >= 0 && y + j < HEIGHT && x + i >= 0 && x + i < WIDTH) {
          board[y + j][x + i] = 1; // 고정된 블록은 값 1
          boardColors[y + j][x + i] = block.getColor();
          se.tetris.team5.items.Item item = block.getItem(i, j);
          boardItems[y + j][x + i] = item;
          // 줄삭제 아이템이 있으면 해당 줄을 기록
          if (item instanceof se.tetris.team5.items.LineClearItem) {
            lineClearRows.add(y + j);
          }
        }
      }
    }
    // 줄삭제 아이템이 있는 줄을 즉시 삭제 (가득 차지 않아도)
    int removedBlockCount = 0; // 줄삭제로 지워진 블럭 수
    if (!lineClearRows.isEmpty()) {
      // 내림차순 정렬(아래줄부터 삭제해야 인덱스 꼬임 방지)
      java.util.List<Integer> sortedRows = new java.util.ArrayList<>(lineClearRows);
      sortedRows.sort(java.util.Collections.reverseOrder());
      for (int row : sortedRows) {
        // 줄에서 지워질 블럭 수 카운트 (고정된 블럭만)
        for (int col = 0; col < WIDTH; col++) {
          if (board[row][col] == 1) {
            removedBlockCount++;
          }
        }
        // 수집: 삭제되는 줄의 아이템들을 removedItems에 추가
        if (removedItems != null) {
          for (int col = 0; col < WIDTH; col++) {
            se.tetris.team5.items.Item removed = boardItems[row][col];
            if (removed != null)
              removedItems.add(removed);
          }
        }
        // 줄 삭제: 위의 줄을 한 칸씩 내림
        for (int moveRow = row; moveRow > 0; moveRow--) {
          for (int col = 0; col < WIDTH; col++) {
            board[moveRow][col] = board[moveRow - 1][col];
            boardColors[moveRow][col] = boardColors[moveRow - 1][col];
            boardItems[moveRow][col] = boardItems[moveRow - 1][col];
          }
        }
        // 맨 위 줄은 빈 줄로 만듦
        for (int col = 0; col < WIDTH; col++) {
          board[0][col] = 0;
          boardColors[0][col] = null;
          boardItems[0][col] = null;
        }
      }
    }
    return removedBlockCount; // 줄삭제 아이템으로 지워진 블럭 수 반환
  }

  /**
   * 각 칸의 아이템 정보를 반환합니다
   */
  public se.tetris.team5.items.Item[][] getBoardItems() {
    return boardItems;
  }

  /**
   * 블록이 주어진 위치로 이동할 수 있는지 확인합니다
   */
  public boolean canMove(int newX, int newY, Block block) {
    // 경계 검사
    if (newX < 0 || newX + block.width() > WIDTH || newY + block.height() > HEIGHT) {
      return false;
    }

    // 상단 경계는 허용 (블록이 위에서 시작할 수 있도록)
    if (newY < 0) {
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
        if (block.getShape(i, j) == 1) {
          if (board[newY + j][newX + i] == 1) {
            return false; // 이미 고정된 블록이 있음
          }
        }
      }
    }
    return true;
  }

  // 마지막으로 삭제된 줄에 타임스톱 아이템이 있었는지 여부
  private boolean timeStopItemCleared = false;
  // last cleared rows from the most recent clear operation (0..HEIGHT-1)
  private java.util.List<Integer> lastClearedRows = new java.util.ArrayList<>();
  // 마지막으로 고정된 블럭의 위치들 (대전모드 공격 블럭 계산용)
  private java.util.Set<String> lastFixedBlockPositions = new java.util.HashSet<>();
  // 마지막 줄 삭제로 생성된 공격 블럭 데이터 (대전모드용)
  private java.util.List<Color[]> lastAttackBlocksData = new java.util.ArrayList<>();
  // 폭탄 폭발로 사라진 셀 좌표 (애니메이션용)
  private java.util.List<se.tetris.team5.components.game.GameBoard.CellPos> lastBombExplosionCells = new java.util.ArrayList<>();

  /**
   * 마지막 줄 삭제 시 타임스톱 아이템이 있었는지 반환하고 플래그 초기화
   */
  public boolean wasTimeStopItemCleared() {
    boolean result = timeStopItemCleared;
    timeStopItemCleared = false; // 플래그 초기화
    return result;
  }
  
  /**
   * 폭탄 폭발로 사라진 셀 좌표들을 반환합니다 (애니메이션용)
   */
  public java.util.List<se.tetris.team5.components.game.GameBoard.CellPos> getLastBombExplosionCells() {
    return new java.util.ArrayList<>(lastBombExplosionCells);
  }

  /**
   * 가득 찬 줄을 제거하고 위의 줄들을 아래로 내립니다
   * 
   * @return 제거된 줄 수
   */
  /**
   * 가득 찬 줄을 제거하고 위의 줄들을 아래로 내립니다
   * 
   * @param removedItems 삭제로 인해 제거된 아이템들을 추가할 리스트 (null 허용)
   * @return 제거된 줄 수
   */
  public int clearLines(java.util.List<se.tetris.team5.items.Item> removedItems) {
    // reset state
    timeStopItemCleared = false;
    lastClearedRows.clear();
    lastAttackBlocksData.clear();

    // collect full rows (fixed blocks only)
    for (int row = 0; row < HEIGHT; row++) {
      boolean fullLine = true;
      for (int col = 0; col < WIDTH; col++) {
        if (board[row][col] != 1) { fullLine = false; break; }
      }
      if (fullLine) {
        lastClearedRows.add(row);
        // collect removed items on that row
        if (removedItems != null) {
          for (int col = 0; col < WIDTH; col++) {
            se.tetris.team5.items.Item removed = boardItems[row][col];
            if (removed != null) removedItems.add(removed);
          }
        }
        // detect time-stop
        for (int col = 0; col < WIDTH; col++) {
          if (boardItems[row][col] instanceof se.tetris.team5.items.TimeStopItem) {
            timeStopItemCleared = true;
            System.out.println("[타임스톱 아이템 발견] 줄 삭제로 타임스톱 충전!");
          }
        }
      }
    }

    if (lastClearedRows.isEmpty()) return 0;

    // 대전모드: 2줄 이상 삭제 시 공격 블럭 데이터 계산
    if (lastClearedRows.size() >= 2) {
      calculateAttackBlocks();
    }

    // remove cleared rows by compressing into new arrays (bottom-up)
    java.util.Set<Integer> clearedSet = new java.util.HashSet<>(lastClearedRows);
    int[][] newBoard = new int[HEIGHT][WIDTH];
    Color[][] newBoardColors = new Color[HEIGHT][WIDTH];
    se.tetris.team5.items.Item[][] newBoardItems = new se.tetris.team5.items.Item[HEIGHT][WIDTH];

    int writeRow = HEIGHT - 1;
    for (int readRow = HEIGHT - 1; readRow >= 0; readRow--) {
      if (clearedSet.contains(readRow)) continue;
      for (int col = 0; col < WIDTH; col++) {
        newBoard[writeRow][col] = board[readRow][col];
        newBoardColors[writeRow][col] = boardColors[readRow][col];
        newBoardItems[writeRow][col] = boardItems[readRow][col];
      }
      writeRow--;
    }

    // fill remaining top rows with empty
    for (int r = writeRow; r >= 0; r--) {
      for (int c = 0; c < WIDTH; c++) {
        newBoard[r][c] = 0;
        newBoardColors[r][c] = null;
        newBoardItems[r][c] = null;
      }
    }

    // replace original boards
    this.board = newBoard;
    this.boardColors = newBoardColors;
    this.boardItems = newBoardItems;

    // Sort and log cleared rows for UI
    java.util.Collections.sort(lastClearedRows);
    System.out.println("[BoardManager DEBUG] clearedRows=" + lastClearedRows);

    return lastClearedRows.size();
  }

  /**
   * Return the last cleared rows (0..HEIGHT-1) from the most recent clearLines call.
   * The returned list is a copy to avoid external mutation.
   */
  public java.util.List<Integer> getLastClearedRows() {
    return new java.util.ArrayList<>(lastClearedRows);
  }

  /**
   * 대전모드: 삭제된 줄들의 블럭 데이터를 계산합니다 (방금 고정된 블럭 제외)
   * 2줄 이상 삭제 시에만 호출됩니다.
   */
  private void calculateAttackBlocks() {
    for (int row : lastClearedRows) {
      Color[] rowColors = new Color[WIDTH];
      for (int col = 0; col < WIDTH; col++) {
        String posKey = col + "," + row;
        // 방금 고정된 블럭의 위치가 아닌 경우만 공격 블럭으로 카운트
        if (!lastFixedBlockPositions.contains(posKey) && board[row][col] == 1) {
          rowColors[col] = boardColors[row][col];
        } else {
          rowColors[col] = null; // 고정된 블럭이거나 빈 칸
        }
      }
      lastAttackBlocksData.add(rowColors);
    }
    System.out.println("[대전모드 공격] " + lastClearedRows.size() + "줄 삭제, 공격 블럭 계산 완료");
  }

  /**
   * 대전모드: 마지막으로 계산된 공격 블럭 데이터를 반환합니다.
   * 각 줄은 Color[] 배열로 표현되며, null은 빈 칸을 의미합니다.
   * 
   * @return 공격 블럭 데이터 리스트 (각 요소는 한 줄의 블럭 색상 배열)
   */
  public java.util.List<Color[]> getAttackBlocksData() {
    return new java.util.ArrayList<>(lastAttackBlocksData);
  }

  /**
   * 대전모드: 공격 블럭을 보드 맨 밑에 추가합니다.
   * 기존 블럭들은 위로 밀려나고, 위로 밀려난 블럭이 화면 밖으로 나가면 게임 오버 가능성이 있습니다.
   * 
   * @param attackRows 추가할 공격 블럭 데이터 (각 Color[] 배열이 한 줄)
   * @return 추가 성공 여부 (false면 게임 오버 상황)
   */
  public boolean addAttackBlocksToBottom(java.util.List<Color[]> attackRows) {
    if (attackRows == null || attackRows.isEmpty()) {
      return true;
    }

    int numRowsToAdd = attackRows.size();
    
    // 기존 블럭들을 위로 밀어올림
    for (int row = 0; row < HEIGHT - numRowsToAdd; row++) {
      for (int col = 0; col < WIDTH; col++) {
        board[row][col] = board[row + numRowsToAdd][col];
        boardColors[row][col] = boardColors[row + numRowsToAdd][col];
        boardItems[row][col] = boardItems[row + numRowsToAdd][col];
      }
    }
    
    // 맨 밑에 공격 블럭 추가 (회색으로)
    Color attackColor = new Color(85, 85, 85); // 무게추 블럭과 동일한 색상
    for (int i = 0; i < numRowsToAdd; i++) {
      int rowIndex = HEIGHT - numRowsToAdd + i;
      Color[] rowData = attackRows.get(i);
      
      for (int col = 0; col < WIDTH; col++) {
        if (rowData[col] != null) {
          board[rowIndex][col] = 1; // 고정된 블럭
          boardColors[rowIndex][col] = attackColor;
          boardItems[rowIndex][col] = null; // 아이템 없음
        } else {
          board[rowIndex][col] = 0;
          boardColors[rowIndex][col] = null;
          boardItems[rowIndex][col] = null;
        }
      }
    }
    
    System.out.println("[공격 블럭 추가] " + numRowsToAdd + "줄이 맨 밑에 추가됨");
    
    // 맨 위 줄에 블럭이 있으면 게임 오버 위험 (하지만 즉시 게임 오버는 아님)
    boolean topRowHasBlock = false;
    for (int col = 0; col < WIDTH; col++) {
      if (board[0][col] == 1) {
        topRowHasBlock = true;
        break;
      }
    }
    
    if (topRowHasBlock) {
      System.out.println("[경고] 공격 블럭 추가로 인해 맨 위까지 블럭이 쌓임");
    }
    
    return true;
  }


  /**
   * 지정된 위치를 중심으로 3x3 범위의 블록을 폭발시킵니다.
   * 
   * @param centerX 폭발 중심 X 좌표
   * @param centerY 폭발 중심 Y 좌표
   * @return 제거된 셀의 개수
   */
  public int explodeArea(int centerX, int centerY) {
    int explodedCells = 0;

    // 3x3 범위 계산 (중심 기준 -1 ~ +1)
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        int targetX = centerX + dx;
        int targetY = centerY + dy;

        // 범위 체크
        if (targetX >= 0 && targetX < WIDTH && targetY >= 0 && targetY < HEIGHT) {
          if (board[targetY][targetX] == 1) { // 고정된 블록만 제거
            board[targetY][targetX] = 0;
            boardColors[targetY][targetX] = null;
            boardItems[targetY][targetX] = null;
            explodedCells++;
            
            // 폭발한 셀 좌표 기록 (애니메이션용)
            lastBombExplosionCells.add(new se.tetris.team5.components.game.GameBoard.CellPos(targetY, targetX));
          }
        }
      }
    }

    System.out.println("[폭발] (" + centerX + "," + centerY + ") 중심 3x3 범위, " + explodedCells + "개 블록 제거");
    return explodedCells;
  }

  /**
   * 보드의 높이를 반환합니다
   */
  public int getHeight() {
    return HEIGHT;
  }

  /**
   * 보드의 너비를 반환합니다
   */
  public int getWidth() {
    return WIDTH;
  }

  /**
   * 보드에서 가장 높은 블록의 위치를 반환합니다 (아래에서부터 센 줄 수)
   * 블록이 없으면 0을 반환합니다
   * 
   * @return 가장 높은 블록이 있는 줄 수 (1-based, 바닥부터 센 높이)
   */
  public int getHighestBlockRow() {
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        if (board[y][x] == 1) {
          // y=0이 맨 위, y=19가 맨 아래
          // 바닥부터 센 높이는 (HEIGHT - y)
          return HEIGHT - y;
        }
      }
    }
    return 0; // 블록이 없으면 0
  }
}