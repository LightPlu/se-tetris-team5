package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.*;

import se.tetris.team5.ScreenController;
import se.tetris.team5.utils.score.ScoreManager;


/**
 * UI 컴포넌트와 사용자 인터페이스 상호작용 테스트
 */
public class UIInteractionTest {

    private ScreenController mockScreenController;
    private game gameInstance;
    private score scoreInstance;
    private ScoreManager scoreManager;
    
    @Before
    public void setUp() {
        // MockScreenController 생성
        mockScreenController = new ScreenController() {
            @Override
            public void showScreen(String screenName) {
                // 테스트용 빈 구현
            }
        };
        
        gameInstance = new game(mockScreenController);
        scoreInstance = new score(mockScreenController);
        scoreManager = ScoreManager.getInstance();
        
        // 테스트를 위해 점수 목록 초기화
        try {
            Field scoresField = ScoreManager.class.getDeclaredField("scores");
            scoresField.setAccessible(true);
            List<?> scores = (List<?>) scoresField.get(scoreManager);
            scores.clear();
        } catch (Exception e) {
            // 필드가 없거나 접근할 수 없는 경우 무시
        }
    }
    
    // ========== 점수 화면 UI 테스트 ==========
    
    @Test
    public void testScoreScreenInitialization() {
        // 점수 화면 초기화 테스트
        assertNotNull("점수 화면 인스턴스가 생성되어야 합니다", scoreInstance);
        // score 클래스는 일반 클래스이므로 Object 타입 확인
        assertTrue("점수 화면이 Object 타입이어야 합니다", scoreInstance instanceof Object);
    }
    
    @Test
    public void testModeTabCreation() throws Exception {
        // 모드 탭 생성 테스트
        Method createModeTabPanelMethod = score.class.getDeclaredMethod("createModeTabPanel");
        createModeTabPanelMethod.setAccessible(true);
        
        JPanel modeTabPanel = (JPanel) createModeTabPanelMethod.invoke(scoreInstance);
        
        assertNotNull("모드 탭 패널이 생성되어야 합니다", modeTabPanel);
        assertTrue("모드 탭 패널에 컴포넌트들이 있어야 합니다", modeTabPanel.getComponentCount() > 0);
        
        // 모든 컴포넌트가 JButton인지 확인
        Component[] components = modeTabPanel.getComponents();
        int buttonCount = 0;
        
        for (Component comp : components) {
            if (comp instanceof JButton) {
                buttonCount++;
                JButton button = (JButton) comp;
                
                // 버튼 텍스트 확인
                String buttonText = button.getText();
                assertNotNull("버튼 텍스트가 설정되어야 합니다", buttonText);
                
                // 한국어 모드 이름인지 확인
                assertTrue("버튼이 한국어 모드 이름을 포함해야 합니다", 
                          buttonText.equals("쉬움") || buttonText.equals("보통") || 
                          buttonText.equals("어려움") || buttonText.equals("아이템"));
                
                // 버튼이 활성화되어 있는지 확인
                assertTrue("모드 버튼이 활성화되어야 합니다", button.isEnabled());
                
                // 액션 리스너가 등록되어 있는지 확인
                ActionListener[] listeners = button.getActionListeners();
                assertTrue("모드 버튼에 액션 리스너가 등록되어야 합니다", listeners.length > 0);
            }
        }
        
        assertEquals("4개의 모드 버튼이 있어야 합니다", 4, buttonCount);
    }
    
