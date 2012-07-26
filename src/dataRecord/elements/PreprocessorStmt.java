package dataRecord.elements;

import dataRecord.Keyword;
import dataRecord.io.ElementVisitor;

public class PreprocessorStmt extends Statement
{
	private String p;

	public PreprocessorStmt(Keyword kw, String packageName, String p)
	{
		super(kw, packageName);
		this.setP(p);
	}

	@Override
	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	public String getP()
	{
		return p;
	}

	public void setP(String p)
	{
		this.p = p;
	}

}
