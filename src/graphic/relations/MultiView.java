package graphic.relations;

import change.Change;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Association.NavigateDirection;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Role;
import graphic.ColoredComponent;
import graphic.GraphicView;
import graphic.MovableComponent;
import graphic.entity.ClassView;
import graphic.entity.EntityView;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenuItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import utility.Utility;

/**
 * MultiView is represented by a diamond and it represents a multi-association
 * UML.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class MultiView extends MovableComponent implements Observer, ColoredComponent {
  private Rectangle bounds = new Rectangle(50, 50);

  LinkedList<MultiLineView> mlvs = new LinkedList<>();

  private final Multi multi;
  private boolean ligthDelete;

  /**
   * Create a new MultiView associated with the multi UML.
   * 
   * @param parent
   *          the graphic view
   * @param multi
   *          the multi UML
   */
  public MultiView(GraphicView parent, Multi multi) {
    super(parent);

    if (multi == null) throw new IllegalArgumentException("multi is null");

    this.multi = multi;
    multi.addObserver(this);

    final LinkedList<Role> roles = multi.getRoles();
    Rectangle bounds = getBounds();
    Point middleClass;
    final Point middle = new Point((int) bounds.getCenterX(),
            (int) bounds.getCenterY());
    int xMoy = 0, yMoy = 0;

    for (Role role : roles) {
      final ClassView cv = (ClassView) parent.searchAssociedComponent(role.getEntity());
      bounds = cv.getBounds();
      middleClass = new Point((int) bounds.getCenterX(),
              (int) bounds.getCenterY());

      final MultiLineView newmlv = new MultiLineView(parent, this, cv, role,
              middle, middleClass, false);

      mlvs.add(newmlv);
      parent.addLineView(newmlv);

      xMoy += bounds.x + bounds.width / 2;
      yMoy += bounds.y + bounds.height / 2;
    }

    xMoy /= roles.size();
    yMoy /= roles.size();

    bounds = getBounds();
    setBounds(new Rectangle(xMoy, yMoy, bounds.width, bounds.height));
    setColor(EntityView.getBasicColor());

    popupMenu.addSeparator();

    final JMenuItem menuItem = makeMenuItem("Delete", "Delete", "delete");
    popupMenu.add(menuItem);
    
    super.pushBufferCreation();

    // Cet élément n'est pas redimensionnable. Suppression des grips.
    boolean isBlocked = Change.isBlocked();
    Change.setBlocked(true);
    leftMovableSquare.delete();
    rightMovableSquare.delete();
    Change.setBlocked(isBlocked);
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);

    if ("Delete".equals(e.getActionCommand()))
      GraphicView.deleteComponent(this);
  }

  public void addMultiLineView(MultiLineView mlv) {
    if (mlvs.contains(mlv)) return;

    mlvs.add(mlv);
    parent.addLineView(mlv);
  }

  @Override
  public Point computeAnchorLocation(Point first, Point next) {
    final Polygon polygon = getPolygon();

    final int[] xpoints = polygon.xpoints;
    final int[] ypoints = polygon.ypoints;
    final int npoins = polygon.npoints;
    int bestDistance = Short.MAX_VALUE, bestI = 0;

    for (int i = 0; i < npoins; i++) {
      final Point point = new Point(xpoints[i], ypoints[i]);
      int distance = (int) point.distance(next);
      distance += (int) point.distance(first);

      if (distance < bestDistance) {
        bestDistance = distance;
        bestI = i;
      }
    }

    return new Point(xpoints[bestI], ypoints[bestI]);
  }

  /**
   * This method is calls when a connexion is removed.
   * 
   * @param mlv
   *          the connexion that was removed
   */
  public void connexionRemoved(MultiLineView mlv) {
    multi.removeRole((Role) mlv.getTextBoxRole().getFirst()
            .getAssociedComponent());
    mlvs.remove(mlv);
  }

  /**
   * Draw the ghost representation.
   * 
   * @param g2
   *          the graphic context
   */
  public void drawGhost(Graphics2D g2) {
    if (pictureMode) return;

    final Polygon polygon = getPolygonFromBounds(ghost);
    final Color color = getColor();
    final BasicStroke borderStroke = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
            new float[] { 2.0f }, 0.0f);

    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
            100));
    g2.fillPolygon(polygon);

    g2.setStroke(borderStroke);
    g2.setColor(color);
    g2.drawPolygon(polygon);
  }
  
  @Override
  public void delete() {
    if (!parent.containsComponent(this)) return;
    
    //if (ligthDelete)
      super.delete();
    //else
      //deleteWithoutChanges();

    if (!ligthDelete)
      parent.getClassDiagram().removeComponent(getAssociedComponent());
  }
  
  public void deleteWithoutChanges() {
    boolean isBlocked = Change.isBlocked();
    Change.setBlocked(true);
    super.delete();
    Change.setBlocked(isBlocked);
  }
  
  @Override
  public void lightDelete() {
    boolean isLigthDelete = ligthDelete;
    ligthDelete = true;
    delete();
    ligthDelete = isLigthDelete;
  }

  @Override
  public IDiagramComponent getAssociedComponent() {
    return multi;
  }

  @Override
  public Rectangle getBounds() {
    if (bounds == null) bounds = new Rectangle();

    return new Rectangle(bounds);
  }

  @Override
  public Color getDefaultColor() {
    return EntityView.getBasicColor();
  }

  @SuppressWarnings("unchecked")
  public LinkedList<MultiLineView> getMultiLinesView() {
    return (LinkedList<MultiLineView>) mlvs.clone();
  }

  /**
   * Get the polygon from the bounds.
   * 
   * @return the polygon from the bounds
   */
  public Polygon getPolygon() {
    return getPolygonFromBounds(getBounds());
  }

  /**
   * Get a polygon from the given bounds.
   * 
   * @param bounds
   *          the bounds to transform in polygon
   * @return a polygon
   */
  public Polygon getPolygonFromBounds(Rectangle bounds) {
    final int x1 = bounds.x, x2 = bounds.x + bounds.width / 2, x3 = bounds.x
            + bounds.width, y1 = bounds.y, y2 = bounds.y + bounds.height / 2, y3 = bounds.y
            + bounds.height;

    final int[] xpoints = { x1, x2, x3, x2 };
    final int[] ypoints = { y2, y1, y2, y3 };

    return new Polygon(xpoints, ypoints, xpoints.length);
  }

  @Override
  public void gMouseEntered(MouseEvent e) {
    super.gMouseEntered(e);

    parent.getScene().setCursor(new Cursor(Cursor.MOVE_CURSOR));
  }

  @Override
  public void gMouseExited(MouseEvent e) {
    super.gMouseExited(e);

    parent.getScene().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  @Override
  public boolean isAtPosition(Point position) {
    return getPolygon().contains(position);
  }

  @Override
  public void paintComponent(Graphics2D g2) {
    final Polygon polygon = getPolygon();

    // Draw Polygon
    g2.setStroke(new BasicStroke(1.3f));

    g2.setColor(getColor());
    g2.fillPolygon(polygon);

    g2.setColor(Color.DARK_GRAY);
    g2.drawPolygon(polygon);

    if (!ghost.isEmpty()) drawGhost(g2);

    if (!pictureMode && isSelected()) {
      final int PADDING = 3;

      g2.setColor(Color.DARK_GRAY);
      g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
              BasicStroke.JOIN_MITER, 10.0f, new float[] { 2f }, 0.0f));

      final int[] xPoints = new int[4];
      final int[] yPoints = new int[4];
      final int[] xPoints2 = new int[4];
      final int[] yPoints2 = new int[4];

      xPoints[0] = polygon.xpoints[0] - PADDING;
      yPoints[0] = polygon.ypoints[0];
      xPoints[1] = polygon.xpoints[1];
      yPoints[1] = polygon.ypoints[1] - PADDING;
      xPoints[2] = polygon.xpoints[2] + PADDING;
      yPoints[2] = polygon.ypoints[2];
      xPoints[3] = polygon.xpoints[3];
      yPoints[3] = polygon.ypoints[3] + PADDING;

      xPoints2[3] = polygon.xpoints[0] + PADDING;
      yPoints2[3] = polygon.ypoints[0];
      xPoints2[0] = polygon.xpoints[1];
      yPoints2[0] = polygon.ypoints[1] + PADDING;
      xPoints2[1] = polygon.xpoints[2] - PADDING;
      yPoints2[1] = polygon.ypoints[2];
      xPoints2[2] = polygon.xpoints[3];
      yPoints2[2] = polygon.ypoints[3] - PADDING;

      g2.drawPolygon(xPoints, yPoints, xPoints.length);
      g2.drawPolygon(xPoints2, yPoints2, xPoints2.length);
    }
  }

  @Override
  public void repaint() {
    parent.getScene().repaint(getBounds());
  }

  @Override
  public void resizeLeft(MouseEvent e) {
    // resizing impossible
  }

  @Override
  public void resizeRight(MouseEvent e) {
    // resizing impossible
  }

  @Override
  public void restore() {
    super.restore();

    parent.addMultiView(this);
    //parent.getClassDiagram().addMulti(multi);
  }

  @Override
  public void setBounds(Rectangle bounds) {
    final Rectangle repaintBounds = getBounds();

    this.bounds = bounds;

    // repaint previous et current position.
    parent.getScene().repaint(repaintBounds);
    parent.getScene().repaint(bounds);

    setChanged();
    notifyObservers();
  }

  @Override
  public void setResizerVisible(boolean visible) {
    // no resizer
    leftMovableSquare.setVisible(false);
    rightMovableSquare.setVisible(false);
  }

  @Override
  public void setSelected(boolean select) {
    if (isSelected() == select) return;

    super.setSelected(select);

    multi.select();

    if (select)
      multi.notifyObservers(UpdateMessage.SELECT);
    else
      multi.notifyObservers(UpdateMessage.UNSELECT);
  }

  @Override
  public String getXmlTagName() {
    return "multiView";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element multiView = doc.createElement(getXmlTagName());
    multiView.setAttribute("relationId",
            String.valueOf(getAssociedComponent().getId()));
    multiView.setAttribute("color", String.valueOf(getColor().getRGB()));
    multiView.appendChild(Utility.boundsToXmlElement(doc, getBounds(),
            "multiViewBounds"));

    for (MultiLineView lv : getMultiLinesView())
      multiView.appendChild(lv.getXmlElement(doc));

    return multiView;
  }

  /**
   * Transforme mutli-association to a binary association.
   */
  public void transformToAssociation() {
    final LinkedList<Role> roles = multi.getRoles();

    if (roles.size() != 2) return;

    final Binary binary = new Binary(roles.getFirst().getEntity(), roles
            .getLast().getEntity(), NavigateDirection.BIDIRECTIONAL,
            multi.getId());

    parent.getClassDiagram().addBinary(binary);
  }

  @Override
  public void update(Observable observable, Object o) {
    if (o != null && o.getClass() == UpdateMessage.class)
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

      repaint();
  }
}
