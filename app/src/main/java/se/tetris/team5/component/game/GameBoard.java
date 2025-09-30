package se.tetris.team5.component.game;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * 게임 보드의 테두리만을 표시하는 컴포넌트
 */
public class GameBoard extends JTextPane {
    
    private static final long serialVersionUID = 1L;
    
    public static final int HEIGHT = 19;
    public static final int WIDTH = 10;
    public static final char BORDER_CHAR = 'X';
    
    private SimpleAttributeSet styleSet;
    
    public GameBoard() {
        initComponents();
    }
    
    private void initComponents() {
        // Board display setting
        setEditable(false);
        setBackground(Color.BLACK);
        CompoundBorder border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 10),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        setBorder(border);
        
        // Document default style
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 18);
        StyleConstants.setFontFamily(styleSet, "Courier New");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
        StyleConstants.setLineSpacing(styleSet, -0.4f);
        
        // 초기 테두리 표시
        drawEmptyBoard();
    }
    
    /**
     * 빈 보드(테두리만)를 그립니다
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
    
    /**
     * 테두리 문자열을 생성합니다
     */
    public String createBorderLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < WIDTH + 2; i++) {
            sb.append(BORDER_CHAR);
        }
        return sb.toString();
    }
    
    /**
     * 보드의 높이를 반환합니다
     */
    public int getBoardHeight() {
        return HEIGHT;
    }
    
    /**
     * 보드의 너비를 반환합니다
     */
    public int getBoardWidth() {
        return WIDTH;
    }
    
    /**
     * 테두리 문자를 반환합니다
     */
    public char getBorderChar() {
        return BORDER_CHAR;
    }
}