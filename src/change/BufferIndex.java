/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package change;

import classDiagram.IDiagramComponent;
import java.util.LinkedList;

/**
 *
 * @author David
 */
public class BufferIndex<T extends Object> implements Changeable
{
	private IDiagramComponent entity;
	private T o;
	private int index;
	private LinkedList<T> list;
	
	public BufferIndex(IDiagramComponent e, LinkedList<T> list, T o)
	{
		entity = e;
		this.o = o;
		this.list = list;
		index = list.indexOf(o);
	}

	@Override
	public void restore()
	{
		IDiagramComponent i = (IDiagramComponent)o;
		list.remove(o);
		list.add(index, o);
		
		entity.select();
		entity.notifyObservers();
		i.select();
		i.notifyObservers(IDiagramComponent.UpdateMessage.SELECT);
	}
}
