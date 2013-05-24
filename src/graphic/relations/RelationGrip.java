package graphic.relations;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.SquareGrip;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import swing.Slyum;
import utility.PersonalizedIcon;
import utility.Utility;
import change.BufferBounds;
import change.BufferCreation;
import change.Change;

/**
 * The relationGrip is a grip who customize a LineView. The LineView uses
 * RelationGrip for draw segments between each RelationGrip.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class RelationGrip extends SquareGrip implements ActionListener
{
	public static int DEFAULT_SIZE = 10;

	private Point anchor = new Point();

	protected JMenuItem menuItemDelete;
	protected JPopupMenu popupMenu;
	protected LineView relation;
	private int index = -1;

	private boolean isMouseDragged;

	/**
	 * Create a new RelationGrip associate with the given LineView.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param relationView
	 *            the LineView associated.
	 */
	public RelationGrip(GraphicView parent, LineView relationView)
	{
		super(parent, DEFAULT_SIZE, Cursor.HAND_CURSOR);

		if (relationView == null)
			throw new IllegalArgumentException("relation is null");

		relation = relationView;

		popupMenu = new JPopupMenu();

		final ImageIcon imgIcon = PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "delete.png");

		menuItemDelete = new JMenuItem("Delete", imgIcon);
		menuItemDelete.setActionCommand("delete");
		menuItemDelete.addActionListener(this);
		popupMenu.add(menuItemDelete);
		
		Change.push(new BufferCreation(false, this));
		Change.push(new BufferCreation(true, this));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ("delete".equals(e.getActionCommand()))
			delete();
	}

	/**
	 * Get the anchor of the RelationGrip. Anchor is the middle of the grip.
	 * 
	 * @return the anchor of the grip.
	 */
	public Point getAnchor()
	{
		return new Point(anchor);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(anchor.x - DEFAULT_SIZE / 2, anchor.y - DEFAULT_SIZE / 2, DEFAULT_SIZE, DEFAULT_SIZE);
	}

	@Override
	public void gMouseDragged(MouseEvent e)
	{
		final Point mouse = e.getPoint();

		setAnchor(new Point(mouse.x, mouse.y));
		notifyObservers();

		parent.unselectAll();
		relation.setSelected(true);
		
		isMouseDragged = true;
	}

	@Override
	public void gMousePressed(MouseEvent e)
	{
		isMouseDragged = false;
		
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			Change.record();
			Change.push(new BufferBounds(this));
		}
		
		maybeShowPopup(e, popupMenu);
	}

	@Override
	public void gMouseReleased(MouseEvent e)
	{
		final GraphicComponent component = parent.getDiagramElementAtPosition(getAnchor(), null), source = relation.getFirstPoint().getAssociedComponentView(), target = relation.getLastPoint().getAssociedComponentView();

		pushBufferChangeMouseReleased(e);

		relation.smoothLines();
		relation.searchUselessAnchor(this);
		
		if (component != null && (component.equals(source) || component.equals(target)))
		
			delete();

		Change.stopRecord();
		
		maybeShowPopup(e, popupMenu);
		
		isMouseDragged = false;
	}
	
	protected void pushBufferChangeMouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
			
			if (isMouseDragged)
			{
				if (Change.getSize() % 2 == 1)
					Change.push(new BufferBounds(this));
			}
			else
				Change.pop();
	}
	
	@Override
	public void delete()
	{
		// save index
		index = relation.getPoints().indexOf(this);
		
		super.delete();
		
		relation.removeGrip(this);
	}
	
	@Override
	public void restore()
	{
		super.restore();
		
		parent.addOthersComponents(this);
		relation.addGrip(this, index);
	}

	/**
	 * Remove the grip from the LineView.
	 * 
	 * @return true if the grip has been removed; false otherwise
	 *//*
	public boolean remove()
	{
		if (relation.removeGrip(this))
		{
			setChanged();
			notifyObservers();
			return true;
		}

		return false;
	}*/

	/**
	 * Set the anchor of the grip.
	 * 
	 * @param anchor
	 *            the new anchor for this grip.
	 */
	public void setAnchor(Point anchor)
	{
		if (anchor == null)
			return;

		final Rectangle repaintBounds = Utility.growRectangle(relation.getBounds(), GraphicView.getGridSize() + 20);

		this.anchor = new Point(anchor);
		relation.gripMoved(repaintBounds);

		setChanged();
	}

	@Override
	public void setBounds(Rectangle bounds)
	{
		setAnchor(new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2));
		
		notifyObservers();
	}
}
