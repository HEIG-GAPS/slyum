package swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SSplitPane extends JSplitPane {
  public SSplitPane(int newOrientation, Component newLeftComponent,
          Component newRightComponent) {
    super(newOrientation, newLeftComponent, newRightComponent);
    setOneTouchExpandable(true);
    setContinuousLayout(true);
    setDividerSize(7);

    setUI(new BasicSplitPaneUI() {

      @Override
      public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this) {

          @Override
          public void setBorder(Border border) {}

          protected JButton createLeftOneTouchButton() {
            JButton b = new JButton() {
              public void setBorder(Border b) {}

              public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (splitPane != null) {
                  int[] xs = new int[3];
                  int[] ys = new int[3];
                  int blockSize;

                  // Fill the background first ...
                  g.setColor(this.getBackground());
                  g.fillRect(0, 0, this.getWidth(), this.getHeight());

                  // ... then draw the arrow.
                  if (orientation == JSplitPane.VERTICAL_SPLIT) {
                    blockSize = Math.min(getHeight(), 5);
                    xs[0] = blockSize;
                    xs[1] = 0;
                    xs[2] = blockSize << 1;
                    ys[0] = 1;
                    ys[1] = ys[2] = blockSize;
                  } else {
                    blockSize = Math.min(getWidth(), 5);
                    xs[0] = xs[2] = blockSize;
                    xs[1] = 1;
                    ys[0] = 0;
                    ys[1] = blockSize;
                    ys[2] = blockSize << 1;
                  }
                  g.setColor(Color.WHITE);
                  g.fillPolygon(xs, ys, 3);
                  g.setColor(Color.GRAY);
                  g.drawPolygon(xs, ys, 3);
                }
              }

              // Don't want the button to participate in focus traversable.
              public boolean isFocusTraversable() {
                return false;
              }
            };
            b.setMinimumSize(new Dimension(5, 5));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setRequestFocusEnabled(false);
            return b;
          }

          /**
           * Creates and return an instance of JButton that can be used to
           * collapse the right component in the split pane.
           */
          protected JButton createRightOneTouchButton() {
            JButton b = new JButton() {
              public void setBorder(Border border) {}

              public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (splitPane != null) {
                  int[] xs = new int[3];
                  int[] ys = new int[3];
                  int blockSize;

                  // Fill the background first ...
                  g.setColor(this.getBackground());
                  g.fillRect(0, 0, this.getWidth(), this.getHeight());

                  // ... then draw the arrow.
                  if (orientation == JSplitPane.VERTICAL_SPLIT) {
                    blockSize = Math.min(getHeight(), 5);
                    xs[0] = blockSize;
                    xs[1] = blockSize << 1;
                    xs[2] = 0;
                    ys[0] = blockSize;
                    ys[1] = ys[2] = 1;
                  } else {
                    blockSize = Math.min(getWidth(), 5);
                    xs[0] = xs[2] = 1;
                    xs[1] = blockSize;
                    ys[0] = 0;
                    ys[1] = blockSize;
                    ys[2] = blockSize << 1;
                  }
                  g.setColor(Color.WHITE);
                  g.fillPolygon(xs, ys, 3);
                  g.setColor(Color.GRAY);
                  g.drawPolygon(xs, ys, 3);
                }
              }

              // Don't want the button to participate in focus traversable.
              public boolean isFocusTraversable() {
                return false;
              }
            };
            b.setMinimumSize(new Dimension(5, 5));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setRequestFocusEnabled(false);
            return b;
          }
        };
      }
    });

    setBorder(null);
  }
}
