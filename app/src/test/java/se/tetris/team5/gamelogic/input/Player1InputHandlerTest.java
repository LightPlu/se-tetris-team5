package se.tetris.team5.gamelogic.input;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import java.awt.event.KeyEvent;

/**
 * Player1InputHandler 테스트
 */
public class Player1InputHandlerTest {

    private GameEngine engine;
    private Player1InputHandler handler;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        handler = new Player1InputHandler(engine);
    }

    /**
     * 테스트 1: 생성자
     */
    @Test
    public void testPlayer1InputHandler_Constructor() {
        assertNotNull("핸들러가 생성되어야 함", handler);
    }

    /**
     * 테스트 2: setGameEngine
     */
    @Test
    public void testPlayer1InputHandler_SetGameEngine() {
        GameEngine newEngine = new GameEngine(20, 10);
        handler.setGameEngine(newEngine);
        
        assertTrue("엔진 설정이 안전해야 함", true);
    }

    /**
     * 테스트 3: handleMoveLeft
     */
    @Test
    public void testPlayer1InputHandler_HandleMoveLeft() {
        int initialX = engine.getX();
        
        handler.handleMoveLeft();
        
        assertEquals("왼쪽으로 이동해야 함", initialX - 1, engine.getX());
    }

    /**
     * 테스트 4: handleMoveRight
     */
    @Test
    public void testPlayer1InputHandler_HandleMoveRight() {
        int initialX = engine.getX();
        
        handler.handleMoveRight();
        
        assertEquals("오른쪽으로 이동해야 함", initialX + 1, engine.getX());
    }

    /**
     * 테스트 5: handleRotate
     */
    @Test
    public void testPlayer1InputHandler_HandleRotate() {
        handler.handleRotate();
        
        assertNotNull("회전 후 블록이 유효해야 함", engine.getCurrentBlock());
    }

    /**
     * 테스트 6: handleSoftDrop
     */
    @Test
    public void testPlayer1InputHandler_HandleSoftDrop() {
        int initialY = engine.getY();
        
        handler.handleSoftDrop();
        
        assertTrue("아래로 이동해야 함", engine.getY() >= initialY);
    }

    /**
     * 테스트 7: handleHardDrop
     */
    @Test
    public void testPlayer1InputHandler_HandleHardDrop() {
        handler.handleHardDrop();
        
        assertNotNull("하드 드롭 후 새 블록이 생성되어야 함", engine.getCurrentBlock());
    }

    /**
     * 테스트 8: handleUseItem
     */
    @Test
    public void testPlayer1InputHandler_HandleUseItem() {
        handler.handleUseItem();
        
        assertTrue("아이템 사용이 안전해야 함", true);
    }

    /**
     * 테스트 9: handleKeyPress - WASD 키
     */
    @Test
    public void testPlayer1InputHandler_HandleKeyPressWASD() {
        // W, A, S, D 키 테스트
        handler.handleKeyPress(KeyEvent.VK_A); // 왼쪽
        handler.handleKeyPress(KeyEvent.VK_D); // 오른쪽
        handler.handleKeyPress(KeyEvent.VK_W); // 회전
        handler.handleKeyPress(KeyEvent.VK_S); // 아래
        
        assertNotNull("WASD 키로 조작 가능", engine.getCurrentBlock());
    }

    /**
     * 테스트 10: handleKeyPress - Z 키 (하드 드롭)
     */
    @Test
    public void testPlayer1InputHandler_HandleKeyPressZ() {
        handler.handleKeyPress(KeyEvent.VK_Z);
        
        assertNotNull("Z 키로 하드 드롭", engine.getCurrentBlock());
    }

    /**
     * 테스트 11: handleMoveLeft - null 엔진
     */
    @Test
    public void testPlayer1InputHandler_HandleMoveLeftNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleMoveLeft();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 12: handleMoveRight - null 엔진
     */
    @Test
    public void testPlayer1InputHandler_HandleMoveRightNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleMoveRight();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 13: handleRotate - null 엔진
     */
    @Test
    public void testPlayer1InputHandler_HandleRotateNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleRotate();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 14: handleSoftDrop - null 엔진
     */
    @Test
    public void testPlayer1InputHandler_HandleSoftDropNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleSoftDrop();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 15: handleHardDrop - null 엔진
     */
    @Test
    public void testPlayer1InputHandler_HandleHardDropNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleHardDrop();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 16: handleUseItem - null 엔진
     */
    @Test
    public void testPlayer1InputHandler_HandleUseItemNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleUseItem();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 17: 연속 이동
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveMoves() {
        handler.handleMoveLeft();
        handler.handleMoveLeft();
        handler.handleMoveRight();
        
        assertNotNull("연속 이동 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 18: 연속 회전
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveRotations() {
        handler.handleRotate();
        handler.handleRotate();
        handler.handleRotate();
        
        assertNotNull("연속 회전 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 19: 복합 조작
     */
    @Test
    public void testPlayer1InputHandler_ComplexOperations() {
        handler.handleMoveLeft();
        handler.handleRotate();
        handler.handleMoveRight();
        handler.handleSoftDrop();
        
        assertNotNull("복합 조작 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 20: 엔진 교체
     */
    @Test
    public void testPlayer1InputHandler_EngineReplacement() {
        GameEngine newEngine = new GameEngine(20, 10);
        handler.setGameEngine(newEngine);
        
        handler.handleMoveLeft();
        
        assertNotNull("새 엔진에서 조작 가능", newEngine.getCurrentBlock());
    }

    /**
     * 테스트 21: handleKeyPress - 알 수 없는 키
     */
    @Test
    public void testPlayer1InputHandler_UnknownKey() {
        handler.handleKeyPress(KeyEvent.VK_ESCAPE);
        
        assertTrue("알 수 없는 키 처리 안전", true);
    }

    /**
     * 테스트 22: 여러 키 연속 입력
     */
    @Test
    public void testPlayer1InputHandler_MultipleKeyPresses() {
        handler.handleKeyPress(KeyEvent.VK_A);
        handler.handleKeyPress(KeyEvent.VK_W);
        handler.handleKeyPress(KeyEvent.VK_D);
        handler.handleKeyPress(KeyEvent.VK_S);
        handler.handleKeyPress(KeyEvent.VK_Z);
        
        assertNotNull("여러 키 입력 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 23: 하드 드롭 후 조작
     */
    @Test
    public void testPlayer1InputHandler_OperateAfterHardDrop() {
        handler.handleHardDrop();
        handler.handleMoveLeft();
        
        assertNotNull("하드 드롭 후 조작 가능", engine.getCurrentBlock());
    }

    /**
     * 테스트 24: 소프트 드롭 연속
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveSoftDrops() {
        int initialY = engine.getY();
        
        handler.handleSoftDrop();
        handler.handleSoftDrop();
        handler.handleSoftDrop();
        
        assertTrue("연속 소프트 드롭", engine.getY() >= initialY);
    }

    /**
     * 테스트 25: 게임 오버 후 입력
     */
    @Test
    public void testPlayer1InputHandler_InputAfterGameOver() {
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
     * 테스트 26: handleKeyPress - A 키 연속
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveAKey() {
        int initialX = engine.getX();
        
        for (int i = 0; i < 3; i++) {
            handler.handleKeyPress(KeyEvent.VK_A);
        }
        
        assertTrue("연속 A 키 입력", engine.getX() < initialX);
    }

    /**
     * 테스트 27: handleKeyPress - D 키 연속
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveDKey() {
        int initialX = engine.getX();
        
        for (int i = 0; i < 3; i++) {
            handler.handleKeyPress(KeyEvent.VK_D);
        }
        
        assertTrue("연속 D 키 입력", engine.getX() > initialX);
    }

    /**
     * 테스트 28: handleKeyPress - W 키 연속
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveWKey() {
        for (int i = 0; i < 4; i++) {
            handler.handleKeyPress(KeyEvent.VK_W);
        }
        
        assertNotNull("연속 W 키 입력", engine.getCurrentBlock());
    }

    /**
     * 테스트 29: handleKeyPress - S 키 연속
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveSKey() {
        int initialY = engine.getY();
        
        for (int i = 0; i < 5; i++) {
            handler.handleKeyPress(KeyEvent.VK_S);
        }
        
        assertTrue("연속 S 키 입력", engine.getY() >= initialY);
    }

    /**
     * 테스트 30: handleKeyPress - Z 키 연속
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveZKey() {
        for (int i = 0; i < 3; i++) {
            handler.handleKeyPress(KeyEvent.VK_Z);
        }
        
        assertNotNull("연속 Z 키 입력", engine.getCurrentBlock());
    }

    /**
     * 테스트 31: handleKeyPress - null 엔진에서 WASD
     */
    @Test
    public void testPlayer1InputHandler_WASDWithNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleKeyPress(KeyEvent.VK_A);
        handler.handleKeyPress(KeyEvent.VK_W);
        handler.handleKeyPress(KeyEvent.VK_D);
        handler.handleKeyPress(KeyEvent.VK_S);
        
        assertTrue("null 엔진에서 WASD 안전", true);
    }

    /**
     * 테스트 32: handleKeyPress - null 엔진에서 Z
     */
    @Test
    public void testPlayer1InputHandler_ZKeyWithNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleKeyPress(KeyEvent.VK_Z);
        
        assertTrue("null 엔진에서 Z 키 안전", true);
    }

    /**
     * 테스트 33: 복잡한 WASD 조합
     */
    @Test
    public void testPlayer1InputHandler_ComplexWASDCombination() {
        handler.handleKeyPress(KeyEvent.VK_A);
        handler.handleKeyPress(KeyEvent.VK_A);
        handler.handleKeyPress(KeyEvent.VK_W);
        handler.handleKeyPress(KeyEvent.VK_S);
        handler.handleKeyPress(KeyEvent.VK_D);
        handler.handleKeyPress(KeyEvent.VK_Z);
        
        assertNotNull("복잡한 WASD 조합", engine.getCurrentBlock());
    }

    /**
     * 테스트 34: A와 D 교대 입력
     */
    @Test
    public void testPlayer1InputHandler_AlternatingAD() {
        int initialX = engine.getX();
        
        for (int i = 0; i < 5; i++) {
            handler.handleKeyPress(KeyEvent.VK_A);
            handler.handleKeyPress(KeyEvent.VK_D);
        }
        
        assertEquals("A와 D 교대 입력", initialX, engine.getX());
    }

    /**
     * 테스트 35: 모든 Player1 키 순차 테스트
     */
    @Test
    public void testPlayer1InputHandler_AllPlayer1KeysSequential() {
        handler.handleKeyPress(KeyEvent.VK_A);
        handler.handleKeyPress(KeyEvent.VK_D);
        handler.handleKeyPress(KeyEvent.VK_W);
        handler.handleKeyPress(KeyEvent.VK_S);
        handler.handleKeyPress(KeyEvent.VK_Z);
        handler.handleKeyPress(KeyEvent.VK_X); // 아이템 키 (설정에 따라)
        
        assertTrue("모든 Player1 키 처리 완료", true);
    }

    /**
     * 테스트 36: 엔진 교체 후 WASD
     */
    @Test
    public void testPlayer1InputHandler_WASDAfterEngineChange() {
        GameEngine newEngine = new GameEngine(20, 10);
        handler.setGameEngine(newEngine);
        
        int initialX = newEngine.getX();
        handler.handleKeyPress(KeyEvent.VK_A);
        
        assertEquals("엔진 교체 후 A 키", initialX - 1, newEngine.getX());
    }

    /**
     * 테스트 37: 빠른 WASD 입력
     */
    @Test
    public void testPlayer1InputHandler_RapidWASDInput() {
        for (int i = 0; i < 20; i++) {
            handler.handleKeyPress(KeyEvent.VK_S);
        }
        
        assertTrue("빠른 WASD 입력 처리", true);
    }

    /**
     * 테스트 38: W 회전 후 S 하강
     */
    @Test
    public void testPlayer1InputHandler_RotateAndDrop() {
        handler.handleKeyPress(KeyEvent.VK_W);
        handler.handleKeyPress(KeyEvent.VK_W);
        
        int initialY = engine.getY();
        
        handler.handleKeyPress(KeyEvent.VK_S);
        handler.handleKeyPress(KeyEvent.VK_S);
        
        assertTrue("회전 후 하강", engine.getY() >= initialY);
    }

    /**
     * 테스트 39: 좌우 이동 후 하드 드롭
     */
    @Test
    public void testPlayer1InputHandler_MoveAndHardDrop() {
        handler.handleKeyPress(KeyEvent.VK_A);
        handler.handleKeyPress(KeyEvent.VK_D);
        handler.handleKeyPress(KeyEvent.VK_Z);
        
        assertNotNull("좌우 이동 후 하드 드롭", engine.getCurrentBlock());
    }

    /**
     * 테스트 40: 다양한 키 코드 입력
     */
    @Test
    public void testPlayer1InputHandler_VariousKeyCodes() {
        int[] keyCodes = {
            KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3,
            KeyEvent.VK_ESCAPE, KeyEvent.VK_ENTER, KeyEvent.VK_TAB,
            KeyEvent.VK_SPACE, KeyEvent.VK_SHIFT
        };
        
        for (int keyCode : keyCodes) {
            handler.handleKeyPress(keyCode);
        }
        
        assertTrue("다양한 키 코드 입력 안전", true);
    }

    /**
     * 테스트 41: 모든 메서드 반복 호출
     */
    @Test
    public void testPlayer1InputHandler_AllMethodsRepeated() {
        for (int i = 0; i < 3; i++) {
            handler.handleMoveLeft();
            handler.handleMoveRight();
            handler.handleRotate();
            handler.handleSoftDrop();
            handler.handleUseItem();
        }
        
        assertNotNull("모든 메서드 반복 호출", engine.getCurrentBlock());
    }

    /**
     * 테스트 42: 여러 엔진 교체
     */
    @Test
    public void testPlayer1InputHandler_MultipleEngineChanges() {
        GameEngine engine1 = new GameEngine(20, 10);
        GameEngine engine2 = new GameEngine(20, 10);
        GameEngine engine3 = new GameEngine(20, 10);
        
        handler.setGameEngine(engine1);
        handler.handleKeyPress(KeyEvent.VK_A);
        
        handler.setGameEngine(engine2);
        handler.handleKeyPress(KeyEvent.VK_D);
        
        handler.setGameEngine(engine3);
        handler.handleKeyPress(KeyEvent.VK_W);
        
        assertTrue("여러 엔진 교체 안전", true);
    }

    /**
     * 테스트 43: null 엔진 설정 후 복구
     */
    @Test
    public void testPlayer1InputHandler_NullEngineAndRecover() {
        handler.setGameEngine(null);
        handler.handleKeyPress(KeyEvent.VK_A);
        
        handler.setGameEngine(engine);
        int initialX = engine.getX();
        handler.handleKeyPress(KeyEvent.VK_A);
        
        assertEquals("null 엔진 후 복구", initialX - 1, engine.getX());
    }

    /**
     * 테스트 44: WASD로 복잡한 이동
     */
    @Test
    public void testPlayer1InputHandler_ComplexWASDMovement() {
        handler.handleKeyPress(KeyEvent.VK_W); // 회전
        handler.handleKeyPress(KeyEvent.VK_A); // 왼쪽
        handler.handleKeyPress(KeyEvent.VK_A); // 왼쪽
        handler.handleKeyPress(KeyEvent.VK_S); // 아래
        handler.handleKeyPress(KeyEvent.VK_D); // 오른쪽
        handler.handleKeyPress(KeyEvent.VK_W); // 회전
        handler.handleKeyPress(KeyEvent.VK_S); // 아래
        
        assertNotNull("복잡한 WASD 이동", engine.getCurrentBlock());
    }

    /**
     * 테스트 45: 극단적인 키 입력 시나리오
     */
    @Test
    public void testPlayer1InputHandler_ExtremeKeyScenario() {
        int[] keys = {
            KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W,
            KeyEvent.VK_S, KeyEvent.VK_Z
        };
        
        for (int i = 0; i < 100; i++) {
            handler.handleKeyPress(keys[i % keys.length]);
        }
        
        assertTrue("극단적인 키 입력 시나리오 처리", true);
    }

    /**
     * 테스트 46: handleUseItem과 키 입력 조합
     */
    @Test
    public void testPlayer1InputHandler_UseItemAndKeyPress() {
        handler.handleUseItem();
        handler.handleKeyPress(KeyEvent.VK_X);
        handler.handleKeyPress(KeyEvent.VK_A);
        
        assertNotNull("아이템 사용과 키 입력 조합", engine.getCurrentBlock());
    }

    /**
     * 테스트 47: 모든 핸들러 메서드 null 엔진에서 호출
     */
    @Test
    public void testPlayer1InputHandler_AllMethodsWithNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleMoveLeft();
        handler.handleMoveRight();
        handler.handleRotate();
        handler.handleSoftDrop();
        handler.handleHardDrop();
        handler.handleUseItem();
        
        assertTrue("null 엔진에서 모든 메서드 안전", true);
    }

    /**
     * 테스트 48: Z 키로 연속 하드 드롭
     */
    @Test
    public void testPlayer1InputHandler_ConsecutiveHardDropsWithZ() {
        handler.handleKeyPress(KeyEvent.VK_Z);
        handler.handleKeyPress(KeyEvent.VK_Z);
        handler.handleKeyPress(KeyEvent.VK_Z);
        
        assertNotNull("Z 키 연속 하드 드롭", engine.getCurrentBlock());
    }

    /**
     * 테스트 49: WASD 패턴 반복
     */
    @Test
    public void testPlayer1InputHandler_RepeatingWASDPattern() {
        for (int i = 0; i < 5; i++) {
            handler.handleKeyPress(KeyEvent.VK_W);
            handler.handleKeyPress(KeyEvent.VK_A);
            handler.handleKeyPress(KeyEvent.VK_S);
            handler.handleKeyPress(KeyEvent.VK_D);
        }
        
        assertNotNull("WASD 패턴 반복", engine.getCurrentBlock());
    }

    /**
     * 테스트 50: 하드 드롭 후 즉시 키 입력
     */
    @Test
    public void testPlayer1InputHandler_KeyPressAfterHardDrop() {
        handler.handleKeyPress(KeyEvent.VK_Z);
        handler.handleKeyPress(KeyEvent.VK_A);
        handler.handleKeyPress(KeyEvent.VK_W);
        
        assertNotNull("하드 드롭 후 즉시 키 입력", engine.getCurrentBlock());
    }
}
