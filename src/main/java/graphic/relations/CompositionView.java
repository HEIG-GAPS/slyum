package graphic.relations;

import classDiagram.relationships.Composition;
import graphic.GraphicView;
import graphic.entity.EntityView;

import java.awt.*;

/**
 * The LineView class represent a collection of lines making a link between two GraphicComponent. When it creates, the
 * LineView have one single line between the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a segment between each grips. Grips are
 * movable and a LineView have two special grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * <p>
 * A RelationView have an associated UML component.
 * <p>
 * An AssociationView is associated with an association UML component.
 * <p>
 * A CompositionView is associated with an Composition UML component.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class CompositionView extends BinaryView {
  /**
   * Create a new CompositionView between source and target.
   *
   * @param parent the graphic view
   * @param source the entity source
   * @param target the entity target
   * @param composition the composition UML
   * @param posSource the position for put the first MagneticGrip
   * @param posTarget the position for put the last MagneticGrip
   * @param checkRecursivity check if the relation is on itself
   */
  public CompositionView(GraphicView parent, EntityView source,
                         EntityView target, Composition composition, Point posSource,
                         Point posTarget, boolean checkRecursivity) {
    super(parent, source, target, composition, posSource, posTarget,
          checkRecursivity);
  }

  @Override
  protected void drawExtremity(Graphics2D g2, Point source, Point target) {
    AggregationView.paintExtremity(g2, points.get(1).getAnchor(), points
        .getFirst().getAnchor(), Color.BLACK, getColor());
  }

  @Override
  protected void paintNavigability(Graphics2D g2) {
    switch (association.getDirected()) {
      case FIRST_TO_SECOND:
        DependencyView.paintExtremity(g2, points.get(points.size() - 2)
                                                .getAnchor(), points.getLast().getAnchor());
        break;
      case SECOND_TO_FIRST:
        Point source = points.get(1).getAnchor(),
            target = points.getFirst().getAnchor();
        double width = target.x - source.x,
            height = target.y - source.y,
            hypo = Math.hypot(width, height),
            ratio = hypo / 26.f,
            x,
            y; // 26 is the size of the relation end.

        width /= ratio;
        height /= ratio;
        x = target.x - width;
        y = target.y - height;

        DependencyView.paintExtremity(g2, source, new Point((int) x, (int) y));
        break;

      case BIDIRECTIONAL:
      default:
        break;
    }
  }

  @Override
  public void restore() {
    super.restore();
    parent.getClassDiagram().addComposition((Composition) getAssociatedComponent(), false);

    repaint();
  }

}
