package graphic.textbox;

import classDiagram.components.Entity;
import classDiagram.verifyName.TypeName;
import graphic.GraphicView;
import utility.Utility;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import java.awt.*;

/**
 * A TextBox is a graphic component from Slyum containing a String. The particularity of a TextBox is it can be moved
 * with mouse and its String can be edited by double-click on it.
 * <p>
 * A TextBoxEntityName is a TextBox displaying the name of an entity. When editing the text, this TextBox parse the
 * String to change it into the name of the entity. It listening entity changes for auto-update itself.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class TextBoxEntityName extends TextBox {
  private int classWidth;

  private final Entity entity;

  /**
   * Create a new TextBoxEntityName with the given entity.
   *
   * @param parent the graphic view
   * @param entity the entity
   */
  public TextBoxEntityName(GraphicView parent, Entity entity) {
    super(parent, entity.getName());

    this.entity = entity;
  }

  @Override
  public void createEffectivFont() {
    int style = Font.BOLD;

    if (entity.isNameItalic()) style |= Font.ITALIC;

    javafx.scene.text.FontWeight fw = ((style & java.awt.Font.BOLD) != 0) ? FontWeight.BOLD : FontWeight.NORMAL;
    javafx.scene.text.FontPosture fp = ((style & java.awt.Font.ITALIC) != 0) ? FontPosture.ITALIC : FontPosture.REGULAR;
    effectivFont = javafx.scene.text.Font.font(getFont().getFamily(), fw, fp, getFont().getSize());
  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(bounds.x, bounds.y, bounds.width, textDim.height);
  }

  @Override
  public String getText() {
    return entity.getName();
  }

  @Override
  public void setBounds(Rectangle bounds) {
    super.setBounds(bounds);

    if (textDim.width > bounds.width)
      this.bounds.width = bounds.width;

    classWidth = bounds.width;
  }

  /** {@inheritDoc} */
  @Override
  public void setText(final String text) {
    final String newText = text.replace(" ", "_");
    if (entity.getName().equals(newText) ||
        !TypeName.getInstance().verifyName(newText))
      return;

    entity.setName(newText);
    super.setText(newText);
  }

  @Override
  public void stopEditing() {
    super.stopEditing();

    entity.notifyObservers();
  }

  @Override
  protected String truncate(GraphicsContext gc, String text, int width) {
    return Utility.truncate(gc.getFont(), text, classWidth);
  }

}
