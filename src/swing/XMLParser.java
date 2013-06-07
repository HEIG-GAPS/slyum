package swing;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.relations.LineCommentary;
import graphic.relations.LineView;
import graphic.relations.MultiLineView;
import graphic.relations.RelationGrip;
import graphic.textbox.TextBox;
import graphic.textbox.TextBoxCommentary;
import graphic.textbox.TextBoxLabel;
import graphic.textbox.TextBoxMethod;
import graphic.textbox.TextBoxMethod.ParametersViewStyle;
import graphic.textbox.TextBoxRole;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import utility.SMessageDialog;
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
import classDiagram.verifyName.MethodName;
import classDiagram.verifyName.SyntaxeNameException;
import classDiagram.verifyName.TypeName;
import classDiagram.verifyName.VariableName;

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
		LinkedList<Role> role = new LinkedList<>();
	};

	private class ClassDiagram
	{
		DiagramElements diagrameElement = null;
		@SuppressWarnings("unused")
		String name = null;
		LinkedList<UMLView> uMLView = new LinkedList<>();
	}

	private class ComponentView
	{
		int color = 0;
		int componentId = -1;
		boolean displayAttributes = true,
		        displayMethods = true;
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
		LinkedList<Association> association = new LinkedList<>();
		LinkedList<Dependency> dependency = new LinkedList<>();
		LinkedList<Entity> entity = new LinkedList<>();
		LinkedList<Inheritance> inheritance = new LinkedList<>();
		@SuppressWarnings("unused")
    LinkedList<InnerClass> innerClass = new LinkedList<>();
	}

	private class Entity
	{
		int associationClassID = -1;
		LinkedList<Variable> attribute = new LinkedList<>();
		EntityType entityType = null;
		int id = -1;
		boolean isAbstract = false;
		LinkedList<Operation> method = new LinkedList<>();
		String name = null;
		Visibility visibility = Visibility.PUBLIC;
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
		LinkedList<RelationView> multiLineView = new LinkedList<>();
		Rectangle multiViewBounds = new Rectangle();
		int relationId = -1;
	}

	private class Note
	{
		Rectangle bounds = new Rectangle();
		int color = 0;
		String content;
		LinkedList<RelationView> line = new LinkedList<>();
	}

	private class Operation
	{
		boolean isAbstract = false;
		boolean isStatic = false;
		ParametersViewStyle view = ParametersViewStyle.TYPE_AND_NAME;
		String name = null;
		Type returnType = null;
		LinkedList<Variable> variable = new LinkedList<>();
		Visibility visibility = Visibility.PUBLIC;
	}

	private class RelationView
	{
		int color = 0;
		Rectangle labelAssociation = new Rectangle();
		LinkedList<Point> line = new LinkedList<>();
		LinkedList<Rectangle> multipliciteAssociations = new LinkedList<>();
		int relationId = -1;
		LinkedList<Rectangle> roleAssociations = new LinkedList<>();
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
		LinkedList<ComponentView> componentView = new LinkedList<>();
		
		@SuppressWarnings("unused")
		String name = null;

		LinkedList<Note> notes = new LinkedList<>();
		
		@SuppressWarnings("unused")
		LinkedList<RelationView> relationView = new LinkedList<>();
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

	LinkedList<AssociationClass> associationClassEntities = new LinkedList<>();
	LinkedList<Association> associations = new LinkedList<>();
	private StringBuffer buffer;

	private final classDiagram.ClassDiagram classDiagram;

	LinkedList<ClassEntity> classEntities = new LinkedList<>();
	// LinkedList<InnerCLass> innerCLass = new LinkedList<InnerCLass>();
	private final HashMap<Integer, ComponentView> componentView = new HashMap<>();
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

	LinkedList<Dependency> dependency = new LinkedList<>();

	private final GraphicView graphicView;

	LinkedList<Inheritance> inheritance = new LinkedList<>();

	private boolean inMultiViewBounds;

	boolean inRelationView = false, inComponentView = false,
			inNoteGeometry = false, inNoteRelation = false,
			inLabelAssociation = false;

	LinkedList<InterfaceEntity> interfaceEntities = new LinkedList<>();

	private final HashMap<Integer, MultiView> multiView = new HashMap<>();
	private final HashMap<Integer, RelationView> relationView = new HashMap<>();
	private ClassDiagram uMLClassDiagram;

	public XMLParser(classDiagram.ClassDiagram classDiagram, GraphicView graphicView)
	{
		super();

		if (classDiagram == null)
			throw new IllegalArgumentException("classDiagram is null");

		if (graphicView == null)
			throw new IllegalArgumentException("graphicView is null");

		this.classDiagram = classDiagram;
		this.graphicView = graphicView;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		final String reader = new String(ch, start, length);

		if (buffer != null)

			buffer.append(reader);
	}

	private void createEntity(Entity e) throws SyntaxeNameException
	{		
		classDiagram.components.Entity ce = null;
		EntityView newEntity;
		e.name = TypeName.verifyAndAskNewName(e.name);

		switch (e.entityType)
		{
			case CLASS:

				ce = new ClassEntity(e.name, e.visibility, e.id);
				
				classDiagram.addClassEntity((ClassEntity) ce);
				ce.setAbstract(e.isAbstract);

				break;

			case INTERFACE:

				ce = new InterfaceEntity(e.name, e.visibility, e.id);
				classDiagram.addInterfaceEntity((InterfaceEntity) ce);
				ce.setAbstract(true);

				break;

			case ASSOCIATION_CLASS:

				final Binary b = (Binary) classDiagram.searchComponentById(e.associationClassID);
				if (b == null) // cr�ation d'une classe normale.
				{
          ce = new ClassEntity(e.name, e.visibility, e.id);
          classDiagram.addClassEntity((ClassEntity) ce);
          ce.setAbstract(e.isAbstract);
          SMessageDialog.showInformationMessage("Association class " + ce.getName() + " has been converted into a normal class.\nIts association no longer exists during importation.");
          break;
				}
				
				try
				{
				    ce = new AssociationClass(e.name, e.visibility, b, e.id);
	                classDiagram.addAssociationClass((AssociationClass) ce);
				}catch (IllegalArgumentException a)
				{
				    SMessageDialog.showErrorMessage(a.getMessage());
				}

				break;
		}
		
		newEntity = (EntityView)PanelClassDiagram
		    .getInstance().getCurrentGraphicView().searchAssociedComponent(ce);

		for (final Variable v : e.attribute) {			
      final Attribute a = new Attribute(VariableName.verifyAndAskNewName(v.name), v.type);
         
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
			final Method m = new Method(
			        MethodName.verifyAndAskNewName(o.name),
			        o.returnType,
			        o.visibility,
			        ce);
			
			ce.addMethod(m);
			ce.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);
			
			// Set le type de vue
			((TextBoxMethod)newEntity.searchAssociedTextBox(m))
			    .setParametersViewStyle(o.view);
			
			m.setStatic(o.isStatic);
			m.setAbstract(o.isAbstract);

			for (final Variable v : o.variable)
			{
        final classDiagram.components.Variable va = new classDiagram.components.Variable(
                VariableName.verifyAndAskNewName(v.name), 
                v.type);
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
	}
	
	public void createDiagram() throws SyntaxeNameException
	{
		// Don't change the order !!
		importClassesAndInterfaces(); // <- need nothing :D
	
		importAssociations(); // <- need importation classes
		importAssociationClass(); // <- need importation classes and associations
		importAssociations(); // Import associations that cannot be imported first time
		importInheritances(); // <- ...
		importDepedency();
    
    graphicView.setPaintBackgroundLast(true);
    graphicView.goRepaint();
		
		locateComponentBounds();
		importNotes();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equals("entity"))
			currentEntity = null;
		else if (qName.equals("method"))
			currentMethod = null;
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

	private void importAssociationClass() throws SyntaxeNameException
	{
		for (final Entity e : uMLClassDiagram.diagrameElement.entity)

			if (e.entityType == EntityType.ASSOCIATION_CLASS)

				createEntity(e);
	}

	public void importAssociations()
	{
		final LinkedList<Association> associationsNotAdded = new LinkedList<>();

		for (final Association a : uMLClassDiagram.diagrameElement.association)
		{
			classDiagram.relationships.Association ac = null;

			final classDiagram.components.Entity source = (classDiagram.components.Entity) classDiagram.searchComponentById(a.role.getFirst().componentId);
			final classDiagram.components.Entity target = (classDiagram.components.Entity) classDiagram.searchComponentById(a.role.getLast().componentId);

			
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
					final LinkedList<classDiagram.components.ClassEntity> entities = new LinkedList<>();

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

	private void importClassesAndInterfaces() throws SyntaxeNameException
	{
		for (final Entity e : uMLClassDiagram.diagrameElement.entity)
		
			if (!(e.entityType == EntityType.ASSOCIATION_CLASS))
				createEntity(e);

	}

	public void importDepedency()
	{
		for (final Dependency d : uMLClassDiagram.diagrameElement.dependency)
		{
			classDiagram.components.Entity source = (classDiagram.components.Entity) classDiagram.searchComponentById(d.source);
			classDiagram.components.Entity target = (classDiagram.components.Entity) classDiagram.searchComponentById(d.target);
			classDiagram.relationships.Dependency dr = new classDiagram.relationships.Dependency(source, target, d.id);
			classDiagram.addDependency(dr);

			dr.setLabel(d.label);
			dr.notifyObservers();
		}
	}

	// view

	public void importInheritances()
	{
		for (final Inheritance h : uMLClassDiagram.diagrameElement.inheritance)
		{
			final classDiagram.components.Entity child = (classDiagram.components.Entity) classDiagram.searchComponentById(h.child);
			final classDiagram.components.Entity parent = (classDiagram.components.Entity) classDiagram.searchComponentById(h.parent);
			
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
		for (final Note note : uMLClassDiagram.uMLView.getFirst().notes) {
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
		// Generals bounds
		for (GraphicComponent g : graphicView.getAllComponents()) {
			IDiagramComponent component = g.getAssociedComponent();

			if (component != null) {
				ComponentView cv = componentView.get(component.getId());

				if (cv != null) {
					g.setBounds(cv.geometry);
					g.setColor(cv.color);
					
					// Gestion des entit�s
					if (g instanceof EntityView) {
					  EntityView entityView = (EntityView)g;
					  entityView.setDisplayAttributes(cv.displayAttributes);
					  entityView.setDisplayMethods(cv.displayMethods);
					}
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
		else if (qName.equals("method"))
			try
			{
				currentMethod = new Operation();
				currentMethod.name = attributes.getValue("name");
				currentMethod.returnType = new Type(TypeName.verifyAndAskNewName(attributes.getValue("returnType")));
				currentMethod.visibility = Visibility.valueOf(attributes.getValue("visibility"));
				currentMethod.isStatic = Boolean.parseBoolean(attributes.getValue("isStatic"));
				currentMethod.isAbstract = Boolean.parseBoolean(attributes.getValue("isAbstract"));
				currentMethod.view = ParametersViewStyle.valueOf(attributes.getValue("view"));

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
				variable.type = new Type(TypeName.verifyAndAskNewName(attributes.getValue("type")));
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
				variable.type = new Type(TypeName.verifyAndAskNewName(attributes.getValue("type")));
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
				currentComponentView.displayAttributes = Boolean.parseBoolean(attributes.getValue("displayAttributes"));
				currentComponentView.displayMethods = Boolean.parseBoolean(attributes.getValue("displayMethods"));
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
			currentLine = new LinkedList<>();
		else if (qName.equals("noteLine"))
		{
			inNoteRelation = true;
			currentLine = new LinkedList<>();
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
