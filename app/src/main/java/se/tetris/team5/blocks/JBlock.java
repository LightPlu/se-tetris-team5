package se.tetris.team5.blocks;

public class JBlock extends Block {
		
	public JBlock() {
		shape = new int[][] { 
				{1, 1, 1},
				{0, 0, 1}
		};
		blockType = "J";
		updateColor();
	}
}
