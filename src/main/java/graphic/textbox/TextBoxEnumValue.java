package graphic.textbox;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.EnumValue;
import graphic.GraphicView;
import utility.Utility;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class TextBoxEnumValue extends TextBox implements Observer {

  private EnumValue enumValue;

  public TextBoxEnumValue(GraphicView parent, EnumValue enumValue) {
    super(parent, enumValue.getValue());
    this.enumValue = enumValue;
    enumValue.addObserver(this);
  }

  @Override
  protected String truncate(Graphics2D g2, String text, int width) {
    return Utility.truncate(g2, text, width);
  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(bounds);
  }

  @Override
  public void setBounds(Rectangle bounds) {
    if (bounds == null) throw new IllegalArgumentException("bounds is null");

    this.bounds = new Rectangle(bounds);
  }

  @Override
  public void setSelected(boolean select) {
    if (isSelected() != select) {
      super.setSelected(select);
      enumValue.select();
      if (select)
        enumValue.notifyObservers(UpdateMessage.SELECT);
      else
        enumValue.notifyObservers(UpdateMessage.UNSELECT);
    }
  }

  @Override
  public void setText(String text) {
    try {
      enumValue.setValue(text);
      super.setText(text);
      enumValue.notifyObservers();
    } catch (IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void update(Observable observable, Object o) {
    if (o != null && o instanceof UpdateMessage)
      switch ((UpdateMessage) o) {
        case SELECT:
          setSelected(true);
          break;
        case UNSELECT:
          setSelected(false);
          break;
        default:
          break;
      }
    else
      super.setText(enumValue.getValue());

    repaint();
  }

  @Override
  protected boolean mustPaintSelectedStyle() {
    return mouseHover;
  }

  @Override
  public IDiagramComponent getAssociatedComponent() {
    return enumValue;
  }

}
