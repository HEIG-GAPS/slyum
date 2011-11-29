package graphic.relations;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.relationships.Aggregation;
import classDiagram.relationships.Inheritance;

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
public class InheritanceView extends RelationView
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
	public static void paintExtremity(Graphics2D g2, Point source, Point target, Color borderColor)
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

		final int[] pointsX = new int[] { target.x, ref.x + vectorXN1, ref.x + vectorXN2 };
		final int[] pointsY = new int[] { target.y, ref.y + vectorYN1, ref.y + vectorYN2 };

		g2.setStroke(new BasicStroke());
		g2.setColor(Color.WHITE);
		g2.fillPolygon(pointsX, pointsY, pointsX.length);
		g2.setColor(borderColor);
		g2.drawPolygon(pointsX, pointsY, pointsX.length);
	}

	private final Inheritance inheritance;

	/**
	 * Create a new InheritanceView between source and target.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param source
	 *            the entity source
	 * @param target
	 *            the entity target
	 * @param inheritance
	 *            the inheritance UML
	 * @param posSource
	 *            the position for put the first MagneticGrip
	 * @param posTarget
	 *            the position for put the last MagneticGrip
	 * @param checkRecursivity
	 *            check if the relation is on itself
	 */
	public InheritanceView(GraphicView parent, EntityView source, EntityView target, Inheritance inheritance, Point posSource, // location
	// for
	// computing
	// last
	// grip
	Point posTarget, boolean checkRecursivity) // location for computin
	// first grip
	{
		super(parent, source, target, inheritance, posSource, posTarget, checkRecursivity);

		this.inheritance = inheritance;

		if (getClass() == InheritanceView.class)
		{
			popupMenu.addSeparator();
			popupMenu.add(makeMenuItem("Overrides & Implementations...", "O&I", "method"));
		}

		if (inheritance.getParent().getClass() == InterfaceEntity.class)
			lineStroke = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 7.f }, 0.0f);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		if ("O&I".equals(e.getActionCommand()))
			inheritance.showOverridesAndImplementations();

	}

	@Override
	public void delete()
	{
		super.delete();

		inheritance.getChild().removeParent(inheritance);
		inheritance.getParent().removeChild(inheritance);
	}

	@Override
	protected void drawExtremity(Graphics2D g2, Point source, Point target)
	{
		paintExtremity(g2, source, target, getColor());
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return inheritance;
	}

	@Override
	public boolean relationChanged(GraphicComponent oldCompo, GraphicComponent newCompo)
	{
		if (newCompo.getClass() != oldCompo.getClass())
			return false;

		inheritance.getChild().removeParent(inheritance);
		inheritance.getParent().removeChild(inheritance);

		Entity newChild, newParent;

		if (oldCompo.equals(getFirstPoint().getAssociedComponentView()))
		{
			newChild = (Entity) newCompo.getAssociedComponent();
			newParent = inheritance.getParent();
		}
		else
		{
			newChild = inheritance.getChild();
			newParent = (Entity) newCompo.getAssociedComponent();
		}

		if (!Inheritance.validate(newChild, newParent))
		{
			inheritance.getChild().addParent(inheritance);
			inheritance.getParent().addChild(inheritance);
			return false;
		}

		inheritance.setChild(newChild);
		inheritance.setParent(newParent);

		return super.relationChanged(oldCompo, newCompo);
	}

	/**
	 * Set the child of the inheritance
	 * 
	 * @param child
	 *            the new child of the inheritance
	 */
	public void setChild(EntityView child)
	{
		inheritance.setChild((Entity) child.getAssociedComponent());
	}

	/**
	 * Set the parent of the inheritance
	 * 
	 * @param parent
	 *            the new parent of the inheritance
	 */
	public void setParent(EntityView parent)
	{
		inheritance.setParent((Entity) parent.getAssociedComponent());
	}

	@Override
	public void setSelected(boolean select)
	{
		if (isSelected() == select)
			return;

		super.setSelected(select);

		inheritance.select();

		if (select)
			inheritance.notifyObservers(UpdateMessage.SELECT);
		else
			inheritance.notifyObservers(UpdateMessage.UNSELECT);
	}
	
	@Override
	public void restore()
	{
		super.restore();
		parent.getClassDiagram().addInheritance((Inheritance)getAssociedComponent());
		
		repaint();
	}
}
