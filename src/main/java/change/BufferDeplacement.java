package change;

import graphic.textbox.TextBoxLabel;

import java.awt.*;

public class BufferDeplacement extends BufferGraphicView {
  Point deplacement;
  TextBoxLabel tbl;

  public BufferDeplacement(TextBoxLabel tbl) {
    super(tbl.getGraphicView());

    this.tbl = tbl;
    deplacement = tbl.getDeplacement();
  }

  @Override
  public void restore() {
    super.restore();

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
