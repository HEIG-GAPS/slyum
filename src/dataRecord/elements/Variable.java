/**
 * Each datafield has a specific type. This type can be a "Primitive type" (int, double, char, etc)
 * a user defined type (an object created by the user) or an API type.
 * This class represent a model of a Variable. A Variable may be a class's variable, 
 * an object's variable, an object's method's variable, or a parameter of a method.
 * This class has two childs : Attribute and Parametre
 *
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see Element
 * @see Attribute
 * @see Parameter
 */
package dataRecord.elements;

import classDiagram.ClassDiagram;
import dataRecord.elementType.ElementType;
import dataRecord.io.ElementVisitor;

public abstract class Variable implements Element
{
	protected String name;
	private String value = "";
	private boolean isFinal = false;
	protected ElementType type;
	private int ID;

	/**
	 * Creates a Variable with the name and a Element type
	 * 
	 * @param name
	 * @param type
	 */
	public Variable(String name, ElementType type)
	{
		this.setName(name);
		this.type = type;
		ID = ClassDiagram.getElementID();
	}

	public Variable(String name, ElementType type, String value)
	{
		this(name, type);
		this.value = value;
		ID = ClassDiagram.getElementID();
	}
	
	public Variable(String name, ElementType type, int id)
	{
		this.setName(name);
		this.type = type;
		this.ID = id;
	}

	public Variable(String name, ElementType type, String value, int id)
	{
		this(name, type);
		this.value = value;
		this.ID = id;
	}

	public String toString()
	{
		String tmp = "";

		if (isFinal)
			tmp += "final ";
		tmp += type.getElementType();
		tmp += " ";
		tmp += name;

		if (!value.isEmpty())
			tmp += " =" + value;

		return tmp;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public void setFinal(boolean isFinal)
	{
		this.isFinal = isFinal;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public void setID(int id)
	{
		this.ID = id;
	}

	@Override
	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 * 
	 */
	public abstract String accept(ElementVisitor visitor);

	public ElementType getType()
	{
		return type;
	}

	public void setType(ElementType type)
	{
		this.type = type;
	}

}
