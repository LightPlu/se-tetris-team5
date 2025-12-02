package se.tetris.team5.gamelogic.ai.training;

import se.tetris.team5.gamelogic.ai.WeightSet;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 유전 알고리즘 결과 보고서 생성기
 */
public class ReportGenerator {
  
  /**
   * 전체 보고서 생성
   */
  public static String generateReport(List<GenerationResult> generationResults, 
                                      Individual finalBest, 
                                      long totalTimeMs) {
    StringBuilder report = new StringBuilder();
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String timestamp = dateFormat.format(new Date());
    
    // 헤더
    report.append("=".repeat(80)).append("\n");
    report.append("테트리스 AI 가중치 최적화 - 유전 알고리즘 결과 보고서\n");
    report.append("=".repeat(80)).append("\n");
    report.append("생성 시간: ").append(timestamp).append("\n");
    report.append("총 소요 시간: ").append(formatTime(totalTimeMs)).append("\n");
    report.append("\n");
    
    // 유전 알고리즘 설정 정보
    report.append("유전 알고리즘 설정\n");
    report.append("-".repeat(80)).append("\n");
    report.append("집단 크기: 20\n");
    report.append("엘리트 개체 수: 4 (20%)\n");
    report.append("최대 세대 수: 5\n");
    report.append("기본 돌연변이 확률: 0.15 (적응적)\n");
    report.append("교차 확률: 0.7\n");
    report.append("\n");
    
    // 적합도 계산 방식
    report.append("적합도 계산 방식\n");
    report.append("-".repeat(80)).append("\n");
    report.append("1. 생존 시간 보너스: 평균 생존 시간(초) × 10.0\n");
    report.append("2. 생존 보너스: 게임 수 × 20.0\n");
    report.append("3. 줄 삭제 보너스: 평균 줄 수 × 100.0\n");
    report.append("4. 점수 보너스: 평균 점수 × 0.5\n");
    report.append("5. 승리 보너스: 승리 횟수 × 500.0\n");
    report.append("6. 패배 패널티: 패배 횟수 × 1000.0\n");
    report.append("7. 조기 게임 오버 패널티: 점수 0일 때 -500.0\n");
    report.append("8. 장기 생존 보너스: 평균 19초(실제 5분) 이상 생존 시 +1000.0\n");
    report.append("\n");
    report.append("적합도 = 생존시간보너스 + 생존보너스 + 줄삭제보너스 + 점수보너스\n");
    report.append("       + 승리보너스 - 패배패널티 + 조기게임오버패널티 + 장기생존보너스\n");
    report.append("\n");
    
    // 평가 환경 정보
    report.append("평가 환경 설정\n");
    report.append("-".repeat(80)).append("\n");
    report.append("평가 모드: AI vs AI 대전 모드\n");
    report.append("빠른 평가: 게임당 최대 10초 (실제 5분 = 평가 8초)\n");
    report.append("정밀 평가: 게임당 최대 20초 (실제 5분 = 평가 19초)\n");
    report.append("게임 타이머 간격: 빠른 평가 20ms, 정밀 평가 50ms (실제 800ms)\n");
    report.append("AI 행동 주기: 빠른 평가 5ms, 정밀 평가 20ms (실제 200ms)\n");
    report.append("개체당 게임 수: 5게임\n");
    report.append("\n");
    
    // 최종 결과
    report.append("최종 최적 가중치\n");
    report.append("-".repeat(80)).append("\n");
    report.append("적합도: ").append(String.format("%.2f", finalBest.fitness)).append("\n");
    report.append("평균 줄 수: ").append(String.format("%.1f", finalBest.averageLinesPerGame)).append("\n");
    report.append("평균 점수: ").append(String.format("%.0f", finalBest.averageScorePerGame)).append("\n");
    report.append("\n");
    report.append("가중치 (El-Tetris + 대전 모드):\n");
    report.append("  Landing Height: ").append(String.format("%.2f", finalBest.weights.weightLandingHeight)).append("\n");
    report.append("  EPCM: ").append(String.format("%.2f", finalBest.weights.weightEPCM)).append("\n");
    report.append("  Row Transitions: ").append(String.format("%.2f", finalBest.weights.weightRowTransitions)).append("\n");
    report.append("  Column Transitions: ").append(String.format("%.2f", finalBest.weights.weightColumnTransitions)).append("\n");
    report.append("  Holes: ").append(String.format("%.2f", finalBest.weights.weightHoles)).append("\n");
    report.append("  Well Sums: ").append(String.format("%.2f", finalBest.weights.weightWellSums)).append("\n");
    report.append("  Attack 2 Lines: ").append(String.format("%.2f", finalBest.weights.weightAttack2Lines)).append("\n");
    report.append("  Attack 3 Lines: ").append(String.format("%.2f", finalBest.weights.weightAttack3Lines)).append("\n");
    report.append("  Attack 4 Lines (Tetris): ").append(String.format("%.2f", finalBest.weights.weightAttack4Lines)).append("\n");
    report.append("\n");
    
    // 코드 사용 예시
    report.append("코드에 사용할 가중치:\n");
    report.append("-".repeat(80)).append("\n");
    report.append(String.format(
        "new WeightSet(%.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f)\n",
        finalBest.weights.weightLandingHeight,
        finalBest.weights.weightEPCM,
        finalBest.weights.weightRowTransitions,
        finalBest.weights.weightColumnTransitions,
        finalBest.weights.weightHoles,
        finalBest.weights.weightWellSums,
        finalBest.weights.weightAttack2Lines,
        finalBest.weights.weightAttack3Lines,
        finalBest.weights.weightAttack4Lines
    ));
    report.append("\n");
    
    // 세대별 통계 (빠른 평가)
    report.append("세대별 통계 (빠른 평가)\n");
    report.append("=".repeat(80)).append("\n");
    report.append(String.format("%-6s %-12s %-12s %-12s %-12s\n", 
        "세대", "최고 적합도", "평균 적합도", "최저 적합도", "소요 시간"));
    report.append("-".repeat(80)).append("\n");
    
    for (GenerationResult result : generationResults) {
      report.append(String.format("%-6d %-12.2f %-12.2f %-12.2f %-12s\n",
          result.generation,
          result.bestFitness,
          result.avgFitness,
          result.worstFitness,
          formatTime(result.evaluationTimeMs)));
    }
    report.append("\n");
    
    // 세대별 정밀 평가 결과
    report.append("세대별 정밀 평가 결과\n");
    report.append("=".repeat(80)).append("\n");
    report.append(String.format("%-6s %-12s %-12s %-12s %-12s\n", 
        "세대", "정밀 적합도", "평균 줄 수", "평균 점수", "소요 시간"));
    report.append("-".repeat(80)).append("\n");
    
    for (GenerationResult result : generationResults) {
      if (result.accurateFitness > 0) {
        report.append(String.format("%-6d %-12.2f %-12.1f %-12.0f %-12s\n",
            result.generation,
            result.accurateFitness,
            result.accurateLinesPerGame,
            result.accurateScorePerGame,
            formatTime(result.accurateEvaluationTimeMs)));
      }
    }
    report.append("\n");
    
    // 세대별 최적 가중치
    report.append("세대별 최적 가중치\n");
    report.append("=".repeat(80)).append("\n");
    for (GenerationResult result : generationResults) {
      if (result.bestWeights != null) {
        report.append("세대 ").append(result.generation).append(":\n");
        report.append("  Landing Height: ").append(String.format("%.2f", result.bestWeights.weightLandingHeight)).append("\n");
        report.append("  EPCM: ").append(String.format("%.2f", result.bestWeights.weightEPCM)).append("\n");
        report.append("  Row Transitions: ").append(String.format("%.2f", result.bestWeights.weightRowTransitions)).append("\n");
        report.append("  Column Transitions: ").append(String.format("%.2f", result.bestWeights.weightColumnTransitions)).append("\n");
        report.append("  Holes: ").append(String.format("%.2f", result.bestWeights.weightHoles)).append("\n");
        report.append("  Well Sums: ").append(String.format("%.2f", result.bestWeights.weightWellSums)).append("\n");
        report.append("  Attack 2 Lines: ").append(String.format("%.2f", result.bestWeights.weightAttack2Lines)).append("\n");
        report.append("  Attack 3 Lines: ").append(String.format("%.2f", result.bestWeights.weightAttack3Lines)).append("\n");
        report.append("  Attack 4 Lines: ").append(String.format("%.2f", result.bestWeights.weightAttack4Lines)).append("\n");
        report.append("\n");
      }
    }
    
    // 적합도 변화 그래프 (텍스트)
    report.append("적합도 변화 그래프 (최고/평균)\n");
    report.append("-".repeat(80)).append("\n");
    generateTextGraph(report, generationResults);
    report.append("\n");
    
    // 가중치 변화 추적 (초기 vs 최종)
    if (generationResults.size() > 0) {
      GenerationResult first = generationResults.get(0);
      report.append("가중치 변화 (초기 세대 vs 최종 세대)\n");
      report.append("-".repeat(80)).append("\n");
      report.append(String.format("%-20s %-20s %-20s\n", "가중치", "초기 세대", "최종 세대"));
      report.append("-".repeat(80)).append("\n");
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "Landing Height", first.bestWeights.weightLandingHeight, finalBest.weights.weightLandingHeight));
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "EPCM", first.bestWeights.weightEPCM, finalBest.weights.weightEPCM));
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "Row Transitions", first.bestWeights.weightRowTransitions, finalBest.weights.weightRowTransitions));
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "Column Transitions", first.bestWeights.weightColumnTransitions, finalBest.weights.weightColumnTransitions));
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "Holes", first.bestWeights.weightHoles, finalBest.weights.weightHoles));
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "Well Sums", first.bestWeights.weightWellSums, finalBest.weights.weightWellSums));
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "Attack 2 Lines", first.bestWeights.weightAttack2Lines, finalBest.weights.weightAttack2Lines));
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "Attack 3 Lines", first.bestWeights.weightAttack3Lines, finalBest.weights.weightAttack3Lines));
      report.append(String.format("%-20s %-20.2f %-20.2f\n", 
          "Attack 4 Lines", first.bestWeights.weightAttack4Lines, finalBest.weights.weightAttack4Lines));
      report.append("\n");
    }
    
    // 개선 통계
    if (generationResults.size() > 1) {
      GenerationResult first = generationResults.get(0);
      GenerationResult last = generationResults.get(generationResults.size() - 1);
      double improvement = last.bestFitness - first.bestFitness;
      double improvementPercent = (improvement / Math.abs(first.bestFitness)) * 100;
      
      report.append("개선 통계\n");
      report.append("-".repeat(80)).append("\n");
      report.append(String.format("초기 최고 적합도: %.2f\n", first.bestFitness));
      report.append(String.format("최종 최고 적합도: %.2f\n", last.bestFitness));
      report.append(String.format("개선량: %.2f (%.1f%%)\n", improvement, improvementPercent));
      report.append("\n");
    }
    
    report.append("=".repeat(80)).append("\n");
    
    return report.toString();
  }
  
  /**
   * 하이브리드 방식 보고서 생성
   */
  public static String generateHybridReport(List<GenerationResult> generationResults,
                                            Individual finalBest,
                                            WeightSet bestSurvivalWeights,
                                            long totalTimeMs) {
    StringBuilder report = new StringBuilder();
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String timestamp = dateFormat.format(new Date());
    
    // 헤더
    report.append("=".repeat(80)).append("\n");
    report.append("테트리스 AI 가중치 최적화 - 하이브리드 유전 알고리즘 결과 보고서\n");
    report.append("=".repeat(80)).append("\n");
    report.append("생성 시간: ").append(timestamp).append("\n");
    report.append("총 소요 시간: ").append(formatTime(totalTimeMs)).append("\n");
    report.append("\n");
    
    // 하이브리드 방식 설명
    report.append("하이브리드 학습 방식\n");
    report.append("-".repeat(80)).append("\n");
    report.append("Step 1: 솔로 플레이로 생존 가중치 학습 (공격 가중치 고정)\n");
    report.append("  - 평가 방식: 보드 상태 기반 생존 능력 평가\n");
    report.append("  - 평가 지표: 최대 높이, 평균 높이, 구멍 수, 생존 시간\n");
    report.append("Step 2: 대전 플레이로 공격 가중치 학습 (생존 가중치 고정)\n");
    report.append("  - 평가 방식: AI vs AI 대전 모드\n");
    report.append("  - 평가 지표: 승리율, 공격 효율, 줄 삭제 수\n");
    report.append("Step 3: 통합 검증 (대전 모드 최종 평가)\n");
    report.append("\n");
    
    // 유전 알고리즘 설정
    report.append("유전 알고리즘 설정\n");
    report.append("-".repeat(80)).append("\n");
    report.append("집단 크기: ").append(20).append("\n");
    report.append("엘리트 개체 수: ").append(4).append(" (20%)\n");
    report.append("생존 가중치 학습 세대 수: ").append(5).append("\n");
    report.append("공격 가중치 학습 세대 수: ").append(5).append("\n");
    report.append("기본 돌연변이 확률: ").append(0.15).append(" (적응적)\n");
    report.append("교차 확률: ").append(0.7).append("\n");
    report.append("\n");
    
    // Step 1 결과 (생존 가중치)
    report.append("Step 1: 생존 가중치 학습 결과\n");
    report.append("-".repeat(80)).append("\n");
    report.append("최적 생존 가중치:\n");
    report.append("  Landing Height: ").append(String.format("%.2f", bestSurvivalWeights.weightLandingHeight)).append("\n");
    report.append("  EPCM: ").append(String.format("%.2f", bestSurvivalWeights.weightEPCM)).append("\n");
    report.append("  Row Transitions: ").append(String.format("%.2f", bestSurvivalWeights.weightRowTransitions)).append("\n");
    report.append("  Column Transitions: ").append(String.format("%.2f", bestSurvivalWeights.weightColumnTransitions)).append("\n");
    report.append("  Holes: ").append(String.format("%.2f", bestSurvivalWeights.weightHoles)).append("\n");
    report.append("  Well Sums: ").append(String.format("%.2f", bestSurvivalWeights.weightWellSums)).append("\n");
    report.append("\n");
    
    // 최종 결과
    report.append("최종 최적 가중치 (생존 + 공격)\n");
    report.append("-".repeat(80)).append("\n");
    report.append("적합도: ").append(String.format("%.2f", finalBest.fitness)).append("\n");
    report.append("평균 줄 수: ").append(String.format("%.1f", finalBest.averageLinesPerGame)).append("\n");
    report.append("평균 점수: ").append(String.format("%.0f", finalBest.averageScorePerGame)).append("\n");
    report.append("\n");
    report.append("가중치:\n");
    report.append("  Landing Height: ").append(String.format("%.2f", finalBest.weights.weightLandingHeight)).append("\n");
    report.append("  EPCM: ").append(String.format("%.2f", finalBest.weights.weightEPCM)).append("\n");
    report.append("  Row Transitions: ").append(String.format("%.2f", finalBest.weights.weightRowTransitions)).append("\n");
    report.append("  Column Transitions: ").append(String.format("%.2f", finalBest.weights.weightColumnTransitions)).append("\n");
    report.append("  Holes: ").append(String.format("%.2f", finalBest.weights.weightHoles)).append("\n");
    report.append("  Well Sums: ").append(String.format("%.2f", finalBest.weights.weightWellSums)).append("\n");
    report.append("  Attack 2 Lines: ").append(String.format("%.2f", finalBest.weights.weightAttack2Lines)).append("\n");
    report.append("  Attack 3 Lines: ").append(String.format("%.2f", finalBest.weights.weightAttack3Lines)).append("\n");
    report.append("  Attack 4 Lines (Tetris): ").append(String.format("%.2f", finalBest.weights.weightAttack4Lines)).append("\n");
    report.append("\n");
    
    // 코드 사용 예시
    report.append("코드에 사용할 가중치:\n");
    report.append("-".repeat(80)).append("\n");
    report.append(String.format(
        "new WeightSet(%.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f)\n",
        finalBest.weights.weightLandingHeight,
        finalBest.weights.weightEPCM,
        finalBest.weights.weightRowTransitions,
        finalBest.weights.weightColumnTransitions,
        finalBest.weights.weightHoles,
        finalBest.weights.weightWellSums,
        finalBest.weights.weightAttack2Lines,
        finalBest.weights.weightAttack3Lines,
        finalBest.weights.weightAttack4Lines
    ));
    report.append("\n");
    
    // 세대별 통계
    if (!generationResults.isEmpty()) {
      report.append("세대별 통계\n");
      report.append("=".repeat(80)).append("\n");
      report.append(String.format("%-6s %-12s %-12s %-12s %-12s\n", 
          "세대", "최고 적합도", "평균 적합도", "최저 적합도", "소요 시간"));
      report.append("-".repeat(80)).append("\n");
      
      for (GenerationResult result : generationResults) {
        report.append(String.format("%-6d %-12.2f %-12.2f %-12.2f %-12s\n",
            result.generation,
            result.bestFitness,
            result.avgFitness,
            result.worstFitness,
            formatTime(result.evaluationTimeMs)));
      }
      report.append("\n");
    }
    
    report.append("=".repeat(80)).append("\n");
    
    return report.toString();
  }

  /**
   * 텍스트 그래프 생성
   */
  private static void generateTextGraph(StringBuilder report, List<GenerationResult> results) {
    if (results.isEmpty()) return;
    
    // 최고값과 최저값 찾기
    double maxFitness = results.stream().mapToDouble(r -> r.bestFitness).max().orElse(0);
    double minFitness = results.stream().mapToDouble(r -> r.avgFitness).min().orElse(0);
    double range = maxFitness - minFitness;
    if (range == 0) range = 1;
    
    int graphWidth = 60;
    
    // 최고 적합도 그래프
    report.append("최고 적합도:\n");
    for (GenerationResult result : results) {
      int barLength = (int) ((result.bestFitness - minFitness) / range * graphWidth);
      String bar = "█".repeat(Math.max(0, barLength));
      report.append(String.format("세대 %2d: %s %.2f\n", 
          result.generation, bar, result.bestFitness));
    }
    report.append("\n");
    
    // 평균 적합도 그래프
    report.append("평균 적합도:\n");
    for (GenerationResult result : results) {
      int barLength = (int) ((result.avgFitness - minFitness) / range * graphWidth);
      String bar = "█".repeat(Math.max(0, barLength));
      report.append(String.format("세대 %2d: %s %.2f\n", 
          result.generation, bar, result.avgFitness));
    }
  }
  
  /**
   * 시간 포맷팅
   */
  private static String formatTime(long ms) {
    if (ms < 1000) {
      return ms + "ms";
    } else if (ms < 60000) {
      return String.format("%.1f초", ms / 1000.0);
    } else {
      return String.format("%.1f분", ms / 60000.0);
    }
  }
  
  /**
   * 보고서를 파일로 저장
   */
  public static void saveReportToFile(String report, String filename) {
    try {
      java.nio.file.Path path = java.nio.file.Paths.get("docs/reports", filename);
      java.nio.file.Files.createDirectories(path.getParent());
      java.nio.file.Files.write(
          path,
          report.getBytes(java.nio.charset.StandardCharsets.UTF_8)
      );
      System.out.println("보고서가 저장되었습니다: " + path.toString());
    } catch (Exception e) {
      System.err.println("보고서 저장 실패: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

