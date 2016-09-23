/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package change;

import graphic.GraphicView;
import swing.MultiViewManager;


public abstract class BufferGraphicView implements Changeable {
  private GraphicView graphicView;
  
  public BufferGraphicView(GraphicView graphicView) {
    this.graphicView = graphicView;
  }

  public GraphicView getGraphicView() {
    return graphicView;
  }

  @Override
  public void restore() {
    if (graphicView != null)
      MultiViewManager.setSelectedGraphicView(graphicView);
  }
}
