package se.tetris.team5.network;

import java.io.Serializable;
import java.awt.Color;

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
  private int[][] boardState;     // 보드 전체 상태
  private int[][][] boardColors;  // 보드 색상 (RGB)
  private String[][] boardItems;  // 보드 아이템 정보
  
  // 점수 정보
  private int score;
  private int level;
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
}
