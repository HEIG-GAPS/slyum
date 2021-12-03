package swing.slyumCustomizedComponents;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * TODO comment...
 *
 * @author David Miserez
 */
public class SScrollBar extends JScrollBar {

  public SScrollBar(int orientation, int value,
                    int extent, int min, int max) {
    super(orientation, value, extent, min, max);
    initialize();
  }

  public SScrollBar(int orientation) {
    super(orientation);
    initialize();
  }

  public SScrollBar() {
    initialize();
  }

  private void initialize() {
    setUI(new BasicScrollBarUI() {

      @Override
      protected void paintThumb(final Graphics g, final JComponent c,
                                final Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !this.scrollbar.isEnabled())
          return;

        int rounded = 2;
        Rectangle dimension;
        Color background, border;

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
          dimension = new Rectangle(
              thumbBounds.x + 3, thumbBounds.y + 2,
              thumbBounds.width - 6, thumbBounds.height - 6);
        else
          dimension = new Rectangle(
              thumbBounds.x + 2, thumbBounds.y + 3,
              thumbBounds.width - 6, thumbBounds.height - 6);

        if (isThumbRollover()) {
          if (isDragging) {
            background = new Color(145, 145, 145);
            border = new Color(126, 126, 126);
          } else {
            background = new Color(193, 193, 193);
            border = new Color(169, 169, 169);
          }
        } else {
          background = new Color(217, 217, 217);
          border = new Color(189, 189, 189);
        }

        g.setColor(background);
        g.fillRoundRect(dimension.x, dimension.y, dimension.width,
                        dimension.height, rounded, rounded);
        g.setColor(border);
        g.drawRoundRect(dimension.x, dimension.y, dimension.width,
                        dimension.height, rounded, rounded);
      }

      @Override
      public Dimension getPreferredSize(JComponent c) {
        return (scrollbar.getOrientation() == JScrollBar.VERTICAL)
            ? new Dimension(15, 0)
            : new Dimension(0, 15);
      }

      @Override
      protected void paintTrack(final Graphics g, final JComponent c,
                                final Rectangle trackBounds) {
        Rectangle dimension;
        Color background = new Color(242, 242, 242),
            border = new Color(219, 219, 219);

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
          dimension = new Rectangle(
              trackBounds.x, trackBounds.y,
              trackBounds.width, trackBounds.height - 1);
        else
          dimension = new Rectangle(
              trackBounds.x, trackBounds.y,
              trackBounds.width - 1, trackBounds.height);

        g.setColor(background);
        g.fillRect(dimension.x, dimension.y, dimension.width, dimension.height);
        g.setColor(border);
        g.drawRect(dimension.x, dimension.y, dimension.width, dimension.height);
      }

      @Override
      protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
      }

      @Override
      protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
      }

      private JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
      }
    });
  }

}
