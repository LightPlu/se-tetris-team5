#!/bin/bash
cd /Users/jeongdaun/source/se-tetris-team5

echo "유전 알고리즘 완료 대기 중..."
while ps aux | grep -q "[G]eneticAlgorithmRunner"; do
  sleep 10
  echo -n "."
done

echo ""
echo "유전 알고리즘 완료! 결과 추출 중..."

# 보고서 파일에서 최종 가중치 추출
LATEST_REPORT=$(ls -t genetic_algorithm_report_*.txt 2>/dev/null | head -1)

if [ -n "$LATEST_REPORT" ]; then
  echo "최신 보고서: $LATEST_REPORT"
  grep -A 10 "코드에 사용할 가중치" "$LATEST_REPORT" | head -5
else
  # 출력 파일에서 직접 추출
  tail -200 genetic_algorithm_output.txt | grep -A 10 "최고 가중치\|최종 최고 개체" | head -15
fi
