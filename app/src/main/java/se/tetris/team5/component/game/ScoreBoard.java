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

import se.tetris.team5.util.GameSettings;

public class ScoreBoard extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextPane scorePane;
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    
    public ScoreBoard() {
        initComponents();
        updateDisplay();
    }
    
    private void initComponents() {
        // GameSettings에서 설정 불러오기 (향후 확장 가능)
        //GameSettings settings = GameSettings.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(240, 400));
        setMinimumSize(new Dimension(200, 300));
        
        JLabel scoreLabel = new JLabel("게임 정보", SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(14f));
        add(scoreLabel, BorderLayout.NORTH);
        
        scorePane = new JTextPane();
        scorePane.setEditable(false);
        scorePane.setBackground(Color.BLACK);
        CompoundBorder scoreBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        scorePane.setBorder(scoreBorder);
        add(scorePane, BorderLayout.CENTER);
    }
    
    public void updateScore(int score, int level, int linesCleared) {
        this.score = score;
        this.level = level;
        this.linesCleared = linesCleared;
        updateDisplay();
    }
    
    private void updateDisplay() {
        if (scorePane == null) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("  점수: ").append(String.format("%,d", score)).append("\n\n");
        sb.append("  레벨: ").append(level).append("\n\n");
        sb.append("  라인: ").append(linesCleared).append("\n\n");
        sb.append("  ────────────────\n\n");
        sb.append("  조작법:\n");
        
        // GameSettings에서 키 설정 불러오기
        GameSettings settings = GameSettings.getInstance();
        sb.append("  ").append(settings.getKeyName(settings.getKeyCode("left")))
          .append("/").append(settings.getKeyName(settings.getKeyCode("right"))).append(" : 이동\n");
        sb.append("  ").append(settings.getKeyName(settings.getKeyCode("down"))).append(" : 빠른 낙하\n");
        sb.append("  ").append(settings.getKeyName(settings.getKeyCode("rotate"))).append(" : 회전\n");
        sb.append("  ").append(settings.getKeyName(settings.getKeyCode("drop"))).append(" : 즉시 낙하\n");
        sb.append("  ESC : 메뉴로\n\n");
        sb.append("  ────────────────\n");
        sb.append("  점수 시스템:\n");
        sb.append("  1줄: 100점\n");
        sb.append("  2줄: 300점\n");
        sb.append("  3줄: 500점\n");
        sb.append("  4줄: 800점\n");
        sb.append("  (레벨 배율 적용)\n");
        
        scorePane.setText(sb.toString());
        StyledDocument doc = scorePane.getStyledDocument();
        
        // 스타일 적용
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, Color.WHITE);
        StyleConstants.setFontSize(style, 11);
        StyleConstants.setFontFamily(style, "Source Code Pro");
        StyleConstants.setBold(style, true);
        doc.setCharacterAttributes(0, doc.getLength(), style, false);
        doc.setParagraphAttributes(0, doc.getLength(), style, false);
        
        // 점수 부분만 강조색상 적용
        String text = sb.toString();
        int scoreIndex = text.indexOf("점수: ");
        if (scoreIndex != -1) {
            SimpleAttributeSet scoreStyle = new SimpleAttributeSet(style);
            StyleConstants.setForeground(scoreStyle, Color.YELLOW);
            int scoreEndIndex = text.indexOf("\n", scoreIndex);
            doc.setCharacterAttributes(scoreIndex, scoreEndIndex - scoreIndex, scoreStyle, false);
        }
        
        // 레벨 부분 강조
        int levelIndex = text.indexOf("레벨: ");
        if (levelIndex != -1) {
            SimpleAttributeSet levelStyle = new SimpleAttributeSet(style);
            StyleConstants.setForeground(levelStyle, Color.CYAN);
            int levelEndIndex = text.indexOf("\n", levelIndex);
            doc.setCharacterAttributes(levelIndex, levelEndIndex - levelIndex, levelStyle, false);
        }
        
        // 라인 부분 강조
        int lineIndex = text.indexOf("라인: ");
        if (lineIndex != -1) {
            SimpleAttributeSet lineStyle = new SimpleAttributeSet(style);
            StyleConstants.setForeground(lineStyle, Color.GREEN);
            int lineEndIndex = text.indexOf("\n", lineIndex);
            doc.setCharacterAttributes(lineIndex, lineEndIndex - lineIndex, lineStyle, false);
        }
    }
    
    // Public getters
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLinesCleared() { return linesCleared; }
}