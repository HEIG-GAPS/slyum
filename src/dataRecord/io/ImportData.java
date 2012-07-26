package dataRecord.io;

import graphic.GraphicView;
import graphic.entity.EntityView;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Attribute;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;
import classDiagram.components.Type;
import classDiagram.components.Visibility;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Inheritance;
import classDiagram.relationships.InnerClass;
import classDiagram.relationships.Multiplicity;
import dataRecord.Keyword;
import dataRecord.ProjectManager;
import dataRecord.elementType.ListType;
import dataRecord.elements.ClassType;
import dataRecord.elements.CompilationUnit;
import dataRecord.elements.Constructor;
import dataRecord.elements.Element;
import dataRecord.elements.InterfaceType;
import dataRecord.elements.Parameter;
import swing.PanelClassDiagram;
import swing.Slyum;
import utility.SMessageDialog;
import utility.Utility;

/**
 * This class manage all the importation of a source code project, from the parsing
 * to the displaying,
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 11-lug-2012
 */
public class ImportData extends Thread
{
	private final classDiagram.ClassDiagram classDiagram = PanelClassDiagram.getInstance().getClassDiagram();
	private final ProjectManager project = ProjectManager.getInstance();
	private File[] files;
	private LinkedList<Entity> association = new LinkedList<Entity>();
	private boolean doLayout = false; // update the layout after a sync()
	private Rectangle r;
	private Entity updated;
	private LinkedList<Entity> referendeToUpdate = new LinkedList<Entity>();
	
	public ImportData(File[] files)
	{
		this.files = files;
	}
	
	public ImportData(File[] files, boolean dolayout, Rectangle r, LinkedList<Entity> updateRef)
	{
		this(files);
		this.doLayout = dolayout;
		this.r = r;
		this.referendeToUpdate = updateRef;
	}
	
	public void run()
	{
		if(doLayout)
		{
			//remove the old CU
			int index = files[0].getName().lastIndexOf('.');
			project.removeCUnit(files[0].getName().substring(0, index));
		}
		
		// select the right parser (Java / C++)
		Parser parser = null;
		for(File file : files)
		{
			try{
				System.out.println(files.length);
				String extension = getExtension(file);
				if(!extension.isEmpty())
				{
					if(extension.equals(Slyum.JAVA_EXTENSION))
						parser = new ParserScanner();
					else if (extension.equals("h"))
						parser = new ParserCpp();
					else
						throw new IllegalArgumentException(" Must be a Java / C++ header source code file.");
					break;
				}
				
			}
			catch (IllegalArgumentException e) 
			{
				SMessageDialog.showErrorMessage(e.getMessage());
				return;
			}
			catch (FileNotFoundException e)
			{e.printStackTrace();}
		}	
			
		LinkedList<CompilationUnit> units = parser.parse(files);
		
		doTranslation(units);
		
		Layout layout = new Layout();
		if(!doLayout)
		{
			setAssociations();
			
			PanelClassDiagram.getInstance().getCurrentGraphicView().adjustWidthAllEntities();
			
			layout.layout();
		}
		else
		{
			EntityView ev = null;
			do
			{
				ev = PanelClassDiagram.getInstance().getCurrentGraphicView().getEntityAtPosition(new Point(0,0));
			}while (ev == null);
			
			ev.setBounds(r);
			ev.adjustWidth();
			setAssociations();
			updateHierarchicalLines(referendeToUpdate);
			layout.fixRelationLines();
			PanelClassDiagram.getInstance().getCurrentGraphicView().repaint();
		}
		for (GraphicView view : PanelClassDiagram.getInstance().getAllGraphicView())
		{
			view.repaint();
		}
	}
	
