package se.tetris.team5.gamelogic.ai;

/**
 * 세대별 결과를 저장하는 클래스
 */
public class GenerationResult {
  public int generation;
  public double bestFitness;
  public double avgFitness;
  public double worstFitness;
  public WeightSet bestWeights;
  public double bestLinesPerGame;
  public double bestScorePerGame;
  public long evaluationTimeMs;
  
  // 일반 평가 모드 결과 (정확한 평가)
  public double accurateFitness;
  public double accurateLinesPerGame;
  public double accurateScorePerGame;
  public long accurateEvaluationTimeMs;

  public GenerationResult(int generation) {
    this.generation = generation;
  }

  @Override
  public String toString() {
    return String.format(
        "세대 %d: 최고=%.2f, 평균=%.2f, 최저=%.2f (%.1f초)",
        generation, bestFitness, avgFitness, worstFitness, evaluationTimeMs / 1000.0);
  }
}

