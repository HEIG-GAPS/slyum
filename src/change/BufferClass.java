/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package change;

import classDiagram.components.Entity;
import classDiagram.components.Visibility;

/**
 *
 * @author David
 */
public class BufferClass implements Changeable
{
	private Entity entity;
	private String name;
	private boolean isAbstract;
    private Visibility visibility;
	
	
	public BufferClass(Entity e)
	{
	    entity = e;
		name = e.getName();
		isAbstract = e.isAbstract();
		visibility = e.getVisibility();
	}

	@Override
	public void restore()
	{
		entity.setAbstract(isAbstract);
		entity.setVisibility(visibility);
		entity.setName(name);
		
		entity.notifyObservers();
	}
}
