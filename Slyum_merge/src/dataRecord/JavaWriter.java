package dataRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import utility.SMessageDialog;

@SuppressWarnings("unused")
public class JavaWriter implements Writer
{
	private String dirPath = "";
	private File dir;
	private FileWriter writer;
	private String log = "";
	
	public JavaWriter(String path)
	{
		this.dirPath = path;
		dir = new File(dirPath); 
	}

	@Override
	public void write(LinkedList<CompilationUnit> units)
	{
		if(!dir.exists())
			dir.mkdir();
		for (CompilationUnit unit : units)
		{
			File file;
			if(unit.getFile() == null) 
			{
				file = new File(dirPath+"/"+unit.getName()+".java");
				unit.setFile(file);
			}
			else
				file = unit.getFile();
			try
			{
				if(!file.exists())
					file.createNewFile();
				
				FileWriter fstream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(unit.toString());
				out.close();
				log += unit.getName()+" written successfully.";
				log += "\n";
			} catch (IOException e)
			{
				log += "/!\\ " + unit.getName() + " could not be written. /!\\";
				log += "\n";
				e.printStackTrace();
			}
		}
		
		SMessageDialog.showInformationMessage(log);
	}

}
