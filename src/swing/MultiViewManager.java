package swing;

import classDiagram.ClassDiagram;
import graphic.GraphicView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import swing.hierarchicalView.HierarchicalView;
import utility.SMessageDialog;
import utility.WatchDir;

/**
 * 
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public class MultiViewManager {
  
  // Static stuff
  private static MultiViewManager instance;
  public static void initialize(
      ClassDiagram classDiagram, HierarchicalView hierarchicalView) {
    instance = new MultiViewManager(classDiagram, hierarchicalView);
  }
  
  public static GraphicView getRootGraphicView() {
    return instance.graphicViews.getFirst();
  }
  
  public static GraphicView getSelectedGraphicView() {
    STab.GraphicViewTabComponent component = 
        STab.getInstance().getTabComponentAt(
            STab.getInstance().getSelectedIndex());
    
    if (component != null)
      return component.getGraphicView();
    return null;
  }
  
  public static List<GraphicView> getAllGraphicViews() {
    return (List<GraphicView>) instance.graphicViews.clone();
  }
  
  public static boolean isGraphicViewOpened(GraphicView graphicView) {
    return STab.getInstance().getAllGraphicsView().contains(graphicView);
  }    
  
  public static GraphicView addNewView() {    
    UserInputDialog uip = 
        new UserInputDialog(
            GraphicView.NO_NAMED_VIEW, "Slyum - New view", "Enter a name for the new view:");
    
    uip.setVisible(true);
    
    if (uip.isAccepted())
      return addNewView(uip.getText());
    
    return null;
  }
  
  public static GraphicView addNewView(String title) {
    GraphicView newGraphicView = new GraphicView(instance.classDiagram, false);
    instance.graphicViews.add(newGraphicView);
    instance.hierarchicalView.addView(newGraphicView);
    newGraphicView.setName(title);
    newGraphicView.notifyObservers();
    
    if (!isXmlImportation())
      addNewViewInFile(newGraphicView);
    
    return newGraphicView;
  }
  
  public static GraphicView openView(GraphicView graphicView) {
    
    if (!graphicView.isOpenInTab()) {
      STab.getInstance().openTab(graphicView);
      
      if (!isXmlImportation())
        changeViewStatInFile(graphicView, true);
    }
    
    setSelectedGraphicView(graphicView);
    
    return graphicView;
  }
  
  public static void setSelectedGraphicView(int index) {
    setSelectedGraphicView(instance.graphicViews.get(index));
  }
  
  public static void setSelectedGraphicView(GraphicView graphicView) {
    if (STab.getInstance().getSelectedComponent() != graphicView.getScrollPane())
      STab.getInstance().setSelectedComponent(graphicView.getScrollPane());
    
    instance.hierarchicalView.setSelectedView(graphicView);
  }
  
  public static GraphicView closeView(GraphicView graphicView) {
    if (!isXmlImportation())
      changeViewStatInFile(graphicView, false);
    
    JTabbedPane pane = STab.getInstance();
    int selectedIndex = pane.getSelectedIndex();
    int index = pane.indexOfComponent(graphicView.getScrollPane());
    pane.remove(graphicView.getScrollPane());
    pane.setSelectedIndex(
        selectedIndex == index ? 
            index - (index == pane.getTabCount() - 1 ? 1 : 0) : 
            (selectedIndex > index ? selectedIndex - 1 : selectedIndex));
    return graphicView;
  }
  
  public static void removeView(GraphicView graphicView) {
    if (graphicView == getRootGraphicView())
      throw new IllegalArgumentException(
          "You cannot remove the root graphic view. " + 
          "Call GraphicView.clean instead.");
    
    if (SMessageDialog.showQuestionMessageYesNo(
        "Are you sure you want to delete the view \"" + graphicView.getName() + 
        "\"?\nThis action can't be undone.") == JOptionPane.NO_OPTION)
      return;
    
    if (graphicView.isOpenInTab())
      closeView(graphicView);
    instance.hierarchicalView.removeView(graphicView);
    removeViewInFile(graphicView);
    instance.graphicViews.remove(graphicView);
  }
  
  public static GraphicView addAndOpenNewView() {
    return openView(addNewView());
  }
  
  public static GraphicView addAndOpenNewView(String title) {
    return openView(addNewView(title));
  }
  
  public static void cleanGraphicViews() {
    while (instance.graphicViews.size() > 1) // Do not remove the root graphic view.
      instance.graphicViews.remove(1);
  }
  
  public static void changeViewStatInFile(GraphicView graphicView, boolean open) {
    if (getCurrentFile() == null)
      return;
    try {
      
      String strOpen = String.valueOf(open);
      WatchDir.ignoreNextEvents(getCurrentPath(), 2);
      
      Document doc = getDocumentFromCurrentFile();
      
      Node nodeUmlView = doc.getElementsByTagName("umlView").item(
          instance.graphicViews.indexOf(graphicView));

      // Update the open attribute of the umlView node.
      Node openNode = nodeUmlView.getAttributes().getNamedItem("open");
      
      if (openNode == null)
        ((Element)nodeUmlView).setAttribute("open", strOpen);
      else
        openNode.setTextContent(strOpen);
      
      saveDocumentInCurrentFile(doc);
    } catch (TransformerException | ParserConfigurationException | SAXException | IOException ex) {
      Logger.getLogger(PanelClassDiagram.class.getName()).log(Level.SEVERE, null, ex);
    }
	}
  
  private static Path getCurrentPath() {
    return PanelClassDiagram.getInstance().getCurrentPath();
  }
  
  private static File getCurrentFile() {
    return PanelClassDiagram.getInstance().getCurrentFile();
  }
  
  private static void removeViewInFile(GraphicView graphicView) {
    if (getCurrentFile() == null)
      return;
    try {
      int viewIndex = instance.graphicViews.indexOf(graphicView);
      Document doc = getDocumentFromCurrentFile();
      
      WatchDir.ignoreNextEvents(getCurrentPath(), 2);
      
      doc.getFirstChild().removeChild(doc.getElementsByTagName("umlView").item(viewIndex));
      saveDocumentInCurrentFile(doc);
    } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
      Logger.getLogger(PanelClassDiagram.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private static void addNewViewInFile(GraphicView graphicView) {
    if (getCurrentFile() == null)
      return;
    try {
      Document doc = getDocumentFromCurrentFile();
      
      WatchDir.ignoreNextEvents(getCurrentPath(), 2);
      doc.getFirstChild().appendChild(graphicView.getXmlElement(doc));
      saveDocumentInCurrentFile(doc);
    } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
      Logger.getLogger(PanelClassDiagram.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private static Document getDocumentFromCurrentFile() 
      throws ParserConfigurationException, SAXException, IOException {
    String filepath = getCurrentFile().getAbsolutePath();
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder(); 
    return docBuilder.parse(filepath);
  }
  
  private static void saveDocumentInCurrentFile(Document doc) 
      throws TransformerConfigurationException, TransformerException {
    
      // write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(getCurrentFile());
      transformer.transform(source, result);
  }
  
  private static boolean isXmlImportation() {
    return PanelClassDiagram.getInstance().isXmlImportation();
  }
  
  private ClassDiagram classDiagram;
  private HierarchicalView hierarchicalView;
  private LinkedList<GraphicView> graphicViews = new LinkedList<>();

  private MultiViewManager(
      ClassDiagram classDiagram, HierarchicalView hierarchicalView) {
    
    GraphicView rootGraphicView = new GraphicView(classDiagram, true);
    graphicViews.add(rootGraphicView);
    
    this.classDiagram = classDiagram;
    this.hierarchicalView = hierarchicalView;
    
    STab.initialize(rootGraphicView);
  }
  
}
