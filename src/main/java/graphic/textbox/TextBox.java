package graphic.textbox;

import graphic.GraphicComponent;
import graphic.GraphicView;
import swing.PropertyLoader;
import swing.Slyum;
import swing.slyumCustomizedComponents.PopupTextField;
import utility.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * A TextBox is a graphic component from Slyum containing a String. The particularity of a TextBox is it text can be
 * changed by double-clinking on it.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class TextBox extends GraphicComponent {
  public final static String FONT_NAME = Slyum.DEFAULT_FONT.getFamily();
  public final static int FONT_SIZE = 12;

  public static Font getFont() {
    return javafx.scene.text.Font.font(getFontName(), getFontSize());
  }

  public static String getFontName() {
    final String prop = PropertyLoader.getInstance().getProperties()
                                      .getProperty(PropertyLoader.FONT_POLICE);
    String name = FONT_NAME;

    if (prop != null) name = prop;

    return name;
  }

  public static int getFontSize() {
    final String prop = PropertyLoader.getInstance().getProperties()
                                      .getProperty(PropertyLoader.FONT_SIZE);
    int size = FONT_SIZE;

    if (prop != null) size = Integer.parseInt(prop);

    return size;
  }

  public static void setFont(Font newFont) {
    setFontName(newFont.getFamily());
    setFontSize((int) newFont.getSize());
  }

  public static void setFontName(String name) {
    PropertyLoader.getInstance().getProperties()
                  .put(PropertyLoader.FONT_POLICE, name);
    PropertyLoader.getInstance().push();
  }

  public static void setFontSize(int size) {
    PropertyLoader.getInstance().getProperties()
                  .put(PropertyLoader.FONT_SIZE, size);
    PropertyLoader.getInstance().push();
  }

  protected Rectangle bounds = new Rectangle();

  protected Font effectivFont = getFont();

  protected boolean mouseHover = false;
  private Cursor previousCursor;

  private String text;
  protected Dimension textDim = new Dimension(50, 30);

  private JTextField textField;
  public static int MARGE = 5;
  private boolean hideWhileEditing = true;

  public TextBox(GraphicView parent, String text) {
    super(parent);

    this.text = text;
  }

  /**
   * This method is called just before the String in the TextBox is draw. Redefine this method for personnalize the font
   * in subclasses.
   */
  protected void createEffectivFont() {
    effectivFont = getFont();
  }

  /**
   * This method change the mode of the TextBox. Calls this method for turn the TextBox in edit mode, allow users to
   * change the String with a JTextField. For stop editing, call manually the method stopEditing() or the user can
   * accept or reject edition of the String by pressing enter or esc key.
   */
  @SuppressWarnings("serial")
  public void editing() {
    stopEditing();

    if (hideWhileEditing)
      setVisible(false);

    final Rectangle bounds = getBounds();

    textField = new PopupTextField(getEditingText()) {

      @Override
      public void paintComponent(Graphics g) {
        // Bug with TextField
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                                          RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        super.paintComponent(g);
      }
    };

    textField.setBackground(new Color(255, 255, 255));
    textField.setFont(new java.awt.Font(effectivFont.getFamily(), java.awt.Font.PLAIN, (int)(parent.getScale() * getFont().getSize())));
    double scale = parent.getScale();
    textField.setBounds(new Rectangle((int) (bounds.x * scale),
                                      (int) (bounds.y * scale), (int) (bounds.width * scale),
                                      (int) (bounds.height * scale)));
    textField.selectAll();

    parent.getScene().add(textField);

    textField.requestFocusInWindow();

    textField.addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent arg0) {
        if (isEditing()) {
          setText(textField.getText());
          stopEditing();
        }
      }
    });

    textField.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_ESCAPE:
            stopEditing();
            break;

          case KeyEvent.VK_ENTER:
            setText(textField.getText());
            stopEditing();
            break;
        }
      }
    });
  }

  public void setHideWhileEditing(boolean hide) {
    hideWhileEditing = hide;
  }

  public boolean isHideWhileEditing() {
    return hideWhileEditing;
  }

  public String getEditingText() {
    return getText();
  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(bounds.x, bounds.y, textDim.width, textDim.height);
  }

  /**
   * Get effective font. Effective font is a save of the font changed by createdEffectivFont() method.
   *
   * @return the effectiv font
   */
  public Font getEffectivFont() {
    return effectivFont;
  }

  /**
   * Get the String displayed by the TextBox.
   *
   * @return the string displayed by the TextBox.
   */
  public String getText() {
    return text;
  }

  /**
   * Get the dimension of the String displayed by the TextBox. The dimension of the String is update by each repaint.
   * Repaint the TextBox to be sure the dimension is the last used.
   *
   * @return the dimension of the String
   */
  public Dimension getTextDim() {
    return (Dimension) textDim.clone();
  }

  @Override
  public void gMouseEntered(MouseEvent e) {
    previousCursor = parent.getScene().getCursor();
    parent.getScene().setCursor(new Cursor(Cursor.TEXT_CURSOR));

    setMouseHover(true);
  }

  @Override
  public void gMouseExited(MouseEvent e) {
    parent.getScene().setCursor(previousCursor);
    setMouseHover(false);
  }

  /**
   * AttributeString allow the graphic context to parameter the style of the text like underline or size. Redefine this
   * method for personnalize the style of the String.
   *
   * @param ats Add AttributeString to this parameter.
   */
  public void initAttributeString(AttributedString ats) {

  }

  @Override
  public boolean isAtPosition(Point mouse) {
    return getBounds().contains(mouse);
  }

  @Override
  public void paintComponent(GraphicsContext gc) {
    paintComponentAt(gc, new Point(bounds.x, bounds.y));
  }

  public void paintComponentAt(GraphicsContext gc, Point location) {
    if (!isVisible()) return;

    final String name = getText();

    createEffectivFont();
    // Convert AWT Font to JavaFX Font
    float scaledSize = (float)(effectivFont.getSize() * parent.getZoom());
    Font fxFont = Font.font(effectivFont.getFamily(), scaledSize);
    gc.setFont(fxFont);

    // Measure text using JavaFX Text node
    javafx.scene.text.Text textNode = new javafx.scene.text.Text(name.isEmpty() ? "X" : name);
    textNode.setFont(fxFont);
    javafx.geometry.Bounds tb = textNode.getBoundsInLocal();
    textDim.width = (int) tb.getWidth();
    textDim.height = (int) tb.getHeight();
    double descent = tb.getMinY() + tb.getHeight(); // approximate descent

    gc.setLineWidth(1.0);
    gc.setLineDashes((double[]) null);
    // Draw mouseHover style (same as selected style)
    if (!pictureMode && mustPaintSelectedStyle()) paintSelectedStyle(gc);

    gc.setFill(javafx.scene.paint.Color.DARKGRAY);
    gc.setStroke(javafx.scene.paint.Color.DARKGRAY);

    final String drawText = truncate(gc, getText(), bounds.width);

    // Draw String
    if (!drawText.isEmpty()) {
      gc.fillText(drawText, location.x, location.y + bounds.height - descent);
    }

    if (!pictureMode && isHighlight())
      paintSelectedStyle(gc, javafx.scene.paint.Color.rgb(76, 175, 80, 150.0/255), javafx.scene.paint.Color.rgb(76, 175, 80, 150.0/255));
  }

  protected void paintSelectedStyle(GraphicsContext gc) {
    paintSelectedStyle(gc, javafx.scene.paint.Color.rgb(150, 150, 150, 150.0/255), javafx.scene.paint.Color.rgb(150, 150, 150, 150.0/255));
  }

  protected void paintSelectedStyle(GraphicsContext gc, javafx.scene.paint.Color borderColor, javafx.scene.paint.Color fillColor) {
    Rectangle bounds = getBounds();

    gc.setFill(fillColor);
    gc.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

    gc.setStroke(borderColor);
    gc.strokeRect(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  protected boolean mustPaintSelectedStyle() {
    return mouseHover || isSelected();
  }

  @Override
  public void repaint() {
    final Rectangle repaintBounds = getBounds();
    parent.getScene().repaint(repaintBounds);
  }

  @Override
  public void restore() {
    parent.addOthersComponents(this);
  }

  @Override
  public void setBounds(Rectangle bounds) {
    if (bounds == null) throw new IllegalArgumentException("bounds is null");
    this.bounds = new Rectangle(bounds.x, bounds.y, textDim.width,
                                textDim.height);
  }

  /**
   * Set if the mouse is hover the component or not.
   *
   * @param hover true for set the mouse hover; false otherwise
   */
  public void setMouseHover(boolean hover) {
    mouseHover = hover;
    repaint();
  }

  /**
   * Set the text containing int the TextBox.
   *
   * @param text the text containing int the TextBox
   */
  public void setText(String text) {
    this.text = text;

    final Rectangle bounds = getBounds();
    final Rectangle repaintBounds = new Rectangle(0, bounds.y, parent
        .getScene().getWidth(), bounds.height);
    parent.getScene().repaint(repaintBounds);
  }

  public boolean isEditing() {
    return textField != null;
  }

  /**
   * Stop the edition of the String.
   */
  public void stopEditing() {
    if (textField == null) return;

    parent.getScene().remove(textField);
    final Rectangle bounds = textField.getBounds();
    textField = null;
    final Rectangle repaintBounds = new Rectangle(0, bounds.y,
                                                  parent.getBounds().width, bounds.height);
    parent.getScene().repaint(repaintBounds);

    setVisible(true);
  }

  /**
   * Truncate the String. By default, no truncation are operated. Redefine this method for initialize a truncation. This
   * method is called just before the drawing of the String.
   *
   * @param g2 the {@link Graphics2D}.
   * @param text the text to truncate.
   * @param width the width allowed.
   *
   * @return the truncated string.
   */
  protected String truncate(final GraphicsContext gc, final String text, final int width) {
    return text;
  }

  @Override
  public String getFullString() {
    return getText();
  }

}
