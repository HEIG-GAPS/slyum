/**
 * Each datafield has a specific type. This type can be a "Primitive type" (int, double, char, etc)
 * a user defined type (an object created by the user) or an API type.
 * This class represent a collection of the choosen type.
 * See Collection for the differents kinds of a collection.
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see ElementType
 * @see Collection
 */
package dataRecord.elementType;

public class ListType implements ElementType
{
	private ElementType eType;
	private Collection collection;
	
	public ListType(ElementType et)
	{
		this.eType = et;
	}
	
	public ListType()
	{
	}

	@Override
	public String getElementType()
	{
		String tmp ="";
		
		if(eType!=null)
			tmp = eType.getElementType();

		return tmp;
	}
	
	public ElementType geteType()
	{
		return eType;
	}

	public void seteType(ElementType eType)
	{
		this.eType = eType;
	}

	public Collection getCollection()
	{
		return collection;
	}

	public void setCollection(Collection collection)
	{
		this.collection = collection;
	}
	
//	public String toString()
//	{
//		return "<"+eType.getElementType()+">";
//	}

}