	private void doTranslation(LinkedList<CompilationUnit> units)
	{
		System.out.println("DO TRANSLATION for " + units.size() + " file(s)");
		project.setName(classDiagram.getName());
			
		for (CompilationUnit cu : units)
		{
			for (Element e : cu.getElements())
			{
				classDiagram.components.Entity ce = null;
				
				if(e.getClass() == ClassType.class)
				{
					addClass((ClassType)e, cu.getFile());
				}
				
				else if(e.getClass() == InterfaceType.class)
				{
					InterfaceType it = new InterfaceType((InterfaceType)e);
					ce = new InterfaceEntity(it.getName(), translateAccessModifiers(it.getAccess()),it.getID());
					ce.setReferenceFile(cu.getFile());
					classDiagram.addInterface((InterfaceEntity) ce);
					association.add(ce);
					updated = ce;
					
					// add member and operations
					for (Element inner : it.getElements())
					{	
						if(inner.getClass() == dataRecord.elements.Method.class)
						{
							addMethod((dataRecord.elements.Method)inner, ce);
						}
						
						else if(inner.getClass() == dataRecord.elements.Attribute.class)
						{
							addAttribute((dataRecord.elements.Attribute)inner, ce);
						}
					}
					ce.notifyObservers();
				}
				
				//TODO add enum
				
				// dont do a generic ce.notifyObserver() because package and import statement will raise a null pointer exception
			}
			
		}
		
		addHierarchicalLines(units);
	}
	
	private void addHierarchicalLines(LinkedList<CompilationUnit> units)
	{
		// add Inheritance 
		for (CompilationUnit cu : units)
		{
			for (Element e : cu.getElements())
			{
				if(e.getClass() == ClassType.class)
					if(!((ClassType)e).getExtendList().isEmpty())
					{
						Entity child = (Entity)classDiagram.searchComponentById(e.getID());
						for(int z=0; z < ((ClassType)e).getExtendList().size(); z++)
						{	
							ClassType dataChild = (ClassType)e;
							if(dataChild.getExtendList().get(z).getClass() == ClassType.class)
							{
								ClassType parent = (ClassType) ((ClassType)e).getExtendList().get(z);
								Entity parent1 = (Entity)classDiagram.searchComponentById(parent.getID());
								classDiagram.addInheritance(new Inheritance(child, parent1));
							}
						}
					}
			}
		}
		
		// add interfaces
		for (CompilationUnit cu : units)
		{
			for (Element e : cu.getElements())
			{
				if(e.getClass() == ClassType.class)
					if(!((ClassType)e).getImplList().isEmpty())
					{
						Entity child = (Entity)classDiagram.searchComponentById(e.getID());
						for(int z=0; z < ((ClassType)e).getImplList().size(); z++)
						{	
							if(((ClassType) e).getImplList().get(0).getClass() == InterfaceType.class)
							{
								InterfaceType parent = (InterfaceType) ((ClassType)e).getImplList().get(z);
								Entity parent1 = (Entity)classDiagram.searchComponentById(parent.getID());
								classDiagram.addDependency(new Dependency(child, parent1));
							}
						}
					}
			}
		}
	}
	
