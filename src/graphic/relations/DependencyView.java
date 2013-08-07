package graphic.relations;

import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.textbox.TextBoxLabelTitle;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Entity;
import classDiagram.relationships.Dependency;

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
 * A DependencyView is associated with a dependency UML.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class DependencyView extends RelationView
{
	/**
	 * Paint the extremity of the relation in the direction given by the source
	 * point.
	 * 
	 * @param g2
	 *            the graphic context
	 * @param source
	 *            this point define the direction of the arrow
	 * @param target
	 *            this point define the location of the arrow
	 * @param borderColor
	 *            the color border
	 */
	public static void paintExtremity(Graphics2D g2, Point source, Point target)
	{
		final double deltaX = target.x - source.x;
		final double deltaY = target.y - source.y;
		final double alpha = Math.atan2(deltaY, deltaX);
		final double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		final double x = Math.cos(alpha) * (length - 20.0) + source.x;
		final double y = Math.sin(alpha) * (length - 20.0) + source.y;

		final Point ref = new Point((int) x, (int) y);

		final int vectorX = target.x - (int) (Math.cos(alpha) * (length - 8.0) + source.x);
		final int vectorY = target.y - (int) (Math.sin(alpha) * (length - 8.0) + source.y);

		final int vectorXN1 = -vectorY;
		final int vectorYN1 = vectorX;
		final int vectorXN2 = vectorY;
		final int vectorYN2 = -vectorX;

		final int[] pointsX = new int[] { ref.x + vectorXN1, target.x, ref.x + vectorXN2 };
		final int[] pointsY = new int[] { ref.y + vectorYN1, target.y, ref.y + vectorYN2 };

		g2.setStroke(new BasicStroke(LINE_WIDTH));
		g2.drawPolyline(pointsX, pointsY, pointsX.length);
	}

	private final Dependency dependency;

	/**
	 * Create a new DependencyView between source and target.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param source
	 *            the entity source
	 * @param target
	 *            the entity target
	 * @param dependency
	 *            the dependency UML
	 * @param posSource
	 *            the position for put the first MagneticGrip
	 * @param posTarget
	 *            the position for put the last MagneticGrip
	 * @param checkRecursivity
	 *            check if the relation is on itself
	 */
	public DependencyView(GraphicView parent, EntityView source, EntityView target, Dependency dependency, Point posSource, Point posTarget, boolean checkRecursivity)
	{
		super(parent, source, target, dependency, posSource, posTarget, checkRecursivity);

		this.dependency = dependency;
		
		lineStroke = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 7.f }, 0.0f);

		final TextBoxLabelTitle tb = new TextBoxLabelTitle(parent, dependency, this);
		tbRoles.add(tb);
		parent.addOthersComponents(tb);
	}

	@Override
	protected void drawExtremity(Graphics2D g2, Point source, Point target)
	{
		g2.setColor(getColor());
		paintExtremity(g2, source, target);
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return dependency;
	}

	@Override
	public void setSelected(boolean select)
	{
		if (isSelected() == select)
			return;

		super.setSelected(select);

		dependency.select();

		if (select)
			dependency.notifyObservers(UpdateMessage.SELECT);
		else
			dependency.notifyObservers(UpdateMessage.UNSELECT);
	}

	/**
	 * Set the source of the inheritance
	 * 
	 * @param source
	 *            the new source of the inheritance
	 */
	public void setSource(EntityView source)
	{
		dependency.setSource((Entity) source.getAssociedComponent());
	}

	/**
	 * Set the target of the inheritance
	 * 
	 * @param target
	 *            the new target of the inheritance
	 */
	public void setTarget(EntityView target)
	{
		dependency.setTarget((Entity) target.getAssociedComponent());
	}
	
	@Override
	public void restore()
	{
		super.restore();
		parent.getClassDiagram().addDependency((Dependency)getAssociedComponent());
		
		repaint();
	}
}
