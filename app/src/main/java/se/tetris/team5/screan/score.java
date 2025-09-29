package se.tetris.team5.screan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class score extends JPanel implements KeyListener {
    
    private home parentHome;
    
    public score(home parent) {
        this.parentHome = parent;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        initComponents();
        setFocusable(true);
        addKeyListener(this);
    }
    
    private void initComponents() {
        // 제목
        JLabel titleLabel = new JLabel("최고 점수", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);
        
        // 스코어 내용 (임시)
        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(Color.BLACK);
        JLabel scoreLabel = new JLabel("<html><center>1위: 1000점<br>2위: 800점<br>3위: 600점</center></html>", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        scoreLabel.setForeground(Color.WHITE);
        scorePanel.add(scoreLabel);
        add(scorePanel, BorderLayout.CENTER);
        
        // 안내 메시지
        JLabel instructionLabel = new JLabel("ESC: 메뉴로 돌아가기", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.LIGHT_GRAY);
        add(instructionLabel, BorderLayout.SOUTH);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            parentHome.showHomeScreen();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
}