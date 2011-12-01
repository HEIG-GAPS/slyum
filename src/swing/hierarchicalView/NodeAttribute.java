package swing.hierarchicalView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Attribute;

/**
 * A JTree node associated with an attribute UML.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class NodeAttribute extends DefaultMutableTreeNode implements ICustomizedIconNode, Observer, IClassDiagramNode
{
	private static final long serialVersionUID = -2998185646864433535L;
	private final Attribute attribute;
	private final JTree tree;
	private final DefaultTreeModel treeModel;

	/**
	 * Create a new node associated with an attribute.
	 * 
	 * @param attribute
	 *            the attribute associated
	 * @param treeModel
	 *            the model of the JTree
	 * @param tree
	 *            the JTree
	 */
	public NodeAttribute(Attribute attribute, DefaultTreeModel treeModel, JTree tree)
	{
		super(attribute.getName());

		if (treeModel == null)
			throw new IllegalArgumentException("treeModel is null");

		if (tree == null)
			throw new IllegalArgumentException("tree is null");

		this.attribute = attribute;
		this.treeModel = treeModel;
		this.tree = tree;

		attribute.addObserver(this);
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return attribute;
	}

	@Override
	public ImageIcon getCustomizedIcon()
	{
		return PersonalizedIcon.createImageIcon("resources/icon/attribute.png");
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
			setUserObject(attribute.getName());
			treeModel.reload(getParent());
		}
	}

}
