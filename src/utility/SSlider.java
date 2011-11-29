package utility;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import swing.JPanelRounded;

public class SSlider extends JPanelRounded
{
	public final static double SLIDER_HEIGHT = 5.0;
	private final static int ROUDING_SLIDER = 5;
	private final static int MARGIN = 15;
	
	private static final long serialVersionUID = 1L;

	private Color color;
	
	private class STicker extends JPanel
	{
		private static final int TICKER_SIZE = 10;
		
		private int value;
		private SSlider sslider;
		
		public STicker(SSlider sslider, int value)
		{
			setOpaque(false);
			setLayout(null);
			this.sslider = sslider;
			this.value = value;
			Rectangle sliderBounds = sslider.getBounds();
			
			setBounds(0, sliderBounds.height - (int)SLIDER_HEIGHT / 2, TICKER_SIZE, TICKER_SIZE*3);
		}
		
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			Utility.setRenderQuality(g);
			
			Rectangle sliderBounds = sslider.getBounds();
			
			setBounds(0, sliderBounds.height - (int)SLIDER_HEIGHT / 2, TICKER_SIZE, TICKER_SIZE*3);
			System.out.println(getBounds());
			
			Graphics2D g2 = (Graphics2D)g;
			
			Rectangle bounds = getBounds();
			
			RoundRectangle2D ticker = new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width - 2, bounds.height - 2, 10, 10);
			
			g2.setStroke(new BasicStroke(1.5f));
			g2.setColor(sslider.getAlphaColor(Color.LIGHT_GRAY, 100));
			g2.fill(ticker);
			
			g2.setColor(Color.GRAY);
			g2.draw(ticker);
		}
	}
	
	public SSlider(Color color)
	{
		this.color = color;
		setLayout(new GridLayout(1, 7, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(getAlphaColor(color, 30));
		setForeground(Color.GRAY);

		setMaximumSize(new Dimension(150, 50));

		setLayout(null);
		
		add(new STicker(this, 500));
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Utility.setRenderQuality(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		Rectangle2D bounds = getBounds().getBounds2D();
		
		double x = MARGIN, y = (bounds.getHeight() - SLIDER_HEIGHT) / 2.0, width = bounds.getWidth() - MARGIN * 2, height = SLIDER_HEIGHT;
		
		RoundRectangle2D slider = new RoundRectangle2D.Double(x, y, width, height, ROUDING_SLIDER, ROUDING_SLIDER);
		GradientPaint gpBackground = new GradientPaint(new Point((int)x, (int)y-2), Color.DARK_GRAY, new Point((int)x, (int)(y + height)), getAlphaColor(Color.YELLOW, 100));

		g2.setPaint(gpBackground);
		g2.fill(slider);
		
		g2.setStroke(new BasicStroke(1.5f));
		g2.setColor(Color.GRAY);
		g2.draw(slider);
	}
	
	protected Color getAlphaColor(Color color, int alpha)
	{
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
}
