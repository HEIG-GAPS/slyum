package dataRecord.elements;

import java.util.LinkedList;
import java.util.List;

import dataRecord.Keyword;
import dataRecord.elementType.ClassKind;
import dataRecord.io.ElementVisitor;

/**
 * A Member represent an operation by an object (Method) or the 
 * creation of that object (Constructor).
 * 
 * It can have many parameters, many exception, and a body.
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 10-lug-2012
 */
public abstract class Member extends Declaration
{
	private List<Parameter> params;
	private List<ClassKind> throwClauses;
	protected String methodBody;

	public Member(String name, Keyword access)
	{
		super(name, access);
		params = new LinkedList<Parameter>();
		throwClauses = new LinkedList<ClassKind>();
	}

	public Member(String name, Keyword access, String methodBody)
	{
		super(name, access);
		params = new LinkedList<Parameter>();
		setThrowClauses(new LinkedList<ClassKind>());
		this.methodBody = methodBody;
	}
	
	public Member(String name, Keyword access, int ID)
	{
		super(name, access, ID);
		params = new LinkedList<Parameter>();
		throwClauses = new LinkedList<ClassKind>();
	}

	public Member(String name, Keyword access, String methodBody, int id)
	{
		super(name, access, id);
		params = new LinkedList<Parameter>();
		setThrowClauses(new LinkedList<ClassKind>());
		this.methodBody = methodBody;
	}

	public String ParamToString()
	{
		String tmp = "(";
		for (Parameter p : params)
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
		for (ClassKind tc : throwClauses)
		{
			if(tc.getClass() == ClassType.class)
				tmp += ((ClassType)tc).getName();
			if (!tc.equals(throwClauses.get(throwClauses.size() - 1)))
				tmp += ", ";
		}

		return tmp;
	}

	@Override
	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 */
	public abstract String accept(ElementVisitor visitor);

	public List<Parameter> getParams()
	{
		return params;
	}

	public void setParams(List<Parameter> params)
	{
		this.params = params;
	}

	public List<ClassKind> getThrowClauses()
	{
		return throwClauses;
	}

	public void setThrowClauses(List<ClassKind> throwClauses)
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

	public void addParam(Parameter p)
	{
		params.add(p);
	}

	public void addThrowClause(ClassKind t)
	{
		throwClauses.add(t);
	}
}
