package se.tetris.team5.gamelogic.p2p;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * P2P 통합 테스트
 * - 서버-클라이언트 연결 테스트
 * - 패킷 송수신 테스트
 * - 연결 끊김 테스트
 * - 동시 통신 테스트
 */
public class P2PIntegrationTest {
    
    private P2PServer server;
    private P2PClient client;
    private CountDownLatch connectionLatch;
    private CountDownLatch packetLatch;
    private AtomicBoolean serverConnected;
    private AtomicBoolean clientConnected;
    private AtomicReference<GameStatePacket> receivedPacket;
    
    @Before
    public void setUp() {
        connectionLatch = new CountDownLatch(2);
        packetLatch = new CountDownLatch(2);
        serverConnected = new AtomicBoolean(false);
        clientConnected = new AtomicBoolean(false);
        receivedPacket = new AtomicReference<>();
    }
    
    @After
    public void tearDown() {
        if (server != null) {
            server.close();
            server = null;
        }
        if (client != null) {
            client.close();
            client = null;
        }
    }
    
    @Test(timeout = 10000)
    public void testServerClientConnection() throws InterruptedException {
        // 서버 시작
        server = new P2PServer(new P2PServer.P2PEventListener() {
            @Override
            public void onClientConnected(String clientAddress) {
                serverConnected.set(true);
                connectionLatch.countDown();
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {}
            
            @Override
            public void onDisconnected(String reason) {}
            
            @Override
            public void onLagDetected(boolean isLagging) {}
            
            @Override
            public void onError(String error) {
                fail("서버 오류 발생: " + error);
            }
        });
        
        server.start();
        
        // 잠시 대기 (서버 초기화)
        Thread.sleep(1000);
        
        // 클라이언트 연결
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {
                clientConnected.set(true);
                connectionLatch.countDown();
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {}
            
            @Override
            public void onDisconnected(String reason) {}
            
            @Override
            public void onLagDetected(boolean isLagging) {}
            
            @Override
            public void onError(String error) {
                fail("클라이언트 오류 발생: " + error);
            }
        });
        
        client.connect("127.0.0.1");
        
        // 연결 대기
        boolean connected = connectionLatch.await(5, TimeUnit.SECONDS);
        assertTrue("서버-클라이언트 연결이 성공해야 함", connected);
        assertTrue("서버가 연결되어야 함", serverConnected.get());
        assertTrue("클라이언트가 연결되어야 함", clientConnected.get());
        assertTrue("서버 연결 상태가 true여야 함", server.isConnected());
        assertTrue("클라이언트 연결 상태가 true여야 함", client.isConnected());
    }
    
