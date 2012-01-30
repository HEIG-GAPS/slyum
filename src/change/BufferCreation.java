package change;

import graphic.GraphicComponent;

public class BufferCreation implements Changeable
{
	private boolean isCreated;
	private GraphicComponent gc;
	
	public BufferCreation(boolean isCreated, GraphicComponent gc)
	{
		this.isCreated = isCreated;
		this.gc = gc;
	}

	@Override
	public void restore()
	{
		if (isCreated)
		
			gc.restore();
		
		else
		{
			final boolean isBlocked = Change.isBlocked();
			Change.setBlocked(true);
			gc.delete();
			Change.setBlocked(isBlocked);
		}
	}
}
