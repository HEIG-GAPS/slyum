package utility;

import graphic.GraphicView;
import graphic.textbox.TextBox;

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
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import swing.JPanelRounded;
import swing.PanelClassDiagram;
import swing.PropertyLoader;
import swing.Slyum;

public class SSlider extends JPanelRounded implements MouseListener
{
	private static final long serialVersionUID = 1L;
	private static final String TOOL_TIP_MESSAGE = "Zoom (Ctrl+MouseWheel)(Right click : 100)";
	public final static double SLIDER_HEIGHT = 4.0;
	private final static int ROUDING_SLIDER = 0;
	private final static int MARGIN = 15;

	private int value;
	private int maxValue;
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
		public void mouseClicked(MouseEvent arg0)
		{
			if (arg0.getButton() == MouseEvent.BUTTON3)
				
				setValue(100);
		}


		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			setIsMouseHover(true);
		}


		@Override
		public void mouseExited(MouseEvent arg0)
		{
			setIsMouseHover(false);
		}


		@Override
		public void mousePressed(MouseEvent arg0)
		{
			setIsMousePressed(true);
			
			mousePressedX = arg0.getX();
		}


		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			setIsMousePressed(false);
		}


		@Override
		public void mouseDragged(MouseEvent arg0)
		{
			setLocation(getX() + arg0.getX() - mousePressedX, getY());
			sslider.setValue((getX() * maxValue) / (sslider.getWidth() - 10));
		}
		
		@Override
		public void setLocation(int x, int y)
		{
			if (x > sslider.getWidth() - TICKER_WIDTH*2 || x < TICKER_WIDTH)
				return;

			super.setLocation(x, y);
		}


		@Override
		public void mouseMoved(MouseEvent arg0)
		{
			
		}
	}
	
	public SSlider(Color color, int value)
	{
		this.maxValue = value;
		this.value = maxValue / 2;
		
		setLayout(new GridLayout(1, 7, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(Utility.getAlphaColor(color, 30));
		setForeground(Color.GRAY);

		setMaximumSize(new Dimension(150, 50));

		setLayout(null);
		
		add(ticker = new STicker(this));
		
		setValue(this.value);
		
		addMouseListener(this);
		
		setToolTipText(TOOL_TIP_MESSAGE);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Utility.setRenderQuality(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		Font valueFont = TextBox.getFont().deriveFont(10.0f);
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
		text = "0";
		g2.drawString(text, (float)(x - 2.0), (float)(height + y + 12.0));
		
		FontMetrics fmValue = g.getFontMetrics(valueFont);
		
		text = String.valueOf(maxValue);
		int textwidth = fmValue.stringWidth(text);
		
		g2.drawString(text, (float)(x + width - textwidth/2 - 4.0), (float)(height + y + 12.0));
		
		text = String.valueOf(maxValue/2);
		textwidth = fmValue.stringWidth(text);
		
		g2.drawString(text, (float)(x + width/2 - textwidth/2), (float)(height + y + 12.0));
	}
	
	public void setValue(int value)
	{
		setScale((double)value / 100.0);

		if (PanelClassDiagram.getInstance() == null)
			return;
		
		GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
		gv.setScale((double)value / 100.0);
	}
	
	public void setScale(double scale)
	{
		int value = (int)(scale * 100.0);
		
		if (value < 11 || value > maxValue - 11)
			return;
		
		this.value = value;
		
		ticker.setLocation((int)(((float)value / (float)maxValue) * ((float)getWidth()-10)), ticker.getY());
	}
	
	public int getValue()
	{
		return value;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
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
		if (e.getButton() == MouseEvent.BUTTON3)
			
			setValue(100);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
}
