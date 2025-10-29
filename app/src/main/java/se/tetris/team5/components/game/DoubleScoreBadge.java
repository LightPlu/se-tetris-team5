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

    // draw progress ring and text centered as a group
    int ringSize = Math.min(h - 2, 24);
    String x2 = "x2";
    Font fMain = new Font("Segoe UI", Font.BOLD, Math.max(8, h * 2 / 6));
    g2.setFont(fMain);
    FontMetrics fmMain = g2.getFontMetrics();
    int x2W = fmMain.stringWidth(x2);
    int spacing = Math.max(6, h / 8);
    String secsText = "";
    if (remainingMillis > 0) {
      int secs = (int) ((remainingMillis + 500) / 1000);
      secsText = String.valueOf(secs) + "s";
    }
    Font fSmall = new Font("Segoe UI", Font.PLAIN, Math.max(10, h / 3));
    g2.setFont(fSmall);
    FontMetrics fmSmall = g2.getFontMetrics();
    int secsW = (secsText.isEmpty() ? 0 : fmSmall.stringWidth(secsText));

    int totalW = ringSize + spacing + x2W + (secsW > 0 ? spacing + secsW : 0);
    int groupX = Math.max(4, (w - totalW) / 2);
    int ringX = groupX;
    int ringY = (h - ringSize) / 2;

    // background ring
    g2.setColor(new Color(255, 255, 255, 60));
    g2.fillOval(ringX, ringY, ringSize, ringSize);

    if (remainingMillis > 0) {
      double frac = Math.max(0.0, Math.min(1.0, (double) remainingMillis / (double) totalMillis));
      double sweep = 360.0 * frac;
      Color arcColor = new Color(255, 255, 255, 200);
      if (frac < 0.25) arcColor = new Color(255, 140, 60, 220);
      else if (frac < 0.5) arcColor = new Color(255, 190, 100, 220);
      else arcColor = new Color(255, 255, 255, 220);
      g2.setColor(arcColor);
      java.awt.BasicStroke stroke = new java.awt.BasicStroke(Math.max(2f, ringSize / 6f), java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND);
      g2.setStroke(stroke);
      int pad = 2;
      g2.drawArc(ringX + pad, ringY + pad, ringSize - pad * 2, ringSize - pad * 2, 90, -(int) Math.round(sweep));
    }

    // draw x2
    int textX = ringX + ringSize + spacing;
    int textY = (h + fmMain.getAscent()) / 2 - 2;
    g2.setFont(fMain);
    g2.setColor(Color.WHITE);
    g2.drawString(x2, textX, textY);

    // draw seconds (if any) to the right of x2, smaller font
    if (!secsText.isEmpty()) {
      int secsX = textX + x2W + spacing;
      int secsY = (h + fmSmall.getAscent()) / 2 - 1;
      g2.setFont(fSmall);
      g2.drawString(secsText, secsX, secsY);
    }

    g2.dispose();
  }
}
