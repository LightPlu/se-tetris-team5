package se.tetris.team5.gamelogic.ai.training;

import se.tetris.team5.gamelogic.ai.WeightSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 하이브리드 유전 알고리즘: 생존 가중치와 공격 가중치를 분리하여 학습
 * Step 1: 솔로 플레이로 생존 가중치 학습 (공격 가중치 고정)
 * Step 2: 대전 플레이로 공격 가중치 학습 (생존 가중치 고정)
 */
public class HybridGeneticAlgorithm {
  private static final int POPULATION_SIZE = 30;
  private static final int ELITE_SIZE = 6; // 집단 크기의 20%
  private static final int MAX_GENERATIONS_SURVIVAL = 10; // 생존 가중치 학습 세대 수
  private static final int MAX_GENERATIONS_ATTACK = 10; // 공격 가중치 학습 세대 수
  private static final double BASE_MUTATION_RATE = 0.15;
  private static final double CROSSOVER_RATE = 0.7;

  private List<Individual> population;
  private final FitnessEvaluator evaluator;
  private final Random random;
  private int currentGeneration;
  private final List<GenerationResult> generationResults;
  
  // Step 1 결과 (생존 가중치)
  private WeightSet bestSurvivalWeights = null;

  public HybridGeneticAlgorithm() {
    this.population = new ArrayList<>();
    this.evaluator = new FitnessEvaluator();
    this.random = new Random();
    this.currentGeneration = 0;
    this.generationResults = new ArrayList<>();
  }

  /**
   * Step 1: 생존 가중치 학습 (솔로 플레이)
   */
  public WeightSet learnSurvivalWeights() {
    System.out.println("\n" + "=".repeat(80));
    System.out.println("=== Step 1: 생존 가중치 학습 (솔로 플레이) ===");
    System.out.println("=".repeat(80));
    System.out.println("공격 가중치는 기본값으로 고정하고 생존 가중치만 학습합니다.");
    System.out.println("평가 방식: 솔로 플레이 (보드 상태 기반 생존 능력 평가)");
    System.out.println("집단 크기: " + POPULATION_SIZE);
    System.out.println("최대 세대 수: " + MAX_GENERATIONS_SURVIVAL + "\n");

    long startTime = System.currentTimeMillis();
    
    // 초기 집단 생성 (생존 가중치만 랜덤)
    initializeSurvivalPopulation();

    for (int gen = 0; gen < MAX_GENERATIONS_SURVIVAL; gen++) {
      currentGeneration = gen;
      evaluateSurvivalGeneration();
      
      if (gen < MAX_GENERATIONS_SURVIVAL - 1) {
        evolveSurvivalGeneration();
      }
    }

    // 최고 생존 가중치 저장
    Collections.sort(population);
    bestSurvivalWeights = copyWeights(population.get(0).weights);
    
    long totalTime = System.currentTimeMillis() - startTime;
    System.out.println("\n=== Step 1 완료 ===");
    System.out.println("최고 생존 가중치:");
    System.out.println(bestSurvivalWeights.toString());
    System.out.println(String.format("소요 시간: %.1f분", totalTime / 60000.0));
    
    return bestSurvivalWeights;
  }

  /**
   * Step 2: 공격 가중치 학습 (대전 플레이, 생존 가중치 고정)
   */
  public WeightSet learnAttackWeights(WeightSet survivalWeights) {
    System.out.println("\n" + "=".repeat(80));
    System.out.println("=== Step 2: 공격 가중치 학습 (대전 플레이) ===");
    System.out.println("=".repeat(80));
    System.out.println("생존 가중치는 Step 1 결과로 고정하고 공격 가중치만 학습합니다.");
    System.out.println("평가 방식: AI vs AI 대전 모드");
    System.out.println("집단 크기: " + POPULATION_SIZE);
    System.out.println("최대 세대 수: " + MAX_GENERATIONS_ATTACK + "\n");

    this.bestSurvivalWeights = survivalWeights;
    long startTime = System.currentTimeMillis();
    
    // 초기 집단 생성 (공격 가중치만 랜덤, 생존 가중치는 고정)
    initializeAttackPopulation(survivalWeights);

    for (int gen = 0; gen < MAX_GENERATIONS_ATTACK; gen++) {
      currentGeneration = gen;
      evaluateAttackGeneration();
      
      if (gen < MAX_GENERATIONS_ATTACK - 1) {
        evolveAttackGeneration(survivalWeights);
      }
    }

    // 최고 공격 가중치와 생존 가중치 결합
    Collections.sort(population);
    WeightSet bestAttackWeights = copyWeights(population.get(0).weights);
    bestAttackWeights.updateSurvivalWeights(survivalWeights);
    
    long totalTime = System.currentTimeMillis() - startTime;
    System.out.println("\n=== Step 2 완료 ===");
    System.out.println("최종 가중치 (생존 + 공격):");
    System.out.println(bestAttackWeights.toString());
    System.out.println(String.format("소요 시간: %.1f분", totalTime / 60000.0));
    
    return bestAttackWeights;
  }

