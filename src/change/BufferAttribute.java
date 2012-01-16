/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package change;

import classDiagram.components.Attribute;

/**
 *
 * @author David
 */
public class BufferAttribute extends BufferVariable
{
	private Attribute attribute, copy;
	
	public BufferAttribute(Attribute attribute)
	{
		super(attribute);
		this.attribute = attribute;
		copy = new Attribute(attribute);
	}

	@Override
	public void restore()
	{
		super.restore();
		attribute.setAttribute(copy);
	}
	
}
