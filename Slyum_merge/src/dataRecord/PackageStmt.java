package dataRecord;

public class PackageStmt extends Statement
{
	private static Keyword kw = Keyword.PACKAGE;

	public PackageStmt(String packageName)
	{
		super(kw, packageName);
	}

	public void accept(ElementVisitor visitor)
	{
		visitor.visit(this);
	}

	public String toString()
	{
		return "package " + getPackageName() + ";";
	}
}
