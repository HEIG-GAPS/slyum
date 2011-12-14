package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.ClassView;
import graphic.entity.EntityView;
import graphic.relations.MultiView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.util.LinkedList;

import javax.swing.JButton;

import utility.PersonalizedIcon;
import classDiagram.ClassDiagram;
import classDiagram.components.ClassEntity;
import classDiagram.relationships.Multi;

/**
 * MultiFactory allows to create a new multi-association view associated with a
 * new association UML. Give this factory at the graphic view using the method
 * initNewComponent() for initialize a new factory. Next, graphic view will use
 * the factory to allow creation of a new component, according to the
 * specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class MultiFactory extends CreateComponent
{
	private final JButton[] buttons = new JButton[2];
	private ClassView classMouseHover = null;
	private final LinkedList<ClassView> classSelected = new LinkedList<ClassView>();
	private final KeyAdapter keyListener;
	private boolean onButton = false;

	private Area subArea = new Area();

	/**
	 * Create a new factory allowing the creation of a multi-association.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param classDiagram
	 *            the class diagram
	 */
	public MultiFactory(final GraphicView parent, ClassDiagram classDiagram)
	{
		super(parent, classDiagram);

		parent.clearAllSelectedComponents();

		parent.getScene().repaint();

		final MouseAdapter ma = new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e)
			{
				onButton = true;
				parent.getScene().setCursor(getCursor());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				onButton = false;
				parent.getScene().setCursor(getCursor());
			}
		};

		buttons[0] = new JButton(PersonalizedIcon.createImageIcon("resources/icon/tick.png"));
		buttons[0].setBounds(10, 10, 66, 42);
		buttons[0].addMouseListener(ma);
		buttons[0].setEnabled(false);
		buttons[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				create();
			}
		});
		parent.getScene().add(buttons[0]);

		buttons[1] = new JButton(PersonalizedIcon.createImageIcon("resources/icon/delete.png"));
		buttons[1].setBounds(buttons[0].getX() + buttons[0].getWidth() + 10, 10, 66, 42);
		buttons[1].addMouseListener(ma);
		buttons[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				parent.deleteCurrentFactory();
			}
		});
		parent.getScene().add(buttons[1]);

		keyListener = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyChar() == '\n')
					buttons[0].doClick();

				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					buttons[1].doClick();
			}
		};

		parent.getScene().addKeyListener(keyListener);
		parent.getScene().requestFocusInWindow();
	}

	/**
	 * Substract the rectangle r from the area a.
	 * 
	 * @param r
	 *            the rectangle
	 * @param a
	 *            the area
	 */
	private void addClassClipped(Rectangle r, Area a)
	{
		r.width++;
		r.height++;
		a.subtract(new Area(r));
	}

	@Override
	public GraphicComponent create()
	{
		final LinkedList<ClassEntity> ce = getClassEntity(classSelected);

		MultiView mv = null;
		if (Multi.canCreate(ce))
		{
			final Multi multi = new Multi(ce);
			mv = new MultiView(parent, multi);

			parent.addMultiView(mv);
			classDiagram.addMulti(multi);
		}

		parent.deleteCurrentFactory();

		return mv;
	}

	@Override
	public void deleteFactory()
	{
		super.deleteFactory();

		parent.getScene().removeKeyListener(keyListener);

		for (final JButton button : buttons)
			parent.getScene().remove(button);
	}

	@Override
	public Rectangle getBounds()
	{
		return parent.getScene().getBounds();
	}

	/**
	 * Get a list of all associated entities contains in classView.
	 * 
	 * @param classView
	 *            a list of classView
	 * @return an array containing all classEntity associated to classView
	 */
	private LinkedList<ClassEntity> getClassEntity(LinkedList<ClassView> classView)
	{
		final LinkedList<ClassEntity> ce = new LinkedList<ClassEntity>();

		for (final ClassView cv : classView)

			ce.add((ClassEntity) cv.getAssociedComponent());

		return ce;
	}

	@Override
	public Cursor getCursor()
	{
		if (classMouseHover != null || onButton)
			return new Cursor(Cursor.HAND_CURSOR);
		else
			return new Cursor(Cursor.CROSSHAIR_CURSOR);
	}

	@Override
	public void gMouseMoved(MouseEvent e)
	{
		final EntityView ev = parent.getEntityAtPosition(e.getPoint());

		if (ev != null && ev.getClass() == ClassView.class)
		{
			if (classMouseHover != null)
				parent.getScene().repaint(classMouseHover.getBounds());

			classMouseHover = (ClassView) ev;
			parent.getScene().repaint(classMouseHover.getBounds());

			parent.getScene().setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		else if (classMouseHover != null)
		{
			parent.getScene().repaint(classMouseHover.getBounds());
			classMouseHover = null;
		}

		parent.getScene().setCursor(getCursor());
	}

	@Override
	public void gMousePressed(MouseEvent e)
	{
		final EntityView ev = parent.getEntityAtPosition(e.getPoint());

		if (ev != null && ev.getClass() == ClassView.class)
		{
			if (!classSelected.remove(ev))

				classSelected.add((ClassView) ev);

			parent.getScene().repaint(ev.getBounds());
		}

		buttons[0].setEnabled(Multi.canCreate(getClassEntity(classSelected)));
	}

	@Override
	public boolean isAtPosition(Point position)
	{
		return false;
	}

	@Override
	public void paintComponent(Graphics2D g2)
	{
		final Rectangle bounds = parent.getScene().getBounds();

		subArea = new Area(new Rectangle(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight()));

		if (classMouseHover != null)

			addClassClipped(classMouseHover.getBounds(), subArea);

		for (final ClassView cv : classSelected)
		{
			final Rectangle cvBounds = cv.getBounds();
			addClassClipped(cv.getBounds(), subArea);

			g2.setColor(Color.RED.darker());
			g2.setStroke(new BasicStroke(2.6f));
			g2.drawRect(cvBounds.x, cvBounds.y, cvBounds.width + 1, cvBounds.height + 1);
		}

		g2.setColor(new Color(150, 150, 150, 120));
		g2.setClip(subArea);
		g2.fillRect(0 - 1, 0 - 1, bounds.width + 2, bounds.height + 2);
	}

	@Override
	public void repaint()
	{
		parent.getScene().repaint();
	}

	@Override
	public void setBounds(Rectangle bounds)
	{

	}

}
