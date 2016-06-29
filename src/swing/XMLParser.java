package swing;

import classDiagram.ClassDiagram.ViewEntity;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.AssociationClass;
import classDiagram.components.Attribute;
import classDiagram.components.ClassEntity;
import classDiagram.components.ConstructorMethod;
import classDiagram.components.EnumEntity;
import classDiagram.components.EnumValue;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;
import classDiagram.components.Method.ParametersViewStyle;
import classDiagram.components.SimpleEntity;
import classDiagram.components.Type;
import classDiagram.components.Visibility;
import classDiagram.relationships.Association.NavigateDirection;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Composition;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Multiplicity;
import classDiagram.verifyName.MethodName;
import classDiagram.verifyName.SyntaxeNameException;
import classDiagram.verifyName.TypeName;
import classDiagram.verifyName.VariableName;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.entity.EnumView;
import graphic.entity.EnumView.TypeEnumDisplay;
import graphic.entity.SimpleEntityView;
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
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.helpers.DefaultHandler;
import swing.propretiesView.DiagramPropreties;
import utility.SMessageDialog;

/**
 * This class read the XML file and create the diagram UML structured from this.
 * 
 * @author David Miserez
 * @verson 1.0 - 25.07.2011
 */
public class XMLParser extends DefaultHandler {
  public enum Aggregation {
    AGGREGATE, COMPOSE, MULTI, NONE
  };

  private class Association {
    int id = -1;
    LinkedList<Role> role = new LinkedList<>();
    Aggregation aggregation = Aggregation.NONE;
    NavigateDirection direction = NavigateDirection.BIDIRECTIONAL;
    String name = null;
  };

  private class ClassDiagram {
    LinkedList<UMLView> uMLView = new LinkedList<>();
    DiagramElements diagrameElement = null;
    
    String name = "";
    String informations = "";
    
    classDiagram.ClassDiagram.ViewEntity defaultViewEntities = 
        GraphicView.getDefaultViewEntities();
    
    ParametersViewStyle defaultViewMethods = 
        GraphicView.getDefaultViewMethods();
    
    boolean defaultViewEnum = GraphicView.getDefaultViewEnum();
    boolean defaultVisibleTypes = GraphicView.getDefaultVisibleTypes();
  }

  private class ComponentView {
    int color = 0;
    int componentId = -1;
    boolean displayAttributes = true, 
            displayMethods = true,
            displayDefault = true;
    TypeEnumDisplay typeEnumDisplay = TypeEnumDisplay.DEFAULT;
    Rectangle geometry = new Rectangle();
  }

  private class Dependency {
    int id = -1;
    int source = -1;
    int target = -1;
    String label = null;
  }

  private class DiagramElements {
    LinkedList<Association> association = new LinkedList<>();
    LinkedList<Dependency> dependency = new LinkedList<>();
    LinkedList<Entity> entity = new LinkedList<>();
    LinkedList<Inheritance> inheritance = new LinkedList<>();
  }

  private class Entity {
    int id = -1;
    int associationClassID = -1;
    boolean isAbstract = false;
    String name = null;
    Visibility visibility = Visibility.PUBLIC;
    EntityType entityType = null;
    LinkedList<Variable> attribute = new LinkedList<>();
    LinkedList<Operation> method = new LinkedList<>();
    LinkedList<EnumValue> enums = new LinkedList<>();
  }

  public enum EntityType {
    ASSOCIATION_CLASS, CLASS, INTERFACE, ENUM
  }

  private class Inheritance {
    int id = -1;
    int child = -1;
    int parent = -1;
    boolean innerClass = false;
  }

  private class MultiView {
    int color = 0;
    int relationId = -1;
    Rectangle multiViewBounds = new Rectangle();
    LinkedList<RelationView> multiLineView = new LinkedList<>();
  }

  private class Note {
    int color = 0;
    String content;
    Rectangle bounds = new Rectangle();
    LinkedList<RelationView> line = new LinkedList<>();
  }

  private class Operation {
    boolean isAbstract = false;
    boolean isStatic = false;
    String name = null;
    ParametersViewStyle view = ParametersViewStyle.TYPE_AND_NAME;
    String returnType = null;
    LinkedList<Variable> variable = new LinkedList<>();
    Visibility visibility = Visibility.PUBLIC;
    boolean isConstructor = false;
  }

