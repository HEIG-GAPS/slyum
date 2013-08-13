package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.AssociationClassView;
import graphic.entity.ClassView;
import graphic.relations.BinaryView;

import java.awt.Point;
import java.awt.Rectangle;

import swing.SPanelDiagramComponent;
import utility.SMessageDialog;
import change.Change;
import classDiagram.components.AssociationClass;
import classDiagram.components.Entity;
import classDiagram.components.Visibility;
import classDiagram.relationships.Binary;

/**
 * AssociationClassFactory allows to create a new association class view
 * associated with a new association UML. Give this factory at the graphic view
 * using the method initNewComponent() for initialize a new factory. Next,
 * graphic view will use the factory to allow creation of a new component,
 * according to the specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class AssociationClassFactory extends RelationFactory {
  public final String ERROR_CREATION_MESSAGE = "Association class creation failed.\nYou must make a bond between two classes or click on an existing association.";

  /**
   * Create a new factory allowing the creation of an association class.
   * 
   * @param parent
   *          the graphic view
   * @param classDiagram
   *          the class diagram
   */
  public AssociationClassFactory(GraphicView parent) {
    super(parent);

    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
            .getBtnClassAssociation());
  }

  @Override
  public GraphicComponent create() {
    AssociationClass ac;
    AssociationClassView acv = null;
    repaint();

    if (componentMousePressed.getClass() == ClassView.class
            && componentMouseReleased.getClass() == ClassView.class) {

      final Rectangle bounds = new Rectangle(mousePressed.x
              + (mouseReleased.x - mousePressed.x) / 2, mousePressed.y
              + (mouseReleased.y - mousePressed.y) / 2 - 100,
              EntityFactory.DEFAULT_SIZE.width,
              EntityFactory.DEFAULT_SIZE.height - 5);

      final ClassView source = (ClassView) componentMousePressed;
      final ClassView target = (ClassView) componentMouseReleased;

      try {
        ac = new AssociationClass("AssociationClass", Visibility.PUBLIC,
                (Entity) source.getAssociedComponent(),
                (Entity) target.getAssociedComponent());

        boolean isRecord = Change.isRecord();
        Change.record();

        acv = new AssociationClassView(parent, ac, source, target,
                (Point) mousePressed.clone(), (Point) mouseReleased.clone(),
                bounds);

        if (!isRecord) Change.stopRecord();

        parent.addEntity(acv);
        classDiagram.addAssociationClass(ac);

        classDiagram.addBinary(ac.getAssociation());

      } catch (IllegalArgumentException a) {
        SMessageDialog.showErrorMessage(a.getMessage());
      }
    } else if (componentMousePressed instanceof BinaryView) {

      final Rectangle bounds = new Rectangle(mouseReleased.x, mouseReleased.y,
              EntityFactory.DEFAULT_SIZE.width,
              EntityFactory.DEFAULT_SIZE.height - 5);

      try {
        ac = new AssociationClass("AssociationClass", Visibility.PUBLIC,
                (Binary) componentMousePressed.getAssociedComponent());

        boolean isRecord = Change.isRecord();
        Change.record();

        acv = new AssociationClassView(parent, ac,
                (BinaryView) componentMousePressed, bounds);

        if (!isRecord) Change.stopRecord();

        parent.addEntity(acv);
        classDiagram.addAssociationClass(ac);
      } catch (IllegalArgumentException a) {
        SMessageDialog.showErrorMessage(a.getMessage());
      }
    }

    repaint();

    if (acv != null) return acv.getBinaryView();
    return null;
  }

  @Override
  protected boolean isFirstComponentValid() {
    return componentMousePressed.getClass() == ClassView.class
            || componentMousePressed instanceof BinaryView;
  }

  @Override
  protected void creationFailed() {
    SMessageDialog.showErrorMessage(ERROR_CREATION_MESSAGE);
  }
}
