package se.tetris.team5.blocks;

public class SBlock extends Block {

	public SBlock() {
		shape = new int[][] { 
			{0, 1, 1},
			{1, 1, 0}
		};
		blockType = "S";
		updateColor();
	}
}
