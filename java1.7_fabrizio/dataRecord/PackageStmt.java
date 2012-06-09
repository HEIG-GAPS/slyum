package dataRecord;

public class PackageStmt extends Statement
{
	private static Keyword kw = Keyword.PACKAGE;

	public PackageStmt(String packageName)
	{
		super(kw, packageName);
	}
	
	public PackageStmt(String packageName, int id)
	{
		super(kw, packageName, id);
	}

	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	public String toString()
	{
		return "package " + getPackageName() + ";";
	}
}
