package classDiagram.relationships;

import classDiagram.IDiagramComponent;
import classDiagram.components.Entity;

public interface Relation extends IDiagramComponent {
  public Entity getSource();

  public void setSource(Entity entity);

  public Entity getTarget();

  public void setTarget(Entity entity);
}
