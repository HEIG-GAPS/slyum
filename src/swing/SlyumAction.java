package swing;

import classDiagram.components.Attribute;
import classDiagram.components.Method;
import classDiagram.components.SimpleEntity;
import classDiagram.relationships.Inheritance;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.ClassView;
import graphic.factory.AggregationFactory;
import graphic.factory.AssociationClassFactory;
import graphic.factory.BinaryFactory;
import graphic.factory.ClassFactory;
import graphic.factory.CompositionFactory;
import graphic.factory.DependencyFactory;
import graphic.factory.EnumFactory;
import graphic.factory.InheritanceFactory;
import graphic.factory.InnerClassFactory;
import graphic.factory.InterfaceFactory;
import graphic.factory.LineCommentaryFactory;
import graphic.factory.MultiFactory;
import graphic.factory.NoteFactory;
import graphic.relations.InheritanceView;
import graphic.textbox.TextBox;
import graphic.textbox.TextBoxAttribute;
import graphic.textbox.TextBoxMethod;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import swing.Slyum.JMenuItemHistory;
import update.UpdateInfo;
import utility.OSValidator;
import utility.PersonalizedIcon;
import utility.SMessageDialog;

/**
 * This class store all actions used in Slyum. 
 * All actions listener in Slyum MUST go through this class. For maintening
 * reasons, constructor is private so all action have to be created in this
 * file.
 * To create a new action in Slyum, follow these steps:
 *  - Add a static value SlyumAction representing the new action.
 *  - Add the action corresponding to the actionCommand.
 * @author David Miserez
 */
public abstract class SlyumAction extends AbstractAction {
  
  private static String DEFAULT_ICON_EXTENSION = ".png";
  
  /* ------------------------ STATIC -----------------------------------------*/
  
  // Menu File -----------------------------------------------------------------
  public static final SlyumAction ACTION_NEW_PROJECT = 
      new SlyumAction(
          "New Project", "new", KeyEvent.VK_N, 
          KeyStroke.getKeyStroke(KeyEvent.VK_N, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().newProject();
    }
  };
  
  public static final SlyumAction ACTION_OPEN_PROJECT = 
      new SlyumAction(
          "Open Project...", "open", KeyEvent.VK_O, 
          KeyStroke.getKeyStroke(KeyEvent.VK_O, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().openFromXML();
    }
  };
  
  public static final SlyumAction ACTION_SAVE_PROJECT = 
      new SlyumAction(
          "Save", "save", KeyEvent.VK_S, 
          KeyStroke.getKeyStroke(KeyEvent.VK_S, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().saveToXML(false);
    }
  };
  
  public static final SlyumAction ACTION_SAVE_AS_PROJECT = 
      new SlyumAction(
          "Save as...", "save-as", KeyEvent.VK_A, 
          KeyStroke.getKeyStroke(KeyEvent.VK_S, Slyum.MENU_SHORTCUT_KEY_MASK | 
                                                InputEvent.SHIFT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().saveToXML(true);
    }
  };
  
  public static final SlyumAction ACTION_EXPORT_AS_IMAGE = 
      new SlyumAction(
          "Export as image...", "export", KeyEvent.VK_E, 
          KeyStroke.getKeyStroke(KeyEvent.VK_E, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().exportAsImage();
    }
  };
  
  public static final SlyumAction ACTION_COPY_TO_CLIPBOARD = 
      new SlyumAction(
          "Copy selection to clipboard", "klipper", KeyEvent.VK_C, 
          KeyStroke.getKeyStroke(KeyEvent.VK_C, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                                     .copyDiagramToClipboard();
    }
  };
  
