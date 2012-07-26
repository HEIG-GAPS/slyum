package dataRecord.elements;

import dataRecord.Keyword;
import dataRecord.io.ElementVisitor;
/**
 * This class represent a Java package.
 * Package = directory. Java classes can be grouped together in packages. 
 * A package name is the same as the directory (folder) name which contains the .java files. 
 * You declare packages when you define your Java program, and you name the packages 
 * you want to use from other libraries in an import statement.
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 5-lug-2012
 */
public class PackageStmt extends Statement
{
	private static Keyword kw = Keyword.PACKAGE;

	public PackageStmt(String packageName)
	{
		super(kw, packageName);
	}
	
	public PackageStmt(String packageName, int id)
	{
		super(kw, packageName, id);
	}

	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 */
	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	public String toString()
	{
		return "package " + getPackageName() + ";";
	}
}
