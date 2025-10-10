package se.tetris.team5.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.components.home.Title;
import se.tetris.team5.utils.setting.GameSettings;
import se.tetris.team5.ScreenController;

public class home implements KeyListener {
    
    private ScreenController screenController;
    private SimpleAttributeSet styleSet;
    private int selectedMenu = 0; // 0: 게임시작, 1: 스코어보기, 2: 설정, 3: 종료
    private JTextPane currentTextPane; // 현재 사용 중인 textPane 저장
    
    // 창 크기 정보
    private int windowWidth;
    private int windowHeight;
    private Title.WindowSize currentWindowSize;
    
    // Title 컴포넌트
    private Title titleComponent;
    
    private String[] menuOptions = {
        "게임 시작",
        "스코어 보기", 
        "설정",
        "종료"
    };
    
    private String[] menuDescriptions = {
        "새로운 테트리스 게임을 시작합니다",
        "역대 최고 기록들을 확인합니다",
        "게임 설정을 변경합니다",
        "게임을 종료합니다"
    };
    
    public home(ScreenController screenController) {
        this.screenController = screenController;
        
        // GameSettings에서 창 크기 가져오기 및 윈도우 크기 설정
        GameSettings settings = GameSettings.getInstance();
        windowWidth = settings.getWindowWidth();
        windowHeight = settings.getWindowHeight();
        
        // 창 크기에 따른 레이아웃 모드 결정
        currentWindowSize = Title.determineWindowSize(windowWidth, windowHeight);
        
        // Title 컴포넌트 초기화
        titleComponent = new Title(currentWindowSize);
        
        initializeStyles();
    }
    
    private void initializeStyles() {
        // 텍스트 스타일 설정 (유니코드 지원 개선)
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 16);
        
        // 유니코드를 잘 지원하는 폰트들을 우선순위로 설정
        String[] fontFamilies = {
            "NanumGothic", "Malgun Gothic", "MS Gothic", 
            "DejaVu Sans Mono", "Consolas", "Courier New", 
            "monospace"
        };
        
        // 사용 가능한 폰트 찾기
        String selectedFont = findBestFont(fontFamilies);
        StyleConstants.setFontFamily(styleSet, selectedFont);
        
        StyleConstants.setBold(styleSet, true);
        