  private class RelationView {
    int relationId = -1;
    int color = 0;
    Rectangle labelAssociation = new Rectangle();
    LinkedList<Point> line = new LinkedList<>();
    LinkedList<Rectangle> multipliciteAssociations = new LinkedList<>();
    LinkedList<Rectangle> roleAssociations = new LinkedList<>();
  }

  private class Role {
    int componentId = -1;
    String name = null;
    Multiplicity multiplicity = null;
    Visibility visibility = Visibility.PUBLIC;
  }

  private class UMLView {

    GraphicView graphicView;
    String name = null;
    boolean open = true;

    LinkedList<Note> notes = new LinkedList<>();

    HashMap<Integer, ComponentView> componentView = new HashMap<>();
    HashMap<Integer, MultiView> multiView = new HashMap<>();
    HashMap<Integer, RelationView> relationView = new HashMap<>();

    public UMLView() {
      graphicView = MultiViewManager.getSelectedGraphicView();
    }

    public UMLView(String name, boolean open) {
      this.name = name;
      
      if (open)
        graphicView = MultiViewManager.addAndOpenNewView(name);
      else
        graphicView = MultiViewManager.addNewView(name);
    }
  }
  
  
  // UML STRUCTURE
  private class Variable {
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
  Association currentAssociation;
  ComponentView currentComponentView;
  Dependency currentDependency;
  Entity currentEntity;
  Rectangle currentGeometry;
  Inheritance currentInheritance;
  LinkedList<Point> currentLine;
  UMLView currentUMLView;

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

  LinkedList<Inheritance> inheritance = new LinkedList<>();

  private boolean inMultiViewBounds;

  boolean inRelationView = false, inComponentView = false,
          inNoteGeometry = false, inNoteRelation = false,
          inLabelAssociation = false;

  LinkedList<InterfaceEntity> interfaceEntities = new LinkedList<>();
  
  private ClassDiagram umlClassDiagram;

  public XMLParser(classDiagram.ClassDiagram classDiagram) {
    super();

    if (classDiagram == null)
      throw new IllegalArgumentException("classDiagram is null");

    this.classDiagram = classDiagram;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    final String reader = new String(ch, start, length);

    if (buffer != null)
      buffer.append(reader);
  }

  private void createEntity(Entity e) 
      throws SyntaxeNameException, SAXNotRecognizedException {
    classDiagram.components.Entity ce = null;
    e.name = TypeName.verifyAndAskNewName(e.name);
    boolean isSimpleEntity = true;

    switch (e.entityType) {
      case CLASS:

        ce = new ClassEntity(e.name, e.visibility, e.id);

        classDiagram.addClassEntity((ClassEntity) ce);
        ((ClassEntity) ce).setAbstract(e.isAbstract);

        break;

      case INTERFACE:

        ce = new InterfaceEntity(e.name, e.visibility, e.id);
        classDiagram.addInterfaceEntity((InterfaceEntity) ce);
        ((InterfaceEntity) ce).setAbstract(true);

        break;

      case ENUM:
        isSimpleEntity = false;
        ce = new EnumEntity(e.name, e.id);
        classDiagram.addEnumEntity((EnumEntity) ce);

        break;

      case ASSOCIATION_CLASS:

        final Binary b = (Binary) classDiagram
                .searchComponentById(e.associationClassID);
        if (b == null) // création d'une classe normale.
        {
          ce = new ClassEntity(e.name, e.visibility, e.id);
          classDiagram.addClassEntity((ClassEntity) ce);
          ((ClassEntity) ce).setAbstract(e.isAbstract);
          break;
        }
        
        ce = new AssociationClass(e.name, e.visibility, b, e.id);
        classDiagram.addAssociationClass((AssociationClass) ce);

        break;
        
        default:
          throw new SAXNotRecognizedException(
              e.entityType + ": wrong entity type.");
    }

    if (isSimpleEntity) {

      SimpleEntity se = (SimpleEntity) ce;
      for (Variable v : e.attribute) {
        Attribute a = new Attribute(VariableName.verifyAndAskNewName(v.name),
                v.type);

        se.addAttribute(a);
        se.notifyObservers(UpdateMessage.ADD_ATTRIBUTE_NO_EDIT);
        a.setConstant(v.constant);
        a.setDefaultValue(v.defaultValue);
        a.setStatic(v.isStatic);
        a.setVisibility(v.visibility);
        a.notifyObservers();
      }

      for (Operation o : e.method) {
        Method m;
        if (o.isConstructor)
          m = new ConstructorMethod(
              MethodName.verifyAndAskNewName(o.name), o.visibility, se);
        else
          m = new Method(
              MethodName.verifyAndAskNewName(o.name),
              new Type(TypeName.verifyAndAskNewName(o.returnType)), 
              o.visibility, se);
        se.addMethod(m);
        se.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);

        m.setParametersViewStyle(o.view);
        m.setStatic(o.isStatic);
        m.setAbstract(o.isAbstract);

        for (Variable v : o.variable) {
          classDiagram.components.Variable va = new classDiagram.components.Variable(
                  VariableName.verifyAndAskNewName(v.name), v.type);
          m.addParameter(va);
        }
        m.notifyObservers();
      }
    } else {
      EnumEntity ee = (EnumEntity) ce;
      for (EnumValue v : e.enums) {
        ee.addEnumValue(v);
        ee.notifyObservers(UpdateMessage.ADD_ENUM_NO_EDIT);
      }
    }

