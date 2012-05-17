package dataRecord;

import java.util.LinkedList;
import java.util.List;

public class ProjectManager
{
	private String name;
	private LinkedList<CompilationUnit> filesRecord = new LinkedList<CompilationUnit>();
	private static ProjectManager instance;

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
		if (instance == null)
			return new ProjectManager();
		return instance;
	}

}
