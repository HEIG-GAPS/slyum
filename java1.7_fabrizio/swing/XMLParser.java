package swing;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.relations.LineCommentary;
import graphic.relations.LineView;
import graphic.relations.MultiLineView;
import graphic.relations.RelationGrip;
import graphic.textbox.TextBox;
import graphic.textbox.TextBoxCommentary;
import graphic.textbox.TextBoxLabel;
import graphic.textbox.TextBoxRole;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import utility.SDialogProjectLoading;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.AssociationClass;
import classDiagram.components.Attribute;
import classDiagram.components.ClassEntity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;
import classDiagram.components.Type;
import classDiagram.components.Visibility;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Composition;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Multiplicity;

/**
 * This class read the XML file and create the diagram UML structured from this.
 * 
 * @author David Miserez
 * @verson 1.0 - 25.07.2011
 */
public class XMLParser extends DefaultHandler
{
	public enum Aggregation
	{
		AGGREGATE, COMPOSE, MULTI, NONE
	};

	private class Association
	{
		Aggregation aggregation = Aggregation.NONE;
		boolean direction = false;
		int id = -1;
		String name = null;
		LinkedList<Role> role = new LinkedList<Role>();
	};

	private class ClassDiagram
	{
		DiagramElements diagrameElement = null;
		@SuppressWarnings("unused")
		String name = null;
		LinkedList<UMLView> uMLView = new LinkedList<UMLView>();
	}

	private class ComponentView
	{
		int color = 0;
		int componentId = -1;
		Rectangle geometry = new Rectangle();
	}

	private class Dependency
	{
		int id = -1;
		String label = null;
		int source = -1;
		int target = -1;
	}

	private class DiagramElements
	{
		LinkedList<Association> association = new LinkedList<Association>();
		LinkedList<Dependency> dependency = new LinkedList<Dependency>();
		LinkedList<Entity> entity = new LinkedList<Entity>();
		LinkedList<Inheritance> inheritance = new LinkedList<Inheritance>();
		LinkedList<InnerClass> innerClass = new LinkedList<InnerClass>();
	}

	private class Entity
	{
		int associationClassID = -1;
		LinkedList<Variable> attribute = new LinkedList<Variable>();
		EntityType entityType = null;
		int id = -1;
		boolean isAbstract = false;
		LinkedList<Operation> method = new LinkedList<Operation>();
		String name = null;
		Visibility visibility = Visibility.PUBLIC;
		File file = null;
	}

	public enum EntityType
	{
		ASSOCIATION_CLASS, CLASS, INTERFACE
	}

	private class Inheritance
	{
		int child = -1;
		int id = -1;
		boolean innerClass = false;
		int parent = -1;
	}

	@SuppressWarnings("unused")
	private class InnerClass
	{
		int boundingClass = -1;
		int id = -1;
		int innerClass = -1;
	}

	private class MultiView
	{
		int color = 0;
		LinkedList<RelationView> multiLineView = new LinkedList<RelationView>();
		Rectangle multiViewBounds = new Rectangle();
		int relationId = -1;
	}

	private class Note
	{
		Rectangle bounds = new Rectangle();
		int color = 0;
		String content;
		LinkedList<RelationView> line = new LinkedList<RelationView>();
	}

	private class Operation
	{
		boolean isAbstract = false;
		boolean isStatic = false;
		String name = null;
		Type returnType = null;
		LinkedList<Variable> variable = new LinkedList<Variable>();
		Visibility visibility = Visibility.PUBLIC;
	}

	private class RelationView
	{
		int color = 0;
		Rectangle labelAssociation = new Rectangle();
		LinkedList<Point> line = new LinkedList<Point>();
		LinkedList<Rectangle> multipliciteAssociations = new LinkedList<Rectangle>();
		int relationId = -1;
		LinkedList<Rectangle> roleAssociations = new LinkedList<Rectangle>();
	}

