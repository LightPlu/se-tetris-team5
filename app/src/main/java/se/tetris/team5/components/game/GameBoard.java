package se.tetris.team5.components.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.items.Item;
import se.tetris.team5.items.TimeStopItem;
import se.tetris.team5.items.BombItem;

/**
 * 게임 보드의 텍스트 기반 백업을 유지하면서, 실제 게임 화면 위에 블록형 그래픽을 그리는 컴포넌트.
 * 기존의 텍스트 API(setText/getStyledDocument 등)는 그대로 유지하되, paintComponent에서
 * 게임 상태를 시각적으로 렌더링합니다 (게임 로직은 건드리지 않음).
 */
public class GameBoard extends JTextPane {

    private static final long serialVersionUID = 1L;

    public static final int HEIGHT = 20;
    public static final int WIDTH = 10;
    public static final char BORDER_CHAR = 'X';

    private SimpleAttributeSet styleSet;

    // overlay state used for graphical rendering
    private int[][] overlayBoard;
    private Color[][] overlayColors;
    private Item[][] overlayItems;
    private Block currentBlock;
    private int currentX, currentY;
    // When true, the underlying JTextPane text will be painted on top of graphics.
    // Used for pause/menu messages and other text-only overlays.
    private boolean showTextOverlay = false;

    public GameBoard() {
        initComponents();
    }

    private void initComponents() {
        // Keep text-based API but hide default background so we can paint custom graphics
        setEditable(false);
        setOpaque(false);
        CompoundBorder border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 10),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        setBorder(border);

