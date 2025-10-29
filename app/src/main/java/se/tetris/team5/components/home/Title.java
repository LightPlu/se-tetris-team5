package se.tetris.team5.components.home;

/**
 * 홈 화면의 제목을 담당하는 컴포넌트 클래스
 * 창 크기에 따라 다른 제목 디자인을 제공합니다.
 */
public class Title {
    
    // 창 크기별 레이아웃 설정
    public enum WindowSize {
        SMALL, MEDIUM, LARGE, XLARGE
    }
    
    private WindowSize currentWindowSize;
    
    public Title(WindowSize windowSize) {
        this.currentWindowSize = windowSize;
    }
    

    
    /**
     * GUI용 HTML 제목을 반환합니다
     */
    public String getGUITitle() {
        switch (currentWindowSize) {
            case SMALL:
                return "<html><center><div style='margin: 30px 0; width: 100%;'><h1 style='font-family: Impact, Arial Black, sans-serif; color: #FF4500; text-shadow: 2px 2px 4px #000000; font-size: 24px;'>⚡ CHAINSAW TETRIS ⚡</h1></div></center></html>";
            case MEDIUM:
                return "<html><center><div style='margin: 35px 0; width: 100%;'><h1 style='font-family: Impact, Arial Black, sans-serif; color: #FF4500; text-shadow: 3px 3px 5px #000000; font-size: 30px;'>⚡ CHAINSAW TETRIS ⚡</h1></div></center></html>";
            case LARGE:
                return "<html><center><div style='margin: 40px 0; width: 100%;'><h1 style='font-family: Impact, Arial Black, sans-serif; color: #FF4500; text-shadow: 4px 4px 7px #000000; font-size: 36px;'>⚡ CHAINSAW TETRIS ⚡</h1></div></center></html>";
            case XLARGE:
                return "<html><center><div style='margin: 45px 0; width: 100%;'><h1 style='font-family: Impact, Arial Black, sans-serif; color: #FF4500; text-shadow: 5px 5px 10px #000000; font-size: 44px;'>⚡ CHAINSAW TETRIS ⚡</h1></div></center></html>";
            default:
                return "<html><center><div style='margin: 45px 0; width: 100%;'><h1 style='font-family: Impact, Arial Black, sans-serif; color: #FF4500; text-shadow: 4px 4px 7px #000000; font-size: 36px;'>⚡ CHAINSAW TETRIS ⚡</h1></div></center></html>";
        }
    }
    
    /**
     * 창 크기에 따른 제목 폰트 크기를 반환합니다
     */
    public int getTitleFontSize() {
        switch (currentWindowSize) {
            case SMALL: return 24;
            case MEDIUM: return 30;
            case LARGE: return 36;
            case XLARGE: return 44;
            default: return 36;
        }
    }

    
    /**
     * 창 크기에 따른 레이아웃 모드를 결정합니다
     */
    public static WindowSize determineWindowSize(int width, int height) {
        if (width <= 350) {
            return WindowSize.SMALL;
        } else if (width <= 450) {
            return WindowSize.MEDIUM;
        } else if (width <= 550) {
            return WindowSize.LARGE;
        } else {
            return WindowSize.XLARGE;
        }
    }
    
    /**
     * 현재 창 크기 설정을 반환합니다
     */
    public WindowSize getCurrentWindowSize() {
        return currentWindowSize;
    }
    
    /**
     * 창 크기 설정을 변경합니다
     */
    public void setWindowSize(WindowSize windowSize) {
        this.currentWindowSize = windowSize;
    }
}