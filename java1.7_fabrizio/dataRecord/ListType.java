package dataRecord;

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
