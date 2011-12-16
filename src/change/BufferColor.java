package change;

import java.awt.Color;

import graphic.GraphicComponent;

public class BufferColor implements Changeable
{
	private GraphicComponent gc;
	private Color color;
	
	public BufferColor(GraphicComponent gc)
	{
		this.gc = gc;
		this.color = gc.getColor();
	}
	
	@Override
	public void restore()
	{
		gc.setColor(color);
		gc.repaint();
	}

}
