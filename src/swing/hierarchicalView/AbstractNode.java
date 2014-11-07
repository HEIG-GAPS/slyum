/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package swing.hierarchicalView;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import swing.hierarchicalView.HierarchicalView.STree;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public class AbstractNode extends DefaultMutableTreeNode {
  
  protected DefaultTreeModel treeModel;
  protected STree tree;

  public AbstractNode(Object userObject, DefaultTreeModel treeModel, STree tree) {
    super(userObject);
    this.treeModel = treeModel;
    this.tree = tree;
  }

  @Override
  public void setUserObject(Object userObject) {
    super.setUserObject(userObject);
    
    if (getParent() != null)
      HierarchicalView.sortAlphabetically(
          (DefaultMutableTreeNode)getParent(), treeModel, tree);
  }
}