    ce.notifyObservers();
  }

  @Override
  public void endDocument() throws SAXException {
    
  }

  public void createDiagram() 
      throws SyntaxeNameException, SAXNotRecognizedException {
    
    MultiViewManager.setSelectedGraphicView(0);
    
    GraphicView rootGraphicView = MultiViewManager.getSelectedGraphicView();
    classDiagram.setName(umlClassDiagram.name);
    classDiagram.setInformation(umlClassDiagram.informations);
    DiagramPropreties.setDiagramsInformations(umlClassDiagram.informations);
    classDiagram.setViewEntity(umlClassDiagram.defaultViewEntities);
    classDiagram.setDefaultViewMethods(umlClassDiagram.defaultViewMethods);
    classDiagram.setDefaultViewEnum(umlClassDiagram.defaultViewEnum);
    classDiagram.setVisibleType(umlClassDiagram.defaultVisibleTypes);
    classDiagram.notifyObservers();
    
    // Don't change the order !!
    importClassesAndInterfaces(); // <- need nothing :D

    importAssociations(); // <- need importation classes
    importAssociationClass(); // <- need importation classes and associations
    importAssociations(); // Import associations that cannot be imported first
                          // time
    importInheritances(); // <- ...
    importDepedency();

    rootGraphicView.setPaintBackgroundLast(true);
    rootGraphicView.goRepaint();

    locateComponentBounds();
  }

  @Override
  public void endElement(String uri, String localName, String qName)
          throws SAXException {
    switch (qName) {
      case "entity":
        currentEntity = null;
        break;
      case "method":
        currentMethod = null;
        break;
      case "EnumValue":
        currentEntity.enums.add(new EnumValue(buffer.toString()));
        buffer = null;
        break;
      case "associationClassID":
        currentEntity.associationClassID = Integer.parseInt(buffer.toString());
        break;
      case "association":
        currentAssociation = null;
        break;
      case "role":
        currentRole = null;
        break;
      case "multiplicity":
        currentRole.multiplicity = new Multiplicity(currentMin, currentMax);
        break;
      case "min":
        currentMin = Integer.parseInt(buffer.toString());
        buffer = null;
        break;
      case "max":
        currentMax = Integer.parseInt(buffer.toString());
        buffer = null;
        break;
      case "child":
        currentInheritance.child = Integer.parseInt(buffer.toString());
        buffer = null;
        break;
      case "parent":
        currentInheritance.parent = Integer.parseInt(buffer.toString());
        buffer = null;
        break;
      case "source":
        currentDependency.source = Integer.parseInt(buffer.toString());
        buffer = null;
        break;
      case "target":
        currentDependency.target = Integer.parseInt(buffer.toString());
        buffer = null;
        break;
      case "relationView":
        inRelationView = false;
        currentUMLView.relationView.put(
            currentRelationView.relationId, currentRelationView);
        break;
      case "multiLineView":
        inRelationView = false;
        currentMultiView.multiLineView.add(currentRelationView);
        break;
      case "multiView":
        currentUMLView.multiView.put(
            currentMultiView.relationId, currentMultiView);
        break;
      case "geometry":
        inNoteGeometry = false;
        currentComponentView.geometry = currentGeometry;
        currentGeometry = null;
        break;
      case "noteGeometry":
        inNoteGeometry = false;
        currentNote.bounds = currentGeometry;
        currentGeometry = null;
        break;
      case "componentView":
        inComponentView = false;
        currentUMLView.componentView.put(
            currentComponentView.componentId, currentComponentView);
        break;
      case "note":
        currentUMLView.notes.add(currentNote);
        currentNote = null;
        break;
      case "multiViewBounds":
        inMultiViewBounds = false;
        currentMultiView.multiViewBounds = currentGeometry;
        currentGeometry = null;
        break;
      case "line":
        currentRelationView.line = currentLine;
        currentLine = null;
        break;
      case "noteLine":
        inNoteRelation = false;
        currentRelationView.line = currentLine;
        currentNote.line.addLast(currentRelationView);
        currentLine = null;
        currentRelationView = null;
        break;
      case "point":
        currentLine.add(currentPoint);
        currentPoint = null;
        break;
      case "labelAssociation":
        currentRelationView.labelAssociation = currentGeometry;
        currentGeometry = null;
        inLabelAssociation = false;
        break;
      case "roleAssociation":
        currentRelationView.roleAssociations.add(currentGeometry);
        currentGeometry = null;
        inLabelAssociation = false;
        break;
      case "multipliciteAssociation":
        currentRelationView.multipliciteAssociations.add(currentGeometry);
        currentGeometry = null;
        inLabelAssociation = false;
        break;
      case "x":
        final int x = Integer.parseInt(buffer.toString());
        if (inComponentView || inNoteGeometry || inLabelAssociation
            || inMultiViewBounds)
          
          currentGeometry.x = x;
        
        else if (inRelationView || inNoteRelation)
          
          currentPoint.x = x;
        buffer = null;
        break;
      case "y":
        final int y = Integer.parseInt(buffer.toString());
        if (inComponentView || inNoteGeometry || inLabelAssociation
            || inMultiViewBounds)
          
          currentGeometry.y = y;
        
        else if (inRelationView || inNoteRelation)
          
          currentPoint.y = y;
        buffer = null;
        break;
      case "w":
        currentGeometry.width = Integer.parseInt(buffer.toString());
        buffer = null;
        break;
      case "h":
        currentGeometry.height = Integer.parseInt(buffer.toString());
        buffer = null;
        break;
    }
  }

