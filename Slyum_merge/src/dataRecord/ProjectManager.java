package dataRecord;

import java.util.LinkedList;
import java.util.List;

public class ProjectManager
{
	private String name;
	private LinkedList<CompilationUnit> filesRecord = new LinkedList<CompilationUnit>();
	private static ProjectManager instance = new ProjectManager();

	private ProjectManager()
	{}

	public List<CompilationUnit> getFilesRecord()
	{
		return filesRecord;
	}

	public void setFilesRecord(LinkedList<CompilationUnit> filesRecord)
	{
		this.filesRecord = filesRecord;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
		
	}

	public void addCUnit(CompilationUnit cu)
	{
		filesRecord.add(cu);
	}

	public void removeCUnit(String name)
	{
		for (int i = 0; i < filesRecord.size(); i++)
		{
			if (filesRecord.get(i).getName().equals(name))
				filesRecord.remove(i);
		}
	}

	public void generateFiles(Writer writer)
	{
		writer.write(filesRecord);
	}

	public static ProjectManager getInstance()
	{
		return instance;
	}
	
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
				for (Element t : inner.getElements())
				{
					if(t.getID() == id)
						return e;
				}
			}
			
		}
		return null;
	}
	
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
	
	
//	public void removeElement(Element e)
//	{
//		for (CompilationUnit cu : filesRecord)
//		{
//			if(cu.removeElement(e))
//				break;
//		}
//	}

}
