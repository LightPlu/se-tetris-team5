package se.tetris.team5.gamelogic.block;

import se.tetris.team5.blocks.*;

public class BlockRotationManager {

  /**
   * Wall Kick 결과를 저장하는 내부 클래스
   */
  public static class WallKickResult {
    public final boolean success;
    public final int offsetX;
    public final int offsetY;
    
    public WallKickResult(boolean success, int offsetX, int offsetY) {
      this.success = success;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
    }
  }

  /**
   * Wall Kick을 적용하여 블록을 회전시킵니다.
   * 성공 시 적용된 오프셋을 반환합니다.
   */
  public WallKickResult rotateBlockWithWallKick(Block block, int x, int y, int[][] board) {
    Block originalBlock = copyBlock(block);

    block.rotate();

    int[][] wallKickOffsets = {
        { 0, 0 }, { -1, 0 }, { 1, 0 }, { 0, -1 },
        { -3, 0 }, { 2, 0 }, { 0, 1 },
        { -1, -1 }, { 1, -1 }
    };

    for (int[] offset : wallKickOffsets) {
      int testX = x + offset[0];
      int testY = y + offset[1];

      if (canPlaceBlock(block, testX, testY, board)) {
        // 성공: 적용된 오프셋 반환
        return new WallKickResult(true, offset[0], offset[1]);
      }
    }

    // 회전 실패시 원래 상태로 복원
    restoreBlock(block, originalBlock);
    return new WallKickResult(false, 0, 0);
  }

  public Block copyBlock(Block original) {
    Block copy = null;

    if (original instanceof IBlock)
      copy = new IBlock();
    else if (original instanceof JBlock)
      copy = new JBlock();
    else if (original instanceof LBlock)
      copy = new LBlock();
    else if (original instanceof OBlock)
      copy = new OBlock();
    else if (original instanceof SBlock)
      copy = new SBlock();
    else if (original instanceof TBlock)
      copy = new TBlock();
    else if (original instanceof ZBlock)
      copy = new ZBlock();
    else if (original instanceof WBlock)
      copy = new WBlock();

    if (copy != null) {
      for (int rotations = 0; rotations < 4; rotations++) {
        if (isSameShape(copy, original)) {
          break;
        }
        copy.rotate();
      }
    }

    return copy;
  }

  private void restoreBlock(Block block, Block original) {
    for (int rotations = 0; rotations < 4; rotations++) {
      if (isSameShape(block, original)) {
        break;
      }
      block.rotate();
    }
  }

  private boolean isSameShape(Block block1, Block block2) {
    if (block1.width() != block2.width() || block1.height() != block2.height()) {
      return false;
    }

    for (int i = 0; i < block1.width(); i++) {
      for (int j = 0; j < block1.height(); j++) {
        if (block1.getShape(i, j) != block2.getShape(i, j)) {
          return false;
        }
      }
    }

    return true;
  }

  private boolean canPlaceBlock(Block block, int x, int y, int[][] board) {
    int HEIGHT = board.length;
    int WIDTH = board[0].length;

    if (x < 0 || x + block.width() > WIDTH || y + block.height() > HEIGHT) {
      return false;
    }

    if (y < 0) {
      for (int i = 0; i < block.width(); i++) {
        for (int j = 0; j < block.height(); j++) {
          if (block.getShape(i, j) == 1 && y + j >= 0) {
            if (board[y + j][x + i] == 1) {
              return false;
            }
          }
        }
      }
      return true;
    }

    for (int i = 0; i < block.width(); i++) {
      for (int j = 0; j < block.height(); j++) {
        if (block.getShape(i, j) == 1) {
          if (board[y + j][x + i] == 1) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
