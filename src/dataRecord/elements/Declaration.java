package dataRecord.elements;

import java.util.LinkedList;
import java.util.List;

import classDiagram.ClassDiagram;
import dataRecord.Keyword;
import dataRecord.io.ElementVisitor;
/**
 * This class represent a declaration.
 * A declaration can be a new object (class/interface/enum) or 
 * an operation on this new object. 
 * A Declaration has a name, an access modifier and a list of 
 * elements.
 * The childs of Declaration are
 * 	+ Type
 * 		- ClassType
 * 		- Interfacetype
 * 		- EnumType
 *  + Member
 *  	- Constructor
 *  	- Method
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 5-lug-2012
 */
public abstract class Declaration implements Element
{
	private String name;
	private Keyword access;
	private List<Element> elements;
	private static int depth = 0;
	private int ID;

	public Declaration(String name, Keyword access)
	{
		this.name = name;
		elements = new LinkedList<Element>();
		if (Keyword.isAccess(access))
			this.access = access;
		else
			throw new IllegalArgumentException("access is not valid");
		
		ID = ClassDiagram.getElementID();
	}
	
	public Declaration(String name, Keyword access, int id)
	{
		this.name = name;
		elements = new LinkedList<Element>();
		if (Keyword.isAccess(access))
			this.access = access;
		else
			throw new IllegalArgumentException("access is not valid");
		
		this.ID = id;
	}

	public void addElement(Element e)
	{
		elements.add(e);
	}

	public void removeElement(Element e)
	{
		elements.remove(e);
	}

	public List<Element> getElements()
	{
		return elements;
	}

	public Element getElement(int id)
	{
		for (Element e : elements)
		{
			if (e.getID() == id)
				return e;
		}
		return null;
	}
	
	public void setElements(List<Element> liste)
	{
		this.elements = liste;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Keyword getAccess()
	{
		return access;
	}

	public void setAccess(Keyword access)
	{
		this.access = access;
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

	public static int getDepth()
	{
		return depth;
	}

	public static void setDepth(int depth)
	{
		Declaration.depth = depth;
	}

}