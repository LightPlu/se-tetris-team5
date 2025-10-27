package se.tetris.team5.blocks;

import java.awt.Color;
import se.tetris.team5.utils.setting.GameSettings;

public abstract class Block {
		
	protected int[][] shape;
	protected Color color;
	protected String blockType; // 블록 타입 식별자
	protected int rotationState = 0; // 0: spawn, 1: right, 2: reverse, 3: left
	
	public Block() {
		shape = new int[][]{ 
				{1, 1}, 
				{1, 1}
		};
		blockType = "O"; // 기본값
		updateColor();
	}
	
	public int getShape(int x, int y) {
		return shape[y][x];
	}
	
	public Color getColor() {
		return color;
	}
	
	// 색상 업데이트 메소드 - 색맹 모드 설정에 따라 색상 변경
	public void updateColor() {
		GameSettings settings = GameSettings.getInstance();
		color = settings.getColorForBlock(blockType);
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
		rotationState = (rotationState + 1) % 4;
	}
    
	public int getRotationState() {
		return rotationState;
	}
    
	public void setRotationState(int state) {
		this.rotationState = state % 4;
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
