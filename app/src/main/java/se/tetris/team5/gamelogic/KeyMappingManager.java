package se.tetris.team5.gamelogic;

import se.tetris.team5.utils.setting.GameSettings;
import java.util.HashMap;
import java.util.Map;

public class KeyMappingManager {
  private static final String[] ACTIONS = { "down", "left", "right", "rotate", "drop", "pause" };
  private static KeyMappingManager instance;
  private Map<String, Integer> keyMap;

  private KeyMappingManager() {
    keyMap = new HashMap<>();
    updateFromSettings();
  }

  public static KeyMappingManager getInstance() {
    if (instance == null) {
      instance = new KeyMappingManager();
    }
    return instance;
  }

  public void updateFromSettings() {
    GameSettings settings = GameSettings.getInstance();
    for (String action : ACTIONS) {
      keyMap.put(action, settings.getKeyCode(action));
    }
  }

  public int getKeyCode(String action) {
    return keyMap.getOrDefault(action, -1);
  }

  public Map<String, Integer> getAllKeyMappings() {
    return new HashMap<>(keyMap);
  }
}
