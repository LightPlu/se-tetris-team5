package se.tetris.team5.gamelogic.battle;

import se.tetris.team5.components.battle.PlayerGamePanel;

/**
 * 대전 모드 게임 컨트롤러
 * 두 플레이어의 게임 상태 관리 및 승패 판정
 */
public class BattleGameController {
    
    private PlayerGamePanel player1Panel;
    private PlayerGamePanel player2Panel;
    private GameOverCallback gameOverCallback;
    
    private boolean gameStarted = false;
    private boolean gameEnded = false;
    private boolean paused = false;
    
    public interface GameOverCallback {
        void onGameOver(int winner);
    }
    
    public BattleGameController(PlayerGamePanel player1Panel, PlayerGamePanel player2Panel, GameOverCallback callback) {
        this.player1Panel = player1Panel;
        this.player2Panel = player2Panel;
        this.gameOverCallback = callback;
    }
    
    /**
     * 게임 시작
     */
    public void start() {
        player1Panel.startGame();
        player2Panel.startGame();
        gameStarted = true;
        gameEnded = false;
        paused = false;
    }
    
    /**
     * 게임 재시작
     */
    public void restart() {
        gameEnded = false;
        paused = false;
        start();
    }
    
    /**
     * 게임 일시정지/재개 설정
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) {
            player1Panel.pauseGame();
            player2Panel.pauseGame();
        } else {
            player1Panel.resumeGame();
            player2Panel.resumeGame();
        }
    }
    
    /**
     * 게임 종료
     */
    public void stop() {
        player1Panel.stopGame();
        player2Panel.stopGame();
        gameStarted = false;
    }
    
    /**
     * UI 업데이트 및 게임 오버 체크
     */
    public void checkGameOver() {
        if (gameEnded) return;
        
        boolean player1GameOver = player1Panel.isGameOver();
        boolean player2GameOver = player2Panel.isGameOver();
        
        if (player1GameOver && player2GameOver) {
            // 동시 게임 오버 - 무승부 (플레이어2 승리로 처리)
            gameEnded = true;
            stop();
            if (gameOverCallback != null) {
                gameOverCallback.onGameOver(2);
            }
        } else if (player1GameOver) {
            // 플레이어 1 패배 -> 플레이어 2 승리
            gameEnded = true;
            stop();
            if (gameOverCallback != null) {
                gameOverCallback.onGameOver(2);
            }
        } else if (player2GameOver) {
            // 플레이어 2 패배 -> 플레이어 1 승리
            gameEnded = true;
            stop();
            if (gameOverCallback != null) {
                gameOverCallback.onGameOver(1);
            }
        }
    }
    
    /**
     * 게임이 종료되었는지 확인
     */
    public boolean isGameOver() {
        return gameEnded;
    }
    
    /**
     * 게임이 시작되었는지 확인
     */
    public boolean isGameStarted() {
        return gameStarted;
    }
}
