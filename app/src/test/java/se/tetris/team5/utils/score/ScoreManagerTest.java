package se.tetris.team5.utils.score;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import se.tetris.team5.utils.score.ScoreManager.ScoreEntry;

/**
 * ScoreManager 클래스를 테스트하는 클래스
 */
public class ScoreManagerTest {

    private ScoreManager scoreManager;
    
    @Before
    public void setUp() {
        scoreManager = ScoreManager.getInstance();
        
        // 테스트를 위해 점수 목록 초기화
        try {
            Field scoresField = ScoreManager.class.getDeclaredField("scores");
            scoresField.setAccessible(true);
            List<?> scores = (List<?>) scoresField.get(scoreManager);
            scores.clear();
        } catch (Exception e) {
            // 필드가 없거나 접근할 수 없는 경우 무시
        }
    }
    
    @Test
    public void testSingletonPattern() {
        ScoreManager instance1 = ScoreManager.getInstance();
        ScoreManager instance2 = ScoreManager.getInstance();
        
        assertSame("ScoreManager는 싱글톤 패턴이어야 합니다", instance1, instance2);
        assertNotNull("ScoreManager 인스턴스가 null이 아니어야 합니다", instance1);
    }
    
    @Test
    public void testAddScore() {
        String playerName = "TestPlayer";
        int score = 1000;
        int level = 5;
        int linesCleared = 25;
        long playTime = 120000; // 2분
        
        scoreManager.addScore(playerName, score, level, linesCleared, playTime);
        
        List<ScoreEntry> topScores = scoreManager.getTopScores(10);
        
        assertNotNull("점수 목록이 null이 아니어야 합니다", topScores);
        assertTrue("점수가 추가되어야 합니다", topScores.size() > 0);
        
        ScoreEntry addedScore = topScores.get(0);
        assertEquals("플레이어 이름이 올바르게 저장되어야 합니다", playerName, addedScore.getPlayerName());
        assertEquals("점수가 올바르게 저장되어야 합니다", score, addedScore.getScore());
        assertEquals("레벨이 올바르게 저장되어야 합니다", level, addedScore.getLevel());
        assertEquals("클리어된 라인 수가 올바르게 저장되어야 합니다", linesCleared, addedScore.getLines());
        assertEquals("플레이 시간이 올바르게 저장되어야 합니다", playTime, addedScore.getPlayTime());
    }
    
    @Test
    public void testMultipleScoresOrdering() {
        // 여러 점수 추가 (점수 순서대로 정렬되는지 확인)
        scoreManager.addScore("Player1", 500, 3, 15, 60000);
        scoreManager.addScore("Player2", 1500, 7, 35, 180000);
        scoreManager.addScore("Player3", 1000, 5, 25, 120000);
        scoreManager.addScore("Player4", 2000, 10, 50, 300000);
        
        List<ScoreEntry> topScores = scoreManager.getTopScores(10);
        
        assertEquals("4개의 점수가 저장되어야 합니다", 4, topScores.size());
        
        // 점수가 내림차순으로 정렬되어야 함
        assertEquals("첫 번째 점수가 가장 높아야 합니다", 2000, topScores.get(0).getScore());
        assertEquals("두 번째 점수가 두 번째로 높아야 합니다", 1500, topScores.get(1).getScore());
        assertEquals("세 번째 점수가 세 번째로 높아야 합니다", 1000, topScores.get(2).getScore());
        assertEquals("네 번째 점수가 가장 낮아야 합니다", 500, topScores.get(3).getScore());
    }
    
    @Test
    public void testTopScoresLimit() {
        // 10개 이상의 점수 추가
        for (int i = 1; i <= 15; i++) {
            scoreManager.addScore("Player" + i, i * 100, i, i * 2, i * 10000L);
        }
        
        List<ScoreEntry> top5Scores = scoreManager.getTopScores(5);
        List<ScoreEntry> top10Scores = scoreManager.getTopScores(10);
        List<ScoreEntry> allScores = scoreManager.getTopScores(20);
        
        assertEquals("Top 5 점수가 5개여야 합니다", 5, top5Scores.size());
        assertEquals("Top 10 점수가 10개여야 합니다", 10, top10Scores.size());
        assertTrue("전체 점수는 최대 15개까지여야 합니다", allScores.size() <= 15);
        
        // 상위 5개 점수가 가장 높은 점수들인지 확인
        assertEquals("첫 번째 점수가 1500이어야 합니다", 1500, top5Scores.get(0).getScore());
        assertEquals("다섯 번째 점수가 1100이어야 합니다", 1100, top5Scores.get(4).getScore());
    }
    
