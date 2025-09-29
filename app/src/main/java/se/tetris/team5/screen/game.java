package se.tetris.team5.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import se.tetris.team5.blocks.Block;
import se.tetris.team5.component.game.GameBoard;
import se.tetris.team5.component.game.NextBlockBoard;
import se.tetris.team5.component.game.ScoreBoard;
import se.tetris.team5.util.GameSettings;

public class game extends JPanel implements KeyListener, 
    GameBoard.GameEventListener, 
    GameBoard.NextBlockListener, 
    GameBoard.ScoreListener {
    
    private static final long serialVersionUID = 1L;
    
    private home parentHome;
    private GameBoard gameBoard;
    private NextBlockBoard nextBlockBoard;
    private ScoreBoard scoreBoard;
    private JPanel rightPanel;
    
    public game(home parent) {
        this.parentHome = parent;
        initComponents();
    }
    
    private void initComponents() {
        // GameSettings에서 설정 불러오기 (향후 확장 가능)
        //GameSettings settings = GameSettings.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        // 오른쪽 패널을 먼저 설정
        setupRightPanel();
        
        // 메인 게임 보드 생성
        gameBoard = new GameBoard();
        gameBoard.setGameEventListener(this);
        gameBoard.setNextBlockListener(this);
        gameBoard.setScoreListener(this);
        gameBoard.setPreferredSize(new Dimension(350, 600));
        add(gameBoard, BorderLayout.CENTER);
        
        // 키 리스너 설정
        setFocusable(true);
        addKeyListener(this);
        
        // 게임 보드에도 포커스 설정
        gameBoard.setFocusable(true);
        gameBoard.requestFocus();
        
        // 디버깅: 컴포넌트가 제대로 생성되었는지 확인
        System.out.println("RightPanel created: " + (rightPanel != null));
        System.out.println("NextBlockBoard created: " + (nextBlockBoard != null));
        System.out.println("ScoreBoard created: " + (scoreBoard != null));
    }
    
    private void setupRightPanel() {
        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(2, 1, 5, 5));
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setPreferredSize(new Dimension(250, 600));
        rightPanel.setMinimumSize(new Dimension(250, 400));
        
        // 다음 블록 미리보기 패널
        nextBlockBoard = new NextBlockBoard();
        nextBlockBoard.setPreferredSize(new Dimension(240, 150));
        rightPanel.add(nextBlockBoard);
        
        // 스코어 패널
        scoreBoard = new ScoreBoard();
        scoreBoard.setPreferredSize(new Dimension(240, 400));
        rightPanel.add(scoreBoard);
        
        add(rightPanel, BorderLayout.EAST);
        
        // 패널이 제대로 표시되는지 확인
        rightPanel.setVisible(true);
        nextBlockBoard.setVisible(true);
        scoreBoard.setVisible(true);
    }
    
    // GameBoard.GameEventListener 구현
    @Override
    public void onGameOver() {
        // 게임 오버 처리 (필요시 확장)
        System.out.println("게임 오버!");
    }
    
    @Override
    public void onGameReset() {
        // 게임 리셋 처리 (필요시 확장)
        System.out.println("게임 리셋!");
    }
    
    // GameBoard.NextBlockListener 구현
    @Override
    public void onNextBlockChanged(Block nextBlock) {
        if (nextBlockBoard != null) {
            nextBlockBoard.updateNextBlock(nextBlock);
        }
    }
    
    // GameBoard.ScoreListener 구현
    @Override
    public void onScoreChanged(int score, int level, int linesCleared) {
        if (scoreBoard != null) {
            scoreBoard.updateScore(score, level, linesCleared);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            // 게임 보드의 타이머 정지는 GameBoard에서 처리하도록 할 수 있음
            parentHome.showHomeScreen();
            break;
        default:
            // 다른 키 이벤트는 게임 보드로 전달
            if (gameBoard != null) {
                gameBoard.keyPressed(e);
            }
            break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        if (gameBoard != null) {
            gameBoard.keyTyped(e);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (gameBoard != null) {
            gameBoard.keyReleased(e);
        }
    }
    
    // 게임 시작/정지 메서드들 (필요시 추가)
    public void startGame() {
        if (gameBoard != null) {
            gameBoard.requestFocus();
        }
    }
    
    public void pauseGame() {
        // 게임 일시정지 기능 (필요시 구현)
    }
    
    public void resetGame() {
        if (gameBoard != null) {
            gameBoard.reset();
        }
    }
}