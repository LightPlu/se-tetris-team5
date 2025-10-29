package se.tetris.team5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.net.URL;

import se.tetris.team5.screens.home;
import se.tetris.team5.screens.score;
import se.tetris.team5.screens.setting;
import se.tetris.team5.screens.game;
import se.tetris.team5.utils.setting.GameSettings;
import se.tetris.team5.components.home.BGMManager;

public class ScreenController extends JFrame {
    private JTextPane textPane;
    private String currentScreen = "loading"; // 로딩 화면부터 시작
    
    // Screen instances
    private home homeScreen;
    private score scoreScreen;
    private setting settingScreen;
    private game gameScreen;
    
    // Loading screen components
    private JLabel loadingBackgroundLabel;
    private Timer loadingTimer;
    private JPanel fadeOverlay;
    private Timer fadeTimer;
    private float fadeAlpha = 0.0f;
    
    // BGM manager
    private BGMManager bgmManager;
    
    public ScreenController() {
        initializeFrame();
        initializeScreens();
        bgmManager = BGMManager.getInstance();
        showLoadingScreen(); // 로딩 화면부터 시작
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
        
        // 페이드 효과 관련 정리
        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }
        fadeAlpha = 0.0f;
        fadeOverlay = null;
        
        // 기존 컨텐트 제거
        getContentPane().removeAll();
        
        // 이전 화면의 KeyListener 제거
        for (KeyListener kl : textPane.getKeyListeners()) {
            textPane.removeKeyListener(kl);
        }
        
        switch(screenName) {
            case "loading":
                showLoadingScreen();
                return; // 로딩 화면은 별도 처리이므로 return
            case "home":
                // 메인 BGM 재생
                bgmManager.playMainBGM();
                
                // Ensure textPane is cleared of any child components left from previous screens
                textPane.removeAll();
                getContentPane().add(textPane);
                homeScreen.display(textPane);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    textPane.requestFocusInWindow();
                });
                break;
            case "game":
                // 게임 화면에서는 BGM 정지 (게임 자체 BGM 사용)
                bgmManager.stopBGM();
                
                getContentPane().add(gameScreen);
                gameScreen.reset();
                // macOS 대응: 여러 번 포커스 요청 (딜레이 포함)
                javax.swing.SwingUtilities.invokeLater(() -> {
                    gameScreen.requestFocusInWindow();
                    // 2차 딜레이 포커스
                    new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                javax.swing.SwingUtilities.invokeLater(() -> gameScreen.requestFocusInWindow());
                            }
                        },
                        150
                    );
                });
                break;
            case "score":
                // Clear any child components so score can rebuild its UI cleanly
                textPane.removeAll();
                getContentPane().add(textPane);
                scoreScreen.display(textPane);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    textPane.requestFocusInWindow();
                });
                break;
            case "setting":
                // Clear previous components to avoid visual artifacts (e.g., leftover scoreboard panel)
                textPane.removeAll();
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
    
    /**
     * 로딩 화면을 표시합니다
     */
    private void showLoadingScreen() {
        currentScreen = "loading";
        
        // 기존 컨텐트 제거
        getContentPane().removeAll();
        
        // 로딩 BGM 재생
        bgmManager.playLoadingBGM();
        
        // 로딩 배경 설정
        setupLoadingBackground();
        
        // 5초 후 페이드아웃 시작 (4초 GIF + 1초 페이드아웃)
        loadingTimer = new Timer(4000, e -> {
            ((Timer) e.getSource()).stop();
            startFadeOut();
        });
        loadingTimer.setRepeats(false);
        loadingTimer.start();
        
        revalidate();
        repaint();
    }
    
    /**
     * 로딩 배경을 설정합니다
     */
    private void setupLoadingBackground() {
        try {
            // background1.gif 로드
            URL gifUrl = getClass().getClassLoader().getResource("background1.gif");
            if (gifUrl != null) {
                ImageIcon backgroundGif = new ImageIcon(gifUrl);
                
                // 창 크기에 맞게 GIF 크기 조정
                Image scaledImage = backgroundGif.getImage().getScaledInstance(
                    getWidth(), getHeight(), Image.SCALE_DEFAULT);
                ImageIcon scaledGif = new ImageIcon(scaledImage);
                
                loadingBackgroundLabel = new JLabel(scaledGif);
                loadingBackgroundLabel.setHorizontalAlignment(SwingConstants.CENTER);
                loadingBackgroundLabel.setVerticalAlignment(SwingConstants.CENTER);
                
                getContentPane().add(loadingBackgroundLabel, BorderLayout.CENTER);
                System.out.println("Loading screen background (background1.gif) loaded successfully");
            } else {
                // 기본 배경 설정
                JPanel gradientPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        GradientPaint gradient = new GradientPaint(
                            0, 0, new Color(20, 20, 40),
                            0, getHeight(), new Color(40, 20, 60)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
                getContentPane().add(gradientPanel, BorderLayout.CENTER);
                System.out.println("background1.gif not found, using default gradient background");
            }
        } catch (Exception e) {
            System.out.println("Error loading background1.gif: " + e.getMessage());
            // 기본 검정 배경
            JPanel blackPanel = new JPanel();
            blackPanel.setBackground(Color.BLACK);
            getContentPane().add(blackPanel, BorderLayout.CENTER);
        }
    }
    
    /**
     * 페이드아웃 효과를 시작합니다
     */
    private void startFadeOut() {
        // 페이드 오버레이 패널 생성
        fadeOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        fadeOverlay.setOpaque(false);
        fadeOverlay.setBounds(0, 0, getWidth(), getHeight());
        
        // 기존 로딩 화면 위에 페이드 오버레이 추가
        if (loadingBackgroundLabel != null) {
            loadingBackgroundLabel.add(fadeOverlay);
            loadingBackgroundLabel.setComponentZOrder(fadeOverlay, 0);
        } else {
            getContentPane().add(fadeOverlay, BorderLayout.CENTER);
        }
        
        // 페이드 애니메이션 타이머 (1초간 페이드아웃)
        fadeTimer = new Timer(60, e -> {
            fadeAlpha += 0.05f; // 30ms마다 0.05씩 증가 (약 600ms에 완료)
            
            if (fadeAlpha >= 1.0f) {
                fadeAlpha = 1.0f;
                ((Timer) e.getSource()).stop();
                
                // 페이드아웃 완료 후 홈 화면으로 전환
                SwingUtilities.invokeLater(() -> {
                    showScreen("home");
                });
            }
            
            // 페이드 오버레이 다시 그리기
            if (fadeOverlay != null) {
                fadeOverlay.repaint();
            }
        });
        fadeTimer.start();
    }
}