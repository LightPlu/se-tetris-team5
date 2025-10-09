package se.tetris.team5;


public class App {

    public static void main(String[] args) {
        System.out.println("file.encoding = " + System.getProperty("file.encoding"));

        // ===== 프로그램 시작 =====
        new ScreenController(); // ScreenController 시작
    }
}
 