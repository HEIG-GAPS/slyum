package dataRecord.elements;

import dataRecord.Keyword;
import dataRecord.elementType.APIinterface;
import dataRecord.elementType.InterfaceKind;
import dataRecord.io.ElementVisitor;

/**
 * This class represent an interface in an object-oriented language
 * 
 * visit http://docs.oracle.com/javase/tutorial/java/concepts/interface.html
 * for a good explaination of "Interface"
 * 
 * An interface can implements from an other interface
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see Type
 * @see InterfaceKind
 */
public class InterfaceType extends Type implements InterfaceKind
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
	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 */ 
	 public String accept(ElementVisitor v)
	{
		return v.visit(this);
	}

	public String toString()
	{
		String tmp = getAccess().toString();

		tmp += " ";
		tmp += "interface " + getName();
		if (!implList.isEmpty())
		{
			tmp += " extends ";
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

	@Override
	public String getElementType()
	{
		return getName();
	}
}
