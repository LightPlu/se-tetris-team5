package se.tetris.team5.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.util.GameSettings;

public class home extends JFrame implements KeyListener {
    
    private static final long serialVersionUID = 1L;
    
    private JTextPane pane;
    private SimpleAttributeSet styleSet;
    private int selectedMenu = 0; // 0: 게임시작, 1: 스코어보기, 2: 설정, 3: 종료
    
    // 창 크기 정보
    private int windowWidth;
    private int windowHeight;
    private WindowSize currentWindowSize;
    
    // 창 크기별 레이아웃 설정
    private enum WindowSize {
        SMALL, MEDIUM, LARGE, XLARGE
    }
    
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
    
    public home() {
        super("5조 테트리스");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // GameSettings에서 창 크기 가져오기 및 윈도우 크기 설정
        GameSettings settings = GameSettings.getInstance();
        String windowSize = settings.getWindowSize();
        String[] sizeParts = windowSize.split("x");
        windowWidth = Integer.parseInt(sizeParts[0]);
        windowHeight = Integer.parseInt(sizeParts[1]);
        
        // 창 크기에 따른 레이아웃 모드 결정
        currentWindowSize = determineWindowSize(windowWidth, windowHeight);
        
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeUI();
        setVisible(true);
    }
    
    /**
     * 창 크기에 따른 레이아웃 모드를 결정합니다
     */
    private WindowSize determineWindowSize(int width, int height) {
        if (width <= 350) {
            return WindowSize.SMALL;
        } else if (width <= 450) {
            return WindowSize.MEDIUM;
        } else if (width <= 550) {
            return WindowSize.LARGE;
        } else {
            return WindowSize.XLARGE;
        }
    }
    
    private void initializeUI() {
        // 화면 설정
        pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(Color.BLACK);
        CompoundBorder border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 10),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        pane.setBorder(border);
        this.getContentPane().add(pane, BorderLayout.CENTER);
        
