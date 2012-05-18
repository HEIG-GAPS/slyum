package dataRecord;

public class APIinterface extends APIclass implements Implementable
{
	@SuppressWarnings("rawtypes")
	public APIinterface(Class c)
	{
		super(c);
	}
	
	@SuppressWarnings("rawtypes")
	public APIinterface(Class c, String name)
	{
		super(c,name);
	}
}