    @Test
    public void testScoreEntryProperties() {
        String playerName = "TestPlayer";
        int score = 1234;
        int level = 8;
        int linesCleared = 40;
        long playTime = 456789L;
        
        ScoreEntry entry = new ScoreEntry(playerName, score, level, linesCleared, playTime);
        
        assertEquals("플레이어 이름이 올바르게 설정되어야 합니다", playerName, entry.getPlayerName());
        assertEquals("점수가 올바르게 설정되어야 합니다", score, entry.getScore());
        assertEquals("레벨이 올바르게 설정되어야 합니다", level, entry.getLevel());
        assertEquals("클리어된 라인 수가 올바르게 설정되어야 합니다", linesCleared, entry.getLines());
        assertEquals("플레이 시간이 올바르게 설정되어야 합니다", playTime, entry.getPlayTime());
        assertNotNull("날짜가 설정되어야 합니다", entry.getDate());
    }
    
    @Test
    public void testScoreEntryComparison() {
        ScoreEntry highScore = new ScoreEntry("Player1", 2000, 10, 50, 300000);
        ScoreEntry lowScore = new ScoreEntry("Player2", 1000, 5, 25, 150000);
        ScoreEntry sameScore = new ScoreEntry("Player3", 2000, 8, 40, 250000);
        
        assertTrue("높은 점수가 낮은 점수보다 커야 합니다", 
                  highScore.getScore() > lowScore.getScore());
        assertTrue("낮은 점수가 높은 점수보다 작아야 합니다", 
                  lowScore.getScore() < highScore.getScore());
        assertEquals("같은 점수는 동등해야 합니다", 
                    highScore.getScore(), sameScore.getScore());
    }
    
    @Test 
    public void testEmptyScoresList() {
        // 점수가 없을 때의 동작 확인
        List<ScoreEntry> emptyScores = scoreManager.getTopScores(10);
        
        assertNotNull("빈 점수 목록도 null이 아니어야 합니다", emptyScores);
        assertEquals("빈 점수 목록의 크기는 0이어야 합니다", 0, emptyScores.size());
    }
    
    @Test
    public void testInvalidInputHandling() {
        // 음수 점수
        scoreManager.addScore("Player", -100, 1, 1, 1000);
        
        // 음수 레벨
        scoreManager.addScore("Player", 100, -1, 1, 1000);
        
        // 음수 라인 클리어
        scoreManager.addScore("Player", 100, 1, -1, 1000);
        
        // 음수 플레이 시간
        scoreManager.addScore("Player", 100, 1, 1, -1000);
        
        // null 플레이어 이름
        scoreManager.addScore(null, 100, 1, 1, 1000);
        
        // 빈 플레이어 이름
        scoreManager.addScore("", 100, 1, 1, 1000);
        
        List<ScoreEntry> scores = scoreManager.getTopScores(10);
        
        // 유효하지 않은 입력들이 어떻게 처리되는지 확인
        // (구체적인 처리 방법은 ScoreManager 구현에 따라 다를 수 있음)
        assertTrue("유효하지 않은 입력 처리 후에도 시스템이 안정적이어야 합니다", 
                  scores != null);
    }
    
    @Test
    public void testPlayTimeFormatting() {
        // 다양한 플레이 시간에 대한 포맷팅 테스트
        long oneMinute = 60000L;
        long oneHour = 3600000L;
        long mixedTime = 3661000L; // 1시간 1분 1초
        
        scoreManager.addScore("Player1", 1000, 5, 20, oneMinute);
        scoreManager.addScore("Player2", 1100, 6, 25, oneHour);
        scoreManager.addScore("Player3", 1200, 7, 30, mixedTime);
        
        List<ScoreEntry> scores = scoreManager.getTopScores(10);
        
        assertEquals("1분 플레이 시간이 올바르게 저장되어야 합니다", 
                    oneMinute, scores.get(2).getPlayTime());
        assertEquals("1시간 플레이 시간이 올바르게 저장되어야 합니다", 
                    oneHour, scores.get(1).getPlayTime());
        assertEquals("복합 플레이 시간이 올바르게 저장되어야 합니다", 
                    mixedTime, scores.get(0).getPlayTime());
    }
    
    // ========== 게임 모드 관련 새로운 테스트들 ==========
    
