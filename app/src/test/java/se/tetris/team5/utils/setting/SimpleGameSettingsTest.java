package se.tetris.team5.utils.setting;

import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;

public class SimpleGameSettingsTest {
    
    @Test
    public void testBasicSettings() {
        GameSettings settings = GameSettings.getInstance();
        assertNotNull("GameSettings instance should not be null", settings);
    }
    
    @Test
    public void testGameSpeed() {
        GameSettings settings = GameSettings.getInstance();
        settings.setGameSpeed(4);
        assertEquals("Game speed should be 4", 4, settings.getGameSpeed());
    }
    
    @Test
    public void testColorblindMode() {
        GameSettings settings = GameSettings.getInstance();
        settings.setColorblindMode(true);
        assertTrue("Colorblind mode should be true", settings.isColorblindMode());
        
        settings.setColorblindMode(false);
        assertFalse("Colorblind mode should be false", settings.isColorblindMode());
    }
    
    @Test
    public void testSoundSettings() {
        GameSettings settings = GameSettings.getInstance();
        settings.setSoundEnabled(false);
        assertFalse("Sound should be disabled", settings.isSoundEnabled());
        
        settings.setSoundEnabled(true);
        assertTrue("Sound should be enabled", settings.isSoundEnabled());
    }
    
    @Test
    public void testWindowSize() {
        GameSettings settings = GameSettings.getInstance();
        settings.setWindowSize(GameSettings.WINDOW_SIZE_SMALL);
        assertEquals("Window size should be SMALL", GameSettings.WINDOW_SIZE_SMALL, settings.getWindowSize());
        assertEquals("Small window width should be 350", 350, settings.getWindowWidth());
        assertEquals("Small window height should be 500", 500, settings.getWindowHeight());
    }
    
    @Test
    public void testKeyBindings() {
        GameSettings settings = GameSettings.getInstance();
        settings.setKeyCode("down", 83); // S키
        assertEquals("Down key should be 83", 83, settings.getKeyCode("down"));
    }
    
    @Test
    public void testGameSpeedNames() {
        GameSettings settings = GameSettings.getInstance();
        assertEquals("Speed 1 should be 매우느림", "매우느림", settings.getGameSpeedName(1));
        assertEquals("Speed 5 should be 매우빠름", "매우빠름", settings.getGameSpeedName(5));
    }
    
    @Test
    public void testKeyNames() {
        GameSettings settings = GameSettings.getInstance();
        assertEquals("Space key name should be Space", "Space", settings.getKeyName(32));
        assertEquals("Arrow up should be ↑", "↑", settings.getKeyName(38));
    }
    
    @Test
    public void testBlockColors() {
        GameSettings settings = GameSettings.getInstance();
        
        // 일반 모드
        settings.setColorblindMode(false);
        Color iColor = settings.getColorForBlock("I");
        assertEquals("I block should be cyan in normal mode", Color.CYAN, iColor);
        
        // 색맹 모드
        settings.setColorblindMode(true);
        Color iColorBlind = settings.getColorForBlock("I");
        assertEquals("I block should be teal in colorblind mode", new Color(0, 158, 115), iColorBlind);
    }
    
    @Test
    public void testUIColors() {
        GameSettings settings = GameSettings.getInstance();
        
        // 일반 모드
        settings.setColorblindMode(false);
        Color normalHighlight = settings.getUIColor("highlight");
        assertEquals("Highlight should be green in normal mode", Color.GREEN, normalHighlight);
        
        // 색맹 모드
        settings.setColorblindMode(true);
        Color colorblindHighlight = settings.getUIColor("highlight");
        assertEquals("Highlight should be yellow in colorblind mode", new Color(240, 228, 66), colorblindHighlight);
    }
}