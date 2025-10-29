package se.tetris.team5.utils.setting;

import java.io.*;
import java.util.Properties;

public class GameSettings {
    private static final String SETTINGS_FILE = "tetris_settings.properties";
    private static GameSettings instance;
    private Properties properties;
    
    // 기본 설정값
    public static final String WINDOW_SIZE_SMALL = "350x500";
    public static final String WINDOW_SIZE_MEDIUM = "450x600";
    public static final String WINDOW_SIZE_LARGE = "550x700";
    public static final String WINDOW_SIZE_XLARGE = "650x800";
    
    private GameSettings() {
        properties = new Properties();
        loadSettings();
    }
    
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    public void loadSettings() {
        try {
            File file = new File(SETTINGS_FILE);
            if (file.exists()) {
                properties.load(new FileInputStream(file));
            } else {
                setDefaultSettings();
            }
        } catch (IOException e) {
            setDefaultSettings();
        }
    }
    
    public void saveSettings() {
        try {
            properties.store(new FileOutputStream(SETTINGS_FILE), "Tetris Game Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setDefaultSettings() {
        properties.setProperty("window.size", WINDOW_SIZE_LARGE);
        properties.setProperty("game.speed", "3");
        properties.setProperty("sound.enabled", "true");
        properties.setProperty("colorblind.mode", "false");
        properties.setProperty("key.down", "40"); // VK_DOWN
        properties.setProperty("key.left", "37"); // VK_LEFT
        properties.setProperty("key.right", "39"); // VK_RIGHT
        properties.setProperty("key.rotate", "38"); // VK_UP
        properties.setProperty("key.drop", "32"); // VK_SPACE
        properties.setProperty("key.pause", "80"); // VK_P
        saveSettings();
    }
    
    public String getWindowSize() {
        return properties.getProperty("window.size", WINDOW_SIZE_LARGE);
    }
    
    public void setWindowSize(String size) {
        properties.setProperty("window.size", size);
        saveSettings();
    }
    
    public int getWindowWidth() {
        String size = getWindowSize();
        return Integer.parseInt(size.split("x")[0]);
    }
    
    public int getWindowHeight() {
        String size = getWindowSize();
        return Integer.parseInt(size.split("x")[1]);
    }
    
    public int getGameSpeed() {
        return Integer.parseInt(properties.getProperty("game.speed", "3"));
    }
    
    public void setGameSpeed(int speed) {
        properties.setProperty("game.speed", String.valueOf(speed));
        saveSettings();
    }
    
    public String getGameSpeedName(int speed) {
        switch (speed) {
            case 1: return "매우느림";
            case 2: return "느림";
            case 3: return "보통";
            case 4: return "빠름";
            case 5: return "매우빠름";
            default: return "보통";
        }
    }
    
    public boolean isSoundEnabled() {
        return Boolean.parseBoolean(properties.getProperty("sound.enabled", "true"));
    }
    
    public void setSoundEnabled(boolean enabled) {
        properties.setProperty("sound.enabled", String.valueOf(enabled));
        saveSettings();
    }
    
    public boolean isColorblindMode() {
        return Boolean.parseBoolean(properties.getProperty("colorblind.mode", "false"));
    }
    
    public void setColorblindMode(boolean enabled) {
        properties.setProperty("colorblind.mode", String.valueOf(enabled));
        saveSettings();
    }
    
    public int getKeyCode(String action) {
        if (action == null) {
            return -1;
        }
        switch (action) {
            case "down": return Integer.parseInt(properties.getProperty("key.down", "40"));
            case "left": return Integer.parseInt(properties.getProperty("key.left", "37"));
            case "right": return Integer.parseInt(properties.getProperty("key.right", "39"));
            case "rotate": return Integer.parseInt(properties.getProperty("key.rotate", "38"));
            case "drop": return Integer.parseInt(properties.getProperty("key.drop", "32"));
            case "pause": return Integer.parseInt(properties.getProperty("key.pause", "80"));
            default: return -1;
        }
    }
    
    public void setKeyCode(String action, int keyCode) {
        // 먼저 다른 액션에서 같은 키를 사용하고 있는지 확인하고 제거
        removeKeyIfExists(keyCode, action);
        
        properties.setProperty("key." + action, String.valueOf(keyCode));
        saveSettings();
    }
    
    // 다른 액션에서 같은 키를 사용하고 있다면 제거하는 메소드
    private void removeKeyIfExists(int keyCode, String currentAction) {
        String[] actions = {"down", "left", "right", "rotate", "drop", "pause"};
        for (String action : actions) {
            if (!action.equals(currentAction)) {
                int existingKeyCode = getKeyCode(action);
                if (existingKeyCode == keyCode) {
                    // 기존 키를 -1로 설정 (비활성화)
                    properties.setProperty("key." + action, "-1");
                }
            }
        }
    }
    
    public String getKeyName(int keyCode) {
        if (keyCode == -1) {
            return "없음";
        }
        
        switch (keyCode) {
            case 37: return "←";
            case 38: return "↑";
            case 39: return "→";
            case 40: return "↓";
            case 32: return "Space";
            case 80: return "P";
            case 87: return "W";
            case 65: return "A";
            case 83: return "S";
            case 68: return "D";
            case 81: return "Q";
            case 69: return "E";
            case 82: return "R";
            case 84: return "T";
            case 89: return "Y";
            case 85: return "U";
            case 73: return "I";
            case 79: return "O";
            case 70: return "F";
            case 71: return "G";
            case 72: return "H";
            case 74: return "J";
            case 75: return "K";
            case 76: return "L";
            case 90: return "Z";
            case 88: return "X";
            case 67: return "C";
            case 86: return "V";
            case 66: return "B";
            case 78: return "N";
            case 77: return "M";
            case 16: return "Shift";
            case 17: return "Ctrl";
            case 18: return "Alt";
            case 10: return "Enter";
            case 27: return "Esc";
            case 9: return "Tab";
            default: return "Key" + keyCode;
        }
    }
    
    public void resetScores() {
        // ScoreManager를 통해 스코어 초기화
    se.tetris.team5.utils.score.ScoreManager.getInstance().clearAllScores();
    }
    
    // 색맹 모드용 색상 팔레트
    public java.awt.Color getColorForBlock(String blockType) {
        if (isColorblindMode()) {
            // 색맹 친화적인 색상 사용
            switch (blockType) {
                case "I": return new java.awt.Color(0, 158, 115);    // 청록색 (Teal)
                case "O": return new java.awt.Color(240, 228, 66);   // 밝은 노란색 (Yellow)
                case "T": return new java.awt.Color(204, 121, 167);  // 연보라색 (Light Purple)
                case "L": return new java.awt.Color(230, 159, 0);    // 주황색 (Orange)
                case "J": return new java.awt.Color(86, 180, 233);   // 하늘색 (Sky Blue)
                case "S": return new java.awt.Color(0, 114, 178);    // 진한 파란색 (Blue)
                case "Z": return new java.awt.Color(213, 94, 0);     // 진한 주황색 (Vermillion)
                case "W": return new java.awt.Color(128, 128, 128);  // 회색 (Gray) - 무게추 블록
                default: return java.awt.Color.WHITE;
            }
        } else {
            // 기본 색상 사용
            switch (blockType) {
                case "I": return java.awt.Color.CYAN;
                case "O": return java.awt.Color.YELLOW;
                case "T": return java.awt.Color.MAGENTA;
                case "L": return java.awt.Color.ORANGE;
                case "J": return java.awt.Color.BLUE;
                case "S": return java.awt.Color.GREEN;
                case "Z": return java.awt.Color.RED;
                case "W": return new java.awt.Color(64, 64, 64);     // 진한 회색 - 무게추 블록
                default: return java.awt.Color.WHITE;
            }
        }
    }
    
    // UI 색상도 색맹 모드에 따라 변경
    public java.awt.Color getUIColor(String element) {
        if (isColorblindMode()) {
            // 색맹 친화적인 UI 색상
            switch (element) {
                case "background": return java.awt.Color.BLACK;
                case "text": return new java.awt.Color(240, 240, 240);     // 밝은 회색 텍스트
                case "highlight": return new java.awt.Color(240, 228, 66); // 밝은 노란색 하이라이트 (색맹 친화적)
                case "border": return new java.awt.Color(128, 128, 128);   // 회색 테두리
                default: return java.awt.Color.WHITE;
            }
        } else {
            // 기본 UI 색상
            switch (element) {
                case "background": return java.awt.Color.BLACK;
                case "text": return java.awt.Color.WHITE;
                case "highlight": return java.awt.Color.GREEN; // 일반 모드에서는 초록색
                case "border": return java.awt.Color.GRAY;
                default: return java.awt.Color.WHITE;
            }
        }
    }
}