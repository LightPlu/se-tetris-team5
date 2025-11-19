package se.tetris.team5.screens;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.IOException;
import java.util.List;

import se.tetris.team5.ScreenController;
import se.tetris.team5.network.*;

/**
 * P2P 대전 모드 시작 화면
 * 서버/클라이언트 선택, IP 입력/표시, 연결 대기, 게임 모드 선택
 */
public class p2p extends JPanel {
  private static final long serialVersionUID = 1L;
  private static final int DEFAULT_PORT = 17777; // 테트리스 포트
  
  private ScreenController screenController;
  private P2PServer server;
  private P2PClient client;
  private RecentIPManager ipManager;
  
  // UI 컴포넌트
  private JPanel modeSelectionPanel;
  private JPanel serverPanel;
  private JPanel clientPanel;
  private JPanel waitingPanel;
  private JPanel gameModePanel;
  
  // 서버 UI
  private JLabel serverIPLabel;
  private JLabel serverStatusLabel;
  private JButton serverStartButton;
  private JButton serverCancelButton;
  
  // 클라이언트 UI
  private JTextField clientIPField;
  private JComboBox<String> recentIPComboBox;
  private JLabel clientStatusLabel;
  private JButton clientConnectButton;
  private JButton clientCancelButton;
  
  // 대기 화면 UI
  private JLabel waitingMessageLabel;
  private JLabel connectionInfoLabel;
  private JLabel latencyLabel;
  private JButton readyButton;
  private JButton disconnectButton;
  
  // 게임 모드 선택 UI (서버만)
  private JComboBox<String> gameModeComboBox;
  private JLabel gameModeLabel;
  
  // 상태
  private boolean isServer = false;
  private boolean isConnected = false;
  private boolean isReady = false;
  private boolean opponentReady = false;
  private String selectedGameMode = "ITEM"; // 기본값: 아이템 모드
  
  // 타이머 (지연 업데이트용)
  private Timer latencyUpdateTimer;

  public p2p(ScreenController screenController) {
    this.screenController = screenController;
    this.ipManager = new RecentIPManager();
    
    setLayout(new BorderLayout());
    setBackground(new Color(18, 18, 24));
    
    initComponents();
  }

  private Font createKoreanFont(int style, int size) {
    String[] koreanFonts = {"맑은 고딕", "Malgun Gothic", "굴림", "Gulim", "Arial Unicode MS", "Dialog"};
    for (String fontName : koreanFonts) {
      Font font = new Font(fontName, style, size);
      if (font.getFamily().equals(fontName) || font.canDisplay('한')) {
        return font;
      }
    }
    return new Font(Font.DIALOG, style, size);
  }

  private void initComponents() {
    // 상단 타이틀
    JLabel titleLabel = new JLabel("P2P 네트워크 대전", SwingConstants.CENTER);
    titleLabel.setFont(createKoreanFont(Font.BOLD, 32));
    titleLabel.setForeground(new Color(100, 200, 255));
    titleLabel.setBorder(new EmptyBorder(30, 20, 30, 20));
    add(titleLabel, BorderLayout.NORTH);
    
    // 중앙 패널 (카드 레이아웃)
    JPanel centerPanel = new JPanel(new CardLayout());
    centerPanel.setOpaque(false);
    
    // 1. 모드 선택 패널
    modeSelectionPanel = createModeSelectionPanel();
    centerPanel.add(modeSelectionPanel, "MODE_SELECTION");
    
    // 2. 서버 패널
    serverPanel = createServerPanel();
    centerPanel.add(serverPanel, "SERVER");
    
    // 3. 클라이언트 패널
    clientPanel = createClientPanel();
    centerPanel.add(clientPanel, "CLIENT");
    
    // 4. 대기 패널
    waitingPanel = createWaitingPanel();
    centerPanel.add(waitingPanel, "WAITING");
    
    add(centerPanel, BorderLayout.CENTER);
    
    // 하단 버튼
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
    bottomPanel.setOpaque(false);
    
    JButton backButton = createStyledButton("메인 메뉴", new Color(150, 150, 150));
    backButton.addActionListener(e -> {
      cleanup();
      screenController.showScreen("home");
    });
    bottomPanel.add(backButton);
    
    add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * 1. 모드 선택 패널 (서버 vs 클라이언트)
   */
  private JPanel createModeSelectionPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setBorder(new EmptyBorder(50, 50, 50, 50));
    
    JLabel infoLabel = new JLabel("역할을 선택하세요", SwingConstants.CENTER);
    infoLabel.setFont(createKoreanFont(Font.BOLD, 20));
    infoLabel.setForeground(Color.WHITE);
    infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(infoLabel);
    
    panel.add(Box.createVerticalStrut(30));
    
    // 서버 버튼
    JButton serverButton = createStyledButton("🖥️ 서버 (호스트)", new Color(100, 200, 255));
    serverButton.setMaximumSize(new Dimension(300, 60));
    serverButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    serverButton.addActionListener(e -> showServerPanel());
    panel.add(serverButton);
    
    panel.add(Box.createVerticalStrut(20));
    
    // 클라이언트 버튼
    JButton clientButton = createStyledButton("💻 클라이언트 (참가자)", new Color(150, 255, 150));
    clientButton.setMaximumSize(new Dimension(300, 60));
    clientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    clientButton.addActionListener(e -> showClientPanel());
    panel.add(clientButton);
    
    panel.add(Box.createVerticalStrut(30));
    
    JLabel descLabel = new JLabel("<html><center>서버: 게임을 호스팅하고 다른 플레이어를 초대<br>클라이언트: 서버 IP로 접속하여 게임 참가</center></html>", SwingConstants.CENTER);
    descLabel.setFont(createKoreanFont(Font.PLAIN, 14));
    descLabel.setForeground(new Color(180, 180, 180));
    descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(descLabel);
    
    return panel;
  }

