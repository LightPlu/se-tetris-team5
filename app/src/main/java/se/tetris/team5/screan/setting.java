package se.tetris.team5.screan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class setting extends JPanel implements KeyListener {
    
    private home parentHome;
    
    public setting(home parent) {
        this.parentHome = parent;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        initComponents();
        setFocusable(true);
        addKeyListener(this);
    }
    
    private void initComponents() {
        // 제목
        JLabel titleLabel = new JLabel("게임 설정", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);
        
        // 설정 내용
        JPanel settingPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        settingPanel.setBackground(Color.BLACK);
        
        // 게임 속도 설정
        JPanel speedPanel = new JPanel();
        speedPanel.setBackground(Color.BLACK);
        JLabel speedLabel = new JLabel("게임 속도:");
        speedLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        speedLabel.setForeground(Color.WHITE);
        JSlider speedSlider = new JSlider(1, 10, 5);
        speedSlider.setBackground(Color.BLACK);
        speedSlider.setForeground(Color.WHITE);
        speedPanel.add(speedLabel);
        speedPanel.add(speedSlider);
        
        // 음향 설정
        JPanel soundPanel = new JPanel();
        soundPanel.setBackground(Color.BLACK);
        JLabel soundLabel = new JLabel("음향:");
        soundLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        soundLabel.setForeground(Color.WHITE);
        JButton soundButton = new JButton("ON");
        soundButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        soundPanel.add(soundLabel);
        soundPanel.add(soundButton);
        
        // 키 설정
        JPanel keyPanel = new JPanel();
        keyPanel.setBackground(Color.BLACK);
        JLabel keyLabel = new JLabel("조작키: 방향키", SwingConstants.CENTER);
        keyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        keyLabel.setForeground(Color.WHITE);
        keyPanel.add(keyLabel);
        
        settingPanel.add(speedPanel);
        settingPanel.add(soundPanel);
        settingPanel.add(keyPanel);
        add(settingPanel, BorderLayout.CENTER);
        
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