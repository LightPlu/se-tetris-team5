package se.tetris.team5.gamelogic.ai;

import se.tetris.team5.gamelogic.GameEngine;
import se.tetris.team5.gamelogic.GameMode;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.awt.Color;

/**
 * AI의 적합도를 평가하는 클래스
 * AI끼리 대전 모드로 평가하여 실제 사용 환경과 동일하게 테스트
 */
public class FitnessEvaluator {
  // 빠른 학습을 위한 타이머 설정 (게임 로직에는 영향 없음)
  // 실제 대전 모드: gameTimer = 800ms, AI_ACTION_INTERVAL = 200ms
  // 학습 환경: 더 빠른 타이머로 학습 속도 향상 (게임 로직은 동일)
  private static final int GAME_TIMER_INTERVAL = 50; // 블록 자동 낙하 간격 (더 빠른 학습용: 100ms -> 50ms)
  private static final int AI_ACTION_INTERVAL = 20; // AI 행동 주기 (더 빠른 학습용: 50ms -> 20ms)

  // 학습 시간 단축을 위한 설정
  // 실제 5분 = 평가 환경 19초 (실제 800ms 간격 = 평가 50ms 간격, 비율 16:1)
  // 실제 5분 = 300초 = 300,000ms, 블록 낙하 375회
  // 평가 환경: 375회 × 50ms = 18,750ms ≈ 19초
  private static final int GAMES_PER_INDIVIDUAL = 5; // 개체당 플레이할 게임 수
  private static final int MAX_MOVES_PER_GAME = 2000; // 게임당 최대 이동 횟수
  private static final int MAX_GAME_TIME_MS = 20000; // 게임당 최대 시간 (실제 5분 = 평가 19초, 여유있게 20초)

  // 빠른 평가 모드 설정 (최대한 빠른 학습 - 최적화됨)
  // 실제 5분 = 평가 환경 8초 (실제 800ms 간격 = 평가 20ms 간격, 비율 40:1)
  // 평가 환경: 375회 × 20ms = 7,500ms ≈ 8초
  private static final int QUICK_GAMES_PER_INDIVIDUAL = 5; // 빠른 평가: 개체당 게임 수
  private static final int QUICK_MAX_MOVES = 1000; // 빠른 평가: 최대 이동 횟수
  private static final int QUICK_GAME_TIMER_INTERVAL = 20; // 빠른 평가: 게임 타이머 간격 (더 빠름: 30ms -> 20ms)
  private static final int QUICK_AI_INTERVAL = 5; // 빠른 평가: AI 행동 주기 (더 빠름: 10ms -> 5ms)
  private static final int QUICK_MAX_TIME = 10000; // 빠른 평가: 최대 대기 시간 (실제 5분 = 평가 8초, 여유있게 10초)

  // 이전 세대 최고 개체 (상대 AI로 사용)
  private Individual previousBestOpponent = null;

  /**
   * 상대 AI 설정 (이전 세대 최고 개체)
   */
  public void setOpponent(Individual opponent) {
    this.previousBestOpponent = opponent;
  }

