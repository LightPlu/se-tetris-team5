package se.tetris.team5.screens;
import javax.swing.*;
import se.tetris.team5.components.score.ScoreBoardPanel;
import se.tetris.team5.utils.score.ScoreManager;
import se.tetris.team5.ScreenController;
import se.tetris.team5.utils.setting.GameSettings;

public class score {
    private ScreenController screenController;
    private JPanel currentPanel;

    public score(ScreenController screenController) {
        this.screenController = screenController;
    }
    
    public void display(JTextPane textPane) {
        // 기존 텍스트 UI 대신 새 ScoreBoardPanel 사용
        ScoreManager sm = ScoreManager.getInstance();
        java.util.List<ScoreManager.ScoreEntry> top = sm.getTopScores(20);
    ScoreManager.ScoreEntry lastSaved = null; // getLastSavedEntry()가 없으면 null 처리
        ScoreBoardPanel panel = new ScoreBoardPanel(top, lastSaved);
        this.currentPanel = panel;
        // 홈 버튼 클릭 시 홈 화면으로 이동
        panel.getHomeButton().addActionListener(e -> screenController.showScreen("home"));
        // 프레임에 패널 추가
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(textPane);
        if (frame != null) {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(panel);
            frame.revalidate();
            frame.repaint();
        }
    }
}