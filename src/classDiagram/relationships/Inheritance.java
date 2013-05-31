package classDiagram.relationships;

import java.util.Observable;

import swing.OverridesAndImplementationsDialog;
import utility.SMessageDialog;
import utility.Utility;
import classDiagram.ClassDiagram;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;

/**
 * Represent a inheritance in UML structure. This inheritance, depends on the
 * entites participating in inheritance, will be a generalization or a
 * relalization.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Inheritance extends Observable 
                         implements Relation
{
	public static boolean validate(Entity child, Entity parent) {
	  /* Cette méthode de validation est a revoir. Désactivation en attendant.
		boolean valide = true;

		valide &= child != parent;

    for (Entity e : child.getAllChilds())
      valide &= !parent.equals(e);

		if (!valide)
			SMessageDialog.showErrorMessage(
					"Error in hierarchical class structure.\n" +
					"Impossible to create inheritance association.");

		return valide;
		*/
	  return true;
	}

	protected Entity child, parent;

	protected final int id;

	/**
	 * Create a new inheritance with the given entities child and parent.
	 * 
	 * @param child
	 *            the child entity
	 * @param parent
	 *            the parent entity
	 */
	public Inheritance(Entity child, Entity parent)
	{
		init(child, parent);

		id = ClassDiagram.getNextId();
	}

	/**
	 * Create a new inheritance with the given entities child and parent. Don't
	 * generate a new id and use this given in parameter.
	 * 
	 * @param child
	 *            the child entity
	 * @param parent
	 *            the parent entity
	 */
	public Inheritance(Entity child, Entity parent, int id)
	{
		init(child, parent);

		this.id = id;
	}

	/**
	 * Get the child for this inheritance.
	 * 
	 * @return the child for this inheritance
	 */
	public Entity getChild()
	{
		return child;
	}

	@Override
	public int getId()
	{
		return id;
	}

	/**
	 * Get the parent for this inheritance.
	 * 
	 * @return the parent for this inheritance
	 */
	public Entity getParent()
	{
		return parent;
	}

	/**
	 * Call by construtor for init parameters.
	 * 
	 * @param child
	 *            the child given in constructor
	 * @param parent
	 *            the parent given in constructor
	 */
	private void init(Entity child, Entity parent)
	{
		if (child.getClass() == InterfaceEntity.class && parent.getClass() == ClassEntity.class)
			throw new IllegalArgumentException("interface cannot implements class");

		this.child = child;
		this.child.addParent(this);

		this.parent = parent;
		this.parent.addChild(this);
	}

	@Override
	public void select()
	{
		setChanged();
	}

	/**
	 * Set the child for this inheritance.
	 * 
	 * @param child
	 *            the new child for this inheritance
	 */
	public void setChild(Entity child)
	{
		this.child.removeParent(this);
		this.child = child;
		child.addParent(this);
		setChanged();
	}

	/**
	 * Set the parent for this inheritance.
	 * 
	 * @param parent
	 *            the new parent for this inheritance
	 */
	public void setParent(Entity parent)
	{
		this.parent.removeChild(this);
		this.parent = parent;
		parent.addChild(this);
    setChanged();
	}

	public void showOverridesAndImplementations()
	{
		boolean thereAbstractMethod = false;
		
		final OverridesAndImplementationsDialog oai = new OverridesAndImplementationsDialog(parent, child);

		if (oai.isAccepted())

			for (final OverridesAndImplementationsDialog.CheckableItem m : oai.getCheckableItems())
			{
				if (m.isSelected())
				{
					child.addMethod(new Method(m.getMethod(), child));
					thereAbstractMethod |= m.getMethod().isAbstract();
				}
				else

					child.removeMethod(m.getMethod());
			}

		if (thereAbstractMethod && !child.isAbstract())
			
			showDeAbstractMessage();
			
		child.notifyObservers();
	}

	private void showDeAbstractMessage() {
		SMessageDialog.showInformationMessage("Child class is not abstract.\nAbstract methods have been de-abstracted.");
	}

  @Override
  public Entity getSource() {
    return getChild();
  }

  @Override
  public Entity getTarget() {
    return getParent();
  }

  @Override
  public void setSource(Entity entity) {
    setChild(entity);
  }

  @Override
  public void setTarget(Entity entity) {
    setParent(entity);
  }

	@Override
	public String toString()
	{
		return getChild().getName() + " - " + getParent().getName();
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		final String xml = tab + "<inheritance id=\"" + id + "\">\n" + tab + "\t<child>" + child.getId() + "</child>\n" + tab + "\t<parent>" + parent.getId() + "</parent>\n";

		return xml + tab + "</inheritance>";
	}
}
