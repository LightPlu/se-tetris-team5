package se.tetris.team5.blocks;

import org.junit.Test;
import static org.junit.Assert.*;
import java.awt.Color;

public class BlockTest {
    
    @Test
    public void iBlockTest() {
        IBlock iBlock = new IBlock();
        assertNotNull("IBlock이 생성되어야 합니다", iBlock);
        assertEquals("IBlock의 색상은 CYAN이어야 합니다", Color.CYAN, iBlock.getColor());
        assertEquals("IBlock의 너비는 4여야 합니다", 4, iBlock.width());
        assertEquals("IBlock의 높이는 1이어야 합니다", 1, iBlock.height());
        
        // IBlock의 초기 모양 검증
        for (int i = 0; i < 4; i++) {
            assertEquals("IBlock의 모든 셀은 1이어야 합니다", 1, iBlock.getShape(i, 0));
        }
    }
    
    @Test
    public void jBlockTest() {
        JBlock jBlock = new JBlock();
        assertNotNull("JBlock이 생성되어야 합니다", jBlock);
        assertEquals("JBlock의 색상은 BLUE여야 합니다", Color.BLUE, jBlock.getColor());
        assertEquals("JBlock의 너비는 3이어야 합니다", 3, jBlock.width());
        assertEquals("JBlock의 높이는 2여야 합니다", 2, jBlock.height());
        
        // JBlock 초기 모양 검증 [[1,1,1], [0,0,1]]
        assertEquals(1, jBlock.getShape(0, 0));
        assertEquals(1, jBlock.getShape(1, 0));
        assertEquals(1, jBlock.getShape(2, 0));
        assertEquals(0, jBlock.getShape(0, 1));
        assertEquals(0, jBlock.getShape(1, 1));
        assertEquals(1, jBlock.getShape(2, 1));
    }
    
    @Test
    public void lBlockTest() {
        LBlock lBlock = new LBlock();
        assertNotNull("LBlock이 생성되어야 합니다", lBlock);
        assertEquals("LBlock의 색상은 ORANGE여야 합니다", Color.ORANGE, lBlock.getColor());
        assertEquals("LBlock의 너비는 3이어야 합니다", 3, lBlock.width());
        assertEquals("LBlock의 높이는 2여야 합니다", 2, lBlock.height());
        
        // LBlock 초기 모양 검증 [[1,1,1], [1,0,0]]
        assertEquals(1, lBlock.getShape(0, 0));
        assertEquals(1, lBlock.getShape(1, 0));
        assertEquals(1, lBlock.getShape(2, 0));
        assertEquals(1, lBlock.getShape(0, 1));
        assertEquals(0, lBlock.getShape(1, 1));
        assertEquals(0, lBlock.getShape(2, 1));
    }
    
    @Test
    public void oBlockTest() {
        OBlock oBlock = new OBlock();
        assertNotNull("OBlock이 생성되어야 합니다", oBlock);
        assertEquals("OBlock의 색상은 YELLOW여야 합니다", Color.YELLOW, oBlock.getColor());
        assertEquals("OBlock의 너비는 2여야 합니다", 2, oBlock.width());
        assertEquals("OBlock의 높이는 2여야 합니다", 2, oBlock.height());
        
        // O블록의 모든 셀이 1이어야 함
        for(int i = 0; i < oBlock.width(); i++) {
            for(int j = 0; j < oBlock.height(); j++) {
                assertEquals("O블록의 모든 셀은 1이어야 합니다", 1, oBlock.getShape(i, j));
            }
        }
    }
    
    @Test
    public void sBlockTest() {
        SBlock sBlock = new SBlock();
        assertNotNull("SBlock이 생성되어야 합니다", sBlock);
        assertEquals("SBlock의 색상은 GREEN이어야 합니다", Color.GREEN, sBlock.getColor());
        assertEquals("SBlock의 너비는 3이어야 합니다", 3, sBlock.width());
        assertEquals("SBlock의 높이는 2여야 합니다", 2, sBlock.height());
    }
    
    @Test
    public void tBlockTest() {
        TBlock tBlock = new TBlock();
        assertNotNull("TBlock이 생성되어야 합니다", tBlock);
        assertEquals("TBlock의 색상은 MAGENTA여야 합니다", Color.MAGENTA, tBlock.getColor());
        assertEquals("TBlock의 너비는 3이어야 합니다", 3, tBlock.width());
        assertEquals("TBlock의 높이는 2여야 합니다", 2, tBlock.height());
    }
    