  /**
   * 생존 가중치 초기 집단 생성
   */
  private void initializeSurvivalPopulation() {
    population.clear();
    for (int i = 0; i < POPULATION_SIZE; i++) {
      WeightSet weights = WeightSet.randomSurvivalWeights();
      population.add(new Individual(weights));
    }
    currentGeneration = 0;
    System.out.println("생존 가중치 초기 집단 생성 완료: " + POPULATION_SIZE + "개 개체");
  }

  /**
   * 공격 가중치 초기 집단 생성 (생존 가중치 고정)
   */
  private void initializeAttackPopulation(WeightSet survivalWeights) {
    population.clear();
    for (int i = 0; i < POPULATION_SIZE; i++) {
      WeightSet weights = WeightSet.randomAttackWeights();
      weights.updateSurvivalWeights(survivalWeights); // 생존 가중치 고정
      population.add(new Individual(weights));
    }
    currentGeneration = 0;
    System.out.println("공격 가중치 초기 집단 생성 완료: " + POPULATION_SIZE + "개 개체");
  }

  /**
   * 생존 세대 평가
   */
  private void evaluateSurvivalGeneration() {
    System.out.println("\n" + "=".repeat(80));
    System.out.println("=== 생존 가중치 세대 " + currentGeneration + " 평가 시작 ===");
    System.out.println("=".repeat(80));
    long generationStartTime = System.currentTimeMillis();

    for (int i = 0; i < population.size(); i++) {
      Individual individual = population.get(i);
      long startTime = System.currentTimeMillis();

      // 솔로 평가 (생존 능력)
      double fitness = evaluator.evaluateSurvival(individual);

      long elapsed = System.currentTimeMillis() - startTime;
      System.out.println(String.format("개체 %d/%d 평가 완료 (%.2f초) - 적합도: %.2f",
          i + 1, population.size(), elapsed / 1000.0, fitness));
    }

    long generationTime = System.currentTimeMillis() - generationStartTime;
    Collections.sort(population);
    
    GenerationResult result = new GenerationResult(currentGeneration);
    result.bestFitness = population.get(0).fitness;
    result.avgFitness = population.stream().mapToDouble(ind -> ind.fitness).average().orElse(0.0);
    result.worstFitness = population.get(population.size() - 1).fitness;
    result.bestWeights = copyWeights(population.get(0).weights);
    result.bestLinesPerGame = population.get(0).averageLinesPerGame;
    result.bestScorePerGame = population.get(0).averageScorePerGame;
    result.evaluationTimeMs = generationTime;

    System.out.println("\n" + "-".repeat(80));
    System.out.println("생존 가중치 세대 " + currentGeneration + " 결과:");
    System.out.println(String.format("  최고 적합도: %.2f", result.bestFitness));
    System.out.println(String.format("  평균 적합도: %.2f", result.avgFitness));
    System.out.println(String.format("  최저 적합도: %.2f", result.worstFitness));
    System.out.println(String.format("  소요 시간: %.1f초", generationTime / 1000.0));
    System.out.println("-".repeat(80));

    generationResults.add(result);
  }

  /**
   * 공격 세대 평가
   */
  private void evaluateAttackGeneration() {
    System.out.println("\n" + "=".repeat(80));
    System.out.println("=== 공격 가중치 세대 " + currentGeneration + " 평가 시작 ===");
    System.out.println("=".repeat(80));
    long generationStartTime = System.currentTimeMillis();

    // 이전 세대 최고 개체를 상대 AI로 설정
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

      // 대전 평가 (공격 능력)
      double fitness = evaluator.quickEvaluate(individual);

      long elapsed = System.currentTimeMillis() - startTime;
      System.out.println(String.format("개체 %d/%d 평가 완료 (%.2f초) - 적합도: %.2f",
          i + 1, population.size(), elapsed / 1000.0, fitness));
    }

