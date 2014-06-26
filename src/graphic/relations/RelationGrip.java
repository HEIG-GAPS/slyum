package graphic.relations;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.SquareGrip;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import utility.Utility;
import change.BufferBounds;
import change.BufferCreation;
import change.Change;
import java.awt.Color;

/**
 * The relationGrip is a grip who customize a LineView. The LineView uses
 * RelationGrip for draw segments between each RelationGrip.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class RelationGrip extends SquareGrip {
  public static int DEFAULT_SIZE = 10;

  private Point anchor = new Point();

  protected LineView relation;
  private int index = -1;

  private boolean isMouseDragged;

  /**
   * Create a new RelationGrip associate with the given LineView.
   * 
   * @param parent
   *          the graphic view
   * @param relationView
   *          the LineView associated.
   */
  public RelationGrip(GraphicView parent, LineView relationView) {
    super(parent, DEFAULT_SIZE, Cursor.HAND_CURSOR);

    if (relationView == null)
      throw new IllegalArgumentException("relation is null");

    relation = relationView;

    Change.push(new BufferCreation(false, this));
    Change.push(new BufferCreation(true, this));
  }

  /**
   * Get the anchor of the RelationGrip. Anchor is the middle of the grip.
   * 
   * @return the anchor of the grip.
   */
  public Point getAnchor() {
    return new Point(anchor);
  }

  @Override
  public Rectangle getBounds() {
    return new Rectangle(anchor.x - DEFAULT_SIZE / 2, anchor.y - DEFAULT_SIZE
            / 2, DEFAULT_SIZE, DEFAULT_SIZE);
  }

  @Override
  public void gMouseDragged(MouseEvent e) {
    Point mouse = e.getPoint();
    relation.showGrips(true);
    setAnchor(new Point(mouse.x, mouse.y));
    notifyObservers();
    isMouseDragged = true;
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    super.gMouseClicked(e);

    if (!GraphicView.isAddToSelection(e)) {
      parent.unselectAll();
      relation.setSelected(true);
    } else {
      relation.setSelected(!relation.isSelected());
    }
  }

  @Override
  public void gMouseEntered(MouseEvent e) {
    super.gMouseEntered(e);
    relation.showGrips(true);
  }

  @Override
  public void gMouseExited(MouseEvent e) {
    previousVisible = true;
    super.gMouseExited(e);
    if (!relation.isSelected()) relation.showGrips(false);
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    isMouseDragged = false;

    if (e.getButton() == MouseEvent.BUTTON1) {
      Change.record();
      Change.push(new BufferBounds(this));
    }
    maybeShowPopup(e, relation);
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    GraphicComponent component = parent.getDiagramElementAtPosition(
            getAnchor(), null), source = relation.getFirstPoint()
            .getAssociedComponentView(), target = relation.getLastPoint()
            .getAssociedComponentView();

    pushBufferChangeMouseReleased(e);
    relation.smoothLines();
    relation.searchUselessAnchor(this);

    if (component != null
            && (component.equals(source) || component.equals(target)))
      delete();

    Change.stopRecord();

    maybeShowPopup(e, relation);
    isMouseDragged = false;

    if (!relation.isSelected()) relation.showGrips(false);
  }

  @Override
  public Color getFillColor() {
    Color c = super.getFillColor();
    if (relation.isSelected())
      return c;
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), 100);
  }

  @Override
  public Color getBorderColor() {
    if (relation.isSelected())
      return super.getBorderColor();
    
    return super.getBorderColor().brighter().brighter().brighter();
  }

  protected void pushBufferChangeMouseReleased(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) if (isMouseDragged) {
      if (Change.getSize() % 2 == 1) Change.push(new BufferBounds(this));
    } else {
      Change.pop();
    }
  }

  @Override
  public void delete() {
    // save index
    index = relation.getPoints().indexOf(this);
    super.delete();
    relation.removeGrip(this);
    notifyObservers();
  }

  @Override
  public void restore() {
    super.restore();

    parent.addOthersComponents(this);
    relation.addGrip(this, index);
  }

  /**
   * Set the anchor of the grip.
   * 
   * @param anchor
   *          the new anchor for this grip.
   */
  public void setAnchor(Point anchor) {
    if (anchor == null) return;

    Rectangle repaintBounds = Utility.growRectangle(relation.getBounds(),
            GraphicView.getGridSize() + 20);

    this.anchor = adjustOnGrid(anchor);
    relation.gripMoved(repaintBounds);

    setChanged();
  }

  public static int adjust(int value) {
    // Use the integer cast for adjusting value.
    return Math.round(value / (float) GraphicView.getGridSize())
            * GraphicView.getGridSize();
  }

  protected Point adjustOnGrid(Point pt) {
    return new Point(adjust(pt.x), adjust(pt.y));
  }

  @Override
  public void setBounds(Rectangle bounds) {
    setAnchor(new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height
            / 2));
    notifyObservers();
  }

  public LineView getRelation() {
    return relation;
  }
}
