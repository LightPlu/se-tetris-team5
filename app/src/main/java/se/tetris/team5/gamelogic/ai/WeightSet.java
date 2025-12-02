package se.tetris.team5.gamelogic.ai;

/**
 * AI 평가 함수의 가중치 집합
 */
public class WeightSet {
  // El-Tetris 특징 가중치
  public double weightLandingHeight; // Feature 1: Landing Height
  public double weightEPCM; // Feature 2: Eroded Piece Cells Metric
  public double weightRowTransitions; // Feature 3: Row Transitions
  public double weightColumnTransitions; // Feature 4: Column Transitions
  public double weightHoles; // Feature 5: Number of Holes
  public double weightWellSums; // Feature 6: Well Sums

  // 대전 모드 공격 보너스 (줄별 가중치)
  public double weightAttack2Lines; // 2줄 삭제 시 공격 보너스
  public double weightAttack3Lines; // 3줄 삭제 시 공격 보너스
  public double weightAttack4Lines; // 4줄 삭제 시 공격 보너스 (Tetris)

  public WeightSet() {
    // 유전 알고리즘 최적 가중치
    this.weightLandingHeight = -7.80; // Feature 1: Landing Height
    this.weightEPCM = 5.58; // Feature 2: Eroded Piece Cells Metric
    this.weightRowTransitions = -1.58; // Feature 3: Row Transitions
    this.weightColumnTransitions = -1.13; // Feature 4: Column Transitions
    this.weightHoles = -18.80; // Feature 5: Number of Holes
    this.weightWellSums = -8.69; // Feature 6: Well Sums

    // 대전 모드 공격 보너스
    this.weightAttack2Lines = 15.14; // 2줄 삭제 시 보너스
    this.weightAttack3Lines = 19.18; // 3줄 삭제 시 보너스
    this.weightAttack4Lines = 59.16; // 4줄 삭제 시 보너스 (Tetris)
  }

  public WeightSet(double weightLandingHeight, double weightEPCM, double weightRowTransitions,
      double weightColumnTransitions, double weightHoles, double weightWellSums,
      double weightAttack2Lines, double weightAttack3Lines, double weightAttack4Lines) {
    this.weightLandingHeight = weightLandingHeight;
    this.weightEPCM = weightEPCM;
    this.weightRowTransitions = weightRowTransitions;
    this.weightColumnTransitions = weightColumnTransitions;
    this.weightHoles = weightHoles;
    this.weightWellSums = weightWellSums;
    this.weightAttack2Lines = weightAttack2Lines;
    this.weightAttack3Lines = weightAttack3Lines;
    this.weightAttack4Lines = weightAttack4Lines;
  }

  /**
   * 랜덤 가중치 생성 (초기 집단용)
   * El-Tetris 특징에 맞는 가중치 범위 설정
   */
  public static WeightSet random() {
    java.util.Random rand = new java.util.Random();
    return new WeightSet(
        -(rand.nextDouble() * 10.0 + 1.0), // Landing Height: -11 ~ -1 (항상 음수)
        rand.nextDouble() * 10.0 + 1.0, // EPCM: 1 ~ 11 (항상 양수)
        -(rand.nextDouble() * 10.0 + 1.0), // Row Transitions: -11 ~ -1 (항상 음수)
        -(rand.nextDouble() * 20.0 + 1.0), // Column Transitions: -21 ~ -1 (항상 음수)
        -(rand.nextDouble() * 15.0 + 5.0), // Holes: -20 ~ -5 (항상 음수, 범위 증가로 생존 능력 강화)
        -(rand.nextDouble() * 10.0 + 1.0), // Well Sums: -11 ~ -1 (항상 음수)
        rand.nextDouble() * 20.0 + 5.0, // Attack 2 Lines: 5 ~ 25 (항상 양수, 범위 감소)
        rand.nextDouble() * 30.0 + 10.0, // Attack 3 Lines: 10 ~ 40 (항상 양수, 범위 감소)
        rand.nextDouble() * 40.0 + 20.0 // Attack 4 Lines: 20 ~ 60 (항상 양수, Tetris, 범위 감소)
    );
  }

