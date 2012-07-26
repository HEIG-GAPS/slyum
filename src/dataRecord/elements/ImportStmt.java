package dataRecord.elements;

import dataRecord.Keyword;
import dataRecord.io.ElementVisitor;

/**
 * This class represent which library your program you must have.
 * Usually they are represented with an import statement.
 * Ex : import dataRecord.Keyword;
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 5-lug-2012
 */
public class ImportStmt extends Statement
{
	private static Keyword kw = Keyword.IMPORT;
	private boolean isStatic = false;

	/**
	 * creates a new import statement with the package name
	 * 
	 * @param importName
	 */
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

	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 * 
	 */
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
