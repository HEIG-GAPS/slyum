package swing.hierarchicalView;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import classDiagram.components.Attribute;
import classDiagram.components.Method;
import classDiagram.components.SimpleEntity;
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

    for (final Attribute a : simpleEntity.getAttributes())
      add(new NodeAttribute(a, treeModel, tree));

    for (final Method m : simpleEntity.getMethods())
      add(new NodeMethod(m, treeModel, tree));

    treeModel.reload(this);
  }
}
