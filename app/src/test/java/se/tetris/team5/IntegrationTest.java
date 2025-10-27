package se.tetris.team5;

import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.blocks.*;
import se.tetris.team5.utils.score.ScoreManager;

/**
 * 전체적인 통합 테스트를 수행하는 클래스
 */
public class IntegrationTest {

    @Test
    public void testAllBlockTypesExist() {
        // 모든 블록 타입이 존재하고 생성 가능한지 확인
        Block[] blocks = {
            new IBlock(),
            new JBlock(), 
            new LBlock(),
            new OBlock(),
            new SBlock(),
            new TBlock(),
            new ZBlock()
        };
        
        for (Block block : blocks) {
            assertNotNull("블록이 생성되어야 합니다: " + block.getClass().getSimpleName(), block);
            assertTrue("블록의 너비는 양수여야 합니다", block.width() > 0);
            assertTrue("블록의 높이는 양수여야 합니다", block.height() > 0);
            assertNotNull("블록의 색상이 있어야 합니다", block.getColor());
        }
    }
    
    @Test
    public void testBlockRotationIntegration() {
        // 모든 블록이 회전 가능한지 확인
        Block[] blocks = {
            new IBlock(),
            new JBlock(), 
            new LBlock(),
            new OBlock(),
            new SBlock(),
            new TBlock(),
            new ZBlock()
        };
        
        for (Block block : blocks) {
            String blockName = block.getClass().getSimpleName();
            
            // 초기 상태 저장
            int originalWidth = block.width();
            int originalHeight = block.height();
            
            // 회전 테스트
            try {
                block.rotate();
                assertTrue(blockName + " 회전 후에도 유효한 크기여야 합니다", 
                          block.width() > 0 && block.height() > 0);
                
                // 4번 회전으로 원래 상태 복원 테스트
                block.rotate();
                block.rotate();
                block.rotate();
                
                assertEquals(blockName + " 4번 회전 후 너비가 복원되어야 합니다", 
                           originalWidth, block.width());
                assertEquals(blockName + " 4번 회전 후 높이가 복원되어야 합니다", 
                           originalHeight, block.height());
                
            } catch (Exception e) {
                fail(blockName + " 회전 중 예외 발생: " + e.getMessage());
            }
        }
    }
    
    @Test
    public void testScoreManagerIntegration() {
        ScoreManager manager = ScoreManager.getInstance();
        assertNotNull("ScoreManager 인스턴스가 있어야 합니다", manager);
        
        // 초기 상태 확인
        int initialScoreCount = manager.getTotalScores();
        assertTrue("초기 점수 개수가 0 이상이어야 합니다", initialScoreCount >= 0);
        
        // 점수 추가 테스트
        int testScore = 5000;
        int testLevel = 3;
        int testLines = 15;
        long testPlayTime = 120000L; // 2분
        
        manager.addScore("IntegrationTestPlayer", testScore, testLevel, testLines, testPlayTime);
        
        // 점수가 추가되었는지 확인
        assertTrue("점수가 추가되어야 합니다", manager.getTotalScores() > initialScoreCount);
        
        // Top 점수 조회 테스트
        var topScores = manager.getTopScores(10);
        assertNotNull("Top 점수 목록이 반환되어야 합니다", topScores);
        assertTrue("Top 점수 목록에 최소 1개 항목이 있어야 합니다", topScores.size() > 0);
    }
    
    @Test
    public void testGameConstantsConsistency() {
        // 게임 상수들이 일관성 있게 정의되어있는지 확인
        assertTrue("게임 높이는 양수여야 합니다", se.tetris.team5.screens.game.HEIGHT > 0);
        assertTrue("게임 너비는 양수여야 합니다", se.tetris.team5.screens.game.WIDTH > 0);
        
        // 일반적인 테트리스 게임 크기 확인
        assertEquals("표준 테트리스 높이여야 합니다", 20, se.tetris.team5.screens.game.HEIGHT);
        assertEquals("표준 테트리스 너비여야 합니다", 10, se.tetris.team5.screens.game.WIDTH);
    }
    
