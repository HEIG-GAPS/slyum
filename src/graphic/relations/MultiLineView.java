package graphic.relations;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.textbox.TextBoxRole;

import java.awt.Point;

import utility.Utility;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Role;

/**
 * The LineView class represent a collection of lines making a link between two
 * GraphicComponent. When it creates, the LineView have one single line between
 * the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a
 * segment between each grips. Grips are movable and a LineView have two special
 * grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * 
 * A MultiLineView is a LineView associated with a Multi-association view.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class MultiLineView extends LineView
{
	/**
	 * Create a new MultiLineView associated with a MultiView.
	 * 
	 * @param graphicView
	 *            the graphic view
	 * @param source
	 *            the multi view
	 * @param target
	 *            A entity participating at the multi-association
	 * @param role
	 *            the role of the association
	 * @param posSource
	 *            the position for put the first MagneticGrip
	 * @param posTarget
	 *            the position for put the last MagneticGrip
	 * @param checkRecursivity
	 *            check if the relation is on itself
	 */
	public MultiLineView(GraphicView graphicView, MultiView source, EntityView target, Role role, Point posSource, Point posTarget, boolean checkRecursivity)
	{
		super(graphicView, source, target, posSource, posTarget, checkRecursivity);

		final TextBoxRole tb = new TextBoxRole(parent, role, getLastPoint());
		tbRoles.add(tb);
		parent.addOthersComponents(tb);
	}

	@Override
	public void delete()
	{
		MultiView mv = (MultiView)getFirstPoint().getAssociedComponentView();
		final int nbLineAssocied = parent.getLinesViewAssociedWith(mv).size();

		if (nbLineAssocied == 3)
		
			mv.delete();

		super.delete();

		mv.connexionRemoved(this);

	}
	
	@Override
	public void restore()
	{
		super.restore();
		
		MultiView mv = (MultiView) getFirstPoint().getAssociedComponentView();
		Multi m = (Multi) mv.getAssociedComponent();
		TextBoxRole tbr = (TextBoxRole)tbRoles.getFirst();
		m.addRole(tbr.getRole());
		mv.addMultiLineView(this);
		
		mv.restore();
	}

	public String getXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		String xml = tab + "<multiLineView relationId=\"" + getFirstPoint().getAssociedComponentView().getAssociedComponent().getId() + "\" color=\"" + getColor().getRGB() + "\">\n";

		xml += tab + "\t<line>\n";

		for (final RelationGrip grip : points)
		{
			final Point anchor = grip.getAnchor();
			xml += tab + "\t\t<point>\n" + tab + "\t\t\t<x>" + anchor.x + "</x>\n" + tab + "\t\t\t<y>" + anchor.y + "</y>\n" + tab + "\t\t</point>\n";
		}

		xml += tab + "\t</line>\n";

		if (tbRoles.size() >= 1)
		{
			xml += utility.Utility.boundsToXML(depth, tbRoles.get(0).getBounds(), "roleAssociation");
			xml += utility.Utility.boundsToXML(depth, ((TextBoxRole) tbRoles.get(0)).getTextBoxMultiplicity().getBounds(), "multipliciteAssociation");
		}

		return xml + tab + "</multiLineView>\n";
	}

	@Override
	public boolean relationChanged(GraphicComponent oldCompo, GraphicComponent newCompo)
	{
		/*
		 * // !!!!! JUST GRAPHIC CHANGE, HAVE TO REPERCUT ON CLASS DIAGRAMM
		 * MODEL
		 * // if relation change from class side
		 * if (oldCompo.equals(getLastPoint().getAssociedComponentView()))
		 * 
		 * return newCompo.getClass() == ClassView.class;
		 */
		// multi side can't be changed.
		return false;
	}

}
