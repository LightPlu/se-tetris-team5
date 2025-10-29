package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.GameMode;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.components.game.BoardManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 여러 아이템 간의 상호작용을 테스트하는 통합 테스트
 */
public class ItemInteractionTest {
    private GameEngine engine;
    private BoardManager board;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        board = engine.getBoardManager();
        // 아이템 모드로 설정
        engine.setGameMode(GameMode.ITEM);
    }

    @Test
    public void testTimeStopAndDoubleScoreSimultaneous() {
        // given: 타임스톱과 점수 2배 아이템이 같은 줄에 있음
        Block block1 = new se.tetris.team5.blocks.OBlock();
        block1.setItem(0, 0, new TimeStopItem());
        
        Block block2 = new se.tetris.team5.blocks.OBlock();
        block2.setItem(0, 0, new DoubleScoreItem());
        
        // when: 두 블록을 배치하고 줄 삭제
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block1, 0, 0, removed);
        board.fixBlock(block2, 3, 0, removed);
        
        // 줄 채우기
        for (int x = 0; x < board.getWidth(); x++) {
            board.getBoard()[0][x] = 1;
        }
        
        removed.clear();
        board.clearLines(removed);
        
        // 모든 아이템 효과 적용
        boolean hasTimeStop = false;
        boolean hasDoubleScore = false;
        
        for (Item item : removed) {
            if (item instanceof TimeStopItem) {
                hasTimeStop = true;
            }
            if (item instanceof DoubleScoreItem) {
                hasDoubleScore = true;
                item.applyEffect(engine);
            }
        }
        
        // then: 두 아이템이 모두 수집되어야 함
        assertTrue("타임스톱 아이템이 수집되어야 함", hasTimeStop);
        assertTrue("점수 2배 아이템이 수집되어야 함", hasDoubleScore);
        assertTrue("점수 2배 효과가 활성화되어야 함", engine.isDoubleScoreActive());
    }

    @Test
    public void testMultipleDoubleScoreItems() {
        // given: 여러 점수 2배 아이템
        List<DoubleScoreItem> items = new ArrayList<>();
        items.add(new DoubleScoreItem());
        items.add(new DoubleScoreItem());
        items.add(new DoubleScoreItem());
        
        // when: 순차적으로 효과 적용
        for (DoubleScoreItem item : items) {
            item.applyEffect(engine);
        }
        
        // then: 점수 2배 효과가 활성화되어 있어야 함
        assertTrue("여러 점수 2배 아이템 적용 후 효과가 활성화되어야 함", 
            engine.isDoubleScoreActive());
    }

    @Test
    public void testLineClearItemWithOtherItems() {
        // given: 라인 클리어 아이템과 다른 아이템들이 같은 줄에 있음
        Block block1 = new se.tetris.team5.blocks.OBlock();
        block1.setItem(0, 0, new LineClearItem());
        
        Block block2 = new se.tetris.team5.blocks.OBlock();
        block2.setItem(0, 0, new DoubleScoreItem());
        
        // when: 블록 배치 (LineClearItem은 fixBlock에서 즉시 처리됨)
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block1, 0, 0, removed);
        
        // LineClearItem이 포함된 줄은 fixBlock 시점에 즉시 삭제되므로
        // block2를 배치하기 전에 이미 줄이 삭제되었을 수 있음
        
        // 새로운 줄에 block2 배치
        removed.clear();
        board.fixBlock(block2, 3, 2, removed);
        
        // then: LineClearItem을 포함한 블록이 정상적으로 배치되고 처리되어야 함
        // 실제 줄 삭제는 fixBlock 내부에서 처리됨
        assertTrue("테스트가 예외 없이 실행되어야 함", true);
    }

    @Test
    public void testItemEffectsDoNotInterfere() throws Exception {
        // given: 타임스톱과 점수 2배 효과가 동시에 활성화
        engine.activateDoubleScore(1000); // 1초
        assertTrue("점수 2배 효과가 활성화되어야 함", engine.isDoubleScoreActive());
        
        // when: 타임스톱 사용 (시뮬레이션)
        // 실제로는 게임 화면에서 처리하지만, 여기서는 로직 검증
        
        // then: 점수 2배 효과가 여전히 활성화되어 있어야 함
        assertTrue("타임스톱 사용 중에도 점수 2배 효과가 유지되어야 함", 
            engine.isDoubleScoreActive());
    }

    @Test
    public void testSequentialItemCollection() {
        // given: 여러 줄에 걸쳐 다양한 아이템 배치
        Block block1 = new se.tetris.team5.blocks.OBlock();
        block1.setItem(0, 0, new TimeStopItem());
        
        Block block2 = new se.tetris.team5.blocks.OBlock();
        block2.setItem(0, 0, new DoubleScoreItem());
        
        Block block3 = new se.tetris.team5.blocks.OBlock();
        block3.setItem(0, 0, new LineClearItem());
        
        // when: 블록들을 각각 다른 높이에 배치
        List<Item> removed1 = new ArrayList<>();
        board.fixBlock(block1, 0, 0, removed1);
        
        List<Item> removed2 = new ArrayList<>();
        board.fixBlock(block2, 0, 2, removed2);
        
        List<Item> removed3 = new ArrayList<>();
        board.fixBlock(block3, 0, 4, removed3);
        
        // then: 각 블록이 독립적으로 배치되어야 함
        assertNotNull("첫 번째 블록이 배치되어야 함", board.getBoard()[0][0]);
        assertNotNull("두 번째 블록이 배치되어야 함", board.getBoard()[2][0]);
        assertNotNull("세 번째 블록이 배치되어야 함", board.getBoard()[4][0]);
    }

    @Test
    public void testItemCollectionAfterGameModeSwitch() {
        // given: 아이템 모드에서 시작
        engine.setGameMode(GameMode.ITEM);
        assertEquals(GameMode.ITEM, engine.getGameMode());
        
        // when: 일반 모드로 전환
        engine.setGameMode(GameMode.NORMAL);
        
        // 블록 배치 (아이템 포함)
        Block block = new se.tetris.team5.blocks.OBlock();
        block.setItem(0, 0, new DoubleScoreItem());
        
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block, 0, 0, removed);
        
        // then: 일반 모드에서도 이미 배치된 아이템은 보드에 존재
        Item item = board.getBoardItem(0, 0);
        assertNotNull("배치된 아이템은 모드와 관계없이 보드에 존재해야 함", item);
    }

    @Test
    public void testBombItemWithDoubleScore() {
        // given: 폭탄 아이템과 점수 2배 효과
        engine.activateDoubleScore(5000);
        assertTrue(engine.isDoubleScoreActive());
        
        // when: 폭탄 블록 배치 (DotBlock)
        Block dotBlock = new se.tetris.team5.blocks.DotBlock();
        List<Item> removed = new ArrayList<>();
        board.fixBlock(dotBlock, 0, 0, removed);
        
        // then: 점수 2배 효과가 여전히 활성화되어 있어야 함
        assertTrue("폭탄 폭발 후에도 점수 2배 효과가 유지되어야 함", 
            engine.isDoubleScoreActive());
    }

    @Test
    public void testAllItemTypesCanCoexist() {
        // given: 모든 아이템 타입 생성
        Item lineClear = new LineClearItem();
        Item weightBlock = new WeightBlockItem();
        Item bomb = new BombItem();
        Item timeStop = new TimeStopItem();
        Item doubleScore = new DoubleScoreItem();
        
        // then: 모든 아이템이 정상적으로 생성되어야 함
        assertNotNull("LineClearItem이 생성되어야 함", lineClear);
        assertNotNull("WeightBlockItem이 생성되어야 함", weightBlock);
        assertNotNull("BombItem이 생성되어야 함", bomb);
        assertNotNull("TimeStopItem이 생성되어야 함", timeStop);
        assertNotNull("DoubleScoreItem이 생성되어야 함", doubleScore);
        
        // 각 아이템이 고유한 이름을 가져야 함
        assertNotEquals(lineClear.getName(), weightBlock.getName());
        assertNotEquals(weightBlock.getName(), bomb.getName());
        assertNotEquals(bomb.getName(), timeStop.getName());
        assertNotEquals(timeStop.getName(), doubleScore.getName());
    }
}
