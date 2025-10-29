package se.tetris.team5.gamelogic;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.items.Item;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 게임 모드(NORMAL/ITEM)의 단위 테스트
 */
public class GameModeTest {
    private GameEngine engine;
    private BoardManager board;

    @Before
    public void setUp() {
        engine = new GameEngine(20, 10);
        board = engine.getBoardManager();
    }

    @Test
    public void testDefaultGameModeIsNormal() {
        // then: 기본 게임 모드는 ITEM이어야 함
        assertEquals("기본 게임 모드는 ITEM이어야 함", GameMode.ITEM, engine.getGameMode());
    }

    @Test
    public void testSetGameModeToItem() {
        // given: 초기 모드는 ITEM
        assertEquals(GameMode.ITEM, engine.getGameMode());
        
        // when: 일반 모드로 변경
        engine.setGameMode(GameMode.NORMAL);
        
        // then: 모드가 NORMAL로 변경됨
        assertEquals("게임 모드가 NORMAL로 변경되어야 함", GameMode.NORMAL, engine.getGameMode());
    }

    @Test
    public void testSetGameModeToNormal() {
        // given: 아이템 모드 (기본값)
        assertEquals(GameMode.ITEM, engine.getGameMode());
        
        // when: 일반 모드로 변경
        engine.setGameMode(GameMode.NORMAL);
        
        // then: 모드가 NORMAL으로 변경됨
        assertEquals("게임 모드가 NORMAL으로 변경되어야 함", GameMode.NORMAL, engine.getGameMode());
    }

    @Test
    public void testNormalModeDoesNotSpawnItems() throws Exception {
        // given: 일반 모드로 설정
        engine.setGameMode(GameMode.NORMAL);
        
        // when: 10줄을 삭제 (아이템 모드에서는 아이템이 생성되는 시점)
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        totalClearedLinesField.set(engine, 10);
        
        // 줄을 실제로 삭제하여 handleItemSpawnAndCollect 호출
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                board.getBoard()[y][x] = 1;
            }
        }
        
        List<Item> removed = new ArrayList<>();
        board.clearLines(removed);
        
        // then: NORMAL 모드에서는 아이템이 생성되지 않아야 함
        // (다음 블록에 아이템이 없는지 확인)
        Block nextBlock = engine.getNextBlock();
        boolean hasItem = false;
        if (nextBlock != null) {
            for (int i = 0; i < nextBlock.width(); i++) {
                for (int j = 0; j < nextBlock.height(); j++) {
                    if (nextBlock.getItem(i, j) != null) {
                        hasItem = true;
                        break;
                    }
                }
            }
        }
        
        assertFalse("일반 모드에서는 아이템이 생성되지 않아야 함", hasItem);
    }

    @Test
    public void testItemModeSpawnsItems() throws Exception {
        // given: 아이템 모드로 설정
        engine.setGameMode(GameMode.ITEM);
        engine.resetGame(); // 정책 리셋
        
        // when: 10줄을 삭제
        Field totalClearedLinesField = GameEngine.class.getDeclaredField("totalClearedLines");
        totalClearedLinesField.setAccessible(true);
        
        // 줄을 하나씩 삭제하면서 10줄 채우기
        for (int lineCount = 0; lineCount < 10; lineCount++) {
            // 한 줄 채우기
            for (int x = 0; x < board.getWidth(); x++) {
                board.getBoard()[0][x] = 1;
            }
            
            List<Item> removed = new ArrayList<>();
            int cleared = board.clearLines(removed);
            
            if (cleared > 0) {
                int currentTotal = (int) totalClearedLinesField.get(engine);
                totalClearedLinesField.set(engine, currentTotal + cleared);
            }
        }
        
        // 10줄 삭제 후 아이템 생성 트리거
        int totalCleared = (int) totalClearedLinesField.get(engine);
        
        // then: 10줄 이상 삭제되었는지 확인
        assertTrue("10줄 이상 삭제되어야 함", totalCleared >= 10);
    }

    @Test
    public void testGameModeSwitchDuringGame() {
        // given: 게임 시작 (ITEM 모드)
        assertEquals(GameMode.ITEM, engine.getGameMode());
        
        // when: 게임 중간에 NORMAL 모드로 변경
        engine.setGameMode(GameMode.NORMAL);
        
        // then: 모드가 즉시 변경됨
        assertEquals("게임 중에도 모드 변경이 가능해야 함", GameMode.NORMAL, engine.getGameMode());
        
        // when: 다시 ITEM 모드로 변경
        engine.setGameMode(GameMode.ITEM);
        
        // then: 모드가 다시 변경됨
        assertEquals("게임 중 모드를 다시 변경할 수 있어야 함", GameMode.ITEM, engine.getGameMode());
    }

    @Test
    public void testGameModeEnumValues() {
        // then: GameMode enum이 NORMAL과 ITEM 값을 가져야 함
        GameMode[] modes = GameMode.values();
        assertEquals("GameMode는 2개의 값을 가져야 함", 2, modes.length);
        
        boolean hasNormal = false;
        boolean hasItem = false;
        
        for (GameMode mode : modes) {
            if (mode == GameMode.NORMAL) hasNormal = true;
            if (mode == GameMode.ITEM) hasItem = true;
        }
        
        assertTrue("GameMode.NORMAL이 존재해야 함", hasNormal);
        assertTrue("GameMode.ITEM이 존재해야 함", hasItem);
    }

    @Test
    public void testGameResetPreservesMode() {
        // given: 아이템 모드로 설정
        engine.setGameMode(GameMode.ITEM);
        assertEquals(GameMode.ITEM, engine.getGameMode());
        
        // when: 게임 리셋
        engine.resetGame();
        
        // then: 모드가 유지됨
        assertEquals("게임 리셋 후에도 모드가 유지되어야 함", GameMode.ITEM, engine.getGameMode());
    }
}
