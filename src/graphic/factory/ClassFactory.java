package graphic.factory;

import classDiagram.components.ClassEntity;
import classDiagram.components.Visibility;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.ClassView;
import graphic.entity.EntityView;
import swing.SPanelDiagramComponent;

/**
 * ClassFactory allows to create a new class view associated with a new class
 * UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class ClassFactory extends EntityFactory {

  /**
   * Create a new factory allowing the creation of a class.
   * 
   * @param parent
   *          the graphic view
   */
  public ClassFactory(GraphicView parent) {
    super(parent);

    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
            .getBtnClass());
  }

  @Override
  public GraphicComponent create() {
    final ClassEntity classEntity = new ClassEntity("Class", Visibility.PUBLIC);
    final EntityView c = new ClassView(parent, classEntity);

    initializeBounds(c);
    parent.addEntity(c);
    classDiagram.addClassEntity(classEntity);
    
    return c;
  }

}