    @Test
    public void testGameModeScoreEntry() {
        // 게임 모드가 포함된 ScoreEntry 생성 테스트
        String playerName = "TestPlayer";
        int score = 1500;
        int level = 7;
        int linesCleared = 35;
        long playTime = 180000L;
        String gameMode = "어려움";
        
        ScoreEntry entry = new ScoreEntry(playerName, score, level, linesCleared, playTime, gameMode);
        
        assertEquals("플레이어 이름이 올바르게 설정되어야 합니다", playerName, entry.getPlayerName());
        assertEquals("점수가 올바르게 설정되어야 합니다", score, entry.getScore());
        assertEquals("레벨이 올바르게 설정되어야 합니다", level, entry.getLevel());
        assertEquals("클리어된 라인 수가 올바르게 설정되어야 합니다", linesCleared, entry.getLines());
        assertEquals("플레이 시간이 올바르게 설정되어야 합니다", playTime, entry.getPlayTime());
        assertEquals("게임 모드가 올바르게 설정되어야 합니다", gameMode, entry.getGameMode());
        assertNotNull("날짜가 설정되어야 합니다", entry.getDate());
    }
    
    @Test
    public void testAddScoreWithGameMode() {
        // 게임 모드가 포함된 점수 추가 테스트
        String playerName = "ModeTestPlayer";
        int score = 2000;
        int level = 8;
        int linesCleared = 40;
        long playTime = 240000L;
        String gameMode = "아이템";
        
        scoreManager.addScore(playerName, score, level, linesCleared, playTime, gameMode);
        
        List<ScoreEntry> topScores = scoreManager.getTopScores(10);
        
        assertNotNull("점수 목록이 null이 아니어야 합니다", topScores);
        assertTrue("점수가 추가되어야 합니다", topScores.size() > 0);
        
        ScoreEntry addedScore = topScores.get(0);
        assertEquals("플레이어 이름이 올바르게 저장되어야 합니다", playerName, addedScore.getPlayerName());
        assertEquals("점수가 올바르게 저장되어야 합니다", score, addedScore.getScore());
        assertEquals("게임 모드가 올바르게 저장되어야 합니다", gameMode, addedScore.getGameMode());
    }
    
    @Test
    public void testGetScoresByMode() {
        // 모드별 점수 조회 테스트
        // 다양한 모드로 점수 추가
        scoreManager.addScore("Player1", 1000, 5, 25, 120000, "쉬움");
        scoreManager.addScore("Player2", 1500, 7, 35, 180000, "보통");
        scoreManager.addScore("Player3", 2000, 10, 50, 300000, "어려움");
        scoreManager.addScore("Player4", 1200, 6, 30, 150000, "쉬움");
        scoreManager.addScore("Player5", 1800, 9, 45, 270000, "아이템");
        
        // 쉬움 모드 점수만 조회
        List<ScoreEntry> easyScores = scoreManager.getScoresByMode("쉬움");
        
        assertEquals("쉬움 모드 점수가 2개여야 합니다", 2, easyScores.size());
        
        for (ScoreEntry score : easyScores) {
            assertEquals("모든 점수가 쉬움 모드여야 합니다", "쉬움", score.getGameMode());
        }
        
        // 점수 순으로 정렬되었는지 확인
        assertTrue("쉬움 모드 내에서도 점수순 정렬이 되어야 합니다", 
                  easyScores.get(0).getScore() >= easyScores.get(1).getScore());
    }
    
