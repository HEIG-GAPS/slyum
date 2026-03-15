package graphic;

import change.Change;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import swing.Slyum;
import utility.PersonalizedIcon;
import utility.Utility;

import java.awt.Point;
import java.awt.Rectangle;

public class StyleCross extends GraphicComponent {

  ButtonCross[] btnCross = new ButtonCross[5];
  String[] paths = new String[] {
      "alignTop.png",
      "alignLeft.png", "adjustWidth.png",
      "alignRight.png", "alignBottom.png"};

  Point pos = new Point(0, 0);

  private int nbrComponentSelected;

  public StyleCross(GraphicView parent, Point location, int nbrComponentSelected) {
    super(parent);

    if (!Slyum.isShowCrossMenu()) return;

    btnCross[0] = new ButtonCross(parent,
                                  PersonalizedIcon.createImageIcon(paths[0])) {

      @Override
      public void mouseClick() {
        parent.alignHorizontal(true);
      }
    };

    btnCross[1] = new ButtonCross(parent,
                                  PersonalizedIcon.createImageIcon(paths[1])) {

      @Override
      public void mouseClick() {
        parent.alignVertical(true);
      }
    };

    btnCross[2] = new ButtonCross(parent,
                                  PersonalizedIcon.createImageIcon(paths[2])) {

      @Override
      public void mouseClick() {
        parent.adjustWidthSelectedEntities();
      }
    };

    btnCross[3] = new ButtonCross(parent,
                                  PersonalizedIcon.createImageIcon(paths[3])) {

      @Override
      public void mouseClick() {
        parent.alignVertical(false);
      }
    };

    btnCross[4] = new ButtonCross(parent,
                                  PersonalizedIcon.createImageIcon(paths[4])) {

      @Override
      public void mouseClick() {
        parent.alignHorizontal(false);
      }
    };

    parent.addOthersComponents(this);

    setBounds(new Rectangle(new Point(location.x
                                      - ((int) (ButtonCross.EDGE_SIZE * 1.5 + 5)), location.y
                                                                                   -
                                                                                   ((int) (ButtonCross.EDGE_SIZE * 1.5 +
                                                                                           5)))));

    this.nbrComponentSelected = nbrComponentSelected;
  }

  @Override
  public Rectangle getBounds() {
    int width, height, x, y;

    if (nbrComponentSelected > 1) {
      width = ButtonCross.EDGE_SIZE * 3 + 10;
      height = ButtonCross.EDGE_SIZE * 3 + 10;

      x = pos.x;
      y = pos.y;
    } else {
      width = ButtonCross.EDGE_SIZE;
      height = ButtonCross.EDGE_SIZE;

      x = pos.x + ButtonCross.EDGE_SIZE + 5;
      y = pos.y + ButtonCross.EDGE_SIZE + 5;
    }

    return new Rectangle(x, y, width, height);
  }

  @Override
  public boolean isAtPosition(Point position) {
    if (getBounds().contains(position))

      return true;
    else {
      delete();
      return false;
    }
  }

  @Override
  public void paintComponent(GraphicsContext gc) {
    if (pictureMode) return;

    Utility.setRenderQuality(gc);

    Rectangle bounds = getBounds();
    Color borderColor = Color.rgb(100, 100, 100);

    double rx = bounds.x - 1, ry = bounds.y - 1;
    double rw = bounds.width + 1, rh = bounds.height + 1;
    double arcSize = 9;

    // Filled rounded background
    gc.setFill(Color.rgb(100, 100, 100, 80 / 255.0));
    gc.fillRoundRect(rx, ry, rw, rh, arcSize, arcSize);

    // Outer shadow stroke
    gc.setLineWidth(4.0);
    gc.setStroke(borderColor.darker());
    gc.strokeRoundRect(bounds.x - 1, bounds.y - 1, bounds.width + 3, bounds.height + 3, arcSize, arcSize);

    // Inner border stroke
    gc.setLineWidth(1.4);
    gc.setStroke(borderColor);
    gc.strokeRoundRect(rx, ry, rw, rh, arcSize, arcSize);

    if (nbrComponentSelected > 1)

      for (int i = 0; i < btnCross.length; i++)

        btnCross[i].paintComponent(gc);
    else

      btnCross[2].paintComponent(gc);
  }

  @Override
  public void repaint() {
    parent.getScene().repaint(getBounds());
  }

  @Override
  public void gMouseExited(MouseEvent e) {
    super.gMouseExited(e);

    delete();
  }

  @Override
  public void gMouseMoved(MouseEvent e) {
    super.gMouseMoved(e);

    Point pos = new Point((int) e.getX(), (int) e.getY());
    for (int i = 0; i < btnCross.length; i++)

      if (btnCross[i].isAtPosition(pos))

        btnCross[i].gMouseEntered(e);

      else

        btnCross[i].gMouseExited(e);
  }

  @Override
  public void delete() {
    final boolean isBlocked = Change.isBlocked();
    Change.setBlocked(true);

    super.delete();

    for (int i = 0; i < btnCross.length; i++)

      btnCross[i].delete();

    repaint();
    Change.setBlocked(isBlocked);
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    super.gMouseReleased(e);

    Point pos = new Point((int) e.getX(), (int) e.getY());
    for (int i = 0; i < btnCross.length; i++)

      if (btnCross[i].isAtPosition(pos)) {
        btnCross[i].gMouseReleased(e);
        break;
      }
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    super.gMousePressed(e);

    Point pos = new Point((int) e.getX(), (int) e.getY());
    for (int i = 0; i < btnCross.length; i++)

      if (btnCross[i].isAtPosition(pos)) {
        btnCross[i].gMousePressed(e);
        break;
      }
  }

  @Override
  public void setBounds(Rectangle bounds) {
    pos = new Point(bounds.x, bounds.y);
    int size = ButtonCross.EDGE_SIZE + 5;

    btnCross[0].setLocation(new Point(bounds.x + size, bounds.y));
    btnCross[1].setLocation(new Point(bounds.x, bounds.y + size));
    btnCross[2].setLocation(new Point(bounds.x + size, bounds.y + size));
    btnCross[3].setLocation(new Point(bounds.x + size * 2, bounds.y + size));
    btnCross[4].setLocation(new Point(bounds.x + size, bounds.y + size * 2));
  }

  @Override
  public String getFullString() {
    return "";
  }

}
