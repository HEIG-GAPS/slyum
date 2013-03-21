/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package change;

import classDiagram.components.Variable;

/**
 *
 * @author David
 */
public class BufferVariable implements Changeable
{
	private Variable variable, copy;
	
	public BufferVariable(Variable variable)
	{
		this.variable = variable;
		copy = new Variable(variable);
	}

	@Override
	public void restore()
	{		
		variable.setVariable(copy);
	}
	
}
