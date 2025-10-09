package se.tetris.team5.components.game;

import java.awt.Color;

/**
 * 게임 보드의 테두리, 위치, 크기만을 관리하는 간단한 클래스
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
     * @return 보드 배열
     */
    public int[][] getBoard() {
        return board;
    }
    
    /**
     * 보드 색상 배열을 반환합니다
     * @return 보드 색상 배열
     */
    public Color[][] getBoardColors() {
        return boardColors;
    }
    
    /**
     * 보드의 테두리를 포함한 전체 너비를 반환합니다
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
     * @return 테두리 문자열
     */
    public String createBorderLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getTotalWidth(); i++) {
            sb.append(BORDER_CHAR);
        }
        return sb.toString();
    }
}