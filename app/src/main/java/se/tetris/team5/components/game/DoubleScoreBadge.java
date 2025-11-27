package se.tetris.team5.components.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/**
 * Small badge component that shows "x2" and a circular progress ring for remaining time.
 */
public class DoubleScoreBadge extends JComponent {
  private static final long serialVersionUID = 1L;

  // remaining and total in milliseconds
  private long remainingMillis = 0L;
  private long totalMillis = 20_000L; // default 20s

  public DoubleScoreBadge() {
    setOpaque(false);
    // 컴팩트한 크기로 조정하여 점수 패널에서 적절한 비율 유지
    setPreferredSize(new Dimension(90, 24));
    setMinimumSize(new Dimension(90, 24));
    setMaximumSize(new Dimension(90, 24));
    setToolTipText("Double score active");
  }

  public void setTotalMillis(long totalMillis) {
    this.totalMillis = Math.max(1, totalMillis);
    repaint();
  }

  public void setRemainingMillis(long rem) {
    this.remainingMillis = Math.max(0L, rem);
    // update tooltip to show seconds
    if (remainingMillis > 0) {
      int secs = (int) ((remainingMillis + 500) / 1000);
      setToolTipText("Double score: " + secs + "s remaining");
    } else {
      setToolTipText("Double score inactive");
    }
    repaint();
  }

  /**
   * 윈도우/macOS에서 한글을 제대로 표시하기 위한 폰트 생성 메서드
   */
  private Font createKoreanFont(int style, int size) {
    // 윈도우/macOS에서 한글을 잘 지원하는 폰트들을 우선순위대로 시도
    String[] koreanFonts = {"맑은 고딕", "Malgun Gothic", "굴림", "Gulim", "Arial Unicode MS", "Dialog"};
    
    for (String fontName : koreanFonts) {
      Font font = new Font(fontName, style, size);
      // 폰트가 시스템에 있는지 확인
      if (font.getFamily().equals(fontName) || font.canDisplay('한')) {
        return font;
      }
    }
    
    // 모든 한글 폰트가 실패하면 기본 Dialog 폰트 사용
    return new Font(Font.DIALOG, style, size);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int w = getWidth();
    int h = getHeight();
    // background rounded rect
    int arc = Math.max(8, h / 2);
    g2.setColor(new Color(120, 180, 255));
    g2.fillRoundRect(0, 0, w, h, arc, arc);

  // draw progress ring on left side (컴팩트한 크기로 조정)
  int ringSize = Math.min(h - 4, 18);
  int ringX = 4;
    int ringY = (h - ringSize) / 2;
    // background ring
    g2.setColor(new Color(255, 255, 255, 60));
    g2.fillOval(ringX, ringY, ringSize, ringSize);

    if (remainingMillis > 0) {
      double frac = Math.max(0.0, Math.min(1.0, (double) remainingMillis / (double) totalMillis));
      double sweep = 360.0 * frac;
      // arc color transitions: full -> pale blue, nearing end -> orange/red
      Color arcColor = new Color(255, 255, 255, 200);
      if (frac < 0.25) arcColor = new Color(255, 140, 60, 220);
      else if (frac < 0.5) arcColor = new Color(255, 190, 100, 220);
      else arcColor = new Color(255, 255, 255, 220);
      g2.setColor(arcColor);
      java.awt.BasicStroke stroke = new java.awt.BasicStroke(Math.max(2f, ringSize / 5f), java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND);
      g2.setStroke(stroke);
      // draw arc from top (-90 deg) clockwise
      int pad = 2;
      g2.drawArc(ringX + pad, ringY + pad, ringSize - pad * 2, ringSize - pad * 2, 90, -(int) Math.round(sweep));
    }

  // draw text "x2" vertically centered, placed right of ring (use smaller font)
  String text = "x2";
  // macOS/Windows 크로스 플랫폼 한글 지원 폰트 사용
  Font f = createKoreanFont(Font.BOLD, Math.max(8, h * 2 / 6));
    g2.setFont(f);
    FontMetrics fm = g2.getFontMetrics();
  // tuck the text closer to the ring so overall width can be smaller
  int tx = ringX + ringSize + 3;
    int ty = (h + fm.getAscent()) / 2 - 2;
    g2.setColor(Color.WHITE);
    g2.drawString(text, tx, ty);
    
    // 남은 시간을 초 단위로 표시 (x2 오른쪽에, 더 작은 폰트)
    if (remainingMillis > 0) {
      int remainingSecs = (int) Math.ceil(remainingMillis / 1000.0);
      String timeText = remainingSecs + "초";
      // macOS/Windows 크로스 플랫폼 한글 지원 폰트 사용
      Font timeFont = createKoreanFont(Font.BOLD, Math.max(8, h / 3));
      g2.setFont(timeFont);
      FontMetrics timeFm = g2.getFontMetrics();
      int timeTx = tx + fm.stringWidth(text) + 4;
      int timeTy = (h + timeFm.getAscent()) / 2 - 2;
      g2.setColor(new Color(255, 255, 255, 220));
      g2.drawString(timeText, timeTx, timeTy);
    }

    g2.dispose();
  }
}