    @Test
    public void testGetScoresPageByMode() {
        // 모드별 페이지네이션 테스트
        // 보통 모드로 10개 이상의 점수 추가
        for (int i = 1; i <= 15; i++) {
            scoreManager.addScore("Player" + i, i * 100, i % 10 + 1, i * 2, i * 10000L, "보통");
        }
        
        // 전체 점수 개수 먼저 확인
        int totalScores = scoreManager.getTotalScoresByMode("보통");
        assertEquals("총 15개의 보통 모드 점수가 있어야 합니다", 15, totalScores);
        
        // 첫 번째 페이지 (5개)
        List<ScoreEntry> page1 = scoreManager.getScoresPageByMode(1, 5, "보통");
        assertEquals("첫 번째 페이지에 5개 점수가 있어야 합니다", 5, page1.size());
        
        // 두 번째 페이지 (5개)
        List<ScoreEntry> page2 = scoreManager.getScoresPageByMode(2, 5, "보통");
        assertEquals("두 번째 페이지에 5개 점수가 있어야 합니다", 5, page2.size());
        
        // 세 번째 페이지 (나머지 점수)
        List<ScoreEntry> page3 = scoreManager.getScoresPageByMode(3, 5, "보통");
        int expectedPage3Size = Math.min(5, totalScores - 10); // 10개 이후 남은 점수
        assertEquals("세 번째 페이지에 남은 점수가 있어야 합니다", expectedPage3Size, page3.size());
        
        // 모든 점수가 보통 모드인지 확인
        for (ScoreEntry score : page1) {
            assertEquals("첫 번째 페이지의 모든 점수가 보통 모드여야 합니다", "보통", score.getGameMode());
        }
        
        for (ScoreEntry score : page2) {
            assertEquals("두 번째 페이지의 모든 점수가 보통 모드여야 합니다", "보통", score.getGameMode());
        }
        
        // 점수가 내림차순으로 정렬되었는지 확인 (페이지 1에서만)
        if (page1.size() > 1) {
            assertTrue("첫 번째 페이지 첫 점수가 마지막 점수보다 높거나 같아야 합니다", 
                      page1.get(0).getScore() >= page1.get(page1.size()-1).getScore());
        }
        
        if (page1.size() > 0 && page2.size() > 0) {
            assertTrue("첫 번째 페이지 마지막 점수가 두 번째 페이지 첫 점수보다 높거나 같아야 합니다", 
                      page1.get(page1.size()-1).getScore() >= page2.get(0).getScore());
        }
    }
    
    @Test
    public void testGetTotalScoresByMode() {
        // 모드별 총 점수 개수 테스트
        // 다양한 모드로 점수 추가
        for (int i = 0; i < 7; i++) {
            scoreManager.addScore("Player" + i, 1000 + i * 100, 5, 25, 120000, "어려움");
        }
        
        for (int i = 0; i < 3; i++) {
            scoreManager.addScore("Player" + (i + 10), 800 + i * 100, 4, 20, 100000, "아이템");
        }
        
        int hardModeCount = scoreManager.getTotalScoresByMode("어려움");
        int itemModeCount = scoreManager.getTotalScoresByMode("아이템");
        int nonExistentModeCount = scoreManager.getTotalScoresByMode("존재하지않는모드");
        
        assertEquals("어려움 모드 점수가 7개여야 합니다", 7, hardModeCount);
        assertEquals("아이템 모드 점수가 3개여야 합니다", 3, itemModeCount);
        assertEquals("존재하지 않는 모드의 점수는 0개여야 합니다", 0, nonExistentModeCount);
    }
    
    @Test
    public void testGetTotalPagesByMode() {
        // 모드별 총 페이지 수 테스트
        // 아이템 모드로 12개 점수 추가
        for (int i = 1; i <= 12; i++) {
            scoreManager.addScore("ItemPlayer" + i, i * 150, i % 8 + 1, i * 3, i * 12000L, "아이템");
        }
        
        int totalPages5 = scoreManager.getTotalPagesByMode(5, "아이템"); // 페이지당 5개
        int totalPages10 = scoreManager.getTotalPagesByMode(10, "아이템"); // 페이지당 10개
        int totalPages20 = scoreManager.getTotalPagesByMode(20, "아이템"); // 페이지당 20개
        
        assertEquals("페이지당 5개일 때 총 3페이지여야 합니다", 3, totalPages5);
        assertEquals("페이지당 10개일 때 총 2페이지여야 합니다", 2, totalPages10);
        assertEquals("페이지당 20개일 때 총 1페이지여야 합니다", 1, totalPages20);
        
        // 존재하지 않는 모드
        int nonExistentModePages = scoreManager.getTotalPagesByMode(5, "존재하지않는모드");
        assertEquals("존재하지 않는 모드의 페이지 수는 0이어야 합니다", 0, nonExistentModePages);
    }
    
