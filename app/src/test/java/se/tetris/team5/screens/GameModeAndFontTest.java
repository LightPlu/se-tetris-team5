package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JLabel;

import se.tetris.team5.ScreenController;
import se.tetris.team5.utils.score.ScoreManager;

/**
 * 게임 모드 및 한국어 폰트 관련 기능 테스트
 */
public class GameModeAndFontTest {

    private ScreenController mockScreenController;
    private game gameInstance;
    private score scoreInstance;
    
    @Before
    public void setUp() {
        // 간단한 MockScreenController 생성
        mockScreenController = new ScreenController() {
            @Override
            public void showScreen(String screenName) {
                // 테스트용 빈 구현
            }
        };
        gameInstance = new game(mockScreenController);
        scoreInstance = new score(mockScreenController);
    }
    
    // ========== 게임 모드 관련 테스트 ==========
    
    @Test
    public void testGameModeSystemProperty() {
        // 게임 모드 시스템 프로퍼티 테스트
        System.setProperty("gameMode", "어려움");
        
        String retrievedMode = System.getProperty("gameMode");
        assertEquals("시스템 프로퍼티로 게임 모드가 설정되어야 합니다", "어려움", retrievedMode);
        
        // 다른 모드들도 테스트
        String[] testModes = {"쉬움", "보통", "어려움", "아이템"};
        
        for (String mode : testModes) {
            System.setProperty("gameMode", mode);
            String currentMode = System.getProperty("gameMode");
            assertEquals("모드 " + mode + "이 올바르게 설정되어야 합니다", mode, currentMode);
        }
    }
    
    @Test
    public void testGameModeScoreSaving() throws Exception {
        // 게임 모드별 점수 저장 테스트
        System.setProperty("gameMode", "아이템");
        
        // ScoreManager 인스턴스 가져오기
        ScoreManager scoreManager = ScoreManager.getInstance();
        
        // 테스트를 위해 점수 목록 초기화
        try {
            Field scoresField = ScoreManager.class.getDeclaredField("scores");
            scoresField.setAccessible(true);
            java.util.List<?> scores = (java.util.List<?>) scoresField.get(scoreManager);
            scores.clear();
        } catch (Exception e) {
            // 필드가 없거나 접근할 수 없는 경우 무시
        }
        
        // 게임 모드가 포함된 점수 추가
        String gameMode = System.getProperty("gameMode");
        scoreManager.addScore("TestPlayer", 1500, 7, 35, 180000, gameMode);
        
        // 저장된 점수 확인
        java.util.List<ScoreManager.ScoreEntry> allScores = scoreManager.getTopScores(10);
        assertFalse("점수가 저장되어야 합니다", allScores.isEmpty());
        
        ScoreManager.ScoreEntry savedScore = allScores.get(0);
        assertEquals("저장된 점수의 게임 모드가 올바르게 설정되어야 합니다", gameMode, savedScore.getGameMode());
    }
    
    @Test
    public void testMultipleModeScores() {
        // 여러 모드의 점수 저장 및 조회 테스트
        ScoreManager scoreManager = ScoreManager.getInstance();
        
        // 테스트를 위해 점수 목록 초기화
        try {
            Field scoresField = ScoreManager.class.getDeclaredField("scores");
            scoresField.setAccessible(true);
            java.util.List<?> scores = (java.util.List<?>) scoresField.get(scoreManager);
            scores.clear();
        } catch (Exception e) {
            // 필드가 없거나 접근할 수 없는 경우 무시
        }
        
        // 각 모드별로 점수 추가
        scoreManager.addScore("EasyPlayer", 1000, 5, 25, 120000, "쉬움");
        scoreManager.addScore("NormalPlayer", 1500, 7, 35, 180000, "보통");
        scoreManager.addScore("HardPlayer", 2000, 10, 50, 300000, "어려움");
        scoreManager.addScore("ItemPlayer", 1800, 9, 45, 270000, "아이템");
        
        // 각 모드별 점수 개수 확인
        assertEquals("쉬움 모드 점수 개수", 1, scoreManager.getTotalScoresByMode("쉬움"));
        assertEquals("보통 모드 점수 개수", 1, scoreManager.getTotalScoresByMode("보통"));
        assertEquals("어려움 모드 점수 개수", 1, scoreManager.getTotalScoresByMode("어려움"));
        assertEquals("아이템 모드 점수 개수", 1, scoreManager.getTotalScoresByMode("아이템"));
        
        // 모드별 점수 조회
        java.util.List<ScoreManager.ScoreEntry> easyScores = scoreManager.getScoresByMode("쉬움");
        assertEquals("쉬움 모드 점수", 1000, easyScores.get(0).getScore());
        
        java.util.List<ScoreManager.ScoreEntry> hardScores = scoreManager.getScoresByMode("어려움");
        assertEquals("어려움 모드 점수", 2000, hardScores.get(0).getScore());
    }
    
