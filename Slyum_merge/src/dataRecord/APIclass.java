package dataRecord;

public class APIclass extends APItype implements Extendable
{
	@SuppressWarnings("rawtypes")
	public APIclass(Class c)
	{
		super(c);
	}
	
	@SuppressWarnings("rawtypes")
	public APIclass(Class c, String name)
	{
		super(c,name);
	}
}
