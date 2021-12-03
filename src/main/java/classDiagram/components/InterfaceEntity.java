package classDiagram.components;

import swing.XMLParser.EntityType;

/**
 * Represent an interface in UML structure.
 *
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class InterfaceEntity extends SimpleEntity {
  public static final String STEREOTYPE_INTERFACE = "interface";

  public InterfaceEntity(String name) {
    super(name);
    initializeComponents();
  }

  /**
   * Construct a new interface.
   *
   * @param name the name of the interface
   * @param visibility the visibility of the interface
   */
  public InterfaceEntity(String name, Visibility visibility) {
    super(name, visibility);
    initializeComponents();
  }

  /**
   * Construct a new interface with the given id.
   *
   * @param name the name of the interface
   * @param visibility the visibility of the interface
   * @param id the id of the interface
   */
  public InterfaceEntity(String name, Visibility visibility, int id) {
    super(name, visibility, id);
    initializeComponents();
  }

  private void initializeComponents() {
    setStereotype(STEREOTYPE_INTERFACE);
    setAbstract(true);
  }

  @Override
  public void addAttribute(Attribute attribute) {
    if (attribute == null)
      throw new IllegalArgumentException("attribute is null");

    // Only static attribute can be added to an interface.
    attribute.setStatic(true);

    super.addAttribute(attribute);
  }

  @Override
  public boolean addMethod(Method operation) {
    if (operation == null)
      throw new IllegalArgumentException("function is null");

    // Only abstract methods can be added to an interface.
    operation.setAbstract(true);

    return super.addMethod(operation);
  }

  @Override
  protected String getEntityType() {
    return EntityType.INTERFACE.toString();
  }

  @Override
  public void setAbstract(boolean isAbstract) {
    super.setAbstract(true);
  }

}
