/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package swing.hierarchicalView;

import graphic.GraphicView;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
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

  public NodeView(GraphicView graphicView) {
    super(graphicView.getName());
    this.graphicView = graphicView;
    graphicView.addObserver(this);
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
    if (o instanceof GraphicView)
      setUserObject(((GraphicView)o).getName());
  }
  
}
