package dataRecord;

import classDiagram.ClassDiagram;

public abstract class Statement implements Element
{
	private Keyword kw;
	private String packageName;
	private int ID;

	public Statement(Keyword kw, String packageName)
	{
		this.kw = kw;
		this.packageName = packageName;
		this.ID = ClassDiagram.getElementID();
	}
	
	public Statement(Keyword kw, String packageName, int ID)
	{
		this.kw = kw;
		this.packageName = packageName;
		this.ID = ID;
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
	
	public int getID()
	{
		return ID;
	}
	
	public abstract String accept(ElementVisitor visitor);

}
