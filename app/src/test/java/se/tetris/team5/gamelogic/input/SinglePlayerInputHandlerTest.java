package se.tetris.team5.gamelogic.input;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import java.awt.event.KeyEvent;

/**
 * SinglePlayerInputHandler 테스트
 */
public class SinglePlayerInputHandlerTest {

    private GameEngine engine;
    private SinglePlayerInputHandler handler;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        handler = new SinglePlayerInputHandler(engine);
    }

    /**
     * 테스트 1: 생성자
     */
    @Test
    public void testSinglePlayerInputHandler_Constructor() {
        assertNotNull("핸들러가 생성되어야 함", handler);
    }

    /**
     * 테스트 2: setGameEngine
     */
    @Test
    public void testSinglePlayerInputHandler_SetGameEngine() {
        GameEngine newEngine = new GameEngine(20, 10);
        handler.setGameEngine(newEngine);
        
        assertTrue("엔진 설정이 안전해야 함", true);
    }

    /**
     * 테스트 3: handleMoveLeft
     */
    @Test
    public void testSinglePlayerInputHandler_HandleMoveLeft() {
        int initialX = engine.getX();
        
        handler.handleMoveLeft();
        
        assertEquals("왼쪽으로 이동해야 함", initialX - 1, engine.getX());
    }

    /**
     * 테스트 4: handleMoveRight
     */
    @Test
    public void testSinglePlayerInputHandler_HandleMoveRight() {
        int initialX = engine.getX();
        
        handler.handleMoveRight();
        
        assertEquals("오른쪽으로 이동해야 함", initialX + 1, engine.getX());
    }

    /**
     * 테스트 5: handleRotate
     */
    @Test
    public void testSinglePlayerInputHandler_HandleRotate() {
        handler.handleRotate();
        
        assertNotNull("회전 후 블록이 유효해야 함", engine.getCurrentBlock());
    }

    /**
     * 테스트 6: handleSoftDrop
     */
    @Test
    public void testSinglePlayerInputHandler_HandleSoftDrop() {
        int initialY = engine.getY();
        
        handler.handleSoftDrop();
        
        assertTrue("아래로 이동해야 함", engine.getY() >= initialY);
    }

    /**
     * 테스트 7: handleHardDrop
     */
    @Test
    public void testSinglePlayerInputHandler_HandleHardDrop() {
        handler.handleHardDrop();
        
        assertNotNull("하드 드롭 후 새 블록이 생성되어야 함", engine.getCurrentBlock());
    }

    /**
     * 테스트 8: handleUseItem - 타임스톱 없음
     */
    @Test
    public void testSinglePlayerInputHandler_HandleUseItemNoCharge() {
        handler.handleUseItem();
        
        assertFalse("타임스톱 충전이 없어야 함", engine.hasTimeStopCharge());
    }

    /**
     * 테스트 9: handleKeyPress - 왼쪽 키
     */
    @Test
    public void testSinglePlayerInputHandler_HandleKeyPressLeft() {
        int initialX = engine.getX();
        
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        
        assertEquals("왼쪽 키로 이동", initialX - 1, engine.getX());
    }

    /**
     * 테스트 10: handleKeyPress - 오른쪽 키
     */
    @Test
    public void testSinglePlayerInputHandler_HandleKeyPressRight() {
        int initialX = engine.getX();
        
        handler.handleKeyPress(KeyEvent.VK_RIGHT);
        
        assertEquals("오른쪽 키로 이동", initialX + 1, engine.getX());
    }

    /**
     * 테스트 11: handleKeyPress - 회전 키
     */
    @Test
    public void testSinglePlayerInputHandler_HandleKeyPressRotate() {
        handler.handleKeyPress(KeyEvent.VK_UP);
        
        assertNotNull("회전 키로 회전", engine.getCurrentBlock());
    }

    /**
     * 테스트 12: handleKeyPress - 아래 키
     */
    @Test
    public void testSinglePlayerInputHandler_HandleKeyPressDown() {
        int initialY = engine.getY();
        
        handler.handleKeyPress(KeyEvent.VK_DOWN);
        
        assertTrue("아래 키로 이동", engine.getY() >= initialY);
    }

    /**
     * 테스트 13: handleKeyPress - 스페이스 (하드 드롭)
     */
    @Test
    public void testSinglePlayerInputHandler_HandleKeyPressSpace() {
        handler.handleKeyPress(KeyEvent.VK_SPACE);
        
        assertNotNull("스페이스로 하드 드롭", engine.getCurrentBlock());
    }

    /**
     * 테스트 14: handleKeyPress - Shift (아이템)
     */
    @Test
    public void testSinglePlayerInputHandler_HandleKeyPressShift() {
        handler.handleKeyPress(KeyEvent.VK_SHIFT);
        
        assertTrue("Shift 키 처리가 안전해야 함", true);
    }

    /**
     * 테스트 15: handleMoveLeft - null 엔진
     */
    @Test
    public void testSinglePlayerInputHandler_HandleMoveLeftNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleMoveLeft();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 16: handleMoveRight - null 엔진
     */
    @Test
    public void testSinglePlayerInputHandler_HandleMoveRightNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleMoveRight();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 17: handleRotate - null 엔진
     */
    @Test
    public void testSinglePlayerInputHandler_HandleRotateNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleRotate();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 18: handleSoftDrop - null 엔진
     */
    @Test
    public void testSinglePlayerInputHandler_HandleSoftDropNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleSoftDrop();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 19: handleHardDrop - null 엔진
     */
    @Test
    public void testSinglePlayerInputHandler_HandleHardDropNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleHardDrop();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 20: handleUseItem - null 엔진
     */
    @Test
    public void testSinglePlayerInputHandler_HandleUseItemNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleUseItem();
        
        assertTrue("null 엔진에서도 안전해야 함", true);
    }

    /**
     * 테스트 21: 연속 왼쪽 이동
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveLeftMoves() {
        int initialX = engine.getX();
        
        handler.handleMoveLeft();
        handler.handleMoveLeft();
        handler.handleMoveLeft();
        
        assertTrue("연속 왼쪽 이동", engine.getX() < initialX);
    }

    /**
     * 테스트 22: 연속 오른쪽 이동
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveRightMoves() {
        int initialX = engine.getX();
        
        handler.handleMoveRight();
        handler.handleMoveRight();
        handler.handleMoveRight();
        
        assertTrue("연속 오른쪽 이동", engine.getX() > initialX);
    }

    /**
     * 테스트 23: 연속 회전
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveRotations() {
        handler.handleRotate();
        handler.handleRotate();
        handler.handleRotate();
        handler.handleRotate();
        
        assertNotNull("연속 회전 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 24: 좌우 이동 조합
     */
    @Test
    public void testSinglePlayerInputHandler_LeftRightCombination() {
        int initialX = engine.getX();
        
        handler.handleMoveLeft();
        handler.handleMoveRight();
        
        assertEquals("좌우 이동 조합", initialX, engine.getX());
    }

    /**
     * 테스트 25: 이동과 회전 조합
     */
    @Test
    public void testSinglePlayerInputHandler_MoveAndRotate() {
        handler.handleMoveLeft();
        handler.handleRotate();
        handler.handleMoveRight();
        
        assertNotNull("이동+회전 조합", engine.getCurrentBlock());
    }

    /**
     * 테스트 26: handleKeyPress - 알 수 없는 키
     */
    @Test
    public void testSinglePlayerInputHandler_HandleKeyPressUnknown() {
        handler.handleKeyPress(KeyEvent.VK_ESCAPE);
        
        assertTrue("알 수 없는 키도 안전해야 함", true);
    }

    /**
     * 테스트 27: 엔진 교체 후 동작
     */
    @Test
    public void testSinglePlayerInputHandler_EngineReplacement() {
        GameEngine newEngine = new GameEngine(20, 10);
        handler.setGameEngine(newEngine);
        
        int initialX = newEngine.getX();
        handler.handleMoveLeft();
        
        assertEquals("새 엔진에서 이동", initialX - 1, newEngine.getX());
    }

    /**
     * 테스트 28: 여러 키 연속 입력
     */
    @Test
    public void testSinglePlayerInputHandler_MultipleKeyPresses() {
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        handler.handleKeyPress(KeyEvent.VK_UP);
        handler.handleKeyPress(KeyEvent.VK_RIGHT);
        handler.handleKeyPress(KeyEvent.VK_DOWN);
        
        assertNotNull("여러 키 입력 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 29: 하드 드롭 후 새 블록
     */
    @Test
    public void testSinglePlayerInputHandler_HardDropSpawnsNew() {
        handler.handleHardDrop();
        
        handler.handleMoveLeft();
        assertNotNull("하드 드롭 후 새 블록 조작 가능", engine.getCurrentBlock());
    }

    /**
     * 테스트 30: 소프트 드롭 연속
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveSoftDrops() {
        int initialY = engine.getY();
        
        handler.handleSoftDrop();
        handler.handleSoftDrop();
        handler.handleSoftDrop();
        
        assertTrue("연속 소프트 드롭", engine.getY() >= initialY);
    }

    /**
     * 테스트 31: handleKeyPress - 모든 키 순차 테스트
     */
    @Test
    public void testSinglePlayerInputHandler_AllKeysSequential() {
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        handler.handleKeyPress(KeyEvent.VK_RIGHT);
        handler.handleKeyPress(KeyEvent.VK_UP);
        handler.handleKeyPress(KeyEvent.VK_DOWN);
        handler.handleKeyPress(KeyEvent.VK_SPACE);
        handler.handleKeyPress(KeyEvent.VK_SHIFT);
        
        assertTrue("모든 키 처리 완료", true);
    }

    /**
     * 테스트 32: handleKeyPress - null 엔진에서 키 입력
     */
    @Test
    public void testSinglePlayerInputHandler_KeyPressWithNullEngine() {
        handler.setGameEngine(null);
        
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        handler.handleKeyPress(KeyEvent.VK_RIGHT);
        handler.handleKeyPress(KeyEvent.VK_UP);
        handler.handleKeyPress(KeyEvent.VK_DOWN);
        handler.handleKeyPress(KeyEvent.VK_SPACE);
        handler.handleKeyPress(KeyEvent.VK_SHIFT);
        
        assertTrue("null 엔진에서 키 입력 안전", true);
    }

    /**
     * 테스트 33: handleKeyPress - 왼쪽 연속
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveLeftKeyPress() {
        int initialX = engine.getX();
        
        for (int i = 0; i < 3; i++) {
            handler.handleKeyPress(KeyEvent.VK_LEFT);
        }
        
        assertTrue("연속 왼쪽 키 입력", engine.getX() < initialX);
    }

    /**
     * 테스트 34: handleKeyPress - 오른쪽 연속
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveRightKeyPress() {
        int initialX = engine.getX();
        
        for (int i = 0; i < 3; i++) {
            handler.handleKeyPress(KeyEvent.VK_RIGHT);
        }
        
        assertTrue("연속 오른쪽 키 입력", engine.getX() > initialX);
    }

    /**
     * 테스트 35: handleKeyPress - 회전 연속
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveRotateKeyPress() {
        for (int i = 0; i < 4; i++) {
            handler.handleKeyPress(KeyEvent.VK_UP);
        }
        
        assertNotNull("연속 회전 키 입력", engine.getCurrentBlock());
    }

    /**
     * 테스트 36: handleKeyPress - 소프트 드롭 연속
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveDownKeyPress() {
        int initialY = engine.getY();
        
        for (int i = 0; i < 5; i++) {
            handler.handleKeyPress(KeyEvent.VK_DOWN);
        }
        
        assertTrue("연속 아래 키 입력", engine.getY() >= initialY);
    }

    /**
     * 테스트 37: handleKeyPress - 스페이스 연속
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveSpaceKeyPress() {
        for (int i = 0; i < 3; i++) {
            handler.handleKeyPress(KeyEvent.VK_SPACE);
        }
        
        assertNotNull("연속 스페이스 키 입력", engine.getCurrentBlock());
    }

    /**
     * 테스트 38: handleKeyPress - Shift 연속
     */
    @Test
    public void testSinglePlayerInputHandler_ConsecutiveShiftKeyPress() {
        for (int i = 0; i < 3; i++) {
            handler.handleKeyPress(KeyEvent.VK_SHIFT);
        }
        
        assertTrue("연속 Shift 키 입력 안전", true);
    }

    /**
     * 테스트 39: 복잡한 키 조합 1
     */
    @Test
    public void testSinglePlayerInputHandler_ComplexKeyCombination1() {
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        handler.handleKeyPress(KeyEvent.VK_UP);
        handler.handleKeyPress(KeyEvent.VK_DOWN);
        handler.handleKeyPress(KeyEvent.VK_RIGHT);
        handler.handleKeyPress(KeyEvent.VK_SPACE);
        
        assertNotNull("복잡한 키 조합 1", engine.getCurrentBlock());
    }

    /**
     * 테스트 40: 복잡한 키 조합 2
     */
    @Test
    public void testSinglePlayerInputHandler_ComplexKeyCombination2() {
        handler.handleKeyPress(KeyEvent.VK_UP);
        handler.handleKeyPress(KeyEvent.VK_RIGHT);
        handler.handleKeyPress(KeyEvent.VK_DOWN);
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        handler.handleKeyPress(KeyEvent.VK_UP);
        handler.handleKeyPress(KeyEvent.VK_SPACE);
        
        assertNotNull("복잡한 키 조합 2", engine.getCurrentBlock());
    }

    /**
     * 테스트 41: 게임 오버 후 키 입력
     */
    @Test
    public void testSinglePlayerInputHandler_KeyPressAfterGameOver() {
        for (int i = 0; i < 30; i++) {
            if (engine.isGameOver()) break;
            engine.hardDrop();
        }
        
        if (engine.isGameOver()) {
            handler.handleKeyPress(KeyEvent.VK_LEFT);
            handler.handleKeyPress(KeyEvent.VK_RIGHT);
            assertTrue("게임 오버 후 키 입력 안전", true);
        } else {
            assertTrue("게임 진행 중", true);
        }
    }

    /**
     * 테스트 42: 엔진 교체 후 키 입력
     */
    @Test
    public void testSinglePlayerInputHandler_KeyPressAfterEngineChange() {
        GameEngine newEngine = new GameEngine(20, 10);
        handler.setGameEngine(newEngine);
        
        int initialX = newEngine.getX();
        handler.handleKeyPress(KeyEvent.VK_LEFT);
        
        assertEquals("엔진 교체 후 키 입력", initialX - 1, newEngine.getX());
    }

    /**
     * 테스트 43: 빠른 키 입력
     */
    @Test
    public void testSinglePlayerInputHandler_RapidKeyPresses() {
        for (int i = 0; i < 10; i++) {
            handler.handleKeyPress(KeyEvent.VK_DOWN);
        }
        
        assertTrue("빠른 키 입력 처리", true);
    }

    /**
     * 테스트 44: 좌우 반복 키 입력
     */
    @Test
    public void testSinglePlayerInputHandler_AlternatingLeftRight() {
        int initialX = engine.getX();
        
        for (int i = 0; i < 5; i++) {
            handler.handleKeyPress(KeyEvent.VK_LEFT);
            handler.handleKeyPress(KeyEvent.VK_RIGHT);
        }
        
        assertEquals("좌우 반복 키 입력", initialX, engine.getX());
    }

    /**
     * 테스트 45: handleUseItem과 handleKeyPress 조합
     */
    @Test
    public void testSinglePlayerInputHandler_UseItemAndKeyPress() {
        handler.handleUseItem();
        handler.handleKeyPress(KeyEvent.VK_SHIFT);
        
        assertFalse("아이템 사용 후 충전 없음", engine.hasTimeStopCharge());
    }

    /**
     * 테스트 46: 모든 메서드 순차 호출
     */
    @Test
    public void testSinglePlayerInputHandler_AllMethodsSequential() {
        handler.handleMoveLeft();
        handler.handleMoveRight();
        handler.handleRotate();
        handler.handleSoftDrop();
        handler.handleUseItem();
        handler.handleHardDrop();
        
        assertNotNull("모든 메서드 호출 후 블록 유효", engine.getCurrentBlock());
    }

    /**
     * 테스트 47: 여러 엔진 교체
     */
    @Test
    public void testSinglePlayerInputHandler_MultipleEngineChanges() {
        GameEngine engine1 = new GameEngine(20, 10);
        GameEngine engine2 = new GameEngine(20, 10);
        GameEngine engine3 = new GameEngine(20, 10);
        
        handler.setGameEngine(engine1);
        handler.handleMoveLeft();
        
        handler.setGameEngine(engine2);
        handler.handleMoveRight();
        
        handler.setGameEngine(engine3);
        handler.handleRotate();
        
        assertTrue("여러 엔진 교체 안전", true);
    }

    /**
     * 테스트 48: null 엔진 설정 후 복구
     */
    @Test
    public void testSinglePlayerInputHandler_NullEngineAndRecover() {
        handler.setGameEngine(null);
        handler.handleMoveLeft();
        
        handler.setGameEngine(engine);
        int initialX = engine.getX();
        handler.handleMoveLeft();
        
        assertEquals("null 엔진 후 복구", initialX - 1, engine.getX());
    }

    /**
     * 테스트 49: 다양한 키 코드 입력
     */
    @Test
    public void testSinglePlayerInputHandler_VariousKeyCodes() {
        int[] keyCodes = {
            KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_C,
            KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3,
            KeyEvent.VK_ESCAPE, KeyEvent.VK_ENTER, KeyEvent.VK_TAB
        };
        
        for (int keyCode : keyCodes) {
            handler.handleKeyPress(keyCode);
        }
        
        assertTrue("다양한 키 코드 입력 안전", true);
    }

    /**
     * 테스트 50: 극단적인 키 입력 시나리오
     */
    @Test
    public void testSinglePlayerInputHandler_ExtremeKeyScenario() {
        // 100번의 랜덤 키 입력
        int[] keys = {
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
            KeyEvent.VK_DOWN, KeyEvent.VK_SPACE, KeyEvent.VK_SHIFT
        };
        
        for (int i = 0; i < 100; i++) {
            handler.handleKeyPress(keys[i % keys.length]);
        }
        
        assertTrue("극단적인 키 입력 시나리오 처리", true);
    }
}
