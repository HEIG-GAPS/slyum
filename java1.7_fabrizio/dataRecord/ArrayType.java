package dataRecord;

public class ArrayType implements ElementType
{
	private int dimension = 0;
	private ElementType eType; // the type name
	private boolean isEllipse = false;
	
	public ArrayType(ElementType et, int dimension)
	{
		this.eType = et;
		this.dimension = dimension;
	}
	
	public ArrayType(ElementType et, int dimension, boolean isEllipse)
	{
		this(et,dimension);
		this.isEllipse = isEllipse;
	}
	
	public ArrayType(int dimansion, boolean isEllipse)
	{
		this.dimension = dimansion;
		this.isEllipse = isEllipse;
	}
	
	public String getElementType()
	{
		String tmp = "";
		
		if (eType!=null)
			tmp = eType.getElementType();
		
		if (!isEllipse)
			for (int i = 0; i < dimension; i++)
				tmp += "[]";
		else
			tmp += "..."; 
		
		return tmp;
	}

	public int getDimension()
	{
		return dimension;
	}

	public void setDimension(int dimension)
	{
		this.dimension = dimension;
	}

	public ElementType geteType()
	{
		return eType;
	}

	public void seteType(ElementType eType)
	{
		this.eType = eType;
	}
	
	public String toString()
	{
		return getElementType();
	}

}
