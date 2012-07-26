package dataRecord.elementType;
/**
 * Each datafield has a specific type. This type can be a "Primitive type" (int, double, char, etc)
 * a user defined type (an object created by the user) or an API type.
 * This class represent an API class. 
 * It implements Extendable.
 * Extendable represent an interface regrouping the APIclasses and the user defined classes 
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see ClassKind
 * @see APItype
 */
public class APIclass extends APItype implements ClassKind
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
