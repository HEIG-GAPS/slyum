package dataRecord.io;

import java.util.LinkedList;

import classDiagram.IDiagramComponent;
import classDiagram.components.Attribute;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Variable;
import classDiagram.components.Visibility;
import classDiagram.relationships.*;
import dataRecord.Keyword;
import dataRecord.ProjectManager;
import dataRecord.elementType.ArrayType;
import dataRecord.elementType.Collection;
import dataRecord.elementType.ElementType;
import dataRecord.elementType.ListType;
import dataRecord.elements.ClassType;
import dataRecord.elements.Comment;
import dataRecord.elements.CompilationUnit;
import dataRecord.elements.Element;
import dataRecord.elements.InterfaceType;
import dataRecord.elements.Member;
import dataRecord.elements.Parameter;
import dataRecord.elements.Statement;
import dataRecord.elements.Type;
import swing.PanelClassDiagram;

/**
 * This class job is to get the uml diagram and to transform it into source code files.
 * If the uml diagram is the result of an importation, the exportation take in consideration
 * the code already written.
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 11-lug-2012
 */
public class ExportData extends Thread
{
	/**
	 * the destination path
	 */
	final String path;
	/**
	 * the uml class diagram
	 */
	private final classDiagram.ClassDiagram classDiagram = PanelClassDiagram.getInstance().getClassDiagram();
	/**
	 * the project manager
	 */
	private final ProjectManager project = ProjectManager.getInstance();
	private IDiagramComponent currentComponent;
	/**
	 * the selected language
	*/
	private ElementVisitor v;
	
	public ExportData(String path, ElementVisitor v)
	{
		this.path = path;
		this.v = v;
	}
	
	private void export()
	{
		//createNewProject();
		if(v.getClass() == JavaVisitor.class)
			update();
		else
			project.generateFiles(new Writer(path,v));
	}
	
	private void update()
	{
		// the latest state of the classDiagram
		ProjectManager copie = createNewProject();
		//if the classDiagram has been modified, need to report the modification to the project manager.
		
		// parcourir la copie et rajouter les infos manquantes
		for(int cu=0; cu<copie.getFilesRecord().size(); cu++)
		{
			CompilationUnit cuOld = (CompilationUnit) project.getCU(copie.getFilesRecord().get(cu).getName());
			CompilationUnit cuNew = copie.getFilesRecord().get(cu);
			
			if(cuOld != null)
			{
				int insert=0;
				for (Element e : cuOld.getElements())
				{
					if(e instanceof Statement || e.getClass() == Comment.class)
					{
						cuNew.getElements().add(insert, e);
						insert++;
					}
				}
				
				for(int index=0; index<cuNew.getElements().size(); index++)
				{	
					Element old = cuOld.getElement(cuNew.getElements().get(index).getName());
					Element recent = cuNew.getElements().get(index);

					if(old != null)
					{
						if (recent instanceof Type)
						{
							Type tn = (Type)recent;
							Type to = (Type)old;
							LinkedList<CommentWrapper> cw = new LinkedList<>();
							
							for (Element latestElement : tn.getElements())
							{
								Element older = to.getElementByName(latestElement.getName());
								
								if(older != null)
								{	
									// add the comment
									int commentIndex = to.getElements().indexOf(older);
									int commentInsertIndex = tn.getElements().indexOf(latestElement);
									if(to.getElements().get(commentIndex-1).getClass() == Comment.class)
										cw.add(new CommentWrapper((Comment) to.getElements().get(commentIndex-1), commentInsertIndex));
									if(latestElement instanceof Member)
									{
										Member mo = (Member) to.getMemberByName(latestElement.getName());
										Member mn = (Member)latestElement;
										mn.setThrowClauses(mo.getThrowClauses());
										mn.setMethodBody(mo.getMethodBody());
									}
							
									else if(latestElement instanceof Variable)
									{
										dataRecord.elements.Variable fo = (dataRecord.elements.Variable) to.getFieldByName(latestElement.getName());
										dataRecord.elements.Variable fn = (dataRecord.elements.Variable)latestElement;
										fn.setValue(fo.getValue());
									}
								}
							}
							
							for(int c=cw.size()-1; c >= 0; c--)
							{
								tn.getElements().add(cw.get(c).index, cw.get(c).c);
							}
						}
					}
				}
			}
		}
		
		copie.generateFiles(new Writer(path,v));
	}
	
	private ProjectManager createNewProject()
	{
		ProjectManager tmp = ProjectManager.getEmptyClone();
		LinkedList<CompilationUnit> tmpFiles = tmp.getFilesRecord();
		
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
				ct.setGeneric(((ClassEntity)currentComponent).getGeneric());
				cux.addElement(ct);
				tmpFiles.add(cux);
			} 
			
