package classDiagram.components;

import java.util.LinkedList;

import swing.XMLParser.EntityType;

/**
 * Represent a class in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class ClassEntity extends Entity
{
	/** Contains all inners classes. / ! \ not used in Slyum 1.0. */
	private final LinkedList<ClassEntity> innersClass = new LinkedList<ClassEntity>();

	/**
	 * Construct a new class.
	 * 
	 * @param name
	 *            the name of the class
	 * @param visibility
	 *            the visibility of the class
	 */
	public ClassEntity(String name, Visibility visibility)
	{
		super(name, visibility);
	}

	/**
	 * Construc a new class. Does not generate a new unique id, but use the
	 * given id in parameters.
	 * 
	 * @param name
	 *            the name of the class
	 * @param visibility
	 *            the visibility of the class
	 * @param id
	 *            the class id
	 */
	public ClassEntity(String name, Visibility visibility, int id)
	{
		super(name, visibility, id);
	}

	/**
	 * Add a new inner class.
	 * 
	 * @param classComponent
	 *            the new inner class.
	 */
	public void addInnerClass(ClassEntity classComponent)
	{
		if (classComponent == null)
			throw new IllegalArgumentException("classComponent is null");

		innersClass.add(classComponent);

		setChanged();
	}

	@Override
	protected String getEntityType()
	{
		return EntityType.CLASS.toString();
	}
}
