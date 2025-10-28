package se.tetris.team5.blocks;

public class DotBlock extends Block {
    
    public DotBlock() {
        shape = new int[][] { 
            {1} 
        };
        blockType = "Dot";
        updateColor();
        items = new se.tetris.team5.items.Item[shape.length][shape[0].length];
    }
    
}
