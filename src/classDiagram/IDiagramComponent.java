package classDiagram;

import java.util.Observer;

/**
 * Interface implemented by all class diagram component.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 * 
 */
public interface IDiagramComponent
{
	public enum UpdateMessage
	{
		ADD_ATTRIBUTE, ADD_METHOD, MODIF, SELECT, UNSELECT
	};

	public void addObserver(Observer o);

	/**
	 * Get the id of the component.
	 * 
	 * @return the id of the component.
	 */
	public int getId();

	public void notifyObservers();

	// all IDiagramComponent must implement an Observer - Observable structure.
	public void notifyObservers(Object arg);

	/**
	 * Select the component. This method just setChanged the component, you must
	 * notify with the UpdateMessage.SELECT for appling change.
	 */
	public void select();

	/**
	 * Get the XML structure of the component (check docs for XSD declaraction).
	 * 
	 * @param depth
	 *            the number of tabs to put before each tag.
	 * @return the XML structure of the component.
	 */
	public String toXML(int depth);
}
