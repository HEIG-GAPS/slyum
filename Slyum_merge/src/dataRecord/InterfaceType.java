package dataRecord;

public class InterfaceType extends Type implements Implementable
{
	public InterfaceType(String name, Keyword access)
	{
		super(name, access, Keyword.INTERFACE);
	}
	
	public InterfaceType(String name, Keyword access, int id)
	{
		super(name, access, Keyword.INTERFACE, id);
	}
	
	// constructeur de recopie
	public InterfaceType(InterfaceType i)
	{
		super(i.getName(), i.getAccess(), Keyword.INTERFACE);
		implList = i.getImplList();
		setStatic(i.isStatic());
		setElements(i.getElements());
		setID(i.getID());
	}

	@Override
	public void accept(ElementVisitor v)
	{
		v.visit(this);
	}

	public String toString()
	{
		String tmp = getAccess().toString();

		tmp += " ";
		tmp += "interface " + getName();
		if (!implList.isEmpty())
		{
			tmp += " extends ";
			for (Implementable ex : implList)
			{
				if (ex.getClass() == APIinterface.class)
					tmp += ((APIinterface)ex).getElementType();
				else
					tmp += ex.getClass().getSimpleName();
				if (!ex.equals(implList.get(implList.size() - 1)))
					tmp += ", ";
			}
		}
		tmp += "\n";
		for (int i = 0; i < depth; i++)
		{
			tmp += "\t";
		}
		tmp += "{";
		depth++;
		for (Element e : getElements())
		{
			tmp += "\n";
			for (int i = 0; i < depth; i++)
			{
				tmp += "\t";
			}
			tmp += e.toString();
		}
		depth--;
		tmp += "\n";
		for (int i = 0; i < depth; i++)
		{
			tmp += "\t";
		}

		tmp += "}";

		return tmp;
	}

	@Override
	public String getElementType()
	{
		return getName();
	}
}
