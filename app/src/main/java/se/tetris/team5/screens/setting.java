package se.tetris.team5.screens;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
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
    private JTextPane hostPane;
    private SimpleAttributeSet styleSet;
    private GameSettings gameSettings;
    private JPanel backgroundPanel;
    private ImageIcon backgroundGif;
    private Image backgroundFallbackImage;
    
    private int selectedOption = 0;
    private static final int SELECTOR_ICON_SIZE = 40;
    private ImageIcon selectorIcon;
    private final String[] menuOptions = {
        "창 크기 설정",
        "게임 속도",
        "키 설정",
        "대전모드 키 설정",
        "색맹 모드",
        "음향 효과",
        "스코어 초기화",
        "기본 설정 복원",
        "뒤로 가기"
    };
    
    private String[] windowSizes = {"소형", "중형", "대형"};  // 3가지로 변경
    private String[] windowSizeValues = {
        GameSettings.WINDOW_SIZE_SMALL,   // 450x600 (기존 중형)
        GameSettings.WINDOW_SIZE_MEDIUM,  // 550x700 (기존 대형)
        GameSettings.WINDOW_SIZE_LARGE    // 650x800 (기존 특대형)
    };
    private int currentSizeIndex = 1;  // 기본값: 중형
    private boolean isKeySettingMode = false;
    private String currentKeyAction = "";
    private String[] keyActions = {"아래", "왼쪽", "오른쪽", "회전", "빠른낙하", "일시정지", "아이템 사용"};
    private String[] keyActionKeys = {"down", "left", "right", "rotate", "drop", "pause", "item"};
    private int currentKeyIndex = 0;
    
    // 대전모드 키 설정
    private boolean isBattleKeySettingMode = false;
    private int battleKeyPlayerNum = 1; // 1 or 2
    private String[] battleKeyActions = {"아래", "왼쪽", "오른쪽", "회전", "빠른낙하", "아이템 사용"};
    private String[] battleKeyActionKeys = {"down", "left", "right", "rotate", "drop", "item"};
    private int currentBattleKeyIndex = 0;
    
    // 확인 메시지 모드
    private boolean isConfirmationMode = false;
    private Thread confirmationTimer = null;
    
    public setting(ScreenController screenController) {
        this.screenController = screenController;
        
        gameSettings = GameSettings.getInstance();
        initializeCurrentSettings();
        loadBackgroundImage();
        loadSelectorImage();
        
        // 텍스트 스타일 설정
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 16);
        StyleConstants.setFontFamily(styleSet, "Source Code Pro");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
    }
    
    public void display(JTextPane textPane) {
        this.hostPane = textPane;
        textPane.removeAll();
        textPane.setOpaque(false);
        textPane.setBackground(Color.BLACK);
        textPane.setLayout(new BorderLayout());
        for (KeyListener kl : textPane.getKeyListeners()) {
            textPane.removeKeyListener(kl);
        }
        
        if (backgroundPanel == null) {
            backgroundPanel = new SettingBackgroundPanel();
        }
        backgroundPanel.removeAll();
        backgroundPanel.setOpaque(false);
        backgroundPanel.setLayout(new BorderLayout());
        
        JTextPane contentPane = new JTextPane();
        contentPane.setOpaque(false);
        contentPane.setFocusable(true);
        contentPane.setEditable(false);
        contentPane.setForeground(Color.WHITE);
        contentPane.setFont(new Font("Source Code Pro", Font.BOLD, 16));
        // Remove existing key listeners
        for (KeyListener kl : contentPane.getKeyListeners()) {
            contentPane.removeKeyListener(kl);
        }
        contentPane.addKeyListener(new SettingKeyListener());
        this.currentTextPane = contentPane;
        
        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setOpaque(false);
        overlay.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        overlay.add(contentPane, BorderLayout.CENTER);
        
        backgroundPanel.add(overlay, BorderLayout.CENTER);
        textPane.add(backgroundPanel, BorderLayout.CENTER);
        
        drawSettingScreen();
        currentTextPane.requestFocusInWindow();
    }
    
    private void loadBackgroundImage() {
        if (backgroundGif != null || backgroundFallbackImage != null) {
            return;
        }
        try {
            java.net.URL resource = getClass().getResource("/settingbackground.gif");
            if (resource != null) {
                backgroundGif = new ImageIcon(resource);
                return;
            }
        } catch (Exception e) {
            System.out.println("[Setting] GIF 로드 실패: " + e.getMessage());
        }
        
        String[] fallbackPaths = {
            "app/src/main/resources/settingbackground.gif",
            "src/main/resources/settingbackground.gif",
            "settingbackground.gif"
        };
        for (String path : fallbackPaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                try {
                    backgroundGif = new ImageIcon(path);
                    return;
                } catch (Exception e) {
                    System.out.println("[Setting] 경로 로드 실패: " + e.getMessage());
                }
            }
        }
        
        String[] fallbackImages = {"/settingbackground.jpg", "/settingbackground.png"};
        for (String img : fallbackImages) {
            try {
                java.net.URL resource = getClass().getResource(img);
                if (resource != null) {
                    backgroundFallbackImage = javax.imageio.ImageIO.read(resource);
                    return;
                }
            } catch (Exception e) {
                System.out.println("[Setting] 배경 이미지 로드 실패: " + e.getMessage());
            }
        }
    }
    
    private void loadSelectorImage() {
        if (selectorIcon != null) return;
        String[] names = {
            "/settingSelectorImg.png",
            "/settingSelectorImg.gif",
            "/settingSelectorImg.jpg"
        };
        for (String name : names) {
            try {
                java.net.URL resource = getClass().getResource(name);
                if (resource != null) {
                    selectorIcon = scaleSelectorIcon(new ImageIcon(resource));
                    return;
                }
            } catch (Exception ignored) {}
        }
        String[] fallbackPaths = {
            "app/src/main/resources/settingSelectorImg.png",
            "app/src/main/resources/settingSelectorImg.gif",
            "app/src/main/resources/settingSelectorImg.jpg",
            "src/main/resources/settingSelectorImg.png",
            "src/main/resources/settingSelectorImg.gif",
            "src/main/resources/settingSelectorImg.jpg",
            "settingSelectorImg.png",
            "settingSelectorImg.gif",
            "settingSelectorImg.jpg"
        };
        for (String path : fallbackPaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                selectorIcon = scaleSelectorIcon(new ImageIcon(path));
                return;
            }
        }
    }
    
    private ImageIcon scaleSelectorIcon(ImageIcon original) {
        if (original == null) return null;
        Image img = original.getImage().getScaledInstance(
            SELECTOR_ICON_SIZE, SELECTOR_ICON_SIZE, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
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
        
        if (isBattleKeySettingMode) {
            drawBattleKeySettingScreen();
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
                sb.append("[SEL]");
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
                case 3: // 대전모드 키 설정
                    sb.append(" >");
                    break;
                case 4: // 색맹 모드
                    sb.append(": ").append(gameSettings.isColorblindMode() ? "ON" : "OFF");
                    break;
                case 5: // 음향 효과
                    sb.append(": ").append(gameSettings.isSoundEnabled() ? "ON" : "OFF");
                    break;
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
        applySelectorIcons();
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
    
    private void drawBattleKeySettingScreen() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n");
        sb.append("═══════════════════════════════════\n");
        sb.append("       대전모드 키 설정\n");
        sb.append("═══════════════════════════════════\n\n");
        
        if (currentKeyAction.isEmpty()) {
            // 플레이어 선택 또는 키 목록 표시
            if (battleKeyPlayerNum == 0) {
                // 플레이어 선택 화면
                sb.append("\n\n");
                sb.append(currentBattleKeyIndex == 0 ? "  ► " : "    ").append("Player 1 키 설정");
                sb.append(currentBattleKeyIndex == 0 ? " ◄\n\n" : "\n\n");
                sb.append(currentBattleKeyIndex == 1 ? "  ► " : "    ").append("Player 2 키 설정");
                sb.append(currentBattleKeyIndex == 1 ? " ◄\n\n" : "\n\n");
                sb.append("\n");
                sb.append("═══════════════════════════════════\n");
                sb.append("↑↓: 선택  Enter: 진입  ESC: 뒤로\n");
                sb.append("═══════════════════════════════════\n");
            } else {
                // 플레이어별 키 목록 표시
                sb.append("   Player " + battleKeyPlayerNum + " 키 설정\n\n");
                
                for(int i = 0; i < battleKeyActions.length; i++) {
                    if(i == currentBattleKeyIndex) {
                        sb.append("  ► ");
                    } else {
                        sb.append("    ");
                    }
                    
                    String keyName = gameSettings.getKeyName(
                        gameSettings.getPlayerKeyCode(battleKeyPlayerNum, battleKeyActionKeys[i]));
                    sb.append(battleKeyActions[i]).append(": ").append(keyName);
                    
                    if(i == currentBattleKeyIndex) {
                        sb.append(" ◄");
                    }
                    sb.append("\n\n");
                }
                
                sb.append("\n");
                sb.append("═══════════════════════════════════\n");
                sb.append("↑↓: 선택  Enter: 키 변경  ESC: 뒤로\n");
                sb.append("═══════════════════════════════════\n");
            }
        } else {
            // 키 입력 대기 상태
            sb.append("\n\n");
            sb.append("   Player " + battleKeyPlayerNum + " - ");
            sb.append(currentKeyAction).append("키를 설정합니다.\n\n");
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
            
        }
    }
    
    private void applySelectorIcons() {
        if (currentTextPane == null) return;
        StyledDocument doc = currentTextPane.getStyledDocument();
        String text = currentTextPane.getText();
        int idx;
        while ((idx = text.indexOf("[SEL]")) != -1) {
            try {
                doc.remove(idx, "[SEL]".length());
                SimpleAttributeSet iconAttr = new SimpleAttributeSet();
                ImageIcon icon = selectorIcon;
                if (icon == null) {
                    StyleConstants.setIcon(iconAttr, new ImageIcon(new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB)));
                } else {
                    StyleConstants.setIcon(iconAttr, icon);
                }
                doc.insertString(idx, " ", iconAttr);
            } catch (Exception e) {
                break;
            }
            text = currentTextPane.getText();
        }
        highlightSelectedLine();
    }
    
    private void highlightSelectedLine() {
        if (currentTextPane == null) return;
        String target = menuOptions[selectedOption];
        String text = currentTextPane.getText();
        int index = text.indexOf(target);
        if (index >= 0) {
            StyledDocument doc = currentTextPane.getStyledDocument();
            SimpleAttributeSet selectedStyle = new SimpleAttributeSet(styleSet);
            Color highlightColor = gameSettings.isColorblindMode() ? 
                gameSettings.getUIColor("highlight") : Color.GREEN;
            StyleConstants.setForeground(selectedStyle, highlightColor);
            StyleConstants.setBold(selectedStyle, true);
            doc.setCharacterAttributes(index, target.length(), selectedStyle, false);
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
            case 3: // 대전모드 키 설정
                isBattleKeySettingMode = true;
                battleKeyPlayerNum = 0; // 플레이어 선택 화면
                currentBattleKeyIndex = 0;
                drawBattleKeySettingScreen();
                break;
            case 4: // 색맹 모드
                gameSettings.setColorblindMode(!gameSettings.isColorblindMode());
                // 게임 화면의 색상도 업데이트
                updateGameColors();
                drawSettingScreen();
                break;
            case 5: // 음향 효과
                gameSettings.setSoundEnabled(!gameSettings.isSoundEnabled());
                // BGM 제어
                controlBGM();
                drawSettingScreen();
                break;
            case 6: // 스코어 초기화
                gameSettings.resetScores();
                showConfirmation("스코어가 초기화되었습니다!");
                break;
            case 7: // 기본 설정 복원
                gameSettings.setDefaultSettings();
                initializeCurrentSettings();
                showConfirmation("기본 설정으로 복원되었습니다!");
                break;
            case 8: // 뒤로 가기
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
            currentTextPane.setOpaque(false);
            currentTextPane.setBackground(new Color(0, 0, 0, 0));
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
        isConfirmationMode = true;
        
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
        
        // 기존 타이머가 있으면 취소
        if (confirmationTimer != null) {
            confirmationTimer.interrupt();
        }
        
        // 3초 후 자동으로 설정 화면으로 돌아가기
        confirmationTimer = new Thread(() -> {
            try {
                Thread.sleep(3000);
                if (isConfirmationMode) {
                    isConfirmationMode = false;
                    javax.swing.SwingUtilities.invokeLater(() -> drawSettingScreen());
                }
            } catch (InterruptedException e) {
                // 타이머가 중단됨 (키 입력으로 즉시 복귀)
            }
        });
        confirmationTimer.start();
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
        String[] actions = {"down", "left", "right", "rotate", "drop", "pause", "item"};
        String[] actionNames = {"아래", "왼쪽", "오른쪽", "회전", "빠른낙하", "일시정지", "아이템 사용"};
        
        for (int i = 0; i < actions.length; i++) {
            if (!actions[i].equals(currentAction)) {
                if (gameSettings.getKeyCode(actions[i]) == keyCode) {
                    return actionNames[i];
                }
            }
        }
        return null;
    }
    
    private String findBattleConflictingAction(int playerNum, int keyCode, String currentAction) {
        String[] actions = {"down", "left", "right", "rotate", "drop", "item"};
        String[] actionNames = {"아래", "왼쪽", "오른쪽", "회전", "빠른낙하", "아이템 사용"};
        
        for (int i = 0; i < actions.length; i++) {
            if (!actions[i].equals(currentAction)) {
                if (gameSettings.getPlayerKeyCode(playerNum, actions[i]) == keyCode) {
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
            
            // 확인 모드일 때 아무 키나 누르면 즉시 복귀
            if (isConfirmationMode) {
                isConfirmationMode = false;
                if (confirmationTimer != null) {
                    confirmationTimer.interrupt();
                    confirmationTimer = null;
                }
                drawSettingScreen();
                return;
            }
            
            if (currentKeyAction.isEmpty()) {
                // 일반 메뉴 모드
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (isBattleKeySettingMode) {
                            if (battleKeyPlayerNum == 0) {
                                // 플레이어 선택 화면
                                currentBattleKeyIndex = (currentBattleKeyIndex - 1 + 2) % 2;
                            } else {
                                // 키 목록 화면
                                currentBattleKeyIndex = (currentBattleKeyIndex - 1 + battleKeyActions.length) % battleKeyActions.length;
                            }
                            drawBattleKeySettingScreen();
                        } else if (isKeySettingMode) {
                            currentKeyIndex = (currentKeyIndex - 1 + keyActions.length) % keyActions.length;
                            drawKeySettingScreen();
                        } else {
                            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
                            drawSettingScreen();
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (isBattleKeySettingMode) {
                            if (battleKeyPlayerNum == 0) {
                                // 플레이어 선택 화면
                                currentBattleKeyIndex = (currentBattleKeyIndex + 1) % 2;
                            } else {
                                // 키 목록 화면
                                currentBattleKeyIndex = (currentBattleKeyIndex + 1) % battleKeyActions.length;
                            }
                            drawBattleKeySettingScreen();
                        } else if (isKeySettingMode) {
                            currentKeyIndex = (currentKeyIndex + 1) % keyActions.length;
                            drawKeySettingScreen();
                        } else {
                            selectedOption = (selectedOption + 1) % menuOptions.length;
                            drawSettingScreen();
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!isKeySettingMode && !isBattleKeySettingMode) {
                            handleLeftRight(false);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (!isKeySettingMode && !isBattleKeySettingMode) {
                            handleLeftRight(true);
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (isBattleKeySettingMode) {
                            if (battleKeyPlayerNum == 0) {
                                // 플레이어 선택
                                battleKeyPlayerNum = currentBattleKeyIndex + 1;
                                currentBattleKeyIndex = 0;
                                drawBattleKeySettingScreen();
                            } else {
                                // 키 입력 대기
                                currentKeyAction = battleKeyActions[currentBattleKeyIndex];
                                drawBattleKeySettingScreen();
                            }
                        } else if (isKeySettingMode) {
                            currentKeyAction = keyActions[currentKeyIndex];
                            drawKeySettingScreen();
                        } else {
                            handleMenuAction();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (isBattleKeySettingMode) {
                            if (battleKeyPlayerNum == 0) {
                                // 플레이어 선택 화면에서 ESC - 메인 설정으로
                                isBattleKeySettingMode = false;
                                drawSettingScreen();
                            } else {
                                // 키 목록 화면에서 ESC - 플레이어 선택으로
                                battleKeyPlayerNum = 0;
                                currentBattleKeyIndex = 0;
                                drawBattleKeySettingScreen();
                            }
                        } else if (isKeySettingMode) {
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
                    if (isBattleKeySettingMode) {
                        drawBattleKeySettingScreen();
                    } else {
                        drawKeySettingScreen();
                    }
                } else {
                    // 새로운 키 설정
                    int newKeyCode = e.getKeyCode();
                    
                    // 설정 인터페이스에 필수적인 키만 제한
                    if (newKeyCode == KeyEvent.VK_ESCAPE || newKeyCode == KeyEvent.VK_ENTER) {
                        // 제한된 키에 대한 경고 메시지
                        showKeyWarning("ESC와 Enter키는 설정할 수 없습니다.");
                        return;
                    }
                    
                    if (isBattleKeySettingMode) {
                        // 대전모드 키 설정
                        String conflictAction = findBattleConflictingAction(battleKeyPlayerNum, newKeyCode, 
                            battleKeyActionKeys[currentBattleKeyIndex]);
                        if (conflictAction != null) {
                            showKeyConflictWarning(conflictAction, gameSettings.getKeyName(newKeyCode));
                        }
                        
                        gameSettings.setPlayerKeyCode(battleKeyPlayerNum, 
                            battleKeyActionKeys[currentBattleKeyIndex], newKeyCode);
                        currentKeyAction = "";
                        drawBattleKeySettingScreen();
                    } else {
                        // 싱글 플레이 키 설정
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
        }

        @Override
        public void keyReleased(KeyEvent e) {
            e.consume(); // 이벤트 소비
        }
    }
    
    private class SettingBackgroundPanel extends JPanel {
        SettingBackgroundPanel() {
            setOpaque(true);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            if (backgroundGif != null) {
                g2d.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            } else if (backgroundFallbackImage != null) {
                g2d.drawImage(backgroundFallbackImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(20, 20, 40),
                    getWidth(), getHeight(), new Color(10, 10, 20)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            g2d.dispose();
        }
    }
}
