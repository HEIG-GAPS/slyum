package swing;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class FlatPanel extends JPanel {

  public FlatPanel() {
    initialize();
  }

  public FlatPanel(LayoutManager layout) {
    super(layout);
    initialize();
  }

  public FlatPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
    initialize();
  }

  public FlatPanel(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
    initialize();
  }

  private void initialize() {
    setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, Slyum.THEME_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    setBackground(Slyum.BACKGROUND_FORHEAD);
    setForeground(Color.GRAY);
  }

}
