package utility;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import swing.JPanelRounded;

public class SSlider extends JPanelRounded implements MouseListener, MouseWheelListener
{
	private static final long serialVersionUID = 1L;
	private static String toolTipMessage;
	public final static double SLIDER_HEIGHT = 4.0;
	private final static int ROUDING_SLIDER = 0;
	private final static int MARGIN = 15;

	private int value;
	private int maxValue = 100;
	private int minValue = 0;
	private int defaultValue;
	private STicker ticker;
	
	private class STicker extends JPanel implements MouseListener, MouseMotionListener
	{
		private static final long serialVersionUID = 3620432451573178768L;
		private static final int TICKER_WIDTH = 10;
		private static final int TICKER_HEIGHT = 20;
		private final static int ROUDING_TICKER = 0;
		
		private SSlider sslider;
		
		private boolean isMouseHover = false;
		private boolean isMousePressed = false;
		private int mousePressedX;

		public STicker(SSlider sslider)
		{
			setOpaque(false);
			this.sslider = sslider;
			setBorder(null);
			
			Dimension sbDim = sslider.getMaximumSize();
			setBounds((sbDim.width - TICKER_WIDTH) / 2, /*(sbDim.height - TICKER_HEIGHT) / 2*/ 9, TICKER_WIDTH, TICKER_HEIGHT);
			
			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(sslider);
		}
		
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			
			Utility.setRenderQuality(g);
			
			Graphics2D g2 = (Graphics2D)g;
			
			Rectangle bounds = getBounds();
			RoundRectangle2D ticker = new RoundRectangle2D.Float(1, 1, bounds.width - 3, bounds.height - 3, ROUDING_TICKER, ROUDING_TICKER);
			
			GradientPaint gp;
			Point gpPoint1 = new Point(bounds.x, bounds.y),
				  gpPoint2 = new Point(bounds.x, bounds.y + bounds.height - 5);
			
			Color gpColor1 = Color.LIGHT_GRAY,
			      gpColor2 = Color.GRAY;
			
			if (isMousePressed)
				gp = new GradientPaint(gpPoint1, gpColor2, gpPoint2, gpColor1);
			else
				gp = new GradientPaint(gpPoint1, gpColor1, gpPoint2, gpColor2);
			
			g2.setStroke(new BasicStroke(1.3f));
			g2.setPaint(gp);
			g2.fill(ticker);
			
			Color borderColor;
			
			if (isMouseHover)
				borderColor = Color.LIGHT_GRAY;
			else
				borderColor = Color.GRAY;
			
			g2.setColor(borderColor);
			g2.draw(ticker);
		}
		
		public void setIsMouseHover(boolean enable)
		{
			isMouseHover = enable;
			
			repaint();
		}
		
		public void setIsMousePressed(boolean enable)
		{
			isMousePressed = enable;
			
			repaint();
		}


		@Override
		public void mouseClicked(MouseEvent e)
		{
			sslider.mouseClicked(e);
		}


		@Override
		public void mouseEntered(MouseEvent e)
		{
			setIsMouseHover(true);
		}


		@Override
		public void mouseExited(MouseEvent e)
		{
			setIsMouseHover(false);
		}


		@Override
		public void mousePressed(MouseEvent e)
		{
			setIsMousePressed(true);
			
			mousePressedX = e.getX();
		}


		@Override
		public void mouseReleased(MouseEvent e)
		{
			setIsMousePressed(false);
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			int x = getX() + e.getX() - mousePressedX;
			
			sslider.setValue(getRelativeValue(x));
		}
		
		private int getRelativeValue(int location)
		{
			int sWidth = getSliderWidth();
			
			if (sWidth == 0)
				return minValue;
			
			location -= MARGIN - getWidth()/2;
			int range = maxValue - minValue;
			
			return ((location * range) / sWidth) + minValue;
		}
		
		private int getAbsoluteValue(int value)
		{
			if (maxValue == 0)
				return minValue;
			
			int sWidth = getSliderWidth();
			int range = maxValue - minValue;
			
			return ((value - minValue) * sWidth) / range + MARGIN - getWidth()/2;
		}
		
		private int getSliderWidth()
		{
			return sslider.getWidth() - MARGIN*2;
		}


		@Override
		public void mouseMoved(MouseEvent arg0)
		{
		}
	}
	
	public SSlider(Color color, final int defaultValue, int minValue, int maxValue) 
	{
		toolTipMessage = "Zoom (Ctrl+MouseWheel)(Right click : " + defaultValue + ")";
				
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.defaultValue = defaultValue;
		
		setLayout(new GridLayout(1, 7, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(Utility.getAlphaColor(color, 30));
		setForeground(Color.GRAY);

		setMaximumSize(new Dimension(150, 50));
		setSize(getMaximumSize());

		setLayout(null);
		
		add(ticker = new STicker(this));
		
		addMouseListener(this);
		addMouseWheelListener(this);
		
		setToolTipText(toolTipMessage);
		
		setValue(defaultValue);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{		
		super.paintComponent(g);
		Utility.setRenderQuality(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		Font valueFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
		Rectangle2D bounds = getBounds().getBounds2D();
		String text;
		
		double x = MARGIN, y = (bounds.getHeight() - SLIDER_HEIGHT) / 2.0, width = bounds.getWidth() - MARGIN * 2, height = SLIDER_HEIGHT;
		
		RoundRectangle2D slider = new RoundRectangle2D.Double(x, y - 6.0, width, height, ROUDING_SLIDER, ROUDING_SLIDER);
		GradientPaint gpBackground = new GradientPaint(new Point((int)x, (int)y-11), Color.DARK_GRAY, new Point((int)x, (int)(y + height)), Utility.getAlphaColor(Color.YELLOW, 100));

		g2.setPaint(gpBackground);
		g2.fill(slider);
		
		g2.setStroke(new BasicStroke(1.3f));
		g2.setColor(Color.GRAY);
		g2.draw(slider);
		
		g2.setFont(valueFont);
		text = String.valueOf(minValue);
		g2.drawString(text, (float)(x - 2.0), (float)(height + y + 12.0));
		
		FontMetrics fmValue = g.getFontMetrics(valueFont);
		
		text = String.valueOf(maxValue);
		int textwidth = fmValue.stringWidth(text);
		
		g2.drawString(text, (float)(x + width - textwidth/2 - 4.0), (float)(height + y + 12.0));
		
		text = String.valueOf(value);
		textwidth = fmValue.stringWidth(text);
		
		g2.drawString(text, (float)(x + width/2 - textwidth/2), (float)(height + y + 12.0));
	}
	
	public void setValue(int value)
	{
		if (value > maxValue) value = maxValue;
		
		if (value < minValue) value = minValue;
		
		int x = ticker.getAbsoluteValue(value),
			y = ticker.getY();
		
		ticker.setLocation(x, y);
		this.value = value;
		repaint();
	}
	
	public int getValue()
	{
		return value;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
			
			setValue(defaultValue);
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		setValue(getValue() + (int)((maxValue - minValue) / 10.0) * -e.getWheelRotation());
	}
}
