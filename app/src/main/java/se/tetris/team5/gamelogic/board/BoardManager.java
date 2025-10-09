package se.tetris.team5.gamelogic.board;

import java.awt.Color;
import se.tetris.team5.blocks.Block;

public class BoardManager {
    private int[][] board;
    private Color[][] boardColors;
    private final int HEIGHT;
    private final int WIDTH;
    
    public BoardManager(int height, int width) {
        this.HEIGHT = height;
        this.WIDTH = width;
        reset();
    }
    
    public void reset() {
        board = new int[HEIGHT][WIDTH];
        boardColors = new Color[HEIGHT][WIDTH];
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    public Color[][] getBoardColors() {
        return boardColors;
    }
    
    public void placeBlock(Block block, int x, int y) {
        for (int j = 0; j < block.height(); j++) {
            for (int i = 0; i < block.width(); i++) {
                if (y + j >= 0 && y + j < HEIGHT && x + i >= 0 && x + i < WIDTH) {
                    if (block.getShape(i, j) == 1) {
                        board[y + j][x + i] = 2;
                        boardColors[y + j][x + i] = block.getColor();
                    }
                }
            }
        }
    }
    
    public void eraseBlock(Block block, int x, int y) {
        for (int i = x; i < x + block.width(); i++) {
            for (int j = y; j < y + block.height(); j++) {
                if (block.getShape(i - x, j - y) == 1) {
                    if (j >= 0 && j < HEIGHT && i >= 0 && i < WIDTH) {
                        if (board[j][i] == 2) {
                            board[j][i] = 0;
                            boardColors[j][i] = null;
                        }
                    }
                }
            }
        }
    }
    
    public void fixBlock(Block block, int x, int y) {
        for (int i = 0; i < block.width(); i++) {
            for (int j = 0; j < block.height(); j++) {
                if (block.getShape(i, j) == 1 && y + j >= 0 && y + j < HEIGHT && x + i >= 0 && x + i < WIDTH) {
                    board[y + j][x + i] = 1;
                    boardColors[y + j][x + i] = block.getColor();
                }
            }
        }
    }
    
    public boolean canMove(int newX, int newY, Block block) {
        if (newX < 0 || newX + block.width() > WIDTH || newY + block.height() > HEIGHT) {
            return false;
        }
        
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
        
        for (int i = 0; i < block.width(); i++) {
            for (int j = 0; j < block.height(); j++) {
                if (block.getShape(i, j) == 1) {
                    if (board[newY + j][newX + i] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public int clearLines() {
        int clearedLinesCount = 0;
        
        for (int row = HEIGHT - 1; row >= 0; row--) {
            boolean fullLine = true;
            for (int col = 0; col < WIDTH; col++) {
                if (board[row][col] != 1) {
                    fullLine = false;
                    break;
                }
            }
            
            if (fullLine) {
                clearedLinesCount++;
                
                for (int moveRow = row; moveRow > 0; moveRow--) {
                    for (int col = 0; col < WIDTH; col++) {
                        board[moveRow][col] = board[moveRow - 1][col];
                        boardColors[moveRow][col] = boardColors[moveRow - 1][col];
                    }
                }
                
                for (int col = 0; col < WIDTH; col++) {
                    board[0][col] = 0;
                    boardColors[0][col] = null;
                }
                
                row++;
            }
        }
        
        return clearedLinesCount;
    }
    
    public int getHeight() {
        return HEIGHT;
    }
    
    public int getWidth() {
        return WIDTH;
    }
}