  private void importAssociationClass() 
      throws SyntaxeNameException, SAXNotRecognizedException {
    for (final Entity e : umlClassDiagram.diagrameElement.entity)

      if (e.entityType == EntityType.ASSOCIATION_CLASS)

      createEntity(e);
  }

  public void importAssociations() {
    final LinkedList<Association> associationsNotAdded = new LinkedList<>();

    for (final Association a : umlClassDiagram.diagrameElement.association) {
      classDiagram.relationships.Association ac = null;
      
      if (a.role.size() < 2)
        throw new IllegalArgumentException("An association must have at least two roles.");

      final classDiagram.components.Entity source = (classDiagram.components.Entity) classDiagram
              .searchComponentById(a.role.getFirst().componentId);
      final classDiagram.components.Entity target = (classDiagram.components.Entity) classDiagram
              .searchComponentById(a.role.getLast().componentId);

      if (source == null || target == null) {
        associationsNotAdded.add(a);
        continue;
      }

      switch (a.aggregation) {
        case NONE:
          ac = new Binary(source, target, a.direction, a.id);
          classDiagram.addBinary((Binary) ac);

          break;

        case AGGREGATE:
          ac = new classDiagram.relationships.Aggregation(source, target,
                  a.direction, a.id);
          classDiagram
                  .addAggregation((classDiagram.relationships.Aggregation) ac);

          break;

        case COMPOSE:
          ac = new Composition(source, target, a.direction, a.id);
          classDiagram.addComposition((Composition) ac);
          break;

        case MULTI:
          final LinkedList<classDiagram.components.ClassEntity> entities = new LinkedList<>();

          for (final Role role : a.role)

            entities.add((classDiagram.components.ClassEntity) classDiagram
                    .searchComponentById(role.componentId));

          ac = new Multi(entities, a.id);
          classDiagram.addMulti((Multi) ac);
          break;
      }

      for (int i = 0; i < a.role.size(); i++) {
        ac.getRoles().get(i).setName(a.role.get(i).name);
        ac.getRoles().get(i).setVisibility(a.role.get(i).visibility);
        ac.getRoles().get(i).setMultiplicity(a.role.get(i).multiplicity);

        ac.getRoles().get(i).notifyObservers();
        ac.getRoles().get(i).getMultiplicity().notifyObservers();
      }

      ac.setName(a.name);

      ac.notifyObservers();
    }

    umlClassDiagram.diagrameElement.association = associationsNotAdded;
  }

