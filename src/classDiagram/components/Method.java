package classDiagram.components;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import utility.PersonnalizedIcon;
import utility.Utility;
import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;

/**
 * Represent a method in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Method extends Observable implements IDiagramComponent, Observer
{
	private boolean _isAbstract = false;

	private boolean _isStatic = false;
	private final Entity entity;
	protected final int id = ClassDiagram.getNextId();
	private String name;
	private final LinkedList<Variable> parameters = new LinkedList<Variable>();
	private Type returnType;
	private Visibility visibility;

	/**
	 * Create a new method.
	 * 
	 * @param name
	 *            the name of the method
	 * @param returnType
	 *            the return type of the method
	 * @param visibility
	 *            the visibility of the method
	 */
	public Method(String name, Type returnType, Visibility visibility, Entity entity)
	{
		if (name.isEmpty())
			throw new IllegalArgumentException("name is null");

		if (returnType == null)
			throw new IllegalArgumentException("type is null");

		if (visibility == null)
			throw new IllegalArgumentException("visibility is null");

		this.entity = entity;
		this.name = name;
		this.returnType = new Type(returnType.getName());
		this.visibility = visibility;
	}
	
	public Method(Method method, Entity newEntity)
	{
		this.entity = newEntity;
		this.name = method.name;
		this.returnType = method.returnType;
		this.visibility = method.visibility;
		this._isAbstract = method._isAbstract;
		this._isStatic = method._isStatic;
		
		for (Variable parameter : method.parameters)
			
			this.parameters.add(new Variable(parameter));
	}

	/**
	 * Add a new parameter.
	 * 
	 * @param parameter
	 *            the new parameter
	 */
	public void addParameter(Variable parameter)
	{
		parameters.add(parameter);
		parameter.addObserver(this);
		setChanged();
	}

	/**
	 * Remove all parameters include in the method.
	 */
	public void clearParameters()
	{
		for (final Variable p : parameters)
			p.deleteObserver(this);

		parameters.clear();
		setChanged();
	}

	@Override
	public int getId()
	{
		return id;
	}

	/**
	 * Get the name of the method.
	 * 
	 * @return the name of the method
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get a copy of the parameters array include in the method.
	 * 
	 * @return a copy of the array of parameters include in the method
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<Variable> getParameters()
	{
		return (LinkedList<Variable>) parameters.clone();
	}

	/**
	 * Get the return type.
	 * 
	 * @return the return type
	 */
	public Type getReturnType()
	{
		setChanged();

		return returnType;
	}

	/**
	 * Get the visibility.
	 * 
	 * @return the visibility
	 */
	public Visibility getVisibility()
	{
		return visibility;
	}

	/**
	 * Get the abstract state of the method.
	 * 
	 * @return the abstract state of the method
	 */
	public boolean isAbstract()
	{
		return _isAbstract;
	}

	/**
	 * Get the static state of the method.
	 * 
	 * @return the static state of the method
	 */
	public boolean isStatic()
	{
		return _isStatic;
	}

	public void moveParameterPosition(Variable parameter, int offset)
	{
		final int index = parameters.indexOf(parameter);

		if (index != -1)
		{
			parameters.remove(parameter);
			parameters.add(index + offset, parameter);

			setChanged();
		}
	}

	public void removeParameters(Variable parameter)
	{
		parameters.remove(parameter);

		setChanged();
	}

	@Override
	public void select()
	{
		setChanged();
	}

	/**
	 * Set the abstract state of the method.
	 * 
	 * @param isAbstract
	 *            the new abstract state
	 */
	public void setAbstract(boolean isAbstract)
	{
		if (isAbstract && !entity.isAbstract())
		{
			JOptionPane.showMessageDialog(null, "Class must be abstract.", "Slyum", JOptionPane.ERROR_MESSAGE, PersonnalizedIcon.getErrorIcon());
			return;
		}

		_isAbstract = isAbstract;

		setChanged();
	}

	/**
	 * Set the name of the method.
	 * 
	 * @param name
	 *            the new name of the method
	 */
	public boolean setName(String name)
	{
		if (name.isEmpty())
			throw new IllegalArgumentException("name is null");

		if (!name.matches("([a-zA-Z|_])(\\w)*"))
			return false;

		this.name = name;

		setChanged();

		return true;
	}

	/**
	 * Set the return type of the method.
	 * 
	 * @param returnType
	 *            the new return type
	 */
	public boolean setReturnType(Type returnType)
	{
		if (returnType == null)
			throw new IllegalArgumentException("returnType is null");

		if (!returnType.getName().matches("([a-zA-Z|_])(\\w)*"))
			return false;

		this.returnType = returnType;

		setChanged();

		return true;
	}

	/**
	 * Set the static state of the method.
	 * 
	 * @param isStatic
	 *            the new static state
	 */
	public void setStatic(boolean isStatic)
	{
		_isStatic = isStatic;
		setChanged();
	}

	public void setText(String text)
	{
		if (text.length() == 0)
			return;

		text = text.trim();
		String newName;
		Visibility newVisibility = Visibility.getVisibility(text.charAt(0));

		if (newVisibility == null)
			newVisibility = getVisibility();
		else
			text = text.substring(1); // Delete the first car (visibility).

		final String[] subString = text.split("\\(");

		newName = subString[0].trim();

		if (!newName.matches("([a-zA-Z|_])(\\w)*"))
			newName = getName();

		if (subString.length == 2)
		{
			final String[] arguments = subString[1].trim().split("\\)");
			clearParameters();

			if (arguments.length > 0 && arguments[0].trim().length() > 0)
			{
				final String[] variables = arguments[0].split(",");

				for (final String v : variables)
				{
					final String[] variable = v.split(":");

					if (variable[0].trim().length() == 0)
						continue;

					if (variable.length != 2)
					{
						if (!variable[0].trim().matches("([a-zA-Z|_])(\\w)*"))
							continue;

						addParameter(new Variable("", new Type(variable[0].trim())));
					}
					else
					{
						if (!variable[0].trim().matches("([a-zA-Z|_])(\\w)*") || !variable[1].trim().matches("([a-zA-Z|_])[(\\w)<>.]*"))
							continue;

						addParameter(new Variable(variable[0].trim(), new Type(variable[1].trim())));
					}
				}
			}

			if (arguments.length > 1)
			{
				final String returnType = arguments[1].substring(arguments[1].indexOf(":") + 1).trim();

				if (!returnType.isEmpty() && returnType.matches("([a-zA-Z|_])[(\\w)<>.]*"))
					setReturnType(new Type(returnType));
			}
		}

		setName(newName);
		setVisibility(newVisibility);
		notifyObservers();
	}

	/**
	 * Set the visibility of the method.
	 * 
	 * @param visibility
	 *            the new visibility
	 */
	public void setVisibility(Visibility visibility)
	{
		if (visibility == null)
			throw new IllegalArgumentException("visibility is null");

		this.visibility = visibility;

		setChanged();
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		String xml = tab + "<method " + "name=\"" + name + "\" returnType=\"" + returnType + "\" visibility=\"" + visibility + "\" isStatic=\"" + _isStatic + "\" isAbstract=\"" + _isAbstract + "\" ";

		if (parameters.size() == 0)

			return xml += "/>";

		xml += ">\n";

		for (final Variable variable : parameters)
			xml += variable.toXML(depth + 1) + "\n";

		return xml + tab + "</method>";
	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		// parameter's changed
		setChanged();
	}
}
