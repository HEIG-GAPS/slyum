/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import utility.PersonalizedIcon;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public class NodeView 
  extends DefaultMutableTreeNode
    implements ICustomizedIconNode, Observer {
  
  private GraphicView graphicView;
  private DefaultTreeModel treeModel;
  private JPopupMenu popupMenu;

  public NodeView(GraphicView graphicView, DefaultTreeModel treeModel) {
    super(graphicView.getName());
    this.graphicView = graphicView;
    this.treeModel = treeModel;
    graphicView.addObserver(this);
    
    popupMenu = new JPopupMenu();
    
    JMenuItem item = new JMenuItem(
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

  public GraphicView getGraphicView() {
    return graphicView;
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "element-view.png");
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
