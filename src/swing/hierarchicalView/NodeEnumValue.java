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
import classDiagram.components.EnumValue;

public class NodeEnumValue extends DefaultMutableTreeNode implements ICustomizedIconNode, Observer, IClassDiagramNode {

  private final EnumValue enumValue;
  private final JTree tree;
  private final DefaultTreeModel treeModel;

  public NodeEnumValue(EnumValue enumValue, DefaultTreeModel treeModel,
          JTree tree) {
    super(enumValue.getValue());

    if (treeModel == null)
      throw new IllegalArgumentException("treeModel is null");

    if (tree == null) throw new IllegalArgumentException("tree is null");

    this.enumValue = enumValue;
    this.treeModel = treeModel;
    this.tree = tree;

    enumValue.addObserver(this);
  }

  @Override
  public IDiagramComponent getAssociedComponent() {
    return enumValue;
  }

  @Override
  public void remove() {}

  @Override
  public void update(Observable observable, Object o) {
    if (o != null && o instanceof UpdateMessage) {
      TreePath path = new TreePath(getPath());
      switch ((UpdateMessage) o) {
        case SELECT:
          tree.addSelectionPath(path.getParentPath());
          tree.addSelectionPath(path);
          break;
        case UNSELECT:
          tree.removeSelectionPath(path);
          break;
        default:
          break;
      }
    } else {
      setUserObject(enumValue.getValue());
      treeModel.reload(getParent());
    }
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "enum.png");
  }

}