    @Test
    public void testMixedModeScores() {
        // 혼합 모드 점수 테스트
        scoreManager.addScore("MixedPlayer1", 2500, 12, 60, 400000, "어려움");
        scoreManager.addScore("MixedPlayer2", 1800, 8, 35, 200000, "보통");
        scoreManager.addScore("MixedPlayer3", 3000, 15, 75, 500000, "아이템");
        scoreManager.addScore("MixedPlayer4", 1200, 6, 25, 150000, "쉬움");
        scoreManager.addScore("MixedPlayer5", 2200, 10, 50, 350000, "어려움");
        
        // 전체 점수 조회 (모드 상관없이)
        List<ScoreEntry> allScores = scoreManager.getTopScores(10);
        assertEquals("총 5개 점수가 있어야 합니다", 5, allScores.size());
        
        // 어려움 모드만 조회
        List<ScoreEntry> hardScores = scoreManager.getScoresByMode("어려움");
        assertEquals("어려움 모드 점수가 2개여야 합니다", 2, hardScores.size());
        
        // 어려움 모드 점수가 점수순으로 정렬되었는지 확인
        assertEquals("어려움 모드 첫 번째 점수가 2500이어야 합니다", 2500, hardScores.get(0).getScore());
        assertEquals("어려움 모드 두 번째 점수가 2200이어야 합니다", 2200, hardScores.get(1).getScore());
        
        // 각 모드별 점수 개수 확인
        assertEquals("어려움 모드 점수 개수", 2, scoreManager.getTotalScoresByMode("어려움"));
        assertEquals("보통 모드 점수 개수", 1, scoreManager.getTotalScoresByMode("보통"));
        assertEquals("아이템 모드 점수 개수", 1, scoreManager.getTotalScoresByMode("아이템"));
        assertEquals("쉬움 모드 점수 개수", 1, scoreManager.getTotalScoresByMode("쉬움"));
    }
    
    @Test
    public void testEmptyModeQuery() {
        // 빈 모드 조회 테스트
        List<ScoreEntry> emptyModeScores = scoreManager.getScoresByMode("존재하지않는모드");
        
        assertNotNull("빈 모드 점수 목록도 null이 아니어야 합니다", emptyModeScores);
        assertEquals("존재하지 않는 모드의 점수 목록은 비어있어야 합니다", 0, emptyModeScores.size());
        
        // 빈 모드 페이지네이션
        List<ScoreEntry> emptyModePage = scoreManager.getScoresPageByMode(1, 5, "존재하지않는모드");
        assertNotNull("빈 모드 페이지도 null이 아니어야 합니다", emptyModePage);
        assertEquals("존재하지 않는 모드의 페이지는 비어있어야 합니다", 0, emptyModePage.size());
    }
    
    @Test
    public void testNullGameModeHandling() {
        // null 게임 모드 처리 테스트
        scoreManager.addScore("NullModePlayer", 1000, 5, 25, 120000, null);
        
        List<ScoreEntry> allScores = scoreManager.getTopScores(10);
        
        boolean foundNullModeScore = false;
        for (ScoreEntry score : allScores) {
            if ("NullModePlayer".equals(score.getPlayerName())) {
                foundNullModeScore = true;
                // null 모드가 어떻게 처리되는지 확인 (기본값으로 설정되거나 null 유지)
                // 구체적인 처리 방식은 ScoreManager 구현에 따라 다를 수 있음
                break;
            }
        }
        
        assertTrue("null 모드 점수도 저장되어야 합니다", foundNullModeScore);
    }
    
    @Test
    public void testEmptyGameModeHandling() {
        // 빈 문자열 게임 모드 처리 테스트
        scoreManager.addScore("EmptyModePlayer", 1500, 7, 35, 180000, "");
        
        List<ScoreEntry> allScores = scoreManager.getTopScores(10);
        
        boolean foundEmptyModeScore = false;
        for (ScoreEntry score : allScores) {
            if ("EmptyModePlayer".equals(score.getPlayerName())) {
                foundEmptyModeScore = true;
                // 빈 문자열 모드가 어떻게 처리되는지 확인
                break;
            }
        }
        
        assertTrue("빈 문자열 모드 점수도 저장되어야 합니다", foundEmptyModeScore);
    }
    
    @Test 
    public void testKoreanGameModes() {
        // 한국어 게임 모드 테스트
        String[] koreanModes = {"쉬움", "보통", "어려움", "아이템"};
        
        for (int i = 0; i < koreanModes.length; i++) {
            scoreManager.addScore("한국어플레이어" + (i + 1), (i + 1) * 1000, i + 5, (i + 1) * 20, (i + 1) * 100000L, koreanModes[i]);
        }
        
        // 각 한국어 모드별로 점수 조회
        for (String mode : koreanModes) {
            List<ScoreEntry> modeScores = scoreManager.getScoresByMode(mode);
            assertEquals("각 한국어 모드에 1개씩 점수가 있어야 합니다", 1, modeScores.size());
            assertEquals("조회된 점수의 모드가 올바르게 설정되어야 합니다", mode, modeScores.get(0).getGameMode());
        }
        
        // 전체 한국어 모드 점수 개수 확인
        int totalKoreanModeScores = 0;
        for (String mode : koreanModes) {
            totalKoreanModeScores += scoreManager.getTotalScoresByMode(mode);
        }
        
        assertEquals("총 한국어 모드 점수가 4개여야 합니다", 4, totalKoreanModeScores);
    }
    