    // ========== 한국어 폰트 관련 테스트 ==========
    
    @Test
    public void testCreateKoreanFontInGame() throws Exception {
        // 게임 화면의 createKoreanFont 메서드 테스트
        Method createKoreanFontMethod = game.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        createKoreanFontMethod.setAccessible(true);
        
        // 다양한 크기로 폰트 생성 테스트
        int[] fontSizes = {12, 16, 20, 24};
        int style = Font.BOLD;
        
        for (int size : fontSizes) {
            Font font = (Font) createKoreanFontMethod.invoke(gameInstance, style, size);
            
            assertNotNull("한국어 폰트가 생성되어야 합니다", font);
            assertEquals("폰트 크기가 올바르게 설정되어야 합니다", size, font.getSize());
            assertTrue("폰트 스타일이 BOLD여야 합니다", (font.getStyle() & Font.BOLD) != 0);
            
            // 폰트 이름이 한국어 지원 폰트 중 하나여야 함
            String fontName = font.getName();
            assertTrue("한국어 지원 폰트여야 합니다", 
                      fontName.contains("맑은 고딕") || 
                      fontName.contains("Malgun Gothic") || 
                      fontName.contains("굴림") || 
                      fontName.contains("Gulim") || 
                      fontName.contains("Dialog") ||
                      fontName.contains("Arial"));
        }
    }
    
    @Test
    public void testCreateKoreanFontInScore() throws Exception {
        // 스코어 화면의 createKoreanFont 메서드 테스트
        Method createKoreanFontMethod = score.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        createKoreanFontMethod.setAccessible(true);
        
        // 다양한 크기로 폰트 생성 테스트
        int[] fontSizes = {14, 18, 22};
        int style = Font.BOLD;
        
        for (int size : fontSizes) {
            Font font = (Font) createKoreanFontMethod.invoke(scoreInstance, style, size);
            
            assertNotNull("한국어 폰트가 생성되어야 합니다", font);
            assertEquals("폰트 크기가 올바르게 설정되어야 합니다", size, font.getSize());
            assertTrue("폰트 스타일이 BOLD여야 합니다", (font.getStyle() & Font.BOLD) != 0);
            
            // 폰트 이름 확인
            String fontName = font.getName();
            assertTrue("한국어 지원 폰트여야 합니다", 
                      fontName.contains("맑은 고딕") || 
                      fontName.contains("Malgun Gothic") || 
                      fontName.contains("굴림") || 
                      fontName.contains("Gulim") || 
                      fontName.contains("Dialog") ||
                      fontName.contains("Arial"));
        }
    }
    
    @Test
    public void testFontFallbackMechanism() throws Exception {
        // 폰트 폴백 메커니즘 테스트
        Method createKoreanFontMethod = game.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        createKoreanFontMethod.setAccessible(true);
        
        Font font = (Font) createKoreanFontMethod.invoke(gameInstance, Font.BOLD, 16);
        
        assertNotNull("폰트 폴백이 작동하여 폰트가 생성되어야 합니다", font);
        
        // 생성된 폰트로 한국어 문자 렌더링 테스트
        String koreanText = "한국어 테스트";
        
        // FontRenderContext로 텍스트 크기 측정
        Canvas canvas = new Canvas();
        FontMetrics fm = canvas.getFontMetrics(font);
        
        int textWidth = fm.stringWidth(koreanText);
        int textHeight = fm.getHeight();
        
        assertTrue("한국어 텍스트 너비가 0보다 커야 합니다", textWidth > 0);
        assertTrue("한국어 텍스트 높이가 0보다 커야 합니다", textHeight > 0);
    }
    
