package se.tetris.team5.blocks;

public class ZBlock extends Block {
	
	public ZBlock() {
		shape = new int[][] { 
			{1, 1, 0},
			{0, 1, 1}
		};
		blockType = "Z";
		updateColor();
		items = new se.tetris.team5.items.Item[shape.length][shape[0].length];
	}
}
