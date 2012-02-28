package change;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Attribute;
import classDiagram.components.Entity;

public class BufferCreationAttribute implements Changeable
{
  private Entity entity;
  private Attribute attribute;
  private boolean isCreated;
  
  public BufferCreationAttribute(Entity e, Attribute a, Boolean isCreated)
  {
    entity = e;
    attribute = a;
    this.isCreated = isCreated;
  }

  @Override
  public void restore()
  {
    
    if (!isCreated)
    {
      entity.addAttribute(attribute);
      entity.notifyObservers(UpdateMessage.ADD_ATTRIBUTE_NO_EDIT);
    }
    else
    {
      entity.removeAttribute(attribute);
      entity.notifyObservers();
    }
    
  }

}
