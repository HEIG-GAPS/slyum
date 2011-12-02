package swing;

import graphic.GraphicView;
import graphic.factory.AggregationFactory;
import graphic.factory.AssociationClassFactory;
import graphic.factory.BinaryFactory;
import graphic.factory.ClassFactory;
import graphic.factory.CompositionFactory;
import graphic.factory.DependencyFactory;
import graphic.factory.InheritanceFactory;
import graphic.factory.InnerClassFactory;
import graphic.factory.InterfaceFactory;
import graphic.factory.LineCommentaryFactory;
import graphic.factory.MultiFactory;
import graphic.factory.NoteFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import swing.hierarchicalView.HierarchicalView;
import swing.propretiesView.PropretiesChanger;
import utility.SMessageDialog;
import utility.SSlider;
import utility.Utility;
import change.Change;
import classDiagram.ClassDiagram;

/**
 * Show the panel containing all views (hierarchical, properties and graphic)
 * and the tool bar.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
@SuppressWarnings("serial")
public class PanelClassDiagram extends JPanel implements ActionListener
{
	private static PanelClassDiagram instance = new PanelClassDiagram();

	public static PanelClassDiagram getInstance()
	{
		return instance;
	}

	private ClassDiagram classDiagram;

	private File currentFile = null;

	private final GraphicView graphicView;
	
	private SPanelStyleComponent panelStyle;
	private SSlider sSlider;

	private PanelClassDiagram()
	{
		super(new BorderLayout());

		// Create new graphiView, contain class diagram.
		graphicView = new GraphicView(getClassDiagram());
		
		// Personalized ToolBar Layout
		JPanel panelToolBar = new JPanel();
		panelToolBar.setLayout(new BoxLayout(panelToolBar, BoxLayout.LINE_AXIS));

		panelToolBar.add(new SPanelFileComponent());
		panelToolBar.add(panelStyle = new SPanelStyleComponent());
		panelToolBar.add(sSlider = new SSlider(Color.YELLOW, 200));
		
		add(panelToolBar, BorderLayout.PAGE_START);

		final SSplitPane mainSplitPane = new SSplitPane(JSplitPane.VERTICAL_SPLIT, graphicView.getScrollPane(), PropretiesChanger.getInstance());

		mainSplitPane.setResizeWeight(1.0);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		
		leftPanel.add(new SPanelDiagramComponent());
		leftPanel.add(new HierarchicalView(getClassDiagram()));
		
		final SSplitPane leftSplitPanel = new SSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, mainSplitPane);
		leftSplitPanel.setDividerLocation(200);
		leftSplitPanel.setBorder(null);

		graphicView.getScene().setMinimumSize(new Dimension(200, 150));

		add(leftSplitPanel, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ("newClass".equals(e.getActionCommand()))
			graphicView.initNewComponent(new ClassFactory(graphicView, classDiagram));

		else if ("newInterface".equals(e.getActionCommand()))
			graphicView.initNewComponent(new InterfaceFactory(graphicView, classDiagram));

		else if ("newGeneralize".equals(e.getActionCommand()))
			graphicView.initNewComponent(new InheritanceFactory(graphicView, classDiagram));

		else if ("newInnerClass".equals(e.getActionCommand()))
			graphicView.initNewComponent(new InnerClassFactory(graphicView, classDiagram));

		else if ("newDependency".equals(e.getActionCommand()))
			graphicView.initNewComponent(new DependencyFactory(graphicView, classDiagram));

		else if ("newAssociation".equals(e.getActionCommand()))
			graphicView.initNewComponent(new BinaryFactory(graphicView, classDiagram));

		else if ("newAggregation".equals(e.getActionCommand()))
			graphicView.initNewComponent(new AggregationFactory(graphicView, classDiagram));

		else if ("newComposition".equals(e.getActionCommand()))
			graphicView.initNewComponent(new CompositionFactory(graphicView, classDiagram));

		else if ("newClassAssoc".equals(e.getActionCommand()))
			graphicView.initNewComponent(new AssociationClassFactory(graphicView, classDiagram));

		else if ("newMulti".equals(e.getActionCommand()))
			graphicView.initNewComponent(new MultiFactory(graphicView, classDiagram));

		else if ("newNote".equals(e.getActionCommand()))
			graphicView.initNewComponent(new NoteFactory(graphicView, classDiagram));

		else if ("export".equals(e.getActionCommand()))
			exportAsImage();

		else if ("alignTop".equals(e.getActionCommand()))
			graphicView.alignHorizontal(true);

		else if ("alignBottom".equals(e.getActionCommand()))
			graphicView.alignHorizontal(false);

		else if ("alignLeft".equals(e.getActionCommand()))
			graphicView.alignVertical(true);

		else if ("alignRight".equals(e.getActionCommand()))
			graphicView.alignVertical(false);

		else if ("print".equals(e.getActionCommand()))
			initPrinting();

		else if ("save".equals(e.getActionCommand()))
			saveToXML(false);

		else if ("saveAs".equals(e.getActionCommand()))
			saveToXML(true);

		else if ("open".equals(e.getActionCommand()))
			openFromXML();

		else if ("newProject".equals(e.getActionCommand()))
			newProject();

		else if ("adjustWidth".equals(e.getActionCommand()))
			graphicView.adjustWidthSelectedEntities();

		else if ("undo".equals(e.getActionCommand()))
			Change.undo();

		else if ("redo".equals(e.getActionCommand()))
			Change.redo();

		else if ("linkNote".equals(e.getActionCommand()))
			graphicView.initNewComponent(new LineCommentaryFactory(graphicView, classDiagram));
	}

	/**
	 * Ask user to save current project.
	 */
	public int askSavingCurrentProject()
	{
		if (!Change.hasChange())
			return JOptionPane.NO_OPTION;
		else
			return SMessageDialog.showQuestionMessageYesNoCancel("Save current project ?");
	}

	/**
	 * Export the current graphic to an image file.
	 */
	public void exportAsImage()
	{
		final JFileChooser fc = new JFileChooser(Slyum.getCurrentDirectoryFileChooser());
		fc.setAcceptAllFileFilterUsed(false);

		fc.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (f.isDirectory())
					return true;

				final String extension = Utility.getExtension(f);
				if (extension != null)
					if (extension.equals("jpg") || extension.equals("png") || extension.equals("gif"))
						return true;

				return false;
			}

			@Override
			public String getDescription()
			{
				return "Images (*.png, *.jpg, *.gif)";
			}
		});

		final int result = fc.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		{
			final File file = fc.getSelectedFile();

			if (file.exists())
			{
				final int answer = SMessageDialog.showQuestionMessageOkCancel(file + " already exists. Overwrite?");

				if (answer == JOptionPane.OK_OPTION)
					saveImageTo(file);
			}
			else
				saveImageTo(file);
		}
	}

	/**
	 * Get the class diagram from project.
	 * 
	 * @return the class diagram
	 */
	public ClassDiagram getClassDiagram()
	{
		if (classDiagram == null)
		{
			classDiagram = new ClassDiagram("Class diagram");
			classDiagram.addComponentsObserver(PropretiesChanger.getInstance());
		}

		return classDiagram;
	}
	
	public JButton getRedoButton()
	{
		return panelStyle.getRedoButton();
	}
	
	public JButton getUndoButton()
	{
		return panelStyle.getUndoButton();
	}

	/**
	 * Get the current GraphicView.
	 * 
	 * @return the current GraphicView
	 */
	public GraphicView getCurrentGraphicView()
	{
		return graphicView;
	}
	
	public SSlider getsSlider()
	{
		return sSlider;
	}

	/**
	 * Init a new save where save project. If no file exist, open a JFileChooser
	 * to ask a new file.
	 * 
	 * @return if file has been succefully created
	 */
	public boolean initCurrentSaveFile()
	{
		final JFileChooser fc = new JFileChooser(Slyum.getCurrentDirectoryFileChooser());
		fc.setAcceptAllFileFilterUsed(false);

		fc.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (f.isDirectory())
					return true;

				final String extension = Utility.getExtension(f);
				if (extension != null)
					if (extension.equals("sly"))
						return true;

				return false;
			}

			@Override
			public String getDescription()
			{
				return "Fichiers SLY (*.sly)";
			}
		});

		final int result = fc.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			String extension = Utility.getExtension(file);

			if (extension == null || !extension.equals("sly"))
			{
				extension = "sly";
				file = new File(file.getPath() + "." + extension);
			}

			if (file.exists())
			{
				final int answer = SMessageDialog.showQuestionMessageOkCancel(file + " already exists. Overwrite?");

				if (answer == JOptionPane.CANCEL_OPTION)
					return false;
			}
			else
				try
				{
					file.createNewFile();
				} catch (final IOException e)
				{
					e.printStackTrace();
				}

			setCurrentFile(file);
			return true;
		}

		return false;
	}

	/**
	 * http://www.javafaq.nu/java-bookpage-33-2.html
	 * 
	 * Launch a new printing.
	 */
	public void initPrinting()
	{
		final Thread runner = new Thread() {

			@Override
			public void run()
			{
				print();
			}

		};

		runner.start();
	}


	/**
	 * Create a new project. Ask user to save current project.
	 */
	public void newProject()
	{
		if (!askForSave())
			return;

		classDiagram.removeAll();
		graphicView.removeAll();
		setCurrentFile(null);
		Change.setHasChange(false);
	}
	
	public void setCurrentFile(File file)
	{
		currentFile = file;
		Change.setHasChange(false);
		
		if (file == null)
			return;
		
		Slyum.updateWindowTitle(currentFile);
		Slyum.setCurrentDirectoryFileChooser(file.getParent());
	}
	
	public boolean askForSave()
	{
		switch (askSavingCurrentProject())
		{
			case JOptionPane.CANCEL_OPTION:
				return false;
	
			case JOptionPane.YES_OPTION:
				saveToXML(false);
				break;
	
			case JOptionPane.NO_OPTION:
				break;
		}
		
		return true;
	}
	
	public void openFromXML(final File file)
	{
		final String extension = Utility.getExtension(file);

		if (!file.exists())
		{
			SMessageDialog.showErrorMessage("File not found. Please select an existing file...");
			return;
		}

		if (extension == null || !extension.equals("sly"))
		{
			SMessageDialog.showErrorMessage("Invalide file format. Only \".sly\" files are accepted.");
			return;
		}

		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser parser;
		graphicView.setVisible(false);
		
		try
		{
			parser = factory.newSAXParser();

			final DefaultHandler handler = new XMLParser(classDiagram, graphicView);
			
			
			new Thread(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						Change.setBlocked(true);
						
						parser.parse(file, handler);
						
						Change.setBlocked(false);
						
					} catch (final Exception e)
					{
						showErrorImportationMessage(e);
					} finally
					{
						graphicView.setVisible(true);
					}
				}
			}).start();

			setCurrentFile(file);
			Change.setHasChange(false);
		} catch (final Exception e)
		{
			showErrorImportationMessage(e);
		}
	}

	/**
	 * Open a new project.
	 */
	public void openFromXML()
	{
		if (!askForSave())
			return;
		
		final JFileChooser fc = new JFileChooser(Slyum.getCurrentDirectoryFileChooser());
		fc.setAcceptAllFileFilterUsed(false);

		fc.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (f.isDirectory())
					return true;

				final String extension = Utility.getExtension(f);

				if (extension != null)
					if (extension.equals("sly"))
						return true;

				return false;
			}

			@Override
			public String getDescription()
			{
				return "Fichiers SLY (*.sly)";
			}
		});

		final int result = fc.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		
			openFromXML(fc.getSelectedFile());
		
	}

	/**
	 * Print a picture of the diagram.
	 */
	public void print()
	{
		try
		{
			final PrinterJob prnJob = PrinterJob.getPrinterJob();

			prnJob.setPrintable(graphicView);

			if (!prnJob.printDialog())

				return;

			setCursor(new Cursor(Cursor.WAIT_CURSOR));

			prnJob.print();

			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			SMessageDialog.showInformationMessage("Printing completed successfully");
		}

		catch (final PrinterException e)
		{
			e.printStackTrace();

			System.err.println("Printing error: " + e.toString());
		}
	}

	/**
	 * Save a picture of the diagram in the given file.
	 * 
	 * @param file
	 *            the file where to save a picture.
	 */
	public void saveImageTo(File file)
	{
		try
		{
			String extension = Utility.getExtension(file);

			if (extension == null)
			{
				extension = "png";
				file = new File(file.getPath() + "." + extension);
			}

			if (extension.equals("jpg") || extension.equals("png") || extension.equals("gif"))

				ImageIO.write(graphicView.getScreen(), extension, file);

			else

				SMessageDialog.showErrorMessage("Extension \"." + extension + "\" not supported.\nSupported extensions : png, jpg, gif.");
		} catch (final Exception e)
		{
			SMessageDialog.showErrorMessage("Class diagram is empty. Empty class diagramm can't be export.");
		}
	}

	/**
	 * Save the diagram to text format, with XML structure.
	 * 
	 * @param selectFile
	 *            true if a dialog must invite the user to choose a file; false
	 *            to save in current file. If no current file, dialog will open.
	 */
	public void saveToXML(boolean selectFile)
	{
		if (selectFile || currentFile == null || !currentFile.exists())
			if (!initCurrentSaveFile())
				return;

		String xml = "<?xml version=\"1.0\" encoding=\"iso-8859-15\"?>\n\n<classDiagram name=\"" + classDiagram.getName() + "\">\n";

		xml += classDiagram.toXML(1) + "\n";

		xml += graphicView.toXML(1) + "\n";

		xml += "</classDiagram>";

		try
		{
			final PrintWriter out = new PrintWriter(currentFile);

			out.print(xml);

			out.close();
		} catch (final IOException e)
		{
			e.printStackTrace();
		}
		
		Change.setHasChange(false);
	}

	private void showErrorImportationMessage(Exception e)
	{
		SMessageDialog.showErrorMessage("Cannot open projet. Error reading from file.\nMessage : " + e.getMessage());
		
		e.printStackTrace();

		classDiagram.removeAll();
		graphicView.removeAll();

		graphicView.setVisible(true);
	}
}
