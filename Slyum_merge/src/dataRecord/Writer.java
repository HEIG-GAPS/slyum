package dataRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import swing.Slyum;
import utility.SMessageDialog;

public class Writer 
{
	private String dirPath = "";
	private File dir;
	private String log = "";
	private ElementVisitor visitor;
	private String outputCode;
	
	public Writer(String path, ElementVisitor visitor)
	{
		this.visitor = visitor;
		this.dirPath = path;
		dir = new File(dirPath);
		
		if( visitor.getClass() == JavaVisitor.class)
			outputCode = "java";
		else
			outputCode = "cpp";
	}

	public void write(LinkedList<CompilationUnit> units)
	{
		if(!dir.exists())
			dir.mkdir();
		if( visitor.getClass() == JavaVisitor.class)
		log += "Output code: "+outputCode+"\n";
		
		for (CompilationUnit unit : units)
		{
			File file;
			if(unit.getFile() == null) 
			{
				file = new File(dirPath+Slyum.FILE_SEPARATOR+unit.getName()+"."+outputCode);
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
				out.write(unit.accept(visitor));
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
