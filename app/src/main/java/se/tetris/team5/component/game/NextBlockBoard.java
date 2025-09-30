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

/**
 * 다음 블록을 표시하는 보드의 테두리만을 담당하는 컴포넌트
 */
public class NextBlockBoard extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextPane nextBlockPane;
    private SimpleAttributeSet styleSet;
    
    public NextBlockBoard() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(240, 150));
        setMinimumSize(new Dimension(200, 120));
        
        // 라벨 추가
        JLabel nextLabel = new JLabel("다음 블록", SwingConstants.CENTER);
        nextLabel.setForeground(Color.WHITE);
        nextLabel.setFont(nextLabel.getFont().deriveFont(14f));
        add(nextLabel, BorderLayout.NORTH);
        
        // 다음 블록 표시 패널
        nextBlockPane = new JTextPane();
        nextBlockPane.setEditable(false);
        nextBlockPane.setBackground(Color.BLACK);
        CompoundBorder nextBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        nextBlockPane.setBorder(nextBorder);
        add(nextBlockPane, BorderLayout.CENTER);
        
        // 스타일 설정
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 16);
        StyleConstants.setFontFamily(styleSet, "Courier New");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
        
        // 초기 빈 상태 표시
        showEmptyNextBlock();
    }
    
    /**
     * 빈 다음 블록 영역을 표시합니다
     */
    public void showEmptyNextBlock() {
        StringBuilder sb = new StringBuilder();
        
        // 작은 테두리로 다음 블록 영역 표시
        for (int i = 0; i < 3; i++) {
            sb.append("      ").append("\n");
        }
        
        nextBlockPane.setText(sb.toString());
        nextBlockPane.getStyledDocument().setCharacterAttributes(0, nextBlockPane.getDocument().getLength(), styleSet, false);
    }
    
    /**
     * JTextPane을 반환합니다 (외부에서 내용을 설정할 수 있도록)
     */
    public JTextPane getTextPane() {
        return nextBlockPane;
    }
    
    /**
     * 스타일 설정을 반환합니다
     */
    public SimpleAttributeSet getStyleSet() {
        return styleSet;
    }
}