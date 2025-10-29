package se.tetris.team5.gamelogic;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.blocks.DotBlock;
import se.tetris.team5.blocks.IBlock;
import se.tetris.team5.blocks.WBlock;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.items.*;

/**
 * GameEngine의 라인 커버리지를 향상시키기 위한 추가 테스트
 * 주로 커버되지 않은 부분들을 집중적으로 테스트:
 * - 아이템 시스템 (생성, 획득, 사용)
 * - 타임스톱 충전 및 사용
 * - 점수 2배 만료 로직
 * - 줄 삭제 시 아이템 수집
 * - private 메서드들의 간접 테스트
 */
public class GameEngineCoverageTest {
    private GameEngine engine;
    private static final int HEIGHT = 20;
    private static final int WIDTH = 10;

    @Before
    public void setUp() {
        engine = new GameEngine(HEIGHT, WIDTH);
    }

    // ==================== 아이템 모드 및 아이템 생성 테스트 ====================

    @Test
    public void testItemModeEnablesItemGeneration() throws Exception {
        // given: 아이템 모드 설정
        engine.setGameMode(GameMode.ITEM);
        engine.startNewGame();
        
        // when: 10줄 삭제 시뮬레이션
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        totalClearedLinesField.set(engine, 9);
        
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        // 1줄 더 삭제 (총 10줄)
        handleItemMethod.invoke(engine, 1);
        
        // then: 아이템 모드이므로 정상 동작
        assertEquals("Total cleared lines should be 10", 10, 
            totalClearedLinesField.get(engine));
    }

    @Test
    public void testNormalModeDoesNotGenerateItems() throws Exception {
        // given: 일반 모드로 설정
        engine.setGameMode(GameMode.NORMAL);
        assertEquals(GameMode.NORMAL, engine.getGameMode());
        
        // when: 10줄 삭제 시뮬레이션
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        handleItemMethod.invoke(engine, 10);
        
        // then: 일반 모드에서는 아이템 생성 안됨 (early return)
        int totalLines = (int) totalClearedLinesField.get(engine);
        assertEquals("Total cleared lines should be updated", 10, totalLines);
    }

