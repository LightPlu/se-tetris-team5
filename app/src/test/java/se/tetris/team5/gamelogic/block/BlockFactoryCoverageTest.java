package se.tetris.team5.gamelogic.block;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import se.tetris.team5.blocks.*;

/**
 * BlockFactory.java에 대한 라인 커버리지를 높이기 위한 테스트
 * BlockFactoryProbabilityTest에서 다루지 않는 메소드들을 테스트합니다.
 */
public class BlockFactoryCoverageTest {

    private BlockFactory factory;

    @Before
    public void setUp() {
        factory = new BlockFactory();
    }

    // ============ Constructor Tests ============

    @Test
    public void testDefaultConstructor() {
        BlockFactory defaultFactory = new BlockFactory();
        assertNotNull("Default constructor should create factory", defaultFactory);
        assertEquals("Default difficulty should be NORMAL", 
                     BlockFactory.Difficulty.NORMAL, defaultFactory.getDifficulty());
    }

    @Test
    public void testConstructorWithDifficultyEasy() {
        BlockFactory easyFactory = new BlockFactory(BlockFactory.Difficulty.EASY);
        assertNotNull("Constructor with EASY should create factory", easyFactory);
        assertEquals("Difficulty should be EASY", 
                     BlockFactory.Difficulty.EASY, easyFactory.getDifficulty());
    }

    @Test
    public void testConstructorWithDifficultyNormal() {
        BlockFactory normalFactory = new BlockFactory(BlockFactory.Difficulty.NORMAL);
        assertNotNull("Constructor with NORMAL should create factory", normalFactory);
        assertEquals("Difficulty should be NORMAL", 
                     BlockFactory.Difficulty.NORMAL, normalFactory.getDifficulty());
    }

    @Test
    public void testConstructorWithDifficultyHard() {
        BlockFactory hardFactory = new BlockFactory(BlockFactory.Difficulty.HARD);
        assertNotNull("Constructor with HARD should create factory", hardFactory);
        assertEquals("Difficulty should be HARD", 
                     BlockFactory.Difficulty.HARD, hardFactory.getDifficulty());
    }

    // ============ Difficulty Management Tests ============

    @Test
    public void testSetDifficultyToEasy() {
        factory.setDifficulty(BlockFactory.Difficulty.EASY);
        assertEquals("Difficulty should be EASY", 
                     BlockFactory.Difficulty.EASY, factory.getDifficulty());
    }

    @Test
    public void testSetDifficultyToNormal() {
        factory.setDifficulty(BlockFactory.Difficulty.NORMAL);
        assertEquals("Difficulty should be NORMAL", 
                     BlockFactory.Difficulty.NORMAL, factory.getDifficulty());
    }

    @Test
    public void testSetDifficultyToHard() {
        factory.setDifficulty(BlockFactory.Difficulty.HARD);
        assertEquals("Difficulty should be HARD", 
                     BlockFactory.Difficulty.HARD, factory.getDifficulty());
    }

    @Test
    public void testChangeDifficultyMultipleTimes() {
        factory.setDifficulty(BlockFactory.Difficulty.EASY);
        assertEquals(BlockFactory.Difficulty.EASY, factory.getDifficulty());
        
        factory.setDifficulty(BlockFactory.Difficulty.HARD);
        assertEquals(BlockFactory.Difficulty.HARD, factory.getDifficulty());
        
        factory.setDifficulty(BlockFactory.Difficulty.NORMAL);
        assertEquals(BlockFactory.Difficulty.NORMAL, factory.getDifficulty());
    }

    // ============ createBlock(int) Tests ============

    @Test
    public void testCreateBlockTypeI() {
        Block block = factory.createBlock(0);
        assertNotNull("Block should be created", block);
        assertTrue("Block should be IBlock", block instanceof IBlock);
    }

    @Test
    public void testCreateBlockTypeJ() {
        Block block = factory.createBlock(1);
        assertNotNull("Block should be created", block);
        assertTrue("Block should be JBlock", block instanceof JBlock);
    }

    @Test
    public void testCreateBlockTypeL() {
        Block block = factory.createBlock(2);
        assertNotNull("Block should be created", block);
        assertTrue("Block should be LBlock", block instanceof LBlock);
    }

    @Test
    public void testCreateBlockTypeZ() {
        Block block = factory.createBlock(3);
        assertNotNull("Block should be created", block);
        assertTrue("Block should be ZBlock", block instanceof ZBlock);
    }

    @Test
    public void testCreateBlockTypeS() {
        Block block = factory.createBlock(4);
        assertNotNull("Block should be created", block);
        assertTrue("Block should be SBlock", block instanceof SBlock);
    }

