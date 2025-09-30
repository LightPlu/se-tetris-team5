package se.tetris.team5.component.home;

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
     * 창 크기에 따른 제목을 그립니다
     */
    public String drawTitle() {
        StringBuilder sb = new StringBuilder();
        
        switch (currentWindowSize) {
            case SMALL:
                drawSmallTitle(sb);
                break;
            case MEDIUM:
                drawMediumTitle(sb);
                break;
            case LARGE:
                drawLargeTitle(sb);
                break;
            case XLARGE:
                drawXLargeTitle(sb);
                break;
        }
        
        return sb.toString();
    }
    
    private void drawSmallTitle(StringBuilder sb) {
        String[] lines = {
            "",
            "┌────────────────────────┐",
            "│ ★ T E T R I S ★ │",
            "├────────────────────────┤",
            "│                        │",
            "│   🎮 5조 테트리스 🎮   │",
            "│                        │",
            "└────────────────────────┘",
            ""
        };
        
        for (String line : lines) {
            sb.append(line).append("\n");
        }
    }
    

    private void drawMediumTitle(StringBuilder sb) {
        String[] lines = {
            "",
            "╔═══════════════════════════════════╗",
            "║     ★ T E T R I S ★     ║",
            "╠═══════════════════════════════════╣",
            "║                                   ║",
            "║   ◆ 클래식 퍼즐 게임 ◆   ║",
            "║                                   ║",
            "║     🎮 5조 테트리스 🎮     ║",
            "║                                   ║",
            "╚═══════════════════════════════════╝",
            ""
        };
        
        for (String line : lines) {
            sb.append(line).append("\n");
        }
    }
    
    private void drawLargeTitle(StringBuilder sb) {
        String[] lines = {
            "",
            "╔═══════════════════════════════════════════╗",
            "║        ★ T E T R I S ★        ║",
            "╠═══════════════════════════════════════════╣",
            "║                                           ║",
            "║      ◆ 클래식 퍼즐 게임 ◆      ║",
            "║       블록을 맞춰 라인을 완성!       ║",
            "║                                           ║",
            "║        🎮 5조 테트리스 🎮        ║",
            "║                                           ║",
            "╠═══════════════════════════════════════════╣",
            "║             ♦ 즐거운 게임! ♦             ║",
            "╚═══════════════════════════════════════════╝",
            ""
        };
        
        for (String line : lines) {
            sb.append(line).append("\n");
        }
    }
    
    private void drawXLargeTitle(StringBuilder sb) {
        String[] lines = {
            "",
            "",
            "╔═════════════════════════════════════════════════╗",
            "║                 ★ T E T R I S ★                  ║",
            "╠═════════════════════════════════════════════════╣",
            "║                                                 ║",
            "║           ◆ 클래식 퍼즐 게임 ◆                 ║",
            "║          블록을 맞춰 라인을 완성!                ║",
            "║           스킬과 전략이 필요한 게임!             ║",
            "║                                                 ║",
            "║            🎮 5조 테트리스 🎮                   ║",
            "║                                                 ║",
            "╠═════════════════════════════════════════════════╣",
            "║             ♦ 도전하고 즐기세요! ♦               ║",
            "║           ♥ 최고 점수를 노려보세요! ♥            ║",
            "╚═════════════════════════════════════════════════╝",
            "",
            ""
        };
        
        for (String line : lines) {
            sb.append(line).append("\n");
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