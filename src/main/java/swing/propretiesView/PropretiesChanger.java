package swing.propretiesView;

import classDiagram.IComponentsObserver;
import classDiagram.IDiagramComponent;
import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
import classDiagram.components.EnumEntity;
import classDiagram.components.InterfaceEntity;
import classDiagram.relationships.Aggregation;
import classDiagram.relationships.Association;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Composition;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Inheritance;
import classDiagram.relationships.InnerClass;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Role;
import swing.slyumCustomizedComponents.SScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * Represent a view in Slyum implementing IComponentsObserver. This view is notifyed when an UML component is selected
 * and display its propreties. It's a singleton.
 *
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class PropretiesChanger extends SScrollPane implements IComponentsObserver {
  private static PropretiesChanger instance = new PropretiesChanger();

  /**
   * Get the unique instance of the PropretiesChanger class.
   *
   * @return instance
   */
  public static PropretiesChanger getInstance() {
    return instance;
  }

  private JLabel labelNoComponent;

  /**
   * Create a new propreties view.
   */
  private PropretiesChanger() {
    setPreferredSize(new Dimension(150, 200));
    setMinimumSize(new Dimension(150, 60));
    setBackground(null);
    setBorder(null);
    getViewport().setBackground(Color.WHITE);

    labelNoComponent = new JLabel(
        "Select a component from the class diagram to change its members.");
    labelNoComponent.setHorizontalAlignment(SwingUtilities.CENTER);
    labelNoComponent.setVerticalAlignment(SwingUtilities.CENTER);
    labelNoComponent.setForeground(Color.GRAY.darker());
    setViewportView(labelNoComponent);
  }

  public void addAggregation(Aggregation component) {
    addAssociation(component);
  }

  /**
   * Add a new association to observe.
   *
   * @param association the association to observe.
   */
  public void addAssociation(Association association) {
    association.addObserver(RelationPropreties.getInstance());

    for (final Role role : association.getRoles())

      role.addObserver(RelationPropreties.getInstance());
  }

  public void addAssociationClass(AssociationClass component) {
    component.addObserver(SimpleEntityPropreties.getInstance());
  }

  public void addBinary(Binary component) {
    addAssociation(component);
  }

  public void addClassEntity(ClassEntity component) {
    component.addObserver(SimpleEntityPropreties.getInstance());
  }

  public void addEnumEntity(EnumEntity component) {
    component.addObserver(EnumEntityPropreties.getInstance());
  }

  public void addComposition(Composition component) {
    addAssociation(component);
  }

  public void addDependency(Dependency component) {
    component.addObserver(RelationPropreties.getInstance());
  }

  public void addInheritance(Inheritance component) {
    component.addObserver(InheritanceProperties.getInstance());
  }

  public void addInterfaceEntity(InterfaceEntity component) {
    component.addObserver(SimpleEntityPropreties.getInstance());
  }

  public void addMulti(Multi component) {
    addAssociation(component);
  }

  @Override
  public void setViewportView(Component view) {
    if (view == null)
      super.setViewportView(DiagramPropreties.getInstance());
    else
      super.setViewportView(view);
  }

  @Override
  public void notifyAggregationCreation(Aggregation component) {
    addAggregation(component);
  }

  @Override
  public void notifyAssociationClassCreation(AssociationClass component) {
    addAssociationClass(component);
  }

  @Override
  public void notifyBinaryCreation(Binary component) {
    addBinary(component);
  }

  @Override
  public void notifyClassEntityCreation(ClassEntity component) {
    addClassEntity(component);
  }

  @Override
  public void notifyCompositionCreation(Composition component) {
    addComposition(component);
  }

  @Override
  public void notifyDependencyCreation(Dependency component) {
    addDependency(component);
  }

  @Override
  public void notifyInheritanceCreation(Inheritance component) {
    addInheritance(component);
  }

  @Override
  public void notifyInnerClassCreation(InnerClass component) {
    // no view for InnerClass
  }

  @Override
  public void notifyInterfaceEntityCreation(InterfaceEntity component) {
    addInterfaceEntity(component);
  }

  @Override
  public void notifyEnumEntityCreation(EnumEntity component) {
    addEnumEntity(component);
  }

  @Override
  public void notifyMultiCreation(Multi component) {
    addMulti(component);
  }

  @Override
  public void notifyRemoveComponent(IDiagramComponent component) {
    // no components saving in this view
  }

}
