package swing.propretiesView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import classDiagram.IComponentsObserver;
import classDiagram.IDiagramComponent;
import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
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

/**
 * Represent a view in Slyum implementing IComponentsObserver. This view is
 * notifyed when an UML component is selected and display its propreties. It's a
 * singleton.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class PropretiesChanger extends JScrollPane implements IComponentsObserver {
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

  @Override
  public void addAggregation(Aggregation component) {
    addAssociation(component);
  }

  /**
   * Add a new association to observe.
   * 
   * @param association
   *          the association to observe.
   */
  public void addAssociation(Association association) {
    association.addObserver(RelationPropreties.getInstance());

    for (final Role role : association.getRoles())

      role.addObserver(RelationPropreties.getInstance());
  }

  @Override
  public void addAssociationClass(AssociationClass component) {
    component.addObserver(SimpleEntityPropreties.getInstance());
  }

  @Override
  public void addBinary(Binary component) {
    addAssociation(component);
  }

  @Override
  public void addClassEntity(ClassEntity component) {
    component.addObserver(SimpleEntityPropreties.getInstance());
  }

  @Override
  public void addEnumEntity(EnumEntity component) {
    component.addObserver(EnumEntityPropreties.getInstance());
  }

  @Override
  public void addComposition(Composition component) {
    addAssociation(component);
  }

  @Override
  public void addDependency(Dependency component) {
    component.addObserver(RelationPropreties.getInstance());
  }

  @Override
  public void addInheritance(Inheritance component) {
    component.addObserver(InheritanceProperties.getInstance());
  }

  @Override
  public void addInnerClass(InnerClass component) {
    // no view for InnerClass
  }

  @Override
  public void addInterfaceEntity(InterfaceEntity component) {
    component.addObserver(SimpleEntityPropreties.getInstance());
  }

  @Override
  public void addMulti(Multi component) {
    addAssociation(component);
  }

  @Override
  public void changeZOrder(Entity entity, int index) {
    // Nothing to do...
  }

  @Override
  public void removeComponent(IDiagramComponent component) {
    // no components saving in this view
  }

  @Override
  public void setViewportView(Component view) {
    if (view == null)
      super.setViewportView(labelNoComponent);
    else
      super.setViewportView(view);
  }
}
