package se.tetris.team5.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreManager {
    private static final String SCORE_FILE = "tetris_scores.dat";
    private static ScoreManager instance;
    private List<ScoreEntry> scores;
    private static final int MAX_SCORES = 100; // 최대 저장할 스코어 수
    
    public static class ScoreEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        private String playerName;
        private int score;
        private int level;
        private int lines;
        private long playTime; // 플레이 시간 (밀리초)
        private Date date;
        
        public ScoreEntry(String playerName, int score, int level, int lines, long playTime) {
            this.playerName = playerName;
            this.score = score;
            this.level = level;
            this.lines = lines;
            this.playTime = playTime;
            this.date = new Date();
        }
        
        // Getters
        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        public int getLevel() { return level; }
        public int getLines() { return lines; }
        public long getPlayTime() { return playTime; }
        public Date getDate() { return date; }
        
        public String getFormattedPlayTime() {
            long seconds = playTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
        
        public String getFormattedDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(date);
        }
        
        @Override
        public String toString() {
            return String.format("%s - %,d점 (Lv.%d)", playerName, score, level);
        }
    }
    
    private ScoreManager() {
        scores = new ArrayList<>();
        loadScores();
    }
    
    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }
    
    public void addScore(String playerName, int score, int level, int lines, long playTime) {
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Unknown Player";
        }
        
        ScoreEntry newEntry = new ScoreEntry(playerName.trim(), score, level, lines, playTime);
        scores.add(newEntry);
        
        // 점수 순으로 내림차순 정렬
        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        // 최대 저장 수 제한
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }
        
        saveScores();
    }
    
    public List<ScoreEntry> getTopScores(int count) {
        if (scores.size() <= count) {
            return new ArrayList<>(scores);
        }
        return new ArrayList<>(scores.subList(0, count));
    }
    
    public List<ScoreEntry> getScoresPage(int page, int pageSize) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, scores.size());
        
        if (startIndex >= scores.size()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(scores.subList(startIndex, endIndex));
    }
    
    public int getTotalScores() {
        return scores.size();
    }
    
    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) scores.size() / pageSize);
    }
    
    public boolean isHighScore(int score) {
        if (scores.size() < 10) {
            return true; // Top 10 안에 들 수 있음
        }
        return score > scores.get(9).getScore(); // 10위보다 높은 점수
    }
    
    public int getRank(int score) {
        for (int i = 0; i < scores.size(); i++) {
            if (score > scores.get(i).getScore()) {
                return i + 1;
            }
        }
        return scores.size() + 1;
    }
    
    public void clearAllScores() {
        scores.clear();
        saveScores();
    }
    
    private void loadScores() {
        try {
            File file = new File(SCORE_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    @SuppressWarnings("unchecked")
                    List<ScoreEntry> loadedScores = (List<ScoreEntry>) ois.readObject();
                    scores = loadedScores;
                    
                    // 로드 후 정렬 (혹시 파일이 손상된 경우 대비)
                    scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                }
            } else {
                // 초기 더미 데이터 생성 (테스트용)
                createInitialScores();
            }
        } catch (Exception e) {
            System.err.println("스코어 로드 실패: " + e.getMessage());
            scores = new ArrayList<>();
            createInitialScores();
        }
    }
    
    private void saveScores() {
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE))) {
                oos.writeObject(scores);
            }
        } catch (IOException e) {
            System.err.println("스코어 저장 실패: " + e.getMessage());
        }
    }
    
    private void createInitialScores() {
        // 초기 더미 데이터 (테스트용)
        String[] names = {"테트리스마스터", "블록킹", "라인클리어", "스피드러너", "퍼펙트플레이어", 
                         "게임러", "챌린저", "프로게이머", "아케이드킹", "레트로게이머",
                         "블록버스터", "퍼즐마니아", "스코어헌터", "랭킹1위", "전설의플레이어"};
        
        Random random = new Random();
        
        for (int i = 0; i < 15; i++) {
            int score = 50000 - (i * 2000) + random.nextInt(1000);
            int level = Math.max(1, 15 - i + random.nextInt(3));
            int lines = score / 100 + random.nextInt(50);
            long playTime = (300 + random.nextInt(600)) * 1000; // 5-15분
            
            addScore(names[i], score, level, lines, playTime);
        }
    }
}