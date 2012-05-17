package dataRecord;

import classDiagram.IDiagramComponent;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Visibility;
import swing.PanelClassDiagram;

public class ExportData
{
	final String path;
	private final classDiagram.ClassDiagram classDiagram = PanelClassDiagram.getInstance().getClassDiagram();
	private final ProjectManager project = ProjectManager.getInstance();
	private IDiagramComponent currentComponent;
	
	public ExportData( String path)
	{
		this.path = path;
		
		export();
	}
	
	private void export()
	{
		createNewProject();
	}
	
	private void createNewProject()
	{
		project.getFilesRecord().clear();
		
		project.setName(classDiagram.getName());
		
		for(int id=0; id<classDiagram.getComponents().size(); id++)
		{
			currentComponent = classDiagram.getComponents().get(id);
			
			if(currentComponent.getClass() == ClassEntity.class)
			{
				CompilationUnit cux = new CompilationUnit();
				ClassType ct = new ClassType(((ClassEntity)currentComponent).getName(), 
								translateAccessModifiers(((ClassEntity)currentComponent).getVisibility()));
				
				cux.addElement(ct);
				project.addCUnit(cux);
			} 
			
			else if(currentComponent.getClass() == InterfaceEntity.class)
			{
				CompilationUnit cux = new CompilationUnit();
				InterfaceType ct = new InterfaceType(((InterfaceEntity)currentComponent).getName(), 
								translateAccessModifiers(((InterfaceEntity)currentComponent).getVisibility()));
				
				cux.addElement(ct);
				project.addCUnit(cux);
			} 
		}
		
		project.generateFiles(new JavaWriter(path));
	}
	
	
	
	
	private Keyword translateAccessModifiers(Visibility v)
	{
		switch (v)
		{
			case PRIVATE:
				
				return Keyword.PRIVATE;
				
			case PUBLIC:
				
				return Keyword.PUBLIC;
				
			case PACKAGE:
				
				return Keyword.PACKAGE;
				
			case PROTECTED:
				
				return Keyword.PROTECTED;
				
			default:
				
				try
				{
					throw new IllegalAccessException(" Access modifiers invalid !");
				} catch (IllegalAccessException e)
				{e.printStackTrace();}
				return null;
		}
	}
	
	public void printDebug() 
	{
		 System.out.println("!!!!! DEBUG EXPORT");
		for (CompilationUnit unit : project.getFilesRecord())
		{
			System.out.println("///// Debut de: " + unit.getName());
			for (Element e : unit.getElements())
			{
				System.out.println(e);
			}
			System.out.println("///////////////////////////////////////////////////////\n");
		}
	}
}
