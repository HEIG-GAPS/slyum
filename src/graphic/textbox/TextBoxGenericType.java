package graphic.textbox;

import graphic.GraphicView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;

import javax.swing.JTextField;

import swing.PropertyLoader;
import utility.Utility;
import classDiagram.components.Entity;
import classDiagram.components.Type;

/**
 * A TextBox is a graphic component from Slyum containing a String. The
 * particularity of a TextBox is it can be moved with mouse and its String can
 * be edited by double-click on it.
 * 
 * A TextBoxGenericType is a TextBox displaying the generic Type of an entity.It listening entity changes for auto-update itself.
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 6-lug-2012
 */
public class TextBoxGenericType extends TextBox
{
	private int classWidth;

	private final Entity entity;

	/**
	 * Create a new TextBoxEntityName with the given entity.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param entity
	 *            the entity
	 */
	public TextBoxGenericType(GraphicView parent, Entity entity)
	{
		super(parent, entity.getGeneric());

		this.entity = entity;
	}

	@Override
	public void createEffectivFont()
	{		
		effectivFont = getFont().deriveFont(Font.HANGING_BASELINE);
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(bounds.x, bounds.y, bounds.width, textDim.height);
	}

	@Override
	public String getText()
	{
		return entity.getGeneric();
	}

	@Override
	public void setBounds(Rectangle bounds)
	{
		super.setBounds(bounds);

		if (textDim.width > bounds.width)

			this.bounds.width = bounds.width;

		classWidth = bounds.width;
	}

	@Override
	public void setText(String text)
	{
		if (!Type.checkSemantic(text))
			return;

		entity.setGeneric(text);
		super.setText(text);
	}

	@Override
	public void stopEditing()
	{
		super.stopEditing();

		entity.notifyObservers();
	}

	@Override
	protected String truncate(Graphics2D g2, String text, int width)
	{
		return Utility.truncate(g2, text, classWidth);
	}
}
