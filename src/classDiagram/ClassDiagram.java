package classDiagram;

import change.Change;
import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.EnumEntity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;
import classDiagram.relationships.Aggregation;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Composition;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Inheritance;
import classDiagram.relationships.InnerClass;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Relation;
import graphic.GraphicView;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.XmlElement;
import utility.Utility;

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

  LinkedList<IDiagramComponent> components = new LinkedList<>();
  LinkedList<Entity> entities = new LinkedList<>();
  LinkedList<IComponentsObserver> observers = new LinkedList<>();
  private boolean defaultViewEnum;
  private Method.ParametersViewStyle defaultViewMethods;
  private String name = "";
  private String informations = "";
  private ViewEntity viewEntity;
  private boolean visibleType;

  public ClassDiagram() {
    initDefaultAttributes();
  }
  
  public void addAggregation(Aggregation component) {
    addAggregation(component, true);
  }
  
  public void addAggregation(Aggregation component, boolean notifyGraphicView) {
    
    if (addComponent(component))
      for (final IComponentsObserver c : observers)
        if (notifyGraphicView || !(c instanceof GraphicView))
          c.notifyAggregationCreation(component);    
  }

  public void addAssociationClass(AssociationClass component) {
    addAssociationClass(component, true);
  }
  
  public void addAssociationClass(AssociationClass component, boolean notifyGraphicView) {
    
    if (addComponent(component)) {
      
      for (final IComponentsObserver c : observers)
        if (notifyGraphicView || !(c instanceof GraphicView))
          c.notifyAssociationClassCreation(component);

      entities.addFirst(component);
    }
  }

  public void addBinary(Binary component) {
    
    addBinary(component, true);
  }

  public void addBinary(Binary component, boolean notifyGraphicView) {
    
    if (addComponent(component))
      for (final IComponentsObserver c : observers)
        if (notifyGraphicView || !(c instanceof GraphicView))
          c.notifyBinaryCreation(component);
  }

  public void addClassEntity(ClassEntity component) {
    
    if (addComponent(component)) {
      for (final IComponentsObserver c : observers)
        c.notifyClassEntityCreation(component);
      entities.addFirst(component);
    }
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
    addComposition(component, true);
  }

  public void addComposition(Composition component, boolean notifyGraphicView) {
    
    if (addComponent(component))
      for (final IComponentsObserver c : observers)
        if (notifyGraphicView || !(c instanceof GraphicView))
          c.notifyCompositionCreation(component);
  }
  
  public void addDependency(Dependency component) {
    addDependency(component, true);
  }

  public void addDependency(Dependency component, boolean notifyGraphicView) {
    
    if (addComponent(component))
      for (final IComponentsObserver c : observers)
        if (notifyGraphicView || !(c instanceof GraphicView))
          c.notifyDependencyCreation(component);
  }

  public void addEnumEntity(EnumEntity component) {
    
    if (addComponent(component)) {
      for (final IComponentsObserver c : observers)
        c.notifyEnumEntityCreation(component);

      entities.addFirst(component);
    }
  }
  
  public void addInheritance(Inheritance component) {
    addInheritance(component, true);
  }

  public void addInheritance(Inheritance component, boolean notifyGraphicView) {
    
    if (addComponent(component))
      for (final IComponentsObserver c : observers)
        if (notifyGraphicView || !(c instanceof GraphicView))
          c.notifyInheritanceCreation(component);
  }
  
  public void addInnerClass(InnerClass component) {
    addInnerClass(component, true);
  }

  public void addInnerClass(InnerClass component, boolean notifyGraphicView) {

    if (addComponent(component))
      for (final IComponentsObserver c : observers)
        if (notifyGraphicView || !(c instanceof GraphicView))
          c.notifyInnerClassCreation(component);
  }

  public void addInterfaceEntity(InterfaceEntity component) {
    
    if (addComponent(component)) {
      for (final IComponentsObserver c : observers)
        c.notifyInterfaceEntityCreation(component);

      entities.addFirst(component);
    }
  }
  
  public void addMulti(Multi component) {
    addMulti(component, true);
  }

  public void addMulti(Multi component, boolean notifyGraphicView) {
    if (components.contains(component)) return;

    for (final IComponentsObserver c : observers)
      if (notifyGraphicView || !(c instanceof GraphicView))
        c.notifyMultiCreation(component);

    addComponent(component);
  }

  public void clean() {
    removeAll();
    initDefaultAttributes();
    setChanged();
  }
  
  public int countComponents(Class<?> type) {
    return Utility.count(type, components);
  }
  
  public LinkedList<IDiagramComponent> getAllMainsComponents() {
    LinkedList<IDiagramComponent> results = new LinkedList<>();
    results.addAll(getRelations());
    results.addAll(entities);
    return results;
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
   * Set the current id.
   *
   * @param id
   *          the new id to set
   */
  public void setCurrentId(int id) {
    currentID = id;
  }
  
  public ViewEntity getDefaultViewEntities() {
    return viewEntity;
  }

  public boolean getDefaultViewEnum() {
    return defaultViewEnum;
  }

  public void setDefaultViewEnum(boolean defaultViewEnum) {
    this.defaultViewEnum = defaultViewEnum;
    setChanged();
    Change.setHasChange(true);
  }

  public Method.ParametersViewStyle getDefaultViewMethods() {
    return defaultViewMethods;
  }

  public void setDefaultViewMethods(Method.ParametersViewStyle defaultViewMethods) {
    this.defaultViewMethods = defaultViewMethods;
    setChanged();
    Change.setHasChange(true);
  }

  public boolean getDefaultVisibleTypes() {
    return visibleType;
  }
  
  public LinkedList<Entity> getEntities() {
    return (LinkedList<Entity>) entities.clone();
  }
  
  /**
   * Get the name of class diagram.
   * 
   * @return the name of class diagram
   */
  @Override
  public String getName() {
    return name;
  }
  
  public String getInformations() {
    return informations;
  }
  
  public void setInformation(String informations) {
    this.informations = informations;
  }

  /**
   * Set the name of class diagram.
   *
   * @param name
   *          the new name of class diagram.
   */
  @Override
  public void setName(String name) {
    if (name == null)
      name = "";
    
    if (this.name.equals(name))
      return;
    
    this.name = name;
    Change.setHasChange(true);
    setChanged();
  }

  public LinkedList<Relation> getRelations() {
    LinkedList<Relation> results = new LinkedList<>();
    for (IDiagramComponent component : components)
      if (component instanceof Relation)
        results.add((Relation)component);
    return results;
  }

  public void setViewEntity(ViewEntity viewEntity) {
    this.viewEntity = viewEntity;
    setChanged();
    Change.setHasChange(true);
  }

  public void setVisibleType(boolean visibleType) {
    this.visibleType = visibleType;
    setChanged();
    Change.setHasChange(true);
  }
  
  @Override
  public Element getXmlElement(Document doc) {
    
    Element classDiagram = doc.createElement(getXmlTagName());
    
    //Attributs
    classDiagram.setAttribute("name", getName());
    classDiagram.setAttribute("informations", getInformations());
    classDiagram.setAttribute("defaultViewEntities", getDefaultViewEntities().name());
    classDiagram.setAttribute("defaultViewMethods", getDefaultViewMethods().name());
    classDiagram.setAttribute("defaultViewEnum", String.valueOf(getDefaultViewEnum()));
    classDiagram.setAttribute("defaultVisibleTypes", String.valueOf(getDefaultVisibleTypes()));
    
    // Components
    for (IDiagramComponent component : components)
      classDiagram.appendChild(component.getXmlElement(doc));

    return classDiagram;
  }
  
  
  @Override
  public String getXmlTagName() {
    return "diagramElements";
  }

  /**
   * Remove all components in class diagram.
   */
  public void removeAll() {
    while (components.size() > 0)
      removeComponent(components.get(0));
  }
  
  public void removeComponent(IDiagramComponent component) {
    if (!components.contains(component))
      return;
    
    components.remove(component);

    // Optimizes this (create more array for specific elements, not just an
    // array for all components.
    if (component instanceof Entity) 
      entities.remove((Entity)component);

    observers.stream().forEach(c -> c.notifyRemoveComponent(component));
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

  private void initDefaultAttributes() {
    setViewEntity(GraphicView.getDefaultViewEntities());
    setDefaultViewMethods(GraphicView.getDefaultViewMethods());
    setDefaultViewEnum(GraphicView.getDefaultViewEnum());
    setVisibleType(GraphicView.getDefaultVisibleTypes());
  }

  public enum ViewEntity {

    ALL, ONLY_ATTRIBUTES, ONLY_METHODS, NOTHING;

    @Override
    public String toString() {
      return super.toString().charAt(0)
              + super.toString().substring(1).toLowerCase().replace('_', ' ');
    }
  }
}
