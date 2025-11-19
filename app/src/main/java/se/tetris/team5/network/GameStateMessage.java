package se.tetris.team5.network;

import java.io.Serializable;
import java.awt.Color;
import se.tetris.team5.blocks.Block;

/**
 * P2P 게임 상태 동기화를 위한 메시지 클래스
 * 블록 이동, 회전, 고정, 보드 상태, 점수 등을 네트워크로 전송
 */
public class GameStateMessage implements Serializable {
  private static final long serialVersionUID = 1L;

  public enum MessageType {
    // 연결 관련
    CONNECT_REQUEST,      // 클라이언트 → 서버: 연결 요청
    CONNECT_ACCEPT,       // 서버 → 클라이언트: 연결 수락
    DISCONNECT,           // 연결 종료
    PING,                 // 핑 (지연 측정)
    PONG,                 // 핑 응답
    
    // 게임 시작/설정
    MODE_SELECT,          // 서버 → 클라이언트: 게임 모드 선택 (일반/아이템/시간제한)
    READY,                // 플레이어 준비 완료
    GAME_START,           // 게임 시작
    
    // 게임 플레이 중
    GAME_STATE,           // 전체 게임 상태 (보드 + 블록 + 점수)
    BLOCK_MOVE,           // 블록 이동 (좌우하)
    BLOCK_ROTATE,         // 블록 회전
    BLOCK_HARD_DROP,      // 하드 드롭
    BLOCK_FIXED,          // 블록 고정됨 (보드에 블록이 고정되었을 때)
    BOARD_UPDATE,         // 전체 보드 상태 업데이트
    NEXT_BLOCK,           // 다음 블록 정보
    SCORE_UPDATE,         // 점수/레벨/줄 업데이트
    ITEM_USED,            // 아이템 사용
    LINE_CLEAR,           // 줄 삭제 발생
    
    // 게임 종료
    GAME_OVER,            // 게임 오버 (패배)
    GAME_WIN,             // 게임 승리
    
    // 에러 처리
    ERROR                 // 에러 메시지
  }

  private MessageType type;
  private long timestamp;         // 메시지 생성 시간 (지연 측정용)
  
  // 블록 관련 데이터
  private String blockType;       // 블록 타입 (IBlock, OBlock 등)
  private int blockX;             // 블록 X 위치
  private int blockY;             // 블록 Y 위치
  private int blockRotation;      // 블록 회전 상태 (0-3)
  
  // 보드 상태
  private int[][] board;          // 보드 전체 상태 (간단한 이름)
  private int[][] boardState;     // 보드 전체 상태 (호환성)
  private int[][][] boardColors;  // 보드 색상 (RGB)
  private String[][] boardItems;  // 보드 아이템 정보
  
  // 블록 객체 (직렬화 가능한 형태로 전송)
  private transient se.tetris.team5.blocks.Block currentBlock;
  private transient se.tetris.team5.blocks.Block nextBlock;
  private int currentBlockX;
  private int currentBlockY;
  
  // 점수 정보
  private int score;
  private int level;
  private int lines;              // linesCleared의 짧은 이름
  private int linesCleared;
  
  // 아이템 정보
  private String itemType;        // 사용한 아이템 타입
  private int itemX;              // 아이템 위치 X
  private int itemY;              // 아이템 위치 Y
  
  // 게임 모드
  private String gameMode;        // "NORMAL", "ITEM", "TIME_LIMIT"
  
  // 에러 메시지
  private String errorMessage;
  
  // 추가 데이터
  private String extraData;       // 기타 정보 (JSON 등)

  public GameStateMessage(MessageType type) {
    this.type = type;
    this.timestamp = System.currentTimeMillis();
  }

  // === Getters and Setters ===
  
  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getBlockType() {
    return blockType;
  }

  public void setBlockType(String blockType) {
    this.blockType = blockType;
  }

  public int getBlockX() {
    return blockX;
  }

  public void setBlockX(int blockX) {
    this.blockX = blockX;
  }

  public int getBlockY() {
    return blockY;
  }

  public void setBlockY(int blockY) {
    this.blockY = blockY;
  }

  public int getBlockRotation() {
    return blockRotation;
  }

