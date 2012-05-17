package dataRecord;

import java.util.LinkedList;

public abstract class Type extends Declaration implements Implementable
{
	private Keyword type;
	private boolean isStatic = false; // only nested types
	protected static int depth = 0;
	protected LinkedList<Implementable> implList = new LinkedList<Implementable>();

	public Type(String name, Keyword access, Keyword type)
	{
		super(name, access);
		this.type = type;
	}

	public abstract void accept(ElementVisitor v);

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

}
