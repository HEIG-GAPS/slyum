package graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import utility.Utility;

public abstract class ButtonCross extends GraphicComponent
{
	public static int EDGE_SIZE = 24;
	private Rectangle bounds = new Rectangle(EDGE_SIZE, EDGE_SIZE);
	private ImageIcon image;
	private boolean isMouseHover = false;
	private boolean isMousePressed = false;
	
	public ButtonCross(GraphicView parent, ImageIcon image)
	{
		super(parent);
		
		this.image = image;
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(bounds);
	}

	@Override
	public boolean isAtPosition(Point position)
	{
		return getBounds().contains(position);
	}

	@Override
	public void paintComponent(Graphics2D g2)
	{
		Rectangle bounds = getBounds();
		
		Utility.setRenderQuality(g2);
		
		g2.drawImage(image.getImage(), bounds.x, bounds.y, null);
		
		if (isMouseHover || isMousePressed)
		{
			Color color2 = new Color(0, 255, 0, 20);
			Color color1;
			
			if (isMousePressed)
				
				color1 = new Color(100, 140, 100, 200);
			
			else
				
				color1 = new Color(200, 240, 200, 200);
			
			final GradientPaint gp = new GradientPaint(bounds.x, bounds.y, color1, bounds.x + bounds.width, bounds.y + bounds.height, color2);
			g2.setPaint(gp);
			
			g2.fillRect(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);
			
			g2.setColor(Color.DARK_GRAY);
			g2.setStroke(new BasicStroke(1.3f));
			g2.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);
		}
	}
	
	@Override
	public void gMouseEntered(MouseEvent e)
	{
		super.gMouseEntered(e);
		setMouseHover(true);
	}
	
	@Override
	public void gMouseExited(MouseEvent e)
	{
		super.gMouseExited(e);
		setMouseHover(false);
	}
	
	public void setMouseHover(boolean mouseHover)
	{
		isMouseHover = mouseHover;
		repaint();
	}
	
	public void setMousePressed(boolean mousePressed)
	{
		isMousePressed = mousePressed;
		repaint();
	}

	@Override
	public void repaint()
	{
		parent.getScene().repaint(getBounds());
	}

	@Override
	public void setBounds(Rectangle newbounds)
	{
		Rectangle oldBounds = getBounds();
		
		bounds = new Rectangle(newbounds);
		
		parent.getScene().repaint(oldBounds);
		parent.getScene().repaint(bounds);
		
	}
	
	public void setLocation(Point location)
	{
		setBounds(new Rectangle(location.x, location.y, bounds.width, bounds.height));
	}
	
	@Override
	public void gMouseReleased(MouseEvent e)
	{
		super.gMouseReleased(e);
		
		mouseClick();
		
		setMousePressed(false);
	}
	
	@Override
	public void gMousePressed(MouseEvent e)
	{
		super.gMousePressed(e);
		
		setMousePressed(true);
	}
	
	public abstract void mouseClick();

}
