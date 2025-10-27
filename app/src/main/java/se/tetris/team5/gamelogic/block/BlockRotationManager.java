package se.tetris.team5.gamelogic.block;

import se.tetris.team5.blocks.*;

public class BlockRotationManager {

  public boolean rotateBlockWithWallKick(Block block, int x, int y, int[][] board) {
  Block originalBlock = copyBlock(block);
  int prevState = block.getRotationState();
  block.rotate();
  int nextState = block.getRotationState();

  // SRS Wall Kick 데이터 (일반 블록)
  int[][][] srsNormal = {
    // 0->R, R->0, R->2, 2->R, 2->L, L->2, L->0, 0->L
    { {0,0},{-1,0},{-1,1},{0,-2},{-1,-2} }, // 0->R
    { {0,0},{1,0},{1,1},{0,-2},{1,-2} },   // R->0
    { {0,0},{1,0},{1,-1},{0,2},{1,2} },    // R->2
    { {0,0},{-1,0},{-1,-1},{0,2},{-1,2} }, // 2->R
    { {0,0},{1,0},{1,1},{0,-2},{1,-2} },   // 2->L
    { {0,0},{-1,0},{-1,1},{0,-2},{-1,-2} },// L->2
    { {0,0},{-1,0},{-1,-1},{0,2},{-1,2} }, // L->0
    { {0,0},{1,0},{1,-1},{0,2},{1,2} }     // 0->L
  };
  // SRS Wall Kick 데이터 (I블록)
  int[][][] srsI = {
    { {0,0},{-2,0},{1,0},{-2,-1},{1,2} },  // 0->R
    { {0,0},{2,0},{-1,0},{2,1},{-1,-2} },  // R->0
    { {0,0},{-1,0},{2,0},{-1,2},{2,-1} },  // R->2
    { {0,0},{1,0},{-2,0},{1,-2},{-2,1} },  // 2->R
    { {0,0},{2,0},{-1,0},{2,1},{-1,-2} },  // 2->L
    { {0,0},{-2,0},{1,0},{-2,-1},{1,2} },  // L->2
    { {0,0},{1,0},{-2,0},{1,-2},{-2,1} },  // L->0
    { {0,0},{-1,0},{2,0},{-1,2},{2,-1} }   // 0->L
  };

  // 회전 방향 인덱스 계산
  int dir = -1;
  if (prevState == 0 && nextState == 1) dir = 0; // 0->R
  else if (prevState == 1 && nextState == 0) dir = 1; // R->0
  else if (prevState == 1 && nextState == 2) dir = 2; // R->2
  else if (prevState == 2 && nextState == 1) dir = 3; // 2->R
  else if (prevState == 2 && nextState == 3) dir = 4; // 2->L
  else if (prevState == 3 && nextState == 2) dir = 5; // L->2
  else if (prevState == 3 && nextState == 0) dir = 6; // L->0
  else if (prevState == 0 && nextState == 3) dir = 7; // 0->L
  if (dir == -1) dir = 0; // fallback

  int[][] kicks;
  if (block instanceof IBlock) {
    kicks = srsI[dir];
  } else {
    kicks = srsNormal[dir];
  }

  for (int[] offset : kicks) {
    int testX = x + offset[0];
    int testY = y + offset[1];
    if (canPlaceBlock(block, testX, testY, board)) {
      return true;
    }
  }

  // 회전 실패시 원래 상태로 복원
  restoreBlock(block, originalBlock);
  block.setRotationState(prevState);
  return false;
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
