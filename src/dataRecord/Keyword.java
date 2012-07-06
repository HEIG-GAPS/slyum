/**
 * This enum show all the keyword used in the java language for declaration.
 * Keyword used inside a method are not mentioned, like "if", "for", "return", etc.
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 5-lug-2012
 */
package dataRecord;

public enum Keyword
{
	ABSTRACT, 
	BOOLEAN,
	BYTE,
	CHAR, 
	CLASS,
	CONST,
	DOUBLE,
	ENUM,
	EXTENDS,
	FINAL,
	FLOAT,
	IMPLEMENTS,
	IMPORT,
	INT,
	INTERFACE,
	LONG,
	NATIVE,
	PACKAGE,
	PRIVATE,
	PROTECTED,
	PUBLIC,
	SHORT,
	STATIC,
	STRICTFP,
	SYNCHRONIZED,
	THROWS,
	TRANSIENT,
	VOID,
	VOLATILE;

	public String toString()
	{
		if (super.equals(PACKAGE))
			return "";
		else
			return super.toString().toLowerCase();
	}

	/**
	 * return if a keyword is an Access modifier (package, private, protected or public)
	 * 
	 * @param kw
	 * @return
	 */
	public static boolean isAccess(Keyword kw)
	{
		return (kw == Keyword.PACKAGE || kw == Keyword.PRIVATE || kw == Keyword.PROTECTED || kw == Keyword.PUBLIC);
	}

	/**
	 * return if a keyword is a primitive type (int, double, char, etc)
	 * @param type
	 * @return
	 */
	public static boolean isPrimitive(Keyword type)
	{
		return (type == Keyword.BOOLEAN || type == Keyword.BYTE || type == Keyword.CHAR || type == Keyword.DOUBLE
				|| type == Keyword.FLOAT || type == Keyword.INT || type == Keyword.LONG);
	}

	public static Keyword[] getTopClassModifiers()
	{
		Keyword[] tmp = { ABSTRACT, FINAL, PACKAGE, PUBLIC, STRICTFP };

		return tmp;
	}

	public static Keyword[] getNestedClassModifiers()
	{
		Keyword[] tmp = { ABSTRACT, FINAL, PACKAGE, PRIVATE, PROTECTED, PUBLIC, STRICTFP, STATIC };

		return tmp;
	}

	public static Keyword[] getTopInterfaceModifiers()
	{
		Keyword[] tmp = { ABSTRACT, PACKAGE, PUBLIC, STRICTFP };

		return tmp;
	}

	public static Keyword[] getNestedInterfaceModifiers()
	{
		Keyword[] tmp = { ABSTRACT, PACKAGE, PRIVATE, PROTECTED, PUBLIC, STRICTFP, STATIC };

		return tmp;
	}

	public static Keyword[] getVariableModifiers()
	{
		Keyword[] tmp = { FINAL, PACKAGE, PRIVATE, PROTECTED, PUBLIC, STATIC, TRANSIENT, VOLATILE };

		return tmp;
	}

	public static Keyword[] getMethodModifiers()
	{
		Keyword[] tmp = { ABSTRACT, FINAL, NATIVE, PACKAGE, PRIVATE, PROTECTED, PUBLIC, STRICTFP, STATIC, SYNCHRONIZED };

		return tmp;
	}

	public static Keyword[] getConstructorModifiers()
	{
		Keyword[] tmp = { PRIVATE, PROTECTED, PUBLIC };

		return tmp;
	}

	public static Keyword[] getBlockModifiers()
	{
		Keyword[] tmp = { PACKAGE, STATIC, SYNCHRONIZED };

		return tmp;
	}
}
