package change;

import graphic.GraphicComponent;

public class BufferCreation extends BufferGraphicView {
  private boolean isCreated;
  private GraphicComponent gc;

  public BufferCreation(boolean isCreated, GraphicComponent gc) {
    super(gc.getGraphicView());
    this.isCreated = isCreated;
    this.gc = gc;
  }

  @Override
  public void restore() {
    super.restore();
    _restore();
  }
  
  public void _restore() {
    
    if (isCreated) {
      gc.restore();
    } else {
      final boolean isBlocked = Change.isBlocked();
      Change.setBlocked(true);
      gc.delete();
      Change.setBlocked(isBlocked);
    }
  }

  @Override
  public Object getAssociedComponent() {
    return gc;
  }
}
