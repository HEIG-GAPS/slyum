/**
 * The class represent the starting point for the whole structure
 * It contains a data structures representing a project made of one or
 * many source code files. Each file of source code is parsed and represent
 * a compilation unit.
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 5-lug-2012
 */
package dataRecord;

import java.util.LinkedList;

import dataRecord.elements.CompilationUnit;
import dataRecord.elements.Element;
import dataRecord.elements.Type;
import dataRecord.io.Writer;

public class ProjectManager
{
	private String name;
	private LinkedList<CompilationUnit> filesRecord = new LinkedList<CompilationUnit>();
	private static ProjectManager instance = new ProjectManager();

	/**
	 * the constructor
	 */
	private ProjectManager()
	{}

	/**
	 * return all the compilation units
	 * 
	 * @return
	 */
	public LinkedList<CompilationUnit> getFilesRecord()
	{
		return filesRecord;
	}

	/**
	 * set a new list of compilation units (change project)
	 * 
	 * @param filesRecord
	 */
	public void setFilesRecord(LinkedList<CompilationUnit> filesRecord)
	{
		this.filesRecord = filesRecord;
	}

	/**
	 * return the project name
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
		
	}

	/**
	 * add a new compilation unit, a new parsed file
	 * @param cu
	 */
	public void addCUnit(CompilationUnit cu)
	{
		filesRecord.add(cu);
	}

	/**
	 * retrieve and remove a compilation unit given by his name
	 * 
	 * @param name
	 */
	public void removeCUnit(String name)
	{
		for (int i = 0; i < filesRecord.size(); i++)
		{
			if (filesRecord.get(i).getName().equals(name))
				filesRecord.remove(i);
		}
	}

	/**
	 * generate the concrete file in the selected language
	 * 
	 * @param writer
	 */
	public void generateFiles(Writer writer)
	{
		writer.write(filesRecord);
	}

	public static ProjectManager getInstance()
	{
		return instance;
	}
	
	/**
	 * return a specific element chosen by ID
	 * 
	 * @param id
	 * @return
	 */
	public Element getElementByID(int id)
	{
		for (CompilationUnit cu : filesRecord)
		{
			for (Element e : cu.getElements())
			{
				if(e.getID() == id)
					return e;
			}
			
			for (Element e : cu.getElements())
			{
				Type inner = null;
				if(e instanceof Type)
					inner = (Type) e;
				try
				{
					for (Element t : inner.getElements())
					{
						if(t.getID() == id)
							return e;
					}
				} catch (Exception e2)
				{
					// TODO: handle exception
				}
			}
			
		}
		return null;
	}
	
	/**
	 * return a specific element chosen by name
	 * 
	 * @param id
	 * @return
	 */
	public Element getElementFromProject(String name)
	{
		for (CompilationUnit cuts : filesRecord)
		{
			for (Element e : cuts.getElements())
			{
				if (e.getName().equals(name))
						return e;
			}
			
		}
		return null;
	}
	
	public Element getCU(String name)
	{
		for (CompilationUnit cuts : filesRecord)
		{
			if( cuts.getName().equals(name))
				return cuts;
		}
		return null;
	}
	
	public static ProjectManager getEmptyClone()
	{
		return new ProjectManager();
	}
	
	
//	public void removeElement(Element e)
//	{
//		for (CompilationUnit cu : filesRecord)
//		{
//			if(cu.removeElement(e))
//				break;
//		}
//	}

}
