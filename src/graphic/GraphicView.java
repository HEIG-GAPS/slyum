package graphic;

import change.BufferBounds;
import change.BufferColor;
import change.Change;
import classDiagram.ClassDiagram;
import classDiagram.ClassDiagram.ViewEntity;
import classDiagram.IComponentsObserver;
import classDiagram.IDiagramComponent;
import classDiagram.INameObserver;
import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.EnumEntity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method.ParametersViewStyle;
import classDiagram.relationships.Aggregation;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Composition;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Inheritance;
import classDiagram.relationships.InnerClass;
import classDiagram.relationships.Multi;
import classDiagram.relationships.Relation;
import classDiagram.relationships.Role;
import graphic.entity.AssociationClassView;
import graphic.entity.ClassView;
import graphic.entity.EntityView;
import graphic.entity.EnumView;
import graphic.entity.InterfaceView;
import graphic.entity.SimpleEntityView;
import graphic.export.ExportViewImage;
import graphic.factory.CreateComponent;
import graphic.factory.MultiFactory;
import graphic.relations.AggregationView;
import graphic.relations.BinaryView;
import graphic.relations.CompositionView;
import graphic.relations.DependencyView;
import graphic.relations.InheritanceView;
import graphic.relations.InnerClassView;
import graphic.relations.LineCommentary;
import graphic.relations.LineView;
import graphic.relations.MultiLineView;
import graphic.relations.MultiView;
import graphic.relations.RelationView;
import graphic.textbox.TextBoxCommentary;
import graphic.textbox.TextBoxDiagramName;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.IListenerComponentSelectionChanged;
import swing.MultiViewManager;
import swing.PanelClassDiagram;
import swing.PropertyLoader;
import swing.slyumCustomizedComponents.SButton;
import swing.SColorAssigner;
import swing.SPanelDiagramComponent;
import swing.SPanelDiagramComponent.Mode;
import swing.SPanelElement;
import swing.slyumCustomizedComponents.SScrollPane;
import swing.Slyum;
import utility.OSValidator;
import utility.SMessageDialog;
import utility.Utility;

