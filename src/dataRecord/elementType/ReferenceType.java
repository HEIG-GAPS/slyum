package dataRecord.elementType;

public class ReferenceType implements ElementType
{
	private final static char symbol  = '&';
	private ElementType eType; 

	public ReferenceType(ElementType eType)
	{
		this.eType = eType;
	}

	public ElementType geteType()
	{
		return eType;
	}

	public void seteType(ElementType eType)
	{
		this.eType = eType;
	}

	public String getElementType()
	{
		return eType==null?"":eType.getElementType()+symbol;		
	}

}
