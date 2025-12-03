package se.tetris.team5.gamelogic.input;

import java.awt.event.KeyEvent;

import se.tetris.team5.gamelogic.GameEngine;

/**
 * 플레이어 2 입력 핸들러 (방향키 + RShift)
 */
public class Player2InputHandler implements PlayerInputHandler {
    
    private GameEngine gameEngine;
    
    public Player2InputHandler(GameEngine gameEngine) {
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
        handleKeyPress(keyCode, KeyEvent.KEY_LOCATION_UNKNOWN);
    }

    /**
     * 키 이벤트 처리 (키 위치 포함) - 왼쪽/오른쪽 Shift 구분
     */
    public void handleKeyPress(int keyCode, int keyLocation) {
        se.tetris.team5.utils.setting.GameSettings settings = 
            se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        int leftKey = settings.getPlayerKeyCode(2, "left");
        int rightKey = settings.getPlayerKeyCode(2, "right");
        int rotateKey = settings.getPlayerKeyCode(2, "rotate");
        int downKey = settings.getPlayerKeyCode(2, "down");
        int dropKey = settings.getPlayerKeyCode(2, "drop");
        int itemKey = settings.getPlayerKeyCode(2, "item");
        
        if (keyCode == leftKey) {
            handleMoveLeft();
        } else if (keyCode == rightKey) {
            handleMoveRight();
        } else if (keyCode == rotateKey) {
            handleRotate();
        } else if (keyCode == downKey) {
            handleSoftDrop();
        } else if (keyCode == dropKey) {
            if (dropKey == KeyEvent.VK_SHIFT) {
                // RShift만 허용해 왼쪽/양쪽 Shift로 인한 오동작 방지
                if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT 
                        || keyLocation == KeyEvent.KEY_LOCATION_UNKNOWN) {
                    handleHardDrop();
                }
            } else {
                handleHardDrop();
            }
        } else if (keyCode == itemKey) {
            handleUseItem();
        }
    }
}
