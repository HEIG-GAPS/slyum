package graphic.entity;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.SquareGrip;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import change.Change;

/**
 * Represent a grip (gray square) using for resizing entities. A grip entity
 * move with mouse cursor when user click on it. Calls move() and apply()
 * methods for entity associated with. But that class must be subclassed for act
 * on an entity. By default it does nothing.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class GripEntity extends SquareGrip
{
	public GripEntity(GraphicView parent, int size, int cursor)
	{
		super(parent, size, cursor);
	}

	@Override
	public void gMouseDragged(MouseEvent e)
	{
		move(e);
	}

	@Override
	public void gMousePressed(MouseEvent e)
	{
		for (final GraphicComponent c : parent.getSelectedComponents())
			c.saveMouseLocation(e);
	}

	@Override
	public void gMouseReleased(MouseEvent e)
	{
		final Rectangle repaintBounds = new Rectangle(getBounds());
		repaintBounds.grow(10, 10);

		boolean isRecord = Change.isRecord();
		Change.record();
		
		for (final GraphicComponent c : parent.getSelectedComponents())
			
			c.apply(e);
		
		if (!isRecord)
			Change.stopRecord();

		parent.getScene().repaint(repaintBounds);
	}

	@Override
	public abstract void move(MouseEvent e);
}
