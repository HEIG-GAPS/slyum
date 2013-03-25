package change;

import java.awt.Color;

import graphic.ColoredComponent;
import graphic.GraphicComponent;

public class BufferColor implements Changeable
{
	private ColoredComponent gc;
	private Color color;
	
	public BufferColor(ColoredComponent gc)
	{
		this.gc = gc;
		this.color = gc.getColor();
	}
	
	@Override
	public void restore()
	{
		gc.setColor(color);
		((GraphicComponent)gc).repaint();
	}

}
