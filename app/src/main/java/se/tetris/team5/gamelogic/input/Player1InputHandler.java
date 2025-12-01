package se.tetris.team5.gamelogic.input;

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
    
    @Override
    public void handleUseItem() {
        if (gameEngine != null && gameEngine.hasTimeStopCharge()) {
            // 아이템 사용 로직은 PlayerGamePanel에서 처리
            // 여기서는 빈 구현체로 남겨두고 PlayerGamePanel에서 호출
        }
    }
    
    /**
     * 키 이벤트 처리 - GameSettings에서 키 코드를 가져와서 처리
     */
    public void handleKeyPress(int keyCode) {
        se.tetris.team5.utils.setting.GameSettings settings = 
            se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        int leftKey = settings.getPlayerKeyCode(1, "left");
        int rightKey = settings.getPlayerKeyCode(1, "right");
        int rotateKey = settings.getPlayerKeyCode(1, "rotate");
        int downKey = settings.getPlayerKeyCode(1, "down");
        int dropKey = settings.getPlayerKeyCode(1, "drop");
        int itemKey = settings.getPlayerKeyCode(1, "item");
        
        System.out.println("[Player1InputHandler] keyCode=" + keyCode + ", dropKey=" + dropKey);
        
        if (keyCode == leftKey) {
            handleMoveLeft();
        } else if (keyCode == rightKey) {
            handleMoveRight();
        } else if (keyCode == rotateKey) {
            handleRotate();
        } else if (keyCode == downKey) {
            handleSoftDrop();
        } else if (keyCode == dropKey) {
            System.out.println("[Player1InputHandler] 하드드롭 호출! gameEngine=" + (gameEngine != null));
            handleHardDrop();
        } else if (keyCode == itemKey) {
            handleUseItem();
        }
    }
}
