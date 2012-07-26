package dataRecord.elementType;

public class PointerType implements ElementType
{
	private final static char symbol  = '*';
	private ElementType eType; 

	public PointerType(ElementType eType)
	{
		this.eType = eType;
	}

	public String getElementType()
	{
		return eType==null?"":eType.getElementType()+symbol;		
	}

	public ElementType geteType()
	{
		return eType;
	}

	public void seteType(ElementType eType)
	{
		this.eType = eType;
	}

}
