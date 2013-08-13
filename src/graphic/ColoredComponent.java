package graphic;

import java.awt.Color;

public interface ColoredComponent {
  Color getColor();

  Color getDefaultColor();

  void setColor(Color color);

  void setDefaultStyle();
}
