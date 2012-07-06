/**
 * Each datafield has a specific type. This type can be a "Primitive type" (int, double, char, etc)
 * a user defined type (an object created by the user) or an API type.
 * This class represent an API interface. 
 * It implements Implementable.
 * Implementable represent an interface regrouping the API interfaces and the user defined intefaces 
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see Implementable
 * @see APItype
 */
package dataRecord.elementType;

public class APIinterface extends APItype implements Implementable
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
