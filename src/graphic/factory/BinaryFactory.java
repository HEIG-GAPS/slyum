package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.ClassView;
import graphic.entity.EntityView;
import graphic.relations.BinaryView;
import graphic.relations.MultiLineView;
import graphic.relations.MultiView;

import java.awt.Point;
import java.awt.Rectangle;

import utility.SMessageDialog;

import classDiagram.ClassDiagram;
import classDiagram.components.ClassEntity;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Role;

/**
 * BinaryFactory allows to create a new binary view associated with a new
 * association UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class BinaryFactory extends RelationFactory
{
	public final String ERROR_CREATION_MESSAGE = "Association creation failed.\nYou must make a bond between two entities (class or interface).";

	/**
	 * Create a new factory allowing the creation of a binary.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param classDiagram
	 *            the class diagram
	 */
	public BinaryFactory(GraphicView parent, ClassDiagram classDiagram)
	{
		super(parent, classDiagram);
	}

	@Override
	public GraphicComponent create()
	{
		if (componentMousePressed instanceof EntityView && componentMouseReleased instanceof EntityView)
		{
			final EntityView source = (EntityView) componentMousePressed;
			final EntityView target = (EntityView) componentMouseReleased;

			final Binary binary = new Binary(source.getComponent(), target.getComponent(), false);

			final BinaryView b = new BinaryView(parent, source, target, binary, mousePressed, mouseReleased, true);

			parent.addLineView(b);
			classDiagram.addBinary(binary);

			parent.clearAllSelectedComponents();
			b.setSelected(true);

			return b;
		}
		else
		{
			final MultiView multiView;
			final ClassView classView;

			if (componentMousePressed.getClass() == MultiView.class && componentMouseReleased instanceof ClassView)
			{
				multiView = (MultiView) componentMousePressed;
				classView = (ClassView) componentMouseReleased;
			}
			else if (componentMouseReleased.getClass() == MultiView.class && componentMousePressed instanceof ClassView)
			{
				multiView = (MultiView) componentMouseReleased;
				classView = (ClassView) componentMousePressed;
			}
			else
			{
				repaint();
				return null;
			}

			final Multi multi = (Multi) multiView.getAssociedComponent();
			final Role role = new Role(multi, (ClassEntity) classView.getAssociedComponent(), "");

			Rectangle bounds = multiView.getBounds();
			final Point multiPos = new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());
			bounds = classView.getBounds();
			final Point classPos = new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());

			final MultiLineView mlv = new MultiLineView(parent, multiView, classView, role, multiPos, classPos, false);
			multiView.addMultiLineView(mlv);
		}

		repaint();
		return null;
	}
	
	@Override
	protected void creationFailed()
	{
		SMessageDialog.showErrorMessage(ERROR_CREATION_MESSAGE);
	}
}
