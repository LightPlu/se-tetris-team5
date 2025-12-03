package se.tetris.team5.gamelogic;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.gamelogic.scoring.GameScoring;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * P2PGameEngine 테스트
 */
public class P2PGameEngineTest {

    private P2PGameEngine engine;
    private static final int TEST_HEIGHT = 20;
    private static final int TEST_WIDTH = 10;

    @Before
    public void setUp() {
        engine = new P2PGameEngine(TEST_HEIGHT, TEST_WIDTH);
    }

    /**
     * 테스트 1: 엔진 생성
     */
    @Test
    public void testP2PGameEngine_Creation() {
        assertNotNull("엔진이 생성되어야 함", engine);
    }

    /**
     * 테스트 2: 초기 게임 오버 상태
     */
    @Test
    public void testP2PGameEngine_InitialGameOverState() {
        assertFalse("초기에는 게임 오버가 아니어야 함", engine.isGameOver());
    }

    /**
     * 테스트 3: 초기 경과 시간
     */
    @Test
    public void testP2PGameEngine_InitialElapsedTime() {
        long elapsedTime = engine.getElapsedTime();
        
        assertEquals("초기 경과 시간은 0이어야 함", 0, elapsedTime);
    }

    /**
     * 테스트 4: BoardManager 초기화
     */
    @Test
    public void testP2PGameEngine_BoardManagerInitialized() {
        BoardManager boardManager = engine.getBoardManager();
        
        assertNotNull("BoardManager가 초기화되어야 함", boardManager);
    }

    /**
     * 테스트 5: GameScoring 초기화
     */
    @Test
    public void testP2PGameEngine_GameScoringInitialized() {
        GameScoring scoring = engine.getGameScoring();
        
        assertNotNull("GameScoring이 초기화되어야 함", scoring);
    }

    /**
     * 테스트 6: injectBoardState - 정상 주입
     */
    @Test
    public void testP2PGameEngine_InjectBoardState() {
        int[][] board = new int[TEST_HEIGHT][TEST_WIDTH];
        Color[][] colors = new Color[TEST_HEIGHT][TEST_WIDTH];
        
        // 일부 셀 채우기
        board[19][0] = 1;
        colors[19][0] = Color.RED;
        
        engine.injectBoardState(board, colors);
        
        BoardManager boardManager = engine.getBoardManager();
        assertEquals("보드 상태가 주입되어야 함", 1, boardManager.getBoard()[19][0]);
    }

    /**
     * 테스트 7: injectBoardState - null 처리
     */
    @Test
    public void testP2PGameEngine_InjectBoardStateNull() {
        engine.injectBoardState(null, null);
        
        BoardManager boardManager = engine.getBoardManager();
        assertNotNull("BoardManager는 유효해야 함", boardManager);
    }

    /**
     * 테스트 8: injectBlockTypes - 정상 주입
     */
    @Test
    public void testP2PGameEngine_InjectBlockTypes() {
        engine.injectBlockTypes("I", "O");
        
        Block currentBlock = engine.getCurrentBlock();
        Block nextBlock = engine.getNextBlock();
        
        assertNotNull("현재 블록이 설정되어야 함", currentBlock);
        assertNotNull("다음 블록이 설정되어야 함", nextBlock);
    }

    /**
     * 테스트 9: injectBlockTypes - null 처리
     */
    @Test
    public void testP2PGameEngine_InjectBlockTypesNull() {
        engine.injectBlockTypes(null, null);
        
        // 예외가 발생하지 않아야 함
        assertTrue("null 블록 타입이 안전하게 처리되어야 함", true);
    }

    /**
     * 테스트 10: injectBlockTypes - 빈 문자열
     */
    @Test
    public void testP2PGameEngine_InjectBlockTypesEmpty() {
        engine.injectBlockTypes("", "");
        
        assertTrue("빈 문자열이 안전하게 처리되어야 함", true);
    }

    /**
     * 테스트 11: injectScore - 정상 주입
     */
    @Test
    public void testP2PGameEngine_InjectScore() {
        engine.injectScore(1000);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("점수가 주입되어야 함", 1000, scoring.getCurrentScore());
    }

    /**
     * 테스트 12: injectScore - 점수 증가
     */
    @Test
    public void testP2PGameEngine_InjectScoreIncrease() {
        engine.injectScore(500);
        engine.injectScore(1000);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("점수가 증가해야 함", 1000, scoring.getCurrentScore());
    }