  private void importClassesAndInterfaces() 
      throws SyntaxeNameException, SAXNotRecognizedException {
    for (final Entity e : umlClassDiagram.diagrameElement.entity)
      if (!(e.entityType == EntityType.ASSOCIATION_CLASS)) createEntity(e);

  }

  public void importDepedency() {
    for (final Dependency d : umlClassDiagram.diagrameElement.dependency) {
      classDiagram.components.Entity source = (classDiagram.components.Entity) classDiagram
              .searchComponentById(d.source);
      classDiagram.components.Entity target = (classDiagram.components.Entity) classDiagram
              .searchComponentById(d.target);
      classDiagram.relationships.Dependency dr = new classDiagram.relationships.Dependency(
              source, target, d.id);
      classDiagram.addDependency(dr);

      dr.setLabel(d.label);
      dr.notifyObservers();
    }
  }

  // view

  public void importInheritances() {
    for (final Inheritance h : umlClassDiagram.diagrameElement.inheritance) {

      if (h.innerClass) {
        final classDiagram.components.Entity child = 
            (classDiagram.components.Entity) classDiagram.searchComponentById(h.child);
        
        final classDiagram.components.Entity parent = 
            (classDiagram.components.Entity) classDiagram.searchComponentById(h.parent);

        final classDiagram.relationships.InnerClass innerClass = 
            new classDiagram.relationships.InnerClass(child, parent, h.id);
        
        classDiagram.addInnerClass(innerClass);
        innerClass.notifyObservers();
        
      } else {
        final classDiagram.components.SimpleEntity child = 
            (classDiagram.components.SimpleEntity) classDiagram.searchComponentById(h.child);
        
        final classDiagram.components.SimpleEntity parent = 
            (classDiagram.components.SimpleEntity) classDiagram.searchComponentById(h.parent);
        
        final classDiagram.relationships.Inheritance i = 
            new classDiagram.relationships.Inheritance(child, parent, h.id);
        classDiagram.addInheritance(i);
        i.notifyObservers();
      }
    }
  }

