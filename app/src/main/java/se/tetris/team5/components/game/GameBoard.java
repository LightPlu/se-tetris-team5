package se.tetris.team5.components.game;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * 게임 보드를 그래픽으로 표시하는 JPanel 기반 컴포넌트
 */

public class GameBoard extends JPanel {
    private static final long serialVersionUID = 1L;
    public static final int HEIGHT = 20;
    public static final int WIDTH = 10;
    public static final int CELL_SIZE = 28; // 셀 크기(px)
    public static final int BORDER_SIZE = 2; // 셀 테두리 두께
    public static final int OUTER_BORDER = 8; // 바깥쪽 테두리 두께

    private BoardManager boardManager;

    public GameBoard(BoardManager boardManager) {
        System.out.println("[DEBUG] GameBoard constructor called. boardManager=" + (boardManager == null ? "null" : "ok"));
        this.boardManager = boardManager;
        setPreferredSize(new Dimension(
            (WIDTH * CELL_SIZE) + OUTER_BORDER * 2,
            (HEIGHT * CELL_SIZE) + OUTER_BORDER * 2
        ));
        setBackground(new Color(18, 22, 40)); // 어두운 남색 계열
        setFocusable(true);
        System.out.println("[DEBUG] GameBoard setPreferredSize and setBackground done");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("[DEBUG] paintComponent called. boardManager=" + (boardManager == null ? "null" : "ok") + ", parent=" + getParent());
        if (boardManager == null) {
            System.out.println("[DEBUG] paintComponent: boardManager is null, skipping draw");
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 바깥쪽 테두리
        g2.setColor(new Color(60, 70, 120));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

        // 내부 배경
        g2.setColor(new Color(18, 22, 40));
        g2.fillRect(OUTER_BORDER, OUTER_BORDER, WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE);

        // 셀 그리기
        int[][] board = boardManager.getBoard();
        Color[][] colors = boardManager.getBoardColors();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int cell = board[y][x];
                int px = OUTER_BORDER + x * CELL_SIZE;
                int py = OUTER_BORDER + y * CELL_SIZE;

                // 셀 배경
                if (cell == 0) {
                    g2.setColor(new Color(30, 35, 60));
                } else {
                    g2.setColor(colors[y][x] != null ? colors[y][x] : Color.YELLOW);
                }
                g2.fillRoundRect(px, py, CELL_SIZE, CELL_SIZE, 8, 8);

                // 셀 테두리
                g2.setColor(new Color(80, 90, 150));
                g2.setStroke(new java.awt.BasicStroke(BORDER_SIZE));
                g2.drawRoundRect(px, py, CELL_SIZE, CELL_SIZE, 8, 8);
            }
        }

        // 보드 외곽선 강조
        g2.setColor(new Color(255, 215, 0)); // 노란색 포인트
        g2.setStroke(new java.awt.BasicStroke(3f));
        g2.drawRoundRect(OUTER_BORDER - 2, OUTER_BORDER - 2, WIDTH * CELL_SIZE + 4, HEIGHT * CELL_SIZE + 4, 16, 16);
    }

    public int getBoardHeight() {
        return HEIGHT;
    }
    public int getBoardWidth() {
        return WIDTH;
    }
}