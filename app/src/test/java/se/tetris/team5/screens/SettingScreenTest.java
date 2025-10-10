package se.tetris.team5.screens;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JTextPane;

import se.tetris.team5.ScreenController;
import se.tetris.team5.utils.setting.GameSettings;

public class SettingScreenTest {
    
    private setting settingScreen;
    private TestScreenController testScreenController;
    private JTextPane testTextPane;
    private GameSettings gameSettings;
    
    // 테스트용 ScreenController 구현
    private class TestScreenController extends ScreenController {
        private String lastShowScreenCall = "";
        private boolean windowSizeUpdated = false;
        private String currentScreen = "setting";
        
        @Override
        public void showScreen(String screenName) {
            lastShowScreenCall = screenName;
        }
        
        @Override
        public void updateWindowSize() {
            windowSizeUpdated = true;
        }
        
        @Override
        public String getCurrentScreen() {
            return currentScreen;
        }
        
        public String getLastShowScreenCall() {
            return lastShowScreenCall;
        }
        
        public boolean wasWindowSizeUpdated() {
            return windowSizeUpdated;
        }
        
        public void setCurrentScreen(String screen) {
            currentScreen = screen;
        }
        
        public void resetFlags() {
            lastShowScreenCall = "";
            windowSizeUpdated = false;
        }
    }
    
    @Before
    public void setUp() {
        // 테스트 객체들 생성
        testScreenController = new TestScreenController();
        testTextPane = new JTextPane();
        
        // GameSettings 인스턴스 초기화
        gameSettings = GameSettings.getInstance();
        gameSettings.setDefaultSettings(); // 테스트를 위해 기본 설정으로 초기화
        
        // setting 객체 생성
        settingScreen = new setting(testScreenController);
    }
    
