package graphic;

import java.awt.*;

public interface ColoredComponent {
  Color getColor();

  Color getDefaultColor();

  void setColor(Color color);

  void setDefaultStyle();

}
