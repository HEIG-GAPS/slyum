package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.relations.LineCommentary;
import graphic.relations.MultiView;
import graphic.relations.RelationView;
import graphic.textbox.TextBoxCommentary;

import java.awt.BasicStroke;

import swing.SPanelDiagramComponent;
import utility.SMessageDialog;

/**
 * LineCommentaryFactory allows to create a new line between a note and a
 * graphic component. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class LineCommentaryFactory extends RelationFactory {
  public final String ERROR_CREATION_MESSAGE = "Note link creation failed.\nYou must make a bond between a note and another graphic component.";

  /**
   * Create a new factory allowing the creation of a line commentary.
   * 
   * @param parent
   *          the graphic view
   * @param classDiagram
   *          the class diagram
   */
  public LineCommentaryFactory(GraphicView parent) {
    super(parent);
    stroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f, new float[] { 4.f }, 0.0f);

    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
            .getBtnLinkNote());
  }

  @Override
  public GraphicComponent create() {
    if (!LineCommentary.checkCreate(componentMousePressed,
            componentMouseReleased, true)) return null;

    final LineCommentary lc = new LineCommentary(parent, componentMousePressed,
            componentMouseReleased, mousePressed, mouseReleased, false);
    parent.addLineView(lc);

    return lc;
  }

  @Override
  protected boolean isFirstComponentValid() {
    return componentMousePressed instanceof RelationView
            || componentMousePressed instanceof EntityView
            || componentMousePressed instanceof TextBoxCommentary
            || componentMousePressed instanceof MultiView;
  }

  @Override
  protected void creationFailed() {
    SMessageDialog.showErrorMessage(ERROR_CREATION_MESSAGE);
  }

}
