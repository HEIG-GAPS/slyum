package classDiagram.components;

import change.BufferMethod;
import change.Change;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import utility.SMessageDialog;
import utility.Utility;
import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;
import classDiagram.verifyName.MethodName;
import classDiagram.verifyName.TypeName;
import classDiagram.verifyName.VariableName;
import graphic.textbox.TextBoxMethod;

/**
 * Represent a method in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Method extends Observable implements IDiagramComponent, Observer
{
	public static final String REGEX_SEMANTIC_METHOD = Variable.REGEX_SEMANTIC_ATTRIBUTE;
	
	public static boolean checkSemantic(String name)
	{
		return name.matches(REGEX_SEMANTIC_METHOD);
	}
	
	private boolean _isAbstract = false;
	private boolean _isStatic = false;
	private final Entity entity;
	protected final int id = ClassDiagram.getNextId();
	private String name;
	private final LinkedList<Variable> parameters = new LinkedList<>();
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
		if (returnType == null)
			throw new IllegalArgumentException("type is null");

		if (visibility == null)
			throw new IllegalArgumentException("visibility is null");
		
		boolean isBlocked = Change.isBlocked();
		Change.setBlocked(true);
		
		if (!setName(name))
			throw new IllegalArgumentException("semantic name incorrect");
			
		this.entity = entity;
		setReturnType(returnType);
		this.visibility = visibility;
		
		Change.setBlocked(isBlocked);
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
	 * Constructor of copy.
	 * @param method method
	 */
	public Method(Method method)
	{
		this(method, method.entity);
	}
	
	public void setMethod(Method method)
	{
		boolean isRecord = Change.isRecord();
		Change.record();
		
		this.name = method.name;
		this.returnType = method.returnType;
		this.visibility = method.visibility;
		this._isAbstract = method._isAbstract;
		this._isStatic = method._isStatic;
		
		clearParameters();
		
		for (Variable parameter : method.parameters)
			
			this.parameters.add(new Variable(parameter));
		
		if (!isRecord)
			Change.stopRecord();		
		
		notifyObservers();
	}

	/**
	 * Add a new parameter.
	 * 
	 * @param parameter
	 *            the new parameter
	 */
	public void addParameter(Variable parameter)
	{
		Change.push(new BufferMethod(this));
		
		parameters.add(parameter);
		
		Change.push(new BufferMethod(this));
		
		parameter.addObserver(this);
		
		setChanged();
	}

	/**
	 * Remove all parameters include in the method.
	 */
	public void clearParameters()
	{
		boolean isRecord = Change.isRecord();
		Change.record();
		
		while (parameters.size() > 0)
			removeParameters(parameters.getFirst());
		
		if (!isRecord)
			Change.stopRecord();
		
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
		Change.push(new BufferMethod(this));
		parameters.remove(parameter);
		parameter.deleteObserver(this);
		Change.push(new BufferMethod(this));

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
		if (isAbstract() == isAbstract)
			return;
		
		if (isAbstract && !entity.isAbstract())
		{
			SMessageDialog.showErrorMessage("Class must be abstract.");
			return;
		}

		Change.push(new BufferMethod(this));
		_isAbstract = isAbstract;
		Change.push(new BufferMethod(this));

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
		if (!MethodName.getInstance().verifyName(name) || name.equals(getName()))
			return false;

		Change.push(new BufferMethod(this));
		this.name = name;
		Change.push(new BufferMethod(this));

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
		
		if (getReturnType() != null && returnType.getName().equals(getReturnType().getName()))
			return false;

		Change.push(new BufferMethod(this));
		this.returnType = returnType;
		Change.push(new BufferMethod(this));

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
		if (isStatic() == isStatic)
			return;
		
		Change.push(new BufferMethod(this));
		_isStatic = isStatic;
		Change.push(new BufferMethod(this));
		
		setChanged();
	}

	public void setText(String text)
	{
		if (text.length() == 0 || text.equals(TextBoxMethod.getStringFromMethod(this, TextBoxMethod.ParametersViewStyle.TYPE_AND_NAME)))
			return;

		String returnType = getReturnType().getName();
		LinkedList<Variable> par = new LinkedList<>();
		text = text.trim();
		String newName;
		Visibility newVisibility = Visibility.getVisibility(text.charAt(0));

		if (newVisibility == null)
			newVisibility = getVisibility();
		else
			text = text.substring(1); // Delete the first car (visibility).

		final String[] subString = text.split("\\(");

		newName = subString[0].trim();

		if (!MethodName.getInstance().verifyName(newName))
			newName = getName();

		if (subString.length == 2)
		{
			final String[] arguments = subString[1].trim().split("\\)");

			if (arguments.length > 0 && arguments[0].trim().length() > 0)
			{
				final String[] variables = arguments[0].split(",");

				for (final String v : variables)
				{
					final String[] variable = v.split(":");

					if (variable[0].trim().length() == 0)
						continue;

					if (variable.length == 2)
					{
						String name = variable[0].trim(), 
							   type = variable[1].trim();
						
						if (!VariableName.getInstance().verifyName(name) || !TypeName.getInstance().verifyName(type))
							continue;

						par.add(new Variable(name, new Type(type)));
					}
				}
			}

			if (arguments.length > 1)
			{
				String rt = arguments[1].substring(arguments[1].indexOf(":") + 1).trim();
				if (TypeName.getInstance().verifyName(rt))
					returnType = rt;
			}
		}

		boolean isRecord = Change.isRecord();
		Change.record();
		
		if (subString.length == 2)
			clearParameters();
		
		setName(newName);
		setVisibility(newVisibility);
		setReturnType(new Type(returnType));
		
		for (Variable v : par)
			addParameter(v);
		
		if(!isRecord)
			Change.stopRecord();
		
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

		if (getVisibility() == visibility)
			return;
		
		Change.push(new BufferMethod(this));
		this.visibility = visibility;
		Change.push(new BufferMethod(this));

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

		String xml = tab + "<method " + "name=\"" + name + "\" returnType=\"" + returnType.toXML(0) + "\" visibility=\"" + visibility + "\" isStatic=\"" + _isStatic + "\" isAbstract=\"" + _isAbstract + "\" ";

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
