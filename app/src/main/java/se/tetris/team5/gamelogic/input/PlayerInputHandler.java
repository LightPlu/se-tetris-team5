package se.tetris.team5.gamelogic.input;

import se.tetris.team5.gamelogic.GameEngine;

/**
 * 플레이어 입력 처리 인터페이스
 */
public interface PlayerInputHandler {
    
    /**
     * 왼쪽 이동
     */
    void handleMoveLeft();
    
    /**
     * 오른쪽 이동
     */
    void handleMoveRight();
    
    /**
     * 회전
     */
    void handleRotate();
    
    /**
     * 하드 드롭
     */
    void handleHardDrop();
    
    /**
     * 소프트 드롭 (아래로 이동)
     */
    void handleSoftDrop();
    
    /**
     * GameEngine 설정
     */
    void setGameEngine(GameEngine gameEngine);
}
