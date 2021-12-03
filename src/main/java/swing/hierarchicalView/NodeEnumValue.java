package swing.hierarchicalView;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.EnumValue;
import swing.PanelClassDiagram;
import swing.Slyum;
import swing.hierarchicalView.HierarchicalView.STree;
import utility.PersonalizedIcon;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.Observable;
import java.util.Observer;

public class NodeEnumValue extends DefaultMutableTreeNode implements ICustomizedIconNode, Observer, IClassDiagramNode {

  private final EnumValue enumValue;
  private final STree tree;
  private final DefaultTreeModel treeModel;

  public NodeEnumValue(EnumValue enumValue, DefaultTreeModel treeModel,
                       STree tree) {
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
  public void remove() { }

  @Override
  public void update(Observable observable, Object o) {
    if (o != null && o instanceof UpdateMessage) {
      TreePath path = new TreePath(getPath());
      switch ((UpdateMessage) o) {
        case SELECT:
          if (!PanelClassDiagram.getInstance().isDisabledUpdate())
            tree.addSelectionPathNoFire(path);
          break;
        case UNSELECT:
          tree.removeSelectionPathNoFire(path);
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
    return PersonalizedIcon.createImageIcon("enum.png");
  }

}
