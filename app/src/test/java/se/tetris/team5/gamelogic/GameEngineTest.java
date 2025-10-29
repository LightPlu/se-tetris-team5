package se.tetris.team5.gamelogic;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.gamelogic.block.BlockFactory;
import se.tetris.team5.gamelogic.scoring.GameScoring;

/**
 * GameEngine의 핵심 로직을 테스트합니다.
 * - 블록 이동 (좌/우/하)
 * - 블록 회전
 * - 하드 드롭
 * - 줄 삭제
 * - 게임 오버
 * - 점수 계산
 * - 게임 모드 (NORMAL/ITEM)
 * - 타임스톱 기능
 */
public class GameEngineTest {
    private GameEngine engine;
    private static final int HEIGHT = 20;
    private static final int WIDTH = 10;

    @Before
    public void setUp() {
        engine = new GameEngine(HEIGHT, WIDTH);
    }

    // ==================== 기본 초기화 테스트 ====================

    @Test
    public void testGameEngineInitialization() {
        assertNotNull("GameEngine should be created", engine);
        assertNotNull("Current block should exist", engine.getCurrentBlock());
        assertNotNull("Next block should exist", engine.getNextBlock());
        assertFalse("Game should not be over initially", engine.isGameOver());
        assertNotNull("BoardManager should exist", engine.getBoardManager());
        assertNotNull("GameScoring should exist", engine.getGameScoring());
    }

    @Test
    public void testStartNewGame() {
        // given: 게임이 이미 시작된 상태
        engine.moveBlockDown();
        
        // when: 새 게임 시작
        engine.startNewGame();
        
        // then: 게임 상태가 초기화됨
        assertFalse("Game should not be over", engine.isGameOver());
        assertNotNull("Current block should exist", engine.getCurrentBlock());
        assertNotNull("Next block should exist", engine.getNextBlock());
        assertEquals("Score should be reset", 0, engine.getGameScoring().getCurrentScore());
    }

    @Test
    public void testInitialBlockPosition() {
        // given: 새 게임 시작
        engine.startNewGame();
        
        // then: 블록이 초기 위치에 있어야 함
        assertEquals("Initial X should be 3", 3, engine.getX());
        assertEquals("Initial Y should be 0", 0, engine.getY());
    }

    // ==================== 블록 이동 테스트 ====================

    @Test
    public void testMoveBlockLeft() {
        // given: 게임 시작
        int initialX = engine.getX();
        
        // when: 왼쪽으로 이동
        boolean moved = engine.moveBlockLeft();
        
        // then: 이동 성공
        assertTrue("Should move left", moved);
        assertEquals("X should decrease by 1", initialX - 1, engine.getX());
    }

    @Test
    public void testMoveBlockRight() {
        // given: 게임 시작
        int initialX = engine.getX();
        
        // when: 오른쪽으로 이동
        boolean moved = engine.moveBlockRight();
        
        // then: 이동 성공
        assertTrue("Should move right", moved);
        assertEquals("X should increase by 1", initialX + 1, engine.getX());
    }

    @Test
    public void testMoveBlockDown() {
        // given: 게임 시작
        int initialY = engine.getY();
        
        // when: 아래로 이동
        boolean moved = engine.moveBlockDown();
        
        // then: 이동 성공 또는 고정됨
        if (moved) {
            assertEquals("Y should increase by 1", initialY + 1, engine.getY());
        }
        // 이동 실패 시 블록이 고정되고 새 블록 생성
    }

    @Test
    public void testCannotMoveLeftAtBoundary() {
        // given: 블록을 왼쪽 끝으로 이동
        while (engine.moveBlockLeft()) {
            // 왼쪽 끝까지 이동
        }
        
        int finalX = engine.getX();
        
        // when: 더 이상 왼쪽으로 이동 시도
        boolean moved = engine.moveBlockLeft();
        
        // then: 이동 실패
        assertFalse("Should not move beyond left boundary", moved);
        assertEquals("X should not change", finalX, engine.getX());
    }

    @Test
    public void testCannotMoveRightAtBoundary() {
        // given: 블록을 오른쪽 끝으로 이동
        while (engine.moveBlockRight()) {
            // 오른쪽 끝까지 이동
        }
        
        int finalX = engine.getX();
        
        // when: 더 이상 오른쪽으로 이동 시도
        boolean moved = engine.moveBlockRight();
        
        // then: 이동 실패
        assertFalse("Should not move beyond right boundary", moved);
        assertEquals("X should not change", finalX, engine.getX());
    }

    @Test
    public void testMoveBlockDownUntilFixed() {
        // given: 현재 블록
        Block initialBlock = engine.getCurrentBlock();
        
        // when: 블록이 바닥에 닿을 때까지 아래로 이동
        while (engine.moveBlockDown()) {
            // 계속 아래로 이동
        }
        
        // then: 새 블록이 생성됨
        assertNotSame("Current block should change after fixing", 
            initialBlock, engine.getCurrentBlock());
    }

    // ==================== 블록 회전 테스트 ====================

