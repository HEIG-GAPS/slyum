package swing.hierarchicalView;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Association;
import classDiagram.relationships.Role;

/**
 * A JTree node associated with an association UML.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class NodeAssociation extends DefaultMutableTreeNode implements IClassDiagramNode, ICustomizedIconNode, Observer
{
	private static final long serialVersionUID = 3002125135918965920L;

	/**
	 * Return the title that the node must show according to its association.
	 * 
	 * @param association
	 *            the association to get the title
	 * @return the title generated from association
	 */
	public static String generateName(Association association)
	{
	  String label = association.getName();
		if (!label.isEmpty())
			return label;

		final LinkedList<Role> roles = association.getRoles();
		String text = "";
		String PREFIX = " - ";
		
		if (roles.isEmpty())
			return "";

		for (Role role : roles)
			text += " - " + role.getEntity().getName();
		
		// On efface le premier préfixe.
		return text.substring(PREFIX.length());
	}

	private final Association association;
	private final ImageIcon imageIcon;
	private final JTree tree;

	private final DefaultTreeModel treeModel;

	/**
	 * Create a new node association with an association.
	 * 
	 * @param association
	 *            the associated association
	 * @param treeModel
	 *            the model of the JTree
	 * @param icon
	 *            the customized icon
	 * @param tree
	 *            the JTree
	 */
	public NodeAssociation(Association association, DefaultTreeModel treeModel, ImageIcon icon, JTree tree)
	{
		super(generateName(association));

		if (treeModel == null)
			throw new IllegalArgumentException("treeModel is null");

		if (tree == null)
			throw new IllegalArgumentException("tree is null");

		this.tree = tree;
		this.association = association;
		association.addObserver(this);

		for (final Role role : association.getRoles())
			role.getEntity().addObserver(this);

		this.treeModel = treeModel;
		imageIcon = icon;
	}

	@Override
	public IDiagramComponent getAssociedComponent()
	{
		return association;
	}

	@Override
	public ImageIcon getCustomizedIcon()
	{
		return imageIcon;
	}

	@Override
	public void update(Observable o, Object arg1)
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
			setUserObject(generateName(association));
			treeModel.reload(this);
		}
	}

	@Override
	public void remove()
	{
		for (final Role role : association.getRoles())
			role.deleteObserver(this);
	}
}