    @Test
    public void testCreateBlockTypeT() {
        Block block = factory.createBlock(5);
        assertNotNull("Block should be created", block);
        assertTrue("Block should be TBlock", block instanceof TBlock);
    }

    @Test
    public void testCreateBlockTypeO() {
        Block block = factory.createBlock(6);
        assertNotNull("Block should be created", block);
        assertTrue("Block should be OBlock", block instanceof OBlock);
    }

    @Test
    public void testCreateBlockInvalidTypeReturnsLBlock() {
        // Invalid index (e.g., -1, 7, 100) should return LBlock (default case)
        Block block1 = factory.createBlock(-1);
        assertNotNull("Block should be created for invalid index -1", block1);
        assertTrue("Invalid index -1 should return LBlock", block1 instanceof LBlock);

        Block block2 = factory.createBlock(7);
        assertNotNull("Block should be created for invalid index 7", block2);
        assertTrue("Invalid index 7 should return LBlock", block2 instanceof LBlock);

        Block block3 = factory.createBlock(999);
        assertNotNull("Block should be created for invalid index 999", block3);
        assertTrue("Invalid index 999 should return LBlock", block3 instanceof LBlock);
    }

    // ============ createWeightBlock Tests ============

    @Test
    public void testCreateWeightBlock() {
        Block block = factory.createWeightBlock();
        assertNotNull("WeightBlock should be created", block);
        assertTrue("Block should be WBlock", block instanceof WBlock);
    }

    @Test
    public void testCreateMultipleWeightBlocks() {
        Block block1 = factory.createWeightBlock();
        Block block2 = factory.createWeightBlock();
        Block block3 = factory.createWeightBlock();
        
        assertNotNull("First WBlock should be created", block1);
        assertNotNull("Second WBlock should be created", block2);
        assertNotNull("Third WBlock should be created", block3);
        
        assertTrue("First block should be WBlock", block1 instanceof WBlock);
        assertTrue("Second block should be WBlock", block2 instanceof WBlock);
        assertTrue("Third block should be WBlock", block3 instanceof WBlock);
        
        // WBlocks should be different instances
        assertNotSame("WBlocks should be different instances", block1, block2);
        assertNotSame("WBlocks should be different instances", block2, block3);
    }

    // ============ Random Seed Tests ============

    @Test
    public void testSetRandomSeedDeterministicBehavior() {
        BlockFactory factory1 = new BlockFactory();
        BlockFactory factory2 = new BlockFactory();
        
        // Set same seed for both factories
        factory1.setRandomSeed(12345L);
        factory2.setRandomSeed(12345L);
        
        // Generate blocks - should be same sequence
        for (int i = 0; i < 10; i++) {
            Block block1 = factory1.createRandomBlock();
            Block block2 = factory2.createRandomBlock();
            
            assertEquals("Blocks should be same type with same seed at iteration " + i,
                        block1.getClass(), block2.getClass());
        }
    }

    @Test
    public void testSetRandomSeedDifferentSeeds() {
        BlockFactory factory1 = new BlockFactory();
        BlockFactory factory2 = new BlockFactory();
        
        // Set different seeds
        factory1.setRandomSeed(11111L);
        factory2.setRandomSeed(99999L);
        
        // Generate blocks - likely to be different
        boolean foundDifference = false;
        for (int i = 0; i < 20; i++) {
            Block block1 = factory1.createRandomBlock();
            Block block2 = factory2.createRandomBlock();
            
            if (!block1.getClass().equals(block2.getClass())) {
                foundDifference = true;
                break;
            }
        }
        
        assertTrue("Different seeds should eventually produce different blocks", foundDifference);
    }

    @Test
    public void testRefreshRandomSeed() {
        BlockFactory factory1 = new BlockFactory();
        
        // Set deterministic seed
        factory1.setRandomSeed(54321L);
        
        // Generate some blocks
        Block block1 = factory1.createRandomBlock();
        Block block2 = factory1.createRandomBlock();
        
        // Refresh seed (should create new Random with different seed)
        factory1.refreshRandomSeed();
        
        // Reset to same seed
        factory1.setRandomSeed(54321L);
        
        // Should get same sequence again
        Block block3 = factory1.createRandomBlock();
        Block block4 = factory1.createRandomBlock();
        
        assertEquals("After refresh and reset, first block should match",
                    block1.getClass(), block3.getClass());
        assertEquals("After refresh and reset, second block should match",
                    block2.getClass(), block4.getClass());
    }