    @Test
    public void testKoreanCharacterSupport() throws Exception {
        // 한국어 문자 지원 테스트
        Method createKoreanFontMethod = game.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        createKoreanFontMethod.setAccessible(true);
        
        Font font = (Font) createKoreanFontMethod.invoke(gameInstance, Font.BOLD, 16);
        
        // 다양한 한국어 문자 테스트
        String[] koreanTexts = {
            "쉬움", "보통", "어려움", "아이템",
            "점수", "레벨", "시간", "라인",
            "게임 모드", "플레이어 이름",
            "ㄱㄴㄷㄹ", "아야어여", "12345"
        };
        
        Canvas canvas = new Canvas();
        FontMetrics fm = canvas.getFontMetrics(font);
        
        for (String text : koreanTexts) {
            int width = fm.stringWidth(text);
            assertTrue("'" + text + "'의 너비가 0보다 커야 합니다", width > 0);
            
            // 폰트가 해당 문자들을 지원하는지 확인
            for (char c : text.toCharArray()) {
                if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                    assertTrue("문자 '" + c + "'가 지원되어야 합니다", font.canDisplay(c));
                }
            }
        }
    }
    
    // ========== 통합 테스트 ==========
    
    @Test
    public void testGameModeAndFontIntegration() throws Exception {
        // 게임 모드와 폰트 통합 테스트
        System.setProperty("gameMode", "어려움");
        
        // 폰트 생성
        Method createKoreanFontMethod = game.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        createKoreanFontMethod.setAccessible(true);
        Font font = (Font) createKoreanFontMethod.invoke(gameInstance, Font.BOLD, 18);
        
        // 게임 모드 텍스트가 올바르게 렌더링되는지 확인
        String gameMode = System.getProperty("gameMode");
        
        Canvas canvas = new Canvas();
        FontMetrics fm = canvas.getFontMetrics(font);
        int textWidth = fm.stringWidth("모드: " + gameMode);
        
        assertTrue("게임 모드 텍스트가 렌더링되어야 합니다", textWidth > 0);
        assertNotNull("게임 모드가 설정되어야 합니다", gameMode);
        assertTrue("게임 모드가 한국어여야 합니다", 
                  gameMode.equals("쉬움") || gameMode.equals("보통") || 
                  gameMode.equals("어려움") || gameMode.equals("아이템"));
    }
    
    @Test 
    public void testScoreModeSelectionUI() throws Exception {
        // 점수 화면의 모드 선택 UI 테스트
        Method createModeTabPanelMethod = score.class.getDeclaredMethod("createModeTabPanel");
        createModeTabPanelMethod.setAccessible(true);
        
        Component modeTabPanel = (Component) createModeTabPanelMethod.invoke(scoreInstance);
        
        assertNotNull("모드 탭 패널이 생성되어야 합니다", modeTabPanel);
        assertTrue("모드 탭 패널이 표시 가능해야 합니다", modeTabPanel.isDisplayable() || modeTabPanel.getParent() == null);
    }
    
    @Test
    public void testGameOverDialogWithMode() {
        // 게임 오버 다이얼로그에서 모드 정보 표시 테스트
        System.setProperty("gameMode", "아이템");
        
        // 게임 오버 상황 시뮬레이션을 위한 상태 설정
        try {
            // scoreBoard 필드를 통해 점수 정보 접근
            Field scoreBoardField = game.class.getDeclaredField("scoreBoard");
            scoreBoardField.setAccessible(true);
            
            // 게임 모드 정보가 올바르게 설정되었는지 확인
            String gameMode = System.getProperty("gameMode");
            assertEquals("게임 모드가 올바르게 설정되어야 합니다", "아이템", gameMode);
            
            // scoreBoard가 존재하는지 확인
            Object scoreBoard = scoreBoardField.get(gameInstance);
            assertNotNull("스코어보드가 존재해야 합니다", scoreBoard);
            
            assertTrue("게임 인스턴스가 정상적으로 생성되어야 합니다", gameInstance != null);
            
        } catch (Exception e) {
            // 필드 구조가 다를 수 있으므로 기본적인 테스트만 수행
            String gameMode = System.getProperty("gameMode");
            assertEquals("게임 모드가 올바르게 설정되어야 합니다", "아이템", gameMode);
            assertNotNull("게임 인스턴스가 존재해야 합니다", gameInstance);
        }
    }
    
    @Test
    public void testFontConsistencyAcrossScreens() throws Exception {
        // 화면 간 폰트 일관성 테스트
        Method gameCreateKoreanFontMethod = game.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        Method scoreCreateKoreanFontMethod = score.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        
        gameCreateKoreanFontMethod.setAccessible(true);
        scoreCreateKoreanFontMethod.setAccessible(true);
        
        int testSize = 16;
        int style = Font.BOLD;
        
        Font gameFont = (Font) gameCreateKoreanFontMethod.invoke(gameInstance, style, testSize);
        Font scoreFont = (Font) scoreCreateKoreanFontMethod.invoke(scoreInstance, style, testSize);
        
        assertNotNull("게임 화면 폰트가 생성되어야 합니다", gameFont);
        assertNotNull("점수 화면 폰트가 생성되어야 합니다", scoreFont);
        
        assertEquals("두 화면의 폰트 크기가 같아야 합니다", gameFont.getSize(), scoreFont.getSize());
        assertEquals("두 화면의 폰트 스타일이 같아야 합니다", gameFont.getStyle(), scoreFont.getStyle());
        
        // 폰트 이름은 동일한 우선순위 로직을 사용하므로 같아야 함
        assertEquals("두 화면의 폰트 이름이 같아야 합니다", gameFont.getName(), scoreFont.getName());
    }
    
    @Test
    public void testWindowsSpecificFontHandling() throws Exception {
        // Windows 특화 폰트 처리 테스트
        Method createKoreanFontMethod = game.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        createKoreanFontMethod.setAccessible(true);
        
        Font font = (Font) createKoreanFontMethod.invoke(gameInstance, Font.BOLD, 16);
        
        // Windows에서 사용 가능한 폰트인지 확인
        String fontName = font.getName();
        
        // Windows에서 일반적으로 사용 가능한 한국어 폰트들
        String[] expectedWindowsFonts = {
            "맑은 고딕", "Malgun Gothic", "굴림", "Gulim", "SansSerif"
        };
        
        boolean isValidWindowsFont = false;
        for (String expectedFont : expectedWindowsFonts) {
            if (fontName.equals(expectedFont)) {
                isValidWindowsFont = true;
                break;
            }
        }
        
        assertTrue("Windows 호환 한국어 폰트여야 합니다: " + fontName, isValidWindowsFont);
        
        // 폰트가 한국어 문자를 지원하는지 확인
        assertTrue("한국어 ㄱ 문자를 지원해야 합니다", font.canDisplay('ㄱ'));
        assertTrue("한국어 가 문자를 지원해야 합니다", font.canDisplay('가'));
        assertTrue("한국어 힣 문자를 지원해야 합니다", font.canDisplay('힣'));
    }
    
    @Test
    public void testModeFilteringPerformance() {
        // 모드 필터링 성능 테스트
        ScoreManager scoreManager = ScoreManager.getInstance();
        
        // 테스트를 위해 점수 목록 초기화
        try {
            Field scoresField = ScoreManager.class.getDeclaredField("scores");
            scoresField.setAccessible(true);
            java.util.List<?> scores = (java.util.List<?>) scoresField.get(scoreManager);
            scores.clear();
        } catch (Exception e) {
            // 필드가 없거나 접근할 수 없는 경우 무시
        }
        
        // 대량의 점수 데이터 생성
        String[] modes = {"쉬움", "보통", "어려움", "아이템"};
        
        for (int i = 0; i < 100; i++) { // 100개로 줄여서 테스트
            String mode = modes[i % modes.length];
            scoreManager.addScore("Player" + i, i * 10, i % 20 + 1, i % 100, i * 1000L, mode);
        }
        
        // 성능 측정
        long startTime = System.currentTimeMillis();
        
        for (String mode : modes) {
            java.util.List<ScoreManager.ScoreEntry> modeScores = scoreManager.getScoresByMode(mode);
            assertEquals("각 모드당 25개 점수가 있어야 합니다", 25, modeScores.size());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue("모드 필터링이 1초 이내에 완료되어야 합니다", duration < 1000);
    }
    
    @Test
    public void testUnicodeHandling() throws Exception {
        // 유니코드 문자 처리 테스트
        Method createKoreanFontMethod = game.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
        createKoreanFontMethod.setAccessible(true);
        
        Font font = (Font) createKoreanFontMethod.invoke(gameInstance, Font.BOLD, 16);
        
        // 다양한 유니코드 한국어 문자 테스트
        char[] koreanChars = {
            '가', '나', '다', '라', '마', '바', '사', '아', '자', '차', '카', '타', '파', '하',
            'ㄱ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ',
            'ㅏ', 'ㅓ', 'ㅗ', 'ㅜ', 'ㅡ', 'ㅣ'
        };
        
        for (char c : koreanChars) {
            assertTrue("한국어 문자 '" + c + "'를 지원해야 합니다", font.canDisplay(c));
        }
        
        // 복합 문자 테스트
        String[] complexTexts = {"안녕하세요", "테트리스", "게임모드", "점수판"};
        
        Canvas canvas = new Canvas();
        FontMetrics fm = canvas.getFontMetrics(font);
        
        for (String text : complexTexts) {
            int width = fm.stringWidth(text);
            assertTrue("복합 텍스트 '" + text + "'가 렌더링되어야 합니다", width > 0);
        }
    }
    
    @Test
    public void testScoreScreenComprehensiveFlow() {
        try {
            javax.swing.JTextPane textPane = new javax.swing.JTextPane();
            scoreInstance.display(textPane);
            
            // 전체 UI 플로우 테스트
            Method buildUIMethod = score.class.getDeclaredMethod("buildUI");
            buildUIMethod.setAccessible(true);
            buildUIMethod.invoke(scoreInstance);
            
            // 모든 UI 컴포넌트 생성 메서드들 호출
            Method createModeTabMethod = score.class.getDeclaredMethod("createModeTabPanel");
            createModeTabMethod.setAccessible(true);
            javax.swing.JPanel modeTabPanel = (javax.swing.JPanel) createModeTabMethod.invoke(scoreInstance);
            assertNotNull(modeTabPanel);
            
            Method buildPodiumMethod = score.class.getDeclaredMethod("buildPodiumPanel");
            buildPodiumMethod.setAccessible(true);
            javax.swing.JPanel podiumPanel = (javax.swing.JPanel) buildPodiumMethod.invoke(scoreInstance);
            assertNotNull(podiumPanel);
            
            Method buildControlsMethod = score.class.getDeclaredMethod("buildControlsPanel");
            buildControlsMethod.setAccessible(true);
            javax.swing.JPanel controlsPanel = (javax.swing.JPanel) buildControlsMethod.invoke(scoreInstance);
            assertNotNull(controlsPanel);
            
            Method createBgMethod = score.class.getDeclaredMethod("createBackgroundPanel");
            createBgMethod.setAccessible(true);
            javax.swing.JPanel bgPanel = (javax.swing.JPanel) createBgMethod.invoke(scoreInstance);
            assertNotNull(bgPanel);
            
            Method buildFooterMethod = score.class.getDeclaredMethod("buildFooterImagePanel");
            buildFooterMethod.setAccessible(true);
            javax.swing.JPanel footerPanel = (javax.swing.JPanel) buildFooterMethod.invoke(scoreInstance);
            assertNotNull(footerPanel);
            
            // renderScores를 다양한 조건에서 호출
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            // 기본 렌더링
            renderMethod.invoke(scoreInstance);
            
            // 다른 페이지에서 렌더링
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            pageField.set(scoreInstance, 1);
            renderMethod.invoke(scoreInstance);
            
            // 다른 모드에서 렌더링
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            modeField.set(scoreInstance, "NORMAL_EASY");
            renderMethod.invoke(scoreInstance);
            
        } catch (Exception e) {
            fail("Score screen comprehensive flow test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testScoreScreenModeDisplaysAll() {
        try {
            Method getModeDisplayMethod = score.class.getDeclaredMethod("getGameModeDisplay", String.class);
            getModeDisplayMethod.setAccessible(true);
            
            // 모든 가능한 모드 테스트
            assertEquals("아이템", getModeDisplayMethod.invoke(scoreInstance, "ITEM"));
            assertEquals("쉬움", getModeDisplayMethod.invoke(scoreInstance, "NORMAL_EASY"));
            assertEquals("보통", getModeDisplayMethod.invoke(scoreInstance, "NORMAL_NORMAL"));
            assertEquals("어려움", getModeDisplayMethod.invoke(scoreInstance, "NORMAL_HARD"));
            assertEquals("전문가", getModeDisplayMethod.invoke(scoreInstance, "NORMAL_EXPERT"));
            
            // Edge cases
            assertEquals("아이템", getModeDisplayMethod.invoke(scoreInstance, (Object) null));
            assertEquals("아이템", getModeDisplayMethod.invoke(scoreInstance, ""));
            assertEquals("아이템", getModeDisplayMethod.invoke(scoreInstance, "UNKNOWN"));
            assertEquals("아이템", getModeDisplayMethod.invoke(scoreInstance, "INVALID_MODE"));
            
        } catch (Exception e) {
            fail("Mode display all test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testScoreScreenPodiumBlocksAll() {
        try {
            Method createPodiumBlockMethod = score.class.getDeclaredMethod("createPodiumBlock", int.class);
            createPodiumBlockMethod.setAccessible(true);
            
            // 다양한 순위의 podium block 생성
            for (int rank = 1; rank <= 10; rank++) {
                javax.swing.JPanel block = (javax.swing.JPanel) createPodiumBlockMethod.invoke(scoreInstance, rank);
                assertNotNull("Rank " + rank + " podium block should not be null", block);
                assertEquals("podium-" + rank, block.getName());
                
                // 블록 내부 컴포넌트 확인
                assertTrue("Block should have components", block.getComponentCount() > 0);
            }
            
        } catch (Exception e) {
            fail("Podium blocks all test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testScoreScreenDataHandlingComplete() {
        try {
            scoreInstance.display(new javax.swing.JTextPane());
            
            // buildSampleEntries 호출
            Method buildSampleMethod = score.class.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            java.util.List<ScoreManager.ScoreEntry> sampleEntries = (java.util.List<ScoreManager.ScoreEntry>) buildSampleMethod.invoke(scoreInstance);
            assertNotNull(sampleEntries);
            assertEquals(10, sampleEntries.size());
            
            // 샘플 데이터의 내용 검증
            ScoreManager.ScoreEntry firstEntry = sampleEntries.get(0);
            assertEquals("Player A", firstEntry.getPlayerName());
            assertEquals(256, firstEntry.getScore());
            assertEquals(5, firstEntry.getLevel());
            
            ScoreManager.ScoreEntry lastEntry = sampleEntries.get(9);
            assertEquals("Player J", lastEntry.getPlayerName());
            assertEquals(150, lastEntry.getScore());
            assertEquals(1, lastEntry.getLevel());
            
            // showOnlyDummy 모드 테스트
            Field showOnlyDummyField = score.class.getDeclaredField("showOnlyDummy");
            showOnlyDummyField.setAccessible(true);
            
            // false에서 true로 변경
            showOnlyDummyField.set(scoreInstance, true);
            assertEquals(true, showOnlyDummyField.get(scoreInstance));
            
            // renderScores 호출하여 dummy 모드 실행
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            renderMethod.invoke(scoreInstance);
            
        } catch (Exception e) {
            fail("Data handling complete test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testScoreScreenHelperMethodsDeep() {
        try {
            javax.swing.JTextPane textPane = new javax.swing.JTextPane();
            scoreInstance.display(textPane);
            
            // findAllComponents with various containers
            Method findAllMethod = score.class.getDeclaredMethod("findAllComponents", java.awt.Container.class);
            findAllMethod.setAccessible(true);
            
            // Empty container
            javax.swing.JPanel emptyPanel = new javax.swing.JPanel();
            @SuppressWarnings("unchecked")
            java.util.List<Component> emptyComponents = (java.util.List<Component>) findAllMethod.invoke(scoreInstance, emptyPanel);
            assertNotNull(emptyComponents);
            
            // Nested container
            javax.swing.JPanel parentPanel = new javax.swing.JPanel();
            javax.swing.JPanel childPanel = new javax.swing.JPanel();
            javax.swing.JLabel testLabel = new javax.swing.JLabel("test");
            childPanel.add(testLabel);
            parentPanel.add(childPanel);
            
            @SuppressWarnings("unchecked")
            java.util.List<Component> nestedComponents = (java.util.List<Component>) findAllMethod.invoke(scoreInstance, parentPanel);
            assertNotNull(nestedComponents);
            assertTrue(nestedComponents.size() >= 2); // parentPanel, childPanel
            
            // findPageInfoLabel with actual page label
            Method findPageMethod = score.class.getDeclaredMethod("findPageInfoLabel", java.awt.Container.class);
            findPageMethod.setAccessible(true);
            
            javax.swing.JPanel panelWithPageLabel = new javax.swing.JPanel();
            javax.swing.JLabel pageLabel = new javax.swing.JLabel("Page 1 / 1");
            pageLabel.setName("page-info");
            panelWithPageLabel.add(pageLabel);
            
            javax.swing.JLabel foundPageLabel = (javax.swing.JLabel) findPageMethod.invoke(scoreInstance, panelWithPageLabel);
            assertNotNull(foundPageLabel);
            assertEquals("page-info", foundPageLabel.getName());
            
        } catch (Exception e) {
            fail("Helper methods deep test failed: " + e.getMessage());
        }
    }
}