package classDiagram;

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

/**
 * Interface implemented by all listeners of class diagram. When the class
 * diagram add, remove or change a new component, it notify all listeners with
 * the specified method.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public interface IComponentsObserver {
  /**
   * Adds a new aggregation and notify that a new aggregation has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addAggregation(Aggregation component);

  /**
   * Adds a new association class and notify that a new assocation class has
   * been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addAssociationClass(AssociationClass component);

  /**
   * Adds a new binary and notify that a new binary has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addBinary(Binary component);

  /**
   * Adds a new class and notify that a new class has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addClassEntity(ClassEntity component);

  /**
   * Adds a new composition and notify that a new composition has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addComposition(Composition component);

  /**
   * Adds a new dependency and notify that a new dependency has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addDependency(Dependency component);

  /**
   * Adds a new inheritance and notify that a new inheritance has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addInheritance(Inheritance component);

  public void addInnerClass(InnerClass component);

  /**
   * Adds a new interface and notify that a new interface has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addInterfaceEntity(InterfaceEntity component);

  public void addEnumEntity(EnumEntity component);

  /**
   * Adds a new multi-association and notify that a new multi-association has
   * been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void addMulti(Multi component);

  /**
   * Changes the index of entity in the array and notifiy observers.
   * 
   * @param entity
   *          the entity to move.
   * @param index
   *          the index to move the entity.
   */
  public void changeZOrder(Entity entity, int index);

  /**
   * Removes the given component and notify that this component has been
   * removed.
   * 
   * @param component
   *          the component to remove.
   */
  public void removeComponent(IDiagramComponent component);
}
