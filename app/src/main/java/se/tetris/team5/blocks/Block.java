package se.tetris.team5.blocks;

import java.awt.Color;

public abstract class Block {
		
	protected int[][] shape;
	protected Color color;
	
	public Block() {
		shape = new int[][]{ 
				{1, 1}, 
				{1, 1}
		};
		color = Color.YELLOW;
	}
	
	public int getShape(int x, int y) {
		return shape[y][x];
	}
	
	public Color getColor() {
		return color;
	}
	
	public void rotate() {
		//Rotate the block 90 deg. clockwise.
		int rows = shape.length;
		int cols = shape[0].length;
		int[][] rotated = new int[cols][rows];
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				rotated[j][rows - 1 - i] = shape[i][j];
			}
		}
		
		shape = rotated;
	}
	
	public int height() {
		return shape.length;
	}
	
	public int width() {
		if(shape.length > 0)
			return shape[0].length;
		return 0;
	}
}
