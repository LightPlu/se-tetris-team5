package se.tetris.team5.gamelogic.input;

import se.tetris.team5.gamelogic.GameEngine;

/**
 * 싱글 플레이어 입력 핸들러 (기본 키 설정 사용)
 * P2P 모드에서도 사용
 */
public class SinglePlayerInputHandler implements PlayerInputHandler {
    
    private GameEngine gameEngine;
    
    public SinglePlayerInputHandler(GameEngine gameEngine) {
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
            System.out.println("[SinglePlayerInputHandler] 하드드롭 실행");
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
            // 아이템 사용 로직
        }
    }
    
    /**
     * 키 이벤트 처리 - 싱글 플레이 기본 키 설정 사용
     */
    public void handleKeyPress(int keyCode) {
        se.tetris.team5.utils.setting.GameSettings settings = 
            se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        int leftKey = settings.getKeyCode("left");      // 왼쪽 화살표
        int rightKey = settings.getKeyCode("right");    // 오른쪽 화살표
        int rotateKey = settings.getKeyCode("rotate");  // 위 화살표
        int downKey = settings.getKeyCode("down");      // 아래 화살표
        int dropKey = settings.getKeyCode("drop");      // Space
        int itemKey = settings.getKeyCode("item");      // Shift
        
        System.out.println("[SinglePlayerInputHandler] keyCode=" + keyCode + ", dropKey=" + dropKey);
        
        if (keyCode == leftKey) {
            handleMoveLeft();
        } else if (keyCode == rightKey) {
            handleMoveRight();
        } else if (keyCode == rotateKey) {
            handleRotate();
        } else if (keyCode == downKey) {
            handleSoftDrop();
        } else if (keyCode == dropKey) {
            System.out.println("[SinglePlayerInputHandler] 하드드롭 호출! gameEngine=" + (gameEngine != null));
            handleHardDrop();
        } else if (keyCode == itemKey) {
            handleUseItem();
        }
    }
}
