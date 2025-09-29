package se.tetris.team5.component.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.util.GameSettings;

public class NextBlockBoard extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextPane nextBlockPane;

    
    public NextBlockBoard() {
        initComponents();
    }
    
    private void initComponents() {
        // GameSettings에서 설정 불러오기 (향후 확장 가능)
        //GameSettings settings = GameSettings.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(240, 150));
        setMinimumSize(new Dimension(200, 120));
        
        JLabel nextLabel = new JLabel("다음 블록", SwingConstants.CENTER);
        nextLabel.setForeground(Color.WHITE);
        nextLabel.setFont(nextLabel.getFont().deriveFont(14f));
        add(nextLabel, BorderLayout.NORTH);
        
        nextBlockPane = new JTextPane();
        nextBlockPane.setEditable(false);
        nextBlockPane.setBackground(Color.BLACK);
        CompoundBorder nextBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        nextBlockPane.setBorder(nextBorder);
        add(nextBlockPane, BorderLayout.CENTER);
    }
    
    public void updateNextBlock(Block next) {
        if (next == null || nextBlockPane == null) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        
        // 4x4 영역에 블록 표시
        boolean[][] shape = new boolean[4][4];
        for (int i = 0; i < next.width() && i < 4; i++) {
            for (int j = 0; j < next.height() && j < 4; j++) {
                shape[i][j] = (next.getShape(i, j) == 1);
            }
        }
        
        for (int j = 0; j < 4; j++) {
            sb.append("  ");
            for (int i = 0; i < 4; i++) {
                if (shape[i][j]) {
                    sb.append("■ ");
                } else {
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }
        
        nextBlockPane.setText(sb.toString());
        StyledDocument doc = nextBlockPane.getStyledDocument();
        
        // 스타일 적용
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, next.getColor());
        StyleConstants.setFontSize(style, 12);
        StyleConstants.setFontFamily(style, "Courier New");
        StyleConstants.setBold(style, true);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
        doc.setCharacterAttributes(0, doc.getLength(), style, false);
        doc.setParagraphAttributes(0, doc.getLength(), style, false);
    }
}