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
 * 점수 정보를 표시하는 보드의 테두리만을 담당하는 컴포넌트
 */
public class ScoreBoard extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextPane scorePane;
    private SimpleAttributeSet styleSet;
    
    public ScoreBoard() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(240, 400));
        setMinimumSize(new Dimension(200, 300));
        
        // 라벨 추가
        JLabel scoreLabel = new JLabel("게임 정보", SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(14f));
        add(scoreLabel, BorderLayout.NORTH);
        
        // 점수 표시 패널
        scorePane = new JTextPane();
        scorePane.setEditable(false);
        scorePane.setBackground(Color.BLACK);
        CompoundBorder scoreBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        scorePane.setBorder(scoreBorder);
        add(scorePane, BorderLayout.CENTER);
        
        // 스타일 설정
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 14);
        StyleConstants.setFontFamily(styleSet, "Courier New");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.YELLOW);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_LEFT);
        
        // 초기 빈 상태 표시
        showEmptyScore();
    }
    
    /**
     * 빈 점수 영역을 표시합니다
     */
    public void showEmptyScore() {
        StringBuilder sb = new StringBuilder();
        sb.append("점수: 0\n");
        sb.append("레벨: 1\n");
        sb.append("줄: 0\n");
        sb.append("\n");
        sb.append("조작법:\n");
        sb.append("↑: 회전\n");
        sb.append("↓: 소프트 드롭\n");
        sb.append("←→: 이동\n");
        sb.append("Space: 하드 드롭\n");
        sb.append("ESC: 나가기\n");
        
        scorePane.setText(sb.toString());
        scorePane.getStyledDocument().setCharacterAttributes(0, scorePane.getDocument().getLength(), styleSet, false);
    }
    
    /**
     * JTextPane을 반환합니다 (외부에서 내용을 설정할 수 있도록)
     */
    public JTextPane getTextPane() {
        return scorePane;
    }
    
    /**
     * 스타일 설정을 반환합니다
     */
    public SimpleAttributeSet getStyleSet() {
        return styleSet;
    }
}