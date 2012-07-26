package dataRecord.elements;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import dataRecord.io.ElementVisitor;

/**
 * This class represent a source code file 
 * It contain a list of elements.
 * 
 * @see Element
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 10-lug-2012
 */
public class CompilationUnit implements Element
{
	// private String name; // a deduire depuis le file name
	private List<Element> elements; 
	/**
	 * the related file 
	 */
	private File file;

	public CompilationUnit()
	{
		elements = new LinkedList<Element>();
	}

	@Override
	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 * 
	 */
	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	/**
	 * return the name, (same as the file name without the extension)
	 * if any file return the name of the first class/interface
	 */
	public String getName()
	{
		if (file == null)
			for (Element e : elements)
			{
				if(e.getClass() == ClassType.class || e.getClass() == InterfaceType.class || e.getClass() == EnumType.class)
					return e.getName();
			}
		
		int index = file.getName().lastIndexOf('.');

		return file.getName().substring(0, index);
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public void addElement(Element e)
	{
		elements.add(e);
	}

	public boolean removeElement(Element e)
	{
		return elements.remove(e);
	}

	public List<Element> getElements()
	{
		return elements;
	}

	public Element getElement(int index)
	{
		return elements.get(index);
	}
	
	public Element getElement(String name)
	{
		for (Element e : elements)
		{
			if(e.getName().equals(name))
				return e;
		}
		return null;
	}

	public void setElements(List<Element> liste)
	{
		this.elements = liste;
	}

	public String toString()
	{
		String tmp = "";

		for (Element e : elements)
		{
			tmp += e.toString();
		}

		return tmp;
	}

	@Override
	public int getID()
	{
		return 0;
	}

}
