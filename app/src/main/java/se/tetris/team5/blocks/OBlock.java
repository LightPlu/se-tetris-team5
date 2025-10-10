package se.tetris.team5.blocks;

public class OBlock extends Block {

	public OBlock() {
		shape = new int[][] { 
			{1, 1}, 
			{1, 1}
		};
		blockType = "O";
		updateColor();
	}
}
