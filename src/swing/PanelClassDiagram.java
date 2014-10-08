package swing;

import swing.slyumCustomizedComponents.SSplitPane;
import change.Change;
import classDiagram.ClassDiagram;
import classDiagram.verifyName.SyntaxeNameException;
import graphic.GraphicView;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import swing.hierarchicalView.HierarchicalView;
import swing.propretiesView.PropretiesChanger;
import swing.slyumCustomizedComponents.STab;
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
  
  public void setSelectedGraphicView(int index) {
    STab.getInstance().setSelectedIndex(index);
  }
  
  public void setSelectedGraphicView(GraphicView graphicView) {
    STab.getInstance().setSelectedComponent(graphicView.getScrollPane());
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
  private STab slyumTabbedPane;
  private boolean disabledUpdate = false;
  private WatchEvent.Kind<Path> fileChanged;
  private WatchFileListener watchFileListener;
  private boolean xmlImportation = false;
  private LinkedList<GraphicView> graphicViews = new LinkedList<>();

  SSplitPane splitInner, // Split graphicview part and properties part.
          splitOuter; // Split inner split and hierarchical part.

  public void setDividerBottom(float location) {
    splitInner.setDividerLocation(location);
  }

  public void setDividerLeft(float location) {
    splitOuter.setDividerLocation(location);
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
  }

  private PanelClassDiagram() {
    super(new MultiBorderLayout());

    // Customize style.
    setBackground(Slyum.DEFAULT_BACKGROUND);

    // Create new graphiView, contain class diagram.
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
    
    GraphicView rootGraphicView = new GraphicView(getClassDiagram(), true);
    graphicViews.add(rootGraphicView);
    STab.initialize(rootGraphicView);
    slyumTabbedPane = STab.getInstance();

    // Personalized ToolBar Layout
    add(SPanelFileComponent.getInstance(), BorderLayout.NORTH);
    add(SPanelDiagramComponent.getInstance(), BorderLayout.NORTH);
    add(SPanelElement.getInstance(), BorderLayout.NORTH);

    // Construct inner split pane.
    splitInner = new SSplitPane(JSplitPane.VERTICAL_SPLIT,
            slyumTabbedPane, PropretiesChanger.getInstance());
    splitInner.setResizeWeight(1.0);

    // Construct outer split pane.
    splitOuter = new SSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        hierarchicalView = new HierarchicalView(getClassDiagram()), splitInner);
    splitOuter.setResizeWeight(0.0);
    hierarchicalView.addView(rootGraphicView);

    add(splitOuter, BorderLayout.CENTER);

    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke("ESCAPE"), "escapePressed");
    getActionMap().put("escapePressed", new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent e) {
        getSelectedGraphicView().deleteCurrentFactory();
      }
    });
    getClassDiagram().addObserver(hierarchicalView);
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
  
  public GraphicView addNewView() {    
    UserInputDialog uip = 
        new UserInputDialog(
            GraphicView.NO_NAMED_VIEW, "Slyum - New view", "Enter a name for the new view:");
    
    uip.setVisible(true);
    
    if (uip.isAccepted())
      return addNewView(uip.getText());
    
    return null;
  }
  
  public GraphicView addNewView(String title) {
    GraphicView newGraphicView = new GraphicView(classDiagram, false);
    graphicViews.add(newGraphicView);
    hierarchicalView.addView(newGraphicView);
    newGraphicView.setName(title);
    newGraphicView.notifyObservers();
    return newGraphicView;
  }
  
  public GraphicView openView(GraphicView graphicView) {
    STab.getInstance().openTab(graphicView);
    setSelectedGraphicView(graphicView);
    
    if (!isXmlImportation())
      changeViewStatInFile(graphicView, true);
    
    return graphicView;
  }
  
  public GraphicView closeView(GraphicView graphicView) {
    if (!isXmlImportation())
      changeViewStatInFile(graphicView, false);
    
    STab.getInstance().remove(graphicView.getScrollPane());
    return graphicView;
  }
  
  public void removeView(GraphicView graphicView) {
    
  }
  
  public GraphicView addAndOpenNewView() {
    return openView(addNewView());
  }
  
  public GraphicView addAndOpenNewView(String title) {
    return openView(addNewView(title));
  }
  
  private Document getDocumentFromCurrentFile() 
      throws ParserConfigurationException, SAXException, IOException {
    String filepath = currentFile.getAbsolutePath();
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder(); 
    return docBuilder.parse(filepath);
  }
  
  private void saveDocumentInCurrentFile(Document doc) 
      throws TransformerConfigurationException, TransformerException {
    
      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(currentFile);
      transformer.transform(source, result);
  }
  
  public void changeViewStatInFile(GraphicView graphicView, boolean open) {
    try {      
      WatchDir.ignoreNextEvents(getCurrentPath(), 2);
      
      Document doc = getDocumentFromCurrentFile();
      
      Node nodeUmlView = doc.getElementsByTagName("umlView").item(
          graphicViews.indexOf(graphicView));

      // Update the open attribute of the umlView node.
      nodeUmlView.getAttributes().getNamedItem("open")
          .setTextContent(String.valueOf(open));
      
      saveDocumentInCurrentFile(doc);
    } catch (TransformerException | ParserConfigurationException | SAXException | IOException ex) {
      Logger.getLogger(PanelClassDiagram.class.getName()).log(Level.SEVERE, null, ex);
    }
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
  public GraphicView getSelectedGraphicView() {
    STab.GraphicViewTabComponent component = 
        STab.getInstance().getTabComponentAt(
            STab.getInstance().getSelectedIndex());
    
    if (component != null)
      return component.getGraphicView();
    return null;
  }
  
  public GraphicView getRootGraphicView() {
    return graphicViews.getFirst();
  }
  
  public List<GraphicView> getAllGraphicViews() {
    return (List<GraphicView>) graphicViews.clone();
  }
  
  public boolean isGraphicViewOpened(GraphicView graphicView) {
    return STab.getInstance().getAllGraphicsView().contains(graphicView);
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
    cleanViews();
    getRootGraphicView().removeAll();
    STab.getInstance().setSelectedIndex(0);
    setCurrentFile(null);
  }
  
  /**
   * Remove all tabs, nodes and graphicviews from this PanelClassDiagram.
   * Do not remove them from current file. This method is generally used with
   * by cleanApplication when opening a new file.
   */
  private void cleanViews() {
    cleanTabs();
    cleanHierarchicalView();
    
    // Clean graphic views last!
    cleanGraphicViews();
  }
  
  private void cleanTabs() {
    
    while (STab.getInstance().getTabCount() > 2) {
      STab.getInstance().getTabComponentAt(1).getGraphicView().removeAll();
      STab.getInstance().remove(1);
    }
  }
  
  private void cleanHierarchicalView() {
    hierarchicalView.removeViews();    
  }
  
  private void cleanGraphicViews() {
    while (graphicViews.size() > 1) // Do not remove the root graphic view.
      graphicViews.remove(1);
  }
  
  public void setVisibleDiagramName(boolean visible) {
    hierarchicalView.setVisibleClassDiagramName(visible);
  }

  public boolean isXmlImportation() {
    return xmlImportation;
  }

  public void setXmlImportation(boolean isXmlImportation) {
    this.xmlImportation = isXmlImportation;
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

    cleanApplication();
    
    final GraphicView rootGraphicView = getSelectedGraphicView();
    rootGraphicView.getScrollPane().setVisible(false);
    
    final boolean isBlocked = Change.isBlocked();
    Change.setBlocked(true);

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    setXmlImportation(true);

    try {
      SAXParser parser = factory.newSAXParser();
      XMLParser handler = new XMLParser(classDiagram);
      parser.parse(file, handler);
      handler.createDiagram();
    } catch (SyntaxeNameException | IOException | ParserConfigurationException | SAXException e) {
      showErrorImportationMessage(e);
      
      rootGraphicView.setPaintBackgroundLast(true);
      rootGraphicView.goRepaint();
    }

    rootGraphicView.getScrollPane().setVisible(true);
    setXmlImportation(false);

    Change.setBlocked(isBlocked);

    setCurrentFile(file);
    Change.setHasChange(false);

    setCursor(null);
    
    RecentProjectManager.addhistoryEntry(file.getAbsolutePath());

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        rootGraphicView.paintBackgroundFirst();
        
        rootGraphicView.unselectAll();
        rootGraphicView.refreshAllComponents();

        rootGraphicView.getScrollPane().getVerticalScrollBar().setValue(0);
        rootGraphicView.getScrollPane().getHorizontalScrollBar().setValue(0);
        
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
      if (SlyumPrinterJob.print(getSelectedGraphicView()))
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
      
      GraphicView graphicView = getSelectedGraphicView();
      switch (extension) {
        case "png":
          ImageIO.write(graphicView.getScreen(BufferedImage.TYPE_INT_ARGB_PRE),
              extension, file);
          break;
        case "jpg":
        case "gif":
          ImageIO.write(graphicView.getScreen(BufferedImage.TYPE_INT_RGB),
              extension, file);
          break;
        default:
          SMessageDialog.showErrorMessage("Extension \"." + extension
              + "\" not supported.\nSupported extensions : png, jpg, gif.");
          break;
      }

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
    SMessageDialog.showErrorMessage("Cannot open projet. Error reading from file.\nMessage : "
                    + e.getMessage());

    e.printStackTrace();
    cleanApplication();
    getSelectedGraphicView().setVisible(true);
  }

  public void openFromXmlAndAsk(File file) {
    if (!askForSave()) 
      return;
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
