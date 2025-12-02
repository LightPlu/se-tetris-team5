package se.tetris.team5.gamelogic.ai.training;

import se.tetris.team5.gamelogic.ai.WeightSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 유전 알고리즘 실행 클래스
 * 메인 메서드로 실행 가능
 */
public class GeneticAlgorithmRunner {
  private static final int NUM_RUNS = 1; // 다중 실행 횟수 (1회로 변경)

  public static void main(String[] args) {
    // 터미널 로그 저장을 위한 PrintStream 설정
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String timestamp = dateFormat.format(new Date());
    String logFilename = "docs/reports/genetic_algorithm_log_" + timestamp + ".txt";

    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;

    try {
      // 디렉토리 생성
      java.nio.file.Files.createDirectories(java.nio.file.Paths.get("docs/reports"));

      // 파일과 콘솔 모두에 출력하는 PrintStream 생성
      PrintStream fileOut = new PrintStream(new FileOutputStream(logFilename), true, "UTF-8");
      PrintStream teeOut = new TeePrintStream(originalOut, fileOut);

      System.setOut(teeOut);
      System.setErr(teeOut);

      originalOut.println("터미널 로그가 저장됩니다: " + logFilename);

      runGeneticAlgorithm();

      // 원래 PrintStream으로 복원
      System.setOut(originalOut);
      System.setErr(originalErr);

      fileOut.close();
      originalOut.println("\n터미널 로그 저장 완료: " + logFilename);

    } catch (IOException e) {
      originalErr.println("로그 파일 생성 실패: " + e.getMessage());
      e.printStackTrace();
      // 실패해도 계속 진행
      runGeneticAlgorithm();
    }
  }

  private static void runGeneticAlgorithm() {
    System.out.println("=== 테트리스 AI 가중치 최적화 (하이브리드 유전 알고리즘) ===\n");
    System.out.println("하이브리드 접근 방법:");
    System.out.println("  Step 1: 솔로 플레이로 생존 가중치 학습 (공격 가중치 고정)");
    System.out.println("  Step 2: 대전 플레이로 공격 가중치 학습 (생존 가중치 고정)");
    System.out.println("  Step 3: 통합 검증 (대전 모드)\n");

    long totalStartTime = System.currentTimeMillis();

    // 하이브리드 유전 알고리즘 실행
    HybridGeneticAlgorithm hybridGA = new HybridGeneticAlgorithm();

    // Step 1: 생존 가중치 학습
    WeightSet bestSurvivalWeights = hybridGA.learnSurvivalWeights();

    // Step 2: 공격 가중치 학습
    WeightSet finalWeights = hybridGA.learnAttackWeights(bestSurvivalWeights);

    // Step 3: 통합 검증 (대전 모드로 최종 평가)
    System.out.println("\n" + "=".repeat(80));
    System.out.println("=== Step 3: 통합 검증 (대전 모드) ===");
    System.out.println("=".repeat(80));

    Individual finalBest = new Individual(finalWeights);
    FitnessEvaluator evaluator = new FitnessEvaluator();
    evaluator.setOpponent(null); // 기본 가중치와 대전

    System.out.println("최종 가중치로 대전 모드 평가 중...");
    evaluator.evaluate(finalBest);

    long totalTime = System.currentTimeMillis() - totalStartTime;

    System.out.println("\n=== 최종 결과 ===");
    System.out.println("적합도: " + String.format("%.2f", finalBest.fitness));
    System.out.println("평균 줄 수: " + String.format("%.1f", finalBest.averageLinesPerGame));
    System.out.println("평균 점수: " + String.format("%.0f", finalBest.averageScorePerGame));
    System.out.println("총 소요 시간: " + String.format("%.1f분", totalTime / 60000.0));

    // 코드로 사용할 수 있도록 출력
    System.out.println("\n=== 코드에 사용할 가중치 ===");
    System.out.println(String.format(
        "new WeightSet(%.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f)",
        finalBest.weights.weightLandingHeight,
        finalBest.weights.weightEPCM,
        finalBest.weights.weightRowTransitions,
        finalBest.weights.weightColumnTransitions,
        finalBest.weights.weightHoles,
        finalBest.weights.weightWellSums,
        finalBest.weights.weightAttack2Lines,
        finalBest.weights.weightAttack3Lines,
        finalBest.weights.weightAttack4Lines));

    // 보고서 생성
    System.out.println("\n" + "=".repeat(80));
    System.out.println("=== 결과 보고서 생성 ===");
    System.out.println("=".repeat(80));

    String report = ReportGenerator.generateHybridReport(
        hybridGA.getGenerationResults(),
        finalBest,
        bestSurvivalWeights,
        totalTime);

    // 콘솔에 출력
    System.out.println(report);

    // 파일로 저장
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String timestamp = dateFormat.format(new Date());
    String reportFilename = "report_" + timestamp + "_hybrid_approach.txt";
    ReportGenerator.saveReportToFile(report, reportFilename);

    System.out.println("\n보고서가 콘솔에 출력되었고 파일로도 저장되었습니다: docs/reports/" + reportFilename);
  }

  /**
   * 두 개의 PrintStream에 동시에 출력하는 클래스
   */
  private static class TeePrintStream extends PrintStream {
    private final PrintStream second;

    public TeePrintStream(PrintStream first, PrintStream second) {
      super(first);
      this.second = second;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
      super.write(buf, off, len);
      second.write(buf, off, len);
    }

    @Override
    public void flush() {
      super.flush();
      second.flush();
    }

    @Override
    public void close() {
      super.close();
      second.close();
    }
  }
}
