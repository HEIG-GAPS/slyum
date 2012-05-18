package dataRecord;

public class Constructor extends Member
{
	public Constructor(String name, Keyword access)
	{
		super(name, access);
	}
	
	public Constructor(String name, Keyword access, int id)
	{
		super(name, access, id);
	}
	
	public Constructor(Constructor m)
	{
		super(m.getName(), m.getAccess());
		setMethodBody(m.getMethodBody());
		setThrowClauses(m.getThrowClauses());
		setParams(m.getParams());
		setID(m.getID());
	}

	@Override
	public void accept(ElementVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	public String toString()
	{
		String s = "";

		s += getAccess().toString();
		s += " ";
		s += getName();
		s += ParamToString();
		s += throwClausesToString();

		return s;
	}

}
