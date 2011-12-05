package classDiagram.components;

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
	protected boolean constant = false;

	protected final int id = ClassDiagram.getNextId();
	protected String name;
	protected Type type;

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
		if (type.toString().isEmpty())
			throw new IllegalArgumentException("type is null");

		this.name = name;
		this.type = new Type(type.getName());
	}
	
	public Variable(Variable variable)
	{
		this.name = variable.name;
		this.type = variable.type;
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
		if (name.isEmpty())
			throw new IllegalArgumentException("name is null");

		if (!name.matches("([a-zA-Z|_])(\\w)*"))
			return false;

		this.name = name;

		setChanged();

		return true;
	}

	/**
	 * Set the type for this variable.
	 * 
	 * @param type
	 *            the new type for this variable
	 */
	public boolean setType(Type type)
	{
		if (!type.getName().matches("([a-zA-Z|_])[(\\w)<>.]*"))
			return false;

		this.type = type;

		setChanged();

		return true;
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
