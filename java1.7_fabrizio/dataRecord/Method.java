package dataRecord;

public class Method extends Member
{
	private String returnType;
	private boolean isAbstract = false;
	private boolean isFinal = false;
	private boolean isStatic = false;
	private boolean isSynchronized = false;

	public Method(String name, Keyword access, String returnType)
	{
		super(name, access);
		this.returnType = returnType;
	}

	public Method(String name, Keyword access, String returnType, String methodBody)
	{
		super(name, access);
		this.returnType = returnType;
		setMethodBody(methodBody);
	}
	
	public Method(String name, Keyword access, String returnType, int id)
	{
		super(name, access, id);
		this.returnType = returnType;
	}

	public Method(String name, Keyword access, String returnType, String methodBody, int id)
	{
		super(name, access, id);
		this.returnType = returnType;
		setMethodBody(methodBody);
	}
	
	public Method(Method m)
	{
		super(m.getName(), m.getAccess());
		this.returnType = m.returnType;
		setMethodBody(m.getMethodBody());
		setFinal(m.isFinal);
		setStatic(m.isStatic);
		setSynchronized(isSynchronized);
		setAbstract(isAbstract);
		setThrowClauses(m.getThrowClauses());
		setParams(m.getParams());
		setID(m.getID());
	}

	@Override
	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public String toString()
	{
		String s = "";

		s += getAccess().toString();
		s += " ";
		if (isFinal)
			s += "final ";
		if (isSynchronized)
			s += "synchronized ";
		if (isStatic)
			s += "static ";
		if (isAbstract)
			s += "abstract ";
		s += returnType;
		s += " ";
		s += getName();
		s += ParamToString();
		s += throwClausesToString();


		if (isAbstract)
			s+= methodBody;
		else
		{	
			String tabs = "";
			for (int i = 0; i < depth; i++)
			{
				tabs += "\t";
			}
			s += "\n"+ tabs + methodBody;
		}

		return s;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}

	public boolean isAbstract()
	{
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract)
	{
		this.isAbstract = isAbstract;
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public void setFinal(boolean isFinal)
	{
		this.isFinal = isFinal;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public void setStatic(boolean isStaic)
	{
		this.isStatic = isStaic;
	}

	public boolean isSynchronized()
	{
		return isSynchronized;
	}

	public void setSynchronized(boolean isSynchronized)
	{
		this.isSynchronized = isSynchronized;
	}

}
