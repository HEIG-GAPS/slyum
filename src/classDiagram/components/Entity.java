package classDiagram.components;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utility.SMessageDialog;
import change.BufferClass;
import change.BufferIndex;
import change.Change;
import classDiagram.relationships.Role;

/**
 * Abstract class containing all classes parameters (attributes, methods,
 * visibility, ...)
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public abstract class Entity extends Type implements Cloneable {
  protected List<Role> roles = new LinkedList<>();
  protected String stereotype = "";

  public Entity(String name) {
    super(name);
  }

  public Entity(String name, int id) {
    super(name, id);
  }

  public Entity(Entity e) {
    super(e.name, e.id);
  }

  /**
   * Add a new role.
   * 
   * @param role
   *          the new role
   */
  public void addRole(Role role) {
    if (role == null) throw new IllegalArgumentException("role is null");

    roles.add(role);

    setChanged();
  }

  /**
   * Use in XML exportation. Get the type of the entity.
   * 
   * @return the type of the entity.
   */
  protected abstract String getEntityType();

  /**
   * Us in XML exportation. Get a string to add new tags if necessary.
   * 
   * @param depth
   *          the number of tabs to add before each tag
   * @return the tag to add before the closure tag.
   */
  protected String getLastBalise(int depth) {
    return ""; // no last balise
  }

  /**
   * Get the stereotype of the entity.
   * 
   * @return the stereotype of the entity.
   */
  public String getStereotype() {
    return stereotype;
  }

  @Override
  public boolean setName(String name) {
    BufferClass bc = new BufferClass(this);
    boolean b = super.setName(name);

    if (b) {
      Change.push(bc);
      Change.push(new BufferClass(this));
    }

    return b;
  }

  /**
   * Set the stereotype of the entity.
   * 
   * @param stereotype
   *          the new stereotype
   */
  public void setStereotype(String stereotype) {
    if (stereotype == null)
      throw new IllegalArgumentException("stereotype is null");

    this.stereotype = stereotype;
  }

  public boolean isNameItalic() {
    return false;
  }

  /**
   * Move the object's position in the given array by the given offset. Offset
   * is added to the current index to compute the new index. The offset can be
   * positive or negative.
   * 
   * @param list
   *          the list containing the object to move
   * @param o
   *          the object to move
   * @param offset
   *          the offset for compute the new index
   */
  protected <T extends Object> void moveComponentPosition(List<T> list, T o,
          int offset) {
    int index = list.indexOf(o);

    if (index != -1) {
      Change.push(new BufferIndex<T>(this, list, o));

      list.remove(o);
      list.add(index + offset, o);

      Change.push(new BufferIndex<T>(this, list, o));

      setChanged();
    }
  }

  @Override
  public Entity clone() throws CloneNotSupportedException {
    try {
      // Création de la copie par réflexion.
      String classToInstanciate = getClass().equals(AssociationClass.class) ? ClassEntity.class
              .getName() : getClass().getName();
      Entity entity = (Entity) Class.forName(classToInstanciate)
              .getConstructor(String.class).newInstance(getName());

      // Copie des attributs primitifs
      entity.setStereotype(getStereotype());
      return entity;

    } catch (Exception e) {
      SMessageDialog
              .showErrorMessage("An error occured when copying the entity.\nThank to send a report.");
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String getXmlTagName() {
    return "entity";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element entity = doc.createElement(getXmlTagName());

    entity.setAttribute("id", String.valueOf(getId()));
    entity.setAttribute("name", toString());
    entity.setAttribute("entityType", getEntityType());

    return entity;
  }
}
