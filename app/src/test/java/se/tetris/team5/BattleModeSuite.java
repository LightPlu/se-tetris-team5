package se.tetris.team5;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 대전 모드 전용 테스트 스위트
 * 대전 모드 관련 모든 테스트를 한 번에 실행
 * 
 * 실행 방법:
 * ./gradlew test --tests "se.tetris.team5.BattleModeSuite"
 */
@RunWith(Suite.class)
@SuiteClasses({
    se.tetris.team5.screens.BattleModeTest.class,
    se.tetris.team5.components.battle.PlayerGamePanelTest.class,
    se.tetris.team5.gamelogic.battle.BattleGameControllerTest.class
})
public class BattleModeSuite {
  // 테스트 스위트 - 본문 필요 없음
}
