package se.tetris.team5.blocks;

import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.Color;

public class BlockTest {
    
    @Test
    public void iBlockTest() {
        IBlock iBlock = new IBlock();
        assertNotNull("IBlock이 생성되어야 합니다", iBlock);
        assertEquals("IBlock의 색상은 CYAN이어야 합니다", Color.CYAN, iBlock.getColor());
        assertEquals("IBlock의 너비는 4여야 합니다", 4, iBlock.width());
        assertEquals("IBlock의 높이는 1이어야 합니다", 1, iBlock.height());
        
        // IBlock의 초기 모양 검증
        for (int i = 0; i < 4; i++) {
            assertEquals("IBlock의 모든 셀은 1이어야 합니다", 1, iBlock.getShape(i, 0));
        }
    }
    
    @Test
    public void jBlockTest() {
        JBlock jBlock = new JBlock();
        assertNotNull("JBlock이 생성되어야 합니다", jBlock);
        assertEquals("JBlock의 색상은 BLUE여야 합니다", Color.BLUE, jBlock.getColor());
        assertEquals("JBlock의 너비는 3이어야 합니다", 3, jBlock.width());
        assertEquals("JBlock의 높이는 2여야 합니다", 2, jBlock.height());
        
        // JBlock 초기 모양 검증 [[1,1,1], [0,0,1]]
        assertEquals(1, jBlock.getShape(0, 0));
        assertEquals(1, jBlock.getShape(1, 0));
        assertEquals(1, jBlock.getShape(2, 0));
        assertEquals(0, jBlock.getShape(0, 1));
        assertEquals(0, jBlock.getShape(1, 1));
        assertEquals(1, jBlock.getShape(2, 1));
    }
    
    @Test
    public void lBlockTest() {
        LBlock lBlock = new LBlock();
        assertNotNull("LBlock이 생성되어야 합니다", lBlock);
        assertEquals("LBlock의 색상은 ORANGE여야 합니다", Color.ORANGE, lBlock.getColor());
        assertEquals("LBlock의 너비는 3이어야 합니다", 3, lBlock.width());
        assertEquals("LBlock의 높이는 2여야 합니다", 2, lBlock.height());
        
        // LBlock 초기 모양 검증 [[1,1,1], [1,0,0]]
        assertEquals(1, lBlock.getShape(0, 0));
        assertEquals(1, lBlock.getShape(1, 0));
        assertEquals(1, lBlock.getShape(2, 0));
        assertEquals(1, lBlock.getShape(0, 1));
        assertEquals(0, lBlock.getShape(1, 1));
        assertEquals(0, lBlock.getShape(2, 1));
    }
    
    @Test
    public void oBlockTest() {
        OBlock oBlock = new OBlock();
        assertNotNull("OBlock이 생성되어야 합니다", oBlock);
        assertEquals("OBlock의 색상은 YELLOW여야 합니다", Color.YELLOW, oBlock.getColor());
        assertEquals("OBlock의 너비는 2여야 합니다", 2, oBlock.width());
        assertEquals("OBlock의 높이는 2여야 합니다", 2, oBlock.height());
        
        // O블록의 모든 셀이 1이어야 함
        for(int i = 0; i < oBlock.width(); i++) {
            for(int j = 0; j < oBlock.height(); j++) {
                assertEquals("O블록의 모든 셀은 1이어야 합니다", 1, oBlock.getShape(i, j));
            }
        }
    }
    
    @Test
    public void sBlockTest() {
        SBlock sBlock = new SBlock();
        assertNotNull("SBlock이 생성되어야 합니다", sBlock);
        assertEquals("SBlock의 색상은 GREEN이어야 합니다", Color.GREEN, sBlock.getColor());
        assertEquals("SBlock의 너비는 3이어야 합니다", 3, sBlock.width());
        assertEquals("SBlock의 높이는 2여야 합니다", 2, sBlock.height());
    }
    
    @Test
    public void tBlockTest() {
        TBlock tBlock = new TBlock();
        assertNotNull("TBlock이 생성되어야 합니다", tBlock);
        assertEquals("TBlock의 색상은 MAGENTA여야 합니다", Color.MAGENTA, tBlock.getColor());
        assertEquals("TBlock의 너비는 3이어야 합니다", 3, tBlock.width());
        assertEquals("TBlock의 높이는 2여야 합니다", 2, tBlock.height());
    }
    
    @Test
    public void zBlockTest() {
        ZBlock zBlock = new ZBlock();
        assertNotNull("ZBlock이 생성되어야 합니다", zBlock);
        assertEquals("ZBlock의 색상은 RED여야 합니다", Color.RED, zBlock.getColor());
        assertEquals("ZBlock의 너비는 3이어야 합니다", 3, zBlock.width());
        assertEquals("ZBlock의 높이는 2여야 합니다", 2, zBlock.height());
    }
    