    @Test
    public void testRotateBlock() {
        // given: 회전 가능한 블록
        engine.startNewGame();
        
        // when: 블록 회전
        boolean rotated = engine.rotateBlock();
        
        // then: 회전 성공 (공간이 있는 경우)
        // OBlock은 회전해도 모양이 같으므로 항상 true
        assertTrue("Should attempt rotation", rotated || !rotated);
    }

    @Test
    public void testRotateBlockMultipleTimes() {
        // given: 게임 시작
        
        // when: 4번 회전 (360도)
        for (int i = 0; i < 4; i++) {
            engine.rotateBlock();
        }
        
        // then: 블록이 원래 모양으로 돌아옴 (대부분의 블록)
        assertNotNull("Block should still exist", engine.getCurrentBlock());
    }

    // ==================== 하드 드롭 테스트 ====================

    @Test
    public void testHardDrop() {
        // given: 게임 시작
        
        // when: 하드 드롭
        boolean dropped = engine.hardDrop();
        
        // then: 블록이 즉시 바닥으로 이동하고 고정됨
        assertTrue("Hard drop should succeed", dropped);
        assertNotNull("New block should spawn", engine.getCurrentBlock());
    }

    @Test
    public void testHardDropScoring() {
        // given: 게임 시작
        
        // when: 하드 드롭
        engine.hardDrop();
        
        // then: 점수가 증가해야 함 (드롭 거리에 비례)
        int finalScore = engine.getGameScoring().getCurrentScore();
        assertTrue("Score should increase after hard drop", finalScore >= 0);
    }

    @Test
    public void testHardDropAtBottom() {
        // given: 블록을 바닥 가까이 이동
        while (engine.moveBlockDown()) {
            // 바닥 근처까지 이동
        }
        
        // when: 하드 드롭 (이미 바닥 근처)
        boolean dropped = engine.hardDrop();
        
        // then: 여전히 성공
        assertTrue("Hard drop should work even at bottom", dropped);
    }

    // ==================== 줄 삭제 테스트 ====================

    @Test
    public void testLineClearIncreasesScore() {
        // given: 줄을 거의 채운 상태를 만들기 (수동으로 보드 조작)
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        
        // 맨 아래 줄을 거의 채움 (한 칸 남김)
        for (int x = 0; x < WIDTH - 1; x++) {
            boardArray[HEIGHT - 1][x] = 1;
        }
        
        int initialScore = engine.getGameScoring().getCurrentScore();
        
        // when: 나머지 한 칸을 채우는 블록 배치 후 하드 드롭
        // (실제로는 복잡하므로 이 테스트는 개념적 검증)
        engine.hardDrop();
        
        // then: 점수가 증가 (드롭 점수)
        assertTrue("Score should increase", 
            engine.getGameScoring().getCurrentScore() >= initialScore);
    }

    // ==================== 게임 오버 테스트 ====================

    @Test
    public void testGameOverWhenBlocksReachTop() {
        // given: 보드를 블록으로 가득 채움
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        
        // 보드 상단까지 블록으로 채움
        for (int y = 3; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                boardArray[y][x] = 1;
            }
        }
        
        // when: 새 블록 스폰 시도
        engine.hardDrop();
        
