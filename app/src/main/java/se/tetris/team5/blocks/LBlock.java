package se.tetris.team5.blocks;

public class LBlock extends Block {
	
	public LBlock() {
		shape = new int[][] { 
			{1, 1, 1},
			{1, 0, 0}
		};
		blockType = "L";
		updateColor();
	}
}
