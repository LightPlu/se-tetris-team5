package se.tetris.team5;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.event.KeyListener;

import se.tetris.team5.screens.home;
import se.tetris.team5.screens.score;
import se.tetris.team5.screens.setting;
import se.tetris.team5.screens.game;
import se.tetris.team5.utils.setting.GameSettings;

public class ScreenController extends JFrame {
    private JTextPane textPane;
    private String currentScreen = "home";
    
    // Screen instances
    private home homeScreen;
    private score scoreScreen;
    private setting settingScreen;
    private game gameScreen;
    
    public ScreenController() {
        initializeFrame();
        initializeScreens();
        showScreen("home");
    }
    
    private void initializeFrame() {
        setTitle("TETRIS - Team 5");
        
        // GameSettings에서 창 크기 가져오기
        GameSettings settings = GameSettings.getInstance();
        setSize(settings.getWindowWidth(), settings.getWindowHeight());
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(Color.BLACK);
        textPane.setForeground(Color.WHITE);
        // ScreenController 자체의 KeyListener는 제거 - 각 화면이 직접 관리
        textPane.setFocusable(true);
        
        // JTextPane의 기본 키 바인딩 비활성화 (화살표 키 충돌 방지)
        disableDefaultKeyBindings(textPane);
        
        add(textPane);
        setVisible(true);
    }
    
    /**
     * JTextPane의 기본 키 바인딩을 비활성화하여 KeyListener가 제대로 작동하도록 합니다
     */
    private void disableDefaultKeyBindings(JTextPane textPane) {
        // 화살표 키와 기타 네비게이션 키의 기본 동작 제거
        javax.swing.InputMap inputMap = textPane.getInputMap();
        javax.swing.ActionMap actionMap = textPane.getActionMap();
        
        // 비활성화할 키 목록
        String[] keys = {
            "UP", "DOWN", "LEFT", "RIGHT",
            "KP_UP", "KP_DOWN", "KP_LEFT", "KP_RIGHT",
            "ENTER", "SPACE", "ESCAPE",
            "PAGE_UP", "PAGE_DOWN",
            "HOME", "END"
        };
        
        for (String key : keys) {
            javax.swing.KeyStroke keyStroke = javax.swing.KeyStroke.getKeyStroke(key);
            if (keyStroke != null) {
                inputMap.put(keyStroke, "none");
            }
            // shift, ctrl, alt 조합도 비활성화
            keyStroke = javax.swing.KeyStroke.getKeyStroke("shift " + key);
            if (keyStroke != null) {
                inputMap.put(keyStroke, "none");
            }
            keyStroke = javax.swing.KeyStroke.getKeyStroke("ctrl " + key);
            if (keyStroke != null) {
                inputMap.put(keyStroke, "none");
            }
        }
        
        // "none" 액션 등록
        actionMap.put("none", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 아무 동작도 하지 않음
            }
        });
    }
    
    private void initializeScreens() {
        homeScreen = new home(this);
        scoreScreen = new score(this);
        settingScreen = new setting(this);
        gameScreen = new game(this);
    }
    
    public void showScreen(String screenName) {
        currentScreen = screenName;
        
        // 기존 컨텐트 제거
        getContentPane().removeAll();
        
        // 이전 화면의 KeyListener 제거
        for (KeyListener kl : textPane.getKeyListeners()) {
            textPane.removeKeyListener(kl);
        }
        
        switch(screenName) {
            case "home":
                getContentPane().add(textPane);
                homeScreen.display(textPane);
                // 약간의 지연 후 포커스 설정 (렌더링 완료 대기)
                javax.swing.SwingUtilities.invokeLater(() -> {
                    textPane.requestFocusInWindow();
                });
                break;
            case "game":
                // game은 JPanel이므로 직접 추가
                getContentPane().add(gameScreen);
                gameScreen.reset(); // 게임 상태 리셋
                javax.swing.SwingUtilities.invokeLater(() -> {
                    gameScreen.requestFocusInWindow();
                });
                break;
            case "score":
                getContentPane().add(textPane);
                scoreScreen.display(textPane);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    textPane.requestFocusInWindow();
                });
                break;
            case "setting":
                getContentPane().add(textPane);
                settingScreen.display(textPane);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    textPane.requestFocusInWindow();
                });
                break;
            default:
                getContentPane().add(textPane);
                homeScreen.display(textPane);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    textPane.requestFocusInWindow();
                });
                break;
        }
        
        revalidate();
        repaint();
    }
    
    private void showPlaceholder(JTextPane textPane, String screenName) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n");
        sb.append("════════════════════════════════════════\n");
        sb.append("         ").append(screenName).append("         \n");
        sb.append("════════════════════════════════════════\n\n");
        sb.append("곧 구현될 예정입니다!\n\n");
        sb.append("ESC: 홈으로 돌아가기\n\n");
        sb.append("════════════════════════════════════════\n");
        
        textPane.setText(sb.toString());
    }
    
    public String getCurrentScreen() {
        return currentScreen;
    }
    
    public JTextPane getTextPane() {
        return textPane;
    }
    
    public void updateWindowSize() {
        GameSettings settings = GameSettings.getInstance();
        setSize(settings.getWindowWidth(), settings.getWindowHeight());
        setLocationRelativeTo(null);
    }
}