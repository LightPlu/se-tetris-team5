package se.tetris.team5.gamelogic.ai.training;

import se.tetris.team5.gamelogic.ai.WeightSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 유전 알고리즘으로 AI 가중치 최적화
 */
public class GeneticAlgorithm {
  // 학습 시간 단축을 위한 설정 (정확도 유지)
  private static final int POPULATION_SIZE = 20; // 집단 크기
  private static final int ELITE_SIZE = 4; // 엘리트 개체 수 (집단 크기의 20%)
  private static final int MAX_GENERATIONS = 5; // 최대 세대 수
  private static final double BASE_MUTATION_RATE = 0.15; // 기본 돌연변이 확률
  private static final double CROSSOVER_RATE = 0.7; // 교차 확률

  private List<Individual> population;
  private final FitnessEvaluator evaluator;
  private final Random random;
  private int currentGeneration;
  private final List<GenerationResult> generationResults; // 세대별 결과 저장

  public GeneticAlgorithm() {
    this.population = new ArrayList<>();
    this.evaluator = new FitnessEvaluator();
    this.random = new Random();
    this.currentGeneration = 0;
    this.generationResults = new ArrayList<>();
  }

  /**
   * 초기 집단 생성
   */
  public void initializePopulation() {
    population.clear();
    for (int i = 0; i < POPULATION_SIZE; i++) {
      WeightSet weights = WeightSet.random();
      population.add(new Individual(weights));
    }
    currentGeneration = 0;
    System.out.println("초기 집단 생성 완료: " + POPULATION_SIZE + "개 개체");
  }

  /**
   * 한 세대 평가
   */
  public void evaluateGeneration() {
    System.out.println("\n" + "=".repeat(80));
    System.out.println("=== 세대 " + currentGeneration + " 평가 시작 ===");
    System.out.println("=".repeat(80));
    long generationStartTime = System.currentTimeMillis();

    // 이전 세대 최고 개체를 상대 AI로 설정 (첫 세대 제외)
    if (currentGeneration > 0 && !generationResults.isEmpty()) {
      Individual previousBest = new Individual(
          copyWeights(generationResults.get(generationResults.size() - 1).bestWeights));
      evaluator.setOpponent(previousBest);
      System.out.println("상대 AI: 이전 세대 최고 개체 사용");
    } else {
      evaluator.setOpponent(null);
      System.out.println("상대 AI: 기본 가중치 사용");
    }

    for (int i = 0; i < population.size(); i++) {
      Individual individual = population.get(i);
      long startTime = System.currentTimeMillis();

      // 빠른 평가 사용 (시간 절약)
      double fitness = evaluator.quickEvaluate(individual);

      long elapsed = System.currentTimeMillis() - startTime;
      System.out.println(String.format("개체 %d/%d 평가 완료 (%.2f초) - 적합도: %.2f",
          i + 1, population.size(), elapsed / 1000.0, fitness));
    }

    long generationTime = System.currentTimeMillis() - generationStartTime;

    // 세대별 결과 수집 (빠른 평가 기준)
    Collections.sort(population);
    GenerationResult result = new GenerationResult(currentGeneration);

    // 빠른 평가 결과 수집
    result.bestFitness = population.get(0).fitness;
    result.avgFitness = population.stream().mapToDouble(ind -> ind.fitness).average().orElse(0.0);
    result.worstFitness = population.get(population.size() - 1).fitness;
    result.bestWeights = copyWeights(population.get(0).weights);
    result.bestLinesPerGame = population.get(0).averageLinesPerGame;
    result.bestScorePerGame = population.get(0).averageScorePerGame;
    result.evaluationTimeMs = generationTime;

    // 실시간 세대별 결과 출력 (빠른 평가)
    System.out.println("\n" + "-".repeat(80));
    System.out.println("세대 " + currentGeneration + " 결과 요약 (빠른 평가):");
    System.out.println(String.format("  최고 적합도: %.2f (평균 줄: %.1f, 평균 점수: %.0f)",
        result.bestFitness, result.bestLinesPerGame, result.bestScorePerGame));
    System.out.println(String.format("  평균 적합도: %.2f", result.avgFitness));
    System.out.println(String.format("  최저 적합도: %.2f", result.worstFitness));
    System.out.println(String.format("  소요 시간: %.1f초", generationTime / 1000.0));
    System.out.println("  최고 가중치: " + result.bestWeights.toString());
    System.out.println("-".repeat(80));

    // 빠른 평가 평균 적합도로 최적 개체 선택 후 정밀 평가
    System.out.println("\n=== 세대 " + currentGeneration + " 최고 개체 정밀 평가 시작 ===");
    System.out.println("선택된 최적 개체 (빠른 평가 평균 적합도 기준):");
    System.out.println("  적합도: " + String.format("%.2f", result.bestFitness));
    System.out.println("  가중치: " + result.bestWeights.toString());

    Individual bestIndividual = new Individual(copyWeights(population.get(0).weights));
    long accurateStartTime = System.currentTimeMillis();
    evaluator.evaluate(bestIndividual); // 정밀 평가 모드
    long accurateTime = System.currentTimeMillis() - accurateStartTime;

    // 정밀 평가 결과 저장
    result.accurateFitness = bestIndividual.fitness;
    result.accurateLinesPerGame = bestIndividual.averageLinesPerGame;
    result.accurateScorePerGame = bestIndividual.averageScorePerGame;
    result.accurateEvaluationTimeMs = accurateTime;

    // 정밀 평가 후 가중치 업데이트 (정밀 평가 결과 반영)
    result.bestWeights = copyWeights(bestIndividual.weights);

    System.out.println(String.format("정밀 평가 결과: 적합도=%.2f, 평균 줄=%.1f, 평균 점수=%.0f (소요 시간: %.1f초)",
        result.accurateFitness, result.accurateLinesPerGame, result.accurateScorePerGame, accurateTime / 1000.0));
    System.out.println("-".repeat(80));

    generationResults.add(result);

    System.out.println(String.format("\n세대 %d 평가 완료 - 소요 시간: %.1f초 (빠른: %.1f초, 정확한: %.1f초)",
        currentGeneration, (generationTime + accurateTime) / 1000.0, generationTime / 1000.0, accurateTime / 1000.0));
  }

