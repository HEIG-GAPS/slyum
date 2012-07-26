package dataRecord.elementType;
/**
 * Each datafield has a specific type. This type can be a "Primitive type" (int, double, char, etc)
 * a user defined type (an object created by the user) or an API type.
 * 
 * This interface represent this type.
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 11-lug-2012
 *
 */
public interface ElementType
{
	/**
	 * get the name of the type
	 * 
	 * @return type name
	 */
	String getElementType();
}