        // Document default style (kept for compatibility but not visually used)
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 18);
        StyleConstants.setFontFamily(styleSet, "Courier New");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
        StyleConstants.setLineSpacing(styleSet, -0.4f);

        // initialize empty overlay
        overlayBoard = new int[HEIGHT][WIDTH];
        overlayColors = new Color[HEIGHT][WIDTH];
        drawEmptyBoard();
    }

    /**
     * 업데이트: 게임 엔진으로부터 보드 데이터를 받아 그래픽으로 렌더하도록 저장합니다.
     * (game logic callers should invoke this after updating engine state)
     */
    public void renderBoard(int[][] board, Color[][] colors, Item[][] items, Block currBlock, int currX, int currY) {
        if (board != null) {
            // copy minimal state reference (don't mutate)
            overlayBoard = board;
            overlayColors = colors;
            overlayItems = items;
        }
        this.currentBlock = currBlock;
        this.currentX = currX;
        this.currentY = currY;
        repaint();
    }

    /**
     * Enable or disable rendering of the text content (JTextPane) on top of the graphics.
     * When enabled, paintComponent will call through to super.paintComponent(g) after drawing
     * the graphical board so text-based messages (pause menu, time-stop messages) remain visible.
     */
    public void setShowTextOverlay(boolean show) {
        this.showTextOverlay = show;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // draw custom background + grid + blocks
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw dark rounded background within border insets
        int w = getWidth();
        int h = getHeight();
        g2.setColor(new Color(18, 18, 24));
        g2.fillRoundRect(8, 8, Math.max(0, w-16), Math.max(0, h-16), 12, 12);

        // compute grid area inside padding
        int pad = 20;
        int gridW = w - pad*2;
        int gridH = h - pad*2;
        // maintain cell aspect ratio
        int cellW = Math.max(4, gridW / WIDTH);
        int cellH = Math.max(4, gridH / HEIGHT);
        int cellSize = Math.min(cellW, cellH);
        int gridPixelW = cellSize * WIDTH;
        int gridPixelH = cellSize * HEIGHT;
        int startX = (w - gridPixelW) / 2;
        int startY = (h - gridPixelH) / 2;

        // draw empty cell backgrounds
        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                int x = startX + c * cellSize;
                int y = startY + r * cellSize;
                g2.setColor(new Color(28, 28, 36));
                g2.fillRoundRect(x+2, y+2, cellSize-4, cellSize-4, 6, 6);
            }
        }

        // draw locked cells from overlayBoard
        if (overlayBoard != null) {
            for (int r = 0; r < Math.min(overlayBoard.length, HEIGHT); r++) {
                for (int c = 0; c < Math.min(overlayBoard[r].length, WIDTH); c++) {
                    if (overlayBoard[r][c] == 1 || overlayBoard[r][c] == 2) {
                        Color col = Color.GRAY;
                        if (overlayColors != null && overlayColors[r][c] != null) col = overlayColors[r][c];
                        int x = startX + c * cellSize;
                        int y = startY + r * cellSize;
                        g2.setColor(col);
                        g2.fillRoundRect(x+3, y+3, cellSize-6, cellSize-6, 6, 6);
                        // highlight
                        g2.setColor(new Color(255,255,255,40));
                        g2.fillRoundRect(x+4, y+4, (cellSize-6)/2, (cellSize-6)/2, 4, 4);

                        // If there's an item on this locked cell, draw an item glyph
                        if (overlayItems != null && overlayItems[r][c] != null) {
                            Item it = overlayItems[r][c];
                            drawItemGlyph(g2, it, x, y, cellSize);
                        }
                    }
                }
            }
        }

    // draw current moving block on top
        if (currentBlock != null) {
            for (int ry = 0; ry < currentBlock.height(); ry++) {
                for (int rx = 0; rx < currentBlock.width(); rx++) {
                    if (currentBlock.getShape(rx, ry) == 1) {
                        int boardX = currentX + rx;
                        int boardY = currentY + ry;
                        if (boardX >= 0 && boardX < WIDTH && boardY >= 0 && boardY < HEIGHT) {
                            int x = startX + boardX * cellSize;
                            int y = startY + boardY * cellSize;
                            Color col = currentBlock.getColor();
                            if (col == null) col = Color.CYAN;
                            g2.setColor(col);
                            g2.fillRoundRect(x+3, y+3, cellSize-6, cellSize-6, 6, 6);
                                g2.setColor(new Color(255,255,255,60));
                                g2.fillRoundRect(x+4, y+4, (cellSize-6)/2, (cellSize-6)/2, 4, 4);
                                // draw item on the moving block if present
                                if (currentBlock.getItem(rx, ry) != null) {
                                    drawItemGlyph(g2, currentBlock.getItem(rx, ry), x, y, cellSize);
                                }
                        }
                    }
                }
            }
        }
        g2.dispose();
        // If requested, draw the JTextPane text on top of the graphics (used for pause/menu messages)
        if (showTextOverlay) {
            super.paintComponent(g);
        }
    }

    /**
     * Draw a small glyph representing an item inside a cell.
     */
    private void drawItemGlyph(java.awt.Graphics2D g2, Item it, int x, int y, int cellSize) {
        int cx = x + cellSize/2;
        int cy = y + cellSize/2;
        int r = Math.max(6, cellSize/3);

        // background ring for contrast
        g2.setColor(new Color(0,0,0,120));
        g2.fillOval(cx - r - 1, cy - r - 1, r*2 + 2, r*2 + 2);

        if (it instanceof TimeStopItem) {
            // stopwatch: pale teal circle + small hand
            g2.setColor(new Color(60, 180, 170));
            g2.fillOval(cx - r, cy - r, r*2, r*2);
            g2.setColor(Color.WHITE);
            int hw = Math.max(2, r/4);
            g2.fillOval(cx - hw, cy - hw, hw*2, hw*2);
            g2.setColor(new Color(255,255,255,200));
            g2.setStroke(new java.awt.BasicStroke(Math.max(1f, r/6)));
            g2.drawLine(cx, cy, cx + r/2, cy - r/3);
        } else if (it instanceof BombItem) {
            // bomb: dark core with red rim
            g2.setColor(new Color(30, 10, 10));
            g2.fillOval(cx - r, cy - r, r*2, r*2);
            g2.setColor(new Color(255, 90, 90));
            g2.setStroke(new java.awt.BasicStroke(Math.max(1f, r/6)));
            g2.drawOval(cx - r + 1, cy - r + 1, r*2 - 2, r*2 - 2);
            // fuse dot
            g2.setColor(new Color(255, 200, 80));
            g2.fillOval(cx + r - Math.max(4, r/4), cy - r - Math.max(2, r/6), Math.max(4, r/3), Math.max(4, r/3));
            // try to draw a bomb emoji or fallback small 'B' at the center for clarity
            try {
                java.awt.Font prev = g2.getFont();
                java.awt.Font emojiFont = new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, Math.max(8, r));
                g2.setFont(emojiFont);
                g2.setColor(new Color(255,255,255,220));
                String bomb = "B";
                // center the string roughly
                java.awt.FontMetrics fm = g2.getFontMetrics();
                int sx = cx - fm.stringWidth(bomb) / 2;
                int sy = cy + fm.getAscent() / 2 - 2;
                g2.drawString(bomb, sx, sy);
                g2.setFont(prev);
            } catch (Exception ex) {
                // fallback: draw a white 'B'
                g2.setColor(Color.WHITE);
                java.awt.Font prev = g2.getFont();
                g2.setFont(prev.deriveFont((float) Math.max(8, r)));
                java.awt.FontMetrics fm = g2.getFontMetrics();
                String bomb = "B";
                int sx = cx - fm.stringWidth(bomb) / 2;
                int sy = cy + fm.getAscent() / 2 - 2;
                g2.drawString(bomb, sx, sy);
                g2.setFont(prev);
            }
        } else if (it instanceof se.tetris.team5.items.LineClearItem) {
            // line-clear: bright yellow horizontal bar
            g2.setColor(new Color(255, 200, 70));
            int hh = Math.max(4, r/3);
            g2.fillRoundRect(cx - r, cy - hh/2, r*2, hh, hh, hh);
            g2.setColor(new Color(255,255,255,180));
            g2.setStroke(new java.awt.BasicStroke(Math.max(1f, hh/3)));
            g2.drawLine(cx - r + 4, cy, cx + r - 4, cy);
        } else if (it instanceof se.tetris.team5.items.ScoreItem) {
            // score: small green circle with + symbol
            g2.setColor(new Color(100, 220, 140));
            g2.fillOval(cx - r, cy - r, r*2, r*2);
            g2.setColor(Color.WHITE);
            int sz = Math.max(8, r);
            java.awt.Font prev = g2.getFont();
            g2.setFont(prev.deriveFont((float) sz));
            g2.drawString("+", cx - sz/3, cy + sz/3 - 2);
            g2.setFont(prev);
        } else if (it instanceof se.tetris.team5.items.WeightBlockItem) {
            // weight: metallic gray square
            g2.setColor(new Color(140,140,150));
            int s = r * 3/2;
            g2.fillRoundRect(cx - s/2, cy - s/2, s, s, Math.max(2, s/4), Math.max(2, s/4));
            g2.setColor(new Color(255,255,255,80));
            g2.setStroke(new java.awt.BasicStroke(Math.max(1f, r/6)));
            g2.drawRoundRect(cx - s/2 + 1, cy - s/2 + 1, s - 2, s - 2, Math.max(2, s/4), Math.max(2, s/4));
        } else {
            // default: star-ish small mark
            g2.setColor(new Color(255, 210, 120));
            g2.fillOval(cx - r/2, cy - r/2, r, r);
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(Math.max(1f, r/6)));
            g2.drawOval(cx - r/2, cy - r/2, r, r);
        }
    }

    /**
     * 빈 보드(테두리만)를 그립니다 (텍스트 백업용)
     */
    public void drawEmptyBoard() {
        StringBuilder sb = new StringBuilder();

        // 위쪽 테두리
        for (int t = 0; t < WIDTH + 2; t++) {
            sb.append(BORDER_CHAR);
        }
        sb.append("\n");

        // 게임 보드 내부 (빈 공간)
        for (int i = 0; i < HEIGHT; i++) {
            sb.append(BORDER_CHAR);
            for (int j = 0; j < WIDTH; j++) {
                sb.append(" ");
            }
            sb.append(BORDER_CHAR);
            sb.append("\n");
        }

        // 아래쪽 테두리
        for (int t = 0; t < WIDTH + 2; t++) {
            sb.append(BORDER_CHAR);
        }

        setText(sb.toString());
        getStyledDocument().setCharacterAttributes(0, getDocument().getLength(), styleSet, false);
        getStyledDocument().setParagraphAttributes(0, getDocument().getLength(), styleSet, false);
    }

    public String createBorderLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < WIDTH + 2; i++) {
            sb.append(BORDER_CHAR);
        }
        return sb.toString();
    }

    public int getBoardHeight() {
        return HEIGHT;
    }

    public int getBoardWidth() {
        return WIDTH;
    }

    public char getBorderChar() {
        return BORDER_CHAR;
    }
    
    @Override
    public Dimension getPreferredSize() {
        // prefer a reasonable size so layout managers size the game area larger than side panel
        return new Dimension(360, 720);
    }
}