package dataRecord;

import classDiagram.ClassDiagram;

public abstract class Field implements Element
{
	protected String name;
	private String value = "";
	private boolean isFinal = false;
	protected ElementType type;
	private int ID;

	public Field(String name, ElementType type)
	{
		this.setName(name);
		this.type = type;
		ID = ClassDiagram.getElementID();
	}

	public Field(String name, ElementType type, String value)
	{
		this(name, type);
		this.value = value;
		ID = ClassDiagram.getElementID();
	}
	
	public Field(String name, ElementType type, int id)
	{
		this.setName(name);
		this.type = type;
		this.ID = id;
	}

	public Field(String name, ElementType type, String value, int id)
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
	public abstract void accept(ElementVisitor visitor);

	public ElementType getType()
	{
		return type;
	}

	public void setType(ElementType type)
	{
		this.type = type;
	}

}
