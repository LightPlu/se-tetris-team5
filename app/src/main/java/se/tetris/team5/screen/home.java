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
        
        // GameSettings에서 창 크기 가져오기
        GameSettings settings = GameSettings.getInstance();
        String windowSize = settings.getWindowSize();
        String[] sizeParts = windowSize.split("x");
        int width = Integer.parseInt(sizeParts[0]);
        int height = Integer.parseInt(sizeParts[1]);
        setSize(width, height);
        
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
        
        // ASCII 아트 제목
        sb.append("\n");
        sb.append("████████╗███████╗████████╗██████╗ ██╗███████╗\n");
        sb.append("╚══██╔══╝██╔════╝╚══██╔══╝██╔══██╗██║██╔════╝\n");
        sb.append("   ██║   █████╗     ██║   ██████╔╝██║███████╗\n");
        sb.append("   ██║   ██╔══╝     ██║   ██╔══██╗██║╚════██║\n");
        sb.append("   ██║   ███████╗   ██║   ██║  ██║██║███████║\n");
        sb.append("   ╚═╝   ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚══════╝\n\n");
        
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("              🎮 5조 테트리스 🎮\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        // 메뉴 옵션들
        for(int i = 0; i < menuOptions.length; i++) {
            if(i == selectedMenu) {
                sb.append("  ►►  ");
            } else {
                sb.append("     ");
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
            if(i == selectedMenu) {
                sb.append("     💬 ").append(menuDescriptions[i]).append("\n");
            }
            sb.append("\n");
        }
        
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("🎮 조작법:\n");
        sb.append("   ↑↓ : 메뉴 선택    Enter : 확인\n");
        sb.append("   ESC : 게임 종료\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        // 게임 정보
        sb.append("📋 게임 정보:\n");
        sb.append("   버전: 1.0.0\n");
        sb.append("   개발팀: 5조\n");
        sb.append("   최고 기록: ").append(getHighestScore()).append("점\n");
        
        updateDisplay(sb.toString());
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
        int tetrisStart = text.indexOf("████████╗███████╗");
        if (tetrisStart != -1) {
            int tetrisEnd = text.indexOf("╚══════╝", tetrisStart) + 8;
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
        // 게임 화면 구현 필요
        se.tetris.team5.component.Board gameBoard = new se.tetris.team5.component.Board();
        gameBoard.setVisible(true);
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
