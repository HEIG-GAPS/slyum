package graphic.entity;

import change.BufferBounds;
import change.Change;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.EnumEntity;
import classDiagram.components.InterfaceEntity;
import graphic.ColoredComponent;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.MovableComponent;
import graphic.relations.RelationGrip;
import graphic.textbox.TextBox;
import graphic.textbox.TextBoxEntityName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.PropertyLoader;
import swing.SPanelElement;
import swing.Slyum;
import utility.Utility;

import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Represent the view of an entity in UML structure.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class EntityView extends MovableComponent implements Observer, ColoredComponent, Cloneable {
  public static final float BORDER_WIDTH = 1.2f;
  public static final Color DEFAULT_BORDER_COLOR = new Color(65, 65, 65);
  public static final int VERTICAL_SPACEMENT = 10; // margin
  public static final Color baseColor = new Color(255, 247, 225);
  private static Color basicColor = new Color(baseColor.getRGB());

  private static final Font stereotypeFontBasic = new Font(
      Slyum.getInstance().defaultFont.getFamily(), 0, 11);

  public static EntityView createFromEntity(
      GraphicView graphicView, Entity entity) {
    if (entity.getClass() == ClassEntity.class)
      return new ClassView(graphicView, (ClassEntity) entity);
    else if (entity.getClass() == InterfaceEntity.class)
      return new InterfaceView(graphicView, (InterfaceEntity) entity);
    else if (entity.getClass() == EnumEntity.class)
      return new EnumView(graphicView, (EnumEntity) entity);
    //else if (entity.getClass() == AssociationClass.class)
    //return new AssociationClassView(graphicView, (AssociationClass)entity, (BinaryView)graphicView
    // .searchAssociedComponent(((AssociationClass)entity).getAssociation()), new Rectangle());
    return null;
  }

  /**
   * Get the default color used then a new entity is created.
   *
   * @return the basic color.
   */
  public static Color getBasicColor() {
    String colorEntities = PropertyLoader.getInstance().getProperties()
                                         .getProperty(PropertyLoader.COLOR_ENTITIES);
    Color color;

    if (colorEntities == null)
      color = basicColor;
    else
      color = new Color(Integer.parseInt(colorEntities));

    return color;
  }

  ;

  /**
   * Set the basic color. Basic color is used as default color while creating a new entity.
   *
   * @param color the new basic color
   */
  public static void setBasicColor(Color color) {
    basicColor = new Color(color.getRGB());
  }

  /**
   * Compute the point intersecting the lines given. Return Point(-1.0f, -1.0f) if lines are //.
   *
   * @param line1 the first line
   * @param line2 the second line
   *
   * @return the intersection point of the two lines
   */
  public static Point2D ptIntersectsLines(Line2D line1, Line2D line2) {
    // convert line2D to point
    final Point p1 = new Point((int) line1.getP1().getX(), (int) line1.getP1()
                                                                      .getY());
    final Point p2 = new Point((int) line1.getP2().getX(), (int) line1.getP2()
                                                                      .getY());
    final Point p3 = new Point((int) line2.getP1().getX(), (int) line2.getP1()
                                                                      .getY());
    final Point p4 = new Point((int) line2.getP2().getX(), (int) line2.getP2()
                                                                      .getY());

    // compute intersection point between two line
    // (http://en.wikipedia.org/wiki/Line-line_intersection)
    final int denom = (p1.x - p2.x) * (p3.y - p4.y) - (p1.y - p2.y)
                                                      * (p3.x - p4.x);

    // no intersection (lines //)
    if (denom == 0) return new Point2D.Float(-1.0f, -1.0f);

    final int x = ((p1.x * p2.y - p1.y * p2.x) * (p3.x - p4.x) - (p1.x - p2.x)
                                                                 * (p3.x * p4.y - p3.y * p4.x))
                  / denom;
    final int y = ((p1.x * p2.y - p1.y * p2.x) * (p3.y - p4.y) - (p1.y - p2.y)
                                                                 * (p3.x * p4.y - p3.y * p4.x))
                  / denom;

    return new Point2D.Float(x, y);
  }

  /**
   * Search the intersection point between the border of a rectangle and the line defined by first and next point. The
   * rectangle is decomposed in for lines and each line go to infinite. So all lines intersect an edge of the rectangle.
   * We must compute if segments intersect each others or not.
   *
   * @param bounds the rectangle
   * @param first the first point
   * @param next the next point
   *
   * @return the intersection point; or null if no points found
   */
  public static Point searchNearestEgde(Rectangle bounds, Point first, Point next) {

    // One offset needed to avoid intersection with the wrong line.
    if (bounds.x + bounds.width <= first.x)
      first.x = bounds.x + bounds.width - 1;
    else if (bounds.x >= first.x) first.x = bounds.x + 1;

    if (bounds.y + bounds.height <= first.y)
      first.y = bounds.height + bounds.y - 1;
    else if (bounds.y >= first.y) first.y = bounds.y + 1;

    Line2D relationLine = new Line2D.Float(first.x, first.y, next.x, next.y);
    Line2D lineTop = new Line2D.Float(bounds.x, bounds.y, bounds.x
                                                          + bounds.width, bounds.y);
    Line2D lineRight = new Line2D.Float(bounds.x + bounds.width, bounds.y,
                                        bounds.x + bounds.width, bounds.y + bounds.height);
    Line2D lineBottom = new Line2D.Float(bounds.x + bounds.width, bounds.y
                                                                  + bounds.height, bounds.x, bounds.y + bounds.height);
    Line2D lineLeft = new Line2D.Float(bounds.x, bounds.y + bounds.height,
                                       bounds.x, bounds.y);

    Point2D ptIntersectTop = ptIntersectsLines(relationLine, lineTop);
    Point2D ptIntersectRight = ptIntersectsLines(relationLine, lineRight);
    Point2D ptIntersectBottom = ptIntersectsLines(relationLine, lineBottom);
    Point2D ptIntersectLeft = ptIntersectsLines(relationLine, lineLeft);

    // line is to infinite, we must verify that the point find interst the
    // correct edge and the relation.
    int distTop = (int) lineTop.ptSegDist(ptIntersectTop)
                  + (int) relationLine.ptSegDist(ptIntersectTop);
    int distRight = (int) lineRight.ptSegDist(ptIntersectRight)
                    + (int) relationLine.ptSegDist(ptIntersectRight);
    int distBottom = (int) lineBottom.ptSegDist(ptIntersectBottom)
                     + (int) relationLine.ptSegDist(ptIntersectBottom);
    int distLeft = (int) lineLeft.ptSegDist(ptIntersectLeft)
                   + (int) relationLine.ptSegDist(ptIntersectLeft);

    if (ptIntersectTop != null && distTop == 0) {
      return new Point(RelationGrip.adjust((int) ptIntersectTop.getX()),
                       (int) ptIntersectTop.getY());

    } else if (ptIntersectRight != null && distRight == 0) {
      return new Point((int) ptIntersectRight.getX(),
                       RelationGrip.adjust((int) ptIntersectRight.getY()));

    } else if (ptIntersectBottom != null && distBottom == 0) {
      return new Point(RelationGrip.adjust((int) ptIntersectBottom.getX()),
                       (int) ptIntersectBottom.getY());

    } else if (ptIntersectLeft != null && distLeft == 0) {
      return new Point((int) ptIntersectLeft.getX(),
                       RelationGrip.adjust((int) ptIntersectLeft.getY()));

    } else {
      return null; // no point found!
    }
  }

  /* Colors */
  public static final javafx.scene.paint.Color DEFAULT_BORDER_COLOR_FX = javafx.scene.paint.Color.rgb(65, 65, 65);
  public final javafx.scene.paint.Color DEFAULT_TEXT_COLOR_FX = javafx.scene.paint.Color.rgb(40, 40, 40);
  public final Color DEFAULT_TEXT_COLOR = new Color(40, 40, 40);
  protected Entity component;
  protected MenuItem menuItemDelete, menuItemMoveDown,
      menuItemMoveUp;

  protected TextBox pressedTextBox;
  protected GraphicComponent saveTextBoxMouseHover;

  private Rectangle bounds = new Rectangle();
  private javafx.scene.paint.Color defaultColor;
  private int fullWidthStereotype = 0;

  private final TextBoxEntityName entityName;

  private Cursor saveCursor = Cursor.getDefaultCursor();

  private Font stereotypeFont = stereotypeFontBasic;

  public EntityView(final GraphicView parent, Entity component) {
    super(parent);

    if (component == null)
      throw new IllegalArgumentException("component is null");

    this.component = component;
    entityName = new TextBoxEntityName(parent, component);
    initializeComponents();
  }

  public void _delete() {
    boolean isRecord = Change.isRecord();
    Change.record();

    delete();

    if (!isRecord) Change.stopRecord();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = (e.getSource() instanceof MenuItem)
        ? ((MenuItem) e.getSource()).getId() : null;

    // TODO fix degueulasse en attendant la refacto des menus.
    if (!Slyum.ACTION_NEW_NOTE_ASSOCIED.equals(actionCommand))
      super.actionPerformed(e);

    if ("Delete".equals(actionCommand)) {
      if (pressedTextBox != null)
        removeTextBox(pressedTextBox);
      else {
        _delete();
      }
    } else if ("DeepDelete".equals(actionCommand)) {
      change.Helper.deepDeleteEntityView(this);
    } else if (Slyum.ACTION_DUPLICATE.equals(actionCommand)) {
      if (pressedTextBox == null) parent.duplicateSelectedEntities();
    } else {
      // TODO: delegate to SPanelElement once SPanelElement.actionPerformed is migrated to javafx.event.ActionEvent
    }
  }

  /**
   * Adjust the width according to its content.
   */
  public void adjustWidth() {
    int width = Short.MIN_VALUE;

    for (final TextBox tb : getAllTextBox()) {
      final int tbWidth = tb.getTextDim().width;

      if (tbWidth > width) width = tbWidth; // get the longer content
    }

    if (fullWidthStereotype > width) width = fullWidthStereotype;

    Change.push(new BufferBounds(this));

    setBounds(new Rectangle(
        bounds.x, bounds.y, width + GraphicView.getGridSize() + 15, bounds.height));

    Change.push(new BufferBounds(this));
  }

  @Override
  public EntityView clone() throws CloneNotSupportedException {
    try {
      Rectangle newBounds = getBounds();
      String classToInstanciate = getClass().equals(AssociationClassView.class) ? ClassView.class
          .getName() : getClass().getName();
      int gridSize = GraphicView.getGridSize();
      newBounds.translate(gridSize, gridSize);
      Entity entity = ((Entity) getAssociatedComponent()).clone();
      EntityView view = (EntityView) Class.forName(classToInstanciate)
                                          .getConstructor(GraphicView.class, entity.getClass())
                                          .newInstance(parent, entity);
      view.regenerateEntity();

      view.setBounds(newBounds);
      view.setColor(defaultColor);
      return view;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Point computeAnchorLocation(Point first, Point next) {
    return searchNearestEgde(getBounds(), first, next);
  }

  /**
   * Compute the height of the class with margin and content.
   *
   * @param classNameHeight the height of class name
   * @param stereotypeHeight the height of stereotype
   * @param elementsHeight the height of each element (methods, attributes)
   *
   * @return the height of the class
   */
  public int computeHeight(int classNameHeight, int stereotypeHeight, int elementsHeight) {
    int height = VERTICAL_SPACEMENT;

    if (!component.getStereotype().isEmpty()) height += stereotypeHeight;

    height += classNameHeight;

    return height + 10;
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

    final javafx.scene.paint.Color backColor = getColor();
    final javafx.scene.paint.Color fill = backColor.deriveColor(0, 1, 1, 100.0 / 255.0);
    final javafx.scene.paint.Color border = backColor.darker();

    gc.setFill(fill);
    gc.fillRect(ghost.x, ghost.y, ghost.width, ghost.height);

    gc.setStroke(border);
    gc.setLineWidth(1.0);
    gc.setLineDashes(2.0);
    gc.strokeRect(ghost.x, ghost.y, ghost.width - 1, ghost.height - 1);
    gc.setLineDashes(0);
  }

  /**
   * Draw a border representing a selection.
   *
   * @param gc the graphic context
   */
  public void drawSelectedStyle(GraphicsContext gc) {
    final int PADDING = 2;
    final javafx.scene.paint.Color selectColor = javafx.scene.paint.Color.rgb(100, 100, 100);

    final Rectangle inRectangle = new Rectangle(bounds.x + PADDING, bounds.y
                                                                    + PADDING, bounds.width - 2 * PADDING,
                                                bounds.height - 2 * PADDING);

    final Rectangle outRectangle = new Rectangle(bounds.x - PADDING, bounds.y
                                                                     - PADDING, bounds.width + 2 * PADDING,
                                                 bounds.height + 2 * PADDING);

    gc.setLineWidth(1.0);
    gc.setLineDashes(2.0);
    gc.setStroke(selectColor);

    gc.strokeRect(inRectangle.x, inRectangle.y, inRectangle.width, inRectangle.height);
    gc.strokeRect(outRectangle.x, outRectangle.y, outRectangle.width, outRectangle.height);
    gc.setLineDashes(0);
  }

  public void editingName() {
    entityName.editing();
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    super.gMouseClicked(e);
    TextBox textBox = GraphicView.searchComponentWithPosition(getAllTextBox(),
                                                              new Point((int) e.getX(), (int) e.getY()));

    if (textBox != null) {
      IDiagramComponent idc = textBox.getAssociatedComponent();
      if (idc != null) {

        if (!GraphicView.isAddToSelection(e)) {
          idc.select();
          idc.notifyObservers(UpdateMessage.SELECT);
        }
      }

      if (e.getClickCount() == 2) textBox.editing();
    }
  }

  @Override
  public void gMouseEntered(MouseEvent e) {
    super.gMouseEntered(e);
    setMouseHoverStyle();
    saveCursor = parent.getScene().getCursor();
    parent.getScene().setCursor(new Cursor(Cursor.MOVE_CURSOR));
  }

  @Override
  public void gMouseExited(MouseEvent e) {
    super.gMouseExited(e);

    if (saveTextBoxMouseHover != null) {
      saveTextBoxMouseHover.gMouseExited(e);
      saveTextBoxMouseHover = null;
    }

    setDefaultStyle();

    parent.getScene().setCursor(saveCursor);
  }

  @Override
  public void gMouseMoved(MouseEvent e) {
    final GraphicComponent textBoxMouseHover = GraphicView
        .searchComponentWithPosition(getAllTextBox(), new Point((int) e.getX(), (int) e.getY()));
    GraphicView.computeComponentEventEnter(textBoxMouseHover,
                                           saveTextBoxMouseHover, e);

    saveTextBoxMouseHover = textBoxMouseHover;
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    pressedTextBox = searchTextBoxAtLocation(new Point((int) e.getX(), (int) e.getY()));
    super.gMousePressed(e);
  }

  /**
   * get all textBox displayed by the entity. TextBox returned are: - textBox for entity name - textBox for attributes -
   * textBox for methods
   *
   * @return an array containing all TextBox
   */
  public List<TextBox> getAllTextBox() {
    List<TextBox> tb = new LinkedList<>();
    tb.add(entityName);
    return tb;
  }

  @Override
  public LinkedList<? extends GraphicComponent> getDirectChilds() {
    return (LinkedList<? extends GraphicComponent>) getAllTextBox();
  }

  @Override
  public IDiagramComponent getAssociatedComponent() {
    return component;
  }

  @Override
  public Rectangle getBounds() {
    if (bounds == null) bounds = new Rectangle();

    return new Rectangle(bounds);
  }

  @Override
  public void setBounds(Rectangle bounds) {
    // Save current bounds, change bounds and repaint old bounds and new
    // bounds.
    final Rectangle repaintBounds = new Rectangle(getBounds());

    final Rectangle newBounds = new Rectangle(ajustOnGrid(bounds.x),
                                              ajustOnGrid(bounds.y), ajustOnGrid(bounds.width), bounds.height);

    newBounds.width = newBounds.width < MINIMUM_SIZE.x ? MINIMUM_SIZE.x
        : newBounds.width;

    this.bounds = newBounds;

    parent.getScene().repaint(repaintBounds);
    parent.getScene().repaint(newBounds);

    // Move graphics elements associated with this component
    leftMovableSquare.setBounds(computeLocationResizer(0));
    rightMovableSquare.setBounds(computeLocationResizer(bounds.width));

    setChanged();
    notifyObservers();
  }

  @Override
  public javafx.scene.paint.Color getColor() {
    if (pictureMode) return defaultColor;
    return super.getColor();
  }

  @Override
  public void setColor(javafx.scene.paint.Color color) {
    setCurrentColor(color);
    defaultColor = color;
  }

  /**
   * Get the entity (UML) associed with this entity view. Same as getAssociedComponent().
   *
   * @return the component associed.
   */
  public Entity getComponent() {
    return component;
  }

  /**
   * Set the current color for this entity.
   *
   * @param color the current color.
   */
  public void setCurrentColor(javafx.scene.paint.Color color) {
    super.setColor(color);
  }

  @Override
  public javafx.scene.paint.Color getDefaultColor() {
    Color c = getBasicColor();
    return javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 255.0);
  }

  public void setLocationRelativeTo(Point dropPoint) {
    Rectangle newBounds = new Rectangle(new Dimension(
        getBounds().width, getBounds().height));
    newBounds.x = dropPoint.x - getBounds().width / 2;
    newBounds.y = dropPoint.y - getBounds().height / 2;
    setBounds(newBounds);
  }

  @Override
  public void setSelected(boolean select) {
    super.setSelected(select);
    component.select();

    if (select)
      component.notifyObservers(UpdateMessage.SELECT);
    else
      component.notifyObservers(UpdateMessage.UNSELECT);

    if (!select) for (final TextBox t : getAllTextBox())
      t.setSelected(false);
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element entityView = doc.createElement(getXmlTagName());
    entityView.setAttribute("componentID",
                            String.valueOf(getAssociatedComponent().getId()));
    entityView.setAttribute("color", String.valueOf(
        new Color((float) defaultColor.getRed(), (float) defaultColor.getGreen(),
                  (float) defaultColor.getBlue()).getRGB()));
    entityView.appendChild(Utility.boundsToXmlElement(doc, getBounds(),
                                                      "geometry"));
    return entityView;
  }

  @Override
  public String getXmlTagName() {
    return "componentView";
  }

  @Override
  public boolean isAtPosition(Point mouse) {
    return bounds.contains(mouse);
  }

  @Override
  public void maybeShowPopup(MouseEvent e, ContextMenu popupMenu) {
    if (e.getButton() == MouseButton.SECONDARY && e.getEventType() == MouseEvent.MOUSE_RELEASED) {
      String text = "Delete ";

      // If context menu is requested on a TextBox, customize popup menu.
      if (pressedTextBox == null) {
        text += "from this view";
        menuItemMoveUp.setDisable(true);
        menuItemMoveDown.setDisable(true);
      } else {
        text += pressedTextBox.getText();
        menuItemMoveUp.setDisable(false);
        menuItemMoveDown.setDisable(false);
      }
      menuItemDelete.setText(text);
    }
    super.maybeShowPopup(e, popupMenu);
  }

  @Override
  public void paintComponent(GraphicsContext gc) {
    if (!isVisible()) return;

    // Use JavaFX font for metrics and rendering
    javafx.scene.text.Font jfxEntityFont = javafx.scene.text.Font.font(
        entityName.getEffectivFont().getFamily(),
        entityName.getEffectivFont().getSize2D() * parent.getZoom());

    String className = component.getName();
    javafx.scene.text.Text classNameNode = new javafx.scene.text.Text(className);
    classNameNode.setFont(jfxEntityFont);
    int classNameWidth = (int) classNameNode.getBoundsInLocal().getWidth();
    int classNameHeight = (int) classNameNode.getBoundsInLocal().getHeight();
    Dimension classNameSize = new Dimension(classNameWidth, classNameHeight);

    double stereoSize = stereotypeFontBasic.getSize() * parent.getZoom();
    javafx.scene.text.Font jfxStereotypeFont = javafx.scene.text.Font.font(
        stereotypeFontBasic.getFamily(), stereoSize);

    final String fullStereotype = "<< " + component.getStereotype() + " >>";
    final String truncatStereotype = Utility.truncate(jfxStereotypeFont, fullStereotype, bounds.width - 15);

    javafx.scene.text.Text fullStNode = new javafx.scene.text.Text(fullStereotype);
    fullStNode.setFont(jfxStereotypeFont);
    javafx.scene.text.Text truncStNode = new javafx.scene.text.Text(truncatStereotype);
    truncStNode.setFont(jfxStereotypeFont);

    fullWidthStereotype = (int) fullStNode.getBoundsInLocal().getWidth();
    int stereotypeWidth = (int) truncStNode.getBoundsInLocal().getWidth();
    int stereotypeHeight = (int) truncStNode.getBoundsInLocal().getHeight();
    Dimension stereotypeSize = new Dimension(stereotypeWidth, stereotypeHeight);

    int textBoxHeight = classNameHeight;

    bounds.height = computeHeight(classNameSize.height, stereotypeHeight, textBoxHeight);

    Rectangle bounds = getBounds();

    int offset = bounds.y + VERTICAL_SPACEMENT / 2;
    int stereotypeLocationWidth = bounds.x + (bounds.width - stereotypeSize.width) / 2;

    entityName.setBounds(new Rectangle(1, 1, bounds.width - 15, textBoxHeight + 2));
    Rectangle entityNameBounds = entityName.getBounds();
    int classNameLocationX = bounds.x + (bounds.width - entityNameBounds.width) / 2;

    // draw background
    if (GraphicView.isEntityGradient()) {
      gc.setFill(new javafx.scene.paint.LinearGradient(
          0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
          new javafx.scene.paint.Stop(0, getColor()),
          new javafx.scene.paint.Stop(1, getColor().darker())));
    } else {
      gc.setFill(getColor());
    }
    gc.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

    // draw border
    gc.setLineWidth(BORDER_WIDTH);
    gc.setStroke(DEFAULT_BORDER_COLOR_FX);
    gc.strokeRect(bounds.x, bounds.y, bounds.width, bounds.height);

    // draw stereotype
    if (!component.getStereotype().isEmpty()) {
      offset += stereotypeSize.height;
      gc.setFont(jfxStereotypeFont);
      gc.setFill(DEFAULT_TEXT_COLOR_FX);
      gc.fillText(truncatStereotype, stereotypeLocationWidth, offset);
    }

    // draw class name
    offset += VERTICAL_SPACEMENT / 2;
    entityName.setBounds(new Rectangle(classNameLocationX, offset, bounds.width - 15, textBoxHeight + 2));
    // TODO: entityName.paintComponent(gc) once TextBox is migrated to JavaFX GraphicsContext
    entityNameBounds = entityName.getBounds();
    offset += entityNameBounds.height;

    offset += paintTextBoxes(gc, bounds, textBoxHeight, offset);

    // is component selected? -> draw selected style
    if (!pictureMode && parent.getSelectedComponents().contains(this))
      drawSelectedStyle(gc);
  }

  /**
   * Delete all TextBox and regenerate them.
   */
  public void regenerateEntity() {
    boolean isStopRepaint = parent.getStopRepaint();
    parent.setStopRepaint(true);

    entityName.setText(component.getName());
    innerRegenerate();

    if (!isStopRepaint) parent.goRepaint();

    updateHeight();
  }

  /**
   * Generic method for remove the associated component for the given TextBox.
   *
   * @param tb the TextBox containing the element to remove.
   *
   * @return true if component has been removed; false otherwise.
   */
  public abstract boolean removeTextBox(TextBox tb);

  @Override
  public void repaint() {
    parent.getScene().repaint(getBounds());
  }

  @Override
  public void restore() {
    super.restore();
    parent.addOthersComponents(leftMovableSquare);
    parent.addOthersComponents(rightMovableSquare);
  }

  public TextBox searchAssociedTextBox(IDiagramComponent search) {
    for (TextBox textbox : getAllTextBox())
      if (textbox.getAssociatedComponent() == search) return textbox;

    return null;
  }

  @Override
  public void setDefaultStyle() {
    setCurrentColor(defaultColor);
    repaint();
  }

  @Override
  public void setMouseHoverStyle() {
    setCurrentColor(getColor().brighter());
    repaint();
  }

  @Override
  public void setStyleClicked() {
    setCurrentColor(getColor().darker());
    repaint();
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    if (arg1 != null && arg1.getClass() == UpdateMessage.class)
      switch ((UpdateMessage) arg1) {
        case SELECT:
          super.setSelected(true);
          break;
        case UNSELECT:
          super.setSelected(false);
          break;
        case MODIF:
          break;
        default:
          break;
      }
    else
      regenerateEntity();
  }

  /**
   * Udpate the height of the entity and notify all components.
   */
  public void updateHeight() {
    Rectangle repaintBounds = getBounds();
    parent.getScene().paintImmediately(repaintBounds);

    // set new height compute while repainting.
    setBounds(new Rectangle(bounds));

    parent.getScene().repaint(repaintBounds);
    setChanged();
    notifyObservers();
  }

  protected final void initializeComponents() {

    MenuItem menuItem;

    // Create the popup menu.
    popupMenu.getItems().add(new SeparatorMenuItem());
    initializeMenuItemsAddElements(popupMenu);
    initializeMenuItemsPropertiesElements(popupMenu);

    menuItemMoveUp = makeMenuItem("Move up", Slyum.ACTION_TEXTBOX_UP,
                                  "arrow-up");
    menuItemMoveUp.setDisable(true);
    popupMenu.getItems().add(menuItemMoveUp);

    menuItemMoveDown = makeMenuItem("Move down", Slyum.ACTION_TEXTBOX_DOWN,
                                    "arrow-down");
    menuItemMoveDown.setDisable(true);
    popupMenu.getItems().add(menuItemMoveDown);

    popupMenu.getItems().add(new SeparatorMenuItem());

    popupMenu.getItems().add(makeMenuItem("Duplicate", Slyum.ACTION_DUPLICATE, "duplicate"));
    popupMenu.getItems().add(menuItemDelete = makeMenuItem("Delete from this view", "Delete", "delete"));
    popupMenu.getItems().add(makeMenuItem("Delete", "DeepDelete", "delete"));

    popupMenu.getItems().add(new SeparatorMenuItem());
    initializeMenuViews(popupMenu);

    SPanelElement p = SPanelElement.getInstance();
    menuItem = makeMenuItem("Move top", Slyum.ACTION_MOVE_TOP, "top");
    // TODO: p.getBtnTop().linkComponent(menuItem) once SButton.linkComponent is migrated to accept MenuItem
    popupMenu.getItems().add(menuItem);

    menuItem = makeMenuItem("Up", Slyum.ACTION_MOVE_UP, "up");
    // TODO: p.getBtnUp().linkComponent(menuItem) once SButton.linkComponent is migrated
    popupMenu.getItems().add(menuItem);

    menuItem = makeMenuItem("Down", Slyum.ACTION_MOVE_DOWN, "down");
    // TODO: p.getBtnDown().linkComponent(menuItem) once SButton.linkComponent is migrated
    popupMenu.getItems().add(menuItem);

    menuItem = makeMenuItem("Move bottom", Slyum.ACTION_MOVE_BOTTOM, "bottom");
    // TODO: p.getBtnBottom().linkComponent(menuItem) once SButton.linkComponent is migrated
    popupMenu.getItems().add(menuItem);

    component.addObserver(this);
    Color c = getBasicColor();
    setColor(javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 255.0));
  }

  protected abstract void initializeMenuItemsAddElements(ContextMenu popupmenu);

  protected abstract void initializeMenuItemsPropertiesElements(ContextMenu popupMenu);

  protected abstract void initializeMenuViews(ContextMenu popupMenu);

  protected abstract void innerRegenerate();

  protected abstract int paintTextBoxes(GraphicsContext gc, Rectangle bounds, int textboxHeight, int offset);

  /**
   * Search and return the Textbox (methods and attributes) at the given location.
   *
   * @param location the location where find a TextBox
   *
   * @return the found TextBox
   */
  private TextBox searchTextBoxAtLocation(Point location) {
    final List<TextBox> tb = getAllTextBox();
    tb.remove(entityName);
    return GraphicView.searchComponentWithPosition(tb, location);
  }

}
