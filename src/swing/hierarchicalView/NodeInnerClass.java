package swing.hierarchicalView;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import swing.Slyum;
import utility.PersonalizedIcon;
import classDiagram.relationships.Inheritance;
import swing.hierarchicalView.HierarchicalView.STree;

public class NodeInnerClass extends NodeInheritance {

  /**
	 * 
	 */
  private static final long serialVersionUID = -4534430187776530177L;

  public NodeInnerClass(Inheritance inheritance, DefaultTreeModel treeModel,
          STree tree) {
    super(inheritance, treeModel, tree);
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "innerClass.png");
  }

}