    @Test(timeout = 10000)
    public void testPacketTransmission() throws InterruptedException {
        AtomicReference<GameStatePacket> serverReceived = new AtomicReference<>();
        AtomicReference<GameStatePacket> clientReceived = new AtomicReference<>();
        
        // 서버 시작
        server = new P2PServer(new P2PServer.P2PEventListener() {
            @Override
            public void onClientConnected(String clientAddress) {
                connectionLatch.countDown();
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                serverReceived.set(packet);
                packetLatch.countDown();
            }
            
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        server.start();
        Thread.sleep(1000);
        
        // 클라이언트 연결
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {
                connectionLatch.countDown();
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                clientReceived.set(packet);
                packetLatch.countDown();
            }
            
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        client.connect("127.0.0.1");
        connectionLatch.await(5, TimeUnit.SECONDS);
        
        // 클라이언트 → 서버 패킷 전송
        GameStatePacket clientPacket = new GameStatePacket(GameStatePacket.PacketType.READY);
        clientPacket.setMessage("client_ready");
        client.sendPacket(clientPacket);
        
        // 서버 → 클라이언트 패킷 전송
        GameStatePacket serverPacket = new GameStatePacket(GameStatePacket.PacketType.GAME_START);
        serverPacket.setBattleMode("NORMAL");
        server.sendPacket(serverPacket);
        
        // 패킷 수신 대기
        boolean received = packetLatch.await(5, TimeUnit.SECONDS);
        assertTrue("패킷이 수신되어야 함", received);
        
        assertNotNull("서버가 패킷을 받아야 함", serverReceived.get());
        assertNotNull("클라이언트가 패킷을 받아야 함", clientReceived.get());
        assertEquals("서버가 받은 패킷 타입이 일치해야 함", 
            GameStatePacket.PacketType.READY, serverReceived.get().getType());
        assertEquals("클라이언트가 받은 패킷 타입이 일치해야 함", 
            GameStatePacket.PacketType.GAME_START, clientReceived.get().getType());
    }
    
    @Test(timeout = 10000)
    public void testGameStatePacketTransmission() throws InterruptedException {
        AtomicReference<GameStatePacket> clientReceived = new AtomicReference<>();
        
        server = new P2PServer(new P2PServer.P2PEventListener() {
            @Override
            public void onClientConnected(String clientAddress) {
                connectionLatch.countDown();
            }
            @Override
            public void onPacketReceived(GameStatePacket packet) {}
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        server.start();
        Thread.sleep(1000);
        
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {
                connectionLatch.countDown();
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                if (packet.getType() == GameStatePacket.PacketType.GAME_STATE) {
                    clientReceived.set(packet);
                    packetLatch.countDown();
                }
            }
            
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        client.connect("127.0.0.1");
        connectionLatch.await(5, TimeUnit.SECONDS);
        
        // 게임 상태 패킷 생성 및 전송
        GameStatePacket gameState = new GameStatePacket(GameStatePacket.PacketType.GAME_STATE);
        gameState.setCurrentBlockX(5);
        gameState.setCurrentBlockY(10);
        gameState.setCurrentBlockType("I");
        gameState.setScore(1000);
        gameState.setLevel(2);
        gameState.setLinesCleared(8);
        
        server.sendPacket(gameState);
        
        packetLatch.await(5, TimeUnit.SECONDS);
        
        GameStatePacket received = clientReceived.get();
        assertNotNull("클라이언트가 게임 상태 패킷을 받아야 함", received);
        assertEquals("X 좌표가 일치해야 함", 5, received.getCurrentBlockX());
        assertEquals("Y 좌표가 일치해야 함", 10, received.getCurrentBlockY());
        assertEquals("블록 타입이 일치해야 함", "I", received.getCurrentBlockType());
        assertEquals("점수가 일치해야 함", 1000, received.getScore());
        assertEquals("레벨이 일치해야 함", 2, received.getLevel());
        assertEquals("줄 수가 일치해야 함", 8, received.getLinesCleared());
    }
    
    @Test(timeout = 10000)
    public void testDisconnection() throws InterruptedException {
        AtomicBoolean disconnected = new AtomicBoolean(false);
        CountDownLatch disconnectLatch = new CountDownLatch(1);
        
        server = new P2PServer(new P2PServer.P2PEventListener() {
            @Override
            public void onClientConnected(String clientAddress) {
                connectionLatch.countDown();
            }
            @Override
            public void onPacketReceived(GameStatePacket packet) {}
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        server.start();
        Thread.sleep(1000);
        
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {
                connectionLatch.countDown();
            }
            @Override
            public void onPacketReceived(GameStatePacket packet) {}
            
            @Override
            public void onDisconnected(String reason) {
                disconnected.set(true);
                disconnectLatch.countDown();
            }
            
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        client.connect("127.0.0.1");
        connectionLatch.await(5, TimeUnit.SECONDS);
        
        // 서버 종료
        server.close();
        
        // 연결 끊김 감지 대기
        boolean detected = disconnectLatch.await(8, TimeUnit.SECONDS);
        assertTrue("연결 끊김이 감지되어야 함", detected);
        assertTrue("연결 끊김 상태가 true여야 함", disconnected.get());
    }
    
    @Test(timeout = 10000)
    public void testMultiplePacketsInSequence() throws InterruptedException {
        final int PACKET_COUNT = 10;
        CountDownLatch multiPacketLatch = new CountDownLatch(PACKET_COUNT);
        AtomicReference<Integer> lastScore = new AtomicReference<>(0);
        
        server = new P2PServer(new P2PServer.P2PEventListener() {
            @Override
            public void onClientConnected(String clientAddress) {
                connectionLatch.countDown();
            }
            @Override
            public void onPacketReceived(GameStatePacket packet) {}
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        server.start();
        Thread.sleep(1000);
        
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {
                connectionLatch.countDown();
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                if (packet.getType() == GameStatePacket.PacketType.GAME_STATE) {
                    lastScore.set(packet.getScore());
                    multiPacketLatch.countDown();
                }
            }
            
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        client.connect("127.0.0.1");
        connectionLatch.await(5, TimeUnit.SECONDS);
        
        // 여러 패킷 순차 전송
        for (int i = 0; i < PACKET_COUNT; i++) {
            GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.GAME_STATE);
            packet.setScore(i * 100);
            server.sendPacket(packet);
            Thread.sleep(50); // 패킷 간 간격
        }
        
        boolean received = multiPacketLatch.await(5, TimeUnit.SECONDS);
        assertTrue("모든 패킷이 수신되어야 함", received);
        assertEquals("마지막 점수가 일치해야 함", 
            Integer.valueOf(900), lastScore.get());
    }
    
    @Test(timeout = 15000)
    public void testReadyHandshakeAndGameStartFlow() throws InterruptedException {
        CountDownLatch readyOnServer = new CountDownLatch(1);
        CountDownLatch readyOnClient = new CountDownLatch(1);
        CountDownLatch gameStartLatch = new CountDownLatch(1);
        
        server = new P2PServer(new P2PServer.P2PEventListener() {
            @Override
            public void onClientConnected(String clientAddress) {
                connectionLatch.countDown();
            }
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                if (packet.getType() == GameStatePacket.PacketType.READY) {
                    readyOnServer.countDown();
                }
            }
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {
                fail("서버 오류: " + error);
            }
        });
        server.start();
        Thread.sleep(1000);
        
        AtomicReference<GameStatePacket> clientReadyPacket = new AtomicReference<>();
        AtomicReference<GameStatePacket> startPacketRef = new AtomicReference<>();
        
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {
                connectionLatch.countDown();
            }
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                if (packet.getType() == GameStatePacket.PacketType.READY) {
                    clientReadyPacket.set(packet);
                    readyOnClient.countDown();
                } else if (packet.getType() == GameStatePacket.PacketType.GAME_START) {
                    startPacketRef.set(packet);
                    gameStartLatch.countDown();
                }
            }
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {
                fail("클라이언트 오류: " + error);
            }
        });
        
        client.connect("127.0.0.1");
        assertTrue("서버/클라이언트 연결", connectionLatch.await(5, TimeUnit.SECONDS));
        
        GameStatePacket serverReady = new GameStatePacket(GameStatePacket.PacketType.READY);
        serverReady.setMessage("ready");
        server.sendPacket(serverReady);
        
        GameStatePacket clientReady = new GameStatePacket(GameStatePacket.PacketType.READY);
        clientReady.setMessage("ready");
        client.sendPacket(clientReady);
        
        assertTrue("서버가 READY 수신", readyOnServer.await(5, TimeUnit.SECONDS));
        assertTrue("클라이언트가 READY 수신", readyOnClient.await(5, TimeUnit.SECONDS));
        assertNotNull("클라이언트 READY 메시지가 있어야 함", clientReadyPacket.get());
        assertEquals("ready", clientReadyPacket.get().getMessage());
        
        GameStatePacket start = new GameStatePacket(GameStatePacket.PacketType.GAME_START);
        start.setBattleMode("NORMAL");
        server.sendPacket(start);
        
        assertTrue("GAME_START 패킷 수신", gameStartLatch.await(5, TimeUnit.SECONDS));
        assertNotNull("GAME_START 패킷 저장", startPacketRef.get());
        assertEquals("NORMAL", startPacketRef.get().getBattleMode());
    }
    
    @Test(timeout = 5000)
    public void testInvalidIPConnection() throws InterruptedException {
        AtomicBoolean errorOccurred = new AtomicBoolean(false);
        CountDownLatch errorLatch = new CountDownLatch(1);
        
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {}
            @Override
            public void onPacketReceived(GameStatePacket packet) {}
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            
            @Override
            public void onError(String error) {
                errorOccurred.set(true);
                errorLatch.countDown();
            }
        });
        
        // 잘못된 IP로 연결 시도
        client.connect("999.999.999.999");
        
        boolean error = errorLatch.await(3, TimeUnit.SECONDS);
        assertTrue("오류가 발생해야 함", error);
        assertTrue("오류 상태가 true여야 함", errorOccurred.get());
        assertFalse("연결되지 않아야 함", client.isConnected());
    }
    
    @Test(timeout = 10000)
    public void testPingPongMechanism() throws InterruptedException {
        CountDownLatch pingLatch = new CountDownLatch(1);
        
        server = new P2PServer(new P2PServer.P2PEventListener() {
            @Override
            public void onClientConnected(String clientAddress) {
                connectionLatch.countDown();
            }
            
            @Override
            public void onPacketReceived(GameStatePacket packet) {
                if (packet.getType() == GameStatePacket.PacketType.PONG) {
                    pingLatch.countDown();
                }
            }
            
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        server.start();
        Thread.sleep(1000);
        
        client = new P2PClient(new P2PClient.P2PEventListener() {
            @Override
            public void onConnected(String serverAddress) {
                connectionLatch.countDown();
            }
            @Override
            public void onPacketReceived(GameStatePacket packet) {}
            @Override
            public void onDisconnected(String reason) {}
            @Override
            public void onLagDetected(boolean isLagging) {}
            @Override
            public void onError(String error) {}
        });
        
        client.connect("127.0.0.1");
        connectionLatch.await(5, TimeUnit.SECONDS);
        
        // Ping 패킷 전송
        GameStatePacket ping = new GameStatePacket(GameStatePacket.PacketType.PING);
        server.sendPacket(ping);
        
        // Pong 응답 대기
        boolean received = pingLatch.await(3, TimeUnit.SECONDS);
        assertTrue("Pong 응답을 받아야 함", received);
    }
}
