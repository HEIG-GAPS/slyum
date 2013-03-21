package classDiagram.relationships;

import graphic.textbox.ILabelTitle;

import java.util.LinkedList;
import java.util.Observable;

import utility.Utility;
import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;
import classDiagram.components.Entity;

/**
 * This abstract class is the upper-class for all associations in UML structure.
 * (Associations can be binary, multi, agregation or composition).
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public abstract class Association extends Observable implements IDiagramComponent, ILabelTitle
{
	protected boolean directed;

	protected final int id;
	protected String name;
	protected LinkedList<Role> roles;

	public Association()
	{
		roles = new LinkedList<Role>();

		id = ClassDiagram.getNextId();
	}

	public Association(int id)
	{
		roles = new LinkedList<Role>();

		this.id = id;
	}

	/**
	 * Add a new role for this association.
	 * 
	 * @param role
	 *            the new role to add
	 */
	public void addRole(Role role)
	{
		roles.add(role);
	}

	/**
	 * Get the association type. The association type is represented by a String
	 * for XML exportation (see XSD in doc).
	 * 
	 * @return a string representing the XML association type.
	 */
	public abstract String getAssociationType();

	/**
	 * Get if the association is directed or not.
	 * 
	 * @return if the association is directed or not
	 */
	public boolean getDirected()
	{
		return directed;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public String getLabel()
	{
		return getName();
	}

	/**
	 * Get the name for this association. The name is same as the label.
	 * 
	 * @return the name for this association
	 */
	public String getName()
	{
		return name == null ? "" : name;
	}

	@SuppressWarnings("unchecked")
	public LinkedList<Role> getRoles()
	{
		return (LinkedList<Role>) roles.clone();
	}

	/**
	 * Return if the association is directed or not.
	 * 
	 * @return ture if the association si directed; false otherwise
	 */
	public boolean isDirected()
	{
		return directed;
	}

	/**
	 * Remove the given role in the array of roles.
	 * 
	 * @param role
	 *            the role to remove
	 * @return true if the role has been removed; false otherwise
	 */
	public boolean removeRole(Role role)
	{
		return roles.remove(role);
	}

	/**
	 * Search a role in the array of roles corresponding to the given entity.
	 * Return null if no corresponding entities are found.
	 * 
	 * @param entity
	 *            the entity for find a role
	 * @return the role corresponding to the entity, or null.
	 */
	public Role searchRoleByEntity(Entity entity)
	{
		for (final Role role : roles)

			if (role.getEntity().equals(entity))
				return role;

		return null;
	}

	@Override
	public void select()
	{
		setChanged();
	}

	/**
	 * Set if the association is directed or not.
	 * 
	 * @param directed
	 *            the new directed state for this association
	 */
	public void setDirected(boolean directed)
	{
		this.directed = directed;

		setChanged();
	}

	@Override
	public void setLabel(String text)
	{
		setName(text);
	}

	/**
	 * Set the name of this association. Name is the same than label.
	 * 
	 * @param name
	 *            the name of this association
	 */
	public void setName(String name)
	{
		this.name = name == null || name.isEmpty() ? "" : name;
		setChanged();
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		String xml = tab + "<association id=\"" + id + "\" " + (name == null ? "" : "name=\"" + name.replaceAll("<", "&lt;") + "\"") + " direction=\"" + directed + "\" aggregation=\"" + getAssociationType() + "\">\n";

		for (final Role role : roles)
			xml += role.toXML(depth + 1) + "\n";

		return xml + tab + "</association>";
	}
	
	@Override
	public String toString()
	{
	  return getClass().getSimpleName() + (getName().isEmpty() ? "" : ": " + getLabel());
	}
}
