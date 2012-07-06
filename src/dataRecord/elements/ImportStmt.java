package dataRecord.elements;

import dataRecord.Keyword;
import dataRecord.io.ElementVisitor;

public class ImportStmt extends Statement
{
	private static Keyword kw = Keyword.IMPORT;
	private boolean isStatic = false;

	public ImportStmt(String importName)
	{
		super(kw, importName);
	}

	public ImportStmt(String importName, boolean isStatic)
	{
		this(importName);
		this.isStatic = isStatic;
	}
	
	public ImportStmt(String importName, int id)
	{
		super(kw, importName, id);
	}

	public ImportStmt(String importName, boolean isStatic, int id)
	{
		super(kw, importName, id);
		this.isStatic = isStatic;
	}

	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	public String toString()
	{
		if (isStatic)
			return "import static " + getPackageName() + ";";
		else
			return super.toString();
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public void setStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}
}
