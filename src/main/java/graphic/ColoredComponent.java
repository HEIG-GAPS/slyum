package graphic;

import javafx.scene.paint.Color;

public interface ColoredComponent {
  Color getColor();

  Color getDefaultColor();

  void setColor(Color color);

  void setDefaultStyle();

}