  private void importNotes() {
    for (UMLView umlView : umlClassDiagram.uMLView) {
      GraphicView graphicView = umlView.graphicView;
      for (final Note note : umlView.notes) {
        final TextBoxCommentary noteView = new TextBoxCommentary(graphicView,
                note.content);

        noteView.setBounds(note.bounds);

        for (final RelationView rv : note.line) {
          GraphicComponent component = graphicView
                  .searchAssociedComponent(classDiagram
                          .searchComponentById(rv.relationId));

          if (rv.relationId == -1)
            component = graphicView;

          if (LineCommentary.checkCreate(noteView, component, false)) {
            final LineCommentary lc = new LineCommentary(
                graphicView, noteView, component, rv.line.getFirst(), 
                rv.line.getLast(), false);
            
            for (int i = 1; i < rv.line.size() - 1; i++) {
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
  }

  public void locateComponentBounds() {

    for (UMLView umlView : umlClassDiagram.uMLView) {
    
      GraphicView graphicView = umlView.graphicView;
      graphicView.setName(umlView.name);
      
      // Generals bounds
      for (GraphicComponent g : graphicView.getAllComponents()) {
        IDiagramComponent component = g.getAssociedComponent();

        if (component != null) {
          ComponentView cv = umlView.componentView.get(component.getId());

          if (cv != null) {
            g.setBounds(cv.geometry);
            g.setColor(cv.color);

            // Gestion des entités
            if (g instanceof SimpleEntityView) {
              SimpleEntityView entityView = (SimpleEntityView) g;
              entityView.setDisplayAttributes(cv.displayAttributes);
              entityView.setDisplayMethods(cv.displayMethods);
              entityView.setDisplayDefault(cv.displayDefault);
            } else if (g instanceof EnumView) {
              ((EnumView) g).setTypeEnumDisplay(cv.typeEnumDisplay);
            }
          } else {
            if (g instanceof EntityView)
              g.delete();
          }
        }
      }

      // Associations
      for (LineView l : graphicView.getLinesView()) {
        IDiagramComponent component = l.getAssociedXmlElement();

        if (component != null) {
          final RelationView rl = umlView.relationView.get(component.getId());
          if (rl == null) continue;

          LinkedList<Point> points = rl.line;

          for (int i = 1; i < points.size() - 1; i++) {
            final RelationGrip rg = new RelationGrip(graphicView, l);
            rg.setAnchor(points.get(i));
            rg.notifyObservers();
            l.addGrip(rg, i);
          }

          RelationGrip first = l.getFirstPoint(), last = l.getLastPoint();

          first.setAnchor(points.getFirst());
          last.setAnchor(points.getLast());

          first.notifyObservers();
          last.notifyObservers();

          l.setColor(rl.color);
          final LinkedList<TextBox> tb = l.getTextBoxRole();

          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              if (tb.size() >= 1) {
                ((TextBoxLabel) tb.getFirst()).computeDeplacement(new Point(
                        rl.labelAssociation.x, rl.labelAssociation.y));

                if (tb.size() >= 3) {
                  ((TextBoxLabel) tb.get(1)).computeDeplacement(new Point(
                          rl.roleAssociations.get(0).x, rl.roleAssociations
                                  .get(0).y));
                  ((TextBoxLabel) tb.get(2)).computeDeplacement(new Point(
                          rl.roleAssociations.get(1).x, rl.roleAssociations
                                  .get(1).y));

                  ((TextBoxRole) tb.get(1)).getTextBoxMultiplicity()
                          .computeDeplacement(
                                  new Point(rl.multipliciteAssociations.get(0).x,
                                          rl.multipliciteAssociations.get(0).y));
                  ((TextBoxRole) tb.get(2)).getTextBoxMultiplicity()
                          .computeDeplacement(
                                  new Point(rl.multipliciteAssociations.get(1).x,
                                          rl.multipliciteAssociations.get(1).y));
                }
              }
            }
          });

        }
      }

      // Multi-association
      for (final graphic.relations.MultiView mv : graphicView.getMultiView()) {
        final IDiagramComponent component = mv.getAssociedXmlElement();

        if (component != null) {
          final MultiView xmlMV = umlView.multiView.get(component.getId());

          final LinkedList<MultiLineView> multiLinesView = mv.getMultiLinesView();
          
          mv.setBounds(xmlMV.multiViewBounds);

          for (int j = 0; j < multiLinesView.size(); j++) {
            final RelationView rl = xmlMV.multiLineView.get(j);
            final LinkedList<Point> points = rl.line;
            final MultiLineView mlv = multiLinesView.get(j);

            for (int i = 1; i < points.size() - 1; i++) {
              final RelationGrip rg = new RelationGrip(graphicView, mlv);
              rg.setAnchor(points.get(i));
              rg.notifyObservers();
              mlv.addGrip(rg, i);
            }

            RelationGrip first = mlv.getFirstPoint(), last = mlv.getLastPoint();

            first.setAnchor(points.getFirst());
            last.setAnchor(points.getLast());

            first.notifyObservers();
            last.notifyObservers();

            // Role
            final LinkedList<TextBox> tb = mlv.getTextBoxRole();

            SwingUtilities.invokeLater(new Runnable() {

              @Override
              public void run() {
                if (tb.size() == 1) {
                  ((TextBoxLabel) tb.getFirst()).computeDeplacement(new Point(
                          rl.roleAssociations.get(0).x, rl.roleAssociations
                                  .get(0).y));

                  ((TextBoxRole) tb.getFirst()).getTextBoxMultiplicity()
                          .computeDeplacement(
                                  new Point(rl.multipliciteAssociations.get(0).x,
                                          rl.multipliciteAssociations.get(0).y));
                }
              }
            });
          }

          mv.setColor(xmlMV.color);
          mv.setBounds(xmlMV.multiViewBounds);
        }
      }
    }
    
    importNotes();
  }

  @Override
  public void startElement(String uri, String localName, String qName,
          Attributes attributes) throws SAXException {
    switch (qName) {
      case "classDiagram":
        try {
          umlClassDiagram = new ClassDiagram();
        } catch (final Exception e) {
          throw new SAXException(e);
        } break;
      case "diagramElements":
        umlClassDiagram.diagrameElement = new DiagramElements();
        
        umlClassDiagram.name = attributes.getValue("name");
        umlClassDiagram.informations = attributes.getValue("informations");
        
        if (attributes.getValue("defaultViewEntities") != null)
          umlClassDiagram.defaultViewEntities = ViewEntity.valueOf(
              attributes.getValue("defaultViewEntities"));
        
        if (attributes.getValue("defaultViewMethods") != null)
          umlClassDiagram.defaultViewMethods = 
              ParametersViewStyle.valueOf(attributes.getValue("defaultViewMethods"));
        
        if (attributes.getValue("defaultViewEnum") != null)
          umlClassDiagram.defaultViewEnum = 
              Boolean.valueOf(attributes.getValue("defaultViewEnum"));
        
        if (attributes.getValue("defaultVisibleTypes") != null)
          umlClassDiagram.defaultVisibleTypes = 
              Boolean.valueOf(attributes.getValue("defaultVisibleTypes"));
        
        break;
      case "entity":
        try {
          currentEntity = new Entity();
          currentEntity.id = Integer.parseInt(attributes.getValue("id"));
          currentEntity.name = attributes.getValue("name");
          
          String currentAttributeValue = attributes.getValue("entityType");
          if (currentAttributeValue != null)
            currentEntity.entityType = EntityType.valueOf(attributes
                .getValue("entityType"));
          
          currentAttributeValue = attributes.getValue("visibility");
          if (currentAttributeValue != null)
            currentEntity.visibility = Visibility.valueOf(attributes
                .getValue("visibility"));
          
          currentAttributeValue = attributes.getValue("isAbstract");
          if (currentAttributeValue != null)
            currentEntity.isAbstract = Boolean.parseBoolean(attributes
                .getValue("isAbstract"));
          
          umlClassDiagram.diagrameElement.entity.add(currentEntity);
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "method":
        try {
          currentMethod = new Operation();
          currentMethod.name = attributes.getValue("name");
          currentMethod.returnType = attributes.getValue("returnType");
          currentMethod.visibility = Visibility.valueOf(attributes
              .getValue("visibility"));
          currentMethod.isStatic = Boolean.parseBoolean(attributes
              .getValue("isStatic"));
          currentMethod.isAbstract = Boolean.parseBoolean(attributes
              .getValue("isAbstract"));
          
          if (attributes.getValue("view") != null)
            currentMethod.view = ParametersViewStyle.valueOf(attributes
                .getValue("view"));
          
          if (attributes.getValue("is-constructor") != null)
            currentMethod.isConstructor = Boolean.valueOf(attributes
                .getValue("is-constructor"));
          
          currentEntity.method.add(currentMethod);
        } catch (final Exception e) {
          throw new SAXException(e);
        } break;
      case "variable":
        try {
          final Variable variable = new Variable();
          variable.name = attributes.getValue("name");
          variable.type = new Type(TypeName.verifyAndAskNewName(attributes
              .getValue("type")));
          variable.constant = Boolean.parseBoolean(attributes.getValue("const"));
          
          currentMethod.variable.add(variable);
        } catch (final SyntaxeNameException e) {
          throw new SAXException(e);
        } break;
      case "attribute":
        try {
          final Variable variable = new Variable();
          variable.name = attributes.getValue("name");
          variable.type = new Type(TypeName.verifyAndAskNewName(attributes
              .getValue("type")));
          variable.constant = Boolean.parseBoolean(attributes.getValue("const"));
          variable.visibility = Visibility.valueOf(attributes
              .getValue("visibility"));
          variable.defaultValue = attributes.getValue("defaultValue");
          // variable.collection =
          // Integer.parseInt(attributes.getValue("collection"));
          variable.isStatic = Boolean.parseBoolean(attributes
              .getValue("isStatic"));
          
          currentEntity.attribute.add(variable);
        } catch (final SyntaxeNameException e) {
          throw new SAXException(e);
        } break;
      case "association":
        try {
          currentAssociation = new Association();
          currentAssociation.id = Integer.parseInt(attributes.getValue("id"));
          currentAssociation.name = attributes.getValue("name");
          try {
            currentAssociation.direction = NavigateDirection.valueOf(attributes
                .getValue("direction"));
          } catch (IllegalArgumentException e) {
            // For older version of sly file. Convert boolean value to
            // navigability.
            if (Boolean.parseBoolean(attributes.getValue("direction")))
              currentAssociation.direction = NavigateDirection.FIRST_TO_SECOND;
            else
              currentAssociation.direction = NavigateDirection.BIDIRECTIONAL;
            
          }
          currentAssociation.aggregation = Aggregation.valueOf(attributes
              .getValue("aggregation"));
          
          umlClassDiagram.diagrameElement.association.add(currentAssociation);
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "role":
        try {
          currentRole = new Role();
          currentRole.name = attributes.getValue("name");
          currentRole.componentId = Integer.parseInt(attributes
              .getValue("componentId"));
          currentRole.visibility = Visibility.valueOf(attributes
              .getValue("visibility"));
          
          currentAssociation.role.add(currentRole);
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "multiplicity":
        break;
      case "inheritance":
        try {
          currentInheritance = new Inheritance();
          currentInheritance.id = Integer.parseInt(attributes.getValue("id"));
          currentInheritance.innerClass = Boolean.parseBoolean(attributes
              .getValue("innerClass"));
          
          buffer = new StringBuffer();
          
          umlClassDiagram.diagrameElement.inheritance.add(currentInheritance);
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "dependency":
        try {
          currentDependency = new Dependency();
          currentDependency.id = Integer.parseInt(attributes.getValue("id"));
          currentDependency.label = attributes.getValue("label");
          
          buffer = new StringBuffer();
          
          umlClassDiagram.diagrameElement.dependency.add(currentDependency);
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "umlView":
        try {
          UMLView newUMLView;
          boolean open = true;
          
          if (attributes.getValue("open") != null)
            open = Boolean.valueOf(attributes.getValue("open"));
          
          if (umlClassDiagram.uMLView.size() == 0) // root graphic view
            newUMLView = new UMLView();
          else // new view
            newUMLView = new UMLView(attributes.getValue("name"), open);
          
          currentUMLView = newUMLView;
          umlClassDiagram.uMLView.add(newUMLView);
        } catch (final Exception e) {
          throw new SAXException(e);
        } break;
      case "componentView":
        try {
          inComponentView = true;
          currentComponentView = new ComponentView();
          currentComponentView.componentId = Integer.parseInt(attributes
              .getValue("componentID"));
          currentComponentView.color = Integer.parseInt(attributes
              .getValue("color"));
          
          if (attributes.getValue("displayAttributes") != null)
            currentComponentView.displayAttributes = Boolean
                .parseBoolean(attributes.getValue("displayAttributes"));
          
          if (attributes.getValue("displayMethods") != null)
            currentComponentView.displayMethods = Boolean.parseBoolean(attributes
                .getValue("displayMethods"));
          
          if (attributes.getValue("displayDefault") != null)
            currentComponentView.displayDefault = Boolean.parseBoolean(attributes
                .getValue("displayDefault"));
          
          if (attributes.getValue("enumValuesVisible") != null)
            currentComponentView.typeEnumDisplay = TypeEnumDisplay
                .valueOf(attributes.getValue("enumValuesVisible"));
          
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "geometry":
      case "noteGeometry":
        inNoteGeometry = true;
        currentGeometry = new Rectangle();
        break;
      case "relationView":
      case "multiLineView":
        try {
          inRelationView = true;
          currentRelationView = new RelationView();
          currentRelationView.relationId = Integer.parseInt(attributes
              .getValue("relationId"));
          currentRelationView.color = Integer.parseInt(attributes
              .getValue("color"));
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "multiView":
        try {
          currentMultiView = new MultiView();
          currentMultiView.relationId = Integer.parseInt(attributes
              .getValue("relationId"));
          currentMultiView.color = Integer.parseInt(attributes.getValue("color"));
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "multiViewBounds":
        inMultiViewBounds = true;
        currentGeometry = new Rectangle();
        break;
      case "line":
        currentLine = new LinkedList<>();
        break;
      case "noteLine":
        inNoteRelation = true;
        currentLine = new LinkedList<>();
        currentRelationView = new RelationView();
        currentRelationView.relationId = Integer.parseInt(attributes
            .getValue("relationId"));
        currentRelationView.color = Integer
            .parseInt(attributes.getValue("color"));
        break;
      case "point":
        currentPoint = new Point();
        break;
      case "labelAssociation":
      case "roleAssociation":
      case "multipliciteAssociation":
        currentGeometry = new Rectangle();
        inLabelAssociation = true;
        break;
      case "note":
        try {
          currentNote = new Note();
          currentNote.content = attributes.getValue("content");
          currentNote.color = Integer.parseInt(attributes.getValue("color"));
        } catch (final NumberFormatException e) {
          throw new SAXException(e);
        } break;
      case "EnumValue":
      case "min":
      case "max":
      case "associationClassID":
      case "child":
      case "parent":
      case "source":
      case "target":
      case "x":
      case "y":
      case "w":
      case "h":
        buffer = new StringBuffer();
        break;
    }
  }
}
