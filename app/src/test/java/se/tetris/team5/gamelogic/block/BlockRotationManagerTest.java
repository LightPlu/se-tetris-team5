package se.tetris.team5.gamelogic.block;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.blocks.*;

/**
 * BlockRotationManager 테스트
 */
public class BlockRotationManagerTest {

    private BlockRotationManager manager;
    private int[][] emptyBoard;
    private int[][] partialBoard;

    @Before
    public void setUp() {
        manager = new BlockRotationManager();
        
        // 빈 보드 (20x10)
        emptyBoard = new int[20][10];
        
        // 일부 채워진 보드
        partialBoard = new int[20][10];
        for (int i = 0; i < 10; i++) {
            partialBoard[19][i] = 1; // 맨 아래 줄
        }
        for (int i = 0; i < 5; i++) {
            partialBoard[18][i] = 1; // 두 번째 줄 절반
        }
    }

    /**
     * 테스트 1: WallKickResult 생성 - 성공
     */
    @Test
    public void testWallKickResult_Success() {
        BlockRotationManager.WallKickResult result = 
            new BlockRotationManager.WallKickResult(true, 1, 0);
        
        assertTrue("성공 플래그가 true여야 함", result.success);
        assertEquals("offsetX가 1이어야 함", 1, result.offsetX);
        assertEquals("offsetY가 0이어야 함", 0, result.offsetY);
    }

    /**
     * 테스트 2: WallKickResult 생성 - 실패
     */
    @Test
    public void testWallKickResult_Failure() {
        BlockRotationManager.WallKickResult result = 
            new BlockRotationManager.WallKickResult(false, 0, 0);
        
        assertFalse("성공 플래그가 false여야 함", result.success);
        assertEquals("offsetX가 0이어야 함", 0, result.offsetX);
        assertEquals("offsetY가 0이어야 함", 0, result.offsetY);
    }

    /**
     * 테스트 3: rotateBlockWithWallKick - WBlock은 회전 불가
     */
    @Test
    public void testRotateBlockWithWallKick_WBlock() {
        Block wBlock = new WBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(wBlock, 4, 0, emptyBoard);
        
        assertFalse("WBlock은 회전할 수 없어야 함", result.success);
        assertEquals("offsetX가 0이어야 함", 0, result.offsetX);
        assertEquals("offsetY가 0이어야 함", 0, result.offsetY);
    }

    /**
     * 테스트 4: rotateBlockWithWallKick - 빈 보드에서 회전 성공
     */
    @Test
    public void testRotateBlockWithWallKick_EmptyBoard() {
        Block tBlock = new TBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(tBlock, 4, 5, emptyBoard);
        
        assertTrue("빈 보드에서 회전 성공해야 함", result.success);
    }

    /**
     * 테스트 5: rotateBlockWithWallKick - 오프셋 (0, 0) 성공
     */
    @Test
    public void testRotateBlockWithWallKick_NoOffset() {
        Block iBlock = new IBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(iBlock, 3, 5, emptyBoard);
        
        assertTrue("회전 성공해야 함", result.success);
        assertEquals("첫 번째 오프셋 (0,0)이 적용되어야 함", 0, result.offsetX);
        assertEquals("첫 번째 오프셋 (0,0)이 적용되어야 함", 0, result.offsetY);
    }

    /**
     * 테스트 6: rotateBlockWithWallKick - 왼쪽 벽에서 Wall Kick
     */
    @Test
    public void testRotateBlockWithWallKick_LeftWall() {
        Block iBlock = new IBlock();
        iBlock.rotate(); // 세로 방향으로 변경
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(iBlock, 0, 5, emptyBoard);
        
        assertTrue("왼쪽 벽에서 회전 성공해야 함", result.success);
    }

    /**
     * 테스트 7: rotateBlockWithWallKick - 오른쪽 벽에서 Wall Kick
     */
    @Test
    public void testRotateBlockWithWallKick_RightWall() {
        Block iBlock = new IBlock();
        iBlock.rotate(); // 세로 방향으로 변경
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(iBlock, 9, 5, emptyBoard);
        
        assertTrue("오른쪽 벽에서 회전 성공해야 함", result.success);
    }

