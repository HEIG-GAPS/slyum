package graphic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import utility.Utility;

import java.awt.Point;
import java.awt.Rectangle;

public abstract class ButtonCross extends GraphicComponent {
  public static int EDGE_SIZE = 24;
  private Rectangle bounds = new Rectangle(EDGE_SIZE, EDGE_SIZE);
  private Image image;
  private boolean isMouseHover = false;
  private boolean isMousePressed = false;

  public ButtonCross(GraphicView parent, Image image) {
    super(parent);

    this.image = image;
  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(bounds);
  }

  @Override
  public boolean isAtPosition(Point position) {
    return getBounds().contains(position);
  }

  @Override
  public void paintComponent(GraphicsContext gc) {
    Rectangle bounds = getBounds();

    Utility.setRenderQuality(gc);
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    gc.drawImage(image, bounds.x + (bounds.width - width) / 2,
                 bounds.y + (bounds.height - height) / 2, width, height);

    if (isMouseHover || isMousePressed) {
      Color color2 = Color.rgb(0, 255, 0, 20 / 255.0);
      Color color1;

      if (isMousePressed)

        color1 = Color.rgb(100, 140, 100, 200 / 255.0);

      else

        color1 = Color.rgb(200, 240, 200, 200 / 255.0);

      final LinearGradient gp = new LinearGradient(
          bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height,
          false, CycleMethod.NO_CYCLE,
          new Stop(0, color1), new Stop(1, color2));
      gc.setFill(gp);

      gc.fillRect(bounds.x - 1, bounds.y - 1, bounds.width + 1,
                  bounds.height + 1);

      gc.setStroke(Color.DARKGRAY);
      gc.setLineWidth(1.3);
      gc.strokeRect(bounds.x - 1, bounds.y - 1, bounds.width + 1,
                    bounds.height + 1);
    }
  }

  @Override
  public void gMouseEntered(MouseEvent e) {
    super.gMouseEntered(e);
    setMouseHover(true);
  }

  @Override
  public void gMouseExited(MouseEvent e) {
    super.gMouseExited(e);
    setMouseHover(false);
  }

  public void setMouseHover(boolean mouseHover) {
    isMouseHover = mouseHover;
    repaint();
  }

  public void setMousePressed(boolean mousePressed) {
    isMousePressed = mousePressed;
    repaint();
  }

  @Override
  public void repaint() {
    parent.getScene().repaint(getBounds());
  }

  @Override
  public void setBounds(Rectangle newbounds) {
    Rectangle oldBounds = getBounds();

    bounds = new Rectangle(newbounds);

    parent.getScene().repaint(oldBounds);
    parent.getScene().repaint(bounds);

  }

  public void setLocation(Point location) {
    setBounds(new Rectangle(location.x, location.y, bounds.width, bounds.height));
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    super.gMouseReleased(e);

    mouseClick();

    setMousePressed(false);
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    super.gMousePressed(e);

    setMousePressed(true);
  }

  @Override
  public String getFullString() {
    return "";
  }

  public abstract void mouseClick();

}
