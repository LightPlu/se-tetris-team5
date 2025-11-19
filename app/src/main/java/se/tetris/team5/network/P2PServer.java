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
   * 우선순위: 192.168.x.x > 10.x.x.x > 172.16-31.x.x > 기타
   */
  public static String getLocalIPAddress() {
    try {
      String bestIP = null;
      int bestPriority = 0;
      
      // 네트워크 인터페이스 순회하여 활성 IP 찾기
      java.util.Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface iface = interfaces.nextElement();
        
        // 루프백, 비활성, 가상 인터페이스 제외
        if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) {
          continue;
        }
        
        java.util.Enumeration<InetAddress> addresses = iface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          
          // IPv4만 (IPv6 제외)
          if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
            String ip = addr.getHostAddress();
            int priority = getIPPriority(ip);
            
            System.out.println("[P2PServer] Found IP: " + ip + 
                             " on " + iface.getDisplayName() + 
                             " (priority: " + priority + ")");
            
            if (priority > bestPriority) {
              bestPriority = priority;
              bestIP = ip;
            }
          }
        }
      }
      
      if (bestIP != null) {
        System.out.println("[P2PServer] Selected IP: " + bestIP);
        return bestIP;
      }
      
    } catch (SocketException e) {
      System.err.println("[P2PServer] Error getting IP address: " + e.getMessage());
    }
    
    // 실패 시 루프백 반환
    System.err.println("[P2PServer] No suitable IP found, returning loopback");
    return "127.0.0.1";
  }
  
  /**
   * IP 주소의 우선순위 계산
   * 높을수록 좋음
   */
  private static int getIPPriority(String ip) {
    // 192.168.x.x (가정용 WiFi) - 최우선
    if (ip.startsWith("192.168.")) {
      return 100;
    }
    
    // 10.x.x.x (일부 기업/학교 네트워크)
    if (ip.startsWith("10.")) {
      return 90;
    }
    
    // 172.16.x.x ~ 172.31.x.x (일부 기업 네트워크)
    if (ip.startsWith("172.")) {
      try {
        int second = Integer.parseInt(ip.split("\\.")[1]);
        if (second >= 16 && second <= 31) {
          return 80;
        }
      } catch (Exception e) {
        // 파싱 실패 시 무시
      }
    }
    
    // 169.254.x.x (APIPA, 자동 할당) - 낮은 우선순위
    if (ip.startsWith("169.254.")) {
      return 10;
    }
    
    // 기타 (공인 IP 등) - 중간 우선순위
    return 50;
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