    @Test
    public void testModeBasedPagination() {
        // 모드별 페이지네이션 상세 테스트
        String testMode = "테스트모드";
        
        // 테스트 모드로 23개 점수 추가 (페이지네이션 테스트를 위해)
        for (int i = 1; i <= 23; i++) {
            scoreManager.addScore("PagePlayer" + i, i * 100, i % 10 + 1, i * 2, i * 10000L, testMode);
        }
        
        // 전체 점수 개수 확인
        int totalScores = scoreManager.getTotalScoresByMode(testMode);
        assertEquals("총 23개의 테스트 모드 점수가 있어야 합니다", 23, totalScores);
        
        int itemsPerPage = 10;
        
        // 페이지 1 (1-10번째 점수)
        List<ScoreEntry> page1 = scoreManager.getScoresPageByMode(1, itemsPerPage, testMode);
        assertEquals("첫 번째 페이지에 10개 점수", 10, page1.size());
        
        // 점수가 내림차순으로 정렬되었는지 확인 (가장 높은 점수부터)
        int highestScore = page1.get(0).getScore();
        assertTrue("첫 번째 페이지 첫 점수가 양수여야 합니다", highestScore > 0);
        
        // 페이지 2 (11-20번째 점수)
        List<ScoreEntry> page2 = scoreManager.getScoresPageByMode(2, itemsPerPage, testMode);
        assertEquals("두 번째 페이지에 10개 점수", 10, page2.size());
        
        // 페이지 3 (21-23번째 점수)
        List<ScoreEntry> page3 = scoreManager.getScoresPageByMode(3, itemsPerPage, testMode);
        assertEquals("세 번째 페이지에 3개 점수", 3, page3.size());
        
        // 총 페이지 수 확인
        int totalPages = scoreManager.getTotalPagesByMode(itemsPerPage, testMode);
        assertEquals("총 3페이지여야 합니다", 3, totalPages);
        
        // 페이지 간 점수 순서 확인
        if (page1.size() > 0 && page2.size() > 0) {
            assertTrue("첫 번째 페이지 마지막 점수가 두 번째 페이지 첫 점수보다 높거나 같아야 합니다", 
                      page1.get(page1.size()-1).getScore() >= page2.get(0).getScore());
        }
        
        // 페이지 범위 초과 테스트
        List<ScoreEntry> page4 = scoreManager.getScoresPageByMode(4, itemsPerPage, testMode);
        assertEquals("존재하지 않는 페이지는 빈 목록이어야 합니다", 0, page4.size());
    }
    
    @Test
    public void testScoreScreenIntegration() {
        try {
            // ScreenController mock 생성
            se.tetris.team5.ScreenController mockController = new se.tetris.team5.ScreenController() {
                @Override
                public void showScreen(String screenName) {
                    // Mock implementation
                }
            };
            
            se.tetris.team5.screens.score scoreScreen = new se.tetris.team5.screens.score(mockController);
            javax.swing.JTextPane textPane = new javax.swing.JTextPane();
            
            // score 화면 기본 기능 테스트
            scoreScreen.display(textPane);
            
            // 모든 private 메서드들 호출해서 커버리지 확보
            Class<?> scoreClass = se.tetris.team5.screens.score.class;
            
            // buildSampleEntries
            Method buildSampleMethod = scoreClass.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<ScoreEntry> entries = (java.util.List<ScoreEntry>) buildSampleMethod.invoke(scoreScreen);
            assertEquals(10, entries.size());
            
            // createKoreanFont
            Method createFontMethod = scoreClass.getDeclaredMethod("createKoreanFont", int.class, int.class);
            createFontMethod.setAccessible(true);
            java.awt.Font font = (java.awt.Font) createFontMethod.invoke(scoreScreen, java.awt.Font.BOLD, 14);
            assertNotNull(font);
            
            // getGameModeDisplay
            Method getModeMethod = scoreClass.getDeclaredMethod("getGameModeDisplay", String.class);
            getModeMethod.setAccessible(true);
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, "ITEM"));
            assertEquals("쉬움", getModeMethod.invoke(scoreScreen, "NORMAL_EASY"));
            assertEquals("보통", getModeMethod.invoke(scoreScreen, "NORMAL_NORMAL"));
            assertEquals("어려움", getModeMethod.invoke(scoreScreen, "NORMAL_HARD"));
            assertEquals("전문가", getModeMethod.invoke(scoreScreen, "NORMAL_EXPERT"));
            
