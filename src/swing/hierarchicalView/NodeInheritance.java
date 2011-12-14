package swing.hierarchicalView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import swing.hierarchicalView.IClassDiagramNode;
import swing.hierarchicalView.ICustomizedIconNode;
import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Inheritance;

/**
 * A JTree node associated with an inheritance.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class NodeInheritance extends DefaultMutableTreeNode implements ICustomizedIconNode, Observer, IClassDiagramNode

{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6765906183481076172L;

	/**
	 * Return the title that the node must show according to its inheritance.
	 * 
	 * @param inheritance
	 *            the inheritance to get the title
	 * @return the title generated from dependency
	 */
	public static String generateName(Inheritance inheritance)
	{
		return inheritance.getChild().getName() + " - " + inheritance.getParent().getName();
	}

	private final Inheritance inheritance;
	private final JTree tree;

	private final DefaultTreeModel treeModel;

	/**
	 * Create a new node associated with an inheritance.
	 * 
	 * @param inheritance
	 *            the inheritance associated
	 * @param treeModel
	 *            the model of the JTree
	 * @param tree
	 *            the JTree
	 */
	public NodeInheritance(Inheritance inheritance, DefaultTreeModel treeModel, JTree tree)
	{
		super(generateName(inheritance));

		if (treeModel == null)
			throw new IllegalArgumentException("treeModel is null");

		if (tree == null)
			throw new IllegalArgumentException("tree is null");

		this.tree = tree;
		this.inheritance = inheritance;
		this.treeModel = treeModel;

		inheritance.addObserver(this);
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return inheritance;
	}

	@Override
	public ImageIcon getCustomizedIcon()
	{
		return PersonalizedIcon.createImageIcon("resources/icon/generalize16.png");
	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		if (arg1 != null && arg1 instanceof UpdateMessage)
		{
			final TreePath path = new TreePath(getPath());

			switch ((UpdateMessage) arg1)
			{
				case SELECT:
					tree.addSelectionPath(path);
					break;
				case UNSELECT:
					tree.removeSelectionPath(path);
					break;
			}
		}
		else
		{
			setUserObject(generateName(inheritance));
			treeModel.reload(getParent());
		}
	}
}