  /**
   * 2. 서버 패널
   */
  private JPanel createServerPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setBorder(new EmptyBorder(50, 50, 50, 50));
    
    JLabel titleLabel = new JLabel("서버 모드", SwingConstants.CENTER);
    titleLabel.setFont(createKoreanFont(Font.BOLD, 24));
    titleLabel.setForeground(new Color(100, 200, 255));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(titleLabel);
    
    panel.add(Box.createVerticalStrut(30));
    
    // IP 주소 표시
    serverIPLabel = new JLabel("IP: ...", SwingConstants.CENTER);
    serverIPLabel.setFont(createKoreanFont(Font.BOLD, 20));
    serverIPLabel.setForeground(new Color(255, 220, 100));
    serverIPLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(serverIPLabel);
    
    panel.add(Box.createVerticalStrut(10));
    
    JLabel portLabel = new JLabel("포트: " + DEFAULT_PORT, SwingConstants.CENTER);
    portLabel.setFont(createKoreanFont(Font.PLAIN, 16));
    portLabel.setForeground(new Color(200, 200, 200));
    portLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(portLabel);
    
    panel.add(Box.createVerticalStrut(20));
    
    // 상태 표시
    serverStatusLabel = new JLabel("대기 중...", SwingConstants.CENTER);
    serverStatusLabel.setFont(createKoreanFont(Font.PLAIN, 16));
    serverStatusLabel.setForeground(new Color(255, 255, 255));
    serverStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(serverStatusLabel);
    
    panel.add(Box.createVerticalStrut(30));
    
    // 버튼
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
    buttonPanel.setOpaque(false);
    
    serverStartButton = createStyledButton("서버 시작", new Color(100, 200, 255));
    serverStartButton.addActionListener(e -> startServer());
    buttonPanel.add(serverStartButton);
    
    serverCancelButton = createStyledButton("취소", new Color(150, 150, 150));
    serverCancelButton.addActionListener(e -> cancelServer());
    buttonPanel.add(serverCancelButton);
    
    panel.add(buttonPanel);
    