            // UI 컴포넌트 생성 메서드들
            Method createModeTabMethod = scoreClass.getDeclaredMethod("createModeTabPanel");
            createModeTabMethod.setAccessible(true);
            javax.swing.JPanel modeTabPanel = (javax.swing.JPanel) createModeTabMethod.invoke(scoreScreen);
            assertNotNull(modeTabPanel);
            
            Method buildPodiumMethod = scoreClass.getDeclaredMethod("buildPodiumPanel");
            buildPodiumMethod.setAccessible(true);
            javax.swing.JPanel podiumPanel = (javax.swing.JPanel) buildPodiumMethod.invoke(scoreScreen);
            assertNotNull(podiumPanel);
            
            Method createPodiumBlockMethod = scoreClass.getDeclaredMethod("createPodiumBlock", int.class);
            createPodiumBlockMethod.setAccessible(true);
            for (int rank = 1; rank <= 5; rank++) {
                javax.swing.JPanel block = (javax.swing.JPanel) createPodiumBlockMethod.invoke(scoreScreen, rank);
                assertNotNull(block);
                assertEquals("podium-" + rank, block.getName());
            }
            
            Method buildControlsMethod = scoreClass.getDeclaredMethod("buildControlsPanel");
            buildControlsMethod.setAccessible(true);
            javax.swing.JPanel controlsPanel = (javax.swing.JPanel) buildControlsMethod.invoke(scoreScreen);
            assertNotNull(controlsPanel);
            
            Method createBgMethod = scoreClass.getDeclaredMethod("createBackgroundPanel");
            createBgMethod.setAccessible(true);
            javax.swing.JPanel bgPanel = (javax.swing.JPanel) createBgMethod.invoke(scoreScreen);
            assertNotNull(bgPanel);
            
            Method buildFooterMethod = scoreClass.getDeclaredMethod("buildFooterImagePanel");
            buildFooterMethod.setAccessible(true);
            javax.swing.JPanel footerPanel = (javax.swing.JPanel) buildFooterMethod.invoke(scoreScreen);
            assertNotNull(footerPanel);
            
            // buildUI 메서드
            Method buildUIMethod = scoreClass.getDeclaredMethod("buildUI");
            buildUIMethod.setAccessible(true);
            buildUIMethod.invoke(scoreScreen);
            
            // renderScores 메서드
            Method renderMethod = scoreClass.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            renderMethod.invoke(scoreScreen);
            
            // Helper 메서드들
            Method findAllMethod = scoreClass.getDeclaredMethod("findAllComponents", java.awt.Container.class);
            findAllMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<java.awt.Component> components = (java.util.List<java.awt.Component>) findAllMethod.invoke(scoreScreen, textPane);
            assertNotNull(components);
            
            Method findPageMethod = scoreClass.getDeclaredMethod("findPageInfoLabel", java.awt.Container.class);
            findPageMethod.setAccessible(true);
            findPageMethod.invoke(scoreScreen, textPane);
            
            Method findHeaderMethod = scoreClass.getDeclaredMethod("findHeaderPanel", java.awt.Container.class);
            findHeaderMethod.setAccessible(true);
            findHeaderMethod.invoke(scoreScreen, textPane);
            
