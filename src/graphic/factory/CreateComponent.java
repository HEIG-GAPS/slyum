package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;

import classDiagram.ClassDiagram;

/**
 * CreateComponent allows to create a new graphic component view. Give this
 * factory at the graphic view using the method initNewComponent() for
 * initialize a new factory. Next, graphic view will use the factory to allow
 * creation of a new component, according to the specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class CreateComponent extends GraphicComponent
{
	protected ClassDiagram classDiagram;

	public CreateComponent(GraphicView parent)
	{
		super(parent);

		this.classDiagram = parent.getClassDiagram();
	}

	/**
	 * Create an instance of the class with informations collected during
	 * life-cycle. Return null if informations collected are not suffisant to
	 * create a class.
	 * 
	 * @return the new class created or null.
	 */
	public abstract GraphicComponent create();

	public void deleteFactory()
	{
		repaint();
	}

	@Override
	public Rectangle getBounds()
	{
		return null;
	}

	public Cursor getCursor()
	{
		return Cursor.getDefaultCursor();
	}

	@Override
	public boolean isAtPosition(Point mouse)
	{
		return false;
	}

	@Override
	public void setBounds(Rectangle bounds)
	{
	}

}
