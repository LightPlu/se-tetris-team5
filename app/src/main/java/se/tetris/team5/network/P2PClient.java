package se.tetris.team5.network;

import java.io.IOException;

/**
 * P2P 클라이언트
 * 서버에 연결하여 게임 진행
 */
public class P2PClient {
  private NetworkManager networkManager;
  private String serverHost;
  private int serverPort;
  
  // 콜백
  private Runnable onConnected;
  private Runnable onDisconnected;

  public P2PClient(String host, int port) {
    this.serverHost = host;
    this.serverPort = port;
  }

  /**
   * 서버에 연결
   */
  public void connect() throws IOException {
    System.out.println("[P2PClient] Connecting to " + serverHost + ":" + serverPort);
    
    networkManager = new NetworkManager();
    
    // 연결 종료 콜백 설정
    networkManager.setOnConnected(() -> {
      if (onConnected != null) {
        onConnected.run();
      }
    });
    
    networkManager.setOnDisconnected(() -> {
      if (onDisconnected != null) {
        onDisconnected.run();
      }
    });
    
    // 서버 연결
    networkManager.connectToServer(serverHost, serverPort);
    
    System.out.println("[P2PClient] Connected to server");
  }

  /**
   * 연결 종료
   */
  public void disconnect() {
    System.out.println("[P2PClient] Disconnecting...");
    if (networkManager != null) {
      networkManager.disconnect();
    }
  }

  // === Getters ===
  
  public NetworkManager getNetworkManager() {
    return networkManager;
  }

  public boolean isConnected() {
    return networkManager != null && networkManager.isConnected();
  }

  // === Setters (콜백) ===
  
  public void setOnConnected(Runnable callback) {
    this.onConnected = callback;
  }

  public void setOnDisconnected(Runnable callback) {
    this.onDisconnected = callback;
  }
}
