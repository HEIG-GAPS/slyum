package change;

import graphic.ColoredComponent;
import graphic.GraphicComponent;
import graphic.GraphicView;
import java.awt.Color;

public class BufferColor extends BufferGraphicView {
  private ColoredComponent gc;
  private Color color;

  public BufferColor(ColoredComponent gc, GraphicView graphicView) {
    super(graphicView);
    this.gc = gc;
    this.color = gc.getColor();
  }

  @Override
  public void restore() {
    super.restore();
    
    gc.setColor(color);
    ((GraphicComponent) gc).repaint();
  }

  @Override
  public Object getAssociedComponent() {
    return gc;
  }

}
