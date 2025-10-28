package se.tetris.team5.blocks;

public class TBlock extends Block {
	
	public TBlock() {
		shape = new int[][] { 
			{0, 1, 0},
			{1, 1, 1}
		};
		blockType = "T";
		updateColor();
		items = new se.tetris.team5.items.Item[shape.length][shape[0].length];
	}
}
