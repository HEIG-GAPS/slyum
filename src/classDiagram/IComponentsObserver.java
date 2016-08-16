package classDiagram;

import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
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
  public void notifyAggregationCreation(Aggregation component);

  /**
   * Adds a new association class and notify that a new assocation class has
   * been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void notifyAssociationClassCreation(AssociationClass component);

  /**
   * Adds a new binary and notify that a new binary has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void notifyBinaryCreation(Binary component);

  /**
   * Adds a new class and notify that a new class has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void notifyClassEntityCreation(ClassEntity component);

  /**
   * Adds a new composition and notify that a new composition has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void notifyCompositionCreation(Composition component);

  /**
   * Adds a new dependency and notify that a new dependency has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void notifyDependencyCreation(Dependency component);

  /**
   * Adds a new inheritance and notify that a new inheritance has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void notifyInheritanceCreation(Inheritance component);

  public void notifyInnerClassCreation(InnerClass component);

  /**
   * Adds a new interface and notify that a new interface has been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void notifyInterfaceEntityCreation(InterfaceEntity component);

  public void notifyEnumEntityCreation(EnumEntity component);

  /**
   * Adds a new multi-association and notify that a new multi-association has
   * been added.
   * 
   * @param component
   *          the component that was added.
   */
  public void notifyMultiCreation(Multi component);

  /**
   * Removes the given component and notify that this component has been
   * removed.
   * 
   * @param component
   *          the component to remove.
   */
  public void notifyRemoveComponent(IDiagramComponent component);
}
