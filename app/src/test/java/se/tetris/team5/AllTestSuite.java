package se.tetris.team5;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.tetris.team5.blocks.BlockTest;
import se.tetris.team5.screens.GameTest;
import se.tetris.team5.screens.GameEdgeCaseTest;
import se.tetris.team5.utils.score.ScoreManagerTest;

/**
 * 모든 테스트를 한꺼번에 실행하는 테스트 스위트
 */
@RunWith(Suite.class)
@SuiteClasses({
    // 블록 관련 테스트
    BlockTest.class,
    
    // 게임 로직 테스트
    GameTest.class,
    GameEdgeCaseTest.class,
    
    // 유틸리티 테스트
    ScoreManagerTest.class,
    
    // 통합 테스트
    IntegrationTest.class
})
public class AllTestSuite {
    // 이 클래스는 테스트 스위트 실행을 위한 것으로 내용이 비어있어도 됩니다.
}