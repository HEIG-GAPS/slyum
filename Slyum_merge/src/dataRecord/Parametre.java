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

	@Override
	public void accept(ElementVisitor visitor)
	{
		visitor.visit(this);
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
