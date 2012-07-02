/**
 * This class represent a parameter. A parameter is a value that is passed to 
 * a method so that the method can use that value in its processing. 
 * A parameter has a name, a type and in some languages even a value.
 * It can be set has a constant value (final).
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see Field
 * @see Element
 */ 
package dataRecord;

public class Parametre extends Field
{
	private boolean isEllipse = false;

	public Parametre(String name, ElementType type)
	{
		super(name, type);
	}

	public Parametre(String name, ElementType type, String value)
	{
		super(name, type, value);
	}
	
	public Parametre(String name, ElementType type, int id)
	{
		super(name, type, id);
	}

	public Parametre(String name, ElementType type, String value, int ID)
	{
		super(name, type, value, ID);
	}

	@Override
	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	public void setEllipse(boolean isEllipse)
	{
		this.isEllipse = isEllipse;
	}

	public boolean getEllipse()
	{
		return isEllipse;
	}
	
	public String toString()
	{
		String tmp = "";

		if (isFinal())
			tmp += "final";
		
		tmp += type.getElementType();
		tmp += " ";
		tmp += name;
		if (isEllipse)
			tmp += "...";
		
		if (!getValue().isEmpty())
			tmp += " =" + getValue();

		return tmp;
	}

}
