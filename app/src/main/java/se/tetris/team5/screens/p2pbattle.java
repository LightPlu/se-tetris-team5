package se.tetris.team5.screens;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import se.tetris.team5.ScreenController;
import se.tetris.team5.gamelogic.p2p.*;
import se.tetris.team5.components.battle.PlayerGamePanel;
import se.tetris.team5.gamelogic.GameMode;
import se.tetris.team5.gamelogic.battle.BattleGameController;
import se.tetris.team5.gamelogic.input.Player1InputHandler;

/**
 * P2P 대전 모드 화면
 * - 서버/클라이언트 선택
 * - IP 입력 (클라이언트)
 * - 게임 모드 선택 (서버)
 * - 게임 시작 및 실시간 동기화
 */
public class p2pbattle extends JPanel implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            // Not used
        }
    
    private static final long serialVersionUID = 1L;
    
    // 화면 상태
    private enum ScreenState {
        ROLE_SELECTION,      // 서버/클라이언트 선택
        SERVER_WAITING,      // 서버 대기 중
        CLIENT_CONNECTING,   // 클라이언트 연결 중
        MODE_SELECTION,      // 게임 모드 선택 (서버)
        READY_WAITING,       // 준비 대기
        PLAYING,             // 게임 중
        GAME_OVER            // 게임 오버
    }
    
    private ScreenController screenController;
    private ScreenState currentState;
    
    // 네트워크
    private P2PServer server;
    private P2PClient client;
    private boolean isServer;
    
    // UI 컴포넌트
    private JPanel mainPanel;
    private JLabel statusLabel;
    private JLabel latencyLabel;  // 실시간 레이턴시 표시
    private Timer latencyUpdateTimer;  // 레이턴시 업데이트 타이머
    private JLabel lobbyLatencyLabel;
    private Timer lobbyLatencyTimer;
    private boolean isLaggingNetwork = false;
    private ImageIcon backgroundGif;
    private Image backgroundFallbackImage;
    
    // 역할 선택 화면
    private JButton serverButton;
    private JButton clientButton;
    private JButton backButton;
    
    // 클라이언트 연결 화면
    private JTextField ipField;
    private JButton connectButton;
    private JComboBox<String> recentIPComboBox;
    
    // 게임 모드 선택 화면 (서버)
    private JButton normalModeButton;
    private JButton itemModeButton;
    private JButton timeLimitModeButton;
    private String selectedBattleMode = "NORMAL";
    
    // 준비 대기 화면
    private JButton readyButton;
    private JLabel readyStatusLabel;
    private boolean isReady = false;
    private boolean opponentReady = false;
    private JTextArea chatArea;
    private JTextField chatInputField;
    private JButton chatSendButton;
    
    // 게임 화면
    private PlayerGamePanel myPanel;
    private PlayerGamePanel opponentPanel;
    private BattleGameController gameController;
    private se.tetris.team5.gamelogic.input.SinglePlayerInputHandler myInputHandler;
    
    private static final int TIME_LIMIT_SECONDS = 300;
    private static final long LAG_WARNING_THRESHOLD_MS = 200;
    private Timer timeLimitTimer;
    private int remainingSeconds;
    
    // 블록 생성 동기화용 랜덤 시드
    private long gameRandomSeed;
    
    private String originalWindowSize;
    private WindowFocusListener windowFocusListener;
    
    public p2pbattle(ScreenController screenController) {
        this.screenController = screenController;
        this.currentState = ScreenState.ROLE_SELECTION;
        
        // 원래 창 크기 저장
        se.tetris.team5.utils.setting.GameSettings settings = 
            se.tetris.team5.utils.setting.GameSettings.getInstance();
        this.originalWindowSize = settings.getWindowSize();
        
        loadBackgroundImage();
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        initializeUI();
        setupWindowFocusHandling();
    }
    
    private void loadBackgroundImage() {
        try {
            java.net.URL resource = getClass().getResource("/background3.gif");
            if (resource != null) {
                backgroundGif = new ImageIcon(resource);
                System.out.println("[P2P] background3.gif loaded");
                return;
            }
        } catch (Exception e) {
            System.out.println("[P2P] 배경 GIF 로드 실패: " + e.getMessage());
        }
        
        String[] fallbackPaths = {
            "app/src/main/resources/background3.gif",
            "src/main/resources/background3.gif",
            "background3.gif"
        };
        for (String path : fallbackPaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                try {
                    backgroundGif = new ImageIcon(path);
                    System.out.println("[P2P] background3.gif loaded from path: " + path);
                    return;
                } catch (Exception e) {
                    System.out.println("[P2P] 경로 로드 실패: " + e.getMessage());
                }
            }
        }
        
        String[] fallbackImages = { "/background3.png", "/background3.jpg" };
        for (String img : fallbackImages) {
            try {
                java.net.URL resource = getClass().getResource(img);
                if (resource != null) {
                    backgroundFallbackImage = javax.imageio.ImageIO.read(resource);
                    return;
                }
            } catch (Exception e) {
                System.out.println("[P2P] fallback 이미지 로드 실패: " + e.getMessage());
            }
        }
    }
    
    /**
     * UI 초기화
     */
    private void initializeUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        
        // 상태 라벨
        statusLabel = new JLabel("P2P 대전 모드", SwingConstants.CENTER);
        statusLabel.setFont(createKoreanFont(Font.BOLD, 24));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 0, 0, 180));
        topPanel.add(statusLabel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        showRoleSelection();
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
                0, 0, new Color(10, 10, 20),
                getWidth(), getHeight(), Color.BLACK);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        g2d.dispose();
    }
    
    /**
     * 역할 선택 화면 (서버/클라이언트)
     */
    private void showRoleSelection() {
        currentState = ScreenState.ROLE_SELECTION;
        statusLabel.setText("P2P 대전 모드 - 역할 선택");
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0, 0, 0, 180));
        
        serverButton = createStyledButton("서버로 시작", new Color(100, 200, 255));
        clientButton = createStyledButton("클라이언트로 접속", new Color(100, 255, 200));
        backButton = createStyledButton("돌아가기", new Color(200, 200, 200));
        
        serverButton.addActionListener(e -> startAsServer());
        clientButton.addActionListener(e -> showClientConnection());
        backButton.addActionListener(e -> returnToHome());
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(createCenteredComponent(serverButton));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createCenteredComponent(clientButton));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createCenteredComponent(backButton));
        centerPanel.add(Box.createVerticalGlue());
        
        updateMainPanel(centerPanel);
    }

    /**
     * 서버 대기 화면에서 보여줄 IP 안내 문자열 생성
     */
    private String getServerIPInfoText() {
        if (server == null) {
            return "<html><div style='text-align:center;'>LAN 접속: 알 수 없음<br>로컬 테스트: 127.0.0.1</div></html>";
        }

        java.util.List<String> reachableIPs = server.getReachableIPs();
        java.util.List<String> lanIPs = new java.util.ArrayList<>();
        String loopbackIP = "127.0.0.1";

        for (String ip : reachableIPs) {
            if ("127.0.0.1".equals(ip)) {
                loopbackIP = ip;
            } else {
                lanIPs.add(ip);
            }
        }

        String lanText = lanIPs.isEmpty() ? "알 수 없음" : String.join(", ", lanIPs);
        return "<html><div style='text-align:center;'>LAN 접속: " + lanText + "<br>로컬 테스트: " + loopbackIP + "</div></html>";
    }
    
    /**
     * 서버로 시작
     */
    private void startAsServer() {
        isServer = true;
        currentState = ScreenState.SERVER_WAITING;
        
        server = new P2PServer(new P2PServer.P2PEventListener() {
            @Override
            public void onClientConnected(String clientAddress) {
                SwingUtilities.invokeLater(() -> {
                    saveRecentIP(clientAddress);
                    JOptionPane.showMessageDialog(
                        p2pbattle.this,
                        "클라이언트가 연결되었습니다!\nIP: " + clientAddress,
                        "연결 성공",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    showModeSelection();
                });
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                handlePacketReceived(packet);
            }
            
            @Override
            public void onDisconnected(String reason) {
                SwingUtilities.invokeLater(() -> handleDisconnection(reason));
            }
            
            @Override
            public void onLagDetected(boolean isLagging) {
                SwingUtilities.invokeLater(() -> handleLagDetectionEvent(isLagging));
            }
            
            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> showError(error));
            }
        });
        
        server.start();
        showServerWaiting();
    }
    
    /**
     * 서버 대기 화면
     */
    private void showServerWaiting() {
        statusLabel.setText("P2P 대전 - 클라이언트 연결 대기 중");
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0, 0, 0, 180));
        
        JLabel ipLabel = new JLabel(getServerIPInfoText());
        ipLabel.setFont(createKoreanFont(Font.BOLD, 18));
        ipLabel.setForeground(Color.YELLOW);
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel waitingLabel = new JLabel("클라이언트의 접속을 기다리는 중...");
        waitingLabel.setFont(createKoreanFont(Font.PLAIN, 16));
        waitingLabel.setForeground(Color.WHITE);
        waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton cancelButton = createStyledButton("취소", new Color(255, 100, 100));
        cancelButton.addActionListener(e -> {
            if (server != null) {
                server.close();
            }
            showRoleSelection();
        });
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(ipLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(waitingLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(createCenteredComponent(cancelButton));
        centerPanel.add(Box.createVerticalGlue());
        
        updateMainPanel(centerPanel);
    }
    
    /**
     * 클라이언트 연결 화면
     */
    private void showClientConnection() {
        currentState = ScreenState.CLIENT_CONNECTING;
        statusLabel.setText("P2P 대전 - 서버 접속");
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0, 0, 0, 180));
        
        JLabel instructionLabel = new JLabel("서버 IP 주소를 입력하세요");
        instructionLabel.setFont(createKoreanFont(Font.PLAIN, 16));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 최근 접속 IP 목록
        String[] recentIPs = loadRecentIPs();
        recentIPComboBox = new JComboBox<>(recentIPs);
        recentIPComboBox.setMaximumSize(new Dimension(300, 30));
        recentIPComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        ipField = new JTextField(15);
        ipField.setMaximumSize(new Dimension(300, 30));
        ipField.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipField.setHorizontalAlignment(JTextField.CENTER);
        ipField.setFont(createKoreanFont(Font.PLAIN, 14));
        
        // 콤보박스에서 선택 시 텍스트 필드에 자동 입력
        recentIPComboBox.addActionListener(e -> {
            String selected = (String) recentIPComboBox.getSelectedItem();
            if (selected != null && !selected.equals("최근 접속 기록 없음")) {
                ipField.setText(selected);
            }
        });
        
        connectButton = createStyledButton("연결", new Color(100, 255, 200));
        connectButton.addActionListener(e -> connectToServer());
        
        JButton cancelButton = createStyledButton("취소", new Color(200, 200, 200));
        cancelButton.addActionListener(e -> showRoleSelection());
        
        // Enter 키로 연결
        ipField.addActionListener(e -> connectToServer());
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(instructionLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        
        if (recentIPs.length > 0 && !recentIPs[0].equals("최근 접속 기록 없음")) {
            JLabel recentLabel = new JLabel("최근 접속 IP:");
            recentLabel.setFont(createKoreanFont(Font.PLAIN, 12));
            recentLabel.setForeground(Color.LIGHT_GRAY);
            recentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(recentLabel);
            centerPanel.add(Box.createVerticalStrut(5));
            centerPanel.add(recentIPComboBox);
            centerPanel.add(Box.createVerticalStrut(15));
        }
        
        centerPanel.add(ipField);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createCenteredComponent(connectButton));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(createCenteredComponent(cancelButton));
        centerPanel.add(Box.createVerticalGlue());
        
        updateMainPanel(centerPanel);
        
        // 포커스를 IP 입력 필드로
        SwingUtilities.invokeLater(() -> ipField.requestFocusInWindow());
    }
    
    /**
     * 서버에 연결
     */
    private void connectToServer() {
        String serverIP = ipField.getText().trim();
        if (serverIP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "IP 주소를 입력해주세요", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        connectToServer(serverIP);
    }
    
    /**
     * 서버 연결 (IP 지정)
     */
    private void connectToServer(String serverIP) {
        isServer = false;
        if (connectButton != null) {
            connectButton.setEnabled(false);
        }
        statusLabel.setText("서버에 연결 중...");
        
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {
                SwingUtilities.invokeLater(() -> {
                    saveRecentIP(serverAddress);
                    JOptionPane.showMessageDialog(
                        p2pbattle.this,
                        "서버에 연결되었습니다!\n서버 IP: " + serverAddress,
                        "연결 성공",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    showReadyWaiting();
                });
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                handlePacketReceived(packet);
            }
            
            @Override
            public void onDisconnected(String reason) {
                SwingUtilities.invokeLater(() -> handleDisconnection(reason));
            }
            
            @Override
            public void onLagDetected(boolean isLagging) {
                SwingUtilities.invokeLater(() -> handleLagDetectionEvent(isLagging));
            }
            
            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> {
                    showError("연결 오류: " + error);
                    if (connectButton != null) {
                        connectButton.setEnabled(true);
                    }
                });
            }
        });
        
        client.connect(serverIP);
    }
    
    /**
     * 게임 모드 선택 화면 (서버만)
     */
    private void showModeSelection() {
        currentState = ScreenState.MODE_SELECTION;
        statusLabel.setText("P2P 대전 - 게임 모드 선택");
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0, 0, 0, 180));
        
        JLabel infoLabel = new JLabel("게임 모드를 선택하세요 (서버 권한)");
        infoLabel.setFont(createKoreanFont(Font.PLAIN, 16));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        normalModeButton = createStyledButton("일반 대전", new Color(100, 200, 255));
        itemModeButton = createStyledButton("아이템 대전", new Color(255, 200, 100));
        timeLimitModeButton = createStyledButton("시간제한 대전", new Color(255, 100, 100));
        
        normalModeButton.addActionListener(e -> selectMode("NORMAL"));
        itemModeButton.addActionListener(e -> selectMode("ITEM"));
        timeLimitModeButton.addActionListener(e -> selectMode("TIMELIMIT"));
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(createCenteredComponent(normalModeButton));
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(createCenteredComponent(itemModeButton));
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(createCenteredComponent(timeLimitModeButton));
        centerPanel.add(Box.createVerticalGlue());
        
        updateMainPanel(centerPanel);
    }
    
    /**
     * 모드 선택 (서버)
     */
    private void selectMode(String mode) {
        selectedBattleMode = mode;
        
        // 블록 생성 동기화를 위한 랜덤 시드 생성
        gameRandomSeed = System.currentTimeMillis();
        
        // 클라이언트에게 모드와 시드 전송
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.GAME_MODE_SELECT);
        packet.setBattleMode(selectedBattleMode);
        packet.setRandomSeed(gameRandomSeed);
        
        if (server != null) {
            server.sendPacket(packet);
        }
        
        showReadyWaiting();
    }
    
    /**
     * 준비 대기 화면
     */
    private void showReadyWaiting() {
        currentState = ScreenState.READY_WAITING;
        isReady = false;
        opponentReady = false;
        stopLobbyLatencyMonitor();
        chatArea = null;
        chatInputField = null;
        chatSendButton = null;
        
        updateReadyScreenModeLabel();
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0, 0, 0, 180));
        
        JLabel infoLabel = new JLabel("양쪽 플레이어가 준비를 완료하면 게임이 시작됩니다");
        infoLabel.setFont(createKoreanFont(Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        readyStatusLabel = new JLabel(getReadyStatusText());
        readyStatusLabel.setFont(createKoreanFont(Font.BOLD, 16));
        readyStatusLabel.setForeground(Color.YELLOW);
        readyStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        readyButton = createStyledButton("게임 시작", new Color(100, 255, 100));
        readyButton.addActionListener(e -> handleGameStartRequest());
        
        JButton cancelButton = createStyledButton("연결 종료", new Color(255, 100, 100));
        cancelButton.addActionListener(e -> disconnect());
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(readyStatusLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createLobbyLatencyPanel());
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createCenteredComponent(readyButton));
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(createCenteredComponent(cancelButton));
        centerPanel.add(Box.createVerticalGlue());
        
        updateMainPanel(centerPanel);
        startLobbyLatencyMonitor();
    }

    /**
     * 준비 화면 상단의 모드 안내 텍스트 갱신
     */
    private void updateReadyScreenModeLabel() {
        if (statusLabel != null && currentState == ScreenState.READY_WAITING) {
            String modeText = getBattleModeText(selectedBattleMode);
            statusLabel.setText("P2P 대전 - 준비 대기 (" + modeText + ")");
        }
    }
    
    /**
     * 게임 시작 버튼 처리 (각 플레이어가 한 번씩 눌러야 게임 시작)
     */
    private void handleGameStartRequest() {
        if (isReady) {
            return;
        }
        
        isReady = true;
        
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.READY);
        packet.setMessage("ready");
        
        if (isServer && server != null) {
            server.sendPacket(packet);
        } else if (!isServer && client != null) {
            client.sendPacket(packet);
        }
        
        updateReadyStatus();
        
        if (isReady && opponentReady) {
            startGame();
        }
    }
    
    /**
     * 준비 상태 업데이트
     */
    private void updateReadyStatus() {
        if (readyStatusLabel != null) {
            readyStatusLabel.setText(getReadyStatusText());
        }
        if (readyButton != null) {
            if (isReady) {
                readyButton.setText("대기 중...");
                readyButton.setBackground(new Color(180, 180, 180));
                readyButton.setEnabled(false);
            } else {
                readyButton.setText("게임 시작");
                readyButton.setBackground(new Color(100, 255, 100));
                readyButton.setEnabled(true);
            }
        }
    }
    
    /**
     * 준비 상태 텍스트
     */
    private String getReadyStatusText() {
        String myStatus = isReady ? "준비 완료" : "준비 중";
        String opponentStatus = opponentReady ? "준비 완료" : "준비 중";
        return String.format("나: %s | 상대: %s", myStatus, opponentStatus);
    }
    
    /**
     * 게임 시작
     */
    private void startGame() {
        stopLobbyLatencyMonitor();
        currentState = ScreenState.PLAYING;
        
        // 창 크기를 2배로 확장
        se.tetris.team5.utils.setting.GameSettings settings = 
            se.tetris.team5.utils.setting.GameSettings.getInstance();
        int originalWidth = settings.getWindowWidth();
        int originalHeight = settings.getWindowHeight();
        settings.setCustomWindowSize(originalWidth * 2, originalHeight);
        screenController.updateWindowSize();
        
        // 게임 화면 초기화
        initializeGameScreen();
        
        // 게임 시작 패킷 전송
        GameStatePacket startPacket = new GameStatePacket(GameStatePacket.PacketType.GAME_START);
        if (isServer && server != null) {
            server.sendPacket(startPacket);
        } else if (!isServer && client != null) {
            client.sendPacket(startPacket);
        }
    }
    
    /**
     * 게임 화면 초기화
     */
    private void initializeGameScreen() {
        removeAll();
        setLayout(new BorderLayout());
        
        // 플레이어 패널 생성 (P2P는 각자 싱글 플레이 키 사용)
        myPanel = new PlayerGamePanel(
            isServer ? "서버" : "클라이언트",
            "화살표 + Space",  // 싱글 플레이 기본 키
            isServer ? new Color(100, 200, 255) : new Color(255, 200, 100)
        );
        
        // 상대방 패널은 P2P 전용 게임 엔진 사용 (네트워크 상태 주입용)
        se.tetris.team5.gamelogic.P2PGameEngine opponentEngine = 
            new se.tetris.team5.gamelogic.P2PGameEngine(
                se.tetris.team5.components.game.GameBoard.HEIGHT,
                se.tetris.team5.components.game.GameBoard.WIDTH
            );
        
        opponentPanel = new PlayerGamePanel(
            isServer ? "클라이언트" : "서버",
            "화살표 + Space",  // 상대방도 싱글 키 사용
            isServer ? new Color(255, 200, 100) : new Color(100, 200, 255),
            opponentEngine  // P2P 엔진 주입
        );
        
        // 게임 모드 설정
        GameMode mode = GameMode.NORMAL;
        if ("ITEM".equals(selectedBattleMode)) {
            mode = GameMode.ITEM;
        }
        
        myPanel.getGameEngine().setGameMode(mode);
        opponentEngine.setGameMode(mode);
        
        // P2P 모드: 각자 랜덤하게 블록 생성 (동기화 안함)
        // 양쪽이 서로 다른 블록으로 플레이하는 진정한 대전 모드
        
        boolean isTimeLimitMode = isTimeLimitMode();
        myPanel.setCountdownTimerEnabled(isTimeLimitMode);
        opponentPanel.setCountdownTimerEnabled(isTimeLimitMode);
        
        // 대전 모드 설정
        myPanel.setOpponentPanel(opponentPanel);
        opponentPanel.setOpponentPanel(myPanel);
        
        // P2P 모드: 게임 컨트롤러 사용 안함 (직접 게임오버 처리)
        // 각자 자신의 게임만 모니터링
        
        // 입력 핸들러 (싱글 플레이 키 사용)
        myInputHandler = new se.tetris.team5.gamelogic.input.SinglePlayerInputHandler(myPanel.getGameEngine());
        
        // 중앙 패널 (내 화면은 항상 왼쪽, 상대 화면은 항상 오른쪽)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(new Color(0, 0, 0, 180));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 모든 플레이어에게 자신의 화면은 왼쪽에 표시
        centerPanel.add(myPanel);
        centerPanel.add(opponentPanel);
        
        // 상단 상태 바
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0, 0, 0, 180));
        
        // 레이턴시 표시 레이블 초기화
        latencyLabel = new JLabel("핑: 0ms");
        latencyLabel.setFont(createKoreanFont(Font.PLAIN, 14));
        latencyLabel.setForeground(Color.GREEN);
        latencyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        latencyLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        statusLabel.setText("P2P 대전 중 - " + getBattleModeText(selectedBattleMode));
        topPanel.add(statusLabel, BorderLayout.CENTER);
        topPanel.add(latencyLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
        
        if (isTimeLimitMode) {
            startTimeLimitMode();
        } else {
            stopTimeLimitTimer();
        }

        // 게임 시작
        myPanel.startGame();  // 내 게임은 실제로 실행
        
        // 상대방 P2P 엔진 시작 (네트워크 데이터 수신 준비)
        se.tetris.team5.gamelogic.GameEngine oppEngine = opponentPanel.getGameEngine();
        if (oppEngine instanceof se.tetris.team5.gamelogic.P2PGameEngine) {
            ((se.tetris.team5.gamelogic.P2PGameEngine) oppEngine).startP2PGame();
            System.out.println("[P2P] 상대방 P2P 엔진 시작됨");
        }
        
        // 내 게임 오버 모니터링
        Timer gameOverCheckTimer = new Timer(100, e -> {
            if (myPanel != null && myPanel.isGameOver() && currentState == ScreenState.PLAYING) {
                handleGameOver(isServer ? 2 : 1); // 내가 졌으므로 상대 승리
                ((Timer)e.getSource()).stop();
            }
        });
        gameOverCheckTimer.start();
        
        // 주기적으로 게임 상태 전송 (30fps로 낮춰서 깜빡임 감소)
        Timer syncTimer = new Timer(33, e -> syncGameState());
        syncTimer.start();
        
        // 레이턴시 표시 업데이트 타이머 (500ms마다)
        latencyUpdateTimer = new Timer(500, e -> updateLatencyDisplay());
        latencyUpdateTimer.start();
        
        ensureGameplayFocus();
    }
    
    /**
     * 게임 상태 동기화
     */
    private void syncGameState() {
        if (currentState != ScreenState.PLAYING || myPanel == null) {
            return;
        }
        
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.GAME_STATE);
        packet.setBoard(myPanel.getGameEngine().getBoardManager().getBoard());
        packet.setBoardColors(myPanel.getGameEngine().getBoardManager().getBoardColors());
        packet.setCurrentBlockX(myPanel.getGameEngine().getX());
        packet.setCurrentBlockY(myPanel.getGameEngine().getY());
        packet.setCurrentBlockType(myPanel.getGameEngine().getCurrentBlock().getBlockType());
        packet.setNextBlockType(myPanel.getGameEngine().getNextBlock().getBlockType());
        packet.setScore(myPanel.getGameEngine().getGameScoring().getCurrentScore());
        packet.setLevel(myPanel.getGameEngine().getGameScoring().getLevel());
        packet.setLinesCleared(myPanel.getGameEngine().getGameScoring().getLinesCleared());
        
        // 게임 경과 시간 계산 및 전송
        long elapsedTime = myPanel.getGameEngine().getElapsedTime();
        packet.setElapsedTime(elapsedTime);
        packet.setHasTimeStopCharge(myPanel.getGameEngine().hasTimeStopCharge());
        
        // 공격 블록 전송 (게임에서 새로 생성된 공격 줄만)
        java.util.List<java.awt.Color[]> attackBlocks = myPanel.drainPendingOutgoingAttackBlocks();
        if (!attackBlocks.isEmpty()) {
            System.out.println("[P2P] 공격 블록 전송: " + attackBlocks.size() + "줄");
            GameStatePacket attackPacket = new GameStatePacket(GameStatePacket.PacketType.ATTACK_BLOCKS);
            attackPacket.setAttackBlocks(encodeAttackBlocks(attackBlocks));
            
            if (isServer && server != null) {
                server.sendPacket(attackPacket);
            } else if (!isServer && client != null) {
                client.sendPacket(attackPacket);
            }
        }
        
        if (isServer && server != null) {
            server.sendPacket(packet);
        } else if (!isServer && client != null) {
            client.sendPacket(packet);
        }
    }
    
    /**
     * 패킷 수신 처리
     */
    private void handlePacketReceived(GameStatePacket packet) {
        SwingUtilities.invokeLater(() -> {
            switch (packet.getType()) {
                case GAME_MODE_SELECT:
                    selectedBattleMode = packet.getBattleMode();
                    gameRandomSeed = packet.getRandomSeed();
                    if (currentState == ScreenState.CLIENT_CONNECTING) {
                        showReadyWaiting();
                    } else if (currentState == ScreenState.READY_WAITING) {
                        updateReadyScreenModeLabel();
                    }
                    break;
                    
                case READY:
                    opponentReady = "ready".equals(packet.getMessage());
                    updateReadyStatus();
                    if (isReady && opponentReady && currentState == ScreenState.READY_WAITING) {
                        startGame();
                    }
                    break;
                    
                case GAME_START:
                    if (currentState == ScreenState.READY_WAITING) {
                        startGame();
                    }
                    break;
                    
                case GAME_STATE:
                    if (opponentPanel != null && currentState == ScreenState.PLAYING) {
                        updateOpponentState(packet);
                    }
                    break;
                    
                case ATTACK_BLOCKS:
                    // 공격 블록 수신 - 내 패널에 적용
                    if (myPanel != null && currentState == ScreenState.PLAYING) {
                        java.util.List<java.awt.Color[]> receivedAttacks = decodeAttackBlocks(packet.getAttackBlocks());
                        if (!receivedAttacks.isEmpty()) {
                            System.out.println("[P2P] 공격 블록 수신: " + receivedAttacks.size() + "줄");
                            myPanel.receiveAttackBlocks(receivedAttacks);
                        }
                    }
                    break;
                    
                case GAME_OVER:
                    handleGameOver(packet.getWinner());
                    break;
                case CHAT_MESSAGE:
                    // 채팅 시스템은 현재 비활성화됨 (더미 패킷만 처리)
                    break;
                    
                default:
                    break;
            }
        });
    }
    
    /**
     * 상대방 게임 상태 업데이트
     */
    private void updateOpponentState(GameStatePacket packet) {
        if (opponentPanel == null) {
            return;
        }
        
        // P2PGameEngine에 네트워크 상태 주입
        se.tetris.team5.gamelogic.GameEngine engine = opponentPanel.getGameEngine();
        if (engine instanceof se.tetris.team5.gamelogic.P2PGameEngine) {
            se.tetris.team5.gamelogic.P2PGameEngine p2pEngine = 
                (se.tetris.team5.gamelogic.P2PGameEngine) engine;
            
            // 완전한 게임 상태 한번에 주입
            p2pEngine.injectCompleteState(
                packet.getBoard(),
                packet.getBoardColors(),
                packet.getCurrentBlockType(),
                packet.getNextBlockType(),
                packet.getScore(),
                packet.getLevel(),
                packet.getLinesCleared(),
                packet.getElapsedTime()
            );
            p2pEngine.setTimeStopCharge(packet.hasTimeStopCharge());
            opponentPanel.updateTimeStopIndicatorFromNetwork(packet.hasTimeStopCharge());
            
            // UI 컴포넌트 업데이트 (P2P 엔진이 상태를 가지고 있으므로 자동 반영됨)
            if (opponentPanel.getGameBoard() != null) {
                // 현재 보드에는 이동 중인 블록(값 2)이 이미 포함되어 있으므로 별도의 currentBlock 전달 불필요
                opponentPanel.getGameBoard().renderBoard(
                    packet.getBoard(),
                    packet.getBoardColors(),
                    null,
                    null,
                    0,
                    0,
                    -1
                );
            }
            
            // 점수/레벨/라인 정보는 P2P 엔진에 이미 주입되었으므로
            // UI 업데이트만 수행
            opponentPanel.updateScore(packet.getScore());
            opponentPanel.updateLevel(packet.getLevel());
            opponentPanel.updateLines(packet.getLinesCleared());
            opponentPanel.updateNextBlock(packet.getNextBlockType());
            if (!isTimeLimitMode()) {
                opponentPanel.updateTimer(packet.getElapsedTime());
            }
        }
    }
    
    /**
     * blockType으로 Block 객체 생성
     */
    private se.tetris.team5.blocks.Block createBlockFromType(String blockType) {
        if (blockType == null) return null;
        
        switch (blockType) {
            case "I": return new se.tetris.team5.blocks.IBlock();
            case "O": return new se.tetris.team5.blocks.OBlock();
            case "T": return new se.tetris.team5.blocks.TBlock();
            case "S": return new se.tetris.team5.blocks.SBlock();
            case "Z": return new se.tetris.team5.blocks.ZBlock();
            case "L": return new se.tetris.team5.blocks.LBlock();
            case "J": return new se.tetris.team5.blocks.JBlock();
            case "W": return new se.tetris.team5.blocks.WBlock();
            case "DOT": return new se.tetris.team5.blocks.DotBlock();
            default: return null;
        }
    }
    
    /**
     * 게임 오버 처리
     */
    private void handleGameOver(int winner) {
        if (currentState != ScreenState.PLAYING) {
            return;
        }
        // Prevent repeated dialogs
        currentState = ScreenState.GAME_OVER;
        stopTimeLimitTimer();

        // 게임 오버 패킷 전송
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.GAME_OVER);
        packet.setWinner(winner);

        if (isServer && server != null) {
            server.sendPacket(packet);
        } else if (!isServer && client != null) {
            client.sendPacket(packet);
        }

        String message = winner == (isServer ? 1 : 2) ?
            "축하합니다! 승리하셨습니다!\n\n메인 메뉴로 돌아가거나 게임을 종료할 수 있습니다." :
            "아쉽게도 패배하셨습니다...\n\n메인 메뉴로 돌아가거나 게임을 종료할 수 있습니다.";

        Object[] options = { "메인 메뉴", "게임 종료" };
        int option = JOptionPane.showOptionDialog(
            this,
            message,
            "게임 종료",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (option == 0 || option == JOptionPane.CLOSED_OPTION) {
            disconnect();
        } else if (option == 1) {
            exitApplication();
        }
    }
    
    /**
     * 연결 종료
     */
    private void disconnect() {
        stopTimeLimitTimer();
        stopLobbyLatencyMonitor();
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.DISCONNECT);
        
        if (isServer && server != null) {
            server.sendPacket(packet);
            server.close();
            server = null;
        } else if (!isServer && client != null) {
            client.sendPacket(packet);
            client.close();
            client = null;
        }
        
        returnToHome();
    }
    
    /**
     * 애플리케이션 완전 종료
     */
    private void exitApplication() {
        stopTimeLimitTimer();
        stopLobbyLatencyMonitor();
        if (latencyUpdateTimer != null) {
            latencyUpdateTimer.stop();
            latencyUpdateTimer = null;
        }

        try {
            GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.DISCONNECT);
            if (isServer && server != null) {
                server.sendPacket(packet);
                server.close();
                server = null;
            } else if (!isServer && client != null) {
                client.sendPacket(packet);
                client.close();
                client = null;
            }
        } catch (Exception ignored) {}

        if (gameController != null) {
            gameController.stop();
            gameController = null;
        }
        if (myPanel != null) {
            myPanel.stopGame();
        }
        if (opponentPanel != null) {
            opponentPanel.stopGame();
        }
        if (screenController != null) {
            screenController.dispose();
        }
        System.exit(0);
    }
    
    /**
     * 연결 끊김 처리
     */
    private void handleDisconnection(String reason) {
        stopTimeLimitTimer();
        stopLobbyLatencyMonitor();
        if (currentState == ScreenState.PLAYING) {
            // gameController가 있다면 중지
            if (gameController != null) {
                gameController.stop();
            }
            // 내 게임 중지
            if (myPanel != null) {
                myPanel.stopGame();
            }
        }
        
        // 연결 끊김 이유에 따른 자세한 메시지
        String detailedMessage = getDisconnectionMessage(reason);
        
        Object[] options = { "메인 메뉴", "게임 종료" };
        int choice = JOptionPane.showOptionDialog(
            this,
            detailedMessage + "\n\n게임을 종료하거나 메인 화면으로 돌아가세요.",
            "연결 종료",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 1) {
            exitApplication();
            return;
        }
        
        returnToHome();
    }
    
    /**
     * 연결 끊김 이유에 따른 상세 메시지 생성
     */
    private String getDisconnectionMessage(String reason) {
        if (reason.contains("타임아웃") || reason.contains("timeout")) {
            return "연결이 끊어졌습니다.\n\n원인: 네트워크 타임아웃\n" +
                   "- 인터넷 연결을 확인해주세요.\n" +
                   "- 방화벽 설정을 확인해주세요.";
        } else if (reason.contains("종료") || reason.contains("close")) {
            return "연결이 종료되었습니다.\n\n상대방이 게임을 종료했습니다.";
        } else if (reason.contains("포트") || reason.contains("port")) {
            return "연결 실패\n\n원인: 포트 접근 오류\n" +
                   "- 포트 15555가 이미 사용 중일 수 있습니다.\n" +
                   "- 방화벽에서 포트를 허용해주세요.";
        } else if (reason.contains("거부") || reason.contains("refused")) {
            return "연결 실패\n\n원인: 연결 거부됨\n" +
                   "- 서버 IP 주소를 확인해주세요.\n" +
                   "- 서버가 실행 중인지 확인해주세요.";
        } else {
            return "연결이 끊어졌습니다.\n\n원인: " + reason;
        }
    }
    
    /**
     * 레이턴시 표시 업데이트
     */
    private void updateLatencyDisplay() {
        if (latencyLabel == null) {
            return;
        }
        
        long latency = 0;
        if (isServer && server != null) {
            latency = server.getCurrentLatency();
        } else if (!isServer && client != null) {
            latency = client.getCurrentLatency();
        }
        
        if (latency <= 0) {
            latencyLabel.setForeground(Color.LIGHT_GRAY);
            latencyLabel.setText("핑 측정 중...");
            updateLobbyLatencyLabel();
            return;
        }
        
        boolean thresholdLagging = latency >= LAG_WARNING_THRESHOLD_MS;
        if (isLaggingNetwork != thresholdLagging) {
            isLaggingNetwork = thresholdLagging;
            updateLobbyLatencyLabel();
        }
        
        Color color = isLaggingNetwork ? Color.RED : getLatencyColor(latency);
        String text = isLaggingNetwork
            ? String.format("핑: %dms (지연)", latency)
            : String.format("핑: %dms", latency);
        latencyLabel.setForeground(color);
        latencyLabel.setText(text);
        updateLobbyLatencyLabel();
    }
    
    private void handleLagDetectionEvent(boolean isLagging) {
        if (this.isLaggingNetwork != isLagging) {
            this.isLaggingNetwork = isLagging;
            updateLobbyLatencyLabel();
        }
        updateLatencyDisplay();
    }
    
    private void sendChatMessage() {
        if (chatInputField == null) {
            return;
        }
        String text = chatInputField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        
        appendChatMessage(getLocalRoleLabel(), text);
        chatInputField.setText("");
        
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.CHAT_MESSAGE);
        packet.setMessage(text);
        
        if (isServer && server != null) {
            server.sendPacket(packet);
        } else if (!isServer && client != null) {
            client.sendPacket(packet);
        }
    }
    
    private void appendChatMessage(String sender, String message) {
        if (chatArea == null) {
            return;
        }
        chatArea.append(String.format("[%s] %s%n", sender, message));
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private Color getLatencyColor(long latency) {
        if (latency == 0) {
            return Color.GRAY;
        } else if (latency < 50) {
            return Color.GREEN;
        } else if (latency < 100) {
            return new Color(144, 238, 144); // Light green
        } else if (latency < 150) {
            return Color.YELLOW;
        } else if (latency < 200) {
            return Color.ORANGE;
        }
        return Color.RED;
    }
    
    private void startLobbyLatencyMonitor() {
        if (lobbyLatencyLabel == null) {
            return;
        }
        stopLobbyLatencyMonitor();
        lobbyLatencyTimer = new Timer(1000, e -> updateLobbyLatencyLabel());
        lobbyLatencyTimer.start();
        updateLobbyLatencyLabel();
    }
    
    private void stopLobbyLatencyMonitor() {
        if (lobbyLatencyTimer != null) {
            lobbyLatencyTimer.stop();
            lobbyLatencyTimer = null;
        }
    }
    
    private void updateLobbyLatencyLabel() {
        if (lobbyLatencyLabel == null) {
            return;
        }
        long latency = 0;
        boolean hasConnection = false;
        if (isServer && server != null) {
            latency = server.getCurrentLatency();
            hasConnection = server.isConnected();
        } else if (!isServer && client != null) {
            latency = client.getCurrentLatency();
            hasConnection = client.isConnected();
        }
        
        if (!hasConnection) {
            lobbyLatencyLabel.setForeground(Color.LIGHT_GRAY);
            lobbyLatencyLabel.setText("네트워크 상태: 연결 대기 중");
            return;
        }
        
        if (latency <= 0) {
            lobbyLatencyLabel.setForeground(Color.LIGHT_GRAY);
            lobbyLatencyLabel.setText("핑 측정 중...");
        } else {
            boolean lagging = isLaggingNetwork || latency >= LAG_WARNING_THRESHOLD_MS;
            if (lagging) {
                lobbyLatencyLabel.setForeground(Color.RED);
                lobbyLatencyLabel.setText(String.format("현재 핑: %dms (지연)", latency));
            } else {
                lobbyLatencyLabel.setForeground(getLatencyColor(latency));
                lobbyLatencyLabel.setText(String.format("현재 핑: %dms", latency));
            }
        }
    }
    
    private String getLocalRoleLabel() {
        return isServer ? "나(서버)" : "나(클라이언트)";
    }
    
    private String getOpponentRoleLabel() {
        return isServer ? "상대(클라이언트)" : "상대(서버)";
    }
    
    private boolean isTimeLimitMode() {
        return "TIMELIMIT".equals(selectedBattleMode);
    }
    
    private void startTimeLimitMode() {
        stopTimeLimitTimer();
        remainingSeconds = TIME_LIMIT_SECONDS;
        updateTimeLimitLabels();
        
        timeLimitTimer = new Timer(1000, e -> {
            if (currentState != ScreenState.PLAYING) {
                return;
            }
            remainingSeconds--;
            updateTimeLimitLabels();
            if (remainingSeconds <= 0) {
                stopTimeLimitTimer();
                handleTimeLimitTimeout();
            }
        });
        timeLimitTimer.start();
    }
    
    private void updateTimeLimitLabels() {
        String timeStr = String.format("%02d:%02d",
            Math.max(remainingSeconds, 0) / 60,
            Math.max(remainingSeconds, 0) % 60);
        
        if (myPanel != null) {
            myPanel.updateTimerLabel(timeStr);
        }
        if (opponentPanel != null) {
            opponentPanel.updateTimerLabel(timeStr);
        }
    }
    
    private void handleTimeLimitTimeout() {
        if (currentState != ScreenState.PLAYING) {
            return;
        }
        int serverScore = isServer ? getPanelScore(myPanel) : getPanelScore(opponentPanel);
        int clientScore = isServer ? getPanelScore(opponentPanel) : getPanelScore(myPanel);
        
        int winner;
        if (serverScore == clientScore) {
            winner = 1; // 서버 우선 승리
        } else {
            winner = serverScore > clientScore ? 1 : 2;
        }
        handleGameOver(winner);
    }
    
    private int getPanelScore(PlayerGamePanel panel) {
        if (panel == null || panel.getGameEngine() == null 
                || panel.getGameEngine().getGameScoring() == null) {
            return 0;
        }
        return panel.getGameEngine().getGameScoring().getCurrentScore();
    }
    
    private void stopTimeLimitTimer() {
        if (timeLimitTimer != null) {
            timeLimitTimer.stop();
            timeLimitTimer = null;
        }
    }
    
    /**
     * 오류 표시
     */
    private void showError(String error) {
        JOptionPane.showMessageDialog(
            this,
            error,
            "오류",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * 홈 화면으로 돌아가기
     */
    private void returnToHome() {
        stopTimeLimitTimer();
        stopLobbyLatencyMonitor();
        if (latencyUpdateTimer != null) {
            latencyUpdateTimer.stop();
            latencyUpdateTimer = null;
        }
        if (server != null) {
            server.close();
            server = null;
        }
        if (client != null) {
            client.close();
            client = null;
        }
        if (gameController != null) {
            gameController.stop();
            gameController = null;
        }
        if (myPanel != null) {
            myPanel.stopGame();
        }
        
        applyOriginalWindowSize();
        screenController.showScreen("home");
    }
    
    /**
     * 최근 접속 IP 저장
     */
    private void ensureGameplayFocus() {
        if (currentState != ScreenState.PLAYING) {
            return;
        }
        removeKeyListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        boolean focused = requestFocusInWindow();

        Timer focusTimer = new Timer(200, e -> {
            if (!hasFocus()) {
                setFocusable(true);
                requestFocusInWindow();
                System.out.println("[P2P] 포커스 재요청");
            }
            ((Timer) e.getSource()).stop();
        });
        focusTimer.setRepeats(false);
        focusTimer.start();

        if (!focused) {
            SwingUtilities.invokeLater(this::requestFocusInWindow);
        }
    }

    private void setupWindowFocusHandling() {
        if (screenController == null || windowFocusListener != null) {
            return;
        }
        windowFocusListener = new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                SwingUtilities.invokeLater(() -> ensureGameplayFocus());
            }
        };
        screenController.addWindowFocusListener(windowFocusListener);
    }

    private void saveRecentIP(String ip) {
        try {
            java.util.Properties props = new java.util.Properties();
            java.io.File file = new java.io.File("tetris_p2p.properties");
            
            if (file.exists()) {
                try (java.io.FileInputStream in = new java.io.FileInputStream(file)) {
                    props.load(in);
                }
            }
            
            // 최근 3개까지 저장
            String ip1 = props.getProperty("recent.ip.1", "");
            String ip2 = props.getProperty("recent.ip.2", "");
            
            if (!ip.equals(ip1)) {
                props.setProperty("recent.ip.3", ip2);
                props.setProperty("recent.ip.2", ip1);
                props.setProperty("recent.ip.1", ip);
            }
            
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(file)) {
                props.store(out, "Tetris P2P Recent IPs");
            }
        } catch (Exception e) {
            System.out.println("IP 저장 오류: " + e.getMessage());
        }
    }
    
    /**
     * 최근 접속 IP 불러오기
     */
    private String[] loadRecentIPs() {
        try {
            java.util.Properties props = new java.util.Properties();
            java.io.File file = new java.io.File("tetris_p2p.properties");
            
            if (file.exists()) {
                try (java.io.FileInputStream in = new java.io.FileInputStream(file)) {
                    props.load(in);
                }
                
                java.util.List<String> ips = new java.util.ArrayList<>();
                for (int i = 1; i <= 3; i++) {
                    String ip = props.getProperty("recent.ip." + i, "").trim();
                    if (!ip.isEmpty()) {
                        ips.add(ip);
                    }
                }
                
                if (!ips.isEmpty()) {
                    return ips.toArray(new String[0]);
                }
            }
        } catch (Exception e) {
            System.out.println("IP 불러오기 오류: " + e.getMessage());
        }
        
        return new String[] { "최근 접속 기록 없음" };
    }
    
    /**
     * 스타일이 적용된 버튼 생성 (home.java 방식)
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(createKoreanFont(Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE); // 흰색 텍스트로 명확히 설정
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(300, 50));
        button.setMinimumSize(new Dimension(300, 50));
        button.setMaximumSize(new Dimension(300, 50));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        return button;
    }
    
    /**
     * 컴포넌트를 중앙 정렬로 감싸기
     */
    private JPanel createCenteredComponent(JComponent component) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);
        wrapper.add(component);
        return wrapper;
    }
    
    private JPanel createLobbyLatencyPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "네트워크 지연 상태",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            createKoreanFont(Font.BOLD, 12),
            Color.LIGHT_GRAY
        ));
        
        JLabel descriptionLabel = new JLabel("대기방에서도 현재 핑을 확인할 수 있습니다.");
        descriptionLabel.setFont(createKoreanFont(Font.PLAIN, 12));
        descriptionLabel.setForeground(Color.LIGHT_GRAY);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lobbyLatencyLabel = new JLabel("핑 측정 준비 중...");
        lobbyLatencyLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        lobbyLatencyLabel.setForeground(Color.LIGHT_GRAY);
        lobbyLatencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(descriptionLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lobbyLatencyLabel);
        panel.setMaximumSize(new Dimension(400, 80));
        return panel;
    }

    private JPanel createChatPanel() {
        // 채팅 기능은 현재 비활성화 상태이며 향후 재도입 시 아래의 더미 패널을 교체하세요.
        JPanel placeholder = new JPanel(new FlowLayout(FlowLayout.CENTER));
        placeholder.setOpaque(false);
        placeholder.setMaximumSize(new Dimension(450, 60));
        JLabel label = new JLabel("채팅 기능은 현재 비활성화되었습니다.");
        label.setFont(createKoreanFont(Font.ITALIC, 12));
        label.setForeground(Color.LIGHT_GRAY);
        placeholder.add(label);
        return placeholder;

        /*
        기존 채팅 구현:
        JPanel wrapper = new JPanel();
        ... (이전 UI 및 메시지 처리 코드)
        */
    }

    /**
     * 게임 화면에서 대기방 UI로 복귀하기 위한 기본 레이아웃/크기 복원
     */
    private void restoreLobbyLayout() {
        applyOriginalWindowSize();

        removeAll();
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * 사용자가 설정한 원래 창 크기를 다시 적용한다.
     */
    private void applyOriginalWindowSize() {
        se.tetris.team5.utils.setting.GameSettings settings =
            se.tetris.team5.utils.setting.GameSettings.getInstance();
        String targetSize = originalWindowSize;
        if (targetSize == null || targetSize.trim().isEmpty()) {
            targetSize = settings.getWindowSize();
        }
        if (targetSize == null || targetSize.trim().isEmpty()) {
            targetSize = se.tetris.team5.utils.setting.GameSettings.WINDOW_SIZE_MEDIUM;
        }
        boolean applied = false;
        try {
            String[] parts = targetSize.toLowerCase().split("x");
            if (parts.length == 2) {
                int width = Integer.parseInt(parts[0].trim());
                int height = Integer.parseInt(parts[1].trim());
                settings.setCustomWindowSize(width, height);
                applied = true;
            }
        } catch (Exception ignored) {}

        if (!applied) {
            settings.setWindowSize(se.tetris.team5.utils.setting.GameSettings.WINDOW_SIZE_MEDIUM);
            targetSize = se.tetris.team5.utils.setting.GameSettings.WINDOW_SIZE_MEDIUM;
        }

        originalWindowSize = targetSize;
        if (screenController != null) {
            screenController.updateWindowSize();
        }
    }

    private java.util.List<int[]> encodeAttackBlocks(java.util.List<java.awt.Color[]> blocks) {
        java.util.List<int[]> encoded = new java.util.ArrayList<>();
        if (blocks == null) {
            return encoded;
        }
        for (java.awt.Color[] row : blocks) {
            if (row == null) {
                continue;
            }
            int[] encodedRow = new int[row.length];
            for (int i = 0; i < row.length; i++) {
                encodedRow[i] = row[i] != null ? row[i].getRGB() : 0;
            }
            encoded.add(encodedRow);
        }
        return encoded;
    }

    private java.util.List<java.awt.Color[]> decodeAttackBlocks(java.util.List<int[]> encoded) {
        java.util.List<java.awt.Color[]> decoded = new java.util.ArrayList<>();
        if (encoded == null || encoded.isEmpty()) {
            return decoded;
        }
        for (int[] row : encoded) {
            if (row == null) {
                continue;
            }
            java.awt.Color[] decodedRow = new java.awt.Color[row.length];
            for (int i = 0; i < row.length; i++) {
                decodedRow[i] = row[i] == 0 ? null : new java.awt.Color(row[i], true);
            }
            decoded.add(decodedRow);
        }
        return decoded;
    }
    
    /**
     * 메인 패널 업데이트
     */
    private void updateMainPanel(JPanel content) {
        if (mainPanel.getComponentCount() > 1) {
            mainPanel.remove(1);
        }
        mainPanel.add(content, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    /**
     * 배틀 모드 텍스트
     */
    private String getBattleModeText(String mode) {
        switch (mode) {
            case "ITEM": return "아이템 대전";
            case "TIMELIMIT": return "시간제한 대전";
            default: return "일반 대전";
        }
    }
    
    /**
     * 한글 폰트 생성
     */
    private Font createKoreanFont(int style, int size) {
        String[] koreanFonts = {"맑은 고딕", "Malgun Gothic", "굴림", "Gulim", 
                                "Arial Unicode MS", "Dialog"};
        for (String fontName : koreanFonts) {
            Font font = new Font(fontName, style, size);
            if (font.getFamily().equals(fontName) || font.canDisplay('한')) {
                return font;
            }
        }
        return new Font(Font.DIALOG, style, size);
    }
    
    public void display(javax.swing.JTextPane textPane) {
        // ScreenController 호환성
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("[P2P] 키 입력 감지: " + KeyEvent.getKeyText(e.getKeyCode()));

        if (currentState == ScreenState.PLAYING) {
            se.tetris.team5.utils.setting.GameSettings settings =
                se.tetris.team5.utils.setting.GameSettings.getInstance();
            int itemKey = settings.getKeyCode("item");
            if (e.getKeyCode() == itemKey && myPanel != null) {
                if (myPanel.useItem()) {
                    ensureGameplayFocus();
                }
                return;
            }
        }

        if (currentState == ScreenState.PLAYING && myInputHandler != null) {
            myInputHandler.handleKeyPress(e.getKeyCode());
            System.out.println("[P2P] 키 입력 처리 완료");

            // 키 입력 후 포커스 유지 (특히 하드드롭 후)
            SwingUtilities.invokeLater(() -> {
                if (!isFocusOwner()) {
                    requestFocusInWindow();
                    System.out.println("[P2P] 포커스 복구");
                }
            });
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (currentState == ScreenState.PLAYING) {
                String message = "게임을 종료하시겠습니까?\n\n메인 메뉴로 돌아가거나 게임을 완전히 종료할 수 있습니다.";
                Object[] options = { "메인 메뉴", "게임 종료" };
                int option = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "게임 종료",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                if (option == 0) {
                    disconnect();
                } else if (option == 1) {
                    exitApplication();
                } else {
                    ensureGameplayFocus();
                }
            }
        }
    }
    public void keyReleased(KeyEvent e) {}
}
