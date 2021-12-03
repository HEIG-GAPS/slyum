package swing.slyumCustomizedComponents;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Miserez
 */
public class SScrollPane extends JScrollPane {

  public SScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
    super(view, vsbPolicy, hsbPolicy);
    initialize();
  }

  public SScrollPane(Component view) {
    super(view);
    initialize();
  }

  public SScrollPane(int vsbPolicy, int hsbPolicy) {
    super(vsbPolicy, hsbPolicy);
    initialize();
  }

  public SScrollPane() {
    initialize();
  }

  private void initialize() {
    setBackground(Color.white);
  }

  @Override
  public JScrollBar createHorizontalScrollBar() {
    return new SScrollBar(JScrollPane.ScrollBar.HORIZONTAL);
  }

  @Override
  public JScrollBar createVerticalScrollBar() {
    return new SScrollBar(JScrollPane.ScrollBar.VERTICAL);
  }

}
