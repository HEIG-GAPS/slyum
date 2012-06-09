package graphic.textbox;

import java.util.Observer;

/**
 * Interface uses by component containing a label. And notify while this label
 * changed.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public interface ILabelTitle
{
	/**
	 * Add an observer.
	 * 
	 * @param o
	 *            observer
	 */
	public void addObserver(Observer o);

	/**
	 * Get the label for this component.
	 * 
	 * @return the label
	 */
	public String getLabel();

	/**
	 * Notify all observers that label changed.
	 */
	public void notifyObservers();

	/**
	 * Set the label for this component.
	 * 
	 * @param text
	 *            the new label
	 */
	public void setLabel(String text);
}
