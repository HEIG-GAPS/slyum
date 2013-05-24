package graphic.relations;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.AssociationClassView;
import graphic.textbox.TextBoxRole;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;

import utility.Utility;
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
  public final static String ACTION_CHANGE_ORIENTATION = "change-orientation";
	private final IDiagramComponent component;

	public RelationView(GraphicView graphicView, GraphicComponent source, GraphicComponent target, IDiagramComponent component, Point posSource, Point posTarget, boolean checkRecursivity)
	{
		super(graphicView, source, target, posSource, posTarget, checkRecursivity);

		if (component == null)
			throw new IllegalArgumentException("component is null");


    popupMenu.addSeparator();

    JMenuItem menuItem = makeMenuItem("Change orientation", ACTION_CHANGE_ORIENTATION, "");
    popupMenu.add(menuItem);
    
		this.component = component;
		component.addObserver(this);
	}

	@Override
	public boolean relationChanged(GraphicComponent oldCompo, GraphicComponent newCompo)
	{
		if (newCompo.getClass() == AssociationClassView.class)
			return false;

		return true;
	}
	
	/**
	 * Replace the old component by the new if it's possible.
	 * @param current the magnetic grid having the component to replace.
	 * @param replace the new component for the magnetic grid.
	 * @return true if it's ok, false otherwise.
	 */
	public boolean changeComponent(MagneticGrip grip, GraphicComponent component) {
	  
	  // Vérifiie si le nouveau composant est compatible avec la relation.
    GraphicComponent c1 = grip.getAssociedComponentView();
	  if (!relationChanged(c1, component)) {
	    System.err.println("Relation change impossible.");
	    return false;
	  }
	  
	  // Changement du composant.
	  grip.setAssociedComponentView(component);
	  return true;
	}
	
	/**
	 * Change the orientation of the association.
	 * @return true if it's ok, false otherwise.
	 */
	public void changeOrientation() {
	  
	  // Inversion des composants.
	  changeComponent(getFirstPoint(), getLastPoint().getAssociedComponentView());
	  changeComponent(getLastPoint(), getFirstPoint().getAssociedComponentView());
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
	public void actionPerformed(ActionEvent e) {
    if (ACTION_CHANGE_ORIENTATION.equals(e.getActionCommand()))
      changeOrientation();
    else
      super.actionPerformed(e);	  
	}

	@Override
	public void update(Observable observable, Object o)
	{
		if (o != null && o.getClass() == UpdateMessage.class)
			switch ((UpdateMessage)o)
			{
				case SELECT:
					setSelected(true);
					break;

				case UNSELECT:
					setSelected(false);
					break;
        default:
          break;
			}
		else
			repaint();
	}
}
