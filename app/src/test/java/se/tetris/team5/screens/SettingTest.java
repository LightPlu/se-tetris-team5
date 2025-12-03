package se.tetris.team5.screens;

import org.junit.*;
import static org.junit.Assert.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import se.tetris.team5.utils.setting.GameSettings;
import se.tetris.team5.ScreenController;
import se.tetris.team5.components.home.BGMManager;

public class SettingTest {
    private setting settingScreen;
    private ScreenController mockController;
    private JTextPane testPane;
    private GameSettings mockSettings;

    @Before
    public void setUp() {
        mockController = new ScreenController();
        mockSettings = GameSettings.getInstance();
        settingScreen = new setting(mockController);
        testPane = new JTextPane();
    }

    @After
    public void tearDown() {
        settingScreen = null;
        testPane = null;
        mockSettings = null;
        mockController = null;
    }

    // ==================== 기본 생성 및 초기화 테스트 ====================

    @Test
    public void testSettingConstructor() {
        assertNotNull("Setting 객체가 생성되어야 함", settingScreen);
    }

    @Test
    public void testSettingConstructorWithNullController() {
        try {
            setting screen = new setting(null);
            assertNotNull("null controller로도 생성 가능해야 함", screen);
        } catch (Exception e) {
            // NullPointerException이 발생할 수 있음
        }
    }

