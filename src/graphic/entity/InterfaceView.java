package graphic.entity;

import graphic.GraphicView;
import classDiagram.components.Attribute;
import classDiagram.components.EnumEntity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;

/**
 * Represent the view of an interface in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class InterfaceView extends SimpleEntityView {

  /**
   * Create a new interface associated with entity component.
   * 
   * @param parent
   *          the graphic component
   * @param component
   *          the interface (UML)
   */
  public InterfaceView(GraphicView parent, InterfaceEntity component) {
    super(parent, component);

    // Change.push(new BufferCreation(false, this));
    // Change.push(new BufferCreation(true, this));
  }

  @Override
  protected void prepareNewAttribute(Attribute attribute) {
    attribute.setStatic(true);
  }

  @Override
  protected void prepareNewMethod(Method method) {
    method.setAbstract(true);
  }

  @Override
  public void restore() {
    super.restore();
    parent.addEntity(this);
    restoreEntity();
    repaint();
  }

  protected void restoreEntity() {
    if (!existsInOthersViews())
      parent.getClassDiagram().addInterfaceEntity((InterfaceEntity) getAssociedComponent());
  }
}
