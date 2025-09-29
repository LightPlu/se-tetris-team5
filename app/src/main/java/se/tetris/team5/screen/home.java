package se.tetris.team5.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

public class home extends JFrame implements KeyListener {
    
    private int selectedMenu = 0; // 0: 게임시작, 1: 스코어보기, 2: 설정, 3: 종료
    private JLabel[] menuLabels;
    private JPanel currentScreen;
    
    public home() {
        super("5조 테트리스");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setFocusable(true);
        addKeyListener(this);
        
        showHomeScreen();
        setVisible(true);
    }
    
    public void showHomeScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);
        
        // 게임 제목
        JLabel titleLabel = new JLabel("5조 TETRIS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        titleLabel.setForeground(Color.CYAN);
        add(titleLabel, BorderLayout.NORTH);
        
        // 메뉴 패널
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 10, 15));
        menuPanel.setBackground(Color.BLACK);
        
        // 메뉴 항목들
        String[] menuTexts = {"게임 시작", "스코어 보기", "설정", "종료"};
        Color[] menuColors = {Color.GREEN, Color.BLUE, Color.ORANGE, Color.RED};
        menuLabels = new JLabel[4];
        
        for (int i = 0; i < 4; i++) {
            menuLabels[i] = new JLabel(menuTexts[i], SwingConstants.CENTER);
            menuLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 20));
            menuLabels[i].setForeground(Color.WHITE);
            menuLabels[i].setOpaque(true);
            menuLabels[i].setBackground(menuColors[i]);
            menuPanel.add(menuLabels[i]);
        }
        
        updateMenuSelection();
        add(menuPanel, BorderLayout.CENTER);
        
        // 하단 안내
        JLabel instructionLabel = new JLabel("↑↓: 선택, Enter: 확인", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.LIGHT_GRAY);
        add(instructionLabel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
        requestFocus();
    }
    
    private void updateMenuSelection() {
        Border selectedBorder = BorderFactory.createLineBorder(Color.YELLOW, 3);
        Border normalBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        
        for (int i = 0; i < menuLabels.length; i++) {
            if (i == selectedMenu) {
                menuLabels[i].setBorder(selectedBorder);
            } else {
                menuLabels[i].setBorder(normalBorder);
            }
        }
    }
    
    private void selectCurrentMenu() {
        switch (selectedMenu) {
            case 0: // 게임 시작
                showGameScreen();
                break;
            case 1: // 스코어 보기
                showScoreScreen();
                break;
            case 2: // 설정
                showSettingScreen();
                break;
            case 3: // 종료
                System.exit(0);
                break;
        }
    }
    
    private void showGameScreen() {
        getContentPane().removeAll();
        currentScreen = new game(this);
        add(currentScreen, BorderLayout.CENTER);
        revalidate();
        repaint();
        currentScreen.requestFocus();
    }
    
    private void showScoreScreen() {
        setVisible(false);
        score scoreWindow = new score();
        scoreWindow.setVisible(true);
    }
    
    private void showSettingScreen() {
        setVisible(false);
        setting settingWindow = new setting();
        settingWindow.setVisible(true);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedMenu = (selectedMenu - 1 + 4) % 4;
                updateMenuSelection();
                break;
            case KeyEvent.VK_DOWN:
                selectedMenu = (selectedMenu + 1) % 4;
                updateMenuSelection();
                break;
            case KeyEvent.VK_ENTER:
                selectCurrentMenu();
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
}
