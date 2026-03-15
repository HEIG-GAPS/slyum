package graphic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * The SquareGrip class represent a (little) gray square. By default, he does nothing but show or hide itself then user
 * entered or exited it.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class SquareGrip extends GraphicComponent {
  public static final float DEFAULT_BORDER_WIDTH = 1.2f;

  protected Rectangle bounds = new Rectangle();
  protected Cursor cursor = Cursor.getDefaultCursor();
  protected Cursor previousCursor = cursor;
  protected boolean previousVisible = isVisible();

  public SquareGrip(GraphicView parent, int size, int cursor) {
    super(parent);

    bounds.width = bounds.height = size;

    this.cursor = new Cursor(cursor);
  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(bounds);
  }

  @Override
  public void gMouseEntered(MouseEvent e) {
    previousVisible = isVisible();
    previousCursor = parent.getScene().getCursor();
    parent.getScene().setCursor(cursor);
    setVisible(true);
  }

  @Override
  public void gMouseExited(MouseEvent e) {
    parent.getScene().setCursor(previousCursor);
    setVisible(previousVisible);
  }

  @Override
  public boolean isAtPosition(Point mouse) {
    return getBounds().contains(mouse);
  }

  @Override
  public void paintComponent(GraphicsContext gc) {
    if (!isVisible()) return;

    final Rectangle localBounds = getBounds();

    gc.setLineWidth(DEFAULT_BORDER_WIDTH);
    gc.setFill(getFillColor());
    gc.fillRect(
        localBounds.x, localBounds.y, localBounds.width, localBounds.height);

    gc.setStroke(getBorderColor());
    gc.strokeRect(
        localBounds.x, localBounds.y, localBounds.width, localBounds.height);
  }

  public Color getFillColor() {
    return Color.rgb(200, 200, 200);
  }

  public Color getBorderColor() {
    return Color.rgb(40, 40, 40);
  }

  @Override
  public void repaint() {
    final Rectangle repaintBounds = getBounds();
    repaintBounds.grow(10, 10);
    parent.getScene().repaint(repaintBounds);
  }

  @Override
  public void setBounds(Rectangle bounds) {
    final Rectangle repaintBounds = new Rectangle(bounds);

    this.bounds = new Rectangle(bounds);

    parent.getScene().repaint(repaintBounds);
    parent.getScene().repaint(bounds);
  }

  @Override
  public String getFullString() {
    return "";
  }

}
