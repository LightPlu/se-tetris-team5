package se.tetris.team5.gamelogic;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.gamelogic.block.BlockFactory;
import se.tetris.team5.components.game.BoardManager;
import se.tetris.team5.gamelogic.scoring.GameScoring;

/**
 * P2P 대전용 게임 엔진
 * - 네트워크로 받은 상태를 주입받아 화면에 표시
 * - 실제 게임 로직은 실행하지 않음 (읽기 전용)
 * - 타이머, 점수, 레벨 등은 네트워크 데이터로 업데이트
 */
public class P2PGameEngine extends GameEngine {
    
    private static final int START_X = 3;
    private static final int START_Y = 0;
    
    // 네트워크로 받은 현재 블록 정보
    private String currentBlockType;
    private String nextBlockType;
    
    // 네트워크로 받은 게임 상태
    private boolean isRemoteGameOver = false;
    private long remoteElapsedTime = 0;
    
    /**
     * P2P 게임 엔진 생성자
     * @param height 보드 높이
     * @param width 보드 너비
     */
    public P2PGameEngine(int height, int width) {
        super(height, width, false); // 자동 시작 비활성화
        
        // P2P용으로 초기화
        initializeP2P();
    }
    
    /**
     * P2P용 초기화
     */
    private void initializeP2P() {
        // BoardManager는 부모에서 이미 생성됨
        getBoardManager().reset();
        
        // 게임 상태 초기화
        setGameOver(false);
        isRemoteGameOver = false;
        remoteElapsedTime = 0;
        
        System.out.println("[P2PGameEngine] P2P 전용 엔진 초기화 완료");
    }
    
    /**
     * 네트워크로 받은 보드 상태 주입
     * @param board 보드 배열
     * @param boardColors 보드 색상 배열
     */
    public void injectBoardState(int[][] board, java.awt.Color[][] boardColors) {
        if (board == null || boardColors == null) {
            return;
        }
        
        BoardManager boardManager = getBoardManager();
        int[][] currentBoard = boardManager.getBoard();
        java.awt.Color[][] currentColors = boardManager.getBoardColors();
        
        // 보드 복사
        for (int i = 0; i < board.length && i < currentBoard.length; i++) {
            for (int j = 0; j < board[i].length && j < currentBoard[i].length; j++) {
                currentBoard[i][j] = board[i][j];
                if (boardColors[i] != null && boardColors[i][j] != null) {
                    currentColors[i][j] = boardColors[i][j];
                }
            }
        }
    }
    
    /**
     * 네트워크로 받은 블록 타입 설정
     * @param currentType 현재 블록 타입
     * @param nextType 다음 블록 타입
     */
    public void injectBlockTypes(String currentType, String nextType) {
        this.currentBlockType = currentType;
        this.nextBlockType = nextType;
        
        // 실제 블록 객체 생성 (화면 표시용)
        if (currentType != null && !currentType.isEmpty()) {
            Block newBlock = createBlockFromType(currentType);
            if (newBlock != null) {
                setCurrentBlock(newBlock);
            }
        }
        
        if (nextType != null && !nextType.isEmpty()) {
            Block newNextBlock = createBlockFromType(nextType);
            if (newNextBlock != null) {
                setNextBlock(newNextBlock);
            }
        }
    }
    
