package dataRecord;

import java.util.LinkedList;
import java.util.List;

public class ClassType extends Type implements Extendable
{
	private boolean isFinal = false;
	private boolean isAbstract = false;
	private List<Extendable> extendList = new LinkedList<Extendable>();

	public ClassType(String name, Keyword access)
	{
		super(name, access, Keyword.CLASS);
	}
	
	public ClassType(String name, Keyword access, int id)
	{
		super(name, access, Keyword.CLASS, id);
	}
	
	// constructeur de recopie
	public ClassType(ClassType c)
	{
		super(c.getName(), c.getAccess(), Keyword.CLASS);
		extendList = c.getExtendList();
		implList = c.getImplList();
		setStatic(c.isStatic());
		setAbstract(c.isAbstract);
		setFinal(c.isFinal);
		setElements(c.getElements());
		setID(c.getID());
	}

	public String toString()
	{
		String tmp = getAccess().toString();

		tmp += " ";
		if (isFinal)
			tmp += "final ";
		if (isAbstract())
			tmp += "abstract ";
		if (isStatic())
			tmp += "static ";
		tmp += "class " + getName() + " ";
		if (!extendList.isEmpty())
		{
			tmp += "extends ";
			for (Extendable ex : extendList)
			{
				if (ex.getClass() == APIclass.class)
					tmp += ((APIclass)ex).getElementType();
				else
					tmp += ((ClassType) ex).getName();
				if (!ex.equals(extendList.get(extendList.size() - 1)))
					tmp += ", ";
			}
			tmp += " ";
		}
		if (!implList.isEmpty())
		{
			tmp += "implements ";
			for (Implementable ex : implList)
			{
				if (ex.getClass() == APIinterface.class)
					tmp += ((APIinterface)ex).getElementType();
				else
					tmp += ((InterfaceType) ex).getName();
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
	public String accept(ElementVisitor v)
	{
		return v.visit(this);
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public void setFinal(boolean isFinal)
	{
		this.isFinal = isFinal;
	}

	public List<Extendable> getExtendList()
	{
		return extendList;
	}

	public void setExtendList(List<Extendable> extendList)
	{
		this.extendList = extendList;
	}
	
	public boolean isAbstract()
	{
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract)
	{
		this.isAbstract = isAbstract;
	}

	@Override
	public String getElementType()
	{
		return getName();
	}

}
