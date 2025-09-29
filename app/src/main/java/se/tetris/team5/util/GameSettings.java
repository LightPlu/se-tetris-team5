package se.tetris.team5.util;

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
        properties.setProperty("window.size", WINDOW_SIZE_MEDIUM);
        properties.setProperty("game.speed", "5");
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
        return properties.getProperty("window.size", WINDOW_SIZE_MEDIUM);
    }
    
    public void setWindowSize(String size) {
        properties.setProperty("window.size", size);
        saveSettings();
    }
    
    public int getGameSpeed() {
        return Integer.parseInt(properties.getProperty("game.speed", "5"));
    }
    
    public void setGameSpeed(int speed) {
        properties.setProperty("game.speed", String.valueOf(speed));
        saveSettings();
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
        properties.setProperty("key." + action, String.valueOf(keyCode));
        saveSettings();
    }
    
    public String getKeyName(int keyCode) {
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
            default: return "Key" + keyCode;
        }
    }
    
    public void resetScores() {
        // ScoreManager를 통해 스코어 초기화
        ScoreManager.getInstance().clearAllScores();
    }
}