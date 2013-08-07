package classDiagram.relationships;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import classDiagram.components.SimpleEntity;

public class InnerClass extends Inheritance
{

	public InnerClass(SimpleEntity child, SimpleEntity parent)
	{
		super(child, parent);
	}

	public InnerClass(SimpleEntity child, SimpleEntity parent, int id)
	{
		super(child, parent, id);
	}
	
	@Override
	public Element getXmlElement(Document doc) {
	  Element innerClass = super.getXmlElement(doc);
	  innerClass.setAttribute("innerClass", "true");
	  return innerClass;
	}
}
