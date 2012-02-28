package change;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Entity;
import classDiagram.components.Method;

public class BufferCreationMethod implements Changeable
{

  private Entity entity;
  private Method method;
  private boolean isCreated;
  
  public BufferCreationMethod(Entity e, Method m, Boolean isCreated)
  {
    entity = e;
    method = m;
    this.isCreated = isCreated;
  }

  @Override
  public void restore()
  {
    if (!isCreated)
    {
      entity.addMethod(method);
      entity.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);
    }
    else
    {
      entity.removeMethod(method);
      entity.notifyObservers();
    } 
  }

}
