package swing.hierarchicalView;

import classDiagram.components.Entity;
import classDiagram.components.EnumEntity;
import classDiagram.components.EnumValue;
import swing.hierarchicalView.HierarchicalView.STree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class NodeEnumEntity extends NodeEntity {

  public NodeEnumEntity(Entity entity, DefaultTreeModel treeModel, STree tree,
                        ImageIcon icon) {
    super(entity, treeModel, tree, icon);
  }

  @Override
  protected void reloadChildsNodes() {
    DefaultMutableTreeNode node;
    EnumEntity enumEntity = (EnumEntity) super.entity;

    setUserObject(enumEntity.getName());
    removeAllChildren();

    for (EnumValue a : enumEntity.getEnumValues()) {
      node = new NodeEnumValue(a, treeModel, tree);
      add(node);
    }

    treeModel.reload(this);
  }

}
