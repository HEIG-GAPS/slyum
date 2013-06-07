package graphic.textbox;

import graphic.GraphicView;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import utility.Utility;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Method;
import classDiagram.components.Variable;

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
  
	/**
	 * Enumeration class for the mode of display parameters in methods.
	 * 
	 * @author David Miserez
	 * @version 1.0 - 25.07.2011
	 */
	public enum ParametersViewStyle { 
	  DEFAULT, NAME, NOTHING , TYPE, TYPE_AND_NAME;
	  
	  @Override
	  public String toString() {
	    return super.toString().charAt(0) + super.toString().substring(1).toLowerCase().replace('_', ' ');
	  };
	};

	/**
	 * Get a String representing the Method.
	 * 
	 * @param method
	 *            the method to convert to String
	 * @param style
	 *            the style of display for parameters
	 * @return the string converted
	 */
	public static String getStringFromMethod(Method method, ParametersViewStyle style)
	{
		String signature = method.getVisibility().toCar() + " " + method.getName() + " (";
		final LinkedList<Variable> parameters = method.getParameters();

		if (style != ParametersViewStyle.NOTHING)

			for (int i = 0; i < parameters.size(); i++)
			{
				if (!parameters.get(i).getName().isEmpty())
					if (style == ParametersViewStyle.TYPE_AND_NAME)

						signature += parameters.get(i).getName() + " : " + parameters.get(i).getType();

					else if (style == ParametersViewStyle.NAME)

						signature += parameters.get(i).getName();

				if (style == ParametersViewStyle.TYPE)

					signature += parameters.get(i).getType();

				if (i < parameters.size() - 1)
					signature += ", ";
			}

		return signature + ") : " + method.getReturnType();
	}

	private ParametersViewStyle currentStyle;

	private final Method method;

	/**
	 * Create a new TextBoxMethod with the given Method.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param attribute
	 *            the method
	 */
	public TextBoxMethod(GraphicView parent, Method method)
	{
		super(parent, getStringFromMethod(method, GraphicView.getDefaultViewMethods()));

		this.method = method;
		method.addObserver(this);
		currentStyle = ParametersViewStyle.DEFAULT;
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

	/**
	 * Get the style of displaying parameters.
	 * 
	 * @return the style of displaying parameters
	 */
	public ParametersViewStyle getParametersViewStyle()
	{
	  if (currentStyle == ParametersViewStyle.DEFAULT)
	    return GraphicView.getDefaultViewMethods();
	  
		return currentStyle;
	}
	
	public ParametersViewStyle getConcretParametersViewStyle() {
    return currentStyle;
	}

	@Override
	public String getText()
	{
		return getStringFromMethod(method, getParametersViewStyle());
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

	/**
	 * Change the style of displaying parameters.
	 * 
	 * @param newStyle
	 *            the new style
	 */
	public void setParametersViewStyle(ParametersViewStyle newStyle)
	{
		currentStyle = newStyle;

		super.setText(getText());
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
	public void setText(String text)
	{
		method.setText(text);
		super.setText(getStringFromMethod(method, getParametersViewStyle()));
	}

	@Override
	protected String truncate(Graphics2D g2, String text, int width)
	{
		return Utility.truncate(g2, text, width);
	}

	@Override
	public void update(Observable observable, Object o)
	{
		if (o != null && o instanceof UpdateMessage)
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
		else
		{
			String text = getStringFromMethod(method, getParametersViewStyle());
			super.setText(text);
		}

		repaint();
	}

}