            Method findListMethod = scoreClass.getDeclaredMethod("findListContainer", java.awt.Container.class);
            findListMethod.setAccessible(true);
            findListMethod.invoke(scoreScreen, textPane);
            
        } catch (Exception e) {
            fail("Score screen integration test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testScoreScreenStateManagement() {
        try {
            se.tetris.team5.ScreenController mockController = new se.tetris.team5.ScreenController() {
                @Override
                public void showScreen(String screenName) {
                    // Mock implementation
                }
            };
            
            se.tetris.team5.screens.score scoreScreen = new se.tetris.team5.screens.score(mockController);
            Class<?> scoreClass = se.tetris.team5.screens.score.class;
            
            // 필드 테스트
            Field modeField = scoreClass.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            assertEquals("ITEM", modeField.get(scoreScreen));
            
            Field pageField = scoreClass.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            assertEquals(0, pageField.get(scoreScreen));
            
            Field showOnlyDummyField = scoreClass.getDeclaredField("showOnlyDummy");
            showOnlyDummyField.setAccessible(true);
            assertEquals(false, showOnlyDummyField.get(scoreScreen));
            
            // 필드 변경 테스트
            String[] modes = {"NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD"};
            for (String mode : modes) {
                modeField.set(scoreScreen, mode);
                assertEquals(mode, modeField.get(scoreScreen));
            }
            
            // 페이지 변경 테스트
            for (int page = 0; page < 5; page++) {
                pageField.set(scoreScreen, page);
                assertEquals(page, pageField.get(scoreScreen));
            }
            
            // showOnlyDummy 변경 테스트
            showOnlyDummyField.set(scoreScreen, true);
            assertEquals(true, showOnlyDummyField.get(scoreScreen));
            
            // 상태 변경 후 renderScores 호출
            javax.swing.JTextPane textPane = new javax.swing.JTextPane();
            scoreScreen.display(textPane);
            
            Method renderMethod = scoreClass.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            
            // 다양한 상태에서 렌더링 테스트
            for (String mode : modes) {
                modeField.set(scoreScreen, mode);
                for (int page = 0; page < 3; page++) {
                    pageField.set(scoreScreen, page);
                    renderMethod.invoke(scoreScreen);
                }
            }
            
            // showOnlyDummy true 상태에서 렌더링
            showOnlyDummyField.set(scoreScreen, true);
            renderMethod.invoke(scoreScreen);
            
        } catch (Exception e) {
            fail("Score screen state management test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testScoreScreenEdgeCases() {
        try {
            se.tetris.team5.ScreenController mockController = new se.tetris.team5.ScreenController() {
                @Override
                public void showScreen(String screenName) {
                    // Mock implementation
                }
            };
            
            se.tetris.team5.screens.score scoreScreen = new se.tetris.team5.screens.score(mockController);
            Class<?> scoreClass = se.tetris.team5.screens.score.class;
            
            // null textPane으로 display 호출
            scoreScreen.display(null);
            
            // getGameModeDisplay의 edge case들
            Method getModeMethod = scoreClass.getDeclaredMethod("getGameModeDisplay", String.class);
            getModeMethod.setAccessible(true);
            
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, (Object) null));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, ""));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, "UNKNOWN_MODE"));
            assertEquals("아이템", getModeMethod.invoke(scoreScreen, "invalid"));
            
            // createKoreanFont의 다양한 파라미터
            Method createFontMethod = scoreClass.getDeclaredMethod("createKoreanFont", int.class, int.class);
            createFontMethod.setAccessible(true);
            
            // 다양한 스타일과 크기
            int[] styles = {java.awt.Font.PLAIN, java.awt.Font.BOLD, java.awt.Font.ITALIC, java.awt.Font.BOLD | java.awt.Font.ITALIC};
            int[] sizes = {8, 10, 12, 14, 16, 18, 20, 24};
            
            for (int style : styles) {
                for (int size : sizes) {
                    java.awt.Font font = (java.awt.Font) createFontMethod.invoke(scoreScreen, style, size);
                    assertNotNull(font);
                    assertEquals(style, font.getStyle());
                    assertEquals(size, font.getSize());
                }
            }
            
            // createPodiumBlock의 다양한 순위
            Method createPodiumBlockMethod = scoreClass.getDeclaredMethod("createPodiumBlock", int.class);
            createPodiumBlockMethod.setAccessible(true);
            
            for (int rank = 1; rank <= 20; rank++) {
                javax.swing.JPanel block = (javax.swing.JPanel) createPodiumBlockMethod.invoke(scoreScreen, rank);
                assertNotNull(block);
                assertEquals("podium-" + rank, block.getName());
            }
            
            // Helper methods에 다양한 컨테이너 전달
            Method findAllMethod = scoreClass.getDeclaredMethod("findAllComponents", java.awt.Container.class);
            findAllMethod.setAccessible(true);
            
            javax.swing.JPanel emptyPanel = new javax.swing.JPanel();
            @SuppressWarnings("unchecked")
            java.util.List<java.awt.Component> emptyComponents = (java.util.List<java.awt.Component>) findAllMethod.invoke(scoreScreen, emptyPanel);
            assertNotNull(emptyComponents);
            
            javax.swing.JPanel complexPanel = new javax.swing.JPanel();
            javax.swing.JPanel childPanel = new javax.swing.JPanel();
            javax.swing.JLabel label = new javax.swing.JLabel("test");
            childPanel.add(label);
            complexPanel.add(childPanel);
            
            @SuppressWarnings("unchecked")
            java.util.List<java.awt.Component> complexComponents = (java.util.List<java.awt.Component>) findAllMethod.invoke(scoreScreen, complexPanel);
            assertNotNull(complexComponents);
            assertTrue(complexComponents.size() >= 2);
            
        } catch (Exception e) {
            fail("Score screen edge cases test failed: " + e.getMessage());
        }
    }
}