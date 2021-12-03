package swing.slyumCustomizedComponents;

import javax.swing.*;
import java.awt.*;

public class SSeparator extends JSeparator {

  public SSeparator() {
    super(SwingConstants.VERTICAL);
    setPreferredSize(new Dimension(8, getHeight()));
  }

  @Override
  public void paintComponent(Graphics g) {
    final int PADDING = 0;
    int x = getWidth() / 2 - 1;
    g.setColor(Color.LIGHT_GRAY);
    g.drawLine(x, PADDING, x, getHeight() - PADDING);
    g.setColor(Color.WHITE);
    g.drawLine(x + 1, PADDING, x + 1, getHeight() - PADDING);
  }

}
