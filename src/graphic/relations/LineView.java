package graphic.relations;

import graphic.ColoredComponent;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.textbox.TextBox;
import graphic.textbox.TextBoxLabel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;

import utility.Utility;
import change.BufferBounds;
import change.BufferCreation;
import change.Change;

/**
 * The LineView class represent a collection of lines making a link between two
 * GraphicComponent. When it creates, the LineView have one single line between
 * the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a
 * segment between each grips. Grips are movable and a LineView have two special
 * grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class LineView extends GraphicComponent 
                               implements ColoredComponent
{
	private static Color basicColor = Color.DARK_GRAY;

	/**
	 * Get the basic color. The basic color is the color given by default at the
	 * LineView.
	 * 
	 * @return the basic color.
	 */
	public static Color getBasicColor()
	{
		return basicColor;
	}

	/**
	 * Set the basic color. The basic color is the color given by default at the
	 * LineView.
	 * 
	 * @param color
	 *            the new basic color.
	 */
	public static void setBasicColor(Color color)
	{
		basicColor = new Color(color.getRGB());
	}

	boolean justSelected = false;

	protected Stroke lineStroke = new BasicStroke(1.2f);

	protected LinkedList<RelationGrip> points = new LinkedList<>();

	private Cursor previousCursor;

	private int saveGrip;
	
	private boolean acceptGripCreation = false;
	
	private BufferBounds[] bb = new BufferBounds[2];
	
	private Point anchor1MousePressed, anchor2MousePressed;

	// More ratio is bigger, more the line near horizontal / vertical degree
	// will be adjusted.
	public final int SMOOTH_RATIO = 15;

	protected LinkedList<TextBoxLabel> tbRoles = new LinkedList<>();

	public LineView (
				final GraphicView parent,
				GraphicComponent source,
				GraphicComponent target,
				Point posSource,
				Point posTarget,
				boolean checkRecursivity) {
		super(parent);
		if (source == null)
			throw new IllegalArgumentException("source is null");

		if (target == null)
			throw new IllegalArgumentException("target is null");

		final boolean isBlocked = Change.isBlocked();
		Change.setBlocked(true);
		
		final MagneticGrip first = new MagneticGrip(parent, this, source, posSource, posTarget);
		final MagneticGrip last = new MagneticGrip(parent, this, target, posTarget, posSource);
		
		Change.setBlocked(isBlocked);

		// Initialize firsts grips (don't use addGrip method to do that, they
		// are inter-dependent!)
		points.add(first);
		points.add(last);

		parent.addOthersComponents(first);
		parent.addOthersComponents(last);

		first.addObserver(last);
		last.addObserver(first);

		smoothLines();

		if (checkRecursivity)
			reinitGrips();

		popupMenu.addSeparator();

		JMenuItem menuItem = makeMenuItem("Delete", "Delete", "delete");
		popupMenu.add(menuItem);

		setColor(getBasicColor());

		Change.push(new BufferCreation(false, this));
		Change.push(new BufferCreation(true, this));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if ("Delete".equals(e.getActionCommand()))
			delete();
	}

	/**
	 * Add a grip to this LineView. A grip is a gray square, movable by user,
	 * customizes the LineView.
	 * 
	 * @param grip
	 *            the new grip
	 * @param index
	 *            index in the array where put the new grip
	 */
	public void addGrip(RelationGrip grip, int index)
	{
		if (grip == null)
			throw new IllegalArgumentException("grip is null");

		// Change first grip observer
		if (index == 1)
		{
			final MagneticGrip firstGrip = getFirstPoint();
			points.get(1).deleteObserver(firstGrip);
			grip.addObserver(firstGrip);
		}

		// change last grip observer
		if (index == points.size() - 1)
		{
			final MagneticGrip lastGrip = getLastPoint();
			points.get(points.size() - 1).deleteObserver(lastGrip);
			grip.addObserver(lastGrip);
		}

		grip.setVisible(false);

		points.add(index, grip);
		parent.addOthersComponents(grip);
		
		repaint();
	}
	
	public void reinitializeTextBoxesLocation() {
	  for (TextBoxLabel textbox : tbRoles)
	    textbox.reinitializeLocation();
	}
	
	/**
	 * Add all the grips in given list.
	 * @param grips the list of grips.
	 * @firstindex the first index (no kidding?).
	 */
	public void addAllGrip(List<RelationGrip> grips, int firstIndex) {
	  for (RelationGrip grip : grips)
	    addGrip(grip, firstIndex++);
	}
	
	final public void removeAllGrip() {
    // Remove all intermediate grip.
    while (points.size() > 2)
      points.get(1).delete();
	}

	/**
	 * Remove and replace all grips.
	 */
	final public void reinitGrips() {
	  removeAllGrip();
	  
		// Create a square with the association to display the recursivity (if any).
		if (isRecursif()) {
			int gridSize = 35;
			MagneticGrip first = getFirstPoint(), last = getLastPoint();
			Rectangle bounds = getFirstPoint().getAssociedComponentView().getBounds();
			RelationGrip grip = new RelationGrip(parent, this);

			grip.setAnchor(new Point(bounds.x - gridSize, bounds.y + gridSize));
			addGrip(grip, 1);

			grip = new RelationGrip(parent, this);
			grip.setAnchor(new Point(bounds.x - gridSize, bounds.y - gridSize));
			addGrip(grip, 2);

			grip = new RelationGrip(parent, this);
			grip.setAnchor(new Point(bounds.x + gridSize, bounds.y - gridSize));
			addGrip(grip, 3);

			first.setAnchor(new Point(bounds.x, bounds.y + gridSize));
			last.setAnchor(new Point(bounds.x + gridSize, bounds.y));
		}
	}
	
	public boolean isRecursif() {
	  return getFirstPoint().getAssociedComponentView().equals(getLastPoint().getAssociedComponentView());
	}

	@Override
	public Point computeAnchorLocation(Point first, Point next)
	{
		Point2D p1, p2;
		final Point2D p3 = first;
		Point2D pfinal = null;
		double distance = Integer.MAX_VALUE, buffDistance;

		for (int i = 0; i < points.size() - 1; i++) {
			p1 = points.get(i).getAnchor();
			p2 = points.get(i + 1).getAnchor();

			final Point2D nearestPoint = Utility.distanceToSegment(p1, p2, p3);

			buffDistance = nearestPoint.distance(p3);

			if (distance > buffDistance) {
				distance = buffDistance;
				pfinal = nearestPoint;
			}
		}

		return new Point((int) pfinal.getX(), (int) pfinal.getY());
	}

	/**
	 * Create a new grip at the given position (if the position is on the
	 * LineView).
	 * 
	 * @param location
	 *            the position (on the LineView)
	 */
	public void createNewGrip(Point location)
	{
		if (location == null)
			throw new IllegalArgumentException("location is null");

		final int firstGripIndex = getGripBeforeLocation(location);

		// location is correct (on a segment?)
		if (firstGripIndex >= 0)
		{
			final RelationGrip grip = new RelationGrip(parent, this);
			grip.setAnchor(location);
			addGrip(grip, firstGripIndex + 1);

			// redirect event mouseDragged from relation to new grip.
			parent.setComponentMousePressed(grip);
		}
	}

	@Override
	public void delete()
	{
		super.delete();

		final boolean isBlocked = Change.isBlocked();
		Change.setBlocked(true);

		for (final TextBox tb : tbRoles)

			tb.delete();

		for (final RelationGrip grip : points)

			parent.removeComponent(grip);
		
		Change.setBlocked(isBlocked);
	}

	/**
	 * This method is called when extremity must be drawed. By default LineView
	 * have no extremity.
	 * 
	 * @param g2
	 *            the graphic context
	 * @param source
	 *            the source for compute the orientation of the extremity
	 * @param target
	 *            the target where extremity is drawed.
	 */
	protected void drawExtremity(Graphics2D g2, Point source, Point target)
	{
		// no extremity
	}

	@Override
	public Rectangle getBounds()
	{
		if (points.size() == 0)
			return new Rectangle();

		final Point firstGrip = getFirstPoint().getAnchor();
		final Rectangle bounds = new Rectangle(firstGrip.x, firstGrip.y, 1, 1);

		for (final RelationGrip pt : points)

			bounds.add(pt.getAnchor());

		return bounds;
	}
    
    @Override
    public Color getDefaultColor() {
        return getBasicColor();
    }

	/**
	 * Get the first grip.
	 * 
	 * @return the first grip
	 */
	public MagneticGrip getFirstPoint()
	{
		return (MagneticGrip) points.getFirst();
	}

	/**
	 * Get the nearest grip by the given location.
	 * 
	 * @param location
	 *            the location to find the nearest grip
	 * @return the index of the grip, or -1 if no grip are found.
	 */
	public int getGripBeforeLocation(Point location)
	{
		Line2D line;
		for (int i = 0; i < points.size() - 1; i++)
		{
			line = new Line2D.Float(points.get(i).getAnchor(), points.get(i + 1).getAnchor());

			if (line.intersects(location.x - 2, location.y - 2, 4, 4))
				return i;
		}

		return -1;
	}

	/**
	 * Get the last grip.
	 * 
	 * @return he last grip
	 */
	public MagneticGrip getLastPoint()
	{
		return (MagneticGrip) points.getLast();
	}

	/**
	 * Get the nearest grip by the given grip. Specifically, return the next
	 * grip in the array of grips, but if the given grip is the last, return the
	 * previous grip.
	 * 
	 * @param grip
	 *            the grip to find a nearest grip
	 * @return the nearest grip in the array
	 */
	public RelationGrip getNearestGrip(RelationGrip grip)
	{
		if (grip == null)
			throw new IllegalArgumentException("grip is null");

		if (!points.contains(grip))
			return null;

		// if the grip is the last, return the previous grip
		if (points.getLast() == grip)
			return previousGrip(grip);

		// else return the next grip.
		return nextGrip(grip);
	}

	/**
	 * Search the nearest segment of the LineView define by the given point.
	 * 
	 * @param first
	 *            the point to find the nearest segment
	 * @return the index of the segment
	 */
	public int getNearestSegment(Point first)
	{
		Point2D p1, p2;
		final Point2D p3 = first;
		double distance = Integer.MAX_VALUE, buffDistance;
		int seg = 0;

		for (int i = 0; i < points.size() - 1; i++)
		{
			p1 = points.get(i).getAnchor();
			p2 = points.get(i + 1).getAnchor();

			final Point2D nearestPoint = Utility.distanceToSegment(p1, p2, p3);

			buffDistance = nearestPoint.distance(p3);

			if (distance > buffDistance)
			{
				distance = buffDistance;
				seg = i;
			}
		}

		return seg;
	}

	/**
	 * Get a copy of arra of grips.
	 * 
	 * @return a copy of array of grips
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<RelationGrip> getPoints()
	{
		return (LinkedList<RelationGrip>) points.clone();
	}

	/**
	 * Get a list containing all the TextBox used by the LineView.
	 * 
	 * @return a list containing all the TextBox
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<TextBox> getTextBoxRole()
	{
		return (LinkedList<TextBox>) tbRoles.clone();
	}

	@Override
	public void gMouseDragged(MouseEvent e) {
		Point mouse = e.getPoint();

		if (acceptGripCreation) {
			createNewGrip(mousePressed);
			acceptGripCreation = false;
		} else {
			Point movement = new Point(mouse.x - mousePressed.x, 
			                           mouse.y - mousePressed.y);

			RelationGrip grip1 = points.get(saveGrip);
			RelationGrip grip2 = points.get(saveGrip + 1);
		
			grip1.setAnchor(new Point(anchor1MousePressed.x + movement.x,
			                          anchor1MousePressed.y + movement.y));
			grip2.setAnchor(new Point(anchor2MousePressed.x + movement.x,
			                          anchor2MousePressed.y + movement.y));

			grip1.notifyObservers();
			grip2.notifyObservers();

			showGrips(true);
		}
	}

	@Override
	public void gMouseEntered(MouseEvent e)
	{
		showGrips(true);
		previousCursor = parent.getScene().getCursor();
		parent.getScene().setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void gMouseExited(MouseEvent e) {
		if (!isSelected())
			showGrips(false);

		parent.getScene().setCursor(previousCursor);
	}

	@Override
	public void gMousePressed(MouseEvent e) {
		super.gMousePressed(e);

		// save mouse location and current line segment clicked by user.
		saveMouseLocation(e);
		saveGrip = getGripBeforeLocation(e.getPoint());

		// save anchor location
    anchor1MousePressed = points.get(saveGrip).getAnchor();
    anchor2MousePressed = points.get(saveGrip + 1).getAnchor();

		if (GraphicView.isAddGripMode())
			acceptGripCreation = true;

		if (e.getButton() == MouseEvent.BUTTON1) {
			bb[0] = new BufferBounds(points.get(saveGrip));
			bb[1] = new BufferBounds(points.get(saveGrip+1));
		}

		maybeShowPopup(e, popupMenu);
	}

	@Override
	public void gMouseReleased(MouseEvent e) {
		super.gMouseReleased(e);
		smoothLines();
		if (e.getButton() == MouseEvent.BUTTON1) {
			BufferBounds bb2 = new BufferBounds(points.get(saveGrip)),
					         bb3 = new BufferBounds(points.get(saveGrip+1));

			if (!(bb[0].getBounds().equals(bb2.getBounds()) && 
			      bb[1].getBounds().equals(bb3.getBounds()))) {
				boolean isRecord = Change.isRecord();
				Change.record();
				
				Change.push(bb[0]);
				Change.push(bb2);
				
				Change.push(bb[1]);
				Change.push(bb3);
				
				if (!isRecord)
					Change.stopRecord();
			}
		}

		maybeShowPopup(e, popupMenu);
		acceptGripCreation = false;
		
		if (!isSelected())
		  showGrips(false);
	}
	
	@Override
	public void gMouseClicked(MouseEvent e) {
	  super.gMouseClicked(e);
    
	  if (!GraphicView.isAddToSelection(e)) {
      parent.unselectAll();
      setSelected(true);
	  } else {
	    setSelected(!isSelected());
	  }
	}

	/**
	 * This method is called when a grip is moved for repainting the relation.
	 * 
	 * @param lastBounds
	 *            the old bounds of the relation
	 */
	public void gripMoved(Rectangle lastBounds)
	{
		parent.getScene().repaint(lastBounds);

		repaint();

		setChanged();
		notifyObservers();
	}

	@Override
	public boolean isAtPosition(Point mouse)
	{
		return getGripBeforeLocation(mouse) >= 0;
	}

	/**
	 * Move the LineView by the given movement.
	 * 
	 * @param movement
	 *            the movement of the LineView.
	 */
	public void move(Point movement)
	{
		for (int i = 1; i < points.size() - 1; i++)
		{
			final Point anchor = points.get(i).getAnchor();
			final Point newAnchor = new Point(anchor.x + movement.x, anchor.y + movement.y);
			final RelationGrip rg = points.get(i);
			
			BufferBounds bbs = new BufferBounds(rg);

			rg.setAnchor(newAnchor);

			Change.push(new BufferBounds(rg));
			Change.push(bbs);
		}
	}

	/**
	 * Get the next grip in the array by the given grip or null if no grip are
	 * found.
	 * 
	 * @param grip
	 *            the grip to find the next grip
	 * @return the next grip in array
	 */
	public RelationGrip nextGrip(RelationGrip grip)
	{
		if (grip == null)
			throw new IllegalArgumentException("grip is null");

		final int index = points.indexOf(grip);

		// if the grip is the last
		if (points.isEmpty() || points.getLast() == grip || index == -1)
			return null;

		return points.get(points.indexOf(grip) + 1);
	}

	@Override
	public void paintComponent(Graphics2D g2)
	{
		if (!isVisible() || points.size() < 2)
			return;
		
		g2.setStroke(lineStroke);
		g2.setColor(getColor());

		final int nbrPoints = points.size();
		final int[] pointsX = new int[nbrPoints];
		final int[] pointsY = new int[nbrPoints];

		for (int i = 0; i < nbrPoints; i++) {
			pointsX[i] = points.get(i).getAnchor().x;
			pointsY[i] = points.get(i).getAnchor().y;
		}

		g2.drawPolyline(pointsX, pointsY, nbrPoints);

		drawExtremity(g2, points.get(points.size() - 2).getAnchor(), points.getLast().getAnchor());
	}

	/**
	 * Get the previous grip in the array by the given grip or null if no grip
	 * are found.
	 * 
	 * @param grip
	 *            the grip to find the previous grip
	 * @return the previous grip in array
	 */
	public RelationGrip previousGrip(RelationGrip grip)
	{
		if (grip == null)
			throw new IllegalArgumentException("grip is null");

		final int index = points.indexOf(grip);

		if (points.isEmpty() || points.getFirst() == grip || index == -1)
			return null;

		return points.get(index - 1);
	}

	/**
	 * This method is called when the source or target GraphicComponent is
	 * changed. This method define if the new GraphicComponent is compatible
	 * with the LineView. If not, the GraphicComponent stay inchanged.
	 * 
	 * @param oldCompo
	 *            the old GraphicComponent before change
	 * @param newCompo
	 *            the new GraphicComnponent afetr change
	 * @return true if the new GraphicCOmponent is compatible; false otherwise
	 */
	public boolean relationChanged(MagneticGrip gripdSource, GraphicComponent target) {
		return true;
	}

	/**
	 * Remove the grip at the given index.
	 * 
	 * @param index
	 *            the index of the grip to remove
	 * @return true if the grip has been removed; false otherwise
	 */
	public boolean removeGrip(int index)
	{
		if (index == -1)
			return false;

		if (index == 1)
			points.get(2).addObserver((MagneticGrip) points.getFirst());
		
		else if (index == points.size() - 1)
			points.get(points.size() - 2).addObserver((MagneticGrip) points.getLast());

		points.remove(index);
		
		repaint();

		return true;
	}

	/**
	 * Remove the grip.
	 * 
	 * @param grip
	 *            the grip to remove
	 * @return true if the grip has beed removed; false otherwise
	 */
	public boolean removeGrip(RelationGrip grip)
	{
		return removeGrip(points.indexOf(grip));
	}

	/**
	 * Compute if grips are useless. A useless grip is a grip that don't change
	 * the direction of the LineView.
	 * 
	 * @param grip
	 *            the grip to know if it's useless
	 * @return true if the grip is useless; false if it useful
	 */
	public boolean removeUselessAnchor(RelationGrip grip)
	{
		final RelationGrip nextGrip = nextGrip(grip);
		final RelationGrip previousGrip = previousGrip(grip);

		if (nextGrip != null && previousGrip != null)
		{
			final double ratio = 2.0;
			double dist1, dist2, distGlobal;

			final Point anchor = grip.getAnchor();
			final Point anchorNext = nextGrip.getAnchor();
			final Point anchorPrevious = previousGrip.getAnchor();

			dist1 = anchor.distance(anchorNext);
			dist2 = anchor.distance(anchorPrevious);
			distGlobal = anchorNext.distance(anchorPrevious);

			if (dist1 + dist2 - distGlobal < ratio)
			{
				grip.delete();
				return true;
			}
		}

		return false;
	}

	@Override
	public void repaint()
	{
		final Rectangle repaintBounds = getBounds();

		parent.getScene().repaint(repaintBounds);
	}
	
	@Override
	public void restore()
	{
		for (final RelationGrip grip : points)

			parent.addOthersComponents(grip);

		for (final TextBox tb : tbRoles)
		
			tb.restore();
		
		parent.addLineView(this);
	}

	/**
	 * Search useless grips from the index of the give grip.
	 * 
	 * @param sourceGrip
	 *            the grip to begin to search useless grip
	 */
	public void searchUselessAnchor(RelationGrip sourceGrip) {
		int index = points.indexOf(sourceGrip);

		if (index == -1)
			return;

		for (int i = index - 1; i >= 1; i--)
			removeUselessAnchor(points.get(i));

		index = points.indexOf(sourceGrip); // get the new index (if anchor
		// suppressed by the first loop).

		for (int i = index; i < points.size() - 1;)
			if (!removeUselessAnchor(points.get(i)))
				i++;
	}

	@Override
	public void setBounds(Rectangle bounds) {
		// Can't change bound from here. LineView bounds depends on grips
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		showGrips(selected);
		
		for (TextBoxLabel textbox : tbRoles)
		  textbox.setSelected(selected);
	}

	/**
	 * Set the stroke for draw the LineView.
	 * 
	 * @param stroke
	 *            the new stroke
	 */
	public void setStroke(BasicStroke stroke) {
		lineStroke = stroke;
	}

	/**
	 * Set visible all grips to true or false.
	 * 
	 * @param show
	 *            show all grips, or not
	 */
	public void showGrips(boolean show) {
		for (final RelationGrip g : points)
			g.setVisible(show);
	}

	/**
	 * This method help to put the lines perfectly horizontal or vertical. If a
	 * line is almost vertical or horizontal, move the grip to make perfect
	 * line. Use the SMOOTH_RATIO to change the ratio for say if the line must
	 * be moved or not.
	 */
	final public void smoothLines() {
		for (int i = 0; i < points.size() - 1; i++) {
			final Point anchor1 = points.get(i).getAnchor();
			final Point anchor2 = points.get(i + 1).getAnchor();
			final int deltaX = Math.abs(anchor2.x - anchor1.x);
			final int deltaY = Math.abs(anchor2.y - anchor1.y);

			if (deltaX < SMOOTH_RATIO)
				points.get(i + 1).setAnchor(new Point(anchor1.x, anchor2.y));

			else if (deltaY < SMOOTH_RATIO)
				points.get(i + 1).setAnchor(new Point(anchor2.x, anchor1.y));
		}
	}
	
	/**
	 * Change the component associed to the grip by the new specified.
	 * @param gripSource the source grip.
	 * @param target the new component.
	 * @return if the change is successful.
	 */
	protected void changeLinkedComponent(MagneticGrip gripSource,
                                  	   GraphicComponent target) {
    gripSource.setAssociedComponentView(target);
    reinitGrips();
	}
}
