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
import se.tetris.team5.component.home.Title;

public class home extends JFrame implements KeyListener {
    
    private static final long serialVersionUID = 1L;
    
    private JTextPane pane;
    private SimpleAttributeSet styleSet;
    private int selectedMenu = 0; // 0: 게임시작, 1: 스코어보기, 2: 설정, 3: 종료
    
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
        currentWindowSize = Title.determineWindowSize(windowWidth, windowHeight);
        
        // Title 컴포넌트 초기화
        titleComponent = new Title(currentWindowSize);
        
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeUI();
        setVisible(true);
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
        
        // Title 컴포넌트를 사용하여 제목 그리기
        sb.append(titleComponent.drawTitle());
        
        // 창 크기에 따른 메뉴 디자인
        drawMenu(sb);
        
        // 조작법 및 정보
        drawControls(sb);
        drawGameInfo(sb);
        
        updateDisplay(sb.toString());
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
            if(i == selectedMenu && currentWindowSize != Title.WindowSize.SMALL) {
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
        if (currentWindowSize == Title.WindowSize.SMALL) {
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
        
        // TETRIS 제목 부분 색상 적용 (창 크기별로 다른 패턴 검색)
        applyTitleColors(doc, text);
        
        // 5조 테트리스 부분 색상 (모든 창 크기 패턴)
        applySubtitleColors(doc, text);
        
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
    
    /**
     * 창 크기에 따른 TETRIS 제목 색상을 적용합니다
     */
    private void applyTitleColors(StyledDocument doc, String text) {
        String[] tetrisPatterns = {
            "🎯 T E T R I S 🎯",     // 모든 크기에서 공통으로 사용할 패턴
            "T E T R I S"             // 텍스트만 있는 패턴
        };
        
        for (String pattern : tetrisPatterns) {
            int tetrisStart = text.indexOf(pattern);
            if (tetrisStart != -1) {
                // 패턴에 따라 끝점을 찾습니다
                int tetrisEnd = findTetrisEnd(text, tetrisStart, pattern);
                if (tetrisEnd > tetrisStart) {
                    SimpleAttributeSet titleStyle = new SimpleAttributeSet(styleSet);
                    StyleConstants.setForeground(titleStyle, Color.RED);
                    StyleConstants.setBold(titleStyle, true);
                    doc.setCharacterAttributes(tetrisStart, tetrisEnd - tetrisStart, titleStyle, false);
                }
                break; // 첫 번째 매치에서 중단
            }
        }
    }
    
    /**
     * TETRIS 패턴의 끝점을 찾습니다
     */
    private int findTetrisEnd(String text, int start, String pattern) {
        // 새로운 간단한 패턴에서는 해당 패턴 길이만큼만 색칠
        return start + pattern.length();
    }
    
    /**
     * "5조 테트리스" 부분의 색상을 적용합니다
     */
    private void applySubtitleColors(StyledDocument doc, String text) {
        String[] subtitlePatterns = {
            "🎮 5조 테트리스",
            "🎮 5조 테트리스 🎮",
            "🎮  5조 테트리스  🎮"
        };
        
        for (String pattern : subtitlePatterns) {
            int subtitleIndex = text.indexOf(pattern);
            if (subtitleIndex != -1) {
                SimpleAttributeSet subtitleStyle = new SimpleAttributeSet(styleSet);
                StyleConstants.setForeground(subtitleStyle, Color.RED);
                StyleConstants.setBold(subtitleStyle, true);
                doc.setCharacterAttributes(subtitleIndex, pattern.length(), subtitleStyle, false);
                break; // 첫 번째 매치에서 중단
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
