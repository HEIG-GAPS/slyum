package dataRecord;

public class APItype implements ElementType
{
	@SuppressWarnings("rawtypes")
	private Class c;
	private String className;

	@SuppressWarnings("rawtypes")
	public APItype(Class c)
	{
		this.setC(c);
	}
	
	@SuppressWarnings("rawtypes")
	public APItype(Class c, String name)
	{
		this.setC(c);
		this.className = name;
	}

	@SuppressWarnings("rawtypes")
	public Class getC()
	{
		return c;
	}

	@SuppressWarnings("rawtypes")
	public void setC(Class c)
	{
		this.c = c;
	}
	
	public String getElementType()
	{
		return className;
	}
}