	private void addClass(ClassType clazz, File file)
	{
		classDiagram.components.Entity ce = null;
		ClassType ct = new ClassType((ClassType)clazz);
		ce = new ClassEntity(ct.getName(), translateAccessModifiers(ct.getAccess()),ct.getID());
		ce.setAbstract(ct.isAbstract());
		ce.setReferenceFile(file);
		ce.setGeneric(ct.getGeneric());
		classDiagram.addClass((ClassEntity) ce);
		association.add(ce);
		updated = ce;
		
		// add member and operations
		for (Element inner : ct.getElements())
		{
			if(inner.getClass() == Constructor.class)
			{
				addConstructor((Constructor)inner, ce);
			}
			
			else if(inner.getClass() == dataRecord.elements.Method.class)
			{
				addMethod((dataRecord.elements.Method)inner, ce);
			}
			
			else if(inner.getClass() == dataRecord.elements.Attribute.class)
			{
				addAttribute((dataRecord.elements.Attribute)inner, ce);
			}
			else if(inner.getClass() == ClassType.class) // classe interne
			{
				addClass((ClassType)inner, file);
				Entity parent = (Entity) classDiagram.searchComponentById(inner.getID());
				classDiagram.addInnerClass(new InnerClass(parent, ce));
			}
		}
		ce.notifyObservers();
	}
	
	
	private void addAttribute(dataRecord.elements.Attribute inner, Entity ce)
	{
		
		{
		Attribute a = new Attribute(inner.getName(), new Type(inner.getType().getElementType()));
		if (inner.getType().getClass() == ListType.class)
		{
			ListType lt = (ListType)inner.getType();
			a.getType().setCollection(lt.getCollection());
			//System.err.println(a.getName() + a.getType().getCollection());
		}
		ce.addAttribute(a);
		ce.notifyObservers(UpdateMessage.ADD_ATTRIBUTE_NO_EDIT);
		a.setConstant(inner.isFinal());
		//a.setDefaultValue(inner.getValue()); // error if value is a String in the xml file
		a.setStatic(inner.isStatic());
		a.setVisibility(translateAccessModifiers(inner.getAccess()));

		a.notifyObservers();
		}
	}

	private void addMethod(dataRecord.elements.Method data, Entity ce)
	{
		Method m = new Method(data.getName(), new Type(data.getReturnType()), translateAccessModifiers(data.getAccess()), ce);
		ce.addMethod(m);
		ce.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);
		m.setStatic(data.isStatic());
		m.setAbstract(data.isAbstract());
		
		// add params
		for (final Parameter p : data.getParams())
		{
			final classDiagram.components.Variable va = new classDiagram.components.Variable(p.getName(), new Type(p.getType().getElementType()));

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
		
		// add params
		for (final Parameter p : c.getParams())
		{
			final classDiagram.components.Variable va = new classDiagram.components.Variable(p.getName(), new Type(p.getType().getElementType()));

			m.addParameter(va);
		}
	}
	
	private void setAssociations()
	{	
		for (IDiagramComponent component : classDiagram.getComponents())
		{
			if (component.getClass() == ClassEntity.class)
			{
				ClassEntity source = (ClassEntity)component;
				for (Attribute ax : source.getAttributes())
				{
					//System.out.println("nom: " + ax.getName() + " nom type: "+ ax.getType() + " " + ax.getType().getCollection());
					for (Entity target : association)
					{
						if(target.getName().equals(ax.getType().getName()))
						{
							Binary b2 = new Binary(source , target, true);
							if(ax.getType().getCollection() != null)
							{
								String[] s = ax.getName().split("=");
								b2.getRoles().getLast().setName("{"+ ax.getType().getCollection() +"} "+ s[0]);
								b2.getRoles().getLast().setMultiplicity(Multiplicity.ZERO_OR_MORE);
							}
							classDiagram.addBinary(b2);
							
							//remove the attribute from the box, leave only the link
							source.removeAttribute(ax);
							source.notifyObservers();
						}
					}
				}
			}
		}
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
	
	private void updateHierarchicalLines(LinkedList<Entity> from)
	{
		// add Inheritance 
		for (Entity cu : from)
		{
			if(updated.getClass() == ClassEntity.class)
			{
				classDiagram.addInheritance(new Inheritance(cu, updated));
			}
			else
			{
				classDiagram.addDependency(new Dependency(cu, updated));
			}
		}
	}
	
	private String getExtension(File file) throws FileNotFoundException
	{
		if (file.isDirectory())
		{
			File[] dir = file.listFiles();
			for (File f : dir)
			{
				if (f.isDirectory())
				{
					getExtension(f);
				} 
				else 
				{
					return Utility.getExtension(f);
				}
			}
		}
		else
			return Utility.getExtension(file);

		// empty directory
		return "";
	}
}
