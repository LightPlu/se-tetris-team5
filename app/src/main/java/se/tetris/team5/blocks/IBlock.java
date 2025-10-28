package se.tetris.team5.blocks;

public class IBlock extends Block {
	
	public IBlock() {
		shape = new int[][] { 
			{1, 1, 1, 1}
		};
		blockType = "I";
		updateColor();
		items = new se.tetris.team5.items.Item[shape.length][shape[0].length];
	}
}
