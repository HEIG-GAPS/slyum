package graphic.textbox;

import change.BufferCreation;
import change.BufferNote;
import change.Change;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import graphic.ColoredComponent;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.MovableComponent;
import graphic.entity.EntityView;
import graphic.relations.LineCommentary;
import graphic.relations.LineView;
import graphic.relations.RelationGrip;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.Slyum;
import swing.UserInputDialog;
import swing.propretiesView.NoteProperties;
import utility.PersonalizedIcon;
import utility.Utility;

import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.awt.*;

/**
 * A TextBoxCommentary is not a subclass of TextBox becauseit is not on signle line. A TextBoxCommentary display a note
 * on multi-line and can be etited by double-click on it. The size of the TextBoxCommentary is automatically compute
 * according to its content.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class TextBoxCommentary extends MovableComponent implements ColoredComponent {
  public static final String DEFAULT_TEXT = "Double-click to edit note.";
  public static final Point MINIMUM_SIZE = new Point(50, 50);

  /**
   * Draw a representation of a note (with creased corner) with the specified bounds and color.
   *
   * @param g2 the graphic context
   * @param bounds the bounds of the note
   * @param cornerSize the size of the corner
   * @param color the color of the note
   */
  public static void drawNote(GraphicsContext gc, Rectangle bounds, int cornerSize,
                              Color color) {
    final int cornerX = bounds.x + bounds.width - cornerSize;
    final int cornerY = bounds.y + cornerSize;

    final int x2 = bounds.x + bounds.width;
    final int y2 = bounds.y + bounds.height;

    final int[] pointsX = new int[] {bounds.x, cornerX, x2, x2, bounds.x};
    final int[] pointsY = new int[] {bounds.y, bounds.y, cornerY, y2, y2};

    gc.setLineWidth(1.2);
    gc.setFill(color);
    gc.fillPolygon(java.util.Arrays.stream(pointsX).asDoubleStream().toArray(), java.util.Arrays.stream(pointsY).asDoubleStream().toArray(), pointsX.length);
    gc.setStroke(Color.DARKGRAY);
    gc.strokePolygon(java.util.Arrays.stream(pointsX).asDoubleStream().toArray(), java.util.Arrays.stream(pointsY).asDoubleStream().toArray(), pointsX.length);
    gc.strokeLine(cornerX, bounds.y, cornerX, cornerY);
    gc.strokeLine(cornerX, cornerY, x2, cornerY);
  }

  private Rectangle bounds;

  private final Font font = new Font("Ubuntu", Font.PLAIN, 12);

  private String text;

  /**
   * Create a new TextBoxCommentary with the given text.
   *
   * @param parent the graphic view
   * @param text the default text include in the note
   */
  public TextBoxCommentary(final GraphicView parent, String text) {
    super(parent);
    init(text);
  }

  /**
   * Create a new TextBoxCommentaray with the givent text associated with anoth component. A TextBoxCommentary
   * associated with a graphic component create a new line between the note and the component. This line can be edited.
   *
   * @param parent the graphic component
   * @param text the default text include in the note
   * @param component the component associated with the not
   */
  public TextBoxCommentary(final GraphicView parent, String text,
                           GraphicComponent component) {
    super(parent);

    if (component == null)
      throw new IllegalArgumentException("component is null");

    final Point middleTextBox = new Point(bounds.x + bounds.width / 2, bounds.y
                                                                       + bounds.height / 2);

    final Point middleComponent = new Point(bounds.x + bounds.width / 2,
                                            bounds.y + bounds.height / 2);

    if (LineCommentary.checkCreate(this, component, true))
      parent.addLineView(new LineCommentary(parent, this, component,
                                            middleTextBox, middleComponent, true));

    init(text);
  }

  @Override
  public Point computeAnchorLocation(Point first, Point next) {
    return EntityView.searchNearestEgde(getBounds(), first, next);
  }

  /**
   * Compute the width of the note according to it content. If a word is too long for current width, the width is
   * automatically enlarged the size of the word.
   *
   * @param g2
   */
  private void computeWidth(GraphicsContext gc) {
    final int PADDING = 5;
    final Rectangle bounds = getBounds();
    final String[] texts = getText().split("\\ ");

    final float scaledSize = (float)(font.getSize() * parent.getZoom());
    final javafx.scene.text.Font fxFont = javafx.scene.text.Font.font(font.getFamily(), scaledSize);
    final javafx.scene.text.Text measurer = new javafx.scene.text.Text();
    measurer.setFont(fxFont);
    final int hgt = (int)measurer.getBoundsInLocal().getHeight() + (int)scaledSize;
    int adv, offsetY = bounds.y + PADDING + hgt, offsetX = PADDING;
    int nbLines = 1;
    int newWidth = bounds.width;
    for (final String text2 : texts) {
      final String currentText = text2 + " ";
      measurer.setText(currentText);
      adv = (int)measurer.getBoundsInLocal().getWidth();

      if (offsetX + adv > bounds.width - PADDING * 2) {
        offsetY += hgt; // new line
        offsetX = PADDING;
        nbLines++;

        if (offsetX + adv > newWidth - PADDING)

          newWidth = adv + PADDING * 4;

      }

      offsetX += adv;
    }

    final int newHeight = offsetY + PADDING * 2 - bounds.y;

    if (nbLines == 1)

      newWidth = offsetX + PADDING * 4;

    if (bounds.width != newWidth || bounds.height != newHeight)

      setBounds(new Rectangle(bounds.x, bounds.y, newWidth, newHeight));
  }

  @Override
  public void delete() {
    super.delete();

    parent.removeComponent(leftMovableSquare);
    parent.removeComponent(rightMovableSquare);
  }

  @Override
  public void drawSelectedEffect(GraphicsContext gc) {
    if (pictureMode) return;
    Color backColor = parent.getColor();
    Color fill = getColor();
    fill = Color.rgb((int)(fill.getRed()*255), (int)(fill.getGreen()*255), (int)(fill.getBlue()*255), 100.0/255);
    Color border = backColor.darker();
    gc.setFill(fill);
    gc.fillRect(ghost.x, ghost.y, ghost.width, ghost.height);
    gc.setStroke(border);
    gc.setLineWidth(1.0);
    gc.setLineDashes(2.0);
    gc.strokeRect(ghost.x, ghost.y, ghost.width - 1, ghost.height - 1);
    gc.setLineDashes((double[]) null);
  }

  /**
   * Draw the text of the note. When a word exceeds the note width, a new line is created. The height is dynamically
   * compute according to the number of lines in the note.
   *
   * @param g2 the graphic context
   */
  private void drawText(GraphicsContext gc) {
    final int PADDING = 5;
    final Rectangle bounds = getBounds();
    final String[] texts = getText().split("\\ ");

    final float scaledSize = (float)(font.getSize() * parent.getZoom());
    final javafx.scene.text.Font fxFont = javafx.scene.text.Font.font(font.getFamily(), scaledSize);
    gc.setFont(fxFont);
    final javafx.scene.text.Text measurer = new javafx.scene.text.Text();
    measurer.setFont(fxFont);
    final int hgt = (int)scaledSize + 2;
    int adv, offsetY = bounds.y + PADDING + hgt, offsetX = PADDING;
    gc.setFill(Color.DARKGRAY);
    for (final String text2 : texts) {
      final String currentText = text2 + " ";
      measurer.setText(currentText);
      adv = (int)measurer.getBoundsInLocal().getWidth();

      if (offsetX + adv > bounds.width - PADDING * 2) {
        offsetY += hgt; // new line
        offsetX = PADDING;
      }
      gc.fillText(currentText, bounds.x + offsetX, offsetY);
      offsetX += adv;
    }
  }

  @Override
  public Rectangle getBounds() {
    if (bounds == null) bounds = new Rectangle();

    return new Rectangle(bounds);
  }

  /**
   * Get the text in the note.
   *
   * @return the text in the note
   */
  public String getText() {
    return text;
  }

  @Override
  public String getFullString() {
    return getText();
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    super.gMouseClicked(e);

    if (e.getClickCount() == 2) {
      final UserInputDialog ecd = new UserInputDialog(text.equals(DEFAULT_TEXT) ? "" : text,
                                                      "Slyum - Commentary editor", "Edit commentary : ");
      ecd.setVisible(true);

      if (ecd.isAccepted())

        setText(ecd.getText());
    }
  }

  /**
   * Calls by constructor for init the note.
   *
   * @param text the text of the note
   */
  private void init(String text) {
    this.text = text;

    setColor(EntityView.getBasicColor());
    popupMenu.getItems().add(new SeparatorMenuItem());
    final MenuItem item = new MenuItem("Delete commentary");
    item.setId("DeleteCommentary");
    item.setOnAction(e -> delete());

    parent.selectOnly(this);
    popupMenu.getItems().add(item);

    pushBufferCreation();
  }

  @Override
  protected void pushBufferCreation() {

    Change.push(new BufferCreation(false, this));
    Change.push(new BufferCreation(true, this));
  }

  @Override
  public boolean isAtPosition(Point position) {
    return getBounds().contains(position);
  }

  @Override
  public void paintComponent(GraphicsContext gc) {
    if (ghost.isEmpty()) computeWidth(gc);
    drawNote(gc, getBounds(), 15, getColor());
    drawText(gc);
    drawSelectedEffect(gc);
    final Rectangle bounds = getBounds();
    if (!pictureMode && isSelected()) {
      gc.setStroke(Color.DARKGRAY);
      gc.setLineWidth(1.0);
      gc.setLineDashes(2.0);
      gc.strokeRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
      gc.strokeRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
      gc.setLineDashes((double[]) null);
    }
    if (!pictureMode && isHighlight()) {
      gc.setLineWidth(1.0);
      gc.setFill(Color.rgb(76, 175, 80, 150.0/255));
      gc.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
      gc.setStroke(Color.rgb(76, 175, 80));
      gc.strokeRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
  }

  @Override
  public void repaint() {
    parent.getScene().repaint(getBounds());
  }

  @Override
  public void restore() {
    super.restore();
    parent.addNotes(this);
    repaint();
  }

  @Override
  public void setBounds(Rectangle bounds) {
    this.bounds = bounds;

    // Move graphics elements associed with this component
    leftMovableSquare.setBounds(computeLocationResizer(0));
    rightMovableSquare.setBounds(computeLocationResizer(bounds.width));

    setChanged();
    notifyObservers();

    repaint();
  }

  /**
   * Set the text of the note.
   *
   * @param text the text of the note
   */
  public void setText(String text) {
    Change.push(new BufferNote(this));
    this.text = text;
    Change.push(new BufferNote(this));

    repaint();
  }

  @Override
  public void setSelected(boolean selected) {
    super.setSelected(selected);

    NoteProperties.getInstance().update(this,
                                        selected ? UpdateMessage.SELECT : UpdateMessage.UNSELECT);
  }

  @Override
  public String getXmlTagName() {
    return "note";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element note = doc.createElement(getXmlTagName());
    note.setAttribute("content", text);
    note.setAttribute("color", String.valueOf(utility.Utility.fxColorToRgbInt(getColor())));
    note.appendChild(Utility.boundsToXmlElement(doc, getBounds(),
                                                "noteGeometry"));

    for (LineView lv : parent.getLinesViewAssociedWith(this)) {
      Element noteLine;
      int id = -1;

      // Recherche du composant UML associé.
      IDiagramComponent associedComponent = lv.getLastPoint()
                                              .getAssociedComponentView().getAssociatedComponent();
      if (associedComponent != null) {
        id = associedComponent.getId();
      } else {
        associedComponent = lv.getFirstPoint().getAssociedComponentView()
                              .getAssociatedComponent();
        if (associedComponent != null) id = associedComponent.getId();
      }

      noteLine = doc.createElement("noteLine");
      noteLine.setAttribute("relationId", String.valueOf(id));
      noteLine.setAttribute("color", String.valueOf(utility.Utility.fxColorToRgbInt(lv.getColor())));

      for (RelationGrip grip : lv.getPoints()) {
        Point pt = grip.getAnchor();
        pt.translate(1, 1);
        noteLine.appendChild(Utility.pointToXmlElement(pt, "point", doc));
      }

      note.appendChild(noteLine);
    }

    return note;
  }

  @Override
  public Color getDefaultColor() {
    return EntityView.getBasicColor();
  }

}
