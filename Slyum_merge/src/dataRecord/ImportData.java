package dataRecord;

import java.io.File;
import java.util.HashMap;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Attribute;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;
import classDiagram.components.Type;
import classDiagram.components.Visibility;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Inheritance;
import swing.PanelClassDiagram;

public class ImportData
{
	private final classDiagram.ClassDiagram classDiagram = PanelClassDiagram.getInstance().getClassDiagram();
	private final ProjectManager project = ProjectManager.getInstance();
	private ParserScanner ps;
	private HashMap<String, Integer> findIdByName = new HashMap<String, Integer>();
	
	public ImportData(File[] files)
	{
		getData(files);
		doTranslation();
	}
	
	public void getData(File[] files)
	{
		project.getFilesRecord().clear();
		ps = new ParserScanner(files);
		ps.printDebug();
	}
	
	private void doTranslation()
	{
		System.out.println("DO TRANSATION for " + project.getFilesRecord().size() + " file(s)");
		project.setName(classDiagram.getName());
			
		for (CompilationUnit cu : project.getFilesRecord())
		{
			for (Element e : cu.getElements())
			{
				classDiagram.components.Entity ce = null;
				
				if(e.getClass() == ClassType.class)
				{
					ClassType ct = new ClassType((ClassType)e);
					ce = new ClassEntity(ct.getName(), translateAccessModifiers(ct.getAccess()),ct.getID());
					classDiagram.addClass((ClassEntity) ce);
					findIdByName.put(ce.getName(), new Integer(ce.getId()));
					
					// add member and operations
					for (Element inner : ct.getElements())
					{
						if(inner.getClass() == Constructor.class)
						{
							addConstructor((Constructor)inner, ce);
						}
						
						else if(inner.getClass() == dataRecord.Method.class)
						{
							addMethod((dataRecord.Method)inner, ce);
						}
						
						else if(inner.getClass() == dataRecord.Attribute.class)
						{
							addAttribute((dataRecord.Attribute)inner, ce);
						}
						else if(inner.getClass() == ClassType.class) // classe interne
						{
							// ajouter une classe interne
						}
							
					}
					ce.notifyObservers();
				}
				
				else if(e.getClass() == InterfaceType.class)
				{
					InterfaceType it = new InterfaceType((InterfaceType)e);
					ce = new InterfaceEntity(it.getName(), translateAccessModifiers(it.getAccess()),it.getID());
					classDiagram.addInterface((InterfaceEntity) ce);
					findIdByName.put(ce.getName(), new Integer(ce.getId()));
					
					// add member and operations
					for (Element inner : it.getElements())
					{	
						if(inner.getClass() == dataRecord.Method.class)
						{
							addMethod((dataRecord.Method)inner, ce);
						}
						
						else if(inner.getClass() == dataRecord.Attribute.class)
						{
							addAttribute((dataRecord.Attribute)inner, ce);
						}
					}
					ce.notifyObservers();
				}
				
				//TODO add enum
				
				// dont do a generic ce.notifyObserver() because package and import statement will raise a null pointer exception
				
			}
			
		}
		
		// add Inheritance 
		for (CompilationUnit cu : project.getFilesRecord())
		{
			for (Element e : cu.getElements())
			{
				if(e.getClass() == ClassType.class)
					if(!((ClassType)e).getExtendList().isEmpty())
					{
						int childID = findIdByName.get(e.getName());
						Entity child = (Entity)classDiagram.searchComponentById(childID);
						if(((ClassType) e).getExtendList().get(0).getClass() == ClassType.class)
						{
							ClassType parent = (ClassType) ((ClassType)e).getExtendList().get(0);
							int parentID = findIdByName.get(parent.getName());
							Entity parent1 = (Entity)classDiagram.searchComponentById(parentID);
							classDiagram.addInheritance(new Inheritance(child, parent1));
						}
						else
						{
							ClassEntity ce2 = new ClassEntity(((APIclass)((ClassType)e).getExtendList().get(0)).getElementType(),Visibility.PUBLIC);
							classDiagram.addClass(ce2);
							ce2.notifyObservers();
							classDiagram.addInheritance(new Inheritance(child, ce2));
						}		
					}
			}
		}
		
		
		// add interfaces 
		//TODO add APIInterfaces
		for (CompilationUnit cu : project.getFilesRecord())
		{
			for (Element e : cu.getElements())
			{
				if(e.getClass() == ClassType.class)
					if(!((ClassType)e).getImplList().isEmpty())
					{
						int childID = findIdByName.get(e.getName());
						Entity child = (Entity)classDiagram.searchComponentById(childID);
						for(int z=0; z < ((ClassType)e).getImplList().size(); z++)
						{	
							if(((ClassType) e).getImplList().get(0).getClass() == InterfaceType.class)
							{
								InterfaceType parent = (InterfaceType) ((ClassType)e).getImplList().get(z);
								int parentID = findIdByName.get(parent.getName());
								Entity parent1 = (Entity)classDiagram.searchComponentById(parentID);
								classDiagram.addDependency(new Dependency(child, parent1));
							}
						}
					}
			}
		}		
	}
	
	
	private void addAttribute(dataRecord.Attribute inner, Entity ce)
	{
		final Attribute a = new Attribute(inner.name, new Type(inner.type.getElementType()));
		ce.addAttribute(a);
		ce.notifyObservers(UpdateMessage.ADD_ATTRIBUTE_NO_EDIT);
		a.setConstant(inner.isFinal());
		a.setDefaultValue(inner.getValue());
		a.setStatic(inner.isStatic());
		a.setVisibility(translateAccessModifiers(inner.getAccess()));

		a.notifyObservers();		
	}

	private void addMethod(dataRecord.Method data, Entity ce)
	{
		Method m = new Method(data.getName(), new Type(data.getReturnType()), translateAccessModifiers(data.getAccess()), ce);
		ce.addMethod(m);
		ce.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);
		m.setStatic(data.isStatic());
		m.setAbstract(data.isAbstract());
		
		// add params
		for (final Parametre p : data.getParams())
		{
			final classDiagram.components.Variable va = new classDiagram.components.Variable(p.name, new Type(p.type.getElementType()));

			m.addParameter(va);
		}
	}
	
	private void addConstructor(Constructor c, Entity ce)
	{
		Method m = new Method(c.getName(), new Type("c"), translateAccessModifiers(c.getAccess()), ce);
		ce.addMethod(m);
		ce.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);
		m.setStatic(false);
		m.setAbstract(false);
	}
	
	
	private Visibility translateAccessModifiers(Keyword kw)
	{
		switch (kw)
		{
			case PRIVATE:
				
				return Visibility.PRIVATE;
				
			case PUBLIC:
				
				return Visibility.PUBLIC;
				
			case PACKAGE:
				
				return Visibility.PACKAGE;
				
			case PROTECTED:
				
				return Visibility.PROTECTED;
				
			default:
				
				try
				{
					throw new IllegalAccessException(" Access modifiers invalid !");
				} catch (IllegalAccessException e)
				{e.printStackTrace();}
				return null;
		}
	}
}
