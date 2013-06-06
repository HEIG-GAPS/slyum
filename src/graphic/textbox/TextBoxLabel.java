package graphic.textbox;

import graphic.GraphicView;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import change.BufferDeplacement;
import change.Change;
import change.Changeable;


/**
 * A TextBox is a graphic component from Slyum containing a String. The
 * particularity of a TextBox is it text can be changed by double-clinking on
 * it.
 * 
 * A TextBoxLabel add the possibility to be moved by user in dragging mouse on
 * it. The TextBoxLabel's position is relative to it's anchor. When the anchor
 * moves, this label moves too to keep the dimension of the anchor always the
 * same.
 * 
 * A TextBox label have an associated graphic component and draw a line between
 * the label and the component when mouse hover the label. Points of the line is
 * compute in this way:
 * 
 * first point : it's the middle bounds of the label second point : it's compute
 * by calling the computeAnchor() abstract method.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class TextBoxLabel extends TextBox implements Observer {
	private Point mousePosition = new Point();
	private Cursor previousCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  protected Point deplacement = new Point();

	public TextBoxLabel(GraphicView parent, String text) {
		super(parent, text);
	}

	/**
	 * Compute the second point of the line drawn between the label and the
	 * component associated.
	 * 
	 * @return the point to draw a line
	 */
	protected abstract Point computeAnchor();

	/**
	 * This method compute the new location of the label according to its
	 * associated component. The location of the label is relative to its
	 * component. If, for exemple, the label is x = 10 and y = 20 compared to
	 * its component and this component moved, the label will compute it's
	 * location in regard to the new component bounds but always with a movement
	 * of x = 10 and y = 20.
	 */
	protected void computeLabelPosition()
	{
		Rectangle repaintBounds = getBounds();
		Point pos = computeAnchor();

		setBounds(
		    new Rectangle(pos.x + deplacement.x, pos.y + deplacement.y, 0, 0));

		parent.getScene().repaint(repaintBounds);
		repaint();
	}

	@Override
	public void createEffectivFont()
	{
		effectivFont = getFont().deriveFont(14.0f);
	}

	@Override
	public void gMouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
			editing();
	}

	@Override
	public void gMouseDragged(MouseEvent e) {
		parent.getScene().setCursor(new Cursor(Cursor.MOVE_CURSOR));
		Point mouse = e.getPoint();
		Rectangle bounds = getBounds();
		Rectangle repainRectangle = getBounds();
		repainRectangle.add(computeAnchor());

		setBounds(new Rectangle(bounds.x + mouse.x - mousePosition.x,
		                        bounds.y + mouse.y - mousePosition.y, 0, 0));

		parent.getScene().repaint(repainRectangle);
		repaint();

		mousePosition = e.getPoint();
		setChanged();
		notifyObservers();
	}

	@Override
	public void gMousePressed(MouseEvent e) {
		super.gMousePressed(e);
		mousePosition = e.getPoint();
		previousCursor = parent.getScene().getCursor();
		Change.push(new BufferDeplacement(this));
	}
	
	@Override
	public void gMouseReleased(MouseEvent e) {
		Point pos = computeAnchor();
		deplacement = new Point(bounds.x - pos.x, bounds.y - pos.y);
		parent.getScene().setCursor(previousCursor);
		
		Changeable c = Change.getLast();
		if (c instanceof BufferDeplacement && 
		    !((BufferDeplacement)Change.getLast()).getDeplacement()
		                               .equals(getDeplacement()))
			Change.push(new BufferDeplacement(this));
		else
			Change.pop();
	}
	
	public Point getDeplacement() {
		return deplacement;
	}

	@Override
	public void paintComponent(Graphics2D g2) {
		super.paintComponent(g2);
		if (!pictureMode && (mouseHover || isSelected()))
		  paintLink(g2);
	}
	
	public void paintLink(Graphics2D g2) {
    
    Rectangle bounds = getBounds();
    Point middle = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    Point middleRelation = computeAnchor();
    
    g2.setStroke(new BasicStroke(
        1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
        10.0f, new float[] { 4.f }, 0.0f));
    g2.drawLine(middle.x, middle.y, middleRelation.x, middleRelation.y);
	}

	@Override
	public void repaint()
	{
		final Rectangle repaintBounds = getBounds();
		repaintBounds.add(computeAnchor());
		parent.getScene().repaint(repaintBounds);
	}
	
	public void setDeplacement(Point point)
	{
		deplacement = point;

		computeLabelPosition();
	}

	public void computeDeplacement(Point point)
	{
		final Point pos = computeAnchor();

		deplacement = new Point(point.x - pos.x, point.y - pos.y);

		computeLabelPosition();
	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		computeLabelPosition();
	}
	
	@Override
	public Rectangle getBounds() {
	  Rectangle bounds = super.getBounds();
	  if (isSelected() && bounds.width == 0)
	    bounds.width = 50;
	  return bounds;
	}
}
