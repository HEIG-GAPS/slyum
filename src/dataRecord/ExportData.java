package dataRecord;

import classDiagram.IDiagramComponent;
import classDiagram.components.Attribute;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Variable;
import classDiagram.components.Visibility;
import classDiagram.relationships.*;
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
		
		// add and create all class and interfaces entity
		// so all C/I will be known for further add of parents / methods / field / etc
		for(int id=0; id<classDiagram.getComponents().size(); id++)
		{
			currentComponent = classDiagram.getComponents().get(id);
			
			if(currentComponent.getClass() == ClassEntity.class)
			{
				CompilationUnit cux = new CompilationUnit();
				ClassType ct = new ClassType(((ClassEntity)currentComponent).getName(), 
								translateAccessModifiers(((ClassEntity)currentComponent).getVisibility()), currentComponent.getId());
						
				ct.setAbstract(((ClassEntity)currentComponent).isAbstract());
				cux.addElement(ct);
				project.addCUnit(cux);
			} 
			
			else if(currentComponent.getClass() == InterfaceEntity.class)
			{
				CompilationUnit cux = new CompilationUnit();
				InterfaceType it = new InterfaceType(((InterfaceEntity)currentComponent).getName(), 
								translateAccessModifiers(((InterfaceEntity)currentComponent).getVisibility()), currentComponent.getId());
				
				cux.addElement(it);
				project.addCUnit(cux);
			} 
		}
		
		// do a second walk into classDiagram components to add the rest of the components
		for(int id=0; id<classDiagram.getComponents().size(); id++)
		{
			currentComponent = classDiagram.getComponents().get(id);
			
			// add parents
			if (currentComponent.getClass() == Inheritance.class)
			{
				Inheritance inherit = (Inheritance)currentComponent;
				
				ClassType child = (ClassType)project.getElementByID(inherit.getChild().getId());
				ClassType parent = (ClassType)project.getElementByID(inherit.getParent().getId());
				
				child.getExtendList().add(parent);
			}
			
			//add interface
			if (currentComponent.getClass() == Dependency.class)
			{
				Dependency d = (Dependency)currentComponent;
				
				ClassType child = (ClassType)project.getElementByID(d.getSource().getId());
				InterfaceType parent = (InterfaceType)project.getElementByID(d.getTarget().getId());
				
				child.getImplList().add(parent);
			}
			
			// add Attributes and methods
			if (currentComponent.getClass() == ClassEntity.class || currentComponent.getClass() == InterfaceEntity.class)
			{
				Entity centity = (Entity)currentComponent;
				dataRecord.Type parent = (dataRecord.Type)project.getElementByID(currentComponent.getId());
				
				if(!centity.getAttributes().isEmpty())
				{
					for(int a=0; a<centity.getAttributes().size(); a++)
					{
						Attribute aEntity= (Attribute)centity.getAttributes().get(a);
						Keyword access = translateAccessModifiers(aEntity.getVisibility());
						dataRecord.Attribute attr = new dataRecord.Attribute(aEntity.getName(), 
													access, new dataRecord.Type(aEntity.getType().getName(),access,Keyword.CLASS), aEntity.getId());
						attr.setFinal(aEntity.isConstant());
						attr.setStatic(aEntity.isStatic());
						//TODO ARRAY??
						
						parent.addElement(attr);
					}
				}
				
				
				if(!centity.getMethods().isEmpty())
				{
					for(int m=0; m<centity.getMethods().size(); m++)
					{
						classDiagram.components.Method mEntity= (classDiagram.components.Method)centity.getMethods().get(m);
						Keyword access = translateAccessModifiers(mEntity.getVisibility());
						dataRecord.Method meth = new dataRecord.Method(mEntity.getName(), 
													access, mEntity.getReturnType().toString(),mEntity.getId());
						//meth.setFinal pas implemente dans Slyum
						meth.setStatic(mEntity.isStatic());
						meth.setAbstract(mEntity.isAbstract());
						//TODO ARRAY??
						
						// add params
						if(!mEntity.getParameters().isEmpty())
						{
							for(int p=0; p<mEntity.getParameters().size(); p++)
							{
								Variable v = mEntity.getParameters().get(p);
								Parametre param = new Parametre(v.getName(), new dataRecord.Type(v.getType().getName(),access,Keyword.CLASS), v.getId());
								
								meth.addParam(param);
							}
						}
						
						parent.addElement(meth);
					}
				}	
			}			
		}
		
		// do a third walk into classDiagram components to manage the associations
		for(int id=0; id<classDiagram.getComponents().size(); id++)
		{
			currentComponent = classDiagram.getComponents().get(id);

			// add inner classes
			if(currentComponent.getClass() == InnerClass.class)
			{
				InnerClass innerc = (InnerClass)currentComponent;
				ClassType child = new ClassType((ClassType)project.getElementByID(innerc.getChild().getId()));
				ClassType parent = (ClassType)project.getElementByID(innerc.getParent().getId());
				
				parent.addElement(child);
				project.removeCUnit(child.getName());
			}
			
			if(currentComponent instanceof Binary)
			{
				Binary b = (Binary)currentComponent;
				Role from = b.getRoles().getFirst();
				Role to = b.getRoles().getLast();
				
				ClassType classInto = (ClassType) project.getElementByID(from.getEntity().getId());
				
				classInto.addElement(getAttrFromRole(from, to));
				
				if(!b.isDirected())
				{
					ClassType classInto2 = (ClassType) project.getElementByID(to.getEntity().getId());

					classInto2.addElement(getAttrFromRole(to, from));
				}
				
				System.out.println(from.getEntity().getName()+ " : "+ from.getMultiplicity());
				System.out.println(to.getEntity().getName()+ " : "+ to.getMultiplicity());
				
			}
		}
		
		project.generateFiles(new JavaWriter(path));
	}

	
	
	private dataRecord.Attribute getAttrFromRole(Role from, Role to)
	{
		String name = to.getName().isEmpty()?to.getEntity().getName().toLowerCase():to.getName();
		ElementType etype = null;
		try
		{
			int i = Integer.valueOf(to.getMultiplicity().toString());
			if (i == 1)
				etype = (ElementType)project.getElementByID(to.getEntity().getId());
			else
				etype = new ArrayType((ElementType)project.getElementByID(to.getEntity().getId()),1);
		} catch (Exception e)
		{
			etype = new ListType((ElementType)project.getElementByID(to.getEntity().getId()));
		}
		dataRecord.Attribute dRa = new dataRecord.Attribute(name,
															translateAccessModifiers(to.getVisibility()),
															etype,
															to.getId());
		return dRa;
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
