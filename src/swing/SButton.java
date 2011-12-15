package swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JButton;

import utility.Utility;

public class SButton extends JButton implements MouseListener
{
	private static final long serialVersionUID = -359453798459739030L;
	
	private boolean isMouseHover = false, isMouseClicked = false;
	
	private Color themeColor;
	
	private Component linkedComponent;

	public SButton(Icon icon, String action, Color color, String tooltip)
	{
		super(icon);
		
		init(action, color, tooltip);
	}
	
	public SButton(String text, String action, Color color, String tooltip)
	{
		super(text);
		init(action, color, tooltip);
	}
	
	private void init(String action, Color color, String tooltip)
	{
		setActionCommand(action);
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Slyum.getInstance().actionPerformed(e);
			}
		});
		setBorderPainted(false);
		setContentAreaFilled(false);
		setAlignmentX(CENTER_ALIGNMENT);
		setToolTipText(tooltip);
		
		themeColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 10);
		
		addMouseListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Utility.setRenderQuality(g);
		
		Graphics2D g2 = (Graphics2D)g;

		if (isEnabled() && (isMouseHover || isMouseClicked))
		{
			g2.setColor(themeColor);
			g2.fillRect(0, 0, getWidth()-1, getHeight()-1);

			g2.setStroke(new BasicStroke(1.3f));
			
			RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 	10, 10);
			
			g2.setColor(Color.GRAY);
			g2.draw(rect);
			
			Color color1 = new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 10);
			Color color2 = new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 40);
			
			if (isMouseHover)
			{
				GradientPaint gp;
				
				if (isMouseClicked)
						gp = new GradientPaint(new Point(1, 1), color2, new Point(1, getHeight() - 2), color1); 
					else
						gp = new GradientPaint(new Point(1, 1), color1, new Point(1, getHeight() - 2), color2);
				
				g2.setPaint(gp);
				g2.fill(rect);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
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
		setIsMouseClicked(true);
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		setIsMouseClicked(false);
		
	}
	
	public void setIsMouseHover(boolean hover)
	{
		isMouseHover = hover;
		repaint();
	}
	
	public void setIsMouseClicked(boolean clicked)
	{
		isMouseClicked = clicked;
		repaint();
	}
	
	@Override
	public void setEnabled(boolean b)
	{
		super.setEnabled(b);
		
		if (linkedComponent != null)
			linkedComponent.setEnabled(b);
	}
	
	public void linkComponent(Component c)
	{
		linkedComponent = c;
	}
}