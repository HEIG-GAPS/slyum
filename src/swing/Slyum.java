package swing;

import com.apple.java.OSXAdapter;
import graphic.GraphicView;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import swing.SPanelDiagramComponent.Mode;
import utility.OSValidator;
import utility.PersonalizedIcon;
import utility.SMessageDialog;

/**
 * Main class! Create a new Instance of Slyum and display it. Create menu.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class Slyum extends JFrame implements ActionListener {
  
	private static final long serialVersionUID = 1L;
	private static final String APP_NAME = "Slyum";
	public static final float version = 3f;
  public final static String EXTENTION = "sly";
  public final static String FULL_EXTENTION = String.format(".%s", EXTENTION);
	public final static String APP_DIR_NAME = APP_NAME;
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	public final static Point DEFAULT_SIZE = new Point(1024, 760);
	public final static Color DEFAULT_BACKGROUND = new Color(239, 239, 242);
	public final static Color BACKGROUND_FORHEAD = new Color(246, 246, 246);
	public final static Color THEME_COLOR = new Color(0, 122, 204);
  
  /**
   * Check that we are on Mac OS X.  This is crucial to loading and using the OSXAdapter class.
   */
  public static final boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os"));

	// Don't use the file separator here. Java resources are get with
	// getResource() and didn't support back-slash character on Windows.
	public final static String RESOURCES_PATH = "resources/";
  public final static String ICON_PATH = RESOURCES_PATH + "icon/";
  public final static String FONTS_PATCH = RESOURCES_PATH + "fonts/";
	
	public final static int DEFAULT_FONT_SIZE = 12;

	private static final String URL_UPDATE_PAGE = "http://code.google.com/p/slyum/downloads";
	
	// Properties
	public final static boolean SHOW_CROSS_MENU = true;
	public final static boolean SHOW_ERRORS_MESSAGES = true;
  public final static boolean SHOW_OPENJDK_WARNING = true;
  public final static int WINDOWS_MAXIMIZED = Frame.MAXIMIZED_BOTH;
  public final static Dimension WINDOWS_SIZE = new Dimension(DEFAULT_SIZE.x, DEFAULT_SIZE.y);
	public final static boolean IS_AUTO_ADJUST_INHERITANCE = true;
	public final static Mode MODE_CURSOR = Mode.CURSOR;
	
	// Action command
  public static final String ACTION_MODE_CURSOR = "ModeCursor";
  public static final String ACTION_MODE_GRIP = "ModeGrip";
	public static final String ACTION_ABOUT = "About";
	public static final String ACTION_HELP = "Help";
	public static final String ACTION_EXIT = "Exit";
	public static final String ACTION_OPEN_RECENT_RPOJECT = "openRecentProject";
	public static final String ACTION_PROPERTIES = "Properties";
	public static final String ACTION_UPDATE = "Update";
	public static final String ACTION_SELECT_ALL = "SelectAll";
	public static final String ACTION_UNSELECT_ALL = "UnselectAll";
	public static final String ACTION_NEW_PROJECT = "NewProject";
	public static final String ACTION_OPEN = "Open";
	public static final String ACTION_SAVE = "Save";
	public static final String ACTION_SAVE_AS = "SaveAs";
	public static final String ACTION_EXPORT = "Export";
	public static final String ACTION_KLIPPER = "Klipper";
	public static final String ACTION_PRINT = "Print";
	public static final String ACTION_NEW_LINK_NOTE = "LinkNote";
	public static final String ACTION_NEW_CLASS = "NewClass";
	public static final String ACTION_NEW_INTERFACE = "NewInterface";
	public static final String ACTION_NEW_GENERALIZE = "NewGeneralize";
	public static final String ACTION_NEW_INNER_CLASS = "NewInnerClass";
	public static final String ACTION_NEW_DEPENDENCY = "NewDependency";
	public static final String ACTION_NEW_ASSOCIATION = "NewAssociation";
	public static final String ACTION_NEW_AGGREGATION = "NewAggregation";
	public static final String ACTION_NEW_COMPOSITION = "NewComposition";
	public static final String ACTION_NEW_CLASS_ASSOCIATION = "NewClassAssociation";
	public static final String ACTION_NEW_MULTI = "NewMulti";
	public static final String ACTION_NEW_NOTE = "NewNote";
	public static final String ACTION_NEW_NOTE_ASSOCIED = "NewNoteAssocied";
	public static final String ACTION_ALIGN_TOP = "AlignTop";
	public static final String ACTION_ALIGN_BOTTOM = "AlignBottom";
	public static final String ACTION_ALIGN_LEFT = "AlignLeft";
	public static final String ACTION_ALIGN_RIGHT = "AlignRight";
	public static final String ACTION_ADJUST_WIDTH = "AdjustWidth";
	public static final String ACTION_UNDO = "Undo";
	public static final String ACTION_REDO = "Redo";
	public static final String ACTION_MOVE_TOP = "MoveTop";
	public static final String ACTION_MOVE_UP = "MoveUp";
	public static final String ACTION_MOVE_DOWN = "MoveDown";
	public static final String ACTION_MOVE_BOTTOM = "MoveBottom";
	public static final String ACTION_COLOR = "Color";
  public static final String ACTION_DELETE = "Delete";
  public static final String ACTION_DUPLICATE = "duplicate";
  
  public static final String ACTION_ADJUST_INHERITANCE = "adjust-inheritance";
	
	public static final String ACTION_ZOOM_ADAPT = "ZoomAdapt";
	public static final String ACTION_ZOOM_ADAPT_SELECTION = "ZoomAdaptSelection";
	public static final String ACTION_ZOOM_PLUS = "Zoom +";
	public static final String ACTION_ZOOM_MINUS = "Zoom -";
	public static final String ACTION_ZOOM_0_5 = "Zoom 0.5x";
	public static final String ACTION_ZOOM_1 = "Zoom 1x";
	public static final String ACTION_ZOOM_1_5 = "Zoom 1.5x";
	public static final String ACTION_ZOOM_2 = "Zoom 2x";

	public static final String ACTION_TEXTBOX_UP = "MoveTextBoxUp";
	public static final String ACTION_TEXTBOX_DOWN = "MoveTextBoxDown";
  
	// Accelerator
	public final static String KEY_NEW_PROJECT = "ctrl alt N";
	public final static String KEY_OPEN_PROJECT = "ctrl O";
	public final static String KEY_SAVE = "ctrl S";
	public final static String KEY_SAVE_AS = "ctrl shift S";
	public final static String KEY_EXPORT = "ctrl E";
	public final static String KEY_KLIPPER = "ctrl C";
	public final static String KEY_PRINT = "ctrl P";
	public final static String KEY_PROPERTIES = "alt ENTER";
	public final static String KEY_EXIT = "alt F4";

	public final static String KEY_UNDO = "ctrl Z";
	public final static String KEY_REDO = "ctrl Y";
	public static final String KEY_SELECT_ALL = "ctrl A";
	public static final String KEY_UNSELECT_ALL = "ctrl U";

  public static final String KEY_COLOR = "ctrl L";
  public static final String KEY_DUPLICATE = "ctrl D";
  public static final String KEY_DELETE = "DELETE";
	
	public final static String KEY_ADJUST_SIZE = "ctrl 1";
	public final static String KEY_ALIGN_UP = "ctrl UP";
	public final static String KEY_ALIGN_DOWN = "ctrl DOWN";
	public final static String KEY_ALIGN_LEFT = "ctrl LEFT";
	public final static String KEY_ALIGN_RIGHT = "ctrl RIGHT";
	
	public static final String KEY_MOVE_TOP = "ctrl alt UP";
	public static final String KEY_MOVE_UP = "ctrl alt RIGHT";
	public static final String KEY_MOVE_DOWN = "ctrl alt LEFT";
	public static final String KEY_MOVE_BOTTOM = "ctrl alt DOWN";
	
	public final static String KEY_ZOOM_PLUS = "ctrl PLUS";
	public final static String KEY_ZOOM_MINUS = "ctrl MINUS";
	public final static String KEY_ZOOM_ADAPT = "ctrl shift E";
	public final static String KEY_ZOOM_1 = "1";

  public final static String KEY_DEFAULT_MODE = "alt Q";
  public final static String KEY_GRIPS_MODE = "alt W";
	public final static String KEY_CLASS = "ctrl shift C";
	public final static String KEY_INTERFACE = "ctrl shift I";
	public final static String KEY_ASSOCIATION_CLASS = "ctrl shift X";
	public final static String KEY_INHERITANCE = "ctrl shift H";
	public final static String KEY_INNER_CLASS = "ctrl shift N";
	public final static String KEY_DEPENDENCY = "ctrl shift D";
	public final static String KEY_ASSOCIATION = "ctrl shift A";
	public final static String KEY_AGGREGATION = "ctrl shift G";
	public final static String KEY_COMPOSITION = "ctrl shift P";
	public final static String KEY_MULTI_ASSOCIATION = "ctrl shift W";
	public final static String KEY_NOTE = "ctrl shift O";
	public final static String KEY_LINK_NOTE = "ctrl shift L";

	public final static String KEY_HELP = "F1";
	
  private static final String ARGUMENT_PRINT_CHANGE_STACK_STATE = "-printChanges";

	private static Slyum instance;
	private static JMenuItem undo, redo;

	private static String windowTitle = APP_NAME;
	
	private static String[] arguments;
	private JMenu menuFile;

	public static void main(String[] args)
	{
	  arguments = args;
	  
		showWarningForOpenJDK();
		
		// Hack pour mettre à jour le fichier de configuration.
		if (PropertyLoader.getInstance().getProperties()
        .getProperty(PropertyLoader.DIVIDER_BOTTOM) == null)
		  PropertyLoader.getInstance().reset();
	
		instance = new Slyum();
	    
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        instance.initializationComplete();
      }
    });
	}
	
	public static boolean isChangeStackStatePrinted()
	{
	  for (String s : arguments)
	    if (s.equals(ARGUMENT_PRINT_CHANGE_STACK_STATE))
	      return true;
	  
	  return false;
	}

	/**
	 * Create the application directory.
	 * @param path the path where create the application directory.
	 */
	public static void createAppDir(String path)
	{
		final File appDir = new File(path);

		if (appDir.mkdir())

			System.out.println("Application directory created.");

		else
		{
			System.err.println("Application directory not created.");
			SMessageDialog.showErrorMessage("Error to create application directory.");
		}
	}

	public static String getCurrentDirectoryFileChooser()
	{
		String defaultPath = null;

		defaultPath = PropertyLoader.getInstance().getProperties().getProperty("PathForFileChooser");

		if (defaultPath == null)
			defaultPath = System.getProperty("user.home");

		return defaultPath;
	}

	public static Slyum getInstance()
	{
		return instance;
	}

	public static String getPathAppDir()
	{
		String fileName;
		final String appData = System.getenv("APPDATA");
		final String userHome = System.getProperty("user.home");

		if (appData == null)

			fileName = userHome + FILE_SEPARATOR + "." + APP_DIR_NAME;

		else

			fileName = appData + FILE_SEPARATOR + APP_DIR_NAME;

		if (!new File(fileName).exists())

			createAppDir(fileName);

		return fileName;
	}

	public static boolean isShowOpenJDKWarning() {
		String prop = PropertyLoader.getInstance().getProperties().getProperty("showOpenJDKWarning");
		boolean enable = SHOW_OPENJDK_WARNING;

		if (prop != null)
			enable = Boolean.parseBoolean(prop);

		return enable;
	}
	
	public static int getExtendedStateSaved() {
    String prop = PropertyLoader.getInstance().getProperties().getProperty(PropertyLoader.WINDOWS_MAXIMIZED);
    int state = WINDOWS_MAXIMIZED;

    if (prop != null)
      state = Integer.parseInt(prop);

    return state;
	}
	
	public static Dimension getSizeSaved() {
    String prop = PropertyLoader.getInstance().getProperties().getProperty(PropertyLoader.WINDOWS_SIZE);
    Dimension state = WINDOWS_SIZE;

    if (prop != null && getExtendedStateSaved() != MAXIMIZED_BOTH) {
      String[] size = prop.split(",");
      state.width = Integer.parseInt(size[0]);
      state.height = Integer.parseInt(size[1]);
    }

    return state;
	}
	
	public static boolean isAutoAdjustInheritance() {
    String prop = PropertyLoader.getInstance().
        getProperties().getProperty(PropertyLoader.AUTO_ADJUST_INHERITANCE);
    boolean enable = IS_AUTO_ADJUST_INHERITANCE;

    if (prop != null)
      enable = Boolean.parseBoolean(prop);

    return enable; 
	}
	
	public static Mode getModeCursor() {
    String prop = PropertyLoader.getInstance().
        getProperties().getProperty(PropertyLoader.MODE_CURSOR);
    Mode mode = MODE_CURSOR;

    if (prop != null)
      mode = Mode.valueOf(prop);

    return mode; 
	}

	public static boolean isShowCrossMenu()
	{
		final String prop = PropertyLoader.getInstance().getProperties().getProperty("ShowCrossMenu");
		boolean enable = SHOW_CROSS_MENU;

		if (prop != null)
			enable = Boolean.parseBoolean(prop);

		return enable;
	}

	public static boolean isShowErrorMessage()
	{
		final String prop = PropertyLoader.getInstance().getProperties().getProperty("ShowErrorMessages");
		boolean enable = SHOW_ERRORS_MESSAGES;

		if (prop != null)
			enable = Boolean.parseBoolean(prop);

		return enable;
	}

	public static void setCurrentDirectoryFileChooser(String path)
	{
		PropertyLoader.getInstance().getProperties().put("PathForFileChooser", String.valueOf(path));
		PropertyLoader.getInstance().push();
	}

	public static void setEnableRedoButtons(boolean enable)
	{
		PanelClassDiagram.getInstance().getRedoButton().setEnabled(enable);
		redo.setEnabled(enable);
	}

	public static void setEnableUndoButtons(boolean enable)
	{
		PanelClassDiagram.getInstance().getUndoButton().setEnabled(enable);
		undo.setEnabled(enable);
	}

	public static void setShowCrossMenu(boolean show)
	{
		PropertyLoader.getInstance().getProperties().put("ShowCrossMenu", show);
		PropertyLoader.getInstance().push();
	}

	public static void setShowErrorMessage(boolean show)
	{
		PropertyLoader.getInstance().getProperties().put("ShowErrorMessages", show);
		PropertyLoader.getInstance().push();
	}

	public static void setShowOpenJDKWarning(boolean show)
	{
		PropertyLoader.getInstance().getProperties().put("showOpenJDKWarning", show);
		PropertyLoader.getInstance().push();
	}

	public static void setSmallIcons(boolean use)
	{
		PropertyLoader.getInstance().getProperties().put("SmallIcon", use);
		PropertyLoader.getInstance().push();
	}

	public static void setStarOnTitle(boolean visible)
	{
		Slyum.getInstance().setTitle(windowTitle + (visible ? "*" : ""));
	}

	private static void showWarningForOpenJDK()
	{
		if (isShowOpenJDKWarning() && OSValidator.isUnix())
			new NoRepopDialog("Problems are observed with OpenJRE. If you use OpenJRE, we encourage you to get official Sun JRE.").setVisible(true);
	}

	public static void updateWindowTitle(File projectName)
	{
		windowTitle = APP_NAME + (projectName == null ? "" : " - " + projectName.getPath());
	}

	public Font defaultFont;
	
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
		setUIProperties();
		createJMenuBar();
		setFrameProperties();
		initEventListener();
	}
	
	public void initializationComplete() {
    setSize(getSizeSaved());
    setExtendedState(getExtendedStateSaved());	  
	  SPanelDiagramComponent.getInstance().setMode(getModeCursor());
    instance.setVisible(true);

    // Locate dividers.
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        
        PanelClassDiagram panel = PanelClassDiagram.getInstance();
        Properties properties = PropertyLoader.getInstance().getProperties();
        String dividerBottom = properties.getProperty(PropertyLoader.DIVIDER_BOTTOM),
               dividerLeft = properties.getProperty(PropertyLoader.DIVIDER_LEFT);
        
        if (dividerBottom != null)
          panel.setDividerBottom(Float.valueOf(dividerBottom));
        
        if (dividerLeft != null)
          panel.setDividerLeft(Float.valueOf(dividerLeft));
      }
    });

    String file = RecentProjectManager.getMoreRecentFile();
    if (file != null)
      PanelClassDiagram.openSlyFile(file);
	}
	
	private void initFont()
	{
	  try {
      defaultFont = 
          Font.createFont(
              Font.TRUETYPE_FONT, 
              Slyum.class.getResource(
                  String.format("%ssegoeui.ttf", 
                  FONTS_PATCH)).openStream()).deriveFont(Font.PLAIN, DEFAULT_FONT_SIZE);
    } catch (FontFormatException | IOException e) {
      e.printStackTrace();
      defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, DEFAULT_FONT_SIZE);
    }
	}
	
	private void initEventListener() {
    addWindowListener(new WindowAdapter() {
      
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        exit();
      }
    });
	}
  
  private void handleMacOSX() {
    if (MAC_OS_X) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      try {
        // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
        // use as delegates for various com.apple.eawt.ApplicationListener methods
        OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("exit", (Class[]) null));
        OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("openAbout", (Class[]) null));
        OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("openProperties", (Class[]) null));
        //OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[]{String.class}));
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
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		PanelClassDiagram p = PanelClassDiagram.getInstance();
		GraphicView gv = p.getCurrentGraphicView();
		
    switch (e.getActionCommand()) {
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
      		PanelClassDiagram.getInstance().openFromXmlAndAsk(new File(((JMenuItemHistory)e.getSource()).getHistoryPath().toString()));
      	else
      		SMessageDialog.showErrorMessage("An error occured while opening project. Please report.");
          break;
      case ACTION_PROPERTIES:
        openProperties();
        break;
      case ACTION_UPDATE:
        openURL(URL_UPDATE_PAGE);
        break;
      case ACTION_SELECT_ALL:
        gv.selectAll();
        break;
      case ACTION_UNSELECT_ALL:
        gv.unselectAll();
        break;
			case ACTION_ZOOM_PLUS:
				PanelClassDiagram.getInstance().getCurrentGraphicView().forwardScale();
				break;
			case ACTION_ZOOM_MINUS:
				PanelClassDiagram.getInstance().getCurrentGraphicView().backScale();
				break;
			case ACTION_ZOOM_ADAPT:
				PanelClassDiagram.getInstance().getCurrentGraphicView().adaptDiagramToWindow();
				break;
			case ACTION_ZOOM_ADAPT_SELECTION:
				PanelClassDiagram.getInstance().getCurrentGraphicView().adaptSelectionToWindow();
				break;
			case ACTION_ZOOM_1:
				PanelClassDiagram.getInstance().getCurrentGraphicView().setScale(1.0);
				break;
			case ACTION_ZOOM_0_5:
				PanelClassDiagram.getInstance().getCurrentGraphicView().setScale(0.5);
				break;
			case ACTION_ZOOM_1_5:
				PanelClassDiagram.getInstance().getCurrentGraphicView().setScale(1.5);
				break;
			case ACTION_ZOOM_2:
				PanelClassDiagram.getInstance().getCurrentGraphicView().setScale(2.0);
				break;
    }
	}

	/**
	 * Initialize the properties of the frame.
	 */
	private void setFrameProperties()
	{
		setName(APP_NAME);
		setTitle(getName());
		setIconImage(PersonalizedIcon.getLogo().getImage());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(400, 400));
		setContentPane(PanelClassDiagram.getInstance());
	}
	
	/**
	 * Initialize the properties of Slyum.
	 */
	private void setUIProperties()
	{
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");
    
    Font f = defaultFont.deriveFont(13.0f);
    UIManager.put("Button.font", f);
    UIManager.put("Label.font", f);
    UIManager.put("CheckBox.font", f);
    UIManager.put("RadioButton.font", f);
    UIManager.put("TabbedPane.font", f);
    UIManager.put("TitledBorder.font", f);
    UIManager.put("List.font", f);
    UIManager.put("Menu.font", f.deriveFont(14.0f));
    UIManager.put("MenuItem.font", f);
    UIManager.put("RadioButtonMenuItem.font", f);
    UIManager.put("ComboBox.font", f);
    UIManager.put("Table.font", f);
    UIManager.put("TextField.font", f);
    UIManager.put("OptionPane.informationIcon", PersonalizedIcon.getInfoIcon());
    UIManager.put("OptionPane.errorIcon", PersonalizedIcon.getErrorIcon());
    UIManager.put("OptionPane.warningIcon", PersonalizedIcon.getWarningIcon());
    UIManager.put("OptionPane.questionIcon", PersonalizedIcon.getQuestionIcon());
	}

	public static void openURL(String url)
	{
		try
		{
			java.awt.Desktop.getDesktop().browse(new URI(url));
		}
		catch (URISyntaxException | IOException e)
		{
			SMessageDialog.showErrorMessage("Unable to open " + URL_UPDATE_PAGE + ".");
		}
	}

	/**
	 * Create the JMenuBar
	 */
	private void createJMenuBar()
	{
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
			menuItem = createMenuItem("New Project", "new", KeyEvent.VK_J, KEY_NEW_PROJECT, ACTION_NEW_PROJECT, p.getBtnNewProject());
			menu.add(menuItem);
	
			// Menu item open project
			menuItem = createMenuItem("Open Project...", "open", KeyEvent.VK_O, KEY_OPEN_PROJECT, ACTION_OPEN, p.getBtnOpen());
			menu.add(menuItem);
	
			menu.addSeparator();
	
			// Menu item save
			menuItem = createMenuItem("Save", "save", KeyEvent.VK_S, KEY_SAVE, ACTION_SAVE, p.getBtnSave());
			menu.add(menuItem);
	
			// Menu item save as...
			menuItem = createMenuItem("Save As...", "save-as", KeyEvent.VK_A, KEY_SAVE_AS, ACTION_SAVE_AS);
			menu.add(menuItem);
	
			menu.addSeparator();
	
			// Menu item Export as image...
			menuItem = createMenuItem("Export as image...", "export", KeyEvent.VK_M, KEY_EXPORT, ACTION_EXPORT, p.getBtnExport());
			menu.add(menuItem);
			
			// Menu item Copy to clipboard
			menuItem = createMenuItem("Copy selection to clipboard", "klipper", KeyEvent.VK_K, KEY_KLIPPER, ACTION_KLIPPER, p.getBtnKlipper());
			menu.add(menuItem);
	
			menu.addSeparator();
	
			// Menu item print
			menuItem = createMenuItem("Print...", "print", KeyEvent.VK_P, KEY_PRINT, ACTION_PRINT, p.getBtnPrint());
			menu.add(menuItem);
	
      if (!MAC_OS_X) {
        menu.addSeparator();

        // Menu item Properties
        menuItem = createMenuItem("Properties...", "properties", KeyEvent.VK_R, KEY_PROPERTIES, ACTION_PROPERTIES);
        menu.add(menuItem);
      }
			
			// Menu recent project
			updateMenuItemHistory();
			
      if (!MAC_OS_X) {
        menu.addSeparator();

        // Menu item exit
        menuItem = createMenuItem("Exit", "exit", KeyEvent.VK_X, KEY_EXIT, ACTION_EXIT);
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
			menuItem = undo = createMenuItem("Undo", "undo", KeyEvent.VK_U, KEY_UNDO, ACTION_UNDO, p.getUndoButton());
			menuItem.setEnabled(false);
			menu.add(menuItem);

			// Menu item Redo
			menuItem = redo = createMenuItem("Redo", "redo", KeyEvent.VK_R, KEY_REDO, ACTION_REDO, p.getRedoButton());
			menuItem.setEnabled(false);
			menu.add(menuItem);
		}

    menu.addSeparator();

    {
      final SPanelElement p = SPanelElement.getInstance();
      
      // Menu item Color
      menuItem = createMenuItem("Color", "color", 
          KeyEvent.VK_C, KEY_COLOR, ACTION_COLOR, p.getBtnColor());
      menu.add(menuItem);
      
      // Menu item Duplicate
      menuItem = createMenuItem("Duplicate", "duplicate", 
          KeyEvent.VK_D, KEY_DUPLICATE, ACTION_DUPLICATE, p.getBtnDuplicate());
      menuItem.setEnabled(false);
      menu.add(menuItem);
      
      // Menu item Delete
      menuItem = createMenuItem("Delete", "delete", 
          KeyEvent.VK_E, KEY_DELETE, ACTION_DELETE, p.getBtnDelete());
      menuItem.setEnabled(false);
      menu.add(menuItem);
    }

		menu.addSeparator();

		// Menu item Select all
		menuItem = createMenuItem("Select all", "select", KeyEvent.VK_S, KEY_SELECT_ALL, ACTION_SELECT_ALL);
		menu.add(menuItem);

		// Menu item Unselect all
		menuItem = createMenuItem("Unselect all", "unselect", KeyEvent.VK_N, KEY_UNSELECT_ALL, ACTION_UNSELECT_ALL);
		menu.add(menuItem);

		menu.addSeparator();

		{
		  SPanelElement p = SPanelElement.getInstance();
			
			// Menu item adjust width
			menuItem = createMenuItemDisable("Adjust Classes Width", "adjustWidth", KeyEvent.VK_W, KEY_ADJUST_SIZE, ACTION_ADJUST_WIDTH, p.getBtnAdjust());
			menu.add(menuItem);
	
			// Menu item align top
			menuItem = createMenuItemDisable("Align Top", "alignTop", KeyEvent.VK_O, KEY_ALIGN_UP, ACTION_ALIGN_TOP, p.getBtnAlignTop());
			menu.add(menuItem);
	
			// Menu item align bottom
			menuItem = createMenuItemDisable("Align Bottom", "alignBottom", KeyEvent.VK_B, KEY_ALIGN_DOWN, ACTION_ALIGN_BOTTOM, p.getBtnAlignBottom());
			menu.add(menuItem);
	
			// Menu item align left
			menuItem = createMenuItemDisable("Align Left", "alignLeft", KeyEvent.VK_F, KEY_ALIGN_LEFT, ACTION_ALIGN_LEFT, p.getBtnAlignLeft());
			menu.add(menuItem);
	
			// Menu item align right
			menuItem = createMenuItemDisable("Align Righ", "alignRight", KeyEvent.VK_H, KEY_ALIGN_RIGHT, ACTION_ALIGN_RIGHT, p.getBtnAlignRight());
			menu.add(menuItem);
		}

		menu.addSeparator();
		
		{
		  SPanelElement p = SPanelElement.getInstance();
			
			// Menu item top
			menuItem = createMenuItemDisable("Move top", "top", KeyEvent.VK_T, KEY_MOVE_TOP, ACTION_MOVE_TOP, p.getBtnTop());
			menu.add(menuItem);
	
			// Menu item up
			menuItem = createMenuItemDisable("Move up", "up", KeyEvent.VK_P, KEY_MOVE_UP, ACTION_MOVE_UP, p.getBtnUp());
			menu.add(menuItem);
	
			// Menu item down
			menuItem = createMenuItemDisable("Move down", "down", KeyEvent.VK_D, KEY_MOVE_DOWN, ACTION_MOVE_DOWN, p.getBtnDown());
			menu.add(menuItem);
	
			// Menu item bottom
			menuItem = createMenuItemDisable("Move Bottom", "bottom", KeyEvent.VK_M, KEY_MOVE_BOTTOM, ACTION_MOVE_BOTTOM, p.getBtnBottom());
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
			menuItem = createMenuItem("Zoom in", "zoomPlus", KeyEvent.VK_I, KEY_ZOOM_PLUS, ACTION_ZOOM_MINUS);
			subMenu.add(menuItem);
			
			// Menu item foreward zoom
			menuItem = createMenuItem("Zoom out", "zoomMinus", KeyEvent.VK_O, KEY_ZOOM_MINUS, ACTION_ZOOM_PLUS);
			subMenu.add(menuItem);
			
			subMenu.addSeparator();
			
			// Menu item adapte zoom
			menuItem = createMenuItem("Adapt diagram to window", "zoomAdapt", KeyEvent.VK_D, KEY_ZOOM_ADAPT, ACTION_ZOOM_ADAPT);
			subMenu.add(menuItem);
			
			// Menu item adapt zoom to selection
			menuItem = createMenuItem("Adapt selection to window", "", KeyEvent.VK_S, null, ACTION_ZOOM_ADAPT_SELECTION);
			subMenu.add(menuItem);
			
			subMenu.addSeparator();
			
			// Menu item Zoom0.5x
			menuItem = createMenuItem("1:1 (100 %)", "zoom1", 0, KEY_ZOOM_1, ACTION_ZOOM_1);
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
      // Menu item default mode
      menuItem = createMenuItem("Default cursor", "pointer-arrow", KeyEvent.VK_E, KEY_DEFAULT_MODE, ACTION_MODE_CURSOR, Mode.CURSOR.getBtnMode());
      menu.add(menuItem);

      // Menu item grips mode
      menuItem = createMenuItem("Add grips", "pointer-grip", KeyEvent.VK_G, KEY_GRIPS_MODE, ACTION_MODE_GRIP, Mode.GRIP.getBtnMode());
      menu.add(menuItem);
      
      menu.addSeparator();
	
			// Menu item add class
			menuItem = createMenuItem("Add Class", "class", KeyEvent.VK_C, KEY_CLASS, ACTION_NEW_CLASS, p.getBtnClass());
			menu.add(menuItem);
	
			// Menu item add interface
			menuItem = createMenuItem("Add Interface", "interface", KeyEvent.VK_I, KEY_INTERFACE, ACTION_NEW_INTERFACE, p.getBtnInterface());
			menu.add(menuItem);
	
			// Menu item add class association
			menuItem = createMenuItem("Add Association class", "classAssoc", KeyEvent.VK_X, KEY_ASSOCIATION_CLASS, ACTION_NEW_CLASS_ASSOCIATION, p.getBtnAssociation());
			menu.add(menuItem);
	
			menu.addSeparator();
	
			// Menu item add generalize
			menuItem = createMenuItem("Add Inheritance", "generalize", KeyEvent.VK_H, KEY_INHERITANCE, ACTION_NEW_GENERALIZE, p.getBtnGeneralize());
			menu.add(menuItem);
	
			// Menu item add inner class
			menuItem = createMenuItem("Add inner class", "innerClass", KeyEvent.VK_N, KEY_INNER_CLASS, ACTION_NEW_INNER_CLASS, p.getBtnInnerClass());
			menu.add(menuItem);
	
			// Menu item add dependency
			menuItem = createMenuItem("Add Dependency", "dependency", KeyEvent.VK_E, KEY_DEPENDENCY, ACTION_NEW_DEPENDENCY, p.getBtnDependency());
			menu.add(menuItem);
	
			// Menu item add association
			menuItem = createMenuItem("Add Association", "association", KeyEvent.VK_S, KEY_ASSOCIATION, ACTION_NEW_ASSOCIATION, p.getBtnAssociation());
			menu.add(menuItem);
	
			// Menu item add aggregation
			menuItem = createMenuItem("Add Aggregation", "aggregation", KeyEvent.VK_G, KEY_AGGREGATION, ACTION_NEW_AGGREGATION, p.getBtnAggregation());
			menu.add(menuItem);
	
			// Menu item add composition
			menuItem = createMenuItem("Add Composition", "composition", KeyEvent.VK_M, KEY_COMPOSITION, ACTION_NEW_COMPOSITION, p.getBtnComposition());
			menu.add(menuItem);
	
			// Menu item add multi association
			menuItem = createMenuItem("Add Multi-association", "multi", KeyEvent.VK_W, KEY_MULTI_ASSOCIATION, ACTION_NEW_MULTI, p.getBtnMulti());
			menu.add(menuItem);
	
			menu.addSeparator();
	
			// Menu item add note
			menuItem = createMenuItem("Add Note", "note", KeyEvent.VK_N, KEY_NOTE, ACTION_NEW_NOTE, p.getBtnNote());
			menu.add(menuItem);
	
			// Menu item link note
			menuItem = createMenuItem("Link Note", "linkNote", KeyEvent.VK_L, KEY_LINK_NOTE, ACTION_NEW_LINK_NOTE, p.getBtnLinkNote());
			menu.add(menuItem);
		}

		// Menu Help
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);

		// Menu item Help
		menuItem = createMenuItem("Help...", "help", KeyEvent.VK_E, KEY_HELP, ACTION_HELP);
		menu.add(menuItem);

		// Menu item Update
		menuItem = createMenuItem("Go to update page...", "update", KeyEvent.VK_U, null, ACTION_UPDATE);
		menu.add(menuItem);

    if (!MAC_OS_X) {
      menu.addSeparator();

      // Menu item About
      menuItem = createMenuItem("About Slyum...", "about", KeyEvent.VK_A, null, ACTION_ABOUT);
      menu.add(menuItem);
    }
		
		// Apply the menu bar.
    setJMenuBar(menuBar);
	}
	
	public void deleteMenuItemHistory()
	{
		boolean remove = false;
        for (int i = 0; i < menuFile.getItemCount(); i++)
        {
            JMenuItem m = menuFile.getItem(i);
            if (m != null && m.getActionCommand().equals(ACTION_OPEN_RECENT_RPOJECT))
            {
            	remove= true;
                menuFile.remove(m);
                i--;
            }
        }
        
        // Suppression du sÃ©parateur.
        if (remove)
        	menuFile.remove((MAC_OS_X ? 8 : 12));
	}
	
	public void updateMenuItemHistory()
	{
	    deleteMenuItemHistory();
	    
	    List<String> histories = RecentProjectManager.getHistoryList();
		
	    if (histories.size() > 0)
	    	menuFile.add(new JSeparator(), (MAC_OS_X ? 8 : 12));
	    
        for (String s : histories)
        {
        	JMenuItemHistory menuItem = new JMenuItemHistory(formatHistoryEntry(s));
        	menuItem.setActionCommand(ACTION_OPEN_RECENT_RPOJECT);
        	menuItem.addActionListener(this);
        	menuItem.setHistoryPath(Paths.get(s));
    		
            menuFile.add(menuItem, 9);
        }
	}
	
	private String formatHistoryEntry(String entry)
	{
		final int VISIBLE_CAR = 10;
		Path p = Paths.get(entry);
		String parent = p.getParent().toString();
		String text = p.getFileName().toString();
		int parentLength = parent.length();
		
		if (parentLength > 20)
			text += " [" + parent.substring(0, VISIBLE_CAR) + "..." + parent.substring(parentLength - VISIBLE_CAR) + "]";
		else
			text += " [" + parent + "]";
		
		return text;
	}
	
	public JMenuItem createMenuItem(String text, String iconName, int mnemonic,
	    String accelerator, String actionCommand, ActionListener al) {
		JMenuItem item;
		final String imgLocation = ICON_PATH + iconName + ".png";
	
		final ImageIcon icon = PersonalizedIcon.createImageIcon(imgLocation);
	
		item = new JMenuItem(text, icon);
		item.setMnemonic(mnemonic);
		item.setActionCommand(actionCommand);
    if (accelerator != null && accelerator.contains("ctrl") && MAC_OS_X) {
      accelerator = accelerator.replace("ctrl", "meta");
      accelerator = accelerator.replace("control", "meta");
    }
		item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		item.addActionListener(al);

		return item;
	}
	
	public JMenuItem createMenuItem(String text, String iconName, int mnemonic, String accelerator, String actionCommand, SButton link)
	{
		JMenuItem item =  createMenuItem(text, iconName, mnemonic, accelerator, actionCommand, link.getActionListeners()[0]);

		link.linkComponent(item);

		return item;
	}
	
	public JMenuItem createMenuItemDisable(String text, String iconName, int mnemonic, String accelerator, String actionCommand, SButton link)
	{
		JMenuItem item =  createMenuItem(text, iconName, mnemonic, accelerator, actionCommand, link);
		
		item.setEnabled(false);

		return item;
	}
	
	public JMenuItem createMenuItem(String text, String iconName, int mnemonic, String accelerator, String actionCommand)
	{
		return createMenuItem(text, iconName, mnemonic, accelerator, actionCommand, this);
	}

  /**
   * Quit the app.
   * 
   * This method is public because on the MacOSX handler.
   * 
   * @return True if the exit operation could be done or False if it has been 
   * canceled.
   */
	public boolean exit() {
		PanelClassDiagram p = PanelClassDiagram.getInstance();
		
		switch (p.askSavingCurrentProject())
		{
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
	  Dimension size = getSize();
	  
	  // Save properties before closing.
    PanelClassDiagram.getInstance().saveSplitLocationInProperties();
    Properties properties = PropertyLoader.getInstance().getProperties();
    properties.put(PropertyLoader.WINDOWS_MAXIMIZED, 
        String.valueOf(getExtendedState()));
    properties.put(PropertyLoader.WINDOWS_SIZE,
        String.format("%s,%s", size.width, size.height));
    
    PropertyLoader.getInstance().push();
    
    System.exit(0);
	}
	
	private class JMenuItemHistory extends JMenuItem
	{
		private static final long serialVersionUID = -6696714308788403479L;
		private Path historyPath;

		public JMenuItemHistory(String text) {
			super(text);
		}
		
		public void setHistoryPath(Path path)
		{
			historyPath = path;
		}
		
		public Path getHistoryPath()
		{
			return historyPath;
		}
		
	}

	/**
	 * Open the help file.
	 */
	private void openHelp()
	{
		final String ERROR_MESSAGE = "Cannot open help file!\nTry manually in help folder.";
		try
		{
			final File pdfFile = new File("Documentation/User manual.pdf");

			if (pdfFile.exists())
			{

					if (Desktop.isDesktopSupported())

							Desktop.getDesktop().open(pdfFile);

					else
							SMessageDialog.showErrorMessage(ERROR_MESSAGE);

			}
			else
					SMessageDialog.showErrorMessage("Help file not found!\nTry to re-download Slyum.");

		} catch (final Exception ex)
		{
			SMessageDialog.showErrorMessage(ERROR_MESSAGE);
		}
	}
}
