package se.tetris.team5.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * P2P 서버 (호스트)
 * 클라이언트 연결을 대기하고 수락
 */
public class P2PServer {
  private ServerSocket serverSocket;
  private NetworkManager networkManager;
  private ExecutorService executor;
  private volatile boolean running = false;
  private int port;
  
  // 콜백
  private Runnable onClientConnected;
  private Runnable onClientDisconnected;

  public P2PServer(int port) {
    this.port = port;
    this.executor = Executors.newSingleThreadExecutor();
  }

  /**
   * 서버 시작 및 클라이언트 대기
   */
  public void start() throws IOException {
    System.out.println("[P2PServer] Starting server on port " + port);
    serverSocket = new ServerSocket(port);
    serverSocket.setSoTimeout(0); // 무한 대기
    running = true;
    
    executor.submit(this::acceptClient);
    
    System.out.println("[P2PServer] Server started. Waiting for client...");
  }

  /**
   * 클라이언트 연결 수락
   */
  private void acceptClient() {
    while (running) {
      try {
        System.out.println("[P2PServer] Waiting for client connection...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("[P2PServer] Client connected from " + clientSocket.getInetAddress());
        
        // NetworkManager 초기화
        networkManager = new NetworkManager();
        networkManager.initWithSocket(clientSocket);
        
        // 연결 종료 콜백 설정
        networkManager.setOnDisconnected(() -> {
          if (onClientDisconnected != null) {
            onClientDisconnected.run();
          }
        });
        
        // 클라이언트 연결 콜백
        if (onClientConnected != null) {
          onClientConnected.run();
        }
        
        break; // 한 명만 수락
      } catch (IOException e) {
        if (running) {
          System.err.println("[P2PServer] Error accepting client: " + e.getMessage());
        }
      }
    }
  }

  /**
   * 서버 중지
   */
  public void stop() {
    System.out.println("[P2PServer] Stopping server...");
    running = false;
    
    if (networkManager != null) {
      networkManager.disconnect();
    }
    
    try {
      if (serverSocket != null) {
        serverSocket.close();
      }
    } catch (IOException e) {
      System.err.println("[P2PServer] Error closing server socket: " + e.getMessage());
    }
    
    executor.shutdownNow();
    System.out.println("[P2PServer] Server stopped");
  }

  /**
   * 로컬 IP 주소 가져오기 (클라이언트에게 제공)
   */
  public static String getLocalIPAddress() {
    try {
      // 네트워크 인터페이스 순회하여 활성 IP 찾기
      java.util.Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface iface = interfaces.nextElement();
        
        // 루프백, 비활성 인터페이스 제외
        if (iface.isLoopback() || !iface.isUp()) {
          continue;
        }
        
        java.util.Enumeration<InetAddress> addresses = iface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          
          // IPv4만 (IPv6 제외)
          if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
            return addr.getHostAddress();
          }
        }
      }
    } catch (SocketException e) {
      System.err.println("[P2PServer] Error getting IP address: " + e.getMessage());
    }
    
    // 실패 시 루프백 반환
    return "127.0.0.1";
  }

  // === Getters ===
  
  public NetworkManager getNetworkManager() {
    return networkManager;
  }

  public boolean isRunning() {
    return running;
  }

  public boolean hasClient() {
    return networkManager != null && networkManager.isConnected();
  }

  // === Setters (콜백) ===
  
  public void setOnClientConnected(Runnable callback) {
    this.onClientConnected = callback;
  }

  public void setOnClientDisconnected(Runnable callback) {
    this.onClientDisconnected = callback;
  }
}
