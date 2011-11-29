package graphic.relations;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.textbox.TextBoxCommentary;

import java.awt.BasicStroke;
import java.awt.Point;

/**
 * The LineView class represent a collection of lines making a link between two
 * GraphicComponent. When it creates, the LineView have one single line between
 * the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a
 * segment between each grips. Grips are movable and a LineView have two special
 * grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * 
 * A LineCommentary is a link between a TextBoxCommentary and a
 * GraphicComponent.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class LineCommentary extends LineView
{

	/**
	 * Create a new LineCommentary between a TextBoxCommentary and a
	 * GraphicComponent. One of the both source or target must be a
	 * TextBoxCommentary.
	 * 
	 * @param graphicView
	 *            the graphic view
	 * @param source
	 *            a graphic component
	 * @param target
	 *            a graphic component
	 * @param posSource
	 *            the point where the first grip will be placed
	 * @param posTarget
	 *            the point where the last grip will be placed
	 * @param checkRecursivity
	 *            check if the relation is on itself
	 */
	public LineCommentary(GraphicView graphicView, GraphicComponent source, GraphicComponent target, Point posSource, Point posTarget, boolean checkRecursivity)
	{
		super(graphicView, source, target, posSource, posTarget, checkRecursivity);

		if (source instanceof GraphicView || target instanceof GraphicView)

			delete();

		setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 4.f }, 0.0f));
	}

	@Override
	public boolean relationChanged(GraphicComponent oldCompo, GraphicComponent newCompo)
	{
		if (oldCompo instanceof TextBoxCommentary && !(newCompo instanceof TextBoxCommentary) || !(oldCompo instanceof TextBoxCommentary) && newCompo instanceof TextBoxCommentary)

			return false;

		if (newCompo instanceof GraphicView)

			delete();

		return true;
	}
}
