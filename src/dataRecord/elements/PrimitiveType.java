package dataRecord.elements;

import dataRecord.elementType.ElementType;
/**
 * Enumerate all the primitive type in an object-oriented language
 * Primitive type are:
 * - boolean
 * - byte
 * - char
 * - double
 * - float
 * - int
 * - long
 * - short
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 10-lug-2012
 */
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