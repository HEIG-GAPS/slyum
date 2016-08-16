/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package change;

import classDiagram.components.Entity;
import graphic.entity.EntityView;
import swing.PanelClassDiagram;


public class Helper {
  
  public static void deepDeleteEntityView(EntityView entityView) {
    deepDeleteEntity((Entity)entityView.getAssociedComponent());
  }
  
  public static void deepDeleteEntity(Entity entity) {
    
    boolean isRecord = Change.isRecord();
    Change.record();

    PanelClassDiagram.getInstance().getClassDiagram().removeComponent(entity);

    if (!isRecord)
      Change.stopRecord();
  }
}
