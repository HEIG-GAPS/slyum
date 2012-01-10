package swing.hierarchicalView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import swing.Slyum;
import swing.hierarchicalView.IClassDiagramNode;
import swing.hierarchicalView.ICustomizedIconNode;
import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Method;

/**
 * A JTree node associated with a method UML.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
@SuppressWarnings("serial")
public class NodeMethod extends DefaultMutableTreeNode implements Observer, IClassDiagramNode, ICustomizedIconNode
{
	private final Method method;
	private final JTree tree;
	private final DefaultTreeModel treeModel;

	/**
	 * Create a new node associated with a method.
	 * 
	 * @param method
	 *            the attribute method
	 * @param treeModel
	 *            the model of the JTree
	 * @param tree
	 *            the JTree
	 */
	public NodeMethod(Method method, DefaultTreeModel treeModel, JTree tree)
	{
		super(method.getName());

		if (treeModel == null)
			throw new IllegalArgumentException("treeModel is null");

		if (tree == null)
			throw new IllegalArgumentException("tree is null");

		this.method = method;
		this.treeModel = treeModel;
		this.tree = tree;

		method.addObserver(this);
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return method;
	}

	@Override
	public ImageIcon getCustomizedIcon()
	{
		return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "method.png");
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
					tree.addSelectionPath(path.getParentPath());
					tree.addSelectionPath(path);
					break;
				case UNSELECT:
					tree.removeSelectionPath(path);
					break;
			}
		}
		else
		{
			setUserObject(method.getName());
			treeModel.reload(getParent());
		}
	}

	@Override
	public void removeAllChildren()
	{
	}
	
	@Override
	public void remove()
	{
	}
}