  /**
   * 교차 (Crossover): 두 부모의 가중치를 섞어 자식 생성
   */
  public static WeightSet crossover(WeightSet parent1, WeightSet parent2) {
    java.util.Random rand = new java.util.Random();
    return new WeightSet(
        rand.nextBoolean() ? parent1.weightLandingHeight : parent2.weightLandingHeight,
        rand.nextBoolean() ? parent1.weightEPCM : parent2.weightEPCM,
        rand.nextBoolean() ? parent1.weightRowTransitions : parent2.weightRowTransitions,
        rand.nextBoolean() ? parent1.weightColumnTransitions : parent2.weightColumnTransitions,
        rand.nextBoolean() ? parent1.weightHoles : parent2.weightHoles,
        rand.nextBoolean() ? parent1.weightWellSums : parent2.weightWellSums,
        rand.nextBoolean() ? parent1.weightAttack2Lines : parent2.weightAttack2Lines,
        rand.nextBoolean() ? parent1.weightAttack3Lines : parent2.weightAttack3Lines,
        rand.nextBoolean() ? parent1.weightAttack4Lines : parent2.weightAttack4Lines);
  }

  /**
   * 돌연변이 (Mutation): 가중치를 약간 변경
   * El-Tetris 특징에 맞는 가중치 제약을 유지하면서 변경
   */
  public void mutate(double mutationRate) {
    java.util.Random rand = new java.util.Random();
    if (rand.nextDouble() < mutationRate) {
      weightLandingHeight += (rand.nextDouble() - 0.5) * 2.0;
      weightLandingHeight = Math.max(-15.0, Math.min(-0.1, weightLandingHeight)); // -15 ~ -0.1로 제한 (항상 음수)
    }
    if (rand.nextDouble() < mutationRate) {
      weightEPCM += (rand.nextDouble() - 0.5) * 2.0;
      weightEPCM = Math.max(0.1, Math.min(20.0, weightEPCM)); // 0.1 ~ 20로 제한 (항상 양수)
    }
    if (rand.nextDouble() < mutationRate) {
      weightRowTransitions += (rand.nextDouble() - 0.5) * 2.0;
      weightRowTransitions = Math.max(-20.0, Math.min(-0.1, weightRowTransitions)); // -20 ~ -0.1로 제한 (항상 음수)
    }
    if (rand.nextDouble() < mutationRate) {
      weightColumnTransitions += (rand.nextDouble() - 0.5) * 4.0;
      weightColumnTransitions = Math.max(-30.0, Math.min(-0.1, weightColumnTransitions)); // -30 ~ -0.1로 제한 (항상 음수)
    }
    if (rand.nextDouble() < mutationRate) {
      weightHoles += (rand.nextDouble() - 0.5) * 4.0;
      weightHoles = Math.max(-25.0, Math.min(-3.0, weightHoles)); // -25 ~ -3로 제한 (항상 음수, 더 큰 패널티 유지)
    }
    if (rand.nextDouble() < mutationRate) {
      weightWellSums += (rand.nextDouble() - 0.5) * 2.0;
      weightWellSums = Math.max(-15.0, Math.min(-0.1, weightWellSums)); // -15 ~ -0.1로 제한 (항상 음수)
    }
    if (rand.nextDouble() < mutationRate) {
      weightAttack2Lines += (rand.nextDouble() - 0.5) * 4.0;
      weightAttack2Lines = Math.max(1.0, Math.min(30.0, weightAttack2Lines)); // 1 ~ 30로 제한 (항상 양수, 범위 감소)
    }
    if (rand.nextDouble() < mutationRate) {
      weightAttack3Lines += (rand.nextDouble() - 0.5) * 6.0;
      weightAttack3Lines = Math.max(5.0, Math.min(50.0, weightAttack3Lines)); // 5 ~ 50로 제한 (항상 양수, 범위 감소)
    }
    if (rand.nextDouble() < mutationRate) {
      weightAttack4Lines += (rand.nextDouble() - 0.5) * 8.0;
      weightAttack4Lines = Math.max(10.0, Math.min(70.0, weightAttack4Lines)); // 10 ~ 70로 제한 (항상 양수, 범위 감소)
    }
  }

