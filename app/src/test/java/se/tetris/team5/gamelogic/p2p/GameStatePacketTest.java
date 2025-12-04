package se.tetris.team5.gamelogic.p2p;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;
import java.io.*;

/**
 * GameStatePacket 테스트
 * - 직렬화/역직렬화 테스트
 * - 게터/세터 테스트
 * - 다양한 패킷 타입 테스트
 */
public class GameStatePacketTest {
    
    private GameStatePacket packet;
    
    @Before
    public void setUp() {
        packet = new GameStatePacket(GameStatePacket.PacketType.GAME_STATE);
    }
    
    @Test
    public void testPacketCreation() {
        assertNotNull("패킷이 생성되어야 함", packet);
        assertEquals("패킷 타입이 일치해야 함", 
            GameStatePacket.PacketType.GAME_STATE, packet.getType());
        assertTrue("타임스탬프가 설정되어야 함", packet.getTimestamp() > 0);
    }
    
    @Test
    public void testBoardDataGetterSetter() {
        int[][] testBoard = new int[20][10];
        testBoard[0][0] = 1;
        testBoard[19][9] = 1;
        
        packet.setBoard(testBoard);
        int[][] result = packet.getBoard();
        
        assertNotNull("보드가 null이 아니어야 함", result);
        assertEquals("보드 높이가 일치해야 함", 20, result.length);
        assertEquals("보드 너비가 일치해야 함", 10, result[0].length);
        assertEquals("보드 데이터가 일치해야 함", 1, result[0][0]);
        assertEquals("보드 데이터가 일치해야 함", 1, result[19][9]);
    }
    
    @Test
    public void testBoardColorsGetterSetter() {
        Color[][] testColors = new Color[20][10];
        testColors[5][5] = Color.RED;
        testColors[10][3] = Color.BLUE;
        
        packet.setBoardColors(testColors);
        Color[][] result = packet.getBoardColors();
        
        assertNotNull("색상 배열이 null이 아니어야 함", result);
        assertEquals("색상이 일치해야 함", Color.RED, result[5][5]);
        assertEquals("색상이 일치해야 함", Color.BLUE, result[10][3]);
    }
    
    @Test
    public void testCurrentBlockDataGetterSetter() {
        packet.setCurrentBlockX(5);
        packet.setCurrentBlockY(10);
        packet.setCurrentBlockType("I");
        
        assertEquals("X 좌표가 일치해야 함", 5, packet.getCurrentBlockX());
        assertEquals("Y 좌표가 일치해야 함", 10, packet.getCurrentBlockY());
        assertEquals("블록 타입이 일치해야 함", "I", packet.getCurrentBlockType());
    }
    
    @Test
    public void testScoreDataGetterSetter() {
        packet.setScore(12345);
        packet.setLevel(5);
        packet.setLinesCleared(20);
        
        assertEquals("점수가 일치해야 함", 12345, packet.getScore());
        assertEquals("레벨이 일치해야 함", 5, packet.getLevel());
        assertEquals("줄 수가 일치해야 함", 20, packet.getLinesCleared());
    }
    
    @Test
    public void testBattleModeGetterSetter() {
        String[] modes = {"NORMAL", "ITEM", "TIMELIMIT"};
        
        for (String mode : modes) {
            packet.setBattleMode(mode);
            assertEquals("배틀 모드가 일치해야 함", mode, packet.getBattleMode());
        }
    }
    
    @Test
    public void testGameOverDataGetterSetter() {
        packet.setWinner(1);
        assertEquals("승자가 일치해야 함", 1, packet.getWinner());
        
        packet.setWinner(2);
        assertEquals("승자가 일치해야 함", 2, packet.getWinner());
    }
    
    @Test
    public void testMessageGetterSetter() {
        String testMessage = "연결 테스트 메시지";
        packet.setMessage(testMessage);
        assertEquals("메시지가 일치해야 함", testMessage, packet.getMessage());
    }
    
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        // 패킷 데이터 설정
        int[][] testBoard = new int[20][10];
        testBoard[5][5] = 1;
        
        Color[][] testColors = new Color[20][10];
        testColors[5][5] = Color.CYAN;
        
        packet.setBoard(testBoard);
        packet.setBoardColors(testColors);
        packet.setCurrentBlockX(3);
        packet.setCurrentBlockY(7);
        packet.setCurrentBlockType("T");
        packet.setScore(5000);
        packet.setLevel(3);
        packet.setLinesCleared(15);
        
        // 직렬화
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(packet);
        oos.flush();
        
        byte[] data = baos.toByteArray();
        assertTrue("직렬화된 데이터가 있어야 함", data.length > 0);
        
        // 역직렬화
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        GameStatePacket deserialized = (GameStatePacket) ois.readObject();
        
