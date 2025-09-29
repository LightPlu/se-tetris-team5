package se.tetris.team5.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.util.GameSettings;

public class setting extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private JTextPane pane;
    private SimpleAttributeSet styleSet;
    private KeyListener keyListener;
    private GameSettings gameSettings;
    
    private int selectedOption = 0;
    private String[] menuOptions = {
        "창 크기 설정",
        "게임 속도",
        "키 설정", 
        "색맹 모드",
        "음향 효과",
        "스코어 초기화",
        "기본 설정 복원",
        "뒤로 가기"
    };
    
    private String[] windowSizes = {"소형", "중형", "대형", "특대형"};
    private String[] windowSizeValues = {
        GameSettings.WINDOW_SIZE_SMALL,
        GameSettings.WINDOW_SIZE_MEDIUM,
        GameSettings.WINDOW_SIZE_LARGE,
        GameSettings.WINDOW_SIZE_XLARGE
    };
    private int currentSizeIndex = 1;
    private boolean isKeySettingMode = false;
    private String currentKeyAction = "";
    private String[] keyActions = {"아래", "왼쪽", "오른쪽", "회전", "빠른낙하", "일시정지"};
    private String[] keyActionKeys = {"down", "left", "right", "rotate", "drop", "pause"};
    private int currentKeyIndex = 0;
    
    public setting() {
        super("5조 테트리스 - 설정");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        gameSettings = GameSettings.getInstance();
        initializeCurrentSettings();
        
        // 화면 설정
        pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(Color.BLACK);
        CompoundBorder border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 10),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        pane.setBorder(border);
        this.getContentPane().add(pane, BorderLayout.CENTER);
        
        // 창 크기 설정
        setSize(500, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // 텍스트 스타일 설정
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 16);
        StyleConstants.setFontFamily(styleSet, "Source Code Pro");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
        
        // 키 리스너 설정
        keyListener = new SettingKeyListener();
        addKeyListener(keyListener);
        setFocusable(true);
        requestFocus();
        
        // 화면 그리기
        drawSettingScreen();
    }
    
    private void initializeCurrentSettings() {
        String currentSize = gameSettings.getWindowSize();
        for (int i = 0; i < windowSizeValues.length; i++) {
            if (windowSizeValues[i].equals(currentSize)) {
                currentSizeIndex = i;
                break;
            }
        }
    }
    
    private void drawSettingScreen() {
        if (isKeySettingMode) {
            drawKeySettingScreen();
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        
        // 제목
        sb.append("\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("         5조 테트리스 설정\n");
        sb.append("═══════════════════════════════════\n\n");
        
        // 메뉴 옵션들
        for(int i = 0; i < menuOptions.length; i++) {
            if(i == selectedOption) {
                sb.append("  ► ");
            } else {
                sb.append("    ");
            }
            
            sb.append(menuOptions[i]);
            
            // 현재 설정값 표시
            switch(i) {
                case 0: // 창 크기
                    sb.append(": ").append(windowSizes[currentSizeIndex]);
                    break;
                case 1: // 게임 속도
                    sb.append(": ").append(gameSettings.getGameSpeed()).append("/10");
                    break;
                case 2: // 키 설정
                    sb.append(" >");
                    break;
                case 3: // 색맹 모드
                    sb.append(": ").append(gameSettings.isColorblindMode() ? "ON" : "OFF");
                    break;
                case 4: // 음향 효과
                    sb.append(": ").append(gameSettings.isSoundEnabled() ? "ON" : "OFF");
                    break;
            }
            
            if(i == selectedOption) {
                sb.append(" ◄");
            }
            sb.append("\n\n");
        }
        
        sb.append("\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("↑↓: 선택  ←→: 변경  Enter: 실행/확인\n");
        sb.append("ESC: 메인 메뉴로\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("설정은 자동으로 저장됩니다.");
        
        updateDisplay(sb.toString());
    }
    
    private void drawKeySettingScreen() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("           키 설정\n");
        sb.append("═══════════════════════════════════\n\n");
        
        if (currentKeyAction.isEmpty()) {
            // 키 목록 표시
            for(int i = 0; i < keyActions.length; i++) {
                if(i == currentKeyIndex) {
                    sb.append("  ► ");
                } else {
                    sb.append("    ");
                }
                
                String keyName = gameSettings.getKeyName(gameSettings.getKeyCode(keyActionKeys[i]));
                sb.append(keyActions[i]).append(": ").append(keyName);
                
                if(i == currentKeyIndex) {
                    sb.append(" ◄");
                }
                sb.append("\n\n");
            }
            
            sb.append("\n");
            sb.append("═══════════════════════════════════\n");
            sb.append("↑↓: 선택  Enter: 키 변경  ESC: 뒤로\n");
            sb.append("═══════════════════════════════════\n");
        } else {
            // 키 입력 대기 상태
            sb.append("\n\n");
            sb.append("   ").append(currentKeyAction).append("키를 설정합니다.\n\n");
            sb.append("   새로운 키를 눌러주세요...\n\n\n");
            sb.append("═══════════════════════════════════\n");
            sb.append("ESC: 취소\n");
            sb.append("═══════════════════════════════════\n");
        }
        
        updateDisplay(sb.toString());
    }
    
    private void updateDisplay(String text) {
        pane.setText(text);
        StyledDocument doc = pane.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(), styleSet, false);
        doc.setParagraphAttributes(0, doc.getLength(), styleSet, false);
        
        // 선택된 항목 색상 변경
        if (text.contains("►") && text.contains("◄")) {
            int startIndex = text.indexOf("►");
            int endIndex = text.indexOf("◄", startIndex) + 1;
            if (startIndex != -1 && endIndex != -1) {
                SimpleAttributeSet selectedStyle = new SimpleAttributeSet(styleSet);
                StyleConstants.setForeground(selectedStyle, Color.YELLOW);
                doc.setCharacterAttributes(startIndex, endIndex - startIndex, selectedStyle, false);
            }
        }
    }
    
    private void handleMenuAction() {
        switch(selectedOption) {
            case 0: // 창 크기 - 좌우 키로 변경됨
                break;
            case 1: // 게임 속도 - 좌우 키로 변경됨
                break;
            case 2: // 키 설정
                isKeySettingMode = true;
                currentKeyIndex = 0;
                drawKeySettingScreen();
                break;
            case 3: // 색맹 모드
                gameSettings.setColorblindMode(!gameSettings.isColorblindMode());
                drawSettingScreen();
                break;
            case 4: // 음향 효과
                gameSettings.setSoundEnabled(!gameSettings.isSoundEnabled());
                drawSettingScreen();
                break;
            case 5: // 스코어 초기화
                gameSettings.resetScores();
                showConfirmation("스코어가 초기화되었습니다!");
                break;
            case 6: // 기본 설정 복원
                gameSettings.setDefaultSettings();
                initializeCurrentSettings();
                showConfirmation("기본 설정으로 복원되었습니다!");
                break;
            case 7: // 뒤로 가기
                setVisible(false);
                new se.tetris.team5.screen.home();
                break;
        }
    }
    
    private void handleLeftRight(boolean isRight) {
        if (isKeySettingMode) return;
        
        switch(selectedOption) {
            case 0: // 창 크기
                if (isRight) {
                    currentSizeIndex = (currentSizeIndex + 1) % windowSizes.length;
                } else {
                    currentSizeIndex = (currentSizeIndex - 1 + windowSizes.length) % windowSizes.length;
                }
                gameSettings.setWindowSize(windowSizeValues[currentSizeIndex]);
                // 창 크기 즉시 적용
                applySizeChange();
                break;
            case 1: // 게임 속도
                int speed = gameSettings.getGameSpeed();
                if (isRight && speed < 10) {
                    gameSettings.setGameSpeed(speed + 1);
                } else if (!isRight && speed > 1) {
                    gameSettings.setGameSpeed(speed - 1);
                }
                break;
        }
        drawSettingScreen();
    }
    
    private void applySizeChange() {
        String[] sizeParts = windowSizeValues[currentSizeIndex].split("x");
        int width = Integer.parseInt(sizeParts[0]);
        int height = Integer.parseInt(sizeParts[1]);
        setSize(width, height);
        setLocationRelativeTo(null); // 화면 중앙에 재배치
    }
    
    private void showConfirmation(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("         알림\n");
        sb.append("═══════════════════════════════════\n\n");
        sb.append("   ").append(message).append("\n\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("아무 키나 눌러 계속하세요...\n");
        sb.append("═══════════════════════════════════\n");
        
        updateDisplay(sb.toString());
        
        // 2초 후 자동으로 설정 화면으로 돌아가기
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                drawSettingScreen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public class SettingKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (currentKeyAction.isEmpty()) {
                // 일반 메뉴 모드
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (isKeySettingMode) {
                            currentKeyIndex = (currentKeyIndex - 1 + keyActions.length) % keyActions.length;
                            drawKeySettingScreen();
                        } else {
                            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
                            drawSettingScreen();
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (isKeySettingMode) {
                            currentKeyIndex = (currentKeyIndex + 1) % keyActions.length;
                            drawKeySettingScreen();
                        } else {
                            selectedOption = (selectedOption + 1) % menuOptions.length;
                            drawSettingScreen();
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isKeySettingMode) {
                            handleLeftRight(false);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (!isKeySettingMode) {
                            handleLeftRight(true);
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (isKeySettingMode) {
                            currentKeyAction = keyActions[currentKeyIndex];
                            drawKeySettingScreen();
                        } else {
                            handleMenuAction();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (isKeySettingMode) {
                            isKeySettingMode = false;
                            drawSettingScreen();
                        } else {
                            setVisible(false);
                            new se.tetris.team5.screen.home();
                        }
                        break;
                }
            } else {
                // 키 입력 대기 모드
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    currentKeyAction = "";
                    drawKeySettingScreen();
                } else {
                    // 새로운 키 설정
                    gameSettings.setKeyCode(keyActionKeys[currentKeyIndex], e.getKeyCode());
                    currentKeyAction = "";
                    drawKeySettingScreen();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }
}