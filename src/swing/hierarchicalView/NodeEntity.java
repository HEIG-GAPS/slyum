package swing.hierarchicalView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Entity;
import swing.PanelClassDiagram;
import swing.hierarchicalView.HierarchicalView.STree;

/**
 * A JTree node associated with an entity UML.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public abstract class NodeEntity extends DefaultMutableTreeNode implements Observer, IClassDiagramNode, ICustomizedIconNode {
  protected final Entity entity;
  protected final ImageIcon icon;
  protected final STree tree;

  protected final DefaultTreeModel treeModel;

  /**
   * Create a new node associated with an entity.
   * 
   * @param entity
   *          the entity associated
   * @param treeModel
   *          the model of the JTree
   * @param tree
   *          the JTree
   * @param icon
   *          the customized icon
   */
  public NodeEntity(Entity entity, DefaultTreeModel treeModel, STree tree,
          ImageIcon icon) {
    super(entity.getName());

    if (treeModel == null)
      throw new IllegalArgumentException("treeModel is null");

    if (tree == null) throw new IllegalArgumentException("tree is null");

    this.entity = entity;
    this.treeModel = treeModel;
    this.tree = tree;
    this.icon = icon;

    entity.addObserver(this);

    reloadChildsNodes();
  }

  @Override
  public IDiagramComponent getAssociedComponent() {
    return entity;
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return icon;
  }

  /**
   * Remove and re-generate all child nodes according to methods and attributs
   * containing by the entity.
   */
  protected abstract void reloadChildsNodes();

  @Override
  public void removeAllChildren() {
    for (int i = getChildCount() - 1; i >= 0; i--) {
      IClassDiagramNode node = (IClassDiagramNode) getChildAt(i);

      node.getAssociedComponent().deleteObserver((Observer) node);
    }

    super.removeAllChildren();
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    if (arg1 != null && arg1.getClass() == UpdateMessage.class) {
      final TreePath path = new TreePath(getPath());

      switch ((UpdateMessage) arg1) {
        case SELECT:
          if (!PanelClassDiagram.getInstance().isDisabledUpdate())
            tree.addSelectionPathNoFire(path);
          break;

        case UNSELECT:
          tree.removeSelectionPathNoFire(path);
          break;

        default:
          reloadChildsNodes();
          break;
      }
    } else
      reloadChildsNodes();
  }

  @Override
  public void remove() {
    removeAllChildren();
  }
}
