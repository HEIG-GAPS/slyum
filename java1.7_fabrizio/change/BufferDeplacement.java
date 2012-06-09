package change;

import java.awt.Point;
import java.awt.Rectangle;

import graphic.textbox.TextBoxLabel;

public class BufferDeplacement implements Changeable
{
	Point deplacement;
	TextBoxLabel tbl;
	
	public BufferDeplacement(TextBoxLabel tbl)
	{
		this.tbl = tbl;
		deplacement = tbl.getDeplacement();
	}

	@Override
	public void restore()
	{
		Rectangle repaintBounds = tbl.getBounds();
		tbl.setDeplacement(deplacement);
		tbl.repaint();
		tbl.getGraphicView().getScene().repaint(repaintBounds);
	}
	
	public Point getDeplacement()
	{
		return deplacement;
	}

}
