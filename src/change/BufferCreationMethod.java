package change;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Method;
import classDiagram.components.SimpleEntity;

public class BufferCreationMethod implements Changeable {

  private SimpleEntity entity;
  private Method method;
  private boolean isCreated;
  private int index;

  public BufferCreationMethod(SimpleEntity e, Method m, Boolean isCreated,
          int index) {
    entity = e;
    method = m;
    this.isCreated = isCreated;
    this.index = index;
  }

  @Override
  public void restore() {
    if (!isCreated) {
      entity.addMethod(method);
      entity.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);

      entity.moveMethodPosition(method, index - entity.getMethods().size() + 1);
      entity.notifyObservers();
    } else {
      entity.removeMethod(method);
      entity.notifyObservers();
    }
  }

}
