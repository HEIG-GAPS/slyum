package graphic.relations;

import graphic.GraphicView;
import graphic.entity.EntityView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import classDiagram.relationships.Inheritance;

public class InnerClassView extends InheritanceView {

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

  public InnerClassView(GraphicView parent, EntityView source,
          EntityView target, Inheritance inheritance, Point posSource,
          Point posTarget, boolean checkRecursivity) {
    super(parent, source, target, inheritance, posSource, posTarget,
            checkRecursivity);
  }

  @Override
  protected void drawExtremity(Graphics2D g2, Point source, Point target) {
    paintExtremity(g2, source, target, getColor());
  }
}