			else if(currentComponent.getClass() == InterfaceEntity.class)
			{
				CompilationUnit cux = new CompilationUnit();
				InterfaceType it = new InterfaceType(((InterfaceEntity)currentComponent).getName(), 
								translateAccessModifiers(((InterfaceEntity)currentComponent).getVisibility()), currentComponent.getId());
				
				cux.addElement(it);
				tmpFiles.add(cux);
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
				
				ClassType child = (ClassType)tmp.getElementByID(inherit.getChild().getId());
				ClassType parent = (ClassType)tmp.getElementByID(inherit.getParent().getId());
				
				child.getExtendList().add(parent);
			}
			
			//add interface
			if (currentComponent.getClass() == Dependency.class)
			{
				Dependency d = (Dependency)currentComponent;
				
				ClassType child = (ClassType)tmp.getElementByID(d.getSource().getId());
				InterfaceType parent = (InterfaceType)tmp.getElementByID(d.getTarget().getId());
				
				child.getImplList().add(parent);
			}
			
			// add Attributes and methods
			if (currentComponent.getClass() == ClassEntity.class || currentComponent.getClass() == InterfaceEntity.class)
			{
				Entity centity = (Entity)currentComponent;
				dataRecord.elements.Type parent = (dataRecord.elements.Type)tmp.getElementByID(currentComponent.getId());
				
				if(!centity.getAttributes().isEmpty())
				{
					for(int a=0; a<centity.getAttributes().size(); a++)
					{
						Attribute aEntity= (Attribute)centity.getAttributes().get(a);
						Keyword access = translateAccessModifiers(aEntity.getVisibility());
						dataRecord.elements.Attribute attr = new dataRecord.elements.Attribute(aEntity.getName(), 
													access, new dataRecord.elements.Type(aEntity.getType().getName(),access,Keyword.CLASS), aEntity.getId());
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
						dataRecord.elements.Method meth = new dataRecord.elements.Method(mEntity.getName(), 
													access, mEntity.getReturnType().toString(),mEntity.getId());
						//meth.setFinal pas implemente dans Slyum
						meth.setStatic(mEntity.isStatic());
						meth.setAbstract(mEntity.isAbstract());
						//TODO ARRAY??
						if(mEntity.isAbstract())
							meth.setMethodBody(";");
						else
							meth.setMethodBody(" {}");
						
						// add params
						if(!mEntity.getParameters().isEmpty())
						{
							for(int p=0; p<mEntity.getParameters().size(); p++)
							{
								Variable v = mEntity.getParameters().get(p);
								Parameter param = new Parameter(v.getName(), new dataRecord.elements.Type(v.getType().getName(),access,Keyword.CLASS), v.getId());
								
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
				ClassType child = new ClassType((ClassType)tmp.getElementByID(innerc.getChild().getId()));
				ClassType parent = (ClassType)tmp.getElementByID(innerc.getParent().getId());
				
				parent.addElement(child);
				tmp.removeCUnit(child.getName());
			}
			
			// add relationships
			if(currentComponent instanceof Binary)
			{
				Binary b = (Binary)currentComponent;
				Role from = b.getRoles().getFirst();
				Role to = b.getRoles().getLast();
				
				ClassType classInto = (ClassType) tmp.getElementByID(from.getEntity().getId());
				
				classInto.addElement(getAttrFromRole(tmp, from, to));
				
				if(!b.isDirected())
				{
					ClassType classInto2 = (ClassType) tmp.getElementByID(to.getEntity().getId());

					classInto2.addElement(getAttrFromRole(tmp, to, from));
				}
				
				System.out.println(from.getEntity().getName()+ " : "+ from.getMultiplicity());
				System.out.println(to.getEntity().getName()+ " : "+ to.getMultiplicity());
				
			}
		}
		return tmp;
		
	}
	
	private dataRecord.elements.Attribute getAttrFromRole(ProjectManager project, Role from, Role to)
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
			ListType lt = new ListType((ElementType)project.getElementByID(to.getEntity().getId()));
			if(!to.getName().isEmpty())
			{
				for (Collection c : Collection.values())
				{
					if(to.getName().contains(c.toString()))
					{
						lt.setCollection(c);
						name = to.getName().replace(c.toString(), "");
						name = name.replace("{}", "");
						//System.err.println("before"+name);
						if(!name.matches(".*[a-zA-Z]+.*"))
						{
							name = to.getEntity().getName().toLowerCase();
//							System.err.println("match " + name);
//							System.out.println("---");
						}
						break;
					}
				}	
			}
			etype = lt;
		}
		dataRecord.elements.Attribute dRa = new dataRecord.elements.Attribute(name,
															translateAccessModifiers(to.getVisibility()),
															etype,
															to.getId());
		return dRa;
	}
	
	public void run()
	{
		export();
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
	
	private class CommentWrapper
	{
		Comment c;
		int index;
		
		CommentWrapper(Comment c, int index)
		{
			this.c = c;
			this.index = index;
		}
	}
}
