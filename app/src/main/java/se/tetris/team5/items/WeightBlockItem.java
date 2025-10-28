package se.tetris.team5.items;

/**
 * 무게추 블록 아이템
 * 10줄 삭제 시 일반 블록 대신 무게추 블록(WBlock)을 생성하는 아이템
 */
public class WeightBlockItem implements Item {
    
    @Override
    public String getName() {
        return "WeightBlockItem";
    }

    @Override
    public void applyEffect(Object target) {
        // 무게추 블록은 블록 자체가 특수하므로 별도 효과 적용 없음
        // GameEngine에서 이 아이템 타입을 확인하여 WBlock을 생성
    }

    @Override
    public String toString() {
        return "W";
    }
}
