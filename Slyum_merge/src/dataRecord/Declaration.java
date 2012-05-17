package dataRecord;

import java.util.LinkedList;
import java.util.List;

public abstract class Declaration implements Element
{
	private String name;
	private Keyword access;
	private List<Element> elements;

	public Declaration(String name, Keyword access)
	{
		this.name = name;
		elements = new LinkedList<Element>();
		if (Keyword.isAccess(access))
			this.access = access;
		else
			throw new IllegalArgumentException("access is not valid");
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

	public Element getElement(String name)
	{
		for (Element e : elements)
		{
			if (e.getName().equals(name))
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

	@Override
	public abstract void accept(ElementVisitor visitor);

}