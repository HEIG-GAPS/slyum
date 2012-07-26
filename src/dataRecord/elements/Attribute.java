package dataRecord.elements;

import dataRecord.*;
import dataRecord.elementType.ElementType;
import dataRecord.io.ElementVisitor;
/**
 * Each datafield has a specific type. This type can be a "Primitive type" (int, double, char, etc)
 * a user defined type (an object created by the user) or an API type.
 * This class represent a data field. A data field is usually with the following format
 * {AccessModifier} {static} {final} type name {= value}
 * example: final String a = "un attribute"
 * 			private int entier
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see Variable
 * @see Element
 */
public class Attribute extends Variable
{
	private Keyword access;
	private boolean isStatic = false;

	/**
	 * creates an Attribute with a name, a access modifier and an elementType
	 * @param name
	 * @param access
	 * @param type
	 */
	public Attribute(String name, Keyword access, ElementType type)
	{
		super(name, type);
		if (Keyword.isAccess(access))
			this.setAccess(access);
		else
			throw new IllegalArgumentException(" access is not legal");
	}
	
	public Attribute(String name, Keyword access, ElementType type, int id)
	{
		super(name, type, id);
		if (Keyword.isAccess(access))
			this.setAccess(access);
		else
			throw new IllegalArgumentException(" access is not legal");
	}

	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 * 
	 */
	@Override
	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	public Keyword getAccess()
	{
		return access;
	}

	public void setAccess(Keyword access)
	{
		this.access = access;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public void setStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}

	public String toString()
	{
		String s = "";
		s += access.toString();
		if (access != Keyword.PACKAGE)
			s += " ";
		if (isStatic)
			s += "static ";
		return s + super.toString() + ";";
	}

}
