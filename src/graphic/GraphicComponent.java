package graphic;

import graphic.entity.EntityView;
import graphic.relations.LineView;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Observable;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import swing.PanelClassDiagram;
import swing.Slyum;
import swing.SColorChooser;
import utility.PersonalizedIcon;
import change.BufferCreation;
import change.Change;
import classDiagram.IDiagramComponent;

/**
 * Represent a graphic component in Slyum. Graphics components can't be draw
 * itself, it's not a Swing component. It must be drawed and managed by the
 * GraphicView class. Graphic components is an abstract class containing methods
 * and attributes useful for representing a graphical component in Slyum. Mouse
 * events, color, drawing, selectable, visible, etc...
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class GraphicComponent extends Observable implements ActionListener
{
	private Color color = Color.DARK_GRAY;
	// Save the location of the mouse uses for computing the movement or the
	// resize.
	protected Point mousePressed = new Point();

	protected GraphicView parent;

	protected JPopupMenu popupMenu;
	protected JMenuItem miNewNote;

	private boolean selected = false;

	private boolean visible = true;

	/**
	 * !!! This constructor is use for create the graphic view, don't use in
	 * another way !!! Graphic view is the parent for all other components, but
	 * can't give itself to this constructor in its constructor...
	 */
	protected GraphicComponent()
	{
		parent = (GraphicView) this;

		init();
	}

	public GraphicComponent(GraphicView parent)
	{
		if (parent == null)
			throw new IllegalArgumentException("parent is null");

		this.parent = parent;

		init();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (Slyum.ACTION_NEW_NOTE_ASSOCIED.equals(e.getActionCommand()))

			parent.linkNewNoteWithSelectedEntities();

		if ("Color".equals(e.getActionCommand()))
			
			askNewColorForSelectedItems();

	}
	
	public static void askNewColorForSelectedItems()
	{
		final SColorChooser scc = new SColorChooser(EntityView.getBasicColor());
		
		scc.setVisible(true);

		if (scc.isAccepted())
			
			PanelClassDiagram.getInstance().getCurrentGraphicView().changeColorForSelectedItems(scc.getColor());
	}

	/**
	 * Confirm ghost changes. If component have a ghost representation, call
	 * this method will translate and scale the component bounds with the ghost
	 * bounds. The ghost is, usually, the representation of component during
	 * movement or resize.
	 * 
	 * @param e
	 *            the mouse event
	 */
	public void apply(MouseEvent e)
	{
	}

	/**
	 * This method is use by the magnetic grip for computing where did it
	 * magnetized. By default, magnetic grips go to the middle bounds of the
	 * component.
	 * 
	 * @param first
	 *            the current location of the magnetic grip
	 * @param next
	 *            the location of the next grip (nearest grip of first grip)
	 * @return
	 */
	public Point computeAnchorLocation(Point first, Point next)
	{
		final Rectangle bounds = getBounds();
		return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
	}

	/**
	 * Delete this component from the parent. Delete a component will delete all
	 * line associed with this component. A deleted component will no longer be
	 * drawn or managed by the graphic view. This operation is irreversible.
	 */
	public void delete()
	{
		if (!parent.containComponent(this))
			return;
		
		// Unselect the component.
		setSelected(false);
		
		Change.push(new BufferCreation(true, this));
		Change.push(new BufferCreation(false, this));
		
		parent.removeComponent(this);

		// Search and remove the UML associated component.
		final IDiagramComponent associed = getAssociedComponent();
		
		if (associed != null)
			parent.getClassDiagram().removeComponent(associed);
		
		// Search and delete all lines (relations, associations, etc...)
		// associated with this component.
		for (final LineView lv : parent.getLinesViewAssociedWith(this))
		
			lv.delete();
		
	}

	/**
	 * When the user select a component, this method is called. It's used for
	 * drawing a selected effect, like an etched border.
	 * 
	 * @param g2
	 *            the graphic context
	 */
	public void drawSelectedEffect(Graphics2D g2)
	{

	}

	/**
	 * Some graphic component is associed with a structural UML component (like
	 * classes, methods, relations, ...). Return null if no component are
	 * associated. !!! GraphicComponent and GraphicComponent associated with UML
	 * component should be separated in newer version !!!
	 * 
	 * @return
	 */
	public IDiagramComponent getAssociedComponent()
	{
		return null;
	}

	/**
	 * Get the bounds of this component. The bounds is the minimum (x, y)
	 * location and the width and height is compute in this way (maxX - minX)
	 * and (maxY - minY).
	 * 
	 * @return the bounds of this component.
	 */
	public abstract Rectangle getBounds();
	
	public GraphicView getGraphicView()
	{
		return parent;
	}

	/**
	 * Get the color of this component. The color can be used by the component
	 * during drawing. But it is the responsibility of the sub class to use it
	 * or not.
	 * 
	 * @return the color of this component.
	 */
	public Color getColor()
	{
		return new Color(color.getRGB());
	}

	/**
	 * Get the mouse pressed location.
	 * 
	 * @return the mouse pressed location.
	 */
	public Point getMousePressed()
	{
		return mousePressed;
	}

	/**
	 * Get the popup menu for this component. The popup menu is shown when user
	 * make a right-click on it. Some component hides this menu and don't use
	 * it.
	 * 
	 * @return the popup menu.
	 */
	public JPopupMenu getPopupMenu()
	{
		return popupMenu;
	}

	/**
	 * Mouse event - this event is called by the graphic view when the user
	 * clicks on it. The graphic view make a link between swing and slyum
	 * events.
	 * 
	 * @param e
	 *            the swing mouse event.
	 */
	public void gMouseClicked(MouseEvent e)
	{

	}

	/**
	 * Mouse event - this event is called by the graphic view when the user
	 * keeps the mouse pressed and move it. The graphic view make a link between
	 * swing and slyum events.
	 * 
	 * @param e
	 *            the swing mouse event.
	 */
	public void gMouseDragged(MouseEvent e)
	{
	}

	/**
	 * Mouse event - this event is called by the graphic view when the user
	 * enters in the component with the mouse. The graphic view make a link
	 * between swing and slyum events.
	 * 
	 * @param e
	 *            the swing mouse event.
	 */
	public void gMouseEntered(MouseEvent e)
	{
	}

	/**
	 * Mouse event - this event is called by the graphic view when the user
	 * exits from the component with the mouse. The graphic view make a link
	 * between swing and slyum events.
	 * 
	 * @param e
	 *            the swing mouse event.
	 */
	public void gMouseExited(MouseEvent e)
	{
	}

	/**
	 * Mouse event - this event is called by the graphic view when the user
	 * moves on the component with the mouse. The graphic view make a link
	 * between swing and slyum events.
	 * 
	 * @param e
	 *            the swing mouse event.
	 */
	public void gMouseMoved(MouseEvent e)
	{
	}

	/**
	 * Mouse event - this event is called by the graphic view when the user
	 * presses a mouse button on the component. The graphic view make a link
	 * between swing and slyum events.
	 * 
	 * @param e
	 *            the swing mouse event.
	 */
	public void gMousePressed(MouseEvent e)
	{
		maybeShowPopup(e, popupMenu);
	}

	/**
	 * Mouse event - this event is called by the graphic view when the user
	 * releases a mouse button on the component. The graphic view make a link
	 * between swing and slyum events.
	 * 
	 * @param e
	 *            the swing mouse event.
	 */
	public void gMouseReleased(MouseEvent e)
	{
		maybeShowPopup(e, popupMenu);
	}
	
	public void restore()
	{
	}

	/**
	 * Calls by the constructor for initialize components.
	 */
	private void init()
	{
		// Create context menu.
		popupMenu = new JPopupMenu();

		JMenuItem menuItem;

		miNewNote = menuItem = makeMenuItem("New note", Slyum.ACTION_NEW_NOTE_ASSOCIED, "note16");
		popupMenu.add(menuItem);

		menuItem = makeMenuItem("Change color...", "Color", "color16");
		popupMenu.add(menuItem);
	}

	/**
	 * Return if the position given in parameter is on the graphic component or
	 * not. This method is used by the graphic view for computing mouse event.
	 * 
	 * @param position
	 *            the position to verify
	 * @return true if the position is on the graphic component; false otherwise
	 */
	public abstract boolean isAtPosition(Point position);

	/**
	 * Return if the component is selected or not.
	 * 
	 * @return true if the component is selected; false otherwise
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * Return if the component is visible or not.
	 * 
	 * @return true if the component is visible; false otherwise
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * Creates a new JMenuItem with this class like action listeners.
	 * 
	 * @param name
	 *            the name for JMenuItem
	 * @param action
	 *            the action command for this JMenuItem
	 * @param imgIcon
	 *            the icon path for the icon of this JMenuItem
	 * @return the new JMenuItem created
	 */
	public JMenuItem makeMenuItem(String name, String action, String imgIcon)
	{
		final ImageIcon img = PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + imgIcon + ".png");

		final JMenuItem menuItem = new JMenuItem(name, img);
		menuItem.setActionCommand(action);
		menuItem.addActionListener(this);
		return menuItem;
	}

	/**
	 * Makes a new JRadioButtonMenuItem with this class like action listeners.
	 * 
	 * @param name
	 *            the name for JRadioButtonMenuItem
	 * @param action
	 *            action the action command for this JRadioButtonMenuItem
	 * @param group
	 *            the group for this JRadioButtonMenuItem
	 * @return the new JRadioButtonMenuItem created
	 */
	public JRadioButtonMenuItem makeRadioButtonMenuItem(String name, String action, ButtonGroup group)
	{
		final JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(name);
		rbMenuItem.setActionCommand(action);
		rbMenuItem.addActionListener(this);
		group.add(rbMenuItem);

		return rbMenuItem;
	}

	/**
	 * Displays the popup menu if e.isPopupTrigger is true, hide otherwise.
	 * 
	 * @param e
	 *            the mouse event
	 * @param popupMenu
	 *            the popupMenu to display or hide.
	 */
	public void maybeShowPopup(MouseEvent e, JPopupMenu popupMenu)
	{
		GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
		
		if (e.isPopupTrigger())
		{
			miNewNote.setEnabled(getAssociedComponent() != null);
			popupMenu.show(e.getComponent(), (int)(e.getX() / gv.getInversedScale()), (int)(e.getY() / gv.getInversedScale()));
		}
	}

	/**
	 * Compute the middle bounds of the component. Middle bounds is compute from
	 * the bounds of the component.
	 * 
	 * @return
	 */
	public Point middleBounds()
	{
		final Rectangle bounds = getBounds();
		return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
	}

	/**
	 * Calls when a component is moved. Most of components use a ghost
	 * representation for moving. Call apply() after move for applying changes.
	 * 
	 * @param e
	 */
	public void move(MouseEvent e)
	{
	}

	/**
	 * Draw the component on the graphic view.
	 * 
	 * @param g2
	 *            The graphic context
	 */
	public abstract void paintComponent(Graphics2D g2);

	/**
	 * Repaint the component on the graphic view. Most of components call
	 * getBounds() method for repaint only its bounds. When a component is moved
	 * or resize, calls this method is not sufficient because it redraws only
	 * current bounds, not old bounds.
	 */
	public abstract void repaint();

	/**
	 * Calls for resizing component from left. Not all components can be
	 * resizing. By default, component can't be resize.
	 * 
	 * @param e
	 *            the mouse event for compute new size
	 */
	public void resizeLeft(MouseEvent e)
	{
	}

	/**
	 * Calls for resizing component from right. Not all components can be
	 * resizing. By default, component can't be resize.
	 * 
	 * @param e
	 *            the mouse event for compute new size
	 */
	public void resizeRight(MouseEvent e)
	{
	}

	/**
	 * Calls this method for saving mouse location. Mouse location is used by
	 * components for compute moving or resizing.
	 * 
	 * @param e
	 *            the mouse event
	 */
	public void saveMouseLocation(MouseEvent e)
	{
		mousePressed = new Point(e.getPoint());
	}

	/**
	 * Set the bounds for the component. Any component don't have rectangulare
	 * bounds and this method can have no effect on them. Calls the appropriate
	 * method of sub element for changed theirs bounds.
	 * 
	 * @param bounds
	 *            the new bounds for this component
	 */
	public abstract void setBounds(Rectangle bounds);

	/**
	 * Set the color for this component. The color can be used by the component
	 * during drawing. But it is the responsibility of the sub class to use it
	 * or not.
	 * 
	 * @param color
	 *            the new color for this component
	 */
	public void setColor(Color color)
	{
		this.color = new Color(color.getRGB());
		repaint();
	}

	/**
	 * Set the color for this component. The color can be used by the component
	 * during drawing. But it is the responsibility of the sub class to use it
	 * or not.
	 * 
	 * @param color
	 *            the new rgb 32-bits color for this component
	 */
	public void setColor(int rgb)
	{
		setColor(new Color(rgb));
	}

	/**
	 * Some component have different styles. Calls this method reset the default
	 * style for this component.
	 */
	public void setDefaultStyle()
	{
	}

	/**
	 * Some component have different styles. Calls this method draw the
	 * component with its mouse hover style.
	 */
	public void setMouseHoverStyle()
	{
	}

	/**
	 * Set the selected state for this component.
	 * 
	 * @param selected
	 *            the new selected state for this component.
	 */
	public void setSelected(boolean selected)
	{
		if (isSelected() != selected)
		{
			this.selected = selected;
			repaint();
			
			parent.componentSelected(selected);
		}
		
		setChanged();
	}

	/**
	 * Some component have different styles. Calls this method draw the
	 * component with its mouse clicked style.
	 */
	public void setStyleClicked()
	{
	}

	/**
	 * Set the visible state for this component. This method repaint the
	 * component. Note that, by default, hide a component means that it will no
	 * longer be drawn.
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
		repaint();
	}

	/**
	 * Return the XML structure for this component in String.
	 * 
	 * @param depth
	 *            the number of tabs before each tags.
	 * @return the XML structure
	 */
	public String toXML(int depth)
	{
		return "";
	}
}
