package graphic.textbox;

import graphic.GraphicView;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Observable;
import java.util.Observer;

import utility.Utility;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Method;
import classDiagram.components.Method.ParametersViewStyle;

/**
 * A TextBox is a graphic component from Slyum containing a String. The
 * particularity of a TextBox is it can be moved with mouse and its String can
 * be edited by double-click on it.
 * 
 * A TextBoxMethod is a TextBox displaying a Method (UML). When editing the
 * text, this TextBox parse the String to change it into a Method. It listening
 * Method changes for auto-update itself.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class TextBoxMethod extends TextBox implements Observer {

	private final Method method;

	/**
	 * Create a new TextBoxMethod with the given Method.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param attribute
	 *            the method
	 */
	public TextBoxMethod(GraphicView parent, Method method) {
		super(parent, method.getStringFromMethod());
		this.method = method;
		method.addObserver(this);
	}

	@Override
	public void createEffectivFont()
	{
		if (method.isAbstract())
			effectivFont = getFont().deriveFont(Font.ITALIC);
		else
			effectivFont = getFont();
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return method;
	}

	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(bounds);
	}

	@Override
	public String getText() {
		return method.getStringFromMethod();
	}
	
	@Override
	public String getEditingText() {
	  return method.getStringFromMethod(ParametersViewStyle.TYPE_AND_NAME);
	}

	@Override
	public void initAttributeString(AttributedString ats)
	{
		if (method.isStatic())
			ats.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 2, ats.getIterator().getEndIndex());
	}

	@Override
	public void setBounds(Rectangle bounds)
	{
		if (bounds == null)
			throw new IllegalArgumentException("bounds is null");

		this.bounds = new Rectangle(bounds);
	}

	@Override
	public void setSelected(boolean select)
	{
		if (isSelected() != select)
		{
			super.setSelected(select);

			method.select();

			if (select)
				method.notifyObservers(UpdateMessage.SELECT);
			else
				method.notifyObservers(UpdateMessage.UNSELECT);
		}
	}

	@Override
	public void setText(String text) {
		method.setText(text);
		super.setText(method.getStringFromMethod());
	}

	@Override
	protected String truncate(Graphics2D g2, String text, int width)
	{
		return Utility.truncate(g2, text, width);
	}

	@Override
	public void update(Observable observable, Object o) {
		if (o != null && o instanceof UpdateMessage) {
			switch ((UpdateMessage) o)
			{
				case SELECT:
					setSelected(true);
					break;
				case UNSELECT:
					setSelected(false);
					break;
      default:
        break;
			}
		} else {
			String text = method.getStringFromMethod();
			super.setText(text);
		}

		repaint();
	}

  @Override
  protected boolean mustPaintSelectedStyle() {
    return mouseHover;
  }
}
