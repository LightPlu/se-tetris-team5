package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import se.tetris.team5.utils.score.ScoreManager;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.*;

public class GameEndIntegrationTest {
    private game gamePanel;
    private MockScreenController screenController;

    @Before
    public void setUp() {
        screenController = new MockScreenController();
        gamePanel = new game(screenController);
    }

    @Test
    public void testGameOverStoresScoreAndNavigatesToScore() {
    // 점수, 레벨, 줄 수, 시간 세팅 (setter 사용)
    gamePanel.setCurrentScore(1234);
    gamePanel.setLevel(2);
    gamePanel.setLinesCleared(10);
    gamePanel.setGameStartTime(System.currentTimeMillis() - 10000);


    // 실제 게임 오버 로직 실행 (화면 전환 발생)
    gamePanel.gameOver();

        // 스코어보드로 이동했는지 확인
        assertEquals("score", screenController.lastScreen);
        // ScoreManager에 점수가 저장됐는지 확인
        assertTrue(ScoreManager.getInstance().getTopScores(10).stream().anyMatch(e -> e.getScore() == 1234));
    }

    // Mock ScreenController
    static class MockScreenController extends se.tetris.team5.ScreenController {
        public String lastScreen = null;
        @Override
        public void showScreen(String screenName) {
            lastScreen = screenName;
        }
    }
}