    @Test
    public void zBlockTest() {
        ZBlock zBlock = new ZBlock();
        assertNotNull("ZBlock이 생성되어야 합니다", zBlock);
        assertEquals("ZBlock의 색상은 RED여야 합니다", Color.RED, zBlock.getColor());
        assertEquals("ZBlock의 너비는 3이어야 합니다", 3, zBlock.width());
        assertEquals("ZBlock의 높이는 2여야 합니다", 2, zBlock.height());
    }
    
    @Test
    public void iBlockRotation() {
        IBlock iBlock = new IBlock();
        int originalWidth = iBlock.width();
        int originalHeight = iBlock.height();
        
        // 회전 테스트
        iBlock.rotate();
        assertEquals("회전 후 너비가 원래 높이와 같아야 합니다", originalHeight, iBlock.width());
        assertEquals("회전 후 높이가 원래 너비와 같아야 합니다", originalWidth, iBlock.height());
        
        // IBlock을 세로로 회전했을 때 모양 검증
        assertEquals("회전된 IBlock의 너비는 1이어야 합니다", 1, iBlock.width());
        assertEquals("회전된 IBlock의 높이는 4여야 합니다", 4, iBlock.height());
        for (int j = 0; j < 4; j++) {
            assertEquals("회전된 IBlock의 모든 셀은 1이어야 합니다", 1, iBlock.getShape(0, j));
        }
    }
    
    @Test
    public void jBlockRotation() {
        JBlock jBlock = new JBlock();
        
        // 4번 회전하면 원래 모양으로 돌아와야 함
        int originalWidth = jBlock.width();
        int originalHeight = jBlock.height();
        
        jBlock.rotate(); // 1번 회전
        jBlock.rotate(); // 2번 회전
        jBlock.rotate(); // 3번 회전
        jBlock.rotate(); // 4번 회전 (원래 상태)
        
        assertEquals("4번 회전 후 너비가 원래와 같아야 합니다", originalWidth, jBlock.width());
        assertEquals("4번 회전 후 높이가 원래와 같아야 합니다", originalHeight, jBlock.height());
    }
    
    @Test
    public void oBlockRotationStaysTheSame() {
        OBlock oBlock = new OBlock();
        int originalWidth = oBlock.width();
        int originalHeight = oBlock.height();
        
        // O블록은 회전해도 모양이 그대로여야 함
        oBlock.rotate();
        assertEquals("O블록 회전 후에도 너비가 같아야 합니다", originalWidth, oBlock.width());
        assertEquals("O블록 회전 후에도 높이가 같아야 합니다", originalHeight, oBlock.height());
        
        // 모든 셀이 여전히 1이어야 함
        for(int i = 0; i < oBlock.width(); i++) {
            for(int j = 0; j < oBlock.height(); j++) {
                assertEquals("회전 후에도 O블록의 모든 셀은 1이어야 합니다", 1, oBlock.getShape(i, j));
            }
        }
    }
    
    @Test
    public void blockShapeConsistency() {
        Block[] blocks = {
            new IBlock(), new OBlock(), new JBlock(), 
            new LBlock(), new SBlock(), new TBlock(), new ZBlock()
        };
        
        for(Block block : blocks) {
            String blockName = block.getClass().getSimpleName();
            
            // 크기가 양수여야 함
            assertTrue(blockName + "의 너비는 양수여야 합니다", block.width() > 0);
            assertTrue(blockName + "의 높이는 양수여야 합니다", block.height() > 0);
            
            // 모든 블록은 최소 하나의 셀이 1이어야 함
            boolean hasFilledCell = false;
            for(int i = 0; i < block.width(); i++) {
                for(int j = 0; j < block.height(); j++) {
                    int cellValue = block.getShape(i, j);
                    assertTrue(blockName + "의 셀 값은 0 또는 1이어야 합니다", 
                              cellValue == 0 || cellValue == 1);
                    if (cellValue == 1) {
                        hasFilledCell = true;
                    }
                }
            }
            assertTrue(blockName + "은 최소 하나의 채워진 셀이 있어야 합니다", hasFilledCell);
        }
    }
    
    @Test
    public void allBlocksHaveColor() {
        Block[] blocks = {
            new IBlock(), new OBlock(), new JBlock(), 
            new LBlock(), new SBlock(), new TBlock(), new ZBlock()
        };
        
        for(Block block : blocks) {
            assertNotNull(block.getClass().getSimpleName() + "의 색상이 null이 아니어야 합니다", 
                         block.getColor());
        }
    }
    
    @Test
    public void rotationPreservesBlockIntegrity() {
        Block[] blocks = {
            new IBlock(), new JBlock(), new LBlock(), 
            new SBlock(), new TBlock(), new ZBlock()
        };
        
        for(Block block : blocks) {
            String blockName = block.getClass().getSimpleName();
            
            // 회전 전 채워진 셀의 개수 세기
            int originalFilledCells = countFilledCells(block);
            
            // 회전 후에도 채워진 셀의 개수가 같아야 함
            block.rotate();
            int rotatedFilledCells = countFilledCells(block);
            
            assertEquals(blockName + " 회전 후에도 채워진 셀의 개수가 같아야 합니다", 
                        originalFilledCells, rotatedFilledCells);
        }
    }
    