    @Test
    public void testRefreshRandomSeedChangesSequence() {
        factory.setRandomSeed(11111L);
        
        Block[] firstSequence = new Block[5];
        for (int i = 0; i < 5; i++) {
            firstSequence[i] = factory.createRandomBlock();
        }
        
        // Refresh and set same seed again
        factory.refreshRandomSeed();
        factory.setRandomSeed(11111L);
        
        Block[] secondSequence = new Block[5];
        for (int i = 0; i < 5; i++) {
            secondSequence[i] = factory.createRandomBlock();
        }
        
        // Sequences should be identical after setting same seed
        for (int i = 0; i < 5; i++) {
            assertEquals("Sequence should match at position " + i,
                        firstSequence[i].getClass(), secondSequence[i].getClass());
        }
    }

    // ============ Integration Tests ============

    @Test
    public void testCreateRandomBlockReturnsValidBlock() {
        // Test multiple random blocks to ensure they're all valid
        for (int i = 0; i < 100; i++) {
            Block block = factory.createRandomBlock();
            assertNotNull("Random block should not be null at iteration " + i, block);
            
            // Should be one of the 7 standard block types
            boolean isValidType = block instanceof IBlock ||
                                 block instanceof JBlock ||
                                 block instanceof LBlock ||
                                 block instanceof ZBlock ||
                                 block instanceof SBlock ||
                                 block instanceof TBlock ||
                                 block instanceof OBlock;
            
            assertTrue("Random block should be valid type at iteration " + i, isValidType);
        }
    }

    @Test
    public void testDifficultyDoesNotAffectCreateBlock() {
        // createBlock(int) should return same type regardless of difficulty
        factory.setDifficulty(BlockFactory.Difficulty.EASY);
        Block easyBlock = factory.createBlock(0);
        
        factory.setDifficulty(BlockFactory.Difficulty.HARD);
        Block hardBlock = factory.createBlock(0);
        
        assertEquals("createBlock(0) should return IBlock regardless of difficulty",
                    easyBlock.getClass(), hardBlock.getClass());
        assertTrue("Both should be IBlock", easyBlock instanceof IBlock);
        assertTrue("Both should be IBlock", hardBlock instanceof IBlock);
    }

    @Test
    public void testDifficultyDoesNotAffectCreateWeightBlock() {
        // createWeightBlock should return WBlock regardless of difficulty
        factory.setDifficulty(BlockFactory.Difficulty.EASY);
        Block easyWBlock = factory.createWeightBlock();
        
        factory.setDifficulty(BlockFactory.Difficulty.NORMAL);
        Block normalWBlock = factory.createWeightBlock();
        
        factory.setDifficulty(BlockFactory.Difficulty.HARD);
        Block hardWBlock = factory.createWeightBlock();
        
        assertTrue("EASY difficulty should create WBlock", easyWBlock instanceof WBlock);
        assertTrue("NORMAL difficulty should create WBlock", normalWBlock instanceof WBlock);
        assertTrue("HARD difficulty should create WBlock", hardWBlock instanceof WBlock);
    }

    @Test
    public void testAllBlockTypesCanBeCreated() {
        // Verify all 7 block types can be explicitly created
        Block[] blocks = new Block[7];
        for (int i = 0; i < 7; i++) {
            blocks[i] = factory.createBlock(i);
            assertNotNull("Block type " + i + " should be created", blocks[i]);
        }
        
        assertTrue("Type 0 should be IBlock", blocks[0] instanceof IBlock);
        assertTrue("Type 1 should be JBlock", blocks[1] instanceof JBlock);
        assertTrue("Type 2 should be LBlock", blocks[2] instanceof LBlock);
        assertTrue("Type 3 should be ZBlock", blocks[3] instanceof ZBlock);
        assertTrue("Type 4 should be SBlock", blocks[4] instanceof SBlock);
        assertTrue("Type 5 should be TBlock", blocks[5] instanceof TBlock);
        assertTrue("Type 6 should be OBlock", blocks[6] instanceof OBlock);
    }

    @Test
    public void testCreateBlocksAreIndependent() {
        // Each created block should be a new instance
        Block block1 = factory.createBlock(0);
        Block block2 = factory.createBlock(0);
        
        assertNotSame("Two created blocks should be different instances", block1, block2);
        assertEquals("But should be same type", block1.getClass(), block2.getClass());
    }

    @Test
    public void testRandomBlocksWithDifferentDifficulties() {
        // Just verify that createRandomBlock works with all difficulties
        BlockFactory.Difficulty[] difficulties = {
            BlockFactory.Difficulty.EASY,
            BlockFactory.Difficulty.NORMAL,
            BlockFactory.Difficulty.HARD
        };
        
        for (BlockFactory.Difficulty diff : difficulties) {
            factory.setDifficulty(diff);
            
            for (int i = 0; i < 10; i++) {
                Block block = factory.createRandomBlock();
                assertNotNull("Block should be created with " + diff + " difficulty", block);
            }
        }
    }
}
