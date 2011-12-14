package swing.hierarchicalView;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import swing.hierarchicalView.ICustomizedIconNode;
import utility.PersonalizedIcon;

@SuppressWarnings("serial")
public class TreeRenderer extends DefaultTreeCellRenderer
{
	public TreeRenderer()
	{
		setLeafIcon(PersonalizedIcon.createImageIcon("resources/icon/boxOpen.png"));
		setClosedIcon(PersonalizedIcon.createImageIcon("resources/icon/boxClose.png"));
		setOpenIcon(PersonalizedIcon.createImageIcon("resources/icon/boxOpen.png"));
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (row == 0) // root
			setIcon(PersonalizedIcon.createImageIcon("resources/icon/diagramIcon.png"));

		else if (value instanceof ICustomizedIconNode)

			setIcon(((ICustomizedIconNode) value).getCustomizedIcon());

		return this;
	}
}
