package swing.hierarchicalView;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import swing.Slyum;
import utility.PersonalizedIcon;

public class TreeRenderer extends DefaultTreeCellRenderer {
  public TreeRenderer() {
    setLeafIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
            + "boxOpen.png"));
    setClosedIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
            + "boxClose.png"));
    setOpenIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
            + "boxOpen.png"));
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
            hasFocus);

    if (row == 0) { // root
      setIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
              + "diagramIcon.png"));
    }
    else if (value instanceof ICustomizedIconNode)

    setIcon(((ICustomizedIconNode) value).getCustomizedIcon());

    return this;
  }
}
