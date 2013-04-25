package classDiagram.components;

import swing.XMLParser.EntityType;
import utility.Utility;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Role;

/**
 * Represent an association class in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class AssociationClass extends ClassEntity
{
	private Binary association;

	/**
	 * Construct a new association class. An association class is a class in UML
	 * structure associated with an association. Does not construct a new binary
	 * association but will use the binary given in parameters.
	 * 
	 * @param name
	 *            the name of the class
	 * @param visibility
	 *            the visibility of the class
	 * @param binary
	 *            the binary association associated with the association class
	 */
	public AssociationClass(String name, Visibility visibility, Binary binary)
	{
		super(name, visibility);
		checkAssociationCreation(binary);
		
		// Use an existing association.
		association = binary;
	}

	/**
	 * Construct a new association class. An association class is a class in UML
	 * structure associated with an association. Does not construct a new binary
	 * association but will use the binary given in parameters. Does not
	 * generate a new unique id but will use the id given in parameters.
	 * 
	 * @param name
	 *            the name of the class
	 * @param visibility
	 *            the visibility of the class
	 * @param binary
	 *            the binary association associated with the association class
	 * @param id
	 *            the id to given to the class
	 */
	public AssociationClass(String name, Visibility visibility, Binary binary, int id)
	{
		super(name, visibility, id);
		checkAssociationCreation(binary);
		
		association = binary;
	}

	/**
	 * Construct a new association class. An association class is a class in UML
	 * structure associated with an association. A new binary association will
	 * be created between the given source and target.
	 * 
	 * @param name
	 *            the name of the class
	 * @param visibility
	 *            the visibility of the class
	 * @param source
	 *            the source of the new association.
	 * @param target
	 *            the target of the new association.
	 */
	public AssociationClass(String name, Visibility visibility, Entity source, Entity target)
	{
		super(name, visibility);
		checkAssociationCreation(source, target);

		// Create a new association.
		association = new Binary(source, target, false);
	}
	
	private void checkAssociationCreation(Binary binary)
	{
        if (binary == null)
            throw new IllegalArgumentException("Creation of association " + name + " failed because binary is null.");

        for (Role r : binary.getRoles())
            if (r.getEntity() instanceof AssociationClass)
               throw new IllegalArgumentException("Creation of association " + name + " failed.\nAn association class cannot be associed with another association class.");
	}
	
	private void checkAssociationCreation(Entity source, Entity target)
    {
        if (source instanceof AssociationClass || target instanceof AssociationClass)
           throw new IllegalArgumentException("Creation of association " + name + " failed.\nAn association class cannot be associed with another association class.");
    }

	/**
	 * Get the association of the association class.
	 * 
	 * @return the association of the association class.
	 */
	public Binary getAssociation()
	{
		return association;
	}

	@Override
    public String getEntityType()
	{
		return EntityType.ASSOCIATION_CLASS.toString();
	}

	@Override
	protected String getLastBalise(int depth)
	{
		final String tab = Utility.generateTab(depth);
		final String xml = tab + "<associationClassID>" + association.getId() + "</associationClassID>\n";

		return xml;
	}

	/**
	 * Change the association of the association class.
	 * 
	 * @param binary
	 *            the new association
	 */
	public void setAssociation(Binary binary)
	{
		if (binary == null)
			throw new IllegalArgumentException("binary is null");

		association = binary;
	}
}