	private class Role
	{
		int componentId = -1;
		Multiplicity multiplicity = null;
		String name = null;
		Visibility visibility = Visibility.PUBLIC;
	}

	private class UMLView
	{
		@SuppressWarnings("unused")
		LinkedList<ComponentView> componentView = new LinkedList<ComponentView>();
		
		//int grid = 0;
		
		@SuppressWarnings("unused")
		String name = null;

		LinkedList<Note> notes = new LinkedList<Note>();
		
		@SuppressWarnings("unused")
		LinkedList<RelationView> relationView = new LinkedList<RelationView>();
	}

	// UML STRUCTURE
	private class Variable
	{
		@SuppressWarnings("unused")
		int collection = 0;
		boolean constant = false;
		String defaultValue = null;
		boolean isStatic = false;
		String name = null;
		Type type = null;
		Visibility visibility = null;
	}

	LinkedList<AssociationClass> associationClassEntities = new LinkedList<AssociationClass>();
	LinkedList<Association> associations = new LinkedList<Association>();
	private StringBuffer buffer;

	private final classDiagram.ClassDiagram classDiagram;

	LinkedList<ClassEntity> classEntities = new LinkedList<ClassEntity>();
	// LinkedList<InnerCLass> innerCLass = new LinkedList<InnerCLass>();
	private final HashMap<Integer, ComponentView> componentView = new HashMap<Integer, ComponentView>();
	Association currentAssociation;
	ComponentView currentComponentView;
	Dependency currentDependency;
	Entity currentEntity;
	Rectangle currentGeometry;
	Inheritance currentInheritance;
	LinkedList<Point> currentLine;

	Operation currentMethod;
	int currentMin, currentMax;
	// InnerClass currentInnerClass;

	Multiplicity currentMultiplicity;

	MultiView currentMultiView;

	Note currentNote;

	Point currentPoint;

	RelationView currentRelationView;

	Role currentRole;
	
	File currentFile;

	LinkedList<Dependency> dependency = new LinkedList<Dependency>();

	private final GraphicView graphicView;

	LinkedList<Inheritance> inheritance = new LinkedList<Inheritance>();

	private boolean inMultiViewBounds;

	boolean inRelationView = false, inComponentView = false,
			inNoteGeometry = false, inNoteRelation = false,
			inLabelAssociation = false;

	LinkedList<InterfaceEntity> interfaceEntities = new LinkedList<InterfaceEntity>();

	private final HashMap<Integer, MultiView> multiView = new HashMap<Integer, MultiView>();
	private final HashMap<Integer, RelationView> relationView = new HashMap<Integer, RelationView>();
	private ClassDiagram uMLClassDiagram;
	private SDialogProjectLoading dpl;

	public XMLParser(classDiagram.ClassDiagram classDiagram, GraphicView graphicView, SDialogProjectLoading dpl)
	{
		super();

		if (classDiagram == null)
			throw new IllegalArgumentException("classDiagram is null");

		if (graphicView == null)
			throw new IllegalArgumentException("graphicView is null");

		this.classDiagram = classDiagram;
		this.graphicView = graphicView;
		this.dpl = dpl;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		final String reader = new String(ch, start, length);

		if (buffer != null)

			buffer.append(reader);
	}

