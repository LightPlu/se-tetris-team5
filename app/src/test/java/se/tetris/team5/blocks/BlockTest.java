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
    }
    
    @Test
    public void oBlockTest() {
        OBlock oBlock = new OBlock();
        assertNotNull("OBlock이 생성되어야 합니다", oBlock);
        assertEquals("OBlock의 색상은 YELLOW여야 합니다", Color.YELLOW, oBlock.getColor());
        assertEquals("OBlock의 너비는 2여야 합니다", 2, oBlock.width());
        assertEquals("OBlock의 높이는 2여야 합니다", 2, oBlock.height());
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
    public void blockRotation() {
        IBlock iBlock = new IBlock();
        int originalWidth = iBlock.width();
        int originalHeight = iBlock.height();
        
        // 회전 테스트
        iBlock.rotate();
        assertEquals("회전 후 너비가 원래 높이와 같아야 합니다", originalHeight, iBlock.width());
        assertEquals("회전 후 높이가 원래 너비와 같아야 합니다", originalWidth, iBlock.height());
    }
    
    @Test
    public void blockShapeTest() {
        OBlock oBlock = new OBlock();
        // O블록의 모든 셀이 1이어야 함
        for(int i = 0; i < oBlock.width(); i++) {
            for(int j = 0; j < oBlock.height(); j++) {
                assertEquals("O블록의 모든 셀은 1이어야 합니다", 1, oBlock.getShape(i, j));
            }
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
}