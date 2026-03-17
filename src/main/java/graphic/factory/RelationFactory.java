package graphic.factory;

import change.Change;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.relations.LineView;
import graphic.relations.RelationGrip;
import utility.Utility;

import java.awt.*;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * RelationFactory allows to create a new relation view associated with a new relation UML. Give this factory at the
 * graphic view using the method initNewComponent() for initialize a new factory. Next, graphic view will use the
 * factory to allow creation of a new component, according to the specificity of the factory.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class RelationFactory extends ComponentFactory {
  private GraphicComponent componentMouseHover;
  protected Point mouseLocation = new Point();
  protected BasicStroke stroke = new BasicStroke(1.2f);
  protected List<Point> points = new LinkedList<>();

  public RelationFactory(GraphicView parent) {
    super(parent);

    componentMouseHover = parent;
  }

  /**
   * Change style of component were mouse pressed.
   *
   * @param e mouse event
   */
  private void drawComponentMouseHoverStyle(MouseEvent e) {
    final GraphicComponent currentComponent = parent.getComponentAtPosition(new Point((int)e.getX(), (int)e.getY()));

    if (componentMouseHover != currentComponent) {
      currentComponent.setMouseHoverStyle();

      if (componentMouseHover != componentMousePressed)
        componentMouseHover.setDefaultStyle();

      componentMouseHover = currentComponent;
    }
  }

  /**
   * Draw the extremity at the location of the mouse.
   *
   * @param g2 the graphic context
   */
  protected void drawExtremity(GraphicsContext gc) {
    // no extremity
  }

  /**
   * Draw a line between points and mouse location. pressed.
   *
   * @param g2 the graphic context
   */
  private void drawLine(GraphicsContext gc) {
    if (points.isEmpty()) return;
    int gray = Utility.getColorGrayLevel(parent.getColor());
    Point p, p1, p2;
    gc.setLineWidth(stroke.getLineWidth());
    gc.setStroke(javafx.scene.paint.Color.rgb(gray, gray, gray, 200.0/255));
    for (int i = 0; i < points.size() - 1; i++) {
      p1 = points.get(i);
      p2 = points.get(i + 1);
      gc.strokeLine(p1.x, p1.y, p2.x, p2.y);
    }
    p = points.get(points.size() - 1);
    gc.strokeLine(p.x, p.y, mouseLocation.x, mouseLocation.y);
  }

  @Override
  public Cursor getCursor() {
    return new Cursor(Cursor.CROSSHAIR_CURSOR);
  }

  @Override
  public void gMouseDragged(MouseEvent e) {
    super.gMouseDragged(e);
    gMouseMoved(e);
    /*mouseLocation = e.getPoint(); mouseLocation = new
     * Point(RelationGrip.adjust(mouseLocation.x),
     * RelationGrip.adjust(mouseLocation.y)); Rectangle repaintBounds = new
     * Rectangle(mousePressed.x, mousePressed.y, mouseLocation.x -
     * mousePressed.x, mouseLocation.y - mousePressed.y); Rectangle
     * repaintExtremity = new Rectangle(mouseLocation.x - 50, mouseLocation.y -
     * 50, 100, 100); // 100 // take // arbitrary repaintBounds =
     * Utility.normalizeRect(repaintBounds);
     * parent.getScene().repaint(repaintBounds);
     * parent.getScene().repaint(repaintExtremity); repaintExtremity = new
     * Rectangle(mousePressed.x - 50, mousePressed.y - 50, 100, 100); // 100 //
     * take // arbitrary parent.getScene().repaint(repaintExtremity);
     * repaintBounds = new Rectangle(mousePressed.x, mousePressed.y,
     * mouseLocation.x - mousePressed.x, mouseLocation.y - mousePressed.y);
     * repaintExtremity = new Rectangle(mouseLocation.x, mouseLocation.y, 100,
     * 100); parent.getScene().repaint(repaintExtremity);
     * parent.getScene().repaint(Utility.normalizeRect(repaintBounds));
     * drawComponentMouseHoverStyle(e); */
  }

  @Override
  public void gMouseMoved(MouseEvent e) {
    mouseLocation = new Point((int)e.getX(), (int)e.getY());
    mouseLocation = new Point(RelationGrip.adjust(mouseLocation.x),
                              RelationGrip.adjust(mouseLocation.y));
    drawComponentMouseHoverStyle(e);
    repaint();
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    super.gMousePressed(e);
    mousePressed = new Point((int)e.getX(), (int)e.getY());

    componentMouseHover.setStyleClicked();

    // Le premier point doit être ajouté lorsque l'on presse sur la souris.
    // Les suivants le sont quand on relâche la souris.
    if (points.isEmpty()) {
      if (isFirstComponentValid())
        points.add(mouseLocation);
      else
        componentMousePressed = null;
    }
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    final GraphicComponent view;
    RelationGrip grip;
    mouseReleased = mouseLocation;

    if (points.isEmpty()) return;

    componentMouseReleased = parent.getComponentAtPosition(mouseReleased);

    boolean isRecord = Change.isRecord();
    Change.record();

    points.add(mouseLocation);
    if ((createdComponent = view = create()) != null) {
      parent.deleteCurrentFactory();
      componentMousePressed.setDefaultStyle();
      componentMouseReleased.setDefaultStyle();

      if (view instanceof LineView && points.size() > 2) {
        ((LineView) view).removeAllGrip();

        // Middle grip
        for (int i = 1; i < points.size() - 1; i++) {
          grip = new RelationGrip(parent, (LineView) view);
          grip.setAnchor(points.get(i));
          ((LineView) view).addGrip(grip, i);
        }

        // Magnetic grip
        ((LineView) view).getFirstPoint().setAnchor(points.get(0));
        ((LineView) view).getLastPoint().setAnchor(
            points.get(points.size() - 1));

        Platform.runLater(() -> {
          ((LineView) view).reinitializeTextBoxesLocation();
          ((LineView) view).setSelected(true);
          ((LineView) view).notifyObservers();
        });
      }
    }

    if (!isRecord)
      Change.stopRecord();
  }

  @Override
  public void paintComponent(GraphicsContext gc) {
    drawLine(gc);
    if (componentMousePressed != null) drawExtremity(gc);
  }

  @Override
  public GraphicComponent create() {
    return null;
  }

  @Override
  public void repaint() {
    if (!points.isEmpty()) {
      Point p = points.get(0);
      Rectangle repaintBounds = new Rectangle(p.x, p.y, 1, 1);
      for (int i = 1; i < points.size(); i++)
        repaintBounds.add(points.get(i));
      repaintBounds.add(mouseLocation);
      repaintBounds.grow(100, 100);
      parent.getScene().repaint(repaintBounds);
    }
  }

  protected abstract boolean isFirstComponentValid();

}
