package se.tetris.team5.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.utils.setting.GameSettings;
import se.tetris.team5.ScreenController;
import se.tetris.team5.components.home.BGMManager;

public class setting {
    
    private ScreenController screenController;
    private JTextPane currentTextPane;
    private SimpleAttributeSet styleSet;
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
    
    public setting(ScreenController screenController) {
        this.screenController = screenController;
        
        gameSettings = GameSettings.getInstance();
        initializeCurrentSettings();
        
        // 텍스트 스타일 설정
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 16);
        StyleConstants.setFontFamily(styleSet, "Source Code Pro");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
    }
    
    public void display(JTextPane textPane) {
        this.currentTextPane = textPane;
        // Clear any child components left in the shared textPane and reset background
        textPane.removeAll();
        // 배경색을 명확히 검정색으로 설정
        textPane.setOpaque(true);
        textPane.setBackground(Color.BLACK);
        // Remove previous key listeners and add our own to avoid duplicates
        for (KeyListener kl : textPane.getKeyListeners()) {
            textPane.removeKeyListener(kl);
        }
        textPane.addKeyListener(new SettingKeyListener());
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
                    int speed = gameSettings.getGameSpeed();
                    sb.append(": ").append(gameSettings.getGameSpeedName(speed));
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
        if (currentTextPane != null) {
            currentTextPane.setText(text);
            StyledDocument doc = currentTextPane.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), styleSet, false);
            doc.setParagraphAttributes(0, doc.getLength(), styleSet, false);
            
            // 선택된 항목 색상 변경 (색맹 모드 대응)
            if (text.contains("►") && text.contains("◄")) {
                int startIndex = text.indexOf("►");
                int endIndex = text.indexOf("◄") + 1;
                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    SimpleAttributeSet selectedStyle = new SimpleAttributeSet(styleSet);
                    // 색맹 모드일 때는 구별하기 쉬운 밝은 노란색, 일반 모드일 때는 초록색
                    Color highlightColor = gameSettings.isColorblindMode() ? 
                        new Color(240, 228, 66) : Color.GREEN;
                    StyleConstants.setForeground(selectedStyle, highlightColor);
                    StyleConstants.setBold(selectedStyle, true);
                    doc.setCharacterAttributes(startIndex, endIndex - startIndex, selectedStyle, false);
                }
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
                // 게임 화면의 색상도 업데이트
                updateGameColors();
                drawSettingScreen();
                break;
            case 4: // 음향 효과
                gameSettings.setSoundEnabled(!gameSettings.isSoundEnabled());
                // BGM 제어
                controlBGM();
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
                // ScreenController를 통해 홈으로 돌아가기
                screenController.showScreen("home");
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
                if (isRight && speed < 5) {
                    gameSettings.setGameSpeed(speed + 1);
                } else if (!isRight && speed > 1) {
                    gameSettings.setGameSpeed(speed - 1);
                }
                // 게임 속도 변경을 게임에 즉시 적용
                updateGameSpeed();
                break;
        }
        drawSettingScreen();
    }
    
    private void applySizeChange() {
        // ScreenController를 통해 창 크기 업데이트
        screenController.updateWindowSize();
        
        // 창 크기 변경 후 잠시 후 화면 다시 그리기 (포커스 복원을 위해)
        new Thread(() -> {
            try {
                Thread.sleep(100); // 100ms 지연
                javax.swing.SwingUtilities.invokeLater(() -> {
                    drawSettingScreen();
                    // 포커스 복원
                    if (currentTextPane != null) {
                        currentTextPane.requestFocusInWindow();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void updateGameColors() {
        // 현재 화면이 게임 화면인지 확인하고 색상 업데이트
        String currentScreen = screenController.getCurrentScreen();
        if ("game".equals(currentScreen)) {
            // game 화면의 색상 업데이트 메소드 호출
            try {
                // ScreenController를 통해 game 인스턴스에 접근
                java.lang.reflect.Field[] fields = screenController.getClass().getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    if (field.getName().equals("gameScreen")) {
                        field.setAccessible(true);
                        se.tetris.team5.screens.game gameInstance = (se.tetris.team5.screens.game) field.get(screenController);
                        if (gameInstance != null) {
                            gameInstance.updateColorsForColorblindMode();
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                // 리플렉션 실패 시 무시
                e.printStackTrace();
            }
        }
        
        // 설정 화면 자체의 색상도 업데이트
        updateSettingColors();
    }
    
    private void updateGameSpeed() {
        // 현재 화면이 게임 화면인지 확인하고 속도 업데이트
        String currentScreen = screenController.getCurrentScreen();
        if ("game".equals(currentScreen)) {
            // game 화면의 속도 업데이트 메소드 호출
            try {
                // ScreenController를 통해 game 인스턴스에 접근
                java.lang.reflect.Field[] fields = screenController.getClass().getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    if (field.getName().equals("gameScreen")) {
                        field.setAccessible(true);
                        se.tetris.team5.screens.game gameInstance = (se.tetris.team5.screens.game) field.get(screenController);
                        if (gameInstance != null) {
                            gameInstance.updateGameSpeed();
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                // 리플렉션 실패 시 무시
                e.printStackTrace();
            }
        }
    }
    
    private void updateSettingColors() {
        // 설정 화면의 색상을 색맹 모드에 맞게 업데이트
        GameSettings settings = GameSettings.getInstance();
        
        // 스타일 색상 업데이트
        if (styleSet != null) {
            StyleConstants.setForeground(styleSet, settings.getUIColor("text"));
        }
        
        // 배경색은 항상 검정색으로 고정
        if (currentTextPane != null) {
            currentTextPane.setBackground(Color.BLACK);
        }
    }
    
    /**
     * 음향 효과 설정에 따라 BGM을 제어합니다
     */
    private void controlBGM() {
        BGMManager bgmManager = BGMManager.getInstance();
        
        // BGMManager에 설정 변경 알림
        bgmManager.onSoundSettingChanged();
        
        if (gameSettings.isSoundEnabled()) {
            // 사운드가 켜져있으면 현재 화면(설정)에 맞는 메인 BGM 재생
            bgmManager.playMainBGM();
            System.out.println("BGM enabled - Playing main BGM in settings");
        }
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
    
    private void showKeyWarning(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("         경고\n");
        sb.append("═══════════════════════════════════\n\n");
        sb.append("   ").append(message).append("\n\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("아무 키나 눌러 계속하세요...\n");
        sb.append("═══════════════════════════════════\n");
        
        updateDisplay(sb.toString());
        
        // 2초 후 자동으로 키 설정 화면으로 돌아가기
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                currentKeyAction = "";
                drawKeySettingScreen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private String findConflictingAction(int keyCode, String currentAction) {
        String[] actions = {"down", "left", "right", "rotate", "drop", "pause"};
        String[] actionNames = {"아래", "왼쪽", "오른쪽", "회전", "빠른낙하", "일시정지"};
        
        for (int i = 0; i < actions.length; i++) {
            if (!actions[i].equals(currentAction)) {
                if (gameSettings.getKeyCode(actions[i]) == keyCode) {
                    return actionNames[i];
                }
            }
        }
        return null;
    }
    
    private void showKeyConflictWarning(String conflictAction, String keyName) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("         키 중복 알림\n");
        sb.append("═══════════════════════════════════\n\n");
        sb.append("   '").append(keyName).append("' 키가 '").append(conflictAction).append("'에서\n");
        sb.append("   제거되고 현재 기능에 할당됩니다.\n\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("아무 키나 눌러 계속하세요...\n");
        sb.append("═══════════════════════════════════\n");
        
        updateDisplay(sb.toString());
        
        // 3초 후 자동으로 키 설정 화면으로 돌아가기
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                drawKeySettingScreen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public class SettingKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            e.consume(); // 이벤트 소비
        }

        @Override
        public void keyPressed(KeyEvent e) {
            e.consume(); // 이벤트 소비하여 전파 방지
            
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
                            // ScreenController를 통해 홈으로 돌아가기
                            screenController.showScreen("home");
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
                    int newKeyCode = e.getKeyCode();
                    
                    // 설정 인터페이스에 필수적인 키만 제한
                    if (newKeyCode == KeyEvent.VK_ESCAPE || newKeyCode == KeyEvent.VK_ENTER) {
                        // 제한된 키에 대한 경고 메시지
                        showKeyWarning("ESC와 Enter키는 설정할 수 없습니다.");
                        return;
                    }
                    
                    // 중복된 키가 있는지 확인하고 사용자에게 알림
                    String conflictAction = findConflictingAction(newKeyCode, keyActionKeys[currentKeyIndex]);
                    if (conflictAction != null) {
                        showKeyConflictWarning(conflictAction, gameSettings.getKeyName(newKeyCode));
                    }
                    
                    gameSettings.setKeyCode(keyActionKeys[currentKeyIndex], newKeyCode);
                    currentKeyAction = "";
                    drawKeySettingScreen();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            e.consume(); // 이벤트 소비
        }
    }
}