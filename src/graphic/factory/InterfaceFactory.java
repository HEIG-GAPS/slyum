package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.entity.InterfaceView;
import swing.SPanelDiagramComponent;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Visibility;
import javax.swing.SwingUtilities;

/**
 * InterfaceFactory allows to create a new interface view associated with a new
 * class UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class InterfaceFactory extends EntityFactory {

  /**
   * Create a new factory allowing the creation of an interface.
   * 
   * @param parent
   *          the graphic view
   */
  public InterfaceFactory(GraphicView parent) {
    super(parent);
    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
               .getBtnInterface());
  }

  @Override
  public GraphicComponent create() {
    InterfaceEntity ie = new InterfaceEntity("Interface", Visibility.PUBLIC);
    final EntityView i = new InterfaceView(parent, ie);

    parent.addEntity(i);
    classDiagram.addInterfaceEntity(ie);
    initializeBounds(i);
    
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        i.editingName();
      }
    });
    
    return i;
  }

}
