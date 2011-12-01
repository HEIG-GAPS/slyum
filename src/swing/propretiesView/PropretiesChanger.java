package swing.propretiesView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import utility.PersonalizedIcon;
import classDiagram.IComponentsObserver;
import classDiagram.IDiagramComponent;
import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
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
@SuppressWarnings("serial")
public class PropretiesChanger extends JScrollPane implements IComponentsObserver
{
	private static PropretiesChanger instance = new PropretiesChanger();

	/**
	 * Get the unique isntance of the PropretiesChanger class.
	 * 
	 * @return
	 */
	public static PropretiesChanger getInstance()
	{
		return instance;
	}

	private final JLabel noComponentLabel;

	/**
	 * Create a new propreties view.
	 */
	private PropretiesChanger()
	{
		setPreferredSize(new Dimension(150, 200));
		setMinimumSize(new Dimension(150, 200));

		noComponentLabel = new JLabel("No component selected", PersonalizedIcon.createImageIcon("resources/icon/loupe.png"), SwingConstants.CENTER);
		noComponentLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		noComponentLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		noComponentLabel.setOpaque(false);
		getViewport().setBackground(Color.WHITE);

		setViewportView(noComponentLabel);
	}

	@Override
	public void addAggregation(Aggregation component)
	{
		addAssociation(component);
	}

	/**
	 * Add a new association to observe.
	 * 
	 * @param association
	 *            the association to observe.
	 */
	public void addAssociation(Association association)
	{
		association.addObserver(RelationPropreties.getInstance());

		for (final Role role : association.getRoles())

			role.addObserver(RelationPropreties.getInstance());
	}

	@Override
	public void addAssociationClass(AssociationClass component)
	{
		component.addObserver(EntityPropreties.getInstance());
	}

	@Override
	public void addBinary(Binary component)
	{
		addAssociation(component);
	}

	@Override
	public void addClass(ClassEntity component)
	{
		component.addObserver(EntityPropreties.getInstance());
	}

	@Override
	public void addComposition(Composition component)
	{
		addAssociation(component);
	}

	@Override
	public void addDependency(Dependency component)
	{
		component.addObserver(RelationPropreties.getInstance());
	}

	@Override
	public void addInheritance(Inheritance component)
	{
		// no view for inheritance
	}

	@Override
	public void addInnerClass(InnerClass component)
	{
		// no view for InnerClass
	}

	@Override
	public void addInterface(InterfaceEntity component)
	{
		component.addObserver(EntityPropreties.getInstance());
	}

	@Override
	public void addMulti(Multi component)
	{
		addAssociation(component);
	}

	@Override
	public void changeZOrder(Entity entity, int index)
	{
		// Nothing to do...

	}

	@Override
	public void removeComponent(IDiagramComponent component)
	{
		// no save of component in this view
	}

	@Override
	public void setViewportView(Component view)
	{
		if (view == null)
			view = noComponentLabel;

		super.setViewportView(view);
	}
}
