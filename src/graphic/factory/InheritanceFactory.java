package graphic.factory;

import change.BufferDeepCreation;
import change.Change;
import classDiagram.relationships.Inheritance;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.SimpleEntityView;
import graphic.relations.InheritanceView;
import java.awt.Color;
import java.awt.Graphics2D;
import swing.SPanelDiagramComponent;
import swing.Slyum;
import utility.SMessageDialog;

/**
 * InheritanceFactory allows to create a new inheritance view associated with a
 * new association UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class InheritanceFactory extends RelationFactory {
  public final String ERROR_CREATION_MESSAGE = "Inheritance creation failed.\nYou must make a bond between two classes or class -> interface.";

  /**
   * Create a new factory allowing the creation of an inheritance.
   * 
   * @param parent
   *          the graphic view
   * @param classDiagram
   *          the class diagram
   */
  public InheritanceFactory(GraphicView parent) {
    super(parent);

    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
            .getBtnGeneralize());
  }

  @Override
  public GraphicComponent create() {
    try {
      if (componentMousePressed instanceof SimpleEntityView
              && componentMouseReleased instanceof SimpleEntityView) {
        SimpleEntityView source = (SimpleEntityView) componentMousePressed;
        SimpleEntityView target = (SimpleEntityView) componentMouseReleased;

        if (!Inheritance.validate(source.getComponent(), target.getComponent())) {
          repaint();
          return null;
        }

        Inheritance inheritance = new Inheritance(source.getComponent(),
                target.getComponent());
        InheritanceView i = new InheritanceView(parent, source, target,
                inheritance, mousePressed, mouseReleased, true);

        parent.addLineView(i);
        classDiagram.addInheritance(inheritance);
      
        Change.push(new BufferDeepCreation(false, inheritance));
        Change.push(new BufferDeepCreation(true, inheritance));

        parent.unselectAll();
        i.setSelected(true);

        if (Slyum.isAutoAdjustInheritance()) i.adjustInheritance();

        return i;
      }
    } catch (final IllegalArgumentException e) {
      System.err
              .println("Inheritance relation between class (child) and interface (parent) is not possible.");
    }

    repaint();
    return null;
  }

  @Override
  protected boolean isFirstComponentValid() {
    return componentMousePressed instanceof SimpleEntityView;
  }

  @Override
  protected void drawExtremity(Graphics2D g2) {
    InheritanceView.paintExtremity(g2, points.get(points.size() - 1),
            mouseLocation, Color.DARK_GRAY);
  }

  @Override
  protected void creationFailed() {
    SMessageDialog.showErrorMessage(ERROR_CREATION_MESSAGE);
  }
}
