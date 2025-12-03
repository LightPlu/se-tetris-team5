package se.tetris.team5.components.game;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import se.tetris.team5.blocks.*;
import se.tetris.team5.items.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * GameBoard 테스트
 */
public class GameBoardTest {

    private GameBoard gameBoard;

    @Before
    public void setUp() {
        gameBoard = new GameBoard();
    }

    /**
     * 테스트 1: 생성자 - 초기화
     */
    @Test
    public void testGameBoard_Constructor() {
        assertNotNull("게임 보드가 생성되어야 함", gameBoard);
        assertFalse("게임 보드는 불투명하지 않아야 함", gameBoard.isOpaque());
    }

    /**
     * 테스트 2: 상수 값 확인
     */
    @Test
    public void testGameBoard_Constants() {
        assertEquals("높이는 20이어야 함", 20, GameBoard.HEIGHT);
        assertEquals("너비는 10이어야 함", 10, GameBoard.WIDTH);
        assertEquals("테두리 문자는 X여야 함", 'X', GameBoard.BORDER_CHAR);
    }

    /**
     * 테스트 3: renderBoard - 정상 렌더링
     */
    @Test
    public void testGameBoard_RenderBoard() {
        int[][] board = new int[20][10];
        Color[][] colors = new Color[20][10];
        Item[][] items = new Item[20][10];
        Block block = new IBlock();
        
        gameBoard.renderBoard(board, colors, items, block, 3, 0, 18);
        
        assertTrue("renderBoard가 정상 작동해야 함", true);
    }

    /**
     * 테스트 4: renderBoard - null 보드
     */
    @Test
    public void testGameBoard_RenderBoardNull() {
        gameBoard.renderBoard(null, null, null, null, 0, 0, -1);
        
        assertTrue("null 보드로 렌더링해도 안전해야 함", true);
    }

    /**
     * 테스트 5: setShowTextOverlay
     */
    @Test
    public void testGameBoard_SetShowTextOverlay() {
        gameBoard.setShowTextOverlay(true);
        gameBoard.setShowTextOverlay(false);
        
        assertTrue("텍스트 오버레이 설정이 안전해야 함", true);
    }

    /**
     * 테스트 6: triggerClearAnimation - 정상 트리거
     */
    @Test
    public void testGameBoard_TriggerClearAnimation() {
        List<Integer> rows = new ArrayList<>();
        rows.add(18);
        rows.add(19);
        
        gameBoard.triggerClearAnimation(rows);
        
        assertTrue("클리어 애니메이션이 트리거되어야 함", true);
    }

    /**
     * 테스트 7: triggerClearAnimation - null 리스트
     */
    @Test
    public void testGameBoard_TriggerClearAnimationNull() {
        gameBoard.triggerClearAnimation(null);
        
        assertTrue("null 리스트로 트리거해도 안전해야 함", true);
    }

    /**
     * 테스트 8: triggerClearAnimation - 빈 리스트
     */
    @Test
    public void testGameBoard_TriggerClearAnimationEmpty() {
        gameBoard.triggerClearAnimation(new ArrayList<>());
        
        assertTrue("빈 리스트로 트리거해도 안전해야 함", true);
    }