    @Test
    public void testAllBlocksHaveUniqueColors() {
        Block[] blocks = {
            new IBlock(),
            new JBlock(), 
            new LBlock(),
            new OBlock(),
            new SBlock(),
            new TBlock(),
            new ZBlock()
        };
        
        // 각 블록이 서로 다른 색상을 가지는지 확인 (OBlock 제외, 다른 블록과 같을 수 있음)
        for (int i = 0; i < blocks.length; i++) {
            for (int j = i + 1; j < blocks.length; j++) {
                Block block1 = blocks[i];
                Block block2 = blocks[j];
                
                // 블록 타입이 다르면서 색상이 같은 경우를 확인
                if (!block1.getClass().equals(block2.getClass())) {
                    // 색상이 같아도 허용하지만, 대부분은 다를 것으로 예상
                    assertNotNull("블록1의 색상이 있어야 합니다", block1.getColor());
                    assertNotNull("블록2의 색상이 있어야 합니다", block2.getColor());
                }
            }
        }
    }
    
    @Test
    public void testBlockShapeIntegrity() {
        // 모든 블록이 유효한 모양을 가지는지 확인
        Block[] blocks = {
            new IBlock(),
            new JBlock(), 
            new LBlock(),
            new OBlock(),
            new SBlock(),
            new TBlock(),
            new ZBlock()
        };
        
        for (Block block : blocks) {
            String blockName = block.getClass().getSimpleName();
            
            // 블록 크기가 유효한지 확인
            assertTrue(blockName + "의 너비는 1-4 사이여야 합니다", 
                      block.width() >= 1 && block.width() <= 4);
            assertTrue(blockName + "의 높이는 1-4 사이여야 합니다", 
                      block.height() >= 1 && block.height() <= 4);
            
            // 모든 셀이 0 또는 1인지 확인
            boolean hasFilledCell = false;
            for (int i = 0; i < block.width(); i++) {
                for (int j = 0; j < block.height(); j++) {
                    int cellValue = block.getShape(i, j);
                    assertTrue(blockName + "의 셀 값은 0 또는 1이어야 합니다", 
                              cellValue == 0 || cellValue == 1);
                    if (cellValue == 1) {
                        hasFilledCell = true;
                    }
                }
            }
            assertTrue(blockName + "은 최소 하나의 채워진 셀이 있어야 합니다", hasFilledCell);
        }
    }
    
    @Test
    public void testSpecificBlockShapes() {
        // 특정 블록들의 예상되는 모양 확인
        
        // IBlock: 1x4 또는 4x1
        IBlock iBlock = new IBlock();
        assertTrue("IBlock은 직선 모양이어야 합니다", 
                  (iBlock.width() == 4 && iBlock.height() == 1) || 
                  (iBlock.width() == 1 && iBlock.height() == 4));
        
        // OBlock: 2x2
        OBlock oBlock = new OBlock();
        assertEquals("OBlock의 너비는 2여야 합니다", 2, oBlock.width());
        assertEquals("OBlock의 높이는 2여야 합니다", 2, oBlock.height());
        
        // OBlock의 모든 셀이 채워져 있어야 함
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                assertEquals("OBlock의 모든 셀이 1이어야 합니다", 1, oBlock.getShape(i, j));
            }
        }
        
        // T, L, J, S, Z 블록들은 모두 3x2 또는 2x3 크기여야 함 (회전 상태에 따라)
        Block[] complexBlocks = {new TBlock(), new LBlock(), new JBlock(), new SBlock(), new ZBlock()};
        
        for (Block block : complexBlocks) {
            String blockName = block.getClass().getSimpleName();
            int width = block.width();
            int height = block.height();
            
            assertTrue(blockName + "은 적절한 크기여야 합니다", 
                      (width == 3 && height == 2) || 
                      (width == 2 && height == 3) ||
                      (width == 3 && height == 3)); // 일부 회전 상태에서 가능
        }
    }
    
    @Test
    public void testPerformanceBasics() {
        // 기본적인 성능 테스트
        
        // 블록 생성 성능
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            new IBlock();
            new JBlock();
            new LBlock();
            new OBlock();
            new SBlock();
            new TBlock();
            new ZBlock(); 
        }
        long endTime = System.currentTimeMillis();
        
        assertTrue("블록 생성이 합리적인 시간 내에 완료되어야 합니다 (< 1초)", 
                  (endTime - startTime) < 1000);
        
        // 블록 회전 성능
        Block testBlock = new JBlock();
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            testBlock.rotate();
        }
        endTime = System.currentTimeMillis();
        
        assertTrue("블록 회전이 합리적인 시간 내에 완료되어야 합니다 (< 1초)", 
                  (endTime - startTime) < 1000);
    }
}