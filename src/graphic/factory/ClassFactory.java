package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.ClassView;
import graphic.entity.EntityView;

import java.awt.Rectangle;

import change.BufferBounds;
import change.BufferCreation;
import change.Change;
import classDiagram.ClassDiagram;
import classDiagram.components.ClassEntity;
import classDiagram.components.Visibility;

/**
 * ClassFactory allows to create a new class view associated with a new class
 * UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class ClassFactory extends EntityFactory
{

	/**
	 * Create a new factory allowing the creation of a class.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param classDiagram
	 *            the class diagram
	 */
	public ClassFactory(GraphicView parent, ClassDiagram classDiagram)
	{
		super(parent, classDiagram);
	}

	@Override
	public GraphicComponent create()
	{
		final ClassEntity classEntity = new ClassEntity("Class", Visibility.PUBLIC);
		final EntityView c = new ClassView(parent, classEntity);

		c.setBounds(new Rectangle(mouseReleased.x - DEFAULT_SIZE.width / 2, mouseReleased.y - DEFAULT_SIZE.height / 2, DEFAULT_SIZE.width, DEFAULT_SIZE.height));

		parent.addEntity(c);
		classDiagram.addClass(classEntity);
		return c;
	}

}
