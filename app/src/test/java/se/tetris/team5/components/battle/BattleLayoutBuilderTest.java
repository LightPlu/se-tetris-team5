package se.tetris.team5.components.battle;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

/**
 * BattleLayoutBuilder 유틸리티 클래스 테스트
 */
public class BattleLayoutBuilderTest {

    /**
     * 테스트 1: 제목이 있는 패널 생성
     */
    @Test
    public void testCreateTitledPanel_BasicCreation() {
        JLabel content = new JLabel("테스트 컨텐츠");
        JPanel panel = BattleLayoutBuilder.createTitledPanel(
            "테스트 제목",
            content,
            Color.BLUE,
            Color.RED
        );
        
        assertNotNull("패널이 생성되어야 함", panel);
        assertNotNull("패널에 테두리가 있어야 함", panel.getBorder());
    }

    /**
     * 테스트 2: 다양한 색상으로 패널 생성
     */
    @Test
    public void testCreateTitledPanel_DifferentColors() {
        JLabel content = new JLabel("컨텐츠");
        
        Color[] colors = {
            Color.RED, Color.GREEN, Color.BLUE,
            Color.YELLOW, Color.CYAN, Color.MAGENTA,
            new Color(100, 200, 255), new Color(255, 100, 100)
        };
        
        for (Color color : colors) {
            JPanel panel = BattleLayoutBuilder.createTitledPanel(
                "제목", content, color, color
            );
            assertNotNull("다양한 색상으로 패널이 생성되어야 함", panel);
        }
    }

    /**
     * 테스트 3: 빈 제목으로 패널 생성
     */
    @Test
    public void testCreateTitledPanel_EmptyTitle() {
        JLabel content = new JLabel("컨텐츠");
        JPanel panel = BattleLayoutBuilder.createTitledPanel(
            "",
            content,
            Color.BLACK,
            Color.WHITE
        );
        
        assertNotNull("빈 제목으로도 패널이 생성되어야 함", panel);
    }

    /**
     * 테스트 4: 긴 제목으로 패널 생성
     */
    @Test
    public void testCreateTitledPanel_LongTitle() {
        JLabel content = new JLabel("컨텐츠");
        String longTitle = "매우 긴 제목 " + "A".repeat(100);
        
        JPanel panel = BattleLayoutBuilder.createTitledPanel(
            longTitle,
            content,
            Color.BLUE,
            Color.RED
        );
        
        assertNotNull("긴 제목으로도 패널이 생성되어야 함", panel);
    }

    /**
     * 테스트 5: 다양한 컴포넌트로 패널 생성
     */
    @Test
    public void testCreateTitledPanel_DifferentComponents() {
        // JLabel
        JPanel panel1 = BattleLayoutBuilder.createTitledPanel(
            "레이블", new JLabel("테스트"), Color.BLUE, Color.RED
        );
        assertNotNull("JLabel로 패널이 생성되어야 함", panel1);
        
        // JPanel
        JPanel panel2 = BattleLayoutBuilder.createTitledPanel(
            "패널", new JPanel(), Color.GREEN, Color.YELLOW
        );
        assertNotNull("JPanel로 패널이 생성되어야 함", panel2);
    }