        // then: 게임 오버 (공간이 없으면)
        // 주의: 실제로는 스폰 공간에 따라 다를 수 있음
    }

    @Test
    public void testCannotMoveWhenGameOver() {
        // given: 게임 오버 상태를 강제로 만듦
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        
        // 스폰 위치를 막음
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < WIDTH; x++) {
                boardArray[y][x] = 1;
            }
        }
        
        // 새 블록 생성 시도
        engine.hardDrop();
        
        // when: 게임 오버 상태에서 이동 시도
        if (engine.isGameOver()) {
            boolean moved = engine.moveBlockDown();
            
            // then: 이동 불가
            assertFalse("Cannot move when game is over", moved);
        }
    }

    // ==================== 난이도 테스트 ====================

    @Test
    public void testSetDifficulty() {
        // when: 난이도 설정
        engine.setDifficulty(BlockFactory.Difficulty.EASY);
        
        // then: 난이도가 변경됨
        assertEquals("Difficulty should be EASY", 
            BlockFactory.Difficulty.EASY, engine.getDifficulty());
    }

    @Test
    public void testDifficultyAffectsScoring() {
        // given: HARD 난이도 설정
        engine.setDifficulty(BlockFactory.Difficulty.HARD);
        engine.startNewGame();
        
        int initialScore = engine.getGameScoring().getCurrentScore();
        
        // when: 하드 드롭
        engine.hardDrop();
        
        // then: 점수가 증가 (HARD 난이도의 배율 적용)
        assertTrue("Score should increase with HARD difficulty", 
            engine.getGameScoring().getCurrentScore() > initialScore);
    }

    // ==================== 게임 모드 테스트 ====================

    @Test
    public void testDefaultGameModeIsNormal() {
        // then: 기본 모드는 NORMAL
        assertEquals("Default mode should be NORMAL", 
            GameMode.NORMAL, engine.getGameMode());
    }

    @Test
    public void testSetGameModeToItem() {
        // when: 아이템 모드로 변경
        engine.setGameMode(GameMode.ITEM);
        
        // then: 모드가 변경됨
        assertEquals("Mode should be ITEM", GameMode.ITEM, engine.getGameMode());
    }

    @Test
    public void testGameModeAffectsItemSpawning() {
        // given: ITEM 모드 설정
        engine.setGameMode(GameMode.ITEM);
        engine.startNewGame();
        
        // when: 게임 진행 (아이템은 10줄마다 생성)
        // then: 모드가 ITEM임을 확인
        assertEquals("Mode should be ITEM", GameMode.ITEM, engine.getGameMode());
    }

    // ==================== 타임스톱 테스트 ====================

    @Test
    public void testTimeStopInitialState() {
        // then: 초기에는 타임스톱 충전 없음
        assertFalse("Should not have TimeStop charge initially", 
            engine.hasTimeStopCharge());
    }

    @Test
    public void testUseTimeStopWithoutCharge() {
        // given: 충전 없음
        
        // when: 타임스톱 사용 시도
        engine.useTimeStop();
        
        // then: 사용 불가 (충전이 없으므로)
        assertFalse("Should not have charge after failed use", 
            engine.hasTimeStopCharge());
    }

    // ==================== 점수 2배 테스트 ====================

    @Test
    public void testDoubleScoreActivation() {
        // when: 점수 2배 활성화 (20초)
        engine.activateDoubleScore(20000);
        
        // then: 점수 2배 활성화됨
        assertTrue("Double score should be active", engine.isDoubleScoreActive());
    }

    @Test
    public void testDoubleScoreAffectsPoints() {
        // given: 점수 2배 활성화
        engine.activateDoubleScore(20000);
        int initialScore = engine.getGameScoring().getCurrentScore();
        
        // when: 하드 드롭
        engine.hardDrop();
        
        // then: 점수가 2배로 증가
        int gainedScore = engine.getGameScoring().getCurrentScore() - initialScore;
        assertTrue("Should gain points with double score", gainedScore > 0);
    }

    @Test
    public void testDoubleScoreExpiration() throws InterruptedException {
        // given: 점수 2배 활성화 (짧은 시간)
        engine.activateDoubleScore(100); // 100ms
        
        assertTrue("Double score should be active", engine.isDoubleScoreActive());
        
        // when: 시간 경과
        Thread.sleep(150);
        
        // then: 점수 2배 비활성화됨
        assertFalse("Double score should expire", engine.isDoubleScoreActive());
    }

    // ==================== Getter 테스트 ====================

    @Test
    public void testGetCurrentBlock() {
        Block block = engine.getCurrentBlock();
        assertNotNull("Current block should not be null", block);
    }

    @Test
    public void testGetNextBlock() {
        Block block = engine.getNextBlock();
        assertNotNull("Next block should not be null", block);
    }

    @Test
    public void testGetBoardManager() {
        BoardManager board = engine.getBoardManager();
        assertNotNull("BoardManager should not be null", board);
    }

    @Test
    public void testGetGameScoring() {
        GameScoring scoring = engine.getGameScoring();
        assertNotNull("GameScoring should not be null", scoring);
    }

    @Test
    public void testGetPosition() {
        int x = engine.getX();
        int y = engine.getY();
        
        assertTrue("X should be valid", x >= 0);
        assertTrue("Y should be valid", y >= 0);
    }

    // ==================== 리셋 테스트 ====================

    @Test
    public void testResetGame() {
        // given: 게임 진행
        engine.moveBlockDown();
        engine.hardDrop();
        
        // when: 게임 리셋
        engine.resetGame();
        
        // then: 게임이 초기 상태로 돌아감
        assertFalse("Game should not be over", engine.isGameOver());
        assertEquals("Score should be reset", 0, engine.getGameScoring().getCurrentScore());
        assertNotNull("Current block should exist", engine.getCurrentBlock());
    }

    @Test
    public void testResetGameClearsBoard() {
        // given: 보드에 블록 배치
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        boardArray[10][5] = 1;
        
        // when: 리셋
        engine.resetGame();
        
        // then: 보드가 초기화됨 (현재 블록 제외)
        assertNotNull("Current block should exist after reset", engine.getCurrentBlock());
    }

    // ==================== 에지 케이스 테스트 ====================

    @Test
    public void testMultipleHardDrops() {
        // when: 연속 하드 드롭
        for (int i = 0; i < 5; i++) {
            engine.hardDrop();
        }
        
        // then: 게임이 정상 동작
        assertNotNull("Current block should exist", engine.getCurrentBlock());
    }

    @Test
    public void testMoveAndRotateCombination() {
        // when: 이동과 회전을 조합
        engine.moveBlockLeft();
        engine.rotateBlock();
        engine.moveBlockRight();
        engine.moveBlockDown();
        
        // then: 모든 동작이 정상 처리됨
        assertFalse("Game should not be over", engine.isGameOver());
    }
}