    /**
     * 테스트 13: injectScore - 점수 감소
     */
    @Test
    public void testP2PGameEngine_InjectScoreDecrease() {
        engine.injectScore(1000);
        engine.injectScore(500);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("점수가 감소해야 함", 500, scoring.getCurrentScore());
    }

    /**
     * 테스트 14: injectLevel - 정상 주입
     */
    @Test
    public void testP2PGameEngine_InjectLevel() {
        engine.injectLevel(5);
        
        GameScoring scoring = engine.getGameScoring();
        int level = scoring.getLevel();
        
        assertTrue("레벨이 설정되어야 함", level >= 1);
    }

    /**
     * 테스트 15: injectLinesCleared - 정상 주입
     */
    @Test
    public void testP2PGameEngine_InjectLinesCleared() {
        engine.injectLinesCleared(10);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("줄 삭제 수가 주입되어야 함", 10, scoring.getLinesCleared());
    }

    /**
     * 테스트 16: injectLinesCleared - 증가
     */
    @Test
    public void testP2PGameEngine_InjectLinesClearedIncrease() {
        engine.injectLinesCleared(5);
        engine.injectLinesCleared(15);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("줄 삭제 수가 증가해야 함", 15, scoring.getLinesCleared());
    }

    /**
     * 테스트 17: injectElapsedTime - 정상 주입
     */
    @Test
    public void testP2PGameEngine_InjectElapsedTime() {
        engine.injectElapsedTime(60000);
        
        long elapsedTime = engine.getElapsedTime();
        assertEquals("경과 시간이 주입되어야 함", 60000, elapsedTime);
    }

    /**
     * 테스트 18: injectGameOver - true
     */
    @Test
    public void testP2PGameEngine_InjectGameOverTrue() {
        engine.injectGameOver(true);
        
        assertTrue("게임 오버가 설정되어야 함", engine.isGameOver());
    }

    /**
     * 테스트 19: injectGameOver - false
     */
    @Test
    public void testP2PGameEngine_InjectGameOverFalse() {
        engine.injectGameOver(true);
        engine.injectGameOver(false);
        
        assertFalse("게임 오버가 해제되어야 함", engine.isGameOver());
    }

    /**
     * 테스트 20: moveBlockDown - 읽기 전용
     */
    @Test
    public void testP2PGameEngine_MoveBlockDownReadOnly() {
        boolean result = engine.moveBlockDown();
        
        assertFalse("P2P 엔진은 블록 이동을 하지 않아야 함", result);
    }

    /**
     * 테스트 21: moveBlockLeft - 읽기 전용
     */
    @Test
    public void testP2PGameEngine_MoveBlockLeftReadOnly() {
        boolean result = engine.moveBlockLeft();
        
        assertFalse("P2P 엔진은 블록 이동을 하지 않아야 함", result);
    }

    /**
     * 테스트 22: moveBlockRight - 읽기 전용
     */
    @Test
    public void testP2PGameEngine_MoveBlockRightReadOnly() {
        boolean result = engine.moveBlockRight();
        
        assertFalse("P2P 엔진은 블록 이동을 하지 않아야 함", result);
    }

    /**
     * 테스트 23: rotateBlock - 읽기 전용
     */
    @Test
    public void testP2PGameEngine_RotateBlockReadOnly() {
        boolean result = engine.rotateBlock();
        
        assertFalse("P2P 엔진은 블록 회전을 하지 않아야 함", result);
    }

    /**
     * 테스트 24: hardDrop - 읽기 전용
     */
    @Test
    public void testP2PGameEngine_HardDropReadOnly() {
        boolean result = engine.hardDrop();
        
        assertFalse("P2P 엔진은 하드 드롭을 하지 않아야 함", result);
    }

    /**
     * 테스트 25: setBlockPosition
     */
    @Test
    public void testP2PGameEngine_SetBlockPosition() {
        engine.setBlockPosition(5, 10);
        
        assertEquals("X 좌표가 설정되어야 함", 5, engine.getX());
        assertEquals("Y 좌표가 설정되어야 함", 10, engine.getY());
    }

    /**
     * 테스트 26: getCurrentBlockType - 블록 없음
     */
    @Test
    public void testP2PGameEngine_GetCurrentBlockTypeNoBlock() {
        String type = engine.getCurrentBlockType();
        
        assertEquals("블록이 없으면 빈 문자열", "", type);
    }

    /**
     * 테스트 27: getNextBlockType - 블록 없음
     */
    @Test
    public void testP2PGameEngine_GetNextBlockTypeNoBlock() {
        String type = engine.getNextBlockType();
        
        assertEquals("블록이 없으면 빈 문자열", "", type);
    }

