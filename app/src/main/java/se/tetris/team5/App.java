package se.tetris.team5;

import se.tetris.team5.component.Board;

public class App {

    public static void main(String[] args) {
        Board main = new Board();
		main.setSize(400, 500);
		main.setVisible(true);
    }
}
