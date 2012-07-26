package dataRecord.elements;

import java.util.LinkedList;

import dataRecord.Keyword;
import dataRecord.elementType.APIinterface;
import dataRecord.elementType.InterfaceKind;
import dataRecord.io.ElementVisitor;

public class EnumType extends Type
{
	private LinkedList<EnumField> enums = new LinkedList<EnumField>();

	public EnumType(String name, Keyword access)
	{
		super(name, access, Keyword.ENUM);
	}
	
	public EnumType(String name, Keyword access, int id)
	{
		super(name, access, Keyword.ENUM, id);
	}
	
	// constructeur de recopie
	public EnumType(EnumType e)
	{
		super(e.getName(), e.getAccess(), Keyword.ENUM);
		implList = e.getImplList();
		setStatic(e.isStatic());
		setElements(e.getElements());
		setID(e.getID());
	}

	@Override
	public String accept(ElementVisitor v)
	{
		return v.visit(this);
	}

	public String toString()
	{
		String tmp = getAccess().toString();

		tmp += " ";
		tmp += "enum " + getName();
		if (!implList.isEmpty())
		{
			tmp += " implements ";
			for (InterfaceKind ex : implList)
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
		for (int i = 0; i < getDepth(); i++)
		{
			tmp += "\t";
		}
		tmp += "{";
		setDepth(getDepth() + 1);
		for (Element e : getElements())
		{
			tmp += "\n";
			for (int i = 0; i < getDepth(); i++)
			{
				tmp += "\t";
			}
			tmp += e.toString();
		}
		setDepth(getDepth() - 1);
		tmp += "\n";
		for (int i = 0; i < getDepth(); i++)
		{
			tmp += "\t";
		}

		tmp += "}";

		return tmp;
	}

	public LinkedList<EnumField> getEnums()
	{
		return enums;
	}

	public void setEnums(LinkedList<EnumField> enums)
	{
		this.enums = enums;
	}

	public void addEnum(EnumField e)
	{
		enums.add(e);
	}

	@Override
	public String getElementType()
	{
		return getName();
	}
}
