package graphic.relations;

import classDiagram.IDiagramComponent.UpdateMessage;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.textbox.TextBoxCommentary;
import swing.propretiesView.NoteProperties;

import java.awt.*;

/**
 * The LineView class represent a collection of lines making a link between two GraphicComponent. When it creates, the
 * LineView have one single line between the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a segment between each grips. Grips are
 * movable and a LineView have two special grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * <p>
 * A LineCommentary is a link between a TextBoxCommentary and a GraphicComponent.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class LineCommentary extends LineView {
  public static final String ERROR_MESSAGE_DIAGRAM_COMPONENTS =
      "A link of note can only be created between a note and an UML component.";

  public static boolean checkCreate(GraphicComponent source,
                                    GraphicComponent target, boolean showMessage) {
    boolean associed = true, graphic = true, ext, ok;
    if (source == null || target == null)
      return false;

    associed = !(source.getAssociatedComponent() == null && target.getAssociatedComponent() == null);
    graphic = !(source instanceof GraphicView || target instanceof GraphicView);
    ext = source instanceof TextBoxCommentary
          || target instanceof TextBoxCommentary;
    ok = associed && graphic && ext;

    // Message only adapted for associed component null.
    // if (graphic && !ok && showMessage)
    // SMessageDialog.showErrorMessage(ERROR_MESSAGE_DIAGRAM_COMPONENTS);

    return ok;
  }

  /**
   * Create a new LineCommentary between a TextBoxCommentary and a GraphicComponent. One of the both source or target
   * must be a TextBoxCommentary.
   *
   * @param graphicView the graphic view
   * @param source a graphic component
   * @param target a graphic component
   * @param posSource the point where the first grip will be placed
   * @param posTarget the point where the last grip will be placed
   * @param checkRecursivity check if the relation is on itself
   */
  public LineCommentary(GraphicView graphicView, GraphicComponent source,
                        GraphicComponent target, Point posSource, Point posTarget,
                        boolean checkRecursivity) {
    super(graphicView, source, target, posSource, posTarget, checkRecursivity);
    setStroke(new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT,
                              BasicStroke.JOIN_MITER, 10.0f, new float[] {4.f}, 0.0f));
    NoteProperties.getInstance().updateComponentInformations(null);
    graphicView.selectOnly(this);
    notifyNoteProperties();
  }

  @Override
  public boolean relationChanged(
      MagneticGrip gripSource, GraphicComponent target) {
    if (target.equals(parent)
        || gripSource.getAssociedComponentView() instanceof TextBoxCommentary
        || target.getAssociatedComponent() == null) return false;
    changeLinkedComponent(gripSource, target);
    return true;
  }

  @Override
  public String toString() {
    if (getComponent().getAssociatedComponent() != null)
      return getComponent().getAssociatedComponent().toString();
    else
      return "unknowed link";
  }

  @Override
  public void setSelected(boolean selected) {
    super.setSelected(selected);

    if (selected)
      notifyNoteProperties();
  }

  @Override
  public void delete() {
    super.delete();
    notifyNoteProperties();
  }

  private void notifyNoteProperties() {
    NoteProperties np = NoteProperties.getInstance();
    np.update(getTextBoxCommentary(), UpdateMessage.SELECT);
    np.setSelectedItem(this);
  }

  public TextBoxCommentary getTextBoxCommentary() {
    GraphicComponent source = getFirstPoint().getAssociedComponentView(),
        target = getLastPoint().getAssociedComponentView();

    if (source.getClass() == TextBoxCommentary.class)
      return (TextBoxCommentary) source;
    else
      return (TextBoxCommentary) target;
  }

  public GraphicComponent getComponent() {
    GraphicComponent source = getFirstPoint().getAssociedComponentView(),
        target = getLastPoint().getAssociedComponentView();

    if (source.getClass() == TextBoxCommentary.class)
      return (GraphicComponent) target;
    else
      return (GraphicComponent) source;
  }

}
