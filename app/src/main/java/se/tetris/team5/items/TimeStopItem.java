package se.tetris.team5.items;

/**
 * 타임스톱(Time Stop) 아이템. 블럭 내에 포함되어 줄 삭제 시 획득하면
 * Shift 키를 눌러 게임을 5초간 일시정지할 수 있는 기회를 1회 부여.
 */
public class TimeStopItem implements Item {
    @Override
    public String getName() {
        return "TimeStopItem";
    }

    @Override
    public void applyEffect(Object target) {
        // 실제 효과는 GameEngine과 game 화면에서 처리
    }

    @Override
    public String toString() {
        return "T";
    }
}
