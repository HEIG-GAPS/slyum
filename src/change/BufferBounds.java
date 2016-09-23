package change;

import graphic.GraphicComponent;
import graphic.GraphicView;
import java.awt.Rectangle;

public class BufferBounds extends BufferGraphicView {
  Rectangle bounds;
  GraphicComponent gc;

  public BufferBounds(GraphicComponent gc) {
    super(gc.getGraphicView());
    
    this.gc = gc;
    bounds = new Rectangle(gc.getBounds());
  }

  @Override
  public void restore() {
    super.restore();
    
    Rectangle repaintBounds = gc.getBounds();
    gc.setBounds(bounds);
    gc.repaint();
    gc.getGraphicView().getScene().repaint(repaintBounds);
  }

  public Rectangle getBounds() {
    return new Rectangle(bounds);
  }

  @Override
  public Object getAssociedComponent() {
    return gc;
  }

}
