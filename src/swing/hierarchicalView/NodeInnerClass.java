package swing.hierarchicalView;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import utility.PersonnalizedIcon;
import classDiagram.relationships.Inheritance;

public class NodeInnerClass extends NodeInheritance
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4534430187776530177L;

	public NodeInnerClass(Inheritance inheritance, DefaultTreeModel treeModel, JTree tree)
	{
		super(inheritance, treeModel, tree);
	}

	@Override
	public ImageIcon getCustomizedIcon()
	{
		return PersonnalizedIcon.createImageIcon("resources/icon/innerClass16.png");
	}

}