    @Test
    public void testScoreTableCreation() throws Exception {
        // 점수 테이블 생성 테스트
        // 테스트용 점수 데이터 추가
        scoreManager.addScore("Player1", 2000, 10, 50, 300000, "어려움");
        scoreManager.addScore("Player2", 1500, 8, 35, 200000, "보통");
        scoreManager.addScore("Player3", 1800, 9, 40, 250000, "아이템");
        
        Method createScoreTableMethod = score.class.getDeclaredMethod("createScoreTable", String.class, int.class);
        createScoreTableMethod.setAccessible(true);
        
        JPanel scoreTable = (JPanel) createScoreTableMethod.invoke(scoreInstance, "어려움", 1);
        
        assertNotNull("점수 테이블이 생성되어야 합니다", scoreTable);
        assertTrue("점수 테이블에 컴포넌트들이 있어야 합니다", scoreTable.getComponentCount() > 0);
        
        // 테이블 헤더와 데이터 행들이 있는지 확인
        Component[] components = scoreTable.getComponents();
        int labelCount = 0;
        
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                labelCount++;
                JLabel label = (JLabel) comp;
                String text = label.getText();
                
                // 라벨 텍스트가 설정되어 있는지 확인
                assertNotNull("라벨 텍스트가 설정되어야 합니다", text);
            }
        }
        
        assertTrue("점수 테이블에 라벨들이 있어야 합니다", labelCount > 0);
    }
    
    @Test
    public void testPageNavigationButtons() throws Exception {
        // 페이지 네비게이션 버튼 테스트
        // 충분한 테스트 데이터 생성 (페이지네이션을 위해)
        for (int i = 1; i <= 25; i++) {
            scoreManager.addScore("Player" + i, i * 100, i % 10 + 1, i * 2, i * 10000L, "보통");
        }
        
        Method createPageNavigationMethod = score.class.getDeclaredMethod("createPageNavigation", String.class, int.class);
        createPageNavigationMethod.setAccessible(true);
        
        JPanel navigation = (JPanel) createPageNavigationMethod.invoke(scoreInstance, "보통", 1);
        
        assertNotNull("페이지 네비게이션이 생성되어야 합니다", navigation);
        
        // 네비게이션 버튼들 확인
        Component[] components = navigation.getComponents();
        boolean hasPrevButton = false;
        boolean hasNextButton = false;
        
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String buttonText = button.getText();
                
                if (buttonText.contains("이전")) {
                    hasPrevButton = true;
                } else if (buttonText.contains("다음")) {
                    hasNextButton = true;
                }
                
                // 버튼에 액션 리스너가 있는지 확인
                ActionListener[] listeners = button.getActionListeners();
                assertTrue("네비게이션 버튼에 액션 리스너가 있어야 합니다", listeners.length > 0);
            }
        }
        
        assertTrue("이전 페이지 버튼이 있어야 합니다", hasPrevButton);
        assertTrue("다음 페이지 버튼이 있어야 합니다", hasNextButton);
    }
    
    // ========== 게임 화면 UI 테스트 ==========
    
    @Test
    public void testGameScreenInitialization() {
        // 게임 화면 초기화 테스트
        assertNotNull("게임 화면 인스턴스가 생성되어야 합니다", gameInstance);
        assertTrue("게임 화면이 JPanel을 상속해야 합니다", gameInstance instanceof JPanel);
    }
    
    @Test
    public void testGameBoardRendering() throws Exception {
        // 게임 보드 렌더링 테스트
        Field boardField = game.class.getDeclaredField("board");
        Field boardColorsField = game.class.getDeclaredField("boardColors");
        
        boardField.setAccessible(true);
        boardColorsField.setAccessible(true);
        
        int[][] board = (int[][]) boardField.get(gameInstance);
        Color[][] boardColors = (Color[][]) boardColorsField.get(gameInstance);
        
        assertNotNull("게임 보드가 초기화되어야 합니다", board);
        assertNotNull("게임 보드 색상이 초기화되어야 합니다", boardColors);
        
        assertEquals("보드 높이가 올바르게 설정되어야 합니다", game.HEIGHT, board.length);
        assertEquals("보드 너비가 올바르게 설정되어야 합니다", game.WIDTH, board[0].length);
    }
    
    @Test
    public void testScoreDisplay() throws Exception {
        // 점수 표시 테스트
        Field currentScoreField = game.class.getDeclaredField("currentScore");
        Field linesClearedField = game.class.getDeclaredField("linesCleared");
        Field levelField = game.class.getDeclaredField("level");
        
        currentScoreField.setAccessible(true);
        linesClearedField.setAccessible(true);
        levelField.setAccessible(true);
        
        // 테스트 값 설정
        currentScoreField.set(gameInstance, 1500);
        linesClearedField.set(gameInstance, 35);
        levelField.set(gameInstance, 7);
        
        int score = (Integer) currentScoreField.get(gameInstance);
        int lines = (Integer) linesClearedField.get(gameInstance);
        int level = (Integer) levelField.get(gameInstance);
        
        assertEquals("점수가 올바르게 설정되어야 합니다", 1500, score);
        assertEquals("라인 수가 올바르게 설정되어야 합니다", 35, lines);
        assertEquals("레벨이 올바르게 설정되어야 합니다", 7, level);
    }
    
    // ========== 키보드 입력 테스트 ==========
    
    @Test
    public void testKeyboardInputHandling() throws Exception {
        // 키보드 입력 처리 테스트
        // KeyListener가 등록되어 있는지 확인
        java.awt.event.KeyListener[] keyListeners = gameInstance.getKeyListeners();
        assertTrue("게임 화면에 키 리스너가 등록되어야 합니다", keyListeners.length > 0);
        
        // 게임 화면이 포커스를 받을 수 있는지 확인
        assertTrue("게임 화면이 포커스를 받을 수 있어야 합니다", gameInstance.isFocusable());
    }
    
    @Test
    public void testGameControlMethods() throws Exception {
        // 게임 제어 메서드들 존재 확인
        Method[] methods = game.class.getDeclaredMethods();
        
        boolean hasMoveLeft = false;
        boolean hasMoveRight = false;
        boolean hasMoveDown = false;
        boolean hasRotate = false;
        boolean hasHardDrop = false;
        
        for (Method method : methods) {
            String methodName = method.getName();
            
            if (methodName.equals("moveLeft")) hasMoveLeft = true;
            else if (methodName.equals("moveRight")) hasMoveRight = true;
            else if (methodName.equals("moveDown")) hasMoveDown = true;
            else if (methodName.equals("rotateBlock")) hasRotate = true;
            else if (methodName.equals("hardDrop")) hasHardDrop = true;
        }
        
        assertTrue("왼쪽 이동 메서드가 있어야 합니다", hasMoveLeft);
        assertTrue("오른쪽 이동 메서드가 있어야 합니다", hasMoveRight);
        assertTrue("아래 이동 메서드가 있어야 합니다", hasMoveDown);
        assertTrue("회전 메서드가 있어야 합니다", hasRotate);
        assertTrue("하드 드롭 메서드가 있어야 합니다", hasHardDrop);
    }
    
    // ========== 모드 선택 상호작용 테스트 ==========
    
    @Test
    public void testModeSelectionInteraction() throws Exception {
        // 모드 선택 상호작용 테스트
        // 다양한 모드로 점수 추가
        scoreManager.addScore("EasyPlayer", 1000, 5, 25, 120000, "쉬움");
        scoreManager.addScore("HardPlayer", 2000, 10, 50, 300000, "어려움");
        
        Method createModeTabPanelMethod = score.class.getDeclaredMethod("createModeTabPanel");
        createModeTabPanelMethod.setAccessible(true);
        
        JPanel modeTabPanel = (JPanel) createModeTabPanelMethod.invoke(scoreInstance);
        
        // 각 모드 버튼 찾기 및 클릭 시뮬레이션
        Component[] components = modeTabPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String buttonText = button.getText();
                
                if (buttonText.equals("쉬움") || buttonText.equals("어려움")) {
                    // 버튼 클릭 시뮬레이션
                    ActionListener[] listeners = button.getActionListeners();
                    if (listeners.length > 0) {
                        ActionEvent mockEvent = new ActionEvent(button, ActionEvent.ACTION_PERFORMED, buttonText);
                        
                        // 액션 리스너 실행 (예외가 발생하지 않으면 성공)
                        try {
                            listeners[0].actionPerformed(mockEvent);
                            assertTrue("모드 버튼 클릭이 정상 처리되어야 합니다", true);
                        } catch (Exception e) {
                            fail("모드 버튼 클릭 처리 중 오류 발생: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
    
    // ========== 폰트 적용 테스트 ==========
    
    @Test
    public void testFontApplicationToComponents() throws Exception {
        // 컴포넌트에 폰트 적용 테스트
        Method createKoreanFontMethod = score.class.getDeclaredMethod("createKoreanFont", int.class);
        createKoreanFontMethod.setAccessible(true);
        
        Font koreanFont = (Font) createKoreanFontMethod.invoke(scoreInstance, 16);
        
        // JLabel에 폰트 적용 테스트
        JLabel testLabel = new JLabel("테스트 라벨");
        testLabel.setFont(koreanFont);
        
        assertEquals("라벨에 한국어 폰트가 적용되어야 합니다", koreanFont, testLabel.getFont());
        
        // JButton에 폰트 적용 테스트
        JButton testButton = new JButton("테스트 버튼");
        testButton.setFont(koreanFont);
        
        assertEquals("버튼에 한국어 폰트가 적용되어야 합니다", koreanFont, testButton.getFont());
    }
    
    @Test
    public void testUIComponentVisibility() throws Exception {
        // UI 컴포넌트 가시성 테스트
        Method createModeTabPanelMethod = score.class.getDeclaredMethod("createModeTabPanel");
        createModeTabPanelMethod.setAccessible(true);
        
        JPanel modeTabPanel = (JPanel) createModeTabPanelMethod.invoke(scoreInstance);
        
        assertTrue("모드 탭 패널이 표시되어야 합니다", modeTabPanel.isVisible());
        
        // 모든 자식 컴포넌트들도 표시되는지 확인
        Component[] components = modeTabPanel.getComponents();
        
        for (Component comp : components) {
            assertTrue("모드 탭의 모든 컴포넌트가 표시되어야 합니다", comp.isVisible());
        }
    }
    
    // ========== 데이터 바인딩 테스트 ==========
    
    @Test
    public void testScoreDataBinding() throws Exception {
        // 점수 데이터 바인딩 테스트
        String testMode = "보통";
        
        // 테스트 점수 데이터 추가
        scoreManager.addScore("DataPlayer1", 1500, 7, 35, 180000, testMode);
        scoreManager.addScore("DataPlayer2", 1200, 6, 28, 150000, testMode);
        
        Method createScoreTableMethod = score.class.getDeclaredMethod("createScoreTable", String.class, int.class);
        createScoreTableMethod.setAccessible(true);
        
        JPanel scoreTable = (JPanel) createScoreTableMethod.invoke(scoreInstance, testMode, 1);
        
        assertNotNull("점수 테이블이 생성되어야 합니다", scoreTable);
        
        // 테이블에 데이터가 반영되었는지 확인
        Component[] components = scoreTable.getComponents();
        boolean foundPlayerData = false;
        
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                String text = label.getText();
                
                if (text != null && (text.contains("DataPlayer1") || text.contains("DataPlayer2") || 
                                   text.contains("1500") || text.contains("1200"))) {
                    foundPlayerData = true;
                    break;
                }
            }
        }
        
        assertTrue("점수 테이블에 플레이어 데이터가 표시되어야 합니다", foundPlayerData);
    }
    
    @Test
    public void testEmptyDataHandling() throws Exception {
        // 빈 데이터 처리 테스트
        Method createScoreTableMethod = score.class.getDeclaredMethod("createScoreTable", String.class, int.class);
        createScoreTableMethod.setAccessible(true);
        
        // 존재하지 않는 모드로 테이블 생성
        JPanel emptyTable = (JPanel) createScoreTableMethod.invoke(scoreInstance, "존재하지않는모드", 1);
        
        assertNotNull("빈 데이터에도 테이블이 생성되어야 합니다", emptyTable);
        
        // 빈 데이터 메시지가 표시되는지 확인
        Component[] components = emptyTable.getComponents();
        
        // 빈 테이블이라도 구조적으로 유효해야 함
        assertTrue("빈 데이터 처리가 정상적으로 되어야 합니다", components != null);
    }
    
    // ========== 레이아웃 테스트 ==========
    
    @Test
    public void testLayoutManagerConfiguration() {
        // 레이아웃 매니저 설정 테스트
        // game 클래스는 JPanel을 상속하므로 레이아웃 매니저 확인 가능
        LayoutManager gameLayout = gameInstance.getLayout();
        assertNotNull("게임 화면에 레이아웃 매니저가 설정되어야 합니다", gameLayout);
        
        // score 클래스는 일반 클래스이므로 인스턴스 존재만 확인
        assertNotNull("점수 화면 인스턴스가 존재해야 합니다", scoreInstance);
    }
    
    @Test
    public void testComponentSizing() throws Exception {
        // 컴포넌트 크기 테스트
        Method createModeTabPanelMethod = score.class.getDeclaredMethod("createModeTabPanel");
        createModeTabPanelMethod.setAccessible(true);
        
        JPanel modeTabPanel = (JPanel) createModeTabPanelMethod.invoke(scoreInstance);
        
        Dimension size = modeTabPanel.getPreferredSize();
        assertTrue("모드 탭 패널의 너비가 0보다 커야 합니다", size.width > 0);
        assertTrue("모드 탭 패널의 높이가 0보다 커야 합니다", size.height > 0);
        
        // 모드 버튼들의 크기도 확인
        Component[] components = modeTabPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JButton) {
                Dimension buttonSize = comp.getPreferredSize();
                assertTrue("모드 버튼의 너비가 0보다 커야 합니다", buttonSize.width > 0);
                assertTrue("모드 버튼의 높이가 0보다 커야 합니다", buttonSize.height > 0);
            }
        }
    }
    
    @Test
    public void testScoreScreenBasicFunctionality() {
        try {
            JTextPane textPane = new JTextPane();
            scoreInstance.display(textPane);
            
            // score 화면이 올바르게 생성되었는지 확인
            assertTrue(textPane.getComponentCount() > 0);
            
            // createKoreanFont 메서드 테스트
            Method createFontMethod = score.class.getDeclaredMethod("createKoreanFont", int.class, int.class);
            createFontMethod.setAccessible(true);
            Font font = (Font) createFontMethod.invoke(scoreInstance, Font.BOLD, 14);
            assertNotNull(font);
            assertEquals(Font.BOLD, font.getStyle());
            
            // getGameModeDisplay 메서드 테스트
            Method getModeMethod = score.class.getDeclaredMethod("getGameModeDisplay", String.class);
            getModeMethod.setAccessible(true);
            assertEquals("아이템", getModeMethod.invoke(scoreInstance, "ITEM"));
            assertEquals("쉬움", getModeMethod.invoke(scoreInstance, "NORMAL_EASY"));
            
            // buildSampleEntries 메서드 테스트
            Method buildSampleMethod = score.class.getDeclaredMethod("buildSampleEntries");
            buildSampleMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<ScoreManager.ScoreEntry> entries = (List<ScoreManager.ScoreEntry>) buildSampleMethod.invoke(scoreInstance);
            assertNotNull(entries);
            assertEquals(10, entries.size());
            
        } catch (Exception e) {
            fail("Score screen basic functionality test failed: " + e.getMessage());
        }
    }

    @Test 
    public void testScoreScreenUIComponents() {
        try {
            JTextPane textPane = new JTextPane();
            scoreInstance.display(textPane);
            
            // createModeTabPanel 테스트  
            Method createTabMethod = score.class.getDeclaredMethod("createModeTabPanel");
            createTabMethod.setAccessible(true);
            JPanel tabPanel = (JPanel) createTabMethod.invoke(scoreInstance);
            assertNotNull(tabPanel);
            assertTrue(tabPanel.getComponentCount() >= 4);
            
            // buildPodiumPanel 테스트
            Method buildPodiumMethod = score.class.getDeclaredMethod("buildPodiumPanel");
            buildPodiumMethod.setAccessible(true);
            JPanel podiumPanel = (JPanel) buildPodiumMethod.invoke(scoreInstance);
            assertNotNull(podiumPanel);
            assertTrue(podiumPanel.getComponentCount() >= 3);
            
            // createPodiumBlock 테스트
            Method createBlockMethod = score.class.getDeclaredMethod("createPodiumBlock", int.class);
            createBlockMethod.setAccessible(true);
            for (int rank = 1; rank <= 3; rank++) {
                JPanel block = (JPanel) createBlockMethod.invoke(scoreInstance, rank);
                assertNotNull(block);
                assertEquals("podium-" + rank, block.getName());
            }
            
            // buildControlsPanel 테스트
            Method buildControlsMethod = score.class.getDeclaredMethod("buildControlsPanel");
            buildControlsMethod.setAccessible(true);
            JPanel controlsPanel = (JPanel) buildControlsMethod.invoke(scoreInstance);
            assertNotNull(controlsPanel);
            
        } catch (Exception e) {
            fail("Score screen UI components test failed: " + e.getMessage());
        }
    }

    @Test
    public void testScoreScreenRenderingAndPagination() {
        try {
            JTextPane textPane = new JTextPane();
            scoreInstance.display(textPane);
            
            // renderScores 메서드 테스트
            Method renderMethod = score.class.getDeclaredMethod("renderScores");
            renderMethod.setAccessible(true);
            renderMethod.invoke(scoreInstance);
            
            // 페이지네이션 테스트
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            
            for (int page = 0; page < 3; page++) {
                pageField.set(scoreInstance, page);
                renderMethod.invoke(scoreInstance);
            }
            
            // 모드 변경 테스트
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            
            String[] modes = {"ITEM", "NORMAL_EASY", "NORMAL_NORMAL", "NORMAL_HARD"};
            for (String mode : modes) {
                modeField.set(scoreInstance, mode);
                renderMethod.invoke(scoreInstance);
            }
            
            // showOnlyDummy 모드 테스트
            Field dummyField = score.class.getDeclaredField("showOnlyDummy");
            dummyField.setAccessible(true);
            dummyField.set(scoreInstance, true);
            renderMethod.invoke(scoreInstance);
            
        } catch (Exception e) {
            fail("Score screen rendering and pagination test failed: " + e.getMessage());
        }
    }

    @Test
    public void testScoreScreenHelperMethods() {
        try {
            JTextPane textPane = new JTextPane();
            scoreInstance.display(textPane);
            
            // findAllComponents 테스트
            Method findAllMethod = score.class.getDeclaredMethod("findAllComponents", Container.class);
            findAllMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Component> components = (List<Component>) findAllMethod.invoke(scoreInstance, textPane);
            assertNotNull(components);
            
            // findPageInfoLabel 테스트
            Method findPageMethod = score.class.getDeclaredMethod("findPageInfoLabel", Container.class);
            findPageMethod.setAccessible(true);
            findPageMethod.invoke(scoreInstance, textPane);
            
            // findHeaderPanel 테스트
            Method findHeaderMethod = score.class.getDeclaredMethod("findHeaderPanel", Container.class);
            findHeaderMethod.setAccessible(true);
            findHeaderMethod.invoke(scoreInstance, textPane);
            
            // findListContainer 테스트
            Method findListMethod = score.class.getDeclaredMethod("findListContainer", Container.class);
            findListMethod.setAccessible(true);
            findListMethod.invoke(scoreInstance, textPane);
            
            // buildFooterImagePanel 테스트
            Method buildFooterMethod = score.class.getDeclaredMethod("buildFooterImagePanel");
            buildFooterMethod.setAccessible(true);
            JPanel footerPanel = (JPanel) buildFooterMethod.invoke(scoreInstance);
            assertNotNull(footerPanel);
            
        } catch (Exception e) {
            fail("Score screen helper methods test failed: " + e.getMessage());
        }
    }

    @Test
    public void testScoreScreenBackgroundAndPainting() {
        try {
            // createBackgroundPanel 테스트
            Method createBgMethod = score.class.getDeclaredMethod("createBackgroundPanel");
            createBgMethod.setAccessible(true);
            JPanel bgPanel = (JPanel) createBgMethod.invoke(scoreInstance);
            assertNotNull(bgPanel);
            
            // 다양한 필드 테스트
            Field modeField = score.class.getDeclaredField("currentGameModeFilter");
            modeField.setAccessible(true);
            assertEquals("ITEM", modeField.get(scoreInstance));
            
            Field pageField = score.class.getDeclaredField("currentPage");
            pageField.setAccessible(true);
            assertEquals(0, pageField.get(scoreInstance));
            
            Field showOnlyDummyField = score.class.getDeclaredField("showOnlyDummy");
            showOnlyDummyField.setAccessible(true);
            assertEquals(false, showOnlyDummyField.get(scoreInstance));
            
            // 상수 테스트
            Field pageSizeField = score.class.getDeclaredField("PAGE_SIZE");
            pageSizeField.setAccessible(true);
            assertEquals(10, pageSizeField.get(null));
            
            Field rowHeightField = score.class.getDeclaredField("ROW_HEIGHT");
            rowHeightField.setAccessible(true);
            assertEquals(40, rowHeightField.get(null));
            
        } catch (Exception e) {
            fail("Score screen background and painting test failed: " + e.getMessage());
        }
    }
}