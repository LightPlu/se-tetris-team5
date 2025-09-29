package se.tetris.team5.component.game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.blocks.IBlock;
import se.tetris.team5.blocks.JBlock;
import se.tetris.team5.blocks.LBlock;
import se.tetris.team5.blocks.OBlock;
import se.tetris.team5.blocks.SBlock;
import se.tetris.team5.blocks.TBlock;
import se.tetris.team5.blocks.ZBlock;
import se.tetris.team5.util.GameSettings;

public class GameBoard extends JTextPane implements KeyListener {
    
    private static final long serialVersionUID = 1L;
    
    public static final int HEIGHT = 19;
    public static final int WIDTH = 10;
    public static final char BORDER_CHAR = 'X';
    
    private int[][] board;         // 보드 상태 저장 (0: 빈칸, 1: 고정된 블록, 2: 움직이는 블록)
    private Color[][] boardColors; // 각 셀의 색상 정보 저장
    private SimpleAttributeSet styleSet;       // 텍스트 스타일 설정
    private Timer timer;           // 블록 자동 낙하 타이머
    private Block curr;            // 현재 움직이는 블록
    private Block next;            // 다음 블록
    int x = 3; //Default Position.
    int y = 0;
    
    // 게임 상태 정보
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    
    // 이벤트 리스너들
    private GameEventListener gameEventListener;
    private NextBlockListener nextBlockListener;
    private ScoreListener scoreListener;
    
    private int initInterval;
    
    public GameBoard() {
        initComponents();
        setFocusable(true);
        addKeyListener(this);
    }
    
    public interface GameEventListener {
        void onGameOver();
        void onGameReset();
    }
    
    public interface NextBlockListener {
        void onNextBlockChanged(Block nextBlock);
    }
    
