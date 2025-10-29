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
   */
  public void fixBlock(Block block, int x, int y, java.util.List<se.tetris.team5.items.Item> removedItems) {
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

      return; // 특수 처리 후 종료
    }

    if (block instanceof se.tetris.team5.blocks.DotBlock) {
      // 폭탄 블록(DotBlock) 특수 처리: 블록의 각 칸 위치를 중심으로 3x3 범위 폭발
      System.out.println("[폭탄 블록] DotBlock 고정 - 폭발 시작!");

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

      // 블록의 각 칸 위치에서 폭발 실행
      for (int i = 0; i < block.width(); i++) {
        for (int j = 0; j < block.height(); j++) {
          if (block.getShape(i, j) == 1) {
            int centerX = x + i;
            int centerY = y + j;

            // 각 칸을 중심으로 3x3 범위 폭발
            explodeArea(centerX, centerY);
          }
        }
      }

      System.out.println("[폭탄 블록] 폭발 완료! 폭탄 블록은 고정되지 않음");
      return; // 폭탄 블록 자체는 고정하지 않고 종료
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
    if (!lineClearRows.isEmpty()) {
      // 내림차순 정렬(아래줄부터 삭제해야 인덱스 꼬임 방지)
      java.util.List<Integer> sortedRows = new java.util.ArrayList<>(lineClearRows);
      sortedRows.sort(java.util.Collections.reverseOrder());
      for (int row : sortedRows) {
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

  /**
   * 마지막 줄 삭제 시 타임스톱 아이템이 있었는지 반환하고 플래그 초기화
   */
  public boolean wasTimeStopItemCleared() {
    boolean result = timeStopItemCleared;
    timeStopItemCleared = false; // 플래그 초기화
    return result;
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
    int clearedLinesCount = 0;
    timeStopItemCleared = false; // 매 clearLines 호출 시 초기화

    for (int row = HEIGHT - 1; row >= 0; row--) {
      // 현재 줄이 가득 찼는지 확인 (고정된 블록만 고려)
      boolean fullLine = true;
      for (int col = 0; col < WIDTH; col++) {
        if (board[row][col] != 1) { // 고정된 블록(값 1)만 고려
          fullLine = false;
        }
      }
      if (fullLine) {
        // 줄을 삭제하기 전에 타임스톱 아이템이 있는지 확인
        for (int col = 0; col < WIDTH; col++) {
          if (boardItems[row][col] instanceof se.tetris.team5.items.TimeStopItem) {
            timeStopItemCleared = true;
            System.out.println("[타임스톱 아이템 발견] 줄 삭제로 타임스톱 충전!");
          }
        }
        
        clearedLinesCount++;
        // 수집: 삭제되는 줄의 아이템들을 removedItems에 추가
        if (removedItems != null) {
          for (int col = 0; col < WIDTH; col++) {
            se.tetris.team5.items.Item removed = boardItems[row][col];
            if (removed != null)
              removedItems.add(removed);
          }
        }
        // 아래 줄을 한 칸씩 내림
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
        // 같은 줄을 다시 검사해야 하므로 row를 증가시킴
        row++;
      }
    }

    return clearedLinesCount;
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
}