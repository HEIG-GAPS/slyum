package dataRecord;

public abstract class Field implements Element
{
	protected String name;
	private String value = "";
	private boolean isFinal = false;
	protected ElementType type;

	public Field(String name, ElementType type)
	{
		this.setName(name);
		this.type = type;
	}

	public Field(String name, ElementType type, String value)
	{
		this(name, type);
		this.value = value;
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
