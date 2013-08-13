package graphic.entity;

import graphic.GraphicView;
import classDiagram.components.Attribute;
import classDiagram.components.ClassEntity;
import classDiagram.components.Method;

/**
 * Represent the view of a class in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class ClassView extends SimpleEntityView {

  /**
   * Create a new view from the given class.
   * 
   * @param parent
   *          the graphic view
   * @param component
   *          the class (UML)
   */
  public ClassView(GraphicView parent, ClassEntity component) {
    super(parent, component);
  }

  @Override
  protected void prepareNewAttribute(Attribute attribute) {}

  @Override
  protected void prepareNewMethod(Method method) {}

  @Override
  public void restore() {
    super.restore();
    parent.addEntity(this);
    restoreEntity();
    repaint();
  }

  protected void restoreEntity() {
    parent.getClassDiagram().addClassEntity(
            (ClassEntity) getAssociedComponent());
  }
}
