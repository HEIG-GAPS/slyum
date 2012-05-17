package dataRecord;

public class ListType implements ElementType
{
	private ElementType eType;
	
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
		
		return "List<"+tmp+">";
	}
	
	public ElementType geteType()
	{
		return eType;
	}

	public void seteType(ElementType eType)
	{
		this.eType = eType;
	}
	
//	public String toString()
//	{
//		return "<"+eType.getElementType()+">";
//	}

}
