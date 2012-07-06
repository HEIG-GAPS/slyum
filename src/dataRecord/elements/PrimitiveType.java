package dataRecord.elements;

import dataRecord.elementType.ElementType;

public enum PrimitiveType implements ElementType
{
	BOOLEAN,
	BYTE,
	CHAR,
	DOUBLE,
	FLOAT,
	INT,
	LONG,
	SHORT;

	@Override
	public String getElementType()
	{
		return super.toString().toLowerCase();
	}
}