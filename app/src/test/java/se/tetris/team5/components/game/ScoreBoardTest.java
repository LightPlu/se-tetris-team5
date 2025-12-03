package se.tetris.team5.components.game;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import java.awt.Color;
import java.awt.Dimension;

/**
 * ScoreBoard 테스트
 */
public class ScoreBoardTest {

    private ScoreBoard scoreBoard;

    @Before
    public void setUp() {
        scoreBoard = new ScoreBoard();
    }

    /**
     * 테스트 1: 생성자 - 초기화
     */
    @Test
    public void testScoreBoard_Constructor() {
        assertNotNull("점수 보드가 생성되어야 함", scoreBoard);
    }

    /**
     * 테스트 2: 기본 크기 설정
     */
    @Test
    public void testScoreBoard_DefaultSize() {
        Dimension preferred = scoreBoard.getPreferredSize();
        assertEquals("기본 너비", 240, preferred.width);
        assertEquals("기본 높이", 400, preferred.height);
        
        Dimension minimum = scoreBoard.getMinimumSize();
        assertEquals("최소 너비", 200, minimum.width);
        assertEquals("최소 높이", 300, minimum.height);
    }

    /**
     * 테스트 3: 배경 색상
     */
    @Test
    public void testScoreBoard_BackgroundColor() {
        assertEquals("배경색이 검정이어야 함", Color.BLACK, scoreBoard.getBackground());
    }

    /**
     * 테스트 4: getTextPane - JTextPane 반환
     */
    @Test
    public void testScoreBoard_GetTextPane() {
        JTextPane pane = scoreBoard.getTextPane();
        assertNotNull("JTextPane이 반환되어야 함", pane);
        assertFalse("JTextPane은 편집 불가여야 함", pane.isEditable());
    }

    /**
     * 테스트 5: getStyleSet - SimpleAttributeSet 반환
     */
    @Test
    public void testScoreBoard_GetStyleSet() {
        SimpleAttributeSet styleSet = scoreBoard.getStyleSet();
        assertNotNull("SimpleAttributeSet이 반환되어야 함", styleSet);
    }

    /**
     * 테스트 6: showEmptyScore - 초기 상태 표시
     */
    @Test
    public void testScoreBoard_ShowEmptyScore() {
        scoreBoard.showEmptyScore();
        
        JTextPane pane = scoreBoard.getTextPane();
        String text = pane.getText();
        
        assertNotNull("텍스트가 설정되어야 함", text);
        assertTrue("점수 정보가 있어야 함", text.contains("점수"));
        assertTrue("레벨 정보가 있어야 함", text.contains("레벨"));
        assertTrue("줄 정보가 있어야 함", text.contains("줄"));
    }

    /**
     * 테스트 7: showEmptyScore - 모드 정보 포함
     */
    @Test
    public void testScoreBoard_ShowEmptyScoreWithMode() {
        scoreBoard.showEmptyScore();
        
        JTextPane pane = scoreBoard.getTextPane();
        String text = pane.getText();
        
        assertTrue("모드 정보가 있어야 함", text.contains("모드"));
    }

    /**
     * 테스트 8: showEmptyScore - 조작법 포함
     */
    @Test
    public void testScoreBoard_ShowEmptyScoreWithControls() {
        scoreBoard.showEmptyScore();
        
        JTextPane pane = scoreBoard.getTextPane();
        String text = pane.getText();
        
        assertTrue("조작법이 있어야 함", text.contains("조작법"));
        assertTrue("회전 키가 있어야 함", text.contains("회전"));
        assertTrue("드롭 키가 있어야 함", text.contains("드롭"));
    }

    /**
     * 테스트 9: updateGameMode - 아이템 모드
     */
    @Test
    public void testScoreBoard_UpdateGameModeItem() {
        System.setProperty("tetris.game.mode", "ITEM");
        
        ScoreBoard board = new ScoreBoard();
        board.showEmptyScore();
        
        JTextPane pane = board.getTextPane();
        String text = pane.getText();
        
        assertTrue("아이템 모드가 표시되어야 함", text.contains("아이템"));
    }

    /**
     * 테스트 10: updateGameMode - 일반 모드 (이지)
     */
    @Test
    public void testScoreBoard_UpdateGameModeNormalEasy() {
        System.setProperty("tetris.game.mode", "NORMAL");
        System.setProperty("tetris.game.difficulty", "EASY");
        
        ScoreBoard board = new ScoreBoard();
        board.showEmptyScore();
        
        JTextPane pane = board.getTextPane();
        String text = pane.getText();
        
        assertTrue("일반 모드가 표시되어야 함", text.contains("일반"));
        assertTrue("이지 난이도가 표시되어야 함", text.contains("이지"));
    }