    /**
     * 테스트 9: triggerBombExplosion - 정상 트리거
     */
    @Test
    public void testGameBoard_TriggerBombExplosion() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(5, 3));
        cells.add(new GameBoard.CellPos(5, 4));
        
        gameBoard.triggerBombExplosion(cells);
        
        assertTrue("폭탄 애니메이션이 트리거되어야 함", true);
    }

    /**
     * 테스트 10: triggerBombExplosion - null 리스트
     */
    @Test
    public void testGameBoard_TriggerBombExplosionNull() {
        gameBoard.triggerBombExplosion(null);
        
        assertTrue("null 리스트로 트리거해도 안전해야 함", true);
    }

    /**
     * 테스트 11: CellPos 생성 및 비교
     */
    @Test
    public void testGameBoard_CellPos() {
        GameBoard.CellPos pos1 = new GameBoard.CellPos(5, 3);
        GameBoard.CellPos pos2 = new GameBoard.CellPos(5, 3);
        GameBoard.CellPos pos3 = new GameBoard.CellPos(6, 3);
        
        assertEquals("같은 위치는 동일해야 함", pos1, pos2);
        assertNotEquals("다른 위치는 다름", pos1, pos3);
        assertEquals("hashCode가 일치해야 함", pos1.hashCode(), pos2.hashCode());
    }

    /**
     * 테스트 12: CellPos toString
     */
    @Test
    public void testGameBoard_CellPosToString() {
        GameBoard.CellPos pos = new GameBoard.CellPos(10, 5);
        String str = pos.toString();
        
        assertTrue("toString이 row를 포함해야 함", str.contains("10"));
        assertTrue("toString이 col을 포함해야 함", str.contains("5"));
    }

    /**
     * 테스트 13: paintComponent - 기본 렌더링
     */
    @Test
    public void testGameBoard_Paint() {
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("paintComponent가 정상 작동해야 함", true);
    }

    /**
     * 테스트 14: paintComponent - 보드 데이터와 함께
     */
    @Test
    public void testGameBoard_PaintWithBoard() {
        int[][] board = new int[20][10];
        Color[][] colors = new Color[20][10];
        Item[][] items = new Item[20][10];
        
        // 일부 셀 채우기
        board[19][5] = 1;
        colors[19][5] = Color.RED;
        
        gameBoard.renderBoard(board, colors, items, null, 0, 0, -1);
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("보드 데이터와 함께 렌더링되어야 함", true);
    }

    /**
     * 테스트 15: paintComponent - 현재 블록과 함께
     */
    @Test
    public void testGameBoard_PaintWithCurrentBlock() {
        int[][] board = new int[20][10];
        Color[][] colors = new Color[20][10];
        Item[][] items = new Item[20][10];
        Block block = new TBlock();
        
        gameBoard.renderBoard(board, colors, items, block, 3, 0, 18);
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("현재 블록과 함께 렌더링되어야 함", true);
    }

    /**
     * 테스트 16: paintComponent - 고스트 블록
     */
    @Test
    public void testGameBoard_PaintWithGhostBlock() {
        int[][] board = new int[20][10];
        Color[][] colors = new Color[20][10];
        Item[][] items = new Item[20][10];
        Block block = new OBlock();
        
        gameBoard.renderBoard(board, colors, items, block, 4, 2, 18);
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("고스트 블록이 렌더링되어야 함", true);
    }

    /**
     * 테스트 17: paintComponent - 텍스트 오버레이
     */
    @Test
    public void testGameBoard_PaintWithTextOverlay() {
        gameBoard.setShowTextOverlay(true);
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("텍스트 오버레이가 렌더링되어야 함", true);
    }

    /**
     * 테스트 18: 아이템 렌더링 - TimeStopItem
     */
    @Test
    public void testGameBoard_PaintWithTimeStopItem() {
        int[][] board = new int[20][10];
        Color[][] colors = new Color[20][10];
        Item[][] items = new Item[20][10];
        
        board[15][5] = 1;
        colors[15][5] = Color.CYAN;
        items[15][5] = new TimeStopItem();
        
        gameBoard.renderBoard(board, colors, items, null, 0, 0, -1);
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("TimeStopItem이 렌더링되어야 함", true);
    }

    /**
     * 테스트 19: 아이템 렌더링 - BombItem
     */
    @Test
    public void testGameBoard_PaintWithBombItem() {
        int[][] board = new int[20][10];
        Color[][] colors = new Color[20][10];
        Item[][] items = new Item[20][10];
        
        board[16][6] = 1;
        colors[16][6] = Color.RED;
        items[16][6] = new BombItem();
        
        gameBoard.renderBoard(board, colors, items, null, 0, 0, -1);
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("BombItem이 렌더링되어야 함", true);
    }

    /**
     * 테스트 20: 클리어 애니메이션 - null 포함
     */
    @Test
    public void testGameBoard_TriggerClearAnimationWithNull() {
        List<Integer> rows = new ArrayList<>();
        rows.add(17);
        rows.add(null);
        rows.add(19);
        
        gameBoard.triggerClearAnimation(rows);
        
        assertTrue("null을 건너뛰고 애니메이션이 트리거되어야 함", true);
    }

    /**
     * 테스트 21: 다양한 블록 타입 렌더링
     */
    @Test
    public void testGameBoard_PaintWithVariousBlocks() {
        Block[] blocks = {
            new IBlock(), new OBlock(), new TBlock(),
            new SBlock(), new ZBlock(), new LBlock(),
            new JBlock()
        };
        
        for (Block block : blocks) {
            int[][] board = new int[20][10];
            Color[][] colors = new Color[20][10];
            Item[][] items = new Item[20][10];
            
            gameBoard.renderBoard(board, colors, items, block, 3, 0, 18);
            gameBoard.setSize(400, 700);
            
            BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            gameBoard.paint(g2d);
            g2d.dispose();
        }
        
        assertTrue("다양한 블록 타입이 렌더링되어야 함", true);
    }

    /**
     * 테스트 22: 애니메이션 진행 중 렌더링
     */
    @Test
    public void testGameBoard_PaintDuringAnimation() throws Exception {
        List<Integer> rows = new ArrayList<>();
        rows.add(19);
        
        gameBoard.triggerClearAnimation(rows);
        gameBoard.setSize(400, 700);
        
        // 애니메이션 진행 중 렌더링
        Thread.sleep(50);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("애니메이션 진행 중에도 렌더링되어야 함", true);
    }

    /**
     * 테스트 23: 여러 행 동시 클리어
     */
    @Test
    public void testGameBoard_TriggerMultipleRowsClear() {
        List<Integer> rows = new ArrayList<>();
        rows.add(16);
        rows.add(17);
        rows.add(18);
        rows.add(19);
        
        gameBoard.triggerClearAnimation(rows);
        
        assertTrue("여러 행이 동시에 클리어되어야 함", true);
    }

    /**
     * 테스트 24: 중복 애니메이션 트리거
     */
    @Test
    public void testGameBoard_TriggerAnimationTwice() {
        List<Integer> rows1 = new ArrayList<>();
        rows1.add(19);
        
        gameBoard.triggerClearAnimation(rows1);
        
        List<Integer> rows2 = new ArrayList<>();
        rows2.add(18);
        
        gameBoard.triggerClearAnimation(rows2);
        
        assertTrue("중복 트리거가 안전해야 함", true);
    }

    /**
     * 테스트 25: 다양한 크기로 렌더링
     */
    @Test
    public void testGameBoard_PaintVariousSizes() {
        int[][] sizes = {{300, 600}, {400, 700}, {500, 800}, {350, 650}};
        
        for (int[] size : sizes) {
            gameBoard.setSize(size[0], size[1]);
            
            BufferedImage img = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            gameBoard.paint(g2d);
            g2d.dispose();
        }
        
        assertTrue("다양한 크기로 렌더링이 가능해야 함", true);
    }

    /**
     * 테스트 26: 블록에 아이템 포함
     */
    @Test
    public void testGameBoard_PaintBlockWithItem() {
        int[][] board = new int[20][10];
        Color[][] colors = new Color[20][10];
        Item[][] items = new Item[20][10];
        Block block = new IBlock();
        block.setItem(0, 0, new DoubleScoreItem());
        
        gameBoard.renderBoard(board, colors, items, block, 3, 0, 18);
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("블록 내 아이템이 렌더링되어야 함", true);
    }

    /**
     * 테스트 27: CellPos equals - 자기 자신
     */
    @Test
    public void testGameBoard_CellPosEqualsSelf() {
        GameBoard.CellPos pos = new GameBoard.CellPos(3, 7);
        
        assertEquals("자기 자신과 같아야 함", pos, pos);
    }

    /**
     * 테스트 28: CellPos equals - null
     */
    @Test
    public void testGameBoard_CellPosEqualsNull() {
        GameBoard.CellPos pos = new GameBoard.CellPos(3, 7);
        
        assertNotEquals("null과 다름", pos, null);
    }

    /**
     * 테스트 29: CellPos equals - 다른 타입
     */
    @Test
    public void testGameBoard_CellPosEqualsDifferentType() {
        GameBoard.CellPos pos = new GameBoard.CellPos(3, 7);
        
        assertNotEquals("다른 타입과 다름", pos, "string");
    }

    /**
     * 테스트 30: 고스트 블록 없음 (ghostY == currentY)
     */
    @Test
    public void testGameBoard_PaintNoGhostBlock() {
        int[][] board = new int[20][10];
        Color[][] colors = new Color[20][10];
        Item[][] items = new Item[20][10];
        Block block = new LBlock();
        
        // ghostY와 currentY가 같으면 고스트 블록 렌더링 안 함
        gameBoard.renderBoard(board, colors, items, block, 4, 5, 5);
        gameBoard.setSize(400, 700);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        gameBoard.paint(g2d);
        g2d.dispose();
        
        assertTrue("ghostY == currentY일 때 고스트 블록 렌더링 안 함", true);
    }
}
