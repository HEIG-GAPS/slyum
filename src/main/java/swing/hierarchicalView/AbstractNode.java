package swing.hierarchicalView;

import swing.PanelClassDiagram;
import swing.Slyum;
import swing.hierarchicalView.HierarchicalView.STree;
import utility.PersonalizedIcon;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * @author David Miserez
 */
public abstract class AbstractNode
    extends DefaultMutableTreeNode
    implements IClassDiagramNode, ICustomizedIconNode, Observer {

  protected JPopupMenu popupMenu;
  protected STree tree;
  protected DefaultTreeModel treeModel;

  public AbstractNode(Object userObject, DefaultTreeModel treeModel, STree tree) {
    super(userObject);
    this.treeModel = treeModel;
    this.tree = tree;
    popupMenu = new JPopupMenu();

    // Menu item delete
    JMenuItem item = new JMenuItem(
        "Delete",
        PersonalizedIcon.createImageIcon("delete.png"));

    item.addActionListener(getMenuItemDeleteActionListener());
    popupMenu.add(item);
  }

  protected ActionListener getMenuItemDeleteActionListener() {
    return (ActionEvent e) -> PanelClassDiagram.getInstance().getClassDiagram().removeComponent(getAssociedComponent());
  }

  public JPopupMenu getPopupMenu() {
    return popupMenu;
  }

  @Override
  public void setUserObject(Object userObject) {
    super.setUserObject(userObject);

    if (getParent() != null)
      HierarchicalView.sortAlphabetically(
          (DefaultMutableTreeNode) getParent(), treeModel, tree);
  }

  @Override
  public void update(Observable o, Object arg) {

  }

}