  public void setBlockRotation(int blockRotation) {
    this.blockRotation = blockRotation;
  }

  public int[][] getBoardState() {
    return boardState;
  }

  public void setBoardState(int[][] boardState) {
    this.boardState = boardState;
  }

  public int[][][] getBoardColors() {
    return boardColors;
  }

  public void setBoardColors(int[][][] boardColors) {
    this.boardColors = boardColors;
  }

  public String[][] getBoardItems() {
    return boardItems;
  }

  public void setBoardItems(String[][] boardItems) {
    this.boardItems = boardItems;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int getLinesCleared() {
    return linesCleared;
  }

  public void setLinesCleared(int linesCleared) {
    this.linesCleared = linesCleared;
    this.lines = linesCleared;
  }

  public int getLines() {
    return lines != 0 ? lines : linesCleared;
  }

  public void setLines(int lines) {
    this.lines = lines;
    this.linesCleared = lines;
  }

  // Block 객체 getter/setter
  public se.tetris.team5.blocks.Block getCurrentBlock() {
    return currentBlock;
  }

  public void setCurrentBlock(se.tetris.team5.blocks.Block block) {
    this.currentBlock = block;
  }

  public se.tetris.team5.blocks.Block getNextBlock() {
    return nextBlock;
  }

  public void setNextBlock(se.tetris.team5.blocks.Block block) {
    this.nextBlock = block;
  }

  public int getX() {
    return currentBlockX != 0 ? currentBlockX : blockX;
  }

  public void setX(int x) {
    this.currentBlockX = x;
    this.blockX = x;
  }

  public int getY() {
    return currentBlockY != 0 ? currentBlockY : blockY;
  }

  public void setY(int y) {
    this.currentBlockY = y;
    this.blockY = y;
  }

  // 보드 getter/setter (간단한 이름)
  public int[][] getBoard() {
    return board != null ? board : boardState;
  }

  public void setBoard(int[][] board) {
    this.board = board;
    this.boardState = board;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public int getItemX() {
    return itemX;
  }

  public void setItemX(int itemX) {
    this.itemX = itemX;
  }

  public int getItemY() {
    return itemY;
  }

  public void setItemY(int itemY) {
    this.itemY = itemY;
  }

  public String getGameMode() {
    return gameMode;
  }

  public void setGameMode(String gameMode) {
    this.gameMode = gameMode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getExtraData() {
    return extraData;
  }

  public void setExtraData(String extraData) {
    this.extraData = extraData;
  }

  @Override
  public String toString() {
    return String.format("GameStateMessage{type=%s, timestamp=%d, blockType=%s, x=%d, y=%d}",
      type, timestamp, blockType, blockX, blockY);
  }

  /**
   * 핑 응답 시간 계산 (밀리초)
   */
  public long getLatency() {
    return System.currentTimeMillis() - timestamp;
  }

  /**
   * Color를 int 배열로 변환
   */
  public static int[] colorToArray(Color color) {
    if (color == null) return new int[]{0, 0, 0};
    return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
  }

  /**
   * int 배열을 Color로 변환
   */
  public static Color arrayToColor(int[] rgb) {
    if (rgb == null || rgb.length < 3) return Color.BLACK;
    return new Color(rgb[0], rgb[1], rgb[2]);
  }

  /**
   * Color[][] 를 int[][][] 로 변환
   */
  public static int[][][] colorArray2DToIntArray(Color[][] colors) {
    if (colors == null) return null;
    int[][][] result = new int[colors.length][colors[0].length][3];
    for (int i = 0; i < colors.length; i++) {
      for (int j = 0; j < colors[i].length; j++) {
        result[i][j] = colorToArray(colors[i][j]);
      }
    }
    return result;
  }

  /**
   * int[][][] 를 Color[][] 로 변환
   */
  public static Color[][] intArrayToColorArray2D(int[][][] intColors) {
    if (intColors == null) return null;
    Color[][] result = new Color[intColors.length][intColors[0].length];
    for (int i = 0; i < intColors.length; i++) {
      for (int j = 0; j < intColors[i].length; j++) {
        result[i][j] = arrayToColor(intColors[i][j]);
      }
    }
    return result;
  }
}
