package swing.hierarchicalView;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import classDiagram.components.Attribute;
import classDiagram.components.Method;
import classDiagram.components.SimpleEntity;

public class NodeSimpleEntity extends NodeEntity {

  public NodeSimpleEntity(SimpleEntity entity, DefaultTreeModel treeModel,
          JTree tree, ImageIcon icon) {
    super(entity, treeModel, tree, icon);
  }

  @Override
  protected void reloadChildsNodes() {
    DefaultMutableTreeNode node;
    SimpleEntity entity = (SimpleEntity) super.entity;

    setUserObject(entity.getName());
    removeAllChildren();

    for (final Attribute a : entity.getAttributes()) {
      node = new NodeAttribute(a, treeModel, tree);
      add(node);
    }

    for (final Method m : entity.getMethods()) {
      node = new NodeMethod(m, treeModel, tree);
      add(node);
    }

    treeModel.reload(this);
  }
}