  /**
   * 다음 세대 생성
   */
  public void evolveGeneration() {
    // 적합도 순으로 정렬
    Collections.sort(population);

    // 최고 개체 출력
    Individual best = population.get(0);
    System.out.println("\n최고 개체: " + best.toString());

    // 다음 세대 생성
    List<Individual> nextGeneration = new ArrayList<>();

    // 엘리트 개체 보존 (상위 ELITE_SIZE개)
    for (int i = 0; i < ELITE_SIZE; i++) {
      nextGeneration.add(new Individual(copyWeights(population.get(i).weights)));
    }

    // 나머지 개체는 교차와 돌연변이로 생성
    while (nextGeneration.size() < POPULATION_SIZE) {
      Individual parent1 = selectParent();
      Individual parent2 = selectParent();

      WeightSet childWeights;
      if (random.nextDouble() < CROSSOVER_RATE) {
        childWeights = WeightSet.crossover(parent1.weights, parent2.weights);
      } else {
        // 교차하지 않으면 부모 중 하나 복사
        childWeights = random.nextBoolean()
            ? copyWeights(parent1.weights)
            : copyWeights(parent2.weights);
      }

      // 적응적 돌연변이 적용 (세대가 진행될수록 돌연변이율 감소)
      double adaptiveMutationRate = getAdaptiveMutationRate(currentGeneration);
      childWeights.mutate(adaptiveMutationRate);

      nextGeneration.add(new Individual(childWeights));
    }

    population = nextGeneration;
    currentGeneration++;
  }

  /**
   * 토너먼트 선택으로 부모 선택
   */
  private Individual selectParent() {
    int tournamentSize = 3;
    Individual best = null;
    for (int i = 0; i < tournamentSize; i++) {
      Individual candidate = population.get(random.nextInt(population.size()));
      if (best == null || candidate.fitness > best.fitness) {
        best = candidate;
      }
    }
    return best;
  }

