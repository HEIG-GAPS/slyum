package classDiagram.relationships;

import classDiagram.components.Entity;

/**
 * Represent a binary association in UML structure. A binary association can
 * have only two roles.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Binary extends Association
{
	/**
	 * Create a new binary association between the entity source and target. Two
	 * news
	 * roles will be created containing this association and an entity (source
	 * or target). Entities will add this new role corresponding to this
	 * association. The parameter directed define if the association is directed
	 * or not. An association directed means, in UML, that an association can be
	 * read only in one direction (represented by an arrow).
	 * 
	 * @param source
	 *            the source entity
	 * @param target
	 *            the target entity
	 * @param directed
	 *            true if the association is directed; false otherwise
	 */
	public Binary(Entity source, Entity target, boolean directed)
	{
		super();

		// role calls addRole for this association, not adding here.
		new Role(this, source, "");
		new Role(this, target, "");

		this.directed = directed;
	}

	/**
	 * Create a new binary association between the entity source and target. Two
	 * news
	 * roles will be created containing this association and an entity (source
	 * or target). Entities will add this new role corresponding to this
	 * association. The parameter directed define if the association is directed
	 * or not. An association directed means, in UML, that an association can be
	 * read only in one direction (represented by an arrow). The id is not
	 * generated and must be passed in parameter.
	 * 
	 * @param source
	 *            the source entity
	 * @param target
	 *            the target entity
	 * @param directed
	 *            true if the association is directed; false otherwise
	 * @param id
	 *            the id for this association
	 */
	public Binary(Entity source, Entity target, boolean directed, int id)
	{
		super(id);

		// role calls addRole for this association, not adding here.
		new Role(this, source, "");
		new Role(this, target, "");

		this.directed = directed;
	}

	@Override
	public String getAssociationType()
	{
		return swing.XMLParser.Aggregation.NONE.toString();
	}
}