    long generationTime = System.currentTimeMillis() - generationStartTime;
    Collections.sort(population);
    
    GenerationResult result = new GenerationResult(currentGeneration);
    result.bestFitness = population.get(0).fitness;
    result.avgFitness = population.stream().mapToDouble(ind -> ind.fitness).average().orElse(0.0);
    result.worstFitness = population.get(population.size() - 1).fitness;
    result.bestWeights = copyWeights(population.get(0).weights);
    result.bestLinesPerGame = population.get(0).averageLinesPerGame;
    result.bestScorePerGame = population.get(0).averageScorePerGame;
    result.evaluationTimeMs = generationTime;

    System.out.println("\n" + "-".repeat(80));
    System.out.println("공격 가중치 세대 " + currentGeneration + " 결과:");
    System.out.println(String.format("  최고 적합도: %.2f (평균 줄: %.1f, 평균 점수: %.0f)",
        result.bestFitness, result.bestLinesPerGame, result.bestScorePerGame));
    System.out.println(String.format("  평균 적합도: %.2f", result.avgFitness));
    System.out.println(String.format("  최저 적합도: %.2f", result.worstFitness));
    System.out.println(String.format("  소요 시간: %.1f초", generationTime / 1000.0));
    System.out.println("-".repeat(80));

    generationResults.add(result);
  }

  /**
   * 생존 세대 진화
   */
  private void evolveSurvivalGeneration() {
    Collections.sort(population);
    List<Individual> nextGeneration = new ArrayList<>();

    // 엘리트 보존
    for (int i = 0; i < ELITE_SIZE; i++) {
      nextGeneration.add(new Individual(copyWeights(population.get(i).weights)));
    }

    // 교차와 돌연변이
    while (nextGeneration.size() < POPULATION_SIZE) {
      Individual parent1 = selectParent();
      Individual parent2 = selectParent();

      WeightSet childWeights;
      if (random.nextDouble() < CROSSOVER_RATE) {
        childWeights = WeightSet.crossover(parent1.weights, parent2.weights);
      } else {
        childWeights = random.nextBoolean()
            ? copyWeights(parent1.weights)
            : copyWeights(parent2.weights);
      }

      double adaptiveMutationRate = getAdaptiveMutationRate(currentGeneration);
      childWeights.mutate(adaptiveMutationRate);

      nextGeneration.add(new Individual(childWeights));
    }

    population = nextGeneration;
  }

  /**
   * 공격 세대 진화 (생존 가중치 고정)
   */
  private void evolveAttackGeneration(WeightSet survivalWeights) {
    Collections.sort(population);
    List<Individual> nextGeneration = new ArrayList<>();

    // 엘리트 보존
    for (int i = 0; i < ELITE_SIZE; i++) {
      WeightSet eliteWeights = copyWeights(population.get(i).weights);
      eliteWeights.updateSurvivalWeights(survivalWeights); // 생존 가중치 고정
      nextGeneration.add(new Individual(eliteWeights));
    }

    // 교차와 돌연변이
    while (nextGeneration.size() < POPULATION_SIZE) {
      Individual parent1 = selectParent();
      Individual parent2 = selectParent();

      WeightSet childWeights;
      if (random.nextDouble() < CROSSOVER_RATE) {
        childWeights = WeightSet.crossover(parent1.weights, parent2.weights);
      } else {
        childWeights = random.nextBoolean()
            ? copyWeights(parent1.weights)
            : copyWeights(parent2.weights);
      }

      // 생존 가중치 고정
      childWeights.updateSurvivalWeights(survivalWeights);

      double adaptiveMutationRate = getAdaptiveMutationRate(currentGeneration);
      childWeights.mutate(adaptiveMutationRate);
      
      // 돌연변이 후에도 생존 가중치 고정 유지
      childWeights.updateSurvivalWeights(survivalWeights);

      nextGeneration.add(new Individual(childWeights));
    }

    population = nextGeneration;
  }

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

  private double getAdaptiveMutationRate(int generation) {
    double decay = 0.995;
    return BASE_MUTATION_RATE * Math.pow(decay, generation);
  }

  public List<GenerationResult> getGenerationResults() {
    return generationResults;
  }
}