	private void createEntity(Entity e)
	{
		dpl.addStep("Create entity " + e.name + "...");
		
		classDiagram.components.Entity ce = null;

		switch (e.entityType)
		{
			case CLASS:

				ce = new ClassEntity(e.name, e.visibility, e.id);
				classDiagram.addClass((ClassEntity) ce);
				ce.setAbstract(e.isAbstract);
				ce.setReferenceFile(e.file);

				break;

			case INTERFACE:

				ce = new InterfaceEntity(e.name, e.visibility, e.id);
				classDiagram.addInterface((InterfaceEntity) ce);
				ce.setAbstract(true);

				break;

			case ASSOCIATION_CLASS:

				final Binary b = (Binary) classDiagram.searchComponentById(e.associationClassID);
				ce = new AssociationClass(e.name, e.visibility, b, e.id);
				classDiagram.addAssociationClass((AssociationClass) ce);

				break;
		}

		for (final Variable v : e.attribute)
		{
			dpl.addStep("Create attribute " + v.name + "...");
			final Attribute a = new Attribute(v.name, v.type);
			ce.addAttribute(a);
			ce.notifyObservers(UpdateMessage.ADD_ATTRIBUTE_NO_EDIT);
			a.setConstant(v.constant);
			a.setDefaultValue(v.defaultValue);
			a.setStatic(v.isStatic);
			a.setVisibility(v.visibility);

			a.notifyObservers();
		}

		for (final Operation o : e.method)
		{
			dpl.addStep("Create method " + o.name + "...");
			final Method m = new Method(o.name, o.returnType, o.visibility, ce);
			ce.addMethod(m);
			ce.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);
			m.setStatic(o.isStatic);
			m.setAbstract(o.isAbstract);

			for (final Variable v : o.variable)
			{
				final classDiagram.components.Variable va = new classDiagram.components.Variable(v.name, v.type);

				m.addParameter(va);
			}

			m.notifyObservers();
		}

