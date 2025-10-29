package se.tetris.team5;

public class App {

  public static void main(String[] args) {
    System.out.println("file.encoding = " + System.getProperty("file.encoding"));

    // ===== 프로그램 시작 =====
    System.out.println("Starting TETRIS Game...");
    
    // ScreenController가 로딩 화면부터 시작
    new ScreenController();
  }
}
