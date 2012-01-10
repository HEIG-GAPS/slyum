package swing.hierarchicalView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import swing.Slyum;
import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Dependency;

/**
 * A JTree node associated with a dependency UML.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class NodeDepedency extends DefaultMutableTreeNode implements IClassDiagramNode, ICustomizedIconNode, Observer
{
	private static final long serialVersionUID = 1674273797529847201L;

	/**
	 * Return the title that the node must show according to its dependency.
	 * 
	 * @param dependency
	 *            the dependency to get the title
	 * @return the title generated from dependency
	 */
	public static String generateName(Dependency dependency)
	{
		return dependency.toString();
	}

	private final Dependency dependency;
	private final JTree tree;

	private final DefaultTreeModel treeModel;

	/**
	 * Create a new node associated with a dependency.
	 * 
	 * @param dependency
	 *            the dependency associated
	 * @param treeModel
	 *            the model of the JTree
	 * @param tree
	 *            the JTree
	 */
	public NodeDepedency(Dependency dependency, DefaultTreeModel treeModel, JTree tree)
	{
		super(generateName(dependency));

		if (treeModel == null)
			throw new IllegalArgumentException("dependency is null");

		if (tree == null)
			throw new IllegalArgumentException("tree is null");

		this.tree = tree;

		this.dependency = dependency;
		this.treeModel = treeModel;

		dependency.addObserver(this);
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return dependency;
	}

	@Override
	public ImageIcon getCustomizedIcon()
	{
		return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "dependency16.png");
	}

	@Override
	public void update(Observable o, Object arg)
	{
		if (arg != null && arg instanceof UpdateMessage)
		{
			final TreePath path = new TreePath(getPath());

			switch ((UpdateMessage) arg)
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
			setUserObject(generateName(dependency));
			treeModel.reload(this);
		}
	}
	
	@Override
	public void remove()
	{
	}

}
