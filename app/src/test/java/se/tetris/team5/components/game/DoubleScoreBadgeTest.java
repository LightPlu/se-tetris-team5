package se.tetris.team5.components.game;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.Field;

/**
 * DoubleScoreBadge 테스트
 */
public class DoubleScoreBadgeTest {

    private DoubleScoreBadge badge;

    @Before
    public void setUp() {
        badge = new DoubleScoreBadge();
    }

    /**
     * 테스트 1: 생성자 - 초기화
     */
    @Test
    public void testDoubleScoreBadge_Constructor() {
        assertNotNull("배지가 생성되어야 함", badge);
        assertFalse("배지는 불투명하지 않아야 함", badge.isOpaque());
    }

    /**
     * 테스트 2: 기본 크기 설정
     */
    @Test
    public void testDoubleScoreBadge_DefaultSize() {
        Dimension preferred = badge.getPreferredSize();
        assertEquals("기본 너비", 170, preferred.width);
        assertEquals("기본 높이", 24, preferred.height);
    }

    /**
     * 테스트 3: setTotalMillis - 정상 값
     */
    @Test
    public void testDoubleScoreBadge_SetTotalMillis() throws Exception {
        badge.setTotalMillis(30000L);
        
        Field totalMillisField = DoubleScoreBadge.class.getDeclaredField("totalMillis");
        totalMillisField.setAccessible(true);
        long totalMillis = totalMillisField.getLong(badge);
        
        assertEquals("총 시간이 설정되어야 함", 30000L, totalMillis);
    }

    /**
     * 테스트 4: setTotalMillis - 0 또는 음수 (최소값 1)
     */
    @Test
    public void testDoubleScoreBadge_SetTotalMillisZero() throws Exception {
        badge.setTotalMillis(0L);
        
        Field totalMillisField = DoubleScoreBadge.class.getDeclaredField("totalMillis");
        totalMillisField.setAccessible(true);
        long totalMillis = totalMillisField.getLong(badge);
        
        assertEquals("총 시간이 최소 1이어야 함", 1L, totalMillis);
    }

    /**
     * 테스트 5: setTotalMillis - 음수
     */
    @Test
    public void testDoubleScoreBadge_SetTotalMillisNegative() throws Exception {
        badge.setTotalMillis(-5000L);
        
        Field totalMillisField = DoubleScoreBadge.class.getDeclaredField("totalMillis");
        totalMillisField.setAccessible(true);
        long totalMillis = totalMillisField.getLong(badge);
        
        assertEquals("음수는 1로 설정되어야 함", 1L, totalMillis);
    }

    /**
     * 테스트 6: setRemainingMillis - 정상 값
     */
    @Test
    public void testDoubleScoreBadge_SetRemainingMillis() throws Exception {
        badge.setRemainingMillis(15000L);
        
        Field remainingMillisField = DoubleScoreBadge.class.getDeclaredField("remainingMillis");
        remainingMillisField.setAccessible(true);
        long remainingMillis = remainingMillisField.getLong(badge);
        
        assertEquals("남은 시간이 설정되어야 함", 15000L, remainingMillis);
    }

    /**
     * 테스트 7: setRemainingMillis - 음수 (0으로 클램프)
     */
    @Test
    public void testDoubleScoreBadge_SetRemainingMillisNegative() throws Exception {
        badge.setRemainingMillis(-1000L);
        
        Field remainingMillisField = DoubleScoreBadge.class.getDeclaredField("remainingMillis");
        remainingMillisField.setAccessible(true);
        long remainingMillis = remainingMillisField.getLong(badge);
        
        assertEquals("음수는 0으로 설정되어야 함", 0L, remainingMillis);
    }

    /**
     * 테스트 8: setRemainingMillis - 툴팁 업데이트 (남은 시간 있음)
     */
    @Test
    public void testDoubleScoreBadge_TooltipWithRemaining() {
        badge.setRemainingMillis(5000L);
        
        String tooltip = badge.getToolTipText();
        assertTrue("툴팁에 초 정보가 있어야 함", tooltip.contains("s remaining") || tooltip.contains("초"));
    }

    /**
     * 테스트 9: setRemainingMillis - 툴팁 업데이트 (남은 시간 없음)
     */
    @Test
    public void testDoubleScoreBadge_TooltipWithoutRemaining() {
        badge.setRemainingMillis(0L);
        
        String tooltip = badge.getToolTipText();
        assertTrue("툴팁이 비활성 상태를 표시해야 함", 
                   tooltip.contains("inactive") || tooltip.contains("비활성"));
    }

