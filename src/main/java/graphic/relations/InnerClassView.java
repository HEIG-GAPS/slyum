package graphic.relations;

import classDiagram.IDiagramComponent;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.relationships.InnerClass;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.entity.InterfaceView;
import utility.SMessageDialog;

import java.awt.*;

public class InnerClassView extends RelationView {

  /**
   * Paint the extremity of the relation in the direction given by the source point.
   *
   * @param g2 the graphic context
   * @param source this point define the direction of the arrow
   * @param target this point define the location of the arrow
   * @param borderColor the color border
   */
  public static void paintExtremity(Graphics2D g2, Point source, Point target,
                                    Color borderColor) {
    final double deltaX = target.x - source.x;
    final double deltaY = target.y - source.y;
    final double alpha = Math.atan2(deltaY, deltaX);
    final double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    final double x = Math.cos(alpha) * (length - 10.0) + source.x;
    final double y = Math.sin(alpha) * (length - 10.0) + source.y;

    final Point ref = new Point((int) x, (int) y);

    final double x2 = Math.cos(alpha) * (length - 20.0) + source.x;
    final double y2 = Math.sin(alpha) * (length - 20.0) + source.y;

    final int vectorX = target.x
                        - (int) (Math.cos(alpha) * (length - 10.0) + source.x);
    final int vectorY = target.y
                        - (int) (Math.sin(alpha) * (length - 10.0) + source.y);

    final int vectorXN1 = -vectorY;
    final int vectorYN1 = vectorX;
    final int vectorXN2 = vectorY;
    final int vectorYN2 = -vectorX;

    // Circle
    g2.setStroke(new BasicStroke(LINE_WIDTH));
    g2.setColor(new Color(255, 246, 219));
    g2.fillOval((int) x - 10, (int) y - 10, 20, 20);
    g2.setColor(borderColor);
    g2.drawOval((int) x - 10, (int) y - 10, 20, 20);

    // Cross
    g2.drawLine((int) x2, (int) y2, target.x, target.y);
    g2.drawLine(ref.x + vectorXN1, ref.y + vectorYN1, ref.x + vectorXN2, ref.y
                                                                         + vectorYN2);
  }

  private InnerClass innerClass;

  public InnerClassView(GraphicView parent, EntityView source,
                        EntityView target, InnerClass innerClass, Point posSource,
                        Point posTarget, boolean checkRecursivity) {

    super(parent, source, target, innerClass, posSource, posTarget,
          checkRecursivity);

    this.innerClass = innerClass;
  }

  @Override
  protected void drawExtremity(Graphics2D g2, Point source, Point target) {
    paintExtremity(g2, source, target, getColor());
  }

  @Override
  public void delete() {
    super.delete();

    innerClass.getChild().removeParent(innerClass);
    innerClass.getParent().removeChild(innerClass);
  }

  @Override
  public IDiagramComponent getAssociatedComponent() {
    return innerClass;
  }

  /**
   * Set the child of the inheritance
   *
   * @param child the new child of the inheritance
   */
  public void setChild(EntityView child) {
    innerClass.setChild((Entity) child.getAssociatedComponent());
  }

  /**
   * Set the parent of the inheritance
   *
   * @param parent the new parent of the inheritance
   */
  public void setParent(EntityView parent) {
    innerClass.setParent((Entity) parent.getAssociatedComponent());
  }

  @Override
  public void setSelected(boolean select) {
    if (isSelected() == select) return;
    super.setSelected(select);
    innerClass.select();

    if (select)
      innerClass.notifyObservers(IDiagramComponent.UpdateMessage.SELECT);
    else
      innerClass.notifyObservers(IDiagramComponent.UpdateMessage.UNSELECT);
  }

  @Override
  public boolean relationChanged(
      MagneticGrip gripSource, GraphicComponent target) {

    if (target.getClass() == InterfaceView.class) return false;

    return super.relationChanged(gripSource, target);
  }

  @Override
  public void changeOrientation() {
    if (innerClass.getParent().getClass() == InterfaceEntity.class)
      SMessageDialog.showErrorMessage(
          "Unable to reverse this relation.\n" +
          "An interface cannot have nested classes.");
    else
      super.changeOrientation();
  }

  @Override
  public void restore() {
    super.restore();
    parent.getClassDiagram().addInnerClass((InnerClass) getAssociatedComponent(), false);

    repaint();
  }

}