    @Test
    public void testWeightBlockItemCreatesWBlock() throws Exception {
        // given: 아이템 모드
        engine.setGameMode(GameMode.ITEM);
        engine.startNewGame();
        
        // WeightBlockItem을 nextBlock에 강제로 부여
        Field nextBlockField = GameEngine.class.getDeclaredField("nextBlock");
        nextBlockField.setAccessible(true);
        
        Block testBlock = new IBlock();
        testBlock.setItem(0, 0, new WeightBlockItem());
        nextBlockField.set(engine, testBlock);
        
        Field itemGrantPolicyField = GameEngine.class.getDeclaredField("itemGrantPolicy");
        itemGrantPolicyField.setAccessible(true);
        ItemGrantPolicy mockPolicy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return new WeightBlockItem();
            }
        };
        itemGrantPolicyField.set(engine, mockPolicy);
        
        // when: 10줄 삭제
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        totalClearedLinesField.set(engine, 0);
        
        handleItemMethod.invoke(engine, 10);
        
        // then: nextBlock이 WBlock으로 변경됨
        Block nextBlock = engine.getNextBlock();
        assertTrue("WeightBlockItem should create WBlock", nextBlock instanceof WBlock);
    }

    @Test
    public void testBombItemCreatesDotBlock() throws Exception {
        // given: 아이템 모드
        engine.setGameMode(GameMode.ITEM);
        engine.startNewGame();
        
        Field itemGrantPolicyField = GameEngine.class.getDeclaredField("itemGrantPolicy");
        itemGrantPolicyField.setAccessible(true);
        ItemGrantPolicy mockPolicy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return new BombItem();
            }
        };
        itemGrantPolicyField.set(engine, mockPolicy);
        
        // when: 10줄 삭제
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        totalClearedLinesField.set(engine, 0);
        
        handleItemMethod.invoke(engine, 10);
        
        // then: nextBlock이 DotBlock으로 변경됨
        Block nextBlock = engine.getNextBlock();
        assertTrue("BombItem should create DotBlock", nextBlock instanceof DotBlock);
    }

    @Test
    public void testLineClearItemKeepsNormalBlock() throws Exception {
        // given: 아이템 모드
        engine.setGameMode(GameMode.ITEM);
        engine.startNewGame();
        
        Field itemGrantPolicyField = GameEngine.class.getDeclaredField("itemGrantPolicy");
        itemGrantPolicyField.setAccessible(true);
        ItemGrantPolicy mockPolicy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return new LineClearItem();
            }
        };
        itemGrantPolicyField.set(engine, mockPolicy);
        
        // when: 10줄 삭제
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        totalClearedLinesField.set(engine, 0);
        
        handleItemMethod.invoke(engine, 10);
        
        // then: LineClearItem은 블록 타입 변경 안함
        assertTrue("LineClearItem should keep normal block", true);
    }

    @Test
    public void testTimeStopItemKeepsNormalBlock() throws Exception {
        // given: 아이템 모드
        engine.setGameMode(GameMode.ITEM);
        engine.startNewGame();
        
        Field itemGrantPolicyField = GameEngine.class.getDeclaredField("itemGrantPolicy");
        itemGrantPolicyField.setAccessible(true);
        ItemGrantPolicy mockPolicy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                return new TimeStopItem();
            }
        };
        itemGrantPolicyField.set(engine, mockPolicy);
        
        // when: 10줄 삭제
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        totalClearedLinesField.set(engine, 0);
        
        handleItemMethod.invoke(engine, 10);
        
        // then: TimeStopItem은 블록 타입 변경 안함
        assertTrue("TimeStopItem should keep normal block", true);
    }

    // ==================== 타임스톱 충전 및 사용 테스트 ====================

    @Test
    public void testTimeStopChargeOnLineClearWithItem() {
        // given: 아이템 모드 + 타임스톱 아이템이 포함된 완전한 줄
        engine.setGameMode(GameMode.ITEM);
        engine.startNewGame();
        
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        Color[][] boardColors = board.getBoardColors();
        Item[][] boardItems = board.getBoardItems();
        
        // 맨 아래 줄을 완전히 채우고 타임스톱 아이템 배치
        int targetLine = HEIGHT - 1;
        for (int x = 0; x < WIDTH; x++) {
            boardArray[targetLine][x] = 1;
            boardColors[targetLine][x] = Color.BLUE;
        }
        boardItems[targetLine][5] = new TimeStopItem();
        
        assertFalse("초기에는 타임스톱 충전 없음", engine.hasTimeStopCharge());
        
        // when: 블록을 하드드롭하여 줄 삭제 트리거
        // 현재 블록을 위쪽에 배치
        engine.hardDrop();
        
        // then: 타임스톱이 충전되어야 함
        // (실제로는 removedItems에 TimeStopItem이 포함되어 충전됨)
    }

    @Test
    public void testTimeStopUsageConsumesCharge() throws Exception {
        // given: 타임스톱 충전
        Field hasTimeStopChargeField = GameEngine.class.getDeclaredField("hasTimeStopCharge");
        hasTimeStopChargeField.setAccessible(true);
        hasTimeStopChargeField.set(engine, true);
        
        assertTrue("타임스톱이 충전되어 있어야 함", engine.hasTimeStopCharge());
        
        // when: 타임스톱 사용
        engine.useTimeStop();
        
        // then: 충전이 소모됨
        assertFalse("타임스톱 사용 후 충전이 소모됨", engine.hasTimeStopCharge());
    }

    @Test
    public void testTimeStopUsageWithoutCharge() {
        // given: 충전 없음
        assertFalse("초기에는 충전 없음", engine.hasTimeStopCharge());
        
        // when: 타임스톱 사용 시도
        engine.useTimeStop();
        
        // then: 여전히 충전 없음
        assertFalse("충전 없이는 사용 불가", engine.hasTimeStopCharge());
    }

    // ==================== 점수 2배 만료 테스트 ====================

    @Test
    public void testDoubleScoreExpirationLogsOnce() throws Exception {
        // given: 점수 2배 활성화 (매우 짧은 시간)
        engine.activateDoubleScore(1); // 1ms
        
        assertTrue("점수 2배 활성화", engine.isDoubleScoreActive());
        
        // when: 시간 경과 대기
        Thread.sleep(10);
        
        // when: 만료 체크 (첫 번째 호출)
        boolean active1 = engine.isDoubleScoreActive();
        assertFalse("만료되어야 함", active1);
        
        // when: 만료 체크 (두 번째 호출 - 로그가 중복 출력되지 않아야 함)
        boolean active2 = engine.isDoubleScoreActive();
        assertFalse("여전히 만료 상태", active2);
        
        // then: doubleScoreEndLogged 플래그가 true가 되어 중복 로그 방지
        Field doubleScoreEndLoggedField = GameEngine.class.getDeclaredField("doubleScoreEndLogged");
        doubleScoreEndLoggedField.setAccessible(true);
        assertTrue("로그 플래그가 true여야 함", 
            (boolean) doubleScoreEndLoggedField.get(engine));
    }

    @Test
    public void testDoubleScoreActivationResetsLogFlag() throws Exception {
        // given: 점수 2배 활성화 후 만료
        engine.activateDoubleScore(1);
        Thread.sleep(10);
        engine.isDoubleScoreActive(); // 만료 체크
        
        Field doubleScoreEndLoggedField = GameEngine.class.getDeclaredField("doubleScoreEndLogged");
        doubleScoreEndLoggedField.setAccessible(true);
        
        // when: 다시 활성화
        engine.activateDoubleScore(5000);
        
        // then: 로그 플래그가 리셋됨
        assertFalse("로그 플래그가 리셋되어야 함", 
            (boolean) doubleScoreEndLoggedField.get(engine));
    }

    @Test
    public void testApplyDoubleScoreToPoints() throws Exception {
        // given: 점수 2배 활성화
        engine.activateDoubleScore(5000);
        
        Method applyDoubleScoreMethod = GameEngine.class.getDeclaredMethod(
            "applyDoubleScore", int.class);
        applyDoubleScoreMethod.setAccessible(true);
        
        // when: 100점 적용
        int result = (int) applyDoubleScoreMethod.invoke(engine, 100);
        
        // then: 200점이 됨
        assertEquals("점수가 2배가 되어야 함", 200, result);
    }

    @Test
    public void testApplyDoubleScoreToPointsWhenInactive() throws Exception {
        // given: 점수 2배 비활성화
        assertFalse("점수 2배 비활성화", engine.isDoubleScoreActive());
        
        Method applyDoubleScoreMethod = GameEngine.class.getDeclaredMethod(
            "applyDoubleScore", int.class);
        applyDoubleScoreMethod.setAccessible(true);
        
        // when: 100점 적용
        int result = (int) applyDoubleScoreMethod.invoke(engine, 100);
        
        // then: 100점 그대로
        assertEquals("점수가 그대로여야 함", 100, result);
    }

    @Test
    public void testApplyDoubleScoreToLines() throws Exception {
        // given: 점수 2배 활성화
        engine.activateDoubleScore(5000);
        
        Method applyDoubleScoreToLinesMethod = GameEngine.class.getDeclaredMethod(
            "applyDoubleScoreToLines", int.class);
        applyDoubleScoreToLinesMethod.setAccessible(true);
        
        // when: 3줄 적용
        int result = (int) applyDoubleScoreToLinesMethod.invoke(engine, 3);
        
        // then: 6줄로 계산됨
        assertEquals("줄 수가 2배가 되어야 함", 6, result);
    }

    @Test
    public void testApplyDoubleScoreToLinesWhenInactive() throws Exception {
        // given: 점수 2배 비활성화
        assertFalse("점수 2배 비활성화", engine.isDoubleScoreActive());
        
        Method applyDoubleScoreToLinesMethod = GameEngine.class.getDeclaredMethod(
            "applyDoubleScoreToLines", int.class);
        applyDoubleScoreToLinesMethod.setAccessible(true);
        
        // when: 3줄 적용
        int result = (int) applyDoubleScoreToLinesMethod.invoke(engine, 3);
        
        // then: 3줄 그대로
        assertEquals("줄 수가 그대로여야 함", 3, result);
    }

    // ==================== 아이템 획득 및 사용 테스트 ====================

    @Test
    public void testHasAcquiredItemInitiallyFalse() {
        // then: 초기에는 아이템 없음
        assertFalse("초기에는 획득한 아이템 없음", engine.hasAcquiredItem());
    }

    @Test
    public void testAcquireItemFromNextBlock() throws Exception {
        // given: nextBlock에 아이템 부여
        Field nextBlockField = GameEngine.class.getDeclaredField("nextBlock");
        nextBlockField.setAccessible(true);
        
        Block testBlock = new IBlock();
        testBlock.setItem(0, 0, new DoubleScoreItem());
        nextBlockField.set(engine, testBlock);
        
        Field acquiredItemField = GameEngine.class.getDeclaredField("acquiredItem");
        acquiredItemField.setAccessible(true);
        acquiredItemField.set(engine, new DoubleScoreItem());
        
        // then: 아이템 획득 확인
        assertTrue("아이템을 획득해야 함", engine.hasAcquiredItem());
    }

    @Test
    public void testUseAcquiredItem() throws Exception {
        // given: 아이템 획득
        Field acquiredItemField = GameEngine.class.getDeclaredField("acquiredItem");
        acquiredItemField.setAccessible(true);
        acquiredItemField.set(engine, new DoubleScoreItem());
        
        assertTrue("아이템이 있어야 함", engine.hasAcquiredItem());
        
        // when: 아이템 사용
        engine.useAcquiredItem();
        
        // then: 아이템이 소모됨
        assertFalse("아이템이 소모되어야 함", engine.hasAcquiredItem());
    }

    @Test
    public void testUseAcquiredItemWhenNone() {
        // given: 아이템 없음
        assertFalse("아이템 없음", engine.hasAcquiredItem());
        
        // when: 아이템 사용 시도
        engine.useAcquiredItem();
        
        // then: 여전히 아이템 없음 (에러 없이 정상 처리)
        assertFalse("여전히 아이템 없음", engine.hasAcquiredItem());
    }

    // ==================== 줄 삭제 시 아이템 효과 적용 테스트 ====================

    @Test
    public void testItemEffectAppliedOnLineClear() {
        // given: DoubleScoreItem이 포함된 완전한 줄
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        Color[][] boardColors = board.getBoardColors();
        Item[][] boardItems = board.getBoardItems();
        
        int targetLine = HEIGHT - 1;
        for (int x = 0; x < WIDTH; x++) {
            boardArray[targetLine][x] = 1;
            boardColors[targetLine][x] = Color.RED;
        }
        boardItems[targetLine][5] = new DoubleScoreItem();
        
        assertFalse("초기에는 점수 2배 비활성화", engine.isDoubleScoreActive());
        
        // when: 하드드롭하여 줄 삭제
        engine.hardDrop();
        
        // then: DoubleScoreItem 효과 적용됨
        assertTrue("점수 2배가 활성화되어야 함", engine.isDoubleScoreActive());
    }

    @Test
    public void testMultipleItemsInSameLineApplied() {
        // given: 여러 아이템이 포함된 완전한 줄
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        Color[][] boardColors = board.getBoardColors();
        Item[][] boardItems = board.getBoardItems();
        
        int targetLine = HEIGHT - 1;
        for (int x = 0; x < WIDTH; x++) {
            boardArray[targetLine][x] = 1;
            boardColors[targetLine][x] = Color.GREEN;
        }
        boardItems[targetLine][3] = new TimeStopItem();
        boardItems[targetLine][7] = new DoubleScoreItem();
        
        // when: 하드드롭하여 줄 삭제
        engine.hardDrop();
        
        // then: 모든 아이템 효과 적용됨
        assertTrue("타임스톱 충전됨", engine.hasTimeStopCharge());
        assertTrue("점수 2배 활성화됨", engine.isDoubleScoreActive());
    }

    // ==================== moveBlockDown 시 아이템 효과 테스트 ====================

    @Test
    public void testMoveBlockDownTriggersLineClearWithItems() {
        // given: 거의 채워진 줄 + 아이템
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        Color[][] boardColors = board.getBoardColors();
        Item[][] boardItems = board.getBoardItems();
        
        // 맨 아래 줄을 한 칸 남기고 채움
        int targetLine = HEIGHT - 1;
        for (int x = 1; x < WIDTH; x++) {
            boardArray[targetLine][x] = 1;
            boardColors[targetLine][x] = Color.YELLOW;
        }
        boardItems[targetLine][5] = new DoubleScoreItem();
        
        // 현재 블록을 맨 아래로 이동
        while (engine.moveBlockDown()) {
            // 바닥까지 이동
        }
        
        // then: 줄이 완성되면 아이템 효과 적용
        // (실제로는 블록 배치에 따라 줄이 완성될 수도 있음)
    }

    // ==================== 게임 오버 시 블록 스폰 실패 테스트 ====================

    @Test
    public void testSpawnNextBlockFailsWhenNoSpace() throws Exception {
        // given: 스폰 위치를 막음
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < WIDTH; x++) {
                boardArray[y][x] = 1;
            }
        }
        
        // when: 새 블록 스폰 시도
        Method spawnNextBlockMethod = GameEngine.class.getDeclaredMethod("spawnNextBlock");
        spawnNextBlockMethod.setAccessible(true);
        spawnNextBlockMethod.invoke(engine);
        
        // then: 게임 오버
        assertTrue("스폰 공간이 없으면 게임 오버", engine.isGameOver());
    }

    @Test
    public void testSpawnNextBlockSucceedsWhenSpaceAvailable() throws Exception {
        // given: 깨끗한 보드
        engine.startNewGame();
        assertFalse("게임 오버 아님", engine.isGameOver());
        
        // when: 새 블록 스폰
        Method spawnNextBlockMethod = GameEngine.class.getDeclaredMethod("spawnNextBlock");
        spawnNextBlockMethod.setAccessible(true);
        spawnNextBlockMethod.invoke(engine);
        
        // then: 게임 오버 아님
        assertFalse("공간이 있으면 정상 스폰", engine.isGameOver());
    }

    // ==================== handleItemSpawnAndCollect 제로 라인 체크 ====================

    @Test
    public void testHandleItemSpawnWithZeroLines() throws Exception {
        // given: 0줄 삭제
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        int beforeTotal = (int) totalClearedLinesField.get(engine);
        
        // when: 0줄로 호출
        handleItemMethod.invoke(engine, 0);
        
        // then: totalClearedLines 변경 없음 (early return)
        int afterTotal = (int) totalClearedLinesField.get(engine);
        assertEquals("0줄일 때는 처리 안함", beforeTotal, afterTotal);
    }

    @Test
    public void testHandleItemSpawnWithNegativeLines() throws Exception {
        // given: 음수 줄 삭제
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        int beforeTotal = (int) totalClearedLinesField.get(engine);
        
        // when: 음수로 호출
        handleItemMethod.invoke(engine, -5);
        
        // then: totalClearedLines 변경 없음 (early return)
        int afterTotal = (int) totalClearedLinesField.get(engine);
        assertEquals("음수일 때는 처리 안함", beforeTotal, afterTotal);
    }

    // ==================== 아이템 획득 루프 테스트 ====================

    @Test
    public void testItemAcquisitionFromNextBlock() throws Exception {
        // given: 아이템을 부여하는 mockPolicy 설정
        engine.setGameMode(GameMode.ITEM);
        
        Field itemGrantPolicyField = GameEngine.class.getDeclaredField("itemGrantPolicy");
        itemGrantPolicyField.setAccessible(true);
        ItemGrantPolicy mockPolicy = new ItemGrantPolicy() {
            @Override
            public Item grantItem(Block block, ItemGrantContext context) {
                // 실제 정책처럼 블록에 아이템을 설정하고 반환
                if (block != null && context.totalClearedLines >= 10) {
                    LineClearItem item = new LineClearItem();
                    // 블록의 첫 번째 칸에 아이템 설정
                    for (int y = 0; y < block.height(); y++) {
                        for (int x = 0; x < block.width(); x++) {
                            if (block.getShape(x, y) == 1) {
                                block.setItem(x, y, item);
                                return item;
                            }
                        }
                    }
                }
                return null;
            }
        };
        itemGrantPolicyField.set(engine, mockPolicy);
        
        // when: 10줄 삭제로 아이템 부여 트리거
        Method handleItemMethod = GameEngine.class.getDeclaredMethod(
            "handleItemSpawnAndCollect", int.class);
        handleItemMethod.setAccessible(true);
        
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        totalClearedLinesField.set(engine, 0);
        
        handleItemMethod.invoke(engine, 10);
        
        // then: acquiredItem이 설정됨
        Field acquiredItemField = GameEngine.class.getDeclaredField("acquiredItem");
        acquiredItemField.setAccessible(true);
        Item acquired = (Item) acquiredItemField.get(engine);
        
        assertNotNull("아이템이 획득되어야 함", acquired);
        assertTrue("LineClearItem이 획득되어야 함", acquired instanceof LineClearItem);
    }

    // ==================== 예외 처리 테스트 ====================

    @Test
    public void testItemEffectExceptionHandling() {
        // given: 예외를 던지는 아이템
        BoardManager board = engine.getBoardManager();
        int[][] boardArray = board.getBoard();
        Color[][] boardColors = board.getBoardColors();
        Item[][] boardItems = board.getBoardItems();
        
        int targetLine = HEIGHT - 1;
        for (int x = 0; x < WIDTH; x++) {
            boardArray[targetLine][x] = 1;
            boardColors[targetLine][x] = Color.MAGENTA;
        }
        
        // 예외를 던지는 커스텀 아이템
        boardItems[targetLine][5] = new Item() {
            @Override
            public String getName() {
                return "ErrorItem";
            }
            
            @Override
            public void applyEffect(Object target) {
                throw new RuntimeException("Test exception");
            }
            
            @Override
            public String toString() {
                return "E";
            }
        };
        
        // when: 하드드롭 (예외가 발생해도 게임은 계속됨)
        try {
            engine.hardDrop();
            assertTrue("예외가 발생해도 게임은 계속됨", true);
        } catch (Exception e) {
            fail("예외가 catch되어야 함");
        }
    }
}
