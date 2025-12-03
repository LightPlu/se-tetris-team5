package se.tetris.team5.gamelogic.input;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.utils.setting.GameSettings;
import java.awt.event.KeyEvent;

/**
 * Player2InputHandler 테스트
 */
public class Player2InputHandlerTest {

    private GameEngine engine;
    private Player2InputHandler handler;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        handler = new Player2InputHandler(engine);
    }

    /**
     * 테스트 1: 생성자
     */
    @Test
    public void testPlayer2InputHandler_Constructor() {
        assertNotNull("핸들러가 생성되어야 함", handler);
    }

    /**
     * 테스트 2: setGameEngine
     */
    @Test
    public void testPlayer2InputHandler_SetGameEngine() {
        GameEngine newEngine = new GameEngine(20, 10);
        handler.setGameEngine(newEngine);
        
        assertTrue("엔진 설정이 안전해야 함", true);
    }

    /**
     * 테스트 3: handleMoveLeft
     */
    @Test
    public void testPlayer2InputHandler_HandleMoveLeft() {
        int initialX = engine.getX();
        
        handler.handleMoveLeft();
        
        assertEquals("왼쪽으로 이동해야 함", initialX - 1, engine.getX());
    }

    /**
     * 테스트 4: handleMoveRight
     */
    @Test
    public void testPlayer2InputHandler_HandleMoveRight() {
        int initialX = engine.getX();
        
        handler.handleMoveRight();
        
        assertEquals("오른쪽으로 이동해야 함", initialX + 1, engine.getX());
    }

    /**
     * 테스트 5: handleRotate
     */
    @Test
    public void testPlayer2InputHandler_HandleRotate() {
        handler.handleRotate();
        
        assertNotNull("회전 후 블록이 유효해야 함", engine.getCurrentBlock());
    }

    /**
     * 테스트 6: handleSoftDrop
     */
    @Test
    public void testPlayer2InputHandler_HandleSoftDrop() {
        int initialY = engine.getY();
        
        handler.handleSoftDrop();
        
        assertTrue("아래로 이동해야 함", engine.getY() >= initialY);
    }

    /**
     * 테스트 7: handleHardDrop
     */
    @Test
    public void testPlayer2InputHandler_HandleHardDrop() {
        handler.handleHardDrop();
        
        assertNotNull("하드 드롭 후 새 블록이 생성되어야 함", engine.getCurrentBlock());
    }

    /**
     * 테스트 8: handleUseItem
     */
    @Test
    public void testPlayer2InputHandler_HandleUseItem() {
        handler.handleUseItem();
        
        assertTrue("아이템 사용이 안전해야 함", true);
    }

    /**
     * 테스트 9: handleKeyPress - 방향키
     */
    @Test
    public void testPlayer2InputHandler_HandleKeyPressArrows() {
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        handler.handleKeyPress(KeyEvent.VK_RIGHT);
        handler.handleKeyPress(KeyEvent.VK_UP);
        handler.handleKeyPress(KeyEvent.VK_DOWN);
        
        assertNotNull("방향키로 조작 가능", engine.getCurrentBlock());
    }

    /**
     * 테스트 10: handleMoveLeft - null 엔진
     */
    @Test
    public void testPlayer2InputHandler_HandleMoveLeftNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleMoveLeft();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 11: handleMoveRight - null 엔진
     */
    @Test
    public void testPlayer2InputHandler_HandleMoveRightNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleMoveRight();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 12: handleRotate - null 엔진
     */
    @Test
    public void testPlayer2InputHandler_HandleRotateNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleRotate();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 13: handleSoftDrop - null 엔진
     */
    @Test
    public void testPlayer2InputHandler_HandleSoftDropNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleSoftDrop();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 14: handleHardDrop - null 엔진
     */
    @Test
    public void testPlayer2InputHandler_HandleHardDropNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleHardDrop();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 15: handleUseItem - null 엔진
     */
    @Test
    public void testPlayer2InputHandler_HandleUseItemNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleUseItem();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 16: 연속 이동
     */
    @Test
    public void testPlayer2InputHandler_ConsecutiveMoves() {
        handler.handleMoveLeft();
        handler.handleMoveLeft();
        handler.handleMoveRight();
        
        assertNotNull("연속 이동 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 17: 연속 회전
     */
    @Test
    public void testPlayer2InputHandler_ConsecutiveRotations() {
        handler.handleRotate();
        handler.handleRotate();
        handler.handleRotate();
        
        assertNotNull("연속 회전 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 18: 복합 조작
     */
    @Test
    public void testPlayer2InputHandler_ComplexOperations() {
        handler.handleMoveLeft();
        handler.handleRotate();
        handler.handleMoveRight();
        handler.handleSoftDrop();
        
        assertNotNull("복합 조작 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 19: 엔진 교체
     */
    @Test
    public void testPlayer2InputHandler_EngineReplacement() {
        GameEngine newEngine = new GameEngine(20, 10);
        handler.setGameEngine(newEngine);
        
        handler.handleMoveLeft();
        
        assertNotNull("새 엔진에서 조작 가능", newEngine.getCurrentBlock());
    }

    /**
     * 테스트 20: handleKeyPress - 알 수 없는 키
     */
    @Test
    public void testPlayer2InputHandler_UnknownKey() {
        handler.handleKeyPress(KeyEvent.VK_ESCAPE);
        
        assertTrue("알 수 없는 키 처리 안전", true);
    }

    /**
     * 테스트 21: 여러 키 연속 입력
     */
    @Test
    public void testPlayer2InputHandler_MultipleKeyPresses() {
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        handler.handleKeyPress(KeyEvent.VK_UP);
        handler.handleKeyPress(KeyEvent.VK_RIGHT);
        handler.handleKeyPress(KeyEvent.VK_DOWN);
        
        assertNotNull("여러 키 입력 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 22: 하드 드롭 후 조작
     */
    @Test
    public void testPlayer2InputHandler_OperateAfterHardDrop() {
        handler.handleHardDrop();
        handler.handleMoveLeft();
        
        assertNotNull("하드 드롭 후 조작 가능", engine.getCurrentBlock());
    }

    /**
     * 테스트 23: 소프트 드롭 연속
     */
    @Test
    public void testPlayer2InputHandler_ConsecutiveSoftDrops() {
        int initialY = engine.getY();
        
        handler.handleSoftDrop();
        handler.handleSoftDrop();
        handler.handleSoftDrop();
        
        assertTrue("연속 소프트 드롭", engine.getY() >= initialY);
    }

    /**
     * 테스트 24: 좌우 이동 조합
     */
    @Test
    public void testPlayer2InputHandler_LeftRightCombination() {
        int initialX = engine.getX();
        
        handler.handleMoveLeft();
        handler.handleMoveRight();
        
        assertEquals("좌우 이동 조합", initialX, engine.getX());
    }

    /**
     * 테스트 25: 게임 오버 후 입력
     */
    @Test
    public void testPlayer2InputHandler_InputAfterGameOver() {
        // 게임 오버 상태로 만들기
        for (int i = 0; i < 30; i++) {
            if (engine.isGameOver()) break;
            engine.hardDrop();
        }
        
        if (engine.isGameOver()) {
            handler.handleMoveLeft();
            assertTrue("게임 오버 후 입력 안전", true);
        } else {
            assertTrue("게임 진행 중", true);
        }
    }

    /**
     * 테스트 26: Shift 좌/우 구분
     */
    @Test
    public void testPlayer2InputHandler_ShiftLocationHandling() {
        TrackingGameEngine trackingEngine = new TrackingGameEngine();
        Player2InputHandler trackingHandler = new Player2InputHandler(trackingEngine);
        GameSettings settings = GameSettings.getInstance();
        int originalDropKey = settings.getPlayerKeyCode(2, "drop");
        
        try {
            settings.setPlayerKeyCode(2, "drop", KeyEvent.VK_SHIFT);
            
            trackingHandler.handleKeyPress(KeyEvent.VK_SHIFT, KeyEvent.KEY_LOCATION_LEFT);
            assertFalse("왼쪽 Shift는 하드드롭을 유발하지 않아야 함", trackingEngine.hardDropCalled);
            
            trackingHandler.handleKeyPress(KeyEvent.VK_SHIFT, KeyEvent.KEY_LOCATION_RIGHT);
            assertTrue("오른쪽 Shift는 하드드롭을 유발해야 함", trackingEngine.hardDropCalled);
        } finally {
            settings.setPlayerKeyCode(2, "drop", originalDropKey);
        }
    }

    private static class TrackingGameEngine extends GameEngine {
        boolean hardDropCalled = false;

        TrackingGameEngine() {
            super(20, 10, false);
        }

        @Override
        public boolean hardDrop() {
            hardDropCalled = true;
            return true;
        }
    }
}
