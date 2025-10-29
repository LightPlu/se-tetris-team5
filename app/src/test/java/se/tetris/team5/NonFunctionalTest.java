package se.tetris.team5;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import se.tetris.team5.screens.game;
import se.tetris.team5.utils.setting.GameSettings;
import se.tetris.team5.ScreenController;

/**
 * 비기능적 요구사항 테스트
 * - 성능 (Performance)
 * - 사용성 (Usability) 
 * - 접근성 (Accessibility)
 * - 안정성 (Reliability)
 */
public class NonFunctionalTest {
    
    private ScreenController mockScreenController;
    private game gameInstance;
    private GameSettings gameSettings;
    
    @Before
    public void setUp() {
        mockScreenController = new ScreenController() {
            @Override
            public void showScreen(String screenName) {
                // 테스트용 빈 구현
            }
        };
        gameInstance = new game(mockScreenController);
        gameSettings = GameSettings.getInstance();
    }
    
    /**
     * 성능 요구사항: 키 입력 응답시간이 50ms 이하여야 함
     */
    @Test
    public void testKeyInputResponseTime() {
        long totalTime = 0;
        int iterations = 100;
        
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            
            // 키 입력 시뮬레이션
            KeyEvent keyEvent = new KeyEvent(gameInstance, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
            gameInstance.keyPressed(keyEvent);
            
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        
        long averageTimeMs = (totalTime / iterations) / 1_000_000; // 나노초를 밀리초로 변환
        assertTrue("키 입력 평균 응답시간이 50ms를 초과했습니다: " + averageTimeMs + "ms", 
                   averageTimeMs <= 50);
    }
    
    /**
     * 접근성 요구사항: 색맹 모드에서 모든 블록 색상이 구분 가능해야 함
     */
    @Test
    public void testColorblindAccessibility() {
        // 색맹 모드 활성화
        gameSettings.setColorblindMode(true);
        
        String[] blockTypes = {"I", "O", "T", "L", "J", "S", "Z", "W"};
        Set<Color> usedColors = new HashSet<>();
        
        for (String blockType : blockTypes) {
            Color blockColor = gameSettings.getColorForBlock(blockType);
            assertNotNull("블록 색상이 null이어서는 안됩니다: " + blockType, blockColor);
            
            assertFalse("색맹 모드에서 중복된 색상이 발견되었습니다. 블록타입: " + blockType + 
                       ", 색상: " + blockColor, usedColors.contains(blockColor));
            
            usedColors.add(blockColor);
        }
        
        assertEquals("8개의 서로 다른 색상이 있어야 합니다", 8, usedColors.size());
    }
    
    /**
     * 접근성 요구사항: 색상 대비율이 충분해야 함 (WCAG 기준)
     */
    @Test
    public void testColorContrast() {
        gameSettings.setColorblindMode(true);
        Color backgroundColor = Color.BLACK;
        
        String[] blockTypes = {"I", "O", "T", "L", "J", "S", "Z"};
        
        for (String blockType : blockTypes) {
            Color blockColor = gameSettings.getColorForBlock(blockType);
            double contrastRatio = calculateContrastRatio(blockColor, backgroundColor);
            
            assertTrue("블록 " + blockType + "의 색상 대비율이 부족합니다 (대비율: " + 
                      String.format("%.2f", contrastRatio) + ", 최소 4.5 필요)", 
                      contrastRatio >= 4.5);
        }
    }
    
    /**
     * 성능 요구사항: 메모리 사용량이 과도하지 않아야 함
     */
    @Test
    public void testMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeUsage = memoryBean.getHeapMemoryUsage();
        long memoryBefore = beforeUsage.getUsed();
        
        // 게임 시뮬레이션 (블록 1000번 이동)
        for (int i = 0; i < 1000; i++) {
            // 블록 이동 시뮬레이션
            KeyEvent moveEvent = new KeyEvent(gameInstance, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
            gameInstance.keyPressed(moveEvent);
            
            // 100번마다 가비지 컬렉션 권장
            if (i % 100 == 0) {
                System.gc();
                try {
                    Thread.sleep(10); // GC 완료 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        System.gc(); // 최종 가비지 컬렉션
        try {
            Thread.sleep(100); // GC 완료 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        MemoryUsage afterUsage = memoryBean.getHeapMemoryUsage();
        long memoryAfter = afterUsage.getUsed();
        long memoryIncrease = (memoryAfter - memoryBefore) / (1024 * 1024); // MB 단위
        
        assertTrue("메모리 증가량이 과도합니다: " + memoryIncrease + "MB (최대 50MB)", 
                   memoryIncrease <= 50);
    }
    
    /**
     * 안정성 요구사항: 잘못된 입력에도 안전하게 처리해야 함
     */
    @Test
    public void testInputStability() {
        // 잘못된 키 코드들로 테스트
        int[] invalidKeyCodes = {-1, 0, 999999, Integer.MAX_VALUE, Integer.MIN_VALUE};
        
        for (int keyCode : invalidKeyCodes) {
            try {
                KeyEvent invalidEvent = new KeyEvent(gameInstance, KeyEvent.KEY_PRESSED, 
                    System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
                gameInstance.keyPressed(invalidEvent);
                // 예외가 발생하지 않으면 성공
            } catch (Exception e) {
                fail("잘못된 키 코드 " + keyCode + "에 대해 예외가 발생했습니다: " + e.getMessage());
            }
        }
    }
    
    /**
     * 사용성 요구사항: 설정 변경이 즉시 반영되어야 함
     */
    @Test
    public void testSettingsResponseiveness() {
        // 색맹 모드 토글 테스트
        boolean originalMode = gameSettings.isColorblindMode();
        
        gameSettings.setColorblindMode(!originalMode);
        assertEquals("색맹 모드 설정이 즉시 반영되어야 합니다", 
                     !originalMode, gameSettings.isColorblindMode());
        
        // 게임 속도 변경 테스트
        int originalSpeed = gameSettings.getGameSpeed();
        int newSpeed = (originalSpeed == 5) ? 1 : originalSpeed + 1;
        
        gameSettings.setGameSpeed(newSpeed);
        assertEquals("게임 속도 설정이 즉시 반영되어야 합니다", 
                     newSpeed, gameSettings.getGameSpeed());
    }
    
    /**
     * 성능 요구사항: 게임 화면 업데이트가 60fps 이상이어야 함
     */
    @Test
    public void testFrameRate() {
        int frameCount = 0;
        long startTime = System.currentTimeMillis();
        long duration = 1000; // 1초간 테스트
        
        while (System.currentTimeMillis() - startTime < duration) {
            // 화면 업데이트 시뮬레이션
            gameInstance.drawBoard();
            frameCount++;
        }
        
        long actualDuration = System.currentTimeMillis() - startTime;
        double fps = (frameCount * 1000.0) / actualDuration;
        
        assertTrue("프레임율이 60fps에 못 미칩니다: " + String.format("%.1f", fps) + "fps", 
                   fps >= 60.0);
    }
    
    /**
     * WCAG 기준 색상 대비율 계산
     */
    private double calculateContrastRatio(Color foreground, Color background) {
        double l1 = getRelativeLuminance(foreground);
        double l2 = getRelativeLuminance(background);
        
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        
        return (lighter + 0.05) / (darker + 0.05);
    }
    
    /**
     * 상대적 밝기 계산 (WCAG 기준)
     */
    private double getRelativeLuminance(Color color) {
        double r = gammaCorrect(color.getRed() / 255.0);
        double g = gammaCorrect(color.getGreen() / 255.0);
        double b = gammaCorrect(color.getBlue() / 255.0);
        
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }
    
    /**
     * 감마 보정
     */
    private double gammaCorrect(double value) {
        if (value <= 0.03928) {
            return value / 12.92;
        } else {
            return Math.pow((value + 0.055) / 1.055, 2.4);
        }
    }
}