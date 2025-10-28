package se.tetris.team5.blocks;

/**
 * 무게추 블록 (W-Block)
 * 10줄 삭제 시 일반 블록 대신 생성될 수 있는 특수 블록
 */
public class WBlock extends Block {
    
    public WBlock() {
        shape = new int[][] { 
            {0, 1, 1, 0},
            {1, 1, 1, 1}
        };
        blockType = "W";
        updateColor();
        items = new se.tetris.team5.items.Item[shape.length][shape[0].length];
    }
}
