package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import utility.Utility;

/**
 * RelationFactory allows to create a new relation view associated with a new
 * relation UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class RelationFactory extends ComponentFactory
{
	private GraphicComponent componentMouseHover;
	protected Point mouseLocation = new Point();
	protected BasicStroke stroke = new BasicStroke(1.2f);

	public RelationFactory(GraphicView parent)
	{
		super(parent);

		componentMouseHover = parent;
	}

	/**
	 * Change style of component were mouse pressed.
	 * 
	 * @param e
	 *            mouse event
	 */
	private void drawComponentMouseHoverStyle(MouseEvent e)
	{
		final GraphicComponent currentComponent = parent.getComponentAtPosition(e.getPoint());

		if (componentMouseHover != currentComponent)
		{
			currentComponent.setMouseHoverStyle();

			if (componentMouseHover != componentMousePressed)
				componentMouseHover.setDefaultStyle();

			componentMouseHover = currentComponent;
		}
	}

	/**
	 * Draw the extremity at the location of the mouse.
	 * 
	 * @param g2
	 *            the graphic context
	 */
	protected void drawExtremity(Graphics2D g2)
	{
		// no extremity
	}

	/**
	 * Draw a line between the mouse location and the location where mouse was
	 * pressed.
	 * 
	 * @param g2
	 *            the graphic context
	 */
	private void drawLine(Graphics2D g2)
	{
		final int gray = Utility.getColorGrayLevel(parent.getColor());

		g2.setStroke(stroke);

		g2.setColor(new Color(gray, gray, gray, 200));
		g2.drawLine(mousePressed.x, mousePressed.y, mouseLocation.x, mouseLocation.y);
	}

	@Override
	public Cursor getCursor()
	{
		return new Cursor(Cursor.CROSSHAIR_CURSOR);
	}

	@Override
	public void gMouseDragged(MouseEvent e)
	{
		Rectangle repaintBounds = new Rectangle(mousePressed.x, mousePressed.y, mouseLocation.x - mousePressed.x, mouseLocation.y - mousePressed.y);

		Rectangle repaintExtremity = new Rectangle(mouseLocation.x - 50, mouseLocation.y - 50, 100, 100); // 100
																											// take
																											// arbitrary

		mouseLocation = e.getPoint();

		repaintBounds = Utility.normalizeRect(repaintBounds);
		parent.getScene().repaint(repaintBounds);
		parent.getScene().repaint(repaintExtremity);

		repaintExtremity = new Rectangle(mousePressed.x - 50, mousePressed.y - 50, 100, 100); // 100
																								// take
																								// arbitrary
		parent.getScene().repaint(repaintExtremity);

		repaintBounds = new Rectangle(mousePressed.x, mousePressed.y, mouseLocation.x - mousePressed.x, mouseLocation.y - mousePressed.y);

		repaintExtremity = new Rectangle(mouseLocation.x, mouseLocation.y, 100, 100);

		parent.getScene().repaint(repaintExtremity);
		parent.getScene().repaint(Utility.normalizeRect(repaintBounds));

		drawComponentMouseHoverStyle(e);
	}

	@Override
	public void gMouseMoved(MouseEvent e)
	{
		mouseLocation = e.getPoint();
		drawComponentMouseHoverStyle(e);
	}

	@Override
	public void gMousePressed(MouseEvent e)
	{
		super.gMousePressed(e);

		componentMouseHover.setStyleClicked();
	}

	@Override
	public void gMouseReleased(MouseEvent e)
	{
		super.gMouseReleased(e);

		componentMousePressed.setDefaultStyle();
		componentMouseReleased.setDefaultStyle();
	}

	@Override
	public void paintComponent(Graphics2D g2)
	{
		if (componentMousePressed == null)
			return;

		drawLine(g2);
		drawExtremity(g2);
	}
	
	@Override
	public GraphicComponent create()
	{
		return null;
	}

	@Override
	public void repaint()
	{
		if (mousePressed == null || mouseReleased == null)
			return;

		Rectangle repaintBounds = new Rectangle(mousePressed.x, mousePressed.y, mouseReleased.x - mousePressed.x, mouseReleased.y - mousePressed.y);

		repaintBounds = Utility.normalizeRect(repaintBounds);
		parent.getScene().repaint(repaintBounds);
	}

}
