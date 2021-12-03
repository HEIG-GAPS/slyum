package swing.slyumCustomizedComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SToolBarButton extends SButton {

  public SToolBarButton() {
    super();
    init();
  }

  public SToolBarButton(Icon icon, Color color, String tooltip) {
    super(icon, tooltip);
    init();
  }

  public SToolBarButton(Icon icon, String action, Color color, String tooltip,
                        ActionListener al) {
    super(icon, action, tooltip, al);
    init();
  }

  private void init() {
    setMaximumSize(new Dimension(22, 18));
    setPreferredSize(new Dimension(22, 18));
  }

}