/**
 * This class is the main container for all diagrams components view
 * (classComponent, interfaceComponent, relation, ...). It performs class
 * diagram, movables and resizables events. Each graphic component have a
 * reference on the graphic view.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class GraphicView extends GraphicComponent 
                         implements MouseMotionListener, 
                                    MouseListener, 
                                    KeyListener,
                                    MouseWheelListener, 
                                    IComponentsObserver, 
                                    INameObserver,
                                    Printable, 
                                    ColoredComponent,
                                    Observer {


  
  public final static boolean BACKGROUND_GRADIENT = false;
  public final static Color BASIC_COLOR = new Color(241, 241, 243);
  public final static boolean CTRL_FOR_GRIP = false;
  public final static int DEFAULT_TITLE_BORDER_WIDTH = 1;
  public final static boolean ENTITY_GRADIENT = false;
  public final static int GRID_COLOR = Color.DARK_GRAY.getRGB();
  public final static int GRID_POINT_OPACITY = 255;
  public final static int GRID_SIZE = 10;
  public final static boolean GRID_VISIBLE = true;
  public final static boolean IS_AUTOMATIC_GRID_COLOR = false;
  public final static boolean IS_GRID_ENABLE = true;
  public final static boolean IS_GRID_OPACITY_ENABLE = false;
  public final static boolean IS_PAINT_TITLE_BORDER = true;
  public final static String NO_NAMED_VIEW = "Unnamed view";
  public final static String ROOT_VIEW_DEFAULT_NAME = "Main view";
  public final static double SCALE_STEP = 0.1;

  /**
   * Compute mouse entered and exited event. the componentMouseHover can be the
   * same as the current component. In this case, no event will be called.
   * 
   * @param component the current mouse hover component
   * @param componentMouseHover the previous mouse hover component
   * @param e the mouse event
   */
  public static void computeComponentEventEnter(
      GraphicComponent component, GraphicComponent componentMouseHover, MouseEvent e) {
    if (component != componentMouseHover) {
      if (componentMouseHover != null) componentMouseHover.gMouseExited(e);

      if (component != null) component.gMouseEntered(e);
    }
  }

  public static void deleteComponent(GraphicComponent component) {
    boolean isRecord = Change.isRecord();
    Change.record();
    component.delete();
    if (!isRecord) Change.stopRecord();
  }

  public static void deleteComponents(List<GraphicComponent> components) {
    if (components.isEmpty()) return;
    
    boolean isRecord = Change.isRecord();
    Change.record();
    
    components.stream().forEach((c) -> { c.delete(); });
    
    if (!isRecord) Change.stopRecord();
  }

  public static void setAutomaticGridColor(boolean enable) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.AUTOMATIC_GRID_COLOR, String.valueOf(enable));
    PropertyLoader.getInstance().push();
  }

  /**
   * Get the basic color for the graphic view. The basic color is the color
   * define by default for new graphic view.
   * 
   * @return
   */
  public static Color getBasicColor() {
    final String basicColor = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.COLOR_GRAPHIC_VIEW);
    Color color;

    if (basicColor == null)
      color = BASIC_COLOR;
    else
      color = new Color(Integer.parseInt(basicColor));

    return color;
  }
  
  /**
   * Set a new basic color for graphic views. The basic color is the color
   * define by default for new graphic view.
   *
   * @param color
   */
  public static void setBasicColor(Color color) {
    PropertyLoader
        .getInstance()
        .getProperties()
        .put(PropertyLoader.COLOR_GRAPHIC_VIEW,
                 String.valueOf(color.getRGB()));
    PropertyLoader.getInstance().push();
  }

  public static void setButtonFactory(SButton btn) {
    SPanelDiagramComponent.getInstance().setButtonModeStyle(btn);
  }
  
  public static ViewEntity getDefaultViewEntities() {
    String prop =
    PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.VIEW_ENTITIES);
    
    ViewEntity view = ViewEntity.ALL;
    if (prop != null)
      view = ViewEntity.valueOf(prop);
    
    return view;
  }
  
  public static boolean getDefaultViewEnum() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.VIEW_ENUM);
    boolean view = true;
    
    if (prop != null) view = Boolean.parseBoolean(prop);
    
    return view;
  }
  
  public static ParametersViewStyle getDefaultViewMethods() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.VIEW_METHODS);
    ParametersViewStyle view = ParametersViewStyle.TYPE_AND_NAME;
    
    if (prop != null) view = ParametersViewStyle.valueOf(prop);
    
    return view;
  }
  
  public static boolean getDefaultVisibleTypes() {
    String prop =
    PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.VIEW_TYPES);
    
    boolean visible = true;
    if (prop != null)
      visible = Boolean.valueOf(prop);
    
    return visible;
  }
  
  public static int getGridColor() {
    final String prop = PropertyLoader.getInstance().getProperties()
            .getProperty(PropertyLoader.GRID_COLOR);
    int color = GRID_COLOR;

    if (prop != null) color = Integer.parseInt(prop);

    return color;
  }
  

  public static void setGridColor(int color) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.GRID_COLOR, String.valueOf(color));
    PropertyLoader.getInstance().push();
  }

  public static int getGridOpacity() {
    if (!isGridOpacityEnable()) return 255;

    final String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.GRID_POINT_OPACITY);
    int opacity = GRID_POINT_OPACITY;

    if (prop != null) opacity = Integer.parseInt(prop);

    return (int) (opacity * 2.55);
  }
  
  public static void setGridPointOpacity(int opacity) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.GRID_POINT_OPACITY, String.valueOf(opacity));
    PropertyLoader.getInstance().push();
  }
  
  public static int getGridSize() {
    final String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.GRID_SIZE);
    int size = GRID_SIZE;
    
    if (prop != null) size = Integer.parseInt(prop);
    
    return isGridEnable() ? size : 1;
  }

  public static void setGridSize(int size) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.GRID_SIZE, String.valueOf(size));
    PropertyLoader.getInstance().push();

    // Update the components bounds for adapting with new grid.
    for (GraphicView gv : MultiViewManager.getAllGraphicViews())
      for (final GraphicComponent c : gv.getAllComponents())
        c.setBounds(c.getBounds());
  }
  
  public static void setGridVisibley(boolean visible) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.GRID_VISIBLE, String.valueOf(visible));
    PropertyLoader.getInstance().push();
  }
  
  public static  boolean isAddGripMode(
          ) {
    return SPanelDiagramComponent.getInstance().getMode() == Mode.GRIP;
  }
  
  public static boolean isAddToSelection(MouseEvent e) {
    return OSValidator.IS_MAC ? e.isMetaDown() : e.isControlDown() || e.isShiftDown();
  }
  
  public static boolean isAutomatiqueGridColor() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.AUTOMATIC_GRID_COLOR);
    boolean enable = IS_AUTOMATIC_GRID_COLOR;
    
    if (prop != null) enable = Boolean.parseBoolean(prop);
    
    return enable;
  }
  
  public static boolean isBackgroundGradient() {
    String gradientBool = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.BACKGROUND_GRADIENT);
    boolean gradient = BACKGROUND_GRADIENT;
    
    if (gradientBool != null) gradient = Boolean.parseBoolean(gradientBool);
    
    return gradient;
  }
  
  public static void setBackgroundGradient(boolean isGradient) {
    PropertyLoader
        .getInstance()
        .getProperties()
        .put(PropertyLoader.BACKGROUND_GRADIENT, String.valueOf(isGradient));
    PropertyLoader.getInstance().push();
  }

  public static boolean isEntityGradient() {
    String gradientBool = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.ENTITY_GRADIENT);
    boolean gradient = ENTITY_GRADIENT;
    
    if (gradientBool != null) gradient = Boolean.parseBoolean(gradientBool);
    
    return gradient;
  }
  
  public static void setEntityGradient(boolean isGradient) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.ENTITY_GRADIENT, String.valueOf(isGradient));
    PropertyLoader.getInstance().push();
  }

  public static boolean isGridEnable() {
    final String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.GRID_ENABLE);
    boolean enable = IS_GRID_ENABLE;
    
    if (prop != null) enable = Boolean.parseBoolean(prop);
    
    return enable;
  }
  
  public static void setGridEnable(boolean enable) {
    PropertyLoader.getInstance().getProperties()
            .put(PropertyLoader.GRID_ENABLE, String.valueOf(enable));
    PropertyLoader.getInstance().push();
  }

  public static boolean isGridOpacityEnable() {
    final String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.GRID_OPACITY_ENABLE);
    boolean enable = IS_GRID_OPACITY_ENABLE;
    
    if (prop != null) enable = Boolean.parseBoolean(prop);
    
    return enable;
  }
  
  public static void setGridOpacityEnable(boolean enable) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.GRID_OPACITY_ENABLE, String.valueOf(enable));
    PropertyLoader.getInstance().push();
  }

  public static boolean isGridVisible() {
    final String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.GRID_VISIBLE);
    boolean visible = GRID_VISIBLE;
    
    if (prop != null) visible = Boolean.parseBoolean(prop);
    
    return visible;
  }

  public static boolean isTitleBorderPainted() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.PAINT_TITLE_BORDER);
    boolean paint = IS_PAINT_TITLE_BORDER;
    
    if (prop != null) paint = Boolean.parseBoolean(prop);
    
    return paint;
  }

  /**
   * Search a component in the given who are on the location given. This method
   * uses the isAtPosition() method from GraphicComponent for know if the
   * location given is on the component or not. Return null if no component are
   * found. This method return the first component found in
   * @param <T> the list.
   *
   * @param components
   *          the list of component extent from GraphicComponent
   * @param pos
   *          the location for find a component
   * @return the component found or null if no component are found
   */
  public static <T extends GraphicComponent> T searchComponentWithPosition(List<T> components, Point pos) {
    for (final T c : components)
      if (c.isAtPosition(pos)) return c;
    
    return null; // no component found
  }
  // use in printing
  private final int MAX_PRINT_PAGE = 1;

  private final ClassDiagram classDiagram;
  // last component mouse pressed
  private GraphicComponent componentMousePressed;

  private CreateComponent currentFactory;
  private final LinkedList<EntityView> entities = new LinkedList<>();
  private GraphicComponent justCreatedComponent;

  private LinkedList<IListenerComponentSelectionChanged> lcsc = new LinkedList<>();

  private final LinkedList<LineView> linesView = new LinkedList<>();
  private int mouseButton = 0;

  private Point mousePressedLocation = new Point();
  private final LinkedList<MultiView> multiViews = new LinkedList<>();
  private String name;

  private final LinkedList<TextBoxCommentary> notes = new LinkedList<>();

  private final LinkedList<GraphicComponent> othersComponents = new LinkedList<>();
  private boolean paintBackgroundLast = false;

  // Selection rectangle.
  private Rectangle rubberBand = new Rectangle();

  // last component mouse hovered
  private GraphicComponent saveComponentMouseHover;
  private final JPanel scene;

  private final JScrollPane scrollPane;
  private boolean stopRepaint = false;
  private TextBoxDiagramName txtBoxDiagramName;

  private Rectangle visibleRect = new Rectangle();
  private float zoom = 1.0f;


  /**
   * Create a new graphic view representing the class diagram given. The new
   * graphic view is empty when created. If classDiagram given is not empty, you
   * must manually add existing component.
   * 
   * @param classDiagram
   *          the class diagram associated with this graphic view.
   * @param isRoot if it's the main class diagram view.
   */
  public GraphicView(ClassDiagram classDiagram, boolean isRoot) {
    super();

    if (classDiagram == null)
      throw new IllegalArgumentException("classDiagram is null");

    this.classDiagram = classDiagram;
    classDiagram.addObserver(this);
    
    scene = new JPanel(null) {
      {
        setTransferHandler(new TransferHandler(){

          @Override
          public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(Entity.ENTITY_FLAVOR);
          }

          @Override
          public boolean importData(final TransferHandler.TransferSupport support) {
            if (!canImport(support))
              return false;
            try {
              Entity importedEntity = 
                  (Entity)support.getTransferable()
                      .getTransferData(Entity.ENTITY_FLAVOR);
          
                // Adding the imported entity view.
                if (createEntityWithRelations(
                    importedEntity,
                    support.getDropLocation().getDropPoint()))
                return true;
             
            } catch (UnsupportedFlavorException | IOException ex) {
              Slyum.LOGGER.log(Level.SEVERE, null, ex);
            }
            return false;
          }
        });
      }

      @Override
      public void paintComponent(Graphics g) {
        updatePreferredSize(); // for scrolling
        super.paintComponent(g);
        paintScene((Graphics2D) g);
      }

      @Override
      public void repaint(int x, int y, int width, int height) {
        repaint(new Rectangle(x, y, width, height));
      }

      @Override
      public void repaint(Rectangle r) {
        if (stopRepaint) return;

        super.repaint(growForRepaint(Utility.scaleRect(r, getScale())));
      }

      @Override
      public void paintImmediately(int x, int y, int w, int h) {
        paintImmediately(new Rectangle(x, y, w, h));
      }

      @Override
      public void paintImmediately(Rectangle r) {
        if (stopRepaint) return;

        super.paintImmediately(r.x, r.y, r.width, r.height);
      }

      private Rectangle growForRepaint(Rectangle rect) {
        return Utility.growRectangle(new Rectangle(rect), getGridSize() + 10);
      }

      @Override
      public void setCursor(Cursor cursor) {
        if (currentFactory != null) // cursor change are disabled during creation of new component
          cursor = currentFactory.getCursor();
        super.setCursor(cursor);
      }
    };

    AdjustmentListener listnener = new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent evt) {
        repaint();
      }
    };
    
    scrollPane = new SScrollPane(scene) {

      @Override
      public String toString() {
        return GraphicView.this.getName();
      }
      
    };
    scrollPane.getVerticalScrollBar().setUnitIncrement(50);
    scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
    scrollPane.setBorder(null);
    scrollPane.getHorizontalScrollBar().addAdjustmentListener(listnener);
    scrollPane.getVerticalScrollBar().addAdjustmentListener(listnener);

    scene.addMouseWheelListener(this);
    scene.addKeyListener(this);
    scene.addMouseMotionListener(this);
    scene.addMouseListener(this);
    saveComponentMouseHover = this;
    classDiagram.addComponentsObserver(this);

    setColor(getBasicColor());

    JMenuItem menuItem;

    // Menu item add class
    menuItem = makeMenuItem("Add Class", Slyum.ACTION_NEW_CLASS, "class");
    popupMenu.add(menuItem);

    // Menu item add interface
    menuItem = makeMenuItem("Add Interface", Slyum.ACTION_NEW_INTERFACE,
        "interface");
    popupMenu.add(menuItem);

    // Menu item add class association
    menuItem = makeMenuItem("Add Association class",
        Slyum.ACTION_NEW_CLASS_ASSOCIATION, "classAssoc");
    popupMenu.add(menuItem);

    popupMenu.addSeparator();

    // Menu item add generalize
    menuItem = makeMenuItem("Add Inheritance", Slyum.ACTION_NEW_GENERALIZE,
        "generalize");
    popupMenu.add(menuItem);

    // Menu item add inner class
    menuItem = makeMenuItem("Add inner class", Slyum.ACTION_NEW_INNER_CLASS,
        "innerClass");
    popupMenu.add(menuItem);

    // Menu item add dependency
    menuItem = makeMenuItem("Add Dependency", Slyum.ACTION_NEW_DEPENDENCY,
        "dependency");
    popupMenu.add(menuItem);

    // Menu item add association
    menuItem = makeMenuItem("Add Association", Slyum.ACTION_NEW_ASSOCIATION,
        "association");
    popupMenu.add(menuItem);

    // Menu item add aggregation
    menuItem = makeMenuItem("Add Aggregation", Slyum.ACTION_NEW_AGGREGATION,
        "aggregation");
    popupMenu.add(menuItem);

    // Menu item add composition
    menuItem = makeMenuItem("Add Composition", Slyum.ACTION_NEW_COMPOSITION,
        "composition");
    popupMenu.add(menuItem);

    // Menu item add composition
    menuItem = makeMenuItem("Add Multi-association", Slyum.ACTION_NEW_MULTI,
        "multi");
    popupMenu.add(menuItem);

    popupMenu.addSeparator();

    // Menu item add note
    menuItem = makeMenuItem("Add Note", Slyum.ACTION_NEW_NOTE, "note");
    popupMenu.add(menuItem);

    // Menu item link note
    menuItem = makeMenuItem("Link Note", Slyum.ACTION_NEW_LINK_NOTE, "linkNote");
    popupMenu.add(menuItem);

    addSPanelListener();
    
    txtBoxDiagramName = new TextBoxDiagramName(this, isRoot ? classDiagram : this);
    txtBoxDiagramName.setVisible(Slyum.isViewTitleOnExport());
    addOthersComponents(txtBoxDiagramName);
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    if ("Color".equals(e.getActionCommand())) {
      SColorAssigner ca = new SColorAssigner();
      if (ca.isAccepted())
        if (ca.isDefaultColor())
          setDefaultColorForComponents(this);
        else
          setColorForComponents(ca.getColor(), this);
    } else {
      super.actionPerformed(e);
    }

    Slyum.getInstance().actionPerformed(e);
  }
  
  public void adaptDiagramToWindow() {
    adaptListToWindow(getAllComponents());
  }
  
  public void adaptListToWindow(LinkedList<? extends GraphicComponent> list) {
    final Rectangle limits = Utility.getLimits(list);
    Rectangle vr = getScene().getVisibleRect(), l;
    
    l = Utility.scaleRect(limits, getScale());
    
    double xRatio = l.getWidth() / vr.getWidth();
    double yRatio = l.getHeight() / vr.getHeight();
    
    if (xRatio > yRatio)
      setScale(getScale() / xRatio);
    else
      setScale(getScale() / yRatio);
    
    // Must wait scaling.
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        getScene().scrollRectToVisible(Utility.scaleRect(limits, getScale()));
      }
    });
  }

  public void adaptSelectionToWindow() {
    adaptListToWindow(getSelectedComponents());
  }
  
  public void addAggregation(Aggregation component) {
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null) {
      final LinkedList<Role> roles = component.getRoles();
      final EntityView source = (EntityView) searchAssociedComponent(roles
          .getFirst().getEntity());
      final EntityView target = (EntityView) searchAssociedComponent(roles
          .getLast().getEntity());
      
      // Are Components in this graphic view?
      if (source == null || target == null)
        return;

      addComponentIn(new AggregationView(this, source, target, component,
          source.middleBounds(), target.middleBounds(), false), linesView);
    }
  }

  public void addAssociationClass(AssociationClass component) {
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null) {
      final BinaryView bv = (BinaryView) searchAssociedComponent(component
          .getAssociation());
      
      // Are Components in this graphic view?
      if (bv == null)
        return;
      
      addComponentIn(new AssociationClassView(this, component, bv,
          new Rectangle(100, 100, 100, 100)), entities);
    }
  }
  
  public void addBinary(Binary component) {
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null) {
      final LinkedList<Role> roles = component.getRoles();
      final EntityView source = (EntityView) searchAssociedComponent(roles
          .getFirst().getEntity());
      final EntityView target = (EntityView) searchAssociedComponent(roles
          .getLast().getEntity());
      
      // Are Components in this graphic view?
      if (source == null || target == null)
        return;
      
      addComponentIn(
          new BinaryView(this, source, target, component,
              source.middleBounds(), target.middleBounds(), false),
          linesView);
    }
  }

  public void addClassEntity(ClassEntity component) {
    GraphicComponent result = searchAssociedComponent(component);
    if (result == null)
      addEntity(new ClassView(this, component));
  }

  public <T extends GraphicComponent> boolean addComponentIn(T component, LinkedList<T> list) {
    if (component == null)
      throw new IllegalArgumentException("component is null");

    if (!list.contains(component) && list.add(component)) {
      getScene().paintImmediately(component.getBounds());
      return true;
    }

    return false;
  }
  
  public  void addComposition(Composition component) {
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null) {
      final LinkedList<Role> roles = component.getRoles();
      final EntityView source = (EntityView) searchAssociedComponent(roles
          .getFirst().getEntity());
      final EntityView target = (EntityView) searchAssociedComponent(roles
          .getLast().getEntity());
      
      // Are Components in this graphic view?
      if (source == null || target == null)
        return;

      addComponentIn(new CompositionView(this, source, target, component,
          source.middleBounds(), target.middleBounds(), false), linesView);
    }
  }
  
  public void addDependency(Dependency component) {
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null) {
      final EntityView source = (EntityView) searchAssociedComponent(component
          .getSource());
      final EntityView target = (EntityView) searchAssociedComponent(component
          .getTarget());
      
      // Are Components in this graphic view?
      if (source == null || target == null)
        return;

      addComponentIn(
          new DependencyView(this, source, target, component,
              source.middleBounds(), target.middleBounds(), false),
              linesView);
    }
  }

  /**
   * Add a new entity view. Adding a component means it will be drawn and
   * managed by the graphic view.
   * 
   * @param component
   *          the entity to add.
   * @return true if the component has been added; false otherwise
   */
  public boolean addEntity(EntityView component) {    
    return addComponentIn(component, entities);
  }
  
  public  void addEnumEntity(EnumEntity component) {
    GraphicComponent result = searchAssociedComponent(component);
    if (result == null)
      addEntity(new EnumView(this, component));
  }
  
  public void addInheritance(
      Inheritance component) {
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null) {
      final EntityView child = (EntityView) searchAssociedComponent(component
          .getChild());
      final EntityView parent = (EntityView) searchAssociedComponent(component
          .getParent());
      
      // Are Components in this graphic view?
      if (child == null || parent == null)
        return;

      addComponentIn(
          new InheritanceView(this, child, parent, component,
              child.middleBounds(), parent.middleBounds(), false),
          linesView);
    }
  }
  
  public void addInnerClass(
      InnerClass component) {
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null) {
      final EntityView child = (EntityView) searchAssociedComponent(component
          .getChild());
      final EntityView parent = (EntityView) searchAssociedComponent(component
          .getParent());
      
      // Are Components in this graphic view?
      if (child == null || parent == null)
        return;

      addComponentIn(
          new InnerClassView(this, child, parent, component,
              child.middleBounds(), parent.middleBounds(), false),
          linesView);
    }
  }

  public void addInterfaceEntity(InterfaceEntity component) {
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null)
      addEntity(new InterfaceView(this, component));
  }

  /**
   * Add a new line view. Adding a component means it will be drawn and managed
   * by the graphic view.
   * 
   * @param component
   *          the line to add.
   * @return true if the component has been added; false otherwise
   */
  public boolean addLineView(LineView component) {
    return addComponentIn(component, linesView);
  }

  public boolean addListenerSelectionChanged(IListenerComponentSelectionChanged i) {
    return lcsc.add(i);
  }
  
  public void addMulti(Multi component) {
    
    
    final GraphicComponent result = searchAssociedComponent(component);

    if (result == null) {
      for (Role role : component.getRoles())
        
        // Are Components in this graphic view?
        if (searchAssociedComponent(role.getEntity()) == null)
          return;
      
      addMultiView(MultiFactory.createMulti(this, component));
    }
  }

  /**
   * Add a new multi line view. Adding a component means it will be drawn and
   * managed by the graphic view.
   * 
   * @param component
   *          the multi line to add.
   * @return true if the component has been added; false otherwise
   */
  public boolean addMultiView(MultiView component) {
    return addComponentIn(component, multiViews);
  }

  /**
   * Add a new note. Adding a component means it will be drawn and managed by
   * the graphic view.
   * 
   * @param component
   *          the note to add.
   * @return true if the component has been added; false otherwise
   */
  public boolean addNotes(TextBoxCommentary component) {
    return addComponentIn(component, notes);
  }

  /**
   * Add a new graphic component. Adding a component means it will be drawn and
   * managed by the graphic view.
   * 
   * @param component
   *          the new graphic component to add.
   * @return true if the component has been added; false otherwise
   */
  public boolean addOthersComponents(GraphicComponent component) {
    return addComponentIn(component, othersComponents);
  }

  /**
   * Get entities contents in this graphic view and adjust their width. See
   * adjustWidth() method from EntityView.
   * 
   * @param list the entities list to selected
   */
  public void adjustEntities(LinkedList<EntityView> list) {
    boolean isRecord = Change.isRecord();
    Change.record();

    list.stream().forEach(ev -> ev.adjustWidth());

    if (!isRecord) Change.stopRecord();
  }

  public void adjustInheritances() {
    for (GraphicComponent c : getSelectedComponents())
      if (c instanceof InheritanceView)
        ((InheritanceView) c).adjustInheritance();
  }

  /**
   * Get all entities contents in this graphic view and adjust their width. See
   * adjustWidth() method from EntityView.
   */
  public void adjustWidthAllEntities() {
    adjustEntities(getEntitiesView());
  }

  /**
   * Get all selected entities contents in this graphic view and adjust their
   * width. See adjustWidth() method from EntityView.
   */
  public void adjustWidthSelectedEntities(
      ) {
    adjustEntities(getSelectedEntities());
  }

  /**
   * Align all selected entities with the hightest or lowest selected entity.
   * Make same space between all selected entities.
   * 
   * @param top on top or bottom?
   */
  public void alignHorizontal(
      boolean top) {
    int totalWidth = 0, bottom = Integer.MIN_VALUE;

    final LinkedList<EntityView> sorted = sortXLocation(getSelectedEntities());

    if (sorted.size() < 2) return;

    for (final EntityView c : sorted) {
      final Rectangle bounds = c.getBounds();

      if (bounds.y > bottom) bottom = bounds.y;

      totalWidth += c.getBounds().width;
    }

    final Rectangle limits = Utility.getLimits(sorted);

    int space = (limits.width - totalWidth) / (sorted.size() - 1);

    space = space < 0 ? 10 : space;

    int offset = limits.x;

    boolean isRecord = Change.isRecord();
    Change.record();

    for (final GraphicComponent c : sorted) {
      final Rectangle bounds = c.getBounds();

      Change.push(new BufferBounds(c));
      c.setBounds(new Rectangle(offset, top ? limits.y : bottom, bounds.width,
          bounds.height));
      Change.push(new BufferBounds(c));
      offset += bounds.width + space;
    }

    if (!isRecord) Change.stopRecord();
  }

  /**
   * Align all selected entities with the leftmost or rightmost selected entity.
   * Make same space between all selected entities.
   */
  public void alignVertical(boolean left) {
    boolean isRecord = Change.isRecord();
    Change.record();

    int totalHeight = 0, right = Integer.MIN_VALUE;

    final LinkedList<EntityView> sorted = sortYLocation(getSelectedEntities());

    if (sorted.size() < 2) return;

    for (final EntityView c : sorted) {
      final Rectangle bounds = c.getBounds();

      if (bounds.x > right) right = bounds.x;

      totalHeight += c.getBounds().height;
    }

    final Rectangle limits = Utility.getLimits(sorted);

    int space = (limits.height - totalHeight) / (sorted.size() - 1);

    space = space < 10 ? 20 : space;

    int offset = limits.y;

    for (final GraphicComponent c : sorted) {
      final Rectangle bounds = c.getBounds();

      Change.push(new BufferBounds(c));
      c.setBounds(new Rectangle(left ? limits.x : right, offset, bounds.width,
          bounds.height));
      Change.push(new BufferBounds(c));
      offset += bounds.height + space;
    }

    if (!isRecord) Change.stopRecord();
  }

  public void backScale() {
    setScale(getScale() + SCALE_STEP);
  }
  
  /**
   * Get all selected component and change their color.
   */
  public void changeColorForSelectedItems() {
    SColorAssigner ca = new SColorAssigner();
    if (ca.isAccepted())
      if (ca.isDefaultColor())
        setDefaultColorForSelectedItems();
      else
        setColorForSelectedItems(ca.getColor());
  }
  
  public void changeComponentsColor(ObtainColor o, ColoredComponent... components) {
    boolean isRecord = Change.isRecord();
    Change.record();
    for (ColoredComponent c : components) {
      // Set default style before save color.
      c.setDefaultStyle();
      Change.push(new BufferColor(c));
      c.setColor(o.getColor(c));
      Change.push(new BufferColor(c));
    }
    if (!isRecord) Change.stopRecord();
  }

  /**
   * Change the size of the police. It is the responsibility of graphic
   * component to consider the new size or not. The police size is compute with
   * the actuel size * zoom. By default zoom is at 1.0f, adding a new grow will
   * add the zoom coefficient (zoom + grow).
   * 
   * @param grow
   *          new value for zoom. A grow positive will make the police size
   *          greater, a negativ grow make the police smaller.
   */
  public void changeSizeText(float grow) {
    setZoom(getZoom() + grow);
  }

  public void changeZOrder(EntityView entityView, int index) {
    
    if (index < 0 || index > entities.size() - 1)
      return;

    entities.remove(entityView);
    entities.add(index, entityView);

    entityView.repaint();
  }

  /**
   * Hide the rubber band, compute components who was in rubber band and select
   * them.
   * @return the number of components in the rubber band.
   */
  public int clearRubberBand() {
    List<GraphicComponent> components = getComponentsInRect(rubberBand);
    selectComponents(components);
    
    // Use for repaint old position
    Rectangle previousRect = new Rectangle(rubberBand);
    rubberBand = new Rectangle();
    scene.repaint(previousRect);

    return components.size();
  }
  
  /**
   * Call when a component is selected or unselected.
   * 
   * @param select
   *          a component is selected or not
   */
  public void componentSelected(boolean select) {
    for (IListenerComponentSelectionChanged i : lcsc)
      i.componentSelectionChanged();
  }

  @Override
  public Point computeAnchorLocation(Point first, Point next) {
    return next;
  }

  public Rectangle computeVisibleCenterBounds(Dimension size) {
    Rectangle rect = new Rectangle(size);
    Point center = getVisibleCenter();
    rect.x = center.x - size.width / 2;
    rect.y = center.y - size.height / 2;
    return rect;
  }
  
  /**
   * Return if the graphic component is in the graphic view or not.
   * 
   * @param component
   *          the component to find.
   * @return true if the component is in the graphic view; false otherwise
   */
  public boolean containsComponent(GraphicComponent component) {
    if (component == null)
      throw new IllegalArgumentException("component is null");
    return getAllComponents().contains(component);
  }

  public void copyDiagramToClipboard() {
    Toolkit.getDefaultToolkit().getSystemClipboard()
        .setContents(new Utility.ImageSelection(getSelectedScreen()), null);
  }
  
  public int countEntities(
      ) {
    return getEntitiesView().size();
  }
  
  public int countNotes() {
    return notes.size();
  }
  
  public int countSelectedComponents() {
    return getSelectedComponents().size();
  }

  public int countSelectedComponents(Class<?> type) {
    return Utility.count(type, getSelectedComponents());
  }
  
  public int countSelectedEntities() {
    return getSelectedEntities().size();
  }

  public int countSelectedNotes() {
    int count = 0;
    for (TextBoxCommentary note : notes)
      if (note.isSelected()) count++;
    return count;
  }

  public GraphicComponent createAndAddRelation(
      Relation relation, EntityView source, EntityView target) {
    
    if (relation.getClass() == Multi.class) {
      if (((Multi)relation).getRoles().stream().filter(r -> searchAssociedComponent(r.getEntity()) != null).count() > 2) {
        MultiView mv = new MultiView(parent, (Multi)relation);
        addMultiView(mv);
        return mv;
      }
    } else {      
      RelationView rv = RelationView.createFromRelation(
          this, relation, source, target);
      addLineView(rv);
      
      classDiagram.getEntities()
          .stream()
          .filter(r -> r instanceof AssociationClass && ((AssociationClass)r).getAssociation() == relation)
          .forEach(r -> addAssociationClass((AssociationClass)r));
      
      return rv;
    }
    
    return null;
  }
  
  /**
   * Remove the current factory.
   */
  public void deleteCurrentFactory() {
    if (currentFactory != null) {
      currentFactory.deleteFactory();
      justCreatedComponent = currentFactory.getCreatedComponent();
      justCreatedComponent = justCreatedComponent instanceof EntityView ? justCreatedComponent : null;
      currentFactory = null;
    }
    getScene().setCursor(Cursor.getDefaultCursor());
  }

  public void deleteSelectedComponents() {
    deleteComponents(getSelectedComponents());
  }
  
  /**
   * Not use in Slyum 1.0. Draw beta margin of A4 format.
   * 
   * @param g2
   *          the graphic context
   */
  public void drawA4Margin(Graphics2D g2) {
    final int screenDpi = Toolkit.getDefaultToolkit().getScreenResolution();
    float width = MediaSize.ISO.A4.getX(Size2DSyntax.INCH);
    float height = MediaSize.ISO.A4.getY(Size2DSyntax.INCH);

    width *= screenDpi;
    height *= screenDpi;

    final Rectangle limits = Utility.getLimits(entities);
    g2.drawRect(limits.x, limits.y, (int) width, (int) height);
  }
  
  public void duplicateSelectedEntities() {
    
    boolean isRecord = Change.isRecord();
    Change.record();
    
    for (final EntityView entityView : getSelectedEntities()) {
      try {
        final EntityView newView = entityView.clone();
        Entity entity = ((Entity) newView.getAssociedComponent());
        
        // Récupération par réflexion de la méthode a appelé pour l'ajout.
        // Le nom de la méthode doit être add<Type_Class>(<Type_Class> entity);
        try {
          parent.addEntity(newView);
          classDiagram
              .getClass()
              .getMethod(
                  String.format("add%s", entity.getClass()
                      .getSimpleName()), entity.getClass())
              .invoke(classDiagram, entity);
          newView.regenerateEntity();
          
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              entityView.setSelected(false);
              newView.setSelected(true);
              newView.repaint();
            }
          });
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
    }
    
    if (!isRecord) Change.stopRecord();
  }
  
  public void forwardScale() {
    setScale(getScale() - SCALE_STEP);
  }
  
  @Override
  public void gMouseDragged(MouseEvent e) {
    if (mouseButton == MouseEvent.BUTTON1)
      redrawRubberBand(mousePressedLocation, e.getPoint());
  }
  
  @Override
  public void gMousePressed(MouseEvent e) {
    super.gMousePressed(e);
    
    switch (mouseButton) {
      case MouseEvent.BUTTON1:
        scene.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        if (!isAddToSelection(e)) unselectAll();
        break;
        
      case MouseEvent.BUTTON2:
        scene.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        break;
    }
  }
  
  @Override
  public void gMouseReleased(MouseEvent e) {
    super.gMouseReleased(e);
    clearRubberBand();
    final int nbrComponentSelected = countSelectedEntities();

    if (nbrComponentSelected > 0 && e.getButton() == MouseEvent.BUTTON1)
      new StyleCross(this, e.getPoint(), nbrComponentSelected);
    
    scene.setCursor(Cursor.getDefaultCursor());
  }
  
  /**
   * Get all components contains in graphic view.
   * 
   * @return an array of all graphic component
   */
  public LinkedList<GraphicComponent> getAllComponents() {
    final LinkedList<GraphicComponent> components = getCurrentComponents();
    components.addAll(othersComponents);
    return components;
  }
  
  /**
   * Get all graphics components having a diagram component associated with.
   * @return a list of all graphic component having a diagram component.
   */
  public LinkedList<GraphicComponent> getAllComponentsAssociated() {
    LinkedList<GraphicComponent> components = new LinkedList<>();
    for (GraphicComponent gc : getAllComponents())
      if (gc.getAssociedComponent() != null)
        components.add(gc);
    return components;
  }
  
  public LinkedList<GraphicComponent> getAllDiagramComponents() {
    final LinkedList<GraphicComponent> components = getCurrentComponents();
    for (GraphicComponent o : othersComponents)
      if (!(o instanceof SquareGrip) && !o.equals(txtBoxDiagramName)) 
        components.add(o);
    return components;
  }
  
  /**
   * Return all mains components. Mains components are associations and
   * entities.
   * @return All mains components.
   */
  public LinkedList<GraphicComponent> getAllMainComponents() {
    LinkedList<GraphicComponent> mainComponents = new LinkedList<>();
    mainComponents.addAll(entities);
    mainComponents.addAll(linesView);
    mainComponents.addAll(multiViews);
    return mainComponents;
  }

  /**
   * Get all unique graphics components having a diagram component associated with.
   * A unique graphic component is a component that exist only in this view.
   * @return a list of all graphic component having a diagram component.
   */
  public LinkedList<GraphicComponent> getAllGraphicalUniqueComponent() {
    return getUniqueComponentsFrom(getAllComponentsAssociated());
  }
  
  public LinkedList<EntityView> getAllUniqueEntities() {
    return getUniqueComponentsFrom(getEntitiesView());
  }
  
  public LinkedList<GraphicComponent> getAllMainUniquesComponents() {
    return getUniqueComponentsFrom(getAllMainComponents());
  }

  @Override
  public Rectangle getBounds() {
    return scene.getBounds();
  }

  @Override
  public void setBounds(Rectangle bounds) {
    scene.setBounds(bounds);
  }
  
  /**
   * Get the class diagram associated with this graphic view.
   * 
   * @return the class diagram associated with this graphic view.
   */
  public  ClassDiagram getClassDiagram() {
    return classDiagram;
  }

  @Override
  public Color getColor() {
    return getBasicColor();
  }
  
  @Override
  public void setColor(Color color) {
    setBasicColor(color);
    
    repaint();
  }
  
  public  void setColorForSelectedItems(final Color color) {
    setColorForComponents(color, getSelectedColoredComponents());
  }
  
  public List<ColoredComponent> getColoredComponents(List<GraphicComponent> components) {
    LinkedList<ColoredComponent> c = new LinkedList<>();
    for (GraphicComponent g : components)
      if (g instanceof ColoredComponent) c.add((ColoredComponent) g);
    return c;

  }

  /**
   * Search a component in the graphic view who are at the given location.
   * Return the graphic view if no component are found.
   * 
   * @param pos
   *          the location for find a component
   * @return the component at the location; or the graphic view if no component
   *         are at this location
   */
  public GraphicComponent getComponentAtPosition(Point pos) {
    final GraphicComponent component = getComponentListAtPosition(
            getAllComponents(), pos);

    return component == null ? this : component;
  }

  /**
   * Same as searchComponentWithPosition(), but inverse the order of graphic
   * components.
   * 
   * @param list
   *          the list where find component
   * @param pos
   *          the location for find graphic components
   * @return the component found; or null if no component are found.
   */
  public  <T extends GraphicComponent> T getComponentListAtPosition(
      LinkedList<T> list, Point pos) {
    // last component first
    final Iterator<T> iter = list.descendingIterator();
    final LinkedList<T> inversed = new LinkedList<T>();

    while (iter.hasNext())
      inversed.add(iter.next());

    final T component = searchComponentWithPosition(inversed, pos);

    return component;
  }

  public GraphicComponent getComponentMouseHover(Point mouseLocation) {
    if (currentFactory != null)
      return currentFactory;
    
    return getComponentAtPosition(mouseLocation);
  }
  
  /**
   * Change the current pressed component by the component given.
   *
   * @param component
   *          the new pressed component
   */
  public void setComponentMousePressed(GraphicComponent component) {
    if (component == null)
      throw new IllegalArgumentException("component is null");
    
    componentMousePressed = component;
  }
  
  public  List<GraphicComponent> getComponentsInRect(Rectangle rect) {
    List<GraphicComponent> components = new LinkedList<>();
    
    for (GraphicComponent c : getDiagramElements()) {
      // see if the intersection area is the same that component area.
      Rectangle bounds = c.getBounds();
      Rectangle intersection = rubberBand.intersection(bounds);
      
      int intersectionArea = intersection.width * intersection.height;
      int componentArea = bounds.width * bounds.height;

      // if it the same, the component is completely covered by the
      // rubber band.
      if (intersectionArea == componentArea)
        components.add(c);
    }
    return components;
  }
  
  @Override
  public Color getDefaultColor() {
    return BASIC_COLOR;
  }

  public void setDefaultColorForComponents(ColoredComponent... components) {
    changeComponentsColor(new ObtainColor() {
      @Override
      public Color getColor(ColoredComponent c) {
        return c.getDefaultColor();
      }
    }, components);    
  }

  public void setDefaultForSelectedEntities(boolean show) {
    for (SimpleEntityView ev : SimpleEntityView
            .getSelectedSimpleEntityView(this))
      ev.setDisplayDefault(show);
  }

  /**
   * Same as getComponentListAtPosition(), but search only diagram elements
   * (include entities, relations and notes). See getDiagramElements for know
   * arrays used. This method permit to define a component we will not find.
   * 
   * @param pos
   *          the location for find diagram components
   * @param except
   *          remove this component from arrays and no longer be found
   * @return the component found or null if no component are found
   */
  public GraphicComponent getDiagramElementAtPosition(
  Point pos, GraphicComponent except) {
    final LinkedList<GraphicComponent> components = getDiagramElements();
    components.remove(except);

    return getComponentListAtPosition(components, pos);
  }

  /**
   * Get all diagram elements contains in the graphic view. Diagram elements are
   * : - Lines views - Entities views - Multi views - Notes views
   * 
   * @return an array containing all diagram elements
   */
  public LinkedList<GraphicComponent> getDiagramElements() {
    // Don't change order!!!!
    final LinkedList<GraphicComponent> components = new LinkedList<GraphicComponent>();
    components.addAll(linesView);
    components.addAll(entities);
    components.addAll(multiViews);
    components.addAll(notes);

    return components;
  }

  /**
   * Get all entities containing in graphic view.
   * 
   * @return an array containing all entities
   */
  @SuppressWarnings("unchecked")
  public LinkedList<EntityView> getEntitiesView() {
    return (LinkedList<EntityView>) entities.clone();
  }

  /**
   * Same as getComponentListAtPosition(), but only for entities.
   * 
   * @param pos
   *          the location of entity to find
   * @return the entity found or null if no entity are found
   */
  public EntityView getEntityAtPosition(Point pos) {
    return getComponentListAtPosition(entities, pos);
  }

  public double getInversedScale() {
    return 1 / getScale();
  }
  
  /**
   * Same as getComponentListAtPosition(), but only for lines views.
   *
   * @param pos
   *          the location of line view to find
   * @return the line view found or null if no line view are found
   */
  public LineView getLineViewAtPosition(Point pos) {
    return getComponentListAtPosition(linesView, pos);
  }

  /**
   * Get all lines views containing in graphic view.
   * 
   * @return an array containing all lines views
   */
  @SuppressWarnings("unchecked")
  public LinkedList<LineView> getLinesView() {
    return (LinkedList<LineView>) linesView.clone();
  }

  /**
   * Search all relations associated with the given graphic component. An
   * associated relation is an association who have at one or more extremities
   * the graphic component given.
   * 
   * @param component
   *          the component to find its relations
   * @return an array containing all relations associated with the given
   *         component
   */
  public LinkedList<LineView> getLinesViewAssociedWith(GraphicComponent component) {
    final LinkedList<LineView> list = new LinkedList<>();

    for (final LineView lv : linesView)
      if (lv.getFirstPoint().getAssociedComponentView().equals(component)
          || lv.getLastPoint().getAssociedComponentView().equals(component))
        list.add(lv);

    return list;
  }

  @SuppressWarnings("unchecked")
  public LinkedList<MultiView> getMultiView() {
    return (LinkedList<MultiView>) multiViews.clone();
  }

  /**
   * Get the name for this graphic view.
   * 
   * @return the name for this graphic view
   */
  @Override
  public String getName() {
    return (name == null || name.isEmpty() ?
            (MultiViewManager.getRootGraphicView() == this ?
             ROOT_VIEW_DEFAULT_NAME : NO_NAMED_VIEW) :
        name);
  }
  
  @Override
  public void setName(String name) {
    this.name = name;
    Change.setHasChange(true);
    setChanged();
  }
  
  public boolean getPaintBackgroundLast(
          ) {
    return paintBackgroundLast;
  }
  
  public void setPaintBackgroundLast(boolean enable) {
    paintBackgroundLast = enable;
  }

  @Override
  public void setPictureMode(boolean enable) {
    super.setPictureMode(enable);

    for (GraphicComponent c : getAllComponents())
      c.setPictureMode(enable);
  }
  
  public double getScale() {
    return SPanelElement.getInstance().getSliderZoom().getValue() / 100.0;
  }

  public void setScale(double scale) {
    SPanelElement.getInstance().getSliderZoom().setValue((int) (scale * 100.0));
  }

  /**
   * Get the scene (JPanel) where graphic view draw components.
   * 
   * @return the scene
   */
  public JPanel getScene() {
    return scene;
  }

  /**
   * Make a picture (BufferedImage) representing the scene. Call refreshAllComponents
   * before calling this method and then call it with SwingUtilites.invokeLater.
   * 
   * @param type int imageType
   * @param displayName must paint the border and the name in the top left corner.
   * @return a picture representing the scene
   */
  public BufferedImage getScreen(int type, boolean displayName) {
    
    setPictureMode(true);
    
    final int margin = 20;
    int marginTop = margin;
    
    if (displayName)
      marginTop += txtBoxDiagramName.getBounds().height;
    
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
    final LinkedList<GraphicComponent> components = getAllDiagramComponents();
    
    if (components.size() == 0)
      return null;

    // Compute the rectangle englobing all graphic components.
    for (final GraphicComponent c : components) {
      final Rectangle bounds = c.getBounds();
      final Point max = new Point(bounds.x + bounds.width, bounds.y
                                                           + bounds.height);
      
      if (minX > bounds.x) minX = bounds.x;
      if (minY > bounds.y) minY = bounds.y;
      if (maxX < max.x) maxX = max.x;
      if (maxY < max.y) maxY = max.y;
    }

    final Rectangle bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);

    // Create the buffered image with margin.
    final BufferedImage img = new BufferedImage(bounds.width + margin * 2,
        bounds.height + margin + marginTop, type);
    final Graphics2D g2 = img.createGraphics();
    Utility.setRenderQuality(g2);

    // Translate the rectangle containing all graphic components at origin.
    g2.translate(-bounds.x + margin, -bounds.y + marginTop);

    if (type == BufferedImage.TYPE_INT_RGB) {
      g2.setColor(Color.WHITE);
      g2.fillRect(bounds.x - margin, bounds.y - margin, bounds.width + margin
                                                                       * 2, bounds.height + margin + marginTop);
    }
    // Paint all components on picture.
    for (final GraphicComponent c : components)
      c.paintComponent(g2);
    
    // Paint diagram's name
    if (displayName) {
      g2.translate(-margin, -marginTop);
      txtBoxDiagramName.paintComponentAt(g2, new Point(minX, minY));

      if (isTitleBorderPainted()) {
        g2.setStroke(new BasicStroke(DEFAULT_TITLE_BORDER_WIDTH));
        g2.draw(new Rectangle2D.Float(
            minX,
            minY,
            img.getWidth() - DEFAULT_TITLE_BORDER_WIDTH,
            img.getHeight() - DEFAULT_TITLE_BORDER_WIDTH));
      }
    }
    
    // Paint diagram's informations
    if (Slyum.isDisplayedDiagramInformationOnExport()) {
      
      final int WIDTH = 200;
      final int HEIGHT = 100;
      
      Rectangle informationsRectangle = new Rectangle(
          img.getWidth() - DEFAULT_TITLE_BORDER_WIDTH - WIDTH, 
          img.getHeight() - DEFAULT_TITLE_BORDER_WIDTH - HEIGHT, 
          WIDTH, HEIGHT);
      
      g2.setStroke(new BasicStroke(DEFAULT_TITLE_BORDER_WIDTH));
      g2.setBackground(Color.WHITE);
      g2.setColor(Color.BLACK);
      
      g2.fillRect(informationsRectangle.x, 
                  informationsRectangle.y, 
                  informationsRectangle.width, 
                  informationsRectangle.height);
      
      g2.drawRect(informationsRectangle.x, 
                  informationsRectangle.y, 
                  informationsRectangle.width, 
                  informationsRectangle.height);
    }
    
    setPictureMode(false);
    return img;
  }

  /**
   * Get the scrollPane containing the scene.
   * 
   * @return
   */
  public JScrollPane getScrollPane() {
    return scrollPane;
  }

  public ColoredComponent[] getSelectedColoredComponents() {
    List<GraphicComponent> gc = getSelectedComponents();
    List<ColoredComponent> colored = getColoredComponents(gc);
    return (ColoredComponent[]) colored.toArray(new ColoredComponent[colored.size()]);
  }
  
  /**
   * Get all selected component containing in graphic view.
   * 
   * @return all selected component
   */
  public LinkedList<GraphicComponent> getSelectedComponents() {
    LinkedList<GraphicComponent> components = new LinkedList<>();
    LinkedList<GraphicComponent> selected = new LinkedList<>();

    components.addAll(entities);
    components.addAll(linesView);
    components.addAll(multiViews);
    components.addAll(notes);

    for (GraphicComponent c : components)
      if (c.isSelected()) selected.add(c);

    return selected;
  }

  /**
   * Get all selected entities containing in graphic view.
   * 
   * @return all selected entities
   */
  public LinkedList<EntityView> getSelectedEntities() {
    final LinkedList<EntityView> selectedEntities = new LinkedList<EntityView>();

    for (final GraphicComponent c : entities)
      if (c.isSelected()) selectedEntities.add((EntityView) c);

    return selectedEntities;
  }

  /**
   * Make a picture (BufferedImage) with the selected components.
   * 
   * @return a picture representing the scene
   */
  public BufferedImage getSelectedScreen() {
    final int margin = 20;
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
    final LinkedList<GraphicComponent> components = getSelectedComponents();

    // Compute the rectangle englobing all selected graphic components.
    for (final GraphicComponent c : components) {
      final Rectangle bounds = c.getBounds();
      final Point max = new Point(bounds.x + bounds.width, bounds.y
                                                           + bounds.height);
      
      if (minX > bounds.x) minX = bounds.x;
      if (minY > bounds.y) minY = bounds.y;
      if (maxX < max.x) maxX = max.x;
      if (maxY < max.y) maxY = max.y;
    }

    Rectangle bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);

    if (bounds.isEmpty()) return null;

    // Create the buffered image with margin.
    BufferedImage img = new BufferedImage(bounds.width + margin * 2,
        bounds.height + margin * 2, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    Utility.setRenderQuality(g2);

    // Translate the rectangle containing all graphic components at origin.
    g2.translate(-bounds.x + margin, -bounds.y + margin);

    setPictureMode(true);
    // Paint all components on picture.
    for (GraphicComponent c : getAllDiagramComponents())
      c.paintComponent(g2);

    setPictureMode(false);

    return img;
  }
  
  public boolean getStopRepaint() {
    return stopRepaint;
  }

  public void setStopRepaint(boolean enable) {
    stopRepaint = enable;
  }
  
  public TextBoxDiagramName getTxtBoxDiagramName() {
    return txtBoxDiagramName;
  }
  
  public Point getVisibleCenter() {
    Rectangle loc = getScene().getVisibleRect();
    return new Point((int) (loc.getCenterX() * getInversedScale()),
        (int) (loc.getCenterY() * getInversedScale()));
  }
  
  public void setVisibleDiagramName(boolean visible) {
    txtBoxDiagramName.setVisible(visible);
    txtBoxDiagramName.repaint();
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element graphicView = doc.createElement(getXmlTagName());
    graphicView.setAttribute("name", getName());
    graphicView.setAttribute("open", String.valueOf(isOpenInTab()));
    graphicView.setAttribute("grid", String.valueOf(getGridSize()));

    for (GraphicComponent c : getAllComponents()) {
      Element el = c.getXmlElement(doc);
      if (el != null && c.getClass() != MultiLineView.class)
        graphicView.appendChild(el);
    }
    
    return graphicView;
  }
  
  @Override
  public String getXmlTagName() {
    return "umlView";
  }
  
  /**
   * Get the police zoom. Use for compute police size.
   * 
   * @return the police zoom
   */
  public float getZoom() {
    return zoom;
  }
  
  /**
   * Set the font zoom. The zoom is multiplied with the font size for computing
   * new size. Default value : 1.0f.
   *
   * @param zoom
   *          the new zoom
   */
  public void setZoom(float zoom) {
    if (zoom < 0.5) return;

    this.zoom = zoom;

    // The font size can only be compute with a graphic context. We must
    // call a repainting for update the font size before redrawing the
    // scene.
    getScene().paintImmediately(0, 0, 1, 1);
    getScene().repaint();
    
    // Adjut the width for selected entities.
    adjustWidthSelectedEntities();
    
    for (final EntityView entity : getEntitiesView())
      
      entity.updateHeight();
  }

  public void goRepaint() {
    setStopRepaint(false);
    repaint();
  }

  /**
   * Redirect all mouse event to the factory given and draw the factory. A
   * factory is use for creating new graphic component. For remove a current
   * factory, calls deleteCurrentFactory().
   * 
   * @param factory
   *          the new factory
   */
  public void initNewComponent(CreateComponent factory) {
    if (factory == null) throw new IllegalArgumentException("factory is null");
    currentFactory = factory;
    scene.setCursor(factory.getCursor());
  }

  @Override
  public boolean isAtPosition(Point mouse) {
    return true;
  }
  
  public boolean isOpenInTab() {
    return MultiViewManager.isGraphicViewOpened(this);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) unselectAll();
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void keyTyped(KeyEvent e) {}

  public void linkNewNoteWithSelectedEntities() {
    setStopRepaint(true);
    LinkedList<GraphicComponent> e = getSelectedComponents();
    TextBoxCommentary tbc;

    boolean isRecord = Change.isRecord();
    Change.record();

    if (e.isEmpty()) {
      tbc = new TextBoxCommentary(
          parent, TextBoxCommentary.DEFAULT_TEXT, this);
    } else {
      tbc = new TextBoxCommentary(
          parent, TextBoxCommentary.DEFAULT_TEXT, e.getFirst());
      
      e.remove(0);
      for (GraphicComponent ev : e)
        if (LineCommentary.checkCreate(ev, tbc, false))
          parent.addLineView(new LineCommentary(parent, ev, tbc, new Point(),
              new Point(), false));
    }
    
    tbc.setBounds(computeVisibleCenterBounds(
        new Dimension(tbc.getBounds().width, tbc.getBounds().height)));
    parent.addNotes(tbc);
    
    if (!isRecord) 
      Change.stopRecord();
    goRepaint();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    e = adapteMouseEvent(e);
    
    GraphicComponent component;
    if (justCreatedComponent != null) {
      component = justCreatedComponent;
      justCreatedComponent = null;
    } else {
      component = getComponentMouseHover(e.getPoint());
    }

    if (mouseButton != MouseEvent.BUTTON2 || component == this)
      component.gMouseClicked(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    MouseEvent ea = adapteMouseEvent(e);
    GraphicComponent component;
    Rectangle newVisibleRect;

    if (mouseButton == MouseEvent.BUTTON2) {
      int dx = (int) (mousePressedLocation.x * getScale() - e.getX()), dy = (int) (mousePressedLocation.y
                                                                                   * getScale() - e.getY());
      
      visibleRect.translate(dx, dy);

      newVisibleRect = new Rectangle(visibleRect);
    } else
      newVisibleRect = new Rectangle((int) ((double) ea.getX() * getScale()),
          (int) ((double) ea.getY() * getScale()), 1, 1);
    
    if (currentFactory != null)
      component = currentFactory;
    else
      component = getComponentAtPosition(ea.getPoint());

    computeComponentEventEnter(component, saveComponentMouseHover, ea);

    if (componentMousePressed!= null && mouseButton != MouseEvent.BUTTON2)
      componentMousePressed.gMouseDragged(ea);

    saveComponentMouseHover = component;

    getScene().scrollRectToVisible(newVisibleRect);
    visibleRect = getScene().getVisibleRect();
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // this event, in Slyum, is called manually when mouseMove is called.
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // this event, in Slyum, is called manually when mouseMove is called.
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    e = adapteMouseEvent(e);
    GraphicComponent component;

    if (currentFactory != null)
      component = currentFactory;
    else
      component = getComponentAtPosition(e.getPoint());

    // Compute mouseEntered and mouseExited event.
    computeComponentEventEnter(component, saveComponentMouseHover, e);

    component.gMouseMoved(e);

    // Save the last component mouse hovered. Useful for compute
    // mouseEntered and mouseExited event.
    saveComponentMouseHover = component;
  }

  @Override
  public void mousePressed(MouseEvent e) {
    scene.requestFocusInWindow(); // get the focus
    mouseButton = e.getButton();

    e = adapteMouseEvent(e);
    mousePressedLocation = e.getPoint();
    visibleRect = getScene().getVisibleRect();
    mousePressedLocation = e.getPoint();

    // Save the last component mouse pressed.
    componentMousePressed = getComponentMouseHover(e.getPoint());

    if (e.getButton() != MouseEvent.BUTTON2 || componentMousePressed == this)
      componentMousePressed.gMousePressed(e);
    else
      unselectAll();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    e = adapteMouseEvent(e);

    if (componentMousePressed != null && mouseButton != MouseEvent.BUTTON2)
      componentMousePressed.gMouseReleased(e);

    getScene().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  @Override
  public void mouseWheelMoved(final MouseWheelEvent e) {
    if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

      boolean ctrlDown = OSValidator.IS_MAC ? e.isMetaDown() : e.isControlDown();
      if (ctrlDown) {
        if (e.getWheelRotation() < 0) {
          backScale();
        } else {
          forwardScale();
        }
      } else {
        scrollPane.dispatchEvent(e);
      }
    }
  }
  
  public void moveZOrderBottomSelectedEntities() {
    boolean isRecord = Change.isRecord();
    Change.record();
    
    getSelectedEntities().stream().forEach(ev -> changeZOrder(ev, 0));
    
    if (!isRecord) Change.stopRecord();
  }
  
  public void moveZOrderDownSelectedEntities() {
    boolean isRecord = Change.isRecord();
    Change.record();
    
    getSelectedEntities().stream().forEach(
        ev -> changeZOrder(ev, getEntitiesView().indexOf(ev) - 1));
    
    if (!isRecord) Change.stopRecord();
  }
  
  public void moveZOrderTopSelectedEntities() {
    boolean isRecord = Change.isRecord();
    Change.record();
    
    int lastIndex = getEntitiesView().size() - 1;
    getSelectedEntities().stream().forEach(ev -> changeZOrder(ev, lastIndex));
    
    if (!isRecord) Change.stopRecord();
  }
  
  public void moveZOrderUpSelectedEntities() {
    LinkedList<EntityView> evs = getSelectedEntities();
    LinkedList<EntityView> evsSorted = new LinkedList<>();
    int current = 0, max = -1, size = evs.size();

    for (int i = 0; i < size; i++) {
      for (EntityView ev : evs) {
        int io = getEntitiesView().indexOf(ev);

        if (io > max) {
          max = io;
          current = evs.indexOf(ev);
        }
      }
      evsSorted.add(evs.remove(current));
      current = 0;
      max = -1;
    }

    boolean isRecord = Change.isRecord();
    Change.record();

    getSelectedEntities().stream().forEach(
        ev -> changeZOrder(ev, getEntitiesView().indexOf(ev) + 1));

    if (!isRecord) Change.stopRecord();
  }
  
  @Override
  public void notifyAggregationCreation(Aggregation component) {
    addAggregation(component);
  }
  
  @Override
  public void notifyAssociationClassCreation(AssociationClass component) {
    addAssociationClass(component);
  }

  @Override
  public void notifyBinaryCreation(Binary component) {
    addBinary(component);
  }
  
  @Override
  public void notifyClassEntityCreation(ClassEntity component) {
    if (MultiViewManager.getSelectedGraphicView() == this ||
        PanelClassDiagram.getInstance().isXmlImportation())
      addClassEntity(component);
  }

  @Override
  public void notifyCompositionCreation(Composition component) {
    addComposition(component);
  }
  
  @Override
  public void notifyDependencyCreation(Dependency component) {
    addDependency(component);
  }

  @Override
  public void notifyEnumEntityCreation(EnumEntity component) {
    if (MultiViewManager.getSelectedGraphicView() == this ||
        PanelClassDiagram.getInstance().isXmlImportation())
    addEnumEntity(component);
  }

  @Override
  public void notifyInheritanceCreation(Inheritance component) {
    addInheritance(component);
  }
  
  @Override
  public void notifyInnerClassCreation(InnerClass component) {
    addInnerClass(component);
  }
  
  @Override
  public void notifyInterfaceEntityCreation(InterfaceEntity component) {
    if (MultiViewManager.getSelectedGraphicView() == this ||
        PanelClassDiagram.getInstance().isXmlImportation())
    addInterfaceEntity(component);
  }
  
  @Override
  public void notifyMultiCreation(Multi component) {
    addMulti(component);
  }
  
  @Override
  public void notifyRemoveComponent(IDiagramComponent component) {
    final GraphicComponent g = searchAssociedComponent(component);
    if (g != null)
      g.delete();
  }
  
  public void paintBackgroundFirst() {
    setPaintBackgroundLast(false);
    repaint();
  }
  
  @Override
  public void paintComponent(Graphics2D g2) {
    paintScene(g2);
  }

  /**
   * Paint the scene. Same has paintComponent, but different name because
   * paintComponent is already use by the parent GraphicComponent.
   * 
   * @param g2
   *          the graphic context
   */
  public void paintScene(Graphics2D g2) {
    int gridSize = getGridSize();

    // Paint background.
    paintBackground(gridSize, getBasicColor(), g2);

    if (!isVisible()) return;

    Utility.setRenderQuality(g2);

    double scale = getScale(), inversedScale = getInversedScale();
    g2.scale(scale, scale);

    // Paint components
    for (GraphicComponent c : getAllComponents())
      c.paintComponent(g2);

    for (GraphicComponent c : getSelectedComponents())
      c.drawSelectedEffect(g2);

    if (currentFactory != null) currentFactory.paintComponent(g2);

    // Paint rubberBand
    final int grayLevel = Utility.getColorGrayLevel(getColor());
    final Color rubberBandColor = new Color(grayLevel, grayLevel, grayLevel);

    paintRubberBand(rubberBand, isAutomatiqueGridColor() ? rubberBandColor
                                : new Color(getGridColor()), g2);
    
    g2.scale(inversedScale, inversedScale);

    if (getPaintBackgroundLast())
      paintBackground(gridSize, getBasicColor(), g2);
  }

  @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
    
    if (pageIndex >= MAX_PRINT_PAGE)
      return NO_SUCH_PAGE;
    
    Utility.setRenderQuality(g);
    
    BufferedImage m_bi = ExportViewImage.create(this).export();
    int wPage = (int) pageFormat.getImageableWidth(),
    hPage = (int) pageFormat.getImageableHeight(),
    wBi = m_bi.getWidth(),
    hBi = m_bi.getHeight(),
        wScaled = wBi,
        hScaled = hBi;
    
    // Compute the ratio for scaling the scene into one page.
    float wScale = (float)wBi / wPage,
    hScale = (float)hBi / hPage;
    
    // If the scene overflow the page, scale it for not overbounding the page.
    if (wScale > 1.0f || hScale > 1.0f)
      if (wScale > hScale) {
        wScaled = wPage;
        hScaled /= wScale;
      } else  {
        hScaled = hPage;
        wScaled /= hScale;
      }

    g.translate((int) pageFormat.getImageableX(),
                 (int) pageFormat.getImageableY());

    g.drawImage(m_bi, 0, 0, wScaled, hScaled, getScene());

    System.gc();

    return PAGE_EXISTS;
  }

  /**
   * Notify obsevers of all components. This will replace and recalculate all
   * component's location to fit at their correct location.
   */
  public void refreshAllComponents() {
    for (GraphicComponent c : getAllComponents())
      c.notifyObservers();
  }

  /**
   * Remove all components in graphic view.
   */
  public void removeAll() {
    for (final GraphicComponent c : getAllComponents())
      c.delete();
    Change.clear();
  }
  
  public void removeComponent(IDiagramComponent component) {
    final GraphicComponent g = searchAssociedComponent(component);
    
    if (g != null)
      removeComponent(g);
  }

  /**
   * Remove the given component from graphic view. Remove the associed UML
   * component too (if exists).
   * 
   * @param component
   *          the component to remove
   * @return true if the component has been removed; false otherwise.
   */
  public boolean removeComponent(GraphicComponent component) {
    if (component == null)
      throw new IllegalArgumentException("component is null");

    boolean success = false;

    // Remove the component, don't know where it is, test all arrays.
    success |= entities.remove(component);
    success |= linesView.remove(component);
    success |= othersComponents.remove(component);
    success |= multiViews.remove(component);
    success |= notes.remove(component);

    if (success)
      component.repaint();
    
    return success;
  }

  public boolean removeListenerSelectionChanged(IListenerComponentSelectionChanged i) {
    return lcsc.remove(i);
  }
  
  @Override
  public void repaint() {
    scene.repaint();
  }

  /**
   * Search a graphic component associated with the object given. Some graphic
   * component have an UML component associated. Return null if no component are
   * associated.
   * 
   * @param search
   *          the object associated with a graphic component
   * @return the graphic component associated with the given object; or null if
   *         no graphic component are found
   */
  public GraphicComponent searchAssociedComponent(Object search) {
    if (search == null) return null;

    for (final GraphicComponent c : getAllComponents())
      if (c.getAssociedComponent() == search) return c;

    return null;
  }

  /**
   * Select all diagram elements.
   */
  public void selectAll() {
    selectComponents(getDiagramElements());
  }
  
  public void selectComponents(List<GraphicComponent> components) {
    PanelClassDiagram.getInstance().setDisabledUpdate(true);
    for (GraphicComponent c : components)
      c.setSelected(true);
    PanelClassDiagram.getInstance().setDisabledUpdate(false);
  }

  /**
   * Select the given component and unselect all other.
   *
   * @param gc
   *          the component to selected
   */
  public void selectOnly(GraphicComponent gc) {
    unselectAll();
    gc.setSelected(true);
    gc.notifyObservers();
  }
  
  public void setColorForComponents(final Color color, ColoredComponent... components) {
    changeComponentsColor(new ObtainColor() {
      @Override
      public Color getColor(ColoredComponent c) {
        return color;
      }
    }, components);
  }
  
  public void setDefaultColorForSelectedItems() {
    setDefaultColorForComponents(getSelectedColoredComponents());
  }

  /**
   * Get all selected entities and display or hide their attributes.
   * 
   * @param show
   *          true for showing attributes, false otherwise
   */
  public void showAttributsForSelectedEntity(boolean show) {
    for (SimpleEntityView ev : SimpleEntityView
        .getSelectedSimpleEntityView(this))
      ev.setDisplayAttributes(show);
  }

  /**
   * Get all selected entities and display or hide their methods.
   * 
   * @param show
   *          true for showing methods, false otherwise
   */
  public void showMethodsForSelectedEntity(boolean show) {
    for (SimpleEntityView ev : SimpleEntityView
            .getSelectedSimpleEntityView(this))
      ev.setDisplayMethods(show);
  }

  /**
   * Sort the given list by x location. The first element of the list will be
   * the leftmost component in graphicview.
   * 
   * @param list
   *          the lost to sort
   * @return the list sorted
   */
  public LinkedList<EntityView> sortXLocation(LinkedList<EntityView> list) {
    @SuppressWarnings("unchecked")
    final LinkedList<EntityView> cpyList = (LinkedList<EntityView>) list
        .clone();
    final LinkedList<EntityView> sorted = new LinkedList<EntityView>();

    // sort list from x location
    while (cpyList.size() > 0) {
      int i = 0, left = Integer.MAX_VALUE;

      for (int j = 0; j < cpyList.size(); j++) {
        final Rectangle current = cpyList.get(j).getBounds();

        if (left > current.x) {
          left = current.x;
          i = j;
        }
      }

      sorted.addLast(cpyList.get(i));
      cpyList.remove(i);
    }

    return sorted;
  }

  /**
   * Sort the given list by y location. The first element of the list will be
   * the hightest component in graphicview.
   * 
   * @param list
   *          the lost to sort
   * @return the list sorted
   */
  public LinkedList<EntityView> sortYLocation(LinkedList<EntityView> list) {
    @SuppressWarnings("unchecked")
    final LinkedList<EntityView> cpyList = (LinkedList<EntityView>) list
        .clone();
    final LinkedList<EntityView> sorted = new LinkedList<EntityView>();

    // sort list from y location
    while (cpyList.size() > 0) {
      int i = 0, top = Integer.MAX_VALUE;

      for (int j = 0; j < cpyList.size(); j++) {
        final Rectangle current = cpyList.get(j).getBounds();

        if (top > current.y) {
          top = current.y;
          i = j;
        }
      }

      sorted.addLast(cpyList.get(i));
      cpyList.remove(i);
    }

    return sorted;
  }
  
  /**
   * Unselect all component.
   */
  public void unselectAll() {
    LinkedList<GraphicComponent> components = getAllComponents();
    for (GraphicComponent c : components)
      c.setSelected(false);
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o.getClass() == ClassDiagram.class) {
      if (arg != null && arg instanceof Boolean && (Boolean)arg)
        for (SimpleEntityView entity : SimpleEntityView.getAll())
          entity.initViewType();
      repaint();
    }
  }
  
  public void regenerateAllEntities() {
    for (EntityView ev : getEntitiesView())
      ev.regenerateEntity();
  }

  /**
   * Compute a new preferred size for the scrollPane. Calls when graphic
   * component is resized or moved.
   */
  public void updatePreferredSize() {
    final Rectangle r = Utility.getLimits(getAllComponents());

    // Add margin.
    final int width = (int) ((double) (r.x + r.width + 1000) * getScale());
    final int height = (int) ((double) (r.y + r.height + 1000) * getScale());

    getScene().setPreferredSize(new Dimension(width, height));
    getScene().revalidate();
  }
  
  protected MouseEvent adapteMouseEvent(MouseEvent e) {
    return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(),
        e.getModifiersEx(), (int) (e.getX() * getInversedScale()),
        (int) (e.getY() * getInversedScale()), e.getXOnScreen(),
        e.getYOnScreen(), e.getClickCount(), e.isPopupTrigger(),
        e.getButton());
  }
  
  @Override
  protected boolean displayGeneralMenuItems() {
    return false;
  }
  
  /**
   * Paint the background with the specified color. A point grid is drawing with
   * space specified by gridSize.
   *
   * @param gridSize
   *          space between point. 0 for no grid.
   * @param color
   *          the background-color.
   */
  protected void paintBackground(int gridSize, Color color, Graphics2D g2) {
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_OFF);
    
    Rectangle vr = getScene().getVisibleRect();
    boolean gradient = isBackgroundGradient();
    
    // Paint a gradient from top to bottom.
    if (gradient)
      g2.setPaint(new GradientPaint(0, 0, color, 0, scene.getHeight(), color
              .brighter()));
    else
      g2.setColor(color);

    g2.fillRect(vr.x, vr.y, vr.width, vr.height);

    // Draw grid
    if (isVisible() && isGridEnable() && isGridVisible() && getGridSize() >= 10) {

      final int grayLevel = Utility.getColorGrayLevel(getColor());
      Color gridColor = new Color(getGridColor());
      
      if (isAutomatiqueGridColor())
        gridColor = new Color(grayLevel, grayLevel, grayLevel, getGridOpacity());
      else
        gridColor = new Color(gridColor.getRed(), gridColor.getGreen(),
            gridColor.getBlue(), getGridOpacity());
      
      g2.setColor(gridColor);
      
      double gridSizeScale = (double) gridSize * getScale();
      
      for (double x = (int) (vr.x / gridSizeScale) * gridSizeScale; x < vr.x
                                                                        + vr.width + gridSizeScale; x += gridSizeScale)
        for (double y = (int) (vr.y / gridSizeScale) * gridSizeScale; y < vr.y
                + vr.height + gridSizeScale; y += gridSizeScale)
          g2.draw(new Line2D.Double(x, y, x, y));
    }
  }

  /**
   * Paint the rubber band for selected multiple items.
   *
   * @param rubberBand
   *          the bounds of the rubber band
   * @param color
   *          the color of the rubber band
   * @param g2
   *          the graphic context
   */
  protected void paintRubberBand(Rectangle rubberBand, Color color, Graphics2D g2) {
    final Color transparentColor = new Color(color.getRed(), color.getGreen(),
        color.getBlue(), 50);
    
    final BasicStroke dashed = new BasicStroke(1.2f, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER);
    
    g2.setStroke(dashed);
    g2.setColor(transparentColor);
    g2.fillRect(rubberBand.x, rubberBand.y, rubberBand.width, rubberBand.height);
    
    g2.setColor(color);
    g2.drawRect(rubberBand.x, rubberBand.y, rubberBand.width, rubberBand.height);
  }
  
  /**
   * Clear and repaint the rubber band with the new bounds compute from Points
   * given in parameters.
   *
   * @param origin
   *          where the user clicked for make a rubber band
   * @param mouse
   *          the current mouse location
   */
  protected void redrawRubberBand(Point origin, Point mouse) {
    // Save previous bounds for redrawing.
    final Rectangle repaintRect = new Rectangle(rubberBand);
    
    rubberBand = new Rectangle(origin.x, origin.y, mouse.x - origin.x, mouse.y
                                                                       - origin.y);
    
    rubberBand = Utility.normalizeRect(rubberBand);
    
    scene.repaint(repaintRect);
    scene.repaint(new Rectangle(rubberBand));
  }
  
  
  public boolean createEntityWithRelations(Entity entity, Point location) {
    
    return addEntityWithRelations(
        EntityView.createFromEntity(GraphicView.this, entity), 
        location);
  }
  
  public boolean createEntityWithRelations(Entity entity)
  {
    Point loc = getScene().getVisibleRect().getLocation();
    loc.translate(100, 100);
    return createEntityWithRelations(entity, loc);
  }
  
  public boolean addEntityWithRelations(final EntityView entityView, final Point location) {
    
    if (containsDiagramComponent(entityView.getAssociedComponent()))
    {
      SMessageDialog.showErrorMessage(
          "The entity " + ((Entity)entityView.getAssociedComponent()).getName() + 
              " is already present in this view.\nYou can only add it once.");
      return false;
    }
    
    final List<RelationView> createdRelationViews = new LinkedList<>();
    
    addEntity(entityView);
    
    Entity entity1 = (Entity)entityView.getAssociedComponent();
    HashMap<Relation, Entity> linked = entity1.getLinkedEntities();
    for (Relation relation : linked.keySet()) {
      
      // Is already in the GraphicView?
      if (searchAssociedComponent(relation) != null)
        continue;
      
      Entity entity2 = linked.get(relation);
      if (containsDiagramComponent(entity2) || relation instanceof Multi) {
        EntityView source = null, target = null, entityView2 = (EntityView)searchAssociedComponent(entity2);
        
        if (!(relation instanceof Multi))
          if (relation.getSource() == entity1) {
            source = entityView;
            target = entityView2;
          } else {
            source = entityView2;
            target = entityView;
          }
        
        GraphicComponent gc = createAndAddRelation(relation, source, target);
        
        if (gc!= null && gc instanceof RelationView)
          createdRelationViews.add((RelationView)gc);
      }
    }
    
    // Place the component corresponding to the mouse location drop.
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        boolean isBlocked = Change.isBlocked();
        Change.setBlocked(true);
        
        entityView.setLocationRelativeTo(location);
        entityView.regenerateEntity();
        entityView.adjustWidth();
        
        createdRelationViews.stream().forEach(rv -> rv.center());
        
        Change.setBlocked(isBlocked);
      }
    });
    
    PanelClassDiagram.refreshHierarchicalView();
    
    return true;
  }

  private void addSPanelListener() {
    addListenerSelectionChanged(SPanelElement.getInstance());
  }

  private void applyBorderTitleToImage(BufferedImage img) {
    Graphics2D g2 = img.createGraphics();
    g2.drawRect(0, 0, img.getWidth(), img.getHeight());
  }

  private boolean containsDiagramComponent(IDiagramComponent diagramComponent) {
    return searchAssociedComponent(diagramComponent) != null;
  }
    
  private LinkedList<GraphicComponent> getCurrentComponents()
  {
    final LinkedList<GraphicComponent> components = new LinkedList<>();

    // Order is important, it is used to compute mouse event.
    components.addAll(linesView);
    components.addAll(multiViews);
    components.addAll(entities);
    components.addAll(notes);

    return components;
  }

  private <T extends GraphicComponent> LinkedList<T> getUniqueComponentsFrom(LinkedList<T> components) {
    LinkedList<T> results = new LinkedList<>();
    for (GraphicComponent gc : components)
      if (!gc.existsInOthersViews())
        results.add((T) gc);
    return results;
  }
  
  public interface ObtainColor {
    public Color getColor(ColoredComponent c);
  }
}