    /**
     * 테스트 11: updateGameMode - 일반 모드 (노말)
     */
    @Test
    public void testScoreBoard_UpdateGameModeNormalNormal() {
        System.setProperty("tetris.game.mode", "NORMAL");
        System.setProperty("tetris.game.difficulty", "NORMAL");
        
        ScoreBoard board = new ScoreBoard();
        board.showEmptyScore();
        
        JTextPane pane = board.getTextPane();
        String text = pane.getText();
        
        assertTrue("일반 모드가 표시되어야 함", text.contains("일반"));
        assertTrue("노말 난이도가 표시되어야 함", text.contains("노말"));
    }

    /**
     * 테스트 12: updateGameMode - 일반 모드 (하드)
     */
    @Test
    public void testScoreBoard_UpdateGameModeNormalHard() {
        System.setProperty("tetris.game.mode", "NORMAL");
        System.setProperty("tetris.game.difficulty", "HARD");
        
        ScoreBoard board = new ScoreBoard();
        board.showEmptyScore();
        
        JTextPane pane = board.getTextPane();
        String text = pane.getText();
        
        assertTrue("일반 모드가 표시되어야 함", text.contains("일반"));
        assertTrue("하드 난이도가 표시되어야 함", text.contains("하드"));
    }

    /**
     * 테스트 13: updateGameMode - 기본값 (모드 속성 없음)
     */
    @Test
    public void testScoreBoard_UpdateGameModeDefault() {
        System.clearProperty("tetris.game.mode");
        System.clearProperty("tetris.game.difficulty");
        
        ScoreBoard board = new ScoreBoard();
        board.showEmptyScore();
        
        JTextPane pane = board.getTextPane();
        String text = pane.getText();
        
        assertNotNull("기본 모드가 표시되어야 함", text);
    }

    /**
     * 테스트 14: updateGameMode - 잘못된 난이도 (노말로 폴백)
     */
    @Test
    public void testScoreBoard_UpdateGameModeInvalidDifficulty() {
        System.setProperty("tetris.game.mode", "NORMAL");
        System.setProperty("tetris.game.difficulty", "INVALID");
        
        ScoreBoard board = new ScoreBoard();
        board.showEmptyScore();
        
        JTextPane pane = board.getTextPane();
        String text = pane.getText();
        
        assertTrue("노말 난이도로 폴백되어야 함", text.contains("노말"));
    }

    /**
     * 테스트 15: JTextPane 스타일 - 폰트 크기
     */
    @Test
    public void testScoreBoard_TextPaneStyleFontSize() {
        SimpleAttributeSet styleSet = scoreBoard.getStyleSet();
        assertNotNull("스타일 셋이 존재해야 함", styleSet);
    }

    /**
     * 테스트 16: JTextPane 배경색
     */
    @Test
    public void testScoreBoard_TextPaneBackgroundColor() {
        JTextPane pane = scoreBoard.getTextPane();
        assertEquals("JTextPane 배경색이 검정이어야 함", Color.BLACK, pane.getBackground());
    }

    /**
     * 테스트 17: showEmptyScore 여러 번 호출
     */
    @Test
    public void testScoreBoard_ShowEmptyScoreMultipleTimes() {
        scoreBoard.showEmptyScore();
        String firstText = scoreBoard.getTextPane().getText();
        
        scoreBoard.showEmptyScore();
        String secondText = scoreBoard.getTextPane().getText();
        
        assertNotNull("첫 번째 텍스트가 존재해야 함", firstText);
        assertNotNull("두 번째 텍스트가 존재해야 함", secondText);
    }

    /**
     * 테스트 18: 레이아웃 확인
     */
    @Test
    public void testScoreBoard_Layout() {
        assertTrue("BorderLayout을 사용해야 함", 
                   scoreBoard.getLayout() instanceof java.awt.BorderLayout);
    }

    /**
     * 테스트 19: 모든 조작키 정보 포함
     */
    @Test
    public void testScoreBoard_AllControlKeys() {
        scoreBoard.showEmptyScore();
        
        JTextPane pane = scoreBoard.getTextPane();
        String text = pane.getText();
        
        assertTrue("이동 키가 있어야 함", text.contains("이동"));
        assertTrue("Space 키가 있어야 함", text.contains("Space"));
        assertTrue("ESC 키가 있어야 함", text.contains("ESC"));
    }

    /**
     * 테스트 20: 초기 점수 값
     */
    @Test
    public void testScoreBoard_InitialScoreValue() {
        scoreBoard.showEmptyScore();
        
        JTextPane pane = scoreBoard.getTextPane();
        String text = pane.getText();
        
        assertTrue("초기 점수가 0이어야 함", text.contains("점수: 0"));
        assertTrue("초기 레벨이 1이어야 함", text.contains("레벨: 1"));
        assertTrue("초기 줄이 0이어야 함", text.contains("줄: 0"));
    }
}
