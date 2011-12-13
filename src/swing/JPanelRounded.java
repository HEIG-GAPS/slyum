package swing;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import utility.Utility;

public class JPanelRounded extends JPanel
{
	private static final long serialVersionUID = 767852553660266730L;

	/** Double values for Horizontal and Vertical radius of corner arcs */
	protected Dimension arcs = new Dimension(5, 5);

	/** Distance between shadow border and opaque panel border */
	protected int shadowGap = 5;

	/** Stroke size. it is recommended to set it to 1 for better view */
	protected float strokeSize = 1.3f;

	public JPanelRounded()
	{
		super.setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		final Graphics2D graphics = (Graphics2D) g;
		Utility.setRenderQuality(graphics);

		final int width = getWidth();
		final int height = getHeight();
		final int shadowGap = this.shadowGap;

		// Draws the rounded opaque panel with borders.
		graphics.setColor(getBackground());
		graphics.fillRoundRect(shadowGap, shadowGap, width - shadowGap * 2, height - shadowGap * 2, arcs.width, arcs.height);

		graphics.setColor(getForeground());
		graphics.setStroke(new BasicStroke(strokeSize));
		graphics.drawRoundRect(shadowGap, shadowGap, width - shadowGap * 2, height - shadowGap * 2, arcs.width, arcs.height);

		// Sets strokes to default.
		graphics.setStroke(new BasicStroke());

		Utility.setDefaultRenderQuality(g);
	}

}
