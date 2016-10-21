package swing;

import swing.slyumCustomizedComponents.SButton;
import com.apple.java.OSXAdapter;
import graphic.GraphicView;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import swing.SPanelDiagramComponent.Mode;
import update.UpdateInfo;
import static update.UpdateInfo.isUpdateAvailable;
import static update.UpdateInfo.UPDATER_FILE;
import static update.UpdateInfo.TAG_UPDATER_VERSION;
import utility.OSValidator;
import utility.PersonalizedIcon;
import utility.SMessageDialog;
import utility.TagDownload;

/**
 * Main class! Create a new Instance of Slyum and display it. Create menu.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class Slyum extends JFrame implements ActionListener {
  
  public static final String ACTION_ABOUT = "About";
  public static final String ACTION_ADD_VIEW = "addView";
  
  public final static String ACTION_ADJUST_INHERITANCE = "adjust-inheritance";
  public final static String ACTION_ADJUST_WIDTH = "AdjustWidth";
  public final static String ACTION_ALIGN_BOTTOM = "AlignBottom";
  public final static String ACTION_ALIGN_LEFT = "AlignLeft";
  public final static String ACTION_ALIGN_RIGHT = "AlignRight";
  public static final String ACTION_ALIGN_TOP = "AlignTop";
  public final static String ACTION_CLOSE_VIEW = "closeView";
  public final static String ACTION_COLOR = "Color";
  public final static String ACTION_DELETE = "Delete"; 
  public final static String ACTION_DELETE_VIEW = "deleteView";
  public static final String ACTION_DUPLICATE = "duplicate";
  public final static String ACTION_EXIT = "Exit";
  public final static String ACTION_EXPORT_EPS = "ExportEps";
  public final static String ACTION_NEW_WINDOW = "NewWindow";

  public final static String ACTION_EXPORT_IMAGE = "ExportImage";

  public static final String ACTION_EXPORT_PDF = "ExportPdf";
  public static final String ACTION_EXPORT_SVG = "ExportSvg";
  public static final String ACTION_FULL_SCREEN = "fullScreen";
  public final static String ACTION_HELP = "Help";
  public final static String ACTION_KLIPPER = "Klipper";
  public final static String ACTION_LOCATE = "locate";

  // Action command
  public static final String ACTION_MODE_CURSOR = "ModeCursor";
  public static final String ACTION_MODE_GRIP = "ModeGrip";
  public static final String ACTION_MOVE_BOTTOM = "MoveBottom";
  public static final String ACTION_MOVE_DOWN = "MoveDown";
  public static final String ACTION_MOVE_TOP = "MoveTop";
  public static final String ACTION_MOVE_UP = "MoveUp";
  public static final String ACTION_NEW_AGGREGATION = "NewAggregation";
  public static final String ACTION_NEW_ASSOCIATION = "NewAssociation";
  public static final String ACTION_NEW_CLASS = "NewClass";
  public static final String ACTION_NEW_CLASS_ASSOCIATION = "NewClassAssociation";
  public static final String ACTION_NEW_COMPOSITION = "NewComposition";
  public static final String ACTION_NEW_DEPENDENCY = "NewDependency";
  public static final String ACTION_NEW_ENUM = "NewEnum";
  public static final String ACTION_NEW_GENERALIZE = "NewGeneralize";
  public static final String ACTION_NEW_INNER_CLASS = "NewInnerClass";
  public static final String ACTION_NEW_INTERFACE = "NewInterface";
  public static final String ACTION_NEW_LINK_NOTE = "LinkNote";
  public static final String ACTION_NEW_MULTI = "NewMulti";
  public static final String ACTION_NEW_NOTE = "NewNote";
  public static final String ACTION_NEW_NOTE_ASSOCIED = "NewNoteAssocied";
  public static final String ACTION_NEW_PROJECT = "NewProject";
  public static final String ACTION_OPEN = "Open";
  public static final String ACTION_OPEN_RECENT_RPOJECT = "openRecentProject";
  public static final String ACTION_OPEN_VIEW = "openView";
  public static final String ACTION_PAGE_SETUP = "PageSetup";
  public static final String ACTION_PATCH_NOTE = "PatchNote";
  public static final String ACTION_PRINT = "Print";
  public static final String ACTION_PROJECT_PAGE = "ProjectPage";
  public static final String ACTION_PROPERTIES = "Properties";
  public static final String ACTION_REDO = "Redo";
  public static final String ACTION_REPORT_ISSUE = "ReportIssue";
  public static final String ACTION_SAVE = "Save";
  public static final String ACTION_SAVE_AS = "SaveAs";
  public static final String ACTION_SELECT_ALL = "SelectAll";
  
  public static final String ACTION_TEXTBOX_DOWN = "MoveTextBoxDown";
  public static final String ACTION_TEXTBOX_UP = "MoveTextBoxUp";
  public static final String ACTION_UNDO = "Undo";
  public static final String ACTION_UNSELECT_ALL = "UnselectAll";
  public static final String ACTION_UPDATE = "Update";

  public static final String ACTION_ZOOM_0_5 = "Zoom 0.5x";
  public static final String ACTION_ZOOM_1 = "Zoom 1x";
  public static final String ACTION_ZOOM_1_5 = "Zoom 1.5x";
  public static final String ACTION_ZOOM_2 = "Zoom 2x";

  public static final String ACTION_ZOOM_ADAPT = "ZoomAdapt";
  public static final String ACTION_ZOOM_ADAPT_SELECTION = "ZoomAdaptSelection";
  
  public static final String ACTION_ZOOM_MINUS = "Zoom -";
  public final static String ACTION_ZOOM_PLUS = "Zoom +";
  public static final String APP_NAME = "Slyum";
  public final static String APP_DIR_NAME = APP_NAME;
  public final static Color BACKGROUND_FORHEAD = new Color(246, 246, 246);
  public final static Color DEFAULT_BACKGROUND = new Color(239, 239, 242);
  public final static Color DEFAULT_BORDER_COLOR = new Color(169, 169, 169);
  public final static Font DEFAULT_FONT;
  public final static int DEFAULT_FONT_SIZE = 12;
  public final static Point DEFAULT_SIZE = new Point(1024, 760);
  public final static Color DISABLE_COLOR = Color.GRAY;
  public final static String EXTENTION = "sly";
  public final static String FILE_SEPARATOR = System.getProperty("file.separator");
  // Don't use the file separator here. Java resources are get with
  // getResource() and didn't support back-slash character on Windows.
  public static final String RESOURCES_PATH = "resources/";
  public final static String FONTS_PATH = RESOURCES_PATH + "fonts/";

  public final static String FULL_EXTENTION = String.format(".%s", EXTENTION);
  public final static String ICON_PATH = RESOURCES_PATH + "icon/";
  public static final boolean IS_AUTO_ADJUST_INHERITANCE = true;
  public static final String KEY_ADD_VIEW = "INSERT";


  public final static String KEY_ADJUST_SIZE = "ctrl 1";
  public final static String KEY_AGGREGATION = "G";
  public final static String KEY_ALIGN_DOWN = "ctrl DOWN";
  public final static String KEY_ALIGN_LEFT = "ctrl LEFT";
  public final static String KEY_ALIGN_RIGHT = "ctrl RIGHT";

  public static final String KEY_ALIGN_UP = "ctrl UP";
  public static final String KEY_ASSOCIATION = "A";
  public static final String KEY_ASSOCIATION_CLASS = "X";
  public static final String KEY_CLASS = "C";
  
  public final static String KEY_CLOSE_VIEW = "ctrl W";
  public static final String KEY_COLOR = "ctrl L";
  public static final String KEY_COMPOSITION = "P";
  public static final String KEY_DEFAULT_MODE = "Q";
  public static final String KEY_DELETE = "DELETE";
  public final static String KEY_DELETE_VIEW = "ctrl DELETE";
  public final static String KEY_DEPENDENCY = "D";
  public static final String KEY_DUPLICATE = "ctrl D";
  public final static String KEY_DUPLICATE_VIEW = "ctrl INSERT";

  public final static String KEY_ENUM = "E";
  public final static String KEY_EXIT = "alt F4";
  public final static String KEY_EXPORT_EPS = "ctrl G";
  public final static String KEY_EXPORT_IMAGE = "ctrl E";

  public final static String KEY_EXPORT_PDF = "ctrl F";
  public static final String KEY_EXPORT_SVG = "ctrl V";
  public static final String KEY_FULL_SCREEN = "ctrl ENTER";
  public final static String KEY_GRIPS_MODE = "W";
  public final static String KEY_HELP = "F1";
  public final static String KEY_INHERITANCE = "H";
  public final static String KEY_INNER_CLASS = "R";
  public final static String KEY_INTERFACE = "I";
  public final static String KEY_KLIPPER = "ctrl C";
  public final static String KEY_LINK_NOTE = "L";
  public final static String KEY_MOVE_BOTTOM = "ctrl alt DOWN";
  public static final String KEY_MOVE_DOWN = "ctrl alt LEFT";
  public static final String KEY_MOVE_TOP = "ctrl alt UP";
  public static final String KEY_MOVE_UP = "ctrl alt RIGHT";
  public final static String KEY_MULTI_ASSOCIATION = "M";
  // Accelerator
  public static final String KEY_NEW_PROJECT = "ctrl N";
  public final static String KEY_NOTE = "O";
  public final static String KEY_OPEN_PROJECT = "ctrl O";

  public final static String KEY_PRINT = "ctrl P";
  
  public final static String KEY_PROPERTIES = "alt ENTER";
  public final static String KEY_REDO = "ctrl Y";
  public static final String KEY_SAVE = "ctrl S";
  public static final String KEY_SAVE_AS = "ctrl shift S";
  public static final String KEY_SELECT_ALL = "ctrl A";
  public static final String KEY_UNDO = "ctrl Z";
  public static final String KEY_UNSELECT_ALL = "ctrl U";
  public static final String KEY_ZOOM_1 = "1";
  public static final String KEY_ZOOM_ADAPT = "ctrl shift Z";
  public static final String KEY_ZOOM_MINUS = "ctrl MINUS";
  public static final String KEY_ZOOM_PLUS = "ctrl PLUS";
  public static final String KEY_NEW_WINDOW = "ctrl shit W";
  
  public final static Logger LOGGER = Logger.getLogger(Slyum.class.getName());
  

  public static final Mode MODE_CURSOR = Mode.CURSOR;
  // Properties
  public static final boolean SHOW_CROSS_MENU = true;
  public static final boolean SHOW_ERRORS_MESSAGES = true;
  public static final boolean SHOW_OPENJDK_WARNING = true;
  public static final Color TEXT_COLOR = new Color(34, 34, 34);
  public static final Color THEME_COLOR = new Color(0, 122, 204); // 007ACC
  public static final Font UI_FONT;
  // !! Always  X.Y.Z (for update safety), even if it's 0.
  public static final String VERSION = "5.0.1";
  public static final boolean VIEW_TITLE_ON_EXPORT_DEFAULT = true;
  public static final boolean DISPLAY_DIAGRAM_INFORMATIONS_ON_EXPERT_DEFAULT = true;
  public static final int WINDOWS_MAXIMIZED = Frame.MAXIMIZED_BOTH;
  public static final Dimension WINDOWS_SIZE = new Dimension(DEFAULT_SIZE.x, DEFAULT_SIZE.y);
  private static final String ARGUMENT_EXIT_WITHOUT_ASK = "-exitWithoutAsk";
  private static final String ARGUMENT_PRINT_CHANGE_STACK_STATE = "-printChanges";
  private static final String ARGUMENT_OPEN_NEW_PROJECT = "-newProject";
  private static final String URL_ISSUES_PAGE = "https://github.com/Slyum/slyum/issues";
  private static final String URL_PROJECT_PAGE = "https://github.com/Slyum/slyum";
  private static String[] arguments;

  private static Slyum instance;
  private static JCheckBoxMenuItem menuItemFullScreen;
  private static JMenuItem redo;
  private static final String ACTION_CLEAN_DIAGRAM = "CleanDiagram";

  private static JMenuItem undo;
  private static String windowTitle = APP_NAME;
  private static boolean CLEAN_AT_OPENING_DEFAULT = true;
  
  static {
    DEFAULT_FONT = addSystemFont("Ubuntu-R");
    UI_FONT = addSystemFont("segoeui");
  }
  
  public static Font addSystemFont(String fileName) {
    Font font = null;
    try {
      font =
      Font.createFont(
          Font.TRUETYPE_FONT,
              Slyum.class.getResource(
                  String.format("%s%s.ttf", FONTS_PATH, fileName)).openStream()
      ).deriveFont(Font.PLAIN, DEFAULT_FONT_SIZE);
      
      // Save the new font in local environment.
      GraphicsEnvironment ge =
      GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(font);
    } catch (IOException | FontFormatException | NullPointerException e) {
      System.err.println("Unable to import font:" + fileName);
    }
    return font;
  }
  
  public static boolean argumentExitWithoutAsk() {
    for (String s : arguments)
      if (s.equals(ARGUMENT_EXIT_WITHOUT_ASK)) return true;

    return false;
  }
  
  public static boolean argumentOpenWithNewProject() {
    for (String s : arguments)
      if (s.equals(ARGUMENT_OPEN_NEW_PROJECT)) return true;

    return false;
  }

  public static String argumentGetFile() {
    for (String s : arguments)
      if (s.contains(FULL_EXTENTION)) {
        File f = new File(s);
        if (f.exists()) {
          return s;
        } else {
          System.err.println("Specified file does not exist.");
          return null;
        }
      }
    return null;
  }

  public static boolean argumentIsChangeStackStatePrinted() {
    for (String s : arguments)
      if (s.equals(ARGUMENT_PRINT_CHANGE_STACK_STATE)) return true;
    
    return false;
  }
  
  /**
   * Create the application directory.
   * 
   * @param path
   *          the path where create the application directory.
   */
  public static void createAppDir(String path) {
    File appDir = new File(path);
    if (appDir.mkdirs()) {
      System.out.println("Application directory created.");
    } else {
      System.err.println("Application directory not created.");
      SMessageDialog.showErrorMessage("Error to create application directory.");
    }
  }

  public static void enableFullScreenMode(Window window) {
    String className = "com.apple.eawt.FullScreenUtilities";
    String methodName = "setWindowCanFullScreen";
    
    try {
      Class<?> clazz = Class.forName(className);
      Method method = clazz.getMethod(methodName, new Class<?>[]{
        Window.class, boolean.class});
      method.invoke(null, window, true);
    } catch (Throwable t) {
      System.err.println("Full screen mode is not supported");
    }
  }
  
  public static String getCurrentDirectoryFileChooser() {
    String defaultPath = null;

    defaultPath = PropertyLoader.getInstance().getProperties()
            .getProperty("PathForFileChooser");

    if (defaultPath == null) defaultPath = System.getProperty("user.home");

    return defaultPath;
  }

  public static void setCurrentDirectoryFileChooser(String path) {
    PropertyLoader.getInstance().getProperties()
        .put("PathForFileChooser", String.valueOf(path));
    PropertyLoader.getInstance().push();
  }
  
  public static Font getDefaultFont() {
    return getInstance()._getDefaultFont();
  }
  
  public static void setEnableRedoButtons(boolean enable) {
    PanelClassDiagram.getInstance().getRedoButton().setEnabled(enable);
    redo.setEnabled(enable);
  }
  
  public static void setEnableUndoButtons(boolean enable) {
    PanelClassDiagram.getInstance().getUndoButton().setEnabled(enable);
    undo.setEnabled(enable);
  }
  
  public static Slyum getInstance() {
    return instance;
  }

  public static Mode getModeCursor() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.MODE_CURSOR);
    Mode mode = MODE_CURSOR;
    
    if (prop != null) mode = Mode.valueOf(prop);
    
    return mode;
  }
  
  public static String getPathAppDir() {
    String fileName = "";
    String appData = System.getenv("APPDATA");
    String userHome = System.getProperty("user.home");

    if (OSValidator.IS_MAC) {
      fileName = userHome + "/Library/Application Support/" + APP_DIR_NAME;
    } else if (OSValidator.IS_WINDOWS) {
      fileName = appData + FILE_SEPARATOR + APP_DIR_NAME;
    } else if (OSValidator.IS_UNIX) {
      fileName = userHome + FILE_SEPARATOR + "." + APP_DIR_NAME;
    }

    if (!new File(fileName).exists()) createAppDir(fileName);

    return fileName;
  }

  public static void setSelectedMenuItemFullScreen(boolean selected) {
    menuItemFullScreen.setSelected(selected);
  }

  public static void setSmallIcons(boolean use) {
    PropertyLoader.getInstance().getProperties().put("SmallIcon", use);
    PropertyLoader.getInstance().push();
  }

  public static void setStarOnTitle(boolean visible) {
    if (Slyum.getInstance() != null)
      Slyum.getInstance().setTitle(windowTitle + (visible ? "*" : ""));
  }

  public static boolean isAutoAdjustInheritance() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.AUTO_ADJUST_INHERITANCE);
    boolean enable = IS_AUTO_ADJUST_INHERITANCE;
    
    if (prop != null) enable = Boolean.parseBoolean(prop);
    
    return enable;
  }

  public static boolean isFullScreenMode() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.FULL_SCREEN_MODE);
    boolean enable = false;
    
    if (prop != null) enable = Boolean.parseBoolean(prop);

    return enable;
  }

  public static void setFullScreenMode(boolean fullScreen) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.FULL_SCREEN_MODE, String.valueOf(fullScreen));
    PropertyLoader.getInstance().push();
  }

  public static int getUpdaterVersion() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.UPDATER_VERSION);
    int updaterVersion = 1;
    
    if (prop != null) updaterVersion = Integer.valueOf(prop);

    return updaterVersion;
  }

  public static void setUpdaterVersion(int updaterVersion) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.UPDATER_VERSION, String.valueOf(updaterVersion));
    PropertyLoader.getInstance().push();
  }

  public static int getRecentColorsSize() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.RECENT_COLORS_SIZE);
    int size = 3;
    
    if (prop != null) size = Integer.valueOf(prop);

    return size;
  }
  
  public static void initRecentColors() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.RECENT_COLORS);
    
    if (prop == null || prop.isEmpty())
      return;
    
    String[] strRecentColors = prop.split(";");
    
    for (String color : strRecentColors)
      try {
        SColorAssigner.addRecentColor(new Color(Integer.valueOf(color)));
      } catch (NumberFormatException e) {
        
      }
  }
  
  public static void saveRecentColors() {
    if (SColorAssigner.getRecentColors().length == 0)
      return;
    
    String strColors = String.join(
        ";", Arrays.stream(SColorAssigner.getRecentColors())
                   .filter(c -> c != null)
                   .map(c -> String.valueOf(c.getRGB())).toArray(size -> new String[size]));
    
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.RECENT_COLORS, strColors);
    PropertyLoader.getInstance().push();
  }

  public static boolean isShowIntersectionLine() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.SHOW_INTERSECTION_LINE);
    boolean value = true;
    
    if (prop != null) value = Boolean.valueOf(prop);

    return value;
  }
  
  public static void setShowIntersectionLine(boolean show) {
    PropertyLoader.getInstance().getProperties().put(PropertyLoader.SHOW_INTERSECTION_LINE, show);
    PropertyLoader.getInstance().push();
  }

  public static SProperties.IntersectionLineSize getSizeIntersectionLine() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.SIZE_INTERSECTION_LINE);
    SProperties.IntersectionLineSize lineSize = SProperties.IntersectionLineSize.MEDIUM;
    
    if (prop != null) lineSize = SProperties.IntersectionLineSize.valueOf(prop);

    return lineSize;
  }
  
  public static void setShowIntersectionLine(SProperties.IntersectionLineSize lineSize) {
    PropertyLoader.getInstance().getProperties().put(PropertyLoader.SIZE_INTERSECTION_LINE, lineSize.name());
    PropertyLoader.getInstance().push();
  }
  
  public static boolean isShowCrossMenu() {
    final String prop = PropertyLoader.getInstance().getProperties()
        .getProperty("ShowCrossMenu");
    boolean enable = SHOW_CROSS_MENU;

    if (prop != null) enable = Boolean.parseBoolean(prop);

    return enable;
  }

  public static void setShowCrossMenu(boolean show) {
    PropertyLoader.getInstance().getProperties().put("ShowCrossMenu", show);
    PropertyLoader.getInstance().push();
  }

  public static boolean isShowErrorMessage() {
    final String prop = PropertyLoader.getInstance().getProperties()
        .getProperty("ShowErrorMessages");
    boolean enable = SHOW_ERRORS_MESSAGES;

    if (prop != null) enable = Boolean.parseBoolean(prop);

    return enable;
  }

  public static void setShowErrorMessage(boolean show) {
    PropertyLoader.getInstance().getProperties().put("ShowErrorMessages", show);
    PropertyLoader.getInstance().push();
  }

  public static boolean isShowOpenJDKWarning() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty("showOpenJDKWarning");
    boolean enable = SHOW_OPENJDK_WARNING;
    
    if (prop != null) enable = Boolean.parseBoolean(prop);
    
    return enable;
  }

  public static void setShowOpenJDKWarning(boolean show) {
    PropertyLoader.getInstance().getProperties()
        .put("showOpenJDKWarning", show);
    PropertyLoader.getInstance().push();
  }

  public static boolean isViewTitleOnExport() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.VIEW_TITLE_ON_EXPORT);
    boolean enable = VIEW_TITLE_ON_EXPORT_DEFAULT;
    if (prop != null) enable = Boolean.parseBoolean(prop);
    return enable;
  }
  
  public static boolean isDisplayedDiagramInformationOnExport() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.DISPLAY_DIAGRAM_INFORMATIONS_ON_EXPORT);
    boolean enable = DISPLAY_DIAGRAM_INFORMATIONS_ON_EXPERT_DEFAULT;
    if (prop != null) enable = Boolean.parseBoolean(prop);
    return enable;
  }

  public static void main(String[] args) {
    Locale.setDefault(Locale.ENGLISH);
    arguments = args;
    setUIProperties();
    
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException |
        IllegalAccessException | UnsupportedLookAndFeelException e) {
      final String MSG = "Unable to load Look and Feel";
      LOGGER.log(Level.SEVERE, MSG, e);
      SMessageDialog.showErrorMessage(MSG);
    }
    
    if (UpdateInfo.isUpdateCheckedAtLaunch())
      UpdateInfo.getNewUpdate(true);
    showWarningForOpenJDK();
    instance = new Slyum();
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        instance.initializationComplete();
      }
    });
  }

  public static void openURL(String url) {
    try {
      java.awt.Desktop.getDesktop().browse(new URI(url));
    } catch (URISyntaxException | IOException e) {
      SMessageDialog
          .showErrorMessage("Unable to open " + url + ".");
    }
  }

  public static void updateWindowTitle(File projectName) {
    windowTitle = APP_NAME
                  + (projectName == null ? "" : " - " + projectName.getPath());
    Slyum.getInstance().setTitle(windowTitle);
  }

  public static boolean isCleanAtOpeningEnable() {
    String prop = PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.CLEAN_AT_OPENING);
    boolean enable = CLEAN_AT_OPENING_DEFAULT;
    if (prop != null) enable = Boolean.parseBoolean(prop);
    return enable;
  }

  public static void setCleanAtOpeningEnable(boolean enabled) {
    PropertyLoader.getInstance().getProperties()
        .put(PropertyLoader.CLEAN_AT_OPENING, enabled);
    PropertyLoader.getInstance().push();
  }

  /**
   * Initialize the properties of Slyum.
   */
  private static void setUIProperties() {
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");
    
    Font f = UI_FONT.deriveFont(13.0f);
    UIManager.put("Button.font", f);
    UIManager.put("Label.font", f);
    UIManager.put("CheckBox.font", f);
    UIManager.put("RadioButton.font", f);
    UIManager.put("TabbedPane.font", f);
    UIManager.put("TitledBorder.font", f);
    UIManager.put("List.font", f);
    UIManager.put("Menu.font", f);
    UIManager.put("MenuItem.font", f);
    UIManager.put("RadioButtonMenuItem.font", f);
    UIManager.put("ComboBox.font", f);
    UIManager.put("Table.font", f);
    UIManager.put("TextField.font", f);
    UIManager.put("TextArea.font", f);
    UIManager.put("OptionPane.informationIcon", PersonalizedIcon.getInfoIcon());
    UIManager.put("OptionPane.errorIcon", PersonalizedIcon.getErrorIcon());
    UIManager.put("OptionPane.warningIcon", PersonalizedIcon.getWarningIcon());
    UIManager.put("OptionPane.questionIcon", PersonalizedIcon.getQuestionIcon());
  }

  private static void showWarningForOpenJDK() {
    if (isShowOpenJDKWarning() && OSValidator.IS_UNIX)
      new NoRepopDialog(
          "Problems are observed with OpenJRE. If you use OpenJRE, we encourage you to get official Sun JRE.")
          .setVisible(true);
  }

  public Font defaultFont;
  private JMenu menuFile;
  private JMenuItem menuItemLocate;
  private JMenu menuOpenViews;

  /**
   * Create a new Slyum :D (slyyy slyy slyyyyy)!
   */
  public Slyum() {
    handleMacOSX();
    initFont();
    createJMenuBar();
    setFrameProperties();
    initEventListener();
  }

  public Font _getDefaultFont() {
    return defaultFont;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    PanelClassDiagram p = PanelClassDiagram.getInstance();
    GraphicView gv = MultiViewManager.getSelectedGraphicView();

    switch (e.getActionCommand()) {
      case ACTION_NEW_WINDOW:
        try {
          openNewApplicationInstance();
        } catch (IOException | URISyntaxException ex) {
          SMessageDialog.showErrorMessage("An error has occurred while opening a new window.", this);
        }
        break;
      case Slyum.ACTION_SAVE_AS:
        p.saveToXML(true);
        break;
      case ACTION_ABOUT:
        openAbout();
        break;
      case ACTION_HELP:
        openHelp();
        break;
      case ACTION_EXIT:
        exit();
        break;
      case ACTION_OPEN_RECENT_RPOJECT:
        if (e.getSource() instanceof JMenuItemHistory)
          PanelClassDiagram.getInstance().openFromXmlAndAsk(
              new File(((JMenuItemHistory) e.getSource()).getHistoryPath()
                  .toString()));
        else
          SMessageDialog
              .showErrorMessage("An error occured while opening project. Please report.");
        break;
      case ACTION_PAGE_SETUP:
        SlyumPrinterJob.pageDialog(MultiViewManager.getSelectedGraphicView());
        break;
      case ACTION_PROPERTIES:
        openProperties();
        break;
      case ACTION_UPDATE:
        if (isUpdateAvailable())
          UpdateInfo.getNewUpdate();
        else
          SMessageDialog.showInformationMessage(
              "You have the latest version of Slyum! Hura!", this);
        break;
      case ACTION_PATCH_NOTE:
        UpdateInfo.getPatchNote();
        break;
      case ACTION_PROJECT_PAGE:
        openURL(URL_PROJECT_PAGE);
        break;
      case ACTION_REPORT_ISSUE:
        openURL(URL_ISSUES_PAGE);
        break;
      case ACTION_SELECT_ALL:
        gv.selectAll();
        break;
      case ACTION_UNSELECT_ALL:
        gv.unselectAll();
        break;
      case ACTION_ZOOM_PLUS:
        MultiViewManager.getSelectedGraphicView().forwardScale();
        break;
      case ACTION_ZOOM_MINUS:
        MultiViewManager.getSelectedGraphicView().backScale();
        break;
      case ACTION_ZOOM_ADAPT:
        MultiViewManager.getSelectedGraphicView()
            .adaptDiagramToWindow();
        break;
      case ACTION_ZOOM_ADAPT_SELECTION:
        MultiViewManager.getSelectedGraphicView()
            .adaptSelectionToWindow();
        break;
      case ACTION_ZOOM_1:
        MultiViewManager.getSelectedGraphicView().setScale(1.0);
        break;
      case ACTION_ZOOM_0_5:
        MultiViewManager.getSelectedGraphicView().setScale(0.5);
        break;
      case ACTION_ZOOM_1_5:
        MultiViewManager.getSelectedGraphicView().setScale(1.5);
        break;
      case ACTION_ZOOM_2:
        MultiViewManager.getSelectedGraphicView().setScale(2.0);
        break;
      case ACTION_LOCATE:
        try {
          Desktop.getDesktop().open(
              PanelClassDiagram.getFileOpen().getParentFile());
        } catch (IOException ex) {
          SMessageDialog.showErrorMessage("No file open!");
        }
        break;
      case ACTION_FULL_SCREEN:
        PanelClassDiagram.getInstance().setFullScreen(menuItemFullScreen.isSelected());
        break;
      case ACTION_OPEN_VIEW:
        SMessageDialog.showErrorMessage("Not implemeted");
        break;
      case ACTION_CLOSE_VIEW:
        MultiViewManager.closeSelectedViewWithWarning();
        break;
      case ACTION_ADD_VIEW:
        MultiViewManager.addAndOpenNewView();
        break;
      case ACTION_DELETE_VIEW:
        try {
          MultiViewManager.removeSelectedView();
        } catch (IllegalArgumentException ex) {
          SMessageDialog.showErrorMessage(ex.getMessage());
        }
        break;
      case ACTION_EXPORT_PDF:
        PanelClassDiagram.getInstance().exportAsVectoriel("pdf", new String[] {"pdf", "svg", "eps"});
        break;
      case ACTION_EXPORT_SVG:
        PanelClassDiagram.getInstance().exportAsVectoriel("svg", new String[] {"pdf", "svg", "eps"});
        break;
      case ACTION_EXPORT_EPS:
        PanelClassDiagram.getInstance().exportAsVectoriel("eps", new String[] {"pdf", "svg", "eps"});
        break;
      case ACTION_CLEAN_DIAGRAM:
        SMessageDialog.showInformationMessage("Cleaning complete!\n" + PanelClassDiagram.cleanComponents() + " component(s) removed.");
        break;
    }
  }

  public JMenuItem createMenuItem(String text, String iconName, int mnemonic, String accelerator, String actionCommand, ActionListener al) {
    JMenuItem item;
    final String imgLocation = ICON_PATH + iconName + ".png";
    
    final ImageIcon icon = PersonalizedIcon.createImageIcon(imgLocation);
    
    item = new JMenuItem(text, icon);
    item.setMnemonic(mnemonic);
    item.setActionCommand(actionCommand);
    if (accelerator != null && accelerator.contains("ctrl")
        && OSValidator.IS_MAC) {
      accelerator = accelerator.replace("ctrl", "meta");
      accelerator = accelerator.replace("control", "meta");
    }
    item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
    item.addActionListener(al);
    
    return item;
  }
  
  public JMenuItem createMenuItem(String text, String iconName, int mnemonic, String accelerator, String actionCommand, SButton link) {
    JMenuItem item = createMenuItem(text, iconName, mnemonic, accelerator,
                                                              actionCommand, link.getActionListeners()[0]);
    
    link.linkComponent(item);
    
    return item;
  }

  public JMenuItem createMenuItem(String text, String iconName, int mnemonic, String accelerator, String actionCommand) {
    return createMenuItem(text, iconName, mnemonic, accelerator, actionCommand,
            this);
  }

  public JMenuItem createMenuItemDisable(String text, String iconName, int mnemonic, String accelerator, String actionCommand, SButton link) {
    JMenuItem item = createMenuItem(text, iconName, mnemonic, accelerator,
                                                              actionCommand, link);
    
    item.setEnabled(false);
    
    return item;
  }
  
  public void deleteMenuItemHistory() {
    boolean remove = false;
    for (int i = 0; i < menuFile.getItemCount(); i++) {
      JMenuItem m = menuFile.getItem(i);
      if (m != null && m.getActionCommand().equals(ACTION_OPEN_RECENT_RPOJECT)) {
        remove = true;
        menuFile.remove(m);
        i--;
      }
    }

    // Suppression du séparateur.
    if (remove) menuFile.remove((OSValidator.IS_MAC ? 10 : 14));
  }
  
  /**
   * Quit the app.
   *
   * This method is public because on the MacOSX handler.
   *
   * @return True if the exit operation could be done or False if it has been
   *         canceled.
   */
  public boolean exit() {
    PanelClassDiagram p = PanelClassDiagram.getInstance();
    
    if (argumentExitWithoutAsk())
      _exit();
    else
      switch (p.askSavingCurrentProject()) {
        case JOptionPane.CANCEL_OPTION:
          return false;
          
        case JOptionPane.YES_OPTION:
          p.saveToXML(false);
          _exit();
          break;

        case JOptionPane.NO_OPTION:
          _exit();
          break;
      }
    return true;
  }
  
  public JMenuItem getMenuItemLocate() {
    return menuItemLocate;
  }
  
  public void initializationComplete() {
    SPanelDiagramComponent.getInstance().setMode(getModeCursor());
    instance.setVisible(true);
    
    // Locate dividers.
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        
        PanelClassDiagram panel = PanelClassDiagram.getInstance();
        Properties properties = PropertyLoader.getInstance().getProperties();
        String dividerBottom = properties
            .getProperty(PropertyLoader.DIVIDER_BOTTOM), dividerLeft = properties
                .getProperty(PropertyLoader.DIVIDER_LEFT);
        
        if (dividerBottom != null)
          panel.setDividerBottom(Float.valueOf(dividerBottom));
        
        if (dividerLeft != null)
          panel.setDividerLeft(Float.valueOf(dividerLeft));
        
        if (isFullScreenMode())
          panel.setFullScreen(true);
        
        IssuesInformation.mustDisplayMessage();
        initRecentColors();
      }
    });
    
    String file = argumentGetFile();
    if (!argumentOpenWithNewProject() && file == null) 
      file = RecentProjectManager.getMoreRecentFile();
    
    if (file != null) PanelClassDiagram.openSlyFile(file);
    
    int updaterVersion;
    try {
      File f = new File(UPDATER_FILE);
      updaterVersion = Integer.parseInt(TagDownload.getContentTag(TAG_UPDATER_VERSION));
      if (f.exists() && Slyum.getUpdaterVersion() < updaterVersion)
        f.delete();
    
    } catch (Exception ex) {
      Logger.getLogger(Slyum.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public void openAbout() {
    new AboutBox(this);
  }
  
  public void openProperties() {
    new SProperties();
  }
  
  public void updateMenuItemHistory() {
    deleteMenuItemHistory();
    
    List<String> histories = RecentProjectManager.getHistoryList();
    
    if (histories.size() > 0)
      menuFile.insertSeparator((OSValidator.IS_MAC ? 10 : 14));
    
    for (String s : histories) {
      JMenuItemHistory menuItem = new JMenuItemHistory(formatHistoryEntry(s));
      menuItem.setActionCommand(ACTION_OPEN_RECENT_RPOJECT);
      menuItem.addActionListener(this);
      menuItem.setHistoryPath(Paths.get(s));
      menuFile.add(menuItem, (OSValidator.IS_MAC ? 10 : 15));
    }
  }
  
  private void _exit() {
    // Save properties before closing.
    PanelClassDiagram.getInstance().saveSplitLocationInProperties();
    saveRecentColors();
    setFullScreenMode(menuItemFullScreen.isSelected());

    System.exit(0);
  }

  /**
   * Create the JMenuBar
   */
  private void createJMenuBar() {
    final JMenuBar menuBar = new JMenuBar();
    menuBar.setBackground(DEFAULT_BACKGROUND);
    menuBar.setBorder(null);

    JMenuItem menuItem;

    // Menu file
    JMenu menu = menuFile = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_F);
    
    menuBar.add(menu);

    {
      SPanelFileComponent p = SPanelFileComponent.getInstance();

      // Menu item New project
      menuItem = createMenuItem("New Project", "new", KeyEvent.VK_J,
              KEY_NEW_PROJECT, ACTION_NEW_PROJECT, p.getBtnNewProject());
      menu.add(menuItem);

      // Menu item open project
      menuItem = createMenuItem("Open Project...", "open", KeyEvent.VK_O,
              KEY_OPEN_PROJECT, ACTION_OPEN, p.getBtnOpen());
      menu.add(menuItem);

      menu.addSeparator();
      
      // Menu item open new instance
      menuItem = createMenuItem("New Window", "logo16", KeyEvent.VK_W, 
              KEY_NEW_WINDOW, ACTION_NEW_WINDOW);
      menu.add(menuItem);

      menu.addSeparator();

      // Menu item save
      menuItem = createMenuItem("Save", "save", KeyEvent.VK_S, KEY_SAVE,
              ACTION_SAVE, p.getBtnSave());
      menu.add(menuItem);

      // Menu item save as...
      menuItem = createMenuItem("Save As...", "save-as", KeyEvent.VK_A,
              KEY_SAVE_AS, ACTION_SAVE_AS);
      menu.add(menuItem);
      
      menu.addSeparator();
      
      // SubMenu Export as...
      {
        JMenu subMenu = new JMenu("Export");
        subMenu.setMnemonic(KeyEvent.VK_E);
        menu.add(subMenu);
        
        // Menu item Export as image...
        menuItem = createMenuItem("As Image...", "export", KeyEvent.VK_I,
                KEY_EXPORT_IMAGE, ACTION_EXPORT_IMAGE, p.getBtnExportImage());
        subMenu.add(menuItem);
        
        // Menu item Export as PDF...
        menuItem = createMenuItem("As PDF...", "pdf-16", KeyEvent.VK_P,
                KEY_EXPORT_PDF, ACTION_EXPORT_PDF);
        subMenu.add(menuItem);
        
        // Menu item Export as SVG...
        menuItem = createMenuItem("As SVG...", "svg-16", KeyEvent.VK_S,
                KEY_EXPORT_SVG, ACTION_EXPORT_SVG);
        subMenu.add(menuItem);
        
        // Menu item Export as EPS...
        menuItem = createMenuItem("As EPS...", "eps-16", KeyEvent.VK_E,
                KEY_EXPORT_EPS, ACTION_EXPORT_EPS);
        subMenu.add(menuItem);
        
        menu.add(subMenu);
      }

      // Menu item Copy to clipboard
      menuItem = createMenuItem("Copy selection to clipboard", "klipper",
              KeyEvent.VK_K, KEY_KLIPPER, ACTION_KLIPPER, p.getBtnKlipper());
      menu.add(menuItem);
      
      // Menu item Copy to clipboard
      menuItemLocate = menuItem = createMenuItem("Locate in " + (OSValidator.IS_MAC ? "Finder" : "explorer"), "explore",
              KeyEvent.VK_K, null, ACTION_LOCATE);
      menuItemLocate.setEnabled(false);
      menu.add(menuItem);

      menu.addSeparator();

      // Menu item print
      menuItem = createMenuItem("Print...", "print", KeyEvent.VK_P, KEY_PRINT,
              ACTION_PRINT, p.getBtnPrint());
      menu.add(menuItem);
      
      // Menu item page setup
      menuItem = createMenuItem("Page setup...", "page-setup", KeyEvent.VK_G, 
              null, ACTION_PAGE_SETUP);
      menu.add(menuItem);

      if (!OSValidator.IS_MAC) {
        menu.addSeparator();

        // Menu item Properties
        menuItem = createMenuItem("Properties...", "properties", KeyEvent.VK_R,
                KEY_PROPERTIES, ACTION_PROPERTIES);
        menu.add(menuItem);
      }

      // Menu recent project
      updateMenuItemHistory();

      if (!OSValidator.IS_MAC) {
        menu.addSeparator();

        // Menu item exit
        menuItem = createMenuItem("Exit", "exit", KeyEvent.VK_X, KEY_EXIT,
                ACTION_EXIT);
        menu.add(menuItem);
      }
    }

    // Menu edit
    menu = new JMenu("Edit");
    menu.setMnemonic(KeyEvent.VK_E);
    menuBar.add(menu);

    {
      final SPanelElement p = SPanelElement.getInstance();
      // Menu item Undo
      menuItem = undo = createMenuItem("Undo", "undo", KeyEvent.VK_U, KEY_UNDO,
              ACTION_UNDO, p.getUndoButton());
      menuItem.setEnabled(false);
      menu.add(menuItem);

      // Menu item Redo
      menuItem = redo = createMenuItem("Redo", "redo", KeyEvent.VK_R, KEY_REDO,
              ACTION_REDO, p.getRedoButton());
      menuItem.setEnabled(false);
      menu.add(menuItem);
    }

    menu.addSeparator();

    {
      final SPanelElement p = SPanelElement.getInstance();

      // Menu item Duplicate
      menuItem = createMenuItem("Duplicate", "duplicate", KeyEvent.VK_D,
              KEY_DUPLICATE, ACTION_DUPLICATE, p.getBtnDuplicate());
      menuItem.setEnabled(false);
      menu.add(menuItem);

      // Menu item Delete
      menuItem = createMenuItem("Delete", "delete", KeyEvent.VK_E, KEY_DELETE,
              ACTION_DELETE, p.getBtnDelete());
      menuItem.setEnabled(false);
      menu.add(menuItem);

      // Menu item Color
      menuItem = createMenuItem("Color", "color", KeyEvent.VK_C, KEY_COLOR,
              ACTION_COLOR, p.getBtnColor());
      menu.add(menuItem);

      // Menu item Recent Color
      menuItem = SColorAssigner.createMenuRecentColor();
      p.getBtnColor().linkComponent(menuItem);
      menu.add(menuItem);
    }

    menu.addSeparator();

    // Menu item Select all
    menuItem = createMenuItem("Select all", "select", KeyEvent.VK_S,
            KEY_SELECT_ALL, ACTION_SELECT_ALL);
    menu.add(menuItem);

    // Menu item Unselect all
    menuItem = createMenuItem("Unselect all", "unselect", KeyEvent.VK_N,
            KEY_UNSELECT_ALL, ACTION_UNSELECT_ALL);
    menu.add(menuItem);

    menu.addSeparator();

    {
      SPanelElement p = SPanelElement.getInstance();

      // Menu item adjust width
      menuItem = createMenuItemDisable("Adjust Classes Width", "adjustWidth",
              KeyEvent.VK_W, KEY_ADJUST_SIZE, ACTION_ADJUST_WIDTH,
              p.getBtnAdjust());
      menu.add(menuItem);

      // Menu item align top
      menuItem = createMenuItemDisable("Align Top", "alignTop", KeyEvent.VK_O,
              KEY_ALIGN_UP, ACTION_ALIGN_TOP, p.getBtnAlignTop());
      menu.add(menuItem);

      // Menu item align bottom
      menuItem = createMenuItemDisable("Align Bottom", "alignBottom",
              KeyEvent.VK_B, KEY_ALIGN_DOWN, ACTION_ALIGN_BOTTOM,
              p.getBtnAlignBottom());
      menu.add(menuItem);

      // Menu item align left
      menuItem = createMenuItemDisable("Align Left", "alignLeft",
              KeyEvent.VK_F, KEY_ALIGN_LEFT, ACTION_ALIGN_LEFT,
              p.getBtnAlignLeft());
      menu.add(menuItem);

      // Menu item align right
      menuItem = createMenuItemDisable("Align Righ", "alignRight",
              KeyEvent.VK_H, KEY_ALIGN_RIGHT, ACTION_ALIGN_RIGHT,
              p.getBtnAlignRight());
      menu.add(menuItem);
    }

    menu.addSeparator();

    {
      SPanelElement p = SPanelElement.getInstance();

      // Menu item top
      menuItem = createMenuItemDisable("Move top", "top", KeyEvent.VK_T,
              KEY_MOVE_TOP, ACTION_MOVE_TOP, p.getBtnTop());
      menu.add(menuItem);

      // Menu item up
      menuItem = createMenuItemDisable("Move up", "up", KeyEvent.VK_P,
              KEY_MOVE_UP, ACTION_MOVE_UP, p.getBtnUp());
      menu.add(menuItem);

      // Menu item down
      menuItem = createMenuItemDisable("Move down", "down", KeyEvent.VK_D,
              KEY_MOVE_DOWN, ACTION_MOVE_DOWN, p.getBtnDown());
      menu.add(menuItem);

      // Menu item bottom
      menuItem = createMenuItemDisable("Move Bottom", "bottom", KeyEvent.VK_M,
              KEY_MOVE_BOTTOM, ACTION_MOVE_BOTTOM, p.getBtnBottom());
      menu.add(menuItem);
    }
    
    {
      // Menu View
      menu = new JMenu("View");
      menu.setMnemonic(KeyEvent.VK_V);
      menuBar.add(menu);
      
      menuItemFullScreen = new JCheckBoxMenuItem("Diagram Only");
      menuItemFullScreen.setAccelerator(KeyStroke.getKeyStroke(KEY_FULL_SCREEN));
      menuItemFullScreen.setActionCommand(ACTION_FULL_SCREEN);
      menuItemFullScreen.addActionListener(this);
      menu.add(menuItemFullScreen);
      menu.addSeparator();
      
      // Sub Menu Open view
      menuOpenViews = new JMenu("Open View");
      menuOpenViews.setIcon(PersonalizedIcon.createImageIcon(
          Slyum.ICON_PATH + "element-view-open.png"));
      menuOpenViews.setMnemonic(KeyEvent.VK_O);
      menuOpenViews.setEnabled(false);
      menu.add(menuOpenViews);
      
      // Menu close
      menuItem = createMenuItem("Close View", "icon", KeyEvent.VK_C,
              KEY_CLOSE_VIEW, ACTION_CLOSE_VIEW);
      menu.add(menuItem);
      
      // Menu add
      menuItem = createMenuItem("Add View...", "element-view-add", KeyEvent.VK_A,
              KEY_ADD_VIEW, ACTION_ADD_VIEW);
      menu.add(menuItem);
      
      // Menu delete
      menuItem = createMenuItem("Delete View", "element-view-delete", KeyEvent.VK_D,
              KEY_DELETE_VIEW, ACTION_DELETE_VIEW);
      menu.add(menuItem);      
    }

    {
      SPanelDiagramComponent p = SPanelDiagramComponent.getInstance();

      // Menu Diagram
      menu = new JMenu("Diagram");
      menu.setMnemonic(KeyEvent.VK_D);
      menuBar.add(menu);

      // Sub Menu Zoom
      JMenu subMenu = new JMenu("Zoom");
      subMenu.setMnemonic(KeyEvent.VK_Z);
      menu.add(subMenu);

      // Menu item back zoom
      menuItem = createMenuItem("Zoom in", "zoomPlus", KeyEvent.VK_I,
              KEY_ZOOM_PLUS, ACTION_ZOOM_MINUS);
      subMenu.add(menuItem);

      // Menu item foreward zoom
      menuItem = createMenuItem("Zoom out", "zoomMinus", KeyEvent.VK_O,
              KEY_ZOOM_MINUS, ACTION_ZOOM_PLUS);
      subMenu.add(menuItem);

      subMenu.addSeparator();

      // Menu item adapte zoom
      menuItem = createMenuItem("Adapt diagram to window", "zoomAdapt",
              KeyEvent.VK_D, KEY_ZOOM_ADAPT, ACTION_ZOOM_ADAPT);
      subMenu.add(menuItem);

      // Menu item adapt zoom to selection
      menuItem = createMenuItem("Adapt selection to window", "", KeyEvent.VK_S,
              null, ACTION_ZOOM_ADAPT_SELECTION);
      subMenu.add(menuItem);

      subMenu.addSeparator();

      // Menu item Zoom0.5x
      menuItem = createMenuItem("1:1 (100 %)", "zoom1", 0, KEY_ZOOM_1,
              ACTION_ZOOM_1);
      subMenu.add(menuItem);

      subMenu.addSeparator();

      // Menu item Zoom1x
      menuItem = createMenuItem("1:2 (50 %)", "", 0, null, ACTION_ZOOM_0_5);
      subMenu.add(menuItem);

      // Menu item Zoom1.5x
      menuItem = createMenuItem("3:2 (150 %)", "", 0, null, ACTION_ZOOM_1_5);
      subMenu.add(menuItem);

      // Menu item Zoom2x
      menuItem = createMenuItem("2:1 (200 %)", "", 0, null, ACTION_ZOOM_2);
      subMenu.add(menuItem);
      menu.addSeparator();
      
      menuItem = new JMenuItem("Clean diagram");
      menuItem.setActionCommand(ACTION_CLEAN_DIAGRAM);
      menuItem.addActionListener(this);
      menu.add(menuItem);

      menu.addSeparator();
      // Menu item default mode
      menuItem = createMenuItem("Default cursor", "pointer-arrow",
              KeyEvent.VK_E, KEY_DEFAULT_MODE, ACTION_MODE_CURSOR,
              Mode.CURSOR.getBtnMode());
      menu.add(menuItem);

      // Menu item grips mode
      menuItem = createMenuItem("Add grips", "pointer-grip", KeyEvent.VK_G,
              KEY_GRIPS_MODE, ACTION_MODE_GRIP, Mode.GRIP.getBtnMode());
      menu.add(menuItem);

      menu.addSeparator();

      // Menu item add class
      menuItem = createMenuItem("Add Class", "class", KeyEvent.VK_C, KEY_CLASS,
              ACTION_NEW_CLASS, p.getBtnClass());
      menu.add(menuItem);

      // Menu item add interface
      menuItem = createMenuItem("Add Interface", "interface", KeyEvent.VK_I,
              KEY_INTERFACE, ACTION_NEW_INTERFACE, p.getBtnInterface());
      menu.add(menuItem);

      // Menu item add interface
      menuItem = createMenuItem("Add Enum", "enum", KeyEvent.VK_E,
              KEY_ENUM, ACTION_NEW_ENUM, p.getBtnEnum());
      menu.add(menuItem);

      // Menu item add class association
      menuItem = createMenuItem("Add Association class", "classAssoc",
              KeyEvent.VK_X, KEY_ASSOCIATION_CLASS,
              ACTION_NEW_CLASS_ASSOCIATION, p.getBtnAssociation());
      menu.add(menuItem);

      menu.addSeparator();

      // Menu item add generalize
      menuItem = createMenuItem("Add Inheritance", "generalize", KeyEvent.VK_H,
              KEY_INHERITANCE, ACTION_NEW_GENERALIZE, p.getBtnGeneralize());
      menu.add(menuItem);

      // Menu item add inner class
      menuItem = createMenuItem("Add inner class", "innerClass", KeyEvent.VK_N,
              KEY_INNER_CLASS, ACTION_NEW_INNER_CLASS, p.getBtnInnerClass());
      menu.add(menuItem);

      // Menu item add dependency
      menuItem = createMenuItem("Add Dependency", "dependency", KeyEvent.VK_E,
              KEY_DEPENDENCY, ACTION_NEW_DEPENDENCY, p.getBtnDependency());
      menu.add(menuItem);

      // Menu item add association
      menuItem = createMenuItem("Add Association", "association",
              KeyEvent.VK_S, KEY_ASSOCIATION, ACTION_NEW_ASSOCIATION,
              p.getBtnAssociation());
      menu.add(menuItem);

      // Menu item add aggregation
      menuItem = createMenuItem("Add Aggregation", "aggregation",
              KeyEvent.VK_G, KEY_AGGREGATION, ACTION_NEW_AGGREGATION,
              p.getBtnAggregation());
      menu.add(menuItem);

      // Menu item add composition
      menuItem = createMenuItem("Add Composition", "composition",
              KeyEvent.VK_M, KEY_COMPOSITION, ACTION_NEW_COMPOSITION,
              p.getBtnComposition());
      menu.add(menuItem);

      // Menu item add multi association
      menuItem = createMenuItem("Add Multi-association", "multi",
              KeyEvent.VK_W, KEY_MULTI_ASSOCIATION, ACTION_NEW_MULTI,
              p.getBtnMulti());
      menu.add(menuItem);

      menu.addSeparator();

      // Menu item add note
      menuItem = createMenuItem("Add Note", "note", KeyEvent.VK_N, KEY_NOTE,
              ACTION_NEW_NOTE, p.getBtnNote());
      menu.add(menuItem);

      // Menu item link note
      menuItem = createMenuItem("Link Note", "linkNote", KeyEvent.VK_L,
              KEY_LINK_NOTE, ACTION_NEW_LINK_NOTE, p.getBtnLinkNote());
      menu.add(menuItem);
    }

    // Menu Help
    menu = new JMenu("Help");
    menu.setMnemonic(KeyEvent.VK_H);
    menuBar.add(menu);

    // Menu item Help
    menuItem = createMenuItem("Help...", "help", KeyEvent.VK_E, KEY_HELP,
            ACTION_HELP);
    menu.add(menuItem);
    menu.addSeparator();

    // Menu item Update
    menuItem = createMenuItem("Go to project page...", "icon_16x16", KeyEvent.VK_P,
            null, ACTION_PROJECT_PAGE);
    menu.add(menuItem);
    
    menuItem = createMenuItem("Check for update...", "eggs-16", KeyEvent.VK_R,
            null, ACTION_UPDATE);
    menu.add(menuItem);
    
    menuItem = createMenuItem("See patch note...", "patchnote", KeyEvent.VK_S,
            null, ACTION_PATCH_NOTE);
    menu.add(menuItem);
    menu.addSeparator();
    
    menuItem = createMenuItem("Report issue / improvments...", 
        "bug", KeyEvent.VK_I, null, ACTION_REPORT_ISSUE);
    menu.add(menuItem);
    
    if (!OSValidator.IS_MAC) {
      menu.addSeparator();

      // Menu item About
      menuItem = createMenuItem("About Slyum...", "about", KeyEvent.VK_A, null,
                                                                          ACTION_ABOUT);
      menu.add(menuItem);
    }

    // Apply the menu bar.
    setJMenuBar(menuBar);
  }

  private String formatHistoryEntry(String entry) {
    final int VISIBLE_CAR = 10;
    Path p = Paths.get(entry);
    String parent = p.getParent().toString();
    String text = p.getFileName().toString();
    int parentLength = parent.length();

    if (parentLength > 20)
      text += " [" + parent.substring(0, VISIBLE_CAR) + "..."
              + parent.substring(parentLength - VISIBLE_CAR) + "]";
    else
      text += " [" + parent + "]";

    return text;
  }
  
  private void handleMacOSX() {
    if (OSValidator.IS_MAC) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      enableFullScreenMode(this);
      try {
        // Generate and register the OSXAdapter, passing it a hash of all the
        // methods we wish to
        // use as delegates for various com.apple.eawt.ApplicationListener
        // methods
        OSXAdapter.setQuitHandler(this,
                                  getClass().getDeclaredMethod("exit", (Class[]) null));
        OSXAdapter.setAboutHandler(this,
                                   getClass().getDeclaredMethod("openAbout", (Class[]) null));
        OSXAdapter.setPreferencesHandler(this,
                                         getClass().getDeclaredMethod("openProperties", (Class[]) null));
        // OSXAdapter.setFileHandler(this,
        // getClass().getDeclaredMethod("loadImageFile", new
        // Class[]{String.class}));
      } catch (NoSuchMethodException | SecurityException e) {
        System.err.println("Error while loading the OSXAdapter:");
        Writer sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
      }
    }
  }

  private void initEventListener() {
    addWindowListener(new WindowAdapter() {

      @Override
      public void windowActivated(WindowEvent e) {
        PanelClassDiagram.refresh();
      }
      
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        exit();
      }
    });
    
    addWindowFocusListener(new WindowFocusListener() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        PanelClassDiagram.refresh();
      }

      @Override
      public void windowLostFocus(WindowEvent e) {
        // nothing to do !
      }
    });
  }
  
  private void initFont() {
    defaultFont = DEFAULT_FONT;
  }
  
  /**
   * Open the help file.
   */
  private void openHelp() {
    openURL("https://github.com/HEIG-GAPS/slyum/blob/master/bin/utils/User_manual_FR.pdf");
  }

  /**
   * Initialize the properties of the frame.
   */
  private void setFrameProperties() {
    setName(APP_NAME);
    setTitle(getName());
    setIconImage(PersonalizedIcon.getLogo().getImage());
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setMinimumSize(new Dimension(400, 400));
    setSize(WINDOWS_SIZE);
    setExtendedState(WINDOWS_MAXIMIZED);
    setContentPane(PanelClassDiagram.getInstance());
  }


  private class JMenuItemHistory extends JMenuItem {
    private static final long serialVersionUID = -6696714308788403479L;
    private Path historyPath;

    public JMenuItemHistory(String text) {
      super(text);
    }

    public void setHistoryPath(Path path) {
      historyPath = path;
    }

    public Path getHistoryPath() {
      return historyPath;
    }
  }
  
  public static void openNewApplicationInstance() throws IOException, URISyntaxException {
    final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    final File currentJar = new File(Slyum.class.getProtectionDomain().getCodeSource().getLocation().toURI());

    /* is it a jar file? */
    if(!currentJar.getName().endsWith(".jar"))
      return;

    /* Build command: java -jar application.jar */
    final ArrayList<String> command = new ArrayList<>();
    command.add(javaBin);
    command.add("-jar");
    command.add(currentJar.getPath());
    command.add(ARGUMENT_OPEN_NEW_PROJECT);

    final ProcessBuilder builder = new ProcessBuilder(command);
    builder.start();
  }

}
