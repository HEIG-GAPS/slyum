package swing.hierarchicalView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import swing.MultiViewManager;
import swing.PanelClassDiagram;
import swing.Slyum;
import swing.hierarchicalView.HierarchicalView.STree;
import utility.PersonalizedIcon;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
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
        PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "delete.png"));
    
    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        PanelClassDiagram.getInstance().getClassDiagram().removeComponent(
            getAssociedComponent());
      }
    });
    popupMenu.add(item);
  }

  public JPopupMenu getPopupMenu() {
    return popupMenu;
  }
  
  @Override
  public void setUserObject(Object userObject) {
    super.setUserObject(userObject);
    
    if (getParent() != null)
      HierarchicalView.sortAlphabetically(
          (DefaultMutableTreeNode)getParent(), treeModel, tree);
  }

  @Override
  public void update(Observable o, Object arg) {
    
  }
    
}