  public static final SlyumAction ACTION_LOCATE_IN = 
      new SlyumAction(
          "Locate in" + (OSValidator.IS_MAC ? "Finder" : "Explorer"), "explore",
          KeyEvent.VK_L, 
          KeyStroke.getKeyStroke(KeyEvent.VK_L, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
          Desktop.getDesktop().open(
              PanelClassDiagram.getFileOpen().getParentFile());
        } catch (IOException exception) {
          SMessageDialog.showErrorMessage("No open file!");
        }
    }
    { setEnabled(false); }};
  
  public static final SlyumAction ACTION_PRINT = 
      new SlyumAction(
          "Print...", "print", KeyEvent.VK_P, 
          KeyStroke.getKeyStroke(KeyEvent.VK_P, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().initPrinting();
    }
  };
  
  public static final SlyumAction ACTION_PAGE_SETUP = 
      new SlyumAction(
          "Page Setup...", "page-setup", KeyEvent.VK_G) {
    @Override
    public void actionPerformed(ActionEvent e) {
        SlyumPrinterJob.pageDialog(
                PanelClassDiagram.getInstance().getCurrentGraphicView());
    }
  };
  
  public static final SlyumAction ACTION_OPEN_RECENT_PROJECT = 
      new SlyumAction("project file") {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JMenuItemHistory)
        PanelClassDiagram.getInstance().openFromXmlAndAsk(
                new File(((JMenuItemHistory) e.getSource()).getHistoryPath()
                        .toString()));
      else
        SMessageDialog.showErrorMessage(
            "An error occured while opening project. Please report.");
    }
  };
  
  public static final SlyumAction ACTION_PROPERTIES = 
      new SlyumAction(
          "Properties...", "properties", KeyEvent.VK_R, 
          KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      Slyum.getInstance().openProperties();
    }
  };
  
  public static final SlyumAction ACTION_EXIT = 
      new SlyumAction(
          "Exit", "exit", KeyEvent.VK_X, 
          KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      Slyum.getInstance().exit();
    }
  };
  
  // Menu Edit -----------------------------------------------------------------
  public static final SlyumAction ACTION_UNDO = 
      new SlyumAction(
          "Undo", "undo", KeyEvent.VK_U, 
          KeyStroke.getKeyStroke(KeyEvent.VK_Z, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      change.Change.undo();
    }
  };
  
  public static final SlyumAction ACTION_REDO = 
      new SlyumAction(
          "Redo", "redo", KeyEvent.VK_R, 
          KeyStroke.getKeyStroke(KeyEvent.VK_Y, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      change.Change.redo();
    }
  };
  
  public static final SlyumAction ACTION_COLOR = 
      new SlyumAction(
          "Color", "color", KeyEvent.VK_C, 
          KeyStroke.getKeyStroke(KeyEvent.VK_L, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicComponent.askNewColorForSelectedItems();
    }
  };
  
  public static final SlyumAction ACTION_DUPLICATE = 
      new SlyumAction(
          "Duplicate", "duplicate", KeyEvent.VK_D, 
          KeyStroke.getKeyStroke(KeyEvent.VK_D, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .duplicateSelectedEntities();
    }
  };
  
  public static final SlyumAction ACTION_DELETE = 
      new SlyumAction(
          "Delete", "delete", KeyEvent.VK_E, 
          KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .deleteSelectedComponents();
    }
  };
  
  public static final SlyumAction ACTION_SELECT_ALL = 
      new SlyumAction(
          "Select all", "select", KeyEvent.VK_S, 
          KeyStroke.getKeyStroke(KeyEvent.VK_A, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView().selectAll();
    }
  };
  
  public static final SlyumAction ACTION_UNSELECT_ALL = 
      new SlyumAction(
          "Unselect all", "unselect", KeyEvent.VK_N, 
          KeyStroke.getKeyStroke(KeyEvent.VK_U, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView().unselectAll();
    }
  };
  
  public static final SlyumAction ACTION_ADJUST_SIZE = 
      new SlyumAction(
          "Adjust Classes Width", "adjustWidth", KeyEvent.VK_W, 
          KeyStroke.getKeyStroke(KeyEvent.VK_1, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .adjustWidthSelectedEntities();
    }
  };
  
  public static final SlyumAction ACTION_ALIGN_TOP = 
      new SlyumAction(
          "Align top", "alignTop", KeyEvent.VK_O, 
          KeyStroke.getKeyStroke(KeyEvent.VK_UP, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .alignHorizontal(true);
    }
  };
  
  public static final SlyumAction ACTION_ALIGN_BOTTOM = 
      new SlyumAction(
          "Align Bottom", "alignBottom", KeyEvent.VK_O, 
          KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .alignHorizontal(false);
    }
  };
  
  public static final SlyumAction ACTION_ALIGN_LEFT = 
      new SlyumAction(
          "Align Left", "alignLeft", KeyEvent.VK_F, 
          KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .alignVertical(true);
    }
  };
  
  public static final SlyumAction ACTION_ALIGN_RIGHT = 
      new SlyumAction(
          "Align Right", "alignRight", KeyEvent.VK_H, 
          KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .alignVertical(false);
    }
  };
  
  public static final SlyumAction ACTION_MOVE_TOP = 
      new SlyumAction(
          "Move Top", "top", KeyEvent.VK_T, 
          KeyStroke.getKeyStroke(KeyEvent.VK_UP, Slyum.MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .moveZOrderTopSelectedEntities();
    }
  };
  
  public static final SlyumAction ACTION_MOVE_UP = 
      new SlyumAction(
          "Move Up", "up", KeyEvent.VK_P, 
          KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Slyum.MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .moveZOrderUpSelectedEntities();
    }
  };
  
  public static final SlyumAction ACTION_MOVE_DOWN = 
      new SlyumAction(
          "Move Down", "down", KeyEvent.VK_D, 
          KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Slyum.MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .moveZOrderDownSelectedEntities();
    }
  };
  
  public static final SlyumAction ACTION_MOVE_BOTTOM = 
      new SlyumAction(
          "Move Bottom", "bottom", KeyEvent.VK_M, 
          KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Slyum.MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .moveZOrderBottomSelectedEntities();
    }
  };

  // Menu Diagram --------------------------------------------------------------
  public static final SlyumAction ACTION_ZOOM_IN = 
      new SlyumAction(
          "Zoom in", "zoomPlus", KeyEvent.VK_I, 
          KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView().backScale();
    }
  };
  
  public static final SlyumAction ACTION_ZOOM_OUT = 
      new SlyumAction(
          "Zoom out", "zoomMinus", KeyEvent.VK_O, 
          KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView().forwardScale();
    }
  };
  
  public static final SlyumAction ACTION_ZOOM_ADAPT = 
      new SlyumAction(
          "Adapt Diagram", "zoomAdapt", KeyEvent.VK_D, 
          KeyStroke.getKeyStroke(KeyEvent.VK_Z, Slyum.MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .adaptDiagramToWindow();
    }
  };
  
  public static final SlyumAction ACTION_ZOOM_ADAPT_SELECTION = 
      new SlyumAction(
          "Adapt Selection To Window", "", KeyEvent.VK_S) {
    @Override
    public void actionPerformed(ActionEvent e) {
        PanelClassDiagram.getInstance().getCurrentGraphicView()
                         .adaptSelectionToWindow();
    }
  };
  
  public static final SlyumAction ACTION_ZOOM_1 = 
      new SlyumAction(
          "1:1 (100%)", "zoom1", 0, 
          KeyStroke.getKeyStroke(KeyEvent.VK_1, Slyum.MENU_SHORTCUT_KEY_MASK | InputEvent.SHIFT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
        PanelClassDiagram.getInstance().getCurrentGraphicView().setScale(1.0);
    }
  };
  
  public static final SlyumAction ACTION_ZOOM_0_5 = 
      new SlyumAction("1:2 (50%)") {
    @Override
    public void actionPerformed(ActionEvent e) {
        PanelClassDiagram.getInstance().getCurrentGraphicView().setScale(0.5);
    }
  };
  
  public static final SlyumAction ACTION_ZOOM_1_5 = 
      new SlyumAction("3:2 (150%)") {
    @Override
    public void actionPerformed(ActionEvent e) {
        PanelClassDiagram.getInstance().getCurrentGraphicView().setScale(1.5);
    }
  };
  
  public static final SlyumAction ACTION_ZOOM_2 = 
      new SlyumAction("2:1 (200%)") {
    @Override
    public void actionPerformed(ActionEvent e) {
        PanelClassDiagram.getInstance().getCurrentGraphicView().setScale(2.0);
    }
  };
  
  public static final SlyumAction ACTION_MODE_CURSOR = 
      new SlyumAction(
          "Default cursor", "pointer-arrow", KeyEvent.VK_A, 
          KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      SPanelDiagramComponent.getInstance()
                            .setMode(SPanelDiagramComponent.Mode.CURSOR);
    }
  };
  
  public static final SlyumAction ACTION_ADD_GRIPS = 
      new SlyumAction(
          "Add Grips", "pointer-grip", KeyEvent.VK_G, 
          KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      SPanelDiagramComponent.getInstance()
                            .setMode(SPanelDiagramComponent.Mode.GRIP);
    }
  };
  
  public static final SlyumAction ACTION_ADD_CLASS = 
      new SlyumAction(
          "Add Class", "class", KeyEvent.VK_G, 
          KeyStroke.getKeyStroke('C')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new ClassFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_INTERFACE = 
      new SlyumAction(
          "Add Interface", "interface", KeyEvent.VK_I, 
          KeyStroke.getKeyStroke('I')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new InterfaceFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_ENUM = 
      new SlyumAction(
          "Add Enum", "enum", KeyEvent.VK_U, 
          KeyStroke.getKeyStroke('U')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new EnumFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_ASSOCIATION_CLASS = 
      new SlyumAction(
          "Add Association Class", "classAssoc", KeyEvent.VK_X, 
          KeyStroke.getKeyStroke('X')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new AssociationClassFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_INHERITANCE = 
      new SlyumAction(
          "Add Inheritance", "generalize", KeyEvent.VK_H, 
          KeyStroke.getKeyStroke('H')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new InheritanceFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_INNER_CLASS = 
      new SlyumAction(
          "Add Nested Class", "innerClass", KeyEvent.VK_N, 
          KeyStroke.getKeyStroke('N')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new InnerClassFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_DEPENDENCY = 
      new SlyumAction(
          "Add Dependency", "dependency", KeyEvent.VK_E, 
          KeyStroke.getKeyStroke('E')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new DependencyFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_ASSOCIATION = 
      new SlyumAction(
          "Add Association", "association", KeyEvent.VK_S, 
          KeyStroke.getKeyStroke('S')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new BinaryFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_AGGREGATION = 
      new SlyumAction(
          "Add Aggregation", "aggregation", KeyEvent.VK_G, 
          KeyStroke.getKeyStroke('G')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new AggregationFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_COMPOSITION = 
      new SlyumAction(
          "Add Composition", "composition", KeyEvent.VK_M, 
          KeyStroke.getKeyStroke('M')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new CompositionFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_MULTI_ASSOCIATION = 
      new SlyumAction(
          "Add Multi-association", "multi", KeyEvent.VK_W, 
          KeyStroke.getKeyStroke('W')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new MultiFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_ADD_NOTE = 
      new SlyumAction(
          "Add Note", "note", KeyEvent.VK_O, 
          KeyStroke.getKeyStroke('O')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new NoteFactory(gv));
    }
  };
  
  public static final SlyumAction ACTION_LINK_NOTE = 
      new SlyumAction(
          "Link Note", "linkNote", KeyEvent.VK_K, 
          KeyStroke.getKeyStroke('K')) {
    @Override
    public void actionPerformed(ActionEvent e) {
      GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
      gv.initNewComponent(new LineCommentaryFactory(gv));
    }
  };
  
  // Menu Help    --------------------------------------------------------------
  public static final SlyumAction ACTION_HELP = 
      new SlyumAction(
          "Help...", "help", KeyEvent.VK_E, 
          KeyStroke.getKeyStroke("F1")) {
    @Override
    public void actionPerformed(ActionEvent e) {
      Slyum.openHelp();
    }
  };
  
  public static final SlyumAction ACTION_PROJECT_PAGE = 
      new SlyumAction(
          "Go to project page...", "icon_16x16", KeyEvent.VK_P) {
    @Override
    public void actionPerformed(ActionEvent e) {
      Slyum.openProjectPage();
    }
  };
  
  public static final SlyumAction ACTION_UPDATE = 
      new SlyumAction(
          "Check for update...", "eggs-16", KeyEvent.VK_R) {
    @Override
    public void actionPerformed(ActionEvent e) {
      
        if (UpdateInfo.isUpdateAvailable())
          UpdateInfo.getNewUpdate();
        else
          SMessageDialog.showInformationMessage(
              "You have the latest version of Slyum! Hura!", Slyum.getInstance());
    }
  };
  
  public static final SlyumAction ACTION_PATCH_NOTE = 
      new SlyumAction(
          "See patch note...", "patchnote", KeyEvent.VK_S) {
    @Override
    public void actionPerformed(ActionEvent e) {
        UpdateInfo.getPatchNote();
    }
  };
  
  public static final SlyumAction ACTION_ISSUES = 
      new SlyumAction(
          "Report issue / improvments...", "bug", KeyEvent.VK_I) {
    @Override
    public void actionPerformed(ActionEvent e) {
        Slyum.openIssuesPage();
    }
  };
  
  public static final SlyumAction ACTION_ABOUT = 
      new SlyumAction(
          "About Slyum...", "about", KeyEvent.VK_A) {
    @Override
    public void actionPerformed(ActionEvent e) {
        Slyum.getInstance().openAbout();
    }
  };
  
  // Others --------------------------------------------------------------------
  
  public static final SlyumAction ACTION_ASSOCIATE_NOTE = 
      new SlyumAction(
          "Associate note", "multiNote") {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .linkNewNoteWithSelectedEntities();
    }
  };
  
  public static final SlyumAction ACTION_ADJUST_INHERITANCE = 
      new SlyumAction(
          "Autopath", "adjust-inheritance") {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                       .adjustSelectedInheritances();
      
      InheritanceView iv = (InheritanceView)
          ((IActionSource)e.getSource()).getObjectAssociatedWithAction();
      if (!iv.isSelected()) iv.adjustInheritance();
    }
  };
  
  public static final SlyumAction ACTION_O_AND_I = 
      new SlyumAction(
          "Overrides & Implementations...", "method") {
    @Override
    public void actionPerformed(ActionEvent e) {      
      InheritanceView iv = (InheritanceView)
          ((IActionSource)e.getSource()).getObjectAssociatedWithAction();
      ((Inheritance)iv.getAssociedComponent()).showOverridesAndImplementations();
    }
  };
  
  public static final SlyumAction ACTION_ADD_CONSTRUCTOR = 
      new SlyumAction(
          "Add Constructor", "constructor") {
    @Override
    public void actionPerformed(ActionEvent e) {      
      ((ClassView)((IActionSource)e.getSource()).
          getObjectAssociatedWithAction()).addConstructor();
    }
  };
  
  public static final SlyumAction ACTION_MOVE_MEMBER_UP = 
      new SlyumAction(
          "Move Member Up", "arrow-up") {
    @Override
    public void actionPerformed(ActionEvent e) {      
      TextBox tb = (TextBox)((IActionSource)e.getSource()).
          getObjectAssociatedWithAction();
      
      int offset = -1;
      if (tb.getClass() == TextBoxAttribute.class) {
        final Attribute attribute = (Attribute) ((TextBoxAttribute) pressedTextBox)
                .getAssociedComponent();
        ((SimpleEntity) component).moveAttributePosition(attribute, offset);
      } else if (pressedTextBox.getClass() == TextBoxMethod.class) {
        final Method method = (Method) ((TextBoxMethod) pressedTextBox)
                .getAssociedComponent();
        ((SimpleEntity) component).moveMethodPosition(method, offset);
    }
  };
  
  /* ------------------------ END STATIC -------------------------------------*/
  
  public String getKeyStrokeText() {
    KeyStroke ks = (KeyStroke)getValue(ACCELERATOR_KEY);
    return ks.toString();
  }
  
  public String getText() {
    return getValue(NAME) + " (" + getKeyStrokeText() + ")";
  }
  
  private SlyumAction() {
  }

  private SlyumAction(String name) {
    super(name);
    initialize();
  }

  private SlyumAction(String name, String iconName) {
    super(name, 
          PersonalizedIcon.createImageIcon(iconName + DEFAULT_ICON_EXTENSION));
    initialize();
  }

  private SlyumAction(
      String name, String iconName, int mnemonic) {
    super(name, 
          PersonalizedIcon.createImageIcon(
              Slyum.ICON_PATH + iconName + DEFAULT_ICON_EXTENSION));
    putValue(MNEMONIC_KEY, mnemonic);
    initialize();
  }

  private SlyumAction(
      String name, String iconName, int mnemonic, KeyStroke accelerator) {
    super(name, 
          PersonalizedIcon.createImageIcon(
              Slyum.ICON_PATH + iconName + DEFAULT_ICON_EXTENSION));
    putValue(MNEMONIC_KEY, mnemonic);
    putValue(ACCELERATOR_KEY, accelerator);
    initialize();
  }
  
  private void initialize() {
    putValue(SHORT_DESCRIPTION, getValue(NAME) + 
             " (" + getValue(ACCELERATOR_KEY) + ")");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
    gv.setStopRepaint(true);
    // disable shortcut
    
    gv.goRepaint();
  }
  
  // Static nested class -------------------------------------------------------
  public static abstract class JActionMenuItem 
      extends JMenuItem implements IActionSource {
    public JActionMenuItem(Action a) {
      super(a);
    }
  }
  
  public static JMenuItem createActionMenuItem(
      SlyumAction action, final Object source) {
    return new JActionMenuItem(action) {

      @Override
      public Object getObjectAssociatedWithAction() {
        return source;
      }
    };
  }
}
