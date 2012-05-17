/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package change;

import classDiagram.components.Method;

/**
 *
 * @author David
 */
public class BufferMethod implements Changeable
{
	private Method method, copy;

	public BufferMethod(Method method)
	{
		this.method = method;
		copy = new Method(method);
	}
	
	@Override
	public void restore()
	{
		method.setMethod(copy);
	}
	
}
