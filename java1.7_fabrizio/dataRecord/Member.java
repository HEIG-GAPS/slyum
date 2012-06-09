package dataRecord;

import java.util.LinkedList;
import java.util.List;

public abstract class Member extends Declaration
{
	private List<Parametre> params;
	private List<String> throwClauses;
	protected String methodBody;

	public Member(String name, Keyword access)
	{
		super(name, access);
		params = new LinkedList<Parametre>();
		throwClauses = new LinkedList<String>();
	}

	public Member(String name, Keyword access, String methodBody)
	{
		super(name, access);
		params = new LinkedList<Parametre>();
		setThrowClauses(new LinkedList<String>());
		this.methodBody = methodBody;
	}
	
	public Member(String name, Keyword access, int ID)
	{
		super(name, access, ID);
		params = new LinkedList<Parametre>();
		throwClauses = new LinkedList<String>();
	}

	public Member(String name, Keyword access, String methodBody, int id)
	{
		super(name, access, id);
		params = new LinkedList<Parametre>();
		setThrowClauses(new LinkedList<String>());
		this.methodBody = methodBody;
	}

	public String ParamToString()
	{
		String tmp = "(";
		for (Parametre p : params)
		{
			tmp += p.toString();
			if (!p.equals(params.get(params.size() - 1)))
				tmp += ", ";
		}

		return tmp + ")";
	}

	public String throwClausesToString()
	{
		if (throwClauses.isEmpty())
			return "";
		String tmp = " throws ";
		for (String tc : throwClauses)
		{
			tmp += tc.toString();
			if (!tc.equals(throwClauses.get(throwClauses.size() - 1)))
				tmp += ", ";
		}

		return tmp;
	}

	@Override
	public abstract String accept(ElementVisitor visitor);

	public List<Parametre> getParams()
	{
		return params;
	}

	public void setParams(List<Parametre> params)
	{
		this.params = params;
	}

	public List<String> getThrowClauses()
	{
		return throwClauses;
	}

	public void setThrowClauses(List<String> throwClauses)
	{
		this.throwClauses = throwClauses;
	}

	public String getMethodBody()
	{
		return methodBody;
	}

	public void setMethodBody(String methodBody)
	{
		this.methodBody = methodBody;
	}

	public void addParam(Parametre p)
	{
		params.add(p);
	}

	public void addThrowClause(String t)
	{
		throwClauses.add(t);
	}
}