    /**
     * 테스트 6: 한글 폰트 생성 - 기본 스타일
     */
    @Test
    public void testCreateKoreanFont_PlainStyle() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.PLAIN, 12);
        
        assertNotNull("폰트가 생성되어야 함", font);
        assertEquals("폰트 크기가 12여야 함", 12, font.getSize());
    }

    /**
     * 테스트 7: 한글 폰트 생성 - 볼드 스타일
     */
    @Test
    public void testCreateKoreanFont_BoldStyle() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.BOLD, 16);
        
        assertNotNull("볼드 폰트가 생성되어야 함", font);
        assertEquals("폰트 크기가 16이어야 함", 16, font.getSize());
        assertTrue("폰트가 볼드여야 함", font.isBold());
    }

    /**
     * 테스트 8: 한글 폰트 생성 - 이탤릭 스타일
     */
    @Test
    public void testCreateKoreanFont_ItalicStyle() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.ITALIC, 14);
        
        assertNotNull("이탤릭 폰트가 생성되어야 함", font);
        assertEquals("폰트 크기가 14여야 함", 14, font.getSize());
        assertTrue("폰트가 이탤릭이어야 함", font.isItalic());
    }

    /**
     * 테스트 9: 한글 폰트 생성 - 다양한 크기
     */
    @Test
    public void testCreateKoreanFont_DifferentSizes() {
        int[] sizes = {8, 10, 12, 14, 16, 18, 20, 24, 32, 48};
        
        for (int size : sizes) {
            Font font = BattleLayoutBuilder.createKoreanFont(Font.PLAIN, size);
            assertNotNull("크기 " + size + "의 폰트가 생성되어야 함", font);
            assertEquals("폰트 크기가 일치해야 함", size, font.getSize());
        }
    }

    /**
     * 테스트 10: 한글 폰트 생성 - 한글 문자 표시 가능 여부
     */
    @Test
    public void testCreateKoreanFont_CanDisplayKorean() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.PLAIN, 12);
        
        assertTrue("한글 문자를 표시할 수 있어야 함", 
                   font.canDisplay('한') || font.canDisplay('가'));
    }

    /**
     * 테스트 11: 한글 폰트 생성 - 매우 작은 크기
     */
    @Test
    public void testCreateKoreanFont_VerySmallSize() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.PLAIN, 1);
        
        assertNotNull("매우 작은 크기의 폰트도 생성되어야 함", font);
        assertEquals("폰트 크기가 1이어야 함", 1, font.getSize());
    }

    /**
     * 테스트 12: 한글 폰트 생성 - 매우 큰 크기
     */
    @Test
    public void testCreateKoreanFont_VeryLargeSize() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.PLAIN, 200);
        
        assertNotNull("매우 큰 크기의 폰트도 생성되어야 함", font);
        assertEquals("폰트 크기가 200이어야 함", 200, font.getSize());
    }

    /**
     * 테스트 13: 한글 폰트 생성 - 볼드+이탤릭 조합
     */
    @Test
    public void testCreateKoreanFont_BoldItalicCombination() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.BOLD | Font.ITALIC, 14);
        
        assertNotNull("볼드+이탤릭 폰트가 생성되어야 함", font);
        assertTrue("폰트가 볼드여야 함", font.isBold());
        assertTrue("폰트가 이탤릭이어야 함", font.isItalic());
    }

    /**
     * 테스트 14: 제목 패널 생성 - null 검증
     */
    @Test
    public void testCreateTitledPanel_NotNull() {
        JLabel content = new JLabel("컨텐츠");
        JPanel panel = BattleLayoutBuilder.createTitledPanel(
            "제목", content, Color.BLACK, Color.WHITE
        );
        
        assertNotNull("패널이 null이 아니어야 함", panel);
        assertFalse("패널이 불투명하지 않아야 함", panel.isOpaque());
    }

    /**
     * 테스트 15: 제목 패널 생성 - 레이아웃 확인
     */
    @Test
    public void testCreateTitledPanel_LayoutType() {
        JLabel content = new JLabel("컨텐츠");
        JPanel panel = BattleLayoutBuilder.createTitledPanel(
            "제목", content, Color.BLACK, Color.WHITE
        );
        
        assertTrue("패널의 레이아웃이 BorderLayout이어야 함", 
                   panel.getLayout() instanceof java.awt.BorderLayout);
    }

    /**
     * 테스트 16: 한글이 포함된 제목으로 패널 생성
     */
    @Test
    public void testCreateTitledPanel_KoreanTitle() {
        JLabel content = new JLabel("컨텐츠");
        String[] koreanTitles = {
            "다음 블록", "점수", "공격 블록",
            "게임 정보", "플레이어 1", "플레이어 2"
        };
        
        for (String title : koreanTitles) {
            JPanel panel = BattleLayoutBuilder.createTitledPanel(
                title, content, Color.BLUE, Color.RED
            );
            assertNotNull("한글 제목 '" + title + "'으로 패널이 생성되어야 함", panel);
        }
    }

    /**
     * 테스트 17: 특수 문자가 포함된 제목으로 패널 생성
     */
    @Test
    public void testCreateTitledPanel_SpecialCharacters() {
        JLabel content = new JLabel("컨텐츠");
        String[] specialTitles = {
            "제목!", "제목?", "제목#1", "제목@", "제목$",
            "제목 (괄호)", "제목 [대괄호]", "제목: 콜론"
        };
        
        for (String title : specialTitles) {
            JPanel panel = BattleLayoutBuilder.createTitledPanel(
                title, content, Color.BLACK, Color.WHITE
            );
            assertNotNull("특수문자 제목으로 패널이 생성되어야 함", panel);
        }
    }

    /**
     * 테스트 18: 투명 색상으로 패널 생성
     */
    @Test
    public void testCreateTitledPanel_TransparentColors() {
        JLabel content = new JLabel("컨텐츠");
        Color transparentColor = new Color(255, 0, 0, 128); // 반투명 빨강
        
        JPanel panel = BattleLayoutBuilder.createTitledPanel(
            "제목", content, transparentColor, transparentColor
        );
        
        assertNotNull("투명 색상으로 패널이 생성되어야 함", panel);
    }

    /**
     * 테스트 19: 한글 폰트 - 0 크기 예외 처리
     */
    @Test
    public void testCreateKoreanFont_ZeroSize() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.PLAIN, 0);
        
        assertNotNull("크기 0의 폰트도 생성되어야 함", font);
        assertEquals("폰트 크기가 0이어야 함", 0, font.getSize());
    }

    /**
     * 테스트 20: 한글 폰트 - 음수 크기 예외 처리
     */
    @Test
    public void testCreateKoreanFont_NegativeSize() {
        Font font = BattleLayoutBuilder.createKoreanFont(Font.PLAIN, -10);
        
        assertNotNull("음수 크기의 폰트도 생성되어야 함", font);
    }
}