    /**
     * 문자열 타입으로부터 블록 생성
     * [테스트 모드] I블록만 생성
     */
    private Block createBlockFromType(String type) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        
        // [테스트 모드] P2P 대전에서는 항상 I블록만 생성
        return new se.tetris.team5.blocks.IBlock();}
        
        /* 원래 코드 (모든 블록 타입 생성)
        switch (type) {
            case "I": return new se.tetris.team5.blocks.IBlock();
            case "O": return new se.tetris.team5.blocks.OBlock();
            case "T": return new se.tetris.team5.blocks.TBlock();
            case "S": return new se.tetris.team5.blocks.SBlock();
            case "Z": return new se.tetris.team5.blocks.ZBlock();
            case "L": return new se.tetris.team5.blocks.LBlock();
            case "J": return new se.tetris.team5.blocks.JBlock();
            case "W": return new se.tetris.team5.blocks.WBlock();
            case "DOT": return new se.tetris.team5.blocks.DotBlock();
            default: 
                System.out.println("[P2PGameEngine] 알 수 없는 블록 타입: " + type);
                return null;
        }
    }
    
    /**
     * 네트워크로 받은 점수 설정
     * @param score 점수
     */
    public void injectScore(int score) {
        GameScoring scoring = getGameScoring();
        if (scoring != null) {
            // 현재 점수와 차이만큼 추가
            int currentScore = scoring.getCurrentScore();
            int diff = score - currentScore;
            if (diff > 0) {
                scoring.addPoints(diff);
            } else if (diff < 0) {
                // 점수가 감소한 경우 (패널티 등)
                scoring.reset();
                scoring.addPoints(score);
            }
        }
    }
    
    /**
     * 네트워크로 받은 레벨 설정
     * @param level 레벨
     */
    public void injectLevel(int level) {
        GameScoring scoring = getGameScoring();
        if (scoring != null) {
            int currentLevel = scoring.getLevel();
            if (level != currentLevel) {
                // 레벨 직접 설정은 GameScoring에 메서드가 없으므로
                // 줄 삭제로 레벨 조정 (근사치)
                int targetLines = (level - 1) * 10;
                int currentLines = scoring.getLinesCleared();
                int diff = targetLines - currentLines;
                if (diff > 0) {
                    scoring.addLinesCleared(diff);
                }
            }
        }
    }
    
    /**
     * 네트워크로 받은 줄 삭제 수 설정
     * @param lines 삭제된 줄 수
     */
    public void injectLinesCleared(int lines) {
        GameScoring scoring = getGameScoring();
        if (scoring != null) {
            int currentLines = scoring.getLinesCleared();
            int diff = lines - currentLines;
            if (diff > 0) {
                scoring.addLinesCleared(diff);
            } else if (diff < 0) {
                // 줄 수가 감소한 경우는 없지만 안전장치
                scoring.reset();
                scoring.addLinesCleared(lines);
            }
        }
    }
    
    /**
     * 네트워크로 받은 경과 시간 설정
     * @param elapsedTime 경과 시간 (밀리초)
     */
    public void injectElapsedTime(long elapsedTime) {
        this.remoteElapsedTime = elapsedTime;
    }
    
    /**
     * 경과 시간 가져오기 (P2P용 오버라이드)
     */
    @Override
    public long getElapsedTime() {
        // 네트워크로 받은 시간 사용
        return remoteElapsedTime;
    }
    
    /**
     * 게임 오버 상태 주입
     * @param gameOver 게임 오버 여부
     */
    public void injectGameOver(boolean gameOver) {
        this.isRemoteGameOver = gameOver;
        setGameOver(gameOver);
    }
    
    /**
     * P2P 엔진은 입력을 받지 않음 (읽기 전용)
     */
    @Override
    public boolean moveBlockDown() {
        // 네트워크 데이터로만 업데이트, 실제 이동 안함
        return false;
    }
    
    @Override
    public boolean moveBlockLeft() {
        return false;
    }
    
    @Override
    public boolean moveBlockRight() {
        return false;
    }
    
    @Override
    public boolean rotateBlock() {
        return false;
    }
    
    @Override
    public boolean hardDrop() {
        return false;
    }
    
    /**
     * P2P 엔진용 현재 블록 위치 설정
     */
    public void setBlockPosition(int x, int y) {
        setX(x);
        setY(y);
    }
    
    /**
     * 블록 타입 문자열 가져오기
     */
    public String getCurrentBlockType() {
        Block block = getCurrentBlock();
        return block != null ? getBlockTypeString(block) : "";
    }
    
    public String getNextBlockType() {
        Block block = getNextBlock();
        return block != null ? getBlockTypeString(block) : "";
    }
    
    private String getBlockTypeString(Block block) {
        if (block == null) return "";
        
        String className = block.getClass().getSimpleName();
        if (className.endsWith("Block")) {
            String type = className.substring(0, className.length() - 5);
            if (type.equals("Dot")) return "DOT";
            return type.toUpperCase();
        }
        return className;
    }
    
    /**
     * 게임 시작 (P2P용)
     */
    public void startP2PGame() {
        initializeP2P();
        setGameRunning(true);
        setGameStartTime(System.currentTimeMillis());
        System.out.println("[P2PGameEngine] P2P 게임 시작");
    }
    
    /**
     * 게임 중지 (P2P용)
     */
    public void stopP2PGame() {
        setGameRunning(false);
        System.out.println("[P2PGameEngine] P2P 게임 중지");
    }
    
    /**
     * 완전한 게임 상태 주입 (한번에 모든 상태 업데이트)
     */
    public void injectCompleteState(
        int[][] board, 
        java.awt.Color[][] boardColors,
        String currentBlockType,
        String nextBlockType,
        int score,
        int level,
        int linesCleared,
        long elapsedTime
    ) {
        injectBoardState(board, boardColors);
        injectBlockTypes(currentBlockType, nextBlockType);
        injectScore(score);
        injectLevel(level);
        injectLinesCleared(linesCleared);
        injectElapsedTime(elapsedTime);
    }
}
