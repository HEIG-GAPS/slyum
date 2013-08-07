package classDiagram.components;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utility.SMessageDialog;
import change.BufferClass;
import change.BufferCreationAttribute;
import change.BufferCreationMethod;
import change.Change;
import classDiagram.relationships.Inheritance;

public class SimpleEntity extends Entity {
  
  private boolean _isAbstract = false;
  protected Visibility visibility = Visibility.PUBLIC;
  protected LinkedList<Attribute> attributes = new LinkedList<>();
  protected List<Inheritance> childs = new LinkedList<>();
  protected LinkedList<Method> methods = new LinkedList<>();
  protected List<Inheritance> parents = new LinkedList<>();

  public SimpleEntity(String name) {
    super(name);
    initializeComponents(Visibility.PUBLIC);
  }

  public SimpleEntity(String name, Visibility visibility) {
    super(name);
    initializeComponents(visibility);
  }

  public SimpleEntity(String name, Visibility visibility, int id) {
    super(name, id);
    initializeComponents(visibility);
  }

  public SimpleEntity(SimpleEntity e) {
    super(e);
    initializeComponents(e.visibility);
  }

  @Override
  protected String getEntityType() {
    return null;
  }
  
  private void initializeComponents(Visibility visibility) {
    if (visibility == null)
      throw new IllegalArgumentException("visibility is null");
    this.visibility = visibility;
  }
  
  /**
   * Add a new attribute.
   * 
   * @param attribute
   *            the new attribute.
   */
  public void addAttribute(Attribute attribute)
  {
    addAttribute(attribute, attributes.size());
  }

  /**
   * Add a new attribute.
   * 
   * @param attribute
   *            the new attribute.
   */
  public void addAttribute(Attribute attribute, int index)
  {
    if (attribute == null)
      throw new IllegalArgumentException("attribute is null");

    attributes.add(index, attribute);
    int i = attributes.indexOf(attribute);
    Change.push(new BufferCreationAttribute(this, attribute, true, i));
    Change.push(new BufferCreationAttribute(this, attribute, false, i));

    setChanged();
  }

  /**
   * Add a new child.
   * 
   * @param child
   *            the new child
   */
  public void addChild(Inheritance child)
  {
    if (child == null)
      throw new IllegalArgumentException("child is null");

    childs.add(child);

    setChanged();
  }

  /**
   * Add a new method.
   * 
   * @param method
   *            the new method.
   * @return
   */
  public boolean addMethod(Method method) {
    return addMethod(method, methods.size());
  }
  
  public boolean addMethod(Method method, int index) {
    if (method == null)
      throw new IllegalArgumentException("method is null");

    if (methods.contains(method))
      return false;
    
    method.setAbstract(isAbstract());

    methods.add(index, method);
    
    int i = methods.indexOf(method);
    Change.push(new BufferCreationMethod(this, method, true, i));
    Change.push(new BufferCreationMethod(this, method, false, i));

    setChanged();

    return true;
  }

  /**
   * Add a new parent.
   * 
   * @param parent
   *            the new parent
   */
  public void addParent(Inheritance parent)
  {
    if (parent == null)
      throw new IllegalArgumentException("parent is null");

    parents.add(parent);

    setChanged();
  }public int countStaticMethods()
  {
    int i = 0;
    for (Method m : getMethods())
      if (m.isStatic()) i++;
    
    return i;
  }
  
  public boolean isEveryMethodsStatic()
  {
    return getMethods().size() - countStaticMethods() == 0;
  }

  public LinkedList<SimpleEntity> getAllChilds()
  {
    LinkedList<SimpleEntity> allChilds = new LinkedList<SimpleEntity>();
    allChilds.add(this);

    for (Inheritance p : childs)
      allChilds.addAll(p.getChild().getAllChilds());

    return allChilds;
  }

  public LinkedList<SimpleEntity> getAllParents()
  {
    final LinkedList<SimpleEntity> allParents = new LinkedList<SimpleEntity>();
    allParents.add(this);

    for (final Inheritance p : parents)
      allParents.addAll(p.getParent().getAllParents());

    return allParents;
  }

  /**
   * Get a copy of the attribute's list.
   * 
   * @return an array containing all attributes of the entity.
   */
  public LinkedList<Attribute> getAttributes()
  {
    final LinkedList<Attribute> copy = new LinkedList<Attribute>();

    for (final Attribute a : attributes)
      copy.add(a);

    return copy;
  }
  
  /**
   * Get a copy of the method's list.
   * 
   * @return an array containing all methods of the entity.
   */
  public LinkedList<Method> getMethods()
  {
    final LinkedList<Method> copy = new LinkedList<Method>();

    for (final Method m : methods)
      copy.add(m);

    return copy;
  }

  /**
   * Get the visibility of the entity.
   * 
   * @return the visibility of the entity
   */
  public Visibility getVisibility()
  {
    return visibility;
  }

  /**
   * Return true if the entity has abstract methods; false otherwise.
   * 
   * @return true if the entity has abstract methods; false otherwise.
   */
  public boolean hasAbstractMethods()
  {
    for (final Method m : getMethods())
      if (m.isAbstract())
        return true;

    return false;
  }

