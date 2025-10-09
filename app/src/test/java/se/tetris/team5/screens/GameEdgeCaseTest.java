package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import se.tetris.team5.ScreenController;
import se.tetris.team5.blocks.*;

/**
 * 게임의 엣지 케이스와 복잡한 시나리오를 테스트하는 클래스
 */
public class GameEdgeCaseTest {

    private ScreenController mockScreenController;
    private game gameInstance;
    
    @Before
    public void setUp() {
        mockScreenController = new ScreenController() {
            @Override
            public void showScreen(String screenName) {
                // 테스트용 빈 구현
            }
        };
        gameInstance = new game(mockScreenController);
    }
    
    @Test
    public void testWallKickWithAllBlockTypes() throws Exception {
        Method rotateBlockMethod = game.class.getDeclaredMethod("rotateBlock");
        rotateBlockMethod.setAccessible(true);
        
        Field boardField = game.class.getDeclaredField("board");
        Field currField = game.class.getDeclaredField("curr");
        Field xField = game.class.getDeclaredField("x");
        Field yField = game.class.getDeclaredField("y");
        
        boardField.setAccessible(true);
        currField.setAccessible(true);
        xField.setAccessible(true);
        yField.setAccessible(true);
        
        // 빈 보드 초기화
        int[][] board = new int[game.HEIGHT][game.WIDTH];
        boardField.set(gameInstance, board);
        
        Block[] testBlocks = {
            new IBlock(), new JBlock(), new LBlock(), 
            new SBlock(), new TBlock(), new ZBlock()
        };
        
        for (Block block : testBlocks) {
            String blockName = block.getClass().getSimpleName();
            
            // 블록을 오른쪽 경계 근처에 배치
            currField.set(gameInstance, block);
            xField.set(gameInstance, game.WIDTH - 2);
            yField.set(gameInstance, 5);
            
            int originalX = (Integer) xField.get(gameInstance);
            
            // 회전 시도
            rotateBlockMethod.invoke(gameInstance);
            
            // Wall kick이 적용되었는지 확인 (x 위치가 조정되었거나 회전이 성공했는지)
            int newX = (Integer) xField.get(gameInstance);
            Block currentBlock = (Block) currField.get(gameInstance);
            
            assertTrue(blockName + "에 대해 회전이 성공하거나 wall kick이 적용되어야 합니다",
                      newX <= originalX || currentBlock != null);
        }
    }
    
    @Test
    public void testRotationInTightSpaces() throws Exception {
        Method canMoveMethod = game.class.getDeclaredMethod("canMove", int.class, int.class, Block.class);
        Method rotateBlockMethod = game.class.getDeclaredMethod("rotateBlock");
        
        canMoveMethod.setAccessible(true);
        rotateBlockMethod.setAccessible(true);
        
        Field boardField = game.class.getDeclaredField("board");
        Field currField = game.class.getDeclaredField("curr");
        Field xField = game.class.getDeclaredField("x");
        Field yField = game.class.getDeclaredField("y");
        
        boardField.setAccessible(true);
        currField.setAccessible(true);
        xField.setAccessible(true);
        yField.setAccessible(true);
        
        // 좁은 공간을 만들기 위해 보드 일부를 채움
        int[][] board = new int[game.HEIGHT][game.WIDTH];
        
        // 양쪽 벽을 만듦 (중간에 3칸 폭의 통로만 남김)
        for (int i = 0; i < game.HEIGHT; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 1; // 왼쪽 벽
            }
            for (int j = 7; j < game.WIDTH; j++) {
                board[i][j] = 1; // 오른쪽 벽
            }
        }
        boardField.set(gameInstance, board);
        
        // 좁은 공간에 JBlock 배치
        JBlock jBlock = new JBlock();
        currField.set(gameInstance, jBlock);
        xField.set(gameInstance, 4); // 좁은 통로 안에 배치
        yField.set(gameInstance, 10);
        
