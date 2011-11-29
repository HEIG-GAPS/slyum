package swing;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
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
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import utility.OSValidator;
import utility.PersonnalizedIcon;
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
	public final static String APP_DIR_NAME = "Slyum";
	// Default JFrame size
	public final static Point DEFAULT_SIZE = new Point(1024, 760);

	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	private static Slyum instance;
	public final static boolean SHOW_CROSS_MENU = true;
	public final static boolean SHOW_ERRORS_MESSAGES = true;
	public final static boolean SHOW_OPENJDK_WARNING = true;
	public final static boolean SMALL_ICON = false;
	
	private static JMenuItem undo, redo;

	public static final float version = 1.3f;

	public final static String WINDOW_TITLE = "Slyum";

	private static String windowTitle = WINDOW_TITLE;

	public static void createAppDir(String path)
	{
		final File appDir = new File(path);

		if (appDir.mkdir())

			System.out.println("Application directory created.");

		else
		{
			System.err.println("Application directory not created.");
			SMessageDialog.showErrorMessage("Impossible to create application directory.\nYour preferences won't be saved.");
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
		windowTitle = WINDOW_TITLE + (projectName == null ? "" : " - " + projectName.getPath());
	}

	private final PanelClassDiagram panel;

	public Font ubuntuFont;

	/**
	 * Create a new JFrame with Slyum :D !
	 */
	public Slyum()
	{
		try
		{
			ubuntuFont = new Font(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("resources/Ubuntu-M.ttf")).getFamily(), Font.PLAIN, 13);
		} catch (final FontFormatException e)
		{
			e.printStackTrace();
		} catch (final IOException e)
		{
			e.printStackTrace();
		}

		// Initialize main window.
		setTitle("Slyum");

		panel = PanelClassDiagram.getInstance();

		final URL imageURL = Slyum.class.getResource("resources/icon/logo32.png");

		if (imageURL != null)
			setIconImage(new ImageIcon(imageURL, "Application icon").getImage());
		else
			System.err.println("application icon resource not found");

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(DEFAULT_SIZE.x, DEFAULT_SIZE.y);
		setMinimumSize(new Dimension(400, 400));
		setContentPane(panel);
		setLocationRelativeTo(null);
		createJMenuBar();

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				exit();
			}
		});

		setVisible(true);
		setExtendedState(MAXIMIZED_BOTH);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ("About".equals(e.getActionCommand()))
			new AboutBox(this);

		else if ("Help".equals(e.getActionCommand()))
			openHelp();

		else if ("Exit".equals(e.getActionCommand()))
			exit();

		else if ("Properties".equals(e.getActionCommand()))
			new SProperties();

		else if ("Update".equals(e.getActionCommand()))
			try
			{
				java.awt.Desktop.getDesktop().browse(new URI("http://code.google.com/p/slyum/downloads"));
			} catch (final IOException e1)
			{
				e1.printStackTrace();
			} catch (final URISyntaxException e1)
			{
				e1.printStackTrace();
			}
		else
			// some actions are in both controls
			panel.actionPerformed(e);
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
				Utility.setRenderQuality(g);

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
		menu.setFont(ubuntuFont);
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		// Menu item New project
		menuItem = createMenuItem("New Project", "newProject", KeyEvent.VK_J, "ctrl alt N", "newProject");
		menu.add(menuItem);

		// Menu item New view
		menuItem = createMenuItem("New View", "newView", KeyEvent.VK_N, "ctrl N", "newView");
		menuItem.setEnabled(false);
		menu.add(menuItem);

		// Menu item open project
		menuItem = createMenuItem("Open Project...", "open16", KeyEvent.VK_O, "ctrl O", "open");
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item close
		menuItem = createMenuItem("Close", "close", KeyEvent.VK_C, "ctrl C", "close");
		menuItem.setEnabled(false);
		menu.add(menuItem);

		menuItem = createMenuItem("Close All", "closeAll", KeyEvent.VK_S, "ctrl S", "closeAll");
		menu.addSeparator();

		// Menu item save
		menuItem = createMenuItem("Save", "save16", KeyEvent.VK_S, "ctrl S", "save");
		menu.add(menuItem);

		// Menu item save as...
		menuItem = createMenuItem("Save As...", "saveAs16", KeyEvent.VK_A, "ctrl shift S", "saveAs");
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item Export as image...
		menuItem = createMenuItem("Export as image...", "camera16", KeyEvent.VK_M, "ctrl E", "export");
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item print
		menuItem = createMenuItem("Print...", "print16", KeyEvent.VK_P, "ctrl P", "print");
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item Properties
		menuItem = createMenuItem("Properties...", "Properties", KeyEvent.VK_R, "alt ENTER", "Properties");
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item exit
		menuItem = createMenuItem("Exit", "exit", KeyEvent.VK_X, "alt F4", "Exit");
		menu.add(menuItem);

		// Menu edit
		menu = new JMenu("Edit");
		menu.setFont(ubuntuFont);
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);

		// Menu item Undo
		menuItem = undo = createMenuItem("Undo", "undo16", KeyEvent.VK_U, "ctrl Z", "undo");
		menuItem.setEnabled(false);
		menu.add(menuItem);

		// Menu item Redo
		menuItem = redo = createMenuItem("Redo", "redo16", KeyEvent.VK_R, "ctrl Y", "redo");
		menuItem.setEnabled(false);
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item adjust width
		menuItem = createMenuItem("Adjust Classes Width", "adjustWidth16", KeyEvent.VK_W, "ctrl 1", "adjustWidth");
		menu.add(menuItem);

		// Menu item align top
		menuItem = createMenuItem("Align Top", "alignTop16", KeyEvent.VK_O, "ctrl UP", "alignTop");
		menu.add(menuItem);

		// Menu item align bottom
		menuItem = createMenuItem("Align Bottom", "alignBottom16", KeyEvent.VK_B, "ctrl DOWN", "alignBottom");
		menu.add(menuItem);

		// Menu item align left
		menuItem = createMenuItem("Align Left", "alignLeft16", KeyEvent.VK_F, "ctrl LEFT", "alignLeft");
		menu.add(menuItem);

		// Menu item align right
		menuItem = createMenuItem("Align Righ", "alignRight16", KeyEvent.VK_H, "ctrl RIGHT", "alignRight");
		menu.add(menuItem);

		// Menu Diagram
		menu = new JMenu("Diagram");
		menu.setFont(ubuntuFont);
		menu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(menu);

		// Submenu Grid
		final JMenu submenu = new JMenu("Grid");
		menu.add(submenu);

		// Menu item no grid
		menuItem = createMenuItem("No Grid", "", KeyEvent.VK_N, "", "NoGrid");
		submenu.add(menuItem);

		// Menu item no grid
		menuItem = createMenuItem("10", "", KeyEvent.VK_1, "", "grid10");
		submenu.add(menuItem);

		// Menu item no grid
		menuItem = createMenuItem("15", "", KeyEvent.VK_5, "", "grid15");
		submenu.add(menuItem);

		// Menu item no grid
		menuItem = createMenuItem("20", "", KeyEvent.VK_0, "", "grid20");
		submenu.add(menuItem);

		// Menu item no grid
		menuItem = createMenuItem("25", "", KeyEvent.VK_2, "", "grid25");
		submenu.add(menuItem);

		// Menu item no grid
		menuItem = createMenuItem("30", "", KeyEvent.VK_3, "", "grid30");
		submenu.add(menuItem);

		menu.addSeparator();

		// Menu item add class
		menuItem = createMenuItem("Add Class", "class16", KeyEvent.VK_C, "ctrl shift C", "newClass");
		menu.add(menuItem);

		// Menu item add interface
		menuItem = createMenuItem("Add Interface", "interface16", KeyEvent.VK_I, "ctrl shift I", "newInterface");
		menu.add(menuItem);

		// Menu item add class association
		menuItem = createMenuItem("Add Association class", "classAssoc16", KeyEvent.VK_X, "ctrl shift X", "newClassAssoc");
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item add generalize
		menuItem = createMenuItem("Add Inheritance", "generalize16", KeyEvent.VK_H, "ctrl shift H", "newGeneralize");
		menu.add(menuItem);

		// Menu item add inner class
		menuItem = createMenuItem("Add inner class", "innerClass16", KeyEvent.VK_N, "ctrl shift N", "newInnerClass");
		menu.add(menuItem);

		// Menu item add dependency
		menuItem = createMenuItem("Add Dependency", "dependency16", KeyEvent.VK_E, "ctrl shift D", "newDependency");
		menu.add(menuItem);

		// Menu item add association
		menuItem = createMenuItem("Add Association", "association16", KeyEvent.VK_S, "ctrl shift A", "newAssociation");
		menu.add(menuItem);

		// Menu item add aggregation
		menuItem = createMenuItem("Add Aggregation", "aggregation16", KeyEvent.VK_G, "ctrl shift G", "newAggregation");
		menu.add(menuItem);

		// Menu item add composition
		menuItem = createMenuItem("Add Composition", "composition16", KeyEvent.VK_M, "ctrl shift P", "newComposition");
		menu.add(menuItem);

		// Menu item add composition
		menuItem = createMenuItem("Add Multi-association", "multi16", KeyEvent.VK_W, "ctrl shift W", "newMulti");
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item add note
		menuItem = createMenuItem("Add Note", "note16", KeyEvent.VK_N, "ctrl shift N", "newNote");
		menu.add(menuItem);

		// Menu item link note
		menuItem = createMenuItem("Link Note", "linkNote16", KeyEvent.VK_L, "ctrl shift L", "linkNote");
		menu.add(menuItem);

		// Menu Element
		menu = new JMenu("Element");
		menu.setMnemonic(KeyEvent.VK_E);
		// menuBar.add(menu);

		// Menu Help
		menu = new JMenu("Help");
		menu.setFont(ubuntuFont);
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);

		// Menu item Help
		menuItem = createMenuItem("Help...", "help", KeyEvent.VK_E, "F1", "Help");
		menu.add(menuItem);

		// Menu item Update
		menuItem = createMenuItem("Go to update page...", "update", KeyEvent.VK_U, null, "Update");
		menu.add(menuItem);

		menu.addSeparator();

		// Menu item About
		menuItem = createMenuItem("About Slyum...", null, KeyEvent.VK_A, null, "About");
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

		final String imgLocation = "resources/icon/" + iconName + ".png";

		final ImageIcon icon = PersonnalizedIcon.createImageIcon(imgLocation);

		item = new JMenuItem(text, icon) {
			@Override
			protected void paintComponent(Graphics g)
			{
				Utility.setRenderQuality(g);
				super.paintComponent(g);
			}
		};
		item.setFont(ubuntuFont);
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
					JOptionPane.showMessageDialog(this, "Cannot open help file!\nTry manually in help folder.", "Slyum", JOptionPane.ERROR_MESSAGE, PersonnalizedIcon.getErrorIcon());

			}
			else
				JOptionPane.showMessageDialog(this, "Help file not found!\nTry to re-download Slyum.", "Slyum", JOptionPane.ERROR_MESSAGE, PersonnalizedIcon.getErrorIcon());

		} catch (final Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
