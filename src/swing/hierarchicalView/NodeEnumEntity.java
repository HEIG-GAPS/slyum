package swing.hierarchicalView;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import classDiagram.components.Entity;
import classDiagram.components.EnumEntity;
import classDiagram.components.EnumValue;
import swing.hierarchicalView.HierarchicalView.STree;

public class NodeEnumEntity extends NodeEntity {

  public NodeEnumEntity(Entity entity, DefaultTreeModel treeModel, STree tree,
          ImageIcon icon) {
    super(entity, treeModel, tree, icon);
  }

  @Override
  protected void reloadChildsNodes() {
    DefaultMutableTreeNode node;
    EnumEntity entity = (EnumEntity) super.entity;

    setUserObject(entity.getName());
    removeAllChildren();

    for (EnumValue a : entity.getEnumValues()) {
      node = new NodeEnumValue(a, treeModel, tree);
      add(node);
    }

    treeModel.reload(this);
  }

}