  /**
   * Get the abstract state of the entity.
   * 
   * @return true if the entity is abstract; false otherwise
   */
  public boolean isAbstract()
  {
    return _isAbstract;
  }

  public boolean isChildOf(Entity entity)
  {
    boolean isChild = false;

    for (final Inheritance i : parents)
      isChild |= i.getParent().isChildOf(entity);

    return isChild || equals(entity);
  }

  public boolean isParentOf(Entity entity)
  {
    boolean isParent = false;

    for (final Inheritance i : childs)
      isParent |= i.getChild().isParentOf(entity);

    return isParent || equals(entity);
  }

  /**
   * Move the attribute's position in the array by the given offset. Offset is
   * added to the current index to compute the new index. The offset can be
   * positive or negative.
   * 
   * @param attribute
   *            the attribute to move
   * @param offset
   *            the offset for compute the new index
   */
  public void moveAttributePosition(Attribute attribute, int offset)
  {
    moveComponentPosition(attributes, attribute, offset);
  }

  /**
   * Move the method's position in the array by the given offset. Offset is
   * added to the current index to compute the new index. The offset can be
   * positive or negative.
   * 
   * @param method
   *            the method to move
   * @param offset
   *            the offset for compute the new index
   */
  public void moveMethodPosition(Method method, int offset)
  {
    moveComponentPosition(methods, method, offset);
  }

  /**
   * Remove the attribute.
   * 
   * @param attribute
   *            the attribute to remove
   * @return true if the attribute has been removed; false otherwise
   */
  public boolean removeAttribute(Attribute attribute)
  {
    if (attribute == null)
      throw new IllegalArgumentException("attribute is null");

    int i = attributes.indexOf(attribute);
    
    if (attributes.remove(attribute))
    {
      Change.push(new BufferCreationAttribute(this, attribute, false, i));
      Change.push(new BufferCreationAttribute(this, attribute, true, i));
      
      setChanged();
      return true;
    }
    else
      return false;
  }

  /**
   * Remove the child.
   * 
   * @param child
   *            the child to remove
   */
  public void removeChild(Inheritance child)
  {
    childs.remove(child);

    setChanged();
  }

  /**
   * Remove the method.
   * 
   * @param method
   *            the method to remove.
   * @return true if the method has been removed; false otherwise
   */
  public boolean removeMethod(Method method)
  {
    if (method == null)
      throw new IllegalArgumentException("method is null");

    int i = methods.indexOf(method);
    
    if (methods.remove(method))
    {
      Change.push(new BufferCreationMethod(this, method, false, i));
      Change.push(new BufferCreationMethod(this, method, true, i));
      
      setChanged();
      notifyObservers();
      return true;
    }

    return false;
  }

  /**
   * Remove the parent.
   * 
   * @param parent
   *            the parent to remove
   */
  public void removeParent(Inheritance parent)
  {
    parents.remove(parent);

    setChanged();
  }

  /**
   * Set the abstract state of the entity.
   * 
   * @param isAbstract
   *            the new abstract state.
   */
  public void setAbstract(boolean isAbstract)
  {
    if (hasAbstractMethods())
      if (SMessageDialog.showQuestionMessageYesNo("Class has abstract methods.\nDe-abstract all methods?") == JOptionPane.NO_OPTION)

        isAbstract = true;

      else
        for (final Method m : getMethods())
          if (m.isAbstract())
            m.setAbstract(false);

    Change.push(new BufferClass(this));
    _isAbstract = isAbstract;
    Change.push(new BufferClass(this));

    setChanged();
  }

  /**
   * Set the visibility of the entity.
   * 
   * @param visibility
   *            the new visibility
   */
  public void setVisibility(Visibility visibility)
  {
    if (visibility == null)
      throw new IllegalArgumentException("visibility is null");

    if (visibility.equals(getVisibility()))
      return;
    
    Change.push(new BufferClass(this));   
    this.visibility = visibility;
    Change.push(new BufferClass(this));

    setChanged();
  }

  public List<Inheritance> getChilds() {
    return childs;
  }

  public List<Inheritance> getParents() {
    return parents;
  }
  
  @Override
  public boolean isNameItalic() {
    return isAbstract();
  }
  
  @Override
  public SimpleEntity clone() throws CloneNotSupportedException {
    SimpleEntity entity = (SimpleEntity)super.clone();
    
    // Copie des attributs primitifs
    entity.setAbstract(isAbstract());
    entity.setVisibility(getVisibility());
    
    // Copie en profondeur des attributs et méthodes.
    for (Attribute a : getAttributes())
      entity.addAttribute(new Attribute(a));
    
    for (Method m : getMethods())
      entity.addMethod(new Method(m, this));
    
    return entity;
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element entity = super.getXmlElement(doc);
    
    entity.setAttribute("visibility", visibility.toString());
    entity.setAttribute("isAbstract", String.valueOf(isAbstract()));

    for (Attribute attribute : attributes)
      entity.appendChild(attribute.getXmlElement(doc));
      
    for (Method operation : methods)
      entity.appendChild(operation.getXmlElement(doc));
    
    return entity;
  }

}
