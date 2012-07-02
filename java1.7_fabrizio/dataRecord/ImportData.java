package dataRecord;

import graphic.entity.EntityView;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
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
import swing.PanelClassDiagram;

public class ImportData extends Thread
{
	private final classDiagram.ClassDiagram classDiagram = PanelClassDiagram.getInstance().getClassDiagram();
	private final ProjectManager project = ProjectManager.getInstance();
	private File[] files;
	private LinkedList<Entity> association = new LinkedList<Entity>();
	private boolean doLayout = false;
	private Rectangle r;
	
	public ImportData(File[] files)
	{
		this.files = files;
	}
	
	public ImportData(File[] files, boolean dolayout, Rectangle r)
	{
		this(files);
		this.doLayout = dolayout;
		this.r = r;
	}
	
	public void run()
	{
		if(doLayout)
		{
			//remove the old CU
			int index = files[0].getName().lastIndexOf('.');
			project.removeCUnit(files[0].getName().substring(0, index));
		}
		ParserScanner ps = new ParserScanner();
		
		LinkedList<CompilationUnit> units = ps.parse(files);
		
		doTranslation(units);
		
		if(!doLayout)
		{
			setAssociations();
			
			PanelClassDiagram.getInstance().getCurrentGraphicView().adjustWidthAllEntities();
			
			Layout layout = new Layout();
			layout.layout();
		}
		else
		{
			PanelClassDiagram.getInstance().getCurrentGraphicView().repaint();
			EntityView ev = null;
			do
			{
				ev = PanelClassDiagram.getInstance().getCurrentGraphicView().getEntityAtPosition(new Point(0,0));
			}while (ev == null);
			
			ev.setBounds(r);
			ev.adjustWidth();
			//addHierarchicalLines(units);
			//setAssociations();
			PanelClassDiagram.getInstance().getCurrentGraphicView().repaint();	
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
		
		addHierarchicalLines(units);
	}
	
	private void addHierarchicalLines(LinkedList<CompilationUnit> units)
	{
		// add Inheritance 
		//TODO api
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
		//TODO api
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
		classDiagram.addClass((ClassEntity) ce);
		association.add(ce);
		
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
				addClass((ClassType)inner, file);
				Entity parent = (Entity) classDiagram.searchComponentById(inner.getID());
				classDiagram.addInnerClass(new InnerClass(parent, ce));
			}
		}
		ce.notifyObservers();
	}
	
	
	private void addAttribute(dataRecord.Attribute inner, Entity ce)
	{
		
		{
		Attribute a = new Attribute(inner.name, new Type(inner.type.getElementType()));
		if (inner.type.getClass() == ListType.class)
		{
			ListType lt = (ListType)inner.type;
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
		
		// add params
		for (final Parametre p : c.getParams())
		{
			final classDiagram.components.Variable va = new classDiagram.components.Variable(p.name, new Type(p.type.getElementType()));

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
							if(ax.getType().getCollection()!= null)
							{
								b2.getRoles().getLast().setName("{"+ ax.getType().getCollection() +"} "+ ax.getName());
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
}
