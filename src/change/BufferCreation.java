package change;

import graphic.GraphicComponent;
import graphic.GraphicView;

public class BufferCreation implements Changeable {
  private boolean isCreated;
  private GraphicComponent gc;

  public BufferCreation(boolean isCreated, GraphicComponent gc) {
    this.isCreated = isCreated;
    this.gc = gc;
  }

  @Override
  public void restore() {
    if (isCreated) {
      gc.restore();
    } else {
      GraphicView gv = gc.getGraphicView();
      final boolean isBlocked = Change.isBlocked(gv);
      Change.setBlocked(true, gv);
      gc.lightDelete();
      Change.setBlocked(isBlocked, gv);
    }
  }

  @Override
  public Object getAssociedComponent() {
    return gc;
  }
}
