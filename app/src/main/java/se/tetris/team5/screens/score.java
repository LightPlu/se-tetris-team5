package se.tetris.team5.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import se.tetris.team5.ScreenController;

public class score {
    private ScreenController screenController;
    private JTextPane currentTextPane;
    private SimpleAttributeSet styleSet;

    public score(ScreenController screenController) {
        this.screenController = screenController;
        
        // 텍스트 스타일 설정
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 14);
        StyleConstants.setFontFamily(styleSet, "Source Code Pro");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
    }
    
    public void display(JTextPane textPane) {
        this.currentTextPane = textPane;
        textPane.setBackground(Color.BLACK);
        textPane.addKeyListener(new ScoreKeyListener());
        drawScoreScreen();
    }
    
    private void drawScoreScreen() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n\n\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("                    스코어 보드                    \n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        sb.append("스코어 보드 기능을 구현 중입니다...\n\n");
        sb.append("ESC : 홈으로 돌아가기\n");
        
        updateDisplay(sb.toString());
    }
    
    private void updateDisplay(String text) {
        if (currentTextPane != null) {
            currentTextPane.setText(text);
            StyledDocument doc = currentTextPane.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), styleSet, false);
            doc.setParagraphAttributes(0, doc.getLength(), styleSet, false);
        }
    }

    private class ScoreKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            e.consume(); // 이벤트 소비
        }

        @Override
        public void keyPressed(KeyEvent e) {
            e.consume(); // 이벤트 소비하여 전파 방지
            switch(e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    screenController.showScreen("home");
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            e.consume(); // 이벤트 소비
        }
    }
}