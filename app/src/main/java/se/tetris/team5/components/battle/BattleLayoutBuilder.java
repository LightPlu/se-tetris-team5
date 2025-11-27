package se.tetris.team5.components.battle;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * 대전 모드 UI 레이아웃 빌더
 * UI 생성을 위한 유틸리티 메서드 제공
 */
public class BattleLayoutBuilder {
    
    /**
     * 제목이 있는 패널 생성
     */
    public static JPanel createTitledPanel(String title, JComponent content, 
                                          Color borderColor, Color titleColor) {
        JPanel wrapper = new JPanel(new java.awt.BorderLayout());
        wrapper.setOpaque(false);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            title
        );
        border.setTitleColor(titleColor);
        border.setTitleFont(new Font("Dialog", Font.BOLD, 12));
        border.setTitleJustification(TitledBorder.CENTER);
        
        wrapper.setBorder(border);
        wrapper.add(content, java.awt.BorderLayout.CENTER);
        
        return wrapper;
    }
    
    /**
     * 한글 폰트 생성
     */
    public static Font createKoreanFont(int style, int size) {
        String[] koreanFonts = {"맑은 고딕", "Malgun Gothic", "굴림", "Gulim", 
                                "Arial Unicode MS", "Dialog"};
        for (String fontName : koreanFonts) {
            Font font = new Font(fontName, style, size);
            if (font.getFamily().equals(fontName) || font.canDisplay('한')) {
                return font;
            }
        }
        return new Font(Font.DIALOG, style, size);
    }
}