		ce.notifyObservers();
	}

	@Override
	public void endDocument() throws SAXException
	{
		classDiagram.removeAll();
		graphicView.removeAll();
		
		int count = uMLClassDiagram.diagrameElement.association.size();
		count += uMLClassDiagram.diagrameElement.dependency.size();
		count += uMLClassDiagram.diagrameElement.entity.size();

		for (Entity e : uMLClassDiagram.diagrameElement.entity)
		{
			count += e.attribute.size();
			count += e.method.size();
		}
		
		count += uMLClassDiagram.diagrameElement.inheritance.size();
		count += uMLClassDiagram.diagrameElement.innerClass.size();
		count += uMLClassDiagram.uMLView.getFirst().notes.size();
		
		dpl.setProgressBarMaximum(count);
	}
	
	public void createDiagram()
	{
		// Don't change order !!
		importClassesAndInterfaces(); // <- need nothing :D
	
		importAssociations(); // <- need importation classes
		importAssociationClass(); // <- need importation classes and
		// associations
		importAssociations(); // Import associations that cannot be imported first time
		// first time
		importInheritances(); // <- ...
		importDepedency();
		
		graphicView.setPaintBackgroundLast(true);
		graphicView.goRepaint();
		
		// components locations
		locateComponentBounds();
		importNotes();
		
		dpl.addStep("Importation complete");
		dpl.setPhase("Finish");
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equals("entity"))
			currentEntity = null;
		else if (qName.equals("method"))
			currentMethod = null;
		else if (qName.equals("file"))
			currentFile = null;
		else if (qName.equals("associationClassID"))
			currentEntity.associationClassID = Integer.parseInt(buffer.toString());
		else if (qName.equals("association"))
			currentAssociation = null;
		else if (qName.equals("role"))
			currentRole = null;
		else if (qName.equals("multiplicity"))
			currentRole.multiplicity = new Multiplicity(currentMin, currentMax);
		else if (qName.equals("min"))
		{
			currentMin = Integer.parseInt(buffer.toString());
			buffer = null;
		}
		else if (qName.equals("max"))
		{
			currentMax = Integer.parseInt(buffer.toString());
			buffer = null;
		}
		else if (qName.equals("child"))
		{
			currentInheritance.child = Integer.parseInt(buffer.toString());
			buffer = null;
		}
		else if (qName.equals("parent"))
		{
			currentInheritance.parent = Integer.parseInt(buffer.toString());
			buffer = null;
		}
		else if (qName.equals("source"))
		{
			currentDependency.source = Integer.parseInt(buffer.toString());
			buffer = null;
		}
		else if (qName.equals("target"))
		{
			currentDependency.target = Integer.parseInt(buffer.toString());
			buffer = null;
		}
		else if (qName.equals("relationView"))
		{
			inRelationView = false;
			relationView.put(currentRelationView.relationId, currentRelationView);
		}
		else if (qName.equals("multiLineView"))
		{
			inRelationView = false;
			currentMultiView.multiLineView.add(currentRelationView);
		}
		else if (qName.equals("multiView"))
		{
			multiView.put(currentMultiView.relationId, currentMultiView);
		}
		else if (qName.equals("geometry"))
		{
			inNoteGeometry = false;
			currentComponentView.geometry = currentGeometry;
			currentGeometry = null;
		}
		else if (qName.equals("noteGeometry"))
		{
			inNoteGeometry = false;
			currentNote.bounds = currentGeometry;
			currentGeometry = null;
		}
		else if (qName.equals("componentView"))
		{
			inComponentView = false;
			componentView.put(currentComponentView.componentId, currentComponentView);
		}
		else if (qName.equals("note"))
		{
			uMLClassDiagram.uMLView.getFirst().notes.add(currentNote);
			currentNote = null;
		}
		else if (qName.equals("multiViewBounds"))
		{
			inMultiViewBounds = false;
			currentMultiView.multiViewBounds = currentGeometry;
			currentGeometry = null;
		}
		else if (qName.equals("line"))
		{
			currentRelationView.line = currentLine;
			currentLine = null;
		}
		else if (qName.equals("noteLine"))
		{
			inNoteRelation = false;
			currentRelationView.line = currentLine;
			currentNote.line.addLast(currentRelationView);
			currentLine = null;
			currentRelationView = null;
		}
		else if (qName.equals("point"))
		{
			currentLine.add(currentPoint);
			currentPoint = null;
		}
		else if (qName.equals("labelAssociation"))
		{
			currentRelationView.labelAssociation = currentGeometry;
			currentGeometry = null;
			inLabelAssociation = false;
		}
		else if (qName.equals("roleAssociation"))
		{
			currentRelationView.roleAssociations.add(currentGeometry);
			currentGeometry = null;
			inLabelAssociation = false;
		}
		else if (qName.equals("multipliciteAssociation"))
		{
			currentRelationView.multipliciteAssociations.add(currentGeometry);
			currentGeometry = null;
			inLabelAssociation = false;
		}
		else if (qName.equals("x"))
		{
			final int x = Integer.parseInt(buffer.toString());

			if (inComponentView || inNoteGeometry || inLabelAssociation || inMultiViewBounds)

				currentGeometry.x = x;

			else if (inRelationView || inNoteRelation)

				currentPoint.x = x;

			buffer = null;
		}
		else if (qName.equals("y"))
		{
			final int y = Integer.parseInt(buffer.toString());

			if (inComponentView || inNoteGeometry || inLabelAssociation || inMultiViewBounds)

				currentGeometry.y = y;

			else if (inRelationView || inNoteRelation)

				currentPoint.y = y;

			buffer = null;
		}
		else if (qName.equals("w"))
		{
			currentGeometry.width = Integer.parseInt(buffer.toString());

			buffer = null;
		}
		else if (qName.equals("h"))
		{
			currentGeometry.height = Integer.parseInt(buffer.toString());

			buffer = null;
		}
	}

	private void importAssociationClass()
	{
		dpl.setPhase("Import association classes...");
		for (final Entity e : uMLClassDiagram.diagrameElement.entity)

			if (e.entityType == EntityType.ASSOCIATION_CLASS)

				createEntity(e);
	}

	public void importAssociations()
	{
		dpl.setPhase("Import associations...");
		final LinkedList<Association> associationsNotAdded = new LinkedList<Association>();

		for (final Association a : uMLClassDiagram.diagrameElement.association)
		{
			classDiagram.relationships.Association ac = null;

			final classDiagram.components.Entity source = (classDiagram.components.Entity) classDiagram.searchComponentById(a.role.getFirst().componentId);
			final classDiagram.components.Entity target = (classDiagram.components.Entity) classDiagram.searchComponentById(a.role.getLast().componentId);

			dpl.addStep("Create association " +  source.getName() + " - " + target.getName()  +"...");
			
			if (source == null || target == null)
			{
				associationsNotAdded.add(a);
				continue;
			}

			switch (a.aggregation)
			{
				case NONE:
					ac = new Binary(source, target, a.direction, a.id);
					classDiagram.addBinary((Binary) ac);

					break;

				case AGGREGATE:
					ac = new classDiagram.relationships.Aggregation(source, target, a.direction, a.id);
					classDiagram.addAggregation((classDiagram.relationships.Aggregation) ac);

					break;

				case COMPOSE:
					ac = new Composition(source, target, a.direction, a.id);
					classDiagram.addComposition((Composition) ac);
					break;

				case MULTI:
					final LinkedList<classDiagram.components.ClassEntity> entities = new LinkedList<ClassEntity>();

					for (final Role role : a.role)

						entities.add((classDiagram.components.ClassEntity) classDiagram.searchComponentById(role.componentId));

					ac = new Multi(entities, a.id);
					classDiagram.addMulti((Multi) ac);
					break;
			}

			for (int i = 0; i < a.role.size(); i++)
			{
				ac.getRoles().get(i).setName(a.role.get(i).name);
				ac.getRoles().get(i).setVisibility(a.role.get(i).visibility);
				ac.getRoles().get(i).setMultiplicity(a.role.get(i).multiplicity);

				ac.getRoles().get(i).notifyObservers();
				ac.getRoles().get(i).getMultiplicity().notifyObservers();
			}

			ac.setName(a.name);

			ac.notifyObservers();
		}

		uMLClassDiagram.diagrameElement.association = associationsNotAdded;
	}

	private void importClassesAndInterfaces()
	{
		dpl.setPhase("Import classes and interfaces...");
		
		for (final Entity e : uMLClassDiagram.diagrameElement.entity)
		
			if (!(e.entityType == EntityType.ASSOCIATION_CLASS))

				createEntity(e);
		

	}

	public void importDepedency()
	{
		dpl.setPhase("Import dependency...");
		for (final Dependency d : uMLClassDiagram.diagrameElement.dependency)
		{
			final classDiagram.components.Entity source = (classDiagram.components.Entity) classDiagram.searchComponentById(d.source);
			final classDiagram.components.Entity target = (classDiagram.components.Entity) classDiagram.searchComponentById(d.target);

			dpl.addStep("Create dependency " +  source.getName() + " - " + target.getName()  +"...");
			final classDiagram.relationships.Dependency dr = new classDiagram.relationships.Dependency(source, target, d.id);
			classDiagram.addDependency(dr);

			dr.setLabel(d.label);
			dr.notifyObservers();
		}
	}

	// view

	public void importInheritances()
	{
		dpl.setPhase("Import inheritances...");
		for (final Inheritance h : uMLClassDiagram.diagrameElement.inheritance)
		{
			final classDiagram.components.Entity child = (classDiagram.components.Entity) classDiagram.searchComponentById(h.child);
			final classDiagram.components.Entity parent = (classDiagram.components.Entity) classDiagram.searchComponentById(h.parent);

			dpl.addStep("Create inheritance " +  child.getName() + " - " + parent.getName()  +"...");
			
			if (h.innerClass)
			{
				final classDiagram.relationships.InnerClass innerClass = new classDiagram.relationships.InnerClass(child, parent, h.id);
				classDiagram.addInnerClass(innerClass);
				innerClass.notifyObservers();
			}
			else
			{
				final classDiagram.relationships.Inheritance i = new classDiagram.relationships.Inheritance(child, parent, h.id);
				classDiagram.addInheritance(i);
				i.notifyObservers();
			}
		}
	}

	private void importNotes()
	{
		dpl.setPhase("Import notes...");
		
		for (final Note note : uMLClassDiagram.uMLView.getFirst().notes)
		{
			dpl.addStep("Import a note.");
			final TextBoxCommentary noteView = new TextBoxCommentary(graphicView, note.content);

			noteView.setBounds(note.bounds);

			for (final RelationView rv : note.line)
			{
				GraphicComponent component = graphicView.searchAssociedComponent(classDiagram.searchComponentById(rv.relationId));

				if (rv.relationId == -1)

					component = graphicView;

				if (LineCommentary.checkCreate(noteView, component, false))
				{
					final LineCommentary lc = new LineCommentary(graphicView, noteView, component, rv.line.getFirst(), rv.line.getLast(), false);

					for (int i = 1; i < rv.line.size() - 1; i++)
					{
						final RelationGrip rg = new RelationGrip(graphicView, lc);
						rg.setAnchor(rv.line.get(i));
						lc.addGrip(rg, i);
					}

					lc.getFirstPoint().setAnchor(rv.line.getFirst());
					lc.getLastPoint().setAnchor(rv.line.getLast());

					lc.setColor(rv.color);

					graphicView.addLineView(lc);
				}
			}

			noteView.setColor(note.color);
			graphicView.addNotes(noteView);
		}
	}

	public void locateComponentBounds()
	{
		dpl.setPhase("Locate components...");
		// Generals bounds
		for (final GraphicComponent g : graphicView.getAllComponents())
		{
			final IDiagramComponent component = g.getAssociedComponent();

			if (component != null)
			{
				final ComponentView cv = componentView.get(component.getId());

				if (cv != null)
				{
					g.setBounds(cv.geometry);
					g.setColor(cv.color);
				}
			}
		}

		// Associations
		for (final LineView l : graphicView.getLinesView())
		{
			final IDiagramComponent component = l.getAssociedComponent();

			if (component != null)
			{
				final RelationView rl = relationView.get(component.getId());

				final LinkedList<Point> points = rl.line;

				for (int i = 1; i < points.size() - 1; i++)
				{
					final RelationGrip rg = new RelationGrip(graphicView, l);
					rg.setAnchor(points.get(i));
					rg.notifyObservers();
					l.addGrip(rg, i);
				}

				RelationGrip first = l.getFirstPoint(),
						     last = l.getLastPoint();
				
				first.setAnchor(points.getFirst());
				last.setAnchor(points.getLast());
				
				first.notifyObservers();
				last.notifyObservers();

				l.setColor(rl.color);
				final LinkedList<TextBox> tb = l.getTextBoxRole();

				if (tb.size() >= 1)
				{
					((TextBoxLabel) tb.getFirst()).computeDeplacement(new Point(rl.labelAssociation.x, rl.labelAssociation.y));

					if (tb.size() >= 3)
					{
						((TextBoxLabel) tb.get(1)).computeDeplacement(new Point(rl.roleAssociations.get(0).x, rl.roleAssociations.get(0).y));
						((TextBoxLabel) tb.get(2)).computeDeplacement(new Point(rl.roleAssociations.get(1).x, rl.roleAssociations.get(1).y));

						((TextBoxRole) tb.get(1)).getTextBoxMultiplicity().computeDeplacement(new Point(rl.multipliciteAssociations.get(0).x, rl.multipliciteAssociations.get(0).y));
						((TextBoxRole) tb.get(2)).getTextBoxMultiplicity().computeDeplacement(new Point(rl.multipliciteAssociations.get(1).x, rl.multipliciteAssociations.get(1).y));
					}
				}
			}
		}

		// Multi-association
		for (final graphic.relations.MultiView mv : graphicView.getMultiView())
		{
			final IDiagramComponent component = mv.getAssociedComponent();

			if (component != null)
			{
				final MultiView xmlMV = multiView.get(component.getId());

				final LinkedList<MultiLineView> multiLinesView = mv.getMultiLinesView();

				for (int j = 0; j < multiLinesView.size(); j++)
				{
					final RelationView rl = xmlMV.multiLineView.get(j);
					final LinkedList<Point> points = rl.line;
					final MultiLineView mlv = multiLinesView.get(j);

					for (int i = 1; i < points.size() - 1; i++)
					{
						final RelationGrip rg = new RelationGrip(graphicView, mlv);
						rg.setAnchor(points.get(i));
						rg.notifyObservers();
						mlv.addGrip(rg, i);
					}

					RelationGrip first = mlv.getFirstPoint(),
								 last = mlv.getLastPoint();
				
				first.setAnchor(points.getFirst());
				last.setAnchor(points.getLast());
				
				first.notifyObservers();
				last.notifyObservers();

					// Role
					final LinkedList<TextBox> tb = mlv.getTextBoxRole();

					if (tb.size() == 1)
					{
						((TextBoxLabel) tb.getFirst()).computeDeplacement(new Point(rl.roleAssociations.get(0).x, rl.roleAssociations.get(0).y));
						((TextBoxRole) tb.getFirst()).getTextBoxMultiplicity().computeDeplacement(new Point(rl.multipliciteAssociations.get(0).x, rl.multipliciteAssociations.get(0).y));
					}
				}

				mv.setColor(xmlMV.color);
				mv.setBounds(xmlMV.multiViewBounds);
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (qName.equals("classDiagram"))
			try
			{
				uMLClassDiagram = new ClassDiagram();

				final String name = attributes.getValue("name");

				uMLClassDiagram.name = name;
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("diagramElements"))
			uMLClassDiagram.diagrameElement = new DiagramElements();
		else if (qName.equals("entity"))
			try
			{
				currentEntity = new Entity();
				currentEntity.id = Integer.parseInt(attributes.getValue("id"));
				currentEntity.name = attributes.getValue("name");
				currentEntity.entityType = EntityType.valueOf(attributes.getValue("entityType"));
				currentEntity.visibility = Visibility.valueOf(attributes.getValue("visibility"));
				currentEntity.isAbstract = Boolean.parseBoolean(attributes.getValue("isAbstract"));

				uMLClassDiagram.diagrameElement.entity.add(currentEntity);
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("file"))
			try
			{
				currentFile = new File(attributes.getValue("path"));

				currentEntity.file = currentFile;
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("method"))
			try
			{
				currentMethod = new Operation();
				currentMethod.name = attributes.getValue("name");
				currentMethod.returnType = new Type(attributes.getValue("returnType"));
				currentMethod.visibility = Visibility.valueOf(attributes.getValue("visibility"));
				currentMethod.isStatic = Boolean.parseBoolean(attributes.getValue("isStatic"));
				currentMethod.isAbstract = Boolean.parseBoolean(attributes.getValue("isAbstract"));

				currentEntity.method.add(currentMethod);
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("variable"))
			try
			{
				final Variable variable = new Variable();
				variable.name = attributes.getValue("name");
				variable.type = new Type(attributes.getValue("type"));
				variable.constant = Boolean.parseBoolean(attributes.getValue("const"));

				currentMethod.variable.add(variable);
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("attribute"))
			try
			{
				final Variable variable = new Variable();
				variable.name = attributes.getValue("name");
				variable.type = new Type(attributes.getValue("type"));
				variable.constant = Boolean.parseBoolean(attributes.getValue("const"));
				variable.visibility = Visibility.valueOf(attributes.getValue("visibility"));
				variable.defaultValue = attributes.getValue("defaultValue");
				// variable.collection =
				// Integer.parseInt(attributes.getValue("collection"));
				variable.isStatic = Boolean.parseBoolean(attributes.getValue("isStatic"));

				currentEntity.attribute.add(variable);
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("association"))
			try
			{
				currentAssociation = new Association();
				currentAssociation.id = Integer.parseInt(attributes.getValue("id"));
				currentAssociation.name = attributes.getValue("name");
				currentAssociation.direction = Boolean.parseBoolean(attributes.getValue("direction"));
				currentAssociation.aggregation = Aggregation.valueOf(attributes.getValue("aggregation"));

				uMLClassDiagram.diagrameElement.association.add(currentAssociation);
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("role"))
			try
			{
				currentRole = new Role();
				currentRole.name = attributes.getValue("name");
				currentRole.componentId = Integer.parseInt(attributes.getValue("componentId"));
				currentRole.visibility = Visibility.valueOf(attributes.getValue("visibility"));

				currentAssociation.role.add(currentRole);
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("multiplicity"))
		{
			// Nothing to do
		}
		else if (qName.equals("inheritance"))
			try
			{
				currentInheritance = new Inheritance();
				currentInheritance.id = Integer.parseInt(attributes.getValue("id"));
				currentInheritance.innerClass = Boolean.parseBoolean(attributes.getValue("innerClass"));

				buffer = new StringBuffer();

				uMLClassDiagram.diagrameElement.inheritance.add(currentInheritance);
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("dependency"))
			try
			{
				currentDependency = new Dependency();
				currentDependency.id = Integer.parseInt(attributes.getValue("id"));
				currentDependency.label = attributes.getValue("label");

				buffer = new StringBuffer();

				uMLClassDiagram.diagrameElement.dependency.add(currentDependency);
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("umlView"))
			try
			{
				uMLClassDiagram.uMLView.add(new UMLView());
				uMLClassDiagram.uMLView.getLast().name = attributes.getValue("name");
				//uMLClassDiagram.uMLView.getLast().grid = Integer.parseInt(attributes.getValue("grid"));
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("componentView"))
			try
			{
				inComponentView = true;
				currentComponentView = new ComponentView();
				currentComponentView.componentId = Integer.parseInt(attributes.getValue("componentID"));
				currentComponentView.color = Integer.parseInt(attributes.getValue("color"));
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("geometry") || qName.equals("noteGeometry"))
		{
			inNoteGeometry = true;
			currentGeometry = new Rectangle();
		}
		else if (qName.equals("relationView") || qName.equals("multiLineView"))
			try
			{
				inRelationView = true;
				currentRelationView = new RelationView();
				currentRelationView.relationId = Integer.parseInt(attributes.getValue("relationId"));
				currentRelationView.color = Integer.parseInt(attributes.getValue("color"));
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("multiView"))
			try
			{
				currentMultiView = new MultiView();
				currentMultiView.relationId = Integer.parseInt(attributes.getValue("relationId"));
				currentMultiView.color = Integer.parseInt(attributes.getValue("color"));
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("multiViewBounds"))
		{
			inMultiViewBounds = true;
			currentGeometry = new Rectangle();
		}
		else if (qName.equals("line"))
			currentLine = new LinkedList<Point>();
		else if (qName.equals("noteLine"))
		{
			inNoteRelation = true;
			currentLine = new LinkedList<Point>();
			currentRelationView = new RelationView();
			currentRelationView.relationId = Integer.parseInt(attributes.getValue("relationId"));
			currentRelationView.color = Integer.parseInt(attributes.getValue("color"));
		}
		else if (qName.equals("point"))
			currentPoint = new Point();
		else if (qName.equals("labelAssociation") || qName.equals("roleAssociation") || qName.equals("multipliciteAssociation"))
		{
			currentGeometry = new Rectangle();
			inLabelAssociation = true;
		}
		else if (qName.equals("note"))
			try
			{
				currentNote = new Note();
				currentNote.content = attributes.getValue("content");
				currentNote.color = Integer.parseInt(attributes.getValue("color"));
			} catch (final Exception e)
			{
				throw new SAXException(e);
			}
		else if (qName.equals("min") || qName.equals("max") || qName.equals("associationClassID") || qName.equals("child") || qName.equals("parent") || qName.equals("source") || qName.equals("target") || qName.equals("x") || qName.equals("y") || qName.equals("w") || qName.equals("h"))
			buffer = new StringBuffer();
	}
}
