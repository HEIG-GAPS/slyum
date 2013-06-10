package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;

import java.awt.Point;
import java.awt.event.MouseEvent;

import swing.Slyum;

/**
 * ComponentFactory allows to create a new component view associated with a new
 * component UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class ComponentFactory extends CreateComponent
{
	protected GraphicComponent componentMousePressed;

	protected GraphicComponent componentMouseReleased;
	protected Point mousePressed;
	protected Point mouseReleased;

	public ComponentFactory(GraphicView parent)
	{
		super(parent);
	}

	@Override
	public void gMousePressed(MouseEvent e)
	{
		mousePressed = e.getPoint();
		componentMousePressed = parent.getComponentAtPosition(mousePressed);
	}

	@Override
	public void gMouseReleased(MouseEvent e)
	{
		mouseReleased = e.getPoint();

		componentMouseReleased = parent.getComponentAtPosition(mouseReleased);

		if (create() == null && Slyum.isShowErrorMessage())
				creationFailed();

		parent.deleteCurrentFactory();
	}
	
	/**
	 * Called when the creation of the component failed.
	 */
	protected void creationFailed()
	{
		// Nothing by default.
	}
}
