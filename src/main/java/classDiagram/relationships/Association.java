package classDiagram.relationships;

import classDiagram.ClassDiagram;
import classDiagram.components.Entity;
import graphic.textbox.ILabelTitle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.Observable;

/**
 * This abstract class is the upper-class for all associations in UML structure. (Associations can be binary, multi,
 * agregation or composition).
 *
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public abstract class Association extends Observable implements Relation, ILabelTitle {

  public enum NavigateDirection {
    BIDIRECTIONAL, FIRST_TO_SECOND, SECOND_TO_FIRST
  }

  protected NavigateDirection directed;

  protected final int id;
  protected String name;
  protected LinkedList<Role> roles;

  public Association() {
    roles = new LinkedList<>();
    id = ClassDiagram.getNextId();
  }

  public Association(int id) {
    roles = new LinkedList<>();
    this.id = id;
  }

  /**
   * Add a new role for this association.
   *
   * @param role the new role to add
   */
  public void addRole(Role role) {
    if (!roles.contains(role))
      roles.add(role);
  }

  /**
   * Get the association type. The association type is represented by a String for XML exportation (see XSD in doc).
   *
   * @return a string representing the XML association type.
   */
  public abstract String getAssociationType();

  /**
   * Get if the association is directed or not.
   *
   * @return if the association is directed or not
   */
  public NavigateDirection getDirected() {
    return directed;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getLabel() {
    return getName();
  }

  /**
   * Get the name for this association. The name is same as the label.
   *
   * @return the name for this association
   */
  public String getName() {
    return name == null ? "" : name;
  }

  @SuppressWarnings("unchecked")
  public LinkedList<Role> getRoles() {
    return (LinkedList<Role>) roles.clone();
  }

  /**
   * Remove the given role in the array of roles.
   *
   * @param role the role to remove
   *
   * @return true if the role has been removed; false otherwise
   */
  public boolean removeRole(Role role) {
    return roles.remove(role);
  }

  /**
   * Search a role in the array of roles corresponding to the given entity. Return null if no corresponding entities are
   * found.
   *
   * @param entity the entity for find a role
   *
   * @return the role corresponding to the entity, or null.
   *
   * @deprecated Cause probleme when the association is recursive.
   */
  public Role searchRoleByEntity(Entity entity) {
    for (final Role role : roles)
      if (role.getEntity().equals(entity))
        return role;
    return null;
  }

  @Override
  public void select() {
    setChanged();
  }

  /**
   * Set the association direction
   *
   * @param directed the new directed state for this association
   */
  public void setDirected(NavigateDirection directed) {
    this.directed = directed;

    setChanged();
  }

  @Override
  public void setLabel(String text) {
    setName(text);
  }

  /**
   * Set the name of this association. Name is the same than label.
   *
   * @param name the name of this association
   */
  public void setName(String name) {
    this.name = name == null || name.isEmpty() ? "" : name;
    setChanged();
  }

  @Override
  public Entity getSource() {
    if (roles.size() == 0)
      return null;
    return roles.getFirst().getEntity();
  }

  @Override
  public Entity getTarget() {
    if (roles.size() == 0)
      return null;
    return roles.getLast().getEntity();
  }

  @Override
  public void setSource(Entity entity) {
    Role role = roles.getFirst();
    role.setEntity(entity);
    role.notifyObservers();
    setChanged();
  }

  @Override
  public void setTarget(Entity entity) {
    Role role = roles.getLast();
    role.setEntity(entity);
    role.notifyObservers();
    setChanged();
  }

  @Override
  public String getXmlTagName() {
    return "association";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element association = doc.createElement(getXmlTagName());

    association.setAttribute("id", String.valueOf(id));
    association.setAttribute("direction", String.valueOf(directed));
    association.setAttribute("aggregation",
                             String.valueOf(getAssociationType()));

    if (name != null) association.setAttribute("name", name);

    for (Role role : roles)
      association.appendChild(role.getXmlElement(doc));

    return association;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
           + (getName().isEmpty() ? "" : ": " + getLabel());
  }

}
