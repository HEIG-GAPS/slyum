package graphic.relations;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.textbox.TextBoxRole;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;

import com.google.common.collect.Lists;

import utility.Utility;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Entity;
import classDiagram.relationships.Relation;
import classDiagram.relationships.RelationChanger;

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
	private Relation relation;

  public RelationView(GraphicView graphicView, 
	                    GraphicComponent source,
	                    GraphicComponent target,
	                    Relation component,
	                    Point posSource,
	                    Point posTarget,
	                    boolean checkRecursivity)
	{
		super(graphicView, source, target, posSource, posTarget, checkRecursivity);

		if (component == null)
			throw new IllegalArgumentException("component is null");

    popupMenu.addSeparator();

    JMenuItem menuItem = makeMenuItem("Change orientation", ACTION_CHANGE_ORIENTATION, "orientation");
    popupMenu.add(menuItem);
    
		relation = component;
		component.addObserver(this);
	}

	@Override
	public boolean relationChanged(
	    MagneticGrip gripSource, GraphicComponent target) {
	  
		if (!(target instanceof EntityView))
		  return false;
    
		RelationChanger.changeRelation(relation, 
		    gripSource.equals(getFirstPoint()),
		    (Entity)target.getAssociedComponent());
		
	  changeLinkedComponent(gripSource, target);
		return true;
	}
	
	/**
	 * Change the orientation of the association.
	 * @return true if it's ok, false otherwise.
	 */
	public void changeOrientation() {
	  GraphicComponent buffer;
	  Rectangle bufferBoundsFirst, bufferBoundsLast;
	  LinkedList<RelationGrip> bufferPoints;
	  List<RelationGrip> reversePoints;

	  // Inversion des composants.
	  buffer = getFirstPoint().getAssociedComponentView();
    bufferBoundsFirst = getFirstPoint().getBounds();
    bufferBoundsLast = getLastPoint().getBounds();
    bufferPoints = getPoints();
    
    // Il ne faut pas ré-ajouter par la suite les grips magnétisés. 
    bufferPoints.removeFirst();
    bufferPoints.removeLast();
    
    // On inverse la liste des points pour pas qu'ils ne se croisent.
    reversePoints = Lists.reverse(bufferPoints);

    // On cache la relation pour éviter qu'elle ne se redissne alors que
    // l'inversion n'est pas terminée.
    setVisible(false);
	  relationChanged(getFirstPoint(), getLastPoint().getAssociedComponentView());
	  relationChanged(getLastPoint(), buffer);
    setVisible(true);
	  
    getFirstPoint().setBounds(bufferBoundsLast);
	  getLastPoint().setBounds(bufferBoundsFirst);
	  addAllGrip(reversePoints, 1);
	}
	
	/**
	 * Return an array with all gripd bounds.
	 * @return an array with all gripd bounds.
	 */
	public Rectangle[] getPointsBounds() {
    LinkedList<RelationGrip> grips = getPoints();
	  Rectangle[] bufferBoundsPoints = new Rectangle[grips.size()];
	  int i = 0;
    for (RelationGrip grip : grips) {
      bufferBoundsPoints[i] = new Rectangle(grip.getBounds());
      i++;
    }
    return bufferBoundsPoints;
	}
	
	/**
	 * Set all points bounds with the given array. First points will be set with
	 * the first index in array, second with the second, etc...
	 * @param pointsBounds an array of bounds, size must be the same than the
	 *                     number of points.
	 */
	public void setAllPointsBounds(Rectangle[] pointsBounds) {
	  LinkedList<RelationGrip> grips = getPoints();
	  if (pointsBounds.length != grips.size())
	    throw new IllegalArgumentException("Array of bounds not the same size " +
	    		                               "than number of points in relation.");
	  
	  int i = 0;
	  for (RelationGrip grip : grips) {
	    grip.setBounds(pointsBounds[i]);
	    i++;
	  }
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		String xml = tab + "<relationView relationId=\"" + relation.getId() + "\" color=\"" + getColor().getRGB() + "\">\n";

		xml += tab + "\t<line>\n";

		for (final RelationGrip grip : points)
		{
			final Point anchor = grip.getAnchor();
			xml += tab + "\t\t<point>\n" + tab + "\t\t\t<x>" + (anchor.x + 1) + "</x>\n" + tab + "\t\t\t<y>" + (anchor.y + 1) + "</y>\n" + tab + "\t\t</point>\n";
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
	
  public Relation getRelation() {
    return relation;
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
