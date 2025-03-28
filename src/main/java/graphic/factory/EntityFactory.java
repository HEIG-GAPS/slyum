package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

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

    final Point middle = new Point(e.getX() - DEFAULT_SIZE.width / 2, e.getY()
                                                                      - DEFAULT_SIZE.height / 2);

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
  public void paintComponent(Graphics2D g2) {
    final Color basicColor = EntityView.getBasicColor();
    final Color fillColor = new Color(basicColor.getRed(),
                                      basicColor.getGreen(), basicColor.getBlue(), 100);

    if (GraphicView.isEntityGradient())
      g2.setPaint(new GradientPaint(bounds.x, bounds.y,
                                    fillColor, bounds.x + bounds.width, bounds.y + bounds.height,
                                    fillColor.darker()));
    else
      g2.setColor(fillColor);

    g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

    g2.setColor(fillColor.darker().darker().darker().darker().darker());
    g2.setStroke(new BasicStroke(EntityView.BORDER_WIDTH));
    g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  @Override
  public void repaint() {
    final Rectangle repaintBounds = new Rectangle(bounds);
    repaintBounds.grow(10, 10);
    parent.getScene().repaint(repaintBounds);

  }

  protected void initializeBounds(final EntityView view) {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        view.setBounds(new Rectangle(mouseReleased.x - DEFAULT_SIZE.width / 2,
                                     mouseReleased.y - DEFAULT_SIZE.height / 2, DEFAULT_SIZE.width,
                                     DEFAULT_SIZE.height));

        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            view.editingName();
          }
        });

      }
    });
  }

}