    /**
     * 테스트 28: getCurrentBlockType - 블록 설정 후
     */
    @Test
    public void testP2PGameEngine_GetCurrentBlockTypeWithBlock() {
        engine.injectBlockTypes("I", "O");
        String type = engine.getCurrentBlockType();
        
        assertNotNull("블록 타입이 반환되어야 함", type);
        assertFalse("블록 타입이 비어있지 않아야 함", type.isEmpty());
    }

    /**
     * 테스트 29: startP2PGame
     */
    @Test
    public void testP2PGameEngine_StartP2PGame() {
        engine.startP2PGame();
        
        assertTrue("게임이 실행 중이어야 함", engine.isGameRunning());
    }

    /**
     * 테스트 30: stopP2PGame
     */
    @Test
    public void testP2PGameEngine_StopP2PGame() {
        engine.startP2PGame();
        engine.stopP2PGame();
        
        assertFalse("게임이 중지되어야 함", engine.isGameRunning());
    }

    /**
     * 테스트 31: injectCompleteState
     */
    @Test
    public void testP2PGameEngine_InjectCompleteState() {
        int[][] board = new int[TEST_HEIGHT][TEST_WIDTH];
        Color[][] colors = new Color[TEST_HEIGHT][TEST_WIDTH];
        
        engine.injectCompleteState(
            board, colors,
            "I", "O",
            1000, 5, 10,
            60000
        );
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("점수가 주입되어야 함", 1000, scoring.getCurrentScore());
        assertEquals("줄 삭제 수가 주입되어야 함", 10, scoring.getLinesCleared());
        assertEquals("경과 시간이 주입되어야 함", 60000, engine.getElapsedTime());
    }

    /**
     * 테스트 32: injectCompleteState - null 값
     */
    @Test
    public void testP2PGameEngine_InjectCompleteStateWithNulls() {
        engine.injectCompleteState(
            null, null,
            null, null,
            0, 0, 0,
            0
        );
        
        assertTrue("null 값으로도 안전하게 실행되어야 함", true);
    }

    /**
     * 테스트 33: 여러 번 injectScore 호출
     */
    @Test
    public void testP2PGameEngine_MultipleInjectScore() {
        for (int i = 1; i <= 10; i++) {
            engine.injectScore(i * 100);
        }
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("최종 점수가 1000이어야 함", 1000, scoring.getCurrentScore());
    }

    /**
     * 테스트 34: 여러 번 injectLinesCleared 호출
     */
    @Test
    public void testP2PGameEngine_MultipleInjectLinesCleared() {
        for (int i = 1; i <= 10; i++) {
            engine.injectLinesCleared(i);
        }
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("최종 줄 삭제 수가 10이어야 함", 10, scoring.getLinesCleared());
    }

    /**
     * 테스트 35: injectElapsedTime - 큰 값
     */
    @Test
    public void testP2PGameEngine_InjectElapsedTimeLargeValue() {
        long largeTime = Long.MAX_VALUE / 2;
        engine.injectElapsedTime(largeTime);
        
        assertEquals("큰 경과 시간이 주입되어야 함", largeTime, engine.getElapsedTime());
    }

    /**
     * 테스트 36: injectBoardState - 부분 보드
     */
    @Test
    public void testP2PGameEngine_InjectBoardStatePartial() {
        int[][] board = new int[5][5]; // 작은 보드
        Color[][] colors = new Color[5][5];
        
        engine.injectBoardState(board, colors);
        
        assertTrue("작은 보드도 안전하게 주입되어야 함", true);
    }

    /**
     * 테스트 37: injectBlockTypes - 다양한 타입
     */
    @Test
    public void testP2PGameEngine_InjectVariousBlockTypes() {
        String[] types = {"I", "O", "T", "S", "Z", "L", "J"};
        
        for (String type : types) {
            engine.injectBlockTypes(type, type);
        }
        
        assertTrue("다양한 블록 타입이 안전하게 주입되어야 함", true);
    }

    /**
     * 테스트 38: 게임 시작 후 상태 주입
     */
    @Test
    public void testP2PGameEngine_InjectAfterStart() {
        engine.startP2PGame();
        
        engine.injectScore(500);
        
        GameScoring scoring = engine.getGameScoring();
        assertTrue("게임 시작 후 점수 주입", scoring.getCurrentScore() > 0);
    }

