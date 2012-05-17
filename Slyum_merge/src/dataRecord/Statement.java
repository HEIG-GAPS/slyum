package dataRecord;

public abstract class Statement implements Element
{
	private Keyword kw;
	private String packageName;

	public Statement(Keyword kw, String packageName)
	{
		this.kw = kw;
		this.packageName = packageName;
	}

	public String toString()
	{
		return kw + " " + packageName + ";";
	}

	public String getPackageName()
	{
		return packageName;
	}

	public String getName()
	{
		return packageName;
	}
	
	public abstract void accept(ElementVisitor visitor);

}
