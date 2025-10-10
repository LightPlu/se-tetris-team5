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
                return "<html><center><h3>🎮 TETRIS 🎮</h3><h5>Team 5 - SMALL</h5></center></html>";
            case MEDIUM:
                return "<html><center><h2>🎮 TETRIS GAME 🎮</h2><h4>Team 5 - MEDIUM</h4></center></html>";
            case LARGE:
                return "<html><center><h1>🎮 TETRIS GAME 🎮</h1><h3>Team 5 - LARGE</h3><p>클래식 퍼즐 게임</p></center></html>";
            case XLARGE:
                return "<html><center><h1>🎮 TETRIS GAME 🎮</h1><h2>Team 5 - XLARGE</h2><p>클래식 퍼즐 게임</p><small>블록을 맞춰 라인을 완성하세요!</small></center></html>";
            default:
                return "<html><center><h1>🎮 TETRIS GAME 🎮</h1><h2>Team 5 - DEFAULT</h2></center></html>";
        }
    }
    
    /**
     * 창 크기에 따른 제목 폰트 크기를 반환합니다
     */
    public int getTitleFontSize() {
        switch (currentWindowSize) {
            case SMALL: return 16;
            case MEDIUM: return 20;
            case LARGE: return 24;
            case XLARGE: return 28;
            default: return 20;
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