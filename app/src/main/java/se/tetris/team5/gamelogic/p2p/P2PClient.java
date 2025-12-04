package se.tetris.team5.gamelogic.p2p;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * P2P 클라이언트
 * 서버에 접속하여 게임 상태를 주고받음
 */
public class P2PClient {
    
    private static final int PORT = 15555;
    private static final int CONNECTION_TIMEOUT = 5000; // 5초
    private static final int PING_INTERVAL = 1000; // 1초마다 핑
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private boolean isConnected = false;
    private boolean isRunning = false;
    
    private P2PEventListener listener;
    
    private ExecutorService executorService;
    private ScheduledExecutorService pingScheduler;
    
    private long lastReceivedTime;
    private volatile boolean lagDetected = false;
    private long pingStartTime = 0;
    private long currentLatency = 0;
    
    public interface P2PEventListener {
        void onConnected(String serverAddress);
        void onPacketReceived(GameStatePacket packet);
        void onDisconnected(String reason);
        void onLagDetected(boolean isLagging);
        void onError(String error);
    }
    
    public P2PClient(P2PEventListener listener) {
        this.listener = listener;
        this.executorService = Executors.newSingleThreadExecutor();
        this.pingScheduler = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * 서버에 연결
     */
    public void connect(String serverIP) {
        isRunning = true;
        executorService.submit(() -> {
            try {
                System.out.println("[P2P Client] 서버 연결 시도: " + serverIP + ":" + PORT);
                
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIP, PORT), CONNECTION_TIMEOUT);
                socket.setSoTimeout(CONNECTION_TIMEOUT);
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true); // Nagle 알고리즘 비활성화 (지연 감소)
                
                System.out.println("[P2P Client] 서버에 연결됨");
                
                // 스트림 초기화
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
                
                isConnected = true;
                lastReceivedTime = System.currentTimeMillis();
                
                if (listener != null) {
                    listener.onConnected(serverIP);
                }
                
                // 연결 요청 패킷 전송
                GameStatePacket connectionPacket = new GameStatePacket(GameStatePacket.PacketType.CONNECTION_REQUEST);
                connectionPacket.setMessage("클라이언트 연결 요청");
                sendPacket(connectionPacket);
                
                // 핑 체크 시작
                startPingCheck();
                
                // 패킷 수신 루프
                receiveLoop();
                
            } catch (SocketTimeoutException e) {
                System.out.println("[P2P Client] 연결 시간 초과");
                if (listener != null) {
                    listener.onError("서버 연결 시간 초과");
                }
            } catch (IOException e) {
                System.out.println("[P2P Client] 연결 오류: " + e.getMessage());
                if (listener != null) {
                    listener.onError("연결 오류: " + e.getMessage());
                }
            } finally {
                close();
            }
        });
    }
    
    /**
     * 패킷 수신 루프
     */
    private void receiveLoop() {
        while (isRunning && isConnected) {
            try {
                GameStatePacket packet = (GameStatePacket) in.readObject();
                lastReceivedTime = System.currentTimeMillis();
                
                // 랙 해제
                if (lagDetected) {
                    lagDetected = false;
                    if (listener != null) {
                        listener.onLagDetected(false);
                    }
                }
                
                if (packet.getType() == GameStatePacket.PacketType.DISCONNECT) {
                    System.out.println("[P2P Client] 서버가 연결을 종료했습니다");
                    isConnected = false;
                    if (listener != null) {
                        listener.onDisconnected("서버가 연결을 종료했습니다");
                    }
                    break;
                }
                
                if (packet.getType() == GameStatePacket.PacketType.PING) {
                    // 핑 응답
                    sendPacket(new GameStatePacket(GameStatePacket.PacketType.PONG));
                    continue;
                } else if (packet.getType() == GameStatePacket.PacketType.PONG) {
                    // 핑 응답 받음 - 레이턴시 계산
                    if (pingStartTime > 0) {
                        currentLatency = System.currentTimeMillis() - pingStartTime;
                        pingStartTime = 0;
                    }
                    continue;
                }
                
                if (listener != null) {
                    listener.onPacketReceived(packet);
                }
                
            } catch (EOFException e) {
                System.out.println("[P2P Client] 연결이 끊어졌습니다");
                isConnected = false;
                if (listener != null) {
                    listener.onDisconnected("연결이 끊어졌습니다");
                }
                break;
            } catch (IOException | ClassNotFoundException e) {
                if (isRunning) {
                    String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                    System.out.println("[P2P Client] 패킷 수신 오류: " + message);
                    isConnected = false;
                    if (listener != null) {
                        listener.onDisconnected("연결 오류: " + message);
                    }
                }
                break;
            }
        }
    }
    
    /**
     * 핑 체크 시작 (연결 상태 모니터링)
     */
    private void startPingCheck() {
        pingScheduler.scheduleAtFixedRate(() -> {
            if (!isConnected || !isRunning) {
                return;
            }
            
            long now = System.currentTimeMillis();
            long elapsed = now - lastReceivedTime;
            
            // 연결 끊김 체크 (5초)
            if (elapsed > CONNECTION_TIMEOUT) {
                System.out.println("[P2P Client] 연결 타임아웃");
                isConnected = false;
                if (listener != null) {
                    listener.onDisconnected("연결 타임아웃");
                }
                return;
            }
            
            // 랙 감지 (200ms 초과)
            if (elapsed > 200 && !lagDetected) {
                lagDetected = true;
                if (listener != null) {
                    listener.onLagDetected(true);
                }
            }
            
            // 핑 전송
            try {
                pingStartTime = System.currentTimeMillis();
                sendPacket(new GameStatePacket(GameStatePacket.PacketType.PING));
            } catch (Exception e) {
                System.out.println("[P2P Client] 핑 전송 실패: " + e.getMessage());
            }
            
        }, PING_INTERVAL, PING_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 패킷 전송
     */
    public synchronized void sendPacket(GameStatePacket packet) {
        if (!isConnected || out == null) {
            return;
        }
        
        try {
            out.writeObject(packet);
            out.flush();
            out.reset(); // 객체 캐시 초기화 (메모리 누수 방지)
        } catch (IOException e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            System.out.println("[P2P Client] 패킷 전송 오류: " + message);
            isConnected = false;
            if (listener != null) {
                listener.onDisconnected("연결 오류: " + message);
            }
        }
    }
    
    /**
     * 연결 상태 확인
     */
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * 현재 레이턴시 가져오기 (밀리초)
     */
    public long getCurrentLatency() {
        return currentLatency;
    }
    
    /**
     * 클라이언트 종료
     */
    public void close() {
        isRunning = false;
        isConnected = false;
        
        try {
            if (pingScheduler != null) {
                pingScheduler.shutdownNow();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (executorService != null) {
                executorService.shutdownNow();
            }
        } catch (IOException e) {
            System.out.println("[P2P Client] 종료 중 오류: " + e.getMessage());
        }
        
        System.out.println("[P2P Client] 클라이언트가 종료되었습니다");
    }
}
