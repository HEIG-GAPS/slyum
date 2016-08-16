package swing.hierarchicalView;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Method;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import swing.PanelClassDiagram;
import swing.hierarchicalView.HierarchicalView.STree;

/**
 * A JTree node associated with a method UML.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
@SuppressWarnings("serial")
public class NodeMethod extends DefaultMutableTreeNode implements Observer, IClassDiagramNode, ICustomizedIconNode {
  private final Method method;
  private final STree tree;
  private final DefaultTreeModel treeModel;

  /**
   * Create a new node associated with a method.
   * 
   * @param method
   *          the attribute method
   * @param treeModel
   *          the model of the JTree
   * @param tree
   *          the JTree
   */
  public NodeMethod(Method method, DefaultTreeModel treeModel, STree tree) {
    super(method.getName());

    if (treeModel == null)
      throw new IllegalArgumentException("treeModel is null");

    if (tree == null) throw new IllegalArgumentException("tree is null");

    this.method = method;
    this.treeModel = treeModel;
    this.tree = tree;

    method.addObserver(this);
  }

  @Override
  public IDiagramComponent getAssociedComponent() {
    return method;
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return method.getImageIcon();
  }

  @Override
  public void update(Observable observable, Object o) {
    if (o != null && o instanceof UpdateMessage) {
      final TreePath path = new TreePath(getPath());

      switch ((UpdateMessage) o) {
        case SELECT:
          if (!PanelClassDiagram.getInstance().isDisabledUpdate()) {
            tree.addSelectionPathNoFire(path);
          }
          break;
        case UNSELECT:
          tree.removeSelectionPathNoFire(path);
          break;
        default:
          break;
      }
    } else {
      setUserObject(method.getName());
      treeModel.reload(this);
    }
  }

  @Override
  public void removeAllChildren() {}

  @Override
  public void remove() {}
}