    @Test
    public void iBlockRotation() {
        IBlock iBlock = new IBlock();
        int originalWidth = iBlock.width();
        int originalHeight = iBlock.height();
        
        // 회전 테스트
        iBlock.rotate();
        assertEquals("회전 후 너비가 원래 높이와 같아야 합니다", originalHeight, iBlock.width());
        assertEquals("회전 후 높이가 원래 너비와 같아야 합니다", originalWidth, iBlock.height());
        
        // IBlock을 세로로 회전했을 때 모양 검증
        assertEquals("회전된 IBlock의 너비는 1이어야 합니다", 1, iBlock.width());
        assertEquals("회전된 IBlock의 높이는 4여야 합니다", 4, iBlock.height());
        for (int j = 0; j < 4; j++) {
            assertEquals("회전된 IBlock의 모든 셀은 1이어야 합니다", 1, iBlock.getShape(0, j));
        }
    }
    
    @Test
    public void jBlockRotation() {
        JBlock jBlock = new JBlock();
        
        // 4번 회전하면 원래 모양으로 돌아와야 함
        int originalWidth = jBlock.width();
        int originalHeight = jBlock.height();
        
        jBlock.rotate(); // 1번 회전
        jBlock.rotate(); // 2번 회전
        jBlock.rotate(); // 3번 회전
        jBlock.rotate(); // 4번 회전 (원래 상태)
        
        assertEquals("4번 회전 후 너비가 원래와 같아야 합니다", originalWidth, jBlock.width());
        assertEquals("4번 회전 후 높이가 원래와 같아야 합니다", originalHeight, jBlock.height());
    }
    
    @Test
    public void oBlockRotationStaysTheSame() {
        OBlock oBlock = new OBlock();
        int originalWidth = oBlock.width();
        int originalHeight = oBlock.height();
        
        // O블록은 회전해도 모양이 그대로여야 함
        oBlock.rotate();
        assertEquals("O블록 회전 후에도 너비가 같아야 합니다", originalWidth, oBlock.width());
        assertEquals("O블록 회전 후에도 높이가 같아야 합니다", originalHeight, oBlock.height());
        
        // 모든 셀이 여전히 1이어야 함
        for(int i = 0; i < oBlock.width(); i++) {
            for(int j = 0; j < oBlock.height(); j++) {
                assertEquals("회전 후에도 O블록의 모든 셀은 1이어야 합니다", 1, oBlock.getShape(i, j));
            }
        }
    }
    
    @Test
    public void blockShapeConsistency() {
        Block[] blocks = {
            new IBlock(), new OBlock(), new JBlock(), 
            new LBlock(), new SBlock(), new TBlock(), new ZBlock()
        };
        
        for(Block block : blocks) {
            String blockName = block.getClass().getSimpleName();
            
            // 크기가 양수여야 함
            assertTrue(blockName + "의 너비는 양수여야 합니다", block.width() > 0);
            assertTrue(blockName + "의 높이는 양수여야 합니다", block.height() > 0);
            
            // 모든 블록은 최소 하나의 셀이 1이어야 함
            boolean hasFilledCell = false;
            for(int i = 0; i < block.width(); i++) {
                for(int j = 0; j < block.height(); j++) {
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
    public void allBlocksHaveColor() {
        Block[] blocks = {
            new IBlock(), new OBlock(), new JBlock(), 
            new LBlock(), new SBlock(), new TBlock(), new ZBlock()
        };
        
        for(Block block : blocks) {
            assertNotNull(block.getClass().getSimpleName() + "의 색상이 null이 아니어야 합니다", 
                         block.getColor());
        }
    }
    
    @Test
    public void rotationPreservesBlockIntegrity() {
        Block[] blocks = {
            new IBlock(), new JBlock(), new LBlock(), 
            new SBlock(), new TBlock(), new ZBlock()
        };
        
        for(Block block : blocks) {
            String blockName = block.getClass().getSimpleName();
            
            // 회전 전 채워진 셀의 개수 세기
            int originalFilledCells = countFilledCells(block);
            
            // 회전 후에도 채워진 셀의 개수가 같아야 함
            block.rotate();
            int rotatedFilledCells = countFilledCells(block);
            
            assertEquals(blockName + " 회전 후에도 채워진 셀의 개수가 같아야 합니다", 
                        originalFilledCells, rotatedFilledCells);
        }
    }
    
    private int countFilledCells(Block block) {
        int count = 0;
        for(int i = 0; i < block.width(); i++) {
            for(int j = 0; j < block.height(); j++) {
                if(block.getShape(i, j) == 1) {
                    count++;
                }
            }
        }
        return count;
    }
}