package change;

import graphic.textbox.TextBoxLabel;

import java.awt.Point;
import java.awt.Rectangle;

public class BufferDeplacement implements Changeable {
  Point deplacement;
  TextBoxLabel tbl;

  public BufferDeplacement(TextBoxLabel tbl) {
    this.tbl = tbl;
    deplacement = tbl.getDeplacement();
  }

  @Override
  public void restore() {
    Rectangle repaintBounds = tbl.getBounds();
    tbl.setDeplacement(deplacement);
    tbl.repaint();
    tbl.getGraphicView().getScene().repaint(repaintBounds);
  }

  public Point getDeplacement() {
    return deplacement;
  }

  @Override
  public Object getAssociedComponent() {
    return tbl;
  }

}
