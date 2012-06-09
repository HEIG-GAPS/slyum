package classDiagram.components;

import change.BufferVariable;
import change.Change;
import java.util.Observable;

import utility.Utility;
import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;

/**
 * Represent a variable in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Variable extends Observable implements IDiagramComponent
{
	public static final String REGEX_SEMANTIC_ATTRIBUTE = "([a-zA-Z|_])(\\w)*";
	
	public static boolean checkSemantic(String name)
	{
		return !name.isEmpty() && name.matches(REGEX_SEMANTIC_ATTRIBUTE);
	}
	
	protected boolean constant = false;

	protected final int id = ClassDiagram.getNextId();
	protected String name;
	protected Type type = PrimitiveType.VOID_TYPE;

	/**
	 * Create a new variable with the given name and type.
	 * 
	 * @param name
	 *            the name for the variable
	 * @param type
	 *            the type for the variable
	 */
	public Variable(String name, Type type)
	{
		boolean isBlocked = Change.isBlocked();
		Change.setBlocked(true);
		
		if (!setName(name))
			throw new IllegalArgumentException("semantic name incorrect");
		
		setType(type);
		
		Change.setBlocked(isBlocked);
	}
	
	/**
	 * Constructor of copy.
	 * @param variable variable
	 */
	public Variable(Variable variable)
	{
		this.name = variable.name;
		this.type = new Type(variable.type.getName());
	}
	
	public void setVariable(Variable variable)
	{
		boolean isRecord = Change.isRecord();
		Change.record();
		
		setName(variable.getName());
		setType(variable.getType());
		
		if(!isRecord)
			Change.stopRecord();
		
		notifyObservers();
	}

	@Override
	public int getId()
	{
		return id;
	}

	/**
	 * Get the name for this variable.
	 * 
	 * @return the name for this variable
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the type for this variable.
	 * 
	 * @return the type for this variable.
	 */
	public Type getType()
	{
		setChanged();

		return type;
	}

	@Override
	public void select()
	{
		setChanged();
	}

	/**
	 * Set the name for this variable.
	 * 
	 * @param name
	 *            the new name for this variable
	 */
	public boolean setName(String name)
	{
//		if (!checkSemantic(name) || name.equals(getName()))
		if ( name.equals(getName()))
			return false;

		Change.push(new BufferVariable(this));
		this.name = name;
		Change.push(new BufferVariable(this));

		setChanged();

		return true;
	}

	/**
	 * Set the type for this variable.
	 * 
	 * @param type
	 *            the new type for this variable
	 */
	public void setType(Type type)
	{		
		if (getType()!= null && type.getName().equals(getType().getName()))
			return;
		
		Change.push(new BufferVariable(this));
		this.type = type;
		Change.push(new BufferVariable(this));

		setChanged();
	}

	@Override
	public String toString()
	{
		return name + " : " + type;
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		return tab + "<variable " + "name=\"" + name + "\" " + "type=\"" + type.toXML(depth+1) + "\" " + "const=\"" + constant + "\"/>";
	}
}