        // 텍스트 스타일 설정
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 16);
        StyleConstants.setFontFamily(styleSet, "Source Code Pro");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
        
        // 키 리스너 설정
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
        
        // 화면 그리기
        showHomeScreen();
    }
    
    public void showHomeScreen() {
        drawHomeScreen();
        requestFocus();
    }
    
    private void drawHomeScreen() {
        StringBuilder sb = new StringBuilder();
        
        // 창 크기에 따른 제목 디자인
        drawTitle(sb);
        
        // 창 크기에 따른 메뉴 디자인
        drawMenu(sb);
        
        // 조작법 및 정보
        drawControls(sb);
        drawGameInfo(sb);
        
        updateDisplay(sb.toString());
    }
    
    /**
     * 창 크기에 따른 제목을 그립니다
     */
    private void drawTitle(StringBuilder sb) {
        switch (currentWindowSize) {
            case SMALL:
                drawSmallTitle(sb);
                break;
            case MEDIUM:
                drawMediumTitle(sb);
                break;
            case LARGE:
                drawLargeTitle(sb);
                break;
            case XLARGE:
                drawXLargeTitle(sb);
                break;
        }
    }
    
    private void drawSmallTitle(StringBuilder sb) {
        sb.append("\n");
        sb.append("┌─────────────────────┐\n");
        sb.append("│  ████ ████ ████ █  │\n");
        sb.append("│   ██  ██    ██   █  │\n");
        sb.append("│   ██  ███   ██   █  │\n");
        sb.append("│   ██  ██    ██   █  │\n");
        sb.append("│   ██  ████  ██   █  │\n");
        sb.append("│                     │\n");
        sb.append("│   🎮 5조 테트리스   │\n");
        sb.append("└─────────────────────┘\n\n");
    }
    
    private void drawMediumTitle(StringBuilder sb) {
        sb.append("\n");
        sb.append("╔═════════════════════════════════╗\n");
        sb.append("║  ██████ ███████ ████████ ███████║\n");
        sb.append("║     ██  ██         ██    ██     ║\n");
        sb.append("║     ██  █████      ██    ███████║\n");
        sb.append("║     ██  ██         ██         ██║\n");
        sb.append("║     ██  ███████    ██    ███████║\n");
        sb.append("║                                 ║\n");
        sb.append("║         🎮 5조 테트리스         ║\n");
        sb.append("╚═════════════════════════════════╝\n\n");
    }
    
    private void drawLargeTitle(StringBuilder sb) {
        sb.append("\n");
        sb.append("╔═══════════════════════════════════════════╗\n");
        sb.append("║  ████████ ███████ ████████ ██████  ██████ ║\n");
        sb.append("║     ██    ██         ██    ██   ██    ██  ║\n");
        sb.append("║     ██    █████      ██    ██████     ██  ║\n");
        sb.append("║     ██    ██         ██    ██   ██    ██  ║\n");
        sb.append("║     ██    ███████    ██    ██   ██ ██████ ║\n");
        sb.append("║                                           ║\n");
        sb.append("║            🎮 5조 테트리스 🎮            ║\n");
        sb.append("╚═══════════════════════════════════════════╝\n\n");
    }
    
    private void drawXLargeTitle(StringBuilder sb) {
        sb.append("\n");
        sb.append("╔═══════════════════════════════════════════════════╗\n");
        sb.append("║                                                   ║\n");
        sb.append("║   ████████ ███████ ████████ ██████  ██ ███████    ║\n");
        sb.append("║      ██    ██         ██    ██   ██ ██ ██         ║\n");
        sb.append("║      ██    █████      ██    ██████  ██ ███████    ║\n");
        sb.append("║      ██    ██         ██    ██   ██ ██      ██    ║\n");
        sb.append("║      ██    ███████    ██    ██   ██ ██ ███████    ║\n");
        sb.append("║                                                   ║\n");
        sb.append("║                🎮  5조 테트리스  🎮               ║\n");
        sb.append("║                                                   ║\n");
        sb.append("╚═══════════════════════════════════════════════════╝\n\n");
    }
    
    /**
     * 창 크기에 따른 메뉴를 그립니다
     */
    private void drawMenu(StringBuilder sb) {
        String menuHeader = getMenuHeader();
        sb.append(menuHeader).append("\n");
        
        // 메뉴 옵션들
        for(int i = 0; i < menuOptions.length; i++) {
            // 창 크기에 따른 메뉴 들여쓰기 조정
            String indent = getMenuIndent();
            
            if(i == selectedMenu) {
                sb.append(indent).append("►►  ");
            } else {
                sb.append(indent).append("   ");
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
            
            // 선택된 메뉴의 설명 표시 (창 크기에 따라 조정)
            if(i == selectedMenu && currentWindowSize != WindowSize.SMALL) {
                sb.append(indent).append("   💬 ").append(menuDescriptions[i]).append("\n");
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
                return "┌─────── 메뉴 ───────┐";
            case MEDIUM:
                return "┌─────────────── 메뉴 ───────────────┐";
            case LARGE:
                return "┌─────────────────── 메뉴 ───────────────────┐";
            case XLARGE:
            default:
                return "┌─────────────────── 메뉴 ───────────────────┐";
        }
    }
    
    /**
     * 창 크기에 따른 메뉴 들여쓰기를 반환합니다
     */
    private String getMenuIndent() {
        switch (currentWindowSize) {
            case SMALL:
                return " ";
            case MEDIUM:
                return "  ";
            case LARGE:
                return "   ";
            case XLARGE:
            default:
                return "  ";
        }
    }
    
    /**
     * 창 크기에 따른 메뉴 푸터를 반환합니다
     */
    private String getMenuFooter() {
        switch (currentWindowSize) {
            case SMALL:
                return "└─────────────────────┘";
            case MEDIUM:
                return "└─────────────────────────────────────┘";
            case LARGE:
                return "└─────────────────────────────────────────────┘";
            case XLARGE:
            default:
                return "└─────────────────────────────────────────────┘";
        }
    }
    
    /**
     * 창 크기에 따른 조작법을 그립니다
     */
    private void drawControls(StringBuilder sb) {
        String separator = getSeparator();
        sb.append(separator).append("\n");
        sb.append("🎮 조작법:\n");
        sb.append("   ↑↓ : 메뉴 선택    Enter : 확인\n");
        sb.append("   ESC : 게임 종료\n");
        sb.append(separator).append("\n\n");
    }
    
    /**
     * 창 크기에 따른 게임 정보를 그립니다
     */
    private void drawGameInfo(StringBuilder sb) {
        // 작은 창에서는 간단하게 표시
        if (currentWindowSize == WindowSize.SMALL) {
            sb.append("📋 버전: 1.0.0 | 5조\n");
            sb.append("🏆 최고: ").append(getHighestScore()).append("점\n");
        } else {
            sb.append("📋 게임 정보:\n");
            sb.append("   버전: 1.0.0\n");
            sb.append("   개발팀: 5조\n");
            sb.append("   최고 기록: ").append(getHighestScore()).append("점\n");
        }
    }
    
    /**
     * 창 크기에 따른 구분선을 반환합니다
     */
    private String getSeparator() {
        switch (currentWindowSize) {
            case SMALL:
                return "═══════════════════════";
            case MEDIUM:
                return "═══════════════════════════════════";
            case LARGE:
                return "═══════════════════════════════════════════";
            case XLARGE:
            default:
                return "═══════════════════════════════════════════════════";
        }
    }
    
    private String getHighestScore() {
        try {
            se.tetris.team5.util.ScoreManager scoreManager = se.tetris.team5.util.ScoreManager.getInstance();
            var topScores = scoreManager.getTopScores(1);
            if (!topScores.isEmpty()) {
                return String.format("%,d", topScores.get(0).getScore());
            }
        } catch (Exception e) {
            // ScoreManager 초기화 중 오류 발생시 기본값 반환
        }
        return "없음";
    }
    
    private void updateDisplay(String text) {
        pane.setText(text);
        StyledDocument doc = pane.getStyledDocument();
        
        // 기본 스타일 적용
        doc.setCharacterAttributes(0, doc.getLength(), styleSet, false);
        doc.setParagraphAttributes(0, doc.getLength(), styleSet, false);
        
        // 제목 색상 변경 (TETRIS 부분)
        int tetrisStart = text.indexOf("████████ ███████");
        if (tetrisStart != -1) {
            int tetrisEnd = text.indexOf("███████    ║", tetrisStart) + 12;
            SimpleAttributeSet titleStyle = new SimpleAttributeSet(styleSet);
            StyleConstants.setForeground(titleStyle, Color.CYAN);
            StyleConstants.setBold(titleStyle, true);
            doc.setCharacterAttributes(tetrisStart, tetrisEnd - tetrisStart, titleStyle, false);
        }
        
        // 5조 테트리스 부분 색상
        int subtitleIndex = text.indexOf("🎮 5조 테트리스 🎮");
        if (subtitleIndex != -1) {
            SimpleAttributeSet subtitleStyle = new SimpleAttributeSet(styleSet);
            StyleConstants.setForeground(subtitleStyle, Color.YELLOW);
            StyleConstants.setBold(subtitleStyle, true);
            doc.setCharacterAttributes(subtitleIndex, "🎮 5조 테트리스 🎮".length(), subtitleStyle, false);
        }
        
        // 선택된 메뉴 강조
        String selectedText = "►►  " + getMenuIcon(selectedMenu) + menuOptions[selectedMenu] + "  ◄◄";
        int selectedIndex = text.indexOf(selectedText);
        if (selectedIndex != -1) {
            SimpleAttributeSet selectedStyle = new SimpleAttributeSet(styleSet);
            StyleConstants.setForeground(selectedStyle, Color.GREEN);
            StyleConstants.setBold(selectedStyle, true);
            doc.setCharacterAttributes(selectedIndex, selectedText.length(), selectedStyle, false);
        }
        
        // 설명 텍스트 색상
        String descText = "💬 " + menuDescriptions[selectedMenu];
        int descIndex = text.indexOf(descText);
        if (descIndex != -1) {
            SimpleAttributeSet descStyle = new SimpleAttributeSet(styleSet);
            StyleConstants.setForeground(descStyle, Color.LIGHT_GRAY);
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
        // 구분선 색상
        String[] separators = {"═══════════", "───────────"};
        for (String sep : separators) {
            int index = 0;
            while ((index = text.indexOf(sep, index)) != -1) {
                SimpleAttributeSet sepStyle = new SimpleAttributeSet(styleSet);
                StyleConstants.setForeground(sepStyle, Color.GRAY);
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
                StyleConstants.setForeground(sectionStyle, Color.ORANGE);
                doc.setCharacterAttributes(index, section.length(), sectionStyle, false);
            }
        }
    }
    
    private void selectCurrentMenu() {
        switch (selectedMenu) {
            case 0: // 게임 시작
                showGameScreen();
                break;
            case 1: // 스코어 보기
                showScoreScreen();
                break;
            case 2: // 설정
                showSettingScreen();
                break;
            case 3: // 종료
                showExitConfirmation();
                break;
        }
    }
    
    private void showExitConfirmation() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("                  게임 종료\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        sb.append("            정말로 게임을 종료하시겠습니까?\n\n");
        sb.append("              Y: 종료    N: 취소\n\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("                  감사합니다!\n");
        sb.append("═══════════════════════════════════════════════════\n");
        
        updateDisplay(sb.toString());
        
        // 임시로 바로 종료 (Y/N 입력 구현 가능)
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void showGameScreen() {
        setVisible(false);
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
        se.tetris.team5.screen.game gamePanel = new se.tetris.team5.screen.game(this);
        gameFrame.add(gamePanel);
        
        gameFrame.setVisible(true);
        gamePanel.requestFocus(); // 키 입력을 위한 포커스 설정
    }
    
    private void showScoreScreen() {
        setVisible(false);
        score scoreWindow = new score();
        scoreWindow.setVisible(true);
    }
    
    private void showSettingScreen() {
        setVisible(false);
        setting settingWindow = new setting();
        settingWindow.setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedMenu = (selectedMenu - 1 + menuOptions.length) % menuOptions.length;
                drawHomeScreen();
                break;
            case KeyEvent.VK_DOWN:
                selectedMenu = (selectedMenu + 1) % menuOptions.length;
                drawHomeScreen();
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
}
