package se.tetris.team5.items;

import se.tetris.team5.blocks.Block;

public interface ItemGrantPolicy {
  /**
   * 아이템을 부여할지 여부와 위치, 아이템 종류를 결정하여 블록에 부여한다.
   * 
   * @param block   아이템을 부여할 블록
   * @param context 정책에 필요한 추가 정보(예: 누적 삭제 줄 수 등)
   * @return 부여된 아이템 (부여되지 않았으면 null)
   */
  Item grantItem(Block block, ItemGrantContext context);

  /**
   * 정책에 필요한 컨텍스트 정보 전달용 내부 클래스
   */
  class ItemGrantContext {
    public int totalClearedLines;
    public ItemFactory itemFactory;

    public ItemGrantContext(int totalClearedLines, ItemFactory itemFactory) {
      this.totalClearedLines = totalClearedLines;
      this.itemFactory = itemFactory;
    }
  }
}
