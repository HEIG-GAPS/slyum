package classDiagram.components;

import java.util.Observable;

import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;

/**
 * Represent a type in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Type extends Observable implements IDiagramComponent
{
	protected final int id;

	protected String name;

	// private LinkedList<Type> collections; // Not use in Slyum 1.0 (can't
	// create array of types).

	/**
	 * Create a new type with the specified name.
	 * 
	 * @param name
	 *            the name of the type
	 */
	public Type(String name)
	{
		if (name.isEmpty())
			throw new IllegalArgumentException("name is null");

		id = ClassDiagram.getNextId();
		this.name = name;
	}

	/**
	 * Create a new type with the specified name and id.
	 * 
	 * @param name
	 *            the name of the type
	 * @param id
	 *            the id of the type
	 */
	public Type(String name, int id)
	{
		if (name.isEmpty())
			throw new IllegalArgumentException("name is null");

		this.id = id;
		this.name = name;
	}

	@Override
	public int getId()
	{
		return id;
	}

	/**
	 * Get the name of the type.
	 * 
	 * @return the name of the type
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public void select()
	{
		setChanged();
	}

	/**
	 * Set the name for this type.
	 * 
	 * @param name
	 *            the new name.
	 */
	public boolean setName(String name)
	{
		if (name == null)
			throw new IllegalArgumentException("name is null");

		if (name.isEmpty() || !name.matches("([a-zA-Z|_])[(\\w)<>.]*"))
			return false;

		this.name = name;

		setChanged();

		return true;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public String toXML(int depth)
	{
		// XML structure is just the name of the type. Sub-elements use this
		// name themselves.
		return getName().replace("<", "&lt;").replace(">", "&gt;");
	}
}