  /**
   * 가중치 복사
   */
  private WeightSet copyWeights(WeightSet original) {
    return new WeightSet(
        original.weightLandingHeight,
        original.weightEPCM,
        original.weightRowTransitions,
        original.weightColumnTransitions,
        original.weightHoles,
        original.weightWellSums,
        original.weightAttack2Lines,
        original.weightAttack3Lines,
        original.weightAttack4Lines);
  }

  /**
   * 유전 알고리즘 실행
   */
  public Individual run() {
    System.out.println("=== 유전 알고리즘 시작 ===");
    System.out.println("집단 크기: " + POPULATION_SIZE);
    System.out.println("엘리트 개체 수: " + ELITE_SIZE);
    System.out.println("최대 세대 수: " + MAX_GENERATIONS);
    System.out.println("기본 돌연변이 확률: " + BASE_MUTATION_RATE + " (적응적)");
    System.out.println("교차 확률: " + CROSSOVER_RATE);
    System.out.println("\n※ 게임은 메모리 상에서만 실행되며 UI는 없습니다.");
    System.out.println("※ 빠른 평가 모드: 게임당 최대 5초, 최대 1000회 이동");
    System.out.println("※ 일반 평가 모드: 각 세대 최고 개체에 대해 정확한 평가 수행\n");

    long totalStartTime = System.currentTimeMillis();
    initializePopulation();

    for (int gen = 0; gen < MAX_GENERATIONS; gen++) {
      evaluateGeneration();

      if (gen < MAX_GENERATIONS - 1) {
        evolveGeneration();
      }

      // 예상 남은 시간 계산
      if (gen > 0) {
        long elapsed = System.currentTimeMillis() - totalStartTime;
        double avgTimePerGen = elapsed / (double) (gen + 1);
        double remainingGens = MAX_GENERATIONS - gen - 1;
        double estimatedRemaining = avgTimePerGen * remainingGens;
        System.out.println(String.format("예상 남은 시간: 약 %.1f분 (%.1f초)",
            estimatedRemaining / 60000.0, estimatedRemaining / 1000.0));
      }
    }

    // 최종 평가 (더 정확한 평가) - 선택적
    System.out.println("\n=== 최종 평가 (정확한 평가) ===");
    Collections.sort(population);
    Individual best = population.get(0);
    System.out.println("최종 평가 중... (5게임 플레이)");
    try {
      evaluator.evaluate(best); // 정확한 평가
      System.out.println("최종 최고 개체: " + best.toString());
    } catch (Exception e) {
      System.err.println("최종 평가 중 오류 발생: " + e.getMessage());
      System.out.println("99세대 최고 개체 사용: " + best.toString());
      System.out.println("  적합도: " + best.fitness);
      System.out.println("  평균 줄 수: " + best.averageLinesPerGame);
      System.out.println("  평균 점수: " + best.averageScorePerGame);
    }

    long totalTime = System.currentTimeMillis() - totalStartTime;
    System.out.println(String.format("\n=== 최적화 완료 ==="));
    System.out.println(String.format("총 소요 시간: %.1f분 (%.0f초)",
        totalTime / 60000.0, totalTime / 1000.0));

    return best;
  }

  /**
   * 현재 세대의 통계 출력
   */
  public void printStatistics() {
    Collections.sort(population);
    double avgFitness = population.stream()
        .mapToDouble(ind -> ind.fitness)
        .average()
        .orElse(0.0);
    double maxFitness = population.get(0).fitness;
    double minFitness = population.get(population.size() - 1).fitness;

    System.out.println(String.format(
        "세대 %d - 평균: %.2f, 최고: %.2f, 최저: %.2f",
        currentGeneration, avgFitness, maxFitness, minFitness));
  }

  /**
   * 최고 개체 반환
   */
  public Individual getBestIndividual() {
    Collections.sort(population);
    return population.get(0);
  }

  /**
   * 세대별 결과 반환
   */
  public List<GenerationResult> getGenerationResults() {
    return generationResults;
  }

  /**
   * 적응적 돌연변이율 계산
   * 세대가 진행될수록 돌연변이율이 감소하여 수렴을 돕는다
   */
  private double getAdaptiveMutationRate(int generation) {
    double decay = 0.995; // 매 세대마다 0.5% 감소
    return BASE_MUTATION_RATE * Math.pow(decay, generation);
  }
}