        // 색맹 모드에 따른 색상 설정
        GameSettings gameSettings = GameSettings.getInstance();
        StyleConstants.setForeground(styleSet, gameSettings.getUIColor("text"));
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
    }
    
    public void display(JTextPane textPane) {
        this.currentTextPane = textPane; // textPane 저장
        
        // 색맹 모드에 따른 배경색 설정
        GameSettings gameSettings = GameSettings.getInstance();
        textPane.setBackground(gameSettings.getUIColor("background"));
        textPane.addKeyListener(this); // KeyListener 추가
        drawHomeScreen(textPane);
    }
    
    private void drawHomeScreen(JTextPane textPane) {
        StringBuilder sb = new StringBuilder();
        
        // Title 컴포넌트를 사용하여 제목 그리기
        sb.append(titleComponent.drawTitle());
        
        // 창 크기에 따른 메뉴 디자인
        drawMenu(sb);
        
        // 조작법 및 정보
        drawControls(sb);
        drawGameInfo(sb);
        
        updateDisplay(textPane, sb.toString());
    }
    

    
    /**
     * 창 크기에 따른 메뉴를 그립니다
     */
    private void drawMenu(StringBuilder sb) {
        String menuHeader = getMenuHeader();
        sb.append(menuHeader).append("\n");
        sb.append("\n");
        
        
        // 메뉴 옵션들
        for(int i = 0; i < menuOptions.length; i++) {
            if(i == selectedMenu) {
                sb.append("►►  ");
            } else {
                sb.append("");
            }
            
            // 메뉴 아이콘 추가
            switch(i) {
                case 0: sb.append("🎯 "); break;
                case 1: sb.append("🏆 "); break;
                case 2: sb.append("⚙️ "); break;
                case 3: sb.append("❌ "); break;
            }
            
            sb.append(menuOptions[i]);
            
            if(i == selectedMenu) {
                sb.append("  ◄◄");
            }
            sb.append("\n");
            
            // 선택된 메뉴의 설명 표시
            if(i == selectedMenu && currentWindowSize != Title.WindowSize.SMALL) {
                sb.append("💬 ").append(menuDescriptions[i]).append("\n");
            }
            else {
            sb.append("\n");
            }
            sb.append("\n");
        }
        
        String menuFooter = getMenuFooter();
        sb.append(menuFooter).append("\n\n");
    }
    
    /**
     * 창 크기에 따른 메뉴 헤더를 반환합니다
     */
    private String getMenuHeader() {
        switch (currentWindowSize) {
            case SMALL:
                return "◆══════ 메뉴 ═══════◆";
            case MEDIUM:
                return "◆════════════ 메뉴 ════════════◆";
            case LARGE:
                return "◆═══════════════ 메뉴 ════════════════◆";
            case XLARGE:
            default:
                return "◆══════════════════ 메뉴 ═══════════════════◆";
        }
    }
    

    
    /**
     * 창 크기에 따른 메뉴 푸터를 반환합니다
     */
    private String getMenuFooter() {
        switch (currentWindowSize) {
            case SMALL:
                return "◆═══════════════════◆";
            case MEDIUM:
                return "◆══════════════════════════════◆";
            case LARGE:
                return "◆════════════════════════════════════◆";
            case XLARGE:
            default:
                return "◆════════════════════════════════════════════◆";
        }
    }
    
    /**
     * 창 크기에 따른 조작법을 그립니다
     */
    private void drawControls(StringBuilder sb) {
        String separator = getSeparator();
        sb.append(separator).append("\n");
        sb.append("🎮 조작법:\n");
        sb.append("↑↓ : 메뉴 선택    Enter : 확인\n");
        sb.append("ESC : 게임 종료\n");
        sb.append(separator).append("\n\n");
    }
    
    /**
     * 창 크기에 따른 게임 정보를 그립니다
     */
    private void drawGameInfo(StringBuilder sb) {
        // 작은 창에서는 간단하게 표시
        if (currentWindowSize == Title.WindowSize.SMALL) {
            sb.append("📋 버전: 1.0.0 | 5조\n");
            sb.append("🏆 최고: ").append(getHighestScore()).append("점\n");
        } else {
            sb.append("📋 게임 정보:\n");
            sb.append("버전: 1.0.0\n");
            sb.append("개발팀: 5조\n");
            sb.append("최고 기록: ").append(getHighestScore()).append("점\n");
        }
    }
    
    /**
     * 창 크기에 따른 구분선을 반환합니다
     */
    private String getSeparator() {
        switch (currentWindowSize) {
            case SMALL:
                return "♦═══════════════════♦";
            case MEDIUM:
                return "♦═══════════════════════════════♦";
            case LARGE:
                return "♦═══════════════════════════════════════♦";
            case XLARGE:
            default:
                return "♦═══════════════════════════════════════════════♦";
        }
    }
    
    private String getHighestScore() {
        try {
            se.tetris.team5.utils.score.ScoreManager scoreManager = se.tetris.team5.utils.score.ScoreManager.getInstance();
            var topScores = scoreManager.getTopScores(1);
            if (!topScores.isEmpty()) {
                return String.format("%,d", topScores.get(0).getScore());
            }
        } catch (Exception e) {
            // ScoreManager 초기화 중 오류 발생시 기본값 반환
        }
        return "없음";
    }
    
    private void updateDisplay(JTextPane textPane, String text) {
        textPane.setText(text);
        StyledDocument doc = textPane.getStyledDocument();
        
        // 기본 스타일 적용
        doc.setCharacterAttributes(0, doc.getLength(), styleSet, false);
        doc.setParagraphAttributes(0, doc.getLength(), styleSet, false);
        

        
        // 선택된 메뉴 강조 (색맹 모드 대응)
        GameSettings gameSettings = GameSettings.getInstance();
        String selectedText = "►►  " + getMenuIcon(selectedMenu) + menuOptions[selectedMenu] + "  ◄◄";
        int selectedIndex = text.indexOf(selectedText);
        if (selectedIndex != -1) {
            SimpleAttributeSet selectedStyle = new SimpleAttributeSet(styleSet);
            // 색맹 모드일 때는 구별하기 쉬운 밝은 노란색, 일반 모드일 때는 초록색
            Color highlightColor = gameSettings.isColorblindMode() ? 
                new Color(240, 228, 66) : Color.GREEN; // 색맹 모드: 밝은 노란색, 일반: 초록색
            StyleConstants.setForeground(selectedStyle, highlightColor);
            StyleConstants.setBold(selectedStyle, true);
            doc.setCharacterAttributes(selectedIndex, selectedText.length(), selectedStyle, false);
        }
        
        // 설명 텍스트 색상
        String descText = "💬 " + menuDescriptions[selectedMenu];
        int descIndex = text.indexOf(descText);
        if (descIndex != -1) {
            SimpleAttributeSet descStyle = new SimpleAttributeSet(styleSet);
            // 색맹 모드에서도 읽기 쉬운 회색 사용
            Color descColor = gameSettings.isColorblindMode() ? 
                new Color(180, 180, 180) : Color.LIGHT_GRAY;
            StyleConstants.setForeground(descStyle, descColor);
            StyleConstants.setItalic(descStyle, true);
            doc.setCharacterAttributes(descIndex, descText.length(), descStyle, false);
        }
        
        // 이모지 및 아이콘 색상 강조
        applyEmojiColors(doc, text);
    }
    
    private String getMenuIcon(int index) {
        switch(index) {
            case 0: return "🎯 ";
            case 1: return "🏆 ";
            case 2: return "⚙️ ";
            case 3: return "❌ ";
            default: return "";
        }
    }
    
    private void applyEmojiColors(StyledDocument doc, String text) {
        GameSettings gameSettings = GameSettings.getInstance();
        
        // 구분선 색상
        String[] separators = {"═══════════", "───────────"};
        for (String sep : separators) {
            int index = 0;
            while ((index = text.indexOf(sep, index)) != -1) {
                SimpleAttributeSet sepStyle = new SimpleAttributeSet(styleSet);
                StyleConstants.setForeground(sepStyle, gameSettings.getUIColor("border"));
                doc.setCharacterAttributes(index, sep.length(), sepStyle, false);
                index += sep.length();
            }
        }
        
        // 섹션 제목들 색상
        String[] sections = {"🎮 조작법:", "📋 게임 정보:"};
        for (String section : sections) {
            int index = text.indexOf(section);
            if (index != -1) {
                SimpleAttributeSet sectionStyle = new SimpleAttributeSet(styleSet);
                // 색맹 모드에서는 주황색 대신 구별하기 쉬운 색상 사용
                Color sectionColor = gameSettings.isColorblindMode() ? 
                    new Color(230, 159, 0) : Color.ORANGE; // 더 진한 주황색
                StyleConstants.setForeground(sectionStyle, sectionColor);
                doc.setCharacterAttributes(index, section.length(), sectionStyle, false);
            }
        }
    }
    

    
    private void selectCurrentMenu() {
        switch (selectedMenu) {
            case 0: // 게임 시작
                screenController.showScreen("game");
                break;
            case 1: // 스코어 보기
                screenController.showScreen("score");
                break;
            case 2: // 설정
                screenController.showScreen("setting");
                break;
            case 3: // 종료
                showExitConfirmation();
                break;
        }
    }
    
    private boolean isExitConfirmMode = false;
    
    private void showExitConfirmation() {
        isExitConfirmMode = true;
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n");
        sb.append("════════════════════════════════════════\n");
        sb.append("게임 종료 확인\n");
        sb.append("════════════════════════════════════════\n\n");
        sb.append("정말로 게임을 종료하시겠습니까?\n\n\n");
        sb.append("   Y: 종료    N: 취소\n\n\n");
        sb.append("════════════════════════════════════════\n");
        
        if (currentTextPane != null) {
            updateDisplay(currentTextPane, sb.toString());
        }
    }
    
    private void handleExitConfirm(boolean confirm) {
        isExitConfirmMode = false;
        if (confirm) {
            // 종료 메시지 표시
            StringBuilder sb = new StringBuilder();
            sb.append("\n\n\n\n\n");
            sb.append("════════════════════════════════════════\n");
            sb.append("테트리스를 플레이해 주셔서\n");
            sb.append("감사합니다!\n");
            sb.append("════════════════════════════════════════\n");
            
            if (currentTextPane != null) {
                updateDisplay(currentTextPane, sb.toString());
            }
            
            // 1초 후 종료
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    System.exit(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            // 취소 - 홈 화면으로 돌아가기
            if (currentTextPane != null) {
                drawHomeScreen(currentTextPane);
            }
        }
    }
    
    /*
    // 이제 ScreenController를 통해 네비게이션하므로 이 메소드들은 불필요
    private void showGameScreen() {
        // setVisible(false); // TODO: ScreenController 패턴에서는 불필요
        // 모듈화된 게임 화면 사용
        JFrame gameFrame = new JFrame("5조 테트리스 - 게임");
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // GameSettings에서 창 크기 가져오기
        GameSettings settings = GameSettings.getInstance();
        String windowSize = settings.getWindowSize();
        String[] sizeParts = windowSize.split("x");
        int width = Integer.parseInt(sizeParts[0]);
        int height = Integer.parseInt(sizeParts[1]);
        gameFrame.setSize(width, height);
        
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setResizable(false);
        
        // 모듈화된 game 패널 추가
        se.tetris.team5.screens.game gamePanel = new se.tetris.team5.screens.game(screenController);
        gameFrame.add(gamePanel);
        
        gameFrame.setVisible(true);
        gamePanel.requestFocus(); // 키 입력을 위한 포커스 설정
    }
    
    private void showScoreScreen() {
        // setVisible(false); // TODO: ScreenController 패턴에서는 불필요
        score scoreWindow = new score();
        scoreWindow.setVisible(true);
    }
    
    private void showSettingScreen() {
        // setVisible(false); // TODO: ScreenController 패턴에서는 불필요
        setting settingWindow = new setting();
        settingWindow.setVisible(true);
    }
    */

    @Override
    public void keyPressed(KeyEvent e) {
        e.consume(); // 이벤트 소비하여 전파 방지
        
        // 종료 확인 모드일 때
        if (isExitConfirmMode) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_Y:
                    handleExitConfirm(true);
                    break;
                case KeyEvent.VK_N:
                case KeyEvent.VK_ESCAPE:
                    handleExitConfirm(false);
                    break;
            }
            return;
        }
        
        // 일반 메뉴 모드일 때
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedMenu = (selectedMenu - 1 + menuOptions.length) % menuOptions.length;
                if (currentTextPane != null) {
                    drawHomeScreen(currentTextPane);
                }
                break;
            case KeyEvent.VK_DOWN:
                selectedMenu = (selectedMenu + 1) % menuOptions.length;
                if (currentTextPane != null) {
                    drawHomeScreen(currentTextPane);
                }
                break;
            case KeyEvent.VK_ENTER:
                selectCurrentMenu();
                break;
            case KeyEvent.VK_ESCAPE:
                showExitConfirmation();
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    /**
     * 유니코드를 잘 지원하는 폰트를 찾습니다
     */
    private String findBestFont(String[] fontFamilies) {
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        // 우선순위 순서대로 사용 가능한 폰트 찾기
        for (String fontFamily : fontFamilies) {
            for (String availableFont : availableFonts) {
                if (availableFont.toLowerCase().contains(fontFamily.toLowerCase()) || 
                    fontFamily.equalsIgnoreCase(availableFont)) {
                    return fontFamily;
                }
            }
        }
        
        // 기본 폰트 반환
        return "Dialog";
    }
}