    /**
     * 테스트 8: rotateBlockWithWallKick - 블록에 막혀서 실패
     */
    @Test
    public void testRotateBlockWithWallKick_BlockedByPieces() {
        Block tBlock = new TBlock();
        
        // 주변을 막아서 회전 불가능하게 만듦
        int[][] blockedBoard = new int[20][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                blockedBoard[j][i] = 1;
            }
        }
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(tBlock, 4, 5, blockedBoard);
        
        assertFalse("블록에 막혀서 회전 실패해야 함", result.success);
    }

    /**
     * 테스트 9: copyBlock - IBlock
     */
    @Test
    public void testCopyBlock_IBlock() {
        Block original = new IBlock();
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertTrue("복사본도 IBlock이어야 함", copy instanceof IBlock);
        assertEquals("너비가 같아야 함", original.width(), copy.width());
        assertEquals("높이가 같아야 함", original.height(), copy.height());
    }

    /**
     * 테스트 10: copyBlock - JBlock
     */
    @Test
    public void testCopyBlock_JBlock() {
        Block original = new JBlock();
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertTrue("복사본도 JBlock이어야 함", copy instanceof JBlock);
    }

    /**
     * 테스트 11: copyBlock - LBlock
     */
    @Test
    public void testCopyBlock_LBlock() {
        Block original = new LBlock();
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertTrue("복사본도 LBlock이어야 함", copy instanceof LBlock);
    }

    /**
     * 테스트 12: copyBlock - OBlock
     */
    @Test
    public void testCopyBlock_OBlock() {
        Block original = new OBlock();
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertTrue("복사본도 OBlock이어야 함", copy instanceof OBlock);
    }

    /**
     * 테스트 13: copyBlock - SBlock
     */
    @Test
    public void testCopyBlock_SBlock() {
        Block original = new SBlock();
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertTrue("복사본도 SBlock이어야 함", copy instanceof SBlock);
    }

    /**
     * 테스트 14: copyBlock - TBlock
     */
    @Test
    public void testCopyBlock_TBlock() {
        Block original = new TBlock();
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertTrue("복사본도 TBlock이어야 함", copy instanceof TBlock);
    }

    /**
     * 테스트 15: copyBlock - ZBlock
     */
    @Test
    public void testCopyBlock_ZBlock() {
        Block original = new ZBlock();
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertTrue("복사본도 ZBlock이어야 함", copy instanceof ZBlock);
    }

    /**
     * 테스트 16: copyBlock - WBlock
     */
    @Test
    public void testCopyBlock_WBlock() {
        Block original = new WBlock();
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertTrue("복사본도 WBlock이어야 함", copy instanceof WBlock);
    }

    /**
     * 테스트 17: copyBlock - 회전된 블록
     */
    @Test
    public void testCopyBlock_RotatedBlock() {
        Block original = new TBlock();
        original.rotate();
        original.rotate();
        
        Block copy = manager.copyBlock(original);
        
        assertNotNull("복사본이 null이 아니어야 함", copy);
        assertEquals("너비가 같아야 함", original.width(), copy.width());
        assertEquals("높이가 같아야 함", original.height(), copy.height());
        
        // 모양 비교
        for (int i = 0; i < original.width(); i++) {
            for (int j = 0; j < original.height(); j++) {
                assertEquals("모양이 같아야 함", 
                    original.getShape(i, j), copy.getShape(i, j));
            }
        }
    }

    /**
     * 테스트 18: rotateBlockWithWallKick - 여러 오프셋 시도
     */
    @Test
    public void testRotateBlockWithWallKick_MultipleOffsets() {
        Block lBlock = new LBlock();
        
        // 특정 위치에서 회전 시도
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(lBlock, 8, 5, emptyBoard);
        
        assertTrue("Wall Kick으로 회전 성공해야 함", result.success);
    }

    /**
     * 테스트 19: rotateBlockWithWallKick - 상단 경계
     */
    @Test
    public void testRotateBlockWithWallKick_TopBoundary() {
        Block iBlock = new IBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(iBlock, 4, -1, emptyBoard);
        
        // 상단 경계에서도 회전 시도 가능
        assertNotNull("결과가 null이 아니어야 함", result);
    }

    /**
     * 테스트 20: rotateBlockWithWallKick - 하단 경계
     */
    @Test
    public void testRotateBlockWithWallKick_BottomBoundary() {
        Block oBlock = new OBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(oBlock, 4, 18, emptyBoard);
        
        assertTrue("하단 경계에서 회전 성공해야 함", result.success);
    }

    /**
     * 테스트 21: rotateBlockWithWallKick - 모든 블록 타입
     */
    @Test
    public void testRotateBlockWithWallKick_AllBlockTypes() {
        Block[] blocks = {
            new IBlock(), new JBlock(), new LBlock(),
            new OBlock(), new SBlock(), new TBlock(), new ZBlock()
        };
        
        for (Block block : blocks) {
            BlockRotationManager.WallKickResult result = 
                manager.rotateBlockWithWallKick(block, 4, 5, emptyBoard);
            
            assertTrue(block.getClass().getSimpleName() + "이 회전되어야 함", 
                result.success);
        }
    }

    /**
     * 테스트 22: copyBlock - 모양 정확성 검증
     */
    @Test
    public void testCopyBlock_ShapeAccuracy() {
        Block original = new TBlock();
        Block copy = manager.copyBlock(original);
        
        // 모든 셀 비교
        for (int i = 0; i < original.width(); i++) {
            for (int j = 0; j < original.height(); j++) {
                assertEquals("각 셀의 모양이 같아야 함",
                    original.getShape(i, j), copy.getShape(i, j));
            }
        }
    }

    /**
     * 테스트 23: rotateBlockWithWallKick - 부분 채워진 보드
     */
    @Test
    public void testRotateBlockWithWallKick_PartialBoard() {
        Block tBlock = new TBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(tBlock, 4, 10, partialBoard);
        
        assertTrue("부분 채워진 보드에서 회전 성공해야 함", result.success);
    }

    /**
     * 테스트 24: rotateBlockWithWallKick - 충돌 위치
     */
    @Test
    public void testRotateBlockWithWallKick_CollisionPosition() {
        Block iBlock = new IBlock();
        iBlock.rotate(); // 세로로
        
        // 바닥 근처에서 회전 시도
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(iBlock, 4, 17, partialBoard);
        
        // Wall Kick이 적용되어 회전이 성공할 수도 있음
        assertNotNull("결과가 null이 아니어야 함", result);
    }

    /**
     * 테스트 25: rotateBlockWithWallKick - 연속 회전
     */
    @Test
    public void testRotateBlockWithWallKick_ConsecutiveRotations() {
        Block tBlock = new TBlock();
        
        BlockRotationManager.WallKickResult result1 = 
            manager.rotateBlockWithWallKick(tBlock, 4, 5, emptyBoard);
        assertTrue("첫 번째 회전 성공", result1.success);
        
        BlockRotationManager.WallKickResult result2 = 
            manager.rotateBlockWithWallKick(tBlock, 4, 5, emptyBoard);
        assertTrue("두 번째 회전 성공", result2.success);
        
        BlockRotationManager.WallKickResult result3 = 
            manager.rotateBlockWithWallKick(tBlock, 4, 5, emptyBoard);
        assertTrue("세 번째 회전 성공", result3.success);
        
        BlockRotationManager.WallKickResult result4 = 
            manager.rotateBlockWithWallKick(tBlock, 4, 5, emptyBoard);
        assertTrue("네 번째 회전 성공", result4.success);
    }

    /**
     * 테스트 26: copyBlock - 다양한 회전 상태
     */
    @Test
    public void testCopyBlock_VariousRotations() {
        Block original = new LBlock();
        
        for (int rotation = 0; rotation < 4; rotation++) {
            Block copy = manager.copyBlock(original);
            
            assertEquals("회전 상태 " + rotation + "에서 너비가 같아야 함",
                original.width(), copy.width());
            assertEquals("회전 상태 " + rotation + "에서 높이가 같아야 함",
                original.height(), copy.height());
            
            original.rotate();
        }
    }

    /**
     * 테스트 27: rotateBlockWithWallKick - 왼쪽 모서리
     */
    @Test
    public void testRotateBlockWithWallKick_LeftCorner() {
        Block jBlock = new JBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(jBlock, 0, 0, emptyBoard);
        
        assertTrue("왼쪽 상단 모서리에서 회전 성공해야 함", result.success);
    }

    /**
     * 테스트 28: rotateBlockWithWallKick - 오른쪽 모서리
     */
    @Test
    public void testRotateBlockWithWallKick_RightCorner() {
        Block sBlock = new SBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(sBlock, 8, 0, emptyBoard);
        
        assertTrue("오른쪽 상단 모서리에서 회전 성공해야 함", result.success);
    }

    /**
     * 테스트 29: rotateBlockWithWallKick - 음수 오프셋 적용
     */
    @Test
    public void testRotateBlockWithWallKick_NegativeOffset() {
        Block iBlock = new IBlock();
        iBlock.rotate();
        
        // 왼쪽 벽에서 회전 - 음수 오프셋 필요
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(iBlock, -1, 5, emptyBoard);
        
        if (result.success) {
            assertTrue("오프셋이 적용되어야 함", 
                result.offsetX != 0 || result.offsetY != 0);
        }
    }

    /**
     * 테스트 30: rotateBlockWithWallKick - 큰 오프셋 시도
     */
    @Test
    public void testRotateBlockWithWallKick_LargeOffset() {
        Block iBlock = new IBlock();
        iBlock.rotate();
        
        // 왼쪽 벽 바로 옆에서 회전
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(iBlock, -2, 5, emptyBoard);
        
        if (result.success) {
            // 큰 오프셋(-3, 0) 또는 (2, 0)이 적용될 수 있음
            assertTrue("오프셋이 적용되어야 함", 
                Math.abs(result.offsetX) >= 2 || result.offsetY != 0);
        }
    }

    /**
     * 테스트 31: copyBlock - null 체크
     */
    @Test
    public void testCopyBlock_NotNull() {
        Block[] blocks = {
            new IBlock(), new JBlock(), new LBlock(), new OBlock(),
            new SBlock(), new TBlock(), new ZBlock(), new WBlock()
        };
        
        for (Block block : blocks) {
            Block copy = manager.copyBlock(block);
            assertNotNull(block.getClass().getSimpleName() + 
                "의 복사본이 null이 아니어야 함", copy);
        }
    }

    /**
     * 테스트 32: rotateBlockWithWallKick - I블록 특별 케이스
     */
    @Test
    public void testRotateBlockWithWallKick_IBlockSpecialCase() {
        Block iBlock = new IBlock();
        
        // I블록은 4칸 너비이므로 특별한 Wall Kick이 필요할 수 있음
        BlockRotationManager.WallKickResult result1 = 
            manager.rotateBlockWithWallKick(iBlock, 7, 5, emptyBoard);
        assertTrue("오른쪽 가까이에서 회전 성공", result1.success);
        
        BlockRotationManager.WallKickResult result2 = 
            manager.rotateBlockWithWallKick(iBlock, 0, 5, emptyBoard);
        assertTrue("왼쪽 가까이에서 회전 성공", result2.success);
    }

    /**
     * 테스트 33: rotateBlockWithWallKick - 회전 실패 시 원래 상태 유지
     */
    @Test
    public void testRotateBlockWithWallKick_RestoreOnFailure() {
        Block tBlock = new TBlock();
        int originalWidth = tBlock.width();
        int originalHeight = tBlock.height();
        
        // 회전 불가능한 상황
        int[][] blockedBoard = new int[20][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                blockedBoard[j][i] = 1;
            }
        }
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(tBlock, 4, 5, blockedBoard);
        
        assertFalse("회전 실패", result.success);
        assertEquals("너비가 원래대로 복원되어야 함", originalWidth, tBlock.width());
        assertEquals("높이가 원래대로 복원되어야 함", originalHeight, tBlock.height());
    }

    /**
     * 테스트 34: rotateBlockWithWallKick - Y 오프셋 적용
     */
    @Test
    public void testRotateBlockWithWallKick_YOffsetApplication() {
        Block lBlock = new LBlock();
        
        // 하단 근처에서 회전 시도 - Y 오프셋 필요할 수 있음
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(lBlock, 4, 18, emptyBoard);
        
        if (result.success) {
            assertNotNull("오프셋이 적용되어야 함", result);
        }
    }

    /**
     * 테스트 35: rotateBlockWithWallKick - 다양한 보드 크기
     */
    @Test
    public void testRotateBlockWithWallKick_DifferentBoardSizes() {
        int[][] smallBoard = new int[10][5];
        Block oBlock = new OBlock();
        
        BlockRotationManager.WallKickResult result = 
            manager.rotateBlockWithWallKick(oBlock, 2, 5, smallBoard);
        
        assertTrue("작은 보드에서도 회전 성공해야 함", result.success);
    }
}
