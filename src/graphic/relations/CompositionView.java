package graphic.relations;

import graphic.GraphicView;
import graphic.entity.EntityView;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import classDiagram.relationships.Composition;

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
 * An AssociationView is associated with an association UML component.
 * 
 * A CompositionView is associated with an Composition UML component.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class CompositionView extends BinaryView
{
	/**
	 * Create a new CompositionView between source and target.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param source
	 *            the entity source
	 * @param target
	 *            the entity target
	 * @param composition
	 *            the composition UML
	 * @param posSource
	 *            the position for put the first MagneticGrip
	 * @param posTarget
	 *            the position for put the last MagneticGrip
	 * @param checkRecursivity
	 *            check if the relation is on itself
	 */
	public CompositionView(GraphicView parent, EntityView source, EntityView target, Composition composition, Point posSource, Point posTarget, boolean checkRecursivity)
	{
		super(parent, source, target, composition, posSource, posTarget, checkRecursivity);
	}

	@Override
	protected void drawExtremity(Graphics2D g2, Point source, Point target)
	{
		AggregationView.paintExtremity(g2, points.get(1).getAnchor(), points.getFirst().getAnchor(), Color.BLACK, getColor());
	}
	
	@Override
	public void restore()
	{
		super.restore();
		parent.getClassDiagram().addComposition((Composition)getAssociedComponent());
		
		repaint();
	}
}
