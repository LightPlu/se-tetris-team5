package se.tetris.team5.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.border.CompoundBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import se.tetris.team5.util.ScoreManager;
import se.tetris.team5.util.ScoreManager.ScoreEntry;

public class score extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private static final int SCORES_PER_PAGE = 10;
    
    private JTextPane pane;
    private SimpleAttributeSet styleSet;
    private KeyListener keyListener;
    private ScoreManager scoreManager;
    
    private int currentPage = 0;
    private int totalPages;
    private boolean showDetailView = false;
    private int selectedRank = 0; // 상세 보기에서 선택된 순위
    
    public score() {
        super("5조 테트리스 - 스코어 보드");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        scoreManager = ScoreManager.getInstance();
        totalPages = scoreManager.getTotalPages(SCORES_PER_PAGE);
        
        // 화면 설정
        pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(Color.BLACK);
        CompoundBorder border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 10),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        pane.setBorder(border);
        this.getContentPane().add(pane, BorderLayout.CENTER);
        
        // 창 크기 설정
        setSize(600, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // 텍스트 스타일 설정
        styleSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(styleSet, 14);
        StyleConstants.setFontFamily(styleSet, "Source Code Pro");
        StyleConstants.setBold(styleSet, true);
        StyleConstants.setForeground(styleSet, Color.WHITE);
        StyleConstants.setAlignment(styleSet, StyleConstants.ALIGN_CENTER);
        
        // 키 리스너 설정
        keyListener = new ScoreKeyListener();
        addKeyListener(keyListener);
        setFocusable(true);
        requestFocus();
        
        // 화면 그리기
        drawScoreScreen();
    }
    
    private void drawScoreScreen() {
        if (showDetailView) {
            drawDetailView();
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        
        // 제목
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("                  스코어 보드\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        // 현재 페이지의 스코어 목록
        List<ScoreEntry> pageScores = scoreManager.getScoresPage(currentPage, SCORES_PER_PAGE);
        
        if (pageScores.isEmpty()) {
            sb.append("              등록된 기록이 없습니다.\n\n");
        } else {
            // 테이블 헤더
            sb.append("순위  이름            점수      레벨  라인  시간    날짜\n");
            sb.append("───────────────────────────────────────────────────\n");
            
            // 스코어 목록
            for (int i = 0; i < pageScores.size(); i++) {
                ScoreEntry entry = pageScores.get(i);
                int rank = currentPage * SCORES_PER_PAGE + i + 1;
                
                String name = entry.getPlayerName();
                if (name.length() > 12) {
                    name = name.substring(0, 12);
                }
                
                sb.append(String.format("%2d.  %-12s %8s  %2d   %3d  %5s  %s\n",
                    rank,
                    name,
                    formatScore(entry.getScore()),
                    entry.getLevel(),
                    entry.getLines(),
                    entry.getFormattedPlayTime(),
                    entry.getFormattedDate().substring(5) // MM-dd HH:mm만 표시
                ));
            }
        }
        
        sb.append("\n");
        
        // 페이지 정보
        if (totalPages > 1) {
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append(String.format("              페이지 %d / %d\n", currentPage + 1, totalPages));
            sb.append("←→: 페이지 이동  ↑↓: 상세보기 선택  Enter: 상세보기\n");
        } else {
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append("↑↓: 상세보기 선택  Enter: 상세보기\n");
        }
        sb.append("R: 새로고침  ESC: 메인 메뉴로\n");
        sb.append("═══════════════════════════════════════════════════\n");
        
        // 통계 정보
        sb.append(String.format("총 %d개의 기록", scoreManager.getTotalScores()));
        
        updateDisplay(sb.toString());
    }
    
    private void drawDetailView() {
        StringBuilder sb = new StringBuilder();
        
        List<ScoreEntry> pageScores = scoreManager.getScoresPage(currentPage, SCORES_PER_PAGE);
        if (selectedRank >= pageScores.size()) {
            selectedRank = 0;
        }
        
        if (pageScores.isEmpty()) {
            showDetailView = false;
            drawScoreScreen();
            return;
        }
        
        ScoreEntry entry = pageScores.get(selectedRank);
        int actualRank = currentPage * SCORES_PER_PAGE + selectedRank + 1;
        
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("                 상세 기록\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        // 상세 정보
        sb.append(String.format("    순위: %d위\n\n", actualRank));
        sb.append(String.format("    플레이어: %s\n\n", entry.getPlayerName()));
        sb.append(String.format("    점수: %s점\n\n", formatScore(entry.getScore())));
        sb.append(String.format("    레벨: %d\n\n", entry.getLevel()));
        sb.append(String.format("    클리어한 라인: %d줄\n\n", entry.getLines()));
        sb.append(String.format("    플레이 시간: %s\n\n", entry.getFormattedPlayTime()));
        sb.append(String.format("    달성 날짜: %s\n\n", entry.getFormattedDate()));
        
        // 계산된 통계
        double lpm = entry.getLines() / (entry.getPlayTime() / 60000.0); // Lines Per Minute
        double spl = entry.getLines() > 0 ? (double) entry.getScore() / entry.getLines() : 0; // Score Per Line
        
        sb.append("    ───── 통계 ─────\n");
        sb.append(String.format("    분당 라인: %.1f LPM\n", lpm));
        sb.append(String.format("    라인당 점수: %.0f점\n", spl));
        
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("↑↓: 다른 기록 보기  ESC: 목록으로\n");
        sb.append("═══════════════════════════════════════════════════\n");
        
        updateDisplay(sb.toString());
    }
    
    private String formatScore(int score) {
        if (score >= 1000000) {
            return String.format("%.1fM", score / 1000000.0);
        } else if (score >= 1000) {
            return String.format("%,d", score);
        } else {
            return String.valueOf(score);
        }
    }
    
    private void updateDisplay(String text) {
        pane.setText(text);
        StyledDocument doc = pane.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(), styleSet, false);
        doc.setParagraphAttributes(0, doc.getLength(), styleSet, false);
        
        // 상세보기에서 선택된 항목 강조
        if (showDetailView) {
            // 제목 색상 변경
            String titleText = "상세 기록";
            int titleIndex = text.indexOf(titleText);
            if (titleIndex != -1) {
                SimpleAttributeSet titleStyle = new SimpleAttributeSet(styleSet);
                StyleConstants.setForeground(titleStyle, Color.CYAN);
                doc.setCharacterAttributes(titleIndex, titleText.length(), titleStyle, false);
            }
        } else {
            // 1위 강조
            if (currentPage == 0 && text.contains(" 1.  ")) {
                int firstPlaceIndex = text.indexOf(" 1.  ");
                if (firstPlaceIndex != -1) {
                    int lineEnd = text.indexOf("\n", firstPlaceIndex);
                    if (lineEnd != -1) {
                        SimpleAttributeSet goldStyle = new SimpleAttributeSet(styleSet);
                        StyleConstants.setForeground(goldStyle, Color.YELLOW);
                        StyleConstants.setBold(goldStyle, true);
                        doc.setCharacterAttributes(firstPlaceIndex, lineEnd - firstPlaceIndex, goldStyle, false);
                    }
                }
            }
            
            // 2-3위 강조
            if (currentPage == 0) {
                for (int rank = 2; rank <= 3; rank++) {
                    String rankText = " " + rank + ".  ";
                    int rankIndex = text.indexOf(rankText);
                    if (rankIndex != -1) {
                        int lineEnd = text.indexOf("\n", rankIndex);
                        if (lineEnd != -1) {
                            SimpleAttributeSet medalStyle = new SimpleAttributeSet(styleSet);
                            StyleConstants.setForeground(medalStyle, rank == 2 ? Color.LIGHT_GRAY : new Color(205, 127, 50));
                            doc.setCharacterAttributes(rankIndex, lineEnd - rankIndex, medalStyle, false);
                        }
                    }
                }
            }
        }
    }
    
    public class ScoreKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (!showDetailView && totalPages > 1) {
                        currentPage = (currentPage - 1 + totalPages) % totalPages;
                        selectedRank = 0; // 페이지 변경시 선택 초기화
                        drawScoreScreen();
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (!showDetailView && totalPages > 1) {
                        currentPage = (currentPage + 1) % totalPages;
                        selectedRank = 0; // 페이지 변경시 선택 초기화
                        drawScoreScreen();
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (showDetailView) {
                        List<ScoreEntry> pageScores = scoreManager.getScoresPage(currentPage, SCORES_PER_PAGE);
                        if (!pageScores.isEmpty()) {
                            selectedRank = (selectedRank - 1 + pageScores.size()) % pageScores.size();
                            drawDetailView();
                        }
                    } else {
                        List<ScoreEntry> pageScores = scoreManager.getScoresPage(currentPage, SCORES_PER_PAGE);
                        if (!pageScores.isEmpty()) {
                            selectedRank = (selectedRank - 1 + pageScores.size()) % pageScores.size();
                            // 목록에서는 시각적 피드백만, 실제 상세보기는 Enter로
                        }
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (showDetailView) {
                        List<ScoreEntry> pageScores = scoreManager.getScoresPage(currentPage, SCORES_PER_PAGE);
                        if (!pageScores.isEmpty()) {
                            selectedRank = (selectedRank + 1) % pageScores.size();
                            drawDetailView();
                        }
                    } else {
                        List<ScoreEntry> pageScores = scoreManager.getScoresPage(currentPage, SCORES_PER_PAGE);
                        if (!pageScores.isEmpty()) {
                            selectedRank = (selectedRank + 1) % pageScores.size();
                            // 목록에서는 시각적 피드백만, 실제 상세보기는 Enter로
                        }
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (!showDetailView) {
                        List<ScoreEntry> pageScores = scoreManager.getScoresPage(currentPage, SCORES_PER_PAGE);
                        if (!pageScores.isEmpty()) {
                            showDetailView = true;
                            drawDetailView();
                        }
                    }
                    break;
                case KeyEvent.VK_R:
                    if (!showDetailView) {
                        // 새로고침
                        scoreManager = ScoreManager.getInstance();
                        totalPages = scoreManager.getTotalPages(SCORES_PER_PAGE);
                        if (currentPage >= totalPages && totalPages > 0) {
                            currentPage = totalPages - 1;
                        }
                        drawScoreScreen();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (showDetailView) {
                        showDetailView = false;
                        drawScoreScreen();
                    } else {
                        setVisible(false);
                        new se.tetris.team5.screen.home();
                    }
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }
}