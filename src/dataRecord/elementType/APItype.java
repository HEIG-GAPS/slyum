package dataRecord.elementType;
/**
 * Each datafield has a specific type. This type can be a "Primitive type" (int, double, char, etc)
 * a user defined type (an object created by the user) or an API type.
 * This class represent an API. It has two sons APIclass and APIinterface
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see ElementType
 */
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
