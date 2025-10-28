package se.tetris.team5.items;

public class BombItem implements Item {

    @Override
    public String getName() {
        return "BombItem";
    }

    @Override
    public void applyEffect(Object target) {
        // 폭탄 아이템의 효과는 BoardManager에서 처리
    }

    @Override
    public String toString() {
        return "B";
    }
    
}
