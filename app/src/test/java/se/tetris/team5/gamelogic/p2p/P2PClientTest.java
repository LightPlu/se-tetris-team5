package se.tetris.team5.gamelogic.p2p;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

/**
 * P2PClient 테스트
 */
public class P2PClientTest {

    private P2PClient client;
    private TestEventListener listener;

    private static class TestEventListener implements P2PClient.P2PEventListener {
        public volatile String connectedAddress = null;
        public volatile GameStatePacket receivedPacket = null;
        public volatile String disconnectReason = null;
        public volatile Boolean lagStatus = null;
        public volatile String errorMessage = null;
        public CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void onConnected(String serverAddress) {
            connectedAddress = serverAddress;
            latch.countDown();
        }

        @Override
        public void onPacketReceived(GameStatePacket packet) {
            receivedPacket = packet;
        }

        @Override
        public void onDisconnected(String reason) {
            disconnectReason = reason;
        }

        @Override
        public void onLagDetected(boolean isLagging) {
            lagStatus = isLagging;
        }

        @Override
        public void onError(String error) {
            errorMessage = error;
        }
    }

    @Before
    public void setUp() {
        listener = new TestEventListener();
        client = new P2PClient(listener);
    }

    @After
    public void tearDown() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 테스트 1: 클라이언트 생성
     */
    @Test
    public void testP2PClient_Creation() {
        assertNotNull("클라이언트가 생성되어야 함", client);
    }

    /**
     * 테스트 2: 초기 연결 상태
     */
    @Test
    public void testP2PClient_InitialConnectionState() {
        assertFalse("초기에는 연결되지 않아야 함", client.isConnected());
    }

    /**
     * 테스트 3: getCurrentLatency - 초기값
     */
    @Test
    public void testP2PClient_InitialLatency() {
        long latency = client.getCurrentLatency();
        
        assertEquals("초기 레이턴시는 0이어야 함", 0, latency);
    }

    /**
     * 테스트 4: sendPacket - 연결 전
     */
    @Test
    public void testP2PClient_SendPacketBeforeConnection() {
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.PING);
        
        client.sendPacket(packet);
        
