
package se.tetris.team5.items;

/**
 * 줄삭제(Line clear) 아이템. 블럭 내에 포함되어 해당 줄이 꽉 차지 않아도 블럭 고정 시 해당 줄을 즉시 삭제.
 */
public class LineClearItem implements Item {
    @Override
    public String getName() {
        return "LineClearItem";
    }

    @Override
    public void applyEffect(Object target) {
        // 실제 효과는 BoardManager에서 처리
    }

    @Override
    public String toString() {
        return "L";
    }
}


