/* To change this template, choose Tools | Templates and open the template in
 * the editor. */
package change;

import java.util.List;

import classDiagram.IDiagramComponent;

/**
 * 
 * @author David
 */
public class BufferIndex<T extends Object> implements Changeable {
  private IDiagramComponent entity;
  private T o;
  private int index;
  private List<T> list;

  public BufferIndex(IDiagramComponent e, List<T> list, T o) {
    entity = e;
    this.o = o;
    this.list = list;
    index = list.indexOf(o);
  }

  @Override
  public void restore() {
    IDiagramComponent i = (IDiagramComponent) o;
    list.remove(o);
    list.add(index, o);

    entity.select();
    entity.notifyObservers();
    i.select();
    i.notifyObservers(IDiagramComponent.UpdateMessage.SELECT);
  }
}
