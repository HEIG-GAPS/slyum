package xml;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import swing.PanelClassDiagram;
import xml.factories.XmlEntityFactory;

public class XmlWriter extends Writer<XmlEntityFactory, Document> {
    
    /* Champs static */
    private static XmlWriter instance;
    private Document doc;
    
    public static XmlWriter getInstance() {
        if (instance == null)
            instance = new XmlWriter();
        return instance;
    }
    /* ----------------------------------------------------------- */

    public XmlWriter() {
        super("xml.factories");
    }
    
    private void createNewDocument() {
        
        // Création du document.
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    public Document getDoc() {
        return doc;
    }

    @Override
    public Document generate() {
        // Réinitialisation du document.
        createNewDocument();
        
        // Elément principal (diagramme de classe).
        Element classDiagram = doc.createElement("classDiagram");
        doc.appendChild(classDiagram);
        
        // Recherche tous les objets associés à une fabrique existante
        // et crée la version string de ces objets.
        for (XmlEntityFactory factory : factories) {
            List<?> objects = PanelClassDiagram.getInstance().getClassDiagram().
                                getComponentsByType(factory.getCreatedClass());
            for (Object o : objects)
                classDiagram.appendChild(factory.create(o));
        }
        
        return doc;
    }
    
    /* Accesseurs rapides */
    public static Document document() {
        return getInstance().getDoc();
    }
    
    public static Document makeGeneration() {
        return getInstance().generate();
    }
}
