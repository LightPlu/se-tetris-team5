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
}