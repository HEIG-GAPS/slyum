/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package change;

import graphic.GraphicComponent;
import graphic.entity.EntityView;


public class BufferFirstCreation extends BufferCreation {

  public BufferFirstCreation(GraphicComponent gc) {
    super(false, gc);
  }
  
  @Override
  public void _restore() {
    
    final boolean isBlocked = Change.isBlocked();
    Change.setBlocked(true);
    change.Helper.deepDeleteEntityView((EntityView)getAssociedComponent());
    Change.setBlocked(isBlocked);
  }
}
