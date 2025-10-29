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
    // compact overall while allowing a larger ring inside
    setPreferredSize(new Dimension(64, 28));
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

  // draw progress ring on left side (make ring visually larger)
  int ringSize = Math.min(h - 2, 24);
  int ringX = 6;
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
      java.awt.BasicStroke stroke = new java.awt.BasicStroke(Math.max(2f, ringSize / 6f), java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND);
      g2.setStroke(stroke);
      // draw arc from top (-90 deg) clockwise
      int pad = 2;
      g2.drawArc(ringX + pad, ringY + pad, ringSize - pad * 2, ringSize - pad * 2, 90, -(int) Math.round(sweep));
    }

  // draw text "x2" vertically centered, placed right of ring (use smaller font)
  String text = "x2";
  Font f = new Font("Segoe UI", Font.BOLD, Math.max(8, h * 2 / 6));
    g2.setFont(f);
    FontMetrics fm = g2.getFontMetrics();
  // tuck the text closer to the ring so overall width can be smaller
  int tx = ringX + ringSize + 4;
    int ty = (h + fm.getAscent()) / 2 - 2;
    g2.setColor(Color.WHITE);
    g2.drawString(text, tx, ty);

    g2.dispose();
  }
}
