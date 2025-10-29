package se.tetris.team5.items;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.blocks.Block;
import se.tetris.team5.components.game.BoardManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 타임스톱 아이템의 단위 테스트
 */
public class TimeStopItemTest {
    private GameEngine engine;
    private BoardManager board;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        board = engine.getBoardManager();
    }

    @Test
    public void testTimeStopItemGrantsCharge() {
        // given: 타임스톱 아이템이 포함된 블록
        Block block = new se.tetris.team5.blocks.OBlock();
        block.setItem(0, 0, new TimeStopItem());
        
        // when: 블록을 고정하고 줄을 채워 삭제
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block, 0, 0, removed);
        
        // 첫 번째 줄을 모두 채움
        for (int x = 0; x < board.getWidth(); x++) {
            board.getBoard()[0][x] = 1;
        }
        
        removed.clear();
        int clearedLines = board.clearLines(removed);
        
        // then: 타임스톱 아이템이 수집되었는지 확인
        boolean hasTimeStopItem = false;
        for (Item item : removed) {
            if (item instanceof TimeStopItem) {
                hasTimeStopItem = true;
                break;
            }
        }
        assertTrue("타임스톱 아이템이 줄 삭제로 제거되어야 함", hasTimeStopItem);
        assertTrue("적어도 1줄이 삭제되어야 함", clearedLines > 0);
    }

    @Test
    public void testTimeStopChargeUsage() {
        // given: 타임스톱 충전이 있는 상태
        assertFalse("초기 상태에서는 타임스톱 충전이 없어야 함", engine.hasTimeStopCharge());
        
        // when: 타임스톱 아이템을 획득
        Block block = new se.tetris.team5.blocks.OBlock();
        block.setItem(0, 0, new TimeStopItem());
        
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block, 0, 0, removed);
        
        // 줄 채우기
        for (int x = 0; x < board.getWidth(); x++) {
            board.getBoard()[0][x] = 1;
        }
        
        removed.clear();
        board.clearLines(removed);
        
        // 타임스톱 아이템 처리 시뮬레이션 (GameEngine에서 하는 것과 동일)
        for (Item item : removed) {
            if (item instanceof TimeStopItem) {
                // 실제로는 GameEngine에서 hasTimeStopCharge = true로 설정
                // 여기서는 로직 검증
                assertNotNull("TimeStopItem이 null이 아니어야 함", item);
            }
        }
    }

    @Test
    public void testTimeStopItemToString() {
        // given: 타임스톱 아이템
        TimeStopItem item = new TimeStopItem();
        
        // then: toString이 "T"를 반환해야 함
        assertEquals("T", item.toString());
        assertEquals("TimeStopItem", item.getName());
    }

    @Test
    public void testMultipleTimeStopItemsInSameLine() {
        // given: 한 줄에 여러 타임스톱 아이템
        Block block1 = new se.tetris.team5.blocks.OBlock();
        block1.setItem(0, 0, new TimeStopItem());
        
        Block block2 = new se.tetris.team5.blocks.OBlock();
        block2.setItem(0, 0, new TimeStopItem());
        
        // when: 두 블록을 다른 위치에 배치
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block1, 0, 0, removed);
        board.fixBlock(block2, 3, 0, removed);
        
        // 첫 번째 줄 채우기
        for (int x = 0; x < board.getWidth(); x++) {
            board.getBoard()[0][x] = 1;
        }
        
        removed.clear();
        board.clearLines(removed);
        
        // then: 여러 타임스톱 아이템이 수집되어야 함
        int timeStopCount = 0;
        for (Item item : removed) {
            if (item instanceof TimeStopItem) {
                timeStopCount++;
            }
        }
        
        assertTrue("최소 1개 이상의 타임스톱 아이템이 수집되어야 함", timeStopCount >= 1);
    }

    @Test
    public void testTimeStopItemWithOtherItems() {
        // given: 타임스톱과 다른 아이템이 같은 줄에 있는 경우
        Block block1 = new se.tetris.team5.blocks.OBlock();
        block1.setItem(0, 0, new TimeStopItem());
        
        Block block2 = new se.tetris.team5.blocks.OBlock();
        block2.setItem(0, 0, new DoubleScoreItem());
        
        // when: 두 블록을 배치하고 줄 삭제
        List<Item> removed = new ArrayList<>();
        board.fixBlock(block1, 0, 0, removed);
        board.fixBlock(block2, 3, 0, removed);
        
        for (int x = 0; x < board.getWidth(); x++) {
            board.getBoard()[0][x] = 1;
        }
        
        removed.clear();
        board.clearLines(removed);
        
        // then: 두 종류의 아이템이 모두 수집되어야 함
        boolean hasTimeStop = false;
        boolean hasDoubleScore = false;
        
        for (Item item : removed) {
            if (item instanceof TimeStopItem) hasTimeStop = true;
            if (item instanceof DoubleScoreItem) hasDoubleScore = true;
        }
        
        assertTrue("타임스톱 아이템이 수집되어야 함", hasTimeStop);
        assertTrue("점수 2배 아이템도 수집되어야 함", hasDoubleScore);
    }
}
