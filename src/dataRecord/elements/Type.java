package dataRecord.elements;

import java.util.LinkedList;

import dataRecord.Keyword;
import dataRecord.elementType.ElementType;
import dataRecord.elementType.Implementable;
import dataRecord.io.ElementVisitor;

public class Type extends Declaration implements Implementable, ElementType
{
	private Keyword type;
	private boolean isStatic = false; // only nested types
	private String generic = "";
	//protected static int depth = 0;
	protected LinkedList<Implementable> implList = new LinkedList<Implementable>();

	public Type(String name, Keyword access, Keyword type)
	{
		super(name, access);
		this.type = type;
	}
	
	public Type(String name, Keyword access, Keyword type, int id)
	{
		super(name, access, id);
		this.type = type;
	}

	public String accept(ElementVisitor v)
	{
		return v.visit(this);
	}

	public Keyword getType()
	{
		return type;
	}

	public void setType(Keyword type)
	{
		this.type = type;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public void setStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}

	public void addImplements(InterfaceType i)
	{
		implList.add(i);
	}

	public LinkedList<Implementable> getImplList()
	{
		return implList;
	}

	public void setImplList(LinkedList<Implementable> implList)
	{
		this.implList = implList;
	}

	@Override
	public String getElementType()
	{
		return getName();
	}
	
	public Element getElementByID(int id)
	{
		for (Element e : getElements())
		{
			if(e.getID() == id)
				return e;
		}
		return null;
	}
	
	public Element getElementByName(String name)
	{
		for (Element e : getElements())
		{
			if(e.getName().equals(name))
				return e;
		}
		return null;
	}
	
	/**
	 * same as getElementByName but only for Member
	 * 
	 * @param name the name to find
	 * @return the meber
	 */
	public Member getMemberByName(String name)
	{
		for (Element e : getElements())
		{
			if(e instanceof Member && e.getName().equals(name))
				return (Member)e;
		}
		return null;
	}
	
	/**
	 * same as getElementByName but only for Member
	 * 
	 * @param name the name to find
	 * @return the meber
	 */
	public Field getFieldByName(String name)
	{
		for (Element e : getElements())
		{
			if(e instanceof Field && e.getName().equals(name))
				return (Field)e;
		}
		return null;
	}

	public String getGeneric()
	{
		return generic;
	}

	public void setGeneric(String generic)
	{
		this.generic = generic;
	}

}
