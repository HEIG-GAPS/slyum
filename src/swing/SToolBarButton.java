package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Icon;

public class SToolBarButton extends SButton {

  public SToolBarButton(Icon icon, Color color, String tooltip) {
    super(icon, color, tooltip);
    init();
  }

  public SToolBarButton(Icon icon, String action, Color color, String tooltip,
      ActionListener al) {
    super(icon, action, color, tooltip, al);
    init();
  }

  public SToolBarButton(String text, String action, Color color,
      String tooltip, ActionListener al) {
    super(text, action, color, tooltip, al);
    init();
  }
  
  private void init() {
    setPreferredSize(new Dimension(22, 18));
  }
}
