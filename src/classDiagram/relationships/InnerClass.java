package classDiagram.relationships;

import utility.Utility;
import classDiagram.components.Entity;

public class InnerClass extends Inheritance
{

	public InnerClass(Entity child, Entity parent)
	{
		super(child, parent);
	}

	public InnerClass(Entity child, Entity parent, int id)
	{
		super(child, parent, id);
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);

		final String xml = tab + "<inheritance id=\"" + id + "\" innerClass=\"true\">\n" + tab + "\t<child>" + child.getId() + "</child>\n" + tab + "\t<parent>" + parent.getId() + "</parent>\n";

		return xml + tab + "</inheritance>";
	}

}
