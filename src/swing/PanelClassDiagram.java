package swing;

import graphic.GraphicView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import swing.hierarchicalView.HierarchicalView;
import swing.propretiesView.PropretiesChanger;
import utility.SDialogProjectLoading;
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
public class PanelClassDiagram extends JPanel
{
	private static PanelClassDiagram instance = new PanelClassDiagram();

	public static PanelClassDiagram getInstance()
	{
		return instance;
	}

	private ClassDiagram classDiagram;

	private File currentFile = null;

	private final GraphicView graphicView;
	
	private SSlider sSlider;

	private PanelClassDiagram()
	{
		super(new BorderLayout());

		// Create new graphiView, contain class diagram.
		graphicView = new GraphicView(getClassDiagram());
        
        setTransferHandler(new FileHandler());
		
		// Personalized ToolBar Layout
		JPanel panelToolBar = new JPanel();
		panelToolBar.setLayout(new BoxLayout(panelToolBar, BoxLayout.LINE_AXIS));

		panelToolBar.add(SPanelFileComponent.getInstance());
		panelToolBar.add(SPanelUndoRedo.getInstance());
		panelToolBar.add(SPanelElement.getInstance());
		panelToolBar.add(SPanelStyleComponent.getInstance());
		panelToolBar.add(SPanelZOrder.getInstance());
		panelToolBar.add(sSlider = new SSlider(Color.YELLOW, 100, 50, 200){

			@Override
			public void setValue(int value)
			{
				super.setValue(value);
				getCurrentGraphicView().repaint();
			}			
		});
		
		add(panelToolBar, BorderLayout.PAGE_START);

		final SSplitPane mainSplitPane = new SSplitPane(JSplitPane.VERTICAL_SPLIT, graphicView.getScrollPane(), PropretiesChanger.getInstance());

		mainSplitPane.setResizeWeight(1.0);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		
		leftPanel.add(SPanelDiagramComponent.getInstance());
		leftPanel.add(new HierarchicalView(getClassDiagram()));
		
		final SSplitPane leftSplitPanel = new SSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, mainSplitPane);
		leftSplitPanel.setDividerLocation(200);
		leftSplitPanel.setBorder(null);

		graphicView.getScene().setMinimumSize(new Dimension(200, 150));

		add(leftSplitPanel, BorderLayout.CENTER);
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
		return SPanelUndoRedo.getInstance().getRedoButton();
	}
	
	public JButton getUndoButton()
	{
		return SPanelUndoRedo.getInstance().getUndoButton();
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

		fc.addChoosableFileFilter(new SlyFileChooser());

		final int result = fc.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			String extension = Utility.getExtension(file);

			if (extension == null || !extension.equals(Slyum.EXTENTION))
			{
				extension = Slyum.EXTENTION;
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
		
		cleanApplication();
	}
	
	public void cleanApplication()
	{
		classDiagram.removeAll();
		graphicView.removeAll();
		setCurrentFile(null);
	}
	
	public void setCurrentFile(File file)
	{
		currentFile = file;
		Change.setHasChange(false);
		Slyum.updateWindowTitle(currentFile);
		
		if (file == null)
			return;
		
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

		if (extension == null || !extension.equals(Slyum.EXTENTION))
		{
			SMessageDialog.showErrorMessage("Invalide file format. Only \"." + Slyum.EXTENTION + "\" files are accepted.");
			return;
		}

		final SAXParserFactory factory = SAXParserFactory.newInstance();

		graphicView.setStopRepaint(true);
		
		final boolean isBlocked = Change.isBlocked();
		Change.setBlocked(true);

		final SDialogProjectLoading dpl = new SDialogProjectLoading(file.getPath());
		dpl.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e)
			{
				cleanApplication();
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() 
			{
				try
				{
					SAXParser parser = factory.newSAXParser();
					XMLParser handler = new XMLParser(classDiagram, graphicView, dpl);
					parser.parse(file, handler);

					handler.createDiagram();
				}
				catch (Exception e)
				{
					showErrorImportationMessage(e);
					
			        graphicView.setPaintBackgroundLast(true);
			        graphicView.goRepaint();
				}
				
				Change.setBlocked(isBlocked);
				
				setCurrentFile(file);
				Change.setHasChange(false);
				dpl.setVisible(false);
			}
		});

		dpl.setVisible(true);
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				graphicView.paintBackgroundFirst();
			}
		});

        RecentProjectManager.addhistoryEntry(file.getAbsolutePath());
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

		fc.addChoosableFileFilter(new SlyFileChooser());

		final int result = fc.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		
			openFromXML(fc.getSelectedFile());
	}
	
	/**
	 * Use for choosing a .sly file.
	 * 
	 * @author David Miserez
	 * @date 6 déc. 2011
	 */
	private class SlyFileChooser extends FileFilter
	{
		@Override
		public boolean accept(File f)
		{
			if (f.isDirectory())
				return true;

			final String extension = Utility.getExtension(f);

			if (extension != null)
				if (extension.equals(Slyum.EXTENTION))
					return true;

			return false;
		}

		@Override
		public String getDescription()
		{
			return "Fichiers " + Slyum.EXTENTION.toUpperCase() + " (*." + Slyum.EXTENTION + ")";
		}
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

			if (extension.equals("png"))
				
				ImageIO.write(graphicView.getScreen(BufferedImage.TYPE_INT_ARGB_PRE), extension, file);
			
			else if (extension.equals("jpg") || extension.equals("gif"))
				
				ImageIO.write(graphicView.getScreen(BufferedImage.TYPE_INT_RGB), extension, file);

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
			SMessageDialog.showErrorMessage(e.getLocalizedMessage());
		}
		
		Change.setHasChange(false);
		
		RecentProjectManager.addhistoryEntry(currentFile.getAbsolutePath());
	}

	private void showErrorImportationMessage(Exception e)
	{
		SMessageDialog.showErrorMessage("Cannot open projet. Error reading from file.\nMessage : " + e.getMessage());
		
		e.printStackTrace();

		cleanApplication();
		graphicView.setVisible(true);
	}

	/**
	 * Return a LinkedList with all opened graphic views.
	 * @return a LinkedList with all opened graphic views
	 */
	public LinkedList<GraphicView> getAllGraphicView()
	{
		// TODO
		LinkedList<GraphicView> l = new LinkedList<GraphicView>();
		l.add(graphicView);
		
		return l;
	}
	
	public void openFromXmlAndAsk(File file)
	{
        if (!askForSave())
            return;
        
        openFromXML(file);
	}
    
    private class FileHandler extends TransferHandler
    {
        private static final long serialVersionUID = 5606903424194929527L;

        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) 
        {
            for (DataFlavor flavor : transferFlavors)
                if (!flavor.isFlavorJavaFileListType())
                    return false;
            
            return true;
        }
        
        @Override
        public boolean importData(JComponent comp, Transferable t)
        {
            try
            {
                @SuppressWarnings("unchecked")
                List<File> dropppedFiles = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
                
                // Open just the last of the list.
                openFromXmlAndAsk((File)dropppedFiles.get(dropppedFiles.size() - 1));
                return true;
                
            } catch (UnsupportedFlavorException | IOException e)
            {
                e.printStackTrace();
                return false;
            }
        }
    }
}