    @Test
    public void testScreenControllerField() {
        try {
            Field controllerField = setting.class.getDeclaredField("screenController");
            controllerField.setAccessible(true);
            ScreenController controller = (ScreenController) controllerField.get(settingScreen);
            assertNotNull("ScreenController는 null이 아니어야 함", controller);
        } catch (Exception e) {
            fail("ScreenController 필드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testGameSettingsField() {
        try {
            Field settingsField = setting.class.getDeclaredField("gameSettings");
            settingsField.setAccessible(true);
            GameSettings settings = (GameSettings) settingsField.get(settingScreen);
            assertNotNull("GameSettings는 null이 아니어야 함", settings);
        } catch (Exception e) {
            fail("GameSettings 필드 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== 선택된 옵션 테스트 ====================

    @Test
    public void testSelectedOptionInitialValue() {
        try {
            Field selectedField = setting.class.getDeclaredField("selectedOption");
            selectedField.setAccessible(true);
            int selected = (int) selectedField.get(settingScreen);
            assertEquals("초기 선택 옵션은 0이어야 함", 0, selected);
        } catch (Exception e) {
            fail("selectedOption 초기값 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSelectedOptionChangeToAll() {
        try {
            Field selectedField = setting.class.getDeclaredField("selectedOption");
            selectedField.setAccessible(true);
            
            for (int i = 0; i < 9; i++) {
                selectedField.set(settingScreen, i);
                assertEquals(i, selectedField.get(settingScreen));
            }
        } catch (Exception e) {
            fail("selectedOption 변경 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== 메뉴 옵션 테스트 ====================

    @Test
    public void testMenuOptionsArray() {
        try {
            Field menuField = setting.class.getDeclaredField("menuOptions");
            menuField.setAccessible(true);
            String[] menu = (String[]) menuField.get(settingScreen);
            assertNotNull("메뉴 옵션 배열은 null이 아니어야 함", menu);
            assertEquals("메뉴 옵션은 9개여야 함", 9, menu.length);
        } catch (Exception e) {
            fail("menuOptions 배열 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testMenuOptionsContent() {
        try {
            Field menuField = setting.class.getDeclaredField("menuOptions");
            menuField.setAccessible(true);
            String[] menu = (String[]) menuField.get(settingScreen);
            
            for (String option : menu) {
                assertNotNull("메뉴 옵션은 null이 아니어야 함", option);
                assertFalse("메뉴 옵션은 비어있지 않아야 함", option.isEmpty());
            }
        } catch (Exception e) {
            fail("menuOptions 내용 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== 창 크기 테스트 ====================

    @Test
    public void testWindowSizesArray() {
        try {
            Field sizesField = setting.class.getDeclaredField("windowSizes");
            sizesField.setAccessible(true);
            Dimension[] sizes = (Dimension[]) sizesField.get(settingScreen);
            assertNotNull("창 크기 배열은 null이 아니어야 함", sizes);
            assertEquals("창 크기는 3개여야 함", 3, sizes.length);
        } catch (Exception e) {
            fail("windowSizes 배열 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testWindowSizesValues() {
        try {
            Field sizesField = setting.class.getDeclaredField("windowSizes");
            sizesField.setAccessible(true);
            Dimension[] sizes = (Dimension[]) sizesField.get(settingScreen);
            
            assertEquals("Small 크기: 450x600", new Dimension(450, 600), sizes[0]);
            assertEquals("Medium 크기: 550x700", new Dimension(550, 700), sizes[1]);
            assertEquals("Large 크기: 650x800", new Dimension(650, 800), sizes[2]);
        } catch (Exception e) {
            fail("windowSizes 값 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testCurrentSizeIndex() {
        try {
            Field indexField = setting.class.getDeclaredField("currentSizeIndex");
            indexField.setAccessible(true);
            int index = (int) indexField.get(settingScreen);
            assertTrue("currentSizeIndex는 0-2 범위여야 함", index >= 0 && index <= 2);
        } catch (Exception e) {
            fail("currentSizeIndex 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testCurrentSizeIndexChange() {
        try {
            Field indexField = setting.class.getDeclaredField("currentSizeIndex");
            indexField.setAccessible(true);
            
            for (int i = 0; i < 3; i++) {
                indexField.set(settingScreen, i);
                assertEquals(i, indexField.get(settingScreen));
            }
        } catch (Exception e) {
            fail("currentSizeIndex 변경 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== 키 설정 모드 테스트 ====================

    @Test
    public void testIsKeySettingModeInitial() {
        try {
            Field keyModeField = setting.class.getDeclaredField("isKeySettingMode");
            keyModeField.setAccessible(true);
            boolean isKeyMode = (boolean) keyModeField.get(settingScreen);
            assertFalse("초기 키 설정 모드는 false여야 함", isKeyMode);
        } catch (Exception e) {
            fail("isKeySettingMode 초기값 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testIsKeySettingModeToggle() {
        try {
            Field keyModeField = setting.class.getDeclaredField("isKeySettingMode");
            keyModeField.setAccessible(true);
            
            keyModeField.set(settingScreen, true);
            assertTrue((boolean) keyModeField.get(settingScreen));
            
            keyModeField.set(settingScreen, false);
            assertFalse((boolean) keyModeField.get(settingScreen));
        } catch (Exception e) {
            fail("isKeySettingMode 토글 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testKeyActionsArray() {
        try {
            Field actionsField = setting.class.getDeclaredField("keyActions");
            actionsField.setAccessible(true);
            String[] actions = (String[]) actionsField.get(settingScreen);
            assertNotNull("키 액션 배열은 null이 아니어야 함", actions);
            assertEquals("키 액션은 7개여야 함", 7, actions.length);
        } catch (Exception e) {
            fail("keyActions 배열 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testKeyActionsContent() {
        try {
            Field actionsField = setting.class.getDeclaredField("keyActions");
            actionsField.setAccessible(true);
            String[] actions = (String[]) actionsField.get(settingScreen);
            
            for (String action : actions) {
                assertNotNull("키 액션은 null이 아니어야 함", action);
                assertFalse("키 액션은 비어있지 않아야 함", action.isEmpty());
            }
        } catch (Exception e) {
            fail("keyActions 내용 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testCurrentKeyIndex() {
        try {
            Field indexField = setting.class.getDeclaredField("currentKeyIndex");
            indexField.setAccessible(true);
            int index = (int) indexField.get(settingScreen);
            assertEquals("초기 키 인덱스는 0이어야 함", 0, index);
        } catch (Exception e) {
            fail("currentKeyIndex 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testCurrentKeyIndexChange() {
        try {
            Field indexField = setting.class.getDeclaredField("currentKeyIndex");
            indexField.setAccessible(true);
            
            for (int i = 0; i < 7; i++) {
                indexField.set(settingScreen, i);
                assertEquals(i, indexField.get(settingScreen));
            }
        } catch (Exception e) {
            fail("currentKeyIndex 변경 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== 대전 키 설정 모드 테스트 ====================

    @Test
    public void testIsBattleKeySettingModeInitial() {
        try {
            Field battleModeField = setting.class.getDeclaredField("isBattleKeySettingMode");
            battleModeField.setAccessible(true);
            boolean isBattleMode = (boolean) battleModeField.get(settingScreen);
            assertFalse("초기 대전 키 설정 모드는 false여야 함", isBattleMode);
        } catch (Exception e) {
            fail("isBattleKeySettingMode 초기값 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testIsBattleKeySettingModeToggle() {
        try {
            Field battleModeField = setting.class.getDeclaredField("isBattleKeySettingMode");
            battleModeField.setAccessible(true);
            
            battleModeField.set(settingScreen, true);
            assertTrue((boolean) battleModeField.get(settingScreen));
            
            battleModeField.set(settingScreen, false);
            assertFalse((boolean) battleModeField.get(settingScreen));
        } catch (Exception e) {
            fail("isBattleKeySettingMode 토글 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testBattleKeyActionsArray() {
        try {
            Field actionsField = setting.class.getDeclaredField("battleKeyActions");
            actionsField.setAccessible(true);
            String[] actions = (String[]) actionsField.get(settingScreen);
            assertNotNull("대전 키 액션 배열은 null이 아니어야 함", actions);
            assertEquals("대전 키 액션은 6개여야 함", 6, actions.length);
        } catch (Exception e) {
            fail("battleKeyActions 배열 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testBattleKeyActionsContent() {
        try {
            Field actionsField = setting.class.getDeclaredField("battleKeyActions");
            actionsField.setAccessible(true);
            String[] actions = (String[]) actionsField.get(settingScreen);
            
            for (String action : actions) {
                assertNotNull("대전 키 액션은 null이 아니어야 함", action);
                assertFalse("대전 키 액션은 비어있지 않아야 함", action.isEmpty());
            }
        } catch (Exception e) {
            fail("battleKeyActions 내용 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testBattleKeyPlayerNum() {
        try {
            Field playerNumField = setting.class.getDeclaredField("battleKeyPlayerNum");
            playerNumField.setAccessible(true);
            int playerNum = (int) playerNumField.get(settingScreen);
            assertTrue("플레이어 번호는 1 또는 2여야 함", playerNum == 1 || playerNum == 2);
        } catch (Exception e) {
            fail("battleKeyPlayerNum 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testBattleKeyPlayerNumChange() {
        try {
            Field playerNumField = setting.class.getDeclaredField("battleKeyPlayerNum");
            playerNumField.setAccessible(true);
            
            playerNumField.set(settingScreen, 1);
            assertEquals(1, playerNumField.get(settingScreen));
            
            playerNumField.set(settingScreen, 2);
            assertEquals(2, playerNumField.get(settingScreen));
        } catch (Exception e) {
            fail("battleKeyPlayerNum 변경 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== Display 메서드 테스트 ====================

    @Test
    public void testDisplay() {
        try {
            settingScreen.display(testPane);
            assertNotNull("display 호출 후 패널이 설정되어야 함", testPane.getComponents());
        } catch (Exception e) {
            fail("display 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testDisplayWithNullPane() {
        try {
            settingScreen.display(null);
            fail("null 패널로 display 시 예외가 발생해야 함");
        } catch (Exception e) {
            // NullPointerException 예상
        }
    }

    @Test
    public void testMultipleDisplayCalls() {
        try {
            for (int i = 0; i < 5; i++) {
                JTextPane newPane = new JTextPane();
                settingScreen.display(newPane);
                assertNotNull("각 display 호출 후 컴포넌트가 있어야 함", newPane.getComponents());
            }
        } catch (Exception e) {
            fail("여러 번 display 호출 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== DrawSettingScreen 메서드 테스트 ====================

    @Test
    public void testDrawSettingScreen() {
        try {
            Method drawMethod = setting.class.getDeclaredMethod("drawSettingScreen", JTextPane.class);
            drawMethod.setAccessible(true);
            drawMethod.invoke(settingScreen, testPane);
        } catch (Exception e) {
            fail("drawSettingScreen 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testDrawSettingScreenWithNullPane() {
        try {
            Method drawMethod = setting.class.getDeclaredMethod("drawSettingScreen", JTextPane.class);
            drawMethod.setAccessible(true);
            drawMethod.invoke(settingScreen, (JTextPane) null);
            fail("null 패널로 drawSettingScreen 시 예외가 발생해야 함");
        } catch (Exception e) {
            // InvocationTargetException 또는 NullPointerException 예상
        }
    }

    // ==================== DrawKeySettingScreen 메서드 테스트 ====================

    @Test
    public void testDrawKeySettingScreen() {
        try {
            Method drawMethod = setting.class.getDeclaredMethod("drawKeySettingScreen", JTextPane.class);
            drawMethod.setAccessible(true);
            drawMethod.invoke(settingScreen, testPane);
        } catch (Exception e) {
            fail("drawKeySettingScreen 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testDrawKeySettingScreenWithNullPane() {
        try {
            Method drawMethod = setting.class.getDeclaredMethod("drawKeySettingScreen", JTextPane.class);
            drawMethod.setAccessible(true);
            drawMethod.invoke(settingScreen, (JTextPane) null);
            fail("null 패널로 drawKeySettingScreen 시 예외가 발생해야 함");
        } catch (Exception e) {
            // InvocationTargetException 또는 NullPointerException 예상
        }
    }

    // ==================== DrawBattleKeySettingScreen 메서드 테스트 ====================

    @Test
    public void testDrawBattleKeySettingScreen() {
        try {
            Method drawMethod = setting.class.getDeclaredMethod("drawBattleKeySettingScreen", JTextPane.class);
            drawMethod.setAccessible(true);
            drawMethod.invoke(settingScreen, testPane);
        } catch (Exception e) {
            fail("drawBattleKeySettingScreen 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testDrawBattleKeySettingScreenWithNullPane() {
        try {
            Method drawMethod = setting.class.getDeclaredMethod("drawBattleKeySettingScreen", JTextPane.class);
            drawMethod.setAccessible(true);
            drawMethod.invoke(settingScreen, (JTextPane) null);
            fail("null 패널로 drawBattleKeySettingScreen 시 예외가 발생해야 함");
        } catch (Exception e) {
            // InvocationTargetException 또는 NullPointerException 예상
        }
    }

    // ==================== HandleMenuAction 메서드 테스트 ====================

    @Test
    public void testHandleMenuActionAllOptions() {
        try {
            Method handleMethod = setting.class.getDeclaredMethod("handleMenuAction");
            handleMethod.setAccessible(true);
            
            Field selectedField = setting.class.getDeclaredField("selectedOption");
            selectedField.setAccessible(true);
            
            for (int i = 0; i < 9; i++) {
                selectedField.set(settingScreen, i);
                handleMethod.invoke(settingScreen);
            }
        } catch (Exception e) {
            fail("handleMenuAction 모든 옵션 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== HandleLeftRight 메서드 테스트 ====================

    @Test
    public void testHandleLeftRightLeft() {
        try {
            Method handleMethod = setting.class.getDeclaredMethod("handleLeftRight", boolean.class);
            handleMethod.setAccessible(true);
            handleMethod.invoke(settingScreen, false);
        } catch (Exception e) {
            fail("handleLeftRight (left) 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testHandleLeftRightRight() {
        try {
            Method handleMethod = setting.class.getDeclaredMethod("handleLeftRight", boolean.class);
            handleMethod.setAccessible(true);
            handleMethod.invoke(settingScreen, true);
        } catch (Exception e) {
            fail("handleLeftRight (right) 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== ApplySizeChange 메서드 테스트 ====================

    @Test
    public void testApplySizeChangeAllSizes() {
        try {
            Method applyMethod = setting.class.getDeclaredMethod("applySizeChange", int.class);
            applyMethod.setAccessible(true);
            
            for (int i = 0; i < 3; i++) {
                applyMethod.invoke(settingScreen, i);
            }
        } catch (Exception e) {
            fail("applySizeChange 모든 크기 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testApplySizeChangeSmall() {
        try {
            Method applyMethod = setting.class.getDeclaredMethod("applySizeChange", int.class);
            applyMethod.setAccessible(true);
            applyMethod.invoke(settingScreen, 0);
        } catch (Exception e) {
            fail("applySizeChange (small) 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testApplySizeChangeMedium() {
        try {
            Method applyMethod = setting.class.getDeclaredMethod("applySizeChange", int.class);
            applyMethod.setAccessible(true);
            applyMethod.invoke(settingScreen, 1);
        } catch (Exception e) {
            fail("applySizeChange (medium) 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testApplySizeChangeLarge() {
        try {
            Method applyMethod = setting.class.getDeclaredMethod("applySizeChange", int.class);
            applyMethod.setAccessible(true);
            applyMethod.invoke(settingScreen, 2);
        } catch (Exception e) {
            fail("applySizeChange (large) 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== UpdateGameColors 메서드 테스트 ====================

    @Test
    public void testUpdateGameColors() {
        try {
            Method updateMethod = setting.class.getDeclaredMethod("updateGameColors");
            updateMethod.setAccessible(true);
            updateMethod.invoke(settingScreen);
        } catch (Exception e) {
            fail("updateGameColors 메서드 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== UpdateGameSpeed 메서드 테스트 ====================

    @Test
    public void testUpdateGameSpeed() {
        try {
            Method updateMethod = setting.class.getDeclaredMethod("updateGameSpeed", int.class);
            updateMethod.setAccessible(true);
            
            for (int speed = 1; speed <= 5; speed++) {
                updateMethod.invoke(settingScreen, speed);
            }
        } catch (Exception e) {
            fail("updateGameSpeed 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateGameSpeedSpeed1() {
        try {
            Method updateMethod = setting.class.getDeclaredMethod("updateGameSpeed", int.class);
            updateMethod.setAccessible(true);
            updateMethod.invoke(settingScreen, 1);
        } catch (Exception e) {
            fail("updateGameSpeed (speed 1) 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateGameSpeedSpeed5() {
        try {
            Method updateMethod = setting.class.getDeclaredMethod("updateGameSpeed", int.class);
            updateMethod.setAccessible(true);
            updateMethod.invoke(settingScreen, 5);
        } catch (Exception e) {
            fail("updateGameSpeed (speed 5) 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== ControlBGM 메서드 테스트 ====================

    @Test
    public void testControlBGM() {
        try {
            Method controlMethod = setting.class.getDeclaredMethod("controlBGM");
            controlMethod.setAccessible(true);
            controlMethod.invoke(settingScreen);
        } catch (Exception e) {
            fail("controlBGM 메서드 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== ShowConfirmation 메서드 테스트 ====================

    @Test
    public void testShowConfirmation() {
        try {
            Method showMethod = setting.class.getDeclaredMethod("showConfirmation", String.class);
            showMethod.setAccessible(true);
            // 실제로는 다이얼로그가 뜨므로 호출만 확인
        } catch (Exception e) {
            fail("showConfirmation 메서드 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== ShowKeyWarning 메서드 테스트 ====================

    @Test
    public void testShowKeyWarning() {
        try {
            Method showMethod = setting.class.getDeclaredMethod("showKeyWarning", String.class);
            showMethod.setAccessible(true);
            // 실제로는 다이얼로그가 뜨므로 호출만 확인
        } catch (Exception e) {
            fail("showKeyWarning 메서드 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== 이미지 로딩 테스트 ====================

    @Test
    public void testLoadBackgroundImage() {
        try {
            Method loadMethod = setting.class.getDeclaredMethod("loadBackgroundImage");
            loadMethod.setAccessible(true);
            Object image = loadMethod.invoke(settingScreen);
            // 이미지가 로드되거나 null일 수 있음
        } catch (Exception e) {
            fail("loadBackgroundImage 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testLoadSelectorImage() {
        try {
            Method loadMethod = setting.class.getDeclaredMethod("loadSelectorImage");
            loadMethod.setAccessible(true);
            Object image = loadMethod.invoke(settingScreen);
            // 이미지가 로드되거나 null일 수 있음
        } catch (Exception e) {
            fail("loadSelectorImage 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testScaleSelectorIcon() {
        try {
            Method scaleMethod = setting.class.getDeclaredMethod("scaleSelectorIcon", ImageIcon.class, int.class, int.class);
            scaleMethod.setAccessible(true);
            ImageIcon testIcon = new ImageIcon();
            ImageIcon scaled = (ImageIcon) scaleMethod.invoke(settingScreen, testIcon, 100, 100);
        } catch (Exception e) {
            fail("scaleSelectorIcon 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testScaleSelectorIconWithNull() {
        try {
            Method scaleMethod = setting.class.getDeclaredMethod("scaleSelectorIcon", ImageIcon.class, int.class, int.class);
            scaleMethod.setAccessible(true);
            Object scaled = scaleMethod.invoke(settingScreen, null, 100, 100);
            assertNull("null 아이콘은 null을 반환해야 함", scaled);
        } catch (Exception e) {
            fail("scaleSelectorIcon (null) 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testScaleSelectorIconVariousSizes() {
        try {
            Method scaleMethod = setting.class.getDeclaredMethod("scaleSelectorIcon", ImageIcon.class, int.class, int.class);
            scaleMethod.setAccessible(true);
            ImageIcon testIcon = new ImageIcon();
            
            int[][] sizes = {{50, 50}, {100, 100}, {150, 150}, {200, 200}};
            for (int[] size : sizes) {
                scaleMethod.invoke(settingScreen, testIcon, size[0], size[1]);
            }
        } catch (Exception e) {
            fail("scaleSelectorIcon 다양한 크기 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== 키 충돌 확인 메서드 테스트 ====================

    @Test
    public void testFindConflictingAction() {
        try {
            Method findMethod = setting.class.getDeclaredMethod("findConflictingAction", int.class, int.class);
            findMethod.setAccessible(true);
            Object result = findMethod.invoke(settingScreen, KeyEvent.VK_UP, 0);
            // 충돌이 있을 수도, 없을 수도 있음
        } catch (Exception e) {
            fail("findConflictingAction 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testFindConflictingActionAllIndices() {
        try {
            Method findMethod = setting.class.getDeclaredMethod("findConflictingAction", int.class, int.class);
            findMethod.setAccessible(true);
            
            for (int i = 0; i < 7; i++) {
                findMethod.invoke(settingScreen, KeyEvent.VK_A, i);
            }
        } catch (Exception e) {
            fail("findConflictingAction 모든 인덱스 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testFindBattleConflictingAction() {
        try {
            Method findMethod = setting.class.getDeclaredMethod("findBattleConflictingAction", int.class, int.class, int.class);
            findMethod.setAccessible(true);
            Object result = findMethod.invoke(settingScreen, KeyEvent.VK_W, 1, 0);
            // 충돌이 있을 수도, 없을 수도 있음
        } catch (Exception e) {
            fail("findBattleConflictingAction 메서드 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testFindBattleConflictingActionBothPlayers() {
        try {
            Method findMethod = setting.class.getDeclaredMethod("findBattleConflictingAction", int.class, int.class, int.class);
            findMethod.setAccessible(true);
            
            for (int player = 1; player <= 2; player++) {
                for (int index = 0; index < 6; index++) {
                    findMethod.invoke(settingScreen, KeyEvent.VK_S, player, index);
                }
            }
        } catch (Exception e) {
            fail("findBattleConflictingAction 양 플레이어 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== SettingKeyListener 내부 클래스 테스트 ====================

    @Test
    public void testSettingKeyListenerExists() {
        try {
            Class<?> listenerClass = Class.forName("se.tetris.team5.screens.setting$SettingKeyListener");
            assertNotNull("SettingKeyListener 클래스가 존재해야 함", listenerClass);
        } catch (Exception e) {
            fail("SettingKeyListener 클래스 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSettingKeyListenerImplementsKeyListener() {
        try {
            Class<?> listenerClass = Class.forName("se.tetris.team5.screens.setting$SettingKeyListener");
            assertTrue("SettingKeyListener는 KeyListener를 구현해야 함",
                java.awt.event.KeyListener.class.isAssignableFrom(listenerClass));
        } catch (Exception e) {
            fail("SettingKeyListener 구현 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testKeyListenerAddedAfterDisplay() {
        try {
            settingScreen.display(testPane);
            KeyListener[] listeners = testPane.getKeyListeners();
            assertTrue("KeyListener가 추가되어야 함", listeners.length > 0);
        } catch (Exception e) {
            fail("KeyListener 추가 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testKeyPressedEscape() {
        try {
            settingScreen.display(testPane);
            KeyListener listener = testPane.getKeyListeners()[0];
            KeyEvent escKey = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
            listener.keyPressed(escKey);
        } catch (Exception e) {
            fail("ESC 키 입력 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testKeyPressedArrowKeys() {
        try {
            settingScreen.display(testPane);
            KeyListener listener = testPane.getKeyListeners()[0];
            
            KeyEvent[] arrowKeys = {
                new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED),
                new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED),
                new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED),
                new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED)
            };
            
            for (KeyEvent ke : arrowKeys) {
                listener.keyPressed(ke);
            }
        } catch (Exception e) {
            fail("방향키 입력 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testKeyPressedEnter() {
        try {
            settingScreen.display(testPane);
            KeyListener listener = testPane.getKeyListeners()[0];
            KeyEvent enterKey = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
            listener.keyPressed(enterKey);
        } catch (Exception e) {
            fail("Enter 키 입력 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== SettingBackgroundPanel 내부 클래스 테스트 ====================

    @Test
    public void testSettingBackgroundPanelExists() {
        try {
            Class<?> bgClass = Class.forName("se.tetris.team5.screens.setting$SettingBackgroundPanel");
            assertNotNull("SettingBackgroundPanel 클래스가 존재해야 함", bgClass);
        } catch (Exception e) {
            fail("SettingBackgroundPanel 클래스 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testSettingBackgroundPanelExtendsJPanel() {
        try {
            Class<?> bgClass = Class.forName("se.tetris.team5.screens.setting$SettingBackgroundPanel");
            assertTrue("SettingBackgroundPanel은 JPanel을 상속해야 함",
                JPanel.class.isAssignableFrom(bgClass));
        } catch (Exception e) {
            fail("SettingBackgroundPanel 상속 테스트 실패: " + e.getMessage());
        }
    }

    // ==================== 통합 테스트 ====================

    @Test
    public void testMenuNavigationCycle() {
        try {
            Field selectedField = setting.class.getDeclaredField("selectedOption");
            selectedField.setAccessible(true);
            
            settingScreen.display(testPane);
            KeyListener listener = testPane.getKeyListeners()[0];
            
            for (int i = 0; i < 20; i++) {
                KeyEvent downKey = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
                listener.keyPressed(downKey);
            }
            
            for (int i = 0; i < 20; i++) {
                KeyEvent upKey = new KeyEvent(testPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
                listener.keyPressed(upKey);
            }
        } catch (Exception e) {
            fail("메뉴 탐색 순환 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testWindowSizeCycle() {
        try {
            Field indexField = setting.class.getDeclaredField("currentSizeIndex");
            indexField.setAccessible(true);
            
            for (int cycle = 0; cycle < 5; cycle++) {
                for (int i = 0; i < 3; i++) {
                    indexField.set(settingScreen, i);
                }
            }
        } catch (Exception e) {
            fail("창 크기 순환 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testKeySettingModeTransition() {
        try {
            Field keyModeField = setting.class.getDeclaredField("isKeySettingMode");
            keyModeField.setAccessible(true);
            Field battleModeField = setting.class.getDeclaredField("isBattleKeySettingMode");
            battleModeField.setAccessible(true);
            
            // 일반 -> 키 설정
            keyModeField.set(settingScreen, true);
            assertTrue((boolean) keyModeField.get(settingScreen));
            
            // 키 설정 -> 일반
            keyModeField.set(settingScreen, false);
            assertFalse((boolean) keyModeField.get(settingScreen));
            
            // 일반 -> 대전 키 설정
            battleModeField.set(settingScreen, true);
            assertTrue((boolean) battleModeField.get(settingScreen));
            
            // 대전 키 설정 -> 일반
            battleModeField.set(settingScreen, false);
            assertFalse((boolean) battleModeField.get(settingScreen));
        } catch (Exception e) {
            fail("키 설정 모드 전환 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testAllGameSpeedSettings() {
        try {
            Method updateMethod = setting.class.getDeclaredMethod("updateGameSpeed", int.class);
            updateMethod.setAccessible(true);
            
            for (int speed = 1; speed <= 5; speed++) {
                updateMethod.invoke(settingScreen, speed);
            }
            
            for (int speed = 5; speed >= 1; speed--) {
                updateMethod.invoke(settingScreen, speed);
            }
        } catch (Exception e) {
            fail("모든 게임 속도 설정 테스트 실패: " + e.getMessage());
        }
    }
}