  /**
   * 생존 가중치만 복사 (하이브리드 학습용)
   */
  public WeightSet copySurvivalWeights() {
    WeightSet copy = new WeightSet();
    copy.weightLandingHeight = this.weightLandingHeight;
    copy.weightEPCM = this.weightEPCM;
    copy.weightRowTransitions = this.weightRowTransitions;
    copy.weightColumnTransitions = this.weightColumnTransitions;
    copy.weightHoles = this.weightHoles;
    copy.weightWellSums = this.weightWellSums;
    // 공격 가중치는 기본값 유지
    return copy;
  }

  /**
   * 공격 가중치만 복사 (하이브리드 학습용)
   */
  public WeightSet copyAttackWeights() {
    WeightSet copy = new WeightSet();
    // 생존 가중치는 기본값 유지
    copy.weightAttack2Lines = this.weightAttack2Lines;
    copy.weightAttack3Lines = this.weightAttack3Lines;
    copy.weightAttack4Lines = this.weightAttack4Lines;
    return copy;
  }

  /**
   * 생존 가중치만 업데이트 (하이브리드 학습용)
   */
  public void updateSurvivalWeights(WeightSet source) {
    this.weightLandingHeight = source.weightLandingHeight;
    this.weightEPCM = source.weightEPCM;
    this.weightRowTransitions = source.weightRowTransitions;
    this.weightColumnTransitions = source.weightColumnTransitions;
    this.weightHoles = source.weightHoles;
    this.weightWellSums = source.weightWellSums;
  }

  /**
   * 공격 가중치만 업데이트 (하이브리드 학습용)
   */
  public void updateAttackWeights(WeightSet source) {
    this.weightAttack2Lines = source.weightAttack2Lines;
    this.weightAttack3Lines = source.weightAttack3Lines;
    this.weightAttack4Lines = source.weightAttack4Lines;
  }

  /**
   * 생존 가중치만 랜덤 생성 (하이브리드 학습용)
   */
  public static WeightSet randomSurvivalWeights() {
    java.util.Random rand = new java.util.Random();
    WeightSet weights = new WeightSet();
    weights.weightLandingHeight = -(rand.nextDouble() * 10.0 + 1.0);
    weights.weightEPCM = rand.nextDouble() * 10.0 + 1.0;
    weights.weightRowTransitions = -(rand.nextDouble() * 10.0 + 1.0);
    weights.weightColumnTransitions = -(rand.nextDouble() * 20.0 + 1.0);
    weights.weightHoles = -(rand.nextDouble() * 15.0 + 5.0);
    weights.weightWellSums = -(rand.nextDouble() * 10.0 + 1.0);
    // 공격 가중치는 기본값 유지
    return weights;
  }

  /**
   * 공격 가중치만 랜덤 생성 (하이브리드 학습용)
   */
  public static WeightSet randomAttackWeights() {
    java.util.Random rand = new java.util.Random();
    WeightSet weights = new WeightSet();
    // 생존 가중치는 기본값 유지
    weights.weightAttack2Lines = rand.nextDouble() * 20.0 + 5.0;
    weights.weightAttack3Lines = rand.nextDouble() * 30.0 + 10.0;
    weights.weightAttack4Lines = rand.nextDouble() * 40.0 + 20.0;
    return weights;
  }

  @Override
  public String toString() {
    return String.format(
        "LandingH: %.2f, EPCM: %.2f, RowTrans: %.2f, ColTrans: %.2f, Holes: %.2f, WellSums: %.2f, Attack2: %.2f, Attack3: %.2f, Attack4: %.2f",
        weightLandingHeight, weightEPCM, weightRowTransitions, weightColumnTransitions, weightHoles, weightWellSums,
        weightAttack2Lines, weightAttack3Lines, weightAttack4Lines);
  }
}