    return panel;
  }

  /**
   * 3. 클라이언트 패널
   */
  private JPanel createClientPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setBorder(new EmptyBorder(50, 50, 50, 50));
    
    JLabel titleLabel = new JLabel("클라이언트 모드", SwingConstants.CENTER);
    titleLabel.setFont(createKoreanFont(Font.BOLD, 24));
    titleLabel.setForeground(new Color(150, 255, 150));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(titleLabel);
    
    panel.add(Box.createVerticalStrut(30));
    
    // 최근 IP 선택
    JLabel recentLabel = new JLabel("최근 접속 IP:", SwingConstants.LEFT);
    recentLabel.setFont(createKoreanFont(Font.PLAIN, 14));
    recentLabel.setForeground(Color.WHITE);
    recentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(recentLabel);
    
    panel.add(Box.createVerticalStrut(5));
    
    List<String> recentIPs = ipManager.getRecentIPs();
    String[] ipArray = recentIPs.isEmpty() ? new String[]{"(최근 기록 없음)"} : recentIPs.toArray(new String[0]);
    recentIPComboBox = new JComboBox<>(ipArray);
    recentIPComboBox.setFont(createKoreanFont(Font.PLAIN, 14));
    recentIPComboBox.setMaximumSize(new Dimension(300, 30));
    recentIPComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
    recentIPComboBox.addActionListener(e -> {
      String selected = (String) recentIPComboBox.getSelectedItem();
      if (selected != null && !selected.equals("(최근 기록 없음)")) {
        clientIPField.setText(selected);
      }
    });
    panel.add(recentIPComboBox);
    
    panel.add(Box.createVerticalStrut(20));
    
    // IP 입력
    JLabel ipLabel = new JLabel("서버 IP 주소:", SwingConstants.LEFT);
    ipLabel.setFont(createKoreanFont(Font.PLAIN, 14));
    ipLabel.setForeground(Color.WHITE);
    ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(ipLabel);
    
    panel.add(Box.createVerticalStrut(5));
    
    clientIPField = new JTextField(ipManager.getMostRecentIP());
    clientIPField.setFont(createKoreanFont(Font.PLAIN, 16));
    clientIPField.setMaximumSize(new Dimension(300, 35));
    clientIPField.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(clientIPField);
    
    panel.add(Box.createVerticalStrut(20));
    
    // 상태 표시
    clientStatusLabel = new JLabel("IP를 입력하고 연결하세요", SwingConstants.CENTER);
    clientStatusLabel.setFont(createKoreanFont(Font.PLAIN, 14));
    clientStatusLabel.setForeground(new Color(255, 255, 255));
    clientStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(clientStatusLabel);
    
    panel.add(Box.createVerticalStrut(20));
    
    // 연결 테스트 버튼
    JButton testButton = createStyledButton("🔍 연결 테스트", new Color(100, 180, 255));
    testButton.setMaximumSize(new Dimension(200, 35));
    testButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    testButton.addActionListener(e -> testConnection());
    panel.add(testButton);
    
    panel.add(Box.createVerticalStrut(20));
    
    // 버튼
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
    buttonPanel.setOpaque(false);
    
    clientConnectButton = createStyledButton("연결", new Color(150, 255, 150));
    clientConnectButton.addActionListener(e -> connectToServer());
    buttonPanel.add(clientConnectButton);
    
    clientCancelButton = createStyledButton("취소", new Color(150, 150, 150));
    clientCancelButton.addActionListener(e -> cancelClient());
    buttonPanel.add(clientCancelButton);
    
    panel.add(buttonPanel);
    
    return panel;
  }

  /**
   * 4. 대기 패널 (연결 후)
   */
  private JPanel createWaitingPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setBorder(new EmptyBorder(50, 50, 50, 50));
    
    waitingMessageLabel = new JLabel("연결됨!", SwingConstants.CENTER);
    waitingMessageLabel.setFont(createKoreanFont(Font.BOLD, 28));
    waitingMessageLabel.setForeground(new Color(100, 255, 100));
    waitingMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(waitingMessageLabel);
    
    panel.add(Box.createVerticalStrut(20));
    
    connectionInfoLabel = new JLabel("", SwingConstants.CENTER);
    connectionInfoLabel.setFont(createKoreanFont(Font.PLAIN, 16));
    connectionInfoLabel.setForeground(new Color(200, 200, 200));
    connectionInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(connectionInfoLabel);
    
    panel.add(Box.createVerticalStrut(10));
    
    latencyLabel = new JLabel("지연: -- ms", SwingConstants.CENTER);
    latencyLabel.setFont(createKoreanFont(Font.PLAIN, 14));
    latencyLabel.setForeground(new Color(255, 220, 100));
    latencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(latencyLabel);
    
    panel.add(Box.createVerticalStrut(30));
    
    // 게임 모드 선택 (서버만)
    gameModePanel = new JPanel();
    gameModePanel.setLayout(new BoxLayout(gameModePanel, BoxLayout.Y_AXIS));
    gameModePanel.setOpaque(false);
    
    gameModeLabel = new JLabel("게임 모드 선택 (서버)", SwingConstants.CENTER);
    gameModeLabel.setFont(createKoreanFont(Font.BOLD, 16));
    gameModeLabel.setForeground(new Color(255, 200, 100));
    gameModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    gameModePanel.add(gameModeLabel);
    
    gameModePanel.add(Box.createVerticalStrut(10));
    
    String[] modes = {"일반 (NORMAL)", "아이템 (ITEM)", "시간 제한 (TIME_LIMIT)"};
    gameModeComboBox = new JComboBox<>(modes);
    gameModeComboBox.setSelectedIndex(1); // 기본: 아이템 모드
    gameModeComboBox.setFont(createKoreanFont(Font.PLAIN, 14));
    gameModeComboBox.setMaximumSize(new Dimension(250, 30));
    gameModeComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
    gameModeComboBox.addActionListener(e -> {
      int index = gameModeComboBox.getSelectedIndex();
      selectedGameMode = index == 0 ? "NORMAL" : index == 1 ? "ITEM" : "TIME_LIMIT";
      sendGameModeToClient();
    });
    gameModePanel.add(gameModeComboBox);
    
    gameModePanel.setVisible(false); // 기본적으로 숨김 (서버만 표시)
    panel.add(gameModePanel);
    
    panel.add(Box.createVerticalStrut(30));
    
    // 준비 버튼
    readyButton = createStyledButton("준비 완료", new Color(100, 255, 100));
    readyButton.setMaximumSize(new Dimension(200, 50));
    readyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    readyButton.addActionListener(e -> toggleReady());
    panel.add(readyButton);
    
    panel.add(Box.createVerticalStrut(20));
    
    // 연결 해제 버튼
    disconnectButton = createStyledButton("연결 해제", new Color(255, 100, 100));
    disconnectButton.setMaximumSize(new Dimension(150, 40));
    disconnectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    disconnectButton.addActionListener(e -> {
      cleanup();
      showModeSelectionPanel();
    });
    panel.add(disconnectButton);
    
    return panel;
  }

  /**
   * 스타일 버튼 생성
   */
  private JButton createStyledButton(String text, Color bgColor) {
    JButton button = new JButton(text);
    button.setFont(createKoreanFont(Font.BOLD, 16));
    button.setForeground(Color.WHITE);
    button.setBackground(bgColor);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setOpaque(true);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(150, 40));
    
    // 호버 효과
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        button.setBackground(bgColor.brighter());
      }
      
      @Override
      public void mouseExited(MouseEvent e) {
        button.setBackground(bgColor);
      }
    });
    
    return button;
  }

  // === 화면 전환 메서드 ===
  
  private void showModeSelectionPanel() {
    CardLayout cl = (CardLayout) ((JPanel) modeSelectionPanel.getParent()).getLayout();
    cl.show((JPanel) modeSelectionPanel.getParent(), "MODE_SELECTION");
  }

  private void showServerPanel() {
    isServer = true;
    serverIPLabel.setText("IP: " + P2PServer.getLocalIPAddress());
    serverStatusLabel.setText("서버를 시작하세요");
    
    CardLayout cl = (CardLayout) ((JPanel) serverPanel.getParent()).getLayout();
    cl.show((JPanel) serverPanel.getParent(), "SERVER");
  }

  private void showClientPanel() {
    isServer = false;
    
    // 최근 IP 목록 업데이트
    List<String> recentIPs = ipManager.getRecentIPs();
    recentIPComboBox.removeAllItems();
    if (recentIPs.isEmpty()) {
      recentIPComboBox.addItem("(최근 기록 없음)");
    } else {
      for (String ip : recentIPs) {
        recentIPComboBox.addItem(ip);
      }
    }
    
    CardLayout cl = (CardLayout) ((JPanel) clientPanel.getParent()).getLayout();
    cl.show((JPanel) clientPanel.getParent(), "CLIENT");
  }

  private void showWaitingPanel() {
    // 게임 모드 패널은 서버만 표시
    gameModePanel.setVisible(isServer);
    
    String role = isServer ? "서버 (호스트)" : "클라이언트";
    connectionInfoLabel.setText("역할: " + role);
    
    // 지연 업데이트 타이머 시작
    startLatencyUpdateTimer();
    
    CardLayout cl = (CardLayout) ((JPanel) waitingPanel.getParent()).getLayout();
    cl.show((JPanel) waitingPanel.getParent(), "WAITING");
  }

  // === 서버 관련 메서드 ===
  
  private void startServer() {
    serverStartButton.setEnabled(false);
    serverStatusLabel.setText("서버 시작 중...");
    
    try {
      server = new P2PServer(DEFAULT_PORT);
      
      server.setOnClientConnected(() -> {
        SwingUtilities.invokeLater(() -> {
          isConnected = true;
          serverStatusLabel.setText("✅ 클라이언트 연결됨!");
          showWaitingPanel();
          setupNetworkHandlers(server.getNetworkManager());
        });
      });
      
      server.setOnClientDisconnected(() -> {
        SwingUtilities.invokeLater(() -> {
          handleDisconnect();
        });
      });
      
      server.start();
      
      // 서버 시작 성공 메시지
      String localIP = P2PServer.getLocalIPAddress();
      serverStatusLabel.setText("✅ 대기 중... (IP: " + localIP + ")");
      serverCancelButton.setEnabled(true);
      
      // 연결 가이드 표시
      showServerGuide(localIP);
      
    } catch (IOException e) {
      serverStatusLabel.setText("❌ 서버 시작 실패");
      serverStartButton.setEnabled(true);
      
      String errorMsg = "서버를 시작할 수 없습니다.\n\n";
      if (e.getMessage().contains("Address already in use")) {
        errorMsg += "포트 " + DEFAULT_PORT + "가 이미 사용 중입니다.\n";
        errorMsg += "다른 프로그램을 종료하거나 잠시 후 다시 시도하세요.";
      } else {
        errorMsg += "오류: " + e.getMessage();
      }
      
      JOptionPane.showMessageDialog(this, errorMsg, "서버 시작 오류", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  /**
   * 서버 연결 가이드 표시
   */
  private void showServerGuide(String ip) {
    String guide = String.format(
      "🌐 서버가 시작되었습니다!\n\n" +
      "📍 서버 IP 주소: %s\n" +
      "📍 포트: %d\n\n" +
      "✅ 클라이언트(상대방)가 접속할 수 있도록:\n" +
      "   1. 위 IP 주소를 상대방에게 알려주세요\n" +
      "   2. 상대방이 '클라이언트' 모드를 선택\n" +
      "   3. 상대방이 IP 주소 입력 후 '연결' 클릭\n\n" +
      "⚠️ 주의: 같은 WiFi에 연결되어 있어야 합니다!",
      ip, DEFAULT_PORT
    );
    
    JOptionPane.showMessageDialog(this, guide, "서버 시작 완료", JOptionPane.INFORMATION_MESSAGE);
  }

  private void cancelServer() {
    if (server != null) {
      server.stop();
      server = null;
    }
    showModeSelectionPanel();
  }

  // === 클라이언트 관련 메서드 ===
  
  private void connectToServer() {
    String serverIP = clientIPField.getText().trim();
    
    if (!RecentIPManager.isValidIP(serverIP)) {
      clientStatusLabel.setText("잘못된 IP 주소 형식입니다");
      JOptionPane.showMessageDialog(this, "올바른 IP 주소를 입력하세요.\n예: 192.168.0.1", "오류", JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    clientConnectButton.setEnabled(false);
    clientStatusLabel.setText("연결 중...");
    
    try {
      client = new P2PClient(serverIP, DEFAULT_PORT);
      
      client.setOnConnected(() -> {
        SwingUtilities.invokeLater(() -> {
          isConnected = true;
          ipManager.addIP(serverIP); // 최근 IP에 추가
          showWaitingPanel();
          setupNetworkHandlers(client.getNetworkManager());
        });
      });
      
      client.setOnDisconnected(() -> {
        SwingUtilities.invokeLater(() -> {
          handleDisconnect();
        });
      });
      
      client.connect();
      
    } catch (IOException e) {
      clientStatusLabel.setText("연결 실패: " + e.getMessage());
      clientConnectButton.setEnabled(true);
      
      // 자세한 에러 메시지 생성
      String errorDetails = getDetailedErrorMessage(e, serverIP);
      JOptionPane.showMessageDialog(this, errorDetails, "연결 오류", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void cancelClient() {
    if (client != null) {
      client.disconnect();
      client = null;
    }
    showModeSelectionPanel();
  }
  
  /**
   * 연결 테스트 (실제 연결 전 진단)
   */
  private void testConnection() {
    String serverIP = clientIPField.getText().trim();
    
    if (!RecentIPManager.isValidIP(serverIP)) {
      clientStatusLabel.setText("잘못된 IP 주소 형식입니다");
      JOptionPane.showMessageDialog(this, "올바른 IP 주소를 입력하세요.\n예: 192.168.0.1", "오류", JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    clientStatusLabel.setText("테스트 중...");
    clientConnectButton.setEnabled(false);
    
    // 백그라운드에서 테스트 수행
    new Thread(() -> {
      StringBuilder result = new StringBuilder();
      result.append("🔍 연결 테스트 결과\n\n");
      result.append("대상 IP: ").append(serverIP).append("\n");
      result.append("포트: ").append(DEFAULT_PORT).append("\n\n");
      
      boolean allPassed = true;
      
      // 1. DNS/IP 확인
      result.append("1️⃣ IP 주소 확인... ");
      try {
        java.net.InetAddress addr = java.net.InetAddress.getByName(serverIP);
        result.append("✅ 통과\n");
        result.append("   해석된 주소: ").append(addr.getHostAddress()).append("\n\n");
      } catch (Exception e) {
        result.append("❌ 실패\n");
        result.append("   오류: ").append(e.getMessage()).append("\n\n");
        allPassed = false;
      }
      
      // 2. Ping 테스트
      result.append("2️⃣ 호스트 도달 가능 여부... ");
      try {
        java.net.InetAddress addr = java.net.InetAddress.getByName(serverIP);
        boolean reachable = addr.isReachable(3000); // 3초 타임아웃
        if (reachable) {
          result.append("✅ 통과\n");
          result.append("   호스트 응답 확인됨\n\n");
        } else {
          result.append("⚠️ 경고\n");
          result.append("   호스트가 응답하지 않음 (방화벽일 수 있음)\n\n");
        }
      } catch (Exception e) {
        result.append("❌ 실패\n");
        result.append("   오류: ").append(e.getMessage()).append("\n\n");
        allPassed = false;
      }
      
      // 3. 포트 연결 테스트
      result.append("3️⃣ 포트 ").append(DEFAULT_PORT).append(" 연결 테스트... ");
      try {
        java.net.Socket testSocket = new java.net.Socket();
        testSocket.connect(new java.net.InetSocketAddress(serverIP, DEFAULT_PORT), 5000);
        testSocket.close();
        result.append("✅ 통과\n");
        result.append("   포트 연결 성공! 서버가 실행 중입니다.\n\n");
      } catch (java.net.ConnectException e) {
        result.append("❌ 실패\n");
        result.append("   연결 거부됨. 서버가 시작되지 않았을 수 있습니다.\n\n");
        allPassed = false;
      } catch (java.net.SocketTimeoutException e) {
        result.append("❌ 실패\n");
        result.append("   연결 시간 초과. 방화벽을 확인하세요.\n\n");
        allPassed = false;
      } catch (Exception e) {
        result.append("❌ 실패\n");
        result.append("   오류: ").append(e.getMessage()).append("\n\n");
        allPassed = false;
      }
      
      // 결과 요약
      result.append("━━━━━━━━━━━━━━━━━━━━\n");
      if (allPassed) {
        result.append("✅ 모든 테스트 통과!\n");
        result.append("'연결' 버튼을 눌러 게임을 시작하세요.");
      } else {
        result.append("⚠️ 일부 테스트 실패\n\n");
        result.append("해결 방법:\n");
        result.append("• 서버가 실행 중인지 확인\n");
        result.append("• 양쪽 PC가 같은 WiFi에 연결되어 있는지 확인\n");
        result.append("• 방화벽 설정 확인\n");
        result.append("• IP 주소가 정확한지 다시 확인");
      }
      
      final String finalResult = result.toString();
      final boolean finalPassed = allPassed;
      
      SwingUtilities.invokeLater(() -> {
        clientStatusLabel.setText(finalPassed ? "✅ 테스트 통과" : "⚠️ 테스트 실패");
        clientConnectButton.setEnabled(true);
        JOptionPane.showMessageDialog(
          this, 
          finalResult, 
          "연결 테스트 결과", 
          finalPassed ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
        );
      });
    }).start();
  }

  // === 네트워크 핸들러 설정 ===
  
  private void setupNetworkHandlers(NetworkManager networkManager) {
    networkManager.setMessageHandler(message -> {
      SwingUtilities.invokeLater(() -> handleMessage(message));
    });
  }

  private void handleMessage(GameStateMessage message) {
    switch (message.getType()) {
      case MODE_SELECT:
        // 서버가 보낸 게임 모드
        selectedGameMode = message.getGameMode();
        updateGameModeDisplay();
        break;
        
      case READY:
        // 상대방 준비 완료
        opponentReady = true;
        checkStartGame();
        break;
        
      case GAME_START:
        // 게임 시작
        startGame();
        break;
        
      default:
        break;
    }
  }

  // === 게임 모드 관련 ===
  
  private void sendGameModeToClient() {
    if (!isServer || !isConnected) return;
    
    GameStateMessage msg = new GameStateMessage(GameStateMessage.MessageType.MODE_SELECT);
    msg.setGameMode(selectedGameMode);
    server.getNetworkManager().sendMessage(msg);
  }

  private void updateGameModeDisplay() {
    // 클라이언트가 받은 게임 모드 표시
    String modeText = selectedGameMode.equals("NORMAL") ? "일반" : 
                      selectedGameMode.equals("ITEM") ? "아이템" : "시간 제한";
    gameModeLabel.setText("게임 모드: " + modeText + " (서버 선택)");
  }

  // === 준비 및 게임 시작 ===
  
  private void toggleReady() {
    isReady = !isReady;
    
    if (isReady) {
      readyButton.setText("준비 취소");
      readyButton.setBackground(new Color(255, 150, 100));
      
      // 상대방에게 준비 상태 전송
      GameStateMessage msg = new GameStateMessage(GameStateMessage.MessageType.READY);
      if (isServer) {
        server.getNetworkManager().sendMessage(msg);
      } else {
        client.getNetworkManager().sendMessage(msg);
      }
      
      checkStartGame();
    } else {
      readyButton.setText("준비 완료");
      readyButton.setBackground(new Color(100, 255, 100));
    }
  }

  private void checkStartGame() {
    if (isReady && opponentReady) {
      // 양측 준비 완료 → 게임 시작
      GameStateMessage msg = new GameStateMessage(GameStateMessage.MessageType.GAME_START);
      if (isServer) {
        server.getNetworkManager().sendMessage(msg);
      }
      startGame();
    }
  }

  private void startGame() {
    stopLatencyUpdateTimer();
    
    // P2P 게임 화면으로 전환
    System.out.println("[p2p] Starting P2P game with mode: " + selectedGameMode);
    
    // 게임 모드를 시스템 속성에 저장
    System.setProperty("tetris.p2p.mode", selectedGameMode);
    System.setProperty("tetris.p2p.isServer", String.valueOf(isServer));
    
    // p2pGame 화면으로 전환 (아직 미구현)
    // TODO: p2pGame 화면 구현 후 연결
    JOptionPane.showMessageDialog(this, "게임 시작! (p2pGame 화면 구현 예정)", "알림", JOptionPane.INFORMATION_MESSAGE);
  }

  // === 연결 해제 처리 ===
  
  private void handleDisconnect() {
    stopLatencyUpdateTimer();
    
    isConnected = false;
    isReady = false;
    opponentReady = false;
    
    JOptionPane.showMessageDialog(this, 
      "연결이 끊어졌습니다.\nP2P 대전 모드 초기 화면으로 돌아갑니다.", 
      "연결 끊김", 
      JOptionPane.WARNING_MESSAGE);
    
    cleanup();
    showModeSelectionPanel();
  }

  // === 지연 업데이트 타이머 ===
  
  private void startLatencyUpdateTimer() {
    latencyUpdateTimer = new Timer(500, e -> updateLatencyDisplay());
    latencyUpdateTimer.start();
  }

  private void stopLatencyUpdateTimer() {
    if (latencyUpdateTimer != null) {
      latencyUpdateTimer.stop();
      latencyUpdateTimer = null;
    }
  }

  private void updateLatencyDisplay() {
    NetworkManager nm = isServer ? server.getNetworkManager() : client.getNetworkManager();
    if (nm == null || !nm.isConnected()) {
      latencyLabel.setText("지연: -- ms");
      latencyLabel.setForeground(new Color(255, 220, 100));
      return;
    }
    
    long latency = nm.getLatency();
    latencyLabel.setText("지연: " + latency + " ms");
    
    // 색상으로 지연 상태 표시
    if (latency < 50) {
      latencyLabel.setForeground(new Color(100, 255, 100)); // 초록
    } else if (latency < 150) {
      latencyLabel.setForeground(new Color(255, 220, 100)); // 노랑
    } else if (latency < 250) {
      latencyLabel.setForeground(new Color(255, 150, 100)); // 주황
    } else {
      latencyLabel.setForeground(new Color(255, 100, 100)); // 빨강
    }
    
    // 랙 표시
    if (nm.isLagging()) {
      waitingMessageLabel.setText("⚠️ 연결 지연 중...");
      waitingMessageLabel.setForeground(new Color(255, 150, 100));
    } else {
      waitingMessageLabel.setText("✅ 연결됨!");
      waitingMessageLabel.setForeground(new Color(100, 255, 100));
    }
  }

  // === 정리 ===
  
  /**
   * 자세한 에러 메시지 생성
   */
  private String getDetailedErrorMessage(IOException e, String serverIP) {
    StringBuilder sb = new StringBuilder();
    sb.append("서버에 연결할 수 없습니다.\n\n");
    
    String errorMsg = e.getMessage().toLowerCase();
    
    // 에러 타입별 상세 설명
    if (errorMsg.contains("host is down") || errorMsg.contains("no route to host")) {
      sb.append("🔴 문제: 서버를 찾을 수 없습니다\n\n");
      sb.append("가능한 원인:\n");
      sb.append("1. 서버가 실행되지 않음\n");
      sb.append("   → 상대방 PC에서 서버를 시작했는지 확인\n\n");
      sb.append("2. IP 주소가 잘못됨\n");
      sb.append("   → 입력한 IP: ").append(serverIP).append("\n");
      sb.append("   → 서버 화면에 표시된 IP와 일치하는지 확인\n\n");
      sb.append("3. 다른 네트워크에 연결됨\n");
      sb.append("   → 양쪽 PC가 같은 WiFi/공유기에 연결되어야 함\n\n");
      sb.append("4. 방화벽이 연결을 차단\n");
      sb.append("   → 방화벽 설정에서 포트 ").append(DEFAULT_PORT).append(" 허용\n");
      
    } else if (errorMsg.contains("connection refused")) {
      sb.append("🔴 문제: 서버가 연결을 거부했습니다\n\n");
      sb.append("가능한 원인:\n");
      sb.append("1. 서버가 아직 시작되지 않음\n");
      sb.append("   → 상대방이 '서버 시작' 버튼을 눌렀는지 확인\n\n");
      sb.append("2. 포트가 이미 사용 중\n");
      sb.append("   → 서버를 다시 시작해보세요\n\n");
      sb.append("3. 이미 다른 클라이언트가 접속 중\n");
      sb.append("   → 서버는 한 번에 한 명만 접속 가능\n");
      
    } else if (errorMsg.contains("timeout") || errorMsg.contains("timed out")) {
      sb.append("🔴 문제: 연결 시간 초과\n\n");
      sb.append("가능한 원인:\n");
      sb.append("1. 네트워크가 너무 느림\n");
      sb.append("   → 인터넷 연결 상태 확인\n\n");
      sb.append("2. 서버가 응답하지 않음\n");
      sb.append("   → 서버 프로그램이 정상 작동 중인지 확인\n\n");
      sb.append("3. 방화벽이 패킷을 차단\n");
      sb.append("   → 잠시 방화벽을 끄고 시도해보세요\n");
      
    } else if (errorMsg.contains("network is unreachable")) {
      sb.append("🔴 문제: 네트워크에 연결할 수 없습니다\n\n");
      sb.append("가능한 원인:\n");
      sb.append("1. WiFi/이더넷이 연결되지 않음\n");
      sb.append("   → 네트워크 연결 상태 확인\n\n");
      sb.append("2. 라우터 문제\n");
      sb.append("   → 공유기를 재시작해보세요\n\n");
      sb.append("3. 잘못된 IP 범위\n");
      sb.append("   → 같은 네트워크(192.168.x.x)인지 확인\n");
      
    } else {
      sb.append("🔴 알 수 없는 오류가 발생했습니다\n\n");
      sb.append("오류 메시지: ").append(e.getMessage()).append("\n\n");
      sb.append("일반적인 해결 방법:\n");
      sb.append("1. 양쪽 PC가 같은 WiFi에 연결되어 있는지 확인\n");
      sb.append("2. 서버가 먼저 시작되었는지 확인\n");
      sb.append("3. IP 주소가 정확한지 확인\n");
      sb.append("4. 방화벽을 일시적으로 끄고 시도\n");
    }
    
    sb.append("\n💡 팁: 같은 WiFi에 연결된 경우에만 작동합니다!");
    
    return sb.toString();
  }
  
  private void cleanup() {
    stopLatencyUpdateTimer();
    
    if (server != null) {
      server.stop();
      server = null;
    }
    
    if (client != null) {
      client.disconnect();
      client = null;
    }
    
    isConnected = false;
    isReady = false;
    opponentReady = false;
  }

  /**
   * 화면이 표시될 때 호출 (ScreenController에서)
   */
  public void onShow() {
    // 초기 화면으로 리셋
    cleanup();
    showModeSelectionPanel();
  }
}
