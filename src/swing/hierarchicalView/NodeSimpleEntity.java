package swing.hierarchicalView;

import classDiagram.components.SimpleEntity;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;
import swing.hierarchicalView.HierarchicalView.STree;

public class NodeSimpleEntity extends NodeEntity {

  public NodeSimpleEntity(SimpleEntity entity, DefaultTreeModel treeModel,
          STree tree, ImageIcon icon) {
    super(entity, treeModel, tree, icon);
  }

  @Override
  protected void reloadChildsNodes() {
    SimpleEntity simpleEntity = (SimpleEntity)super.entity;

    setUserObject(simpleEntity.getName());
    removeAllChildren();

    simpleEntity.getAttributes().stream().forEach((a) -> {
      add(new NodeAttribute(a, treeModel, tree));
    });

    simpleEntity.getMethods().stream().forEach((m) -> {
      add(new NodeMethod(m, treeModel, tree));
    });

    HierarchicalView.sortAlphabetically(this, treeModel, tree);
    treeModel.reload(this);
  }
}
