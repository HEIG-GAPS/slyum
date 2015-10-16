package graphic.entity;

import graphic.ColoredComponent;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.MovableComponent;
import graphic.relations.RelationGrip;
import graphic.textbox.TextBox;
import graphic.textbox.TextBoxEntityName;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import swing.PropertyLoader;
import swing.SPanelElement;
import swing.Slyum;
import utility.Utility;
import change.BufferBounds;
import change.Change;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.EnumEntity;
import classDiagram.components.InterfaceEntity;

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
        return new ClassView(graphicView, (ClassEntity)entity);
     else if (entity.getClass() == InterfaceEntity.class)
        return new InterfaceView(graphicView, (InterfaceEntity)entity);
     else if (entity.getClass() == EnumEntity.class)
        return new EnumView(graphicView, (EnumEntity)entity);
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
  };

  /**
   * Set the basic color. Basic color is used as default color while creating a
   * new entity.
   *
   * @param color
   *          the new basic color
   */
  public static void setBasicColor(Color color) {
    basicColor = new Color(color.getRGB());
  }
  
  /**
   * Compute the point intersecting the lines given. Return Point(-1.0f, -1.0f)
   * if lines are //.
   * 
   * @param line1
   *          the first line
   * @param line2
   *          the second line
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
   * Search the intersection point between the border of a rectangle and the
   * line defined by first and next point. The rectangle is decomposed in for
   * lines and each line go to infinite. So all lines intersect an edge of the
   * rectangle. We must compute if segments intersect each others or not.
   * 
   * @param bounds
   *          the rectangle
   * @param first
   *          the first point
   * @param next
   *          the next point
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
  public final Color DEFAULT_TEXT_COLOR = new Color(40, 40, 40);
  protected Entity component;
  protected JMenuItem menuItemDelete;
  protected JMenuItem menuItemMoveDown;
  protected JMenuItem menuItemMoveUp;
  protected TextBox pressedTextBox;
  protected GraphicComponent saveTextBoxMouseHover;

  private Rectangle bounds = new Rectangle();
  private Color defaultColor;
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
    
    // TODO fix degueulasse en attendant la refacto des menus.
    if (!Slyum.ACTION_NEW_NOTE_ASSOCIED.equals(e.getActionCommand()))
      super.actionPerformed(e);

    if ("Delete".equals(e.getActionCommand())) {
      if (pressedTextBox != null)
        removeTextBox(pressedTextBox);
      else {
        _delete();
      }
    } else if (Slyum.ACTION_DUPLICATE.equals(e.getActionCommand())) {
      if (pressedTextBox == null) parent.duplicateSelectedEntities();
    } else {
      SPanelElement.getInstance().actionPerformed(e);
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
      Entity entity = ((Entity) getAssociedComponent()).clone();
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
   * @param classNameHeight
   *          the height of class name
   * @param stereotypeHeight
   *          the height of stereotype
   * @param elementsHeight
   *          the height of each element (methods, attributes)
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
  public void drawSelectedEffect(Graphics2D g2) {
    if (pictureMode) return;

    final Color backColor = getColor();
    final Color fill = new Color(backColor.getRed(), backColor.getGreen(),
        backColor.getBlue(), 100);
    
    final Color border = backColor.darker();
    final BasicStroke borderStroke = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
        new float[] { 2.0f }, 0.0f);
    
    g2.setColor(fill);
    g2.fillRect(ghost.x, ghost.y, ghost.width, ghost.height);

    g2.setColor(border);
    g2.setStroke(borderStroke);
    g2.drawRect(ghost.x, ghost.y, ghost.width - 1, ghost.height - 1);
  }

  /**
   * Draw a border representing a selection.
   * 
   * @param g2
   *          the graphic context
   */
  public void drawSelectedStyle(Graphics2D g2) {
    final int PADDING = 2;
    final Color selectColor = new Color(100, 100, 100);

    final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 10.0f, new float[] { 2f }, 0.0f);
    
    final Rectangle inRectangle = new Rectangle(bounds.x + PADDING, bounds.y
                                                                    + PADDING, bounds.width - 2 * PADDING, bounds.height - 2 * PADDING);
    
    final Rectangle outRectangle = new Rectangle(bounds.x - PADDING, bounds.y
                                                                     - PADDING, bounds.width + 2 * PADDING, bounds.height + 2 * PADDING);
    
    g2.setStroke(dashed);
    g2.setColor(selectColor);

    g2.drawRect(inRectangle.x, inRectangle.y, inRectangle.width,
                                              inRectangle.height);
    g2.drawRect(outRectangle.x, outRectangle.y, outRectangle.width,
                                                outRectangle.height);
  }
  
  public void editingName() {
    entityName.editing();
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    super.gMouseClicked(e);
    TextBox textBox = GraphicView.searchComponentWithPosition(getAllTextBox(),
            e.getPoint());

    if (textBox != null) {
      IDiagramComponent idc = textBox.getAssociedComponent();
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
        .searchComponentWithPosition(getAllTextBox(), e.getPoint());
    GraphicView.computeComponentEventEnter(textBoxMouseHover,
                                           saveTextBoxMouseHover, e);
    
    saveTextBoxMouseHover = textBoxMouseHover;
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    pressedTextBox = searchTextBoxAtLocation(e.getPoint());
    super.gMousePressed(e);
  }
  
  /**
   * get all textBox displayed by the entity. TextBox returned are: - textBox
   * for entity name - textBox for attributes - textBox for methods
   *
   * @return an array containing all TextBox
   */
  public List<TextBox> getAllTextBox() {
    List<TextBox> tb = new LinkedList<>();
    tb.add(entityName);
    return tb;
  }
  
  @Override
  public IDiagramComponent getAssociedComponent() {
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
  public Color getColor() {
    if (pictureMode) return defaultColor;
    return super.getColor();
  }
  
  @Override
  public void setColor(Color color) {
    setCurrentColor(color);
    defaultColor = color;
  }
  
  /**
   * Get the entity (UML) associed with this entity view. Same as
   * getAssociedComponent().
   *
   * @return the component associed.
   */
  public Entity getComponent() {
    return component;
  }
  
  /**
   * Set the current color for this entity.
   *
   * @param color
   *          the current color.
   */
  public void setCurrentColor(Color color) {
    super.setColor(color);
  }
  
  @Override
  public Color getDefaultColor() {
    return getBasicColor();
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
                            String.valueOf(getAssociedComponent().getId()));
    entityView.setAttribute("color", String.valueOf(defaultColor.getRGB()));
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
  public void maybeShowPopup(MouseEvent e, JPopupMenu popupMenu) {
    if (e.isPopupTrigger()) {
      String text = "Delete ";

      // If context menu is requested on a TextBox, customize popup menu.
      if (pressedTextBox == null) {
        text += component.getName();
        menuItemMoveUp.setEnabled(false);
        menuItemMoveDown.setEnabled(false);
      }
      menuItemDelete.setText(text);
    }
    super.maybeShowPopup(e, popupMenu);
  }

  @Override
  public void paintComponent(Graphics2D g2) {
    if (!isVisible()) return;

    Paint background;
    if (GraphicView.isEntityGradient())
      background = new GradientPaint(bounds.x, bounds.y, getColor(), bounds.x
                                                                     + bounds.width, bounds.y + bounds.height, getColor().darker());
    else
      background = getColor();

    String className = component.getName();

    FontMetrics classNameMetrics = g2.getFontMetrics(entityName
        .getEffectivFont());
    int classNameWidth = classNameMetrics.stringWidth(className);
    int classNameHeight = classNameMetrics.getHeight();

    Dimension classNameSize = new Dimension(classNameWidth, classNameHeight);

    stereotypeFont = stereotypeFont.deriveFont(stereotypeFontBasic.getSize()
                                               * parent.getZoom());
    
    g2.setFont(stereotypeFont);
    final String fullStereotype = "<< " + component.getStereotype() + " >>";
    final String truncatStereotype = Utility.truncate(g2, fullStereotype, bounds.width - 15);
    final FontMetrics stereotypeMetrics = g2.getFontMetrics(stereotypeFont);
    
    fullWidthStereotype = stereotypeMetrics.stringWidth(fullStereotype);
    int stereotypeWidth = stereotypeMetrics.stringWidth(truncatStereotype);
    int stereotypeHeight = stereotypeMetrics.getHeight();

    Dimension stereotypeSize = new Dimension(stereotypeWidth, stereotypeHeight);

    FontMetrics metrics = g2.getFontMetrics(entityName.getEffectivFont());
    int textBoxHeight = metrics.getHeight();

    bounds.height = computeHeight(classNameSize.height, stereotypeHeight,
                                                        textBoxHeight);
    
    Rectangle bounds = getBounds();

    int offset = bounds.y + VERTICAL_SPACEMENT / 2;
    int stereotypeLocationWidth = bounds.x
                                  + (bounds.width - stereotypeSize.width) / 2;
    
    entityName.setBounds(new Rectangle(1, 1, bounds.width - 15,
        textBoxHeight + 2));
    Rectangle entityNameBounds = entityName.getBounds();
    int classNameLocationX = bounds.x + (bounds.width - entityNameBounds.width)
                                        / 2;
    
    // draw background
    g2.setPaint(background);
    g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

    // draw border
    g2.setStroke(new BasicStroke(BORDER_WIDTH));
    g2.setColor(DEFAULT_BORDER_COLOR);
    g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

    // draw stereotype
    if (!component.getStereotype().isEmpty()) {
      offset += stereotypeSize.height;

      g2.setFont(stereotypeFont);
      g2.setColor(DEFAULT_TEXT_COLOR);
      g2.drawString(truncatStereotype, stereotypeLocationWidth, offset);
    }

    // draw class name
    offset += /* classNameSize.height + */VERTICAL_SPACEMENT / 2;

    entityName.setBounds(new Rectangle(classNameLocationX, offset,
        bounds.width - 15, textBoxHeight + 2));
    entityName.paintComponent(g2);

    offset += entityNameBounds.height;

    offset += paintTextBoxes(g2, bounds, textBoxHeight, offset);

    // is component selected? -> draw selected style
    if (!pictureMode && parent.getSelectedComponents().contains(this))
      drawSelectedStyle(g2);
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
   * @param tb
   *          the TextBox containing the element to remove.
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
      if (textbox.getAssociedComponent() == search) return textbox;
    
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
    
    JMenuItem menuItem;
    
    // Create the popup menu.
    popupMenu.addSeparator();
    initializeMenuItemsAddElements(popupMenu);
    initializeMenuItemsPropertiesElements(popupMenu);
    
    menuItemMoveUp = makeMenuItem("Move up", Slyum.ACTION_TEXTBOX_UP,
                                             "arrow-up");
    menuItemMoveUp.setEnabled(false);
    popupMenu.add(menuItemMoveUp);
    
    menuItemMoveDown = makeMenuItem("Move down", Slyum.ACTION_TEXTBOX_DOWN,
                                                 "arrow-down");
    menuItemMoveDown.setEnabled(false);
    popupMenu.add(menuItemMoveDown);

    popupMenu.addSeparator();

    popupMenu
            .add(makeMenuItem("Duplicate", Slyum.ACTION_DUPLICATE, "duplicate"));
    popupMenu.add(menuItemDelete = makeMenuItem("Delete", "Delete", "delete"));

    popupMenu.addSeparator();
    initializeMenuViews(popupMenu);

    SPanelElement p = SPanelElement.getInstance();
    menuItem = makeMenuItem("Move top", Slyum.ACTION_MOVE_TOP, "top");
    p.getBtnTop().linkComponent(menuItem);
    popupMenu.add(menuItem);
    
    menuItem = makeMenuItem("Up", Slyum.ACTION_MOVE_UP, "up");
    p.getBtnUp().linkComponent(menuItem);
    popupMenu.add(menuItem);
    
    menuItem = makeMenuItem("Down", Slyum.ACTION_MOVE_DOWN, "down");
    p.getBtnDown().linkComponent(menuItem);
    popupMenu.add(menuItem);

    menuItem = makeMenuItem("Move bottom", Slyum.ACTION_MOVE_BOTTOM, "bottom");
    p.getBtnBottom().linkComponent(menuItem);
    popupMenu.add(menuItem);

    component.addObserver(this);
    setColor(getBasicColor());
  }

  protected abstract void initializeMenuItemsAddElements(JPopupMenu popupmenu);

  protected abstract void initializeMenuItemsPropertiesElements(JPopupMenu popupMenu);

  protected abstract void initializeMenuViews(JPopupMenu popupMenu);

  protected abstract void innerRegenerate();

  protected abstract int paintTextBoxes(Graphics2D g2, Rectangle bounds, int textboxHeight, int offset);

  /**
   * Search and return the Textbox (methods and attributes) at the given
   * location.
   *
   * @param location
   *          the location where find a TextBox
   * @return the found TextBox
   */
  private TextBox searchTextBoxAtLocation(Point location) {
    final List<TextBox> tb = getAllTextBox();
    tb.remove(entityName);
    return GraphicView.searchComponentWithPosition(tb, location);
  }
}
