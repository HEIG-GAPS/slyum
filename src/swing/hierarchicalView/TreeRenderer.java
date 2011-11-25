package swing.hierarchicalView;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import utility.PersonnalizedIcon;

@SuppressWarnings("serial")
public class TreeRenderer extends DefaultTreeCellRenderer
{
	public TreeRenderer()
	{
		setLeafIcon(PersonnalizedIcon.createImageIcon("resources/icon/boxOpen.png"));
		setClosedIcon(PersonnalizedIcon.createImageIcon("resources/icon/boxClose.png"));
		setOpenIcon(PersonnalizedIcon.createImageIcon("resources/icon/boxOpen.png"));
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (row == 0) // root
			setIcon(PersonnalizedIcon.createImageIcon("resources/icon/diagramIcon.png"));

		else if (value instanceof ICustomizedIconNode)

			setIcon(((ICustomizedIconNode) value).getCustomizedIcon());

		return this;
	}
}
