package classDiagram.components;

/**
 * This class, extent of Type contains a list of final attributes representing
 * the primitives types.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class PrimitiveType extends Type
{
	public static final Type BOOLEAN_TYPE = new PrimitiveType("boolean");
	public static final Type BYTE_TYPE = new PrimitiveType("byte");
	public static final Type CHAR_TYPE = new PrimitiveType("chat");
	public static final Type DOUBLE_TYPE = new PrimitiveType("double");
	public static final Type FLOAT_TYPE = new PrimitiveType("float");
	public static final Type INTEGER_TYPE = new PrimitiveType("int");
	public static final Type LONG_TYPE = new PrimitiveType("long");
	public static final Type STRING_TYPE = new PrimitiveType("string");
	public static final Type VOID_TYPE = new PrimitiveType("void");

	/**
	 * Create a new primitive type.
	 * 
	 * @param name
	 *            the name of the primitive type.
	 */
	private PrimitiveType(String name)
	{
		super(name);
	}
}
