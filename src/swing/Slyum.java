package swing;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import utility.OSValidator;
import utility.PersonalizedIcon;
import utility.SMessageDialog;
import utility.Utility;

/**
 * Main class! Create a new Instance of Slyum and display it. Create menu.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
@SuppressWarnings("serial")
public class Slyum extends JFrame implements ActionListener
{
	private static final String APP_NAME = "Slyum";
	public static final float version = 1.3f;
	public final static String EXTENTION = "sly";
	public final static String APP_DIR_NAME = APP_NAME;
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	public final static Point DEFAULT_SIZE = new Point(1024, 760);

	public final static String RESOURCES_PATH = "resources" + FILE_SEPARATOR;
	public final static String ICON_PATH = RESOURCES_PATH + "icon" + FILE_SEPARATOR;
	public final static String FONT_PATH = RESOURCES_PATH + "fonts" + FILE_SEPARATOR;
	
	public final static String DEFAULT_FONT_NAME = "DejaVu/DejaVuSans";
	public final static String DEFAULT_FONT_PATH = FONT_PATH + DEFAULT_FONT_NAME;
	
	public final static int DEFAULT_FONT_SIZE = 12;

	private static final String URL_UPDATE_PAGE = "http://code.google.com/p/slyum/downloads";
	
	// Properties
	public final static boolean SHOW_CROSS_MENU = true;
	public final static boolean SHOW_ERRORS_MESSAGES = true;
	public final static boolean SHOW_OPENJDK_WARNING = true;
	public final static boolean SMALL_ICON = false;
	
	// Action command
	public static final String ACTION_ABOUT = "About";
	public static final String ACTION_HELP = "Help";
	public static final String ACTION_EXIT = "Exit";
	public static final String ACTION_PROPERTIES = "Properties";
	public static final String ACTION_UPDATE = "Update";
	public static final String ACTION_SELECT_ALL = "SelectAll";
	public static final String ACTION_UNSELECT_ALL = "UnselectAll";
	public static final String ACTION_NEW_PROJECT = "NewProject";
	public static final String ACTION_OPEN = "Open";
	public static final String ACTION_SAVE = "Save";
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
	public static final String ACTION_SAVE_AS = "SaveAs";
	public static final String ACTION_ADJUST_WIDTH = "AdjustWidth";
	public static final String ACTION_UNDO = "Undo";
	public static final String ACTION_REDO = "Redo";
	
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
	public final static String KEY_REDO = "ctrl X";
	public static final String KEY_SELECT_ALL = "ctrl A";
	public static final String KEY_UNSELECT_ALL = "ctrl U";
	
	public final static String KEY_ADJUST_SIZE = "ctrl 1";
	public final static String KEY_ALIGN_UP = "ctrl UP";
	public final static String KEY_ALIGN_DOWN = "ctrl DOWN";
	public final static String KEY_ALIGN_LEFT = "ctrl LEFT";
	public final static String KEY_ALIGN_RIGHT = "ctrl RIGHT";

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

	private static Slyum instance;
	private static JMenuItem undo, redo;

	private static String windowTitle = APP_NAME;

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

		try
		{
			defaultPath = PropertyLoader.getInstance().getProperties().getProperty("PathForFileChooser");
		} catch (final Exception e)
		{
			e.printStackTrace();
		}

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

	public static boolean isShowOpenJDKWarning()
	{
		final String prop = PropertyLoader.getInstance().getProperties().getProperty("showOpenJDKWarning");
		boolean enable = SHOW_OPENJDK_WARNING;

		if (prop != null)
			enable = Boolean.parseBoolean(prop);

		return enable;
	}

	public static boolean getSmallIcons()
	{
		final String prop = PropertyLoader.getInstance().getProperties().getProperty("SmallIcon");
		boolean enable = SMALL_ICON;

		if (prop != null)
			enable = Boolean.parseBoolean(prop);

		return enable;
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

	public static void main(String[] args)
	{
		showWarningForOpenJDK();

		PropertyLoader.getInstance();
		instance = new Slyum();
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
		final boolean show = isShowOpenJDKWarning();

		if (show && OSValidator.isUnix())

			new NoRepopDialog("Slowdowns are observed with OpenJDK. If you use OpenJDK, we encourage you to get Sun JDK or put graphic quality to low.").setVisible(true);
	}

	public static void updateWindowTitle(File projectName)
	{
		windowTitle = APP_NAME + (projectName == null ? "" : " - " + projectName.getPath());
	}

	private final PanelClassDiagram panel;

	public Font defaultFont;

	/**
	 * Create a new JFrame with Slyum :D !
	 */
	public Slyum()
	{
		try
		{
			defaultFont = new Font(Font.createFont(
					Font.TRUETYPE_FONT,
					getClass().getResourceAsStream(DEFAULT_FONT_PATH + ".ttf")).getFamily(),
					Font.PLAIN,
					DEFAULT_FONT_SIZE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		setUIProperties();
		panel = PanelClassDiagram.getInstance();
		createJMenuBar();
		setFrameProperties();

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				exit();
			}
		});
	}
	
	/**
	 * Initialize the properties of the frame.
	 */
	public void setFrameProperties()
	{
		setName(APP_NAME);
		setTitle(getName());
		setIconImage(PersonalizedIcon.getLogo().getImage());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(DEFAULT_SIZE.x, DEFAULT_SIZE.y);
		setMinimumSize(new Dimension(400, 400));
		setContentPane(panel);
		setLocationRelativeTo(null);
		setVisible(true);
		setExtendedState(MAXIMIZED_BOTH);
	}
	
	/**
	 * Initialize the properties of Slyum.
	 */
	public void setUIProperties()
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
		UIManager.put("Menu.font", f);
		UIManager.put("MenuItem.font", f);
		UIManager.put("ComboBox.font", f);
		UIManager.put("Table.font", f);
		UIManager.put("TextField.font", f);
		UIManager.put("OptionPane.informationIcon", PersonalizedIcon.getInfoIcon());
		UIManager.put("OptionPane.errorIcon", PersonalizedIcon.getErrorIcon());
		UIManager.put("OptionPane.warningIcon", PersonalizedIcon.getWarningIcon());
		UIManager.put("OptionPane.questionIcon", PersonalizedIcon.getQuestionIcon());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (ACTION_ABOUT.equals(e.getActionCommand()))
			
			new AboutBox(this);

		else if (ACTION_HELP.equals(e.getActionCommand()))
			
			openHelp();

		else if (ACTION_EXIT.equals(e.getActionCommand()))
			
			exit();

		else if (ACTION_PROPERTIES.equals(e.getActionCommand()))
			
			new SProperties();

		else if (ACTION_UPDATE.equals(e.getActionCommand()))
			
			openURL(URL_UPDATE_PAGE);
			
		else if (ACTION_KLIPPER.equals(e.getActionCommand()))
			
			PanelClassDiagram.getInstance().getCurrentGraphicView().copyDiagramToClipboard();
		
		else if (ACTION_SELECT_ALL.equals(e.getActionCommand()))
			
			PanelClassDiagram.getInstance().getCurrentGraphicView().selectAllComponents();
		
		else if (ACTION_UNSELECT_ALL.equals(e.getActionCommand()))
			
			PanelClassDiagram.getInstance().getCurrentGraphicView().clearAllSelectedComponents();
		
		else
			
			// some actions are in PanelClassDiagram too.
			panel.actionPerformed(e);
	}
	
	public static void openURL(String url)
	{
		try
		{
			java.awt.Desktop.getDesktop().browse(new URI(url));
		}
		catch (Exception e)
		{
			SMessageDialog.showErrorMessage("Unable to open " + URL_UPDATE_PAGE + ".");
		}
	}

	/**
	 * Create the JMenuBar
	 */
	private void createJMenuBar()
	{
		final JMenuBar menuBar = new JMenuBar() {

			@Override
			public void paintComponent(Graphics g)
			{
				final Graphics2D g2 = (Graphics2D) g;
				final int w = getWidth();
				final int h = getHeight();

				// Paint a gradient from top to bottom.
				final Color color = getBackground();
				final GradientPaint gp = new GradientPaint(0, 0, color.brighter(), 0, h, color);

				g2.setPaint(gp);
				g2.fillRect(0, 0, w, h);

				// setOpaque(false);
				// super.paintComponent(g);
				// setOpaque(true);
			}
		};

		menuBar.setBorder(null);

		JMenu menu;
		JMenuItem menuItem;

		// Menu file
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		// Menu item New project
		menuItem = createMenuItem("New Project", "newProject", KeyEvent.VK_J, KEY_NEW_PROJECT, ACTION_NEW_PROJECT);
		menu.add(menuItem);

		// Menu item New view
		menuItem = createMenuItem("New View", "newView", KeyEvent.VK_N, "ctrl N", "newView");
		menuItem.setEnabled(false);
		menu.add(menuItem);

		// Menu item open project
		menuItem = createMenuItem("Open Project...", "open16", KeyEvent.VK_O, KEY_OPEN_PROJECT, ACTION_OPEN);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item close
		menuItem = createMenuItem("Close", "close", KeyEvent.VK_C, "ctrl C", "close");
		menuItem.setEnabled(false);
		menu.add(menuItem);

		menuItem = createMenuItem("Close All", "closeAll", KeyEvent.VK_S, "ctrl aS", "closeAll");
		menu.addSeparator();

		// Menu item save
		menuItem = createMenuItem("Save", "save16", KeyEvent.VK_S, KEY_SAVE, ACTION_SAVE);
		menu.add(menuItem);

		// Menu item save as...
		menuItem = createMenuItem("Save As...", "saveAs16", KeyEvent.VK_A, KEY_SAVE_AS, ACTION_SAVE_AS);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item Export as image...
		menuItem = createMenuItem("Export as image...", "camera16", KeyEvent.VK_M, KEY_EXPORT, ACTION_EXPORT);
		menu.add(menuItem);
		
		// Menu item Copy to clipboard
		menuItem = createMenuItem("Copy selection to clipboard", "klipper16", KeyEvent.VK_K, KEY_KLIPPER, ACTION_KLIPPER);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item print
		menuItem = createMenuItem("Print...", "print16", KeyEvent.VK_P, KEY_PRINT, ACTION_PRINT);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item Properties
		menuItem = createMenuItem("Properties...", "Properties", KeyEvent.VK_R, KEY_PROPERTIES, ACTION_PROPERTIES);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item exit
		menuItem = createMenuItem("Exit", "exit", KeyEvent.VK_X, KEY_EXIT, ACTION_EXIT);
		menu.add(menuItem);

		// Menu edit
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);

		// Menu item Undo
		menuItem = undo = createMenuItem("Undo", "undo16", KeyEvent.VK_U, KEY_UNDO, ACTION_UNDO);
		menuItem.setEnabled(false);
		menu.add(menuItem);

		// Menu item Redo
		menuItem = redo = createMenuItem("Redo", "redo16", KeyEvent.VK_R, KEY_REDO, ACTION_REDO);
		menuItem.setEnabled(false);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item Select all
		menuItem = createMenuItem("Select all", "select16", KeyEvent.VK_S, KEY_SELECT_ALL, ACTION_SELECT_ALL);
		menu.add(menuItem);

		// Menu item Unselect all
		menuItem = createMenuItem("Unselect all", "unselect16", KeyEvent.VK_N, KEY_UNSELECT_ALL, ACTION_UNSELECT_ALL);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item adjust width
		menuItem = createMenuItem("Adjust Classes Width", "adjustWidth16", KeyEvent.VK_W, KEY_ADJUST_SIZE, ACTION_ADJUST_WIDTH);
		menu.add(menuItem);

		// Menu item align top
		menuItem = createMenuItem("Align Top", "alignTop16", KeyEvent.VK_O, KEY_ALIGN_UP, ACTION_ALIGN_TOP);
		menu.add(menuItem);

		// Menu item align bottom
		menuItem = createMenuItem("Align Bottom", "alignBottom16", KeyEvent.VK_B, KEY_ALIGN_DOWN, ACTION_ALIGN_BOTTOM);
		menu.add(menuItem);

		// Menu item align left
		menuItem = createMenuItem("Align Left", "alignLeft16", KeyEvent.VK_F, KEY_ALIGN_LEFT, ACTION_ALIGN_LEFT);
		menu.add(menuItem);

		// Menu item align right
		menuItem = createMenuItem("Align Righ", "alignRight16", KeyEvent.VK_H, KEY_ALIGN_RIGHT, ACTION_ALIGN_RIGHT);
		menu.add(menuItem);

		// Menu Diagram
		menu = new JMenu("Diagram");
		menu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(menu);

		// Menu item add class
		menuItem = createMenuItem("Add Class", "class16", KeyEvent.VK_C, KEY_CLASS, ACTION_NEW_CLASS);
		menu.add(menuItem);

		// Menu item add interface
		menuItem = createMenuItem("Add Interface", "interface16", KeyEvent.VK_I, KEY_INTERFACE, ACTION_NEW_INTERFACE);
		menu.add(menuItem);

		// Menu item add class association
		menuItem = createMenuItem("Add Association class", "classAssoc16", KeyEvent.VK_X, KEY_ASSOCIATION_CLASS, ACTION_NEW_CLASS_ASSOCIATION);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item add generalize
		menuItem = createMenuItem("Add Inheritance", "generalize16", KeyEvent.VK_H, KEY_INHERITANCE, ACTION_NEW_GENERALIZE);
		menu.add(menuItem);

		// Menu item add inner class
		menuItem = createMenuItem("Add inner class", "innerClass16", KeyEvent.VK_N, KEY_INNER_CLASS, ACTION_NEW_INNER_CLASS);
		menu.add(menuItem);

		// Menu item add dependency
		menuItem = createMenuItem("Add Dependency", "dependency16", KeyEvent.VK_E, KEY_DEPENDENCY, ACTION_NEW_DEPENDENCY);
		menu.add(menuItem);

		// Menu item add association
		menuItem = createMenuItem("Add Association", "association16", KeyEvent.VK_S, KEY_ASSOCIATION, ACTION_NEW_ASSOCIATION);
		menu.add(menuItem);

		// Menu item add aggregation
		menuItem = createMenuItem("Add Aggregation", "aggregation16", KeyEvent.VK_G, KEY_AGGREGATION, ACTION_NEW_AGGREGATION);
		menu.add(menuItem);

		// Menu item add composition
		menuItem = createMenuItem("Add Composition", "composition16", KeyEvent.VK_M, KEY_COMPOSITION, ACTION_NEW_COMPOSITION);
		menu.add(menuItem);

		// Menu item add multi association
		menuItem = createMenuItem("Add Multi-association", "multi16", KeyEvent.VK_W, KEY_MULTI_ASSOCIATION, ACTION_NEW_MULTI);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item add note
		menuItem = createMenuItem("Add Note", "note16", KeyEvent.VK_N, KEY_NOTE, ACTION_NEW_NOTE);
		menu.add(menuItem);

		// Menu item link note
		menuItem = createMenuItem("Link Note", "linkNote16", KeyEvent.VK_L, KEY_LINK_NOTE, ACTION_NEW_LINK_NOTE);
		menu.add(menuItem);

		// Menu Element
		menu = new JMenu("Element");
		menu.setMnemonic(KeyEvent.VK_E);
		// menuBar.add(menu);

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

		menu.addSeparator();

		// Menu item About
		menuItem = createMenuItem("About Slyum...", null, KeyEvent.VK_A, null, ACTION_ABOUT);
		menu.add(menuItem);

		setJMenuBar(menuBar);
	}

	/**
	 * Create a JMenuItem with given informations.
	 * 
	 * @param text
	 *            the text of the menu
	 * @param iconName
	 *            the icon path for the menu
	 * @param mnemonic
	 *            the mnemoni for the menu
	 * @param accelerator
	 *            the keystroke, in string, for the menu
	 * @param actionCommand
	 *            the action command
	 * @return the new JMenuItem created
	 */
	public JMenuItem createMenuItem(String text, String iconName, int mnemonic, String accelerator, String actionCommand)
	{
		JMenuItem item;

		final String imgLocation = ICON_PATH + iconName + ".png";

		final ImageIcon icon = PersonalizedIcon.createImageIcon(imgLocation);

		item = new JMenuItem(text, icon);
		item.setMnemonic(mnemonic);
		item.setActionCommand(actionCommand);
		item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		item.addActionListener(this);

		return item;
	}

	private void exit()
	{
		switch (panel.askSavingCurrentProject())
		{
			case JOptionPane.CANCEL_OPTION:
				return;

			case JOptionPane.YES_OPTION:
				panel.saveToXML(false);
				System.exit(0);
				break;

			case JOptionPane.NO_OPTION:
				System.exit(0);
				break;
		}
	}

	/**
	 * Open the help file.
	 */
	private void openHelp()
	{
		try
		{
			final File pdfFile = new File("Documentation/User manual.pdf");

			if (pdfFile.exists())
			{

				if (Desktop.isDesktopSupported())

					Desktop.getDesktop().open(pdfFile);

				else
					SMessageDialog.showErrorMessage("Cannot open help file!\nTry manually in help folder.");

			}
			else
				SMessageDialog.showErrorMessage("Help file not found!\nTry to re-download Slyum.");

		} catch (final Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
