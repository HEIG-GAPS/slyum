package classDiagram;

import change.Change;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import swing.XmlElement;
import utility.Utility;
import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.EnumEntity;
import classDiagram.components.InterfaceEntity;
import classDiagram.relationships.Aggregation;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Composition;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Inheritance;
import classDiagram.relationships.InnerClass;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Relation;
import java.util.Observable;

/**
 * This class contains all structurals UML components. Add classes, interfaces,
 * associations, inheritances, dependecies from here. It implements
 * IComponentObserver and notify all listeners when a new UML components is
 * added, removed or modified.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 * 
 */
public class ClassDiagram extends Observable 
                          implements XmlElement, INameObserver {
  private static int currentID = 0;

  public static int getNextId() {
    return ++currentID;
  }

  private String name = "";
  LinkedList<IDiagramComponent> components = new LinkedList<>();
  LinkedList<Entity> entities = new LinkedList<>();
  LinkedList<IComponentsObserver> observers = new LinkedList<>();

  public int countComponents(Class<?> type) {
    return Utility.count(type, components);
  }

  public void addAggregation(Aggregation component) {
    for (final IComponentsObserver c : observers)
      c.notifyAggregationCreation(component);
    addComponent(component);
  }

  public void addAssociationClass(AssociationClass component) {
    for (final IComponentsObserver c : observers)
      c.notifyAssociationClassCreation(component);

    addComponent(component);
    entities.addFirst(component);
  }

  public void addBinary(Binary component) {
    for (final IComponentsObserver c : observers)
      c.notifyBinaryCreation(component);

    addComponent(component);
  }

  public void addClassEntity(ClassEntity component) {
    for (final IComponentsObserver c : observers)
      c.notifyClassEntityCreation(component);
    addComponent(component);
    entities.addFirst(component);
  }

  /**
   * Add a new in class diagram. /!\ Does not notify listners.
   * 
   * @param component
   *          the new component.
   * @return true if the component has been added; false otherwise
   */
  private boolean addComponent(IDiagramComponent component) {
    if (component.getId() > currentID) 
      setCurrentId(component.getId() + 1);

    if (!components.contains(component)) {
      components.addFirst(component);
      return true;
    }

    return false;
  }

  /**
   * Add a new observer who will be notified when the class diagram changed.
   * 
   * @param c
   *          the new obserer.
   * @return true if the observer has been added; false otherwise.
   */
  public boolean addComponentsObserver(IComponentsObserver c) {
    return observers.add(c);
  }

  public void addComposition(Composition component) {
    for (final IComponentsObserver c : observers)
      c.notifyCompositionCreation(component);

    addComponent(component);
  }

  public void addDependency(Dependency component) {
    for (final IComponentsObserver c : observers)
      c.notifyDependencyCreation(component);

    addComponent(component);
  }

  public void addInheritance(Inheritance component) {
    for (final IComponentsObserver c : observers)
      c.notifyInheritanceCreation(component);

    addComponent(component);
  }

  public void addInnerClass(InnerClass component) {

    for (final IComponentsObserver c : observers)
      c.notifyInnerClassCreation(component);

    addComponent(component);
  }

  public void addInterfaceEntity(InterfaceEntity component) {
    for (final IComponentsObserver c : observers)
      c.notifyInterfaceEntityCreation(component);

    addComponent(component);
    entities.addFirst(component);
  }

  public void addEnumEntity(EnumEntity component) {
    for (final IComponentsObserver c : observers)
      c.notifyEnumEntityCreation(component);

    addComponent(component);
    entities.addFirst(component);
  }

  public void addMulti(Multi component) {
    if (components.contains(component)) return;

    for (final IComponentsObserver c : observers)
      c.notifyMultiCreation(component);

    addComponent(component);
  }

  public void changeZOrder(Entity entity, int index) {
    if (index < 0 || index >= entities.size()) return;

    // Change.push(new BufferZOrder(entity, entities.indexOf(entity)));

    entities.remove(entity);
    entities.add(index, entity);

    // Change.push(new BufferZOrder(entity, index));

    for (final IComponentsObserver c : observers)
      c.notifyChangeZOrder(entity, index);
  }

  /**
   * Return a copy of the array containing all class diagram elements.
   * 
   * @return a copy of the array containing all class diagram elements
   */
  @SuppressWarnings("unchecked")
  public LinkedList<IDiagramComponent> getComponents() {
    return (LinkedList<IDiagramComponent>) components.clone();
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getComponentsByType(Class<T> type) {
    LinkedList<T> filteredList = new LinkedList<>();
    for (IDiagramComponent c : components)
      if (c.getClass().equals(type)) filteredList.add((T) c);
    return filteredList;
  }

  /**
   * Get the name of class diagram.
   * 
   * @return the name of class diagram
   */
  public String getName() {
    return name;
  }
  
  public LinkedList<Relation> getAllRelations() {
    LinkedList<Relation> relations = new LinkedList<>();
    for (IDiagramComponent component : components)
      if (component instanceof Relation)
        relations.add((Relation)component);
    return relations;
  }

  /**
   * Remove all components in class diagram.
   */
  public void removeAll() {
    while (components.size() > 0)

      removeComponent(components.get(0));
  }

  public void removeComponent(IDiagramComponent component) {
    components.remove(component);

    // Optimizes this (create more array for specific elements, not just an
    // array for all components.
    if (component instanceof Entity) entities.remove(component);

    for (final IComponentsObserver c : observers)
      c.notifyRemoveComponent(component);
  }

  /**
   * remove the given IComponentsObserver from the list of observers.
   * 
   * @param c
   *          the IComponentsObserver to remove
   * @return true if IComponentsObserver has been removed; else otherwise.
   */
  public boolean removeComponentsObserver(IComponentsObserver c) {
    return observers.remove(c);
  }

  /**
   * Search the IDiagramComponent corresponding to the given id. Return null if
   * no component are found.
   * 
   * @param id
   *          id of the component to search
   * @return the component corresponding to the given id, or null if no
   *         component are found.
   */
  public IDiagramComponent searchComponentById(int id) {
    for (final IDiagramComponent c : components)

      if (c.getId() == id)

      return c;

    return null;
  }

  /**
   * Set the current id.
   * 
   * @param id
   *          the new id to set
   */
  public void setCurrentId(int id) {
    currentID = id;
  }

  /**
   * Set the name of class diagram.
   * 
   * @param name
   *          the new name of class diagram.
   */
  public void setName(String name) {
    if (name == null)
      name = "";
    
    if (this.name.equals(name))
      return;
    
    this.name = name;
    Change.setHasChange(true);
    setChanged();
  }

  @Override
  public String getXmlTagName() {
    return "diagramElements";
  }

  @Override
  public Element getXmlElement(Document doc) {

    Element classDiagram = doc.createElement(getXmlTagName());
    classDiagram.setAttribute("name", getName());
    for (IDiagramComponent component : components)
      classDiagram.appendChild(component.getXmlElement(doc));

    return classDiagram;
  }
}