    public interface ScoreListener {
        void onScoreChanged(int score, int level, int linesCleared);
    }
    
    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }
    
    public void setNextBlockListener(NextBlockListener listener) {
        this.nextBlockListener = listener;
    }
    
    public void setScoreListener(ScoreListener listener) {
        this.scoreListener = listener;
    }
    
    private void initComponents() {
        // GameSettings에서 설정 불러오기
        GameSettings settings = GameSettings.getInstance();
        
        // 게임 속도 설정 (1-10, 1이 가장 빠름)
        int speed = settings.getGameSpeed();
        initInterval = 1100 - (speed * 100); // 1000ms ~ 100ms
        
        //Board display setting.
        setEditable(false);
        setBackground(Color.BLACK);
        CompoundBorder border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 10),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        setBorder(border);
        
        //Document default style.
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 18);
        StyleConstants.setFontFamily(styleSet, "Source Code Pro");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
        
        //Set timer for block drops.
        timer = new Timer(initInterval, new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e) {
                moveDown();
                drawBoard();
            }
        });
        
        //Initialize board for the game.
        board = new int[HEIGHT][WIDTH];
        boardColors = new Color[HEIGHT][WIDTH];
        
        //Create the first block and next block
        curr = getRandomBlock();
        next = getRandomBlock();
        placeBlock();
        drawBoard();
        notifyNextBlockChanged();
        notifyScoreChanged();
        timer.start();
    }

    private Block getRandomBlock() {
        Random rnd = new Random(System.currentTimeMillis());
        int block = rnd.nextInt(7); // 0~6까지 7개 블록
        switch(block) {
        case 0:
            return new IBlock();
        case 1:
            return new JBlock();
        case 2:
            return new LBlock();
        case 3:
            return new ZBlock();
        case 4:
            return new SBlock();
        case 5:
            return new TBlock();
        case 6:
            return new OBlock();            
        }
        return new LBlock();
    }
    
    private void placeBlock() {
        // 보드 배열 업데이트만 수행 (색상은 drawBoard에서 처리)
        for(int j=0; j<curr.height(); j++) {
            for(int i=0; i<curr.width(); i++) {
                if(y+j >= 0 && y+j < HEIGHT && x+i >= 0 && x+i < WIDTH) {
                    if(curr.getShape(i, j) == 1) {
                        board[y+j][x+i] = 2; // 움직이는 블록은 값 2로 설정
                        boardColors[y+j][x+i] = curr.getColor(); // 색상 정보도 저장
                    }
                }
            }
        }
    }
    
    private void eraseCurr() {
        for(int i=x; i<x+curr.width(); i++) {
            for(int j=y; j<y+curr.height(); j++) {
                if(curr.getShape(i-x, j-y) == 1) {
                    // 배열 경계 검사를 추가
                    if(j >= 0 && j < HEIGHT && i >= 0 && i < WIDTH) {
                        // 움직이는 블록(값 2)만 지우고, 고정된 블록(값 1)은 지우지 않음
                        if(board[j][i] == 2) {
                            board[j][i] = 0;
                            boardColors[j][i] = null; // 색상 정보도 제거
                        }
                    }
                }
            }
        }
    }
    
    // 블록이 주어진 위치로 이동할 수 있는지 확인하는 메서드
    private boolean canMove(int newX, int newY, Block block) {
        // 경계 검사
        if(newX < 0 || newX + block.width() > WIDTH || 
           newY + block.height() > HEIGHT) {
            return false;
        }
        
        // 상단 경계는 허용 (블록이 위에서 시작할 수 있도록)
        if(newY < 0) {
            // 블록의 일부가 보드 위에 있어도 되지만, 보드 안쪽 부분만 검사
            for(int i = 0; i < block.width(); i++) {
                for(int j = 0; j < block.height(); j++) {
                    if(block.getShape(i, j) == 1 && newY + j >= 0) {
                        if(board[newY + j][newX + i] == 1) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        
        // 고정된 블록과의 충돌 검사
        for(int i = 0; i < block.width(); i++) {
            for(int j = 0; j < block.height(); j++) {
                if(block.getShape(i, j) == 1) { // 블록의 실제 부분만 검사
                    if(board[newY + j][newX + i] == 1) {
                        return false; // 이미 고정된 블록이 있음
                    }
                }
            }
        }
        return true;
    }
    
    // 현재 블록을 보드에 영구적으로 고정하는 메서드
    private void fixBlock() {
        for(int i = 0; i < curr.width(); i++) {
            for(int j = 0; j < curr.height(); j++) {
                if(curr.getShape(i, j) == 1 && y + j >= 0 && y + j < HEIGHT && x + i >= 0 && x + i < WIDTH) {
                    board[y + j][x + i] = 1; // 움직이는 블록을 고정된 블록으로 변환
                    boardColors[y + j][x + i] = curr.getColor(); // 색상도 고정
                }
            }
        }
    }

    protected void moveDown() {
        eraseCurr();
        
        // 한 칸 아래로 이동할 수 있는지 확인
        if(canMove(x, y + 1, curr)) {
            y++;
            placeBlock();
        } else {
            // 더 이상 내려갈 수 없으면 현재 위치에 블록을 고정
            // 먼저 현재 위치에 블록을 다시 그리기
            placeBlock();
            fixBlock();
            
            // 가득 찬 줄이 있는지 확인하고 제거
            clearLines();
            
            // 새로운 블록 생성 (다음 블록을 현재 블록으로)
            curr = next;
            next = getRandomBlock();
            x = 3;
            y = 0;
            notifyNextBlockChanged();
            
            // 게임 오버 체크 (새 블록이 시작 위치에 놓일 수 없는 경우)
            if(!canMove(x, y, curr)) {
                // 게임 오버 처리 (일단 리셋)
                reset();
                if(gameEventListener != null) {
                    gameEventListener.onGameOver();
                }
                return;
            }
            
            placeBlock();
        }
    }
    
    protected void moveRight() {
        eraseCurr();
        if(canMove(x + 1, y, curr)) {
            x++;
        }
        placeBlock();
    }

    protected void moveLeft() {
        eraseCurr();
        if(canMove(x - 1, y, curr)) {
            x--;
        }
        placeBlock();
    }

    public void drawBoard() {
        StringBuffer sb = new StringBuffer();
        for(int t=0; t<WIDTH+2; t++) sb.append(BORDER_CHAR);
        sb.append("\n");
        
        for(int i=0; i < board.length; i++) {
            sb.append(BORDER_CHAR);
            for(int j=0; j < board[i].length; j++) {
                if(board[i][j] == 1 || board[i][j] == 2) {
                    sb.append("■"); // 블록 문자 변경
                } else {
                    sb.append(" ");
                }
            }
            sb.append(BORDER_CHAR);
            sb.append("\n");
        }
        for(int t=0; t<WIDTH+2; t++) sb.append(BORDER_CHAR);
        
        setText(sb.toString());
        StyledDocument doc = getStyledDocument();
        
        // 기본 스타일 적용 (테두리 색상을 하얀색으로 고정)
        SimpleAttributeSet borderStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(borderStyle, Color.WHITE);
        StyleConstants.setFontSize(borderStyle, 16);
        StyleConstants.setFontFamily(borderStyle, "Courier New");
        StyleConstants.setBold(borderStyle, true);
        StyleConstants.setLineSpacing(borderStyle, -0.4f); // 줄 간격 더 많이 줄이기
        doc.setCharacterAttributes(0, doc.getLength(), borderStyle, false);
        doc.setParagraphAttributes(0, doc.getLength(), borderStyle, false);
        
        // 각 블록에 색상 적용
        int textOffset = WIDTH + 3; // 첫 번째 줄(테두리) 건너뛰기
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if((board[i][j] == 1 || board[i][j] == 2) && boardColors[i][j] != null) {
                    SimpleAttributeSet colorStyle = new SimpleAttributeSet(borderStyle); // 기본 스타일 복사
                    StyleConstants.setForeground(colorStyle, boardColors[i][j]); // 색상만 변경
                    
                    int charPos = textOffset + j + 1; // +1은 왼쪽 테두리
                    doc.setCharacterAttributes(charPos, 1, colorStyle, false);
                }
            }
            textOffset += WIDTH + 3; // 다음 줄로 이동 (테두리 2개 + 줄바꿈 1개)
        }
    }
    
    // 가득 찬 줄을 제거하는 메서드
    private void clearLines() {
        int clearedCount = 0;
        
        for(int row = HEIGHT - 1; row >= 0; row--) {
            // 현재 줄이 가득 찼는지 확인 (고정된 블록만 고려)
            boolean fullLine = true;
            for(int col = 0; col < WIDTH; col++) {
                if(board[row][col] != 1) { // 고정된 블록(값 1)만 고려
                    fullLine = false;
                    break;
                }
            }
            
            // 가득 찬 줄이 있으면 제거하고 위의 줄들을 아래로 내림
            if(fullLine) {
                clearedCount++;
                
                // 현재 줄부터 위의 모든 줄을 한 줄씩 아래로 이동
                for(int moveRow = row; moveRow > 0; moveRow--) {
                    for(int col = 0; col < WIDTH; col++) {
                        board[moveRow][col] = board[moveRow - 1][col];
                        boardColors[moveRow][col] = boardColors[moveRow - 1][col];
                    }
                }
                // 맨 위 줄은 빈 줄로 만듦
                for(int col = 0; col < WIDTH; col++) {
                    board[0][col] = 0;
                    boardColors[0][col] = null;
                }
                // 같은 줄을 다시 검사해야 하므로 row를 증가시킴
                row++;
            }
        }
        
        // 점수 계산 및 업데이트
        if(clearedCount > 0) {
            linesCleared += clearedCount;
            
            // 라인 수에 따른 점수 계산
            int lineScore = 0;
            switch(clearedCount) {
                case 1: lineScore = 100; break;
                case 2: lineScore = 300; break;
                case 3: lineScore = 500; break;
                case 4: lineScore = 800; break; // 테트리스!
            }
            
            score += lineScore * level;
            
            // 레벨 업 (10라인마다)
            int newLevel = (linesCleared / 10) + 1;
            if(newLevel > level) {
                level = newLevel;
                // 레벨이 올라가면 게임 속도 증가
                int newDelay = Math.max(100, initInterval - (level - 1) * 100);
                timer.setDelay(newDelay);
            }
            
            notifyScoreChanged();
        }
    }

    public void reset() {
        this.board = new int[HEIGHT][WIDTH];
        this.boardColors = new Color[HEIGHT][WIDTH];
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
        // 새 블록 생성 및 게임 재시작
        curr = getRandomBlock();
        next = getRandomBlock();
        x = 3;
        y = 0;
        placeBlock();
        drawBoard();
        notifyNextBlockChanged();
        notifyScoreChanged();
        if(gameEventListener != null) {
            gameEventListener.onGameReset();
        }
    }
    
    private void notifyNextBlockChanged() {
        if(nextBlockListener != null && next != null) {
            nextBlockListener.onNextBlockChanged(next);
        }
    }
    
    private void notifyScoreChanged() {
        if(scoreListener != null) {
            scoreListener.onScoreChanged(score, level, linesCleared);
        }
    }
    
    // Public getters for game state
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLinesCleared() { return linesCleared; }
    public Block getNextBlock() { return next; }
    
    @Override
    public void keyPressed(KeyEvent e) {
        GameSettings settings = GameSettings.getInstance();
        int keyCode = e.getKeyCode();
        
        if(keyCode == settings.getKeyCode("down")) {
            moveDown();
            drawBoard();
        } else if(keyCode == settings.getKeyCode("right")) {
            moveRight();
            drawBoard();
        } else if(keyCode == settings.getKeyCode("left")) {
            moveLeft();
            drawBoard();
        } else if(keyCode == settings.getKeyCode("rotate")) {
            eraseCurr();
            curr.rotate();
            // 회전 후 위치가 유효한지 확인
            if(!canMove(x, y, curr)) {
                // 회전이 불가능하면 다시 되돌림
                curr.rotate();
                curr.rotate();
                curr.rotate();
            }
            placeBlock();
            drawBoard();
        } else if(keyCode == settings.getKeyCode("drop")) {
            // 빠른 낙하 (스페이스바)
            while(canMove(x, y + 1, curr)) {
                moveDown();
            }
            drawBoard();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
}