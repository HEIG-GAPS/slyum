package swing;

import swing.slyumCustomizedComponents.SSplitPane;
import change.Change;
import classDiagram.ClassDiagram;
import graphic.GraphicComponent;
import graphic.GraphicView;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import swing.hierarchicalView.HierarchicalView;
import swing.propretiesView.PropretiesChanger;
import utility.MultiBorderLayout;
import utility.SMessageDialog;
import utility.Utility;
import utility.WatchDir;
import utility.WatchFileListener;

/**
 * Show the panel containing all views (hierarchical, properties and graphic)
 * and the tool bar.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class PanelClassDiagram extends JPanel {
  private static PanelClassDiagram instance = new PanelClassDiagram();

  public static PanelClassDiagram getInstance() {
    return instance;
  }

  public static File getFileOpen() {
    if (getInstance() != null) return getInstance().getCurrentFile();
    return null;
  }
  
  public static void setCurrentDiagramName(String name) {
    getInstance().setDiagramName(name);
    getInstance().getClassDiagram().notifyObservers();
  }
  
  public static void setVisibleCurrentDiagramName(boolean visible) {
    getInstance().setVisibleDiagramName(visible);
  }
  
  public static void refresh() {
    getInstance()._refresh();
  }
  
  private void _refresh() {
    if (fileChanged == StandardWatchEventKinds.ENTRY_MODIFY) {
      if (SMessageDialog.showQuestionMessageYesNo(
          "The file has been modified by another program.\n" +
          "Do you want to reload it?", PanelClassDiagram.this) 
          == JOptionPane.YES_OPTION)
        openFromXML(currentFile);
    } else if (fileChanged == StandardWatchEventKinds.ENTRY_DELETE) {
      if (SMessageDialog.showQuestionMessageYesNo(
          "The file has been deleted by another program.\n" +
          "Do you want to close it?", PanelClassDiagram.this) 
          == JOptionPane.YES_OPTION)
        newProject();
    }
    
    fileChanged = null;
  }

  private ClassDiagram classDiagram;
  private HierarchicalView hierarchicalView;
  private File currentFile = null;
  private GraphicView graphicView;
  private boolean disabledUpdate = false;
  private WatchEvent.Kind<Path> fileChanged;
  private WatchFileListener watchFileListener;

  SSplitPane splitInner, // Split graphicview part and properties part.
            splitOuter; // Split inner split and hierarchical part.

  public void setDividerBottom(float location) {
    splitInner.setDividerLocation(location);
  }

  public void setDividerLeft(float location) {
    splitOuter.setDividerLocation(location);
  }

  public void setDividerBottom(int location) {
    splitInner.setDividerLocation(location);
  }

  public void setDividerLeft(int location) {
    splitOuter.setDividerLocation(location);
  }

  public int getDividerBottom() {
    return splitInner.getDividerLocation();
  }

  public int getDividerLeft() {
    return splitOuter.getDividerLocation();
  }

  public void saveSplitLocationInProperties() {
    Properties properties = PropertyLoader.getInstance().getProperties();
    float dividerLocationBottom, dividerLocationLeft;

    dividerLocationBottom = (float) splitInner.getDividerLocation()
            / (float) (splitInner.getHeight() - splitInner.getDividerSize());
    properties.put(PropertyLoader.DIVIDER_BOTTOM,
            String.valueOf(dividerLocationBottom));

    dividerLocationLeft = (float) splitOuter.getDividerLocation()
            / (float) (splitOuter.getWidth() - splitOuter.getDividerSize());
    properties.put(PropertyLoader.DIVIDER_LEFT,
            String.valueOf(dividerLocationLeft));
    PropertyLoader.getInstance().push();
  }

  private PanelClassDiagram() {
    super(new MultiBorderLayout());

    // Customize style.
    setBackground(Slyum.DEFAULT_BACKGROUND);

    // Create new graphiView, contain class diagram.
    graphicView = new GraphicView(getClassDiagram());
    setTransferHandler(new FileHandler());
    
    watchFileListener = new WatchFileListener() {

      @Override
      public void fileModified() {
        fileChanged = StandardWatchEventKinds.ENTRY_MODIFY;
      }

      @Override
      public void fileDeleted() {
        fileChanged = StandardWatchEventKinds.ENTRY_DELETE;
      }
    };

    // Personalized ToolBar Layout
    add(SPanelFileComponent.getInstance(), BorderLayout.NORTH);
    add(SPanelDiagramComponent.getInstance(), BorderLayout.NORTH);
    add(SPanelElement.getInstance(), BorderLayout.NORTH);

    // Construct inner split pane.
    splitInner = new SSplitPane(JSplitPane.VERTICAL_SPLIT,
            graphicView.getScrollPane(), PropretiesChanger.getInstance());
    splitInner.setResizeWeight(1.d);

    // Construct outer split pane.
    splitOuter = new SSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        hierarchicalView = new HierarchicalView(getClassDiagram()), 
        splitInner);
    
    splitOuter.setResizeWeight(0.d);
    splitOuter.setBorder(
        BorderFactory.createMatteBorder(2, 0, 0, 0, Slyum.THEME_COLOR));

    add(splitOuter, BorderLayout.CENTER);

    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke("ESCAPE"), "escapePressed");
    getActionMap().put("escapePressed", new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        graphicView.deleteCurrentFactory();
      }
    });
    getClassDiagram().addObserver(hierarchicalView);
  }
  
  private int savedDividerBottomLocation;
  
  public void setFullScreen(boolean fullScreen) {
    Slyum.setSelectedMenuItemFullScreen(fullScreen);
    
    if (fullScreen) {
      
      // Set the same border for the diagram than the one for the splitter.
      graphicView.getScrollPane().setBorder(
          BorderFactory.createMatteBorder(2, 0, 0, 0, Slyum.THEME_COLOR));
      
      // Remove the splitter and replace it with the diagram.
      remove(splitOuter);
      add(graphicView.getScrollPane(), BorderLayout.CENTER);
      
      // Save the bottom divider location since we remove it's left component.
      savedDividerBottomLocation = getDividerBottom();
    } else {
      
      // Remove the border of the diagram.
      graphicView.getScrollPane().setBorder(null);
      
      // Restore the splitter and the diagram.
      splitInner.setLeftComponent(graphicView.getScrollPane());
      add(splitOuter, BorderLayout.CENTER);
      
      // Restore tge bottom divider location.
      setDividerBottom(savedDividerBottomLocation);
    }
    validate();
    repaint();
  }

  /**
   * Ask user to save current project.
   * @return 
   */
  public int askSavingCurrentProject() {
    if (!Change.hasChange())
      return JOptionPane.NO_OPTION;
    else
      return SMessageDialog
              .showQuestionMessageYesNoCancel("Save current project ?");
  }

  /**
   * Export the current graphic to an image file.
   */
  public void exportAsImage() {
    final JFileChooser fc = new JFileChooser(
            Slyum.getCurrentDirectoryFileChooser());
    fc.setAcceptAllFileFilterUsed(false);

    fc.addChoosableFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        if (f.isDirectory()) return true;

        final String extension = Utility.getExtension(f);
        if (extension != null)
          if (extension.equals("jpg") || extension.equals("png")
                  || extension.equals("gif")) return true;

        return false;
      }

      @Override
      public String getDescription() {
        return "Images (*.png, *.jpg, *.gif)";
      }
    });

    final int result = fc.showSaveDialog(this);

    if (result == JFileChooser.APPROVE_OPTION)
      saveImageTo(fc.getSelectedFile());
  }

  /**
   * Get the class diagram from project.
   * 
   * @return the class diagram
   */
  public ClassDiagram getClassDiagram() {
    if (classDiagram == null) {
      classDiagram = new ClassDiagram();
      classDiagram.addComponentsObserver(PropretiesChanger.getInstance());
    }

    return classDiagram;
  }

  public JButton getRedoButton() {
    return SPanelElement.getInstance().getRedoButton();
  }

  public JButton getUndoButton() {
    return SPanelElement.getInstance().getUndoButton();
  }

  /**
   * Get the current GraphicView.
   * 
   * @return the current GraphicView
   */
  public GraphicView getCurrentGraphicView() {
    return graphicView;
  }
  
  public List<GraphicView> getAllGraphicViews() {
    return Arrays.asList(graphicView);
  }

  /**
   * Init a new save where save project. If no file exist, open a JFileChooser
   * to ask a new file.
   * 
   * @return if file has been succefully created
   */
  public boolean initCurrentSaveFile() {
    final JFileChooser fc = new JFileChooser(
            Slyum.getCurrentDirectoryFileChooser());
    fc.setAcceptAllFileFilterUsed(false);

    fc.addChoosableFileFilter(new SlyFileChooser());

    final int result = fc.showSaveDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();

      String extension = Utility.getExtension(file);

      if (extension == null || !extension.equals(Slyum.EXTENTION)) {
        extension = Slyum.EXTENTION;
        file = new File(file.getPath() + "." + extension);
      }

      if (file.exists()) {
        final int answer = SMessageDialog.showQuestionMessageOkCancel(file
                + " already exists. Overwrite?");

        if (answer == JOptionPane.CANCEL_OPTION) return false;
      } else {
        try {
          file.createNewFile();
        } catch (final IOException e) {
          e.printStackTrace();
        }
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
  public void initPrinting() {
    new Thread(new Runnable() {

      @Override
      public void run() {
        print();
      }
    }).start();
  }

  /**
   * Create a new project. Ask user to save current project.
   */
  public void newProject() {
    if (!askForSave()) return;
    cleanApplication();
  }

  public void cleanApplication() {
    setDiagramName("");
    classDiagram.removeAll();
    graphicView.removeAll();
    setCurrentFile(null);
  }
  
  public void setVisibleDiagramName(boolean visible) {
    hierarchicalView.setVisibleClassDiagramName(visible);
  }
  
  public void setDiagramName(String name) {
    classDiagram.setName(name);
    classDiagram.notifyObservers();
  }

  public void setCurrentFile(File file) {
    WatchDir.unregister(getCurrentPath());
    currentFile = file;
    Slyum.getInstance().getMenuItemLocate().setEnabled(file != null);
    Change.setHasChange(false);
    Slyum.updateWindowTitle(currentFile);
    
    if (file == null) return;
    try {
      WatchDir.register(getCurrentPath(), watchFileListener);
    } catch (IOException ex) {
      Logger.getLogger(PanelClassDiagram.class.getName()).log(
          Level.SEVERE, "Unable to register file", ex);
    }

    Slyum.setCurrentDirectoryFileChooser(file.getParent());
  }

  public File getCurrentFile() {
    return currentFile;
  }
  
  public Path getCurrentPath() {
    if (currentFile == null)
      return null;
    return currentFile.toPath();
  }

  public boolean askForSave() {
    switch (askSavingCurrentProject()) {
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

  public static void openSlyFile(String filename) {
    getInstance().openFromXML(new File(filename));
  }

  public void openFromXML(final File file) {
    final String extension = Utility.getExtension(file);
    final SAXParserFactory factory = SAXParserFactory.newInstance();

    if (!file.exists()) {
      SMessageDialog
              .showErrorMessage("File not found. Please select an existing file...");
      return;
    }

    if (extension == null || !extension.equals(Slyum.EXTENTION)) {
      SMessageDialog.showErrorMessage("Invalide file format. Only \"."
              + Slyum.EXTENTION + "\" files are accepted.");
      return;
    }

    graphicView.getScrollPane().setVisible(false);

    final boolean isBlocked = Change.isBlocked();
    Change.setBlocked(true);

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    try {
      SAXParser parser = factory.newSAXParser();
      XMLParser handler = new XMLParser(classDiagram, graphicView);
      parser.parse(file, handler);
      handler.createDiagram();
    } catch (Exception e) {
      showErrorImportationMessage(e);
      graphicView.setPaintBackgroundLast(true);
      graphicView.goRepaint();
    }

    graphicView.getScrollPane().setVisible(true);

    Change.setBlocked(isBlocked);

    setCurrentFile(file);
    Change.setHasChange(false);

    setCursor(null);
    
    RecentProjectManager.addhistoryEntry(file.getAbsolutePath());

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        graphicView.paintBackgroundFirst();
        graphicView.unselectAll();

        for (GraphicComponent c : getCurrentGraphicView().getAllComponents())
          c.notifyObservers();

        graphicView.getScrollPane().getVerticalScrollBar().setValue(0);
        graphicView.getScrollPane().getHorizontalScrollBar().setValue(0);
        
        System.gc();
      }
    });
  }

  /**
   * Open a new project.
   */
  public void openFromXML() {
    if (!askForSave()) return;

    final JFileChooser fc = new JFileChooser(
            Slyum.getCurrentDirectoryFileChooser());
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
   * @date 6 dÃ©c. 2011
   */
  private class SlyFileChooser extends FileFilter {
    @Override
    public boolean accept(File f) {
      if (f.isDirectory()) return true;

      final String extension = Utility.getExtension(f);

      if (extension != null)
        if (extension.equals(Slyum.EXTENTION)) return true;

      return false;
    }

    @Override
    public String getDescription() {
      return "Fichiers " + Slyum.EXTENTION.toUpperCase() + " (*."
              + Slyum.EXTENTION + ")";
    }
  }

  /**
   * Print a picture of the diagram.
   */
  public void print() {
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
      if (SlyumPrinterJob.print(getCurrentGraphicView()))
        SMessageDialog.showInformationMessage("Print completed successfully");
    } catch (PrinterException ex) {
      Logger.getLogger(
              PanelClassDiagram.class.getName()).log(Level.SEVERE, null, ex);
      SMessageDialog.showInformationMessage("An error occurs while printing.");
    }
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * Save a picture of the diagram in the given file.
   * 
   * @param file
   *          the file where to save a picture.
   */
  public void saveImageTo(File file) {
    try {
      String extension = Utility.getExtension(file);

      if (extension == null) {
        extension = "png";
        file = new File(file.getPath() + "." + extension);
      }

      if (file.exists())
        if (SMessageDialog.showQuestionMessageOkCancel(file
                + " already exists. Overwrite?") == JOptionPane.CANCEL_OPTION)
          return;

      if (extension.equals("png"))
        ImageIO.write(graphicView.getScreen(BufferedImage.TYPE_INT_ARGB_PRE),
                extension, file);
      else if (extension.equals("jpg") || extension.equals("gif"))
        ImageIO.write(graphicView.getScreen(BufferedImage.TYPE_INT_RGB),
                extension, file);
      else
        SMessageDialog.showErrorMessage("Extension \"." + extension
                + "\" not supported.\nSupported extensions : png, jpg, gif.");

    } catch (final Exception e) {
      SMessageDialog
              .showErrorMessage("Class diagram is empty. Empty class diagramm can't be export.");
    }
  }

  /**
   * Save the diagram to text format, with XML structure.
   * 
   * @param selectFile
   *          true if a dialog must invite the user to choose a file; false to
   *          save in current file. If no current file, dialog will open.
   */
  public void saveToXML(boolean selectFile) {
    if (selectFile || currentFile == null || !currentFile.exists())
      if (!initCurrentSaveFile()) return;

    // Ignore les deux prochains event qui sont générés par Slyum.
    WatchDir.ignoreNextEvents(getCurrentPath(), 2);
    
    // Génération du document xml.
    DOMSource xmlInput = new DOMSource(XmlFactory.getDocument());

    // Création et configuration du Transformer. Sauvegarde du fichier.
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(
              "{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.ENCODING, "iso-8859-15");
      StreamResult xmlOutput = new StreamResult(currentFile);
      transformer.transform(xmlInput, xmlOutput);
    } catch (TransformerException e) {
      Logger.getGlobal().log(Level.SEVERE, "Unable to save file.", e);
      SMessageDialog.showErrorMessage(e.getLocalizedMessage());
    }
    Change.setHasChange(false);
    RecentProjectManager.addhistoryEntry(currentFile.getAbsolutePath());
  }

  private void showErrorImportationMessage(Exception e) {
    SMessageDialog
            .showErrorMessage("Cannot open projet. Error reading from file.\nMessage : "
                    + e.getMessage());

    e.printStackTrace();

    cleanApplication();
    graphicView.setVisible(true);
  }

  public void openFromXmlAndAsk(File file) {
    if (!askForSave()) return;

    openFromXML(file);
  }

  private class FileHandler extends TransferHandler {
    private static final long serialVersionUID = 5606903424194929527L;

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
      for (DataFlavor flavor : transferFlavors)
        if (!flavor.isFlavorJavaFileListType()) return false;

      return true;
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
      try {
        @SuppressWarnings("unchecked")
        List<File> dropppedFiles = (List<File>) t
                .getTransferData(DataFlavor.javaFileListFlavor);

        // Open just the last of the list.
        openFromXmlAndAsk((File) dropppedFiles.get(dropppedFiles.size() - 1));
        return true;

      } catch (UnsupportedFlavorException | IOException e) {
        e.printStackTrace();
        return false;
      }
    }
  }

  public boolean isDisabledUpdate() {
    return disabledUpdate;
  }

  public void setDisabledUpdate(boolean disabledUpdate) {
    if (this.disabledUpdate == disabledUpdate)
      return;
    this.disabledUpdate = disabledUpdate;
  }
}
