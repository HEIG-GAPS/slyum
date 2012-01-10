package graphic;

import graphic.entity.GripEntity;
import graphic.relations.LineView;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import change.BufferBounds;
import change.BufferCreation;
import change.Change;

/**
 * This graphic class represant all movable and resizable component with a ghost
 * representation. A ghost representation is a copy of the component show when
 * the component is resized or moved. When the apply method is called, the bound
 * of the component are translate by the bounds of the ghost.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class MovableComponent extends GraphicComponent
{
	public static final Point MINIMUM_SIZE = new Point(50, 50);

	protected Rectangle ghost = new Rectangle();
	private boolean justSelected = false;

	protected GripEntity leftMovableSquare;

	protected GripEntity rightMovableSquare;

	public MovableComponent(GraphicView parent)
	{
		super(parent);

		final int resizerSize = 10;
		final GraphicComponent g = this;

		// Construct two movable squares for resizing left and right.
		setLeftResizer(new GripEntity(parent, resizerSize, Cursor.W_RESIZE_CURSOR) {

			@Override
			public void gMousePressed(MouseEvent e)
			{
				if (!g.isSelected())
					parent.unselectAll();

				g.setSelected(true);
				super.gMousePressed(e);
			}

			@Override
			public void move(MouseEvent e)
			{
				for (final GraphicComponent c : parent.getSelectedComponents())
					c.resizeLeft(e);
			}
		});

		setRightResizer(new GripEntity(parent, resizerSize, Cursor.E_RESIZE_CURSOR) {

			@Override
			public void gMousePressed(MouseEvent e)
			{
				if (!g.isSelected())
					parent.unselectAll();

				g.setSelected(true);
				super.gMousePressed(e);
			}

			@Override
			public void move(MouseEvent e)
			{
				for (final GraphicComponent c : parent.getSelectedComponents())

					c.resizeRight(e);
			}
		});

		pushBufferCreation();
	}
	
	protected void pushBufferCreation()
	{
		Change.push(new BufferCreation(false, this));
		Change.push(new BufferCreation(true, this));
	}

	/**
	 * Adjust the value on the grid.
	 * 
	 * @param value
	 *            the value to adjust
	 * @return the value adjusted
	 */
	public int ajustOnGrid(int value)
	{
		// Use the integer cast for adjusting value.
		return value / GraphicView.getGridSize() * GraphicView.getGridSize();
	}

	@Override
	public void apply(MouseEvent e)
	{
		
		final Rectangle saveBounds = getBounds();
		saveBounds.grow(20, 20);

		// ghost not initialized!
		if (ghost.isEmpty())
			return;

		Change.push(new BufferBounds(this)); // save state
		
		final Rectangle bounds = getBounds();
		final Point movement = new Point(ghost.x - bounds.x, ghost.y - bounds.y);

		for (final LineView line : parent.getLinesViewAssociedWith(this))

			if (line.getFirstPoint().getAssociedComponentView().equals(this) && line.getLastPoint().getAssociedComponentView().isSelected())

				line.move(movement);

		setBounds(ghost);

		parent.getScene().repaint(saveBounds);
		ghost = new Rectangle();
		repaint();

		Change.push(new BufferBounds(this)); // save state
	}

	/**
	 * Compute the location of the resize. The offset define if it's the right
	 * or the left resizer. The offset is add to the bounds.x location. The y
	 * location is the middle bounds.y (bounds.y + bounds.heigth / 2)
	 * 
	 * @param offset
	 *            value to add at the bounds.x location (0 for left,
	 *            bounds.width for right).
	 * @return the bounds of the resizer
	 */
	public Rectangle computeLocationResizer(int offset)
	{
		final int size = leftMovableSquare.getBounds().width;
		final Rectangle bounds = getBounds();

		return new Rectangle(bounds.x - size / 2 + offset, bounds.y + (bounds.height - size) / 2, size, size);
	}

	@Override
	public void gMouseClicked(MouseEvent e)
	{
		super.gMouseClicked(e);

		if (!e.isControlDown()) // If ctrl is not down, unselect all component
		// except this one.
		{
			parent.unselectAll();
			setSelected(true);
		}
		else // If control is down, inverse the current selected state of this
		// component.
		if (!justSelected)

			setSelected(!isSelected());

		justSelected = false; // Avoid to unselect a just selected component
		// when gMouseReleased is called.
		

		int selectedComponentSize = parent.countSelectedEntities();
		
		if (e.isControlDown() && selectedComponentSize > 0)
			
			new StyleCross(parent, e.getPoint(), selectedComponentSize);
	}

	@Override
	public void gMouseDragged(MouseEvent e)
	{
		for (final GraphicComponent c : parent.getSelectedComponents())

			c.move(e);
	}

	@Override
	public void gMouseEntered(MouseEvent e)
	{
		setResizerVisible(true);
	}

	@Override
	public void gMouseExited(MouseEvent e)
	{
		setResizerVisible(false);
	}

	@Override
	public void gMousePressed(MouseEvent e)
	{
		super.gMousePressed(e);

		if (!parent.getSelectedComponents().contains(this) && !e.isControlDown())

			parent.unselectAll();

		if (!isSelected()) // Select the component, see doc for know how
		// component are selected.
		{
			setSelected(true);
			justSelected = true; // Avoid to unselect a just selected component
			// when gMouseReleased is called.
		}

		// Save mouse location of all selected components. When a movable
		// component is moved or resized, he notifies all selected components.
		// So they must save this
		// location for compute themselves their new bounds.
		for (final GraphicComponent c : parent.getSelectedComponents())

			c.saveMouseLocation(e);
	}
	@Override
	public void gMouseReleased(MouseEvent e)
	{
		super.gMouseReleased(e);

		boolean isRecord = Change.isRecord();
		Change.record();
		
		// Confirm the ghost translation for all selected component.
		for (final GraphicComponent c : parent.getSelectedComponents())

			c.apply(e);
		
		if (!isRecord);
			Change.stopRecord();
	}

	@Override
	public void move(MouseEvent e)
	{
		// Create a new ghost and draw it. See move description for more
		// details.
		final Rectangle saveGhost = new Rectangle(ghost);
		ghost = new Rectangle(getBounds());

		// Compute the ghost position from the buffered mouse location and the
		// current mouse location.
		ghost.x = ajustOnGrid(e.getX() - mousePressed.x) + ghost.x;
		ghost.y = ajustOnGrid(e.getY() - mousePressed.y) + ghost.y;

		// Can't go out of scene bounds.
		if (ghost.x < 0)
			ghost.x = 0;
		if (ghost.y < 0)
			ghost.y = 0;

		parent.getScene().repaint(saveGhost); // Repaint old location.
		parent.getScene().repaint(ghost); // Repaint current location.
	}

	@Override
	public void resizeLeft(MouseEvent e)
	{
		/**
		 * Compute the new size of the ghost and the new position of left
		 * resizer from the buffered mouse location and the current mouse
		 * location. The bounds of a rectangle is the location of the left
		 * corner plus two size. For resize from left, we can't just grow width
		 * and height, but move its left corner location too.
		 */
		final Rectangle saveGhost = new Rectangle(ghost);
		ghost = new Rectangle(getBounds());
		final Rectangle boundsResizer = leftMovableSquare.getBounds();

		boundsResizer.x += e.getX() - leftMovableSquare.getMousePressed().x;
		leftMovableSquare.saveMouseLocation(e);
		leftMovableSquare.setBounds(boundsResizer);

		final int anchor = (ghost.x + ghost.width) / GraphicView.getGridSize();
		ghost.x = boundsResizer.x;
		ghost.x = ajustOnGrid(ghost.x);

		if (ghost.x < 0)
			ghost.x = 0;
		if (ghost.y < 0)
			ghost.y = 0;

		ghost.width = anchor * GraphicView.getGridSize() - ghost.x;

		if (ghost.width < MINIMUM_SIZE.x)
		{
			ghost.width = saveGhost.width;
			ghost.x = saveGhost.x;
		}

		parent.getScene().repaint(saveGhost);
		parent.getScene().repaint(ghost);
	}

	@Override
	public void resizeRight(MouseEvent e)
	{
		/**
		 * Compute new size from right resize is quit easy than left resize. We
		 * just have to grow it's width. This method compute a new location for
		 * the right resizer too.
		 */
		final Rectangle saveGhost = new Rectangle(ghost);
		ghost = new Rectangle(getBounds());
		final Rectangle boundsResizer = rightMovableSquare.getBounds();

		boundsResizer.x += e.getX() - rightMovableSquare.getMousePressed().x;
		rightMovableSquare.saveMouseLocation(e);
		rightMovableSquare.setBounds(boundsResizer);

		ghost.width = ajustOnGrid(boundsResizer.x - ghost.x) + GraphicView.getGridSize();

		if (ghost.width < MINIMUM_SIZE.x)
			ghost.width = saveGhost.width;

		saveGhost.grow(boundsResizer.width, 0);
		parent.getScene().repaint(saveGhost);
		parent.getScene().repaint(ghost);
	}

	@Override
	public void saveMouseLocation(MouseEvent e)
	{
		super.saveMouseLocation(e);
		leftMovableSquare.saveMouseLocation(e);
		rightMovableSquare.saveMouseLocation(e);
	}

	/**
	 * Set a left resizer. A resizer is used to resizer graphic component
	 * bounds. The left resizer resize the component from left.
	 * 
	 * @param left
	 *            the new resizer.
	 */
	public void setLeftResizer(GripEntity left)
	{
		if (left == null)
			throw new IllegalArgumentException("left is null");

		if (leftMovableSquare != null)
			parent.removeComponent(leftMovableSquare);

		leftMovableSquare = left;
		parent.addOthersComponents(leftMovableSquare);
		leftMovableSquare.setBounds(computeLocationResizer(0));
		leftMovableSquare.setVisible(false);
	}

	/**
	 * Show or hide resizers.
	 * 
	 * @param visible
	 *            true for showing resizers; false for hide them.
	 */
	public void setResizerVisible(boolean visible)
	{
		leftMovableSquare.setVisible(visible);
		rightMovableSquare.setVisible(visible);
	}

	/**
	 * Set a right resizer. A resizer is used to resizer graphic component
	 * bounds. The right resizer resize the component from right.
	 * 
	 * @param left
	 *            the new resizer.
	 */
	public void setRightResizer(GripEntity right)
	{
		if (right == null)
			throw new IllegalArgumentException("right is null");

		if (rightMovableSquare != null)
			parent.removeComponent(rightMovableSquare);

		rightMovableSquare = right;
		parent.addOthersComponents(rightMovableSquare);
		rightMovableSquare.setBounds(computeLocationResizer(getBounds().width));
		rightMovableSquare.setVisible(false);
	}
}