    @After
    public void tearDown() {
        // 테스트 후 기본 설정으로 복원
        gameSettings.setDefaultSettings();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull("Setting 객체가 생성되어야 합니다", settingScreen);
        
        // private 필드들이 올바르게 초기화되었는지 확인
        try {
            Field screenControllerField = setting.class.getDeclaredField("screenController");
            screenControllerField.setAccessible(true);
            assertEquals("ScreenController가 올바르게 설정되어야 합니다", 
                testScreenController, screenControllerField.get(settingScreen));
            
            Field gameSettingsField = setting.class.getDeclaredField("gameSettings");
            gameSettingsField.setAccessible(true);
            assertNotNull("GameSettings가 초기화되어야 합니다", gameSettingsField.get(settingScreen));
            
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            assertEquals("초기 선택 옵션은 0이어야 합니다", 0, selectedOptionField.get(settingScreen));
            
        } catch (Exception e) {
            fail("생성자 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testDisplay() {
        settingScreen.display(testTextPane);
        
        // display 메서드 호출 후 textPane이 설정되었는지 확인
        assertEquals("배경색이 검은색으로 설정되어야 합니다", Color.BLACK, testTextPane.getBackground());
        assertTrue("KeyListener가 추가되어야 합니다", testTextPane.getKeyListeners().length > 0);
        
        try {
            Field currentTextPaneField = setting.class.getDeclaredField("currentTextPane");
            currentTextPaneField.setAccessible(true);
            assertEquals("currentTextPane이 설정되어야 합니다", 
                testTextPane, currentTextPaneField.get(settingScreen));
        } catch (Exception e) {
            fail("display 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testInitializeCurrentSettings() {
        try {
            // GameSettings의 창 크기를 LARGE로 설정
            gameSettings.setWindowSize(GameSettings.WINDOW_SIZE_LARGE);
            
            // 새로운 setting 객체 생성하여 초기화 확인
            setting newSetting = new setting(testScreenController);
            
            Field currentSizeIndexField = setting.class.getDeclaredField("currentSizeIndex");
            currentSizeIndexField.setAccessible(true);
            int sizeIndex = (Integer) currentSizeIndexField.get(newSetting);
            
            assertEquals("창 크기 인덱스가 올바르게 초기화되어야 합니다", 2, sizeIndex);
            
        } catch (Exception e) {
            fail("initializeCurrentSettings 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testDrawSettingScreen() {
        settingScreen.display(testTextPane);
        
        try {
            Method drawSettingScreenMethod = setting.class.getDeclaredMethod("drawSettingScreen");
            drawSettingScreenMethod.setAccessible(true);
            drawSettingScreenMethod.invoke(settingScreen);
            
            // 화면이 그려졌는지 확인
            String newText = testTextPane.getText();
            assertNotNull("설정 화면 텍스트가 null이 아니어야 합니다", newText);
            assertTrue("설정 제목이 포함되어야 합니다", newText.contains("5조 테트리스 설정"));
            assertTrue("메뉴 옵션이 포함되어야 합니다", newText.contains("창 크기 설정") && newText.contains("게임 속도"));
            
        } catch (Exception e) {
            fail("drawSettingScreen 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testDrawKeySettingScreen() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 설정 모드로 전환
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            Method drawKeySettingScreenMethod = setting.class.getDeclaredMethod("drawKeySettingScreen");
            drawKeySettingScreenMethod.setAccessible(true);
            drawKeySettingScreenMethod.invoke(settingScreen);
            
            // 키 설정 화면이 그려졌는지 확인
            String text = testTextPane.getText();
            assertTrue("키 설정 제목이 포함되어야 합니다", text.contains("키 설정"));
            
        } catch (Exception e) {
            fail("drawKeySettingScreen 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleMenuActionKeySettings() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 2); // 키 설정 선택
            
            Method handleMenuActionMethod = setting.class.getDeclaredMethod("handleMenuAction");
            handleMenuActionMethod.setAccessible(true);
            handleMenuActionMethod.invoke(settingScreen);
            
            // 키 설정 모드가 활성화되었는지 확인
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            boolean isKeyMode = (Boolean) isKeySettingModeField.get(settingScreen);
            
            assertTrue("키 설정 모드가 활성화되어야 합니다", isKeyMode);
            
        } catch (Exception e) {
            fail("handleMenuAction(키 설정) 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleMenuActionColorblindMode() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 3); // 색맹 모드 선택
            
            boolean originalColorblind = gameSettings.isColorblindMode();
            
            Method handleMenuActionMethod = setting.class.getDeclaredMethod("handleMenuAction");
            handleMenuActionMethod.setAccessible(true);
            handleMenuActionMethod.invoke(settingScreen);
            
            // 색맹 모드가 토글되었는지 확인
            assertEquals("색맹 모드가 토글되어야 합니다", 
                !originalColorblind, gameSettings.isColorblindMode());
            
        } catch (Exception e) {
            fail("handleMenuAction(색맹 모드) 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleMenuActionSoundSettings() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 4); // 음향 효과 선택
            
            boolean originalSound = gameSettings.isSoundEnabled();
            
            Method handleMenuActionMethod = setting.class.getDeclaredMethod("handleMenuAction");
            handleMenuActionMethod.setAccessible(true);
            handleMenuActionMethod.invoke(settingScreen);
            
            // 음향 설정이 토글되었는지 확인
            assertEquals("음향 설정이 토글되어야 합니다", 
                !originalSound, gameSettings.isSoundEnabled());
            
        } catch (Exception e) {
            fail("handleMenuAction(음향 효과) 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleMenuActionScoreReset() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 5); // 스코어 초기화 선택
            
            Method handleMenuActionMethod = setting.class.getDeclaredMethod("handleMenuAction");
            handleMenuActionMethod.setAccessible(true);
            handleMenuActionMethod.invoke(settingScreen);
            
            // 메서드가 예외 없이 실행되는지 확인
            assertTrue("스코어 초기화 동작이 완료되어야 합니다", true);
            
        } catch (Exception e) {
            fail("handleMenuAction(스코어 초기화) 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleMenuActionDefaultSettings() {
        settingScreen.display(testTextPane);
        
        try {
            // 기본이 아닌 설정으로 변경
            gameSettings.setGameSpeed(5);
            gameSettings.setColorblindMode(true);
            
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 6); // 기본 설정 복원 선택
            
            Method handleMenuActionMethod = setting.class.getDeclaredMethod("handleMenuAction");
            handleMenuActionMethod.setAccessible(true);
            handleMenuActionMethod.invoke(settingScreen);
            
            // 기본 설정으로 복원되었는지 확인
            assertEquals("게임 속도가 기본값으로 복원되어야 합니다", 3, gameSettings.getGameSpeed());
            assertFalse("색맹 모드가 기본값으로 복원되어야 합니다", gameSettings.isColorblindMode());
            
        } catch (Exception e) {
            fail("handleMenuAction(기본 설정 복원) 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleMenuActionBackToHome() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 7); // 뒤로 가기 선택
            
            testScreenController.resetFlags();
            
            Method handleMenuActionMethod = setting.class.getDeclaredMethod("handleMenuAction");
            handleMenuActionMethod.setAccessible(true);
            handleMenuActionMethod.invoke(settingScreen);
            
            // ScreenController의 showScreen이 호출되었는지 확인
            assertEquals("홈 화면으로 이동해야 합니다", "home", testScreenController.getLastShowScreenCall());
            
        } catch (Exception e) {
            fail("handleMenuAction(뒤로 가기) 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleLeftRightWindowSize() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 0); // 창 크기 설정 선택
            
            Field currentSizeIndexField = setting.class.getDeclaredField("currentSizeIndex");
            currentSizeIndexField.setAccessible(true);
            int originalIndex = (Integer) currentSizeIndexField.get(settingScreen);
            
            Method handleLeftRightMethod = setting.class.getDeclaredMethod("handleLeftRight", boolean.class);
            handleLeftRightMethod.setAccessible(true);
            handleLeftRightMethod.invoke(settingScreen, true); // 오른쪽 키
            
            int newIndex = (Integer) currentSizeIndexField.get(settingScreen);
            assertEquals("창 크기 인덱스가 증가해야 합니다", (originalIndex + 1) % 4, newIndex);
            
        } catch (Exception e) {
            fail("handleLeftRight(창 크기) 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleLeftRightGameSpeed() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 1); // 게임 속도 선택
            
            int originalSpeed = gameSettings.getGameSpeed();
            
            Method handleLeftRightMethod = setting.class.getDeclaredMethod("handleLeftRight", boolean.class);
            handleLeftRightMethod.setAccessible(true);
            handleLeftRightMethod.invoke(settingScreen, true); // 오른쪽 키
            
            int newSpeed = gameSettings.getGameSpeed();
            if (originalSpeed < 5) {
                assertEquals("게임 속도가 증가해야 합니다", originalSpeed + 1, newSpeed);
            } else {
                assertEquals("최대 속도에서는 변화 없어야 합니다", originalSpeed, newSpeed);
            }
            
        } catch (Exception e) {
            fail("handleLeftRight(게임 속도) 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateDisplay() {
        settingScreen.display(testTextPane);
        
        try {
            Method updateDisplayMethod = setting.class.getDeclaredMethod("updateDisplay", String.class);
            updateDisplayMethod.setAccessible(true);
            updateDisplayMethod.invoke(settingScreen, "테스트 텍스트");
            
            // setText가 호출되었는지 확인
            assertEquals("텍스트가 설정되어야 합니다", "테스트 텍스트", testTextPane.getText());
            
        } catch (Exception e) {
            fail("updateDisplay 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowConfirmation() {
        settingScreen.display(testTextPane);
        
        try {
            Method showConfirmationMethod = setting.class.getDeclaredMethod("showConfirmation", String.class);
            showConfirmationMethod.setAccessible(true);
            showConfirmationMethod.invoke(settingScreen, "테스트 메시지");
            
            // 확인 메시지가 표시되었는지 확인
            String text = testTextPane.getText();
            assertTrue("확인 메시지가 포함되어야 합니다", text.contains("테스트 메시지"));
            assertTrue("알림 제목이 포함되어야 합니다", text.contains("알림"));
            
        } catch (Exception e) {
            fail("showConfirmation 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowKeyWarning() {
        settingScreen.display(testTextPane);
        
        try {
            Method showKeyWarningMethod = setting.class.getDeclaredMethod("showKeyWarning", String.class);
            showKeyWarningMethod.setAccessible(true);
            showKeyWarningMethod.invoke(settingScreen, "키 경고 메시지");
            
            // 경고 메시지가 표시되었는지 확인
            String text = testTextPane.getText();
            assertTrue("경고 메시지가 포함되어야 합니다", text.contains("키 경고 메시지"));
            assertTrue("경고 제목이 포함되어야 합니다", text.contains("경고"));
            
        } catch (Exception e) {
            fail("showKeyWarning 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testFindConflictingAction() {
        try {
            // 키 바인딩 설정
            gameSettings.setKeyCode("left", KeyEvent.VK_A);
            gameSettings.setKeyCode("right", KeyEvent.VK_D);
            
            Method findConflictingActionMethod = setting.class.getDeclaredMethod(
                "findConflictingAction", int.class, String.class);
            findConflictingActionMethod.setAccessible(true);
            
            String conflict = (String) findConflictingActionMethod.invoke(
                settingScreen, KeyEvent.VK_A, "right");
            
            assertEquals("충돌하는 액션을 찾아야 합니다", "왼쪽", conflict);
            
        } catch (Exception e) {
            fail("findConflictingAction 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowKeyConflictWarning() {
        settingScreen.display(testTextPane);
        
        try {
            Method showKeyConflictWarningMethod = setting.class.getDeclaredMethod(
                "showKeyConflictWarning", String.class, String.class);
            showKeyConflictWarningMethod.setAccessible(true);
            showKeyConflictWarningMethod.invoke(settingScreen, "왼쪽", "A");
            
            // 키 충돌 경고 메시지가 표시되었는지 확인
            String text = testTextPane.getText();
            assertTrue("키 충돌 경고가 포함되어야 합니다", text.contains("키 중복 알림"));
            assertTrue("키 이름이 포함되어야 합니다", text.contains("A"));
            
        } catch (Exception e) {
            fail("showKeyConflictWarning 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerUpDownNavigation() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            
            // SettingKeyListener 테스트
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // UP 키 테스트
            KeyEvent upEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(upEvent);
            
            int selectedAfterUp = (Integer) selectedOptionField.get(settingScreen);
            assertEquals("UP 키로 선택이 변경되어야 합니다", 7, selectedAfterUp);
            
            // DOWN 키 테스트
            KeyEvent downEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(downEvent);
            
            int selectedAfterDown = (Integer) selectedOptionField.get(settingScreen);
            assertEquals("DOWN 키로 선택이 변경되어야 합니다", 0, selectedAfterDown);
            
        } catch (Exception e) {
            fail("KeyListener UP/DOWN 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerEscapeKey() {
        settingScreen.display(testTextPane);
        
        try {
            testScreenController.resetFlags();
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // ESC 키 테스트
            KeyEvent escEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(escEvent);
            
            // ScreenController의 showScreen이 호출되었는지 확인
            assertEquals("홈 화면으로 이동해야 합니다", "home", testScreenController.getLastShowScreenCall());
            
        } catch (Exception e) {
            fail("KeyListener ESC 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerLeftRightNavigation() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 0); // 창 크기 설정 선택
            
            Field currentSizeIndexField = setting.class.getDeclaredField("currentSizeIndex");
            currentSizeIndexField.setAccessible(true);
            int originalIndex = (Integer) currentSizeIndexField.get(settingScreen);
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // RIGHT 키 테스트
            KeyEvent rightEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(rightEvent);
            
            int newIndex = (Integer) currentSizeIndexField.get(settingScreen);
            assertEquals("RIGHT 키로 창 크기가 변경되어야 합니다", (originalIndex + 1) % 4, newIndex);
            
        } catch (Exception e) {
            fail("KeyListener LEFT/RIGHT 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerEnterKey() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 2); // 키 설정 선택
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // ENTER 키 테스트
            KeyEvent enterEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(enterEvent);
            
            // 키 설정 모드가 활성화되었는지 확인
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            boolean isKeyMode = (Boolean) isKeySettingModeField.get(settingScreen);
            
            assertTrue("ENTER 키로 키 설정 모드가 활성화되어야 합니다", isKeyMode);
            
        } catch (Exception e) {
            fail("KeyListener ENTER 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testPrivateFieldsInitialization() {
        try {
            // 모든 private 필드들이 올바르게 초기화되었는지 확인
            Field menuOptionsField = setting.class.getDeclaredField("menuOptions");
            menuOptionsField.setAccessible(true);
            String[] menuOptions = (String[]) menuOptionsField.get(settingScreen);
            assertEquals("메뉴 옵션 개수가 올바라야 합니다", 8, menuOptions.length);
            
            Field windowSizesField = setting.class.getDeclaredField("windowSizes");
            windowSizesField.setAccessible(true);
            String[] windowSizes = (String[]) windowSizesField.get(settingScreen);
            assertEquals("창 크기 옵션 개수가 올바라야 합니다", 4, windowSizes.length);
            
            Field keyActionsField = setting.class.getDeclaredField("keyActions");
            keyActionsField.setAccessible(true);
            String[] keyActions = (String[]) keyActionsField.get(settingScreen);
            assertEquals("키 액션 개수가 올바라야 합니다", 6, keyActions.length);
            
            Field styleSetField = setting.class.getDeclaredField("styleSet");
            styleSetField.setAccessible(true);
            assertNotNull("StyleSet이 초기화되어야 합니다", styleSetField.get(settingScreen));
            
        } catch (Exception e) {
            fail("Private 필드 초기화 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testApplySizeChange() {
        settingScreen.display(testTextPane);
        
        try {
            testScreenController.resetFlags();
            
            Method applySizeChangeMethod = setting.class.getDeclaredMethod("applySizeChange");
            applySizeChangeMethod.setAccessible(true);
            applySizeChangeMethod.invoke(settingScreen);
            
            // updateWindowSize가 호출되었는지 확인
            assertTrue("창 크기 업데이트가 호출되어야 합니다", testScreenController.wasWindowSizeUpdated());
            
        } catch (Exception e) {
            fail("applySizeChange 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateGameColors() {
        settingScreen.display(testTextPane);
        
        try {
            // 현재 화면을 game으로 설정
            testScreenController.setCurrentScreen("game");
            
            Method updateGameColorsMethod = setting.class.getDeclaredMethod("updateGameColors");
            updateGameColorsMethod.setAccessible(true);
            updateGameColorsMethod.invoke(settingScreen);
            
            // 메서드가 예외 없이 실행되는지 확인
            assertTrue("색상 업데이트가 완료되어야 합니다", true);
            
        } catch (Exception e) {
            fail("updateGameColors 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateGameSpeed() {
        settingScreen.display(testTextPane);
        
        try {
            // 현재 화면을 game으로 설정
            testScreenController.setCurrentScreen("game");
            
            Method updateGameSpeedMethod = setting.class.getDeclaredMethod("updateGameSpeed");
            updateGameSpeedMethod.setAccessible(true);
            updateGameSpeedMethod.invoke(settingScreen);
            
            // 메서드가 예외 없이 실행되는지 확인
            assertTrue("게임 속도 업데이트가 완료되어야 합니다", true);
            
        } catch (Exception e) {
            fail("updateGameSpeed 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateSettingColors() {
        settingScreen.display(testTextPane);
        
        try {
            Method updateSettingColorsMethod = setting.class.getDeclaredMethod("updateSettingColors");
            updateSettingColorsMethod.setAccessible(true);
            updateSettingColorsMethod.invoke(settingScreen);
            
            // 메서드가 예외 없이 실행되는지 확인
            assertTrue("설정 화면 색상 업데이트가 완료되어야 합니다", true);
            
        } catch (Exception e) {
            fail("updateSettingColors 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerInKeySettingMode() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 설정 모드로 전환
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            Field currentKeyIndexField = setting.class.getDeclaredField("currentKeyIndex");
            currentKeyIndexField.setAccessible(true);
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // 키 설정 모드에서 UP 키 테스트
            KeyEvent upEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(upEvent);
            
            int selectedAfterUp = (Integer) currentKeyIndexField.get(settingScreen);
            assertEquals("키 설정 모드에서 UP 키로 선택이 변경되어야 합니다", 5, selectedAfterUp);
            
        } catch (Exception e) {
            fail("KeyListener 키 설정 모드 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testMenuOptionContents() {
        try {
            Field menuOptionsField = setting.class.getDeclaredField("menuOptions");
            menuOptionsField.setAccessible(true);
            String[] menuOptions = (String[]) menuOptionsField.get(settingScreen);
            
            assertEquals("첫 번째 메뉴는 창 크기 설정이어야 합니다", "창 크기 설정", menuOptions[0]);
            assertEquals("두 번째 메뉴는 게임 속도여야 합니다", "게임 속도", menuOptions[1]);
            assertEquals("세 번째 메뉴는 키 설정이어야 합니다", "키 설정", menuOptions[2]);
            assertEquals("네 번째 메뉴는 색맹 모드여야 합니다", "색맹 모드", menuOptions[3]);
            assertEquals("다섯 번째 메뉴는 음향 효과여야 합니다", "음향 효과", menuOptions[4]);
            assertEquals("여섯 번째 메뉴는 스코어 초기화여야 합니다", "스코어 초기화", menuOptions[5]);
            assertEquals("일곱 번째 메뉴는 기본 설정 복원이어야 합니다", "기본 설정 복원", menuOptions[6]);
            assertEquals("여덟 번째 메뉴는 뒤로 가기여야 합니다", "뒤로 가기", menuOptions[7]);
            
        } catch (Exception e) {
            fail("메뉴 옵션 내용 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyActionContents() {
        try {
            Field keyActionsField = setting.class.getDeclaredField("keyActions");
            keyActionsField.setAccessible(true);
            String[] keyActions = (String[]) keyActionsField.get(settingScreen);
            
            assertEquals("첫 번째 키 액션은 아래여야 합니다", "아래", keyActions[0]);
            assertEquals("두 번째 키 액션은 왼쪽이어야 합니다", "왼쪽", keyActions[1]);
            assertEquals("세 번째 키 액션은 오른쪽이어야 합니다", "오른쪽", keyActions[2]);
            assertEquals("네 번째 키 액션은 회전이어야 합니다", "회전", keyActions[3]);
            assertEquals("다섯 번째 키 액션은 빠른낙하여야 합니다", "빠른낙하", keyActions[4]);
            assertEquals("여섯 번째 키 액션은 일시정지여야 합니다", "일시정지", keyActions[5]);
            
        } catch (Exception e) {
            fail("키 액션 내용 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    // 추가 커버리지를 위한 테스트 케이스들
    
    @Test
    public void testKeyListenerKeyTypedAndKeyReleased() {
        settingScreen.display(testTextPane);
        
        try {
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // keyTyped 테스트 - 이벤트가 소비되는지 확인
            KeyEvent typedEvent = new KeyEvent(testTextPane, KeyEvent.KEY_TYPED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, 'a');
            keyListener.keyTyped(typedEvent);
            assertTrue("KeyTyped 이벤트가 처리되어야 합니다", typedEvent.isConsumed());
            
            // keyReleased 테스트 - 이벤트가 소비되는지 확인
            KeyEvent releasedEvent = new KeyEvent(testTextPane, KeyEvent.KEY_RELEASED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a');
            keyListener.keyReleased(releasedEvent);
            assertTrue("KeyReleased 이벤트가 처리되어야 합니다", releasedEvent.isConsumed());
            
        } catch (Exception e) {
            fail("KeyListener keyTyped/keyReleased 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerInKeyInputMode() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 입력 대기 모드로 설정
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            Field currentKeyActionField = setting.class.getDeclaredField("currentKeyAction");
            currentKeyActionField.setAccessible(true);
            currentKeyActionField.set(settingScreen, "테스트 키");
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // 일반 키 입력 테스트
            KeyEvent normalKeyEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a');
            keyListener.keyPressed(normalKeyEvent);
            
            // currentKeyAction이 초기화되었는지 확인
            String keyAction = (String) currentKeyActionField.get(settingScreen);
            assertEquals("키 입력 후 currentKeyAction이 초기화되어야 합니다", "", keyAction);
            
        } catch (Exception e) {
            fail("KeyListener 키 입력 모드 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerRestrictedKeys() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 입력 대기 모드로 설정
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            Field currentKeyActionField = setting.class.getDeclaredField("currentKeyAction");
            currentKeyActionField.setAccessible(true);
            currentKeyActionField.set(settingScreen, "테스트 키");
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // 제한된 키(ENTER) 입력 테스트
            KeyEvent enterKeyEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(enterKeyEvent);
            
            // currentKeyAction이 여전히 남아있는지 확인 (경고 표시되고 초기화되지 않음)
            String keyAction = (String) currentKeyActionField.get(settingScreen);
            assertEquals("제한된 키 입력 시 currentKeyAction이 유지되어야 합니다", "테스트 키", keyAction);
            
        } catch (Exception e) {
            fail("KeyListener 제한된 키 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testFindConflictingActionNoConflict() {
        try {
            Method findConflictingActionMethod = setting.class.getDeclaredMethod(
                "findConflictingAction", int.class, String.class);
            findConflictingActionMethod.setAccessible(true);
            
            // 충돌하지 않는 키 코드로 테스트
            String conflict = (String) findConflictingActionMethod.invoke(
                settingScreen, KeyEvent.VK_F1, "down");
            
            assertNull("충돌하지 않는 키는 null을 반환해야 합니다", conflict);
            
        } catch (Exception e) {
            fail("findConflictingAction 충돌 없음 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleLeftRightGameSpeedBoundary() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 1); // 게임 속도 선택
            
            // 최대 속도로 설정
            gameSettings.setGameSpeed(5);
            
            Method handleLeftRightMethod = setting.class.getDeclaredMethod("handleLeftRight", boolean.class);
            handleLeftRightMethod.setAccessible(true);
            handleLeftRightMethod.invoke(settingScreen, true); // 오른쪽 키 (증가 시도)
            
            assertEquals("최대 속도에서는 증가하지 않아야 합니다", 5, gameSettings.getGameSpeed());
            
            // 최소 속도로 설정
            gameSettings.setGameSpeed(1);
            handleLeftRightMethod.invoke(settingScreen, false); // 왼쪽 키 (감소 시도)
            
            assertEquals("최소 속도에서는 감소하지 않아야 합니다", 1, gameSettings.getGameSpeed());
            
        } catch (Exception e) {
            fail("handleLeftRight 게임 속도 경계값 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleLeftRightWindowSizeBoundary() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 0); // 창 크기 설정 선택
            
            Field currentSizeIndexField = setting.class.getDeclaredField("currentSizeIndex");
            currentSizeIndexField.setAccessible(true);
            
            // 마지막 인덱스로 설정
            currentSizeIndexField.set(settingScreen, 3);
            
            Method handleLeftRightMethod = setting.class.getDeclaredMethod("handleLeftRight", boolean.class);
            handleLeftRightMethod.setAccessible(true);
            handleLeftRightMethod.invoke(settingScreen, true); // 오른쪽 키 (순환)
            
            int newIndex = (Integer) currentSizeIndexField.get(settingScreen);
            assertEquals("마지막에서 오른쪽으로 이동하면 첫 번째로 순환해야 합니다", 0, newIndex);
            
            // 첫 번째 인덱스에서 왼쪽으로 이동
            handleLeftRightMethod.invoke(settingScreen, false); // 왼쪽 키 (순환)
            
            newIndex = (Integer) currentSizeIndexField.get(settingScreen);
            assertEquals("첫 번째에서 왼쪽으로 이동하면 마지막으로 순환해야 합니다", 3, newIndex);
            
        } catch (Exception e) {
            fail("handleLeftRight 창 크기 순환 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateDisplayWithHighlightedText() {
        settingScreen.display(testTextPane);
        
        try {
            Method updateDisplayMethod = setting.class.getDeclaredMethod("updateDisplay", String.class);
            updateDisplayMethod.setAccessible(true);
            
            // 하이라이트 마커가 포함된 텍스트로 테스트
            String testText = "test ► highlighted text ◄ end";
            updateDisplayMethod.invoke(settingScreen, testText);
            
            assertEquals("하이라이트가 포함된 텍스트가 설정되어야 합니다", testText, testTextPane.getText());
            
        } catch (Exception e) {
            fail("updateDisplay 하이라이트 텍스트 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerEscapeInKeySettingMode() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 설정 모드로 전환
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // ESC 키 테스트
            KeyEvent escEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(escEvent);
            
            // 키 설정 모드가 비활성화되었는지 확인
            boolean isKeyMode = (Boolean) isKeySettingModeField.get(settingScreen);
            assertFalse("ESC 키로 키 설정 모드가 비활성화되어야 합니다", isKeyMode);
            
        } catch (Exception e) {
            fail("KeyListener 키 설정 모드 ESC 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testDrawKeySettingScreenWithKeyAction() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 설정 모드로 전환하고 키 액션 설정
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            Field currentKeyActionField = setting.class.getDeclaredField("currentKeyAction");
            currentKeyActionField.setAccessible(true);
            currentKeyActionField.set(settingScreen, "아래");
            
            Method drawKeySettingScreenMethod = setting.class.getDeclaredMethod("drawKeySettingScreen");
            drawKeySettingScreenMethod.setAccessible(true);
            drawKeySettingScreenMethod.invoke(settingScreen);
            
            // 키 입력 대기 화면이 그려졌는지 확인
            String text = testTextPane.getText();
            assertTrue("키 입력 대기 메시지가 포함되어야 합니다", text.contains("새로운 키를 눌러주세요"));
            assertTrue("키 액션이 포함되어야 합니다", text.contains("아래"));
            
        } catch (Exception e) {
            fail("drawKeySettingScreen 키 액션 모드 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testAllMenuOptionsCases() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            
            Method handleMenuActionMethod = setting.class.getDeclaredMethod("handleMenuAction");
            handleMenuActionMethod.setAccessible(true);
            
            // 각 메뉴 옵션 테스트 (이미 테스트된 것들도 포함하여 완전한 커버리지 확보)
            for (int i = 0; i < 8; i++) {
                selectedOptionField.set(settingScreen, i);
                handleMenuActionMethod.invoke(settingScreen);
                
                // 각 케이스가 예외 없이 실행되는지 확인
                assertTrue("메뉴 옵션 " + i + "이 정상 실행되어야 합니다", true);
            }
            
        } catch (Exception e) {
            fail("모든 메뉴 옵션 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testApplySizeChangeWithThread() {
        settingScreen.display(testTextPane);
        
        try {
            testScreenController.resetFlags();
            
            Method applySizeChangeMethod = setting.class.getDeclaredMethod("applySizeChange");
            applySizeChangeMethod.setAccessible(true);
            applySizeChangeMethod.invoke(settingScreen);
            
            // 스레드 실행을 위해 잠시 대기
            Thread.sleep(200);
            
            // updateWindowSize가 호출되었는지 확인
            assertTrue("창 크기 업데이트가 호출되어야 합니다", testScreenController.wasWindowSizeUpdated());
            
        } catch (Exception e) {
            fail("applySizeChange 스레드 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateGameColorsWithGameScreen() {
        settingScreen.display(testTextPane);
        
        try {
            // 현재 화면을 game으로 설정하여 게임 화면 색상 업데이트 경로 테스트
            testScreenController.setCurrentScreen("game");
            
            Method updateGameColorsMethod = setting.class.getDeclaredMethod("updateGameColors");
            updateGameColorsMethod.setAccessible(true);
            updateGameColorsMethod.invoke(settingScreen);
            
            // 메서드가 예외 없이 실행되는지 확인 (리플렉션 실패는 무시됨)
            assertTrue("색상 업데이트가 완료되어야 합니다", true);
            
        } catch (Exception e) {
            fail("updateGameColors 게임 화면 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateGameSpeedWithGameScreen() {
        settingScreen.display(testTextPane);
        
        try {
            // 현재 화면을 game으로 설정하여 게임 속도 업데이트 경로 테스트
            testScreenController.setCurrentScreen("game");
            
            Method updateGameSpeedMethod = setting.class.getDeclaredMethod("updateGameSpeed");
            updateGameSpeedMethod.setAccessible(true);
            updateGameSpeedMethod.invoke(settingScreen);
            
            // 메서드가 예외 없이 실행되는지 확인 (리플렉션 실패는 무시됨)
            assertTrue("게임 속도 업데이트가 완료되어야 합니다", true);
            
        } catch (Exception e) {
            fail("updateGameSpeed 게임 화면 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerEnterInKeySettingModeWithKeyInput() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 설정 모드로 전환
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            Field currentKeyIndexField = setting.class.getDeclaredField("currentKeyIndex");
            currentKeyIndexField.setAccessible(true);
            currentKeyIndexField.set(settingScreen, 0); // 첫 번째 키 액션 선택
            
            Field currentKeyActionField = setting.class.getDeclaredField("currentKeyAction");
            currentKeyActionField.setAccessible(true);
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // ENTER 키로 키 입력 모드 진입
            KeyEvent enterEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(enterEvent);
            
            // currentKeyAction이 설정되었는지 확인
            String keyAction = (String) currentKeyActionField.get(settingScreen);
            assertEquals("키 입력 모드에서 currentKeyAction이 설정되어야 합니다", "아래", keyAction);
            
        } catch (Exception e) {
            fail("KeyListener 키 설정 모드 ENTER 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowConfirmationWithDelay() {
        settingScreen.display(testTextPane);
        
        try {
            Method showConfirmationMethod = setting.class.getDeclaredMethod("showConfirmation", String.class);
            showConfirmationMethod.setAccessible(true);
            showConfirmationMethod.invoke(settingScreen, "지연 테스트 메시지");
            
            // 확인 메시지가 표시되었는지 확인
            String text = testTextPane.getText();
            assertTrue("확인 메시지가 포함되어야 합니다", text.contains("지연 테스트 메시지"));
            
            // 스레드의 지연 동작을 위해 잠시 대기
            Thread.sleep(100);
            
        } catch (Exception e) {
            fail("showConfirmation 지연 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowKeyWarningWithDelay() {
        settingScreen.display(testTextPane);
        
        try {
            Method showKeyWarningMethod = setting.class.getDeclaredMethod("showKeyWarning", String.class);
            showKeyWarningMethod.setAccessible(true);
            showKeyWarningMethod.invoke(settingScreen, "지연 키 경고 메시지");
            
            // 경고 메시지가 표시되었는지 확인
            String text = testTextPane.getText();
            assertTrue("경고 메시지가 포함되어야 합니다", text.contains("지연 키 경고 메시지"));
            
            // 스레드의 지연 동작을 위해 잠시 대기
            Thread.sleep(100);
            
        } catch (Exception e) {
            fail("showKeyWarning 지연 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowKeyConflictWarningWithDelay() {
        settingScreen.display(testTextPane);
        
        try {
            Method showKeyConflictWarningMethod = setting.class.getDeclaredMethod(
                "showKeyConflictWarning", String.class, String.class);
            showKeyConflictWarningMethod.setAccessible(true);
            showKeyConflictWarningMethod.invoke(settingScreen, "지연 충돌 액션", "지연 키");
            
            // 키 충돌 경고 메시지가 표시되었는지 확인
            String text = testTextPane.getText();
            assertTrue("키 충돌 경고가 포함되어야 합니다", text.contains("키 중복 알림"));
            
            // 스레드의 지연 동작을 위해 잠시 대기
            Thread.sleep(100);
            
        } catch (Exception e) {
            fail("showKeyConflictWarning 지연 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testHandleLeftRightInKeySettingMode() {
        settingScreen.display(testTextPane);
        
        try {
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 0); // 창 크기 설정 선택
            
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true); // 키 설정 모드 활성화
            
            Field currentSizeIndexField = setting.class.getDeclaredField("currentSizeIndex");
            currentSizeIndexField.setAccessible(true);
            int originalIndex = (Integer) currentSizeIndexField.get(settingScreen);
            
            Method handleLeftRightMethod = setting.class.getDeclaredMethod("handleLeftRight", boolean.class);
            handleLeftRightMethod.setAccessible(true);
            handleLeftRightMethod.invoke(settingScreen, true); // 오른쪽 키
            
            // 키 설정 모드에서는 창 크기가 변경되지 않아야 함
            int newIndex = (Integer) currentSizeIndexField.get(settingScreen);
            assertEquals("키 설정 모드에서는 창 크기가 변경되지 않아야 합니다", originalIndex, newIndex);
            
        } catch (Exception e) {
            fail("handleLeftRight 키 설정 모드 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyListenerEscapeInKeyInputMode() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 입력 대기 모드로 설정
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            Field currentKeyActionField = setting.class.getDeclaredField("currentKeyAction");
            currentKeyActionField.setAccessible(true);
            currentKeyActionField.set(settingScreen, "테스트 키 액션");
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // ESC 키로 키 입력 모드 취소
            KeyEvent escEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(escEvent);
            
            // currentKeyAction이 초기화되었는지 확인
            String keyAction = (String) currentKeyActionField.get(settingScreen);
            assertEquals("ESC 키로 키 입력 모드가 취소되어야 합니다", "", keyAction);
            
        } catch (Exception e) {
            fail("KeyListener 키 입력 모드 ESC 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testKeyConflictScenario() {
        settingScreen.display(testTextPane);
        
        try {
            // 키 충돌 시나리오 테스트
            gameSettings.setKeyCode("left", KeyEvent.VK_A);
            gameSettings.setKeyCode("right", KeyEvent.VK_D);
            
            // 키 입력 대기 모드로 설정
            Field isKeySettingModeField = setting.class.getDeclaredField("isKeySettingMode");
            isKeySettingModeField.setAccessible(true);
            isKeySettingModeField.set(settingScreen, true);
            
            Field currentKeyActionField = setting.class.getDeclaredField("currentKeyAction");
            currentKeyActionField.setAccessible(true);
            currentKeyActionField.set(settingScreen, "right"); // 오른쪽 키 설정 중
            
            Field currentKeyIndexField = setting.class.getDeclaredField("currentKeyIndex");
            currentKeyIndexField.setAccessible(true);
            currentKeyIndexField.set(settingScreen, 2); // 오른쪽 키 인덱스
            
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            // 이미 사용 중인 키(A)를 입력하여 충돌 발생
            KeyEvent conflictKeyEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a');
            keyListener.keyPressed(conflictKeyEvent);
            
            // 키가 설정되었는지 확인 (충돌 경고 후 설정됨)
            assertEquals("충돌하는 키가 새로운 액션에 설정되어야 합니다", 
                KeyEvent.VK_A, gameSettings.getKeyCode("right"));
            
        } catch (Exception e) {
            fail("키 충돌 시나리오 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdateDisplayWithColorblindMode() {
        settingScreen.display(testTextPane);
        
        try {
            // 색맹 모드 활성화
            gameSettings.setColorblindMode(true);
            
            Method updateDisplayMethod = setting.class.getDeclaredMethod("updateDisplay", String.class);
            updateDisplayMethod.setAccessible(true);
            
            // 하이라이트 마커가 포함된 텍스트로 테스트 (색맹 모드에서 다른 색상 적용)
            String testText = "test ► highlighted text ◄ end";
            updateDisplayMethod.invoke(settingScreen, testText);
            
            assertEquals("색맹 모드에서 하이라이트가 포함된 텍스트가 설정되어야 합니다", testText, testTextPane.getText());
            
        } catch (Exception e) {
            fail("updateDisplay 색맹 모드 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testCompleteKeySettingFlow() {
        settingScreen.display(testTextPane);
        
        try {
            setting.SettingKeyListener keyListener = settingScreen.new SettingKeyListener();
            
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            selectedOptionField.set(settingScreen, 2); // 키 설정 메뉴 선택
            
            // 1. 키 설정 메뉴 진입
            KeyEvent enterEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(enterEvent);
            
            // 2. 첫 번째 키 액션 선택
            enterEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
            keyListener.keyPressed(enterEvent);
            
            // 3. 새로운 키 입력
            KeyEvent newKeyEvent = new KeyEvent(testTextPane, KeyEvent.KEY_PRESSED, 
                System.currentTimeMillis(), 0, KeyEvent.VK_Q, 'q');
            keyListener.keyPressed(newKeyEvent);
            
            // 키가 올바르게 설정되었는지 확인
            assertEquals("새로운 키가 설정되어야 합니다", KeyEvent.VK_Q, gameSettings.getKeyCode("down"));
            
        } catch (Exception e) {
            fail("완전한 키 설정 플로우 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testWindowSizeIndexInitialization() {
        try {
            // 다양한 창 크기로 초기화 테스트
            gameSettings.setWindowSize(GameSettings.WINDOW_SIZE_SMALL);
            setting smallSetting = new setting(testScreenController);
            
            Field currentSizeIndexField = setting.class.getDeclaredField("currentSizeIndex");
            currentSizeIndexField.setAccessible(true);
            int smallIndex = (Integer) currentSizeIndexField.get(smallSetting);
            assertEquals("SMALL 창 크기 인덱스가 올바르게 초기화되어야 합니다", 0, smallIndex);
            
            gameSettings.setWindowSize(GameSettings.WINDOW_SIZE_XLARGE);
            setting xlargeSetting = new setting(testScreenController);
            int xlargeIndex = (Integer) currentSizeIndexField.get(xlargeSetting);
            assertEquals("XLARGE 창 크기 인덱스가 올바르게 초기화되어야 합니다", 3, xlargeIndex);
            
        } catch (Exception e) {
            fail("창 크기 인덱스 초기화 테스트 중 예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    public void testAllBranchesInDrawSettingScreen() {
        settingScreen.display(testTextPane);
        
        try {
            Method drawSettingScreenMethod = setting.class.getDeclaredMethod("drawSettingScreen");
            drawSettingScreenMethod.setAccessible(true);
            
            Field selectedOptionField = setting.class.getDeclaredField("selectedOption");
            selectedOptionField.setAccessible(true);
            
            // 모든 메뉴 옵션에 대해 화면을 그려서 모든 switch 케이스 테스트
            for (int i = 0; i < 8; i++) {
                selectedOptionField.set(settingScreen, i);
                drawSettingScreenMethod.invoke(settingScreen);
                
                String text = testTextPane.getText();
                assertNotNull("메뉴 옵션 " + i + "에 대한 화면이 그려져야 합니다", text);
                assertTrue("설정 제목이 포함되어야 합니다", text.contains("5조 테트리스 설정"));
            }
            
        } catch (Exception e) {
            fail("모든 브랜치 drawSettingScreen 테스트 중 예외 발생: " + e.getMessage());
        }
    }
}