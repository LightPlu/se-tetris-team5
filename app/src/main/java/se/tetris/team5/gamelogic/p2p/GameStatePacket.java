package se.tetris.team5.gamelogic.p2p;

import java.io.Serializable;
import java.awt.Color;
import java.util.List;

/**
 * P2P 대전 모드에서 주고받을 게임 상태 패킷
 * 직렬화하여 네트워크로 전송
 */
public class GameStatePacket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // 패킷 타입
    public enum PacketType {
        CONNECTION_REQUEST,    // 연결 요청
        CONNECTION_ACCEPTED,   // 연결 수락
        GAME_MODE_SELECT,      // 게임 모드 선택 (서버만)
        READY,                 // 준비 완료
        GAME_START,            // 게임 시작
        GAME_STATE,            // 게임 상태 업데이트
        ATTACK_BLOCKS,         // 공격 블럭 전송
        GAME_OVER,             // 게임 오버
        DISCONNECT,            // 연결 종료
        PING,                  // 연결 확인 (핑)
        PONG,                  // 핑 응답
        CHAT_MESSAGE           // 대기방 채팅 메시지
    }
    
    private PacketType type;
    private long timestamp;
    
    // 게임 상태 데이터
    private int[][] board;
    private Color[][] boardColors;
    private int currentBlockX;
    private int currentBlockY;
    private String currentBlockType; // I, O, T, S, Z, L, J 등
    private String nextBlockType; // 다음 블록 타입
    private int score;
    private int level;
    private int linesCleared;
    private long elapsedTime; // 게임 경과 시간 (밀리초)
    private boolean hasTimeStopCharge; // 타임스톱 보유 여부
    
    // 공격 블럭 데이터
    private List<int[]> attackBlocks;
    
    // 게임 모드 선택
    private String battleMode; // "NORMAL", "ITEM", "TIMELIMIT"
    
    // 블록 생성 동기화용 랜덤 시드
    private long randomSeed;
    
    // 게임 오버 정보
    private int winner; // 1 또는 2
    
    // 연결 정보
    private String message;
    
    public GameStatePacket(PacketType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public PacketType getType() {
        return type;
    }
    
    public void setType(PacketType type) {
        this.type = type;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    public void setBoard(int[][] board) {
        this.board = board;
    }
    
    public Color[][] getBoardColors() {
        return boardColors;
    }
    
    public void setBoardColors(Color[][] boardColors) {
        this.boardColors = boardColors;
    }
    
    public int getCurrentBlockX() {
        return currentBlockX;
    }
    
    public void setCurrentBlockX(int currentBlockX) {
        this.currentBlockX = currentBlockX;
    }
    
    public int getCurrentBlockY() {
        return currentBlockY;
    }
    
    public void setCurrentBlockY(int currentBlockY) {
        this.currentBlockY = currentBlockY;
    }
    
    public String getCurrentBlockType() {
        return currentBlockType;
    }
    
    public void setCurrentBlockType(String currentBlockType) {
        this.currentBlockType = currentBlockType;
    }
    
    public String getNextBlockType() {
        return nextBlockType;
    }
    
    public void setNextBlockType(String nextBlockType) {
        this.nextBlockType = nextBlockType;
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
    
    public long getElapsedTime() {
        return elapsedTime;
    }
    
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    
    public boolean hasTimeStopCharge() {
        return hasTimeStopCharge;
    }

    public void setHasTimeStopCharge(boolean hasTimeStopCharge) {
        this.hasTimeStopCharge = hasTimeStopCharge;
    }
    
    public List<int[]> getAttackBlocks() {
        return attackBlocks;
    }
    
    public void setAttackBlocks(List<int[]> attackBlocks) {
        this.attackBlocks = attackBlocks;
    }
    
    public String getBattleMode() {
        return battleMode;
    }
    
    public void setBattleMode(String battleMode) {
        this.battleMode = battleMode;
    }
    
    public long getRandomSeed() {
        return randomSeed;
    }
    
    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }
    
    public int getWinner() {
        return winner;
    }
    
    public void setWinner(int winner) {
        this.winner = winner;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
