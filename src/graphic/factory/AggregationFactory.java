package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.relations.AggregationView;

import java.awt.Color;
import java.awt.Graphics2D;

import swing.SPanelDiagramComponent;

import classDiagram.relationships.Aggregation;

/**
 * AggregationFactory allows to create a new aggregation view associated with a
 * new association UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class AggregationFactory extends RelationFactory
{

	/**
	 * Create a new factory allowing the creation of an aggregation.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param classDiagram
	 *            the class diagram
	 */
	public AggregationFactory(GraphicView parent)
	{
		super(parent);
    
    GraphicView.setButtonFactory(
        SPanelDiagramComponent.getInstance().getBtnAggregation());
	}

	@Override
	public GraphicComponent create()
	{
		if (componentMousePressed instanceof EntityView && componentMouseReleased instanceof EntityView)
		{
			final EntityView source = (EntityView) componentMousePressed;
			final EntityView target = (EntityView) componentMouseReleased;

			final Aggregation aggregation = new Aggregation(source.getComponent(), target.getComponent(), false);
			final AggregationView a = new AggregationView(parent, source, target, aggregation, mousePressed, mouseReleased, true);

			parent.addLineView(a);
			classDiagram.addAggregation(aggregation);

			parent.unselectAll();
			a.setSelected(true);

			return a;
		}

		repaint();
		return null;
	}

	@Override
	protected void drawExtremity(Graphics2D g2)
	{
		AggregationView.paintExtremity(g2, mouseLocation, mousePressed, Color.WHITE, Color.DARK_GRAY);
	}
}
