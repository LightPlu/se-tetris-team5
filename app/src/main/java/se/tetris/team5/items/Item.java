package se.tetris.team5.items;

/**
 * 아이템의 기본 인터페이스. 다양한 아이템 효과를 위해 확장 가능.
 */
public interface Item {
  /**
   * 아이템 이름 반환
   */
  String getName();

  /**
   * 아이템 효과 적용 (예: 게임 컨트롤러나 플레이어에 적용)
   */
  void applyEffect(Object target);
}
