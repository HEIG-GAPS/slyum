package swing;

import com.apple.java.OSXAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import swing.SPanelDiagramComponent.Mode;
import update.UpdateInfo;
import utility.OSValidator;
import utility.PersonalizedIcon;
import utility.SMessageDialog;

/**
 * Main class! Create a new Instance of Slyum and display it. Create menu.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class Slyum extends JFrame {
  
  public final static String APP_NAME = "Slyum";
  public final static String VERSION = "4.1.0";
  public final static String EXTENTION = "sly";
  public final static String FULL_EXTENTION = String.format(".%s", EXTENTION);
  public final static String APP_DIR_NAME = APP_NAME;
  
  public final static String FILE_SEPARATOR = 
      System.getProperty("file.separator");
  
  public final static Point DEFAULT_SIZE = new Point(1024, 760);
  public static final Color TEXT_COLOR = new Color(34, 34, 34);
  public final static Color DEFAULT_BACKGROUND = new Color(239, 239, 242);
  public final static Color BACKGROUND_FORHEAD = new Color(246, 246, 246);
  public final static Color THEME_COLOR = new Color(0, 122, 204);
  
  public final static int MENU_SHORTCUT_KEY_MASK = 
      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  
  public final static Color DEFAULT_BORDER_COLOR = new Color(169, 169, 169);
  public static final Color DISABLE_COLOR = Color.GRAY;

  // Don't use the file separator here. Java resources are get with
  // getResource() and didn't support back-slash character on Windows.
  public final static String RESOURCES_PATH = "resources/";
  public final static String ICON_PATH = RESOURCES_PATH + "icon/";
  public final static String FONTS_PATH = RESOURCES_PATH + "fonts/";

  public final static int DEFAULT_FONT_SIZE = 12;

  private static final String URL_UPDATE_PAGE = 
      "https://drive.google.com/folderview?id=0B8LiFU0_u3AZdTRPY0JKallDRm8&usp=sharing";
  private static final String URL_PROJECT_PAGE = 
      "https://code.google.com/p/slyum/";
  private static final String URL_ISSUES_PAGE = 
      "https://code.google.com/p/slyum/issues/list";
  
  // Properties
  public final static boolean SHOW_CROSS_MENU = true;
  public final static boolean SHOW_ERRORS_MESSAGES = true;
  public final static boolean SHOW_OPENJDK_WARNING = true;
  public final static boolean VIEW_TITLE_ON_EXPORT_DEFAULT = true;
  public final static int WINDOWS_MAXIMIZED = Frame.MAXIMIZED_BOTH;
  
  public final static Dimension WINDOWS_SIZE = 
      new Dimension(DEFAULT_SIZE.x, DEFAULT_SIZE.y);
  
  public final static boolean IS_AUTO_ADJUST_INHERITANCE = true;
  public final static Mode MODE_CURSOR = Mode.CURSOR;
  
  public final static Font DEFAULT_FONT;
  public final static Font UI_FONT;
  
  private final static Logger LOGGER = Logger.getLogger(Slyum.class.getName());
  
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
  
  static {
    DEFAULT_FONT = addSystemFont("Ubuntu-R");
    UI_FONT = addSystemFont("segoeui");
  }

  private static final String ARGUMENT_PRINT_CHANGE_STACK_STATE = "-printChanges";
  private static final String ARGUMENT_EXIT_WITHOUT_ASK = "-exitWithoutAsk";

  private static Slyum instance;
  private static String windowTitle = APP_NAME;
  private static String[] arguments;

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

  public static boolean argumentIsChangeStackStatePrinted() {
    for (String s : arguments)
      if (s.equals(ARGUMENT_PRINT_CHANGE_STACK_STATE)) return true;

    return false;
  }

  public static boolean argumentExitWithoutAsk() {
    for (String s : arguments)
      if (s.equals(ARGUMENT_EXIT_WITHOUT_ASK)) return true;

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

  public static String getCurrentDirectoryFileChooser() {
    String defaultPath;

    defaultPath = PropertyLoader.getInstance().getProperties()
            .getProperty("PathForFileChooser");

    if (defaultPath == null) defaultPath = System.getProperty("user.home");

    return defaultPath;
  }

  public static Slyum getInstance() {
    return instance;
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

  public static boolean isShowOpenJDKWarning() {
    String prop = PropertyLoader.getInstance().getProperties()
            .getProperty("showOpenJDKWarning");
    boolean enable = SHOW_OPENJDK_WARNING;

    if (prop != null) enable = Boolean.parseBoolean(prop);

    return enable;
  }
  
  public static boolean isViewTitleOnExport() {
    String prop = PropertyLoader.getInstance().getProperties()
            .getProperty(PropertyLoader.VIEW_TITLE_ON_EXPORT);
    boolean enable = VIEW_TITLE_ON_EXPORT_DEFAULT;
    if (prop != null) enable = Boolean.parseBoolean(prop);
    return enable;
  }

  public static boolean isAutoAdjustInheritance() {
    String prop = PropertyLoader.getInstance().getProperties()
            .getProperty(PropertyLoader.AUTO_ADJUST_INHERITANCE);
    boolean enable = IS_AUTO_ADJUST_INHERITANCE;

    if (prop != null) enable = Boolean.parseBoolean(prop);

    return enable;
  }

  public static Mode getModeCursor() {
    String prop = PropertyLoader.getInstance().getProperties()
            .getProperty(PropertyLoader.MODE_CURSOR);
    Mode mode = MODE_CURSOR;

    if (prop != null) mode = Mode.valueOf(prop);

    return mode;
  }

  public static boolean isShowCrossMenu() {
    final String prop = PropertyLoader.getInstance().getProperties()
            .getProperty("ShowCrossMenu");
    boolean enable = SHOW_CROSS_MENU;

    if (prop != null) enable = Boolean.parseBoolean(prop);

    return enable;
  }

  public static boolean isShowErrorMessage() {
    final String prop = PropertyLoader.getInstance().getProperties()
            .getProperty("ShowErrorMessages");
    boolean enable = SHOW_ERRORS_MESSAGES;

    if (prop != null) enable = Boolean.parseBoolean(prop);

    return enable;
  }

  public static void setCurrentDirectoryFileChooser(String path) {
    PropertyLoader.getInstance().getProperties()
            .put("PathForFileChooser", String.valueOf(path));
    PropertyLoader.getInstance().push();
  }

  public static void setEnableRedoButtons(boolean enable) {
    SlyumAction.ACTION_REDO.setEnabled(enable);
  }

  public static void setEnableUndoButtons(boolean enable) {
    SlyumAction.ACTION_UNDO.setEnabled(enable);
  }

  public static void setShowCrossMenu(boolean show) {
    PropertyLoader.getInstance().getProperties().put("ShowCrossMenu", show);
    PropertyLoader.getInstance().push();
  }

  public static void setShowErrorMessage(boolean show) {
    PropertyLoader.getInstance().getProperties().put("ShowErrorMessages", show);
    PropertyLoader.getInstance().push();
  }

  public static void setShowOpenJDKWarning(boolean show) {
    PropertyLoader.getInstance().getProperties()
            .put("showOpenJDKWarning", show);
    PropertyLoader.getInstance().push();
  }

  public static void setSmallIcons(boolean use) {
    PropertyLoader.getInstance().getProperties().put("SmallIcon", use);
    PropertyLoader.getInstance().push();
  }

  public static void setStarOnTitle(boolean visible) {
    Slyum.getInstance().setTitle(windowTitle + (visible ? "*" : ""));
  }

  private static void showWarningForOpenJDK() {
    if (isShowOpenJDKWarning() && OSValidator.IS_UNIX)
      new NoRepopDialog(
              "Problems are observed with OpenJRE. If you use OpenJRE, we encourage you to get official Sun JRE.")
              .setVisible(true);
  }

  public static void updateWindowTitle(File projectName) {
    windowTitle = APP_NAME
            + (projectName == null ? "" : " - " + projectName.getPath());
    Slyum.getInstance().setTitle(windowTitle);
  }

  public Font defaultFont;
  private JMenuItem menuItemLocate;
  private JMenu menuFile;

  public JMenuItem getMenuItemLocate() {
    return menuItemLocate;
  }

  public Font _getDefaultFont() {
    return defaultFont;
  }

  public static Font getDefaultFont() {
    return getInstance()._getDefaultFont();
  }

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
        
        IssuesInformation.mustDisplayMessage();
      }
    });

    String file = argumentGetFile();
    if (file == null) file = RecentProjectManager.getMoreRecentFile();

    if (file != null) PanelClassDiagram.openSlyFile(file);
  }

  private void initFont() {
    defaultFont = DEFAULT_FONT;
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
  }

  public static void enableFullScreenMode(Window window) {
    String className = "com.apple.eawt.FullScreenUtilities";
    String methodName = "setWindowCanFullScreen";

    try {
      Class<?> clazz = Class.forName(className);
      Method method = clazz.getMethod(methodName, new Class<?>[]{
                Window.class, boolean.class});
      method.invoke(null, window, true);
    } catch (ClassNotFoundException | IllegalAccessException | 
             IllegalArgumentException | NoSuchMethodException | 
             SecurityException | InvocationTargetException t) {
      LOGGER.warning("Full screen mode is not supported");
    }
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

  public void openAbout() {
    new AboutBox(this);
  }

  public void openProperties() {
    new SProperties();
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

  public static void openURL(String url) {
    try {
      java.awt.Desktop.getDesktop().browse(new URI(url));
    } catch (URISyntaxException | IOException e) {
      SMessageDialog
              .showErrorMessage("Unable to open " + url + ".");
    }
  }
  
  public static void openProjectPage()
  {
    openURL(URL_PROJECT_PAGE);
  }
  
  public static void openIssuesPage()
  {
    openURL(URL_ISSUES_PAGE);
  }
  
  public static void openHelp() {
    openURL("https://docs.google.com/file/d/0B8LiFU0_u3AZX2NYSmpwMW9TdGc/edit");
  }

  /**
   * Create the JMenuBar
   */
  private void createJMenuBar() {
    final JMenuBar menuBar = new JMenuBar();
    menuBar.setBackground(DEFAULT_BACKGROUND);
    menuBar.setBorder(null);

    // Menu file
    JMenu menu = menuFile = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_F);
    
    menuBar.add(menu);
    menu.add(new JMenuItem(SlyumAction.ACTION_NEW_PROJECT));
    menu.add(new JMenuItem(SlyumAction.ACTION_OPEN_PROJECT));
    menu.addSeparator();
    menu.add(new JMenuItem(SlyumAction.ACTION_SAVE_PROJECT));
    menu.add(new JMenuItem(SlyumAction.ACTION_SAVE_AS_PROJECT));
    menu.addSeparator();
    menu.add(new JMenuItem(SlyumAction.ACTION_EXPORT_AS_IMAGE));
    menu.add(new JMenuItem(SlyumAction.ACTION_COPY_TO_CLIPBOARD));
    menu.add(new JMenuItem(SlyumAction.ACTION_LOCATE_IN));
    menu.addSeparator();
    menu.add(new JMenuItem(SlyumAction.ACTION_PRINT));
    menu.add(new JMenuItem(SlyumAction.ACTION_PAGE_SETUP));
    
    if (!OSValidator.IS_MAC) {
      menu.addSeparator();
      menu.add(new JMenuItem(SlyumAction.ACTION_PROPERTIES));
    }

    // Menu recent project
    updateMenuItemHistory();

    if (!OSValidator.IS_MAC) {
      menu.addSeparator();
      menu.add(new JMenuItem(SlyumAction.ACTION_EXIT));
    }

    // Menu edit
    menu = new JMenu("Edit");
    menu.setMnemonic(KeyEvent.VK_E);
    menuBar.add(menu);
    
    menu.add(new JMenuItem(SlyumAction.ACTION_UNDO));
    menu.add(new JMenuItem(SlyumAction.ACTION_REDO));
    SlyumAction.ACTION_UNDO.setEnabled(false);
    SlyumAction.ACTION_REDO.setEnabled(false);
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_COLOR));
    menu.add(new JMenuItem(SlyumAction.ACTION_DUPLICATE));
    menu.add(new JMenuItem(SlyumAction.ACTION_DELETE));
    SlyumAction.ACTION_DELETE.setEnabled(false);
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_SELECT_ALL));
    menu.add(new JMenuItem(SlyumAction.ACTION_UNSELECT_ALL));
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_ADJUST_SIZE));
    menu.add(new JMenuItem(SlyumAction.ACTION_ALIGN_TOP));
    menu.add(new JMenuItem(SlyumAction.ACTION_ALIGN_BOTTOM));
    menu.add(new JMenuItem(SlyumAction.ACTION_ALIGN_LEFT));
    menu.add(new JMenuItem(SlyumAction.ACTION_ALIGN_RIGHT));
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_MOVE_TOP));
    menu.add(new JMenuItem(SlyumAction.ACTION_MOVE_UP));
    menu.add(new JMenuItem(SlyumAction.ACTION_MOVE_DOWN));
    menu.add(new JMenuItem(SlyumAction.ACTION_MOVE_BOTTOM));
    menu.addSeparator();
    
    // Menu Diagram
    menu = new JMenu("Diagram");
    menu.setMnemonic(KeyEvent.VK_D);
    menuBar.add(menu);

    // Sub Menu Zoom
    JMenu subMenu = new JMenu("Zoom");
    subMenu.setMnemonic(KeyEvent.VK_Z);
    menu.add(subMenu);
    
    subMenu.add(new JMenuItem(SlyumAction.ACTION_ZOOM_IN));
    subMenu.add(new JMenuItem(SlyumAction.ACTION_ZOOM_OUT));
    subMenu.addSeparator();
    
    subMenu.add(new JMenuItem(SlyumAction.ACTION_ZOOM_ADAPT));
    subMenu.add(new JMenuItem(SlyumAction.ACTION_ZOOM_ADAPT_SELECTION));
    subMenu.addSeparator();
    
    subMenu.add(new JMenuItem(SlyumAction.ACTION_ZOOM_1));
    subMenu.add(new JMenuItem(SlyumAction.ACTION_ZOOM_0_5));
    subMenu.add(new JMenuItem(SlyumAction.ACTION_ZOOM_1_5));
    subMenu.add(new JMenuItem(SlyumAction.ACTION_ZOOM_2));
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_MODE_CURSOR));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_GRIPS));
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_CLASS));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_INTERFACE));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_ENUM));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_ASSOCIATION_CLASS));
    menu.addSeparator();

    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_INHERITANCE));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_INNER_CLASS));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_DEPENDENCY));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_ASSOCIATION));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_AGGREGATION));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_COMPOSITION));
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_MULTI_ASSOCIATION));
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_ADD_NOTE));
    menu.add(new JMenuItem(SlyumAction.ACTION_LINK_NOTE));

    // Menu Help
    menu = new JMenu("Help");
    menu.setMnemonic(KeyEvent.VK_H);
    menuBar.add(menu);
    
    menu.add(new JMenuItem(SlyumAction.ACTION_HELP));
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_PROJECT_PAGE));
    menu.add(new JMenuItem(SlyumAction.ACTION_UPDATE));
    menu.add(new JMenuItem(SlyumAction.ACTION_PATCH_NOTE));
    menu.addSeparator();
    
    menu.add(new JMenuItem(SlyumAction.ACTION_ISSUES));
    
    if (!OSValidator.IS_MAC) {
      menu.addSeparator();
      menu.add(new JMenuItem(SlyumAction.ACTION_ABOUT));
    }

    setJMenuBar(menuBar);
  }

  public void deleteMenuItemHistory() {
    boolean remove = false;
    for (int i = 0; i < menuFile.getItemCount(); i++) {
      JMenuItem m = menuFile.getItem(i);
      if (m != null && m instanceof JMenuItemHistory) {
        remove = true;
        menuFile.remove(m);
        i--;
      }
    }

    // Suppression du séparateur.
    if (remove) menuFile.remove((OSValidator.IS_MAC ? 10 : 14));
  }

  public void updateMenuItemHistory() {
    deleteMenuItemHistory();

    List<String> histories = RecentProjectManager.getHistoryList();

    if (histories.size() > 0)
      menuFile.insertSeparator((OSValidator.IS_MAC ? 10 : 14));

    for (String s : histories) {
      JMenuItemHistory menuItem = new JMenuItemHistory(formatHistoryEntry(s));
      menuItem.setAction(SlyumAction.ACTION_OPEN_RECENT_PROJECT);
      menuItem.setHistoryPath(Paths.get(s));
      menuFile.add(menuItem, (OSValidator.IS_MAC ? 10 : 15));
    }
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

  private void _exit() {
    // Save properties before closing.
    PanelClassDiagram.getInstance().saveSplitLocationInProperties();

    System.exit(0);
  }

  public static class JMenuItemHistory extends JMenuItem {
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
}
