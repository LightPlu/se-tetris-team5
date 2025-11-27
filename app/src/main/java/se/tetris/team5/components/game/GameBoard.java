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
    // previous board snapshot to detect cleared rows (so we can animate without changing engine)
    private int[][] previousBoard = null;
    // per-row animation progress map (rowIndex -> progress 0..1)
    private java.util.Map<Integer, Float> animRowProgress = new java.util.LinkedHashMap<>();
    private javax.swing.Timer animTimer = null;
    // per-row particle lists for explosion effect
    private java.util.Map<Integer, java.util.List<Particle>> rowParticles = new java.util.HashMap<>();

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
            // Debug logging disabled for performance
            // NOTE: previous-board diff based detection of cleared rows has been
            // disabled. Cleared-row animations are triggered explicitly by the
            // GameEngine -> UI plumbing (game consumes engine.consumeLastClearedRows()
            // and calls triggerClearAnimation). Keeping both detection mechanisms
            // enabled caused timing/race issues where only a subset of rows were
            // animated (esp. on hard-drop). Rely on the engine-provided list now.

            // copy minimal state reference (don't mutate)
            overlayBoard = board;
            overlayColors = colors;
            overlayItems = items;
            // snapshot for next comparison (shallow copy of rows)
            previousBoard = new int[board.length][board[0].length];
            for (int i = 0; i < board.length; i++) {
                System.arraycopy(board[i], 0, previousBoard[i], 0, board[i].length);
            }
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
                        
                        // 색상과 패턴을 함께 그리기
                        String blockType = guessBlockTypeFromColor(col);
                        drawBlockWithPattern(g2, x, y, cellSize, col, blockType);

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
                            
                            // 현재 블록도 패턴과 함께 그리기
                            String blockType = currentBlock.getBlockType();
                            drawBlockWithPattern(g2, x, y, cellSize, col, blockType);
                            
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
        // draw cleared-row animation overlay if active (strong full-row orange flash)
        if (animRowProgress != null && !animRowProgress.isEmpty()) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (java.util.Map.Entry<Integer, Float> en : animRowProgress.entrySet()) {
                int row = en.getKey();
                float prog = en.getValue();
                // stronger flash curve: start bright then fade quickly
                float alpha = Math.max(0f, 1f - prog);
                // emphasize initial flash
                float flash = (float) Math.sin((1f - prog) * Math.PI);
                float outerAlpha = Math.max(0f, Math.min(1f, flash * 0.95f + alpha * 0.05f));
                int opaOuter = (int) (outerAlpha * 220);
                java.awt.Color outer = new java.awt.Color(255, 150, 40, Math.max(30, opaOuter));

                int y = startY + row * cellSize;
                // full-width opaque band
                g3.setColor(outer);
                g3.fillRect(startX, y, gridPixelW, cellSize);

                // inner glow (smaller, brighter center)
                float innerAlpha = Math.max(0f, outerAlpha * 0.8f + 0.1f);
                int opaInner = (int) (innerAlpha * 200);
                java.awt.Color inner = new java.awt.Color(255, 210, 120, Math.max(20, opaInner));
                int inset = Math.max(2, cellSize / 8);
                g3.setColor(inner);
                g3.fillRect(startX + inset, y + inset, gridPixelW - inset * 2, cellSize - inset * 2);
            }
            g3.dispose();
        }

        // draw explosion particles for cleared rows
        if (rowParticles != null && !rowParticles.isEmpty()) {
            Graphics2D g4 = (Graphics2D) g.create();
            g4.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (java.util.Map.Entry<Integer, java.util.List<Particle>> en : rowParticles.entrySet()) {
                int row = en.getKey();
                java.util.List<Particle> pls = en.getValue();
                int baseY = startY + row * cellSize;
                for (Particle p : pls) {
                    int px = Math.round(startX + p.x);
                    int py = Math.round(baseY + p.y);
                    int sz = Math.max(2, p.size);
                    int ia = Math.max(0, Math.min(255, (int) (p.alpha * 255)));
                    java.awt.Color pc = new java.awt.Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), ia);
                    g4.setColor(pc);
                    g4.fillOval(px - sz/2, py - sz/2, sz, sz);
                }
            }
            g4.dispose();
        }



        // If requested, draw the JTextPane text on top of the graphics (used for pause/menu messages)
        if (showTextOverlay) {
            super.paintComponent(g);
        }
    }

    /** Trigger a cleared-rows animation for the given rows (row indices, 0..HEIGHT-1). */
    public void triggerClearAnimation(java.util.List<Integer> rows) {
        if (rows == null || rows.isEmpty()) return;
        // register rows for animation (if not already present)
        for (Integer r : rows) {
            if (r == null) continue;
            if (!animRowProgress.containsKey(r)) {
                animRowProgress.put(r, 0f);
            }
        }

    // spawn particles even if the timer is already running so new clears get visualized
    spawnRowParticles(rows);

        if (animTimer != null && animTimer.isRunning()) {
            return; // timer already running; it will pick up new rows and particles
        }

        animTimer = new javax.swing.Timer(40, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                java.util.List<Integer> finished = new java.util.ArrayList<>();
                // advance row fade progress
                for (java.util.Map.Entry<Integer, Float> en : new java.util.ArrayList<>(animRowProgress.entrySet())) {
                    float p = en.getValue() + 0.08f;
                    if (p >= 1f) {
                        finished.add(en.getKey());
                    } else {
                        animRowProgress.put(en.getKey(), p);
                    }
                }
                for (Integer k : finished) animRowProgress.remove(k);

                // advance particles
                java.util.List<Integer> emptyRows = new java.util.ArrayList<>();
                for (java.util.Map.Entry<Integer, java.util.List<Particle>> en : new java.util.ArrayList<>(rowParticles.entrySet())) {
                    java.util.List<Particle> pls = en.getValue();
                    for (Particle p : new java.util.ArrayList<>(pls)) {
                        p.vy += 0.4f; // gravity
                        p.x += p.vx;
                        p.y += p.vy;
                        p.life -= 0.06f;
                        p.alpha = Math.max(0f, p.life / p.maxLife);
                        if (p.life <= 0f) pls.remove(p);
                    }
                    if (pls.isEmpty()) emptyRows.add(en.getKey());
                }
                for (Integer k : emptyRows) rowParticles.remove(k);

                if (animRowProgress.isEmpty() && rowParticles.isEmpty()) {
                    animTimer.stop();
                }
                repaint();
            }
        });
        animTimer.setInitialDelay(0);
        animTimer.start();
    }

    /**
     * Create explosion particles for the cleared rows. Called by external code before starting
     * the animation timer; we compute pixel-relative positions so paintComponent can render them.
     */
    private void spawnRowParticles(java.util.List<Integer> rows) {
        if (rows == null || rows.isEmpty()) return;
        int w = getWidth();
        int h = getHeight();
        int pad = 20;
        int gridW = w - pad*2;
        int gridH = h - pad*2;
        int cellW = Math.max(4, gridW / WIDTH);
        int cellH = Math.max(4, gridH / HEIGHT);
        int cellSize = Math.min(cellW, cellH);
    java.util.Random rand = new java.util.Random();

        for (Integer row : rows) {
            if (row == null) continue;
            java.util.List<Particle> pls = new java.util.ArrayList<>();
            for (int c = 0; c < WIDTH; c++) {
                // spawn more fragments per cell for a stronger visible effect
                int fragments = 6;
                for (int f = 0; f < fragments; f++) {
                    // positions are relative to the left edge of the grid; we'll offset by startX when drawing
                    float cx = c * cellSize + cellSize * 0.5f + (rand.nextFloat() - 0.5f) * cellSize * 0.6f;
                    float cy = cellSize * 0.5f + (rand.nextFloat() - 0.5f) * cellSize * 0.4f;
                    float vx = (rand.nextFloat() - 0.5f) * 10f;
                    float vy = -(rand.nextFloat() * 10f + 3f);
                    float life = 0.9f + rand.nextFloat() * 1.2f;
                    int size = Math.max(5, Math.round(cellSize * (0.35f + rand.nextFloat() * 0.45f)));
                    java.awt.Color col = Color.ORANGE;
                    try {
                        if (overlayColors != null && overlayColors.length > row && overlayColors[row][c] != null) {
                            col = overlayColors[row][c];
                        }
                    } catch (Exception ignored) {}
                    Particle p = new Particle(cx, cy, vx, vy, life, size, col);
                    pls.add(p);
                }
            }
            rowParticles.put(row, pls);
        }
    }

    /** Simple particle used for explosion fragments. Positions are relative to the left of the grid. */
    private static class Particle {
        float x, y; // pixel offsets relative to left edge of the grid
        float vx, vy;
        float life, maxLife;
        float alpha = 1f;
        int size;
        java.awt.Color color;

        Particle(float x, float y, float vx, float vy, float life, int size, java.awt.Color color) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.life = life; this.maxLife = life; this.size = size; this.color = color;
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
            // line-clear: bright yellow badge with centered 'L' - 다른 아이템과 동일한 크기로 조정
            g2.setColor(new Color(255, 200, 70));
            int arc = Math.max(4, r / 2);
            g2.fillRoundRect(cx - r, cy - r, r * 2, r * 2, arc, arc);
            // draw a clear 'L' glyph centered
            g2.setColor(new Color(255,255,255,220));
            java.awt.Font prev = g2.getFont();
            java.awt.Font glyphFont = prev.deriveFont(java.awt.Font.BOLD, (float) Math.max(8, r * 1.1));
            g2.setFont(glyphFont);
            java.awt.FontMetrics fm = g2.getFontMetrics();
            String glyph = "L";
            int sx = cx - fm.stringWidth(glyph) / 2;
            int sy = cy + fm.getAscent() / 2 - 2;
            g2.drawString(glyph, sx, sy);
            g2.setFont(prev);
        } else if (it instanceof se.tetris.team5.items.DoubleScoreItem) {
            // double-score: small badge with 'x2'
            g2.setColor(new Color(120, 180, 255)); // pale blue
            int badgeW = Math.max(r * 2, 14);
            int badgeH = Math.max(r, 10);
            g2.fillRoundRect(cx - badgeW/2, cy - badgeH/2, badgeW, badgeH, badgeH/2, badgeH/2);
            g2.setColor(Color.WHITE);
            java.awt.Font prev = g2.getFont();
            java.awt.Font glyphFont = prev.deriveFont((float) Math.max(10, badgeH));
            g2.setFont(glyphFont);
            String glyph = "x2";
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int sx = cx - fm.stringWidth(glyph) / 2;
            int sy = cy + fm.getAscent() / 2 - 2;
            g2.drawString(glyph, sx, sy);
            g2.setFont(prev);
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
    
    /**
     * 색상과 패턴을 함께 그려서 블록 타입을 더 명확하게 구분
     */
    private void drawBlockWithPattern(java.awt.Graphics2D g2, int x, int y, int cellSize, java.awt.Color color, String blockType) {
        // 기본 블록 배경 그리기
        g2.setColor(color);
        g2.fillRoundRect(x+3, y+3, cellSize-6, cellSize-6, 6, 6);
        
        // 색맹 모드일 때만 패턴 추가
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        if (settings.isColorblindMode()) {
            drawBlockPattern(g2, x, y, cellSize, blockType);
        }
        
        // 기본 하이라이트
        g2.setColor(new java.awt.Color(255,255,255,40));
        g2.fillRoundRect(x+4, y+4, (cellSize-6)/2, (cellSize-6)/2, 4, 4);
    }
    
    /**
     * 블록 타입별 고유 패턴 그리기
     */
    private void drawBlockPattern(java.awt.Graphics2D g2, int x, int y, int cellSize, String blockType) {
        g2.setColor(new java.awt.Color(0, 0, 0, 120)); // 반투명 검정색 패턴
        int innerSize = cellSize - 10;
        int patX = x + 5;
        int patY = y + 5;
        
        java.awt.Stroke oldStroke = g2.getStroke();
        g2.setStroke(new java.awt.BasicStroke(2.0f));
        
        switch (blockType) {
            case "I": // 수직선 패턴
                g2.drawLine(patX + innerSize/2, patY + 2, patX + innerSize/2, patY + innerSize - 2);
                break;
            case "O": // 작은 사각형 패턴
                int rectSize = innerSize/3;
                g2.drawRect(patX + (innerSize-rectSize)/2, patY + (innerSize-rectSize)/2, rectSize, rectSize);
                break;
            case "T": // T자 패턴
                g2.drawLine(patX + 2, patY + innerSize/3, patX + innerSize - 2, patY + innerSize/3);
                g2.drawLine(patX + innerSize/2, patY + innerSize/3, patX + innerSize/2, patY + innerSize - 2);
                break;
            case "L": // L자 패턴
                g2.drawLine(patX + innerSize/3, patY + 2, patX + innerSize/3, patY + innerSize - 2);
                g2.drawLine(patX + innerSize/3, patY + innerSize - 2, patX + innerSize - 2, patY + innerSize - 2);
                break;
            case "J": // J자 패턴 (뒤집힌 L)
                g2.drawLine(patX + 2*innerSize/3, patY + 2, patX + 2*innerSize/3, patY + innerSize - 2);
                g2.drawLine(patX + 2, patY + innerSize - 2, patX + 2*innerSize/3, patY + innerSize - 2);
                break;
            case "S": // S자 지그재그 패턴
                g2.drawLine(patX + 2, patY + 2*innerSize/3, patX + innerSize/2, patY + 2*innerSize/3);
                g2.drawLine(patX + innerSize/2, patY + 2*innerSize/3, patX + innerSize/2, patY + innerSize/3);
                g2.drawLine(patX + innerSize/2, patY + innerSize/3, patX + innerSize - 2, patY + innerSize/3);
                break;
            case "Z": // Z자 패턴
                g2.drawLine(patX + 2, patY + innerSize/3, patX + innerSize/2, patY + innerSize/3);
                g2.drawLine(patX + innerSize/2, patY + innerSize/3, patX + innerSize/2, patY + 2*innerSize/3);
                g2.drawLine(patX + innerSize/2, patY + 2*innerSize/3, patX + innerSize - 2, patY + 2*innerSize/3);
                break;
            case "W": // 무게추 - X 패턴
                g2.drawLine(patX + 2, patY + 2, patX + innerSize - 2, patY + innerSize - 2);
                g2.drawLine(patX + innerSize - 2, patY + 2, patX + 2, patY + innerSize - 2);
                break;
        }
        
        g2.setStroke(oldStroke);
    }
    
    /**
     * 색상을 바탕으로 블록 타입을 추정하는 헬퍼 메소드
     */
    private String guessBlockTypeFromColor(java.awt.Color color) {
        // 기본 색상 체크
        if (color.equals(java.awt.Color.CYAN)) return "I";
        if (color.equals(java.awt.Color.YELLOW)) return "O";
        if (color.equals(java.awt.Color.MAGENTA)) return "T";
        if (color.equals(java.awt.Color.ORANGE)) return "L";
        if (color.equals(java.awt.Color.BLUE)) return "J";
        if (color.equals(java.awt.Color.GREEN)) return "S";
        if (color.equals(java.awt.Color.RED)) return "Z";
        if (color.equals(new java.awt.Color(64, 64, 64))) return "W";
        
        // 색맹 모드 색상 체크
        if (color.equals(new java.awt.Color(135, 206, 250))) return "I"; // sky blue
        if (color.equals(new java.awt.Color(255, 255, 0))) return "O";   // yellow
        if (color.equals(new java.awt.Color(199, 21, 133))) return "T";  // reddish purple
        if (color.equals(new java.awt.Color(255, 165, 0))) return "L";   // orange
        if (color.equals(new java.awt.Color(0, 0, 255))) return "J";     // blue
        if (color.equals(new java.awt.Color(0, 158, 115))) return "S";   // bluish green
        if (color.equals(new java.awt.Color(213, 94, 0))) return "Z";    // vermilion
        if (color.equals(new java.awt.Color(85, 85, 85))) return "W";    // 밝은 검정색
        
        return "O"; // 기본값
    }
    
    @Override
    public Dimension getPreferredSize() {
        // prefer a reasonable size so layout managers size the game area larger than side panel
        return new Dimension(360, 720);
    }
}