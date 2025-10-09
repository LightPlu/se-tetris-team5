package se.tetris.team5.gamelogic.movement;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.board.BoardManager;

public class MovementManager {
  private BoardManager boardManager;

  public MovementManager(BoardManager boardManager) {
    this.boardManager = boardManager;
  }

  public boolean moveDown(Block block, int x, int y) {
    return boardManager.canMove(x, y + 1, block);
  }

  public boolean moveLeft(Block block, int x, int y) {
    return boardManager.canMove(x - 1, y, block);
  }

  public boolean moveRight(Block block, int x, int y) {
    return boardManager.canMove(x + 1, y, block);
  }

  public int hardDrop(Block block, int x, int y) {
    int dropDistance = 0;
    int newY = y;

    while (boardManager.canMove(x, newY + 1, block)) {
      newY++;
      dropDistance++;
    }

    return dropDistance;
  }

  public int getDropPosition(Block block, int x, int y) {
    int newY = y;
    while (boardManager.canMove(x, newY + 1, block)) {
      newY++;
    }
    return newY;
  }

  /**
   * 주어진 위치로 블록이 이동 가능한지 확인합니다
   */
  public boolean canMoveToPosition(Block block, int x, int y) {
    return boardManager.canMove(x, y, block);
  }
}