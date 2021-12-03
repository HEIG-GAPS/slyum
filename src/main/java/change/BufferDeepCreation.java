/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package change;

import classDiagram.IDiagramComponent;
import swing.MultiViewManager;

import java.util.List;
import java.util.stream.Collectors;

public class BufferDeepCreation implements Changeable {

  private boolean isCreated;
  private IDiagramComponent component;
  private List<BufferCreation> bufferedGraphicComponent;

  public BufferDeepCreation(boolean isCreated, IDiagramComponent component) {
    this.isCreated = isCreated;

    setComponent(component);
  }

  @Override
  public void restore() {
    bufferedGraphicComponent.stream().forEach(bc -> bc._restore());
  }

  @Override
  public Object getAssociedComponent() {
    return component;
  }

  public void setComponent(IDiagramComponent component) {

    if (component == null)
      return;

    this.component = component;

    bufferedGraphicComponent =
        MultiViewManager.getAllGraphicViews().stream()
                        .filter(gv -> gv.searchAssociedComponent(component) != null)
                        .map(gv -> new BufferCreation(isCreated, gv.searchAssociedComponent(component)))
                        .collect(Collectors.toList());
  }

}
