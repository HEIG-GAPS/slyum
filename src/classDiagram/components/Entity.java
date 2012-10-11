package classDiagram.components;

import change.BufferClass;
import change.BufferCreationAttribute;
import change.BufferCreationMethod;
import change.BufferIndex;
import change.Change;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import utility.SMessageDialog;
import utility.Utility;
import classDiagram.relationships.Inheritance;
import classDiagram.relationships.Role;

/**
 * Abstract class containing all classes parameters (attributes, methods,
 * visibility, ...)
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public abstract class Entity extends Type
{	
	private boolean _isAbstract = false;
	protected LinkedList<Attribute> attributes = new LinkedList<>();
	protected List<Inheritance> childs = new LinkedList<>();
	protected LinkedList<Method> methods = new LinkedList<>();
	protected List<Inheritance> parents = new LinkedList<>();
	protected List<Role> roles = new LinkedList<>();

	protected String stereotype = "";

	protected Visibility visibility = Visibility.PUBLIC;

	public Entity(String name, Visibility visibility)
	{
		super(name);

		initComponent(visibility);
	}

	public Entity(String name, Visibility visibility, int id)
	{
		super(name, id);
		
		initComponent(visibility);
	}
	
	public Entity(Entity e)
	{
		super(e.name, e.id);
		
		initComponent(e.visibility);
	}
	
	private void initComponent(Visibility visibility)
	{
		if (visibility == null)
			throw new IllegalArgumentException("visibility is null");

		this.visibility = visibility;
	}

	/**
	 * Add a new attribute.
	 * 
	 * @param attribute
	 *            the new attribute.
	 */
	public void addAttribute(Attribute attribute)
  {
    if (attribute == null)
      throw new IllegalArgumentException("attribute is null");

    attributes.add(attribute);
    int i = attributes.indexOf(attribute);
    Change.push(new BufferCreationAttribute(this, attribute, true, i));
    Change.push(new BufferCreationAttribute(this, attribute, false, i));

    setChanged();
  }

	/**
	 * Add a new child.
	 * 
	 * @param child
	 *            the new child
	 */
	public void addChild(Inheritance child)
	{
		if (child == null)
			throw new IllegalArgumentException("child is null");

		childs.add(child);

		setChanged();
	}

	/**
	 * Add a new method.
	 * 
	 * @param method
	 *            the new method.
	 * @return
	 */
	public boolean addMethod(Method method)
	{
		if (method == null)
			throw new IllegalArgumentException("method is null");

		if (methods.contains(method))
			return false;
		
		method.setAbstract(isAbstract());

		methods.add(method);
		
		int i = methods.indexOf(method);
    Change.push(new BufferCreationMethod(this, method, true, i));
		Change.push(new BufferCreationMethod(this, method, false, i));

		setChanged();

		return true;
	}

	/**
	 * Add a new parent.
	 * 
	 * @param parent
	 *            the new parent
	 */
	public void addParent(Inheritance parent)
	{
		if (parent == null)
			throw new IllegalArgumentException("parent is null");

		parents.add(parent);

		setChanged();
	}

	/**
	 * Add a new role.
	 * 
	 * @param role
	 *            the new role
	 */
	public void addRole(Role role)
	{
		if (role == null)
			throw new IllegalArgumentException("role is null");

		roles.add(role);

		setChanged();
	}
	
	public int countStaticMethods()
	{
		int i = 0;
		for (Method m : getMethods())
			if (m.isStatic()) i++;
		
		return i;
	}
	
	public boolean isEveryMethodsStatic()
	{
		return getMethods().size() - countStaticMethods() == 0;
	}

	public LinkedList<Entity> getAllChilds()
	{
		final LinkedList<Entity> allChilds = new LinkedList<Entity>();
		allChilds.add(this);

		for (final Inheritance p : childs)
			allChilds.addAll(p.getChild().getAllChilds());

		return allChilds;
	}

	public LinkedList<Entity> getAllParents()
	{
		final LinkedList<Entity> allParents = new LinkedList<Entity>();
		allParents.add(this);

		for (final Inheritance p : parents)
			allParents.addAll(p.getParent().getAllParents());

		return allParents;
	}

	/**
	 * Get a copy of the attribute's list.
	 * 
	 * @return an array containing all attributes of the entity.
	 */
	public LinkedList<Attribute> getAttributes()
	{
		final LinkedList<Attribute> copy = new LinkedList<Attribute>();

		for (final Attribute a : attributes)
			copy.add(a);

		return copy;
	}

	/**
	 * Use in XML exportation. Get the type of the entity.
	 * 
	 * @return the type of the entity.
	 */
	protected abstract String getEntityType();

	/**
	 * Us in XML exportation. Get a string to add new tags if necessary.
	 * 
	 * @param depth
	 *            the number of tabs to add before each tag
	 * @return the tag to add before the closure tag.
	 */
	protected String getLastBalise(int depth)
	{
		return ""; // no last balise
	}

	/**
	 * Get a copy of the method's list.
	 * 
	 * @return an array containing all methods of the entity.
	 */
	public LinkedList<Method> getMethods()
	{
		final LinkedList<Method> copy = new LinkedList<Method>();

		for (final Method m : methods)
			copy.add(m);

		return copy;
	}

	/**
	 * Get the stereotype of the entity.
	 * 
	 * @return the stereotype of the entity.
	 */
	public String getStereotype()
	{
		return stereotype;
	}

	/**
	 * Get the visibility of the entity.
	 * 
	 * @return the visibility of the entity
	 */
	public Visibility getVisibility()
	{
		return visibility;
	}

	/**
	 * Return true if the entity has abstract methods; false otherwise.
	 * 
	 * @return true if the entity has abstract methods; false otherwise.
	 */
	public boolean hasAbstractMethods()
	{
		for (final Method m : getMethods())
			if (m.isAbstract())
				return true;

		return false;
	}

	/**
	 * Get the abstract state of the entity.
	 * 
	 * @return true if the entity is abstract; false otherwise
	 */
	public boolean isAbstract()
	{
		return _isAbstract;
	}

	public boolean isChildOf(Entity entity)
	{
		boolean isChild = false;

		for (final Inheritance i : parents)
			isChild |= i.getParent().isChildOf(entity);

		return isChild || equals(entity);
	}

	public boolean isParentOf(Entity entity)
	{
		boolean isParent = false;

		for (final Inheritance i : childs)
			isParent |= i.getChild().isParentOf(entity);

		return isParent || equals(entity);
	}

	/**
	 * Move the attribute's position in the array by the given offset. Offset is
	 * added to the current index to compute the new index. The offset can be
	 * positive or negative.
	 * 
	 * @param attribute
	 *            the attribute to move
	 * @param offset
	 *            the offset for compute the new index
	 */
	public void moveAttributePosition(Attribute attribute, int offset)
	{
		moveComponentPosition(attributes, attribute, offset);
	}

	/**
	 * Move the object's position in the given array by the given offset. Offset
	 * is added to the current index to compute the new index. The offset can be
	 * positive or negative.
	 * 
	 * @param list
	 *            the list containing the object to move
	 * @param o
	 *            the object to move
	 * @param offset
	 *            the offset for compute the new index
	 */
	private <T extends Object> void moveComponentPosition(LinkedList<T> list, T o, int offset)
	{
		final int index = list.indexOf(o);

		if (index != -1)
		{
			Change.push(new BufferIndex<T>(this, list, o));
			
			list.remove(o);
			list.add(index + offset, o);
			
			Change.push(new BufferIndex<T>(this, list, o));

			setChanged();
		}
	}

	/**
	 * Move the method's position in the array by the given offset. Offset is
	 * added to the current index to compute the new index. The offset can be
	 * positive or negative.
	 * 
	 * @param method
	 *            the method to move
	 * @param offset
	 *            the offset for compute the new index
	 */
	public void moveMethodPosition(Method method, int offset)
	{
		moveComponentPosition(methods, method, offset);
	}

	/**
	 * Remove the attribute.
	 * 
	 * @param attribute
	 *            the attribute to remove
	 * @return true if the attribute has been removed; false otherwise
	 */
	public boolean removeAttribute(Attribute attribute)
	{
		if (attribute == null)
			throw new IllegalArgumentException("attribute is null");

		int i = attributes.indexOf(attribute);
		
		if (attributes.remove(attribute))
		{
	    Change.push(new BufferCreationAttribute(this, attribute, false, i));
	    Change.push(new BufferCreationAttribute(this, attribute, true, i));
	    
			setChanged();
			return true;
		}
		else
			return false;
	}

	/**
	 * Remove the child.
	 * 
	 * @param child
	 *            the child to remove
	 */
	public void removeChild(Inheritance child)
	{
		childs.remove(child);

		setChanged();
	}

	/**
	 * Remove the method.
	 * 
	 * @param method
	 *            the method to remove.
	 * @return true if the method has been removed; false otherwise
	 */
	public boolean removeMethod(Method method)
	{
		if (method == null)
			throw new IllegalArgumentException("method is null");

		int i = methods.indexOf(method);
		
		if (methods.remove(method))
		{
      Change.push(new BufferCreationMethod(this, method, false, i));
      Change.push(new BufferCreationMethod(this, method, true, i));
      
			setChanged();
			notifyObservers();
			return true;
		}

		return false;
	}

	/**
	 * Remove the parent.
	 * 
	 * @param parent
	 *            the parent to remove
	 */
	public void removeParent(Inheritance parent)
	{
		parents.remove(parent);

		setChanged();
	}

	/**
	 * Set the abstract state of the entity.
	 * 
	 * @param isAbstract
	 *            the new abstract state.
	 */
	public void setAbstract(boolean isAbstract)
	{
		if (hasAbstractMethods())
			if (SMessageDialog.showQuestionMessageYesNo("Class has abstract methods.\nDe-abstract all methods?") == JOptionPane.NO_OPTION)

				isAbstract = true;

			else
				for (final Method m : getMethods())
					if (m.isAbstract())
						m.setAbstract(false);

		Change.push(new BufferClass(this));
		_isAbstract = isAbstract;
		Change.push(new BufferClass(this));

		setChanged();
	}
	
	@Override
	public boolean setName(String name)
	{
		BufferClass bc = new BufferClass(this);
		boolean b;
		
		b = super.setName(name);
		
		if (b)
		{
			Change.push(bc);
			Change.push(new BufferClass(this));
		}
		
		return b;
	}

	/**
	 * Set the stereotype of the entity.
	 * 
	 * @param stereotype
	 *            the new stereotype
	 */
	public void setStereotype(String stereotype)
	{
		if (stereotype == null)
			throw new IllegalArgumentException("stereotype is null");

		this.stereotype = stereotype;
	}

	/**
	 * Set the visibility of the entity.
	 * 
	 * @param visibility
	 *            the new visibility
	 */
	public void setVisibility(Visibility visibility)
	{
		if (visibility == null)
			throw new IllegalArgumentException("visibility is null");

		if (visibility.equals(getVisibility()))
			return;
		
		Change.push(new BufferClass(this));		
		this.visibility = visibility;
		Change.push(new BufferClass(this));

		setChanged();
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		String xml = tab + "<entity " + "id=\"" + getId() + "\" " + "name=\"" + super.toXML(0) + "\" " + "visibility=\"" + visibility + "\" " + "entityType=\"" + getEntityType() + "\" " + "isAbstract=\"" + isAbstract() + "\" ";

		if (attributes.size() == 0 && methods.size() == 0 && getLastBalise(depth).isEmpty())
			return xml + "/>";

		xml += ">\n";

		for (final Attribute attribute : attributes)
			xml += attribute.toXML(depth + 1) + "\n";

		for (final Method operation : methods)
			xml += operation.toXML(depth + 1) + "\n";

		xml += getLastBalise(depth + 1);

		return xml + tab + "</entity>";
	}
}
