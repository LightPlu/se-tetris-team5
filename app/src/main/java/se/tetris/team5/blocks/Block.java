package se.tetris.team5.blocks;

import java.awt.Color;
import se.tetris.team5.utils.setting.GameSettings;

public abstract class Block {
		
	protected int[][] shape;
	protected Color color;
	protected String blockType; // 블록 타입 식별자
		// 각 칸별 아이템 정보 (null이면 아이템 없음)
		protected se.tetris.team5.items.Item[][] items;
	
	public Block() {
		shape = new int[][]{ 
				{1, 1}, 
				{1, 1}
		};
		blockType = "O"; // 기본값
		updateColor();
		// 아이템 배열 초기화 (shape와 항상 크기 동기화)
		items = new se.tetris.team5.items.Item[shape.length][shape[0].length];
	}

	public String getBlockType() {
		return blockType;
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
		se.tetris.team5.items.Item[][] rotatedItems = new se.tetris.team5.items.Item[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				rotated[j][rows - 1 - i] = shape[i][j];
				// items 배열이 null이거나 크기가 다를 수 있으므로 안전하게 접근
				if (items != null && i < items.length && j < items[0].length) {
					rotatedItems[j][rows - 1 - i] = items[i][j];
				} else {
					rotatedItems[j][rows - 1 - i] = null;
				}
			}
		}
		shape = rotated;
		items = rotatedItems;
	}

	/**
	 * (x, y) 위치에 아이템 부여
	 */
	public void setItem(int x, int y, se.tetris.team5.items.Item item) {
		if (y >= 0 && y < items.length && x >= 0 && x < items[0].length) {
			items[y][x] = item;
		}
	}

	/**
	 * (x, y) 위치의 아이템 반환 (없으면 null)
	 */
	public se.tetris.team5.items.Item getItem(int x, int y) {
		if (y >= 0 && y < items.length && x >= 0 && x < items[0].length) {
			return items[y][x];
		}
		return null;
	}

	/**
	 * (x, y) 위치의 아이템 제거 (획득 시 호출)
	 */
	public se.tetris.team5.items.Item removeItem(int x, int y) {
		if (y >= 0 && y < items.length && x >= 0 && x < items[0].length) {
			se.tetris.team5.items.Item item = items[y][x];
			items[y][x] = null;
			return item;
		}
		return null;
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
