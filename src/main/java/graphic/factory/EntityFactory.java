package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;

import java.awt.*;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 * EntityFactory allows to create a new entity view associated with a new entity UML. Give this factory at the graphic
 * view using the method initNewComponent() for initialize a new factory. Next, graphic view will use the factory to
 * allow creation of a new component, according to the specificity of the factory.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class EntityFactory extends ComponentFactory {
  public final static Dimension DEFAULT_SIZE = new Dimension(150, 61);
  private final Rectangle bounds;

  public EntityFactory(GraphicView parent) {
    super(parent);
    bounds = new Rectangle(0, 0, DEFAULT_SIZE.width, DEFAULT_SIZE.height);
  }

  @Override
  public abstract GraphicComponent create();

  @Override
  public Rectangle getBounds() {
    return new Rectangle(bounds);
  }

  @Override
  public Cursor getCursor() {
    return new Cursor(Cursor.MOVE_CURSOR);
  }

  @Override
  public void gMouseMoved(MouseEvent e) {
    Rectangle repaintBounds = new Rectangle(bounds);
    repaintBounds.grow(20, 20);

    final Point middle = new Point((int)e.getX() - DEFAULT_SIZE.width / 2, (int)e.getY() - DEFAULT_SIZE.height / 2);

    int gs = GraphicView.getGridSize();
    int x = (middle.x / gs) * gs;
    int y = (middle.y / gs) * gs;

    bounds.setLocation(x, y);

    parent.getScene().repaint(repaintBounds);
    repaintBounds = new Rectangle(bounds);
    repaintBounds.grow(20, 20);
    parent.getScene().repaint(repaintBounds);
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    super.gMouseReleased(e);

    repaint();
  }

  @Override
  public void paintComponent(GraphicsContext gc) {
    final javafx.scene.paint.Color basicColor = EntityView.getBasicColor();
    final javafx.scene.paint.Color fillColor = javafx.scene.paint.Color.rgb((int)(basicColor.getRed()*255), (int)(basicColor.getGreen()*255), (int)(basicColor.getBlue()*255), 100.0/255);

    gc.setFill(fillColor);
    gc.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    gc.setStroke(fillColor.darker().darker().darker());
    gc.setLineWidth(EntityView.BORDER_WIDTH);
    gc.strokeRect(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  @Override
  public void repaint() {
    final Rectangle repaintBounds = new Rectangle(bounds);
    repaintBounds.grow(10, 10);
    parent.getScene().repaint(repaintBounds);

  }

  protected void initializeBounds(final EntityView view) {

    Platform.runLater(() -> {
      view.setBounds(new Rectangle(mouseReleased.x - DEFAULT_SIZE.width / 2,
                                   mouseReleased.y - DEFAULT_SIZE.height / 2, DEFAULT_SIZE.width,
                                   DEFAULT_SIZE.height));
      Platform.runLater(view::editingName);
    });
  }

}
