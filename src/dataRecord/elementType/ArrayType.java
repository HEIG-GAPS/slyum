package dataRecord.elementType;
/**
 * Each datafield has a specific type. This type can be a "Primitive type" (int, double, char, etc)
 * a user defined type (an object created by the user) or an API type.
 * This class represent an Array of the choosen type
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 2-lug-2012
 * @see ElementType
 */
public class ArrayType implements ElementType
{
	private int dimension = 0;
	private ElementType eType; 
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
