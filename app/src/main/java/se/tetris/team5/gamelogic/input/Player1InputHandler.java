package se.tetris.team5.gamelogic.input;

import java.awt.event.KeyEvent;
import se.tetris.team5.gamelogic.GameEngine;

/**
 * 플레이어 1 입력 핸들러 (WASD + Z)
 */
public class Player1InputHandler implements PlayerInputHandler {
    
    private GameEngine gameEngine;
    
    public Player1InputHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    @Override
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    @Override
    public void handleMoveLeft() {
        if (gameEngine != null) {
            gameEngine.moveBlockLeft();
        }
    }
    
    @Override
    public void handleMoveRight() {
        if (gameEngine != null) {
            gameEngine.moveBlockRight();
        }
    }
    
    @Override
    public void handleRotate() {
        if (gameEngine != null) {
            gameEngine.rotateBlock();
        }
    }
    
    @Override
    public void handleHardDrop() {
        if (gameEngine != null) {
            gameEngine.hardDrop();
        }
    }
    
    @Override
    public void handleSoftDrop() {
        if (gameEngine != null) {
            gameEngine.moveBlockDown();
        }
    }
    
    /**
     * 키 이벤트 처리
     */
    public void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_A: // 왼쪽
                handleMoveLeft();
                break;
            case KeyEvent.VK_D: // 오른쪽
                handleMoveRight();
                break;
            case KeyEvent.VK_W: // 회전
                handleRotate();
                break;
            case KeyEvent.VK_S: // 소프트 드롭
                handleSoftDrop();
                break;
            case KeyEvent.VK_Z: // 하드 드롭
                handleHardDrop();
                break;
        }
    }
}
