package classDiagram.relationships;

import java.util.LinkedList;

import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;

/**
 * Represent a multi-association in UML structure. A multi-association must have
 * minimum three role or more.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Multi extends Association {
  /**
   * Use to know if the creation of a new multi-association will be a success or
   * not. Parameters of this method must be the same of given parameters for
   * constructor.
   * 
   * @param components
   *          The classes participating in multi-association.
   * @return true if the creation of multi-association is possible with the
   *         given parameters; false otherwise
   */
  public static boolean canCreate(LinkedList<ClassEntity> components) {
    return components.size() >= 3;
  }

  /**
   * Create a new multi-association with the given classes. The array of classes
   * given in parameters must have a minimum size of three. A role will be
   * created for each classes.
   * 
   * @param components
   *          An array of classes participating in multi-association.
   */
  public Multi(LinkedList<ClassEntity> components) {
    super();

    if (components == null)
      throw new IllegalArgumentException("components is null");

    if (components.size() < 3)
      throw new IllegalArgumentException("multi : no more components");

    for (final ClassEntity ce : components)

      addClass(ce);

    directed = NavigateDirection.BIDIRECTIONAL;
  }

  /**
   * Create a new multi-association with the given classes. The array of classes
   * given in parameters must have a minimum size of three. A role will be
   * created for each classes.
   * 
   * @param components
   *          An array of classes participating in multi-association.
   * @param id
   *          the existing id of the element
   */
  public Multi(LinkedList<ClassEntity> components, int id) {
    super(id);

    if (components == null)
      throw new IllegalArgumentException("components is null");

    if (components.size() < 3)
      throw new IllegalArgumentException("multi : no more components");

    for (final ClassEntity ce : components)

      addClass(ce);

    directed = NavigateDirection.BIDIRECTIONAL;
  }

  /**
   * Add a class participating for this multi-association. Identic to addRole in
   * super class.
   * 
   * @param component
   */
  public void addClass(Entity component) {
    if (component == null)
      throw new IllegalArgumentException("component is null");

    new Role(this, component, "");
  }

  @Override
  public String getAssociationType() {
    return swing.XMLParser.Aggregation.MULTI.toString();
  }

  /**
   * Remove a class participating to this multi-association.
   * 
   * @param component
   *          the class to remove.
   * 
   * @return true if classe has been removed; false otherwise
   */
  @SuppressWarnings("deprecation")
  public boolean removeClass(Entity component) {
    if (component == null)
      throw new IllegalArgumentException("component is null");

    return removeRole(searchRoleByEntity(component));
  }
}