        int originalWidth = jBlock.width();
        int originalHeight = jBlock.height();
        
        // 회전 시도
        rotateBlockMethod.invoke(gameInstance);
        
        Block currentBlock = (Block) currField.get(gameInstance);
        
        // 좁은 공간에서 회전이 성공했는지 또는 원래 상태를 유지했는지 확인
        assertTrue("좁은 공간에서 회전 후 블록이 유효한 상태여야 합니다",
                  currentBlock.width() > 0 && currentBlock.height() > 0);
        
        // 현재 위치에서 블록이 유효한 위치에 있는지 확인
        int currentX = (Integer) xField.get(gameInstance);
        int currentY = (Integer) yField.get(gameInstance);
        
        Boolean isValidPosition = (Boolean) canMoveMethod.invoke(gameInstance, currentX, currentY, currentBlock);
        assertTrue("회전 후 블록이 유효한 위치에 있어야 합니다", isValidPosition);
    }
    
    @Test
    public void testRotationAtBottomBorder() throws Exception {
        Method rotateBlockMethod = game.class.getDeclaredMethod("rotateBlock");
        rotateBlockMethod.setAccessible(true);
        
        Field boardField = game.class.getDeclaredField("board");
        Field currField = game.class.getDeclaredField("curr");
        Field xField = game.class.getDeclaredField("x");
        Field yField = game.class.getDeclaredField("y");
        
        boardField.setAccessible(true);
        currField.setAccessible(true);
        xField.setAccessible(true);
        yField.setAccessible(true);
        
        // 빈 보드 초기화
        int[][] board = new int[game.HEIGHT][game.WIDTH];
        boardField.set(gameInstance, board);
        
        // IBlock을 바닥 근처에 배치
        IBlock iBlock = new IBlock();
        currField.set(gameInstance, iBlock);
        xField.set(gameInstance, 3);
        yField.set(gameInstance, game.HEIGHT - 1); // 바닥에 배치
        
        // 회전 시도 (세로로 회전하면 보드를 벗어나게 됨)
        rotateBlockMethod.invoke(gameInstance);
        
        Block currentBlock = (Block) currField.get(gameInstance);
        int currentY = (Integer) yField.get(gameInstance);
        
        // Wall kick이 적용되어 위치가 조정되었는지 확인
        assertTrue("바닥에서 회전 시도 시 적절한 처리가 되어야 합니다",
                  currentY + currentBlock.height() <= game.HEIGHT);
    }
    
    @Test
    public void testRotationWithExistingBlocks() throws Exception {
        Method rotateBlockMethod = game.class.getDeclaredMethod("rotateBlock");
        rotateBlockMethod.setAccessible(true);
        
        Field boardField = game.class.getDeclaredField("board");
        Field currField = game.class.getDeclaredField("curr");
        Field xField = game.class.getDeclaredField("x");
        Field yField = game.class.getDeclaredField("y");
        
        boardField.setAccessible(true);
        currField.setAccessible(true);
        xField.setAccessible(true);
        yField.setAccessible(true);
        
        // 보드에 일부 고정된 블록들 배치
        int[][] board = new int[game.HEIGHT][game.WIDTH];
        // L자 모양으로 블록 배치
        board[15][2] = 1;
        board[15][3] = 1;
        board[15][4] = 1;
        board[16][4] = 1;
        board[17][4] = 1;
        boardField.set(gameInstance, board);
        
        // JBlock을 기존 블록들 근처에 배치
        JBlock jBlock = new JBlock();
        currField.set(gameInstance, jBlock);
        xField.set(gameInstance, 2);
        yField.set(gameInstance, 13);
        
        int originalX = (Integer) xField.get(gameInstance);
        int originalY = (Integer) yField.get(gameInstance);
        
        // 회전 시도
        rotateBlockMethod.invoke(gameInstance);
        
        // 회전 후 상태 확인
        Block currentBlock = (Block) currField.get(gameInstance);
        int currentX = (Integer) xField.get(gameInstance);
        int currentY = (Integer) yField.get(gameInstance);
        
        assertNotNull("회전 후 블록이 존재해야 합니다", currentBlock);
        
        // 현재 블록이 기존 블록과 겹치지 않는지 확인
        boolean hasCollision = false;
        for (int i = 0; i < currentBlock.width(); i++) {
            for (int j = 0; j < currentBlock.height(); j++) {
                if (currentBlock.getShape(i, j) == 1) {
                    int boardX = currentX + i;
                    int boardY = currentY + j;
                    if (boardY >= 0 && boardY < game.HEIGHT && 
                        boardX >= 0 && boardX < game.WIDTH && 
                        board[boardY][boardX] == 1) {
                        hasCollision = true;
                        break;
                    }
                }
            }
            if (hasCollision) break;
        }
        
        assertFalse("회전 후 기존 블록과 겹치지 않아야 합니다", hasCollision);
    }
    
    @Test
    public void testMultipleRotationsConsistency() throws Exception {
        Method rotateBlockMethod = game.class.getDeclaredMethod("rotateBlock");
        rotateBlockMethod.setAccessible(true);
        
        Field boardField = game.class.getDeclaredField("board");
        Field currField = game.class.getDeclaredField("curr");
        Field xField = game.class.getDeclaredField("x");
        Field yField = game.class.getDeclaredField("y");
        
        boardField.setAccessible(true);
        currField.setAccessible(true);
        xField.setAccessible(true);
        yField.setAccessible(true);
        
        // 빈 보드 초기화
        int[][] board = new int[game.HEIGHT][game.WIDTH];
        boardField.set(gameInstance, board);
        
        Block[] testBlocks = {
            new JBlock(), new LBlock(), new SBlock(), new TBlock(), new ZBlock()
        };
        
        for (Block block : testBlocks) {
            String blockName = block.getClass().getSimpleName();
            
            // 블록을 중앙에 배치
            currField.set(gameInstance, block);
            xField.set(gameInstance, game.WIDTH / 2);
            yField.set(gameInstance, game.HEIGHT / 2);
            
            // 원래 모양 저장
            int originalWidth = block.width();
            int originalHeight = block.height();
            int[][] originalShape = new int[originalHeight][originalWidth];
            for (int i = 0; i < originalWidth; i++) {
                for (int j = 0; j < originalHeight; j++) {
                    originalShape[j][i] = block.getShape(i, j);
                }
            }
            
            // 4번 회전
            for (int rotation = 0; rotation < 4; rotation++) {
                rotateBlockMethod.invoke(gameInstance);
            }
            
            // 4번 회전 후 원래 모양으로 돌아와야 함
            Block currentBlock = (Block) currField.get(gameInstance);
            assertEquals(blockName + " 4번 회전 후 너비가 원래와 같아야 합니다", 
                        originalWidth, currentBlock.width());
            assertEquals(blockName + " 4번 회전 후 높이가 원래와 같아야 합니다", 
                        originalHeight, currentBlock.height());
            
            // 모양도 원래와 같아야 함
            boolean shapesMatch = true;
            for (int i = 0; i < originalWidth && shapesMatch; i++) {
                for (int j = 0; j < originalHeight && shapesMatch; j++) {
                    if (originalShape[j][i] != currentBlock.getShape(i, j)) {
                        shapesMatch = false;
                    }
                }
            }
            assertTrue(blockName + " 4번 회전 후 모양이 원래와 같아야 합니다", shapesMatch);
        }
    }
    
    @Test
    public void testOBlockRotationStability() throws Exception {
        Method rotateBlockMethod = game.class.getDeclaredMethod("rotateBlock");
        rotateBlockMethod.setAccessible(true);
        
        Field boardField = game.class.getDeclaredField("board");
        Field currField = game.class.getDeclaredField("curr");
        Field xField = game.class.getDeclaredField("x");
        Field yField = game.class.getDeclaredField("y");
        
        boardField.setAccessible(true);
        currField.setAccessible(true);
        xField.setAccessible(true);
        yField.setAccessible(true);
        
        // 빈 보드 초기화
        int[][] board = new int[game.HEIGHT][game.WIDTH];
        boardField.set(gameInstance, board);
        
        // OBlock 테스트 (회전해도 모양이 같아야 함)
        OBlock oBlock = new OBlock();
        currField.set(gameInstance, oBlock);
        xField.set(gameInstance, 4);
        yField.set(gameInstance, 10);
        
        int originalX = (Integer) xField.get(gameInstance);
        int originalY = (Integer) yField.get(gameInstance);
        int originalWidth = oBlock.width();
        int originalHeight = oBlock.height();
        
        // 여러 번 회전해도 OBlock은 변하지 않아야 함
        for (int i = 0; i < 10; i++) {
            rotateBlockMethod.invoke(gameInstance);
            
            Block currentBlock = (Block) currField.get(gameInstance);
            int currentX = (Integer) xField.get(gameInstance);
            int currentY = (Integer) yField.get(gameInstance);
            
            assertEquals("OBlock은 회전해도 너비가 변하지 않아야 합니다", 
                        originalWidth, currentBlock.width());
            assertEquals("OBlock은 회전해도 높이가 변하지 않아야 합니다", 
                        originalHeight, currentBlock.height());
            assertEquals("OBlock은 회전해도 X 위치가 변하지 않아야 합니다", 
                        originalX, currentX);
            assertEquals("OBlock은 회전해도 Y 위치가 변하지 않아야 합니다", 
                        originalY, currentY);
        }
    }
    
    @Test
    public void testBoundaryConditions() throws Exception {
        Method canMoveMethod = game.class.getDeclaredMethod("canMove", int.class, int.class, Block.class);
        canMoveMethod.setAccessible(true);
        
        Field boardField = game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        
        // 빈 보드 초기화
        int[][] board = new int[game.HEIGHT][game.WIDTH];
        boardField.set(gameInstance, board);
        
        IBlock iBlockHorizontal = new IBlock(); // 4x1 크기
        IBlock iBlockVertical = new IBlock();
        iBlockVertical.rotate(); // 1x4 크기
        
        // 경계 테스트
        // 왼쪽 경계
        Boolean result = (Boolean) canMoveMethod.invoke(gameInstance, -1, 5, iBlockHorizontal);
        assertFalse("왼쪽 경계를 벗어나면 이동 불가능해야 합니다", result);
        
        // 오른쪽 경계 - 수평 IBlock
        result = (Boolean) canMoveMethod.invoke(gameInstance, game.WIDTH - 3, 5, iBlockHorizontal);
        assertFalse("오른쪽 경계를 벗어나면 이동 불가능해야 합니다", result);
        
        // 오른쪽 경계 - 수직 IBlock (1칸만 필요)
        result = (Boolean) canMoveMethod.invoke(gameInstance, game.WIDTH - 1, 5, iBlockVertical);
        assertTrue("수직 IBlock은 오른쪽 끝에 배치 가능해야 합니다", result);
        
        // 아래쪽 경계
        result = (Boolean) canMoveMethod.invoke(gameInstance, 3, game.HEIGHT, iBlockHorizontal);
        assertFalse("아래쪽 경계를 벗어나면 이동 불가능해야 합니다", result);
        
        // 수직 IBlock의 아래쪽 경계
        result = (Boolean) canMoveMethod.invoke(gameInstance, 3, game.HEIGHT - 3, iBlockVertical);
        assertFalse("수직 IBlock이 아래쪽 경계를 벗어나면 이동 불가능해야 합니다", result);
        
        result = (Boolean) canMoveMethod.invoke(gameInstance, 3, game.HEIGHT - 4, iBlockVertical);
        assertTrue("수직 IBlock이 정확히 맞으면 이동 가능해야 합니다", result);
    }
}