package classDiagram.components;

import classDiagram.verifyName.TypeName;
import change.BufferAttribute;
import change.Change;
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

		boolean isBlocked = Change.isBlocked();
		Change.setBlocked(true);
		
		setDefaultValue("");
		
		Change.setBlocked(isBlocked);
	}
	
	/**
	 * Constructor of copy.
	 * @param attribute attribute
	 */
	public Attribute(Attribute attribute)
	{
		super(attribute.getName(), new Type(attribute.getType().getName()));
		
		boolean isBlocked = Change.isBlocked();
		Change.setBlocked(true);
		
		name = attribute.name;
		type = new Type(attribute.getType().getName());
		defaultValue = attribute.defaultValue;
		visibility = attribute.visibility;
		_isStatic = attribute._isStatic;
		_isConstant = attribute._isConstant;
		
		Change.setBlocked(isBlocked);
	}
	
	public void setAttribute(Attribute attribute)
	{
		boolean isRecord = Change.isRecord();
		Change.record();
		
		setName(attribute.getName());
		setType(new Type(attribute.getType().getName()));
		setDefaultValue(attribute.getDefaultValue());
		setVisibility(attribute.getVisibility());
		setStatic(attribute.isStatic());
		setConstant(attribute.isConstant());
		
		if(!isRecord)
			Change.stopRecord();
		
		notifyObservers();
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
		Change.push(new BufferAttribute(this));
		_isConstant = isConst;
		Change.push(new BufferAttribute(this));
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
		Change.push(new BufferAttribute(this));
		this.defaultValue = defaultValue;
		Change.push(new BufferAttribute(this));
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
		Change.push(new BufferAttribute(this));
		_isStatic = isStatic;
		Change.push(new BufferAttribute(this));
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
		Type type = getType();
		text = text.trim();
		Visibility newVisibility = Visibility.getVisibility(text.charAt(0));

		if (newVisibility == null)
			newVisibility = getVisibility();
		else
			text = text.substring(1); // Delete the first car (visibility).

		final String[] subString = text.split(":");

		newName = subString[0].trim();

		if (subString.length == 2)
		{
			subString[1] = subString[1].trim();
			
			if (!TypeName.getInstance().verifyName(subString[1]))
				return;
			
			type = new Type(subString[1]);
		}
		
		boolean isRecord = Change.isRecord();
		Change.record();
		
		setType(type);
		setName(newName);
		setVisibility(newVisibility);
		
		if(!isRecord)
			Change.stopRecord();
		
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
		
		if (visibility.getName().equals(getVisibility().getName()))
			return;

		Change.push(new BufferAttribute(this));
		this.visibility = visibility;
		Change.push(new BufferAttribute(this));
		setChanged();
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);
		return tab + "<attribute " + "name=\"" + name + "\" type=\"" + type.toXML(depth+1) + "\" const=\"" + constant + "\" visibility=\"" + visibility + "\" " + (defaultValue == null ? "" : "defaultValue=\"" + defaultValue) + "\" isStatic=\"" + _isStatic + "\" " + "/>";
	}
}
