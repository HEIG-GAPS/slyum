package dataRecord.elements;

import dataRecord.Keyword;
import dataRecord.io.ElementVisitor;

/**
 * This class represent a constructor.
 * A constructor is a method that creates the instance of the object
 * It has the same name of the surrounding class, has no return type
 *
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 5-lug-2012
 */
public class Constructor extends Member
{
	/**
	 * the construcotr of the constructor
	 * it creates a new costrucotr with name and access modifier
	 * 
	 * @param name
	 * @param access
	 */
	public Constructor(String name, Keyword access)
	{
		super(name, access);
	}
	
	public Constructor(String name, Keyword access, int id)
	{
		super(name, access, id);
	}
	
	/**
	 * creates a clone
	 * @param m
	 */
	public Constructor(Constructor m)
	{
		super(m.getName(), m.getAccess());
		setMethodBody(m.getMethodBody());
		setThrowClauses(m.getThrowClauses());
		setParams(m.getParams());
		setID(m.getID());
	}

	@Override
	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 * 
	 */
	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	@Override
	public String toString()
	{
		String s = "";

		s += getAccess().toString();
		s += " ";
		s += getName();
		s += ParamToString();
		s += throwClausesToString();
		s += "\n" + methodBody;
		
		return s;
	}

}