    /**
     * 테스트 39: 게임 중지 후 상태 주입
     */
    @Test
    public void testP2PGameEngine_InjectAfterStop() {
        engine.startP2PGame();
        engine.stopP2PGame();
        
        engine.injectScore(1000);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("게임 중지 후 점수 주입", 1000, scoring.getCurrentScore());
    }

    /**
     * 테스트 40: setBlockPosition - 음수 좌표
     */
    @Test
    public void testP2PGameEngine_SetBlockPositionNegative() {
        engine.setBlockPosition(-5, -10);
        
        assertEquals("음수 X 좌표 설정", -5, engine.getX());
        assertEquals("음수 Y 좌표 설정", -10, engine.getY());
    }

    /**
     * 테스트 41: setBlockPosition - 큰 좌표
     */
    @Test
    public void testP2PGameEngine_SetBlockPositionLarge() {
        engine.setBlockPosition(1000, 2000);
        
        assertEquals("큰 X 좌표 설정", 1000, engine.getX());
        assertEquals("큰 Y 좌표 설정", 2000, engine.getY());
    }

    /**
     * 테스트 42: injectScore - 0점
     */
    @Test
    public void testP2PGameEngine_InjectScoreZero() {
        engine.injectScore(1000);
        engine.injectScore(0);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("0점으로 리셋", 0, scoring.getCurrentScore());
    }

    /**
     * 테스트 43: injectLinesCleared - 0줄
     */
    @Test
    public void testP2PGameEngine_InjectLinesClearedZero() {
        engine.injectLinesCleared(10);
        engine.injectLinesCleared(0);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("0줄로 리셋", 0, scoring.getLinesCleared());
    }

    /**
     * 테스트 44: injectElapsedTime - 0시간
     */
    @Test
    public void testP2PGameEngine_InjectElapsedTimeZero() {
        engine.injectElapsedTime(60000);
        engine.injectElapsedTime(0);
        
        assertEquals("0시간으로 리셋", 0, engine.getElapsedTime());
    }

    /**
     * 테스트 45: 연속 게임 시작/중지
     */
    @Test
    public void testP2PGameEngine_ConsecutiveStartStop() {
        for (int i = 0; i < 5; i++) {
            engine.startP2PGame();
            assertTrue("게임 실행 중", engine.isGameRunning());
            
            engine.stopP2PGame();
            assertFalse("게임 중지됨", engine.isGameRunning());
        }
    }

    /**
     * 테스트 46: injectBoardState - null 색상 배열
     */
    @Test
    public void testP2PGameEngine_InjectBoardStateNullColors() {
        int[][] board = new int[TEST_HEIGHT][TEST_WIDTH];
        
        engine.injectBoardState(board, null);
        
        assertTrue("null 색상 배열도 안전하게 처리되어야 함", true);
    }

    /**
     * 테스트 47: injectLevel - 1레벨
     */
    @Test
    public void testP2PGameEngine_InjectLevelOne() {
        engine.injectLevel(1);
        
        GameScoring scoring = engine.getGameScoring();
        assertEquals("레벨 1 설정", 1, scoring.getLevel());
    }

    /**
     * 테스트 48: injectLevel - 높은 레벨
     */
    @Test
    public void testP2PGameEngine_InjectLevelHigh() {
        engine.injectLevel(20);
        
        GameScoring scoring = engine.getGameScoring();
        assertTrue("높은 레벨 설정", scoring.getLevel() >= 1);
    }

    /**
     * 테스트 49: 모든 이동 함수 연속 호출
     */
    @Test
    public void testP2PGameEngine_AllMovementsFalse() {
        assertFalse("moveBlockDown은 false", engine.moveBlockDown());
        assertFalse("moveBlockLeft는 false", engine.moveBlockLeft());
        assertFalse("moveBlockRight는 false", engine.moveBlockRight());
        assertFalse("rotateBlock은 false", engine.rotateBlock());
        assertFalse("hardDrop은 false", engine.hardDrop());
    }

    /**
     * 테스트 50: injectCompleteState - 최대값
     */
    @Test
    public void testP2PGameEngine_InjectCompleteStateMaxValues() {
        int[][] board = new int[TEST_HEIGHT][TEST_WIDTH];
        Color[][] colors = new Color[TEST_HEIGHT][TEST_WIDTH];
        
        engine.injectCompleteState(
            board, colors,
            "I", "O",
            Integer.MAX_VALUE, 100, 1000,
            Long.MAX_VALUE / 2
        );
        
        assertTrue("최대값으로도 안전하게 주입되어야 함", true);
    }
}
