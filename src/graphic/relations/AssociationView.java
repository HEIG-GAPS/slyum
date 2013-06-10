package graphic.relations;

import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.textbox.TextBoxLabelTitle;

import java.awt.Graphics2D;
import java.awt.Point;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Association;

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
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class AssociationView extends RelationView
{
	private Association association;

	public AssociationView(
	    GraphicView parent, EntityView source, EntityView target, 
	    Association association, Point posSource, Point posTarget, 
	    boolean checkRecursivity)
	{
		super(parent, source, target, association, posSource, posTarget, checkRecursivity);

		this.association = association;

		TextBoxLabelTitle tb = new TextBoxLabelTitle(parent, association, this);
		tbRoles.add(tb);
		parent.addOthersComponents(tb);
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return association;
	}

	@Override
	public void paintComponent(Graphics2D g2)
	{
		super.paintComponent(g2);

		if (association.isDirected())
			DependencyView.paintExtremity(g2, points.get(points.size() - 2).getAnchor(), points.getLast().getAnchor());
	}

	@Override
	public void setSelected(boolean select)
	{
		if (isSelected() == select)
			return;

		super.setSelected(select);

		association.select();

		if (select)
			association.notifyObservers(UpdateMessage.SELECT);
		else
			association.notifyObservers(UpdateMessage.UNSELECT);
	}
}
