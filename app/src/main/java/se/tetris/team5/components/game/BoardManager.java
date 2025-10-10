package se.tetris.team5.components.game;

import java.awt.Color;
import se.tetris.team5.blocks.Block;

/**
 * 게임 보드의 상태 관리 및 블록 배치/제거 등을 담당하는 클래스
 */
public class BoardManager {

  // 보드 크기 상수
  public static final int HEIGHT = 20;
  public static final int WIDTH = 10;
  public static final char BORDER_CHAR = 'X';

  // 보드 상태와 색상 정보
  private int[][] board;
  private Color[][] boardColors;

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
  public void fixBlock(Block block, int x, int y) {
    for (int i = 0; i < block.width(); i++) {
      for (int j = 0; j < block.height(); j++) {
        if (block.getShape(i, j) == 1 && y + j >= 0 && y + j < HEIGHT && x + i >= 0 && x + i < WIDTH) {
          board[y + j][x + i] = 1; // 고정된 블록은 값 1
          boardColors[y + j][x + i] = block.getColor();
        }
      }
    }
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

  /**
   * 가득 찬 줄을 제거하고 위의 줄들을 아래로 내립니다
   * 
   * @return 제거된 줄 수
   */
  public int clearLines() {
    int clearedLinesCount = 0;

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

    return clearedLinesCount;
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