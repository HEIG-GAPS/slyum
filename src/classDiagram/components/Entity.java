package classDiagram.components;

import change.BufferClass;
import change.BufferIndex;
import change.Change;
import classDiagram.relationships.IParentChild;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Relation;
import classDiagram.relationships.Role;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.PanelClassDiagram;
import swing.Slyum;
import utility.SMessageDialog;

/**
 * Abstract class containing all classes parameters (attributes, methods,
 * visibility, ...)
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public abstract class Entity extends Type implements Cloneable, Transferable {
  
  final public static DataFlavor ENTITY_FLAVOR = 
      new DataFlavor(Entity.class, "Entity Type");
  private static DataFlavor flavors[] = { ENTITY_FLAVOR };
  
  protected List<IParentChild> childs = new LinkedList<>();
  protected List<IParentChild> parents = new LinkedList<>();
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
   * Add a new child.
   *
   * @param child
   *          the new child
   */
  public void addChild(IParentChild child) {
    if (child == null) throw new IllegalArgumentException("child is null");
    
    childs.add(child);
    
    setChanged();
  }

  /**
   * Add a new parent.
   *
   * @param parent
   *          the new parent
   */
  public void addParent(IParentChild parent) {
    if (parent == null) throw new IllegalArgumentException("parent is null");
    
    parents.add(parent);
    
    setChanged();
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
      
    } catch (
        ClassNotFoundException | NoSuchMethodException |  SecurityException |
            InstantiationException | IllegalAccessException |
            IllegalArgumentException | InvocationTargetException e) {
      SMessageDialog.showErrorMessage(
          "An error occured when copying the entity.\nPlease send a report.");
      Slyum.LOGGER.log(
          Level.SEVERE,
          "An error occured when copying the entity.", e);
    }
    return null;
  }
  
  public LinkedList<Entity> getAllChilds() {
    LinkedList<Entity> allChilds = new LinkedList<>();
    allChilds.add(this);
    
    for (IParentChild p : childs)
      allChilds.addAll(p.getChild().getAllChilds());

    return allChilds;
  }

  public LinkedList<Entity> getAllParents() {
    final LinkedList<Entity> allParents = new LinkedList<>();
    allParents.add(this);
    
    for (final IParentChild p : parents)
      allParents.addAll(p.getParent().getAllParents());

    return allParents;
  }

  public List<IParentChild> getChilds() {
    return childs;
  }

  /**
   * Return all entities associed with this one. Associed means that there 
   * is a relation between this entity and another one.
   * @return Entities linked with.
   */
  public HashMap<Relation, Entity> getLinkedEntities() {
    HashMap<Relation, Entity> entities = new HashMap<>();
    for (Relation relation : 
        PanelClassDiagram.getInstance().getClassDiagram().getRelations()) {
      
      if (relation.getSource() == this)
        entities.put(relation, relation.getTarget());
      
      else if (relation.getTarget() == this) 
        entities.put(relation, relation.getSource());
      
      else if (relation instanceof Multi)
        entities.put(relation, null);
    }
    return entities;
  }

  public List<IParentChild> getParents() {
    return parents;
  }

  public  List<Role> getRoles() {
    return new LinkedList<>(roles);
  }

  /**
   * Get the stereotype of the entity.
   * 
   * @return the stereotype of the entity.
   */
  public String getStereotype() {
    return stereotype;
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

  @Override
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (!isDataFlavorSupported(flavor))
      throw new UnsupportedFlavorException(flavor);
    return this;
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element entity = doc.createElement(getXmlTagName());

    entity.setAttribute("id", String.valueOf(getId()));
    entity.setAttribute("name", toString());
    entity.setAttribute("entityType", getEntityType());

    return entity;
  }

  @Override
  public String getXmlTagName() {
    return "entity";
  }

  public boolean isChildOf(Entity entity) {
    boolean isChild = false;

    for (final IParentChild i : parents)
      isChild |= i.getParent().isChildOf(entity);

    return isChild || equals(entity);
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return Arrays.asList(getTransferDataFlavors()).contains(flavor);
  }

  public boolean isNameItalic() {
    return false;
  }

  public boolean isParentOf(Entity entity) {
    boolean isParent = false;

    for (final IParentChild i : childs)
      isParent |= i.getChild().isParentOf(entity);

    return isParent || equals(entity);
  }

  /**
   * Remove the child.
   * 
   * @param child
   *          the child to remove
   */
  public void removeChild(IParentChild child) {
    childs.remove(child);

    setChanged();
  }

  /**
   * Remove the parent.
   * 
   * @param parent
   *          the parent to remove
   */
  public void removeParent(IParentChild parent) {
    parents.remove(parent);

    setChanged();
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
  protected <T extends Object> void moveComponentPosition(List<T> list, T o, int offset) {
    int index = list.indexOf(o);
    
    if (index != -1) {
      Change.push(new BufferIndex<T>(this, list, o));
      
      list.remove(o);
      list.add(index + offset, o);
      
      Change.push(new BufferIndex<T>(this, list, o));

      setChanged();
    }
  }
}
