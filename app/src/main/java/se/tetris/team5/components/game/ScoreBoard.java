package se.tetris.team5.components.game;

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
    setLayout(new BorderLayout(0, 8));
    setBackground(new Color(24, 26, 48));
    setPreferredSize(new Dimension(240, 260));
    setMinimumSize(new Dimension(200, 180));

    // 라벨 추가 (더 크고 컬러풀하게)
    JLabel scoreLabel = new JLabel("게임 정보", SwingConstants.CENTER);
    scoreLabel.setForeground(new Color(144, 238, 144));
    scoreLabel.setFont(scoreLabel.getFont().deriveFont(java.awt.Font.BOLD, 20f));
    scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
    add(scoreLabel, BorderLayout.NORTH);

    // 점수 표시 패널
    scorePane = new JTextPane();
    scorePane.setEditable(false);
    scorePane.setBackground(new Color(30, 32, 60));
    CompoundBorder scoreBorder = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(144, 238, 144), 3, true),
        BorderFactory.createEmptyBorder(10, 10, 10, 10));
    scorePane.setBorder(scoreBorder);
    add(scorePane, BorderLayout.CENTER);

    // 스타일 설정
    styleSet = new SimpleAttributeSet();
    StyleConstants.setFontSize(styleSet, 18);
    StyleConstants.setFontFamily(styleSet, "Consolas");
    StyleConstants.setBold(styleSet, true);
    StyleConstants.setForeground(styleSet, new Color(255, 255, 224));
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
    scorePane.setText(sb.toString());
    scorePane.getStyledDocument().setCharacterAttributes(0, scorePane.getDocument().getLength(), styleSet, false);
    }

    /**
     * 별도의 조작키 안내 패널을 반환합니다
     */
    public static JPanel createControlsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(24, 26, 48));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("<html><b style='color:#64b5f6;font-size:16px;'>조작키 안내</b></html>", SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        panel.add(title, BorderLayout.NORTH);

        JTextPane help = new JTextPane();
        help.setEditable(false);
        help.setBackground(new Color(30, 32, 60));
        help.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        SimpleAttributeSet helpStyle = new SimpleAttributeSet();
        StyleConstants.setFontSize(helpStyle, 15);
        StyleConstants.setFontFamily(helpStyle, "Consolas");
        StyleConstants.setForeground(helpStyle, new Color(173, 216, 230));
        StyleConstants.setBold(helpStyle, true);
        StyleConstants.setAlignment(helpStyle, StyleConstants.ALIGN_LEFT);
        StringBuilder sb = new StringBuilder();
        sb.append("\u2191  : 회전 (Rotate)\n");
        sb.append("\u2193  : 소프트 드롭 (↓)\n");
        sb.append("\u2190\u2192 : 좌우 이동\n");
        sb.append("Space : 하드 드롭\n");
        sb.append("ESC   : 나가기\n");
        help.setText(sb.toString());
        help.getStyledDocument().setCharacterAttributes(0, help.getDocument().getLength(), helpStyle, false);
        panel.add(help, BorderLayout.CENTER);
        return panel;
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