  /**
   * 개체의 적합도를 평가 (AI 대전 모드)
   * 평가 대상 AI와 이전 세대 최고 개체(또는 기본 가중치)가 대전하여 승리/패배를 기반으로 적합도 계산
   * 
   * @param individual 평가할 개체
   * @return 적합도 점수
   */
  public double evaluate(Individual individual) {
    individual.resetFitness();

    // 상대 AI는 이전 세대 최고 개체 또는 기본 가중치 사용
    WeightSet opponentWeights;
    if (previousBestOpponent != null) {
      opponentWeights = previousBestOpponent.weights;
    } else {
      opponentWeights = new WeightSet();
    }

    for (int game = 0; game < GAMES_PER_INDIVIDUAL; game++) {
      // 두 개의 게임 엔진 생성 (AI 대전 모드)
      GameEngine engine1 = new GameEngine(20, 10, false);
      GameEngine engine2 = new GameEngine(20, 10, false);

      engine1.setGameMode(GameMode.NORMAL);
      engine2.setGameMode(GameMode.NORMAL);

      engine1.startNewGame();
      engine2.startNewGame();

      // 평가 대상 AI와 상대 AI 생성
      TetrisAI ai1 = new TetrisAI(engine1);
      TetrisAI ai2 = new TetrisAI(engine2);

      ai1.setWeights(individual.weights); // 평가 대상
      ai2.setWeights(opponentWeights); // 상대 (기본 가중치)

      // 공격 로직: 블록 고정 후 콜백으로 처리
      engine1.setOnBlockFixedCallback(() -> handleAttack(engine1, engine2));
      engine2.setOnBlockFixedCallback(() -> handleAttack(engine2, engine1));

      final int[] moves = { 0 };
      final boolean[] gameFinished = { false };
      final int[] winner = { 0 }; // 0: 무승부, 1: ai1 승리, 2: ai2 승리

      // 게임 진행 타이머 (실제 대전 모드와 동일한 간격)
      Timer gameTimer = new Timer(GAME_TIMER_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!engine1.isGameOver() && !engine2.isGameOver()) {
            engine1.moveBlockDown();
            engine2.moveBlockDown();
          } else {
            // 게임 오버 체크
            if (engine1.isGameOver() && !engine2.isGameOver()) {
              winner[0] = 2; // ai2 승리
            } else if (!engine1.isGameOver() && engine2.isGameOver()) {
              winner[0] = 1; // ai1 승리
            } else {
              winner[0] = 0; // 동시 게임 오버 (무승부)
            }
            gameFinished[0] = true;
          }
        }
      });

      // AI1 행동 타이머
      Timer ai1Timer = new Timer(AI_ACTION_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!engine1.isGameOver() && moves[0] < MAX_MOVES_PER_GAME) {
            ai1.makeMove();
          }
        }
      });

      // AI2 행동 타이머
      Timer ai2Timer = new Timer(AI_ACTION_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!engine2.isGameOver() && moves[0] < MAX_MOVES_PER_GAME) {
            ai2.makeMove();
          }
          moves[0]++;
        }
      });

      gameTimer.start();
      ai1Timer.start();
      ai2Timer.start();

      // 게임이 끝날 때까지 대기
      long gameStartTime = System.currentTimeMillis();
      while (!gameFinished[0] && moves[0] < MAX_MOVES_PER_GAME
          && (System.currentTimeMillis() - gameStartTime) < MAX_GAME_TIME_MS) {
        try {
          Thread.sleep(1); // 빠른 학습을 위한 매우 짧은 간격 (10ms -> 1ms)
        } catch (InterruptedException e) {
          break;
        }
      }

      gameTimer.stop();
      ai1Timer.stop();
      ai2Timer.stop();

      // 게임 시간 계산
      long gameTimeMs = System.currentTimeMillis() - gameStartTime;

      // 결과 기록 (대전 모드: 승리/패배 기반)
      int linesCleared = engine1.getGameScoring().getLinesCleared();
      int score = engine1.getGameScoring().getCurrentScore();

      // 승리/패배 정보 전달
      Boolean isWin = null;
      if (winner[0] == 1) {
        // 승리: 큰 보너스
        score += 10000;
        linesCleared += 20; // 승리 보너스
        isWin = true;
      } else if (winner[0] == 2) {
        // 패배: 패널티
        score = Math.max(0, score - 5000);
        isWin = false;
      }

      individual.addGameResult(linesCleared, score, isWin, gameTimeMs);
    }

    return individual.fitness;
  }

  /**
   * 공격 로직: 한 AI가 2줄 이상 삭제하면 상대방에게 공격 블록 전송
   * 실제 대전 모드와 동일한 로직 (PlayerGamePanel.updateGameUI 참고)
   */
  private void handleAttack(GameEngine attacker, GameEngine defender) {
    try {
      // 실제 대전 모드에서는 updateGameUI에서 consumeLastClearedRows를 호출
      // 하지만 학습 환경에서는 콜백에서 처리하므로 직접 확인
      List<Integer> clearedRows = attacker.consumeLastClearedRows();
      if (clearedRows != null && clearedRows.size() >= 2) {
        List<Color[]> attackData = attacker.getBoardManager().getAttackBlocksData();
        if (attackData != null && !attackData.isEmpty()) {
          // 공격 블록을 상대방 보드 하단에 추가
          boolean success = defender.getBoardManager().addAttackBlocksToBottom(attackData);
          if (!success) {
            // 공격 블록 추가 실패 시 게임 오버 가능성
            // 실제 대전 모드와 동일하게 처리
          }
        }
      }
    } catch (Exception e) {
      // 공격 처리 실패해도 게임 계속 진행
      // System.err.println("공격 처리 중 오류: " + e.getMessage());
    }
  }

  /**
   * 솔로 플레이 평가 (생존 가중치 평가용)
   * 보드 상태 기반으로 생존 능력을 평가
   */
  public double evaluateSurvival(Individual individual) {
    individual.resetFitness();

    for (int game = 0; game < QUICK_GAMES_PER_INDIVIDUAL; game++) {
      GameEngine engine = new GameEngine(20, 10, false);
      engine.setGameMode(GameMode.NORMAL);
      engine.startNewGame();

      TetrisAI ai = new TetrisAI(engine);
      ai.setWeights(individual.weights);

      final int[] moves = { 0 };
      final boolean[] gameFinished = { false };
      
      // 보드 상태 추적
      final int[] maxHeight = { 0 };
      final int[] sumHeight = { 0 };
      final int[] heightCount = { 0 };
      final int[] sumHoles = { 0 };

      Timer gameTimer = new Timer(QUICK_GAME_TIMER_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!engine.isGameOver()) {
            engine.moveBlockDown();
            
            // 보드 상태 측정
            int[][] board = engine.getBoardManager().getBoard();
            int currentMaxHeight = engine.getBoardManager().getHighestBlockRow();
            int currentAvgHeight = calculateAverageHeight(board);
            int currentHoles = countHoles(board);
            
            maxHeight[0] = Math.max(maxHeight[0], currentMaxHeight);
            sumHeight[0] += currentAvgHeight;
            heightCount[0]++;
            sumHoles[0] += currentHoles;
          } else {
            gameFinished[0] = true;
          }
        }
      });

      Timer aiTimer = new Timer(QUICK_AI_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!engine.isGameOver() && moves[0] < QUICK_MAX_MOVES) {
            ai.makeMove();
          }
          moves[0]++;
        }
      });

      gameTimer.start();
      aiTimer.start();

      long gameStartTime = System.currentTimeMillis();
      while (!gameFinished[0] && moves[0] < QUICK_MAX_MOVES
          && (System.currentTimeMillis() - gameStartTime) < QUICK_MAX_TIME) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          break;
        }
      }

      gameTimer.stop();
      aiTimer.stop();

      long gameTimeMs = System.currentTimeMillis() - gameStartTime;
      int linesCleared = engine.getGameScoring().getLinesCleared();
      int score = engine.getGameScoring().getCurrentScore();

      // 생존 능력 기반 적합도 계산
      double avgMaxHeight = maxHeight[0];
      double avgHeight = (heightCount[0] > 0) ? (double) sumHeight[0] / heightCount[0] : 0.0;
      double avgHoles = (heightCount[0] > 0) ? (double) sumHoles[0] / heightCount[0] : 0.0;

      // 생존 적합도: 보드 상태 기반
      double survivalFitness = 0.0;
      survivalFitness += (20.0 - avgMaxHeight) * 10.0; // 최대 높이가 낮을수록 좋음
      survivalFitness += (20.0 - avgHeight) * 5.0; // 평균 높이가 낮을수록 좋음
      survivalFitness += avgHoles * -5.0; // 구멍이 적을수록 좋음
      survivalFitness += linesCleared * 100.0; // 줄 삭제 보너스
      survivalFitness += gameTimeMs / 1000.0 * 10.0; // 생존 시간 보너스
      
      if (gameTimeMs > 18750) { // 19초 이상 생존
        survivalFitness += 1000.0;
      }

      individual.addGameResult(linesCleared, (int) survivalFitness, null, gameTimeMs);
    }

    return individual.fitness;
  }

  /**
   * 평균 보드 높이 계산
   */
  private int calculateAverageHeight(int[][] board) {
    int sum = 0;
    int count = 0;
    for (int x = 0; x < 10; x++) {
      for (int y = 0; y < 20; y++) {
        if (board[y][x] == 1) {
          sum += (20 - y);
          count++;
          break;
        }
      }
    }
    return count > 0 ? sum / count : 0;
  }

  /**
   * 구멍 개수 계산
   */
  private int countHoles(int[][] board) {
    int holes = 0;
    for (int x = 0; x < 10; x++) {
      boolean foundBlock = false;
      for (int y = 0; y < 20; y++) {
        if (board[y][x] == 1) {
          foundBlock = true;
        } else if (foundBlock) {
          holes++;
        }
      }
    }
    return holes;
  }

  /**
   * 빠른 평가 (AI 대전 모드, 최적화된 버전)
   * 게임이 메모리 상에서만 실행되며 UI는 없음
   */
  public double quickEvaluate(Individual individual) {
    individual.resetFitness();

    // 상대 AI는 이전 세대 최고 개체 또는 기본 가중치 사용
    WeightSet opponentWeights;
    if (previousBestOpponent != null) {
      opponentWeights = previousBestOpponent.weights;
    } else {
      opponentWeights = new WeightSet();
    }

    // 빠른 평가: 3게임 플레이
    for (int game = 0; game < QUICK_GAMES_PER_INDIVIDUAL; game++) {
      // 두 개의 게임 엔진 생성
      GameEngine engine1 = new GameEngine(20, 10, false);
      GameEngine engine2 = new GameEngine(20, 10, false);

      engine1.setGameMode(GameMode.NORMAL);
      engine2.setGameMode(GameMode.NORMAL);

      engine1.startNewGame();
      engine2.startNewGame();

      // 평가 대상 AI와 상대 AI 생성
      TetrisAI ai1 = new TetrisAI(engine1);
      TetrisAI ai2 = new TetrisAI(engine2);

      ai1.setWeights(individual.weights); // 평가 대상
      ai2.setWeights(opponentWeights); // 상대

      // 공격 로직: 블록 고정 후 콜백으로 처리
      engine1.setOnBlockFixedCallback(() -> handleAttack(engine1, engine2));
      engine2.setOnBlockFixedCallback(() -> handleAttack(engine2, engine1));

      final int[] moves = { 0 };
      final boolean[] gameFinished = { false };
      final int[] winner = { 0 };

      // 빠른 평가: 더 짧은 타이머 간격
      Timer gameTimer = new Timer(QUICK_GAME_TIMER_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!engine1.isGameOver() && !engine2.isGameOver()) {
            engine1.moveBlockDown();
            engine2.moveBlockDown();
          } else {
            if (engine1.isGameOver() && !engine2.isGameOver()) {
              winner[0] = 2;
            } else if (!engine1.isGameOver() && engine2.isGameOver()) {
              winner[0] = 1;
            } else {
              winner[0] = 0;
            }
            gameFinished[0] = true;
          }
        }
      });

      Timer ai1Timer = new Timer(QUICK_AI_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!engine1.isGameOver() && moves[0] < QUICK_MAX_MOVES) {
            ai1.makeMove();
          }
        }
      });

      Timer ai2Timer = new Timer(QUICK_AI_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!engine2.isGameOver() && moves[0] < QUICK_MAX_MOVES) {
            ai2.makeMove();
          }
          moves[0]++;
        }
      });

      gameTimer.start();
      ai1Timer.start();
      ai2Timer.start();

      long gameStartTime = System.currentTimeMillis();
      while (!gameFinished[0] && moves[0] < QUICK_MAX_MOVES
          && (System.currentTimeMillis() - gameStartTime) < QUICK_MAX_TIME) {
        try {
          Thread.sleep(1); // 빠른 평가를 위한 매우 짧은 간격 (5ms -> 1ms)
        } catch (InterruptedException e) {
          break;
        }
      }

      gameTimer.stop();
      ai1Timer.stop();
      ai2Timer.stop();

      // 게임 시간 계산
      long gameTimeMs = System.currentTimeMillis() - gameStartTime;

      int linesCleared = engine1.getGameScoring().getLinesCleared();
      int score = engine1.getGameScoring().getCurrentScore();

      // 승리/패배 정보 전달
      Boolean isWin = null;
      if (winner[0] == 1) {
        score += 10000;
        linesCleared += 20;
        isWin = true;
      } else if (winner[0] == 2) {
        score = Math.max(0, score - 5000);
        isWin = false;
      }

      individual.addGameResult(linesCleared, score, isWin, gameTimeMs);
    }

    return individual.fitness;
  }
}