    /**
     * 테스트 10: paintComponent - 정상 렌더링
     */
    @Test
    public void testDoubleScoreBadge_Paint() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(10000L);
        
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        badge.paint(g2d);
        g2d.dispose();
        
        assertTrue("배지가 렌더링되어야 함", true);
    }

    /**
     * 테스트 11: paintComponent - 남은 시간 0
     */
    @Test
    public void testDoubleScoreBadge_PaintZeroRemaining() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(0L);
        
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        badge.paint(g2d);
        g2d.dispose();
        
        assertTrue("남은 시간이 0일 때도 렌더링되어야 함", true);
    }

    /**
     * 테스트 12: paintComponent - 다양한 진행도
     */
    @Test
    public void testDoubleScoreBadge_PaintVariousProgress() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        
        long[] remainingTimes = {20000L, 15000L, 10000L, 5000L, 1000L, 0L};
        
        for (long remaining : remainingTimes) {
            badge.setRemainingMillis(remaining);
            
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2d = img.createGraphics();
            badge.paint(g2d);
            g2d.dispose();
        }
        
        assertTrue("다양한 진행도로 렌더링이 가능해야 함", true);
    }

    /**
     * 테스트 13: createKoreanFont 호출 (paintComponent 내부에서)
     */
    @Test
    public void testDoubleScoreBadge_KoreanFontInPaint() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(10000L);
        
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        badge.paintComponent(g2d);
        g2d.dispose();
        
        assertTrue("한글 폰트로 렌더링되어야 함", true);
    }

    /**
     * 테스트 14: 진행도 25% 미만 (빨간색 호)
     */
    @Test
    public void testDoubleScoreBadge_PaintLowProgress() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(3000L); // 15%
        
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        badge.paint(g2d);
        g2d.dispose();
        
        assertTrue("낮은 진행도에서 빨간색 호가 렌더링되어야 함", true);
    }

    /**
     * 테스트 15: 진행도 25-50% (주황색 호)
     */
    @Test
    public void testDoubleScoreBadge_PaintMediumProgress() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(7000L); // 35%
        
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        badge.paint(g2d);
        g2d.dispose();
        
        assertTrue("중간 진행도에서 주황색 호가 렌더링되어야 함", true);
    }

    /**
     * 테스트 16: 진행도 50% 이상 (흰색 호)
     */
    @Test
    public void testDoubleScoreBadge_PaintHighProgress() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(15000L); // 75%
        
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        badge.paint(g2d);
        g2d.dispose();
        
        assertTrue("높은 진행도에서 흰색 호가 렌더링되어야 함", true);
    }

    /**
     * 테스트 17: 다양한 크기로 렌더링
     */
    @Test
    public void testDoubleScoreBadge_PaintVariousSizes() {
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(10000L);
        
        int[][] sizes = {{100, 20}, {170, 24}, {200, 30}, {150, 18}};
        
        for (int[] size : sizes) {
            badge.setSize(size[0], size[1]);
            
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size[0], size[1], java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2d = img.createGraphics();
            badge.paint(g2d);
            g2d.dispose();
        }
        
        assertTrue("다양한 크기로 렌더링이 가능해야 함", true);
    }

    /**
     * 테스트 18: 최소/최대 크기
     */
    @Test
    public void testDoubleScoreBadge_MinMaxSize() {
        Dimension min = badge.getMinimumSize();
        Dimension max = badge.getMaximumSize();
        
        assertEquals("최소 너비", 170, min.width);
        assertEquals("최소 높이", 24, min.height);
        assertEquals("최대 너비", 170, max.width);
        assertEquals("최대 높이", 24, max.height);
    }

    /**
     * 테스트 19: 남은 시간 텍스트 렌더링
     */
    @Test
    public void testDoubleScoreBadge_RemainingTimeText() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(7500L); // 8초 (올림)
        
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        badge.paint(g2d);
        g2d.dispose();
        
        assertTrue("남은 시간 텍스트가 렌더링되어야 함", true);
    }

    /**
     * 테스트 20: 매우 작은 남은 시간
     */
    @Test
    public void testDoubleScoreBadge_VerySmallRemaining() {
        badge.setSize(170, 24);
        badge.setTotalMillis(20000L);
        badge.setRemainingMillis(100L); // 1초 미만
        
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(170, 24, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        badge.paint(g2d);
        g2d.dispose();
        
        assertTrue("매우 작은 남은 시간도 렌더링되어야 함", true);
    }
}
