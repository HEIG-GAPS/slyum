package graphic.relations;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.AssociationClassView;
import graphic.textbox.TextBoxRole;

import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

import utility.Utility;
import change.BufferCreation;
import change.Change;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;

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
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class RelationView extends LineView implements Observer
{
	private final IDiagramComponent component;

	public RelationView(GraphicView graphicView, GraphicComponent source, GraphicComponent target, IDiagramComponent component, Point posSource, Point posTarget, boolean checkRecursivity)
	{
		super(graphicView, source, target, posSource, posTarget, checkRecursivity);

		if (component == null)
			throw new IllegalArgumentException("component is null");

		this.component = component;
		component.addObserver(this);

		Change.push(new BufferCreation(false, this));
		Change.push(new BufferCreation(true, this));
	}

	@Override
	public boolean relationChanged(GraphicComponent oldCompo, GraphicComponent newCompo)
	{
		if (newCompo.getClass() == AssociationClassView.class)
			return false;

		return true;
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		String xml = tab + "<relationView relationId=\"" + component.getId() + "\" color=\"" + getColor().getRGB() + "\">\n";

		xml += tab + "\t<line>\n";

		for (final RelationGrip grip : points)
		{
			final Point anchor = grip.getAnchor();
			xml += tab + "\t\t<point>\n" + tab + "\t\t\t<x>" + anchor.x + "</x>\n" + tab + "\t\t\t<y>" + anchor.y + "</y>\n" + tab + "\t\t</point>\n";
		}

		xml += tab + "\t</line>\n";

		if (tbRoles.size() >= 1)
		{
			xml += utility.Utility.boundsToXML(depth, tbRoles.get(0).getBounds(), "labelAssociation");

			if (tbRoles.size() >= 3)
			{
				xml += utility.Utility.boundsToXML(depth, tbRoles.get(1).getBounds(), "roleAssociation");
				xml += utility.Utility.boundsToXML(depth, tbRoles.get(2).getBounds(), "roleAssociation");
				xml += utility.Utility.boundsToXML(depth, ((TextBoxRole) tbRoles.get(1)).getTextBoxMultiplicity().getBounds(), "multipliciteAssociation");
				xml += utility.Utility.boundsToXML(depth, ((TextBoxRole) tbRoles.get(2)).getTextBoxMultiplicity().getBounds(), "multipliciteAssociation");
			}
		}

		return xml + tab + "</relationView>\n";
	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		if (arg1 != null && arg1.getClass() == UpdateMessage.class)
			switch ((UpdateMessage) arg1)
			{
				case SELECT:
					setSelected(true);
					break;

				case UNSELECT:
					setSelected(false);
					break;
			}
		else

			repaint();
	}
}