    private int countFilledCells(Block block) {
        int count = 0;
        for(int i = 0; i < block.width(); i++) {
            for(int j = 0; j < block.height(); j++) {
                if(block.getShape(i, j) == 1) {
                    count++;
                }
            }
        }
        return count;
    }
    
    // GameSettings 테스트 추가 - 라인 커버리지 향상을 위해
    @Test
    public void gameSettingsBasicTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        assertNotNull("GameSettings instance should not be null", settings);
        
        // 기본 설정 테스트
        settings.setDefaultSettings();
        assertEquals("Default game speed should be 3", 3, settings.getGameSpeed());
        assertTrue("Default sound should be enabled", settings.isSoundEnabled());
        assertFalse("Default colorblind mode should be disabled", settings.isColorblindMode());
    }
    
    @Test
    public void gameSettingsSpeedTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // 속도 설정 테스트
        for (int speed = 1; speed <= 5; speed++) {
            settings.setGameSpeed(speed);
            assertEquals("Game speed should be " + speed, speed, settings.getGameSpeed());
        }
        
        // 속도 이름 테스트
        assertEquals("Speed 1 name should be 매우느림", "매우느림", settings.getGameSpeedName(1));
        assertEquals("Speed 2 name should be 느림", "느림", settings.getGameSpeedName(2));
        assertEquals("Speed 3 name should be 보통", "보통", settings.getGameSpeedName(3));
        assertEquals("Speed 4 name should be 빠름", "빠름", settings.getGameSpeedName(4));
        assertEquals("Speed 5 name should be 매우빠름", "매우빠름", settings.getGameSpeedName(5));
        assertEquals("Invalid speed should return 보통", "보통", settings.getGameSpeedName(0));
    }
    
    @Test
    public void gameSettingsWindowSizeTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // 창 크기 설정 테스트
        settings.setWindowSize(se.tetris.team5.utils.setting.GameSettings.WINDOW_SIZE_SMALL);
        assertEquals("Window size should be SMALL", se.tetris.team5.utils.setting.GameSettings.WINDOW_SIZE_SMALL, settings.getWindowSize());
        assertEquals("Small window width should be 450", 450, settings.getWindowWidth());
        assertEquals("Small window height should be 600", 600, settings.getWindowHeight());
        
        settings.setWindowSize(se.tetris.team5.utils.setting.GameSettings.WINDOW_SIZE_LARGE);
        assertEquals("Large window width should be 650", 650, settings.getWindowWidth());
        assertEquals("Large window height should be 800", 800, settings.getWindowHeight());
    }
    
    @Test
    public void gameSettingsKeyBindingTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // 키 바인딩 테스트
        assertEquals("Default down key should be 40", 40, settings.getKeyCode("down"));
        assertEquals("Default left key should be 37", 37, settings.getKeyCode("left"));
        assertEquals("Default right key should be 39", 39, settings.getKeyCode("right"));
        assertEquals("Default rotate key should be 38", 38, settings.getKeyCode("rotate"));
        assertEquals("Default drop key should be 32", 32, settings.getKeyCode("drop"));
        assertEquals("Default pause key should be 80", 80, settings.getKeyCode("pause"));
        
        // 잘못된 액션
        assertEquals("Invalid action should return -1", -1, settings.getKeyCode("invalid"));
        
        // 키 변경 테스트
        settings.setKeyCode("down", 83); // S키
        assertEquals("Down key should be changed to 83", 83, settings.getKeyCode("down"));
    }
    
    @Test
    public void gameSettingsColorTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // 일반 모드 색상 테스트
        settings.setColorblindMode(false);
        assertEquals("I block should be cyan in normal mode", Color.CYAN, settings.getColorForBlock("I"));
        assertEquals("O block should be yellow in normal mode", Color.YELLOW, settings.getColorForBlock("O"));
        assertEquals("T block should be magenta in normal mode", Color.MAGENTA, settings.getColorForBlock("T"));
        assertEquals("L block should be orange in normal mode", Color.ORANGE, settings.getColorForBlock("L"));
        assertEquals("J block should be blue in normal mode", Color.BLUE, settings.getColorForBlock("J"));
        assertEquals("S block should be green in normal mode", Color.GREEN, settings.getColorForBlock("S"));
        assertEquals("Z block should be red in normal mode", Color.RED, settings.getColorForBlock("Z"));
        assertEquals("Unknown block should be white", Color.WHITE, settings.getColorForBlock("unknown"));
        
        // 색맹 모드 색상 테스트
        settings.setColorblindMode(true);
        assertEquals("I block should be teal in colorblind mode", new Color(0, 158, 115), settings.getColorForBlock("I"));
        assertEquals("O block should be yellow in colorblind mode", new Color(240, 228, 66), settings.getColorForBlock("O"));
        assertEquals("T block should be light purple in colorblind mode", new Color(204, 121, 167), settings.getColorForBlock("T"));
    }
    
    @Test
    public void gameSettingsUIColorTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // 일반 모드 UI 색상
        settings.setColorblindMode(false);
        assertEquals("Background should be black", Color.BLACK, settings.getUIColor("background"));
        assertEquals("Text should be white", Color.WHITE, settings.getUIColor("text"));
        assertEquals("Highlight should be green in normal mode", Color.GREEN, settings.getUIColor("highlight"));
        assertEquals("Border should be gray", Color.GRAY, settings.getUIColor("border"));
        assertEquals("Unknown UI element should be white", Color.WHITE, settings.getUIColor("unknown"));
        
        // 색맹 모드 UI 색상
        settings.setColorblindMode(true);
        assertEquals("Text should be light gray in colorblind mode", new Color(240, 240, 240), settings.getUIColor("text"));
        assertEquals("Highlight should be yellow in colorblind mode", new Color(240, 228, 66), settings.getUIColor("highlight"));
        assertEquals("Border should be gray in colorblind mode", new Color(128, 128, 128), settings.getUIColor("border"));
    }
    
    @Test
    public void gameSettingsKeyNameTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // 특수 키 이름 테스트
        assertEquals("Arrow left should be ←", "←", settings.getKeyName(37));
        assertEquals("Arrow up should be ↑", "↑", settings.getKeyName(38));
        assertEquals("Arrow right should be →", "→", settings.getKeyName(39));
        assertEquals("Arrow down should be ↓", "↓", settings.getKeyName(40));
        assertEquals("Space should be Space", "Space", settings.getKeyName(32));
        assertEquals("P should be P", "P", settings.getKeyName(80));
        assertEquals("Enter should be Enter", "Enter", settings.getKeyName(10));
        assertEquals("Esc should be Esc", "Esc", settings.getKeyName(27));
        assertEquals("Tab should be Tab", "Tab", settings.getKeyName(9));
        assertEquals("Shift should be Shift", "Shift", settings.getKeyName(16));
        assertEquals("Ctrl should be Ctrl", "Ctrl", settings.getKeyName(17));
        assertEquals("Alt should be Alt", "Alt", settings.getKeyName(18));
        assertEquals("Invalid key should be 없음", "없음", settings.getKeyName(-1));
        assertEquals("Unknown key should be Key999", "Key999", settings.getKeyName(999));
        
        // 알파벳 키 테스트
        assertEquals("A should be A", "A", settings.getKeyName(65));
        assertEquals("S should be S", "S", settings.getKeyName(83));
        assertEquals("W should be W", "W", settings.getKeyName(87));
        assertEquals("Z should be Z", "Z", settings.getKeyName(90));
    }
    
    @Test
    public void gameSettingsDuplicateKeyTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // 중복 키 처리 테스트
        settings.setKeyCode("down", 65); // A키로 설정
        settings.setKeyCode("left", 65); // 같은 A키로 설정
        
        // down 키는 -1로 변경되어야 함 (비활성화)
        assertEquals("Previous key binding should be disabled", -1, settings.getKeyCode("down"));
        assertEquals("New key binding should be active", 65, settings.getKeyCode("left"));
    }
    
    @Test
    public void gameSettingsFileOperationsTest() {
        se.tetris.team5.utils.setting.GameSettings settings = se.tetris.team5.utils.setting.GameSettings.getInstance();
        
        // 설정 저장/로드 테스트
        settings.setGameSpeed(5);
        settings.setColorblindMode(true);
        settings.setSoundEnabled(false);
        settings.setWindowSize(se.tetris.team5.utils.setting.GameSettings.WINDOW_SIZE_SMALL);
        
        // 설정 저장
        settings.saveSettings();
        
        // 설정 로드
        settings.loadSettings();
        
        // 설정이 올바르게 로드되었는지 확인
        assertEquals("Game speed should be persisted", 5, settings.getGameSpeed());
        assertTrue("Colorblind mode should be persisted", settings.isColorblindMode());
        assertFalse("Sound setting should be persisted", settings.isSoundEnabled());
        assertEquals("Window size should be persisted", se.tetris.team5.utils.setting.GameSettings.WINDOW_SIZE_SMALL, settings.getWindowSize());
    }
}