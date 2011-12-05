package classDiagram.components;

import utility.Utility;

/**
 * Represent an attribute in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Attribute extends Variable
{
	private boolean _isConstant = false;
	/** /!\ not used in Slyum 1.0. */
	private boolean _isStatic = false;
	private String defaultValue;
	private Visibility visibility = Visibility.PRIVATE;

	/**
	 * Construct a new attribute.
	 * 
	 * @param name
	 *            the name of the attribute.
	 * @param type
	 *            the type of the attribute.
	 */
	public Attribute(String name, Type type)
	{
		super(name, type);

		setDefaultValue("");
	}

	/**
	 * Get the default value of the attribute.
	 * 
	 * @return the default value of the attribute.
	 */
	public String getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Get the visibility of the attribute.
	 * 
	 * @return the visibility of the attribute
	 */
	public Visibility getVisibility()
	{
		return visibility;
	}

	/**
	 * Get the constant state of the attribute.
	 * 
	 * @return true if attribute is constant; false otherwise
	 */
	public boolean isConstant()
	{
		return _isConstant;
	}

	/**
	 * Get the static state of the attribute.
	 * 
	 * @return true if attribute is static; false otherwise
	 */
	public boolean isStatic()
	{
		return _isStatic;
	}

	/**
	 * Set the constant state of the attribut.
	 * 
	 * @param isConst
	 *            the new constant state.
	 */
	public void setConstant(boolean isConst)
	{
		_isConstant = isConst;
		setChanged();
	}

	/**
	 * Set the default value of the attribute.
	 * 
	 * @param defaultValue
	 *            the new default value.
	 */
	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
		setChanged();
	}

	/**
	 * Set the static state of the attribute.
	 * 
	 * @param isStatic
	 *            the new static state
	 */
	public void setStatic(boolean isStatic)
	{
		_isStatic = isStatic;
		setChanged();
	}

	/**
	 * Change this attribute according to the text. If the syntax of the text is
	 * incorrect, this method will not make changes.
	 * 
	 * @param text
	 *            the text representing an UML Attribute
	 */
	public void setText(String text)
	{
		if (text.length() == 0)
			return;

		String newName;
		text = text.trim();
		Visibility newVisibility = Visibility.getVisibility(text.charAt(0));

		if (newVisibility == null)
			newVisibility = getVisibility();
		else
			text = text.substring(1); // Delete the first car (visibility).

		final String[] subString = text.split(":");

		newName = subString[0].trim();

		if (!newName.matches("([a-zA-Z|_])(\\w)*"))
			newName = getName();

		if (subString.length == 2)
		{
			subString[1] = subString[1].trim();
			if (!subString[1].matches("([a-zA-Z|_])[(\\w)<>.]*"))
				return;

			setType(new Type(subString[1]));
		}

		setName(newName);
		setVisibility(newVisibility);
		notifyObservers();
	}

	/**
	 * Set the visibility of the attribute.
	 * 
	 * @param visibility
	 *            the new visibility
	 */
	public void setVisibility(Visibility visibility)
	{
		if (visibility == null)
			throw new IllegalArgumentException("visibility is null");

		this.visibility = visibility;
		setChanged();
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);
		return tab + "<attribute " + "name=\"" + name + "\" type=\"" + type.toXML(depth+1) + "\" const=\"" + constant + "\" visibility=\"" + visibility + "\" " + (defaultValue == null ? "" : "defaultValue=\"" + defaultValue) + "\" isStatic=\"" + _isStatic + "\" " + "/>";
	}
}