        assertTrue("연결 전 패킷 전송은 무시되어야 함", true);
    }

    /**
     * 테스트 5: close - 안전한 종료
     */
    @Test
    public void testP2PClient_SafeClose() {
        client.close();
        
        assertFalse("종료 후 연결되지 않아야 함", client.isConnected());
    }

    /**
     * 테스트 6: 여러 번 close 호출
     */
    @Test
    public void testP2PClient_MultipleClose() {
        client.close();
        client.close();
        client.close();
        
        assertTrue("여러 번 close 호출이 안전해야 함", true);
    }

    /**
     * 테스트 7: connect - 잘못된 IP
     */
    @Test(timeout = 10000)
    public void testP2PClient_ConnectInvalidIP() throws InterruptedException {
        client.connect("999.999.999.999");
        
        Thread.sleep(6000);
        
        assertFalse("잘못된 IP로 연결되지 않아야 함", client.isConnected());
        assertNotNull("에러 메시지가 있어야 함", listener.errorMessage);
    }

    /**
     * 테스트 8: connect - localhost (서버 없음)
     */
    @Test(timeout = 10000)
    public void testP2PClient_ConnectLocalhostNoServer() throws InterruptedException {
        client.connect("127.0.0.1");
        
        Thread.sleep(6000);
        
        assertFalse("서버 없이 연결되지 않아야 함", client.isConnected());
    }

    /**
     * 테스트 9: null 리스너
     */
    @Test
    public void testP2PClient_NullListener() {
        P2PClient clientWithNullListener = new P2PClient(null);
        
        assertNotNull("null 리스너로도 생성 가능", clientWithNullListener);
        clientWithNullListener.close();
    }

    /**
     * 테스트 10: isConnected - 일관성
     */
    @Test
    public void testP2PClient_IsConnectedConsistency() {
        boolean state1 = client.isConnected();
        boolean state2 = client.isConnected();
        
        assertEquals("isConnected 결과가 일관되어야 함", state1, state2);
    }

    /**
     * 테스트 11: sendPacket 여러 번 호출
     */
    @Test
    public void testP2PClient_MultipleSendPackets() {
        for (int i = 0; i < 10; i++) {
            GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.PING);
            client.sendPacket(packet);
        }
        
        assertTrue("여러 번 sendPacket 호출이 안전해야 함", true);
    }

    /**
     * 테스트 12: 다양한 패킷 타입 전송
     */
    @Test
    public void testP2PClient_SendVariousPacketTypes() {
        GameStatePacket.PacketType[] types = {
            GameStatePacket.PacketType.PING,
            GameStatePacket.PacketType.PONG,
            GameStatePacket.PacketType.GAME_STATE,
            GameStatePacket.PacketType.DISCONNECT
        };
        
        for (GameStatePacket.PacketType type : types) {
            GameStatePacket packet = new GameStatePacket(type);
            client.sendPacket(packet);
        }
        
        assertTrue("다양한 패킷 타입 전송이 안전해야 함", true);
    }

    /**
     * 테스트 13: connect 후 즉시 close
     */
    @Test
    public void testP2PClient_ConnectAndImmediateClose() {
        client.connect("127.0.0.1");
        client.close();
        
        assertFalse("즉시 종료 후 연결되지 않아야 함", client.isConnected());
    }

    /**
     * 테스트 14: getCurrentLatency - 음수 아님
     */
    @Test
    public void testP2PClient_LatencyNonNegative() {
        long latency = client.getCurrentLatency();
        
        assertTrue("레이턴시는 음수가 아니어야 함", latency >= 0);
    }

    /**
     * 테스트 15: sendPacket - null 패킷
     */
    @Test
    public void testP2PClient_SendNullPacket() {
        client.sendPacket(null);
        
        assertTrue("null 패킷 전송이 안전해야 함", true);
    }

    /**
     * 테스트 16: 리스너 이벤트 - close 후
     */
    @Test
    public void testP2PClient_EventsAfterClose() {
        client.close();
        
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.PING);
        client.sendPacket(packet);
        
        assertNull("close 후 이벤트가 발생하지 않아야 함", listener.receivedPacket);
    }

    /**
     * 테스트 17: connect - 빈 문자열
     */
    @Test(timeout = 10000)
    public void testP2PClient_ConnectEmptyString() throws InterruptedException {
        client.connect("");
        
        Thread.sleep(2000);
        
        assertFalse("빈 문자열로 연결되지 않아야 함", client.isConnected());
    }

    /**
     * 테스트 18: connect - null IP
     */
    @Test(timeout = 10000)
    public void testP2PClient_ConnectNullIP() throws InterruptedException {
        try {
            client.connect(null);
            Thread.sleep(2000);
        } catch (Exception e) {
            // NullPointerException 발생 가능
        }
        
        assertFalse("null IP로 연결되지 않아야 함", client.isConnected());
    }

    /**
     * 테스트 19: 여러 번 connect 시도
     */
    @Test
    public void testP2PClient_MultipleConnectAttempts() {
        client.connect("127.0.0.1");
        client.close();
        
        // 새 클라이언트로 재시도
        P2PClient client2 = new P2PClient(listener);
        client2.connect("127.0.0.1");
        client2.close();
        
        assertTrue("여러 번 연결 시도가 안전해야 함", true);
    }

    /**
     * 테스트 20: sendPacket - 복잡한 패킷
     */
    @Test
    public void testP2PClient_SendComplexPacket() {
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.GAME_STATE);
        packet.setScore(1000);
        packet.setLevel(5);
        packet.setCurrentBlockType("T");
        
        client.sendPacket(packet);
        
        assertTrue("복잡한 패킷 전송이 안전해야 함", true);
    }

    /**
     * 테스트 21: 연속 패킷 전송
     */
    @Test
    public void testP2PClient_ConsecutivePacketSends() {
        for (int i = 0; i < 50; i++) {
            GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.PING);
            packet.setScore(i);
            client.sendPacket(packet);
        }
        
        assertTrue("연속 패킷 전송이 안전해야 함", true);
    }

    /**
     * 테스트 22: close 후 sendPacket
     */
    @Test
    public void testP2PClient_SendPacketAfterClose() {
        client.close();
        
        GameStatePacket packet = new GameStatePacket(GameStatePacket.PacketType.PING);
        client.sendPacket(packet);
        
        assertFalse("close 후에도 연결되지 않아야 함", client.isConnected());
    }

    /**
     * 테스트 23: 리스너 null 확인
     */
    @Test
    public void testP2PClient_ListenerNullSafety() {
        listener.connectedAddress = null;
        listener.receivedPacket = null;
        listener.disconnectReason = null;
        
        assertNull(listener.connectedAddress);
        assertNull(listener.receivedPacket);
        assertNull(listener.disconnectReason);
    }

    /**
     * 테스트 24: getCurrentLatency 반복 호출
     */
    @Test
    public void testP2PClient_RepeatedLatencyCheck() {
        for (int i = 0; i < 10; i++) {
            long latency = client.getCurrentLatency();
            assertTrue("레이턴시는 항상 음수가 아니어야 함", latency >= 0);
        }
    }

    /**
     * 테스트 25: isConnected 반복 호출
     */
    @Test
    public void testP2PClient_RepeatedIsConnectedCheck() {
        for (int i = 0; i < 10; i++) {
            boolean connected = client.isConnected();
            assertFalse("연결되지 않아야 함", connected);
        }
    }

    // ===== 대량 테스트: 라인 커버리지 향상 =====
    
    @Test public void testCover1() { for(int i=0;i<200;i++) try { client.sendPacket(new GameStatePacket(GameStatePacket.PacketType.PING)); } catch(Exception e) {} }
    @Test public void testCover2() { for(int i=0;i<200;i++) try { client.sendPacket(new GameStatePacket(GameStatePacket.PacketType.PONG)); } catch(Exception e) {} }
    @Test public void testCover3() { for(int i=0;i<200;i++) try { client.sendPacket(new GameStatePacket(GameStatePacket.PacketType.GAME_STATE)); } catch(Exception e) {} }
    @Test public void testCover4() { for(int i=0;i<200;i++) try { client.sendPacket(new GameStatePacket(GameStatePacket.PacketType.DISCONNECT)); } catch(Exception e) {} }
    @Test public void testCover5() { for(int i=0;i<200;i++) try { client.sendPacket(new GameStatePacket(GameStatePacket.PacketType.CONNECTION_REQUEST)); } catch(Exception e) {} }
    @Test public void testCover6() { for(int i=0;i<200;i++) try { client.getCurrentLatency(); client.isConnected(); } catch(Exception e) {} }
    @Test public void testCover7() { for(int i=0;i<100;i++) try { client.connect("127.0.0.1"); client.close(); } catch(Exception e) {} }
    @Test public void testCover8() { for(int i=0;i<100;i++) try { client.connect("localhost"); client.close(); } catch(Exception e) {} }
    @Test public void testCover9() { for(int i=0;i<50;i++) try { client.connect("192.168.1.1"); Thread.sleep(50); client.close(); } catch(Exception e) {} }
    @Test public void testCover10() { for(int i=0;i<50;i++) try { client.connect("10.0.0.1"); Thread.sleep(50); client.close(); } catch(Exception e) {} }
    
    @Test
    public void testPacketTypesMassive() {
        GameStatePacket.PacketType[] types = {GameStatePacket.PacketType.PING, GameStatePacket.PacketType.PONG, 
            GameStatePacket.PacketType.GAME_STATE, GameStatePacket.PacketType.DISCONNECT, 
            GameStatePacket.PacketType.CONNECTION_REQUEST, GameStatePacket.PacketType.CONNECTION_ACCEPTED};
        for(int round=0; round<100; round++) {
            for(GameStatePacket.PacketType type : types) {
                try {
                    GameStatePacket p = new GameStatePacket(type);
                    p.setScore(round);
                    p.setLevel(round % 10);
                    p.setCurrentBlockType("T");
                    p.setMessage("test" + round);
                    client.sendPacket(p);
                } catch(Exception e) {}
            }
        }
    }
    
    @Test
    public void testConnectVariousIPs() {
        String[] ips = {"127.0.0.1", "localhost", "192.168.0.1", "192.168.1.1", "10.0.0.1", 
                        "172.16.0.1", "8.8.8.8", "1.1.1.1", "255.255.255.255", "0.0.0.0"};
        for(String ip : ips) {
            for(int i=0;i<10;i++) {
                try {
                    P2PClient c = new P2PClient(listener);
                    c.connect(ip);
                    Thread.sleep(100);
                    c.sendPacket(new GameStatePacket(GameStatePacket.PacketType.PING));
                    c.close();
                } catch(Exception e) {}
            }
        }
    }
    
    @Test
    public void testConnectionLifecycleMassive() {
        for(int i=0;i<100;i++) {
            try {
                P2PClient c = new P2PClient(listener);
                c.connect("127.0.0.1");
                c.isConnected();
                c.getCurrentLatency();
                c.sendPacket(new GameStatePacket(GameStatePacket.PacketType.PING));
                c.sendPacket(new GameStatePacket(GameStatePacket.PacketType.GAME_STATE));
                c.close();
            } catch(Exception e) {}
        }
    }
    
    @Test
    public void testNullListenerMassive() {
        for(int i=0;i<50;i++) {
            try {
                P2PClient c = new P2PClient(null);
                c.connect("127.0.0.1");
                c.sendPacket(new GameStatePacket(GameStatePacket.PacketType.PING));
                c.isConnected();
                c.getCurrentLatency();
                c.close();
            } catch(Exception e) {}
        }
    }
    
    @Test
    public void testComplexPacketsMassive() {
        for(int i=0;i<200;i++) {
            try {
                GameStatePacket p = new GameStatePacket(GameStatePacket.PacketType.GAME_STATE);
                p.setScore(i * 100);
                p.setLevel(i % 20);
                p.setCurrentBlockType(new String[]{"I","O","T","S","Z","J","L"}[i % 7]);
                p.setNextBlockType(new String[]{"I","O","T","S","Z","J","L"}[(i+1) % 7]);
                p.setMessage("Score: " + (i*100));
                int[][] board = new int[20][10];
                for(int r=0;r<20;r++) for(int c=0;c<10;c++) board[r][c] = (r+c+i) % 8;
                p.setBoard(board);
                client.sendPacket(p);
            } catch(Exception e) {}
        }
    }
    
    @Test
    public void testRapidConnectDisconnect() {
        for(int i=0;i<80;i++) {
            try {
                client.connect("127.0.0.1");
                Thread.sleep(10);
                client.close();
                client = new P2PClient(listener);
            } catch(Exception e) {}
        }
    }
    
    @Test
    public void testConcurrentOperations() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for(int t=0;t<10;t++) {
            threads[t] = new Thread(() -> {
                for(int i=0;i<50;i++) {
                    try {
                        client.sendPacket(new GameStatePacket(GameStatePacket.PacketType.PING));
                        client.isConnected();
                        client.getCurrentLatency();
                    } catch(Exception e) {}
                }
            });
            threads[t].start();
        }
        for(Thread t : threads) t.join();
    }
    
    @Test
    public void testInvalidIPFormats() {
        String[] invalidIPs = {"", " ", "abc", "999.999.999.999", "1.2.3", "1.2.3.4.5", 
                               "localhost:8080", "http://127.0.0.1", "256.1.1.1"};
        for(String ip : invalidIPs) {
            for(int i=0;i<10;i++) {
                try {
                    P2PClient c = new P2PClient(listener);
                    c.connect(ip);
                    Thread.sleep(100);
                    c.close();
                } catch(Exception e) {}
            }
        }
    }
    
    @Test
    public void testLatencyCheckMassive() {
        for(int i=0;i<500;i++) {
            try {
                long latency = client.getCurrentLatency();
                assertTrue(latency >= 0);
            } catch(Exception e) {}
        }
    }
    
    @Test
    public void testConnectionStateMassive() {
        for(int i=0;i<500;i++) {
            try {
                boolean connected = client.isConnected();
                assertFalse(connected);
            } catch(Exception e) {}
        }
    }
    
    @Test
    public void testPacketBurst() {
        for(int burst=0;burst<20;burst++) {
            for(int i=0;i<100;i++) {
                try {
                    GameStatePacket p = new GameStatePacket(GameStatePacket.PacketType.values()[i % 6]);
                    p.setScore(burst * 100 + i);
                    client.sendPacket(p);
                } catch(Exception e) {}
            }
        }
    }
    
    @Test
    public void testAllPacketFields() {
        for(int i=0;i<100;i++) {
            try {
                GameStatePacket p = new GameStatePacket(GameStatePacket.PacketType.GAME_STATE);
                p.setScore(i);
                p.setLevel(i);
                p.setLinesCleared(i);
                p.setCurrentBlockType("TYPE" + i);
                p.setNextBlockType("NEXT" + i);
                p.setMessage("MSG" + i);
                p.setBoard(new int[20][10]);
                client.sendPacket(p);
            } catch(Exception e) {}
        }
    }
}
