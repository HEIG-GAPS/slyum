package graphic.relations;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.SimpleEntity;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Inheritance;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.entity.SimpleEntityView;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import swing.Slyum;
import utility.SMessageDialog;

/**
 * The LineView class represent a collection of lines making a link between two
 * GraphicComponent. When it creates, the LineView have one single line between
 * the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a
 * segment between each grips. Grips are movable and a LineView have two special
 * grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * 
 * A RelationView have an associated UML component.
 * 
 * An InheritanceView is associated with an inheritance UML.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class InheritanceView extends RelationView {
  /**
   * Paint the extremity of the relation in the direction given by the source
   * point.
   * 
   * @param g2
   *          the graphic context
   * @param source
   *          this point define the direction of the arrow
   * @param target
   *          this point define the location of the arrow
   * @param borderColor
   *          the color border
   */
  public static void paintExtremity(Graphics2D g2, Point source, Point target,
          Color borderColor) {
    final double deltaX = target.x - source.x;
    final double deltaY = target.y - source.y;
    final double alpha = Math.atan2(deltaY, deltaX);
    final double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    final double x = Math.cos(alpha) * (length - 20.0) + source.x;
    final double y = Math.sin(alpha) * (length - 20.0) + source.y;

    final Point ref = new Point((int) x, (int) y);

    final int vectorX = target.x
            - (int) (Math.cos(alpha) * (length - 8.0) + source.x);
    final int vectorY = target.y
            - (int) (Math.sin(alpha) * (length - 8.0) + source.y);

    final int vectorXN1 = -vectorY;
    final int vectorYN1 = vectorX;
    final int vectorXN2 = vectorY;
    final int vectorYN2 = -vectorX;

    final int[] pointsX = new int[] { target.x, ref.x + vectorXN1,
            ref.x + vectorXN2 };
    final int[] pointsY = new int[] { target.y, ref.y + vectorYN1,
            ref.y + vectorYN2 };

    g2.setStroke(new BasicStroke(LINE_WIDTH));
    g2.setColor(Color.WHITE);
    g2.fillPolygon(pointsX, pointsY, pointsX.length);
    g2.setColor(borderColor);
    g2.drawPolygon(pointsX, pointsY, pointsX.length);
  }

  private final Inheritance inheritance;
  private JMenuItem menuItemOI;

  /**
   * Create a new InheritanceView between source and target.
   * 
   * @param parent
   *          the graphic view
   * @param source
   *          the entity source
   * @param target
   *          the entity target
   * @param inheritance
   *          the inheritance UML
   * @param posSource
   *          the position for put the first MagneticGrip
   * @param posTarget
   *          the position for put the last MagneticGrip
   * @param checkRecursivity
   *          check if the relation is on itself
   */
  public InheritanceView(GraphicView parent, EntityView source,
          EntityView target, Inheritance inheritance, Point posSource,
          Point posTarget, boolean checkRecursivity) {
    super(parent, source, target, inheritance, posSource, posTarget,
            checkRecursivity);

    this.inheritance = inheritance;

    popupMenu.addSeparator();
    popupMenu.add(menuItemOI = makeMenuItem("Overrides & Implementations...",
            "O&I", "method"));
    popupMenu.add(makeMenuItem("Autopath", Slyum.ACTION_ADJUST_INHERITANCE,
            "adjust-inheritance"));

    if (inheritance.getParent().getClass() == InterfaceEntity.class)
      lineStroke = getInterfaceLineStroke();
  }
  
  private Stroke getInterfaceLineStroke() {
    return new BasicStroke(
        LINE_WIDTH, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER, 10.0f, new float[] { 7.f }, 0.0f);
  }

  @Override
  public void maybeShowPopup(MouseEvent e, JPopupMenu popupMenu) {
    if (menuItemOI != null)
      menuItemOI.setEnabled(!inheritance.getParent().isEveryMethodsStatic());
    super.maybeShowPopup(e, popupMenu);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    if (null != e.getActionCommand())
      switch (e.getActionCommand()) {
      case "O&I":
        inheritance.showOverridesAndImplementations();
        break;
      case Slyum.ACTION_ADJUST_INHERITANCE:
        parent.adjustInheritances();
        // parent.adjustInheritances() will not adjust this inheritance if
        // it's not selected. This code will.
        if (!isSelected()) adjustInheritance();
        break;
    }
  }

  @Override
  public void delete() {
    super.delete();

    inheritance.getChild().removeParent(inheritance);
    inheritance.getParent().removeChild(inheritance);
  }

  @Override
  protected void drawExtremity(Graphics2D g2, Point source, Point target) {
    paintExtremity(g2, source, target, getColor());
  }

  @Override
  public IDiagramComponent getAssociedComponent() {
    return inheritance;
  }

  @Override
  public boolean relationChanged(
      MagneticGrip gripSource, GraphicComponent target) {
    
    Entity newChild, newParent;
    
    if (gripSource == getFirstPoint()) {
      newChild = (Entity)target.getAssociedComponent();
      newParent = (Entity)getLastPoint().getAssociedComponentView().getAssociedComponent();
    } else {
      newChild = (Entity)getFirstPoint().getAssociedComponentView().getAssociedComponent();
      newParent = (Entity)target.getAssociedComponent();
    }
    
    if (!(target instanceof EntityView) || 
        !Inheritance.validate(newChild, newParent)) 
      return false;

    return super.relationChanged(gripSource, target);
  }

  @Override
  public void adaptRelationToComponent() {
    
    super.adaptRelationToComponent();
    
    if (inheritance.getTarget().getClass() == InterfaceEntity.class)
      lineStroke = getInterfaceLineStroke();
    else
      lineStroke = getDefaultLineStroke();
  }

  @Override
  public void changeOrientation() {
    if (!Inheritance.validate(inheritance.getParent(), inheritance.getChild()))
      SMessageDialog.showErrorMessage(
          "Unable to reverse this relation.\n" + 
          "A class cannot be a parent of an interface.");
    else
      super.changeOrientation();
  }

  /**
   * Set the child of the inheritance
   * 
   * @param child
   *          the new child of the inheritance
   */
  public void setChild(SimpleEntityView child) {
    inheritance.setChild((SimpleEntity) child.getAssociedComponent());
  }

  /**
   * Set the parent of the inheritance
   * 
   * @param parent
   *          the new parent of the inheritance
   */
  public void setParent(SimpleEntityView parent) {
    inheritance.setParent((SimpleEntity) parent.getAssociedComponent());
  }

  @Override
  public void setSelected(boolean select) {
    if (isSelected() == select) return;
    super.setSelected(select);
    inheritance.select();

    if (select)
      inheritance.notifyObservers(UpdateMessage.SELECT);
    else
      inheritance.notifyObservers(UpdateMessage.UNSELECT);
  }

  public void adjustInheritance() {
    final int offsetChild = 10, offsetParent = offsetChild + 20; // px
    Rectangle boundsParent = getLastPoint().getAssociedComponentView()
            .getBounds(), boundsChild = getFirstPoint()
            .getAssociedComponentView().getBounds();
    Point ptParent = new Point(boundsParent.x + boundsParent.width / 2,
            boundsParent.y + boundsParent.height), ptChild = new Point(
            boundsChild.x + boundsChild.width / 2, boundsChild.y);

    // Supprime tous les grips présents afin de les repositionner correctement.
    reinitGrips();

    // Positionnement des grip centraux.
    // Plus haut ou plus bas?
    if (ptParent.y > ptChild.y - (offsetChild + offsetParent)) { // enfant plus
                                                                 // haut
      addGripAtLocation(1, new Point(ptChild.x, ptChild.y - offsetChild));

      // Enfant à gauche ou à droite?
      if (boundsChild.x > boundsParent.x + boundsParent.width) { // à droite
        int x = (boundsChild.x + boundsParent.x + boundsParent.width) / 2;
        addGripAtLocation(2, new Point(x, ptChild.y - offsetChild));
        addGripAtLocation(3, new Point(x, ptParent.y + offsetParent));

      } else if (boundsChild.x + boundsChild.width < boundsParent.x) { // à
                                                                       // gauche
        int x = (boundsChild.x + boundsChild.width + boundsParent.x) / 2;
        addGripAtLocation(2, new Point(x, ptChild.y - offsetChild));
        addGripAtLocation(3, new Point(x, ptParent.y + offsetParent));

      } else { // Ils se croisent

        // Ils sont proche
        if (ptParent.y <= ptChild.y) {
          reinitGrips();
          int limitLeft = Math.max(boundsChild.x, boundsParent.x), limitRight = Math
                  .min(boundsChild.x + boundsChild.width, boundsParent.x
                          + boundsParent.width);
          int offset = (limitRight - limitLeft) / 2 + limitLeft;
          getFirstPoint().setAnchor(new Point(offset, ptChild.y));
          getLastPoint().setAnchor(new Point(offset, ptParent.y));
          return;
        }

        // Quelle chemin est le plus court?
        if (Math.abs(boundsChild.x - boundsParent.x) < Math
                .abs((boundsChild.x + boundsChild.width)
                        - (boundsParent.x + boundsParent.width))) { // gauche
          int min = Math.min(boundsChild.x, boundsParent.x) - offsetChild;
          addGripAtLocation(2, new Point(min, ptChild.y - offsetChild));
          addGripAtLocation(3, new Point(min, ptParent.y + offsetParent));
        } else { // droite
          int max = Math.max(boundsChild.x + boundsChild.width, boundsParent.x
                  + boundsParent.width)
                  + offsetChild;
          addGripAtLocation(2, new Point(max, ptChild.y - offsetChild));
          addGripAtLocation(3, new Point(max, ptParent.y + offsetParent));
        }
      }
      addGripAtLocation(4, new Point(ptParent.x, ptParent.y + offsetParent));

    } else { // enfant plus bas

      int vertical = (ptChild.y - (ptParent.y + 20)) / 2;
      addGripAtLocation(1, new Point(ptChild.x, ptChild.y - vertical));
      addGripAtLocation(2, new Point(ptParent.x, ptChild.y - vertical));
    }

    // Positionnement des grips d'extremités.
    getFirstPoint().setAnchor(ptChild);
    getLastPoint().setAnchor(ptParent);

    // Remove useless grips (if the entities are aligned).
    searchUselessAnchor(getFirstPoint());
  }
  
  @Override
  public void restore() {
    super.restore();

    if (this.getClass().equals(InheritanceView.class))
      parent.getClassDiagram().addInheritance((Inheritance) getAssociedComponent(), false);

    repaint();
  }

  @Override
  protected boolean mustPaintIntersection(LineView otherLineView) {
    return super.mustPaintIntersection(otherLineView) &&
           !(otherLineView instanceof InheritanceView);
  }

}
