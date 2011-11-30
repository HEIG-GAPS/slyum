package utility;

import graphic.GraphicComponent;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import swing.PropertyLoader;

import classDiagram.components.Visibility;
import classDiagram.relationships.Multiplicity;

/**
 * Utilities class containing tools methods.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Utility
{
	public final static GraphicQualityType GRAPHIQUE_QUALITY_TYPE = GraphicQualityType.MAX;
	
	public enum GraphicQualityType
	{
		LOW, MAX, MEDIUM
	};

	/**
	 * http://www.exampledepot.com/egs/java.awt.datatransfer/ToClipImg.html
	 */
	public static class ImageSelection implements Transferable
	{
		private final Image image;

		public ImageSelection(Image image)
		{
			this.image = image;
		}

		// Returns image
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
		{
			if (!DataFlavor.imageFlavor.equals(flavor))
				throw new UnsupportedFlavorException(flavor);
			return image;
		}

		// Returns supported flavors
		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		// Returns true if flavor is supported
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			return DataFlavor.imageFlavor.equals(flavor);
		}
	}

	private static GraphicQualityType graphicQualityType = GraphicQualityType.MAX;

	/**
	 * Get a string representing the tag geometry in the XML structure.
	 * 
	 * @param depth
	 *            the number of tabs before each tags
	 * @param bounds
	 *            the bounds to put into XML tags
	 * @param baliseName
	 *            the name of the balise
	 * @return the string representing a geometry tags with the bounds given.
	 */
	public static String boundsToXML(int depth, Rectangle bounds, String baliseName)
	{
		final String tab = Utility.generateTab(depth);

		return tab + "\t<" + baliseName + ">\n" + tab + "\t\t<x>" + bounds.x + "</x>\n" + tab + "\t\t<y>" + bounds.y + "</y>\n" + tab + "\t\t<w>" + bounds.width + "</w>\n" + tab + "\t\t<h>" + bounds.height + "</h>\n" + tab + "\t</" + baliseName + ">\n";
	}

	/**
	 * Compute a point on the segment defined by p1 and p2 that are the nearest
	 * point from p3. This algorithme was found on :
	 * http://paulbourke.net/geometry/pointline/
	 * 
	 * @param p1
	 *            the first point defined a segment
	 * @param p2
	 *            the second point defined a segment
	 * @param p3
	 *            the point used for find the nearest point on the segment (p1,
	 *            p2)
	 * @return
	 */
	public static Point2D distanceToSegment(Point2D p1, Point2D p2, Point2D p3)
	{
		final double xDelta = p2.getX() - p1.getX();
		final double yDelta = p2.getY() - p1.getY();

		if (xDelta == 0 && yDelta == 0)

			return p1;

		final double u = ((p3.getX() - p1.getX()) * xDelta + (p3.getY() - p1.getY()) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

		Point2D closestPoint;

		if (u < 0)

			closestPoint = p1;

		else if (u > 1)

			closestPoint = p2;

		else

			closestPoint = new Point2D.Double(p1.getX() + u * xDelta, p1.getY() + u * yDelta);

		return closestPoint;
	}

	/**
	 * Find a JMenuItem in the JPopupMenu given that correspond to the given
	 * text. Return null if no JMenuItem is found. The given text must not be
	 * the exact title of the JMenuItem, string compare use startsWith() method.
	 * 
	 * @param menu
	 *            the popup menu where find the JMenuItem
	 * @param text
	 *            the title of the JMenuItem to find.
	 * @return the JMenuItem find; or null if no JMenuItem has this title.
	 */
	public static JMenuItem fintMenuItem(JPopupMenu menu, String text)
	{
		for (final Component component : menu.getComponents())

			if (component instanceof JMenuItem)

				if (((JMenuItem) component).getText().startsWith(text))

					return (JMenuItem) component;

		return null;
	}

	/**
	 * Return a string containing a 'number' of '\t'.
	 * 
	 * @param number
	 *            the number of '\t'
	 * @return a string with 'number' tabs
	 */
	public static String generateTab(int number)
	{
		String tab = "";

		for (int i = 0; i < number; i++)
			tab += "\t";

		return tab;
	}

	/**
	 * Return the complementary color. The complementary color is calculated in
	 * the way define here :
	 * http://help.adobe.com/fr_FR/Illustrator/13.0/help.html
	 * ?content=WS714a382cdf7d304e7e07d0100196cbc5f-6288.html
	 * 
	 * @param color
	 *            the color to compute its complementary
	 * @return the complementary color
	 */
	public static Color getComplementary(Color color)
	{
		final int r = color.getRed();
		final int g = color.getGreen();
		final int b = color.getBlue();

		final int min = Math.min(Math.min(r, g), b);
		final int max = Math.max(Math.max(r, g), b);

		final int add = min + max;

		final int newR = add - r;
		final int newG = add - g;
		final int newB = add - b;

		return new Color(newR, newG, newB);
	}

	/**
	 * Get the extension of a file. Return null if the file has no extension.
	 * (find on : http://download.oracle.com/)
	 * 
	 * @param f
	 *            the file to find its extension
	 * @return the extension of the file; or null if no extension are found.
	 */
	public static String getExtension(File f)
	{
		String ext = null;
		final String s = f.getName();
		final int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
			ext = s.substring(i + 1).toLowerCase();
		return ext;
	}
	
	public static GraphicQualityType getGraphicQualityType()
	{
		String graphiqueQuality = PropertyLoader.getInstance().getProperties().getProperty("GraphicQuality");
		GraphicQualityType gqt = GRAPHIQUE_QUALITY_TYPE;
		
		if (graphiqueQuality != null)
			gqt = GraphicQualityType.valueOf(graphiqueQuality);
		
		return gqt;
	}

	/**
	 * Compute the gray level of a color. The gray level is calculated by adding
	 * the hightest RGB component with the lowest and divided by 2. The gray
	 * level is a float value between 0.0 and 1.0f. This method return a color
	 * with RGB equals 100 if the gray level is less than 0.5f, 200 otherwise.
	 * It permit to find a gray color who are visible on the given color (except
	 * if the color is a gray near of 127 RGB).
	 * 
	 * @param color
	 * @return
	 */
	public static int getGrayLevel(Color color)
	{
		final float rp = color.getRed() / 255.0f;
		final float gp = color.getGreen() / 255.0f;
		final float bp = color.getBlue() / 255.0f;

		final float max = Math.max(Math.max(rp, gp), bp);
		final float min = Math.min(Math.min(rp, gp), bp);

		return (max + min) / 2.0f > 0.5f ? 100 : 200;
	}

	/**
	 * Return a set of keys that corresponding to the given value in a Map. Fint
	 * on :
	 * http://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key
	 * -from-value
	 * 
	 * @param map
	 * @param value
	 * @return
	 */
	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value)
	{
		final Set<T> keys = new HashSet<T>();
		for (final Entry<T, E> entry : map.entrySet())
			if (entry.getValue().equals(value))
				keys.add(entry.getKey());
		return keys;
	}

	/**
	 * Return a rectangle englobing all graphic components from the given list.
	 * 
	 * @param components
	 *            the graphic components
	 * @return a rectangle englobing all graphic components from the given list
	 */
	public static Rectangle getLimits(LinkedList<? extends GraphicComponent> components)
	{
		if (components.size() == 0)
			return new Rectangle();

		int top = Integer.MAX_VALUE;
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int bottom = Integer.MIN_VALUE;

		for (final GraphicComponent c : components)
		{
			final Rectangle current = c.getBounds();

			// find lower y value
			if (top > current.y)
				top = current.y;

			// find lower x value
			if (left > current.x)
				left = current.x;

			// find rightmost x value (for width)
			if (right < current.x + current.width)
				right = current.x + current.width;

			// find hightest y value (for hight)
			if (bottom < current.y + current.height)
				bottom = current.y + current.height;
		}

		return new Rectangle(left, top, right - left, bottom - top);
	}

	/**
	 * Return a JComboBox containing all default multiplicities.
	 * 
	 * @return a JComboBox containing all default multiplicities
	 */
	public static JComboBox getMultiplicityComboBox()
	{
		final JComboBox cmb = new JComboBox();
		cmb.setEditable(true);

		cmb.addItem(Multiplicity.ONE_ONLY);
		cmb.addItem(Multiplicity.ONE_OR_MORE);
		cmb.addItem(Multiplicity.ZERO);
		cmb.addItem(Multiplicity.ZERO_OR_MORE);
		cmb.addItem(Multiplicity.ZERO_OR_ONE);

		cmb.setSelectedIndex(0);

		return cmb;
	}

	/**
	 * Return a JComboBox containing all default visibilities.
	 * 
	 * @return a JComboBox containing all default visibilities
	 */
	public static JComboBox getVisibilityComboBox()
	{
		final Object[] list = new Object[Visibility.values().length];

		for (int i = 0; i < list.length; i++)
			list[i] = Visibility.values()[i].getName();

		return new JComboBox(list);
	}

	/**
	 * Grow rectangle in this way: x-size, y-size, width+size*2, height+size*2.
	 * 
	 * @param rect
	 *            Rectangle to grow.
	 * @param size
	 *            Size to add.
	 * @return The rectangle elarged.
	 */
	public static Rectangle growRectangle(Rectangle rect, int size)
	{
		final int size2 = size * 2;

		return new Rectangle(rect.x - size, rect.y - size, rect.width + size2, rect.height + size2);
	}

	/**
	 * Normalize the given rect. If the given rectangle have some negative
	 * values, compute a new rectangle having the same bounds, but with only
	 * positive values.
	 * 
	 * @param rect
	 *            the rectangle to normalize
	 * @return the normalized rectangle
	 */
	public static Rectangle normalizeRect(Rectangle rect)
	{
		final Rectangle normalizedRect = new Rectangle();

		normalizedRect.width = Math.abs(rect.width);
		normalizedRect.height = Math.abs(rect.height);

		normalizedRect.x = rect.width < 0 ? rect.x + rect.width : rect.x;
		normalizedRect.y = rect.height < 0 ? rect.y + rect.height : rect.y;

		return normalizedRect;
	}

	/**
	 * Put the default render hints to the given graphic.
	 * 
	 * @param g
	 *            the graphic context
	 */
	public static void setDefaultRenderQuality(Graphics g)
	{
		final Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);

		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);

	}

	public static void setGraphicQualityType(GraphicQualityType type)
	{
		PropertyLoader.getInstance().getProperties().put("GraphicQuality", type);
		PropertyLoader.getInstance().push();
	}

	/**
	 * Activate all renderhint used in Slyum for the graphic context given.
	 * 
	 * @param g
	 *            the graphic context
	 */
	public static void setRenderQuality(Graphics g)
	{
		if (getGraphicQualityType().equals(GraphicQualityType.LOW))
			return;

		final Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (getGraphicQualityType().equals(GraphicQualityType.MEDIUM))
			return;

		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

	}

	/**
	 * Truncate the given text that its width is equals to the given width. The
	 * graphic context is used to compute text size. If text must be resized,
	 * '...' will be add at its end.
	 * 
	 * @param g2
	 *            the graphi context
	 * @param text
	 *            the text to truncate
	 * @param width
	 *            the width of the text
	 * @return the text truncate if too long; return empty text if text can't be
	 *         truncate in specified width.
	 */
	public static String truncate(Graphics2D g2, String text, int width)
	{
		final String carTrunc = "...";

		final FontMetrics metrics = g2.getFontMetrics(g2.getFont());

		int adv = metrics.stringWidth(text);

		if (adv < width)
			return text;

		text += carTrunc;

		do
		{
			if (text.length() <= 3)
				return ""; // If text can't be truncate (the
			// '...' is longer than the
			// specified width).

			text = text.substring(0, text.length() - carTrunc.length() - 1) + carTrunc;
			adv = metrics.stringWidth(text);
		} while (adv > width);

		return text;
	}

	protected static Color getAlphaColor(Color color, int alpha)
	{
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
}
