package se.tetris.team5.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * P2P 네트워크 통신 관리자
 * Socket 기반 통신, 메시지 송수신, 연결 관리
 */
public class NetworkManager {
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private ExecutorService executor;
  private Consumer<GameStateMessage> messageHandler;
  private Consumer<String> errorHandler;
  
  private volatile boolean connected = false;
  private volatile boolean running = false;
  
  // 네트워크 품질 측정
  private volatile long lastReceivedTime;
  private volatile long lastPingTime;
  private volatile long latency = 0; // 밀리초
  
  // 연결 상태 콜백
  private Runnable onConnected;
  private Runnable onDisconnected;
  
  // 타임아웃 설정
  private static final int CONNECTION_TIMEOUT_MS = 15000; // 15초 (네트워크가 느릴 수 있음)
  private static final int SOCKET_TIMEOUT_MS = 5000;      // 5초
  private static final int PING_INTERVAL_MS = 1000;       // 1초마다 핑
  private static final int MAX_NO_RESPONSE_MS = 5000;     // 5초 동안 응답 없으면 끊김

  public NetworkManager() {
    this.executor = Executors.newFixedThreadPool(3);
    this.lastReceivedTime = System.currentTimeMillis();
  }

  /**
   * 서버로 연결 (클라이언트 모드)
   */
  public void connectToServer(String host, int port) throws IOException {
    System.out.println("[NetworkManager] Attempting connection to " + host + ":" + port);
    System.out.println("[NetworkManager] Connection timeout: " + CONNECTION_TIMEOUT_MS + "ms");
    
    try {
      socket = new Socket();
      
      // 연결 시도 전 호스트 도달 가능 여부 확인
      System.out.println("[NetworkManager] Testing host reachability...");
      InetAddress addr = InetAddress.getByName(host);
      System.out.println("[NetworkManager] Host resolved: " + addr.getHostAddress());
      
      // 실제 연결 시도
      System.out.println("[NetworkManager] Connecting to socket...");
      socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT_MS);
      
      System.out.println("[NetworkManager] Socket connected successfully");
      socket.setSoTimeout(SOCKET_TIMEOUT_MS);
      
      initStreams();
      connected = true;
      running = true;
      
      // 연결 성공 콜백
      if (onConnected != null) {
        executor.submit(onConnected);
      }
      
      // 수신 스레드 시작
      startReceiving();
      
      // 핑 스레드 시작
      startPingThread();
      
      // 연결 감시 스레드 시작
      startConnectionMonitor();
      
      System.out.println("[NetworkManager] Connection established and threads started");
      
    } catch (java.net.UnknownHostException e) {
      System.err.println("[NetworkManager] Unknown host: " + host);
      throw new IOException("호스트를 찾을 수 없습니다: " + host, e);
    } catch (java.net.ConnectException e) {
      System.err.println("[NetworkManager] Connection refused: " + e.getMessage());
      throw new IOException("연결이 거부되었습니다. 서버가 실행 중인지 확인하세요.", e);
    } catch (java.net.SocketTimeoutException e) {
      System.err.println("[NetworkManager] Connection timeout after " + CONNECTION_TIMEOUT_MS + "ms");
      throw new IOException("연결 시간이 초과되었습니다. 네트워크 상태를 확인하세요.", e);
    } catch (IOException e) {
      System.err.println("[NetworkManager] Connection failed: " + e.getMessage());
      throw e;
    }
  }

  /**
   * 기존 소켓으로 초기화 (서버 모드)
   */
  public void initWithSocket(Socket clientSocket) throws IOException {
    System.out.println("[NetworkManager] Initializing with client socket");
    this.socket = clientSocket;
    socket.setSoTimeout(SOCKET_TIMEOUT_MS);
    initStreams();
    connected = true;
    running = true;
    
    // 연결 성공 콜백
    if (onConnected != null) {
      executor.submit(onConnected);
    }
    
    // 수신 스레드 시작
    startReceiving();
    
    // 핑 스레드 시작
    startPingThread();
    
    // 연결 감시 스레드 시작
    startConnectionMonitor();
    
    System.out.println("[NetworkManager] Client socket initialized");
  }

  private void initStreams() throws IOException {
    // OutputStream 먼저 생성 (헤더 전송)
    out = new ObjectOutputStream(socket.getOutputStream());
    out.flush();
    
    // InputStream 생성
    in = new ObjectInputStream(socket.getInputStream());
  }

  /**
   * 메시지 전송
   */
  public void sendMessage(GameStateMessage message) {
    if (!connected || out == null) {
      System.err.println("[NetworkManager] Cannot send message: not connected");
      return;
    }
    
    executor.submit(() -> {
      try {
        synchronized (out) {
          out.writeObject(message);
          out.flush();
          out.reset(); // 객체 캐시 초기화 (메모리 누수 방지)
        }
        // System.out.println("[NetworkManager] Sent: " + message.getType());
      } catch (IOException e) {
        System.err.println("[NetworkManager] Failed to send message: " + e.getMessage());
        handleDisconnect();
      }
    });
  }

  /**
   * 메시지 수신 스레드
   */
  private void startReceiving() {
    executor.submit(() -> {
      System.out.println("[NetworkManager] Receive thread started");
      while (running && connected) {
        try {
          GameStateMessage message = (GameStateMessage) in.readObject();
          lastReceivedTime = System.currentTimeMillis();
          
          // 핑/퐁 처리
          if (message.getType() == GameStateMessage.MessageType.PING) {
            sendPong();
          } else if (message.getType() == GameStateMessage.MessageType.PONG) {
            latency = message.getLatency();
            // System.out.println("[NetworkManager] Latency: " + latency + "ms");
          } else {
            // 일반 메시지 처리
            if (messageHandler != null) {
              messageHandler.accept(message);
            }
          }
        } catch (SocketTimeoutException e) {
          // 타임아웃은 정상 (주기적으로 발생)
          continue;
        } catch (IOException | ClassNotFoundException e) {
          if (running) {
            System.err.println("[NetworkManager] Receive error: " + e.getMessage());
            handleDisconnect();
          }
          break;
        }
      }
      System.out.println("[NetworkManager] Receive thread stopped");
    });
  }

  /**
   * 핑 전송 스레드 (주기적으로 지연 측정)
   */
  private void startPingThread() {
    executor.submit(() -> {
      System.out.println("[NetworkManager] Ping thread started");
      while (running && connected) {
        try {
          Thread.sleep(PING_INTERVAL_MS);
          sendPing();
        } catch (InterruptedException e) {
          break;
        }
      }
      System.out.println("[NetworkManager] Ping thread stopped");
    });
  }

  /**
   * 연결 감시 스레드 (끊김 감지)
   */
  private void startConnectionMonitor() {
    executor.submit(() -> {
      System.out.println("[NetworkManager] Connection monitor started");
      while (running && connected) {
        try {
          Thread.sleep(1000);
          
          // 일정 시간 동안 응답 없으면 끊김으로 판단
          long timeSinceLastReceived = System.currentTimeMillis() - lastReceivedTime;
          if (timeSinceLastReceived > MAX_NO_RESPONSE_MS) {
            System.err.println("[NetworkManager] No response for " + timeSinceLastReceived + "ms. Connection lost.");
            handleDisconnect();
            break;
          }
        } catch (InterruptedException e) {
          break;
        }
      }
      System.out.println("[NetworkManager] Connection monitor stopped");
    });
  }

  /**
   * 핑 전송
   */
  private void sendPing() {
    GameStateMessage ping = new GameStateMessage(GameStateMessage.MessageType.PING);
    lastPingTime = System.currentTimeMillis();
    sendMessage(ping);
  }

  /**
   * 퐁 전송
   */
  private void sendPong() {
    GameStateMessage pong = new GameStateMessage(GameStateMessage.MessageType.PONG);
    sendMessage(pong);
  }

  /**
   * 연결 종료 처리
   */
  private void handleDisconnect() {
    if (!connected) return;
    
    System.out.println("[NetworkManager] Handling disconnect");
    connected = false;
    running = false;
    
    // 연결 종료 콜백
    if (onDisconnected != null) {
      executor.submit(onDisconnected);
    }
    
    // 소켓 종료
    closeSocket();
  }

  /**
   * 소켓 종료
   */
  private void closeSocket() {
    try {
      if (out != null) out.close();
      if (in != null) in.close();
      if (socket != null) socket.close();
    } catch (IOException e) {
      System.err.println("[NetworkManager] Error closing socket: " + e.getMessage());
    }
  }

  /**
   * 연결 종료
   */
  public void disconnect() {
    System.out.println("[NetworkManager] Disconnecting...");
    running = false;
    connected = false;
    
    // DISCONNECT 메시지 전송
    try {
      if (out != null) {
        GameStateMessage disconnectMsg = new GameStateMessage(GameStateMessage.MessageType.DISCONNECT);
        synchronized (out) {
          out.writeObject(disconnectMsg);
          out.flush();
        }
      }
    } catch (IOException e) {
      // 이미 끊긴 경우 무시
    }
    
    closeSocket();
    executor.shutdownNow();
    
    System.out.println("[NetworkManager] Disconnected");
  }

  // === Getters ===
  
  public boolean isConnected() {
    return connected;
  }

  public long getLatency() {
    return latency;
  }

  public boolean isLagging() {
    // 200ms 이상이면 랙으로 판단
    return latency > 200;
  }

  // === Setters (콜백) ===
  
  public void setMessageHandler(Consumer<GameStateMessage> handler) {
    this.messageHandler = handler;
  }

  public void setErrorHandler(Consumer<String> handler) {
    this.errorHandler = handler;
  }

  public void setOnConnected(Runnable callback) {
    this.onConnected = callback;
  }

  public void setOnDisconnected(Runnable callback) {
    this.onDisconnected = callback;
  }
}
