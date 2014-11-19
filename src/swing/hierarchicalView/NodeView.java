package swing.hierarchicalView;

import graphic.GraphicView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import swing.MultiViewManager;
import swing.Slyum;
import swing.UserInputDialog;
import utility.PersonalizedIcon;

public class NodeView 
    extends DefaultMutableTreeNode
    implements ICustomizedIconNode, Observer {
  
  private GraphicView graphicView;
  private JPopupMenu popupMenu;
  private DefaultTreeModel treeModel;

  public NodeView(GraphicView graphicView, DefaultTreeModel treeModel) {
    super(graphicView.getName());
    this.graphicView = graphicView;
    this.treeModel = treeModel;
    graphicView.addObserver(this);
    
    popupMenu = new JPopupMenu();
    
    // Menu item open
    JMenuItem item = new JMenuItem(
        "Open", 
        PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "element-view-open.png"));
    
    item.setEnabled(graphicView != MultiViewManager.getRootGraphicView());
    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        MultiViewManager.openView(NodeView.this.graphicView);
      }
    });
    popupMenu.add(item);
    
    // Menu item rename
    item = new JMenuItem(
        "Rename", 
        PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "element-view-open.png"));
    
    item.setEnabled(graphicView != MultiViewManager.getRootGraphicView());
    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        GraphicView gv = NodeView.this.graphicView;
        String gvName = gv.getName();
        
        UserInputDialog dialog = new UserInputDialog(
            gvName, 
            "Slyum - Rename view", 
            "Enter a new name for the view \"" + gvName + "\":");
        
        dialog.setVisible(true);
        
        if (dialog.isAccepted()) {
          gv.setName(dialog.getText());
          gv.notifyObservers();
        }
      }
    });
    popupMenu.add(item);
    
    // Menu item delete
    item = new JMenuItem(
        "Delete", 
        PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "delete.png"));
    
    item.setEnabled(graphicView != MultiViewManager.getRootGraphicView());
    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        MultiViewManager.removeView(NodeView.this.graphicView);
      }
    });
    popupMenu.add(item);
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "element-view.png");
  }

  public GraphicView getGraphicView() {
    return graphicView;
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof GraphicView) {
      setUserObject(((GraphicView)o).getName());
      treeModel.reload(getParent());
    }
  }

  JPopupMenu getPopupMenu() {
    return popupMenu;
  }  
}