        // 검증
        assertNotNull("역직렬화된 패킷이 null이 아니어야 함", deserialized);
        assertEquals("패킷 타입이 일치해야 함", packet.getType(), deserialized.getType());
        assertEquals("X 좌표가 일치해야 함", 3, deserialized.getCurrentBlockX());
        assertEquals("Y 좌표가 일치해야 함", 7, deserialized.getCurrentBlockY());
        assertEquals("블록 타입이 일치해야 함", "T", deserialized.getCurrentBlockType());
        assertEquals("점수가 일치해야 함", 5000, deserialized.getScore());
        assertEquals("레벨이 일치해야 함", 3, deserialized.getLevel());
        assertEquals("줄 수가 일치해야 함", 15, deserialized.getLinesCleared());
        assertEquals("보드 데이터가 일치해야 함", 1, deserialized.getBoard()[5][5]);
        assertEquals("색상이 일치해야 함", Color.CYAN, deserialized.getBoardColors()[5][5]);
    }
    
    @Test
    public void testAllPacketTypes() {
        GameStatePacket.PacketType[] types = GameStatePacket.PacketType.values();
        
        for (GameStatePacket.PacketType type : types) {
            GameStatePacket p = new GameStatePacket(type);
            assertEquals("패킷 타입이 일치해야 함", type, p.getType());
        }
    }
    
    @Test
    public void testPacketTypeChange() {
        assertEquals("초기 타입이 GAME_STATE여야 함", 
            GameStatePacket.PacketType.GAME_STATE, packet.getType());
        
        packet.setType(GameStatePacket.PacketType.GAME_OVER);
        assertEquals("변경된 타입이 GAME_OVER여야 함", 
            GameStatePacket.PacketType.GAME_OVER, packet.getType());
    }
    
    @Test
    public void testTimestampUniqueness() throws InterruptedException {
        GameStatePacket packet1 = new GameStatePacket(GameStatePacket.PacketType.PING);
        Thread.sleep(2);
        GameStatePacket packet2 = new GameStatePacket(GameStatePacket.PacketType.PONG);
        
        assertTrue("타임스탬프가 달라야 함", 
            packet1.getTimestamp() != packet2.getTimestamp());
    }
    
    @Test
    public void testBlockTypeValidation() {
        String[] validTypes = {"I", "O", "T", "S", "Z", "L", "J", "W", "DOT"};
        
        for (String type : validTypes) {
            packet.setCurrentBlockType(type);
            assertEquals("블록 타입이 일치해야 함", type, packet.getCurrentBlockType());
        }
    }
    
    @Test
    public void testNullValues() {
        packet.setBoard(null);
        assertNull("null 보드가 허용되어야 함", packet.getBoard());
        
        packet.setBoardColors(null);
        assertNull("null 색상 배열이 허용되어야 함", packet.getBoardColors());
        
        packet.setCurrentBlockType(null);
        assertNull("null 블록 타입이 허용되어야 함", packet.getCurrentBlockType());
        
        packet.setMessage(null);
        assertNull("null 메시지가 허용되어야 함", packet.getMessage());
    }
    
    @Test
    public void testLargeScoreValues() {
        int largeScore = 999999;
        packet.setScore(largeScore);
        assertEquals("큰 점수가 처리되어야 함", largeScore, packet.getScore());
    }
    
    @Test
    public void testNegativeValues() {
        packet.setScore(-100);
        assertEquals("음수 점수가 허용되어야 함", -100, packet.getScore());
        
        packet.setLevel(-1);
        assertEquals("음수 레벨이 허용되어야 함", -1, packet.getLevel());
    }

    @Test
    public void testNextBlockType() {
        packet.setNextBlockType("O");
        assertEquals("다음 블록 타입 설정", "O", packet.getNextBlockType());
    }

    @Test
    public void testElapsedTime() {
        packet.setElapsedTime(60000L);
        assertEquals("경과 시간 설정", 60000L, packet.getElapsedTime());
    }

    @Test
    public void testRandomSeed() {
        packet.setRandomSeed(123456789L);
        assertEquals("랜덤 시드 설정", 123456789L, packet.getRandomSeed());
    }

    @Test
    public void testAttackBlocks() {
        java.util.List<int[]> attackBlocks = new java.util.ArrayList<>();
        attackBlocks.add(new int[]{1, 0, 1, 0, 1, 0, 1, 0, 1, 0});
        
        packet.setAttackBlocks(attackBlocks);
        assertEquals("공격 블록 설정", attackBlocks, packet.getAttackBlocks());
    }

    @Test
    public void testCompletePacketSerialization() throws IOException, ClassNotFoundException {
        // 완전한 패킷 데이터 설정
        packet.setCurrentBlockX(5);
        packet.setCurrentBlockY(10);
        packet.setCurrentBlockType("L");
        packet.setNextBlockType("J");
        packet.setScore(9999);
        packet.setLevel(7);
        packet.setLinesCleared(35);
        packet.setElapsedTime(120000L);
        packet.setBattleMode("ITEM");
        packet.setRandomSeed(987654321L);
        packet.setWinner(1);
        packet.setMessage("Complete test");
        
        // 직렬화
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(packet);
        oos.flush();
        
        // 역직렬화
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        GameStatePacket result = (GameStatePacket) ois.readObject();
        
        // 검증
        assertEquals(5, result.getCurrentBlockX());
        assertEquals(10, result.getCurrentBlockY());
        assertEquals("L", result.getCurrentBlockType());
        assertEquals("J", result.getNextBlockType());
        assertEquals(9999, result.getScore());
        assertEquals(7, result.getLevel());
        assertEquals(35, result.getLinesCleared());
        assertEquals(120000L, result.getElapsedTime());
        assertEquals("ITEM", result.getBattleMode());
        assertEquals(987654321L, result.getRandomSeed());
        assertEquals(1, result.getWinner());
        assertEquals("Complete test", result.getMessage());
    }

    @Test
    public void testPacketTypeEnumValues() {
        GameStatePacket.PacketType[] expectedTypes = {
            GameStatePacket.PacketType.CONNECTION_REQUEST,
            GameStatePacket.PacketType.CONNECTION_ACCEPTED,
            GameStatePacket.PacketType.GAME_MODE_SELECT,
            GameStatePacket.PacketType.READY,
            GameStatePacket.PacketType.GAME_START,
            GameStatePacket.PacketType.GAME_STATE,
            GameStatePacket.PacketType.ATTACK_BLOCKS,
            GameStatePacket.PacketType.GAME_OVER,
            GameStatePacket.PacketType.DISCONNECT,
            GameStatePacket.PacketType.PING,
            GameStatePacket.PacketType.PONG,
            GameStatePacket.PacketType.CHAT_MESSAGE
        };
        
        for (GameStatePacket.PacketType type : expectedTypes) {
            assertNotNull("패킷 타입이 존재해야 함", type);
        }
    }

    @Test
    public void testEmptyBoardSerialization() throws IOException, ClassNotFoundException {
        int[][] emptyBoard = new int[20][10];
        packet.setBoard(emptyBoard);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(packet);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        GameStatePacket result = (GameStatePacket) ois.readObject();
        
        assertNotNull("빈 보드가 직렬화되어야 함", result.getBoard());
        assertEquals(20, result.getBoard().length);
    }

    @Test
    public void testMultipleSerializationCycles() throws IOException, ClassNotFoundException {
        packet.setScore(5000);
        
        for (int i = 0; i < 5; i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(packet);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            packet = (GameStatePacket) ois.readObject();
        }
        
        assertEquals("여러 번 직렬화 후에도 데이터 유지", 5000, packet.getScore());
    }

    @Test
    public void testZeroValues() {
        packet.setScore(0);
        packet.setLevel(0);
        packet.setLinesCleared(0);
        packet.setCurrentBlockX(0);
        packet.setCurrentBlockY(0);
        
        assertEquals(0, packet.getScore());
        assertEquals(0, packet.getLevel());
        assertEquals(0, packet.getLinesCleared());
        assertEquals(0, packet.getCurrentBlockX());
        assertEquals(0, packet.getCurrentBlockY());
    }

    @Test
    public void testBoundaryValues() {
        packet.setScore(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, packet.getScore());
        
        packet.setScore(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, packet.getScore());
    }

    @Test
    public void testAttackBlocksNull() {
        packet.setAttackBlocks(null);
        assertNull("null 공격 블록 허용", packet.getAttackBlocks());
    }

    @Test
    public void testAttackBlocksEmpty() {
        java.util.List<int[]> emptyList = new java.util.ArrayList<>();
        packet.setAttackBlocks(emptyList);
        
        assertNotNull("빈 공격 블록 리스트", packet.getAttackBlocks());
        assertTrue("리스트가 비어있어야 함", packet.getAttackBlocks().isEmpty());
    }

    @Test
    public void testAllBlockTypes() {
        String[] allTypes = {"I", "O", "T", "S", "Z", "L", "J", "W", "DOT"};
        
        for (String type : allTypes) {
            packet.setCurrentBlockType(type);
            packet.setNextBlockType(type);
            
            assertEquals(type, packet.getCurrentBlockType());
            assertEquals(type, packet.getNextBlockType());
        }
    }

    @Test
    public void testLongElapsedTime() {
        long longTime = 3600000L; // 1시간
        packet.setElapsedTime(longTime);
        assertEquals(longTime, packet.getElapsedTime());
    }

    @Test
    public void testBattleModeNull() {
        packet.setBattleMode(null);
        assertNull("null 배틀 모드 허용", packet.getBattleMode());
    }

    @Test
    public void testWinnerBoundary() {
        packet.setWinner(0);
        assertEquals(0, packet.getWinner());
        
        packet.setWinner(3);
        assertEquals(3, packet.getWinner());
    }

    @Test
    public void testEmptyMessage() {
        packet.setMessage("");
        assertEquals("", packet.getMessage());
    }

    @Test
    public void testLongMessage() {
        String longMessage = "A".repeat(1000);
        packet.setMessage(longMessage);
        assertEquals(longMessage, packet.getMessage());
    }
}
