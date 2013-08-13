package change;

import graphic.ColoredComponent;
import graphic.GraphicComponent;

import java.awt.Color;

public class BufferColor implements Changeable {
  private ColoredComponent gc;
  private Color color;

  public BufferColor(ColoredComponent gc) {
    this.gc = gc;
    this.color = gc.getColor();
  }

  @Override
  public void restore() {
    gc.setColor(color);
    ((GraphicComponent) gc).repaint();
  }

}
