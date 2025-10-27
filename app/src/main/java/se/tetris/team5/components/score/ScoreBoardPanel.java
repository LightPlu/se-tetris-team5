// (불필요한 중괄호 삭제)
package se.tetris.team5.components.score;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import se.tetris.team5.utils.score.ScoreManager.ScoreEntry;

public class ScoreBoardPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel titleLabel;
    private JButton homeButton;
    private JButton prevButton, nextButton;
    private JLabel pageLabel;
    private List<ScoreEntry> allScores;
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private ScoreEntry highlightEntry;

    private JPanel paginationPanel;
    private JPanel centerPanel;

    public ScoreBoardPanel(List<ScoreEntry> scores, ScoreEntry highlight) {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 32, 48));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        // 더미 데이터 추가 (테스트용)
        if (scores == null || scores.isEmpty()) {
            scores = new java.util.ArrayList<>();
            for (int i = 0; i < 15; i++) {
                scores.add(new ScoreEntry("Player" + (i+1), 1000 - i*50, 1, i, (60 + i*10) * 1000L));
            }
        }
        this.allScores = scores;
        this.highlightEntry = highlight;
        this.totalPages = Math.max(1, (int)Math.ceil((double)scores.size() / pageSize));

        // 타이틀
        titleLabel = new JLabel("스코어 보드", SwingConstants.CENTER);
        titleLabel.setFont(new Font("NanumGothic", Font.BOLD, 30));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 테이블 및 스크롤
        String[] columns = {"순위", "이름", "점수", "레벨", "줄", "플레이타임", "날짜"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("NanumGothic", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.setBackground(new Color(44, 47, 63));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 215, 0, 80));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(60, 60, 80));
        table.getTableHeader().setFont(new Font("NanumGothic", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(30, 32, 48));
        table.getTableHeader().setForeground(new Color(255, 215, 0));
        table.getTableHeader().setReorderingAllowed(false);
        table.setFocusable(false);
        table.setRowSelectionAllowed(false);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        int[] colWidths = {50, 140, 110, 70, 70, 130, 220};
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(44, 47, 63));

        // 명예의 전당(1~3등 순위대) - 테이블 아래에 자연스럽게
        JPanel podiumPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth();
                int h = getHeight();
                // 1등, 2등, 3등 순서대로 좌표/색상 지정
                int[] heights = {90, 60, 40}; // 1등, 2등, 3등
                int[] xs = {w/2-30, w/2-90, w/2+30}; // 1등, 2등, 3등
                int[] ys = {h-heights[0], h-heights[1], h-heights[2]};
                Color[] colors = {new Color(255,215,0), new Color(192,192,192), new Color(205,127,50)};
                String[] rankText = {"1등", "2등", "3등"};
                for (int i = 0; i < 3; i++) {
                    g.setColor(colors[i]);
                    g.fillRoundRect(xs[i], ys[i], 60, heights[i], 18, 18);
                    g.setColor(new Color(30,32,48));
                    g.setFont(new Font("NanumGothic", Font.BOLD, 18));
                    g.drawString(rankText[i], xs[i]+15, ys[i]+25);
                }
            }
        };
        podiumPanel.setPreferredSize(new Dimension(320, 110));
        podiumPanel.setOpaque(false);
        podiumPanel.setLayout(null);
        // 1~3등 이름만 순위대 위에 띄우기 (각 순위대 색상)
        // 순위대 위에 이름을 정확히 중앙에 배치 (좌표 수동 조정)
        int panelW = 320;
        int[] heights = {90, 60, 40}; // 1등, 2등, 3등
        int[] baseXs = {panelW/2-30, panelW/2-90, panelW/2+30}; // 1등, 2등, 3등
        Color[] nameColors = {new Color(255,215,0), new Color(192,192,192), new Color(205,127,50)};
        Font nameFont = new Font("NanumGothic", Font.BOLD, 16);
        for (int i = 0; i < 3; i++) {
            String playerName = (allScores.size() > i) ? allScores.get(i).getPlayerName() : "-";
            JLabel name = new JLabel(playerName);
            name.setFont(nameFont);
            name.setForeground(nameColors[i]);
            // FontMetrics로 텍스트 폭 계산하여 중앙 정렬, 오른쪽으로 60px 이동
            FontMetrics fm = name.getFontMetrics(nameFont);
            int textWidth = fm.stringWidth(playerName);
            int podiumCenterX = baseXs[i] + 30 + 115;
            int nameX = podiumCenterX - textWidth/2;
            int nameY = 110 - heights[i] - 20;
            name.setBounds(nameX, nameY, textWidth+2, 20);
            podiumPanel.add(name);
        }

        // 페이지네이션 패널 (centerPanel 위에 올릴 수 있도록 먼저 선언)
    paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        paginationPanel.setBackground(new Color(30, 32, 48));
        prevButton = new JButton("이전");
        nextButton = new JButton("다음");
        pageLabel = new JLabel();
        Font navFont = new Font("NanumGothic", Font.BOLD, 15);
        Color navy = new Color(30, 32, 48);
        prevButton.setFont(navFont);
        nextButton.setFont(navFont);
        pageLabel.setFont(navFont);
        prevButton.setBackground(new Color(80, 80, 80));
        prevButton.setForeground(navy);
        nextButton.setBackground(new Color(80, 80, 80));
        nextButton.setForeground(navy);
    pageLabel.setForeground(new Color(255, 215, 0));
        prevButton.setFocusPainted(false);
        nextButton.setFocusPainted(false);
        paginationPanel.add(prevButton);
        paginationPanel.add(Box.createHorizontalStrut(8));
        paginationPanel.add(pageLabel);
        paginationPanel.add(Box.createHorizontalStrut(8));
        paginationPanel.add(nextButton);

        // 테이블+순위대+페이지네이션을 수직으로 쌓는 패널
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(30, 32, 48));
        centerPanel.add(paginationPanel);
        centerPanel.add(scrollPane);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(podiumPanel);
        add(centerPanel, BorderLayout.CENTER);

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTable();
            }
        });
        nextButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateTable();
            }
        });

        // 홈 버튼
        homeButton = new JButton("홈으로");
        homeButton.setFont(new Font("NanumGothic", Font.BOLD, 18));
        homeButton.setBackground(new Color(255, 215, 0));
        homeButton.setForeground(Color.WHITE); // 흰색 텍스트
        homeButton.setFocusPainted(false);
        homeButton.setBorder(BorderFactory.createEmptyBorder(10, 36, 10, 36));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setBackground(new Color(30, 32, 48));
        btnPanel.setBorder(new EmptyBorder(4, 0, 0, 0));
        btnPanel.add(homeButton);
        add(btnPanel, BorderLayout.SOUTH);

        // 키보드 네비게이션 및 ESC 홈 이동
    setFocusable(true);
    SwingUtilities.invokeLater(() -> requestFocusInWindow());
    addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    if (currentPage > 1) {
                        currentPage--;
                        updateTable();
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
                    if (currentPage < totalPages) {
                        currentPage++;
                        updateTable();
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    int result = JOptionPane.showConfirmDialog(
                        ScoreBoardPanel.this,
                        "홈으로 돌아가시겠습니까?",
                        "확인",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (result == JOptionPane.YES_OPTION) {
                        homeButton.doClick();
                    }
                }
            }
        });

        updateTable();
    }

    private void updateTable() {
        // 페이지네이션 동적 표시/숨김
        if (allScores.size() > pageSize) {
            paginationPanel.setVisible(true);
        } else {
            paginationPanel.setVisible(false);
        }
        // 최신 점수 개수로 totalPages 재계산
        totalPages = Math.max(1, (int)Math.ceil((double)allScores.size() / pageSize));
        // 현재 페이지가 범위 초과 시 마지막 페이지로 보정
        if (currentPage > totalPages) currentPage = totalPages;
        tableModel.setRowCount(0);
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, allScores.size());
        int highlightRow = -1;
        for (int i = start; i < end; i++) {
            ScoreEntry e = allScores.get(i);
            Object[] row = {
                i + 1,
                e.getPlayerName(),
                String.format("%,d", e.getScore()),
                e.getLevel(),
                e.getLines(),
                e.getFormattedPlayTime(),
                e.getFormattedDate()
            };
            tableModel.addRow(row);
            if (highlightEntry != null && e == highlightEntry) highlightRow = i - start;
        }
        if (highlightRow >= 0) {
            table.addRowSelectionInterval(highlightRow, highlightRow);
        }
        pageLabel.setText(currentPage + " / " + totalPages);
        // 키보드 포커스 유지
        requestFocusInWindow();
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }

    public JButton getHomeButton() {

        return homeButton;
    }
}

