package graphic.factory;

import change.BufferDeepCreation;
import change.Change;
import classDiagram.relationships.Association.NavigateDirection;
import classDiagram.relationships.Composition;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.relations.AggregationView;
import graphic.relations.CompositionView;
import swing.SPanelDiagramComponent;

import java.awt.*;

/**
 * CompositionFactpry allows to create a new composition view associated with a new association UML. Give this factory
 * at the graphic view using the method initNewComponent() for initialize a new factory. Next, graphic view will use the
 * factory to allow creation of a new component, according to the specificity of the factory.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class CompositionFactory extends RelationFactory {
  /**
   * Create a new factory allowing the creation of a composition.
   *
   * @param parent the graphic view
   */
  public CompositionFactory(final GraphicView parent) {
    super(parent);

    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
                                                       .getBtnComposition());
  }

  @Override
  public GraphicComponent create() {
    if (isFirstComponentValid() && componentMouseReleased instanceof EntityView) {
      final EntityView source = (EntityView) componentMousePressed;
      final EntityView target = (EntityView) componentMouseReleased;

      final Composition composition = new Composition(source.getComponent(),
                                                      target.getComponent(), NavigateDirection.BIDIRECTIONAL);
      final CompositionView c = new CompositionView(parent, source, target,
                                                    composition, mousePressed, mouseReleased, true);

      parent.addLineView(c);
      classDiagram.addComposition(composition);

      Change.push(new BufferDeepCreation(false, composition));
      Change.push(new BufferDeepCreation(true, composition));

      parent.unselectAll();
      c.setSelected(true);

      return c;
    }

    repaint();
    return null;
  }

  @Override
  protected boolean isFirstComponentValid() {
    return componentMousePressed instanceof EntityView;
  }

  @Override
  protected void drawExtremity(Graphics2D g2) {
    Point p = points.size() < 2 ? mouseLocation : points.get(1);
    AggregationView.paintExtremity(g2, p, points.get(0), Color.BLACK,
                                   Color.DARK_GRAY);
  }

}
