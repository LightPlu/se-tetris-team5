package se.tetris.team5.components.game;

import java.awt.*;
import javax.swing.*;
import se.tetris.team5.blocks.Block;

/**
 * 다음 블록을 표시하는 보드의 테두리만을 담당하는 컴포넌트
 */
public class NextBlockBoard extends JPanel {
    private static final long serialVersionUID = 1L;
    private Block nextBlock;

    public NextBlockBoard() {
        setPreferredSize(new Dimension(240, 170));
        setMinimumSize(new Dimension(200, 120));
        setBackground(new Color(24, 26, 48));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 3, true),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));
    }

    public void setNextBlock(Block block) {
        this.nextBlock = block;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 라벨
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.setColor(new Color(255, 215, 0));
        FontMetrics fm = g2.getFontMetrics();
        String label = "다음 블록";
        int labelWidth = fm.stringWidth(label);
        g2.drawString(label, (getWidth() - labelWidth) / 2, 36);

        // 미리보기 영역 배경
        int gridSize = 28;
        int previewX = (getWidth() - gridSize * 4) / 2;
        int previewY = 50;
        g2.setColor(new Color(30, 32, 60));
        g2.fillRoundRect(previewX - 8, previewY - 8, gridSize * 4 + 16, gridSize * 4 + 16, 16, 16);
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(previewX - 8, previewY - 8, gridSize * 4 + 16, gridSize * 4 + 16, 16, 16);

        // 블록 중앙정렬: 블록의 실제 크기에 따라 offset 계산
        if (nextBlock != null) {
            int blockW = nextBlock.width();
            int blockH = nextBlock.height();
            int offsetX = (4 - blockW) * gridSize / 2;
            int offsetY = (4 - blockH) * gridSize / 2;
            for (int row = 0; row < blockH; row++) {
                for (int col = 0; col < blockW; col++) {
                    if (nextBlock.getShape(col, row) == 1) {
                        g2.setColor(nextBlock.getColor());
                        g2.fillRoundRect(previewX + offsetX + col * gridSize, previewY + offsetY + row * gridSize, gridSize, gridSize, 8, 8);
                        g2.setColor(new Color(80, 90, 150));
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawRoundRect(previewX + offsetX + col * gridSize, previewY + offsetY + row * gridSize, gridSize, gridSize, 8, 8);
                    }
                }
            }
        }
    }
}